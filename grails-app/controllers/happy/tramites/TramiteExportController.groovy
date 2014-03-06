package happy.tramites

class TramiteExportController {

    def crearPdf() {
        def tramite = Tramite.get(params.id)

        def texto = tramite.texto

        def de = tramite.de

        def rolPara = RolPersonaTramite.findByCodigo('R001')
        def rolCC = RolPersonaTramite.findByCodigo('R002')

        def para = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(tramite, rolPara)
        def cc = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(tramite, rolCC)

        render "OK"
    }
}
