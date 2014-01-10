package happy.geografia


class CantonController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def cantonInstanceList = Canton.list(params)
        def cantonInstanceCount = Canton.count()
        if(cantonInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        cantonInstanceList = Canton.list(params)
        return [cantonInstanceList: cantonInstanceList, cantonInstanceCount: cantonInstanceCount]
    } //list

    def show_ajax() {
        if(params.id) {
            def cantonInstance = Canton.get(params.id)
            if(!cantonInstance) {
                notFound_ajax()
                return
            }
            return [cantonInstance: cantonInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def cantonInstance = new Canton(params)
        if(params.id) {
            cantonInstance = Canton.get(params.id)
            if(!cantonInstance) {
                notFound_ajax()
                return
            }
        }
        return [cantonInstance: cantonInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        def cantonInstance = new Canton()
        if(params.id) {
            cantonInstance = Canton.get(params.id)
            if(!cantonInstance) {
                notFound_ajax()
                return
            }
        } //update
        cantonInstance.properties = params
        if(!cantonInstance.save(flush:true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} Canton."
            msg += renderErrors(bean: cantonInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de Canton exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if(params.id) {
            def cantonInstance = Canton.get(params.id)
            if(cantonInstance) {
                try {
                    cantonInstance.delete(flush:true)
                    render "OK_Eliminación de Canton exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar Canton."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró Canton."
    } //notFound para ajax

}
