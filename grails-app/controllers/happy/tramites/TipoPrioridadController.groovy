package happy.tramites


class TipoPrioridadController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def tipoPrioridadInstanceList = TipoPrioridad.list(params)
        def tipoPrioridadInstanceCount = TipoPrioridad.count()
        if(tipoPrioridadInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        tipoPrioridadInstanceList = TipoPrioridad.list(params)
        return [tipoPrioridadInstanceList: tipoPrioridadInstanceList, tipoPrioridadInstanceCount: tipoPrioridadInstanceCount]
    } //list

    def show_ajax() {
        if(params.id) {
            def tipoPrioridadInstance = TipoPrioridad.get(params.id)
            if(!tipoPrioridadInstance) {
                notFound_ajax()
                return
            }
            return [tipoPrioridadInstance: tipoPrioridadInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def tipoPrioridadInstance = new TipoPrioridad(params)
        if(params.id) {
            tipoPrioridadInstance = TipoPrioridad.get(params.id)
            if(!tipoPrioridadInstance) {
                notFound_ajax()
                return
            }
        }
        return [tipoPrioridadInstance: tipoPrioridadInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        params.each { k, v ->
            if (v != "date.struct" && v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }
        def tipoPrioridadInstance = new TipoPrioridad()
        if(params.id) {
            tipoPrioridadInstance = TipoPrioridad.get(params.id)
            if(!tipoPrioridadInstance) {
                notFound_ajax()
                return
            }
        } //update
        tipoPrioridadInstance.properties = params
        if(!tipoPrioridadInstance.save(flush:true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} TipoPrioridad."
            msg += renderErrors(bean: tipoPrioridadInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de TipoPrioridad exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if(params.id) {
            def tipoPrioridadInstance = TipoPrioridad.get(params.id)
            if(tipoPrioridadInstance) {
                try {
                    tipoPrioridadInstance.delete(flush:true)
                    render "OK_Eliminación de TipoPrioridad exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar TipoPrioridad."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró TipoPrioridad."
    } //notFound para ajax

}
