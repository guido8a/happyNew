package happy.tramites

import happy.alertas.Alerta
import happy.seguridad.Persona
import happy.utilitarios.DiaLaborable

class TramiteController extends happy.seguridad.Shield {

    def diasLaborablesService
    def enviarService
    def tramitesService
    def dbConnectionService

    def index() {
        redirect(action: "list", params: params)
    } //index

    def cambiarMembrete() {
        def tramite = Tramite.get(params.id.toLong())
        tramite.conMembrete = params.membrete
        if (tramite.save(flush: true)) {
            render "OK*Se generará el PDF ${params.membrete == '1' ? 'con' : 'sin'} membrete."
        } else {
            println "cambiarMembrete" + tramite.errors
            render "NO*Ha ocurrido un error al guardar"
        }
    }

    def redactar() {
        //def usuario = session.usuario
        //def persona = Persona.get(usuario.id)
        def esEditor = session.usuario.puedeEditor
        def tramite = Tramite.get(params.id)
        if (tramite?.estadoTramite?.codigo == "E001") { //borrador, por enviar
            return [tramite: tramite, esEditor: esEditor]
        } else {
            flash.message = "El trámite seleccionado no puede ser editado"
            redirect(action: "errores")
        }
    }

    def saveDEX() {
//        println "saveDEX"
        def tramite = Tramite.get(params.id)
        tramite.texto = (params.editorTramite).replaceAll("\\n", "")
        tramite.fechaModificacion = new Date()

        def ok = true
        def msg = ""

        if (tramite.save(flush: true)) {
            def para = tramite.para

            if (params.para) {
                if (params.para.toLong() > 0) {
                    para.persona = Persona.get(params.para.toLong())
                } else {
                    para.departamento = Departamento.get(params.para.toLong() * -1)
                }
                enviarService.crearPdf(tramite, session.usuario, "1", 'download', servletContext.getRealPath("/"), message(code: 'pathImages').toString());
                if (para.save(flush: true)) {
                    ok = true
                } else {
                    ok = false
                    msg = "<li>Ha ocurrido un error al guardar el destinatario: " + renderErrors(bean: para) + "</li>"
                }
            } else {
                ok = true
            }
        } else {
            ok = false
            msg = "<li>Ha ocurrido un error al guardar el trámite: " + renderErrors(bean: tramite) + "</li>"
        }
        if (ok) {
            //aqui envia y recibe automaticamente el tramite
            def ahora = new Date();
            def rolEnvia = RolPersonaTramite.findByCodigo("E004")
            def rolRecibe = RolPersonaTramite.findByCodigo("E003")
            def rolPara = RolPersonaTramite.findByCodigo("R001")

            def estadoEnviado = EstadoTramite.findByCodigo('E003')
            def estadoRecibido = EstadoTramite.findByCodigo('E004')

            def pdt = new PersonaDocumentoTramite()
            pdt.tramite = tramite
            pdt.persona = session.usuario
            pdt.departamento = session.departamento

            pdt.personaSigla = pdt.persona.login
            pdt.personaNombre = pdt.persona.nombre + " " + pdt.persona.apellido
            pdt.departamentoNombre = pdt.departamento.descripcion
            pdt.departamentoSigla = pdt.departamento.codigo

            pdt.fechaEnvio = ahora
            pdt.rolPersonaTramite = rolEnvia
            if (!pdt.save(flush: true)) {
                println pdt.errors
            }

            def pdt2 = new PersonaDocumentoTramite()
            pdt2.tramite = tramite
            pdt2.persona = session.usuario
            pdt2.departamento = session.departamento

            pdt2.personaSigla = pdt2.persona.login
            pdt2.personaNombre = pdt2.persona.nombre + " " + pdt2.persona.apellido
            pdt2.departamentoNombre = pdt2.departamento.descripcion
            pdt2.departamentoSigla = pdt2.departamento.codigo

            pdt2.fechaEnvio = ahora
            pdt2.fechaRecepcion = ahora
            pdt2.rolPersonaTramite = rolRecibe
            if (!pdt2.save(flush: true)) {
                println pdt2.errors
            }

            def pdtPara = PersonaDocumentoTramite.withCriteria {
                eq("tramite", tramite)
                eq("rolPersonaTramite", rolPara)
            }
            if (pdtPara.size() > 0) {
                def limite = ahora
                limite = diasLaborablesService.fechaMasTiempo(limite, tramite.prioridad.tiempo)
                if (limite[0]) {
                    limite = limite[1]
                } else {
                    flash.message = "Ha ocurrido un error al calcular la fecha límite: " + limite[1]
                    redirect(controller: 'tramite', action: 'errores')
                    return
                }
                if (pdtPara.size() > 1) {
                    println "Se encontraron varios pdtPara!! se utiliza el primero......."
                }
                pdtPara = pdtPara.first()
                pdtPara.fechaEnvio = ahora
                pdtPara.fechaRecepcion = ahora
                pdtPara.fechaLimiteRespuesta = limite
                pdtPara.estado = estadoRecibido

                if (!pdtPara.save(flush: true)) {
                    println "error ala guardar pdtPara: " + pdtPara.errors
                }
            }

            tramite.fechaEnvio = ahora
            tramite.estadoTramite = estadoRecibido
            if (tramite.save(flush: true)) {
                def realPath = servletContext.getRealPath("/")
                def mensaje = message(code: 'pathImages').toString();
                enviarService.crearPdf(tramite, session.usuario, "1", 'download', realPath, mensaje);
            } else {
                println tramite.errors
                msg += "<li>" + renderErrors(bean: tramite) + "<li>"
            }
        }
        if (msg == "") {
            render "OK_" + createLink(controller: 'tramite3', action: "bandejaEntradaDpto")
        } else {
            render "NO_<ul>" + msg + "</ul>"
        }
    }

