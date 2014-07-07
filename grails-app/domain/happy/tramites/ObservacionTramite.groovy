package happy.tramites

import happy.seguridad.Persona

class ObservacionTramite {
    static auditable = true
    Tramite tramite
    Persona persona
    Date fecha
    String observaciones
    String tipo             //tipo de observación  (archivar, anular)
    static mapping = {
        table 'obtr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'obtr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'obtr__id'
            tramite column: 'trmt__id'
            persona column: 'prsn__id'
            fecha column: 'obtrfcha'
            observaciones column: 'obtrobsr'
            tipo column: 'obtrtipo'
        }
    }
    static constraints = {
        tramite(blank: true, nullable: true, attributes: [title: 'Tramite'])
        persona(blank: true, nullable: true, attributes: [title: 'persona'])
        fecha(blank: false, attributes: [title: 'fecha'])
        observaciones(maxSize: 1023, blank: false, attributes: [title: 'observaciones'])
        tipo(maxSize: 10, blank: true, nullable: true, attributes: [title: 'tipo'])
    }
}