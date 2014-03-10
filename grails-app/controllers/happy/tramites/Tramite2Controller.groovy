package happy.tramites

import happy.seguridad.Persona

class Tramite2Controller extends happy.seguridad.Shield{

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



    def bandejaSalida() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def tramitesPasados = alertaNoRecibidos().tramitesPasados
        return [persona: persona, tramitesPasados: tramitesPasados]

    }

    def tablaBandejaSalida() {
        def persona = Persona.get( session.usuario.id)
        def estados = EstadoTramite.findAllByCodigoInList(["E001","E002","E003"])
        def tramites = Tramite.findAllByDeAndEstadoTramiteInList(persona,estados,[sort:"fechaCreacion",order:"desc"])

        return [persona: persona, tramites: tramites,idTramitesNoRecibidos:[] ]
    }

    //alertas

    def alertaRevisados() {
        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def revisados = EstadoTramite.get(2)
        def tramitesRevisados = []
        def tramites = []
        def pxt = PersonaDocumentoTramite.findAllByPersona(persona)
        pxt.each {
            if (it?.tramite?.de?.id == usuario?.id) {
                tramites.add(it.tramite)
            }
        }

        tramites.each {
            if (it.estadoTramite == revisados) {
                tramitesRevisados.add(it)
            }
        }

        return [tramites: tramitesRevisados.size()]
    }


    def alertaEnviados() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def enviados = EstadoTramite.get(3)
        def tramites = []
        def tramitesEnviados = []
        def idTramitesEnviados = []
        def cantidadEnviados = 0
        def pxt = PersonaDocumentoTramite.findAllByPersona(persona)
        pxt.each {
            if (it?.tramite?.de?.id == usuario?.id) {
                tramites.add(it.tramite)
            }
        }
        tramites.each {
            if (it?.estadoTramite == enviados) {
                tramitesEnviados.add(it)
            }
        }

        def fechaEnvio
        def dosHoras = 7200000  //milisegundos
        def fecha
        Date nuevaFecha

        tramitesEnviados.each {

            fechaEnvio = it?.fechaEnvio
            fecha = fechaEnvio.getTime()
            nuevaFecha = new Date(fecha + dosHoras)
            if (!nuevaFecha.before(new Date())) {
                cantidadEnviados++
                idTramitesEnviados.add(it.id)
            }
        }

        return [tramites: cantidadEnviados, idTramitesEnviados: idTramitesEnviados]
    }


    def alertaNoRecibidos() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def enviados = EstadoTramite.get(3)
        def listaNoRecibidos = []
        def tramites = []

        def pxt = PersonaDocumentoTramite.findAllByPersona(persona)
        pxt.each {
            if (it?.tramite?.de?.id == usuario?.id) {
                listaNoRecibidos.add(it?.tramite)
            }
        }
        listaNoRecibidos.each {

            if (it?.estadoTramite == enviados) {
                tramites.add(it)
            }
        }

        def fechaEnvio
        def dosHoras = 7200000  //milisegundos
        def ch = 172800000

        def fecha
        Date nuevaFecha
        Date fechaLimite

        def tramitesNoRecibidos = 0
        def idTramitesNoRecibidos = []

        def tramitesPasados = 0

        tramites.each {
            fechaEnvio = it?.fechaEnvio
            fecha = fechaEnvio.getTime()
            nuevaFecha = new Date(fecha + dosHoras)
            fechaLimite = new Date(fecha + ch)
            if (nuevaFecha.before(new Date())) {
                tramitesNoRecibidos++
                idTramitesNoRecibidos.add(it.id)
            }
            if (fechaLimite.before(new Date())) {

                tramitesPasados++
            }
        }

        return [tramitesNoRecibidos: tramitesNoRecibidos, idTramitesNoRecibidos: idTramitesNoRecibidos, tramitesPasados: tramitesPasados]
    }

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
