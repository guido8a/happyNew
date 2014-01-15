package happy.tramites


class TipoDocumentoController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def tipoDocumentoInstanceList = TipoDocumento.list(params)
        def tipoDocumentoInstanceCount = TipoDocumento.count()
        if(tipoDocumentoInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        tipoDocumentoInstanceList = TipoDocumento.list(params)
        return [tipoDocumentoInstanceList: tipoDocumentoInstanceList, tipoDocumentoInstanceCount: tipoDocumentoInstanceCount]
    } //list

    def show_ajax() {
        if(params.id) {
            def tipoDocumentoInstance = TipoDocumento.get(params.id)
            if(!tipoDocumentoInstance) {
                notFound_ajax()
                return
            }
            return [tipoDocumentoInstance: tipoDocumentoInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def tipoDocumentoInstance = new TipoDocumento(params)
        if(params.id) {
            tipoDocumentoInstance = TipoDocumento.get(params.id)
            if(!tipoDocumentoInstance) {
                notFound_ajax()
                return
            }
        }
        return [tipoDocumentoInstance: tipoDocumentoInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        params.each { k, v ->
            if (v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }
        def tipoDocumentoInstance = new TipoDocumento()
        if(params.id) {
            tipoDocumentoInstance = TipoDocumento.get(params.id)
            if(!tipoDocumentoInstance) {
                notFound_ajax()
                return
            }
        } //update
        tipoDocumentoInstance.properties = params
        if(!tipoDocumentoInstance.save(flush:true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} TipoDocumento."
            msg += renderErrors(bean: tipoDocumentoInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de TipoDocumento exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if(params.id) {
            def tipoDocumentoInstance = TipoDocumento.get(params.id)
            if(tipoDocumentoInstance) {
                try {
                    tipoDocumentoInstance.delete(flush:true)
                    render "OK_Eliminación de TipoDocumento exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar TipoDocumento."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró TipoDocumento."
    } //notFound para ajax

}
