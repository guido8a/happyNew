package happy.geografia

class Provincia {
    Zona zona
    String numero
    String nombre
    static mapping = {
        table 'prov'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prov__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prov__id'
            zona column: 'zona__id'
            numero column: 'provnmro'
            nombre column: 'provnmbr'
        }
    }
    static constraints = {
        zona(blank: true, nullable: true)
        numero(maxSize: 2, blank: false)
        nombre(maxSize: 63, blank: true, nullable: true)
    }
}