package happy.tramites

import happy.seguridad.Persona


class BuscarTramiteController extends happy.seguridad.Shield {

//    def index() {
//
//    }

    def dbConnectionService

    def busquedaTramite() {
    }

    def ampliarPlazo_ajax() {
        def error = ""
        params.each { k, v ->
            if (k.contains("input")) {
                def parts = k.split("_")
                def persDocId = parts[1]
                def persDocTram = PersonaDocumentoTramite.get(persDocId)
                def fecha = new Date().parse("dd-MM-yyyy HH:mm", v.toString() + " " + persDocTram.fechaLimiteRespuesta.format("HH:mm"))

                persDocTram.fechaLimiteRespuesta = fecha
                if (!persDocTram.save(flush: true)) {
                    error += renderErrors(bean: persDocTram)
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

        println "****************************"
        println dpto.id

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

//        def tramite1 = Tramite.get(50)
//        println("params tablaBusquedaTramite:" + params)

        def persona = session.usuario.id
        def estadoAnulado = EstadoTramite.findByCodigo("E006")

        if (params.fecha) {
            params.fechaIni = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 00:00:00")
            params.fechaFin = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 23:59:59")
        }

        if (params.fechaRecepcion) {
            params.fechaIniR = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fechaRecepcion + " 00:00:00")
            params.fechaFinR = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fechaRecepcion + " 23:59:59")
        }

        def res

//        if (Persona.get(session.usuario.id).esTriangulo()) {
//
//            res = PersonaDocumentoTramite.withCriteria {
////                eq("departamento", Persona.get(session.usuario.id).departamento)
//                ne('estado', EstadoTramite.findByCodigo("E006"))
//                if (params.fecha) {
//                    gt('fechaEnvio', params.fechaIni)
//                    lt('fechaEnvio', params.fechaFin)
//                }
//                tramite {
//                    if (params.asunto) {
//                        ilike('asunto', '%' + params.asunto + '%')
//                    }
//                    if (params.memorando) {
//                        ilike('codigo', '%' + params.memorando + '%')
//                    }
//                    if (params.fechaRecepcion) {
//                        gt('fechaCreacion', params.fechaIniR)
//                        lt('fechaCreacion', params.fechaFinR)
//                    }
//                    order('codigo')
//                }
//                maxResults(20);
//            }
//
//        } else {
//            res = PersonaDocumentoTramite.withCriteria {
////                eq("persona", Persona.get(session.usuario.id))
//                ne('estado', EstadoTramite.findByCodigo("E006"))
//                if (params.fecha) {
//                    gt('fechaEnvio', params.fechaIni)
//                    lt('fechaEnvio', params.fechaFin)
//                }
//                tramite {
//                    if (params.asunto) {
//                        ilike('asunto', '%' + params.asunto + '%')
//                    }
//                    if (params.memorando) {
//                        ilike('codigo', '%' + params.memorando + '%')
//                    }
//                    if (params.fechaRecepcion) {
//                        gt('fechaCreacion', params.fechaIniR)
//                        lt('fechaCreacion', params.fechaFinR)
//                    }
//                    order('codigo')
//                }
//                maxResults(20);
//            }
//        }

        println session.usuario.puedeAdmin

        res = PersonaDocumentoTramite.withCriteria {

            if (!session.usuario.puedeAdmin) {
                isNotNull("fechaEnvio")
                and{
                    ne('estado', EstadoTramite.findByCodigo('E005'))
                    ne('estado', EstadoTramite.findByCodigo('E006'))
                }
            }else{

                or{
                    ne('estado', estadoAnulado)
                    ne('estado', EstadoTramite.findByCodigo('E005'))
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
            maxResults(20);
        }

//println("res" + res)

        def filtro = []
        def unicos = []
        def tramitesFiltrados = []

        res.each {
//            println("-->" + it?.estado?.codigo)
//            if(!session.usuario.puedeAdmin){
//                if(it?.estado?.codigo == 'E005' || it?.estado?.codigo == 'E006'){
//
//                }
//            else{
            filtro += it.tramite
//            }
//            }

        }

        filtro.unique().each {
//            unicos += it
            tramitesFiltrados += resTramites(it);
        }

//        println("ids" + res)
//        println("filtro:" + filtro)
//        println("filtrados:" + tramitesFiltrados)

        tramitesFiltrados.sort { it.trmtcdgo }
        //println " tramites "+tramitesFiltrados
//        return [tramites: res, resTramites: filtro]
        return [tramites: tramitesFiltrados, persona: persona]

    }

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
            if (pers.jefe == 1) {
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
