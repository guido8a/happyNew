package happy

class PasoProceso {
    Proceso proceso
    PasoProceso padre
    String nombre
    int orden
    int tiempo
    String funciones
    static mapping = {
        table 'pspc'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'pspc__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'pspc__id'
            proceso column: 'prcs__id'
            padre column: 'pspcpdre'
            nombre column: 'pspcnmbr'
            orden column: 'pspcordn'
            tiempo column: 'pspctmpo'
            funciones column: 'pspcfunc'
        }
    }
    static constraints = {
        proceso(blank: true, nullable: true)
        padre(blank: true, nullable: true)
        nombre(maxSize: 255, blank: false)
        orden(blank: true, nullable: true)
        tiempo(blank: true, nullable: true)
        funciones(maxSize: 255, blank: true, nullable: true)
    }
}