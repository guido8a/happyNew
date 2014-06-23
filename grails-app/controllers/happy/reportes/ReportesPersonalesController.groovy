package happy.reportes

import happy.seguridad.Persona

class ReportesPersonalesController {

    def personal() {
        def usu = Persona.get(session.usuario.id)
        return [persona: usu]
    }

    def jefe() {
        def usu = Persona.get(session.usuario.id)
        if (usu.puedeJefe) {
            return [persona: usu]
        } else {
            redirect(action: "personal")
        }
    }

    def director() {
        def usu = Persona.get(session.usuario.id)
        if (usu.puedeDirector) {
            return [persona: usu]
        } else {
            redirect(action: "personal")
        }
    }
}
