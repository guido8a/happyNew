package happy.tramites


class PermisoUsuarioController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def getLista(params, all) {
        params = params.clone()
        if (all) {
            params.remove("offset")
            params.remove("max")
        }
        def lista
        if (params.search) {
            def c = PermisoUsuario.createCriteria()
            lista = c.list(params) {
                or {
                    persona {
                        or {
                            ilike("cedula", "%" + params.search + "%")
                            ilike("nombre", "%" + params.search + "%")
                            ilike("apellido", "%" + params.search + "%")
                            ilike("sigla", "%" + params.search + "%")
                            ilike("titulo", "%" + params.search + "%")
                            ilike("cargo", "%" + params.search + "%")
                            ilike("login", "%" + params.search + "%")
                            ilike("codigo", "%" + params.search + "%")
                        }
                    }
                    asignadoPor {
                        or {
                            ilike("cedula", "%" + params.search + "%")
                            ilike("nombre", "%" + params.search + "%")
                            ilike("apellido", "%" + params.search + "%")
                            ilike("sigla", "%" + params.search + "%")
                            ilike("titulo", "%" + params.search + "%")
                            ilike("cargo", "%" + params.search + "%")
                            ilike("login", "%" + params.search + "%")
                            ilike("codigo", "%" + params.search + "%")
                        }
                    }
                    permisoTramite {
                        or {
                            ilike("codigo", "%" + params.search + "%")
                            ilike("descripcion", "%" + params.search + "%")
                        }
                    }
                }
            }
        } else {
            lista = PermisoUsuario.list(params)
        }
        return lista
    }

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def permisoUsuarioInstanceList = getLista(params, false)
        def permisoUsuarioInstanceCount = getLista(params, true).size()
        if (permisoUsuarioInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        permisoUsuarioInstanceList = getLista(params, false)
        return [permisoUsuarioInstanceList: permisoUsuarioInstanceList, permisoUsuarioInstanceCount: permisoUsuarioInstanceCount, params: params]
    } //list

    def show_ajax() {
        if (params.id) {
            def permisoUsuarioInstance = PermisoUsuario.get(params.id)
            if (!permisoUsuarioInstance) {
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
        if (params.id) {
            permisoUsuarioInstance = PermisoUsuario.get(params.id)
            if (!permisoUsuarioInstance) {
                notFound_ajax()
                return
            }
        }
        return [permisoUsuarioInstance: permisoUsuarioInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        params.each { k, v ->
            if (v != "date.struct" && v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }
        def permisoUsuarioInstance = new PermisoUsuario()
        if (params.id) {
            permisoUsuarioInstance = PermisoUsuario.get(params.id)
            if (!permisoUsuarioInstance) {
                notFound_ajax()
                return
            }
        } //update
        permisoUsuarioInstance.properties = params
        if (!permisoUsuarioInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} PermisoUsuario."
            msg += renderErrors(bean: permisoUsuarioInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de PermisoUsuario exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def permisoUsuarioInstance = PermisoUsuario.get(params.id)
            if (permisoUsuarioInstance) {
                try {
                    permisoUsuarioInstance.delete(flush: true)
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
