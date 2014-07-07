package happy.tramites

class TipoPrioridad {
    static auditable = true
    String codigo
    String descripcion
    int tiempo
    int maximoRezagados
    static mapping = {
        table 'tppd'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tppd__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tppd__id'
            codigo column: 'tppdcdgo'
            descripcion column: 'tppddscr'
            tiempo column: 'tppdtmpo'
            maximoRezagados column: 'tppdmxrz'
        }
    }
    static constraints = {
        codigo(maxSize: 4, blank: false, attributes: [title: 'codigo'])
        descripcion(maxSize: 31, blank: false, attributes: [title: 'descripcion'])
        tiempo(blank: true, nullable: true, attributes: [title: 'tiempo'])
    }
}