package happy.tramites

import happy.alertas.Alerta
import happy.seguridad.Persona
import happy.seguridad.Shield

class TramiteAdminController extends Shield {

    def tramitesService

    def asociarTramite_ajax() {
        def original = PersonaDocumentoTramite.get(params.original)
        def duenioDep = original.tramite.deDepartamento
        def duenioPer = original.tramite.de
        def codigo = params.codigo;
        def msg
        def tramites = Tramite.findAllByCodigoIlike(codigo)
        if (tramites.size() == 0) {
            msg = "<div class='alert alert-danger'>"
            msg += "No se encontró un trámite disponible con código " + codigo.toUpperCase()
            msg += "</div>"
        } else {
            def rolPara = RolPersonaTramite.findByCodigo("R001")
            def rolCc = RolPersonaTramite.findByCodigo("R002")

            def estadoArchivado = EstadoTramite.findByCodigo("E005")
            def estadoAnulado = EstadoTramite.findByCodigo("E006")
            def estadoEnviado = EstadoTramite.findByCodigo("E003")
            def estadoRecibido = EstadoTramite.findByCodigo("E004")

            msg = "<p>Seleccione el trámite al que se asociará <strong>${original.tramite.codigo}</strong> "
            msg += "(creado el ${original.fechaCreacion.format('dd-MM-yyyy HH:mm')}, asunto: <strong>${original.tramite.asunto}</strong>)</p>"
            msg += "<table class='table table-condensed table-bordered'>"
            msg += "<thead>"
            msg += "<tr>"
            msg += "<th>Trámite</th>"
            msg += "<th>De</th>"
            msg += "<th>Para</th>"
            msg += "<th>Info.</th>"
            msg += "<th>Seleccionar</th>"
            msg += "</tr>"
            msg += "</thead>"
            def algo = false
            tramites.each { tr ->
                def hijosVivos = 0
                (Tramite.findAllByPadre(tr)).each { th ->
                    def prtrHijo = PersonaDocumentoTramite.withCriteria {
                        eq("tramite", th)
                        inList("rolPersonaTramite", [rolCc, rolPara])
                        ne("estado", estadoAnulado)
                    }
                    hijosVivos += prtrHijo.size()
                }
                if (hijosVivos == 0) {
                    def cod = tr.codigo
                    def de = tr.deDepartamento ? tr.deDepartamento.codigo : tr.de.login
                    def asunto = tr.asunto

//                println "tramite: " + tr
//                println "duenioDep: " + duenioDep
//                println "duenioPer: " + duenioPer

                    def personas = PersonaDocumentoTramite.withCriteria {
                        eq("tramite", tr)
                        or {
                            eq("rolPersonaTramite", rolPara)
                            eq("rolPersonaTramite", rolCc)
                        }
                        eq("estado", estadoRecibido)
                        tramite {
//                        lt("fechaCreacion", original.tramite.fechaCreacion)
                            lt("fechaEnvio", original.tramite.fechaCreacion)
                        }
                        if (duenioDep) {
                            eq("departamento", duenioDep)
                        } else if (duenioPer) {
                            eq("persona", duenioPer)
                        }
                    }
                    personas.each { cc ->
                        algo = true
                        msg += "<tr>"
                        msg += "<td>${cod}</td>"
                        msg += "<td>${de}</td>"
                        msg += "<td>${cc.rolPersonaTramite.descripcion} ${cc.departamento ? cc.departamento.codigo : cc.persona.login}</td>"
                        msg += "<td><strong>Asunto: ${asunto}</strong><br/>${tramiteFechas(cc)}</td>"
                        msg += "<td><a href='#' class='btn btn-success select' id='${cc.id}'><i class='fa fa-check'></i></a></td>"
                        msg += "</tr>"
                    }
                }
            }
            msg += "</table>"

            msg += "<script type='text/javascript'>"
            msg += '$(function(){'
            msg += '$(".select").click(function() {'
            msg += "openLoader('Asociando trámites');"
            msg += '$.ajax({\n' +
                    '   type: "POST",\n' +
                    '   url: "' + createLink(action: 'guardarAsociarTramite') + '",\n' +
                    '   data: {\n' +
                    '\tid: $(this).attr("id"),\n' +
                    '\toriginal: ' + params.original + '\n' +
                    '\t},\n' +
                    '   success: function(msg){\n' +
                    '     location.reload(true);\n' +
                    '   }\n' +
                    ' });'
            msg += "return false;"
            msg += '});'
            msg += '});'
            msg += "</script>"

            if (!algo) {
                msg = "<div class='alert alert-danger'>"
                msg += "No se encontró un trámite con código " + codigo.toUpperCase() + " que cumpla las condiciones necesarias."
                msg += "</div>"
            }
        }
        render msg
    }

    def guardarAsociarTramite() {
        def original = PersonaDocumentoTramite.get(params.original)
        def nuevoPadre = PersonaDocumentoTramite.get(params.id)

        original.tramite.padre = nuevoPadre.tramite
        original.tramite.aQuienContesta = nuevoPadre

//        original.observaciones = (original.observaciones ?: "") + " Trámite asociado al trámite ${nuevoPadre.tramite.codigo} por " +
//                "${session.usuario.login} el ${new Date().format('dd-MM-yyyy HH:mm')}"
//        def nuevaObs = " Trámite asociado al trámite ${nuevoPadre.tramite.codigo} por " +
//                "${session.usuario.login} el ${new Date().format('dd-MM-yyyy HH:mm')}"
//        original.observaciones = tramitesService.modificaObservaciones(original.observaciones, nuevaObs)
//        original.tramite.observaciones = tramitesService.modificaObservaciones(original.tramite.observaciones, nuevaObs)
        def nuevaObsPersDoc = "Asociado al trámite ${nuevoPadre.tramite.codigo}"
        def nuevaObsTram = "Trámite ${original.rolPersonaTramite.descripcion}"
        if (original.departamento) {
            nuevaObsTram += " el dpto. ${original.departamento.codigo}"
        } else if (original.persona) {
            nuevaObsTram += " el usuario ${original.persona.login}"
        }
        nuevaObsTram += " asociado al trámite ${nuevoPadre.tramite.codigo}"

//        original.observaciones = tramitesService.makeObservaciones(original.observaciones, nuevaObsPersDoc, "", session.usuario.login)
//        original.tramite.observaciones = tramitesService.makeObservaciones(original.tramite.observaciones, nuevaObsTram, "", session.usuario.login)

        def observacionOriginal = original.observaciones
        def accion = "Asociación de trámite"
        def solicitadoPor = ""
        def usuario = session.usuario.login
        def texto = nuevaObsPersDoc
        def nuevaObservacion = ""
        original.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
        observacionOriginal = original.tramite.observaciones
        texto = nuevaObsTram
        original.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

        nuevoPadre.tramite.estado = "C"
        def msg = ""
        if (!original.save(flush: true)) {
            msg += renderErrors(bean: original)
        }
        if (!original.tramite.save(flush: true)) {
            msg += renderErrors(bean: original.tramite)
        }
        if (!nuevoPadre.tramite.save(flush: true)) {
            msg += renderErrors(bean: nuevoPadre.tramite)
        }
        if (msg != "") {
            msg = "NO*<ul>" + msg + "</ul>"
        } else {
            msg = "OK"
        }
        render msg
    }

