package happy.tramites


class NumeroController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def numeroInstanceList = Numero.list(params)
        def numeroInstanceCount = Numero.count()
        if(numeroInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        numeroInstanceList = Numero.list(params)
        return [numeroInstanceList: numeroInstanceList, numeroInstanceCount: numeroInstanceCount]
    } //list

    def show_ajax() {
        if(params.id) {
            def numeroInstance = Numero.get(params.id)
            if(!numeroInstance) {
                notFound_ajax()
                return
            }
            return [numeroInstance: numeroInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def numeroInstance = new Numero(params)
        if(params.id) {
            numeroInstance = Numero.get(params.id)
            if(!numeroInstance) {
                notFound_ajax()
                return
            }
        }
        return [numeroInstance: numeroInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        params.each { k, v ->
            if (v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }
        def numeroInstance = new Numero()
        if(params.id) {
            numeroInstance = Numero.get(params.id)
            if(!numeroInstance) {
                notFound_ajax()
                return
            }
        } //update
        numeroInstance.properties = params
        if(!numeroInstance.save(flush:true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} Numero."
            msg += renderErrors(bean: numeroInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de Numero exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if(params.id) {
            def numeroInstance = Numero.get(params.id)
            if(numeroInstance) {
                try {
                    numeroInstance.delete(flush:true)
                    render "OK_Eliminación de Numero exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar Numero."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró Numero."
    } //notFound para ajax

}
