package happy.tramites

class EstadoTramite {
    static auditable = true
    String codigo
    String descripcion
    static mapping = {
        table 'edtr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'edtr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'edtr__id'
            codigo column: 'edtrcdgo'
            descripcion column: 'edtrdscr'
        }
    }
    static constraints = {
        codigo(maxSize: 4, blank: false, attributes: [title: 'codigo'])
        descripcion(maxSize: 31, blank: false, attributes: [title: 'descripcion'])
    }
}