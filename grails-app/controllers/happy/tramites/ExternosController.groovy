package happy.tramites

import happy.alertas.Alerta
import happy.seguridad.Persona
import happy.seguridad.Shield

class ExternosController extends Shield {

    def bandejaExternos() {
        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def bloqueo = false
        return [persona: persona, bloqueo: bloqueo]

    }

    def tablaBandeja() {

        def persona = Persona.get(session.usuario.id)
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def enviado = EstadoTramite.findByCodigo("E003")
        def recibido = EstadoTramite.findByCodigo("E004")
        def anexo


        params.domain = params.domain ?: "persDoc"
        params.sort = params.sort ?: "fechaEnvio"
        params.order = params.order ?: "desc"

        def tramites = Tramite.findAll("from Tramite where externo='1' and (de=${persona.id} ${(persona.esTriangulo()) ? 'or deDepartamento=' + persona.departamento.id : ''}) and tipoDocumento!=${TipoDocumento.findByCodigo('DEX')?.id}")
        def pdts = []
        tramites.each { t ->
            def pdt = PersonaDocumentoTramite.findByTramiteAndRolPersonaTramite(t, rolPara)
            if (pdt && (pdt.estado == enviado || pdt.estado == recibido)) {
                if (pdt.fechaEnvio) {
                    pdts += pdt
                }
            }

        }
        pdts = pdts.sort { it.fechaEnvio }



        return [tramites: pdts, params: params]
    }

    def recibirTramitesExternos_ajax() {
        def ids = params.ids.split("_")
        def errores = ""
        ids.each { id ->
            errores += recibirTramiteExterno_funcion(id.toString().toLong())
        }
        if (errores == "") {
            render "OK_Confirmada recepción"
        } else {
            render "ERROR_<ul>" + errores + "</ul>"
        }
    }

    def recibirTramiteExterno_funcion(Long id) {
        println "recibir tramite " + id
        def pdt = PersonaDocumentoTramite.get(id)
        if (pdt?.estado?.codigo == "E006") {
            return "<li>El trámite ${pdt.rolPersonaTramite.descripcion} ${pdt.departamento ? pdt.departamento.descripcion : pdt.persona?.login} ya ha sido anulado, no puede ser recibido.</li>"
        } else {
            if (request.getMethod() == "POST") {
                def persona = Persona.get(session.usuario.id)

                def enviado = EstadoTramite.findByCodigo("E003")
                def recibido = EstadoTramite.findByCodigo("E004")
                //tambien puede recibir si ya esta en estado recibido (se pone en recibido cuando recibe el PARA)
                if (pdt.tramite.estadoTramite != enviado && pdt.tramite.estadoTramite != recibido) {
                    return "<li>Se ha cancelado el proceso de recepción.<br/>Este trámite no puede ser gestionado.</li>"
                }

                pdt.fechaRecepcion = new Date()
                pdt.estado = recibido
                pdt.tramite.estadoTramite = recibido
                pdt.save(flush: true)
                pdt.tramite.save(flush: true)
                def pdtRecibe = new PersonaDocumentoTramite()
                pdtRecibe.tramite = pdt.tramite
                pdtRecibe.persona = persona

                pdtRecibe.personaSigla = persona.login
                pdtRecibe.personaNombre = persona.nombre + " " + persona.apellido
                pdtRecibe.departamentoNombre = persona.departamento.descripcion
                pdtRecibe.departamentoSigla = persona.departamento.codigo
                pdtRecibe.personaSigla = persona.login

                pdtRecibe.rolPersonaTramite = RolPersonaTramite.findByCodigo("E003")
                pdtRecibe.departamentoPersona = persona.departamento

                pdtRecibe.fechaRecepcion = new Date()
                pdtRecibe.save(flush: true)
//                return "<li>Trámite ${c.rolPersonaTramite.descripcion} ${c.departamento ? c.departamento.descripcion : c.persona?.login} recibido correctamente</li>"
                return ""
            } else {
                return "<li>Ha ocurrido un error grave, no puede confirmar la recepción</li>"
            }
        }
    }

    def recibirTramiteExterno() {
        println "confirmar recibir tramite externo no se usa... " + params

        def tramitetr = Tramite.get(params.id)
        if (tramitetr) {
//            println("entro!")
            def paratr = tramitetr.para
            def copiastr = tramitetr.copias
            (copiastr + paratr).each { c ->
                if (c?.estado?.codigo == "E006") {
                    render "NO_Este trámite ya ha sido anulado, no puede ser recibido."
                    return
                } else {


                    if (request.getMethod() == "POST") {
                        def persona = Persona.get(session.usuario.id)
                        def tramite = Tramite.get(params.id)
                        def rolPara = RolPersonaTramite.findByCodigo("R001")
                        def pdt = PersonaDocumentoTramite.findByTramiteAndRolPersonaTramite(tramite, rolPara)
                        if (!pdt) {
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
                        pdt.estado = recibido
                        pdt.tramite.estadoTramite = recibido
                        pdt.save(flush: true)
                        pdt.tramite.save(flush: true)
                        def pdtRecibe = new PersonaDocumentoTramite()
                        pdtRecibe.tramite = tramite
                        pdtRecibe.persona = persona

                        pdtRecibe.personaSigla = persona.login
                        pdtRecibe.personaNombre = persona.nombre + " " + persona.apellido
                        pdtRecibe.departamentoNombre = persona.departamento.descripcion
                        pdtRecibe.departamentoSigla = persona.departamento.codigo
                        pdtRecibe.personaSigla = persona.login

                        pdtRecibe.rolPersonaTramite = RolPersonaTramite.findByCodigo("E003")
                        pdtRecibe.departamentoPersona = persona.departamento

                        pdtRecibe.fechaRecepcion = new Date()
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
