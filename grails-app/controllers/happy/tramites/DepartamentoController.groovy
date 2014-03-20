package happy.tramites

import happy.seguridad.Persona


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

    def arbol() {

    }

    def loadTreePart() {
        render(makeTreeNode(params.id))
    }

    def makeTreeNode(id) {
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
                    "<a href='#' class='label_arbol'>Departamentos</a>" +
                    "</li>"
        } else if (id == "root") {
            hijos = Departamento.findAllByPadreIsNull([sort: "descripcion"])
        } else {
            def parts = id.split("_")
            def node_id = parts[1].toLong()

            padre = Departamento.get(node_id)
            if (padre) {
                hijos = []
                hijos += Departamento.findAllByPadre(padre, [sort: "descripcion"])
                hijos += Persona.findAllByDepartamento(padre, [sort: "apellido"])
            }
        }

        if (tree == "" && (padre || hijos.size() > 0)) {
            tree += "<ul>"

            hijos.each { hijo ->
                def tp = ""
                def data = ""
                if (hijo instanceof Departamento) {
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

                } else if (hijo instanceof Persona) {
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
                }
                if (hijo.activo == 1) {
                    rel += "Activo"
                } else {
                    rel += "Inactivo"
                }

                tree += "<li id='li${tp}_" + hijo.id + "' class='" + clase + "' ${data} data-jstree='{\"type\":\"${rel}\"}' >"
                tree += "<a href='#' class='label_arbol'>" + hijo + "</a>"
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
        if (params.id) {
            def departamentoInstance = Departamento.get(params.id)
            if (!departamentoInstance) {
                notFound_ajax()
                return
            }
            return [departamentoInstance: departamentoInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def departamentoInstance = new Departamento(params)
        if (params.id) {
            departamentoInstance = Departamento.get(params.id)
            if (!departamentoInstance) {
                notFound_ajax()
                return
            }
        }
        return [departamentoInstance: departamentoInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        if (!params.activo) {
            params.activo = 1
        }
        params.each { k, v ->
            if (v != "date.struct" && v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
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
