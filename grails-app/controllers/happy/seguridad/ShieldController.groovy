package happy.seguridad

import happy.alertas.Alerta
import happy.tramites.EstadoTramite
import happy.tramites.Tramite

class ShieldController {
    def loginService
    def ataques = {
        def msn = "Se ha detectado que esta ejecutando una acción que atenta contra la seguridad del sistema.<br>Dicha accion sera registrada en su historial.<br>"
        render(view: "advertencia", model: [msn: msn])
    }

//    def alertaNoRecibidos () {
//
//        def usuario = session.usuario
//        def enviados = EstadoTramite.get(3)
//        def tramites = Tramite.findAllByEstadoTramite(enviados)
//
//        def fechaEnvio
//        def dosHoras =  7200000  //milisegundos
//        def ch = 172800000
//
//        def fecha
//        Date nuevaFecha
//        Date fechaLimite
//
//        def tramitesNoRecibidos = 0
//        def idTramitesNoRecibidos = []
//
//        def tramitesPasados = 0
//        def idTramitesPasados = []
//
//        tramites.each {
//
//            fechaEnvio = it.fechaEnvio
//            fecha = fechaEnvio.getTime()
//            nuevaFecha = new Date(fecha+dosHoras)
//            fechaLimite = new Date(fecha+ch)
//
//            if(nuevaFecha.before(new Date())){
//
//                tramitesNoRecibidos++
//                idTramitesNoRecibidos.add(it.id)
//            }
//            if(fechaLimite.before(new Date())){
//
//                tramitesPasados++
//                idTramitesPasados.add(it.id)
//            }
//        }
//
//        return [tramitesNoRecibidos: tramitesNoRecibidos, idTramitesNoRecibidos: idTramitesNoRecibidos, tramitesPasados: tramitesPasados, idTramitesPasados: idTramitesPasados ]
//
//    }


    def unauthorized = {

        def msn = "No autorizado"



    }
    def bloqueo = {

        if(params.dep){
            return [dep:session.departamento]
        }

    }


    def forbidden = {

        def alerta = new Alerta()
        alerta.accion=" "
        alerta.controlador=" "
        alerta.mensaje="Usuario: ${session.usuario.login} Fecha: ${new Date().format('dd-MM-yyyy hh:mm')}"
        alerta.fechaCreacion=new Date()
        alerta.save(flush: true)
        def msn = "Forbidden"
        if(flash.message)
            msn=flash.message
        flash.message=null
        return [msn: msn]

    }


    def notFound = {
        println ""
        def msn = "Esta tratando de ingresar a una accion no registrada en el sistema. Por favor use las opciones del menu para navegar por el sistema."
        return [msn: msn]
    }


    def internalServerError = {
        def msn = "Ha ocurrido un error interno."
        try {
            def er = new ErrorLog()
            er.fecha = new Date()
            er.error = request["exception"].message?.encodeAsHTML()
            er.causa = request["exception"].cause?.message?.encodeAsHTML()
            er.url = request["javax.servlet.forward.request_uri"];
            er.usuario = session.usuario
            er.save()
            // println " \n<===Error Aqui===> "+request["javax.servlet.forward.request_uri"]
            //println " \n<===Que eres pal burro?????? ===> "+request["exception"].message?.encodeAsHTML()
            //println " \n<===Causa===> "+request["exception"].cause?.message?.encodeAsHTML()

        } catch (e) {
            println "error en error " + e
        }
        return [msn: msn, error: true]
    }
    def comprobarPassword = {
        if (request.method == 'POST') {
//            println "comprobar password "+params
            def resp = loginService.autorizaciones(session.usuario, params.atrz)
            render(resp)
        } else {
            response.sendError(403)
        }
    }
}
