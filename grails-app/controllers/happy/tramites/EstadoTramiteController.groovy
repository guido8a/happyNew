package happy.tramites


class EstadoTramiteController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def getLista(params, all) {
        params = params.clone()
        if (all) {
            params.remove("offset")
            params.remove("max")
        }
        def lista
        if (params.search) {
            def c = EstadoTramite.createCriteria()
            lista = c.list(params) {
                or {
                    ilike("codigo", "%" + params.search + "%")
                    ilike("descripcion", "%" + params.search + "%")
                }
            }
        } else {
            lista = EstadoTramite.list(params)
        }
        return lista
    }

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def estadoTramiteInstanceList = getLista(params, false)
        def estadoTramiteInstanceCount = getLista(params, true).size()
        if (estadoTramiteInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        estadoTramiteInstanceList = getLista(params, false)
        return [estadoTramiteInstanceList: estadoTramiteInstanceList, estadoTramiteInstanceCount: estadoTramiteInstanceCount, params: params]
    } //list

    def show_ajax() {
        if (params.id) {
            def estadoTramiteInstance = EstadoTramite.get(params.id)
            if (!estadoTramiteInstance) {
                notFound_ajax()
                return
            }
            return [estadoTramiteInstance: estadoTramiteInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def estadoTramiteInstance = new EstadoTramite(params)
        if (params.id) {
            estadoTramiteInstance = EstadoTramite.get(params.id)
            if (!estadoTramiteInstance) {
                notFound_ajax()
                return
            }
        }
        return [estadoTramiteInstance: estadoTramiteInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        params.each { k, v ->
            if (v != "date.struct" && v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }
        def estadoTramiteInstance = new EstadoTramite()
        if (params.id) {
            estadoTramiteInstance = EstadoTramite.get(params.id)
            if (!estadoTramiteInstance) {
                notFound_ajax()
                return
            }
        } //update
        estadoTramiteInstance.properties = params
        if (!estadoTramiteInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} EstadoTramite."
            msg += renderErrors(bean: estadoTramiteInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de EstadoTramite exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def estadoTramiteInstance = EstadoTramite.get(params.id)
            if (estadoTramiteInstance) {
                try {
                    estadoTramiteInstance.delete(flush: true)
                    render "OK_Eliminación de EstadoTramite exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar EstadoTramite."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró EstadoTramite."
    } //notFound para ajax

}
