package happy.tramites

class TramiteExportController {

    def crearPdf() {
        def tramite = Tramite.get(params.id)

        def texto = tramite.texto

        render "OK"
    }
}
