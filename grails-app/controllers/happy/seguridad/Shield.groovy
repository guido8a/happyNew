package happy.seguridad

import com.sun.java.swing.plaf.windows.resources.windows
import happy.tramites.EstadoTramite
import happy.tramites.Tramite

class Shield {
    def beforeInterceptor = [action: this.&auth, except: 'login']
    /**
     * Verifica si se ha iniciado una sesión
     * Verifica si el usuario actual tiene los permisos para ejecutar una acción
     */
    def auth() {
//        println "an " + actionName + " cn " + controllerName + "  "
//        println session
        session.an = actionName
        session.cn = controllerName
        session.pr = params
//        return true

        def para = alertaNoRecibidos().tramitesPasados

        /** **************************************************************************/
        if (!session.usuario || !session.perfil) {
            //            println "1"
            redirect(controller: 'login', action: 'login')
            session.finalize()
            return false
//            return true
        } else {

            if(para != 0){

//           redirect(controller: 'login', action: 'pantallaBloqueo', params: [alerta: alertaNoRecibidos().idTramitesPasados ])

            response.sendError(401)
            return false

            }

//            return true
        }
        /*************************************************************************** */
    }



    boolean isAllowed() {
//        try {
//            if (session.permisos[actionName] == controllerName)
//                return true
//        } catch (e) {
//            println "Shield execption e: " + e
//            return true
//        }
//        return true
        return true
    }


    //tramites no recibidos -- bloqueo


    def alertaNoRecibidos () {

        def usuario = session.usuario
        def enviados = EstadoTramite.get(3)
        def tramites = Tramite.findAllByEstadoTramite(enviados)

        def fechaEnvio
        def dosHoras =  7200000  //milisegundos
        def ch = 172800000

        def fecha
        Date nuevaFecha
        Date fechaLimite

        def tramitesNoRecibidos = 0
        def idTramitesNoRecibidos = []

        def tramitesPasados = 0
        def idTramitesPasados = []

        tramites.each {

            fechaEnvio = it.fechaEnvio
            fecha = fechaEnvio.getTime()
            nuevaFecha = new Date(fecha+dosHoras)
            fechaLimite = new Date(fecha+ch)

            if(nuevaFecha.before(new Date())){

                tramitesNoRecibidos++
                idTramitesNoRecibidos.add(it.id)
            }
            if(fechaLimite.before(new Date())){

                tramitesPasados++
                idTramitesPasados.add(it.id)
            }



        }

//   println("tramites pasados:" + idTramitesPasados)

        return [tramitesNoRecibidos: tramitesNoRecibidos, idTramitesNoRecibidos: idTramitesNoRecibidos, tramitesPasados: tramitesPasados, idTramitesPasados: idTramitesPasados ]



    }



}
 