    def saveTramite() {
//        println "saveTramite, params: $params"

        def tramite = Tramite.get(params.id)
        def paratr = tramite.para
        def copiastr = tramite.copias
        def enviado = false
        (copiastr + paratr).each { c ->
            if (c?.estado?.codigo == "E003") {
                enviado = true
            }
        }


        def tramitetr = Tramite.get(params.id)
        if (tramitetr) {
            def paratr1 = tramitetr.para
            def copiastr1 = tramitetr.copias
            (copiastr1 + paratr1).each { c ->
                if (c?.estado?.codigo == "E006") {
                    render "NO_Este trámite ya ha sido anulado, no puede guardar modificaciones"
                    return
                }
                if (c?.estado?.codigo == 'E005') {
                    render "NO_Este trámite ya ha sido archivado, no puede guardar modificaciones"
                    return
                } else {

                    if (!enviado) {
                        tramite.texto = (params.editorTramite).replaceAll("\\n", "")
                        tramite.fechaModificacion = new Date()
                        //log Jefe
                        if(session.usuario) {
                            if (session.usuario.getPuedeJefe()) {
                                tramite.fechaModificacion = new Date()
                                tramite.observaciones = tramitesService.observaciones(tramite.observaciones, 'Editado por: ' + session.usuario.login, '', ' ', '', '')
                            }
                        }  else {
                            tramite.save(flush: true)
                            println "guardar redaccion tramite, sesion perdida: ${tramite.codigo}"
                            redirect(controller: 'login', action: 'login')
                            return
                        }

                        if (tramite.save(flush: true)) {
                            def para = tramite.para
                            enviarService.crearPdf(tramite, session.usuario, "1", 'download', servletContext.getRealPath("/"), message(code: 'pathImages').toString());
                            if (params.para) {
                                if (params.para.toLong() > 0) {
                                    para.persona = Persona.get(params.para.toLong())
                                } else {
                                    para.departamento = Departamento.get(params.para.toLong() * -1)
                                }
                                if (para.save(flush: true)) {
                                    render "OK_Trámite guardado exitosamente"
                                } else {
                                    render "NO_Ha ocurrido un error al guardar el destinatario: " + renderErrors(bean: para)
                                }
                            } else {
                                render "OK_Trámite guardado exitosamente"
                            }
                        } else {
                            render "NO_Ha ocurrido un error al guardar el trámite: " + renderErrors(bean: tramite)
                        }
                    } else {
                        render "NO_Este trámite ya ha sido enviado, no puede guardar modificaciones"
                    }
                }
            }
        }

    }

    def tiempoRespuestaEsperada_ajax() {

        def fecha = new Date().parse("dd-MM-yyyy HH:mm", params.fecha)
        def prioridad = TipoPrioridad.get(params.prioridad)

        def horas = prioridad.tiempo

        def fechaEsperada = diasLaborablesService.fechaMasTiempo(fecha, horas)
        if (fechaEsperada[0]) {
            render "OK_" + fechaEsperada[1].format("dd-MM-yyyy HH:mm")
        } else {
            render "NO_" + fechaEsperada[1]
        }
    }

    def getParaNuevo_ajax() {
        def sql = "SELECT * FROM trmt_para($session.usuario.id)"
        def cn = dbConnectionService.getConnection()
        def rows = cn.rows(sql.toString())

        Tramite tramite = null
        if (params.tramite) {
            tramite = Tramite.get(params.tramite)
        }

        def html = "<div class=\"col-xs-3 negrilla\" id=\"divPara\" style=\"margin-top: -25px;margin-left: -25px\">"
        html += "<b>Para:</b>"
        html += g.select(name: "tramite.para", id: "para", optionKey: "id", optionValue: "dscr", from: rows,
                value: tramite?.para?.departamento ? tramite.para.departamentoId * -1 : tramite?.para?.personaId,
                style: "width:300px;", class: "form-control label-shared required")
        html += "</div>"
        html += "    <div class=\"col-xs-1 negrilla\" id=\"divBotonInfo\" style=\"margin-left: 30px\">\n" +
                "                    <a href=\"#\" id=\"btnInfoPara\" class=\"btn btn-sm btn-info\">\n" +
                "                    <i class=\"fa fa-info-circle\"></i>\n" +
                "                    </a>\n" +
                "                    </div>"
        html += "<script type='text/javascript'>"
        html += " \$(\"#para\").change(function () {\n" +
                "            var paraId = \$(this).val();\n" +
                "            \$(\"#ulSeleccionados\").children().each(function () {\n" +
                "                if(\$(this).data(\"id\") == paraId ){\n" +
                "                    \$(this).addClass('selected')\n" +
                "                    moveSelected(\$(\"#ulSeleccionados\"), \$(\"#ulDisponibles\"), true);\n" +
                "                }\n" +
                "            });\n" +
                "        });"
        html += " \$(\"#btnInfoPara\").click(function () {\n" +
                "                    var para = \$(\"#para\").val();\n" +
                "                    var paraExt = \$(\"#paraExt\").val();\n" +
                "                    var id;\n" +
                "                    var url = \"\";\n" +
                "                    if (para) {\n" +
                "                        if (parseInt(para) > 0) {\n" +
                "                            url = \"${createLink(controller: 'persona', action: 'show_ajax')}\";\n" +
                "                            id = para;\n" +
                "                        } else {\n" +
                "                            url = \"${createLink(controller: 'departamento', action: 'show_ajax')}\";\n" +
                "                            id = parseInt(para) * -1;\n" +
                "                        }\n" +
                "                    }\n" +
                "                    if (paraExt) {\n" +
                "                        url = \"${createLink(controller: 'origenTramite', action: 'show_ajax')}\";\n" +
                "                        id = paraExt;\n" +
                "                    }\n" +
                "                    \$.ajax({\n" +
                "                        type    : \"POST\",\n" +
                "                        url     : url,\n" +
                "                        data    : {\n" +
                "                            id : id\n" +
                "                        },\n" +
                "                        success : function (msg) {\n" +
                "                            bootbox.dialog({\n" +
                "                                title   : \"Información\",\n" +
                "                                message : msg,\n" +
                "                                buttons : {\n" +
                "                                    aceptar : {\n" +
                "                                        label     : \"Aceptar\",\n" +
                "                                        className : \"btn-primary\",\n" +
                "                                        callback  : function () {\n" +
                "                                        }\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            });\n" +
                "                        }\n" +
                "                    });\n" +
                "                    return false;\n" +
                "                });"
        html += "</script>"
        cn.close()
        render html
    }

