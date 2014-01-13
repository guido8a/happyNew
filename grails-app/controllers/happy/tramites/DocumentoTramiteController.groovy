package happy.tramites


class DocumentoTramiteController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def documentoTramiteInstanceList = DocumentoTramite.list(params)
        def documentoTramiteInstanceCount = DocumentoTramite.count()
        if(documentoTramiteInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        documentoTramiteInstanceList = DocumentoTramite.list(params)
        return [documentoTramiteInstanceList: documentoTramiteInstanceList, documentoTramiteInstanceCount: documentoTramiteInstanceCount]
    } //list

    def show_ajax() {
        if(params.id) {
            def documentoTramiteInstance = DocumentoTramite.get(params.id)
            if(!documentoTramiteInstance) {
                notFound_ajax()
                return
            }
            return [documentoTramiteInstance: documentoTramiteInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def documentoTramiteInstance = new DocumentoTramite(params)
        if(params.id) {
            documentoTramiteInstance = DocumentoTramite.get(params.id)
            if(!documentoTramiteInstance) {
                notFound_ajax()
                return
            }
        }
        return [documentoTramiteInstance: documentoTramiteInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        def documentoTramiteInstance = new DocumentoTramite()
        if(params.id) {
            documentoTramiteInstance = DocumentoTramite.get(params.id)
            if(!documentoTramiteInstance) {
                notFound_ajax()
                return
            }
        } //update
        documentoTramiteInstance.properties = params
        if(!documentoTramiteInstance.save(flush:true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} DocumentoTramite."
            msg += renderErrors(bean: documentoTramiteInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de DocumentoTramite exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if(params.id) {
            def documentoTramiteInstance = DocumentoTramite.get(params.id)
            if(documentoTramiteInstance) {
                try {
                    documentoTramiteInstance.delete(flush:true)
                    render "OK_Eliminación de DocumentoTramite exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar DocumentoTramite."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró DocumentoTramite."
    } //notFound para ajax

}
