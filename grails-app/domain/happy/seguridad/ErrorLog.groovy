package happy.seguridad


class ErrorLog {
    static auditable = false
    Date fecha
    String error
    String causa
    String url
    Persona usuario

    static mapping = {
        table 'logf'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'logf__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'logf__id'
            fecha column: 'logffcha'
            error column: 'logferro'
            causa column: 'logfcaus'
            url column: 'logf_url'
            usuario column: 'logfusro'
        }
    }
    static constraints = {
        error(size: 1..4024)
        causa(size: 1..4024)
        url(size: 1..1024)

    }

    String toString() {
        "${this.error}"
    }
}
