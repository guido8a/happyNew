package happy.tramites

import groovy.time.TimeCategory
import happy.seguridad.Persona
import happy.utilitarios.Parametros


class BuscarTramiteController extends happy.seguridad.Shield {


    def dbConnectionService
    def tramitesService

    def verificarAgregarDoc() {
        def tramite = Tramite.get(params.id)
        def persona = Persona.get(session.usuario.id)
        def esDepartamento = persona.esTriangulo

        if (!esDepartamento && persona == tramite.de) {
//            println "1.1: " + persona
            render "OK"
            return
        }
        if (esDepartamento && tramite.deDepartamento == persona.departamento) {
//            println "1.2: " + persona.departamento
            render "OK"
            return
        }
        def principal = tramite
        while (principal.padre) {
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
//        def inicio = new Date()
        def persona = session.usuario.id

        if (params.fecha) {
            params.fechaIni = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 00:00:00")
            params.fechaFin = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 23:59:59")
        }

        if (params.fechaRecepcion) {
            params.fechaIniR = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fechaRecepcion + " 00:00:00")
            params.fechaFinR = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fechaRecepcion + " 23:59:59")
        }

        def res

        def estadoArchivado = EstadoTramite.findByCodigo('E005')
        def estadoAnulado = EstadoTramite.findByCodigo('E006')

        res = PersonaDocumentoTramite.withCriteria {
            if (!session.usuario.puedeAdmin) {
//                isNotNull("fechaEnvio")
//                and {
//                    ne('estado', estadoArchivado)
//                    ne('estado', estadoAnulado)
//                }
            }
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
                if (params.fechaRecepcion) {
                    gt('fechaCreacion', params.fechaIniR)
                    lt('fechaCreacion', params.fechaFinR)
                }
                order('codigo')
            }
            maxResults(200)
        }
//        println "registros....: ${res.size()}"
        def tramitesFiltrados = res.tramite.unique()
//        println "registros.... filtrados: ${tramitesFiltrados.size()}"
        tramitesFiltrados.sort { it.codigo }
        def msg = ""
        if (tramitesFiltrados.size() > 20) {
            tramitesFiltrados = tramitesFiltrados[0..19]
            msg = "<div class='alert alert-danger'> <i class='fa fa-warning fa-2x pull-left'></i> Su búsqueda ha generado más de 20 resultados. Por favor utilice los filtros.</div>"
        }

//        def fin = new Date()
//        println "${TimeCategory.minus(fin, inicio)}"

        return [tramites: tramitesFiltrados, persona: persona, msg: msg]
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
