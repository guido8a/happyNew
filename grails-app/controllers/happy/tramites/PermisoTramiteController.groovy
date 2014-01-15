package happy.tramites


class PermisoTramiteController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def permisoTramiteInstanceList = PermisoTramite.list(params)
        def permisoTramiteInstanceCount = PermisoTramite.count()
        if(permisoTramiteInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        permisoTramiteInstanceList = PermisoTramite.list(params)
        return [permisoTramiteInstanceList: permisoTramiteInstanceList, permisoTramiteInstanceCount: permisoTramiteInstanceCount]
    } //list

    def show_ajax() {
        if(params.id) {
            def permisoTramiteInstance = PermisoTramite.get(params.id)
            if(!permisoTramiteInstance) {
                notFound_ajax()
                return
            }
            return [permisoTramiteInstance: permisoTramiteInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def permisoTramiteInstance = new PermisoTramite(params)
        if(params.id) {
            permisoTramiteInstance = PermisoTramite.get(params.id)
            if(!permisoTramiteInstance) {
                notFound_ajax()
                return
            }
        }
        return [permisoTramiteInstance: permisoTramiteInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        params.each { k, v ->
            if (v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }
        def permisoTramiteInstance = new PermisoTramite()
        if(params.id) {
            permisoTramiteInstance = PermisoTramite.get(params.id)
            if(!permisoTramiteInstance) {
                notFound_ajax()
                return
            }
        } //update
        permisoTramiteInstance.properties = params
        if(!permisoTramiteInstance.save(flush:true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} PermisoTramite."
            msg += renderErrors(bean: permisoTramiteInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de PermisoTramite exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if(params.id) {
            def permisoTramiteInstance = PermisoTramite.get(params.id)
            if(permisoTramiteInstance) {
                try {
                    permisoTramiteInstance.delete(flush:true)
                    render "OK_Eliminación de PermisoTramite exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar PermisoTramite."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró PermisoTramite."
    } //notFound para ajax

}
