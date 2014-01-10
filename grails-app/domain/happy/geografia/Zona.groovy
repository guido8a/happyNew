package happy.geografia

class Zona {
    int numero
    String nombre
    static mapping = {
        table 'zona'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'zona__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'zona__id'
            numero column: 'zonanmro'
            nombre column: 'zonanmbr'
        }
    }
    static constraints = {
        numero(blank: false)
        nombre(maxSize: 31, blank: true, nullable: true)
    }
}