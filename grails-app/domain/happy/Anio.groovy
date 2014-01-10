package happy

class Anio {
    String numero
    static mapping = {
        table 'anio'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'anio__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'anio__id'
            numero column: 'anionmro'
        }
    }
    static constraints = {
        numero(maxSize: 4, blank: false)
    }
}