    def copiaParaLista_ajax() {
        def tramite
        if (params.id) {
            def persDocTram = PersonaDocumentoTramite.get(params.id)
            tramite = persDocTram.tramite
        } else if (params.tramite) {
            tramite = Tramite.get(params.tramite)
        }
        def paraTramite = tramite.para
        def estadoAnulado = EstadoTramite.findByCodigo("E006")
        def estadoArchivado = EstadoTramite.findByCodigo("E005")
        def estadosNo = [estadoAnulado, estadoArchivado]

        if (estadosNo.contains(paraTramite.estado)) {
            return [tramite: tramite, error: "El trámite se encuentra <strong>${paraTramite.estado.descripcion}</strong>, no puede crear copias"]
        } else {
            def de = tramite.de
            def deDep = tramite.deDepartamento
            def para = tramite.para
            def persona = Persona.get(session.usuario.id)

            def disp, disponibles = [], users = [], disp2 = [], todos = []

            if (session.usuario.puedeTramitar) {
                disp = Departamento.list([sort: 'descripcion'])
            } else {
                disp = [persona.departamento]
            }
            disp.each { dep ->
//            disponibles.add([id: dep.id * -1, label: dep.descripcion, obj: dep])
                if (dep.id == persona.departamento.id) {
//                def users = Persona.findAllByDepartamento(dep)
                    def usuarios = Persona.findAllByDepartamento(dep)
                    usuarios.each {
                        if (it.id != de.id) {
                            if (!para.persona || (para.persona && para.personaId != it.id)) {
                                users += it
                            }
                        }
                    }
                    for (int i = users.size() - 1; i > -1; i--) {
                        if (!(users[i].estaActivo && users[i].puedeRecibir)) {
                            users.remove(i)
                        } else {
                            if (!(tramite.copias.persona.id*.toLong()).contains(users[i].id.toLong())) {
                                disponibles.add([id: users[i].id, label: users[i].toString(), obj: users[i]])
                            }
                        }
                    }
                }
            }

            disp.each { dep ->
                if (!deDep || (deDep && deDep.id != dep.id)) {
                    if (!(tramite.copias.departamento.id*.toLong()).contains(dep.id.toLong())) {
                        if (dep.triangulos.size() > 0) {
                            if (!para.departamento || (para.departamento && para.departamentoId != dep.id)) {
                                disp2.add([id: dep.id * -1, label: dep.descripcion, obj: dep])
                            }
                        }
                    }
                }
            }
            todos = disponibles + disp2
            return [tramite: tramite, disponibles: todos]
        }
    }

    def enviarCopias_ajax() {
        def tramite
        if (params.id) {
            def persDocTram = PersonaDocumentoTramite.get(params.id)
            tramite = persDocTram.tramite
        } else if (params.tramite) {
            tramite = Tramite.get(params.tramite)
        }
        def copias = params.copias.split("_")

        def rolCopia = RolPersonaTramite.findByCodigo("R002")
        def estadoEnviado = EstadoTramite.findByCodigo("E003")

        def errores = ""

        copias.each { copia ->
            def id = copia.toInteger()
            def copiaPers = new PersonaDocumentoTramite()
            if (id > 0) {
                copiaPers.persona = Persona.get(id)
            } else {
                copiaPers.departamento = Departamento.get(id * -1)
            }
            copiaPers.fechaEnvio = new Date()
            copiaPers.tramite = tramite
            copiaPers.rolPersonaTramite = rolCopia
            copiaPers.estado = estadoEnviado

            if (!copiaPers.save(flush: true)) {
                errores += "<li>" + renderErrors(bean: copiaPers) + "</li>"
            } else {
                def alerta = new Alerta()
                alerta.mensaje = "${session.departamento.codigo}:${session.usuario} te ha enviado un trámite."
                if (copiaPers.persona) {
                    alerta.controlador = "tramite"
                    alerta.accion = "bandejaEntrada"
                    alerta.persona = copiaPers.persona
                } else {
                    alerta.departamento = copiaPers.departamento
                    alerta.accion = "bandejaEntradaDpto"
                    alerta.controlador = "tramite3"
                }
                alerta.datos = copiaPers.id
                alerta.tramite = copiaPers.tramite
                if (!alerta.save(flush: true)) {
                    println "error save alerta " + alerta.errors
                }
            }
        }
        if (errores == "") {
            render "OK"
        } else {
            render "NO*<ul>" + errores + "</ul>"
        }
    }

    def cambiarEstado() {
        def tramite = Tramite.get(params.id)
        return [params: params, tramite: tramite]
    }

    def guardarEstado() {
        def tramite = Tramite.get(params.id)
        def estado = EstadoTramiteExterno.get(params.estado)

        tramite.estadoTramiteExterno = estado
        if (tramite.save(flush: true)) {
            render "OK*Estado cambiado exitosamente"
        } else {
            render "NO*Ha ocurrido un error al cambiar de estado el trámite: " + renderErrors(bean: tramite)
        }
    }

