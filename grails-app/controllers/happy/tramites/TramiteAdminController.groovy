package happy.tramites

import happy.alertas.Alerta
import happy.seguridad.Persona

class TramiteAdminController {

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

            msg = "<p>Seleccione el trámite al que se asociará ${original.tramite.codigo}</p>"
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
                def cod = tr.codigo
                def de = tr.deDepartamento ? tr.deDepartamento.codigo : tr.de.login

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
                        lt("fechaCreacion", original.tramite.fechaCreacion)
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
                    msg += "<td>${tramiteFechas(cc)}</td>"
                    msg += "<td><a href='#' class='btn btn-success select' id='${cc.id}'><i class='fa fa-check'></i></a></td>"
                    msg += "</tr>"
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
                msg += "No se encontró un trámite disponible con código " + codigo.toUpperCase()
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

        original.observaciones = (original.observaciones ?: "") + " Trámite asociado al trámite ${nuevoPadre.tramite.codigo} por " +
                "${session.usuario.login} el ${new Date().format('dd-MM-yyyy HH:mm')}"

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

//        println "get des "+getCadenaDown(PersonaDocumentoTramite.get(297))
            }
            url = createLink(controller: "buscarTramite", action: "busquedaTramite")
//        switch (params.b) {
//            case "bep":
//                url = createLink(controller: "tramite", action: "bandejaEntrada")
//                break;
//            case "bed":
//                url = createLink(controller: "tramite3", action: "bandejaEntradaDpto")
//                break;
//            case "bsp":
//                url = createLink(controller: "tramite2", action: "bandejaSalida")
//                break;
//            case "bsd":
//                url = createLink(controller: "tramite2", action: "bandejaSalidaDep")
//                break;
//            case "bqt":
//                url = createLink(controller: "buscarTramite", action: "busquedaTramite")
//                break;
//            case "bqe":
//                url = createLink(controller: "buscarTramite", action: "busquedaEnviados")
//                break;
//
//        }
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
        def hijos = Tramite.findAllByAQuienContesta(pdt)
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
        paraStr += pdt.departamento ? pdt.departamento.descripcion : pdt.persona.departamento.codigo + ":" + pdt.persona.login
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
            paraStr = tramiteParaInfo.departamento ?
                    tramiteParaInfo.departamento.descripcion :
                    tramiteParaInfo.persona.departamento.codigo + ":" + tramiteParaInfo.persona.login
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
        def obs = " Quitado el archivado por ${session.usuario.login} el ${new Date().format('dd-MM-yyyy HH:mm')} " +
                "(originalmente archivado el ${persDocTram.fechaArchivo.format('dd-MM-yyyy HH:mm')}): " + params.texto
        persDocTram.observaciones = (persDocTram.observaciones ?: "") + obs
        if (persDocTram.rolPersonaTramite.codigo == "R001") { //PARA
            persDocTram.tramite.observaciones = (persDocTram.tramite.observaciones ?: "") + obs
        } else if (persDocTram.rolPersonaTramite.codigo == "R002") { //CC
            persDocTram.tramite.observaciones = (persDocTram.tramite.observaciones ?: "") + " COPIA" + obs
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

    def desrecibir() {
        def persDocTram = PersonaDocumentoTramite.get(params.id)

        def obs = " Quitado el recibido por ${session.usuario.login} el ${new Date().format('dd-MM-yyyy HH:mm')} " +
                "(originalmente recibido el ${persDocTram.fechaRecepcion.format('dd-MM-yyyy HH:mm')}): " + params.texto

        persDocTram.observaciones = (persDocTram.observaciones ?: "") + obs

        if (persDocTram.rolPersonaTramite.codigo == "R001") { //PARA
            persDocTram.tramite.observaciones = (persDocTram.tramite.observaciones ?: "") + obs
        } else if (persDocTram.rolPersonaTramite.codigo == "R002") { //CC
            persDocTram.tramite.observaciones = (persDocTram.tramite.observaciones ?: "") + " COPIA" + obs
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

    def anular() {

        def funcion = { objeto ->
            def anulado = EstadoTramite.findByCodigo("E006")
            objeto.estado = anulado
            objeto.fechaAnulacion = new Date()
            objeto.observaciones = (objeto.observaciones ?: "") + "Documento anulado por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}: ${params.texto};"
            if (objeto.rolPersonaTramite.codigo == "R002")
                objeto.tramite.observaciones = (objeto.tramite.observaciones ?: "") + " COPIA anulada por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto}; "
            if (objeto.rolPersonaTramite.codigo == "R001")
                objeto.tramite.observaciones = (objeto.tramite.observaciones ?: "") + " Documento anulado por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto}; "
            objeto.tramite.save(flush: true)
            objeto.save(flush: true)
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
            pdt.tramite.aQuienContesta.observaciones = (pdt.tramite.aQuienContesta.observaciones ?: "") + " Tramite reactivado por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto}; "
            pdt.tramite.aQuienContesta.save(flush: true)
        }

        render "OK"
    }

    def desanular() {
        def pdt = PersonaDocumentoTramite.get(params.id)
        pdt.estado = EstadoTramite.findByCodigo("E004")
        pdt.observaciones = (pdt.observaciones ?: "") + "Documento reactivado por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto};"
        pdt.fechaAnulacion = null
        if (pdt.rolPersonaTramite.codigo == "R002")
            pdt.tramite.observaciones = (pdt.tramite.observaciones ?: "") + "COPIA reactivada por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto}; "
        if (pdt.rolPersonaTramite.codigo == "R001")
            pdt.tramite.observaciones = (pdt.tramite.observaciones ?: "") + "Documento reactivado por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto}; "
        pdt.tramite.save(flush: true)
        if (pdt.save(flush: true)) {
            render "OK"
        } else {
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
