package happy.utilitarios

class Parametros {

    Integer horaInicio
    Integer minutoInicio

    Integer horaFin
    Integer minutoFin

    static mapping = {
        table 'prmt'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prmt__id'
        id generator: 'identity'
        version false
        columns {
            horaInicio column: 'prmthrin'
            minutoInicio column: 'prmtmnin'
            horaFin column: 'prmthrfn'
            minutoFin column: 'prmtmnfn'
        }
    }
    static constraints = {

    }

    def getInicioJornada() {
        return this.horaInicio.toString().padLeft(2, '0') + ":" + this.minutoInicio.toString().padLeft(2, '0')
    }

    def getFinJornada() {
        return this.horaFin.toString().padLeft(2, '0') + ":" + this.minutoFin.toString().padLeft(2, '0')
    }
}