    def getPara_ajax() {
//        println "Get para: " + params
        Tramite tramite = null
        if (params.tramite) {
            tramite = Tramite.get(params.tramite)
        }
        def html
        def tipoDoc = TipoDocumento.get(params.doc)
        if (!tipoDoc) {
            html = "<div class=\"col-xs-4 negrilla\" id=\"divPara\" style=\"margin-top: -10px\">"
            html += "</div>"
        } else {
//            println "tipoDoc: " + tipoDoc.codigo
            switch (tipoDoc.codigo) {
            //*** deba aparecer el para ****   gdo */
/*
                case "CIR":
                    html = "<div class=\"col-xs-4 negrilla\" id=\"divPara\" style=\"margin-top: -10px\">"
                    html += "</div>"
                    break;
*/
                case "OFI":
                    html = "<div class=\"col-xs-3 negrilla\" id=\"divPara\" style=\"margin-top: -25px; margin-left: -25px\"> "
                    html += "<b>Para:</b>"
                    html += g.textField(name: "paraExt",
                            class: "form-control label-shared required",
                            value: tramite?.paraExterno,
                            style: "width:300px;")
                    html += "</div>"
                    break;
                default: //DEX SUM MEM PLA
//                    println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
//                    println tramite?.para?.departamento ? tramite.para.departamentoId * -1 : tramite?.para?.personaId
//                    println "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
                    html = "<div class=\"col-xs-3 negrilla\" id=\"divPara\" style=\"margin-top: -25px;margin-left: -25px\">"
                    html += "<b>Para:</b>"
                    html += elm.comboPara(name: "tramite.para",
                            id: "para",
                            value: tramite?.para?.departamento ? tramite.para.departamentoId * -1 : tramite?.para?.personaId,
                            style: "width:300px;",
                            class: "form-control label-shared required",
                            tipoDoc: tipoDoc,
                            tipo: params.tipo)
                    html += "</div>"
                    html += "    <div class=\"col-xs-1 negrilla\" id=\"divBotonInfo\" style=\"margin-left: 30px\">\n" +
                            "                    <a href=\"#\" id=\"btnInfoPara\" class=\"btn btn-sm btn-info\">\n" +
                            "                    <i class=\"fa fa-info-circle\"></i>\n" +
                            "                    </a>\n" +
                            "                    </div>"
                    html += "<script type='text/javascript'>"
                    html += " \$(\"#btnInfoPara\").click(function () {\n" +
                            "                    var para = \$(\"#para\").val();\n" +
                            "                    var paraExt = \$(\"#paraExt\").val();\n" +
                            "                    var id;\n" +
                            "                    var url = \"\";\n" +
                            "                    if (para) {\n" +
                            "                        if (parseInt(para) > 0) {\n" +
                            "                            url = \"${createLink(controller: 'persona', action: 'show_ajax')}\";\n" +
                            "                            id = para;\n" +
                            "                        } else {\n" +
                            "                            url = \"${createLink(controller: 'departamento', action: 'show_ajax')}\";\n" +
                            "                            id = parseInt(para) * -1;\n" +
                            "                        }\n" +
                            "                    }\n" +
                            "                    if (paraExt) {\n" +
                            "                        url = \"${createLink(controller: 'origenTramite', action: 'show_ajax')}\";\n" +
                            "                        id = paraExt;\n" +
                            "                    }\n" +
                            "                    \$.ajax({\n" +
                            "                        type    : \"POST\",\n" +
                            "                        url     : url,\n" +
                            "                        data    : {\n" +
                            "                            id : id\n" +
                            "                        },\n" +
                            "                        success : function (msg) {\n" +
                            "                            bootbox.dialog({\n" +
                            "                                title   : \"Información\",\n" +
                            "                                message : msg,\n" +
                            "                                buttons : {\n" +
                            "                                    aceptar : {\n" +
                            "                                        label     : \"Aceptar\",\n" +
                            "                                        className : \"btn-primary\",\n" +
                            "                                        callback  : function () {\n" +
                            "                                        }\n" +
                            "                                    }\n" +
                            "                                }\n" +
                            "                            });\n" +
                            "                        }\n" +
                            "                    });\n" +
                            "                    return false;\n" +
                            "                });"
                    html += "</script>"
            }
        }

        def js = "<script type='text/javascript'>"
        js += " \$(\"#para\").change(function () {\n" +
                "            var paraId = \$(this).val();\n" +
                "            \$(\"#ulSeleccionados\").children().each(function () {\n" +
                "                if(\$(this).data(\"id\") == paraId ){\n" +
                "                    \$(this).addClass('selected')\n" +
                "                    moveSelected(\$(\"#ulSeleccionados\"), \$(\"#ulDisponibles\"), true);\n" +
                "                }\n" +
                "            });\n" +
                "        });"
        js += "</script>"

        render html + js
    }

