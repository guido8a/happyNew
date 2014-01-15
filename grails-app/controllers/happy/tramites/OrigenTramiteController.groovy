package happy.tramites


class OrigenTramiteController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def origenTramiteInstanceList = OrigenTramite.list(params)
        def origenTramiteInstanceCount = OrigenTramite.count()
        if(origenTramiteInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        origenTramiteInstanceList = OrigenTramite.list(params)
        return [origenTramiteInstanceList: origenTramiteInstanceList, origenTramiteInstanceCount: origenTramiteInstanceCount]
    } //list

    def show_ajax() {
        if(params.id) {
            def origenTramiteInstance = OrigenTramite.get(params.id)
            if(!origenTramiteInstance) {
                notFound_ajax()
                return
            }
            return [origenTramiteInstance: origenTramiteInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def origenTramiteInstance = new OrigenTramite(params)
        if(params.id) {
            origenTramiteInstance = OrigenTramite.get(params.id)
            if(!origenTramiteInstance) {
                notFound_ajax()
                return
            }
        }
        return [origenTramiteInstance: origenTramiteInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        params.each { k, v ->
            if (v != "date.struct" && v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }
        def origenTramiteInstance = new OrigenTramite()
        if(params.id) {
            origenTramiteInstance = OrigenTramite.get(params.id)
            if(!origenTramiteInstance) {
                notFound_ajax()
                return
            }
        } //update
        origenTramiteInstance.properties = params
        if(!origenTramiteInstance.save(flush:true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} OrigenTramite."
            msg += renderErrors(bean: origenTramiteInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de OrigenTramite exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if(params.id) {
            def origenTramiteInstance = OrigenTramite.get(params.id)
            if(origenTramiteInstance) {
                try {
                    origenTramiteInstance.delete(flush:true)
                    render "OK_Eliminación de OrigenTramite exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar OrigenTramite."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró OrigenTramite."
    } //notFound para ajax

}
