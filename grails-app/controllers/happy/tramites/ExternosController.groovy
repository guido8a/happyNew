package happy.tramites

import happy.seguridad.Persona

class ExternosController {

    def bandejaExternos(){
        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def bloqueo = false
        return [persona: persona, bloqueo: bloqueo]

    }
    def tablaBandeja(){
        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def enviado = EstadoTramite.findByCodigo("E003")
        def recibido = EstadoTramite.findByCodigo("E004")
        def anexo


        params.domain = params.domain ?: "persDoc"
        params.sort = params.sort ?: "fechaEnvio"
        params.order = params.order ?: "desc"

        def tramites = PersonaDocumentoTramite.withCriteria {
            eq("persona", persona)
            inList("rolPersonaTramite", [rolPara, rolCopia])
            isNotNull("fechaEnvio")
            inList("estado", [enviado, recibido])
            tramite {
//                inList("estadoTramite", [enviado, recibido])
                if (params.domain == "tramite") {
                    order(params.sort, params.order)
                }
            }
            if (params.domain == "persDoc") {
                order(params.sort, params.order)
            }
        }

        def tramitesSinHijos = []
        def anulado = EstadoTramite.findByCodigo("E006")
        def band = false
        tramites.each { tr ->
            band = verificaHijos(tr, anulado)
            println "estado!!! " + band + "   " + tr.id
            if (!band) {
                tramitesSinHijos += tr
            }
        }

        return [tramites: tramitesSinHijos, params: params]
    }
}
