package happy.tramites

import groovy.time.TimeCategory
import happy.DbConnectionService
import happy.seguridad.Persona
import happy.utilitarios.Parametros


class BuscarTramiteController extends happy.seguridad.Shield {


    def dbConnectionService
    def tramitesService

    def verificarAgregarDoc() {
        def tramite = Tramite.get(params.id)
        def persona = Persona.get(session.usuario.id)
        def esDepartamento = persona.esTriangulo

//        println("tramite " + tramite)

//        if(pdt.estado == EstadoTramite.findByCodigo("E005")){
//                        flash.message = "No puede agregar un documento a este tramite."
//                        response.sendError(403)
//                        return
//                    }

//        println "puede: ${persona.puedeAgregarDocumento}, esDpto: ${esDepartamento} de: ${persona} = ${tramite.de}, " +
//                "deDpto: ${esDepartamento} : ${tramite.deDepartamento} = ${persona.departamento}"

        if(persona.puedeAgregarDocumento){
//            println "puedeAgregarDocumento: ok"
            render "OK"
            return
        }

        if (!esDepartamento && persona == tramite.de) {
//            println "1.1: " + persona
            render "OK"
            return
        }
//        if (esDepartamento && tramite.deDepartamento == persona.departamento) {
        if (esDepartamento && tramite.departamento == persona.departamento) {  //el triángulo puede agregar trámite de cualquiera de su oficina (2017)
//            println "1.2: " + persona.departamento
            render "OK"
            return
        }
        def principal = tramite
        def contador = 0
        while (principal.padre) {
            contador++
            principal = principal.padre
            if (!esDepartamento && persona == principal.de) {
//                println "2.1: " + persona
                render "OK"
                return

            }
            if (esDepartamento && principal.deDepartamento == persona.departamento) {
//                println "2.2: " + persona.departamento
                render "OK"
                return
            }
        }
        println "contador: $contador"

        def tramitePrincipal = principal.tramitePrincipal
        def tramites
        if (tramitePrincipal > 0) {
            tramites = Tramite.findAllByTramitePrincipal(tramitePrincipal, [sort: "fechaCreacion"])
        } else {
            tramites = [principal]
        }

        def puede = false

        tramites.each { tr ->
            puede = hijosTramite(tr, persona, esDepartamento, puede)
            if (puede) {
                return
            }
        }
        if (puede) {
            render "OK"
            return
        }
        render "NO"
    }

    def hijosTramite(Tramite principal, Persona persona, boolean esDepartamento, boolean puede) {
        if (!puede) {
            def rolPara = RolPersonaTramite.findByCodigo("R001")
            def rolCc = RolPersonaTramite.findByCodigo("R002")

            def paras = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(principal, rolPara)
            def ccs = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(principal, rolCc)

            paras.each { para ->
                if (!puede) {
//                    println "4.1: " + persona
                    puede = hijosPdt(para, persona, esDepartamento, puede)
                }
            }

            if (!puede) {
                ccs.each { para ->
                    if (!puede) {
//                        println "4.2: " + persona
                        puede = hijosPdt(para, persona, esDepartamento, puede)
                    }
                }
            }
        }
        return puede
    }

    def hijosPdt(PersonaDocumentoTramite pdt, Persona persona, boolean esDepartamento, boolean puede) {
        if (!puede) {
            def hijos = Tramite.findAllByAQuienContesta(pdt, [sort: "fechaCreacion", order: "asc"])

            hijos.each { h ->
                if (!esDepartamento && h.de == persona) {
//                    println "3.1: " + persona
                    puede = true
                }
                if (esDepartamento && h.deDepartamento == persona.departamento) {
//                    println "3.2: " + persona.departamento
                    puede = true
                }
                if (!puede) {
                    puede = hijosTramite(h, persona, esDepartamento, puede)
                }
            }
        }
        return puede
    }

    def busquedaTramite() {
//        println "busqueda "
    }

