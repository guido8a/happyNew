package happy.tramites

import happy.alertas.Alerta
import happy.seguridad.Persona
import happy.seguridad.Shield

class ExternosController extends Shield {

    def bandejaExternos(){
        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def bloqueo = false
        return [persona: persona, bloqueo: bloqueo]

    }
    def tablaBandeja(){

        def persona = Persona.get(session.usuario.id)
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def enviado = EstadoTramite.findByCodigo("E003")
        def recibido = EstadoTramite.findByCodigo("E004")
        def anexo


        params.domain = params.domain ?: "persDoc"
        params.sort = params.sort ?: "fechaEnvio"
        params.order = params.order ?: "desc"

        def tramites=Tramite.findAll("from Tramite where externo='1' and (de=${persona.id} ${(persona.esTriangulo())?'or deDepartamento='+persona.departamento.id:''}) and tipoDocumento!=${TipoDocumento.findByCodigo('DEX')?.id}")
        def pdts = []
        tramites.each {t->
            def pdt = PersonaDocumentoTramite.findByTramiteAndRolPersonaTramite(t,rolPara)
            if(pdt && (pdt.estado==enviado || pdt.estado==recibido)){
                if(pdt.fechaEnvio)
                    pdts +=pdt
            }

        }
        pdts=pdts.sort{it.fechaEnvio}



        return [tramites: pdts, params: params]
    }

    def recibirTramiteExterno() {
        println "recibir tramite "+params

        def tramitetr = Tramite.get(params.id)
        if(tramitetr){
//            println("entro!")
            def paratr = tramitetr.para
            def copiastr = tramitetr.copias
            (copiastr + paratr).each {c->
                if(c?.estado?.codigo == "E006") {
                    render "NO_Este trámite ya ha sido anulado, no puede ser recibido."
                    return
                }else{


                    if (request.getMethod() == "POST") {
                        def persona = Persona.get(session.usuario.id)
                        def tramite = Tramite.get(params.id)
                        def rolPara = RolPersonaTramite.findByCodigo("R001")
                        def pdt = PersonaDocumentoTramite.findByTramiteAndRolPersonaTramite(tramite,rolPara)
                        if(!pdt){
                            render "NO_no se encontro el destinatario"
                            return
                        }
                        def porEnviar = EstadoTramite.findByCodigo("E001")
                        def enviado = EstadoTramite.findByCodigo("E003")
                        def recibido = EstadoTramite.findByCodigo("E004")
                        //tambien puede recibir si ya esta en estado recibido (se pone en recibido cuando recibe el PARA)
                        if (tramite.estadoTramite != enviado && tramite.estadoTramite != recibido) {
                            render "ERROR_Se ha cancelado el proceso de recepción.<br/>Este trámite no puede ser gestionado."
                            return
                        }

                        pdt.fechaRecepcion = new Date()
                        pdt.estado=recibido
                        pdt.tramite.estadoTramite=recibido
                        pdt.save(flush: true)
                        pdt.tramite.save(flush: true)
                        def pdtRecibe = new PersonaDocumentoTramite()
                        pdtRecibe.tramite = tramite
                        pdtRecibe.persona = persona
                        pdtRecibe.rolPersonaTramite = RolPersonaTramite.findByCodigo("E003")
                        pdtRecibe.fechaRecepcion =  new Date()
                        pdtRecibe.save(flush: true)
                        render "OK_Trámite recibido correctamente"


                    } else {
                        response.sendError(403)
                    }

                }
            }
        }





    }
}
