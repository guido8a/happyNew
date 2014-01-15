package happy.tramites


class PersonaDocumentoTramiteController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def personaDocumentoTramiteInstanceList = PersonaDocumentoTramite.list(params)
        def personaDocumentoTramiteInstanceCount = PersonaDocumentoTramite.count()
        if (personaDocumentoTramiteInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        personaDocumentoTramiteInstanceList = PersonaDocumentoTramite.list(params)
        return [personaDocumentoTramiteInstanceList: personaDocumentoTramiteInstanceList, personaDocumentoTramiteInstanceCount: personaDocumentoTramiteInstanceCount]
    } //list

    def show_ajax() {
        if (params.id) {
            def personaDocumentoTramiteInstance = PersonaDocumentoTramite.get(params.id)
            if (!personaDocumentoTramiteInstance) {
                notFound_ajax()
                return
            }
            return [personaDocumentoTramiteInstance: personaDocumentoTramiteInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def personaDocumentoTramiteInstance = new PersonaDocumentoTramite(params)
        if (params.id) {
            personaDocumentoTramiteInstance = PersonaDocumentoTramite.get(params.id)
            if (!personaDocumentoTramiteInstance) {
                notFound_ajax()
                return
            }
        }
        return [personaDocumentoTramiteInstance: personaDocumentoTramiteInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        params.each { k, v ->
            if (v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }
        def personaDocumentoTramiteInstance = new PersonaDocumentoTramite()
        if (params.id) {
            personaDocumentoTramiteInstance = PersonaDocumentoTramite.get(params.id)
            if (!personaDocumentoTramiteInstance) {
                notFound_ajax()
                return
            }
        } //update
        personaDocumentoTramiteInstance.properties = params
        if (!personaDocumentoTramiteInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} PersonaDocumentoTramite."
            msg += renderErrors(bean: personaDocumentoTramiteInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de PersonaDocumentoTramite exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def personaDocumentoTramiteInstance = PersonaDocumentoTramite.get(params.id)
            if (personaDocumentoTramiteInstance) {
                try {
                    personaDocumentoTramiteInstance.delete(flush: true)
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
