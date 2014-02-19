package happy.tramites


class TipoPersonaController extends happy.seguridad.Shield {

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
            def c = TipoPersona.createCriteria()
            lista = c.list(params) {
                or {
                    ilike("codigo", "%" + params.search + "%")
                    ilike("descripcion", "%" + params.search + "%")
                }
            }
        } else {
            lista = TipoPersona.list(params)
        }
        return lista
    }

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def tipoPersonaInstanceList = getLista(params, false)
        def tipoPersonaInstanceCount = getLista(params, true).size()
        if (tipoPersonaInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        tipoPersonaInstanceList = getLista(params, false)
        return [tipoPersonaInstanceList: tipoPersonaInstanceList, tipoPersonaInstanceCount: tipoPersonaInstanceCount, params: params]
    } //list

    def show_ajax() {
        if (params.id) {
            def tipoPersonaInstance = TipoPersona.get(params.id)
            if (!tipoPersonaInstance) {
                notFound_ajax()
                return
            }
            return [tipoPersonaInstance: tipoPersonaInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def tipoPersonaInstance = new TipoPersona(params)
        if (params.id) {
            tipoPersonaInstance = TipoPersona.get(params.id)
            if (!tipoPersonaInstance) {
                notFound_ajax()
                return
            }
        }
        return [tipoPersonaInstance: tipoPersonaInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        params.each { k, v ->
            if (v != "date.struct" && v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }
        def tipoPersonaInstance = new TipoPersona()
        if (params.id) {
            tipoPersonaInstance = TipoPersona.get(params.id)
            if (!tipoPersonaInstance) {
                notFound_ajax()
                return
            }
        } //update
        tipoPersonaInstance.properties = params
        if (!tipoPersonaInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} TipoPersona."
            msg += renderErrors(bean: tipoPersonaInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de TipoPersona exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def tipoPersonaInstance = TipoPersona.get(params.id)
            if (tipoPersonaInstance) {
                try {
                    tipoPersonaInstance.delete(flush: true)
                    render "OK_Eliminación de TipoPersona exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar TipoPersona."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró TipoPersona."
    } //notFound para ajax


    def validarCodigo_ajax() {
        params.codigo = params.codigo.toString().trim()
        if (params.id) {
            def tipo = TipoPersona.get(params.id)
            if (tipo.codigo == params.codigo) {
                render true
                return
            } else {
                render TipoPersona.countByCodigo(params.codigo) == 0
                return
            }
        } else {
            render TipoPersona.countByCodigo(params.codigo) == 0
            return
        }
    }//validador unique


}
