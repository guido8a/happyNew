package happy.reportes

import happy.seguridad.Persona

class ReportesPersonalesController {

    def index() {
        def usu = Persona.get(session.usuario.id)
        return [persona: usu]
    }
}