    def redireccionarTramites() {
        def persona = Persona.get(params.id)

        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def rolImprimir = RolPersonaTramite.findByCodigo('I005')

        def estadoEnviado = EstadoTramite.findByCodigo("E003")
        def estadoRecibido = EstadoTramite.findByCodigo("E004")
//                    def tramites = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite as p  inner join fetch p.tramite as tramites where p.persona=${hijo.id} and  p.rolPersonaTramite in (${rolPara.id + "," + rolCopia.id + "," + rolImprimir.id}) and p.fechaEnvio is not null and tramites.estadoTramite in (3,4) order by p.fechaEnvio desc ")

        def tramites = PersonaDocumentoTramite.withCriteria {
            eq("persona", persona)
            or {
                eq("rolPersonaTramite", rolPara)
                eq("rolPersonaTramite", rolCopia)
            }
            or {
                eq("estado", estadoEnviado)
                eq("estado", estadoRecibido)
            }
            order("fechaEnvio", "desc")
        }
        tramites = tramites.findAll { Tramite.countByAQuienContesta(it) == 0 }
        def personas
        def dep = persona.departamento
        if (persona.estaActivo) {
            personas = Persona.withCriteria {
                eq("departamento", persona.departamento)
                ne("id", persona.id)
                order("apellido", "asc")
            }.findAll {
                it.estaActivo
            }
        } else {
            def deps = Tramite.findAll("from Tramite where de=${persona.id} and departamento != ${dep.id} order by id desc")
            if (deps.size() > 0) {
                dep = deps.departamento.first()
            }
            personas = Persona.withCriteria {
                eq("departamento", dep)
                ne("id", persona.id)
                order("apellido", "asc")
            }.findAll {
                it.estaActivo
            }
        }
        return [persona: persona, tramites: tramites, personas: personas, dep: dep]
    }

    def redireccionarTramite_ajax() {
        def persona = Persona.get(params.id)
//        def dpto = persona.departamento
        def redDpto = null, redPrsn = null
        if (params.quien.toString().startsWith("-")) {
            redDpto = Departamento.get(params.quien.toInteger() * -1)
        } else {
            redPrsn = Persona.get(params.quien)
        }
        def pr = PersonaDocumentoTramite.get(params.pr)

        def errores = ""

//        println "redireccionar!! " + params
//        println persona
//        println pr
//        println redDpto
//        println redPrsn

        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCopia = RolPersonaTramite.findByCodigo("R002")
        def estadoAnulado = EstadoTramite.findByCodigo("E006")

        if (pr.rolPersonaTramite.codigo == "I005") {
            pr.delete(flush: true)
        } else {
            def obs = "Trámite antes dirigido a " + persona.nombre + " " + persona.apellido + ", redireccionado"

            def personaAntes = pr.persona
            def dptoAntes = pr.departamento

            if (redDpto) {
                def prtrExisten = PersonaDocumentoTramite.withCriteria {
                    eq("tramite", pr.tramite)
                    eq("departamento", redDpto)
                    inList("rolPersonaTramite", [rolPara, rolCopia])
                }
                if (prtrExisten.size() == 0) {
                    pr.persona = null
                    pr.departamento = redDpto
                    obs += " al departamento ${pr.departamento.descripcion}"
                } else {
//                    println "1"
                    if (pr.rolPersonaTramiteId == rolCopia.id) {
//                        println "2"
                        //si es copia, la anulo
                        pr.estado = estadoAnulado
                        pr.fechaAnulacion = new Date()
                        obs = "Trámite anulado automáticamente al redireccionar debido a un duplicado en la bandeja receptora"
                    } else {
//                        println "3"
                        prtrExisten.each { prtrExiste ->
                            if (prtrExiste.rolPersonaTramiteId == rolCopia.id) {
//                                println "4"
                                prtrExiste.estado = estadoAnulado
                                prtrExiste.fechaAnulacion = new Date()
                                def obs1 = "Trámite anulado automáticamente al redireccionar debido a un duplicado en la bandeja receptora"
                                def observacionOriginal = prtrExiste.observaciones
                                def accion = "Redirección de trámite"
                                def solicitadoPor = ""
                                def usuario = session.usuario.login
                                def texto = obs1
                                def nuevaObservacion = ""
                                prtrExiste.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
                                observacionOriginal = prtrExiste.tramite.observaciones
                                prtrExiste.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
//                                println "AQUI:: " + obs1 + "  " + prtrExiste.id
                                if (!prtrExiste.save(flush: true)) {
                                    println "ERROR AQUI: " + prtrExiste.errors
                                }
                                if (!prtrExiste.tramite.save(flush: true)) {
                                    println "ERROR AQUI3 : " + prtrExiste.tramite.errors
                                }
                            }
                        }
//                        println "AQUI>>> " + pr.id
                        pr.persona = null
                        pr.departamento = redDpto
                        obs += " al departamento ${pr.departamento.descripcion}"
                    }
                }
            } else {
                def prtrExisten = PersonaDocumentoTramite.withCriteria {
                    eq("tramite", pr.tramite)
                    eq("persona", redPrsn)
                    inList("rolPersonaTramite", [rolPara, rolCopia])
                }
                if (prtrExisten.size() == 0) {
                    pr.persona = redPrsn
                    obs += " al usuario ${pr.persona.login}"
                } else {
//                    println "5"
                    if (pr.rolPersonaTramiteId == rolCopia.id) {
                        //si es copia, la anulo
                        pr.estado = estadoAnulado
                        pr.fechaAnulacion = new Date()
                        obs = "Trámite anulado automáticamente al redireccionar debido a un duplicado en la bandeja receptora"
                    } else {
//                        println "6"
                        prtrExisten.each { prtrExiste ->
                            if (prtrExiste.rolPersonaTramiteId == rolCopia.id) {
//                                println "7"
                                prtrExiste.estado = estadoAnulado
                                prtrExiste.fechaAnulacion = new Date()
                                def obs1 = "Trámite anulado automáticamente al redireccionar debido a un duplicado en la bandeja receptora"
                                def observacionOriginal = prtrExiste.observaciones
                                def accion = "Redirección de trámite"
                                def solicitadoPor = ""
                                def usuario = session.usuario.login
                                def texto = obs1
                                def nuevaObservacion = ""
                                prtrExiste.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
                                observacionOriginal = prtrExiste.tramite.observaciones
                                prtrExiste.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

                                if (!prtrExiste.save(flush: true)) {
                                    println "ERROR AQUI2: " + prtrExiste.errors
                                }
                            }
                        }
                        pr.persona = redPrsn
                        obs += " al usuario ${pr.persona.login}"
                    }
                }
            }
//            obs += " el ${new Date().format('dd-MM-yyyy HH:mm')} por ${session.usuario.login}; "
            def tramite = pr.tramite
//            tramite.observaciones = (tramite.observaciones ?: "") + obs
//            pr.observaciones = (pr.observaciones ?: "") + obs

//            tramite.observaciones = tramitesService.modificaObservaciones(tramite.observaciones, obs)
//            pr.observaciones = tramitesService.modificaObservaciones(pr.observaciones, obs)

//            tramite.observaciones = tramitesService.makeObservaciones(tramite.observaciones, obs, "", session.usuario.login)
//            pr.observaciones = tramitesService.makeObservaciones(pr.observaciones, obs, "", session.usuario.login)

            def observacionOriginal = pr.observaciones
            def accion = "Redirección de trámite"
            def solicitadoPor = ""
            def usuario = session.usuario.login
            def texto = obs
            def nuevaObservacion = ""
            pr.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
            observacionOriginal = tramite.observaciones
            tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

            if (tramite.save(flush: true)) {
//                        println "tr.save ok"
            } else {
                errores += renderErrors(bean: tramite)
                println tramite.errors
            }
            if (!pr.persona && !pr.departamento) {
                pr.persona = personaAntes
                pr.departamento = dptoAntes
//                pr.observaciones += " Ha ocurrido un error al redireccionar. "
//                tramite.observaciones += " Ha ocurrido un error al redireccionar. "
//                pr.observaciones = tramitesService.modificaObservaciones(pr.observaciones, "Ha ocurrido un error al redireccionar (${new Date().format('dd-MM-yyyy HH:mm')}).")
//                tramite.observaciones = tramitesService.modificaObservaciones(tramite.observaciones, "Ha ocurrido un error al redireccionar.")
//                pr.observaciones = tramitesService.makeObservaciones(pr.observaciones, "Redirección no efectuada a causa de un error.", "", "")
//                tramite.observaciones = tramitesService.makeObservaciones(tramite.observaciones, "Redirección no efectuada a causa de un error.", "", "")

                observacionOriginal = pr.observaciones
                accion = ""
                solicitadoPor = ""
                usuario = ""
                texto = "Redirección no efectuada a causa de un error."
                nuevaObservacion = ""
                pr.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
                observacionOriginal = tramite.observaciones
                tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

                if (tramite.save(flush: true)) {
//                    println "ok"
                }
                errores += "<ul><li>Ha ocurrido un error al redireccionar.</li></ul>"
            }
            if (pr.save(flush: true)) {
//                        println "pr save ok"
            } else {
                println pr.errors
                errores += renderErrors(bean: pr)
            }
        }
        if (errores == "") {
            render "OK"
        } else {
            render errores
        }
    }

