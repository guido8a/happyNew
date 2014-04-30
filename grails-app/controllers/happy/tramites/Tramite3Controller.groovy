package happy.tramites

import groovy.time.TimeCategory
import happy.alertas.Alerta
import happy.seguridad.Persona

class Tramite3Controller extends happy.seguridad.Shield {

    def diasLaborablesService

    def save() {

        def persona = Persona.get(session.usuario.id)
        def estadoTramiteBorrador = EstadoTramite.findByCodigo("E001");

        def paramsOrigen = params.remove("origen")
        def paramsTramite = params.remove("tramite")

        def tipoTramite
        if (params.confi == "on") {
            tipoTramite = TipoTramite.findByCodigo("C")
        } else {
            tipoTramite = TipoTramite.findByCodigo("N")
        }
        paramsTramite.tipoTramite = tipoTramite
        if (params.anexo == "on") {
            paramsTramite.anexo = 1
        } else {
            paramsTramite.anexo = 0
        }

        if (params.paraExt) {
            paramsTramite.paraExterno = params.paraExt
        } else {
            paramsTramite.paraExterno = null
        }

        paramsTramite.de = persona
        paramsTramite.estadoTramite = estadoTramiteBorrador
        if (paramsTramite.id) {
            paramsTramite.fechaModificacion = new Date()
        } else {
            paramsTramite.fechaCreacion = new Date()
            paramsTramite.anio = Anio.findByNumero(paramsTramite.fechaCreacion.format("yyyy"))
            def num = 1
            Numero objNum
            def numero = Numero.withCriteria {
                eq("departamento", persona.departamento)
                eq("tipoDocumento", TipoDocumento.get(paramsTramite.tipoDocumento.id))
                order("valor", "desc")
            }
            if (numero.size() == 0) {
                objNum = new Numero([
                        departamento : persona.departamento,
                        tipoDocumento: TipoDocumento.get(paramsTramite.tipoDocumento.id)
                ])
            } else {
                objNum = numero.first()
                num = objNum.valor + 1
            }
            objNum.valor = num
            if (!objNum.save(flush: true)) {
                println "Error al crear Numero: " + objNum.errors
            }
            paramsTramite.numero = num
            paramsTramite.codigo = TipoDocumento.get(paramsTramite.tipoDocumento.id).codigo + "-" + num + "-" + persona.departamento.codigo + "-" + paramsTramite.anio.numero[2..3]
        }
        def tramite
        def error = false
        if (paramsTramite.id) {
            tramite = Tramite.get(paramsTramite.id)
            if (tramite.padre && tramite.padre.tipoTramite.codigo == "C") {
                tramite.tipoTramite = TipoTramite.findByCodigo("C")
            }
        } else {
            tramite = new Tramite()
        }
        tramite.properties = paramsTramite

        if (!tramite.save(flush: true)) {
            println "error save tramite " + tramite.errors
            flash.tipo = "error"
            flash.message = "Ha ocurrido un error al grabar el tramite, por favor, verifique la información ingresada"
            redirect(controller: 'tramite', action: "crearTramite", id: tramite.id)
            return
        } else {
            if (tramite.padre) {
                tramite.padre.estado = "C"
                tramite.padre.save(flush: true)
            }
            /*
             * para/cc: si es negativo el id > es a la bandeja de entrada del departamento
             *          si es positivo es una persona
             */
            if (paramsTramite.para || tramite.tipoDocumento.codigo == "OFI") {
                def rolPara = RolPersonaTramite.findByCodigo('R001')
                def para
                if (paramsTramite.para) {
                    para = paramsTramite.para.toInteger()
                } else {
                    para = session.usuario.id
                }
                def paraDocumentoTramite = PersonaDocumentoTramite.withCriteria {
                    eq("tramite", tramite)
                    eq("rolPersonaTramite", rolPara)
                }
                if (paraDocumentoTramite.size() == 0) {
                    paraDocumentoTramite = new PersonaDocumentoTramite([
                            tramite          : tramite,
                            rolPersonaTramite: rolPara
                    ])
                } else if (paraDocumentoTramite.size() == 1) {
                    paraDocumentoTramite = paraDocumentoTramite.first()
                } else {
                    paraDocumentoTramite.each {
                        it.delete(flush: true)
                    }
                    paraDocumentoTramite = new PersonaDocumentoTramite([
                            tramite          : tramite,
                            rolPersonaTramite: rolPara
                    ])
                }
                if (para > 0) {
                    //persona
                    paraDocumentoTramite.persona = Persona.get(para)
                    paraDocumentoTramite.departamento = null
                } else {
                    //departamento
                    paraDocumentoTramite.persona = null
                    paraDocumentoTramite.departamento = Departamento.get(para * -1)
                }
                if (!paraDocumentoTramite.save(flush: true)) {
                    println "error para: " + paraDocumentoTramite.errors
                }
            } else {
                def paraOld = PersonaDocumentoTramite.withCriteria {
                    eq("tramite", tramite)
                    eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R001'))
                }
                if (paraOld.size() > 0) {
                    println "Habian ${paraOld.size()} paras que fueron borrados"
                    paraOld.each {
                        it.delete(flush: true)
                    }
                }
            }
            def rolCc = RolPersonaTramite.findByCodigo('R002')

            PersonaDocumentoTramite.withCriteria {
                eq("tramite", tramite)
                eq("rolPersonaTramite", rolCc)
            }.each {
                it.delete(flush: true)
            }

            if (paramsTramite.hiddenCC.toString().size() > 0) {
                (paramsTramite.hiddenCC.split("_")).each { cc ->
                    def ccDocumentoTramite = new PersonaDocumentoTramite([
                            tramite          : tramite,
                            rolPersonaTramite: rolCc
                    ])
                    if (cc.toInteger() > 0) {
                        //persona
                        ccDocumentoTramite.persona = Persona.get(cc.toInteger())
                    } else {
                        //departamento
                        ccDocumentoTramite.departamento = Departamento.get(cc.toInteger() * -1)
                    }
                    if (!ccDocumentoTramite.save(flush: true)) {
                        println "error cc: " + ccDocumentoTramite.errors
                    }
                }
            }
//            if (params.cc == "on") {
            def tipoDoc
            if (paramsTramite.id) {
                tipoDoc = tramite.tipoDocumento
            } else {
                tipoDoc = TipoDocumento.get(paramsTramite.tipoDocumento.id)
            }
            if (tipoDoc.codigo == "DEX") {
                paramsOrigen.tramite = tramite
                paramsOrigen.fecha = new Date()
                def origen = OrigenTramite.findAllByCedula(paramsOrigen.cedula)
                if (origen.size() == 0) {
                    origen = new OrigenTramite(paramsOrigen)
                } else {
                    println "Hay ${origen.size()} filas de origenTramite con cedula ${paramsOrigen.cedula}"
                    origen = origen.first()
                    origen.properties = paramsOrigen
                }
                if (!origen.save(flush: true)) {
                    println "error origen tramite: " + origen.errors
                }
                tramite.origenTramite = origen
                if (!tramite.save(flush: true)) {
                    println "ERROR AAAAA: " + tramite.errors
                }
            } else {
                if (tipoDoc.codigo != "OFI") {
                    def origen = OrigenTramite.findAllByCedula(paramsOrigen.cedula)
                    if (origen.size() > 0) {
                        println "Hay ${origen.size()} filas de origenTramite con cedula ${paramsOrigen.cedula} que se eliminan"
                        origen.each {
                            it.delete(flush: true)
                        }
                    }
                }
            }
        }

        if (tramite.tipoDocumento.codigo == "SUM") {
            redirect(controller: "tramite2", action: "bandejaSalida", id: tramite.id)
        } else {
            if (params.anexo == "on") {
                redirect(controller: "documentoTramite", action: "anexo", id: tramite.id)
            } else {
//            redirect(controller: "tramite", action: "redactar", id: tramite.id)
                redirect(controller: "tramite", action: "redactar", id: tramite.id)
            }
        }
    }

