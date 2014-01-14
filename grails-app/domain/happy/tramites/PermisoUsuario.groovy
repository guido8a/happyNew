package happy.tramites

import happy.seguridad.Persona

class PermisoUsuario {
    Persona persona
    PermisoTramite permisoTramite
    Date fechaInicio
    Date fechaFin
    static mapping = {
        table 'prus'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prus__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prus__id'
            persona column: 'prsn__id'
            permisoTramite column: 'perm__id'
            fechaInicio column: 'prusfcin'
            fechaFin column: 'prusfcfn'
        }
    }
    static constraints = {
        persona(blank: true, nullable: true, attributes: [title: 'persona'])
        permisoTramite(blank: true, nullable: true, attributes: [title: 'permisoTramite'])
        fechaInicio(blank: false, attributes: [title: 'fechaInicio'])
        fechaFin(blank: true, nullable: true, attributes: [title: 'fechaFin'])
    }
}