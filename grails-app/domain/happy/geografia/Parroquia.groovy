package happy.geografia

class Parroquia {
    Canton canton
    String codigo
    String nombre
    static mapping = {
        table 'parr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'parr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'parr__id'
            canton column: 'cntn__id'
            codigo column: 'parrcdgo'
            nombre column: 'parrnmbr'
        }
    }
    static constraints = {
        canton(blank: true, nullable: true)
        codigo(maxSize: 6, blank: false)
        nombre(maxSize: 63, blank: false)
    }
}