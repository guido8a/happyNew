package happy.utilitarios

import grails.transaction.Transactional
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.Tramite

@Transactional
class TramitesService {

    /*Verifica toda la cadena del tramite en busca de un estado, retorna true si  encontro un personaDocumentoTramite que no es del estado que recibe como parametro*/
    Boolean verificaHijos(pdt,estado){
        def hijos = Tramite.findAllByAQuienContesta(pdt)
        def res = false
//        println "tramite ver hijos "+hijos
        hijos.each{t->
            if(!res){
                def pdts=PersonaDocumentoTramite.findAllByTramite(t)
                pdts.each {pd->
//                    println "pdt "+pd+"   "+pd.estado?.descripcion+"    "+(pd.estado!=estado)
                    if(pd.estado?.codigo!=estado.codigo){
                        res = true
                    }else{
                        if(verficaHijos(pd,estado))
                            res = true
                    }


                }
            }
        }
        return res
    }
}
