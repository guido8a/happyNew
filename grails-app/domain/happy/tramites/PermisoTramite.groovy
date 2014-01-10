package happy.tramites

import happy.seguridad.Persona

class PermisoTramite {
    Tramite tramite
    Persona persona
    String permiso
    static mapping = {
        table 'prtr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prtr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prtr__id'
            tramite column: 'trmt__id'
            persona column: 'prsn__id'
            permiso column: 'prtrprms'
        }
    }
    static constraints = {
        tramite(blank: true, nullable: true)
        persona(blank: true, nullable: true)
        permiso(maxSize: 4, blank: true, nullable: true)
    }
}