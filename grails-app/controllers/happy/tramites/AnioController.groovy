package happy.tramites


class AnioController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def anioInstanceList = Anio.list(params)
        def anioInstanceCount = Anio.count()
        if(anioInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        anioInstanceList = Anio.list(params)
        return [anioInstanceList: anioInstanceList, anioInstanceCount: anioInstanceCount]
    } //list

    def show_ajax() {
        if(params.id) {
            def anioInstance = Anio.get(params.id)
            if(!anioInstance) {
                notFound_ajax()
                return
            }
            return [anioInstance: anioInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def anioInstance = new Anio(params)
        if(params.id) {
            anioInstance = Anio.get(params.id)
            if(!anioInstance) {
                notFound_ajax()
                return
            }
        }
        return [anioInstance: anioInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        params.each { k, v ->
            if (v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }
        def anioInstance = new Anio()
        if(params.id) {
            anioInstance = Anio.get(params.id)
            if(!anioInstance) {
                notFound_ajax()
                return
            }
        } //update
        anioInstance.properties = params
        if(!anioInstance.save(flush:true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} Anio."
            msg += renderErrors(bean: anioInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de Anio exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if(params.id) {
            def anioInstance = Anio.get(params.id)
            if(anioInstance) {
                try {
                    anioInstance.delete(flush:true)
                    render "OK_Eliminación de Anio exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar Anio."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró Anio."
    } //notFound para ajax

}
