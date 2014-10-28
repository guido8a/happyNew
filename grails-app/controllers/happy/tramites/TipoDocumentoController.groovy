package happy.tramites


class TipoDocumentoController extends happy.seguridad.Shield {

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
            def c = TipoDocumento.createCriteria()
            lista = c.list(params) {
                or {
                    ilike("codigo", "%" + params.search + "%")
                    ilike("descripcion", "%" + params.search + "%")
                }
            }
        } else {
            lista = TipoDocumento.list(params)
        }
        return lista
    }

    def list() {
        if(session.usuario.puedeAdmin) {
           params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
            def tipoDocumentoInstanceList = getLista(params, false)
            def tipoDocumentoInstanceCount = getLista(params, true).size()
            if (tipoDocumentoInstanceList.size() == 0 && params.offset && params.max) {
                params.offset = params.offset - params.max
            }
            tipoDocumentoInstanceList = getLista(params, false)
            return [tipoDocumentoInstanceList: tipoDocumentoInstanceList, tipoDocumentoInstanceCount: tipoDocumentoInstanceCount, params: params]
        }else{
            flash.message="Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será reportada"
            response.sendError(403)
        }

    } //list

    def show_ajax() {
        if (params.id) {
            def tipoDocumentoInstance = TipoDocumento.get(params.id)
            if (!tipoDocumentoInstance) {
                notFound_ajax()
                return
            }
            return [tipoDocumentoInstance: tipoDocumentoInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def tipoDocumentoInstance = new TipoDocumento(params)
        if (params.id) {
            tipoDocumentoInstance = TipoDocumento.get(params.id)
            if (!tipoDocumentoInstance) {
                notFound_ajax()
                return
            }
        }
        return [tipoDocumentoInstance: tipoDocumentoInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        params.each { k, v ->
            if (v != "date.struct" && v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }
        def tipoDocumentoInstance = new TipoDocumento()
        if (params.id) {
            tipoDocumentoInstance = TipoDocumento.get(params.id)
            if (!tipoDocumentoInstance) {
                notFound_ajax()
                return
            }
        } //update
        tipoDocumentoInstance.properties = params
        if (!tipoDocumentoInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} TipoDocumento."
            msg += renderErrors(bean: tipoDocumentoInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de TipoDocumento exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def tipoDocumentoInstance = TipoDocumento.get(params.id)
            if (tipoDocumentoInstance) {
                try {
                    tipoDocumentoInstance.delete(flush: true)
                    render "OK_Eliminación de TipoDocumento exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar TipoDocumento."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró TipoDocumento."
    } //notFound para ajax


    def validarCodigo_ajax() {
        println params
        params.codigo = params.codigo.toString().trim()
        def tipo = TipoDocumento.findAllByCodigo(params.codigo.toUpperCase())
            render tipo.size() == 0
            return
    }//validador unique

}
