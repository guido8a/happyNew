package happy.tramites

class TipoDocumentoDepartamento {
    Departamento departamento
    TipoDocumento tipo
    String estado
    static mapping = {
        table 'tddp'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'tddp__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'tddp__id'
            departamento column: 'dpto__id'
            tipo         column: 'tpdc__id'
            estado       column: 'tddpetdo'
        }
    }
    static constraints = {
        estado(maxSize: 1, blank: false, attributes: [title: 'estado: Activo (1) o Desactivo (0)'])
    }
}
