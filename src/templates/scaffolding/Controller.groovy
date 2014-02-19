<%=packageName ? "package ${packageName}\n" : ''%>

class ${className}Controller extends <%=packageName.split("\\.")[0]%>.seguridad.Shield {

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
            def c = ${className}.createCriteria()
            lista = c.list(params) {
                or {
                    /* TODO: cambiar aqui segun sea necesario */
                    ilike("codigo", "%" + params.search + "%")
                    ilike("descripcion", "%" + params.search + "%")
                }
            }
        } else {
            lista = ${className}.list(params)
        }
        return lista
    }

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def ${propertyName}List = getLista(params, false)
        def ${propertyName}Count = getLista(params, true).size()
        if(${propertyName}List.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        ${propertyName}List = getLista(params, false)
        return [${propertyName}List: ${propertyName}List, ${propertyName}Count: ${propertyName}Count, params: params]
    } //list

    def show_ajax() {
        if(params.id) {
            def ${propertyName} = ${className}.get(params.id)
            if(!${propertyName}) {
                notFound_ajax()
                return
            }
            return [${propertyName}: ${propertyName}]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def ${propertyName} = new ${className}(params)
        if(params.id) {
            ${propertyName} = ${className}.get(params.id)
            if(!${propertyName}) {
                notFound_ajax()
                return
            }
        }
        return [${propertyName}: ${propertyName}]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        def ${propertyName} = new ${className}()
        if(params.id) {
            ${propertyName} = ${className}.get(params.id)
            if(!${propertyName}) {
                notFound_ajax()
                return
            }
        } //update
        ${propertyName}.properties = params
        if(!${propertyName}.save(flush:true)) {
            def msg = "NO_No se pudo \${params.id ? 'actualizar' : 'crear'} ${className}."
            msg += renderErrors(bean: ${propertyName})
            render msg
            return
        }
        render "OK_\${params.id ? 'Actualización' : 'Creación'} de ${className} exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if(params.id) {
            def ${propertyName} = ${className}.get(params.id)
            if(${propertyName}) {
                try {
                    ${propertyName}.delete(flush:true)
                    render "OK_Eliminación de ${className} exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar ${className}."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró ${className}."
    } //notFound para ajax

}
