package happy.reportes

import happy.seguridad.Persona;
import happy.seguridad.Shield;

class ReportesPersonalesController extends Shield {

    def personal() {
        def usu = Persona.get(session.usuario.id)
        return [persona: usu]
    }
}