    def verTramite() {
        def tramite = Tramite.get(params.id)

        def primerTramite = tramite
        while (primerTramite.padre) {
            primerTramite = primerTramite.padre
        }

        def html = creaHtmlVer(primerTramite, true)
        return [html: html]
    }


    def creaHtmlVer(Tramite tramite, boolean inicial) {
        def enter = "\n"
        def html = "<div class=\"panel panel-${inicial ? 'primary' : 'info'}\">" + enter
        def de = tramite.de.departamento.descripcion
        if (tramite.fechaEnvio) {
            de += " (enviado el " + tramite.fechaEnvio.format("dd-MM-yyyy HH:mm") + ")"
        }
        def trPara = tramite.para
        def trCc = tramite.copias
        def para = trPara.departamento ? trPara.departamento.descripcion : trPara.persona.nombre + ' ' + trPara.persona.apellido
        if (trPara.fechaRecepcion) {
            para += " (recibido el " + trPara.fechaRecepcion.format("dd-MM-yyyy HH:mm") + ")"
        }
        def cc = ""
        trCc.each { c ->
            if (cc != "") {
                cc += ", "
            }
            cc += c.persona ? c.persona.nombre + ' ' + c.persona.apellido : c.departamento.descripcion
            if (c.fechaRecepcion) {
                cc += " (recibido el " + c.fechaRecepcion.format("dd-MM-yyyy HH:mm") + ")"
            }
        }
        def hijos = Tramite.findAllByPadre(tramite)
        html += "   <div class=\"panel-heading\">" + enter
        html += "       <h3 class=\"panel-title\">${tramite.codigo}: ${tramite.asunto}</h3>" + enter
        html += "       <div>De: ${de}</div>" + enter
        html += "       <div>Para: ${para}</div>" + enter
        if (cc != "") {
            html += "       <div>CC: ${cc}</div>" + enter
        }
        if (tramite.observaciones && tramite.observaciones != "") {
            html += "       <div>Obs.: ${tramite.observaciones}</div>" + enter
        }
        if (hijos.size() > 0) {
            html += "       <div class='show'>Ver ${hijos.size()} trámite${hijos.size() == 1 ? '' : 's'} derivado${hijos.size() == 1 ? '' : 's'}</div>" + enter
        }
        html += "   </div>" + enter

        html += "<div class=\"panel-body hide\">" + enter
        hijos.each { h ->
            html += creaHtmlVer(h, false)
        }
        html += "</div>" + enter
        html += "</div>" + enter
        return html
    }