    def crearTramite() {
//        println "CREAR TRAMITE: " + params
        if (params.padre) {
            def padre = Tramite.get(params.padre)

            if (params.pdt) {
                def aQuienEstaContestando = PersonaDocumentoTramite.get(params.pdt)

                if (aQuienEstaContestando == null) {
                    flash.message = "No se puede contestar este documento.<br/>" +
                            g.link(controller: 'tramite', action: 'bandejaEntrada', class: "btn btn-danger") {
                                "Volver a la bandeja de entrada"
                            }
                    redirect(controller: 'tramite', action: "errores")
                    return
                }

                if (params.esRespuestaNueva == 'S') {
                    def respv = aQuienEstaContestando.respuestasVivasEsrn
//                    println "RESPV " + respv
                    if (respv.size() != 0) {
                        flash.message = "*Ya ha realizado una respuesta a este trámite, no puede crear otra.<br/>" +
                                g.link(controller: 'tramite', action: 'bandejaEntrada', class: "btn btn-danger") {
                                    "Volver a la bandeja de entrada"
                                }
                        redirect(controller: 'tramite', action: "errores")
                        return

                    }
                }
            }

        }


        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def esEditor = session.usuario.puedeEditor
        if (esEditor) {
            redirect(controller: "tramite2", action: "bandejaSalida")
            return
        }
        params.esRespuesta = params.esRespuesta ?: 0
        if (session.usuario.tiposDocumento.size() == 0) {
            flash.message = "No puede crear ningún tipo de documento. Contáctese con el administrador."
            redirect(action: "errores")
            return
        }

        def anio = Anio.findAllByNumero(new Date().format("yyyy"), [sort: "id"])
        if (anio.size() == 0) {
            flash.message = "El año ${new Date().format('yyyy')} no está creado, no puede crear trámites nuevos. Contáctese con el administrador."
            redirect(action: "errores")
            return
        } else if (anio.size() > 1) {
            println "HAY MAS DE 1 AÑO ${new Date().format('yyyy')}!!!!!: ${anio}"
        }

        if (anio.findAll { it.estado == 1 }.size() == 0) {
            flash.message = "El año ${new Date().format('yyyy')} no está activado, no puede crear trámites nuevos. Contáctese con el administrador."
            redirect(action: "errores")
            return
        }


        def dias = DiaLaborable.countByAnio(anio.first())
        if (dias < 365) {
            flash.message = "No se encontraron los registros de días laborables del año ${new Date().format('yyyy')}, no puede crear trámites nuevos. Contáctese con el administrador."
            redirect(action: "errores")
            return
        }

//        println("params " + params)
        def rolesNo = [RolPersonaTramite.findByCodigo("E004"), RolPersonaTramite.findByCodigo("E003")]
        def padre = null
        def cc = ""
        def tramite = new Tramite(params)
        def principal = null
        def users = []
        if (params.padre) {
            padre = Tramite.get(params.padre)
            principal = padre
            while (true) {
                if (!principal.padre) {
                    break
                } else {
                    principal = principal.padre
                }
            }

            if (params.pdt) {
                if (params.esRespuesta == 1 || params.esRespuesta == '1') {
                    def pdt = PersonaDocumentoTramite.get(params.pdt)
                    def hijos = Tramite.findAllByAQuienContestaAndEstadoNotEqual(pdt, EstadoTramite.findByCodigo("E006"))
                    def tiene = false
                    hijos.each { h ->
//                        println "hijo -> "+h
                        PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteInList(h, [RolPersonaTramite.findByCodigo("E001"), RolPersonaTramite.findByCodigo("E002")]).each { pq ->
//                            println "pq "+pq.estado?.descripcion
                            if (pq.estado?.codigo != "E006") {
                                tiene = true
                            }
                        }
                    }
//                    println hijos
                    if (tiene) {
                        flash.message = "Ya ha realizado una respuesta a este trámite. "
                        redirect(controller: 'tramite', action: "errores")
                        return
                    }
                }
            }
        }


        if (params.id) {
            tramite = Tramite.get(params.id)
            padre = tramite.padre
            principal = padre
            if (principal) {
                while (true) {
                    if (!principal.padre) {
                        break
                    } else {
                        principal = principal.padre
                    }
                }
            }
            (tramite.copias).each { c ->
                if (cc != '') {
                    cc += "_"
                }
                if (c.departamento) {
                    cc += ("-" + c.departamentoId)
                } else {
                    cc += c.personaId
                }
            }
        } else {
            tramite.fechaCreacion = new Date()
        }

        def de = session.usuario
        def todos = []

/*        def disp, disponibles = []
        def disp2 = []

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
                        users += it
                    }
                }
                for (int i = users.size() - 1; i > -1; i--) {
                    if (!(users[i].estaActivo && users[i].puedeRecibir)) {
                        users.remove(i)
                    } else {
                        if (params.id) {
                            if (!(tramite.copias.persona.id*.toLong()).contains(users[i].id.toLong())) {
                                disponibles.add([id     : users[i].id,
                                                 label  : users[i].toString(),
                                                 obj    : users[i],
                                                 externo: false])
                            }
                        } else {
                            disponibles.add([id     : users[i].id,
                                             label  : users[i].toString(),
                                             obj    : users[i],
                                             externo: false])
                        }
                    }
                }
            }
        }

        disp.each { dep ->
            if (params.id) {
                if (!(tramite.copias.departamento.id*.toLong()).contains(dep.id.toLong())) {
                    if (dep.triangulos.size() > 0) {
                        disp2.add([id     : dep.id * -1,
                                   label  : dep.descripcion,
                                   obj    : dep,
                                   externo: dep.externo == 1])
                    }
                }
            } else {
                if (dep.triangulos.size() > 0) {
                    disp2.add([id     : dep.id * -1,
                               label  : dep.descripcion,
                               obj    : dep,
                               externo: dep.externo == 1])
                }
            }
        }

        todos = disponibles + disp2
 */


        def sql = "SELECT id, dscr as label, externo FROM trmt_para($session.usuario.id)"
        def cn = dbConnectionService.getConnection()
        todos = cn.rows(sql.toString())
//



        def bloqueo = false
        if (session.usuario.estado == "B") {
            bloqueo = true
        }

        def pdt = null
        if (params.pdt) {
            pdt = params.pdt
            def pdto = PersonaDocumentoTramite.get(pdt)
            if (pdto?.estado?.codigo != "E004") {
                flash.message = "No puede responder a este tramite puesto que ha sido anulado, archivado o no ha sido recibido"
                response.sendError(403)
            }
        } else if (params.hermano) {
            def herm = Tramite.get(params.hermano)
            def p = herm
//            while (p.padre) {
//                p = p.padre
//            }
            padre = p
            pdt = p.para
            padre = herm.padre

//            println "Hermano: " + herm
            tramite.agregadoA = herm

            if (!padre) {
                padre = herm
//                println "en realidad es padre..."
                def rolPara = RolPersonaTramite.findByCodigo("R001")
                def quienRecibePadre = PersonaDocumentoTramite.withCriteria {
                    eq("tramite", padre)
                    eq("rolPersonaTramite", rolPara)
                }
                if (quienRecibePadre.size() == 1) {
                    pdt = quienRecibePadre.first()
//                    println "PDT 1: " + pdt
                } else {
                    flash.message = "No puede agregar un documento a este tramite."
                    response.sendError(403)
                    return
                }
            } else {
//                println "PDT 2: " + pdt
                pdt = herm.aQuienContesta
            }
            if (!pdt) {
                pdt = p.copias
                if (pdt.size() == 0) {
                    flash.message = "No puede agregar un documento a este tramite."
                    response.sendError(403)
                    return
                } else {
                    pdt = pdt[0]
                }
            }
            if (pdt.estado?.codigo == "E006") {
                flash.message = "No puede agregar un tramite a un documento anulado"
                response.sendError(403)
            } else {
                pdt = pdt.id
            }

        }


        tramite.tramitePrincipal = 0
//        if (params.buscar == '1') {
//            def p = padre
//            while (p.padre) {
//                p = p.padre
//            }
//            tramite.tramitePrincipal = p.tramitePrincipal
//            padre = null
//            pdt = null
//        }

        if (tramite.id && tramite.esRespuestaNueva) {
            params.esRespuestaNueva = tramite.esRespuestaNueva
        }

        /** Elimina de Disponiles las copias **/
        if(tramite.id) {
//            println "si hay trámite"
            // elimina de todos las copias existentes
            tramite.copias.each { prtr ->
//                println "copias para prsn: ${prtr.persona}"
//                println "copias para dpto: ${prtr.departamento}"
                if(prtr.persona) {
                    if(todos.find { it.id == prtr.persona.id}) {
                        todos -= todos.find { it.id == prtr.persona.id}
                    }
                } else {
                    if(todos.find { it.id == -prtr.departamento.id}) {
                        todos -= todos.find { it.id == -prtr.departamento.id}
                    }
                }
            }
//            println "todos:.... $todos"

        }

        return [de     : de, padre: padre, principal: principal, disponibles: todos, tramite: tramite,
                persona: persona, bloqueo: bloqueo, cc: cc, rolesNo: rolesNo, pxt: pdt, params: params]
    }

    def cargaUsuarios() {
        def dir = Departamento.get(params.dir)
        def users = Persona.findAllByDepartamento(dir)
        for (int i = users.size() - 1; i > -1; i--) {
            if (!(users[i].estaActivo && users[i].puedeRecibir)) {
                users.remove(i)
            }
        }
        return [users: users]
    }

    //ALERTAS BANDEJA ENTRADA

