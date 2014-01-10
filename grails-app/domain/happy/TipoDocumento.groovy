package happy

class TipoDocumento {
    String codigo
    String descripcion
    static mapping = {
        table 'tpdc'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tpdc__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tpdc__id'
            codigo column: 'tpdccdgo'
            descripcion column: 'tpdcdscr'
        }
    }
    static constraints = {
        codigo(maxSize: 4, blank: false)
        descripcion(maxSize: 31, blank: false)
    }
}