    def ampliarPlazo_ajax() {
        def error = ""
        params.each { k, v ->
            if (k.contains("input")) {
                def parts = k.split("_")
                def persDocId = parts[1]
                def persDocTram = PersonaDocumentoTramite.get(persDocId)
                def fecha = new Date().parse("dd-MM-yyyy HH:mm", v.toString() + " " + persDocTram.fechaLimiteRespuesta.format("HH:mm"))

                if (fecha != persDocTram.fechaLimiteRespuesta) {
                    def para = ""
                    if (persDocTram.departamento) {
                        para = "para: " + persDocTram.departamento.codigo
                    } else if (persDocTram.persona) {
                        para = "para: " + persDocTram.persona.login
                    }
//                    para += " (${persDocTram.rolPersonaTramite.descripcion})"

                    def l = ", hasta: ${fecha.format('dd-MM-yyyy HH:mm')}, " +
                            "plazo anterior: ${persDocTram.fechaLimiteRespuesta.format('dd-MM-yyyy HH:mm')}"
                    def log = "Ampliado el plazo" + l
                    def log2 = para + l

                    //(String observacionOriginal, String accion, String solicitadoPor, String usuario, String texto, String nuevaObservacion)
                    def observacionOriginal = persDocTram.observaciones
                    def accion = "Ampliación de plazo"
                    def solicitadoPor = ""
                    def usuario = "por: " + session.usuario.login
                    def texto = log
                    def nuevaObservacion = ""
                    persDocTram.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
                    observacionOriginal = persDocTram.tramite.observaciones
                    texto = log2
                    persDocTram.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

                    persDocTram.fechaLimiteRespuesta = fecha
                    if (!persDocTram.save(flush: true)) {
                        error += renderErrors(bean: persDocTram)
                    }
                }
            }
        }
        if (error == "") {
            render "OK_Plazo ampliado exitosamente"
        } else {
            render "NO_" + error
        }
    }

    def ampliarPlazoUI_ajax() {
        def tramite = Tramite.get(params.id)
        def jefe = Persona.get(session.usuario.id)
        def dpto = jefe.departamento

        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCc = RolPersonaTramite.findByCodigo("R002")


        def personas = PersonaDocumentoTramite.withCriteria {
            eq("tramite", tramite)
            or {
                eq("rolPersonaTramite", rolPara)
                eq("rolPersonaTramite", rolCc)
            }
        }


        return [tramite: tramite, jefe: jefe, personas: personas, dpto: dpto]
    }


    def nuevoAmpliarPlazo_ajax() {

        def jefe = Persona.get(session.usuario.id)
        def dpto = jefe.departamento

        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCc = RolPersonaTramite.findByCodigo("R002")

        def personas = PersonaDocumentoTramite.get(params.id)


        return [jefe: jefe, pers: personas, dpto: dpto]

    }


    def tablaBusquedaTramite() {
//        println "tablaBusquedaTramite: $params"
        def cn = dbConnectionService.getConnection()
        /** todo: Hacer con SQL **/

        def persona = session.usuario.id

        def fechaDesde = ""
        if (params.fcds) {
            fechaDesde = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fcds + " 00:00:00")
        }

        def fechaHasta = ""
        if (params.fchs) {
            fechaHasta = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fchs + " 00:00:00")
        }

        def maximo = (params.registros?.toInteger())?: 20


/*
        def sql = "select trmt.trmt__id from trmt where trmtcdgo is not null "
        def w_fcds = params.fcds ? " and trmtfccr >= '${params.fcds}'" : ""
        def w_fchs = params.fchs ? " and trmtfccr <= '${params.fchs}'" : ""
        def w_asnt = params.asunto ? " and trmtasnt ilike '%${params.asunto.toLowerCase()}%'" : ""
        def w_cdgo = params.codigo ? " and trmtcdgo like '%${params.codigo.toUpperCase()}%'" : ""
        def w_inst = params.institucion ? " and trmtprex ilike '%${params.institucion.toLowerCase()}%'" : ""
        def w_cntc = params.contacto ? " and trmtcntc ilike '%${params.contacto.toLowerCase()}%'" : ""

        sql += w_fcds + w_fchs + w_asnt + w_cdgo + w_inst + w_cntc + " order by trmtcdgo limit 101"
        println "sql: ${sql}"

        def res = []
        cn.eachRow(sql.toString()) {d ->
            res.add(Tramite.get(d.trmt__id))
        }

        println "---> ${res[0].id}"
*/

        def res
