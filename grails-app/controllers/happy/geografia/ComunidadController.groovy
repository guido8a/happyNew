package happy.geografia


class ComunidadController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def comunidadInstanceList = Comunidad.list(params)
        def comunidadInstanceCount = Comunidad.count()
        if (comunidadInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        comunidadInstanceList = Comunidad.list(params)
        return [comunidadInstanceList: comunidadInstanceList, comunidadInstanceCount: comunidadInstanceCount]
    } //list

    def show_ajax() {
        if (params.id) {
            def comunidadInstance = Comunidad.get(params.id)
            if (!comunidadInstance) {
                notFound_ajax()
                return
            }
            return [comunidadInstance: comunidadInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def comunidadInstance = new Comunidad(params)
        if (params.id) {
            comunidadInstance = Comunidad.get(params.id)
            if (!comunidadInstance) {
                notFound_ajax()
                return
            }
        }
        return [comunidadInstance: comunidadInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        def comunidadInstance = new Comunidad()
        if (params.id) {
            comunidadInstance = Comunidad.get(params.id)
            if (!comunidadInstance) {
                notFound_ajax()
                return
            }
        } //update
        comunidadInstance.properties = params
        if (!comunidadInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} Comunidad."
            msg += renderErrors(bean: comunidadInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de Comunidad exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def comunidadInstance = Comunidad.get(params.id)
            if (comunidadInstance) {
                try {
                    comunidadInstance.delete(flush: true)
                    render "OK_Eliminación de Comunidad exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar Comunidad."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró Comunidad."
    } //notFound para ajax

}
