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

        def funcion = {objeto->
            def anulado = EstadoTramite.findByCodigo("E006")
            objeto.estado=anulado
            objeto.fechaAnulacion=new Date()
            objeto.observaciones = (objeto.observaciones ?: "") +  "Documento anulado por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}: ${params.texto};"
            if(objeto.rolPersonaTramite.codigo=="R002")
                objeto.tramite.observaciones = (objeto.tramite.observaciones ?: "") +"COPIA anulada por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto}; "
            if(objeto.rolPersonaTramite.codigo=="R001")
                objeto.tramite.observaciones = (objeto.tramite.observaciones ?: "") + "Documento anulado por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto}; "
            objeto.tramite.save(flush:true)
            objeto.save(flush: true)
        }
        def pdt = PersonaDocumentoTramite.get(params.id)
        getCadenaDown(pdt,funcion)
        if( pdt.tramite.aQuienContesta){
            pdt.tramite.aQuienContesta.estado = EstadoTramite.findByCodigo("E004")
            pdt.tramite.aQuienContesta.fechaAnulacion=null
            pdt.tramite.aQuienContesta.fechaArchivo=null
            pdt.tramite.aQuienContesta.observaciones = ( pdt.tramite.aQuienContesta.observaciones ?: "") + "Tramite reactivado por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto}; "
            pdt.tramite.aQuienContesta.save(flush: true)
        }

    }

    def desanular() {
        def pdt = PersonaDocumentoTramite.get(params.id)
        pdt.estado=EstadoTramite.findByCodigo("E004")
        pdt.observaciones=  (pdt.observaciones ?: "")+"Documento reactivado por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto};"
        pdt.fechaAnulacion=null
        if(pdt.rolPersonaTramite.codigo=="R002")
            pdt.tramite.observaciones=  (pdt.tramite.observaciones ?: "")+"COPIA reactivada por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto}; "
        if(pdt.rolPersonaTramite.codigo=="R001")
            pdt.tramite.observaciones = (pdt.tramite.observaciones ?: "")+"Documento reactivado por ${session.usuario} el ${new Date().format('dd-MM-yyyy HH:mm')}:${params.texto}; "
        pdt.tramite.save(flush: true)
        if(pdt.save(flush: true)){
            render "OK"
        }else{
            render "NO"
        }
    }

    def getCadenaDown(pdt,funcion){
        println "get cade down "+pdt
        def res = []
        def tramite = Tramite.findAll("from Tramite where aQuienContesta=${pdt.id}")
        println "tramite "+tramite
        def roles = [RolPersonaTramite.findByCodigo("R002"),RolPersonaTramite.findByCodigo("R001")]
        def lvl
        funcion pdt
        if(tramite){
            tramite=tramite.pop()
            def tmp = [:]
            tmp.put("nodo",tramite)
            tmp.put("tipo","tramite")
            def pdts = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteInList(tramite,roles)
            tmp.put("hijos",[])

            pdts.each {
                def r = getHijos(it,roles,funcion)
                if(r.size()>0)
                    tmp["hijos"]+=r
            }
            tmp.put("origen",pdt)
            res.add(tmp)
            res = getHermanos(tramite,res,roles,funcion)
        }else{
            return []
        }


        println "res lol "+res

    }

    def getHermanos(tramite,res,roles,funcion){
//        println "get hermanos "+tramite.id
        def lvl
        def hermanos = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteInList(tramite,roles)
        while(hermanos.size()>0){
            def nodo = hermanos.pop()
            def tmp = [:]
            tmp.put("nodo",nodo)
            tmp.put("hijos",getHijos(nodo,roles,funcion))
            tmp.put("tipo","pdt")
            funcion nodo
            res.add(tmp)

        }
//        println "return get hermanos "+res
        return res
    }

    def getHijos(pdt,roles,funcion){
//        println "get hijos "+pdt.id+" "+pdt.rolPersonaTramite.descripcion
        def res =[]
        def t = Tramite.findByAQuienContesta(pdt)
        if(t){
            def tmp = [:]
            tmp.put("nodo",t)
            tmp.put("tipo","tramite")
            tmp.put("hijos",[])
            def pdts = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteInList(t,roles)
            tmp.put("hijos",[])
            pdts.each {
                def r = getHijos(it,roles,funcion)
                if(r.size()>0)
                    tmp["hijos"]+=r
            }
            res = getHermanos(t,res,roles,funcion)
            res.add(tmp)
        }
//        println "fin hijos "+res
        return res
    }


}
