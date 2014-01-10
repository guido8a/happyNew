package happy

class TipoProceso {
    String codigo
    String descripcion
    static mapping = {
        table 'tppc'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tppc__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tppc__id'
            codigo column: 'tppccdgo'
            descripcion column: 'tppcdscr'
        }
    }
    static constraints = {
        codigo(maxSize: 8, blank: false)
        descripcion(maxSize: 255, blank: false)
    }
}