    def seguimientoTramite() {
//        println("params:" + params)
        def tramite = Tramite.get(params.id)

        def primerTramite = tramite
        while (primerTramite.padre) {
            primerTramite = primerTramite.padre
        }

        def html = ""

        html += "<table class='table table-bordered table-condensed'>"
        html += "<thead>"
        html += "<tr>"
        html += "<th>N. trámite</th>"
        html += "<th>Fecha</th>"
        html += "<th>De</th>"
        html += "<th>Creado por</th>"
        html += "<th>Para</th>"
        html += "<th>Prioridad</th>"
        html += "<th>Fecha límite</th>"
//        html += "<th>En trámite</th>"
//        html += "<th>Recibido</th>"
        html += "<th>Recepción</th>"
//        html += "<th>Anulado</th>"
        html += "<th>Estado</th>"
        html += "</tr>"
        html += "</thead>"
        html += "<tbody>"
        html += creaHtmlSeguimiento(primerTramite, tramite, "62, 100, 141")
        html += "</tbody>"
        html += "</table>"

        return [tramite: primerTramite, html: html, selected: tramite, params: params]
    }

    def creaHtmlSeguimiento(Tramite tramite, Tramite selected, String colorAnterior) {
        def partsColor = colorAnterior.split(",")
        def nr = partsColor[0].toInteger() + 10
        def ng = partsColor[1].toInteger() + 10
        def nb = partsColor[2].toInteger() + 10
        def nc = nr + "," + ng + "," + nb
        def html = ""
//        def hijos = Tramite.findAllByPadreAndFechaEnvioIsNotNull(tramite)
        def hijos = Tramite.findAllByPadre(tramite)
        hijos.each { h ->
            def hijos2 = Tramite.countByPadreAndFechaEnvioIsNotNull(h)
            def style = ""
            if (hijos2 > 0) {
                style = " style='background: rgb(${nc})' "
            }
            html += "<tr ${style} class='hijo ${hijos2 > 0 ? 'padre' : ''} ${h == selected ? 'current' : ''}' " +
                    "data-id='${h.id}' data-asunto='${h.asunto}' data-observaciones='${h.observaciones}'>"
            html += "<td>${h.codigo}</td>"
            html += "<td>${h.fechaEnvio ? h.fechaEnvio.format('dd-MM-yyyy HH:mm') : 'no enviado'}</td>"
            html += "<td title='${h.de.departamento.descripcion}'>${h.de.departamento.codigo}</td>"
            html += "<td title='${h.de.nombre + ' ' + h.de.apellido}'>${h.de.login}</td>"
            html += "<td title='${h.para.persona ? h.para.persona.nombre + ' ' + h.para.persona.apellido : h.para.departamento.descripcion}'>" +
                    "${h.para.persona ? h.para.persona.login : h.para.departamento.codigo}</td>"
            html += "<td>${h.prioridad.descripcion}</td>"
            html += "<td>${h.fechaMaximoRespuesta ? h.fechaMaximoRespuesta.format('dd-MM-yyyy HH:mm') : 'no recibido'}</td>"
            html += "<td>${h.para.fechaRecepcion ? h.para.fechaRecepcion.format('dd-MM-yyyy HH:mm') : 'no recibido'}</td>"
            html += "<td>${h.estadoTramite.descripcion}</td>"
            html += "</tr>"
            creaHtmlSeguimiento(h, selected, nc)
        }
        return html
    }

