package happy.tramites

import happy.seguridad.Persona


class BuscarTramiteController extends happy.seguridad.Shield {

//    def index() {
//
//    }

    def dbConnectionService
    def tramitesService

    //solo si la persona ha enviado un tramite de la cadena puede agregar doc al tramite
    def verificarAgregarDoc() {
        def tramite = Tramite.get(params.id)
        def persona = Persona.get(session.usuario.id)
        def esDepartamento = persona.esTriangulo

        if (!esDepartamento && persona == tramite.de) {
            println "1.1: " + persona
            render "OK"
            return
        }
        if (esDepartamento && tramite.deDepartamento == persona.departamento) {
            println "1.2: " + persona.departamento
            render "OK"
            return
        }
        def principal = tramite
        while (principal.padre) {
            principal = principal.padre
            if (!esDepartamento && persona == principal.de) {
                println "2.1: " + persona
                render "OK"
                return
            }
            if (esDepartamento && principal.deDepartamento == persona.departamento) {
                println "2.2: " + persona.departamento
                render "OK"
                return
            }
        }
        // TODO: preguntar si es la cadena del tramite principal o tambien de los asociados
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
//                render "OK"
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
                    puede = hijosPdt(para, persona, esDepartamento, puede)
                }
            }

            if (!puede) {
                ccs.each { para ->
                    if (!puede) {
                        puede = hijosPdt(para, persona, esDepartamento, puede)
                    }
                }
            }
        }
//        println "hijosTramite: " + puede
        return puede
    }

    def hijosPdt(PersonaDocumentoTramite pdt, Persona persona, boolean esDepartamento, boolean puede) {
        if (!puede) {
            def hijos = Tramite.findAllByAQuienContesta(pdt, [sort: "fechaCreacion", order: "asc"])

            hijos.each { h ->
                if (!esDepartamento && h.de == persona) {
                    println "3.1: " + persona
                    puede = true
                }
                if (esDepartamento && h.deDepartamento == persona.departamento) {
                    println "3.2: " + persona.departamento
                    puede = true
                }
                if (!puede) {
                    puede = hijosTramite(h, persona, esDepartamento, puede)
                }
            }
        }
//        println "hijosPdt: " + puede
        return puede
    }

    def busquedaTramite() {
    }

//    def busquedaTramite_old() {
//    }

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
                        para = "para el dpto. " + persDocTram.departamento.codigo
                    } else if (persDocTram.persona) {
                        para = "para el usuario " + persDocTram.persona.login
                    }
                    para += " (${persDocTram.rolPersonaTramite.descripcion})"

                    def l = " hasta: ${fecha.format('dd-MM-yyyy HH:mm')}, " +
                            "plazo anterior: ${persDocTram.fechaLimiteRespuesta.format('dd-MM-yyyy HH:mm')}"
                    def log = "Ampliado el plazo" + l
                    def log2 = "Ampliado el plazo ${para}" + l
//                    persDocTram.observaciones = tramitesService.makeObservaciones(persDocTram.observaciones, log, params.aut, session.usuario.login)
//                    persDocTram.tramite.observaciones = tramitesService.makeObservaciones(persDocTram.tramite.observaciones, log2, params.aut, session.usuario.login)
//                persDocTram.observaciones = tramitesService.modificaObservaciones(persDocTram.observaciones, log)
//                persDocTram.tramite.observaciones = tramitesService.modificaObservaciones(persDocTram.tramite.observaciones, log2)

                    //(String observacionOriginal, String accion, String solicitadoPor, String usuario, String texto, String nuevaObservacion)
                    def observacionOriginal = persDocTram.observaciones
                    def accion = "Ampliación de plazo"
                    def solicitadoPor = ""
                    def usuario = session.usuario.login
                    def texto = log
                    def nuevaObservacion = ""
                    persDocTram.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
                    observacionOriginal = persDocTram.tramite.observaciones
                    texto = log2
                    persDocTram.tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

                    persDocTram.fechaLimiteRespuesta = fecha
//                    println persDocTram.id
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

