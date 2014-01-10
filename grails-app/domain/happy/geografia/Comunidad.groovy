package happy.geografia

class Comunidad {
    Parroquia parroquia
    String numero
    String nombre
    static mapping = {
        table 'cmnd'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'cmnd__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'cmnd__id'
            parroquia column: 'parr__id'
            numero column: 'cmndnmro'
            nombre column: 'cmndnmbr'
        }
    }
    static constraints = {
        parroquia(blank: true, nullable: true)
        numero(maxSize: 8, blank: false)
        nombre(maxSize: 63, blank: false)
    }
}