    def arbolAdminTramite() {
        def html = "", url = "", tramite = null
        if (params.id) {
            def usu = Persona.get(session.usuario.id)
            def puedeAdministrar = session.usuario.puedeAdmin
//            println "PUEDE??? " + puedeAdministrar

            tramite = Tramite.get(params.id.toLong())
            if (tramite) {
                def principal = tramite
                if (tramite.padre) {
                    principal = tramite.padre
                    while (true) {
                        if (!principal.padre)
                            break
                        else {
                            principal = principal.padre
                        }
                    }
                }
                html = "<ul>" + "\n"
                html += makeTreeExtended(principal)
                html += "</ul>" + "\n"
            }
            url = createLink(controller: "buscarTramite", action: "busquedaTramite")
        }
        return [html2: html, url: url, tramite: tramite]
    }

    private String makeTreeExtended(Tramite principal) {
        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCc = RolPersonaTramite.findByCodigo("R002")

        def paras = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(principal, rolPara)
        def ccs = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(principal, rolCc)

        def html = ""

        //esto muestra una hoja por destinatario
        paras.each { para ->
            html += makeLeaf(para)
        }

        //el para y las copias son hermanos
        ccs.each { para ->
            html += makeLeaf(para)
        }
        return html
    }

    private String makeLeaf(PersonaDocumentoTramite pdt) {
        def html = "", clase = "", rel = "para", data = ""
        if (pdt.rolPersonaTramite.codigo == "R002") {
            rel = "copia"
        }
        def hijos = Tramite.findAllByAQuienContesta(pdt, [sort: "fechaCreacion", order: "asc"])
        if (hijos.size() > 0) {
            clase += " jstree-open"
        }
        def estado = ""
        if (pdt.fechaEnvio) {
            clase += " enviado"
            estado = "Enviado"
        }
        if (pdt.fechaRecepcion) {
            clase += " recibido"
            estado = "Recibido"
        }

        if (pdt.fechaArchivo) {
            clase += " archivado"
            estado = "Archivado"
        }

        if (pdt.fechaAnulacion) {
            clase += " anulado"
            estado = "Anulado"
        }

        if (pdt.tramite.estadoTramiteExterno) {
            clase += " externo"
        }

        rel += estado

        def rol = pdt.rolPersonaTramite
        def duenioPrsn = pdt.tramite.de.id
        def duenioDpto = pdt.tramite.deDepartamento?.id
        def paraStr = "Para: "
        if (rol.codigo == "R002") {
            paraStr = "CC: "
        }
//        paraStr += pdt.departamento ? pdt.departamento.descripcion : pdt.persona.departamento.codigo + ":" + pdt.persona.login
        if (pdt.departamento) {
            paraStr += pdt.departamento.descripcion
        } else if (pdt.persona) {
            paraStr += pdt.persona.departamento.codigo + ":" + pdt.persona.login
        }

        def deStr = "De: " + (pdt.tramite.deDepartamento ? pdt.tramite.deDepartamento.codigo : pdt.tramite.de.departamento.codigo + ":" + pdt.tramite.de.login)

        data += ',"tramite":"' + pdt.tramiteId + '"'
//        data += ',"duenio":"' + duenio + '"'
        data += ',"codigo":"' + pdt.tramite.codigo + '"'
        data += ',"de":"' + deStr + '"'
        data += ',"para":"' + paraStr + '"'
        if (pdt.tramite.padre) {
            data += ',"padre":"' + pdt.tramite.padreId + '"'
        }
        if (tramitesService.verificaHijos(pdt, EstadoTramite.findByCodigo("E006"))) {
            //false: no tiene hijos vivos
//            println "tiene hijos"
            clase += " tieneHijos"
        }
        if (pdt.tramite.padre) {
            clase += " tienePadre"
        }

        if (duenioPrsn == session.usuario.id || duenioDpto == session.usuario.departamento.id) {
            clase += " esMio"
        }

        html += "<li id='${pdt.id}' class='${clase}' data-jstree='{\"type\":\"${rel}\"${data}}' >"
        html += tramiteInfo(pdt)
        html += "\n"
        if (hijos.size() > 0) {
            html += "<ul>" + "\n"
            hijos.each { hijo ->
                html += makeTreeExtended(hijo)
            }
            html += "</ul>" + "\n"
        }
        html += "</li>"
        return html
    }

