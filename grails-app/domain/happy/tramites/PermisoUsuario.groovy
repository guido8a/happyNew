package happy.tramites

import happy.seguridad.Accs
import happy.seguridad.Persona

class PermisoUsuario {
    static auditable = true
    Persona persona
    PermisoTramite permisoTramite
    Date fechaInicio
    Date fechaFin
//    String observaciones
    Persona asignadoPor
    Persona modificadoPor
    Accs acceso

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
//            observaciones column: 'prusobsv'

            asignadoPor column: 'prsnasgn'
            modificadoPor column: 'prsnmdfc'
            acceso column: 'accs__id'
        }
    }
    static constraints = {
        persona(blank: true, nullable: true, attributes: [title: 'persona'])
        permisoTramite(blank: true, nullable: true, attributes: [title: 'permisoTramite'])
        fechaInicio(blank: false, attributes: [title: 'fechaInicio'])
        fechaFin(blank: true, nullable: true, attributes: [title: 'fechaFin'])
//        observaciones(blank: true, nullable: true, maxSize: 100, attributes: [title: 'observaciones'])

        asignadoPor(blank: false, nullable: false, attributes: [title: 'usuario que asigna el permiso'])
        modificadoPor(blank: true, nullable: true, attributes: [title: 'modificado por'])
        acceso(blank: true, nullable: true)
    }

    boolean getEstaActivo() {
        def now = new Date()

//        def fi = new Date().parse("dd-MM-yyyy HH:mm", fechaInicio.format("dd-MM-yyyy 00:01"))
//        def ff = fechaFin ? new Date().parse("dd-MM-yyyy HH:mm", fechaFin.format("dd-MM-yyyy 23:59")) : null
        def fi = fechaInicio
        def ff = fechaFin

//        if (persona.id == 5513L) {
//            println "\t\t" + permisoTramite.descripcion + " fecha ini: " + fi + "   fecha fin: " + ff + "  now: " + now
//        }

//        println "permiso esta activo? now=" + now + "   fi=" + fi + "   ff=" + ff +
//                "   (ff == null)=" + (ff == null) + "   (fi <= now)=" +
//                (fi <= now) + "   if1= " + (ff == null && fi <= now) +
//                "   (fechaInicio?.clearTime() <= now.clearTime())=" + (fi <= now) +
//                "   (fechaFin?.clearTime() > now.clearTime())=" + (ff > now) +
//                "   if2=" + (fi <= now && ff > now)

        if (ff == null && fi <= now) {
            return true
        }
        if (fi <= now && ff > now) {
            return true
        }
        return false
//        return (this.fechaInicio <= now && this.fechaFin == null)
//        return (this.fechaInicio <= now && (this.fechaFin >= now || this.fechaFin == null))
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