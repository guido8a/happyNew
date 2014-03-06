package happy.tramites

import happy.seguridad.Persona

class Tramite2Controller {

    def verTramite(){
        /*comentar esto*/
        params.id="13"
        def tramite = Tramite.get(params.id)
        /*Aqui controlar los permisos para ver el tramite por el usuario*/


        /*fin permisos*/

       return  [tramite:tramite]
    }

    def revision(){
        /*comentar esto*/
        params.id="12"
        def tramite = Tramite.get(params.id).refresh()
        /*Todo hacer la validacion para determinar si es el jefe*/

        return  [tramite:tramite]
    }
}
