package happy.alertas

class AlertasController {

    def list(){
        def alertas = Alerta.findAllByPersonaAndFechaRecibidoIsNull(session.usuario,[sort:"fechaCreacion"])
        if(session.usuario.esTriangulo()){
            def temp = Alerta.findAllByDepartamentoAndFechaRecibidoIsNull(session.departamento,[sort:"fechaCreacion"])
            if(temp)
                alertas+=temp
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
