package happy.tramites

class BusquedaExternosController {

    def index() {}

    def buscarExternos () {



    }

    def tablaBusquedaExternos () {
//           println("params:" + params)

        def res
        def filtrados = []

        if(!params.contacto && !params.numero && !params.codigo && !params.institucion ){

            res = []
        }else{
            res = Tramite.withCriteria {

                if(params.contacto){
                    ilike('contacto', '%' + params.contacto + '%')
                }
                if(params.codigo){
                      ilike('codigo', '%' + params.codigo + '%')
                }
                if(params.numero){
                    ilike('numeroDocExterno', '%' + params.numero + '%')
                }
                if(params.institucion){
                    ilike('paraExterno', '%' + params.institucion + '%')
                }

            }
        }
//            println("res:" + res)

            if(res){
                res.each {
//                    println("externo:" + it.externo)
                    if(it.externo == '1'){
                        filtrados += it
                    }

                }
            }

//            println("filtrados:" + filtrados)

        return [tramites: filtrados]
}

}
