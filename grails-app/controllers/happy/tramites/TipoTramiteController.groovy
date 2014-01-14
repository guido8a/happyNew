package happy.tramites


class TipoTramiteController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def tipoTramiteInstanceList = TipoTramite.list(params)
        def tipoTramiteInstanceCount = TipoTramite.count()
        if(tipoTramiteInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        tipoTramiteInstanceList = TipoTramite.list(params)
        return [tipoTramiteInstanceList: tipoTramiteInstanceList, tipoTramiteInstanceCount: tipoTramiteInstanceCount]
    } //list

    def show_ajax() {
        if(params.id) {
            def tipoTramiteInstance = TipoTramite.get(params.id)
            if(!tipoTramiteInstance) {
                notFound_ajax()
                return
            }
            return [tipoTramiteInstance: tipoTramiteInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def tipoTramiteInstance = new TipoTramite(params)
        if(params.id) {
            tipoTramiteInstance = TipoTramite.get(params.id)
            if(!tipoTramiteInstance) {
                notFound_ajax()
                return
            }
        }
        return [tipoTramiteInstance: tipoTramiteInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        def tipoTramiteInstance = new TipoTramite()
        if(params.id) {
            tipoTramiteInstance = TipoTramite.get(params.id)
            if(!tipoTramiteInstance) {
                notFound_ajax()
                return
            }
        } //update
        tipoTramiteInstance.properties = params
        if(!tipoTramiteInstance.save(flush:true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} TipoTramite."
            msg += renderErrors(bean: tipoTramiteInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de TipoTramite exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if(params.id) {
            def tipoTramiteInstance = TipoTramite.get(params.id)
            if(tipoTramiteInstance) {
                try {
                    tipoTramiteInstance.delete(flush:true)
                    render "OK_Eliminación de TipoTramite exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar TipoTramite."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró TipoTramite."
    } //notFound para ajax

}