//        println "****************************"
//        println dpto.id

        def personas = PersonaDocumentoTramite.withCriteria {
            eq("tramite", tramite)
            or {
                eq("rolPersonaTramite", rolPara)
                eq("rolPersonaTramite", rolCc)
            }
        }

//        println personas.persona.departamento.id
//        println personas.departamento.id
//        println "****************************"

        return [tramite: tramite, jefe: jefe, personas: personas, dpto: dpto]
    }

    def tablaBusquedaTramite() {

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

//        println "#####################################################"
//        println params
//        println params.fecha
//        println params.fechaRecepcion
//        println session.usuario.puedeAdmin
//        println "#####################################################"

        res = PersonaDocumentoTramite.withCriteria {
            if (!session.usuario.puedeAdmin) {
                isNotNull("fechaEnvio")
                and {
                    ne('estado', estadoArchivado)
                    ne('estado', estadoAnulado)
                }
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
//        println res.tramite
        def tramitesFiltrados = res.tramite.unique()
        tramitesFiltrados.sort { it.codigo }
        def msg = ""
        if (tramitesFiltrados.size() > 20) {
            tramitesFiltrados = tramitesFiltrados[0..19]
            msg = "<div class='alert alert-danger'> <i class='fa fa-warning fa-2x pull-left'></i> Su búsqueda ha generado más de 20 resultados. Por favor utilice los filtros.</div>"
        }
        return [tramites: tramitesFiltrados, persona: persona, msg: msg]
    }

//    def tablaBusquedaTramite_old() {
//
////        def tramite1 = Tramite.get(50)
////        println("params tablaBusquedaTramite:" + params)
//
//        def persona = session.usuario.id
//
//        if (params.fecha) {
//            params.fechaIni = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 00:00:00")
//            params.fechaFin = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 23:59:59")
//        }
//
//        if (params.fechaRecepcion) {
//            params.fechaIniR = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fechaRecepcion + " 00:00:00")
//            params.fechaFinR = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fechaRecepcion + " 23:59:59")
//        }
//
//        def res
//
////        if (Persona.get(session.usuario.id).esTriangulo()) {
////
////            res = PersonaDocumentoTramite.withCriteria {
//////                eq("departamento", Persona.get(session.usuario.id).departamento)
////                ne('estado', EstadoTramite.findByCodigo("E006"))
////                if (params.fecha) {
////                    gt('fechaEnvio', params.fechaIni)
////                    lt('fechaEnvio', params.fechaFin)
////                }
////                tramite {
////                    if (params.asunto) {
////                        ilike('asunto', '%' + params.asunto + '%')
////                    }
////                    if (params.memorando) {
////                        ilike('codigo', '%' + params.memorando + '%')
////                    }
////                    if (params.fechaRecepcion) {
////                        gt('fechaCreacion', params.fechaIniR)
////                        lt('fechaCreacion', params.fechaFinR)
////                    }
////                    order('codigo')
////                }
////                maxResults(20);
////            }
////
////        } else {
////            res = PersonaDocumentoTramite.withCriteria {
//////                eq("persona", Persona.get(session.usuario.id))
////                ne('estado', EstadoTramite.findByCodigo("E006"))
////                if (params.fecha) {
////                    gt('fechaEnvio', params.fechaIni)
////                    lt('fechaEnvio', params.fechaFin)
////                }
////                tramite {
////                    if (params.asunto) {
////                        ilike('asunto', '%' + params.asunto + '%')
////                    }
////                    if (params.memorando) {
////                        ilike('codigo', '%' + params.memorando + '%')
////                    }
////                    if (params.fechaRecepcion) {
////                        gt('fechaCreacion', params.fechaIniR)
////                        lt('fechaCreacion', params.fechaFinR)
////                    }
////                    order('codigo')
////                }
////                maxResults(20);
////            }
////        }
//
////        println "session.usuario.puedeAdmin: " + session.usuario.puedeAdmin
//
//        def estadoArchivado = EstadoTramite.findByCodigo('E005')
//        def estadoAnulado = EstadoTramite.findByCodigo('E006')
//
//        res = PersonaDocumentoTramite.withCriteria {
//            if (!session.usuario.puedeAdmin) {
//                isNotNull("fechaEnvio")
//                and {
//                    ne('estado', estadoArchivado)
//                    ne('estado', estadoAnulado)
//                }
//            }/* else {
//                or {
//                    ne('estado', estadoAnulado)
//                    ne('estado', estadoArchivado)
//                }
//            }*/
//            if (params.fecha) {
//                gt('fechaEnvio', params.fechaIni)
//                lt('fechaEnvio', params.fechaFin)
//            }
//            tramite {
//                if (params.asunto) {
//                    ilike('asunto', '%' + params.asunto + '%')
//                }
//                if (params.memorando) {
//                    ilike('codigo', '%' + params.memorando + '%')
//                }
//                if (params.fechaRecepcion) {
//                    gt('fechaCreacion', params.fechaIniR)
//                    lt('fechaCreacion', params.fechaFinR)
//                }
//                order('codigo')
//            }
//            maxResults(20);
//        }
//
////println("res" + res)
//
//        def filtro = []
//        def unicos = []
//        def tramitesFiltrados = []
//
//        res.each {
////            println("-->" + it?.estado?.codigo)
////            if(!session.usuario.puedeAdmin){
////                if(it?.estado?.codigo == 'E005' || it?.estado?.codigo == 'E006'){
////
////                }
////            else{
//            filtro += it.tramite
////            }
////            }
//
//        }
//
//        filtro.unique().each {
////            unicos += it
//            tramitesFiltrados += resTramites(it);
//        }
//
////        println("ids" + res)
////        println("filtro:" + filtro)
////        println("filtrados:" + tramitesFiltrados)
//
//        tramitesFiltrados.sort { it.trmtcdgo }
//        //println " tramites "+tramitesFiltrados
////        return [tramites: res, resTramites: filtro]
//        return [tramites: tramitesFiltrados, persona: persona]
//
//    }

    def resTramites(Tramite tramite) {

        def sql = ""

        def result = []
        def idsUnicos = []

        def cn = dbConnectionService.getConnection();

        sql = "select * from tramites(" + tramite.id + ") "
//        println "........" + sql
        cn.eachRow(sql) { r ->
//            println(">>>>>" + r)
            result.add(r.toRowResult())
        }

        return result

    }

    def busquedaEnviados() {
    }

    def tablaBusquedaEnviados() {

//        println("params" + params)

        if (params.fecha) {
            params.fechaFin = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 23:59:59")
        }

        if (params.fechaRecepcion) {
            params.fechaIni = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fechaRecepcion + " 00:00:00")
        }

        def pers = Persona.get(session.usuario.id)

        def res

        res = Tramite.withCriteria {
            if (params.fecha) {
                ge('fechaEnvio', params.fechaIni)
                le('fechaEnvio', params.fechaFin)
            }
            if (params.asunto) {
                ilike('asunto', '%' + params.asunto + '%')
            }
            if (params.memorando) {
                ilike('codigo', '%' + params.memorando + '%')
            }
            if (pers.puedeJefe) {
                inList('de', Persona.findAllByDepartamento(pers.departamento))
                inList('estadoTramite', EstadoTramite.findAllByCodigoInList(["E003", "E004"]))
            } else {
                eq('de', pers)
                inList('estadoTramite', EstadoTramite.findAllByCodigoInList(["E003", "E004"]))
            }
            maxResults(20);
            order("estadoTramite", 'desc')
            order('codigo', 'desc')
        }



        return [persona: pers, tramites: res]
    }

    def busquedaArchivados() {

    }

    def tablaBusquedaArchivados() {

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

            maxResults(20);

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

            maxResults(20);

        }

        pxtPara += pxtCopia



        if (params.fecha) {
            params.fechaFin = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 23:59:59")
        }

        if (params.fechaRecepcion) {
            params.fechaIni = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fechaRecepcion + " 00:00:00")
        }


        def res

        if (Persona.get(session.usuario.id).esTriangulo()) {

            res = PersonaDocumentoTramite.withCriteria {

                if (params.fecha) {
                    gt('fechaEnvio', params.fechaIni)
                    lt('fechaEnvio', params.fechaFin)
                }

//            eq("departamento", departamento)

                eq('estado', EstadoTramite.findByCodigo("E005"))
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

                    eq('deDepartamento', departamento)
                    order('codigo', 'desc')
                    order('estadoTramite', 'desc')

                }

                maxResults(20);

            }


        } else {
            res = PersonaDocumentoTramite.withCriteria {

                if (params.fecha) {
                    gt('fechaEnvio', params.fechaIni)
                    lt('fechaEnvio', params.fechaFin)
                }

//            eq("departamento", departamento)

                eq('estado', EstadoTramite.findByCodigo("E005"))
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

                    eq('de', persona)
                    order('codigo', 'desc')
                    order('estadoTramite', 'desc')

                }

                maxResults(20);

            }
        }

