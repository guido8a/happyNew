package happy.tramites

class PermisoTramite {
    static auditable = true
    String codigo
    String descripcion
    String texto
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
            texto column: 'permtxto'
        }
    }
    static constraints = {
        codigo(maxSize: 4, blank: false, attributes: [title: 'codigo'])
        descripcion(maxSize: 63, blank: false, attributes: [title: 'descripcion'])
        texto(maxSize: 255, blank: false, attributes: [title: 'descripcion del permiso'])
    }

    String toString() {
        return "${this.descripcion}"
    }
}