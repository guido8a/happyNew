package happy.tramites

class TramiteAnexosController {

    def index() {}

    def anexo() {
        def tramite = Tramite.get(params.id)
        return [tramite: tramite]
    }

    def uploadFile() {
        def tramite = Tramite.get(params.id)
        def path = servletContext.getRealPath("/") + "anexos/" + tramite.id    //web-app/archivos
        new File(path).mkdirs()

        def f = request.getFile('file')  //archivo = name del input type file

        def okContents = ['image/png': "png", 'image/jpeg': "jpeg", 'image/jpg': "jpg"]

        if (f && !f.empty) {
            def fileName = f.getOriginalFilename() //nombre original del archivo
            def ext
//            if (okContents.containsKey(f.getContentType())) {

//            } //ok contents
        } //f && !f.empty

        println params
        render "OK"
    }
}
