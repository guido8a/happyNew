package happy.tramites

import groovy.time.TimeCategory
import happy.seguridad.Persona

class Tramite2Controller extends happy.seguridad.Shield{

    def verTramite(){
        /*comentar esto*/
        def tramite = Tramite.get(params.id)
        /*Aqui controlar los permisos para ver el tramite por el usuario*/


        /*fin permisos*/

        return  [tramite:tramite]
    }

    def revision(){

        def tramite = Tramite.get(params.id).refresh()


        /*Todo hacer la validacion para determinar si es el jefe*/

        return  [tramite:tramite]
    }

    def saveNotas(){
        def tramite = Tramite.get(params.tramite)
        tramite.nota=params.notas
        if(tramite.save(flush: true))
            render "ok"
        else
            render "error"

    }

    def revisar(){

        if(request.getMethod()=="POST"){
            def tramite = Tramite.get(params.id)
            /*validaciones*/
            def user = Persona.get(session.usuario.id)
            def msg =""
            def band = true
            def per = PermisoUsuario.findByPersonaAndPermisoTramite(user,PermisoTramite.findByCodigo("P005"))
            if(tramite.de.departamento.id!=user.departamento.id ){
                band=false
            }
            if(user.jefe!=1 && !per )
                band=false
            if(band){
                if(tramite.estadoTramite.codigo=="E001"){
                    tramite.estadoTramite=EstadoTramite.findByCodigo("E002")
                }
                if(tramite.save(flush: true))
                    render "ok"
                else
                    render "error"
            }else{
                msg="Usted no tiene autorización para revisar este tramite"
                render "error_"+msg
            }

        }else{
            response.sendError(403)
        }
    }



    def bandejaSalida() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def revisar = false
        def bloqueo = false
        if(session.departamento.estado=="B")
            bloqueo=true
        println "bloqueo "+bloqueo
        if(persona.jefe==1)
            revisar=true
        else{
            def per = PermisoUsuario.findByPersonaAndPermisoTramite(persona,PermisoTramite.findByCodigo("P005"))
            if(per)
                revisar=true
        }
        return [persona: persona,revisar:revisar,bloqueo:bloqueo]

    }

    def tablaBandejaSalida() {
//        println "carga bandeja"
        def persona = Persona.get( session.usuario.id)
        def tramites = []
        def estados = EstadoTramite.findAllByCodigoInList(["E001","E002","E003"])
        if(persona.jefe==1){
            Persona.findAllByDepartamento(persona.departamento).each {p->
                def t =  Tramite.findAllByDeAndEstadoTramiteInList(p,estados,[sort:"fechaCreacion",order:"desc"])
                if(t.size()>0)
                    tramites+=t
            }
        }else{
            tramites = Tramite.findAllByDeAndEstadoTramiteInList(persona,estados,[sort:"fechaCreacion",order:"desc"])
        }



        return [persona: persona, tramites: tramites,idTramitesNoRecibidos:[] ]
    }

    //alertas

    def enviar(){
//        println "method "+request.getMethod()
        /*todo sin validacion alguna... que envie no mas cualquiera*/
        if(request.getMethod()=="POST"){
            def msg =""
            def tramite = Tramite.get(params.id)
            def envio = new Date()

            PersonaDocumentoTramite.findAllByTramite(tramite).each{t->
                t.fechaEnvio=envio
                t.save(flush: true)
            }
            def pdt = new PersonaDocumentoTramite()
            pdt.tramite=tramite
            pdt.persona=session.usuario
            pdt.departamento=session.departamento
            pdt.fechaEnvio = envio
            pdt.rolPersonaTramite = RolPersonaTramite.findByCodigo("E004")
            pdt.save(flush: true)
            tramite.fechaEnvio=envio
            tramite.estadoTramite=EstadoTramite.findByCodigo('E003')
            if(tramite.save(flush: true))
                render "ok"

        }else{
            response.sendError(403)
        }

    }



    def busquedaBandejaSalida() {


        if (params.fecha) {
            params.fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        }

        def res = Tramite.withCriteria {

            if (params.fecha) {
                eq('fechaIngreso', params.fecha)
            }
            if (params.asunto) {
                ilike('asunto', '%' + params.asunto + '%')
            }
            if (params.memorando) {

                ilike('numero', '%' + params.memorando + '%')

            }
        }

        return [tramites: res]


    }

    def verRezagados(){
        def dep = session.departamento
        def tramites =[]
        def ahora = new Date().plus(2)
        PersonaDocumentoTramite.findAllByFechaEnvioIsNotNullAndFechaRecepcionIsNull().each {pdt->
            if(pdt.tramite.de.departamento.id==dep.id)
                println "pdt --> "+pdt.id+" tramite "+pdt.tramite.id+" - ${pdt.tramite.de.departamento.descripcion} "+pdt.fechaEnvio+"  "+pdt.departamento+"   "+pdt.persona
            println "fecha bloqueo "+pdt.tramite.fechaBloqueo
            def fechaBloqueo = pdt.tramite.fechaBloqueo
            if(fechaBloqueo && fechaBloqueo<ahora){
                println "add tramites "+pdt
                tramites.add(pdt)
            }
        }
        [tramites:tramites ]
    }

}
