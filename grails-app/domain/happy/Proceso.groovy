package happy

class Proceso {
    TipoProceso tipoProceso
    String nombre
    String descripcion
    int numero
    int tiempo
    Date fecha
    String observaciones
    static mapping = {
        table 'prcs'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prcs__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prcs__id'
            tipoProceso column: 'tppc__id'
            nombre column: 'prcsnmbr'
            descripcion column: 'prcsdscr'
            numero column: 'prcsnmro'
            tiempo column: 'prcstmpo'
            fecha column: 'prcsfcha'
            observaciones column: 'prcsobsr'
        }
    }
    static constraints = {
        tipoProceso(blank: true, nullable: true)
        nombre(maxSize: 255, blank: false)
        descripcion(maxSize: 1023, blank: true, nullable: true)
        numero(blank: true, nullable: true)
        tiempo(blank: true, nullable: true)
        fecha(blank: true, nullable: true)
        observaciones(maxSize: 255, blank: true, nullable: true)
    }
}