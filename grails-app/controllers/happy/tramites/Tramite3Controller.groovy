package happy.tramites

import groovy.time.TimeCategory
import happy.seguridad.Persona

class Tramite3Controller extends happy.seguridad.Shield {
    def save() {
        def persona = Persona.get(session.usuario.id)
        def estadoTramiteBorrador = EstadoTramite.findByCodigo("E001");

        def paramsOrigen = params.remove("origen")
        def paramsTramite = params.remove("tramite")

        paramsTramite.de = persona
        paramsTramite.estadoTramite = estadoTramiteBorrador
        paramsTramite.fechaCreacion = new Date()
        paramsTramite.anio = Anio.findByNumero(paramsTramite.fechaCreacion.format("yyyy"))
        /* CODIGO DEL TRAMITE:
         *      tipoDoc.codigo-secuencial-dtpoEnvia.codigo-anio(yy)
         *      INF-1-DGCP-14       MEM-10-CEV-13
         */
        //el numero del ultimo tramite del anio, por tipo doc y dpto
        def num = Tramite.withCriteria {
            eq("anio", paramsTramite.anio)
            eq("tipoDocumento", TipoDocumento.get(paramsTramite.tipoDocumento.id))
            de {
                eq("departamento", persona.departamento)
            }
            projections {
                max "numero"
            }
        }
        if (num && num.size() > 0) {
            num = num.first()
        } else {
            num = 0
        }
        if (!num) {
            num = 0
        }
        num = num + 1
        paramsTramite.numero = num
        paramsTramite.codigo = TipoDocumento.get(paramsTramite.tipoDocumento.id).codigo + "-" + num + "-" + persona.departamento.codigo + "-" + paramsTramite.anio.numero[2..3]

        def tramite
        def error = false
        if (paramsTramite.id) {
            tramite = Tramite.get(paramsTramite.id)
        } else {
            tramite = new Tramite()
        }
        tramite.properties = paramsTramite

        if (!tramite.save(flush: true)) {
            println "error save tramite " + tramite.errors
            flash.tipo = "error"
            flash.message = "Ha ocurrido un error al grabar el tramite, por favor, verifique la información ingresada"
            redirect(action: "crearTramite")
            return
        } else {

            /*
             * para/cc: si es negativo el id > es a la bandeja de entrada del departamento
             *          si es positivo es una persona
             */
            if (paramsTramite.para) {
                def para = paramsTramite.para.toInteger()
                def paraDocumentoTramite = new PersonaDocumentoTramite([
                        tramite: tramite,
                        rolPersonaTramite: RolPersonaTramite.findByCodigo('R001')
                ])
                if (para > 0) {
                    //persona
                    paraDocumentoTramite.persona = Persona.get(para)
                } else {
                    //departamento
                    paraDocumentoTramite.departamento = Departamento.get(para * -1)
                }
                if (!paraDocumentoTramite.save(flush: true)) {
                    println "error para: " + paraDocumentoTramite.errors
                }
            }
            if (paramsTramite.hiddenCC.toString().size() > 0) {
                (paramsTramite.hiddenCC.split("_")).each { cc ->
                    def ccDocumentoTramite = new PersonaDocumentoTramite([
                            tramite: tramite,
                            rolPersonaTramite: RolPersonaTramite.findByCodigo('R002')
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
            def tipoDoc = TipoDocumento.get(params.tramite.tipoDocumento.id)
            if (tipoDoc.codigo == "DEX") {
                paramsOrigen.tramite = tramite
                paramsOrigen.fecha = paramsTramite.fechaCreacion
                def origen = new OrigenTramite(paramsOrigen)
                if (!origen.save(flush: true)) {
                    println "error origen tramite: " + origen.errors
                }
            }

        }
        redirect(controller: "tramite", action: "redactar", id: tramite.id)
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
            html += "<td title='${h.de.nombre + ' ' + h.de.apellido}'>${h.de.sigla}</td>"
            html += "<td title='${h.para.persona ? h.para.persona.nombre + ' ' + h.para.persona.apellido : h.para.departamento.descripcion}'>" +
                    "${h.para.persona ? h.para.persona.sigla : h.para.departamento.codigo}</td>"
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
        def tienePermiso = PermisoUsuario.withCriteria {
            eq("persona", usu)
            eq("permisoTramite", triangulo)
            lt("fechaInicio", new Date())
            or {
                gt("fechaFin", new Date())
                isNull("fechaFin")
            }
        }
        if (tienePermiso.size() == 0) {
            redirect(controller: "tramite", action: "bandejaEntrada")
            return
        }
        return [persona: usu]
    }

    def tablaBandejaEntradaDpto() {
        def persona = Persona.get(session.usuario.id)
        def departamento = persona?.departamento

        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def rolImprimir = RolPersonaTramite.findByCodigo('I005');

        def pxtTodos

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
        def pxtImprimir = PersonaDocumentoTramite.withCriteria {
            eq("departamento", departamento)
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
        return [persona: persona, tramites: pxtTodos]
    }

    def recibirTramite() {
        def persona = Persona.get(session.usuario.id)

        def tramite = Tramite.get(params.id)
        def para = tramite.para.departamento

        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCC = RolPersonaTramite.findByCodigo("R002")
        def rolImprimir = RolPersonaTramite.findByCodigo("I005")


        def estado = EstadoTramite.findByCodigo('E004') //recibido
        def pxt = PersonaDocumentoTramite.withCriteria {
            eq("tramite", tramite)
            eq("departamento", persona.departamento)
            or {
                eq("rolPersonaTramite", rolPara)
                eq("rolPersonaTramite", rolCC)
                eq("rolPersonaTramite", rolImprimir)
            }
        }//PersonaDocumentoTramite.findByTramiteAndDepartamento(tramite, persona.departamento)

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

        if (persona.departamentoId == para.id) {
            tramite.estadoTramite = estado
        }

        def hoy = new Date()
        def limite = hoy
        use(TimeCategory) {
            limite = limite + tramite.prioridad.tiempo.hours
        }

        pxt.fechaRecepcion = hoy
        pxt.fechaLimiteRespuesta = limite

        if (pxt.save(flush: true) && tramite.save(flush: true)) {
            def pdt = new PersonaDocumentoTramite([
                    tramite: tramite,
                    persona: persona,
                    rolPersonaTramite: RolPersonaTramite.findByCodigo("E003"),
                    fechaRecepcion: hoy,
                    fechaLimiteRespuesta: limite
            ])
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

        tramite.observaciones = obs
        tramite.estadoTramite = EstadoTramite.findByCodigo("E007")

        if (tramite.save(flush: true)) {
            render "OK_Trámite enviado al jefe"
        } else {
            println tramite.errors
            render "NO_Ha ocurrido un error al enviar el trámite al jefe"
        }
    }

    def errores() {
        return [params: params]
    }
}
