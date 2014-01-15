package happy.tramites


class PermisoUsuarioController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def permisoUsuarioInstanceList = PermisoUsuario.list(params)
        def permisoUsuarioInstanceCount = PermisoUsuario.count()
        if(permisoUsuarioInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        permisoUsuarioInstanceList = PermisoUsuario.list(params)
        return [permisoUsuarioInstanceList: permisoUsuarioInstanceList, permisoUsuarioInstanceCount: permisoUsuarioInstanceCount]
    } //list

    def show_ajax() {
        if(params.id) {
            def permisoUsuarioInstance = PermisoUsuario.get(params.id)
            if(!permisoUsuarioInstance) {
                notFound_ajax()
                return
            }
            return [permisoUsuarioInstance: permisoUsuarioInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def permisoUsuarioInstance = new PermisoUsuario(params)
        if(params.id) {
            permisoUsuarioInstance = PermisoUsuario.get(params.id)
            if(!permisoUsuarioInstance) {
                notFound_ajax()
                return
            }
        }
        return [permisoUsuarioInstance: permisoUsuarioInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        params.each { k, v ->
            if (v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }
        def permisoUsuarioInstance = new PermisoUsuario()
        if(params.id) {
            permisoUsuarioInstance = PermisoUsuario.get(params.id)
            if(!permisoUsuarioInstance) {
                notFound_ajax()
                return
            }
        } //update
        permisoUsuarioInstance.properties = params
        if(!permisoUsuarioInstance.save(flush:true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} PermisoUsuario."
            msg += renderErrors(bean: permisoUsuarioInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de PermisoUsuario exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if(params.id) {
            def permisoUsuarioInstance = PermisoUsuario.get(params.id)
            if(permisoUsuarioInstance) {
                try {
                    permisoUsuarioInstance.delete(flush:true)
                    render "OK_Eliminación de PermisoUsuario exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar PermisoUsuario."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró PermisoUsuario."
    } //notFound para ajax

}