    def alertRecibidos() {

        def usuario = session.usuario

        def persona = Persona.get(usuario.id)

        def recibidos = EstadoTramite.get(4)

        //------------------------------------------------

        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def rolImprimir = RolPersonaTramite.findByCodigo('I005')

        def pxtTodos = []
        def pxtTramites = []

        def pxtPara = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolPara)
        def pxtCopia = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolCopia)
        def pxtImprimir = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolImprimir)

        pxtTodos = pxtPara
        pxtTodos += pxtCopia
        pxtTodos += pxtImprimir

        pxtTodos.each {
            if (it?.tramite?.estadoTramite?.codigo == 'E003' || it?.tramite?.estadoTramite?.codigo == 'E004') {
                pxtTramites.add(it)
            }
        }
        //------------------------------------------------

        def tramites = []

        pxtTramites.each {
            if (it.tramite.estadoTramite == recibidos) {
                tramites.add(it.tramite)
            }
        }

        def fechaEnvio
        def prioridad

        def hora = 3600000  //milisegundos

        def totalPrioridad = 0
        def fecha

        Date nuevaFecha

        def tramitesRecibidos = 0

        def idTramites = []

        tramites.each {

            fechaEnvio = it.fechaEnvio

            prioridad = TipoPrioridad.get(it?.prioridad?.id).tiempo
            totalPrioridad = hora * prioridad
            fecha = fechaEnvio.getTime()
            nuevaFecha = new Date(fecha + totalPrioridad)

            if (!nuevaFecha.before(new Date())) {
                tramitesRecibidos++
                idTramites.add(it.id)
            }

        }
        return [tramitesRecibidos: tramitesRecibidos, idTramites: idTramites]

    }

    def errores() {
        return [params: params]
    }

    def alertaPendientes() {

        def usuario = session.usuario

        def persona = Persona.get(usuario.id)

        def pendientes = EstadoTramite.get(8)

        def tramitesPendientes = 0
        def totalPendientes = []
        //------------------------------------------------
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def rolImprimir = RolPersonaTramite.findByCodigo('I005')

        def pxtTodos = []
        def pxtTramites = []

        def pxtPara = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolPara)
        def pxtCopia = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolCopia)
        def pxtImprimir = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolImprimir)

        pxtTodos = pxtPara
        pxtTodos += pxtCopia
        pxtTodos += pxtImprimir

        pxtTodos.each {
            if (it?.tramite?.estadoTramite?.codigo == 'E003' || it?.tramite?.estadoTramite?.codigo == "E004") {
                pxtTramites.add(it)
            }
        }

        //------------------------------------------------
        pxtTramites.each {
            if (it.tramite.estadoTramite == pendientes) {
                totalPendientes.add(it.tramite)
            }
        }

        tramitesPendientes = totalPendientes.size()

        def dosHoras = 6200000

        def fechaEnvio
        def fecha
        def fechaRoja

        def tramitesPendientesRojos = 0
        def idRojos = []

        totalPendientes.each {
            if (it.fechaEnvio) {
//                println("fecha envio" + it.fechaEnvio)
                fechaEnvio = it.fechaEnvio
                fecha = fechaEnvio.getTime()
                fechaRoja = diasLaborablesService.fechaMasTiempo(fecha, 2)
                if (fechaRoja[0]) {
                    fechaRoja = fechaRoja[1]
                } else {
                    flash.message = "Ha ocurrido un error al calcular la fecha límite: " + fechaRoja[1]
                    redirect(action: 'errores')
                    return
                }
                if (fechaRoja.before(new Date())) {
                    tramitesPendientesRojos++
                    idRojos.add(it.id)
                }
            }
        }

        return [tramitesPendientesRojos: tramitesPendientesRojos, tramitesPendientes: tramitesPendientes, idRojos: idRojos]

    }


    def rojoPendiente() {
        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def pendientes = EstadoTramite.get(8)
        def tramitesPendientes = 0
        def totalPendientes = []

        //------------------------------------------------

        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def rolImprimir = RolPersonaTramite.findByCodigo('I005')

        def pxtTodos = []
        def pxtTramites = []

        def pxtPara = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolPara)
        def pxtCopia = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolCopia)
        def pxtImprimir = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolImprimir)

        pxtTodos = pxtPara
        pxtTodos += pxtCopia
        pxtTodos += pxtImprimir

        pxtTodos.each {
            if (it?.tramite?.estadoTramite?.codigo == 'E003' || it?.tramite?.estadoTramite?.codigo == "E004") {
                pxtTramites.add(it)
            }
        }

        //------------------------------------------------


        pxtTramites.each {
            if (it.tramite.estadoTramite == pendientes) {
                totalPendientes.add(it.tramite)
            }
        }
        tramitesPendientes = totalPendientes.size()
        return [tramitesPendientes: tramitesPendientes]
    }


    def alertaRetrasados() {
        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        //------------------------------------------------
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def rolImprimir = RolPersonaTramite.findByCodigo('I005')

        def pxtTodos = []
        def pxtTramites = []

        def pxtPara = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolPara)
        def pxtCopia = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolCopia)
        def pxtImprimir = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolImprimir)

        pxtTodos = pxtPara
        pxtTodos += pxtCopia
        pxtTodos += pxtImprimir

        pxtTodos.each {
            if (it?.tramite?.estadoTramite?.codigo == 'E003' || it?.tramite?.estadoTramite?.codigo == "E004") {
                pxtTramites.add(it)
            }
        }

        //------------------------------------------------

        def recibidos = EstadoTramite.get(4)

        def tramitesRetrasados = []

        pxtTramites.each {
            if (it.tramite.estadoTramite == recibidos) {
//                tramitesRetrasados.add(it.tramite)
                tramitesRetrasados += it.tramite

            }
        }

        def fechaEnvio
        def prioridad

        def hora = 3600000  //milisegundos

        def totalPrioridad = 0
        def fecha

        Date nuevaFecha

        def tramitesAtrasados = 0

        def idTramites = []

        def para

        tramitesRetrasados.each {
            fechaEnvio = it.fechaEnvio
            prioridad = TipoPrioridad.get(it?.prioridad?.id).tiempo
            totalPrioridad = hora * prioridad
            fecha = fechaEnvio.getTime()
            nuevaFecha = diasLaborablesService.fechaMasTiempo(fecha, 2)
            if (nuevaFecha[0]) {
                nuevaFecha = nuevaFecha[1]
            } else {
                flash.message = "Ha ocurrido un error al calcular la fecha límite: " + nuevaFecha[1]
                redirect(action: 'errores')
                return
            }

            if (nuevaFecha.before(new Date())) {
                tramitesAtrasados++
                idTramites.add(it.id)
            }
        }

        return [tramitesAtrasados: tramitesAtrasados, idTramites: idTramites]
    }

    //fin alertas bandeja entrada

    //bandeja entrada personal nueva con el procedure
    def bandejaEntrada() {
        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def esEditor = session.usuario.puedeEditor
        if (esEditor) {
            redirect(controller: "tramite2", action: "bandejaSalida")
            return
        }
        def bloqueo = false
        if (session.usuario.esTriangulo()) {
            flash.message = "Su perfil no le permite ingresar a la bandeja de entrada personal"
            redirect(controller: 'tramite3', action: 'bandejaEntradaDpto')
            return
        }
        if (!session.usuario.puedeRecibir) {
            flash.message = "Su perfil no tiene acceso a la bandeja de entrada personal"
            response.sendError(403)
        }

        params.sort = "trmtfcen"
        params.order = "desc"

        return [persona: persona, bloqueo: bloqueo, params: params]
    }

    def tablaBandeja() {
//        println params

        //** forzar actualización de bloqueos al Actualizar
        def job = new BloqueosJob()
        job.executeRecibir(session.usuario.departamento, session.usuario)
        job = null
        //** fin forzar actualización de bloqueos al Actualizar

        def busca = false
        def where = ""

        if (!params.sort || params.sort == "") {
            params.sort = "trmtfcen"
        }
        if (!params.order || params.order == "" || params.order == null) {
            params.order = "DESC"
        }

        if (params.fecha) {
            busca = true
            def fechaIni = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 00:00:00")
            def fechaFin = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 23:59:59")
            where += "WHERE (trmtfcen >= '${fechaIni.format('yyyy-MM-dd HH:mm:ss')}'" +
                    " AND trmtfcen <= '${fechaFin.format('yyyy-MM-dd HH:mm:ss')}')"
        }
        if (params.asunto) {
            busca = true
            if (where == "") {
                where = "WHERE "
            } else {
                where += " AND "
            }
            where += "(trmtasnt ilike '%${params.asunto.trim()}%')"
        }
        if (params.memorando) {
            busca = true
            if (where == "") {
                where = "WHERE "
            } else {
                where += " AND "
            }
            where += "(trmtcdgo ilike '%${params.memorando.trim()}%')"
        }

        def sql = "SELECT * FROM entrada_prsn($session.usuario.id) ${where} ORDER BY ${params.sort} ${params.order}"
//        println "bandeja de entrada: $sql"
        def cn = dbConnectionService.getConnection()
        def rows = cn.rows(sql.toString())
        return [rows: rows, busca: busca]
    }

    //BANDEJA PERSONAL
    def bandejaEntrada_old() {
        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def esEditor = session.usuario.puedeEditor
        if (esEditor) {
            redirect(controller: "tramite2", action: "bandejaSalida")
            return
        }
        def bloqueo = false
        if (session.usuario.esTriangulo()) {
            flash.message = "Su perfil no le permite ingresar a la bandeja de entrada personal"
            redirect(controller: 'tramite3', action: 'bandejaEntradaDpto')
            return
        }
        if (!session.usuario.puedeRecibir) {
            flash.message = "Su perfil no tiene acceso a la bandeja de entrada personal"
            response.sendError(403)
        }

        return [persona: persona, bloqueo: bloqueo]
    }


    def tablaBandeja_old() {
//        println "1.... --- " + System.currentTimeMillis()/1000
        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def enviado = EstadoTramite.findByCodigo("E003")
        def recibido = EstadoTramite.findByCodigo("E004")
        def anexo
        /*
        from PersonaDocumentoTramite as p
        inner join fetch p.tramite as tramites
        where p.persona=${session.usuario.id}
        and  p.rolPersonaTramite in (${rolPara.id + "," + rolCopia.id})
        and p.fechaEnvio is not null
        and tramites.estadoTramite in (3,4)
        order by p.fechaEnvio desc
        */

        //** forzar actualización de bloqueos al Actualizar
//        println "tablaBandeja... persona: $persona, dpto: ${persona.departamento}"
        def job = new BloqueosJob()
        job.executeRecibir(persona.departamento, session.usuario)
        job = null
        //** fin forzar actualización de bloqueos al Actualizar


        params.domain = params.domain ?: "persDoc"
        params.sort = params.sort ?: "fechaEnvio"
        params.order = params.order ?: "desc"

        println persona.id

        def tramites = PersonaDocumentoTramite.withCriteria {
            eq("persona", persona)
            inList("rolPersonaTramite", [rolPara, rolCopia])
            isNotNull("fechaEnvio")
            inList("estado", [enviado, recibido])
            tramite {
//                inList("estadoTramite", [enviado, recibido])
                if (params.domain == "tramite") {
                    order(params.sort, params.order)
                }
            }
            if (params.domain == "persDoc") {
                order(params.sort, params.order)
            }
        }

        def tramitesSinHijos = []
        def anulado = EstadoTramite.findByCodigo("E006")
        def band = false
        tramites.each { tr ->
            if (!(tr.tramite.tipoDocumento.codigo == "OFI")) {
                band = tramitesService.verificaHijos(tr, anulado)
                if (!band) {
                    tramitesSinHijos += tr
                }
            } else {
                if (tr.rolPersonaTramite.codigo == "R002") {
                    band = tramitesService.verificaHijos(tr, anulado)
                    if (!band) {
                        tramitesSinHijos += tr
                    }
                }
            }
        }
//        println "2.... --- " + System.currentTimeMillis()/1000
        return [tramites: tramitesSinHijos, params: params]
    }

    def observaciones() {

        def tramite = Tramite.get(params.id)

        return [tramite: tramite]

    }


    def guardarObservacion() {

        def tramite = Tramite.get(params.id)
        println "NO DEBERIA IMPRIMIR ESTO NUNCA"

        def alerta = new Alerta()
        alerta.mensaje = "entro a guardar observacion deprecated!!!!!!"
        alerta.controlador = "tramiteController"
        alerta.accion = "guardarObservacion"
        alerta.save(flush: true)
        tramite.observaciones = tramitesService.modificaObservaciones(tramite.observaciones, params.texto)
        if (!tramite.save(flush: true)) {
            render "Ocurrió un error al guardar"
        } else {
            render "Observación guardada correctamente"
        }

    }

    def observacionArchivado() {

        def tramite = Tramite.get(params.id)

        def observacion = ObservacionTramite.findByTramite(tramite)

        return [tramite: tramite, observacion: observacion]

    }


    def recibir() {

        def tramite = Tramite.get(params.id)
        return [tramite: tramite]


    }

    def guardarRecibir() {
//        println "GUARDAR RECIBIR " + params
        def persona = session.usuario

        def tramite = Tramite.get(params.id)
        def para = tramite.getPara()?.persona
        def estadoRecibido = EstadoTramite.findByCodigo("E004") //recibido
        def pxt = PersonaDocumentoTramite.findByTramiteAndPersona(tramite, persona)

        if (persona.id == para?.id) {
            tramite.estadoTramite = estadoRecibido
        }

        def hoy = new Date()
        def limite = hoy

        limite = diasLaborablesService.fechaMasTiempo(limite, tramite.prioridad.tiempo)
        if (limite[0]) {
            limite = limite[1]
        } else {
            flash.message = "Ha ocurrido un error al calcular la fecha límite: " + limite[1]
            redirect(controller: 'tramite', action: 'errores')
            return
        }

        pxt.fechaRecepcion = hoy
        pxt.fechaLimiteRespuesta = limite

        tramite.save(flush: true)
        pxt.save(flush: true)
        def alerta
        if (pxt.persona) {
            alerta = Alerta.findByPersonaAndTramite(pxt.persona, pxt.tramite)
        } else {
            alerta = Alerta.findByDepartamentoAndTramite(pxt.departamento, pxt.tramite)
        }
        if (alerta) {
            if (!alerta.fechaRecibido) {
                alerta.mensaje += " - Recibido"
                alerta.fechaRecibido = new Date()
                alerta.save(flush: true)
            }
        }

        if (!tramite.save(flush: true)) {
            println "Ocurrió un error al recibir: " + tramite.errors
            render "No_Ocurrió un error al recibir"
        } else {
            render "Ok_Trámite recibido correctamente"
        }


    }

    def busquedaBandeja() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def enviado = EstadoTramite.findByCodigo("E003")
        def recibido = EstadoTramite.findByCodigo("E004")

        if (params.fecha) {
            params.fechaIni = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 00:00:00")
            params.fechaFin = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 23:59:59")
        }

        params.domain = params.domain ?: "persDoc"
        params.sort = params.sort ?: "fechaEnvio"
        params.order = params.order ?: "desc"

        def tramites = PersonaDocumentoTramite.withCriteria {

            if (params.fecha) {
                ge('fechaEnvio', params.fechaIni)
                le('fechaEnvio', params.fechaFin)
            }

            eq("persona", persona)
            inList("rolPersonaTramite", [rolPara, rolCopia])
            isNotNull("fechaEnvio")
            inList("estado", [enviado, recibido])

            tramite {
                if (params.asunto) {
                    ilike('asunto', '%' + params.asunto + '%')
                }
                if (params.memorando) {
                    ilike('codigo', '%' + params.memorando + '%')
                }
            }
            order("fechaEnvio", "desc")
        }

        if (params.domain == "persDoc") {
            tramites.sort { it[params.sort] }
        }

        if (params.domain == "tramite") {
            tramites.sort { it.tramite[params.sort] }
        }
        if (params.order == "desc") {
            tramites = tramites.reverse()
        }

        def tramitesSinHijos = []
        def anulado = EstadoTramite.findByCodigo("E006")
        def band = false
        tramites.each { tr ->
            if (!(tr.tramite.tipoDocumento.codigo == "OFI")) {
                band = tramitesService.verificaHijos(tr, anulado)
                if (!band) {
                    tramitesSinHijos += tr
                }
            }
        }

        def pxtTramites = tramitesSinHijos

        return [tramites: pxtTramites]
    }


    def archivados() {
        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        return [persona: persona, si: params.dpto]
    }


    def tablaArchivados() {

        def persona = Persona.get(session.usuario.id)
        def departamento = persona?.departamento
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def pxtPara = PersonaDocumentoTramite.withCriteria {
            if (persona?.esTriangulo()) {
                eq("departamento", departamento)
            } else {
                eq("persona", persona)
            }
            eq("rolPersonaTramite", rolPara)
            eq('estado', EstadoTramite.findByCodigo("E005"))
            isNotNull("fechaEnvio")

            or {
                eq("estado", EstadoTramite.findByCodigo("E003")) //enviado
                eq("estado", EstadoTramite.findByCodigo("E007")) //enviado al jefe
                eq("estado", EstadoTramite.findByCodigo("E004")) //recibido
                eq('estado', EstadoTramite.findByCodigo("E005"))
            }

        }
        def pxtCopia = PersonaDocumentoTramite.withCriteria {
            if (persona?.esTriangulo()) {
                eq("departamento", departamento)
            } else {
                eq("persona", persona)
            }
            eq("rolPersonaTramite", rolCopia)
            eq('estado', EstadoTramite.findByCodigo("E005"))
            isNotNull("fechaEnvio")

            or {
                eq("estado", EstadoTramite.findByCodigo("E003")) //enviado
                eq("estado", EstadoTramite.findByCodigo("E007")) //enviado al jefe
                eq("estado", EstadoTramite.findByCodigo("E004")) //recibido
                eq('estado', EstadoTramite.findByCodigo("E005"))
            }

        }
        pxtPara += pxtCopia
        return [tramites: pxtPara]
    }

    def busquedaArchivados() {

        def persona = Persona.get(session.usuario.id)
        def departamento = persona?.departamento

        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');

        def pxtPara = PersonaDocumentoTramite.withCriteria {
            eq("departamento", departamento)
            eq("rolPersonaTramite", rolPara)
            eq('estado', EstadoTramite.findByCodigo("E005"))
            isNotNull("fechaEnvio")

            or {
                eq("estado", EstadoTramite.findByCodigo("E003")) //enviado
                eq("estado", EstadoTramite.findByCodigo("E007")) //enviado al jefe
                eq("estado", EstadoTramite.findByCodigo("E004")) //recibido
                eq('estado', EstadoTramite.findByCodigo("E005"))
            }

        }
        def pxtCopia = PersonaDocumentoTramite.withCriteria {
            eq("departamento", departamento)
            eq("rolPersonaTramite", rolCopia)
            eq('estado', EstadoTramite.findByCodigo("E005"))
            isNotNull("fechaEnvio")

            or {
                eq("estado", EstadoTramite.findByCodigo("E003")) //enviado
                eq("estado", EstadoTramite.findByCodigo("E007")) //enviado al jefe
                eq("estado", EstadoTramite.findByCodigo("E004")) //recibido
                eq('estado', EstadoTramite.findByCodigo("E005"))
            }

        }
        pxtPara += pxtCopia
        if (params.fecha) {
            params.fechaIni = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 00:00:00")
            params.fechaFin = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 23:59:59")
        }

        def res = PersonaDocumentoTramite.withCriteria {

            if (params.fecha) {
                gt('fechaEnvio', params.fechaIni)
                lt('fechaEnvio', params.fechaFin)
            }

            tramite {
                if (params.asunto) {
                    ilike('asunto', '%' + params.asunto + '%')
                }
                if (params.memorando) {
                    ilike('codigo', '%' + params.memorando + '%')
                }
            }
        }
        return [tramites: res, pxtTramites: pxtPara]
    }

    def busquedaBandejaSalida() {
        if (params.fecha) {
            params.fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        }
        def res = Tramite.withCriteria {

            if (params.fecha) {
                eq('fechaIngreso', params.fecha)
            }
            if (params.asunto) {
                ilike('asunto', '%' + params.asunto + '%')
            }
            if (params.memorando) {

                ilike('numero', '%' + params.memorando + '%')

            }
        }
        return [tramites: res]
    }


    def todaDescendencia(Tramite tramite) {
        def decendencia = []
        def hijos = Tramite.findAllByPadre(tramite)
        hijos.each { h ->
            decendencia += h
            if (Tramite.countByPadre(h) > 0) {
                decendencia += todaDescendencia(h)
            }
        }
        return decendencia
    }

    def todaDescendenciaExtended(Tramite tramite, String tipo, objeto) {
        def decendencia = []
        def hijos
        if (tipo == "dep") {
            hijos = Tramite.findAllByPadreAndDeDepartamento(tramite, objeto)
        } else {
            hijos = Tramite.findAllByPadreAndDe(tramite, objeto)
        }
        hijos.each { h ->
            decendencia += h
            def cantHijos
            def nuevoObjeto
            if (tipo == "dep") {
                cantHijos = Tramite.countByPadreAndDeDepartamento(tramite, objeto)
            } else {
                cantHijos = Tramite.countByPadreAndDe(tramite, objeto)
            }
            if (cantHijos > 0) {
                decendencia += todaDescendenciaExtended(h, tipo, nuevoObjeto)
            }
        }
        return decendencia
    }

    def revisarHijos() {

        //nuevo
        def pxt = PersonaDocumentoTramite.get(params.id)
        if (pxt) {
            def hijos = []
            if (params.tipo == 'archivar') {
                if (pxt?.departamento) {
                    hijos = Tramite.findAllByPadreAndDeDepartamento(pxt?.tramite, pxt?.departamento)
                } else {
                    hijos = Tramite.findAllByPadreAndDe(pxt?.tramite, pxt?.persona)
                }
            } else if (params.tipo == 'anular') {
                if (pxt?.departamento) {
                    hijos = todaDescendenciaExtended(pxt?.tramite, 'dep', pxt?.departamento)
                } else {
                    hijos = todaDescendenciaExtended(pxt?.tramite, 'per', pxt?.persona)
                }
            }
            return [pxt: pxt, hijos: hijos]
        } else {
            return [error: "No se encontró"]
        }
    }


    def archivar() {

        def persona = Persona.get(session.usuario.id)
        def pdt = PersonaDocumentoTramite.get(params.id)

        if (pdt?.estado?.codigo == 'E003' || pdt == null) {
            render 'no'
//            println("errores" + errors)
            return
        }

        if (pdt.estado.codigo != "E006" && pdt.estado.codigo != "E005") {
            def estadoTramite = EstadoTramite.findByCodigo('E005')
            pdt.estado = estadoTramite
            pdt.fechaArchivo = new Date();
            def observacionOriginal = pdt.observaciones
            def accion = "Archivado"
            def solicitadoPor = params.aut
            def usuario = session.usuario.login
            def texto = ""
            def nuevaObservacion = params.texto
            pdt.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

            if (pdt.rolPersonaTramite.codigo == "R001") {
                def nuevaObs = "Trámite PARA "
                if (pdt.departamento) {
                    nuevaObs += "el dpto. ${pdt.departamento.codigo}"
                } else if (pdt.persona) {
                    nuevaObs += "el usuario ${pdt.persona.login}"
                }
                nuevaObs += " archivado"
                observacionOriginal = pdt.tramite.observaciones
//                texto = nuevaObs
                pdt.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

                pdt.tramite.save()
            } else {
                def nuevaObs = "COPIA para "
                if (pdt.departamento) {
                    nuevaObs += "el dpto. ${pdt.departamento.codigo}"
                } else if (pdt.persona) {
                    nuevaObs += "el usuario ${pdt.persona.login}"
                }
                nuevaObs += " archivado"
                observacionOriginal = pdt.tramite.observaciones
//                texto = nuevaObs
                pdt.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
                pdt.tramite.save()

                if (!pdt.save(flush: true)) {
                    render("no")
//                    println("errores" + errors)
                    return
                } else {
                    render("ok")
                    return
                }
            }
            render "ok"
        } else {
//            println("errores" + errors)
            render("no")
        }
    }


    def anular() {
        def persona = Persona.get(session.usuario.id)
        def tramite = Tramite.get(params.id)
        def estadoTramite = EstadoTramite.findByCodigo('E006')
        def hijos = todaDescendencia(tramite)

        tramite.estadoTramite = estadoTramite
        def observacion = new ObservacionTramite()
        observacion.persona = persona
        observacion.tramite = tramite
        observacion.fecha = new Date()
        println "NO DEBERIA IMPRIMIR ESTO NUNCA"

        def alerta = new Alerta()
        alerta.mensaje = "entro a anular deprecated!!!!!!"
        alerta.controlador = "tramiteController"
        alerta.accion = "anular"
        alerta.save(flush: true)
        observacion.observaciones = tramitesService.modificaObservaciones(observacion.observaciones, params.texto + " (${new Date().format('dd-MM-yyyy HH:mm')})")
        observacion.tipo = 'anular'
        observacion.save(flush: true)

        if (!tramite.save(flush: true) || !observacion.save(flush: true)) {
            render("no")
        } else {
            render("ok")
        }

        if (hijos) {
            hijos.each { t ->
                t.estadoTramite = estadoTramite
                def observacionHijos = new ObservacionTramite()
                observacionHijos.persona = persona
                observacionHijos.tramite = tramite
                observacionHijos.fecha = new Date()
                observacionHijos.observaciones = tramitesService.modificaObservaciones(observacionHijos.observaciones, "Trámite padre anulado:" + tramite?.codigo + "observaciones originales:" + params.texto + " (${new Date().format('dd-MM-yyyy HH:mm')})")
                observacion.tipo = 'anular'
                observacionHijos.save(flush: true)
            }
        }
    }

    //trámites anulados

    def anulados() {
        def persona = Persona.get(session.usuario.id)
        return [persona: persona]
    }

    def tablaAnulados() {
        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def pxtTodos = []
        def pxtTramites = []
        def pxtPara = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolPara)
        def pxtCopia = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolCopia)

        pxtTodos = pxtPara
        pxtTodos += pxtCopia
        pxtTodos.each {
            if (it?.tramite?.estadoTramite?.codigo == 'E006') {
                pxtTramites.add(it)
            }
        }
        return [tramites: pxtTramites]
    }


    def busquedaAnulados() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def pxtTodos = []
        def pxtTramites = []
        def pxtPara = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolPara)
        def pxtCopia = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolCopia)

        pxtTodos = pxtPara
        pxtTodos += pxtCopia
        pxtTodos.each {
            if (it?.tramite?.estadoTramite?.codigo == 'E006') {
                pxtTramites.add(it)
            }
        }

        if (params.fecha) {
            params.fechaIni = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 00:00:00")
            params.fechaFin = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 23:59:59")
        }

        def res = PersonaDocumentoTramite.withCriteria {

            if (params.fecha) {
                gt('fechaEnvio', params.fechaIni)
                lt('fechaEnvio', params.fechaFin)
            }

            tramite {
                if (params.asunto) {
                    ilike('asunto', '%' + params.asunto + '%')
                }
                if (params.memorando) {
                    ilike('codigo', '%' + params.memorando + '%')
                }
            }
        }
        return [tramites: res, pxtTramites: pxtTramites]
    }

    def revisarConfidencial() {
        def tramite = Tramite.get(params.id)
        def persona = Persona.get(session.usuario.id)
        def condifencial = tramite?.tipoTramite?.id

        if (condifencial == 1) {
            if (tramite.getPara().persona == persona) {
                render 'ok'
            } else {
                render 'no'
            }
        } else {
            render 'ok'
        }
    }
}