//        println("filtrados" + res)

        return [tramites: res, pxtTramites: pxtPara]


    }

    def busquedaAnulados() {


    }


    def tablaBusquedaAnulados() {

        def persona = Persona.get(session.usuario.id)
        def departamento = persona?.departamento

//        println("departamento" + departamento)


        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');

        def pxtPara = PersonaDocumentoTramite.withCriteria {
            eq("departamento", departamento)
            eq("rolPersonaTramite", rolPara)
            eq('estado', EstadoTramite.findByCodigo("E006"))
            isNotNull("fechaEnvio")

            maxResults(20);

        }
        def pxtCopia = PersonaDocumentoTramite.withCriteria {
            eq("departamento", departamento)
            eq("rolPersonaTramite", rolCopia)
            eq('estado', EstadoTramite.findByCodigo("E006"))
            isNotNull("fechaEnvio")


            maxResults(20);
        }

        pxtPara += pxtCopia

//        println("---" + pxtPara)

        def res


        if (params.fecha) {
            params.fechaFin = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 23:59:59")
        }

        if (params.fechaRecepcion) {
            params.fechaIni = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fechaRecepcion + " 00:00:00")
        }


        if (Persona.get(session.usuario.id).esTriangulo()) {

            res = PersonaDocumentoTramite.withCriteria {

                if (params.fecha) {
                    gt('fechaEnvio', params.fechaIni)
                    lt('fechaEnvio', params.fechaFin)
                }

//                eq("departamento", departamento)
                eq('estado', EstadoTramite.findByCodigo("E006"))

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

                    eq('deDepartamento', departamento)
                    order('codigo', 'desc')
                    order('estadoTramite', 'desc')

                }

                maxResults(20);

            }


        } else {

            res = PersonaDocumentoTramite.withCriteria {

                if (params.fecha) {
                    gt('fechaEnvio', params.fechaIni)
                    lt('fechaEnvio', params.fechaFin)
                }

//                eq("departamento", departamento)
                eq('estado', EstadoTramite.findByCodigo("E006"))

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

                    eq('de', persona)
                    order('codigo', 'desc')
                    order('estadoTramite', 'desc')

                }

                maxResults(20);

            }


        }

//       res = PersonaDocumentoTramite.withCriteria {
//
//            if (params.fecha) {
//                gt('fechaEnvio', params.fechaIni)
//                lt('fechaEnvio', params.fechaFin)
//            }
//
//            eq("departamento", departamento)
//            eq('estado', EstadoTramite.findByCodigo("E006"))
//
//            isNotNull("fechaEnvio")
//
//            or{
//                eq("rolPersonaTramite", rolPara)
//                eq("rolPersonaTramite", rolCopia)
//            }
//
//
//            tramite {
//                if (params.asunto) {
//                    ilike('asunto', '%' + params.asunto + '%')
//                }
//                if (params.memorando) {
//                    ilike('codigo', '%' + params.memorando + '%')
//                }
//
//                eq('de', persona)
//                order ('codigo', 'desc')
//                order ('estadoTramite', 'desc')
//
//            }
//
//            maxResults(20);
//
//        }

        println("res" + res + ' ' + res.unique())

        return [tramites: res.unique(), pxtTramites: pxtPara]


    }

}
