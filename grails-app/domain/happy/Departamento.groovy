package happy

class Departamento {
    TipoDependencia tipoDependencia
    Departamento padre
    String descripcion
    String codigo
    static mapping = {
        table 'dpto'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dpto__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'dpto__id'
            tipoDependencia column: 'tpdp__id'
            padre column: 'dptopdre'
            descripcion column: 'dptodscr'
            codigo column: 'dptocdgo'
        }
    }
    static constraints = {
        tipoDependencia(blank: true, nullable: true)
        padre(blank: true, nullable: true)
        descripcion(maxSize: 63, blank: false)
        codigo(maxSize: 6, blank: false)
    }
}