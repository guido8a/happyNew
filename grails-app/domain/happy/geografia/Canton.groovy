package happy.geografia

class Canton {
    Provincia provincia
    String numero
    String nombre
    static mapping = {
        table 'cntn'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'cntn__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'cntn__id'
            provincia column: 'prov__id'
            numero column: 'cntnnmro'
            nombre column: 'cntnnmbr'
        }
    }
    static constraints = {
        provincia(blank: true, nullable: true)
        numero(maxSize: 4, blank: false)
        nombre(maxSize: 63, blank: false)
    }
}