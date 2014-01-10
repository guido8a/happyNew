package happy.tramites

class DocumentoTramite {
    Tramite tramite
    Tramite anexo
    Date fecha
    String resumen
    String clave
    String path
    String descripcion
    Date fechaLectura
    static mapping = {
        table 'dctr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dctr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'dctr__id'
            tramite column: 'trmt__id'
            anexo column: 'trmtanxo'
            fecha column: 'dctrfcha'
            resumen column: 'dctrrsmn'
            clave column: 'dctrclve'
            path column: 'dcmtpath'
            descripcion column: 'dctrdscr'
            fechaLectura column: 'dctrfcrv'
        }
    }
    static constraints = {
        tramite(blank: true, nullable: true)
        anexo(blank: true, nullable: true)
        fecha(blank: true, nullable: true)
        resumen(maxSize: 1024, blank: true, nullable: true)
        clave(maxSize: 63, blank: true, nullable: true)
        path(maxSize: 1024, blank: true, nullable: true)
        descripcion(maxSize: 63, blank: true, nullable: true)
        fechaLectura(blank: true, nullable: true)
    }
}