package happy.tramites

import happy.tramites.Departamento

class DepartamentoPara {
    static auditable = true
    Departamento deparatamento
    Departamento deparatamentoPara
    Date fechaDesde
    Date fechaHasta


    static mapping = {
        table 'dpdp'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dpdp__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'dpdp__id'
            deparatamento column: 'dpto__id'
            deparatamentoPara column: 'dptopara'
            fechaDesde column: 'dpdpfcin'
            fechaHasta column: 'dpdpfcfn'
        }
    }
    static constraints = {
        deparatamento(blank: false, nullable: false, attributes: [title: 'Departamento'])
        deparatamentoPara(blank: false, nullable: false, attributes: [title: 'DepartamentoPara'])
        fechaDesde(blank: false, nullable: false, attributes: [title: 'Fecha desde la cual puede enviar trámites'])
        fechaHasta(blank: true, nullable: true, attributes: [title: 'Fecha hasta la cual puede enviar trámites'])
    }

}