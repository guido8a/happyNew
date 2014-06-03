package happy.utilitarios


class ParametrosController extends happy.seguridad.Shield {

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
            def c = Parametros.createCriteria()
            lista = c.list(params) {
                or {
                    /* TODO: cambiar aqui segun sea necesario */
                    ilike("codigo", "%" + params.search + "%")
                    ilike("descripcion", "%" + params.search + "%")
                }
            }
        } else {
            lista = Parametros.list(params)
        }
        return lista
    }

    def list() {
        if(session.usuario.puedeAdmin) {
            params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
            def parametrosInstanceList = getLista(params, false)
            def parametrosInstanceCount = getLista(params, true).size()
            if(parametrosInstanceList.size() == 0 && params.offset && params.max) {
                params.offset = params.offset - params.max
            }
            parametrosInstanceList = getLista(params, false)
            return [parametrosInstanceList: parametrosInstanceList, parametrosInstanceCount: parametrosInstanceCount, params: params]

        }else{
            flash.message="Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será reportada"
            response.sendError(403)
        }

    } //list

    def show_ajax() {
        if(params.id) {
            def parametrosInstance = Parametros.get(params.id)
            if(!parametrosInstance) {
                notFound_ajax()
                return
            }
            return [parametrosInstance: parametrosInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def parametrosInstance = new Parametros(params)
        if(params.id) {
            parametrosInstance = Parametros.get(params.id)
            if(!parametrosInstance) {
                notFound_ajax()
                return
            }
        }
        return [parametrosInstance: parametrosInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        println params
        def parametrosInstance = new Parametros()
        if(params.id) {
            parametrosInstance = Parametros.get(params.id)
            if(!parametrosInstance) {
                notFound_ajax()
                return
            }
        } //update
        parametrosInstance.properties = params
        if(!parametrosInstance.save(flush:true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} Parametros."
            msg += renderErrors(bean: parametrosInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de Parametros exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if(params.id) {
            def parametrosInstance = Parametros.get(params.id)
            if(parametrosInstance) {
                try {
                    parametrosInstance.delete(flush:true)
                    render "OK_Eliminación de Parametros exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar Parametros."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró Parametros."
    } //notFound para ajax

}
