package happy.tramites

import happy.alertas.Alerta
import happy.seguridad.Persona
import happy.seguridad.Shield

class TramiteAdminController extends Shield {

    def tramitesService

    def redireccionarTramitesUI() {

    }

    def buscarPersonasRedireccionar() {
        def nombre = params.nombre.trim() != "" ? params.nombre.trim() : null
        def apellido = params.apellido.trim() != "" ? params.apellido.trim() : null
        def user = params.user.trim() != "" ? params.user.trim() : null
        def resultado = []
        def band
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def enviado = EstadoTramite.findByCodigo("E003")
        def recibido = EstadoTramite.findByCodigo("E004")
        def anulado = EstadoTramite.findByCodigo("E006")
        def data = [:]
        def personas
        def contador = 0
        personas = Persona.withCriteria {
            if (nombre) {
                ilike("nombre", "%" + nombre + "%")
            }
            if (apellido) {
                ilike("apellido", "%" + apellido + "%")
            }
            if (user) {
                ilike("login", "%" + user + "%")
            }
            maxResults(10)
        }

        personas.each { pr ->
            contador = 0
            data = [:]
            data.persona = pr
            data.tieneTrmt = 'N'
            def tramites = PersonaDocumentoTramite.withCriteria {
                eq("persona", pr)
                inList("rolPersonaTramite", [rolPara, rolCopia])
                isNotNull("fechaEnvio")
                inList("estado", [enviado, recibido])
            }

            tramites.each { tr ->
                if (!(tr.tramite.tipoDocumento.codigo == "OFI")) {
                    band = tramitesService.verificaHijos(tr, anulado)
                    if (!band) {
                        contador += 1
                    }
                }
            }
            if (contador) data.tieneTrmt = 'S'
            resultado.add(data)
        }
        return [personas: resultado]
    }

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

