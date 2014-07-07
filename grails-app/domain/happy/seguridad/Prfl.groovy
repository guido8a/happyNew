package happy.seguridad

class Prfl {
    static auditable = true
    String nombre
    String descripcion
    Prfl padre
    String observaciones
    String codigo

    static hasMany = [permisos: Prms, perfiles: Prfl]


    static mapping = {
        table 'prfl'
        cache usage: 'read-write'
        version false
        id generator: 'identity'
        sort nombre: "asc"
        columns {
            id column: 'prfl__id'
            nombre column: 'prflnmbr'
            descripcion column: 'prfldscr'
            padre column: 'prflpdre'
            observaciones column: 'prflobsr'
            codigo column: 'prflcdgo'
        }
    }

    static constraints = {
        padre(blank: true, nullable: true)
    }

    String toString() {
        return "${this.nombre}"
    }
}
