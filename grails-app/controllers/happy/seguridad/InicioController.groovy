package happy.seguridad

class InicioController {

    def index() {
        def usu = Persona.get(session.usuario.id)
        def now = new Date().clearTime()
        if ((usu.password == usu.cedula.encodeAsMD5()) || usu.fechaCambioPass <= now) {
            redirect(controller: "login", action: "cambiarPass")
            return
        }
    }


}