    private static String tramiteFechas(PersonaDocumentoTramite tramiteParaInfo) {
        def strInfo = ""
        strInfo += "<strong>creado</strong> el " + tramiteParaInfo.tramite.fechaCreacion.format("dd-MM-yyyy HH:mm")
        def clase
        if (tramiteParaInfo.fechaEnvio) {
            clase = tramiteParaInfo.fechaAnulacion ? 'muted' : 'info'
            strInfo += ", <span class='text-${clase}'><strong>enviado</strong> el " + tramiteParaInfo.fechaEnvio.format("dd-MM-yyyy HH:mm") + "</span>"
        }
        if (tramiteParaInfo.fechaRecepcion) {
            clase = tramiteParaInfo.fechaAnulacion ? 'muted' : 'success'
            strInfo += ", <span class='text-${clase}'><strong>recibido</strong> el " + tramiteParaInfo.fechaRecepcion.format("dd-MM-yyyy HH:mm") + "</span>"
        }
        if (tramiteParaInfo.fechaArchivo) {
            clase = tramiteParaInfo.fechaAnulacion ? 'muted' : 'warning'
            strInfo += ", <span class='text-${clase}'><strong>archivado</strong> el " + tramiteParaInfo.fechaArchivo.format("dd-MM-yyyy HH:mm") + "</span>"
        }
        if (tramiteParaInfo.fechaAnulacion) {
            strInfo += ", <span class='text-muted'><strong>anulado</strong> el " + tramiteParaInfo.fechaAnulacion.format("dd-MM-yyyy HH:mm") + "</span>"
        }
        return strInfo
    }

    private static String tramiteInfo(PersonaDocumentoTramite tramiteParaInfo) {
        def paraStr, deStr
        if (tramiteParaInfo.tramite.tipoDocumento.codigo == "OFI") {
            paraStr = tramiteParaInfo.tramite.paraExterno + " (EXT)"
        } else {
            if (tramiteParaInfo.departamento) {
                paraStr = tramiteParaInfo.departamento.descripcion
            } else if (tramiteParaInfo.persona) {
                paraStr = tramiteParaInfo.persona.departamento.codigo + ":" + tramiteParaInfo.persona.login
            } else {
                paraStr = ""
            }
        }
        if (tramiteParaInfo.tramite.tipoDocumento.codigo == "DEX") {
            deStr = tramiteParaInfo.tramite.paraExterno + " (EXT)"
        } else {
            deStr = tramiteParaInfo.tramite.deDepartamento ?
                    tramiteParaInfo.tramite.deDepartamento.codigo :
                    tramiteParaInfo.tramite.de.departamento.codigo + ":" + tramiteParaInfo.tramite.de.login
        }
        def rol = tramiteParaInfo.rolPersonaTramite
        def strInfo = ""
        if (tramiteParaInfo.fechaAnulacion) {
            strInfo += "<span class='text-muted'>"
        }
        if (rol.codigo == "R002") {
            strInfo += "[CC] "
        }
        strInfo += "<strong>${tramiteParaInfo.tramite.codigo} </strong>"
        strInfo += "<small>("
        strInfo += "<strong>DE</strong>: ${deStr}, <strong>${rol.descripcion}</strong>: ${paraStr}, "
        strInfo += tramiteFechas(tramiteParaInfo)
        strInfo += ")</small>"
        if (tramiteParaInfo.fechaAnulacion) {
            strInfo += "</span>"
        }
        if (tramiteParaInfo.tramite.estadoTramiteExterno) {
            strInfo += " - " + tramiteParaInfo.tramite.estadoTramiteExterno.descripcion
        }
        return strInfo
    }

    def desarchivar() {
        def persDocTram = PersonaDocumentoTramite.get(params.id)

        def estadoArchivado = EstadoTramite.findByCodigo("E005")
        def estadoAnulado = EstadoTramite.findByCodigo("E006")
//        def estados = [estadoAnulado, estadoArchivado]
        def estados = [estadoAnulado]


        if (estados.contains(persDocTram.estado)) {
            render "NO*el trámite está ${persDocTram.estado.descripcion}, no puede quitar el archivado"
        } else {
//            def obs = " Quitado el archivado por ${session.usuario.login} el ${new Date().format('dd-MM-yyyy HH:mm')} " +
//                    "(originalmente archivado el ${persDocTram.fechaArchivo.format('dd-MM-yyyy HH:mm')}): " + params.texto
//            persDocTram.observaciones = (persDocTram.observaciones ?: "") + obs
            def o = "Archivado originalmente el ${persDocTram.fechaArchivo.format('dd-MM-yyyy HH:mm')}"
//            if (params.texto.trim() != "") {
//                o += ": " + params.obs
//            }
//            def obs = "Quitado el archivado. " + o
//            persDocTram.observaciones = tramitesService.makeObservaciones(persDocTram.observaciones, obs, params.aut, session.usuario.login)

            def observacionOriginal = persDocTram.observaciones
            def accion = "Quitado el archivado"
            def solicitadoPor = params.aut
            def usuario = session.usuario.login
            def texto = o
            def nuevaObservacion = params.obs
            persDocTram.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

//            persDocTram.observaciones = tramitesService.modificaObservaciones(persDocTram.observaciones, obs)
            if (persDocTram.rolPersonaTramite.codigo == "R001") { //PARA
//                persDocTram.tramite.observaciones = (persDocTram.tramite.observaciones ?: "") + obs
//                persDocTram.observaciones = tramitesService.modificaObservaciones(persDocTram.observaciones, obs)
                def obs = "PARA "
                if (persDocTram.departamento) {
                    obs += "el dpto. ${persDocTram.departamento.codigo}"
                } else if (persDocTram.persona) {
                    obs += "el usuario ${persDocTram.persona.login}"
                }
                obs += ", " + o
//                persDocTram.tramite.observaciones = tramitesService.makeObservaciones(persDocTram.tramite.observaciones, obs, params.aut, session.usuario.login)
                observacionOriginal = persDocTram.tramite.observaciones
                texto = obs
                persDocTram.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
            } else if (persDocTram.rolPersonaTramite.codigo == "R002") { //CC
//                persDocTram.tramite.observaciones = (persDocTram.tramite.observaciones ?: "") + " COPIA" + obs
//                persDocTram.observaciones = tramitesService.modificaObservaciones(persDocTram.observaciones, " COPIA " + obs)
                def obs = "COPIA para "
                if (persDocTram.departamento) {
                    obs += "el dpto. ${persDocTram.departamento.codigo}"
                } else if (persDocTram.persona) {
                    obs += "el usuario ${persDocTram.persona.login}"
                }
                obs += ", " + o
//                persDocTram.tramite.observaciones = tramitesService.makeObservaciones(persDocTram.tramite.observaciones, obs, params.aut, session.usuario.login)
                observacionOriginal = persDocTram.tramite.observaciones
                texto = obs
                persDocTram.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
            }
            persDocTram.fechaArchivo = null
            persDocTram.estado = EstadoTramite.findByCodigo("E004")  // RECIBIDO
            if (persDocTram.save(flush: true)) {
                if (!persDocTram.tramite.save(flush: true)) {
                    println "error al guardar observaciones del tramite: " + persDocTram.tramite.errors
                }
                render "OK"
            } else {
                render "NO*" + renderErrors(bean: persDocTram)
            }
        }

    }