/*
        res = PersonaDocumentoTramite.withCriteria {
            if (params.fecha) {
                gt('fechaEnvio', params.fechaIni)
                lt('fechaEnvio', params.fechaFin)
            }
*/
         res = Tramite.withCriteria {
                if (params.fecha) {
                    gt('fechaEnvio', params.fechaIni)
                    lt('fechaEnvio', params.fechaFin)
                }
                if (params.asunto) {
                    ilike('asunto', '%' + params.asunto.trim() + '%')
                }
                if (params.codigo) {
                    ilike('codigo', '%' + params.codigo.trim() + '%')
                }

                if (params.fcds) {
                    if(params.fechas == 'fccr') {
                        gt('fechaCreacion', fechaDesde)
                    } else {
                        gt('fechaEnvio', fechaDesde)
                    }
                }
                if (params.fchs) {
                    if(params.fechas == 'fccr') {
                        lt('fechaCreacion', fechaHasta)
                    } else {
                        lt('fechaEnvio', fechaHasta)
                    }
                }
                if(params.doc){
                    ilike('numeroDocExterno', '%' + params.doc.trim() + '%')
                }
                if(params.institucion){
                    ilike('paraExterno', '%' + params.institucion.trim() + '%')
                }
                if(params.contacto){
                    ilike('contacto', '%' + params.contacto.trim() + '%')
                }
                order('tipoDocumento')
                order('fechaCreacion', 'desc')
             maxResults(maximo + 1)
         }

//        println "registros....: ${res.size()}"

//        def tramitesFiltrados = res.tramite.unique()
//        println "registros.... filtrados: ${tramitesFiltrados.size()}"
//        tramitesFiltrados.sort { it.codigo }
//        def msg = ""
//        if (tramitesFiltrados.size() > 200) {
//            msg = "<div class='alert alert-danger' style='margin-left:0px'> <i class='fa fa-warning fa-2x pull-left'></i> Su búsqueda ha generado más de 200 resultados. Por favor utilice los filtros.</div>"
//        }

//        def fin = new Date()
//        println "${TimeCategory.minus(fin, inicio)}"

        def msg = ""
        if (res.size() > maximo) {
            res.pop()
            msg = "<div class='alert alert-danger clearfix' style='margin-left:0px; margin-top:-32px; height: 45px'> " +
                    "<i class='fa fa-warning fa-2x pull-left'></i> " +
                    "Su búsqueda ha generado más de ${maximo} resultados. Por favor utilice más criterios de búsqueda como por " +
                    "ejemplo un rango de fechas de creación de los trámites.</div>"

//            msg = "<i class='fa fa-warning fa-2x pull-left'></i> " +
//                    "Su búsqueda ha generado más de ${maximo} resultados. Por favor utilice más criterios de búsqueda como por " +
//                    "ejemplo un rango de fechas de creación de los trámites"
        }


        params.dgsg = Persona.get(session.usuario.id).puedeAgregarDocumento? "DGSG" : ""

//        println "puede dgsg: ${params.dgsg}"

