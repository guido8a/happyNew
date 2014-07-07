package happy.utilitarios

import happy.tramites.Anio

class DiaLaborable {
    static auditable = true
    Date fecha
    String dia              //lun:1, mar:2, mie:3, jue:4, vie:5, sab:6, dom:0
    Anio anio            //anio de la fecha (para facilitar las busquedas)
    Integer ordinal
//    String observaciones

    Integer horaInicio
    Integer minutoInicio

    Integer horaFin
    Integer minutoFin

    static mapping = {
        table 'ddlb'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'ddlb__id'
        id generator: 'identity'
        version false
        columns {
            fecha column: "ddlbfcha"
            dia column: "ddlbddia"
            anio column: "anio__id"
            ordinal column: "ddlbordn"
//            observaciones column: 'ddlbobsr'

            horaInicio column: 'ddlbhrin'
            minutoInicio column: 'ddlbmnin'
            horaFin column: 'ddlbhrfn'
            minutoFin column: 'ddlbmnfn'
        }
    }
    static constraints = {
        dia(blank: false, nullable: false, maxSize: 3)
//        observaciones(blank: true, nullable: true, maxSize: 511)
    }

    def getInicioJornada() {
        return this.horaInicio.toString().padLeft(2, '0') + ":" + this.minutoInicio.toString().padLeft(2, '0')
    }

    def getFinJornada() {
        return this.horaFin.toString().padLeft(2, '0') + ":" + this.minutoFin.toString().padLeft(2, '0')
    }

}