    def desrecibir() {
        def persDocTram = PersonaDocumentoTramite.get(params.id)

        def estadoArchivado = EstadoTramite.findByCodigo("E005")
        def estadoAnulado = EstadoTramite.findByCodigo("E006")
        def estados = [estadoAnulado, estadoArchivado]
        if (estados.contains(persDocTram.estado)) {
            render "NO*el trámite está ${persDocTram.estado.descripcion}, no puede quitar el recibido"
        } else {

//            def obs = " Quitado el recibido por ${session.usuario.login} el ${new Date().format('dd-MM-yyyy HH:mm')} " +
//                    "(originalmente recibido el ${persDocTram.fechaRecepcion.format('dd-MM-yyyy HH:mm')}): " + params.texto

            def o = " Recibido originalmente el ${persDocTram.fechaRecepcion.format('dd-MM-yyyy HH:mm')}"
//            if (params.texto.trim() != "") {
//                o += ": " + params.texto
//            }
//            def obs = "Quitado el recibido. " + o
//            persDocTram.observaciones = tramitesService.makeObservaciones(persDocTram.observaciones, obs, params.aut, session.usuario.login)

            def observacionOriginal = persDocTram.observaciones
            def accion = "Quitado el recibido"
            def solicitadoPor = ""
            def usuario = session.usuario.login
            def texto = o
            def nuevaObservacion = params.texto
            persDocTram.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

//            persDocTram.observaciones = (persDocTram.observaciones ?: "") + obs
//            persDocTram.observaciones = tramitesService.modificaObservaciones(persDocTram.observaciones, obs)
            if (persDocTram.rolPersonaTramite.codigo == "R001") { //PARA
//                persDocTram.tramite.observaciones = (persDocTram.tramite.observaciones ?: "") + obs
//                persDocTram.tramite.observaciones = tramitesService.modificaObservaciones(persDocTram.tramite.observaciones, obs)
                def obs = "PARA "
                if (persDocTram.departamento) {
                    obs += "el dpto. ${persDocTram.departamento.codigo}"
                } else if (persDocTram.persona) {
                    obs += "el usuario ${persDocTram.persona.login}"
                }
                obs += ", " + o
//                persDocTram.tramite.observaciones = tramitesService.makeObservaciones(persDocTram.tramite.observaciones, obs, params.aut, session.usuario.login)

                observacionOriginal = persDocTram.tramite.observaciones
                texto = obs
                persDocTram.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

            } else if (persDocTram.rolPersonaTramite.codigo == "R002") { //CC
//                persDocTram.tramite.observaciones = (persDocTram.tramite.observaciones ?: "") + " COPIA" + obs
//                persDocTram.tramite.observaciones = tramitesService.modificaObservaciones(persDocTram.tramite.observaciones, "COPIA " + obs)
                def obs = "COPIA para "
                if (persDocTram.departamento) {
                    obs += "el dpto. ${persDocTram.departamento.codigo}"
                } else if (persDocTram.persona) {
                    obs += "el usuario ${persDocTram.persona.login}"
                }
                obs += ", " + o
//                persDocTram.tramite.observaciones = tramitesService.makeObservaciones(persDocTram.tramite.observaciones, obs, params.aut, session.usuario.login)
                observacionOriginal = persDocTram.tramite.observaciones
                texto = obs
                persDocTram.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
            }
            persDocTram.fechaRecepcion = null
            persDocTram.fechaLimiteRespuesta = null
            persDocTram.estado = EstadoTramite.findByCodigo("E003")  // ENVIADO
            if (persDocTram.save(flush: true)) {
                if (!persDocTram.tramite.save(flush: true)) {
                    println "error al guardar observaciones del tramite: " + persDocTram.tramite.errors
                }
                if (persDocTram.rolPersonaTramite.codigo == "R001") { //PARA
                    def estadoEnviado = EstadoTramite.findByCodigo("E003")
                    persDocTram.tramite.estadoTramite = estadoEnviado
                    if (!persDocTram.tramite.save(flush: true)) {
                        println "Error al cambiar el estado del tramite: " + persDocTram.tramite.errors
                    }
                }
                render "OK"
            } else {
                render "NO*" + renderErrors(bean: persDocTram)
            }
        }
    }

