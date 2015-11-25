package happy.utilitarios

import grails.transaction.Transactional
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite

//@Transactional
class TramitesService {

    static transactional = false
    /**
     <fecha> - <accion> - solicitado por técnico <solicitadoPor> <texto> observaciones: <observaciones> ||
     */
    String observaciones(String observacionOriginal, String accion, String solicitadoPor, String usuario, String texto, String nuevaObservacion) {
        def fecha = new Date().format('dd-MM-yyyy HH:mm')
        observacionOriginal = observacionOriginal ? observacionOriginal.trim() : ""
        accion = accion ? accion.trim() : ""
        solicitadoPor = solicitadoPor ? solicitadoPor.trim() : ""
        usuario = usuario ? usuario.trim() : ""
        texto = texto ? texto.trim() : ""
        nuevaObservacion = nuevaObservacion ? nuevaObservacion.trim() : ""

        def obs = "${fecha}"
        obs += (accion != "" ? " - ${accion}" : "")
        obs += (solicitadoPor != "" ? " - solicitado por: ${solicitadoPor}" : "")
        if (usuario != "") {
            if (solicitadoPor != "") {
                obs += " - técnico: " + usuario
            } else {
                if (usuario.startsWith("por")) {
                    obs += " " + usuario
                } else {
                    obs += " - usuario: " + usuario
                }
            }
        }
//        obs += (usuario != "" ? ((solicitadoPor != "" ? " - técnico: " : " - usuario: ") + usuario) : "")
//        obs += " - técnico ${usuario}"
        obs += (texto != "" ? " - ${texto}" : "")
        obs += (nuevaObservacion != "" ? " - observaciones: ${nuevaObservacion}" : "")

        if (observacionOriginal != "") {
            obs += " || " + observacionOriginal
        }

        return obs
    }

    /**
     * hace prepend la nuevaObservacion a la observacionOriginal
     * @param observacionOriginal
     * @param nuevaObservacion
     * @return
     */
    String modificaObservaciones(String observacionOriginal, String nuevaObservacion) {
        def obs = ""
        if (nuevaObservacion) {
            obs += nuevaObservacion.trim()
        }
        if (observacionOriginal && observacionOriginal.trim() != "") {
            obs += "; " + observacionOriginal
        }
        return obs;
    }

    /**
     * Crea la observacion con el formato deseado y hace prepend a la observacion original agregando la fecha actual. Formato:
     *           <fecha> <usuario> <autorizado por> <accion>
     * @param observacionOriginal : String      la observacion original del objeto
     * @param nuevaObservacion : String      la observacion nueva a agregar (sin fecha ni usuario ni autorizacion)
     * @param autorizadoPor : String      quien autoriza la accion (viene de un textfield)
     * @param usuario : String     el login del usuario que esta efectuando la operacion (session.usuario)
     * @return : String      la observacion lista para ser insertada en el objeto
     */
    String makeObservaciones(String observacionOriginal, String nuevaObservacion, String autorizadoPor, String usuario) {
        observacionOriginal = observacionOriginal ? observacionOriginal.trim() : ""
        nuevaObservacion = nuevaObservacion ? nuevaObservacion.trim() : ""
        autorizadoPor = autorizadoPor ? autorizadoPor.trim() : ""
        usuario = usuario ? usuario.trim() : ""
        def fecha = new Date().format("dd-MM-yyyy HH:mm")
        def obs = ""
        if (nuevaObservacion) {
            obs = "El " + fecha
            if (usuario != "") {
                obs += ", por " + usuario
            }
            if (autorizadoPor != "") {
                obs += ", autorizado por " + autorizadoPor
            } /*else {
                obs += ", sin autorización"
            }*/
            if (nuevaObservacion != "") {
                obs += ": " + nuevaObservacion
            }
        }
        if (observacionOriginal && observacionOriginal != "") {
            obs += "; " + observacionOriginal
        }
        return obs
    }

    /**
     * Verifica toda la cadena del tramite en busca de un estado,
     *      retorna true si encontro un personaDocumentoTramite que no es del estado que recibe como parametro
     * @param pdt
     * @param estado
     * @return
     */
    Boolean verificaHijos(pdt, estado) {
        def hijos = Tramite.findAllByAQuienContestaAndEsRespuestaNueva(pdt, "S")
        def res = false
//        println "-------------------!!---------------------------"
//        println "tramite ver hijos "+pdt.id+"   "+pdt.persona+"   "+pdt.departamento+"  "+pdt.tramite.codigo+"   "+estado.descripcion+"   "+estado.codigo
//        println "hijos "+hijos
        def roles = [RolPersonaTramite.findByCodigo("R001"), RolPersonaTramite.findByCodigo("R002")]
        hijos.each { t ->
            if (!res) {
                def pdts = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteInList(t, roles)
                pdts.each { pd ->
//                    println "pdt del hijo "+t.codigo+"  --> "+pd+"   "+pd.estado?.descripcion+"    "+(pd.estado!=estado)
                    if (!pd.estado) {
                        res = true
                    } else {
                        if (!res) {
                            if (pd.estado?.codigo != estado.codigo) {
                                res = true
                            } else {
//                                println "dentro del bucle"
                                if (verificaHijos(pd, estado)) {
                                    res = true
                                }
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