//        return [tramites: tramitesFiltrados, persona: persona, msje: msg]
        return [tramites: res, persona: persona, msje: msg, maximo: maximo]
    }

    def resTramites(Tramite tramite) {

        def sql = ""

        def result = []
        def idsUnicos = []

        def cn = dbConnectionService.getConnection();

        sql = "select * from tramites(" + tramite.id + ") "
        cn.eachRow(sql) { r ->
            result.add(r.toRowResult())
        }

        return result

    }

    def busquedaEnviados() {
    }

    def tablaBusquedaEnviados() {

        def persona = Persona.get(session.usuario.id)
        def departamento = persona?.departamento
        def pxtPara
        def pxtCopia
        def res

        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');


        if (params.fechaDesde) {
            params.fechaDesde = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fechaDesde + " 00:00:00")
        }
        if (params.fechaHasta) {
            params.fechaHasta = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fechaHasta + " 23:59:59")
        } else {
            params.fechaHasta = new Date()
        }

        res = PersonaDocumentoTramite.withCriteria {
            if (params.fechaDesde) {
                ge('fechaEnvio', params.fechaDesde)
            }
            if (params.fechaHasta) {
                le('fechaEnvio', params.fechaHasta)
            }
            isNotNull("fechaEnvio")
            or {
                eq("rolPersonaTramite", rolPara)
                eq("rolPersonaTramite", rolCopia)
            }
            tramite {
                if (params.asunto) {
                    ilike('asunto', '%' + params.asunto + '%')
                }
                if (params.memorando) {
                    ilike('codigo', '%' + params.memorando + '%')
                }
                if (persona.esTriangulo()) {
                    eq('deDepartamento', departamento)
                } else {
                    and {
                        eq('de', persona)
                        isNull("deDepartamento")
                    }
                }
//                order('codigo', 'desc')
//                order('estadoTramite', 'desc')
                order("fechaCreacion", "desc")
            }
            maxResults(20)
        }
        return [tramites: res.unique()]
    }

    def busquedaArchivados() {

    }

    def tablaBusquedaArchivados() {

        def persona = Persona.get(session.usuario.id)
        def departamento = persona?.departamento
        def res

        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');

        if (params.fecha) {
            params.fechaFin = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 23:59:59")
        }

        if (params.fechaRecepcion) {
            params.fechaIni = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fechaRecepcion + " 00:00:00")
        }

        //old
//        def pxtPara = PersonaDocumentoTramite.withCriteria {
//            eq("departamento", departamento)
//            eq("rolPersonaTramite", rolPara)
//            eq('estado', EstadoTramite.findByCodigo("E005"))
//            isNotNull("fechaEnvio")
//
//            or {
//                eq("estado", EstadoTramite.findByCodigo("E003")) //enviado
//                eq("estado", EstadoTramite.findByCodigo("E007")) //enviado al jefe
//                eq("estado", EstadoTramite.findByCodigo("E004")) //recibido
//                eq('estado', EstadoTramite.findByCodigo("E005"))
//            }
//
//            maxResults(20);
//
//        }
//        def pxtCopia = PersonaDocumentoTramite.withCriteria {
//            eq("departamento", departamento)
//            eq("rolPersonaTramite", rolCopia)
//            eq('estado', EstadoTramite.findByCodigo("E005"))
//            isNotNull("fechaEnvio")
//
//            or {
//                eq("estado", EstadoTramite.findByCodigo("E003")) //enviado
//                eq("estado", EstadoTramite.findByCodigo("E007")) //enviado al jefe
//                eq("estado", EstadoTramite.findByCodigo("E004")) //recibido
//                eq('estado', EstadoTramite.findByCodigo("E005"))
//            }
//
//            maxResults(20);
//
//        }
//
//        pxtPara += pxtCopia
//
//
//
//        if (params.fecha) {
//            params.fechaFin = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 23:59:59")
//        }
//
//        if (params.fechaRecepcion) {
//            params.fechaIni = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fechaRecepcion + " 00:00:00")
//        }
//
//
//        def res
//
//        if (Persona.get(session.usuario.id).esTriangulo()) {
//
//            res = PersonaDocumentoTramite.withCriteria {
//
//                if (params.fecha) {
//                    gt('fechaEnvio', params.fechaIni)
//                    lt('fechaEnvio', params.fechaFin)
//                }
//
//                eq('estado', EstadoTramite.findByCodigo("E005"))
//                isNotNull("fechaEnvio")
//
//                or {
//                    eq("rolPersonaTramite", rolPara)
//                    eq("rolPersonaTramite", rolCopia)
//                }
//
//
//                tramite {
//                    if (params.asunto) {
//                        ilike('asunto', '%' + params.asunto + '%')
//                    }
//                    if (params.memorando) {
//                        ilike('codigo', '%' + params.memorando + '%')
//                    }
//
//                    eq('deDepartamento', departamento)
//                    order('codigo', 'desc')
//                    order('estadoTramite', 'desc')
//
//                }
//
//                maxResults(20);
//
//            }
//
//
//        } else {
//            res = PersonaDocumentoTramite.withCriteria {
//
//                if (params.fecha) {
//                    gt('fechaEnvio', params.fechaIni)
//                    lt('fechaEnvio', params.fechaFin)
//                }
//
//                eq('estado', EstadoTramite.findByCodigo("E005"))
//                isNotNull("fechaEnvio")
//
//                or {
//                    eq("rolPersonaTramite", rolPara)
//                    eq("rolPersonaTramite", rolCopia)
//                }
//
//
//                tramite {
//                    if (params.asunto) {
//                        ilike('asunto', '%' + params.asunto + '%')
//                    }
//                    if (params.memorando) {
//                        ilike('codigo', '%' + params.memorando + '%')
//                    }
//
//                    eq('de', persona)
//                    order('codigo', 'desc')
//                    order('estadoTramite', 'desc')
//
//                }
//
//                maxResults(20);
//
//            }
//        }

        //new


        res = PersonaDocumentoTramite.withCriteria {


            if (persona?.esTriangulo()) {
                eq("departamento", departamento)
                eq("estado", EstadoTramite.findByCodigo("E005"))
                inList("rolPersonaTramite", rolPara, rolCopia)
            } else {
                eq("persona", persona)
                eq("estado", EstadoTramite.findByCodigo("E005"))
                inList("rolPersonaTramite", rolPara, rolCopia)
            }

            if (params.fechaIni) {
                ge('fechaArchivo', params.fechaIni)

            }
            if (params.fechaFin) {
                le('fechaArchivo', params.fechaFin)
            }

            isNotNull("fechaArchivo")

            tramite {
                if (params.asunto) {
                    ilike('asunto', '%' + params.asunto + '%')
                }
                if (params.memorando) {
                    ilike('codigo', '%' + params.memorando + '%')
                }

//                order('codigo', 'desc')
            }
            maxResults(20)
        }

        def tramitesFiltrados = res.unique()
        tramitesFiltrados.tramite.sort { it.codigo }
        def msg = ""
        if (tramitesFiltrados.size() > 20) {
            tramitesFiltrados = tramitesFiltrados[0..19]
            msg = "<div class='alert alert-danger'> <i class='fa fa-warning fa-2x pull-left'></i> Su búsqueda ha generado más de 20 resultados. Por favor utilice los filtros.</div>"
        }

