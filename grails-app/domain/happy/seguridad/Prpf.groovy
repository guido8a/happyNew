package happy.seguridad

class Prpf {

    happy.tramites.PermisoTramite permiso
    Prfl perfil
    static auditable = [ignore: []]

    static mapping = {
        table 'prpf'
        cache usage: 'read-write', include: 'non-lazy'
        version false
        id generator: 'identity'

        columns {
            id column: 'prpf__id'
            permiso column: 'perm__id'
            perfil column: 'prfl__id'
        }
    }

    static constraints = {
    }
}
