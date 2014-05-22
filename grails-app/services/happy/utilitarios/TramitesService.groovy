package happy.utilitarios

import grails.transaction.Transactional
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite

@Transactional
class TramitesService {

    /*Verifica toda la cadena del tramite en busca de un estado, retorna true si  encontro un personaDocumentoTramite que no es del estado que recibe como parametro*/

    Boolean verificaHijos(pdt, estado) {
        def hijos = Tramite.findAllByAQuienContesta(pdt)
        def res = false
//        println "-------------------!!---------------------------"
//        println "tramite ver hijos "+pdt.id+"   "+pdt.persona+"   "+pdt.departamento+"  "+pdt.tramite.codigo+"   "+estado.descripcion+"   "+estado.codigo
//        println "hijos "+hijos
        def roles = [RolPersonaTramite.findByCodigo("R001"),RolPersonaTramite.findByCodigo("R002")]
        hijos.each { t ->
            if (!res) {
                def pdts = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteInList(t,roles)
                pdts.each { pd ->
//                    println "pdt del hijo "+t.codigo+"  --> "+pd+"   "+pd.estado?.descripcion+"    "+(pd.estado!=estado)
                    if(!pd.estado)
                        res=true
                    else{
                        if(!res){
                            if (pd.estado?.codigo != estado.codigo) {
                                res = true
                            } else {
//                                println "dentro del bucle"
                                if (verificaHijos(pd, estado))
                                    res = true
                            }
                        }

                    }
                }
            }
        }
//        println "return !!!! "+res
//        println "----------------------------------------------"
        return res
    }
}
