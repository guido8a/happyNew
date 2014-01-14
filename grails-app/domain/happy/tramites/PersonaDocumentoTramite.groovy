package happy.tramites

import happy.seguridad.Persona

class PersonaDocumentoTramite {
    RolPersonaTramite rolPersonaTramite
    Persona persona
    Tramite tramite
    Date fecha
    String observaciones
    String permiso
    static mapping = {
        table 'prtr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prtr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prtr__id'
            rolPersonaTramite column: 'rltr__id'
            persona column: 'prsn__id'
            tramite column: 'trmt__id'
            fecha column: 'prtrfcha'
            observaciones column: 'prtrobsr'
            permiso column: 'prtrprms'
        }
    }
    static constraints = {
        rolPersonaTramite(blank: true, nullable: true, attributes: [title: 'rolPersonaTramite'])
        persona(blank: true, nullable: true, attributes: [title: 'persona'])
        tramite(blank: true, nullable: true, attributes: [title: 'Tramite'])
        fecha(blank: true, nullable: true, attributes: [title: 'fecha'])
        observaciones(maxSize: 1023, blank: true, nullable: true, attributes: [title: 'observaciones'])
        permiso(maxSize: 4, blank: true, nullable: true, attributes: [title: 'permiso'])
    }
}