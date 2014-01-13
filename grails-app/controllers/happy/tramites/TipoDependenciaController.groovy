package happy.tramites


class TipoDependenciaController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def tipoDependenciaInstanceList = TipoDependencia.list(params)
        def tipoDependenciaInstanceCount = TipoDependencia.count()
        if (tipoDependenciaInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        tipoDependenciaInstanceList = TipoDependencia.list(params)
        return [tipoDependenciaInstanceList: tipoDependenciaInstanceList, tipoDependenciaInstanceCount: tipoDependenciaInstanceCount]
    } //list

    def show_ajax() {
        if (params.id) {
            def tipoDependenciaInstance = TipoDependencia.get(params.id)
            if (!tipoDependenciaInstance) {
                notFound_ajax()
                return
            }
            return [tipoDependenciaInstance: tipoDependenciaInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def tipoDependenciaInstance = new TipoDependencia(params)
        if (params.id) {
            tipoDependenciaInstance = TipoDependencia.get(params.id)
            if (!tipoDependenciaInstance) {
                notFound_ajax()
                return
            }
        }
        return [tipoDependenciaInstance: tipoDependenciaInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        def tipoDependenciaInstance = new TipoDependencia()
        if (params.id) {
            tipoDependenciaInstance = TipoDependencia.get(params.id)
            if (!tipoDependenciaInstance) {
                notFound_ajax()
                return
            }
        } //update
        tipoDependenciaInstance.properties = params
        if (!tipoDependenciaInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} TipoDependencia."
            msg += renderErrors(bean: tipoDependenciaInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de TipoDependencia exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def tipoDependenciaInstance = TipoDependencia.get(params.id)
            if (tipoDependenciaInstance) {
                try {
                    tipoDependenciaInstance.delete(flush: true)
                    render "OK_Eliminación de TipoDependencia exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar TipoDependencia."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró TipoDependencia."
    } //notFound para ajax

}