                    def personas = PersonaDocumentoTramite.withCriteria {
                        eq("tramite", tr)
                        or {
                            eq("rolPersonaTramite", rolPara)
                            eq("rolPersonaTramite", rolCc)
                        }
                        eq("estado", estadoRecibido)
                        tramite {
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

        def nuevaObsPersDoc = "Asociado al trámite ${nuevoPadre.tramite.codigo}"
        def nuevaObsTram = "Trámite ${original.rolPersonaTramite.descripcion}"
        if (original.departamento) {
            nuevaObsTram += " el dpto. ${original.departamento.codigo}"
        } else if (original.persona) {
            nuevaObsTram += " el usuario ${original.persona.login}"
        }
        nuevaObsTram += " asociado al trámite ${nuevoPadre.tramite.codigo}"

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

//        def puede = true

        if(!paraTramite) {
            if(tramite.copias.size() == 0) {
                return [tramite: tramite, error: "No puede crear copias"]
            }
        }

//        (tramite.para + tramite.allCopias).each {prtr->
//            if(estadosNo.contains(prtr.estado)) {
//                puede = false;
//            }
//        }

        if (estadosNo.contains(paraTramite?.estado)) {
//        if (puede) {
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
                if (dep.id == persona.departamento.id) {
                    def usuarios = Persona.findAllByDepartamento(dep)
                    usuarios.each {
                        if (it.id != de.id) {
                            if (!para?.persona || (para?.persona && para?.personaId != it.id)) {
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
        println("params " +  params)
        def tramite
        if (params.id) {
            def persDocTram = PersonaDocumentoTramite.get(params.id)
            tramite = persDocTram.tramite
        } else if (params.tramite) {
            tramite = Tramite.get(params.tramite)
        }
        def copias = params.copias.trim().split("_")

        def rolCopia = RolPersonaTramite.findByCodigo("R002")
        def estadoEnviado = EstadoTramite.findByCodigo("E003")
        def estadoAnulado = EstadoTramite.findByCodigo("E006")
        def estadoArchivado = EstadoTramite.findByCodigo("E005")

        def errores = ""

        if(params.copias.trim() == "") {
            render "NO*"+"Tiene que seleccionar al menos una persona para enviar copia."
            return
        }

        if(tramite.para) {
            if(tramite.para?.estado == estadoAnulado || tramite.para?.estado == estadoArchivado){
                println("entrof")
                render "NO*"+"El trámite se encuentra <strong>${tramite.para?.estado.descripcion}</strong>, no puede crear copias"
                return
            }
        } else {
            if(tramite.copias.size() == 0) {
                render "NO*"+"No puede crear copias"
                return
            }
        }


        copias.each { copia ->
            copia = copia.trim()
            if (copia != "") {
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
            } else {

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
        def redDpto = null, redPrsn = null
        if (params.quien.toString().startsWith("-")) {
            redDpto = Departamento.get(params.quien.toInteger() * -1)
        } else {
            redPrsn = Persona.get(params.quien)
        }
        def pr = PersonaDocumentoTramite.get(params.pr)

        def errores = ""

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
                    if (pr.rolPersonaTramiteId == rolCopia.id) {
                        //si es copia, la anulo
                        pr.estado = estadoAnulado
                        pr.fechaAnulacion = new Date()
                        obs = "Trámite anulado automáticamente al redireccionar debido a un duplicado en la bandeja receptora"
                    } else {
                        prtrExisten.each { prtrExiste ->
                            if (prtrExiste.rolPersonaTramiteId == rolCopia.id) {
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
                                    println "ERROR AQUI: " + prtrExiste.errors
                                }
                                if (!prtrExiste.tramite.save(flush: true)) {
                                    println "ERROR AQUI3 : " + prtrExiste.tramite.errors
                                }
                            }
                        }
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
                    if (pr.rolPersonaTramiteId == rolCopia.id) {
                        //si es copia, la anulo
                        pr.estado = estadoAnulado
                        pr.fechaAnulacion = new Date()
                        obs = "Trámite anulado automáticamente al redireccionar debido a un duplicado en la bandeja receptora"
                    } else {
                        prtrExisten.each { prtrExiste ->
                            if (prtrExiste.rolPersonaTramiteId == rolCopia.id) {
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
            def tramite = pr.tramite
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
                }
                errores += "<ul><li>Ha ocurrido un error al redireccionar.</li></ul>"
            }
            if (pr.save(flush: true)) {
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
                html += makeNewTreeExtended(principal)
                html += "</ul>" + "\n"
            }
            url = createLink(controller: "buscarTramite", action: "busquedaTramite")
        }
        return [html2: html, url: url, tramite: tramite]
    }

    def dialogAdmin() {
        def tramite = Tramite.get(params.id)
        def icon = params.icon
        def msg = params.msg
        def personas = []
        Persona.findAllByDepartamento(tramite.de?.departamento).each { p ->
            if (p.estaActivo) {
                def m = [:]
                m.key = p.nombre + " " + p.apellido + " (funcionario de ${p.departamento.codigo})"
                m.value = p.nombre + " " + p.apellido + " (" + p.login + ")"
                personas.add(m)
            }
        }
        println msg
        println icon
        return [tramite: tramite, icon: icon, msg: msg, personas: personas]
    }

    private String makeNewTreeExtended(Tramite principal) {
        def html = ""
        def tramitePrincipal = principal.tramitePrincipal
        //debe hacer un arbol para cada tramite que tenga tramite.tramitePrincipal = principal.tramitePrincipal
        def tramites
        if (tramitePrincipal > 0) {
            tramites = Tramite.findAllByTramitePrincipal(tramitePrincipal, [sort: "fechaCreacion"])
        } else {
            tramites = [principal]
        }

        tramites.each { p ->
            def type = "tramite"
            if (p.tramitePrincipal == p.id) {
                type += "Principal"
            }
            html += "<li id='t_${p.id}' class='jstree-open' data-jstree='{\"type\":\"${type}\"}' >"
            html += "<b>" + p.codigo + "</b>"
            html += "<ul>"
            html += makeTreeExtended(p)
            html += "</ul>"
        }

        return html
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
        if(pdt.tramite.tipoDocumento?.codigo=="CIR"){
            clase+=" CIR"

        }
        rel += estado

        def rol = pdt.rolPersonaTramite
        def duenioPrsn = pdt.tramite.de.id
        def duenioDpto = pdt.tramite.deDepartamento?.id
        def paraStr = "Para: "
        if (rol.codigo == "R002") {
            paraStr = "CC: "
        }
        if (pdt.departamento) {
            paraStr += pdt.departamento.descripcion
        } else if (pdt.persona) {
            paraStr += pdt.persona.departamento.codigo + ":" + pdt.persona.login
        }

        def deStr = "De: " + (pdt.tramite.deDepartamento ? pdt.tramite.deDepartamento.codigo : pdt.tramite.de.departamento.codigo + ":" + pdt.tramite.de.login)

        data += ',"tramite":"' + pdt.tramiteId + '"'
        data += ',"codigo":"' + pdt.tramite.codigo + '"'
        data += ',"de":"' + deStr + '"'
        data += ',"para":"' + paraStr + '"'
        if (pdt.tramite.padre) {
            data += ',"padre":"' + pdt.tramite.padreId + '"'
        }
        if (tramitesService.verificaHijos(pdt, EstadoTramite.findByCodigo("E006"))) {
            //false: no tiene hijos vivos
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
        def estados = [estadoAnulado]

        if (estados.contains(persDocTram.estado)) {
            render "NO*el trámite está ${persDocTram.estado.descripcion}, no puede quitar el archivado"
        } else {
            def o = "Archivado originalmente el ${persDocTram.fechaArchivo.format('dd-MM-yyyy HH:mm')}"

            def observacionOriginal = persDocTram.observaciones
            def accion = "Reactivado"
            def solicitadoPor = params.aut
            def usuario = session.usuario.login
            def texto = o
            def nuevaObservacion = params.obs
            persDocTram.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
            if (persDocTram.rolPersonaTramite.codigo == "R001") { //PARA
                def obs = "PARA "
                if (persDocTram.departamento) {
                    obs += "el dpto. ${persDocTram.departamento.codigo}"
                } else if (persDocTram.persona) {
                    obs += "el usuario ${persDocTram.persona.login}"
                }
                obs += ", " + o
                observacionOriginal = persDocTram.tramite.observaciones
                texto = obs
                persDocTram.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
            } else if (persDocTram.rolPersonaTramite.codigo == "R002") { //CC
                def obs = "COPIA para "
                if (persDocTram.departamento) {
                    obs += "el dpto. ${persDocTram.departamento.codigo}"
                } else if (persDocTram.persona) {
                    obs += "el usuario ${persDocTram.persona.login}"
                }
                obs += ", " + o
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

        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCopia = RolPersonaTramite.findByCodigo("R002")

        def hijosVivos = 0

        Tramite.findAllByPadre(persDocTram.tramite).each { tr->
            def prtr = PersonaDocumentoTramite.withCriteria {
                eq("tramite", tr)
                ne("estado", estadoAnulado)
                inList("rolPersonaTramite", [rolPara, rolCopia])
            }
//            prtr.each {hj->
//                println " "+hj.rolPersonaTramite.descripcion+"  "+hj.estado.descripcion
//            }
            hijosVivos+=prtr.size()
        }

        println("tiene hijos " + hijosVivos)


        if (hijosVivos>0) {
            render "NO*"
            return
        }


        if (estados.contains(persDocTram.estado)) {
            render "NO*el trámite está ${persDocTram.estado.descripcion}, no puede quitar el recibido"
        } else {
            def o = " Recibido originalmente el ${persDocTram.fechaRecepcion.format('dd-MM-yyyy HH:mm')}"

            def observacionOriginal = persDocTram.observaciones
            def accion = "Quitado el Recibido"
            def solicitadoPor = params.aut
            def usuario = session.usuario.login
            def texto = o
            def nuevaObservacion = params.texto
            persDocTram.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
            if (persDocTram.rolPersonaTramite.codigo == "R001") { //PARA
                def obs = "PARA "
                if (persDocTram.departamento) {
                    obs += "el dpto. ${persDocTram.departamento.codigo}"
                } else if (persDocTram.persona) {
                    obs += "el usuario ${persDocTram.persona.login}"
                }
                obs += ", " + o
                observacionOriginal = persDocTram.tramite.observaciones
                texto = obs
                persDocTram.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

            } else if (persDocTram.rolPersonaTramite.codigo == "R002") { //CC
                def obs = "COPIA para "
                if (persDocTram.departamento) {
                    obs += "el dpto. ${persDocTram.departamento.codigo}"
                } else if (persDocTram.persona) {
                    obs += "el usuario ${persDocTram.persona.login}"
                }
                obs += ", " + o
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

    def anularCircular(){
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
                def nuevaObs = "Anulado"
                if (params.texto.trim() != "") {
                    nuevaObs += ": " + params.texto
                }
                def observacionOriginal = objeto.observaciones
                def accion = "Anulación"
                def solicitadoPor = params.aut
                def usuario = session.usuario.login
                def texto = ""
                def nuevaObservacion = params.texto
                objeto.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

                if (objeto.rolPersonaTramite.codigo == "R002") {
                    nuevaObs = "COPIA para "
                    if (objeto.departamento) {
                        nuevaObs += "el dpto. ${objeto.departamento.codigo}"
                    } else if (objeto.persona) {
                        nuevaObs += "el usuario ${objeto.persona.login}"
                    }

                    observacionOriginal = objeto.tramite.observaciones
                    texto = nuevaObs
                    objeto.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
                }
                if (objeto.rolPersonaTramite.codigo == "R001") {
                    nuevaObs = "PARA "
                    if (objeto.departamento) {
                        nuevaObs += "el dpto. ${objeto.departamento.codigo}"
                    } else if (objeto.persona) {
                        nuevaObs += "el usuario ${objeto.persona.login}"
                    }
                    observacionOriginal = objeto.tramite.observaciones
                    texto = nuevaObs
                    objeto.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
                }
                objeto.tramite.save(flush: true)
                if (!objeto.save(flush: true)) {
                    println "error en el save anular " + objeto.errors
                } else {
                    /*alertas*/
                    def alerta
                    if (objeto.departamento) {
                        alerta = Alerta.findAllByTramiteAndDepartamento(objeto.tramite, objeto.departamento)
                        alerta.each { a ->
                            if (a.fechaRecibido == null) {
                                a.fechaRecibido = new Date();
                                a.save(flush: true)
                            }
                        }
                    }
                    if (objeto.persona) {

                        alerta = Alerta.findAllByTramiteAndPersona(objeto.tramite, objeto.persona)
                        alerta.each { a ->
                            if (a.fechaRecibido == null) {
                                a.fechaRecibido = new Date();
                                a.save(flush: true)
                            }
                        }
                    }
                }
            }
            /*aqui especial para circular*/
            def rolCopia = RolPersonaTramite.findByCodigo("R002")
            def pdt = PersonaDocumentoTramite.get(params.id)
            if(pdt.tramite.tipoDocumento?.codigo!="CIR"){
                response.sendError(403)
            }
            def pdts = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(pdt.tramite,rolCopia)
            pdts.each {p->
                getCadenaDown(p, funcion)
            }
            if (pdt.tramite.aQuienContesta) {
                if (pdt.tramite.aQuienContesta.fechaRecepcion) {
                    pdt.tramite.aQuienContesta.estado = EstadoTramite.findByCodigo("E004")
                } else {
                    pdt.tramite.aQuienContesta.estado = EstadoTramite.findByCodigo("E003")
                }
                pdt.tramite.aQuienContesta.fechaAnulacion = null
                pdt.tramite.aQuienContesta.fechaArchivo = null
                def nuevaObs = "Reactivado por anulación de: ${persDocTram.tramite.codigo}"
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
                observacionOriginal = pdt.tramite.aQuienContesta.tramite.observaciones
                texto = nuevaObs
                nuevaObservacion = params.texto
                pdt.tramite.aQuienContesta.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
            }

            render "OK"
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
                def nuevaObs = "Anulado"
                if (params.texto.trim() != "") {
                    nuevaObs += ": " + params.texto
                }
                def observacionOriginal = objeto.observaciones
                def accion = "Anulado"
                def solicitadoPor = params.aut
                def usuario = session.usuario.login
                def texto = ""
                def nuevaObservacion = params.texto
                objeto.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

                if (objeto.rolPersonaTramite.codigo == "R002") {
                    nuevaObs = "COPIA para "
                    if (objeto.departamento) {
                        nuevaObs += "el dpto. ${objeto.departamento.codigo}"
                    } else if (objeto.persona) {
                        nuevaObs += "el usuario ${objeto.persona.login}"
                    }
                    observacionOriginal = objeto.tramite.observaciones
                    texto = nuevaObs
                    objeto.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

                }
                if (objeto.rolPersonaTramite.codigo == "R001") {
                    nuevaObs = "PARA "
                    if (objeto.departamento) {
                        nuevaObs += "el dpto. ${objeto.departamento.codigo}"
                    } else if (objeto.persona) {
                        nuevaObs += "el usuario ${objeto.persona.login}"
                    }
                    observacionOriginal = objeto.tramite.observaciones
                    texto = nuevaObs
                    objeto.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
                }
                objeto.tramite.save(flush: true)
                if (!objeto.save(flush: true)) {
                    println "error en el save anular " + objeto.errors
                } else {
                    /*alertas*/
                    def alerta
                    if (objeto.departamento) {
                        alerta = Alerta.findAllByTramiteAndDepartamento(objeto.tramite, objeto.departamento)
                        alerta.each { a ->
                            if (a.fechaRecibido == null) {
                                a.fechaRecibido = new Date();
                                a.save(flush: true)
                            }
                        }
                    }
                    if (objeto.persona) {
                        alerta = Alerta.findAllByTramiteAndPersona(objeto.tramite, objeto.persona)
                        alerta.each { a ->
                            if (a.fechaRecibido == null) {
                                a.fechaRecibido = new Date();
                                a.save(flush: true)
                            }
                        }
                    }
                }


//                if (objeto.rolPersonaTramite.codigo == "R002") {
//                    if(objeto.delete(flush: true)){
//                        println("anulado y borrado")
//                    }else{
//                        println("error anulado-borrado " + objeto.errors)
//                    }
//                }

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
                if (pdt.tramite.aQuienContesta.fechaRecepcion) {
                    pdt.tramite.aQuienContesta.estado = EstadoTramite.findByCodigo("E004")
                } else {
                    pdt.tramite.aQuienContesta.estado = EstadoTramite.findByCodigo("E003")
                }
                pdt.tramite.aQuienContesta.fechaAnulacion = null
                pdt.tramite.aQuienContesta.fechaArchivo = null
                def nuevaObs = "Reactivado por anulación de: ${persDocTram.tramite.codigo}"
                def observacionOriginal = pdt.tramite.aQuienContesta.observaciones
                def accion = "Reactivado por anulación de trámite derivado"
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
                observacionOriginal = pdt.tramite.aQuienContesta.tramite.observaciones
                texto = nuevaObs
                nuevaObservacion = params.texto
                pdt.tramite.aQuienContesta.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
            }

            render "OK"
        }


    }

    def desanularPdt(PersonaDocumentoTramite pdt) {
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

        def observacionOriginal = pdt.observaciones
        def accion = "Reactivado"
        def solicitadoPor = params.aut
        def usuario = session.usuario.login
        def texto = ""
        def nuevaObservacion = params.texto
        pdt.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

        pdt.fechaAnulacion = null
        if (pdt.rolPersonaTramite.codigo == "R002") {
            def nuevaObs = "COPIA para"
            if (pdt.departamento) {
                nuevaObs += " el dpto. ${pdt.departamento.codigo}"
            } else if (pdt.persona) {
                nuevaObs += " el usuario ${pdt.persona.login}"
            }
            nuevaObs += " reactivada"
            observacionOriginal = pdt.tramite.observaciones
            texto = nuevaObs
            pdt.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

        }
        if (pdt.rolPersonaTramite.codigo == "R001") {
            def nuevaObs = "PARA"
            if (pdt.departamento) {
                nuevaObs += " el dpto. ${pdt.departamento.codigo}"
            } else if (pdt.persona) {
                nuevaObs += " el usuario ${pdt.persona.login}"
            }
            nuevaObs += " reactivado"
            observacionOriginal = pdt.tramite.observaciones
            texto = nuevaObs
            pdt.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
        }
        pdt.tramite.save(flush: true)
        if (pdt.save(flush: true)) {
            return true
        } else {
            println "erros " + pdt.errors
            return false
        }
    }

    def desanular() {
        def pdt = PersonaDocumentoTramite.get(params.id)
        if (pdt.rolPersonaTramite.codigo == "R001") { //es PARA
            def tramite = pdt.tramite
            def copias = tramite.allCopias
            def ok = true
            (copias + pdt).each { p ->
                println "desanular: " + p.rolPersonaTramite.descripcion
                if (!desanularPdt(p)) {
                    ok = false
                }
            }
            render ok ? "OK" : "NO"
        } else {
            render "NO"
        }
    }

    def getCadenaDown(pdt, funcion) {
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
    }

    def getHermanos(tramite, res, roles, funcion) {
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
        return res
    }

    def getHijos(pdt, roles, funcion) {
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
        return res
    }


}
