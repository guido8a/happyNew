package happy.tramites

class PermisoTramite {
    String codigo
    String descripcion
    static mapping = {
        table 'perm'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'perm__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'perm__id'
            codigo column: 'permcdgo'
            descripcion column: 'permdscr'
        }
    }
    static constraints = {
        codigo(maxSize: 4, blank: false, attributes: [title: 'codigo'])
        descripcion(maxSize: 63, blank: false, attributes: [title: 'descripcion'])
    }
}