package happy.alertas

class AlertasController {

    def list(){
        def alertas = Alerta.findAllByPersonaAndFechaRecibidoIsNull(session.usuario,[sort:"fechaCreacion"])
        return [alertas:alertas]
    }

    def revisar(){
        def alerta = Alerta.get(params.id)
        alerta.fechaRecibido=new Date()
        alerta.save(flush: true)
        render "ok"
    }

}
