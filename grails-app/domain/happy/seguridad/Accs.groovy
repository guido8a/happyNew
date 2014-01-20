package happy.seguridad

class Accs implements Serializable {

    Date accsFechaInicial
    Date accsFechaFinal
    String accsObservaciones

    Persona usuario

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
        }
    }

    static constraints = {
        accsFechaInicial(blank: false, nullable: false)
        accsFechaFinal(blank: false, nullable: false)
        accsObservaciones(blank: true, nullable: true)
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
