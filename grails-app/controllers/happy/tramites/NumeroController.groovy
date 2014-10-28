package happy.tramites


class NumeroController extends happy.seguridad.Shield {

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
            def c = Numero.createCriteria()
            lista = c.list(params) {
                or {
                    if (params.search.toString().isNumber()) {
                        eq("valor", params.search.toInteger())
                    }
                    tipoDocumento {
                        or {
                            ilike("codigo", "%" + params.search + "%")
                            ilike("descripcion", "%" + params.search + "%")
                        }
                    }
                    departamento {
                        or {
                            ilike("codigo", "%" + params.search + "%")
                            ilike("descripcion", "%" + params.search + "%")
                        }
                    }
                }
            }
        } else {
            lista = Numero.list(params)
        }
        return lista
    }

    def config() {
        if(session.usuario.puedeAdmin) {
            def tiposDoc = TipoDocumento.list([sort: 'descripcion'])
            def departamentos = Departamento.list([sort: "descripcion"])

            def html = "<table class='table table-condensed table-bordered'>"
            html += "<thead>"
            html += "<tr>"
            html += "<th rowspan='2'>Departamento</th>"
            html += "<th colspan='${tiposDoc.size()}'>Tipo de documento</th>"
            html += "</tr>"
            html += "<tr>"
            tiposDoc.each { tp ->
                html += "<th>${tp.descripcion}</th>"
            }
            html += "</tr>"
            html += "</thead>"
            html += "<tbody>"
            def body = ""
            departamentos.each { dep ->
                def cont = 0
                def linea = "<tr>"
                linea += "<td class='departamento'>" + dep.descripcion + "</td>"
                tiposDoc.each { tp ->
                    def num = Numero.findAllByDepartamentoAndTipoDocumento(dep, tp)
                    def puede = TipoDocumentoDepartamento.withCriteria {
                        eq("departamento", dep)
                        eq("tipo", tp)
                        eq("estado", 1)
                    }
                    if (puede.size() > 0) {
                        if (num.size() == 0) {
                            linea += "<td class='tipoDoc' title='${dep.codigo} - ${tp.descripcion}'>" +
                                    g.textField(name: dep.id + "_" + tp.id, class: "form-control input-sm", value: 0) +
                                    "</td>"
                        } else if (num.size() > 1) {
                            linea += "<td class='tipoDoc danger' title='Este campo tiene un error. Comuníquese con el administrador.'>${num.valor}</td>"
                        } else {
                            num = num.first()
                            linea += "<td class='tipoDoc info' title='${dep.codigo} - ${tp.descripcion}'>" +
                                    g.textField(name: num.id, class: "form-control input-sm text-info", value: num.valor) +
                                    "</td>"
                        }
                        cont++
                    } else {
                        linea += "<td class='tipoDoc warning' title='Este departamento no tiene asignado este tipo de documento.'>-</td>"
                    }
                }
                linea += "</tr>"
                if (cont == 0) {
                    linea = ""
                }
                body += linea
            }
            html += body
            html += "</tbody>"
            html += "<table>"
            return [html: html]
        }else{
            flash.message="Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será reportada"
            response.sendError(403)
        }
    }

    def saveConfig() {
        params.remove("action")
        params.remove("format")
        params.remove("controller")
        params.each { k, v ->
            if (k.toString().contains("_")) {
                //es nuevo [0]->dep.id, [1]->tipoDoc.id
                if (v.toInteger() > 0) {
                    def parts = k.toString().split("_")
                    def depId = parts[0]
                    def tipoDocId = parts[1]
                    println "NUEVO: " + depId + "    " + tipoDocId
                    def num = new Numero([
                            departamento : Departamento.get(depId),
                            tipoDocumento: TipoDocumento.get(tipoDocId),
                            valor        : v.toInteger()
                    ])
                    if (!num.save(flush: true)) {
                        println "error en create: " + num.errors
                    }
                }
            } else {
                // es save
                def num = Numero.get(k.toLong())
                if (num.valor != v.toInteger()) {
                    println "EXISTE: " + num
                    num.valor = v.toInteger()
                    if (!num.save(flush: true)) {
                        println "error en ${num.id}: " + num.errors
                    }
                }
            }
        }
        redirect(action: "config")
    }

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def numeroInstanceList = Numero.list(params)
        def numeroInstanceCount = Numero.count()
        if (numeroInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        numeroInstanceList = Numero.list(params)
        return [numeroInstanceList: numeroInstanceList, numeroInstanceCount: numeroInstanceCount, params: params]
    } //list

    def show_ajax() {
        if (params.id) {
            def numeroInstance = Numero.get(params.id)
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
        def numeroInstance = new Numero(params)
        if (params.id) {
            numeroInstance = Numero.get(params.id)
            if (!numeroInstance) {
                notFound_ajax()
                return
            }
        }
        return [numeroInstance: numeroInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        params.each { k, v ->
            if (v != "date.struct" && v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }
        def numeroInstance = new Numero()
        if (params.id) {
            numeroInstance = Numero.get(params.id)
            if (!numeroInstance) {
                notFound_ajax()
                return
            }
        } //update
        numeroInstance.properties = params
        if (!numeroInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} Numero."
            msg += renderErrors(bean: numeroInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de Numero exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def numeroInstance = Numero.get(params.id)
            if (numeroInstance) {
                try {
                    numeroInstance.delete(flush: true)
                    render "OK_Eliminación de Numero exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar Numero."
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
