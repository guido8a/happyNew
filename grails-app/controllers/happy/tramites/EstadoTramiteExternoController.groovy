package happy.tramites


class EstadoTramiteExternoController extends happy.seguridad.Shield {

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
            def c = EstadoTramiteExterno.createCriteria()
            lista = c.list(params) {
                or {
                    /* TODO: cambiar aqui segun sea necesario */
                    ilike("codigo", "%" + params.search + "%")
                    ilike("descripcion", "%" + params.search + "%")
                }
            }
        } else {
            lista = EstadoTramiteExterno.list(params)
        }
        return lista
    }

    def list() {
        if (session.usuario.puedeAdmin) {
            params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
            def estadoTramiteExternoInstanceList = getLista(params, false)
            def estadoTramiteExternoInstanceCount = getLista(params, true).size()
            if (estadoTramiteExternoInstanceList.size() == 0 && params.offset && params.max) {
                params.offset = params.offset - params.max
            }
            estadoTramiteExternoInstanceList = getLista(params, false)
            return [estadoTramiteExternoInstanceList: estadoTramiteExternoInstanceList, estadoTramiteExternoInstanceCount: estadoTramiteExternoInstanceCount, params: params]
        } else {
            flash.message = "Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será registrada."
            response.sendError(403)
        }
    } //list

    def show_ajax() {
        if (params.id) {
            def estadoTramiteExternoInstance = EstadoTramiteExterno.get(params.id)
            if (!estadoTramiteExternoInstance) {
                notFound_ajax()
                return
            }
            return [estadoTramiteExternoInstance: estadoTramiteExternoInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def estadoTramiteExternoInstance = new EstadoTramiteExterno(params)
        if (params.id) {
            estadoTramiteExternoInstance = EstadoTramiteExterno.get(params.id)
            if (!estadoTramiteExternoInstance) {
                notFound_ajax()
                return
            }
        }
        return [estadoTramiteExternoInstance: estadoTramiteExternoInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        params.each { k, v ->
            if (v != "date.struct" && v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }
        def estadoTramiteExternoInstance = new EstadoTramiteExterno()
        if (params.id) {
            estadoTramiteExternoInstance = EstadoTramiteExterno.get(params.id)
            if (!estadoTramiteExternoInstance) {
                notFound_ajax()
                return
            }
        } //update
        estadoTramiteExternoInstance.properties = params
        if (!estadoTramiteExternoInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} EstadoTramiteExterno."
            msg += renderErrors(bean: estadoTramiteExternoInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de EstadoTramiteExterno exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def estadoTramiteExternoInstance = EstadoTramiteExterno.get(params.id)
            if (estadoTramiteExternoInstance) {
                try {
                    estadoTramiteExternoInstance.delete(flush: true)
                    render "OK_Eliminación de EstadoTramiteExterno exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar EstadoTramiteExterno."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró EstadoTramiteExterno."
    } //notFound para ajax

    def validarCodigo_ajax() {
        params.codigo = params.codigo.toString().trim()
        if (params.id) {
            def obj = EstadoTramite.get(params.id)
            if (obj.codigo.toLowerCase() == params.codigo.toLowerCase()) {
                render true
                return
            } else {
                render EstadoTramite.countByCodigoIlike(params.codigo) == 0
                return
            }
        } else {
            render EstadoTramite.countByCodigoIlike(params.codigo) == 0
            return
        }
    }

}
