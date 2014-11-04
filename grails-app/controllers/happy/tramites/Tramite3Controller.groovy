package happy.tramites

import groovy.time.TimeCategory
import happy.alertas.Alerta
import happy.seguridad.Persona

class Tramite3Controller extends happy.seguridad.Shield {

    def diasLaborablesService
    def tramitesService

    def save() {

//        println("params " + params)

        if(params.tramite.aQuienContesta.id){
          if(PersonaDocumentoTramite.get(params.tramite.aQuienContesta.id).estado.codigo == 'E003' || PersonaDocumentoTramite.get(params.tramite.aQuienContesta.id).estado.codigo == 'E005' || PersonaDocumentoTramite.get(params.tramite.aQuienContesta.id).estado.codigo == 'E006'  ){
            flash.tipo = "error"
            flash.message = "Ha ocurrido un error al grabar el tramite"
            redirect(controller: 'tramite', action: "bandejaEntrada")
            return
        }
        }

        def persona = Persona.get(session.usuario.id)
        def estadoTramiteBorrador = EstadoTramite.findByCodigo("E001");

        def paramsOrigen = params.remove("origen")
        def paramsTramite = params.remove("tramite")
        println "aaa " + paramsTramite.aQuienContesta.id

        if (paramsTramite.padre.id) {
            def padre = Tramite.get(paramsTramite.padre.id)


        }

//        println params
//        println paramsTramite
//        println paramsOrigen

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
        if (params.externo == "on") {
            paramsTramite.externo = 1
        } else {
            paramsTramite.externo = 0
        }

        if (paramsTramite.externo == '1' || paramsTramite.externo == 1) {
            paramsTramite.estadoTramiteExterno = EstadoTramiteExterno.findByCodigo("EX03") //pendiente
        }

        if (params.paraExt) {
            paramsTramite.paraExterno = params.paraExt
        } else {
            paramsTramite.paraExterno = null
        }
        if (params.paraExt2) {
            paramsTramite.paraExterno = params.paraExt2
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
        def aqc
        if (paramsTramite.id) {
            tramite = Tramite.get(paramsTramite.id)
            aqc = tramite.aQuienContesta
//            if (tramite.padre && tramite.padre.tipoTramite.codigo == "C") {
//                tramite.tipoTramite = TipoTramite.findByCodigo("C")
//            }
        } else {
            tramite = new Tramite()
            /*aqui validaciones de numero de hijos*/
            if (paramsTramite.aQuienContesta.id) {
                if (paramsTramite.esRespuesta == 1 || paramsTramite.esRespuesta == '1') {
                    //println "entro aqui"
                    def pdt = PersonaDocumentoTramite.get(paramsTramite.aQuienContesta.id)
                    //println "dpt "+pdt
                    def hijos = Tramite.findAllByAQuienContestaAndEstadoNotEqual(pdt, EstadoTramite.findByCodigo("E006"))
                    def tiene = false
                    hijos.each { h ->
//                        println "hijo -> "+h
                        PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteInList(h, [RolPersonaTramite.findByCodigo("E001"), RolPersonaTramite.findByCodigo("E002")]).each { pq ->
//                            println "pq "+pq.estado?.descripcion
                            if (pq.estado?.codigo != "E006")
                                tiene = true
                        }
                    }
//                    println hijos
                    if (tiene) {
                        flash.message = "Ya ha realizado una respuesta a este trámite."
                        redirect(controller: 'tramite', action: "errores")
                        return
                    }
                }
            }


        }
//        println "ANTES DEL SAVE " + paramsTramite

        tramite.properties = paramsTramite
        if (tramite.tipoDocumento.codigo == "DEX")
            tramite.estadoTramiteExterno = EstadoTramiteExterno.findByCodigo("E001")

        def externos = ["DEX", "OFI"]
        if (externos.contains(tramite.tipoDocumento.codigo)) {
            tramite.externo = '1'
        }
        tramite.departamento = tramite.de.departamento
        if (tramite.aQuienContesta == null)
            tramite.aQuienContesta = aqc
        if (!tramite.save(flush: true)) {
            println "error save tramite " + tramite.errors
            flash.tipo = "error"
            flash.message = "Ha ocurrido un error al grabar el tramite, por favor, verifique la información ingresada"
            redirect(controller: 'tramite', action: "crearTramite", id: tramite.id)
            return
        } else {
//            println "SAVED!!"
//            println "externo? " + paramsTramite.externo
//            println "externo? " + tramite.externo
            if (tramite.externo == 0) {
                def documentos = DocumentoTramite.findAllByTramite(tramite)
                if (documentos.size() > 0) {
                    def ids = documentos.id
                    ids.each { id ->
                        def doc = DocumentoTramite.get(id)
                        def departamento = doc.tramite.deDepartamento
                        if (!departamento) {
                            departamento = doc.tramite.de.departamento
                        }
                        def path = servletContext.getRealPath("/") + "anexos/${departamento.codigo}/" + doc.tramite.codigo + "/" + doc.path
                        try {
                            doc.delete(flush: true)
                            def file = new File(path)
                            file.delete()
                        } catch (e) {
                            println "Error al eliminar anexo: ${id}: " + e
                        }
                    }
                }
            }

            /*
             * para/cc: si es negativo el id > es a la bandeja de entrada del departamento
             *          si es positivo es una persona
             */
            if (tramite.padre) {
                tramite.padre.estado = "C"
                tramite.aQuienContesta = PersonaDocumentoTramite.get(paramsTramite.aQuienContesta.id)
                if (tramite.aQuienContesta == null)
                    tramite.aQuienContesta = aqc
                else {
                    aqc = tramite.aQuienContesta
                }
                tramite.padre.save(flush: true)
                if (tramite.padre.estadoTramiteExterno) {
                    tramite.estadoTramiteExterno = tramite.padre.estadoTramiteExterno

                }
                tramite.save(flush: true)
            } else {
                //si no tiene padre, es create y no llegó parámetro de trámite principal
                // ponerle el numero de tramite principal
                if (!paramsTramite.id && (!paramsTramite.tramitePrincipal || paramsTramite.tramitePrincipal.toString() == "0")) {
                    tramite.tramitePrincipal = tramite.id
                    tramite.save(flush: true)
                }
            }
            def tram = Tramite.lock(tramite.id)
//            println "DESPUES1: " + tramite.aQuienContesta
//            println "DESPUES1: " + tramite.aQuienContesta.id
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
//                println "DESPUES2: " + tramite.aQuienContesta
//                println "DESPUES2: " + tramite.aQuienContesta.id
                println "pdt para " + paraDocumentoTramite
                if (paraDocumentoTramite.size() == 0) {
                    paraDocumentoTramite = new PersonaDocumentoTramite()
                    paraDocumentoTramite.tramite = tram
                    paraDocumentoTramite.rolPersonaTramite = rolPara
//                    println "DESPUES2.5: " + tramite.aQuienContesta
//                    println "DESPUES2.5: " + tramite.aQuienContesta.id
                } else if (paraDocumentoTramite.size() == 1) {
                    paraDocumentoTramite = paraDocumentoTramite.first()
                } else {
                    paraDocumentoTramite.each {
//                        println "delete "+it.id
                        it.delete(flush: true)
                    }
                    paraDocumentoTramite = new PersonaDocumentoTramite()
                    paraDocumentoTramite.tramite = tram
                    paraDocumentoTramite.rolPersonaTramite = rolPara
                }
//                println "DESPUES3: " + tramite.aQuienContesta
//                println "DESPUES3: " + tramite.aQuienContesta.id
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
//                println "DESPUES4: " + tramite.aQuienContesta
//                println "DESPUES4: " + tramite.aQuienContesta.id
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
//            println "DESPUES dp: " + tramite.aQuienContesta
//            println "DESPUES dp: " + tramite.aQuienContesta.id

            def rolCc = RolPersonaTramite.findByCodigo('R002')

            PersonaDocumentoTramite.withCriteria {
                eq("tramite", tramite)
                eq("rolPersonaTramite", rolCc)
            }.each {
                it.delete(flush: true)
            }

            if (paramsTramite.hiddenCC.toString().size() > 0) {
                (paramsTramite.hiddenCC.split("_")).each { cc ->
                    def ccDocumentoTramite = new PersonaDocumentoTramite()
                    ccDocumentoTramite.tramite = tramite
                    ccDocumentoTramite.rolPersonaTramite = rolCc
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
//            println "DESPUES hc: " + tramite.aQuienContesta
//            println "DESPUES hc: " + tramite.aQuienContesta.id
//            if (params.cc == "on") {
            def tipoDoc
            if (paramsTramite.id) {
                tipoDoc = tramite.tipoDocumento
            } else {
                tipoDoc = TipoDocumento.get(paramsTramite.tipoDocumento.id)
            }
            if (tipoDoc.codigo == "DEX") {

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
//                println "****************"
//                println ahora
//                println tramite.prioridad.descripcion
//                println tramite.prioridad.tiempo
//                println limite
//                println "****************"
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
                if (tramite.aQuienContesta == null)
                    tramite.aQuienContesta = aqc
                if (tramite.save(flush: true)) {
//                    def realPath = servletContext.getRealPath("/")
//                    def mensaje = message(code: 'pathImages').toString();
//                    enviarService.crearPdf(tramite, session.usuario, "1", 'download', realPath, mensaje);
                } else {
                    println tramite.errors
//                    msg += "<li>" + renderErrors(bean: tramite) + "<li>"
                }

                if (params.anexo == "on") {
                    redirect(controller: "documentoTramite", action: "anexo", id: tramite.id)
                    return
                } else {
                    redirect(controller: "tramite2", action: "bandejaSalida")
                    return
                }

//                paramsOrigen.tramite = tram
//                paramsOrigen.fecha = new Date()
//                def origen = OrigenTramite.findAllByCedula(paramsOrigen.cedula)
//                if (origen.size() == 0) {
//                    origen = new OrigenTramite(paramsOrigen)
//                } else {
//                    println "Hay ${origen.size()} filas de origenTramite con cedula ${paramsOrigen.cedula}"
//                    origen = origen.first()
//                    origen.properties = paramsOrigen
//                }
//                if (!origen.save(flush: true)) {
//                    println "error origen tramite: " + origen.errors
//                }
//                tramite.origenTramite = origen
//
//                if (!tramite.save(flush: true)) {
//                    println "ERROR AAAAA: " + tramite.errors
//                }
//                println "save mas abajo " + tramite.aQuienContesta
            } else {
                if (tipoDoc.codigo != "OFI") {
//                    def origen = OrigenTramite.findAllByCedula(paramsOrigen.cedula)
//                    if (origen.size() > 0) {
//                        println "Hay ${origen.size()} filas de origenTramite con cedula ${paramsOrigen.cedula} que se eliminan"
//                        origen.each {
//                            it.delete(flush: true)
//                        }
//                    }
                }
            }
            tram.discard()
        }
//
//        println "DESPUES u: " + tramite.aQuienContesta
//        println "DESPUES u: " + tramite.aQuienContesta.id

        if (tramite.tipoDocumento.codigo == "SUM"/* || tramite.tipoDocumento.codigo == "DEX"*/) {
            redirect(controller: "tramite2", action: "bandejaSalida", id: tramite.id)
            return
        } else {
            if (params.anexo == "on") {
                redirect(controller: "documentoTramite", action: "anexo", id: tramite.id)
                return
            } else {
//            redirect(controller: "tramite", action: "redactar", id: tramite.id)
                redirect(controller: "tramite", action: "redactar", id: tramite.id)
                return
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
        println "de " + de
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
//        def triangulo = PermisoTramite.findByCodigo("E001")
        def bloqueo = false
        if (!session.usuario.esTriangulo()) {
            flash.message = "Su perfil (${session.perfil}), no tiene acceso a la bandeja de entrada departamental"
            response.sendError(403)
        }
        return [persona: usu, bloqueo: bloqueo]
    }

    def infoRemitente() {
        def tramite = Tramite.get(params.id)
        return [tramite: tramite]
    }

    def detalles() {
        def tramite = Tramite.get(params.id)
        def tramites = []
        def principal = null
        def rolesNo = [RolPersonaTramite.findByCodigo("E004"), RolPersonaTramite.findByCodigo("E003"), RolPersonaTramite.findByCodigo("I005")]
        if (tramite) {
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
        }
        tramites = tramites.reverse()
        return [tramite: tramite, principal: principal, tramites: tramites, rolesNo: rolesNo]
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

            or {
                eq("estado", EstadoTramite.findByCodigo("E003")) //enviado
                eq("estado", EstadoTramite.findByCodigo("E007")) //enviado al jefe
                eq("estado", EstadoTramite.findByCodigo("E004")) //recibido
            }


        }
        def pxtCopia = PersonaDocumentoTramite.withCriteria {
            eq("departamento", departamento)
            eq("rolPersonaTramite", rolCopia)
            isNotNull("fechaEnvio")

            or {
                eq("estado", EstadoTramite.findByCodigo("E003")) //enviado
                eq("estado", EstadoTramite.findByCodigo("E007")) //enviado al jefe
                eq("estado", EstadoTramite.findByCodigo("E004")) //recibido
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
        def band = false
        def anulado = EstadoTramite.findByCodigo("E006")
        pxtTodos.each { tr ->
            if (!(tr.tramite.tipoDocumento.codigo == "OFI")) {
                band = tramitesService.verificaHijos(tr, anulado)
//            println "estado!!! " + band + "   " + tr.id
                if (!band) {
                    tramitesSinHijos += tr
                }
            }

        }


        return [persona: persona, tramites: tramitesSinHijos, ahora: ahora, params: params]
    }

//    def recibir() {
//        def tramite = Tramite.get(params.id)
//        return [tramite: tramite]
//    }

    def verificarEstado() {
        //println "verifica estado "+params
        def tramite = Tramite.get(params.id)
        def para = tramite.para
        //println "para "+para+"  "+para?.estado?.codigo
        if (!para) {
            render "ok"
            return
        } else {
            if (para.estado?.codigo != "E006") {
                render "ok"
                return
            } else {
                render "error"
                return
            }
        }
    }


    def recibirTramite() {
        println "recibir tramite " + params
        if (request.getMethod() == "POST") {

            def persona = Persona.get(session.usuario.id)

            def tramite = Tramite.get(params.id)
            def porEnviar = EstadoTramite.findByCodigo("E001")
            def enviado = EstadoTramite.findByCodigo("E003")
            def recibido = EstadoTramite.findByCodigo("E004")
            //tambien puede recibir si ya esta en estado recibido (se pone en recibido cuando recibe el PARA)
            // println tramite.estadoTramite.descripcion
            if (tramite.estadoTramite != enviado && tramite.estadoTramite != recibido) {
                render "ERROR_Se ha cancelado el proceso de recepción.<br/>Este trámite no puede ser gestionado."
                return
            }
            def paraDpto = tramite.para?.departamento
            def paraPrsn = tramite.para?.persona

            def archivado = EstadoTramite.findByCodigo("E005")
            def anulado = EstadoTramite.findByCodigo("E006")
            def noRecibe = [archivado, anulado]

//            def persDocTrams = PersonaDocumentoTramite.withCriteria {
//                eq("tramite", tramite)
//                if (paraDpto) {
//                    eq("departamento", paraDpto)
//                } else if (paraPrsn) {
//                    eq("persona", paraPrsn)
//                }
//            }
//            def recibe = true
//            persDocTrams.each { pdt ->
//                if (noRecibe.contains(pdt.estado.codigo)) {
//                    recibe = false
//                }
//            }
//            if (!recibe) {
//                render "ERROR_El trámite se encuentra anulado o archivado y no puede ser gestionado."
//                return
//            }

            def esCircular = false
            if (!paraPrsn && !paraDpto) {
                esCircular = true
            }

            def rolPara = RolPersonaTramite.findByCodigo("R001")
            def rolCC = RolPersonaTramite.findByCodigo("R002")
            def rolImprimir = RolPersonaTramite.findByCodigo("I005")
            def triangulo = false
            if (params.source == "bed")
                triangulo = true
            def estadoRecibido = EstadoTramite.findByCodigo('E004') //recibido
            def estadoAnulado = EstadoTramite.findByCodigo('E006') //recibido
            def estadoArchivado = EstadoTramite.findByCodigo('E005') //recibido
//            println "es circu "+esCircular+" depto "+triangulo
            def pxt = PersonaDocumentoTramite.withCriteria {
                eq("tramite", tramite)
                if (!esCircular) {
                    if (triangulo) {
                        eq("departamento", persona.departamento)
                    } else {
                        eq("persona", persona)
                    }
                } else {
                    if (triangulo) {
                        eq("departamento", persona.departamento)
                    } else {
                        eq("persona", persona)
                    }
//                    or {
//                        eq("departamento", persona.departamento)
//                        eq("persona", persona)
//                    }
                }
                or {
                    eq("rolPersonaTramite", rolPara)
                    eq("rolPersonaTramite", rolCC)
                    eq("rolPersonaTramite", rolImprimir)
                }
                and {
                    ne("estado", estadoAnulado)
                    ne("estado", estadoArchivado)
                }
            }//PersonaDocumentoTramite.findByTramiteAndDepartamento(tramite, persona.departamento)

            println "pxt: " + pxt

//        println "tramite: " + tramite
//        println "paraDpto: " + paraDpto
//        println "paraPrsn: " + paraPrsn
//        println "rolPara: " + rolPara
//        println "rolCC: " + rolCC
//        println "rolImprimir: " + rolImprimir
//            println "pxt 1 "+pxt.estado.codigo


            if (pxt.size() > 1) {
                pxt.each {
                    println " " + it.persona + "   " + it.departamento + "   " + it.rolPersonaTramite.descripcion + "  " + it.tramite
                }
//                flash.message = "ERROR"
                println "mas de 1 PDT: ${pxt}"
//                redirect(action: "errores")
                return
            } else if (pxt.size() == 0) {
                flash.message = "ERROR"
                println "0 PDT"
                redirect(action: "errores")
            } else {
                pxt = pxt.first()
                def recibe = true
                if (noRecibe.contains(pxt.estado)) {
                    recibe = false
                }
                if (!recibe) {
                    render "ERROR_El trámite se encuentra anulado o archivado y no puede ser gestionado."
                    return
                }
            }

//            println("pxt 2"  + pxt )
            println "Estado del pdt "+pxt.estado.codigo
            if (pxt.estado.codigo != "E004") {

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
//            println "aaa1"
//            println "hoy "+hoy
//            println "pxt "+pxt
                pxt.fechaRecepcion = hoy
//            println "aaa2"
                pxt.fechaLimiteRespuesta = limite
                pxt.estado = EstadoTramite.findByCodigo("E004")

                if (pxt.save(flush: true) && tramite.save(flush: true)) {
                    def pdt = new PersonaDocumentoTramite()
                    pdt.tramite = tramite
                    pdt.persona = persona
                    pdt.rolPersonaTramite = RolPersonaTramite.findByCodigo("E003")
                    pdt.fechaRecepcion = hoy
                    pdt.fechaLimiteRespuesta = limite
                    def alerta
                    if (pxt.departamento) {
                        alerta = Alerta.findByTramiteAndDepartamento(pxt.tramite, pxt.departamento)
                    }
                    if (pxt.persona) {
                        alerta = Alerta.findByTramiteAndPersona(pxt.tramite, pxt.persona)
                    }
                    if (alerta) {
                        if (alerta.fechaRecibido == null) {
                            alerta.mensaje += " - Recibido"
                            alerta.fechaRecibido = new Date()
                            alerta.save()
                        }
                    }
                    if (pdt.save(flush: true)) {
                        render "OK_Trámite recibido correctamente"
                    } else {
                        println "error pdt recibir "+pdt.errors
                        render "NO_Ocurrió un error al recibir"
                    }
                    def job = new BloqueosJob()
                    job.executeRecibir(persona.departamento, session.usuario)
                    job = null
                } else {
                    println "pxt error " +pxt.errors
                    println "error tramite recibir "+tramite.errors
                    render "NO_Ocurrió un error al recibir"
                }
            } else {
                println "estado 4 "+ pxt.id + "  " + pxt.estado.codigo + "   " + pxt.estado.descripcion + "   " + pxt.fechaRecepcion
                render "NO_Ocurrió un error al recibir"
            }


        } else {
            response.sendError(403)
        }


    }

    def enviarTramiteJefe() {
        def tramite = Tramite.get(params.id)
//        def obs = params.obs
//        def persona = Persona.get(session.usuario.id)
//        tramite.observaciones = (tramite.observaciones ? tramite.observaciones + "; " : "") + persona.login + " (" + (new Date().format("dd-MM-yyyy HH:mm")) + "): " + obs
//        def nuevaObsTram = persona.login + " (" + (new Date().format("dd-MM-yyyy HH:mm")) + "): " + obs
//        tramite.observaciones = tramitesService.modificaObservaciones(tramite.observaciones, nuevaObsTram)
//        tramite.observaciones = tramitesService.makeObservaciones(tramite.observaciones, obs, "", session.usuario.login)
//        tramite.estadoTramite = EstadoTramite.findByCodigo("E007")

        def observacionOriginal = tramite.observaciones
        def accion = ""
        def solicitadoPor = ""
        def usuario = session.usuario.login
        def texto = ""
        def nuevaObservacion = params.obs
        tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

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
        def departamento = persona?.departamento

        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');


        def pxtPara = PersonaDocumentoTramite.withCriteria {
            eq("departamento", departamento)
            eq("rolPersonaTramite", rolPara)
            isNotNull("fechaEnvio")
//            tramite {
            or {
                eq("estado", EstadoTramite.findByCodigo("E003")) //enviado
                eq("estado", EstadoTramite.findByCodigo("E007")) //enviado al jefe
                eq("estado", EstadoTramite.findByCodigo("E004")) //recibido
            }
//            }
        }
        def pxtCopia = PersonaDocumentoTramite.withCriteria {
            eq("departamento", departamento)
            eq("rolPersonaTramite", rolCopia)
            isNotNull("fechaEnvio")
//            tramite {
            or {
                eq("estado", EstadoTramite.findByCodigo("E003")) //enviado
                eq("estado", EstadoTramite.findByCodigo("E007")) //enviado al jefe
                eq("estado", EstadoTramite.findByCodigo("E004")) //recibido
            }
//            }
        }


        def pxtTodos = pxtPara
        pxtTodos += pxtCopia
//        def pxtTramites = pxtTodos

        if (params.domain == "persDoc") {
            pxtTodos.sort { it[params.sort] }
        } else if (params.domain == "tramite") {
            pxtTodos.sort { it.tramite[params.sort] }
        }
        if (params.order == "desc") {
            pxtTodos = pxtTodos.reverse()
        }

        def tramitesSinHijos = []
        def band = false
        def anulado = EstadoTramite.findByCodigo("E006")
        pxtTodos.each { tr ->
            if (!(tr.tramite.tipoDocumento.codigo == "OFI")) {
                band = tramitesService.verificaHijos(tr, anulado)
//            println "estado!!! " + band + "   " + tr.id
                if (!band) {
                    tramitesSinHijos += tr
                }
            }
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

//            order("fechaEnvio", 'desc')
        }

        return [tramites: res, pxtTramites: tramitesSinHijos]

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


    def arbolTramite() {
        def tramite = Tramite.get(params.id.toLong())
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
        def html2 = "<ul>" + "\n"
        html2 += makeNewTreeExtended(principal)
        html2 += "</ul>" + "\n"

        def url = ""
        switch (params.b) {
            case "bep":
                url = createLink(controller: "tramite", action: "bandejaEntrada")
                break;
            case "bed":
                url = createLink(controller: "tramite3", action: "bandejaEntradaDpto")
                break;
            case "bsp":
                url = createLink(controller: "tramite2", action: "bandejaSalida")
                break;
            case "bsd":
                url = createLink(controller: "tramite2", action: "bandejaSalidaDep")
                break;
            case "bqt":
                url = createLink(controller: "buscarTramite", action: "busquedaTramite")
                break;
            case "bqe":
                url = createLink(controller: "buscarTramite", action: "busquedaEnviados")
                break;

        }

        return [html2: html2, url: url]
    }

    private static String tramiteInfo(PersonaDocumentoTramite tramiteParaInfo) {
        def strInfo = ""
//        println "*****" + tramiteParaInfo
        if (tramiteParaInfo) {
//            println "AQUI " + tramiteParaInfo
//            println "AQUI2 " + tramiteParaInfo.departamento
//            println "AQUI3 " + tramiteParaInfo.persona
//            def paraStr = tramiteParaInfo.departamento ? tramiteParaInfo.departamento.codigo : tramiteParaInfo.persona.departamento.codigo + ":" + tramiteParaInfo.persona.login

            def paraStr = ""
            if (tramiteParaInfo.departamento) {
                paraStr = tramiteParaInfo.departamento.codigo
            } else if (tramiteParaInfo.persona) {
                paraStr = tramiteParaInfo.persona.departamento.codigo + ":" + tramiteParaInfo.persona.login
            }

            def deStr = tramiteParaInfo.tramite.deDepartamento ? tramiteParaInfo.tramite.deDepartamento.codigo : tramiteParaInfo.tramite.de.departamento.codigo + ":" + tramiteParaInfo.tramite.de.login
            def rol = tramiteParaInfo.rolPersonaTramite
            if (rol.codigo == "R002") {
                strInfo += "[CC] "
            }
            strInfo += "<strong>${tramiteParaInfo.tramite.codigo} </strong>"
            strInfo += "<small>("
            strInfo += "<strong>DE</strong>: ${deStr}, <strong>${rol.descripcion}</strong>: ${paraStr}"
            strInfo += ", <strong>creado</strong> el " + tramiteParaInfo.tramite.fechaCreacion.format("dd-MM-yyyy HH:mm")
            if (tramiteParaInfo.fechaEnvio) {
                strInfo += ", <strong>enviado</strong> el " + tramiteParaInfo.fechaEnvio.format("dd-MM-yyyy HH:mm")
            }
            if (tramiteParaInfo.fechaRecepcion) {
                strInfo += ", <strong>recibido</strong> el " + tramiteParaInfo.fechaRecepcion.format("dd-MM-yyyy HH:mm")
            }
            if (tramiteParaInfo.fechaArchivo) {
                strInfo += ", <strong>archivado</strong> el " + tramiteParaInfo.fechaArchivo.format("dd-MM-yyyy HH:mm")
            }
            if (tramiteParaInfo.fechaAnulacion) {
                strInfo += ", <strong>anulado</strong> el " + tramiteParaInfo.fechaAnulacion.format("dd-MM-yyyy HH:mm")
            }
            strInfo += ")</small>"
        }
        return strInfo
    }

    private String makeLeaf(PersonaDocumentoTramite pdt) {
        def html = "", clase = "", rel = "para", data = ""
        if (pdt) {
            if (pdt.rolPersonaTramite.codigo == "R002") {
                rel = "copia"
            }
            if (!pdt.tramite.padre) {
                rel = "principal"
            }
            if (pdt.fechaAnulacion) {
                rel = "anulado"
            }
            def strInfo = tramiteInfo(pdt)
            def hijos = Tramite.findAllByAQuienContesta(pdt, [sort: "fechaCreacion", order: "asc"])
//        def hijos
//        if (para.departamento) {
//            hijos = Tramite.findAllByPadreAndDeDepartamento(para.tramite, para.departamento)
//        } else {
//            hijos = Tramite.withCriteria {
//                eq("padre", para.tramite)
//                eq("de", para.persona)
//                isNull("deDepartamento")
//            }
//        }
            if (hijos.size() > 0) {
                clase += " jstree-open"
            }
            data += ',"tramite":"' + pdt.tramiteId + '"'
            html += "<li id='${pdt.id}' class='${clase}' data-jstree='{\"type\":\"${rel}\"${data}}' >"
            if (pdt.fechaAnulacion) {
                html += "<span class='text-muted'>"
            }
            html += strInfo
            if (pdt.fechaAnulacion) {
                html += "</span>"
            }
            html += "\n"
            if (hijos.size() > 0) {
                html += "<ul>" + "\n"
                hijos.each { hijo ->
                    html += makeTreeExtended(hijo)
                }
                html += "</ul>" + "\n"
            }
            html += "</li>"
        }
        return html
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

    //Antes de cambiar la estructura de tramites relacionados
    private String makeTreeExtended(Tramite principal) {

        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCc = RolPersonaTramite.findByCodigo("R002")

        def paras = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(principal, rolPara)
        def ccs = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(principal, rolCc)

        def html = ""

        //esto muestra una hoja por destinatario
//        println paras;
//        println ccs;

        paras.each { para ->
            html += makeLeaf(para)
        }

        //el para y las copias son hermanos
        ccs.each { para ->
            html += makeLeaf(para)
        }

        return html
    }

    private String makeTree(Tramite principal, Tramite tramite) {
        def html = ""
        def clase = ""
        def rel = "hijo"
        def hijos = Tramite.findAllByPadre(principal)
        if (principal.id == tramite.id) {
            clase = "active"
        }
        if (hijos.size() > 0) {
            clase += " jstree-open"
            rel = "padre"
        }

        def tramiteInfo = { PersonaDocumentoTramite tramiteParaInfo ->
            def paraStr = tramiteParaInfo.departamento ? tramiteParaInfo.departamento.descripcion : tramiteParaInfo.persona.login
            def deStr = tramiteParaInfo.tramite.deDepartamento ? tramiteParaInfo.tramite.deDepartamento.descripcion : tramiteParaInfo.tramite.de.login


            def strInfo = "(DE: ${deStr}, PARA ${paraStr})"
            return strInfo
        }
//
//        def rolPara = RolPersonaTramite.findByCodigo("R001")
//        def rolCc = RolPersonaTramite.findByCodigo("R002")
//
//        def paras = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(principal, rolPara)
//        def ccs = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(principal, rolCc)

        //esto muestra una sola hoja por tramite
        html += "<li id='${principal.id}' class='${clase}' data-jstree='{\"type\":\"${rel}\"}' >" + principal.codigo + "\n"

        //esto muestra una hoja por destinatario (para es padre de las copias, si no hay para la 1ra copia es padre del resto)
//        paras.each { para ->
//            def strInfo = tramiteInfo(para)
//            html += "<li id='${para.id}' class='${clase}' data-jstree='{\"type\":\"${rel}\"}' >" + para.tramite.codigo + " ${strInfo}\n"
//        }
//        if (paras.size() == 0) {
//            def para = ccs.first()
//            def strInfo = tramiteInfo(para)
//            html += "<li id='${para.id}' class='${clase}' data-jstree='{\"type\":\"${rel}\"}' >" + para.tramite.codigo + " ${strInfo}\n"
//            ccs = ccs[1..(ccs.size() - 1)]
//        }
////        if (ccs.size() > 0) {
////            html += "<ul>"
////        }
//        ccs.each { para ->
//            def strInfo = tramiteInfo(para)
//            html += "<li id='${para.id}' class='${clase}' data-jstree='{\"type\":\"${rel}\"}' >" + para.tramite.codigo + " ${strInfo}\n"
//        }
//        if (ccs.size() > 0) {
//            html += "</ul>"
//        }

        if (hijos.size() > 0) {
            html += "<ul>" + "\n"
            hijos.each { hijo ->
                html += makeTree(hijo, tramite)
            }
            html += "</ul>" + "\n"
            html += "</li>" + "\n"
        }
        return html
    }


    def getCadenaDown(pdt, funcion) {
        //println "get cade down " + pdt
        def res = []
        def tramite = Tramite.findAll("from Tramite where aQuienContesta=${pdt.id}")
        //println "tramite " + tramite
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


    def bandejaImprimir() {
        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def revisar = false
        def bloqueo = false
        if (session.usuario.esTriangulo()) {
            redirect(action: 'bandejaSalidaDep')
            return
        }
/*
        if (persona.jefe == 1)
            revisar = true
        else {
            def per = PermisoUsuario.findByPersonaAndPermisoTramite(persona, PermisoTramite.findByCodigo("P005"))
            if (per) {
                revisar = true
            }
        }
*/
        def departamento = Persona.get(usuario.id).departamento
        def personal = Persona.findAllByDepartamento(departamento)
        def personalActivo = []
        personal.each {
            if (it?.estaActivo && it?.id != usuario.id) {
                personalActivo += it
            }
        }
        return [persona: persona, revisar: revisar, bloqueo: bloqueo, personal: personalActivo, esEditor: persona.puedeEditor]
    }

    def tablaBandejaImprimir() {

        def rolImprimir = RolPersonaTramite.findByCodigo('I005')

        def persona = Persona.get(session.usuario.id)
        def tramites = []

        def t = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolImprimir).tramite
        if (t.size() > 0) {
            tramites += t
        }
        tramites?.sort { it.fechaCreacion }
        tramites = tramites?.reverse()

        return [persona: persona, tramites: tramites]
    }

}
