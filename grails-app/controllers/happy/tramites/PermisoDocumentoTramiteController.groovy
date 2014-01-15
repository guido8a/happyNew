package happy.tramites


class PermisoDocumentoTramiteController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def permisoDocumentoTramiteInstanceList = PersonaDocumentoTramite.list(params)
        def permisoDocumentoTramiteInstanceCount = PersonaDocumentoTramite.count()
        if (permisoDocumentoTramiteInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        permisoDocumentoTramiteInstanceList = PersonaDocumentoTramite.list(params)
        return [permisoDocumentoTramiteInstanceList: permisoDocumentoTramiteInstanceList, permisoDocumentoTramiteInstanceCount: permisoDocumentoTramiteInstanceCount]
    } //list

    def show_ajax() {
        if (params.id) {
            def permisoDocumentoTramiteInstance = PersonaDocumentoTramite.get(params.id)
            if (!permisoDocumentoTramiteInstance) {
                notFound_ajax()
                return
            }
            return [permisoDocumentoTramiteInstance: permisoDocumentoTramiteInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def permisoDocumentoTramiteInstance = new PersonaDocumentoTramite(params)
        if (params.id) {
            permisoDocumentoTramiteInstance = PersonaDocumentoTramite.get(params.id)
            if (!permisoDocumentoTramiteInstance) {
                notFound_ajax()
                return
            }
        }
        return [permisoDocumentoTramiteInstance: permisoDocumentoTramiteInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        params.each { k, v ->
            if (v != "date.struct" && v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }
        def permisoDocumentoTramiteInstance = new PersonaDocumentoTramite()
        if (params.id) {
            permisoDocumentoTramiteInstance = PersonaDocumentoTramite.get(params.id)
            if (!permisoDocumentoTramiteInstance) {
                notFound_ajax()
                return
            }
        } //update
        permisoDocumentoTramiteInstance.properties = params
        if (!permisoDocumentoTramiteInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} PersonaDocumentoTramite."
            msg += renderErrors(bean: permisoDocumentoTramiteInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de PersonaDocumentoTramite exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def permisoDocumentoTramiteInstance = PersonaDocumentoTramite.get(params.id)
            if (permisoDocumentoTramiteInstance) {
                try {
                    permisoDocumentoTramiteInstance.delete(flush: true)
                    render "OK_Eliminación de PersonaDocumentoTramite exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar PersonaDocumentoTramite."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró PersonaDocumentoTramite."
    } //notFound para ajax

}
