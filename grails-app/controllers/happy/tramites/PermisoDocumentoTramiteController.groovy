package happy.tramites


class PermisoDocumentoTramiteController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def permisoDocumentoTramiteInstanceList = PermisoDocumentoTramite.list(params)
        def permisoDocumentoTramiteInstanceCount = PermisoDocumentoTramite.count()
        if (permisoDocumentoTramiteInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        permisoDocumentoTramiteInstanceList = PermisoDocumentoTramite.list(params)
        return [permisoDocumentoTramiteInstanceList: permisoDocumentoTramiteInstanceList, permisoDocumentoTramiteInstanceCount: permisoDocumentoTramiteInstanceCount]
    } //list

    def show_ajax() {
        if (params.id) {
            def permisoDocumentoTramiteInstance = PermisoDocumentoTramite.get(params.id)
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
        def permisoDocumentoTramiteInstance = new PermisoDocumentoTramite(params)
        if (params.id) {
            permisoDocumentoTramiteInstance = PermisoDocumentoTramite.get(params.id)
            if (!permisoDocumentoTramiteInstance) {
                notFound_ajax()
                return
            }
        }
        return [permisoDocumentoTramiteInstance: permisoDocumentoTramiteInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        def permisoDocumentoTramiteInstance = new PermisoDocumentoTramite()
        if (params.id) {
            permisoDocumentoTramiteInstance = PermisoDocumentoTramite.get(params.id)
            if (!permisoDocumentoTramiteInstance) {
                notFound_ajax()
                return
            }
        } //update
        permisoDocumentoTramiteInstance.properties = params
        if (!permisoDocumentoTramiteInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} PermisoDocumentoTramite."
            msg += renderErrors(bean: permisoDocumentoTramiteInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de PermisoDocumentoTramite exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def permisoDocumentoTramiteInstance = PermisoDocumentoTramite.get(params.id)
            if (permisoDocumentoTramiteInstance) {
                try {
                    permisoDocumentoTramiteInstance.delete(flush: true)
                    render "OK_Eliminación de PermisoDocumentoTramite exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar PermisoDocumentoTramite."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró PermisoDocumentoTramite."
    } //notFound para ajax

}
