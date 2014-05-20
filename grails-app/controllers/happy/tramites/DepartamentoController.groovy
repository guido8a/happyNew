package happy.tramites

import groovy.json.JsonBuilder
import happy.seguridad.Persona
import org.apache.commons.lang.WordUtils


class DepartamentoController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def activar_ajax() {
        def dpto = Departamento.get(params.id)
        dpto.activo = 1
        if (dpto.save(flush: true)) {
            render "OK_Cambio efectado exitosamente"
        } else {
            render "NO_Ocurrió un error: " + renderErrors(bean: dpto)
        }
    }

    def desactivar_ajax() {
        def dpto = Departamento.get(params.id)
        def dptoNuevo = Departamento.get(params.nuevo)
        dpto.activo = 0

        if (dpto.save(flush: true)) {
            def rolPara = RolPersonaTramite.findByCodigo('R001');
            def rolCopia = RolPersonaTramite.findByCodigo('R002');
            def rolImprimir = RolPersonaTramite.findByCodigo('I005');

            def pxtPara = PersonaDocumentoTramite.withCriteria {
                eq("departamento", dpto)
                eq("rolPersonaTramite", rolPara)
                isNotNull("fechaEnvio")
                tramite {
                    or {
                        eq("estadoTramite", EstadoTramite.findByCodigo("E003")) //enviado
                        eq("estadoTramite", EstadoTramite.findByCodigo("E007")) //enviado al jefe
                        eq("estadoTramite", EstadoTramite.findByCodigo("E004")) //recibido
                    }
                }
            }
            def pxtCopia = PersonaDocumentoTramite.withCriteria {
                eq("departamento", dpto)
                eq("rolPersonaTramite", rolCopia)
                isNotNull("fechaEnvio")
                tramite {
                    or {
                        eq("estadoTramite", EstadoTramite.findByCodigo("E003")) //enviado
                        eq("estadoTramite", EstadoTramite.findByCodigo("E007")) //enviado al jefe
                        eq("estadoTramite", EstadoTramite.findByCodigo("E004")) //recibido
                    }
                }
            }
            def pxtImprimir = PersonaDocumentoTramite.withCriteria {
                eq("departamento", dpto)
                eq("rolPersonaTramite", rolImprimir)
                isNotNull("fechaEnvio")
                tramite {
                    or {
                        eq("estadoTramite", EstadoTramite.findByCodigo("E003")) //enviado
                        eq("estadoTramite", EstadoTramite.findByCodigo("E007")) //enviado al jefe
                        eq("estadoTramite", EstadoTramite.findByCodigo("E004")) //recibido
                    }
                }
            }
            def pxtTodos = pxtPara
            pxtTodos += pxtCopia
            pxtTodos += pxtImprimir

            def errores = "", ok = 0
            pxtTodos.each { pr ->
                if (pr.rolPersonaTramite.codigo == "I005") {
                    pr.delete(flush: true)
                } else {
                    pr.departamento = dptoNuevo
                    def tramite = pr.tramite
                    tramite.observaciones = (tramite.observaciones ?: "") + "Trámite antes dirigido a " + dpto.codigo + " " + dpto.descripcion
                    if (tramite.save(flush: true)) {
//                        println "tr.save ok"
                    } else {
                        errores += renderErrors(bean: tramite)
                        println tramite.errors
                    }
                    if (pr.save(flush: true)) {
//                        println "pr save ok"
                        ok++
                    } else {
                        println pr.errors
                        errores += renderErrors(bean: pr)
                    }
                }
            }
            if (errores != "") {
                println "NOPE: " + errores
                render "NO_" + errores
            } else {
//                println "OK"
                render "OK_Cambio realizado exitosamente"
            }
        } else {
            render "NO_Ha ocurrido un error al desactivar el departamento.<br/>" + renderErrors(bean: dpto)
        }

    }

    def arbolSearch_ajax() {
//        println params
//        def parts = params.search_string.split("~")
        def search = params.str.trim()
        if (search != "") {
            def c = Persona.createCriteria()
            def find = c.list(params) {
                or {
                    ilike("cedula", "%" + search + "%")
                    ilike("nombre", "%" + search + "%")
                    ilike("apellido", "%" + search + "%")
                    ilike("cargo", "%" + search + "%")
                    ilike("login", "%" + search + "%")
                    ilike("codigo", "%" + search + "%")
                    departamento {
                        or {
                            ilike("descripcion", "%" + search + "%")
                        }
                    }
                }
            }
//            println "FIND"
//            println find
            def departamentos = []
            find.each { pers ->
                if (pers.departamento && !departamentos.contains(pers.departamento)) {
                    departamentos.add(pers.departamento)
                    def dep = pers.departamento
                    def padre = dep.padre
//                    println "ANTES: " + departamentos
                    while (padre) {
                        dep = padre
                        padre = dep.padre
                        if (!departamentos.contains(dep)) {
                            departamentos.add(dep)
                        }
                    }
//                    println "DESPUES: " + departamentos
                }
            }
//            println departamentos
            departamentos = departamentos.reverse()

            def ids = "["

            if (find.size() > 0) {
                ids += "\"#root\","
//                ids += "\"#lidep_11\","
                departamentos.each { dp ->
                    ids += "\"#lidep_" + dp.id + "\","
                }
                ids = ids[0..-2]
            }
            ids += "]"
//            println ">>>>>>"
//            println ids
//            println "<<<<<<<"
            render ids
        } else {
            render ""
        }
    }

    def arbolReportes() {
        return [params: params]
    }

    def arbol() {
        return [params: params]
    }

    def loadTreePart() {
        render(makeTreeNode(params))
    }

    def makeTreeNode(params) {
        def id = params.id
        if (!params.sort) {
            params.sort = "apellido"
        }
        if (!params.order) {
            params.order = "asc"
        }
        String tree = "", clase = "", rel = ""
        def padre
        def hijos = []

        if (id == "#") {
            //root
            def hh = Departamento.countByPadreIsNull([sort: "descripcion"])
            if (hh > 0) {
                clase = "hasChildren jstree-closed"
            }
            tree = "<li id='root' class='root ${clase}' data-jstree='{\"type\":\"root\"}' level='0' >" +
                    "<a href='#' class='label_arbol'>Estructura</a>" +
                    "</li>"
        } else if (id == "root") {
            hijos = Departamento.findAllByPadreIsNull([sort: "descripcion"])
        } else {
            def parts = id.split("_")
            def node_id = parts[1].toLong()

            padre = Departamento.get(node_id)
            if (padre) {
                hijos = []
                hijos += Persona.findAllByDepartamento(padre, [sort: params.sort, order: params.order])
                hijos += Departamento.findAllByPadre(padre, [sort: "descripcion"])
            }
        }

        if (tree == "" && (padre || hijos.size() > 0)) {
            tree += "<ul>"
            def lbl = ""

            hijos.each { hijo ->
                def tp = ""
                def data = ""
                if (hijo instanceof Departamento) {
                    lbl = hijo.descripcion
                    if (hijo.codigo) {
                        lbl += " (${hijo.codigo})"
                    }
                    tp = "dep"
                    def hijosH = Departamento.findAllByPadre(hijo, [sort: "descripcion"])
                    rel = (hijosH.size() > 0) ? "padre" : "hijo"
                    hijosH += Persona.findAllByDepartamento(hijo, [sort: "apellido"])
                    clase = (hijosH.size() > 0) ? "jstree-closed hasChildren" : ""
                    if (hijosH.size() > 0) {
                        clase += " ocupado "
                    }

                    //cuenta los tramites de la bandeja de entrada de la oficina
                    def rolPara = RolPersonaTramite.findByCodigo('R001');
                    def rolCopia = RolPersonaTramite.findByCodigo('R002');
                    def rolImprimir = RolPersonaTramite.findByCodigo('I005');

                    def pxtPara = PersonaDocumentoTramite.withCriteria {
                        eq("departamento", hijo)
                        eq("rolPersonaTramite", rolPara)
                        isNotNull("fechaEnvio")
                        tramite {
                            or {
                                eq("estadoTramite", EstadoTramite.findByCodigo("E003")) //enviado
                                eq("estadoTramite", EstadoTramite.findByCodigo("E007")) //enviado al jefe
                                eq("estadoTramite", EstadoTramite.findByCodigo("E004")) //recibido
                            }
                        }
                    }
                    def pxtCopia = PersonaDocumentoTramite.withCriteria {
                        eq("departamento", hijo)
                        eq("rolPersonaTramite", rolCopia)
                        isNotNull("fechaEnvio")
                        tramite {
                            or {
                                eq("estadoTramite", EstadoTramite.findByCodigo("E003")) //enviado
                                eq("estadoTramite", EstadoTramite.findByCodigo("E007")) //enviado al jefe
                                eq("estadoTramite", EstadoTramite.findByCodigo("E004")) //recibido
                            }
                        }
                    }
                    def pxtImprimir = PersonaDocumentoTramite.withCriteria {
                        eq("departamento", hijo)
                        eq("rolPersonaTramite", rolImprimir)
                        isNotNull("fechaEnvio")
                        tramite {
                            or {
                                eq("estadoTramite", EstadoTramite.findByCodigo("E003")) //enviado
                                eq("estadoTramite", EstadoTramite.findByCodigo("E007")) //enviado al jefe
                                eq("estadoTramite", EstadoTramite.findByCodigo("E004")) //recibido
                            }
                        }
                    }

                    def pxtTodos = pxtPara
                    pxtTodos += pxtCopia
                    pxtTodos += pxtImprimir

                    data = "data-tramites='${pxtTodos.size()}'"

                    if (hijo.externo == 1) {
                        rel += "Externo"
                    }

                } else if (hijo instanceof Persona) {
                    switch (params.sort) {
                        case 'apellido':
                            lbl = "${hijo.apellido} ${hijo.nombre} ${hijo.login ? '(' + hijo.login + ')' : ''}"
                            break;
                        case 'nombre':
                            lbl = "${hijo.nombre} ${hijo.apellido} ${hijo.login ? '(' + hijo.login + ')' : ''}"
                            break;
                        default:
                            lbl = "${hijo.apellido} ${hijo.nombre} ${hijo.login ? '(' + hijo.login + ')' : ''}"
                    }

                    tp = "usu"
                    if (hijo.jefe == 1) {
                        rel = "jefe"
                    } else {
                        rel = "usuario"
                    }
                    clase = "usuario"
                    def rolPara = RolPersonaTramite.findByCodigo('R001');
                    def rolCopia = RolPersonaTramite.findByCodigo('R002');
                    def rolImprimir = RolPersonaTramite.findByCodigo('I005')

                    def tramites = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite as p  inner join fetch p.tramite as tramites where p.persona=${hijo.id} and  p.rolPersonaTramite in (${rolPara.id + "," + rolCopia.id + "," + rolImprimir.id}) and p.fechaEnvio is not null and tramites.estadoTramite in (3,4) order by p.fechaEnvio desc ")

                    data = "data-tramites='${tramites.size()}'"

                    if (hijo.esTriangulo) {
                        rel += "Triangulo"
//                        println "++++++++++++++++++++++++++++"
//                        println hijo
//                        println hijo.departamento
//                        println hijo.departamento.triangulos
//                        println hijo.departamento.triangulos.size()
//                        println "++++++++++++++++++++++++++++"
                        data += "data-triangulos=" + (hijo.departamento.triangulos.size())
                    }

                }
                if (hijo.activo == 1) {
                    rel += "Activo"
                } else {
                    rel += "Inactivo"
                }

                tree += "<li id='li${tp}_" + hijo.id + "' class='" + clase + "' ${data} data-jstree='{\"type\":\"${rel}\"}' >"
                tree += "<a href='#' class='label_arbol'>" + lbl + "</a>"
                tree += "</li>"
            }

            tree += "</ul>"
        }
        return tree
    }

    def index() {
        redirect(action: "arbol", params: params)
    } //index

    def getLista(params, all) {
        params = params.clone()
        if (all) {
            params.remove("offset")
            params.remove("max")
        }
        def lista
        if (params.search) {
            def c = Departamento.createCriteria()
            lista = c.list(params) {
                or {
                    ilike("codigo", "%" + params.search + "%")
                    ilike("descripcion", "%" + params.search + "%")
                    tipoDepartamento {
                        or {
                            ilike("codigo", "%" + params.search + "%")
                            ilike("descripcion", "%" + params.search + "%")
                        }
                    }
                }
            }
        } else {
            lista = Departamento.list(params)
        }
        return lista
    }

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def departamentoInstanceList = getLista(params, false)
        def departamentoInstanceCount = getLista(params, true).size()
        if (departamentoInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        departamentoInstanceList = getLista(params, false)
        return [departamentoInstanceList: departamentoInstanceList, departamentoInstanceCount: departamentoInstanceCount, params: params]
    } //list

    def show_ajax() {

        def personal = []

        if (params.id) {
            def departamentoInstance = Departamento.get(params.id)
            personal = departamentoInstance.getTriangulos();
            println("personal" + personal)
            if (!departamentoInstance) {
                notFound_ajax()
                return
            }
            return [departamentoInstance: departamentoInstance, personal: personal]
        } else {
            notFound_ajax()
        }


    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def departamentoInstance = new Departamento(params)
        def pxtTodos = []
        if (params.id) {
            departamentoInstance = Departamento.get(params.id)
            if (!departamentoInstance) {
                notFound_ajax()
                return
            }

            //cuenta los tramites de la bandeja de entrada de la oficina
            def rolPara = RolPersonaTramite.findByCodigo('R001');
            def rolCopia = RolPersonaTramite.findByCodigo('R002');
            def rolImprimir = RolPersonaTramite.findByCodigo('I005');

            def pxtPara = PersonaDocumentoTramite.withCriteria {
                eq("departamento", departamentoInstance)
                eq("rolPersonaTramite", rolPara)
                isNotNull("fechaEnvio")
                tramite {
                    or {
                        eq("estadoTramite", EstadoTramite.findByCodigo("E003")) //enviado
                        eq("estadoTramite", EstadoTramite.findByCodigo("E007")) //enviado al jefe
                        eq("estadoTramite", EstadoTramite.findByCodigo("E004")) //recibido
                    }
                }
            }
            def pxtCopia = PersonaDocumentoTramite.withCriteria {
                eq("departamento", departamentoInstance)
                eq("rolPersonaTramite", rolCopia)
                isNotNull("fechaEnvio")
                tramite {
                    or {
                        eq("estadoTramite", EstadoTramite.findByCodigo("E003")) //enviado
                        eq("estadoTramite", EstadoTramite.findByCodigo("E007")) //enviado al jefe
                        eq("estadoTramite", EstadoTramite.findByCodigo("E004")) //recibido
                    }
                }
            }
            def pxtImprimir = PersonaDocumentoTramite.withCriteria {
                eq("departamento", departamentoInstance)
                eq("rolPersonaTramite", rolImprimir)
                isNotNull("fechaEnvio")
                tramite {
                    or {
                        eq("estadoTramite", EstadoTramite.findByCodigo("E003")) //enviado
                        eq("estadoTramite", EstadoTramite.findByCodigo("E007")) //enviado al jefe
                        eq("estadoTramite", EstadoTramite.findByCodigo("E004")) //recibido
                    }
                }
            }

            pxtTodos = pxtPara
            pxtTodos += pxtCopia
            pxtTodos += pxtImprimir
        }

        return [departamentoInstance: departamentoInstance, tramites: pxtTodos.size()]
    } //form para cargar con ajax en un dialog

    def tipoDoc_ajax() {
        println params
        def dpto = Departamento.get(params.id)
        def permisos = TipoDocumentoDepartamento.findAllByDepartamentoAndEstado(dpto, 1).tipo.id

        return [departamentoInstance: dpto, permisos: permisos]
    } //form para cargar con ajax en un dialog

    def saveTipoDoc_ajax() {
//        println "***" + params
        def dep = Departamento.get(params.id)
        def tiene = TipoDocumentoDepartamento.findAllByDepartamentoAndEstado(dep, 1).tipo
        def nuevos = []

        def quitar = []
        def agregar = []

        (params.tipoDoc).each { id ->
            nuevos += TipoDocumento.get(id)
        }

        nuevos.each { nuevo ->
            if (!tiene.contains(nuevo)) {
                agregar += nuevo
            }
        }

        tiene.each { old ->
            if (!nuevos.contains(old)) {
                quitar += old
            }
        }

//        println "dep: " + dep
//        println "tiene: " + tiene
//        println "nuevos: " + nuevos
//        println "agregar: " + agregar
//        println "quitar: " + quitar

        agregar.each { tp ->
            def old = TipoDocumentoDepartamento.findAllByDepartamentoAndTipo(dep, tp)
            def tipo
            if (old.size() == 0) {
                tipo = new TipoDocumentoDepartamento([
                        departamento: dep,
                        tipo        : tp,
                        estado      : 1
                ])
                if (!tipo.save(flush: true)) {
                    println "Error al guardar tipoDocumentoDepartamento: " + renderErrors(bean: tipo)
                }
            } else if (old.size() == 1) {
                tipo = old.first()
                tipo.estado = 1
                if (!tipo.save(flush: true)) {
                    println "Error al guardar tipoDocumentoDepartamento: " + renderErrors(bean: tipo)
                }
            } else {
                println "Mas de un tipoDocumentoDepartamento para ${dep.descripcion} ${tp.descripcion}: ${old}"
                old.eachWithIndex { o, i ->
                    if (i == 0) {
                        o.estado = 1
                    } else {
                        o.estado = 0
                    }
                    if (!o.save(flush: true)) {
                        println "Error al guardar tipoDocumentoDepartamento: " + renderErrors(bean: o)
                    }
                }
            }
        }

        quitar.each { tp ->
            def old = TipoDocumentoDepartamento.findAllByDepartamentoAndTipo(dep, tp)
            def tipo
            if (old.size() == 0) {
                println "no hay tipoDocumentoDepartamento para ${dep.descripcion} ${tp.descripcion}"
            } else if (old.size() == 1) {
                tipo = old.first()
                tipo.estado = 0
                if (!tipo.save(flush: true)) {
                    println "Error al guardar tipoDocumentoDepartamento: " + renderErrors(bean: tipo)
                }
            } else {
                println "Mas de un tipoDocumentoDepartamento para ${dep.descripcion} ${tp.descripcion}: ${old}"
                old.eachWithIndex { o, i ->
                    o.estado = 0
                    if (!o.save(flush: true)) {
                        println "Error al guardar tipoDocumentoDepartamento: " + renderErrors(bean: o)
                    }
                }
            }
        }

        render "OK_Actualización de tipos de documento exitosa."
    }

    def save_ajax() {
        println params
        if (!params.activo) {
            params.activo = 1
        }
        params.each { k, v ->
            if (v != "date.struct" && v instanceof java.lang.String) {
                if (k != "direccion") {
                    params[k] = v.toUpperCase()
                }
            }
        }
        def departamentoInstance = new Departamento()
        if (params.id) {
            departamentoInstance = Departamento.get(params.id)
            if (!departamentoInstance) {
                notFound_ajax()
                return
            }
        } //update
        departamentoInstance.properties = params
        if (!departamentoInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} Departamento."
            msg += renderErrors(bean: departamentoInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de Departamento exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def departamentoInstance = Departamento.get(params.id)
            if (departamentoInstance) {
                def personas = Persona.countByDepartamento(departamentoInstance)
                if (personas == 0) {
                    try {
                        departamentoInstance.delete(flush: true)
                        render "OK_Eliminación de Departamento exitosa."
                    } catch (e) {
                        render "NO_No se pudo eliminar Departamento."
                    }
                } else {
                    render "NO_No se pudo eliminar el departamento pues tiene ${personas} persona${personas == 1 ? '' : 's'}."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró Departamento."
    } //notFound para ajax

    def validarCodigo_ajax() {
        params.codigo = params.codigo.toString().trim()
        if (params.id) {
            def dpto = Departamento.get(params.id)
            if (dpto.codigo == params.codigo) {
                render true
                return
            } else {
                render Departamento.countByCodigo(params.codigo) == 0
                return
            }
        } else {
            render Departamento.countByCodigo(params.codigo) == 0
            return
        }
    }

}
