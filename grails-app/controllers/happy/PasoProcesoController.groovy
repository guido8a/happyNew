package happy

class PasoProcesoController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def pasoProcesoInstanceList = PasoProceso.list(params)
        def pasoProcesoInstanceCount = PasoProceso.count()
        if (pasoProcesoInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        pasoProcesoInstanceList = PasoProceso.list(params)
        return [pasoProcesoInstanceList: pasoProcesoInstanceList, pasoProcesoInstanceCount: pasoProcesoInstanceCount]
    } //list

    def show_ajax() {
        if (params.id) {
            def pasoProcesoInstance = PasoProceso.get(params.id)
            if (!pasoProcesoInstance) {
                notFound_ajax()
                return
            }
            return [pasoProcesoInstance: pasoProcesoInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def pasoProcesoInstance = new PasoProceso(params)
        if (params.id) {
            pasoProcesoInstance = PasoProceso.get(params.id)
            if (!pasoProcesoInstance) {
                notFound_ajax()
                return
            }
        }
        return [pasoProcesoInstance: pasoProcesoInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        def pasoProcesoInstance = new PasoProceso()
        if (params.id) {
            pasoProcesoInstance = PasoProceso.get(params.id)
            if (!pasoProcesoInstance) {
                notFound_ajax()
                return
            }
        } //update
        pasoProcesoInstance.properties = params
        if (!pasoProcesoInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} PasoProceso."
            msg += renderErrors(bean: pasoProcesoInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de PasoProceso exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def pasoProcesoInstance = PasoProceso.get(params.id)
            if (pasoProcesoInstance) {
                try {
                    pasoProcesoInstance.delete(flush: true)
                    render "OK_Eliminación de PasoProceso exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar PasoProceso."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró PasoProceso."
    } //notFound para ajax

}
