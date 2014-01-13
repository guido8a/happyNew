package happy.tramites

import happy.seguridad.Persona

class ObservacionTramite {
    Tramite tramite
    Persona persona
    Date fecha
    String observaciones
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
        }
    }
    static constraints = {
        tramite(blank: true, nullable: true, attributes: [title: 'Tramite'])
        persona(blank: true, nullable: true, attributes: [title: 'persona'])
        fecha(blank: false, attributes: [title: 'fecha'])
        observaciones(maxSize: 1023, blank: false, attributes: [title: 'observaciones'])
    }
}