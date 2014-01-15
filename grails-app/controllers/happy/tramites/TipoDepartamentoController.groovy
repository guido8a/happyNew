package happy.tramites


class TipoDepartamentoController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def tipoDepartamentoInstanceList = TipoDepartamento.list(params)
        def tipoDepartamentoInstanceCount = TipoDepartamento.count()
        if(tipoDepartamentoInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        tipoDepartamentoInstanceList = TipoDepartamento.list(params)
        return [tipoDepartamentoInstanceList: tipoDepartamentoInstanceList, tipoDepartamentoInstanceCount: tipoDepartamentoInstanceCount]
    } //list

    def show_ajax() {
        if(params.id) {
            def tipoDepartamentoInstance = TipoDepartamento.get(params.id)
            if(!tipoDepartamentoInstance) {
                notFound_ajax()
                return
            }
            return [tipoDepartamentoInstance: tipoDepartamentoInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def tipoDepartamentoInstance = new TipoDepartamento(params)
        if(params.id) {
            tipoDepartamentoInstance = TipoDepartamento.get(params.id)
            if(!tipoDepartamentoInstance) {
                notFound_ajax()
                return
            }
        }
        return [tipoDepartamentoInstance: tipoDepartamentoInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        def tipoDepartamentoInstance = new TipoDepartamento()
        if(params.id) {
            tipoDepartamentoInstance = TipoDepartamento.get(params.id)
            if(!tipoDepartamentoInstance) {
                notFound_ajax()
                return
            }
        } //update
        tipoDepartamentoInstance.properties = params
        if(!tipoDepartamentoInstance.save(flush:true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} TipoDepartamento."
            msg += renderErrors(bean: tipoDepartamentoInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de TipoDepartamento exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if(params.id) {
            def tipoDepartamentoInstance = TipoDepartamento.get(params.id)
            if(tipoDepartamentoInstance) {
                try {
                    tipoDepartamentoInstance.delete(flush:true)
                    render "OK_Eliminación de TipoDepartamento exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar TipoDepartamento."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró TipoDepartamento."
    } //notFound para ajax

}
