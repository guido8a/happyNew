package happy.seguridad

import happy.tramites.BloqueosJob

class InicioController extends happy.seguridad.Shield {
    def dbConnectionService

    def index() {
        if (session.usuario.getPuedeDirector()) {
            redirect(controller: "retrasadosWeb", action: "reporteRetrasadosConsolidadoDir", params: [dpto: Persona.get(session.usuario.id).departamento.id, inicio: "1", dir: "1"])
        } else {
            if (session.usuario.getPuedeJefe()) {
                redirect(controller: "retrasadosWeb", action: "reporteRetrasadosConsolidado", params: [dpto: Persona.get(session.usuario.id).departamento.id, inicio: "1"])
            } else {
            }

        }

        def job = new BloqueosJob()
        job.execute()
        job = null

    }

    def parametros = {

        if (session.usuario.puedeAdmin) {
            return []
        } else {
            flash.message = "Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será registrada."
            response.sendError(403)
        }
    }
}
