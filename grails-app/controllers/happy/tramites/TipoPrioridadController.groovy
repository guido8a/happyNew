package happy.tramites


class TipoPrioridadController extends happy.seguridad.Shield {

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
            def c = TipoPrioridad.createCriteria()
            lista = c.list(params) {
                or {
                    ilike("codigo", "%" + params.search + "%")
                    ilike("descripcion", "%" + params.search + "%")
                    if (params.search.toString().isNumber()) {
                        eq("tiempo", params.search.toInteger())
                    }
                }
            }
        } else {
            lista = TipoPrioridad.list(params)
        }
        return lista
    }

    def list() {
        if (session.usuario.puedeAdmin) {
            params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
            def tipoPrioridadInstanceList = getLista(params, false)
            def tipoPrioridadInstanceCount = getLista(params, true).size()
            if (tipoPrioridadInstanceList.size() == 0 && params.offset && params.max) {
                params.offset = params.offset - params.max
            }
            tipoPrioridadInstanceList = getLista(params, false)
            return [tipoPrioridadInstanceList: tipoPrioridadInstanceList, tipoPrioridadInstanceCount: tipoPrioridadInstanceCount, params: params]
        } else {
            flash.message = "Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será reportada"
            response.sendError(403)
        }

    } //list

    def show_ajax() {
        if (params.id) {
            def tipoPrioridadInstance = TipoPrioridad.get(params.id)
            if (!tipoPrioridadInstance) {
                notFound_ajax()
                return
            }
            return [tipoPrioridadInstance: tipoPrioridadInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def tipoPrioridadInstance = new TipoPrioridad(params)
        if (params.id) {
            tipoPrioridadInstance = TipoPrioridad.get(params.id)
            if (!tipoPrioridadInstance) {
                notFound_ajax()
                return
            }
        }
        return [tipoPrioridadInstance: tipoPrioridadInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        params.each { k, v ->
            if (v != "date.struct" && v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }
        def tipoPrioridadInstance = new TipoPrioridad()
        if (params.id) {
            tipoPrioridadInstance = TipoPrioridad.get(params.id)
            if (!tipoPrioridadInstance) {
                notFound_ajax()
                return
            }
        } //update
        tipoPrioridadInstance.properties = params
        if (!tipoPrioridadInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} TipoPrioridad."
            msg += renderErrors(bean: tipoPrioridadInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de TipoPrioridad exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def tipoPrioridadInstance = TipoPrioridad.get(params.id)
            if (tipoPrioridadInstance) {
                try {
                    tipoPrioridadInstance.delete(flush: true)
                    render "OK_Eliminación de TipoPrioridad exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar TipoPrioridad."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró TipoPrioridad."
    } //notFound para ajax

//    def validarCodigo_ajax(){
//        params.codigo = params.codigo.toString().trim()
//        def tipo = TipoPrioridad.findAllByCodigo(params.codigo.toUpperCase())
//
//        render tipo.size() == 0
//        return
//
//
//    }

    def validarCodigo_ajax() {
        params.codigo = params.codigo.toString().trim()
        if (params.id) {
            def obj = TipoPrioridad.get(params.id)
            if (obj.codigo.toLowerCase() == params.codigo.toLowerCase()) {
                render true
                return
            } else {
                render TipoPrioridad.countByCodigoIlike(params.codigo) == 0
                return
            }
        } else {
            render TipoPrioridad.countByCodigoIlike(params.codigo) == 0
            return
        }
    }

}
