package happy


class TipoProcesoController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def tipoProcesoInstanceList = TipoProceso.list(params)
        def tipoProcesoInstanceCount = TipoProceso.count()
        if (tipoProcesoInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        tipoProcesoInstanceList = TipoProceso.list(params)
        return [tipoProcesoInstanceList: tipoProcesoInstanceList, tipoProcesoInstanceCount: tipoProcesoInstanceCount]
    } //list

    def show_ajax() {
        if (params.id) {
            def tipoProcesoInstance = TipoProceso.get(params.id)
            if (!tipoProcesoInstance) {
                notFound_ajax()
                return
            }
            return [tipoProcesoInstance: tipoProcesoInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def tipoProcesoInstance = new TipoProceso(params)
        if (params.id) {
            tipoProcesoInstance = TipoProceso.get(params.id)
            if (!tipoProcesoInstance) {
                notFound_ajax()
                return
            }
        }
        return [tipoProcesoInstance: tipoProcesoInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        def tipoProcesoInstance = new TipoProceso()
        if (params.id) {
            tipoProcesoInstance = TipoProceso.get(params.id)
            if (!tipoProcesoInstance) {
                notFound_ajax()
                return
            }
        } //update
        tipoProcesoInstance.properties = params
        if (!tipoProcesoInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} TipoProceso."
            msg += renderErrors(bean: tipoProcesoInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de TipoProceso exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def tipoProcesoInstance = TipoProceso.get(params.id)
            if (tipoProcesoInstance) {
                try {
                    tipoProcesoInstance.delete(flush: true)
                    render "OK_Eliminación de TipoProceso exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar TipoProceso."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró TipoProceso."
    } //notFound para ajax

}