    def anular() {

        def persDocTram = PersonaDocumentoTramite.get(params.id)
        def estadoArchivado = EstadoTramite.findByCodigo("E005")
        def estados = [estadoArchivado]

        if (estados.contains(persDocTram.estado)) {
            render "NO*el trámite está ${persDocTram.estado.descripcion}, no puede anular el trámite archivado"

        } else {
            def funcion = { objeto ->
                println "anulando " + objeto.id + " " + objeto.rolPersonaTramite.descripcion + "  " + objeto.tramite
                def anulado = EstadoTramite.findByCodigo("E006")
                objeto.estado = anulado
                objeto.fechaAnulacion = new Date()
//            objeto.observaciones = (objeto.observaciones ?: "") + "Documento anulado por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}: ${params.texto};"
//                def nuevaObs = "Documento anulado por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}: ${params.texto}"
//                objeto.observaciones = tramitesService.modificaObservaciones(objeto.observaciones.toString(), nuevaObs)
                def nuevaObs = "Anulado"
                if (params.texto.trim() != "") {
                    nuevaObs += ": " + params.texto
                }
//                objeto.observaciones = tramitesService.makeObservaciones(objeto.observaciones, nuevaObs, params.aut, session.usuario.login)

                def observacionOriginal = objeto.observaciones
                def accion = "Aulación"
                def solicitadoPor = params.aut
                def usuario = session.usuario.login
                def texto = ""
                def nuevaObservacion = params.texto
                objeto.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

                if (objeto.rolPersonaTramite.codigo == "R002") {
//                    def nuevaObs2 = " COPIA anulada por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto}"
//                    objeto.tramite.observaciones = tramitesService.modificaObservaciones(objeto.tramite.observaciones, nuevaObs2)
                    //                objeto.tramite.observaciones = (objeto.tramite.observaciones ?: "") + " COPIA anulada por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto}; "
                    nuevaObs = "COPIA para "
                    if (objeto.departamento) {
                        nuevaObs += "el dpto. ${objeto.departamento.codigo}"
                    } else if (objeto.persona) {
                        nuevaObs += "el usuario ${objeto.persona.login}"
                    }
//                    nuevaObs += " anulada"
//                    if (params.texto.trim() != "") {
//                        nuevaObs += ": " + params.texto
//                    }
//                    objeto.tramite.observaciones = tramitesService.makeObservaciones(objeto.tramite.observaciones, nuevaObs, params.aut, session.usuario.login)

                    observacionOriginal = objeto.tramite.observaciones
                    texto = nuevaObs
                    objeto.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
                }
                if (objeto.rolPersonaTramite.codigo == "R001") {
//                    objeto.tramite.observaciones = tramitesService.modificaObservaciones(objeto.tramite.observaciones, nuevaObs)
//                objeto.tramite.observaciones = (objeto.tramite.observaciones ?: "") + " Documento anulado por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto}; "
                    nuevaObs = "PARA "
                    if (objeto.departamento) {
                        nuevaObs += "el dpto. ${objeto.departamento.codigo}"
                    } else if (objeto.persona) {
                        nuevaObs += "el usuario ${objeto.persona.login}"
                    }
//                    nuevaObs += " anulado"
//                    if (params.texto.trim() != "") {
//                        nuevaObs += ": " + params.texto
//                    }
//                    objeto.tramite.observaciones = tramitesService.makeObservaciones(objeto.tramite.observaciones, nuevaObs, params.aut, session.usuario.login)
                    observacionOriginal = objeto.tramite.observaciones
                    texto = nuevaObs
                    objeto.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
                }
                objeto.tramite.save(flush: true)
                if (!objeto.save(flush: true)) {
                    println "error en el save anular " + objeto.errors
                }else{
                    /*alertas*/
                    def alerta
                    if(objeto.departamento){
                        alerta=Alerta.findAllByTramiteAndDepartamento(objeto.tramite,objeto.departamento)
                        //println "busco alerta dep "+alerta+"  "+objeto.tramite.id+"  "+objeto.departamento.id
                        alerta.each {a->
                            if(a.fechaRecibido==null) {
                                a.fechaRecibido = new Date();
                                a.save(flush: true)
                            }
                        }
                    }
                    if(objeto.persona){

                        alerta=Alerta.findAllByTramiteAndPersona(objeto.tramite,objeto.persona)
                       // println "busco alerta persona "+alerta+"  "+objeto.tramite.id+"  "+objeto.persona.id
                        alerta.each {a->
                            if(a.fechaRecibido==null) {
                                a.fechaRecibido = new Date();
                                a.save(flush: true)
                            }
                        }
                    }
                }
            }
            def rolCopia = RolPersonaTramite.findByCodigo("R002")
            def pdt = PersonaDocumentoTramite.get(params.id)
            getCadenaDown(pdt, funcion)
            if (pdt.rolPersonaTramite.codigo == "R001") {
                def copias = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(pdt.tramite, rolCopia)
                if (copias.size() > 0) {
                    copias.each {
                        getCadenaDown(it, funcion)
                    }
                }
            }

            if (pdt.tramite.aQuienContesta) {
                pdt.tramite.aQuienContesta.estado = EstadoTramite.findByCodigo("E004")
                pdt.tramite.aQuienContesta.fechaAnulacion = null
                pdt.tramite.aQuienContesta.fechaArchivo = null
//            pdt.tramite.aQuienContesta.observaciones = (pdt.tramite.aQuienContesta.observaciones ?: "") + " Tramite reactivado por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto}; "
//                def nuevaObs = " Tramite reactivado por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto}"
//                pdt.tramite.aQuienContesta.observaciones = tramitesService.modificaObservaciones(pdt.tramite.aQuienContesta.observaciones, nuevaObs)
                def nuevaObs = "Trámite reactivado al anularse ${persDocTram.tramite.codigo}"
//                if (params.texto.trim()) {
//                    nuevaObs += ": " + params.texto
//                }
//                pdt.tramite.aQuienContesta.observaciones = tramitesService.makeObservaciones(pdt.tramite.aQuienContesta.observaciones, nuevaObs, params.aut, session.usuario.login)
                def observacionOriginal = pdt.tramite.aQuienContesta.observaciones
                def accion = "Reactivación por anulación de trámite derivado"
                def solicitadoPor = params.aut
                def usuario = session.usuario.login
                def texto = nuevaObs
                def nuevaObservacion = params.texto
                pdt.tramite.aQuienContesta.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

                pdt.tramite.aQuienContesta.save(flush: true)
                nuevaObs = "Trámite ${pdt.tramite.aQuienContesta.rolPersonaTramite.descripcion}"
                if (pdt.tramite.aQuienContesta.departamento) {
                    nuevaObs += " el dpto. ${pdt.tramite.aQuienContesta.departamento.codigo}"
                } else if (pdt.tramite.aQuienContesta.persona) {
                    nuevaObs += " el usuario ${pdt.tramite.aQuienContesta.persona.login}"
                }
                nuevaObs += " reactivado al anularse ${persDocTram.tramite.codigo}"
//                if (params.texto.trim()) {
//                    nuevaObs += ": " + params.texto
//                }
//                pdt.tramite.aQuienContesta.tramite.observaciones = tramitesService.makeObservaciones(pdt.tramite.aQuienContesta.tramite.observaciones, nuevaObs, params.aut, session.usuario.login)
                observacionOriginal = pdt.tramite.aQuienContesta.tramite.observaciones
                texto = nuevaObs
                nuevaObservacion = params.texto
                pdt.tramite.aQuienContesta.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
            }

            render "OK"
        }


    }

