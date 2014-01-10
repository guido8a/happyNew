package happy.tramites

class TipoTramite {
    String codigo
    String descripcion
    static mapping = {
        table 'tptr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tptr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tptr__id'
            codigo column: 'tptrcdgo'
            descripcion column: 'tptrdscr'
        }
    }
    static constraints = {
        codigo(maxSize: 4, blank: false)
        descripcion(maxSize: 31, blank: false)
    }
}