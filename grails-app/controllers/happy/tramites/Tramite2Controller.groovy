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

    def saveNotas(){
        def tramite = Tramite.get(params.tramite)
        tramite.nota=params.notas
        if(tramite.save(flush: true))
            render "ok"
        else
            render "error"

    }

    def revisar(){
        /*todo validar que sea el jefe*/
        def tramite = Tramite.get(params.id)
        if(tramite.estadoTramite.codigo=="E001"){
            tramite.estadoTramite=EstadoTramite.findByCodigo("E002")
        }
        if(tramite.save(flush: true))
            render "ok"
        else
            render "error"
    }

}
