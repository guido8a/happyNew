package happy.tramites

class TramiteAdminController {

    def arbolAdminTramite() {
        params.id = 98
        def html = "", url = ""
        def tramite = Tramite.get(params.id.toLong())
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
        return [html2: html, url: url]
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

    private String makeLeaf(PersonaDocumentoTramite para) {
        def html = "", clase = "", rel = "para", data = ""
        if (para.rolPersonaTramite.codigo == "R002") {
            rel = "copia"
        }
        def hijos
        if (para.departamento) {
            hijos = Tramite.findAllByPadreAndDeDepartamento(para.tramite, para.departamento)
        } else {
            hijos = Tramite.findAllByPadreAndDe(para.tramite, para.persona)
        }
        if (hijos.size() > 0) {
            clase += " jstree-open"
        }
        def estado = ""
        if (para.fechaEnvio) {
            clase += " enviado"
            estado = "Enviado"
        }
        if (para.fechaRecepcion) {
            clase += " recibido"
            estado = "Recibido"
        }

        if (para.fechaArchivo) {
            clase += " archivado"
            estado = "Archivado"
        }

        if (para.fechaAnulacion) {
            clase += " anulado"
            estado = "Anulado"
        }

        rel += estado

        def duenio = para.tramite.deDepartamento ? "-" + para.tramite.deDepartamento.id : para.tramite.de.id

        data += ',"tramite":"' + para.tramiteId + '"'
        data += ',"duenio":"' + duenio + '"'
        data += ',"codigo":"' + para.tramite.codigo + '"'
        html += "<li id='${para.id}' class='${clase}' data-jstree='{\"type\":\"${rel}\"${data}}' >${tramiteInfo(para)}\n"
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

    private static String tramiteInfo(PersonaDocumentoTramite tramiteParaInfo) {
        def paraStr = tramiteParaInfo.departamento ? tramiteParaInfo.departamento.descripcion : tramiteParaInfo.persona.login
        def deStr = tramiteParaInfo.tramite.deDepartamento ? tramiteParaInfo.tramite.deDepartamento.codigo : tramiteParaInfo.tramite.de.login
        def rol = tramiteParaInfo.rolPersonaTramite
        def strInfo = ""
        if (rol.codigo == "R002") {
            strInfo += "[CC] "
        }
        strInfo += "<strong>${tramiteParaInfo.tramite.codigo} </strong>"
        strInfo += "<small>("
        strInfo += "<strong>DE</strong>: ${deStr}, <strong>${rol.descripcion}</strong>: ${paraStr}"
        strInfo += ", <strong>creado</strong> el " + tramiteParaInfo.tramite.fechaCreacion.format("dd-MM-yyyy HH:mm")
        if (tramiteParaInfo.fechaEnvio) {
            strInfo += ", <span class='text-info'><strong>enviado</strong> el " + tramiteParaInfo.fechaEnvio.format("dd-MM-yyyy HH:mm") + "</span>"
        }
        if (tramiteParaInfo.fechaRecepcion) {
            strInfo += ", <span class='text-success'><strong>recibido</strong> el " + tramiteParaInfo.fechaRecepcion.format("dd-MM-yyyy HH:mm") + "</span>"
        }
        if (tramiteParaInfo.fechaArchivo) {
            strInfo += ", <span class='text-warning'><strong>archivado</strong> el " + tramiteParaInfo.fechaArchivo.format("dd-MM-yyyy HH:mm") + "</span>"
        }
        if (tramiteParaInfo.fechaAnulacion) {
            strInfo += ", <span class='text-danger'><strong>anulado</strong> el " + tramiteParaInfo.fechaAnulacion.format("dd-MM-yyyy HH:mm") + "</span>"
        }
        strInfo += ")</small>"
        return strInfo
    }

    def desrecibir() {
        def persDocTram = PersonaDocumentoTramite.get(params.id)
        persDocTram.observaciones = (persDocTram.observaciones ?: "") +
                " Quitado el recibido por ${session.usuario.login} el ${new Date().format('dd-MM-yyyy HH:mm')} " +
                "(originalmente recibido el ${persDocTram.fechaRecepcion.format('dd-MM-yyyy HH:mm')}): " + params.observaciones
        persDocTram.fechaRecepcion = null
        persDocTram.fechaLimiteRespuesta = null
        if (persDocTram.save(flush: true)) {
            render "OK"
        } else {
            render "NO*" + renderErrors(bean: persDocTram)
        }
    }

    def anular() {

    }

    def desanular() {

    }

}
