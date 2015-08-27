package happy.seguridad


class Sesn {
    static auditable = true
    Persona usuario
    Prfl perfil
    Date fechaInicio
    Date fechaFin

    static mapping = {
        table 'sesn'
        cache usage: 'read-write', include: 'non-lazy'
        version false
        id generator: 'identity'
        sort "perfil"
        columns {
            id column: 'sesn__id'
            perfil column: 'prfl__id'
            usuario column: 'prsn__id'
            fechaInicio column: 'sesnfcin'
            fechaFin column: 'sesnfcfn'
        }
    }


    static constraints = {
        fechaInicio(blank: true, nullable: true)
        fechaFin(blank: true, nullable: true)
    }

    boolean getEstaActivo() {
        def now = new Date()
        if(fechaInicio == null)
            return false
        else
        if (fechaInicio <= now && fechaFin == null) {
            println "activo ..."
            return true
        }
        else {
            if (fechaInicio < now && fechaFin > now)
                return true
            else
                return false
        }

    }

    String toString() {
        return "${this.perfil}"
    }

}
