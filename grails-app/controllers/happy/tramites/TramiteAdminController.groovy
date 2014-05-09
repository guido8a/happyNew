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
        def strInfo = tramiteInfo(para)
        def hijos
        if (para.departamento) {
            hijos = Tramite.findAllByPadreAndDeDepartamento(para.tramite, para.departamento)
        } else {
            hijos = Tramite.findAllByPadreAndDe(para.tramite, para.persona)
        }
        if (hijos.size() > 0) {
            clase += " jstree-open"
        }

        if (para.fechaArchivo) {
            clase += " archivado"
            rel += "Archivado"
        }

        if (para.fechaAnulacion) {
            clase += " anulado"
            rel += "Anulado"
        }

        if (para.fechaRecepcion) {
            clase += " recibido"
        }

        data += ',"tramite":"' + para.tramiteId + '"'
        html += "<li id='${para.id}' class='${clase}' data-jstree='{\"type\":\"${rel}\"${data}}' >${strInfo}\n"
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
        return strInfo
    }

}
