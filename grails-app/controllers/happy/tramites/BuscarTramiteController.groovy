package happy.tramites



class BuscarTramiteController  extends happy.seguridad.Shield {

//    def index() {
//
//    }

    def dbConnectionService

    def busquedaTramite () {


    }

    def tablaBusquedaTramite () {

//        def tramite1 = Tramite.get(50)

        println("params" + params)

        if (params.fecha) {
            params.fechaIni = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha+" 00:00:00")
            params.fechaFin = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha+" 23:59:59")
        }

        if(params.fechaRecepcion){
            params.fechaIniR = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fechaRecepcion+" 00:00:00")
            params.fechaFinR = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fechaRecepcion+" 23:59:59")
        }

        def res = PersonaDocumentoTramite.withCriteria {


            if (params.fecha) {
                gt('fechaEnvio', params.fechaIni)
                lt('fechaEnvio', params.fechaFin)
            }

//            if(params.fechaRecepcion){
//                gt('fechaRecepcion', params.fechaIniR)
//                lt('fechaRecepcion', params.fechaFinR)
//
//
//
//            }

            tramite {
                if (params.asunto) {
                    ilike('asunto', '%' + params.asunto + '%')
                }
                if (params.memorando) {
                    ilike('codigo', '%' + params.memorando + '%')
                }
                if(params.fechaRecepcion){
                    gt('fechaCreacion', params.fechaIniR)
                    lt('fechaCreacion', params.fechaFinR)
                }

            }
        }

        def filtro = []
        def unicos = []
        def tramitesFiltrados = []

        res.each {
            filtro += it.tramite
        }

        filtro.unique().each {
//            unicos += it
            tramitesFiltrados += resTramites(it);
        }

//        println("ids" + res)
//        println("filtro:" + unicos)
//        println("filtro:" + tramitesFiltrados)


//        return [tramites: res, resTramites: filtro]
        return [tramites: tramitesFiltrados]

    }

    def resTramites (Tramite tramite) {

        def sql = ""

        def result = []
        def idsUnicos = []

        def cn = dbConnectionService.getConnection();

        sql = "select * from tramites(" + tramite.id + ") "

        cn.eachRow(sql) { r ->
//            println(">>>>>" + r)
            result.add(r.toRowResult())
        }

//        println("res: " + result)


        return result

    }

    def busquedaEnviados () {


    }

    def tablaBusquedaEnviados () {


    }

}
