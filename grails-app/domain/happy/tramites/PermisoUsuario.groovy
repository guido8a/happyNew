package happy.tramites

import happy.seguridad.Persona

class PermisoUsuario {
    Persona persona
    PermisoTramite permisoTramite
    Date fechaInicio
    Date fechaFin
    String observaciones
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
            observaciones column: 'prusobsv'
        }
    }
    static constraints = {
        persona(blank: true, nullable: true, attributes: [title: 'persona'])
        permisoTramite(blank: true, nullable: true, attributes: [title: 'permisoTramite'])
        fechaInicio(blank: false, attributes: [title: 'fechaInicio'])
        fechaFin(blank: true, nullable: true, attributes: [title: 'fechaFin'])
        observaciones(blank: true, nullable: true, maxSize: 100, attributes: [title: 'observaciones'])
    }

    boolean getEstaActivo() {
        def now = new Date()
        return (this.fechaInicio <= now && (this.fechaFin >= now || this.fechaFin == null))
    }

    String getEstado() {
        def now = new Date()
        if (this.estaActivo) {
            return "A"
        } else {
            if (this.fechaInicio > now) {
                return "F"
            } else {
                return "P"
            }
        }
    }

}