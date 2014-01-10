package happy.geografia


class ZonaController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def zonaInstanceList = Zona.list(params)
        def zonaInstanceCount = Zona.count()
        if (zonaInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        zonaInstanceList = Zona.list(params)
        return [zonaInstanceList: zonaInstanceList, zonaInstanceCount: zonaInstanceCount]
    } //list

    def show_ajax() {
        if (params.id) {
            def zonaInstance = Zona.get(params.id)
            if (!zonaInstance) {
                notFound_ajax()
                return
            }
            return [zonaInstance: zonaInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def zonaInstance = new Zona(params)
        if (params.id) {
            zonaInstance = Zona.get(params.id)
            if (!zonaInstance) {
                notFound_ajax()
                return
            }
        }
        return [zonaInstance: zonaInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        def zonaInstance = new Zona()
        if (params.id) {
            zonaInstance = Zona.get(params.id)
            if (!zonaInstance) {
                notFound_ajax()
                return
            }
        } //update
        zonaInstance.properties = params
        if (!zonaInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} Zona."
            msg += renderErrors(bean: zonaInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de Zona exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def zonaInstance = Zona.get(params.id)
            if (zonaInstance) {
                try {
                    zonaInstance.delete(flush: true)
                    render "OK_Eliminación de Zona exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar Zona."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró Zona."
    } //notFound para ajax

}
