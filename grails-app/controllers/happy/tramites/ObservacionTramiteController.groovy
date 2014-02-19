package happy.tramites


class ObservacionTramiteController extends happy.seguridad.Shield {

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
            def c = ObservacionTramite.createCriteria()
            lista = c.list(params) {
                or {
                    ilike("observaciones", "%" + params.search + "%")
                    tramite {
                        or {
                            ilike("codigo", "%" + params.search + "%")
                            ilike("numero", "%" + params.search + "%")
                            ilike("asunto", "%" + params.search + "%")
                        }
                    }
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
                }
            }
        } else {
            lista = ObservacionTramite.list(params)
        }
        return lista
    }

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def observacionTramiteInstanceList = getLista(params, false)
        def observacionTramiteInstanceCount = getLista(params, true).size()
        if (observacionTramiteInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        observacionTramiteInstanceList = getLista(params, false)
        return [observacionTramiteInstanceList: observacionTramiteInstanceList, observacionTramiteInstanceCount: observacionTramiteInstanceCount, params: params]
    } //list

    def show_ajax() {
        if (params.id) {
            def observacionTramiteInstance = ObservacionTramite.get(params.id)
            if (!observacionTramiteInstance) {
                notFound_ajax()
                return
            }
            return [observacionTramiteInstance: observacionTramiteInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def observacionTramiteInstance = new ObservacionTramite(params)
        if (params.id) {
            observacionTramiteInstance = ObservacionTramite.get(params.id)
            if (!observacionTramiteInstance) {
                notFound_ajax()
                return
            }
        }
        return [observacionTramiteInstance: observacionTramiteInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        params.each { k, v ->
            if (v != "date.struct" && v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }
        def observacionTramiteInstance = new ObservacionTramite()
        if (params.id) {
            observacionTramiteInstance = ObservacionTramite.get(params.id)
            if (!observacionTramiteInstance) {
                notFound_ajax()
                return
            }
        } //update
        observacionTramiteInstance.properties = params
        if (!observacionTramiteInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} ObservacionTramite."
            msg += renderErrors(bean: observacionTramiteInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de ObservacionTramite exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def observacionTramiteInstance = ObservacionTramite.get(params.id)
            if (observacionTramiteInstance) {
                try {
                    observacionTramiteInstance.delete(flush: true)
                    render "OK_Eliminación de ObservacionTramite exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar ObservacionTramite."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró ObservacionTramite."
    } //notFound para ajax

}
