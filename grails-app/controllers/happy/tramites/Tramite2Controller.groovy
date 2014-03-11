package happy.tramites

import groovy.time.TimeCategory
import happy.seguridad.Persona

class Tramite2Controller extends happy.seguridad.Shield{

    def verTramite(){
        /*comentar esto*/
        def tramite = Tramite.get(params.id)
        /*Aqui controlar los permisos para ver el tramite por el usuario*/


        /*fin permisos*/

        return  [tramite:tramite]
    }

    def revision(){

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



    def bandejaSalida() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        return [persona: persona]

    }

    def tablaBandejaSalida() {
//        println "carga bandeja"
        def persona = Persona.get( session.usuario.id)
        def estados = EstadoTramite.findAllByCodigoInList(["E001","E002","E003"])
        def tramites = Tramite.findAllByDeAndEstadoTramiteInList(persona,estados,[sort:"fechaCreacion",order:"desc"])

        return [persona: persona, tramites: tramites,idTramitesNoRecibidos:[] ]
    }

    //alertas



    def busquedaBandejaSalida() {


        if (params.fecha) {
            params.fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        }

        def res = Tramite.withCriteria {

            if (params.fecha) {
                eq('fechaIngreso', params.fecha)
            }
            if (params.asunto) {
                ilike('asunto', '%' + params.asunto + '%')
            }
            if (params.memorando) {

                ilike('numero', '%' + params.memorando + '%')

            }
        }

        return [tramites: res]


    }

}
