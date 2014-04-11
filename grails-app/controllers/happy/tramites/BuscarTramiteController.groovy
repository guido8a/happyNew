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

//        println("params" + params)

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

            if(params.fechaRecepcion){
                gt('fechaRecepcion', params.fechaIniR)
                lt('fechaRecepcion', params.fechaFinR)



            }

            tramite {
                if (params.asunto) {
                    ilike('asunto', '%' + params.asunto + '%')
                }
                if (params.memorando) {
                    ilike('codigo', '%' + params.memorando + '%')
                }
            }
        }

        def idTramite

        res.each {
            idTramite = it.tramite
        }


        def filtro = resTramites(idTramite);

        return [tramites: res, resTramites: filtro]

    }

    def resTramites (Tramite tramite) {

        def sql = ""

        def result = []

        def cn = dbConnectionService.getConnection();

        sql = "select * from tramites(" + tramite.id + ") "
//        def res1 = cn.rows(sql.toString())
//        def res1 = cn.eachRow(sql.toString()){
//
//            println(it)
//        }

        cn.eachRow(sql) { r ->
            result.add(r.toRowResult())
        }
//        cn.eachRow(sql.toString()) { r ->
//           println(r)
//        }
//        cn.close()

//        println("res: " + res1)

        return result

    }

}
