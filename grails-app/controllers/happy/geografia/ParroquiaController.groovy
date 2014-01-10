package happy.geografia


class ParroquiaController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def parroquiaInstanceList = Parroquia.list(params)
        def parroquiaInstanceCount = Parroquia.count()
        if (parroquiaInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        parroquiaInstanceList = Parroquia.list(params)
        return [parroquiaInstanceList: parroquiaInstanceList, parroquiaInstanceCount: parroquiaInstanceCount]
    } //list

    def show_ajax() {
        if (params.id) {
            def parroquiaInstance = Parroquia.get(params.id)
            if (!parroquiaInstance) {
                notFound_ajax()
                return
            }
            return [parroquiaInstance: parroquiaInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def parroquiaInstance = new Parroquia(params)
        if (params.id) {
            parroquiaInstance = Parroquia.get(params.id)
            if (!parroquiaInstance) {
                notFound_ajax()
                return
            }
        }
        return [parroquiaInstance: parroquiaInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        def parroquiaInstance = new Parroquia()
        if (params.id) {
            parroquiaInstance = Parroquia.get(params.id)
            if (!parroquiaInstance) {
                notFound_ajax()
                return
            }
        } //update
        parroquiaInstance.properties = params
        if (!parroquiaInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} Parroquia."
            msg += renderErrors(bean: parroquiaInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de Parroquia exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def parroquiaInstance = Parroquia.get(params.id)
            if (parroquiaInstance) {
                try {
                    parroquiaInstance.delete(flush: true)
                    render "OK_Eliminación de Parroquia exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar Parroquia."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró Parroquia."
    } //notFound para ajax

}
