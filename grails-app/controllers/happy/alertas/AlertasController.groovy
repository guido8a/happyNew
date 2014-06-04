package happy.alertas

class AlertasController {

    def list(){
        def alertas
        if(session.usuario.esTriangulo()){
            alertas = Alerta.findAllByDepartamentoAndFechaRecibidoIsNull(session.departamento,[sort:"fechaCreacion"])
        }else{
            alertas = Alerta.findAllByPersonaAndFechaRecibidoIsNull(session.usuario,[sort:"fechaCreacion"])
        }
        alertas.sort{
            it.fechaCreacion
        }
        return [alertas:alertas]
    }

    def revisar(){
        def alerta = Alerta.get(params.id)
        alerta.fechaRecibido=new Date()
        alerta.save(flush: true)
        render "ok"
    }

}
