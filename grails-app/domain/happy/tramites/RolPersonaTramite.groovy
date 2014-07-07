package happy.tramites

class RolPersonaTramite {
    static auditable = true
    String codigo
    String descripcion
    static mapping = {
        table 'rltr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'rltr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'rltr__id'
            codigo column: 'rltrcdgo'
            descripcion column: 'rltrdscr'
        }
    }
    static constraints = {
        codigo(maxSize: 4, blank: false, attributes: [title: 'codigo'])
        descripcion(maxSize: 63, blank: false, attributes: [title: 'descripcion'])
    }
}