    def desanular() {
        def pdt = PersonaDocumentoTramite.get(params.id)

        def estadoPorEnviar = EstadoTramite.findByCodigo("E001")
        def estadoEnviado = EstadoTramite.findByCodigo("E003")
        def estadoRecibido = EstadoTramite.findByCodigo("E004")
        def estadoArchivado = EstadoTramite.findByCodigo("E005")
        def estadoAnulado = EstadoTramite.findByCodigo("E006")

        if (!pdt.fechaEnvio) {
            pdt.estado = estadoPorEnviar
        }
        if (pdt.fechaEnvio) {
            pdt.estado = estadoEnviado
        }
        if (pdt.fechaRecepcion) {
            pdt.estado = estadoRecibido
        }
        if (pdt.fechaArchivo) {
            pdt.estado = estadoArchivado
        }
//        def nuevaObs = "Documento reactivado por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto}"
////        pdt.observaciones = (pdt.observaciones ?: "") + "Documento reactivado por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto};"
//        pdt.observaciones = tramitesService.modificaObservaciones(pdt.observaciones, nuevaObs)
//        def nuevaObs = "Documento reactivado"
//        if (params.texto.trim() != "") {
//            nuevaObs += ": " + params.texto
//        }
//        pdt.observaciones = tramitesService.makeObservaciones(pdt.observaciones, nuevaObs, params.aut, session.usuario.login)

        def observacionOriginal = pdt.observaciones
        def accion = "Reactivación"
        def solicitadoPor = params.aut
        def usuario = session.usuario.login
        def texto = ""
        def nuevaObservacion = params.texto
        pdt.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

        pdt.fechaAnulacion = null
        if (pdt.rolPersonaTramite.codigo == "R002") {
//            def nuevaObs2 = "COPIA reactivada por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto}"
//            pdt.tramite.observaciones = tramitesService.modificaObservaciones(pdt.tramite.observaciones, nuevaObs2)
//            pdt.tramite.observaciones = (pdt.tramite.observaciones ?: "") + "COPIA reactivada por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto}; "
            def nuevaObs = "COPIA para"
            if (pdt.departamento) {
                nuevaObs += " el dpto. ${pdt.departamento.codigo}"
            } else if (pdt.persona) {
                nuevaObs += " el usuario ${pdt.persona.login}"
            }
            nuevaObs += " reactivada"
//            if (params.texto.trim() != "") {
//                nuevaObs += ": " + params.texto
//            }
//            pdt.tramite.observaciones = tramitesService.makeObservaciones(pdt.tramite.observaciones, nuevaObs, params.aut, session.usuario.login)

            observacionOriginal = pdt.tramite.observaciones
            texto = nuevaObs
            pdt.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

        }
        if (pdt.rolPersonaTramite.codigo == "R001") {
//            pdt.tramite.observaciones = (pdt.tramite.observaciones ?: "") + "Documento reactivado por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto}; "
//            pdt.tramite.observaciones = tramitesService.modificaObservaciones(pdt.tramite.observaciones, nuevaObs)
            def nuevaObs = "PARA"
            if (pdt.departamento) {
                nuevaObs += " el dpto. ${pdt.departamento.codigo}"
            } else if (pdt.persona) {
                nuevaObs += " el usuario ${pdt.persona.login}"
            }
            nuevaObs += " reactivado"
//            if (params.texto.trim() != "") {
//                nuevaObs += ": " + params.texto
//            }
//            pdt.tramite.observaciones = tramitesService.makeObservaciones(pdt.tramite.observaciones, nuevaObs, params.aut, session.usuario.login)

            observacionOriginal = pdt.tramite.observaciones
            texto = nuevaObs
            pdt.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
        }
        pdt.tramite.save(flush: true)
        if (pdt.save(flush: true)) {
            render "OK"
        } else {
            println "erros " + pdt.errors
            render "NO"
        }
    }

    def getCadenaDown(pdt, funcion) {
        println "get cade down " + pdt
        def res = []
        def tramite = Tramite.findAll("from Tramite where aQuienContesta=${pdt.id}")
        println "tramite " + tramite
        def roles = [RolPersonaTramite.findByCodigo("R002"), RolPersonaTramite.findByCodigo("R001")]
        def lvl
        funcion pdt
        if (tramite) {
            tramite = tramite.pop()
            def tmp = [:]
            tmp.put("nodo", tramite)
            tmp.put("tipo", "tramite")
            def pdts = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteInList(tramite, roles)
            tmp.put("hijos", [])

            pdts.each {
                def r = getHijos(it, roles, funcion)
                if (r.size() > 0)
                    tmp["hijos"] += r
            }
            tmp.put("origen", pdt)
            res.add(tmp)
            res = getHermanos(tramite, res, roles, funcion)
        } else {
            return []
        }


        println "res lol " + res

    }

    def getHermanos(tramite, res, roles, funcion) {
//        println "get hermanos "+tramite.id
        def lvl
        def hermanos = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteInList(tramite, roles)
        while (hermanos.size() > 0) {
            def nodo = hermanos.pop()
            def tmp = [:]
            tmp.put("nodo", nodo)
            tmp.put("hijos", getHijos(nodo, roles, funcion))
            tmp.put("tipo", "pdt")
            funcion nodo
            res.add(tmp)

        }
//        println "return get hermanos "+res
        return res
    }

    def getHijos(pdt, roles, funcion) {
//        println "get hijos "+pdt.id+" "+pdt.rolPersonaTramite.descripcion
        def res = []
        def t = Tramite.findByAQuienContesta(pdt)
        if (t) {
            def tmp = [:]
            tmp.put("nodo", t)
            tmp.put("tipo", "tramite")
            tmp.put("hijos", [])
            def pdts = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteInList(t, roles)
            tmp.put("hijos", [])
            pdts.each {
                def r = getHijos(it, roles, funcion)
                if (r.size() > 0)
                    tmp["hijos"] += r
            }
            res = getHermanos(t, res, roles, funcion)
            res.add(tmp)
        }
//        println "fin hijos "+res
        return res
    }


}
