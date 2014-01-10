package happy

class TipoDependencia {
    String codigo
    String descripcion
    static mapping = {
        table 'tpdp'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tpdp__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tpdp__id'
            codigo column: 'tpdpcdgo'
            descripcion column: 'tpdpdscr'
        }
    }
    static constraints = {
        codigo(maxSize: 4, blank: false)
        descripcion(maxSize: 31, blank: false)
    }
}