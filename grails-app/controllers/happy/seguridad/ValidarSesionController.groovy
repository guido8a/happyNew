package happy.seguridad

class ValidarSesionController {
    def validarSesion = {
        if (session.usuario) {
            render "OK"
        } else {
            render "NO"
        }
    }
}