//        return [tramites: res.unique(), pxtTramites: pxtPara]
        return [tramites: tramitesFiltrados]
   }

    def busquedaAnulados() {


    }


    def tablaBusquedaAnulados() {

        def persona = Persona.get(session.usuario.id)
        def departamento = persona?.departamento
        def pxtPara
        def pxtCopia
        def res

        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');

        //nuevo

        if (params.fechaDesde) {
            params.fechaDesde = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fechaDesde + " 00:00:00")
        }

        if (params.fechaHasta) {
            params.fechaHasta = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fechaHasta + " 23:59:59")
        }


        res = PersonaDocumentoTramite.withCriteria {
            eq("estado", EstadoTramite.findByCodigo("E006"))
            if (persona?.esTriangulo()) {
                tramite {
                    eq("deDepartamento", departamento)
                }
            } else {
                tramite {
                    and {
                        eq("de", persona)
                        isNull("deDepartamento")
                    }
                }
            }
            if (params.fechaDesde) {
                ge('fechaAnulacion', params.fechaDesde)
            }
            if (params.fechaHasta) {
                le('fechaAnulacion', params.fechaHasta)
            }
            isNotNull("fechaAnulacion")
            tramite {
                if (params.asunto) {
                    ilike('asunto', '%' + params.asunto + '%')
                }
                if (params.memorando) {
                    ilike('codigo', '%' + params.memorando + '%')
                }
//                order('codigo', 'desc')
            }
            maxResults(20)
        }



        def tramitesFiltrados = res.unique()
        tramitesFiltrados.tramite.sort { it.codigo }
        def msg = ""
        if (tramitesFiltrados.size() > 20) {
            tramitesFiltrados = tramitesFiltrados[0..19]
            msg = "<div class='alert alert-danger'> <i class='fa fa-warning fa-2x pull-left'></i> Su búsqueda ha generado más de 20 resultados. Por favor utilice los filtros.</div>"
        }

