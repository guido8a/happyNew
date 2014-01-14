package happy.tramites


class TramiteController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def tramiteInstanceList = Tramite.list(params)
        def tramiteInstanceCount = Tramite.count()
        if(tramiteInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        tramiteInstanceList = Tramite.list(params)
        return [tramiteInstanceList: tramiteInstanceList, tramiteInstanceCount: tramiteInstanceCount]
    } //list

    def show_ajax() {
        if(params.id) {
            def tramiteInstance = Tramite.get(params.id)
            if(!tramiteInstance) {
                notFound_ajax()
                return
            }
            return [tramiteInstance: tramiteInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def tramiteInstance = new Tramite(params)
        if(params.id) {
            tramiteInstance = Tramite.get(params.id)
            if(!tramiteInstance) {
                notFound_ajax()
                return
            }
        }
        return [tramiteInstance: tramiteInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        def tramiteInstance = new Tramite()
        if(params.id) {
            tramiteInstance = Tramite.get(params.id)
            if(!tramiteInstance) {
                notFound_ajax()
                return
            }
        } //update
        tramiteInstance.properties = params
        if(!tramiteInstance.save(flush:true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} Tramite."
            msg += renderErrors(bean: tramiteInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de Tramite exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if(params.id) {
            def tramiteInstance = Tramite.get(params.id)
            if(tramiteInstance) {
                try {
                    tramiteInstance.delete(flush:true)
                    render "OK_Eliminación de Tramite exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar Tramite."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró Tramite."
    } //notFound para ajax

}