    def bandejaEntradaDpto() {
        def usu = Persona.get(session.usuario.id)
        def triangulo = PermisoTramite.findByCodigo("E001")
        def bloqueo = false
//        if (session.departamento.estado == "B") {
//            bloqueo = true
//        }
        def tienePermiso = PermisoUsuario.withCriteria {
            eq("persona", usu)
            eq("permisoTramite", triangulo)
            le("fechaInicio", new Date())
            or {
                ge("fechaFin", new Date())
                isNull("fechaFin")
            }
        }

        if (tienePermiso.size() == 0) {
//            flash.message = "El usuario no tiene los permisos necesarios para acceder a la bandeja de entrada del departamento. Ha sido redireccionado a su bandeja de entrada personal."
//            flash.tipo = "error"
            redirect(controller: "tramite", action: "bandejaEntrada")
            return
        }
        return [persona: usu, bloqueo: bloqueo]
    }


    def detalles() {
        def tramite = Tramite.get(params.id)
        def tramites = []
        def principal = null
        tramites.add(tramite)
        if (tramite.padre) {
            principal = tramite.padre
            while (true) {
                tramites.add(principal)
                if (!principal.padre)
                    break
                else {
                    principal = principal.padre
                }

            }
        }
        tramites = tramites.reverse()
        return [tramite: tramite, principal: principal, tramites: tramites]
    }


