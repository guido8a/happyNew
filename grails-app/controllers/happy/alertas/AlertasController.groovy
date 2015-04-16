package happy.alertas

class AlertasController extends happy.seguridad.Shield {

    def list() {
        def alertas
        if (session.usuario.esTriangulo()) {
            alertas = Alerta.findAllByDepartamentoAndFechaRecibidoIsNull(session.departamento, [sort: "fechaCreacion", order: 'desc'])
        } else {
            alertas = Alerta.findAllByPersonaAndFechaRecibidoIsNull(session.usuario, [sort: "fechaCreacion", order: 'desc'])
        }
//        alertas.sort {
//            it.fechaCreacion
//        }
        return [alertas: alertas]
    }

    def revisar() {
        def alerta = Alerta.get(params.id)
        alerta.fechaRecibido = new Date()
        alerta.save(flush: true)
        render "ok"
    }

}
