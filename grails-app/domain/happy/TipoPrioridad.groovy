package happy

class TipoPrioridad {
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
        codigo(maxSize: 4, blank: false)
        descripcion(maxSize: 31, blank: false)
    }
}