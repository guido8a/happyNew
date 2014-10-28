package happy.seguridad

class ModuloController extends happy.seguridad.Shield  {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST", delete: "GET"]

    def index = {
        redirect(action: "list", params: params)
    }

    def form = {
        def title
        def moduloInstance

        if (params.source == "create") {
            moduloInstance = new Modulo()
            moduloInstance.properties = params
            title = g.message(code: "modulo.create", default: "Create Modulo")
        } else if (params.source == "edit") {
            moduloInstance = Modulo.get(params.id)
            if (!moduloInstance) {
                flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'modulo.label', default: 'Modulo'), params.id])}"
                redirect(action: "list")
            }
            title = g.message(code: "modulo.edit", default: "Edit Modulo")
        }

        return [moduloInstance: moduloInstance, title: title, source: params.source]
    }

    def create = {
        params.source = "create"
        redirect(action: "form", params: params)
    }

    def save = {
        def title
        if (params.id) {
            title = g.message(code: "modulo.edit", default: "Edit Modulo")
            def moduloInstance = Modulo.get(params.id)
            if (moduloInstance) {
                moduloInstance.properties = params
                if (!moduloInstance.hasErrors() && moduloInstance.save(flush: true)) {
                    flash.message = "${message(code: 'default.updated.message', args: [message(code: 'modulo.label', default: 'Modulo'), moduloInstance.id])}"
                    redirect(action: "show", id: moduloInstance.id)
                }
                else {
                    render(view: "form", model: [moduloInstance: moduloInstance, title: title, source: "edit"])
                }
            }
            else {
                flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'modulo.label', default: 'Modulo'), params.id])}"
                redirect(action: "list")
            }
        } else {
            title = g.message(code: "modulo.create", default: "Create Modulo")
            def moduloInstance = new Modulo(params)
            if (moduloInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.created.message', args: [message(code: 'modulo.label', default: 'Modulo'), moduloInstance.id])}"
                redirect(action: "show", id: moduloInstance.id)
            }
            else {
                render(view: "form", model: [moduloInstance: moduloInstance, title: title, source: "create"])
            }
        }
    }

    def update = {
        def moduloInstance = Modulo.get(params.id)
        if (moduloInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (moduloInstance.version > version) {

                    moduloInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'modulo.label', default: 'Modulo')] as Object[], "Another user has updated this Modulo while you were editing")
                    render(view: "edit", model: [moduloInstance: moduloInstance])
                    return
                }
            }
            moduloInstance.properties = params
            if (!moduloInstance.hasErrors() && moduloInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'modulo.label', default: 'Modulo'), moduloInstance.id])}"
                redirect(action: "show", id: moduloInstance.id)
            }
            else {
                render(view: "edit", model: [moduloInstance: moduloInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'modulo.label', default: 'Modulo'), params.id])}"
            redirect(action: "list")
        }
    }

    def show = {
        def moduloInstance = Modulo.get(params.id)
        if (!moduloInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'modulo.label', default: 'Modulo'), params.id])}"
            redirect(action: "list")
        }
        else {

            def title = g.message(code: "modulo.show", default: "Show Modulo")

            [moduloInstance: moduloInstance, title: title]
        }
    }

    def edit = {
        params.source = "edit"
        redirect(action: "form", params: params)
    }

    def delete = {
        def moduloInstance = Modulo.get(params.id)
        if (moduloInstance) {
            try {
                moduloInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'modulo.label', default: 'Modulo'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'modulo.label', default: 'Modulo'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'modulo.label', default: 'Modulo'), params.id])}"
            redirect(action: "list")
        }
    }


    def list() {
        if (session.usuario.puedeAdmin) {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def moduloInstanceList = Modulo.list(params)
        def moduloInstanceCount = Modulo.count()
        if (moduloInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        moduloInstanceList = Modulo.list(params)
        return [moduloInstanceList: moduloInstanceList, moduloInstanceCount: moduloInstanceCount]
    } else {
        flash.message = "Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será registrada."
        response.sendError(403)
    }
    } //list
    def show_ajax() {
        if (params.id) {
            def numeroInstance = Modulo.get(params.id)
            if (!numeroInstance) {
                notFound_ajax()
                return
            }
            return [numeroInstance: numeroInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def moduloInstance = new Modulo(params)
        if (params.id) {
            moduloInstance = Modulo.get(params.id)
            if (!moduloInstance) {
                notFound_ajax()
                return
            }
        }
        return [moduloInstance: moduloInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        println params
        params.each { k, v ->
        }
        def moduloInstance = new Modulo()
        if (params.id) {
            moduloInstance = Modulo.get(params.id)
            if (!moduloInstance) {
                notFound_ajax()
                return
            }
        } //update
        moduloInstance.properties = params
        if (!moduloInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} Modulo."
            msg += renderErrors(bean: moduloInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de Modulo exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def moduloInstance = Modulo.get(params.id)
            if (moduloInstance) {
                try {
                    moduloInstance.delete(flush: true)
                    render "OK_Eliminación de Modulo exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar Modulo."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró Numero."
    } //notFound para ajax
}
