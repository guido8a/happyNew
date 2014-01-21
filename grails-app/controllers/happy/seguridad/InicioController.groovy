package happy.seguridad

class InicioController {

    def index() {
        def usu = Persona.get(session.usuario.id)
        def now = new Date().clearTime()

        if ((usu.password == usu.cedula.encodeAsMD5()) || usu.fechaCambioPass <= now) {
//            println((usu.password == usu.cedula.encodeAsMD5()) ? "El pass es la cedula" : "pass ok")
//            println(usu.fechaCambioPass >= now ? "Fecha cambio: ${usu.fechaCambioPass.format('dd-MM-yyyy')}" : "fecha ok")
            redirect(controller: "login", action: "cambiarPass")
            return
        }

    }


}
