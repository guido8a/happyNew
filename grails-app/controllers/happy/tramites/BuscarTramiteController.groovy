package happy.tramites

class BuscarTramiteController  extends happy.seguridad.Shield {

//    def index() {
//
//    }

    def busquedaTramite () {


    }

    def tablaBusquedaTramite () {

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

        return [tramites: res]


    }

}