    def tablaBandejaEntradaDpto() {

        params.domain = params.domain ?: "persDoc"
        params.sort = params.sort ?: "fechaEnvio"
        params.order = params.order ?: "desc"

        def persona = Persona.get(session.usuario.id)
        def departamento = persona?.departamento

        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
//        def rolImprimir = RolPersonaTramite.findByCodigo('I005');

        def pxtPara = PersonaDocumentoTramite.withCriteria {
            eq("departamento", departamento)
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
            eq("departamento", departamento)
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
//        def pxtImprimir = PersonaDocumentoTramite.withCriteria {
//            eq("departamento", departamento)
//            eq("rolPersonaTramite", rolImprimir)
//            isNotNull("fechaEnvio")
//            tramite {
//                or {
//                    eq("estadoTramite", EstadoTramite.findByCodigo("E003")) //enviado
//                    eq("estadoTramite", EstadoTramite.findByCodigo("E007")) //enviado al jefe
//                    eq("estadoTramite", EstadoTramite.findByCodigo("E004")) //recibido
//                }
//            }
//        }

        def pxtTodos = pxtPara
        pxtTodos += pxtCopia
        if (params.domain == "persDoc") {
            pxtTodos.sort { it[params.sort] }
        } else if (params.domain == "tramite") {
            pxtTodos.sort { it.tramite[params.sort] }
        }
        if (params.order == "desc") {
            pxtTodos = pxtTodos.reverse()
        }
        def ahora = new Date()

//        println("tramites:" + pxtTodos)
//        println("domain:" + params.domain)

        def tramitesSinHijos = []

        pxtTodos.each { tr ->
            if (Tramite.countByPadre(tr.tramite) == 0) {
                tramitesSinHijos += tr
            }
        }

        return [persona: persona, tramites: tramitesSinHijos, ahora: ahora, params: params]
    }

//    def recibir() {
//        def tramite = Tramite.get(params.id)
//        return [tramite: tramite]
//    }

    def recibirTramite() {
        def persona = Persona.get(session.usuario.id)

        def tramite = Tramite.get(params.id)
        def porEnviar = EstadoTramite.findByCodigo("E001")
        def enviado = EstadoTramite.findByCodigo("E003")
        def recibido = EstadoTramite.findByCodigo("E004")
        //tambien puede recibir si ya esta en estado recibido (se pone en recibido cuando recibe el PARA)
        if (tramite.estadoTramite != enviado && tramite.estadoTramite != recibido) {
            render "ERROR_Se ha cancelado el envío.<br/>Este trámite no puede ser gestionado."
            return
        }

        def paraDpto = tramite.para?.departamento
        def paraPrsn = tramite.para?.persona
        def esCircular = false
        if (!paraPrsn && !paraDpto) {
            esCircular = true
        }

        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCC = RolPersonaTramite.findByCodigo("R002")
        def rolImprimir = RolPersonaTramite.findByCodigo("I005")

        def estadoRecibido = EstadoTramite.findByCodigo('E004') //recibido
        def pxt = PersonaDocumentoTramite.withCriteria {
            eq("tramite", tramite)
            if (!esCircular) {
                if (paraDpto) {
                    eq("departamento", persona.departamento)
                } else if (paraPrsn) {
                    eq("persona", persona)
                }
            } else {
                or {
                    eq("departamento", persona.departamento)
                    eq("persona", persona)
                }
            }
            or {
                eq("rolPersonaTramite", rolPara)
                eq("rolPersonaTramite", rolCC)
                eq("rolPersonaTramite", rolImprimir)
            }
        }//PersonaDocumentoTramite.findByTramiteAndDepartamento(tramite, persona.departamento)

//        println "tramite: " + tramite
//        println "paraDpto: " + paraDpto
//        println "paraPrsn: " + paraPrsn
//        println "rolPara: " + rolPara
//        println "rolCC: " + rolCC
//        println "rolImprimir: " + rolImprimir


        if (pxt.size() > 1) {
            flash.message = "ERROR"
            println "mas de 1 PDT: ${pxt}"
            redirect(action: "errores")
            return
        } else if (pxt.size() == 0) {
            flash.message = "ERROR"
            println "0 PDT"
            redirect(action: "errores")
        } else {
            pxt = pxt.first()
        }

        if (paraDpto && persona.departamentoId == paraDpto.id) {
            tramite.estadoTramite = estadoRecibido
        }
        if (paraPrsn && persona.id == paraPrsn.id) {
            tramite.estadoTramite = estadoRecibido
        }

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

        if (pxt.save(flush: true) && tramite.save(flush: true)) {
            def pdt = new PersonaDocumentoTramite([
                    tramite             : tramite,
                    persona             : persona,
                    rolPersonaTramite   : RolPersonaTramite.findByCodigo("E003"),
                    fechaRecepcion      : hoy,
                    fechaLimiteRespuesta: limite
            ])
            def alerta
            if (pxt.departamento) {
                alerta = Alerta.findByDepartamentoAndTramite(pxt.departamento, pxt.tramite)
            } else {
                alerta = Alerta.findByPersonaAndTramite(pxt.persona, pxt.tramite)
            }
            if (alerta) {
                if (!alerta.fechaRecibido) {
                    alerta.mensaje += " - Recibido"
                    alerta.fechaRecibido = new Date()
                    alerta.save(flush: true)
                }
            }
            if (pdt.save(flush: true)) {
                render "OK_Trámite recibido correctamente"
            } else {
                println pdt.errors
                render "NO_Ocurrió un error al recibir"
            }
        } else {
            println pxt.errors
            println tramite.errors
            render "NO_Ocurrió un error al recibir"
        }
    }

    def enviarTramiteJefe() {
        def tramite = Tramite.get(params.id)
        def obs = params.obs

        def persona = Persona.get(session.usuario.id)

        tramite.observaciones = (tramite.observaciones ? tramite.observaciones + "; " : "") + persona.login + " (" + (new Date().format("dd-MM-yyyy HH:mm")) + "): " + obs
//        tramite.estadoTramite = EstadoTramite.findByCodigo("E007")

        if (tramite.save(flush: true)) {
            render "OK_Observaciones agregadas exitosamente"
        } else {
            println tramite.errors
            render "NO_Ha ocurrido un error al agregar las observaciones: " + renderErrors(bean: tramite)
        }
    }

    def errores() {
        return [params: params]
    }


    def busquedaBandeja() {

        params.domain = params.domain ?: "persDoc"
        params.sort = params.sort ?: "fechaEnvio"
        params.order = params.order ?: "desc"

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def departamento = persona?.departamento

        def pxtPara = PersonaDocumentoTramite.withCriteria {
            eq("departamento", departamento)
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
            eq("departamento", departamento)
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


        def pxtTodos = pxtPara
        pxtTodos += pxtCopia
        def pxtTramites = pxtTodos

        if (params.domain == "persDoc") {
            pxtTramites.sort { it[params.sort] }
        } else if (params.domain == "tramite") {
            pxtTramites.sort { it.tramite[params.sort] }
        }
        if (params.order == "desc") {
            pxtTramites = pxtTramites.reverse()
        }

        //busqueda
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
//        println("dominio2:" + params.domain)

//        return [tramites: res, pxtTramites: pxtTramites, idTramitesRetrasados: idTramitesRetrasados, idTramitesRecibidos: idTramitesRecibidos, idRojos: idRojos]
        return [tramites: res, pxtTramites: pxtTramites]

    }

    def archivadosDpto() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)

        return [persona: persona, si: params.dpto]


    }

    def tablaArchivadosDep() {

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
//            println("-->" + it?.tramite?.deDepartamento?.id)
            if (it?.tramite?.estadoTramite?.codigo == 'E005' && it?.tramite?.deDepartamento?.id != null) {
//                println("entro!!!")
                pxtTramites.add(it)
            }
        }

//        println("px" + pxtTramites)


        return [tramites: pxtTramites]


    }

}
