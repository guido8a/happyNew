package happy.seguridad

class Accs {
    static auditable = true
    Date accsFechaInicial
    Date accsFechaFinal
    String accsObservaciones

    Persona usuario
    Persona asignadoPor

    static mapping = {
        table 'accs'
        version false
        id generator: 'identity'

        columns {
            id column: 'accs__id'
            usuario column: 'usro__id'
            accsFechaInicial column: 'accsfcin'
            accsFechaFinal column: 'accsfcfn'
            accsObservaciones column: 'accsobsr'

            asignadoPor column: 'prsnasgn'
        }
    }

    static constraints = {
        accsFechaInicial(blank: false, nullable: false)
        accsFechaFinal(blank: false, nullable: false)
        accsObservaciones(blank: true, nullable: true,size: 1..1024)
        asignadoPor(blank: false, nullable: false, attributes: [title: 'usuario que asigna el acceso'])
    }

    boolean getEstaActivo() {
        def now = new Date()
        return (this.accsFechaInicial <= now && (this.accsFechaFinal >= now || this.accsFechaFinal == null))
    }

    String getEstado() {
        def now = new Date()
        if (this.estaActivo) {
            return "A"
        } else {
            if (this.accsFechaInicial > now) {
                return "F"
            } else {
                return "P"
            }
        }
    }

}