//old
//        if(persona?.esTriangulo()){
//            pxtPara = PersonaDocumentoTramite.withCriteria {
////                eq("departamento", departamento)
//                eq("rolPersonaTramite", rolPara)
//                eq('estado', EstadoTramite.findByCodigo("E006"))
////                isNotNull("fechaEnvio")
//
//                tramite {
//
//                    eq('deDepartamento', departamento)
//
//                }
//
//                maxResults(20);
//
//            }
//            pxtCopia = PersonaDocumentoTramite.withCriteria {
////            eq("departamento", departamento)
//                eq("rolPersonaTramite", rolCopia)
//                eq('estado', EstadoTramite.findByCodigo("E006"))
////                isNotNull("fechaEnvio")
//
//                tramite {
//
//                    eq ('deDepartamento', departamento)
//                }
//
//
//                maxResults(20);
//            }
//        }else{
//
//            pxtPara = PersonaDocumentoTramite.withCriteria {
//                eq("persona", persona)
//                eq("rolPersonaTramite", rolPara)
//                eq('estado', EstadoTramite.findByCodigo("E006"))
////                isNotNull("fechaEnvio")
//
//                tramite {
//
//                    eq('de', persona)
//                }
//
//                maxResults(20);
//
//            }
//            pxtCopia = PersonaDocumentoTramite.withCriteria {
////                eq("persona", persona)
//                eq("rolPersonaTramite", rolCopia)
//                eq('estado', EstadoTramite.findByCodigo("E006"))
////                isNotNull("fechaEnvio")
//
//
//                tramite {
//                    eq('de', persona)
//                }
//
//                maxResults(20);
//            }
//        }

//        pxtPara += pxtCopia

//        if (params.fechaDesde) {
//            params.fechaDesde = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fechaDesde + " 00:00:00")
//        }
//
//        if (params.fechaHasta) {
//            params.fechaHasta = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fechaHasta + " 23:59:59")
//        }
//
//
//        if (persona.esTriangulo()) {
//
//            res = PersonaDocumentoTramite.withCriteria {
//
//                if (params.fechaDesde) {
//                    ge('fechaAnulacion', params.fechaDesde)
//
//                }
//                if(params.fechaHasta){
//                    le('fechaAnulacion', params.fechaHasta)
//                }
//
//                eq('estado', EstadoTramite.findByCodigo("E006"))
//
//                isNotNull("fechaAnulacion")
//
//                or {
//                    eq("rolPersonaTramite", rolPara)
//                    eq("rolPersonaTramite", rolCopia)
//                }
//
//
//                tramite {
//                    if (params.asunto) {
//                        ilike('asunto', '%' + params.asunto + '%')
//                    }
//                    if (params.memorando) {
//                        ilike('codigo', '%' + params.memorando + '%')
//                    }
//
//                    eq('deDepartamento', departamento)
//                    order('codigo', 'desc')
//                    order('estadoTramite', 'desc')
//
//                }
//
//                maxResults(20);
//
//            }
//
//
//        } else {
//
//            res = PersonaDocumentoTramite.withCriteria {
//
//                if (params.fechaDesde) {
//                    ge('fechaAnulacion', params.fechaDesde)
//
//                }
//                if(params.fechaHasta){
//                    le('fechaAnulacion', params.fechaHasta)
//                }
//
//                eq('estado', EstadoTramite.findByCodigo("E006"))
//
//                isNotNull("fechaAnulacion")
//
//                or {
//                    eq("rolPersonaTramite", rolPara)
//                    eq("rolPersonaTramite", rolCopia)
//                }
//
//
//                tramite {
//                    if (params.asunto) {
//                        ilike('asunto', '%' + params.asunto + '%')
//                    }
//                    if (params.memorando) {
//                        ilike('codigo', '%' + params.memorando + '%')
//                    }
//
//                    eq('de', persona)
//                    order('codigo', 'desc')
//                    order('estadoTramite', 'desc')
//
//                }
//
//                maxResults(20);
//
//            }
//
//
//        }

//        println("res " + res)

//        return [tramites: res.unique(), pxtTramites: pxtPara]
        return [tramites: tramitesFiltrados]


    }

}
