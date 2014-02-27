package happy.tramites

import happy.seguridad.Persona

class Tramite2Controller {

    def verTramite(){
        /*comentar esto*/
        params.id="2"
        def traminte = Tramite.get(params.id)
        /*Aqui controlar los permisos para ver el tramite por el usuario*/


        /*fin permisos*/

       return  [traminte:traminte]
    }
}
