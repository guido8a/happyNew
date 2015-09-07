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
        alerta.accion=session.an
        alerta.controlador=session.cn
        alerta.mensaje="ALERTA: ingreso restringido. Usuario: ${session.usuario.login} Fecha: ${new Date().format('dd-MM-yyyy HH:mm')}"
        alerta.fechaCreacion=new Date()
        alerta.save(flush: true)
        def msn = "Forbidden"
        if(flash.message)
            msn=flash.message
        flash.message=null
        return [msn: msn]

    }


    def notFound = {
//        println "notFound -- Shield  params: $params"
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
        } catch (e) {
            println "error en error " + e
        }
        return [msn: msn, error: true]
    }
    def comprobarPassword = {
        if (request.method == 'POST') {
            def resp = loginService.autorizaciones(session.usuario, params.atrz)
            render(resp)
        } else {
            response.sendError(403)
        }
    }
}
