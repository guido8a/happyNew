package happy.tramites

class Numero {
    static auditable=true
    Departamento departamento
    TipoDocumento tipoDocumento
    int valor                       //se utiliza este valor+1, es el ultimo valor utilizado: debe iniciar en 0
    static mapping = {
        table 'nmro'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'nmro__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'nmro__id'
            departamento column: 'dpto__id'
            tipoDocumento column: 'tpdc__id'
            valor column: 'nmrovlor'
        }
    }
    static constraints = {
        departamento(blank: true, nullable: true, attributes: [title: 'departamento'])
        tipoDocumento(blank: true, nullable: true, attributes: [title: 'tipoDocumento'])
        valor(blank: false, attributes: [title: 'valor'])
    }
}