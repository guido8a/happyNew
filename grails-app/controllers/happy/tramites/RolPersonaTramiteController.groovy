package happy.tramites


class RolPersonaTramiteController extends happy.seguridad.Shield {

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
            def c = RolPersonaTramite.createCriteria()
            lista = c.list(params) {
                or {
                    ilike("codigo", "%" + params.search + "%")
                    ilike("descripcion", "%" + params.search + "%")
                }
            }
        } else {
            lista = RolPersonaTramite.list(params)
        }
        return lista
    }

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def rolPersonaTramiteInstanceList = getLista(params, false)
        def rolPersonaTramiteInstanceCount = getLista(params, true).size()
        if (rolPersonaTramiteInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        rolPersonaTramiteInstanceList = getLista(params, false)
        return [rolPersonaTramiteInstanceList: rolPersonaTramiteInstanceList, rolPersonaTramiteInstanceCount: rolPersonaTramiteInstanceCount, params: params]
    } //list

    def show_ajax() {
        if (params.id) {
            def rolPersonaTramiteInstance = RolPersonaTramite.get(params.id)
            if (!rolPersonaTramiteInstance) {
                notFound_ajax()
                return
            }
            return [rolPersonaTramiteInstance: rolPersonaTramiteInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def rolPersonaTramiteInstance = new RolPersonaTramite(params)
        if (params.id) {
            rolPersonaTramiteInstance = RolPersonaTramite.get(params.id)
            if (!rolPersonaTramiteInstance) {
                notFound_ajax()
                return
            }
        }
        return [rolPersonaTramiteInstance: rolPersonaTramiteInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        params.each { k, v ->
            if (v != "date.struct" && v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }
        def rolPersonaTramiteInstance = new RolPersonaTramite()
        if (params.id) {
            rolPersonaTramiteInstance = RolPersonaTramite.get(params.id)
            if (!rolPersonaTramiteInstance) {
                notFound_ajax()
                return
            }
        } //update
        rolPersonaTramiteInstance.properties = params
        if (!rolPersonaTramiteInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} RolPersonaTramite."
            msg += renderErrors(bean: rolPersonaTramiteInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de RolPersonaTramite exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def rolPersonaTramiteInstance = RolPersonaTramite.get(params.id)
            if (rolPersonaTramiteInstance) {
                try {
                    rolPersonaTramiteInstance.delete(flush: true)
                    render "OK_Eliminación de RolPersonaTramite exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar RolPersonaTramite."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró RolPersonaTramite."
    } //notFound para ajax

}
