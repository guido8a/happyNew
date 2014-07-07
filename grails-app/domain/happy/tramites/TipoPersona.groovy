package happy.tramites

class TipoPersona {
    static auditable = true
    String codigo
    String descripcion
    static mapping = {
        table 'tppr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tppr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tppr__id'
            codigo column: 'tpprcdgo'
            descripcion column: 'tpprdscr'
        }
    }
    static constraints = {
        codigo(maxSize: 1, blank: false, attributes: [title: 'codigo'])
        descripcion(maxSize: 15, blank: false, attributes: [title: 'descripcion'])
    }
}