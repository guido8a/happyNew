package happy.tramites

class EstadoTramiteExterno {
    static auditable=true
    String codigo
    String descripcion
    static mapping = {
        table 'edtx'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'edtx__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'edtx__id'
            codigo column: 'edtxcdgo'
            descripcion column: 'edtxdscr'
        }
    }
    static constraints = {
        codigo(maxSize: 4, blank: false, attributes: [title: 'codigo'])
        descripcion(maxSize: 31, blank: false, attributes: [title: 'descripcion'])
    }
}
