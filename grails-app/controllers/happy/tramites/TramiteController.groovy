package happy.tramites

import groovy.json.JsonBuilder
import groovy.time.TimeCategory
import happy.alertas.Alerta
import happy.seguridad.Persona
import happy.utilitarios.DiaLaborable
import happy.utilitarios.DiaLaborableController


class TramiteController extends happy.seguridad.Shield {

    def diasLaborablesService
    def enviarService

//    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def redactar() {
        def tramite = Tramite.get(params.id)
        if (tramite.estadoTramite.codigo == "E001") { //borrador, por enviar
            return [tramite: tramite]
        } else {
            flash.message = "El trámite seleccionado no puede ser editado"
            redirect(action: "errores")
        }
    }

    def saveDEX() {
        def tramite = Tramite.get(params.id)
        tramite.texto = params.editorTramite
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

            def estadoEnviado = EstadoTramite.findByCodigo('E003')
            def estadoRecibido = EstadoTramite.findByCodigo('E004')

            def pdt = new PersonaDocumentoTramite()
            pdt.tramite = tramite
            pdt.persona = session.usuario
            pdt.departamento = session.departamento
            pdt.fechaEnvio = ahora
            pdt.rolPersonaTramite = rolEnvia
            if (!pdt.save(flush: true)) {
                println pdt.errors
            }

            def pdt2 = new PersonaDocumentoTramite()
            pdt2.tramite = tramite
            pdt2.persona = session.usuario
            pdt2.departamento = session.departamento
            pdt2.fechaEnvio = ahora
            pdt2.fechaRecepcion = ahora
            pdt2.rolPersonaTramite = rolRecibe
            if (!pdt2.save(flush: true)) {
                println pdt2.errors
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
            render "OK*" + createLink(controller: 'tramite3', action: "bandejaEntradaDpto")
        } else {
            render "NO*<ul>" + msg + "</ul>"
        }
    }

    def saveTramite() {
//        println "save tramite"
//        println params
        /*
         ['editorTramite':'<p>s asdf asdfasd asdf</p>\n', 'tramite':'4', 'action':'saveTramite', 'format':null, 'controller':'tramiteImagenes']
         */
        def tramite = Tramite.get(params.id)
        tramite.texto = params.editorTramite
//        tramite.asunto = params.asunto
        tramite.fechaModificacion = new Date()

        if (tramite.save(flush: true)) {
            def para = tramite.para

//            crearPdf(tramite, Persona usuario, String enviar, String type, String editorTramite, String asunto, String realPath, String mensaje)
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

    def getPara_ajax() {
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
            switch (tipoDoc.codigo) {
                case "CIR":
                    html = "<div class=\"col-xs-4 negrilla\" id=\"divPara\" style=\"margin-top: -10px\">"
                    html += "</div>"
                    break;
                case "OFI":
                    html = "<div class=\"col-xs-4 negrilla\" id=\"divPara\" style=\"margin-top: -10px\">Para: "
                    html += g.textField(name: "paraExt",
                            class: "form-control label-shared required",
                            value: tramite?.paraExterno,
                            style: "width:310px;")
                    html += "</div>"
                    break;
                default: //DEX SUM MEM PLA
                    html = "<div class=\"col-xs-4 negrilla\" id=\"divPara\" style=\"margin-top: -10px\">Para: "
                    html += elm.comboPara(name: "tramite.para",
                            id: "para",
                            value: tramite?.para?.departamento ? tramite.para.departamentoId * -1 : tramite?.para?.personaId,
                            style: "width:310px;",
                            class: "form-control label-shared required",
                            tipoDoc: tipoDoc,
                            tipo: params.tipo)
                    html += "</div>"
                    html += "    <div class=\"col-xs-1 negrilla\" id=\"divBotonInfo\">\n" +
                            "                    <a href=\"#\" id=\"btnInfoPara\" class=\"btn btn-sm btn-info\">\n" +
                            "                    <i class=\"fa fa-search\"></i>\n" +
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
        render html
    }

    def crearTramite() {

        if (Persona.get(session.usuario.id).tiposDocumento.size() == 0) {
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
            println "HAY MAS DE 1 ANIO ${new Date().format('yyyy')}!!!!!: ${anio}"
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

        def persona = Persona.get(session.usuario.id)

        def de = session.usuario
        def disp, disponibles = []
        def disp2 = []
        def todos = []

        if (persona.puedeTramitar) {
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
                        users += it
                    }
                }
                for (int i = users.size() - 1; i > -1; i--) {
                    if (!(users[i].estaActivo && users[i].puedeRecibir)) {
                        users.remove(i)
                    } else {
                        if (params.id) {
                            if (!(tramite.copias.persona.id*.toLong()).contains(users[i].id.toLong())) {
                                disponibles.add([id: users[i].id, label: users[i].toString(), obj: users[i]])
                            }
                        } else {
                            disponibles.add([id: users[i].id, label: users[i].toString(), obj: users[i]])
                        }
                    }
                }
            }
        }

        disp.each { dep ->
            if (params.id) {
                if (!(tramite.copias.departamento.id*.toLong()).contains(dep.id.toLong())) {
                    if (dep.triangulos.size() > 0) {
                        disp2.add([id: dep.id * -1, label: dep.descripcion, obj: dep])
                    }
                }
            } else {
                if (dep.triangulos.size() > 0) {
                    disp2.add([id: dep.id * -1, label: dep.descripcion, obj: dep])
                }
            }
        }

        todos = disponibles + disp2

        def bloqueo = false
//        if (session.departamento.estado == "B") {
//            bloqueo = true
//        }

        return [de     : de, padre: padre, principal: principal, disponibles: todos, tramite: tramite,
                persona: persona, bloqueo: bloqueo, cc: cc, rolesNo: rolesNo, pdt: params.pdt]
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

//    def save_bck() {
//        /*todo comentar esto*/
//        params.fechaLimiteRespuesta_hour = "13"
//        params.fechaLimiteRespuesta_minutes = "26"
//        println " save tramite " + params
//        def estadoTramite = EstadoTramite.get(1)
//        def tramite
//        def error = false
//        if (params.tramite.id) {
//            tramite = Tramite.get(params.tramite.id)
//        } else {
//            tramite = new Tramite()
//        }
//        if (params.fechaLimiteRespuesta_day.size() == 1)
//            params.fechaLimiteRespuesta_day = "0" + params.fechaLimiteRespuesta_day
//        if (params.fechaLimiteRespuesta_month.size() == 1)
//            params.fechaLimiteRespuesta_month = "0" + params.fechaLimiteRespuesta_month
//        def fechaLimite = params.fechaLimiteRespuesta_day + "-" + params.fechaLimiteRespuesta_month + "-" + params.fechaLimiteRespuesta_year + " " + params.fechaLimiteRespuesta_hour + ":" + params.fechaLimiteRespuesta_minutes
//        println "fecha limite " + fechaLimite
//        fechaLimite = new Date().parse("dd-MM-yyyy HH:mm", fechaLimite)
//        params.tramite.fechaLimiteRespuesta = fechaLimite
//        /*Aqui falta generar el numero de tramite*/
//        params.tramite.numero = "MEMPRUEBA-0001"
//        tramite.properties = params.tramite
//        tramite.estadoTramite = estadoTramite
//        if (!tramite.save(flush: true)) {
//            println "error save tramite " + tramite.errors
//            flash.message = "Ha ocurrido un error al grabar el tramite, por favor, verifique la información ingresada"
//            redirect(action: "crearTramite")
//            return
//        } else {
//            def departamentos = []
//            def parts = params.data.split("%")
//            println "parts " + parts + " data " + params.data
//            parts.each() { p ->
//                if (p != "") {
//                    def datos = p.split(";")
//                    println "datos " + datos
//                    def user = happy.seguridad.Persona.get(session.usuario.id)
//                    /*persona que recibe ya sea para o copia*/
//                    def prsn = happy.seguridad.Persona.get(datos[1])
//                    def rol = RolPersonaTramite.get(datos[2])
//                    if (user.departamento.id != prsn.departamento.id) {
//                        println "necesita puerta de entrada y saldia "
//                        if (!departamentos.contains(prsn.departamento.id)) {
//                            departamentos.add(prsn.departamento.id)
//                            println "insert puerta para  " + prsn.departamento.descripcion + "  " + prsn.departamento.id
//                            /*crea registros para puerta de salida*/
//                            def salida
//                            def permisoEnv = PermisoTramite.findByCodigo("E002")
//                            def rolSalida = RolPersonaTramite.findByCodigo("E004")
//                            def entrada
//                            def permisoRec = PermisoTramite.findByCodigo("E001")
//                            def rolIngreso = RolPersonaTramite.findByCodigo("E003")
//                            println "permiso salida " + permisoEnv.id
//                            println "permiso entrada " + permisoRec.id
//                            /*Busco puertas de salida*/
//                            happy.seguridad.Persona.findAllByDepartamento(user.departamento).each { pr ->
//                                println "buscando " + pr.id
//                                def usu = PermisoUsuario.findByPersonaAndPermisoTramite(pr, permisoEnv)
//                                if (usu)
//                                    salida = usu
//                            }
//                            println "usuario de salida " + salida?.persona?.nombre + " " + salida?.persona?.id
//                            if (!salida) {
//                                println "error no hay puerta de salida al departamento"
//                                flash.message = "Ha ocurrido un error al procesar el tramite, el Departamento ${prsn.departamento.descripcion} no tiene asignado un usuario para el envio de documentos"
//                                redirect(action: "crearTramite", id: tramite.id)
//                                return
//                            } else {
//                                def des = new PersonaDocumentoTramite()
//                                des.persona = salida.persona
//                                des.tramite = tramite
//                                des.permiso = "E"
//                                des.rolPersonaTramite = rolSalida
//                                if (!des.save(flush: true))
//                                    println "error destinatario " + des.errors
//                            }
//                            /*crea registros para puertas de entrada al departamento*/
//                            happy.seguridad.Persona.findAllByDepartamento(prsn.departamento).each { pr ->
//                                def usu = PermisoUsuario.findByPersonaAndPermisoTramite(pr, permisoRec)
//                                if (usu)
//                                    entrada = usu
//                            }
//                            println "usuario de entrada " + entrada?.persona?.nombre + " " + entrada?.persona?.id
//                            if (!entrada) {
//                                println "error no hay puerta de entrada al departamento"
//                                flash.message = "Ha ocurrido un error al procesar el tramite, el Departamento ${prsn.departamento.descripcion} no tiene asignado un usuario para la recepción de documentos"
//                                redirect(action: "crearTramite", id: tramite.id)
//                                return
//                            } else {
//                                def des = new PersonaDocumentoTramite()
//                                des.persona = entrada.persona
//                                des.tramite = tramite
//                                des.permiso = "R"
//                                des.rolPersonaTramite = rolIngreso
//                                if (!des.save(flush: true))
//                                    println "error destinatario " + des.errors
//                            }
//                        }
//                    }
//                    /*creo registros de para y copia*/
//                    def des = new PersonaDocumentoTramite()
//                    des.persona = prsn
//                    des.tramite = tramite
//                    des.permiso = "PO"
//                    des.rolPersonaTramite = rol
//                    println "creo recipiente " + des.persona.nombre + "  " + des.persona.id + "  " + des.rolPersonaTramite.descripcion
//                    if (!des.save(flush: true))
//                        println "error destinatario para o copia " + des.errors
//                }
//            }
//        }
//        /*Fechas en nulo.. eso se llena en enviar*/
//        redirect(action: "redactarTramite", id: tramite.id)
//    }

//    def redactarTramite() {
//        def tramite = Tramite.get(params.id)
//        if (tramite.de.id.toInteger() != session.usuario.id.toInteger()) {
//            response.sendError(403)
//            return
//        } else {
//            [tramite: tramite]
//        }
//    }

//    def randomDep() {
//        return
//        def rand = new Random()
//        def numPer
//        def permisos = PermisoTramite.findAllByCodigoNotLike("E%")
//        def perRec = PermisoTramite.findByCodigo("E001")
//        def perEnv = PermisoTramite.findByCodigo("E002")
//        def maxPer = permisos.size() - 1
//        def deps = []
//        happy.seguridad.Persona.findAll("from Persona where id>4 order by id").each { per ->
//            def dep = null
//            def num
//            while (dep == null) {
//                num = rand.nextInt(13)
//                dep = Departamento.get(num + 2)
//            }
//            println "prsn " + per.id + " dep " + dep.id + " num " + (num + 2) + " " + per
//            per.departamento = dep
//            if (per.cedula == "1111111111")
//                per.cedula = "22" + rand.nextInt(9) + "3" + rand.nextInt(9) + rand.nextInt(9) + rand.nextInt(9) + rand.nextInt(9) + rand.nextInt(9) + rand.nextInt(9)
//            if (!per.save(flush: true))
//                println "error " + per.errors
//            println "despues " + per.departamento.id + " " + per.cedula
//            if (!deps.contains(dep)) {
//                deps.add(dep)
//                def pr = new PermisoUsuario()
//                pr.persona = per
//                pr.fechaInicio = new Date()
//                pr.permisoTramite = perRec
//                if (!pr.save())
//                    println "error save perm " + pr.errors
//                pr = new PermisoUsuario()
//                pr.persona = per
//                pr.fechaInicio = new Date()
//                pr.permisoTramite = perEnv
//                if (!pr.save())
//                    println "error save perm " + pr.errors
//            } else {
//                numPer = rand.nextInt(5)
//                numPer.times { t ->
//                    def pr = new PermisoUsuario()
//                    pr.persona = per
//                    pr.fechaInicio = new Date()
//                    num = rand.nextInt(maxPer)
//                    pr.permisoTramite = permisos[num]
//                    if (!pr.save())
//                        println "error save perm " + pr.errors
//                }
//            }
//
//        }
//
//    }

    //ALERTAS BANDEJA ENTRADA

    def alertRecibidos() {

        def usuario = session.usuario

        def persona = Persona.get(usuario.id)

        def recibidos = EstadoTramite.get(4)

//        def pxt = PersonaDocumentoTramite.findAllByPersona(persona)

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

//            println("tramite" + it)
////
//            println("--->>" + it?.getFechaMaximoRespuesta())

            fechaEnvio = it.fechaEnvio

            prioridad = TipoPrioridad.get(it?.prioridad?.id).tiempo

            totalPrioridad = hora * prioridad

            fecha = fechaEnvio.getTime()

            nuevaFecha = new Date(fecha + totalPrioridad)

//            nuevaFecha = it?.getfechaMaximoRespuesta

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
//        def pxt = PersonaDocumentoTramite.findAllByPersona(persona)

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

//            println("__<<<<____" +  it?.getFechaLimite)

            if (it.fechaEnvio) {
                println("fecha envio" + it.fechaEnvio)
                fechaEnvio = it.fechaEnvio
                fecha = fechaEnvio.getTime()
//                fechaRoja = new Date(fecha + dosHoras)
//                fechaRoja = it?.getFechaLimite
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
//        def pxt = PersonaDocumentoTramite.findAllByPersona(persona)

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

//        def pxt = PersonaDocumentoTramite.findAllByPersona(persona)

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

//            nuevaFecha = new Date(fecha + totalPrioridad)

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

    def bandejaSalidaDepartamento() {
        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        return [persona: persona]
    }

    def tablaBandejaSalidaDepartamento() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def departamento = persona?.departamento
        def estados = EstadoTramite.findAllByCodigoInList(["E001", "E002", "E003"])
        def personasDepartamento = Persona.findAllByDepartamento(departamento)
//        def tramites = Tramite.findAllByDeAndEstadoTramiteInList(persona,estados,[sort:"fechaCreacion",order:"desc"])
        def tramites = Tramite.findAllByDeInListAndEstadoTramiteInList(personasDepartamento, estados, [sort: "fechaCreacion", order: "desc"])

//        println("tramites salida departamento" + tramites)
        return [persona: persona, tramites: tramites]
    }

    //BANDEJA PERSONAL


    def bandejaEntrada() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def bloqueo = false
//        if (session.departamento.estado == "B")
//            bloqueo = true
        return [persona: persona, bloqueo: bloqueo]

    }


    def tablaBandeja() {
        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
//        def rolImprimir = RolPersonaTramite.findByCodigo('I005')
        def enviado = EstadoTramite.findByCodigo("E003")
        def recibido = EstadoTramite.findByCodigo("E004")
        def anexo
//        def tramites = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite as p  inner join fetch p.tramite as tramites where p.persona=${session.usuario.id} and  p.rolPersonaTramite in (${rolPara.id + "," + rolCopia.id/* + "," + rolImprimir.id*/}) and p.fechaEnvio is not null and tramites.estadoTramite in (3,4) order by p.fechaEnvio desc ")
        /*
        from PersonaDocumentoTramite as p
        inner join fetch p.tramite as tramites
        where p.persona=${session.usuario.id}
        and  p.rolPersonaTramite in (${rolPara.id + "," + rolCopia.id})
        and p.fechaEnvio is not null
        and tramites.estadoTramite in (3,4)
        order by p.fechaEnvio desc
        */

        params.domain = params.domain ?: "persDoc"
        params.sort = params.sort ?: "fechaEnvio"
        params.order = params.order ?: "desc"

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

        tramites.each { tr ->
            if (Tramite.countByPadreAndDe(tr.tramite, session.usuario) == 0) {
                tramitesSinHijos += tr
            }
        }

        return [tramites: tramitesSinHijos, params: params]
    }

    //alertas


    def observaciones() {

        def tramite = Tramite.get(params.id)

        return [tramite: tramite]

    }


    def guardarObservacion() {

        def tramite = Tramite.get(params.id)
        tramite.observaciones = params.texto

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
        def persona = session.usuario

        def tramite = Tramite.get(params.id)
        def para = tramite.getPara()?.persona

//        def estadoRecibido = EstadoTramite.get(4)
        def estadoRecibido = EstadoTramite.findByCodigo("E004") //recibido
        def pxt = PersonaDocumentoTramite.findByTramiteAndPersona(tramite, persona)

        if (persona.id == para?.id) {
            tramite.estadoTramite = estadoRecibido
        }

//        pxt.fechaRecepcion = new Date()
//        def fecha = pxt.fechaRecepcion
//        use(TimeCategory) {
//            fecha = fecha + (tramite.prioridad.tiempo).hours
//        }
//        pxt.fechaLimiteRespuesta = fecha
        def hoy = new Date()
        def limite = hoy
//        use(TimeCategory) {
//            limite = limite + tramite.prioridad.tiempo.hours
//        }
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
        if (pxt.persona)
            alerta = Alerta.findByPersonaAndTramite(pxt.persona, pxt.tramite)
        else
            alerta = Alerta.findByDepartamentoAndTramite(pxt.departamento, pxt.tramite)
        if (alerta) {
            if (!alerta.fechaRecibido) {
                alerta.mensaje += " - Recibido"
                alerta.fechaRecibido = new Date()
                alerta.save(flush: true)
            }
        }

        if (!tramite.save(flush: true)) {
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

//      def tramites = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite as p  inner join fetch p.tramite as tramites where p.persona=${session.usuario.id} and  p.rolPersonaTramite in (${rolPara.id + "," + rolCopia.id/* + "," + rolImprimir.id*/}) and p.fechaEnvio is not null and tramites.estadoTramite in (3,4) order by p.fechaEnvio desc ")

        params.domain = params.domain ?: "persDoc"
        params.sort = params.sort ?: "fechaEnvio"
        params.order = params.order ?: "desc"

        def tramites = PersonaDocumentoTramite.withCriteria {
            eq("persona", persona)
            inList("rolPersonaTramite", [rolPara, rolCopia])
            isNotNull("fechaEnvio")
            tramite {
                inList("estadoTramite", [enviado, recibido])
                if (params.domain == "tramite") {
                    order(params.sort, params.order)
                }
            }
            if (params.domain == "persDoc") {
                order(params.sort, params.order)
            }
        }

        def tramitesSinHijos = []

        tramites.each { tr ->
            if (Tramite.countByPadreAndDe(tr.tramite, session.usuario) == 0) {
                tramitesSinHijos += tr
            }
        }



        def pxtTodos = []
        def pxtTramites = tramitesSinHijos

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

//        println("--->" + res)
//        println("DDDD:" + pxtTramites)

//        return [tramites: res, pxtTramites: pxtTramites, idTramitesRetrasados: idTramitesRetrasados, idTramitesRecibidos: idTramitesRecibidos, idRojos: idRojos]
        return [tramites: res, pxtTramites: pxtTramites]

    }


    def archivados() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)

        return [persona: persona, si: params.dpto]


    }


    def tablaArchivados() {

        //old

//        def usuario = session.usuario
//        def persona = Persona.get(usuario.id)
//        def rolPara = RolPersonaTramite.findByCodigo('R001');
//        def rolCopia = RolPersonaTramite.findByCodigo('R002');
//
//
//        def pxtTodos = []
//        def pxtTramites = []
//
//        def pxtPara = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolPara)
//        def pxtCopia = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolCopia)
//
//        pxtTodos = pxtPara
//        pxtTodos += pxtCopia
//
//        pxtTodos.each {
//            println("-->" + it?.tramite?.deDepartamento?.id)
//            if (it?.tramite?.estadoTramite?.codigo == 'E005' && it?.tramite?.deDepartamento?.id == null) {
//                pxtTramites.add(it)
//            }
//        }

        //nuevo

        def persona = Persona.get(session.usuario.id)
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');

        def estadoArchivado = EstadoTramite.findByCodigo('E005')
        def pxtPara = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramiteAndEstado(persona, rolPara, estadoArchivado)
        def pxtCopia = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramiteAndEstado(persona, rolCopia, estadoArchivado)

        pxtPara += pxtCopia

//        println("archivados:" +  pxtPara)
        return [tramites: pxtPara]
    }

    def busquedaArchivados() {

        //old
//        def usuario = session.usuario
//        def persona = Persona.get(usuario.id)
//        def rolPara = RolPersonaTramite.findByCodigo('R001');
//        def rolCopia = RolPersonaTramite.findByCodigo('R002');
//        def rolImprimir = RolPersonaTramite.findByCodigo('I005')
//
//        def pxtTodos = []
//        def pxtTramites = []
//
//        def pxtPara = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolPara)
//        def pxtCopia = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolCopia)
//        def pxtImprimir = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolImprimir)
//
//
//        pxtTodos = pxtPara
//        pxtTodos += pxtCopia
//        pxtTodos += pxtImprimir
//
//        pxtTodos.each {
//            if (it?.tramite?.estadoTramite?.codigo == 'E005') {
//                pxtTramites += it
//            }
//        }

        //nuevo

        def persona = Persona.get(session.usuario.id)
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');

        def estadoArchivado = EstadoTramite.findByCodigo('E005')
        def pxtPara = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramiteAndEstado(persona, rolPara, estadoArchivado)
        def pxtCopia = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramiteAndEstado(persona, rolCopia, estadoArchivado)


        pxtPara += pxtCopia

//        println("todos:" + pxtPara)


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

//        println("filtrados" + res)

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

        //original
//        def tramite = Tramite.get(params.id)
//        def observacion = ObservacionTramite.findByTramite(tramite)
//        def hijos
//
//        if (params.tipo == 'archivar') {
//            hijos = Tramite.findAllByPadre(tramite)
//        } else if (params.tipo == 'anular') {
//            def padre = tramite
//            hijos = todaDescendencia(tramite)
//        }
//        return [tramite: tramite, observacion: observacion, hijos: hijos, params: params]

        //nuevo
//        def tramite = Tramite.get(params.id)
//        println "rev hijos "+params
        def pxt = PersonaDocumentoTramite.get(params.id)
        def hijos = []
        if (params.tipo == 'archivar') {
            if (pxt.departamento) {
                hijos = Tramite.findAllByPadreAndDeDepartamento(pxt.tramite, pxt.departamento)
            } else {
                hijos = Tramite.findAllByPadreAndDe(pxt.tramite, pxt.persona)
            }
        } else if (params.tipo == 'anular') {
            if (pxt.departamento) {
                hijos = todaDescendenciaExtended(pxt.tramite, 'dep', pxt.departamento)
            } else {
                hijos = todaDescendenciaExtended(pxt.tramite, 'per', pxt.persona)
            }
        }
        [pxt: pxt, hijos: hijos]
    }


    def archivar() {

        println("params" + params)

        def persona = Persona.get(session.usuario.id)
        def pdt = PersonaDocumentoTramite.get(params.id)
        def estadoTramite = EstadoTramite.findByCodigo('E005')
        pdt.estado = estadoTramite
        pdt.fechaArchivo = new Date();
        pdt.observaciones = (pdt.observaciones ?: "") + " Archivado por ${persona.login} el ${new Date().format('dd-MM-yyyy HH:mm')}: " + params.texto + ";"
        if (pdt.rolPersonaTramite.codigo == "R001") {
            pdt.tramite.observaciones = (pdt.tramite.observaciones ?: "") + " Archivado por ${persona.login} el ${new Date().format('dd-MM-yyyy HH:mm')}: " + params.texto + ";"
            pdt.tramite.save()
        } else {
            pdt.tramite.observaciones = (pdt.tramite.observaciones ?: "") + " COPIA Archivada por ${persona.login} el ${new Date().format('dd-MM-yyyy HH:mm')}: " + params.texto + ";"
            pdt.tramite.save()
        }
        if (!pdt.save(flush: true)) {
            render("no")
        } else {
            render("ok")
        }
    }


    def anular() {

//        print("params anular" + params)

        def persona = Persona.get(session.usuario.id)
        def tramite = Tramite.get(params.id)
        def estadoTramite = EstadoTramite.findByCodigo('E006')
        def hijos = todaDescendencia(tramite)

        tramite.estadoTramite = estadoTramite
        def observacion = new ObservacionTramite()
        observacion.persona = persona
        observacion.tramite = tramite
        observacion.fecha = new Date()
        observacion.observaciones = params.texto
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
                observacionHijos.observaciones = "Trámite padre anulado:" + tramite?.codigo + "observaciones originales:" + params.texto
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
//        println("params" + params)
        def tramite = Tramite.get(params.id)
        def persona = Persona.get(session.usuario.id)
        def condifencial = tramite?.tipoTramite?.id

//        println("para:" + tramite.getPara().persona.id)
//        println("persona:" + persona.id)

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
