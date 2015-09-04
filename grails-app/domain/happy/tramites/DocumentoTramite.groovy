package happy.tramites

class DocumentoTramite {
    static auditable = true
    Tramite tramite
    Tramite anexo
    Date fecha
    String resumen
    String clave
    String path
    String descripcion
//    Date fechaLectura
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
//            fechaLectura column: 'dctrfcrv'
        }
    }
    static constraints = {
        tramite(blank: true, nullable: true, attributes: [title: 'Tramite'])
        anexo(blank: true, nullable: true, attributes: [title: 'anexo'])
        fecha(blank: true, nullable: true, attributes: [title: 'fecha'])
        resumen(maxSize: 1024, blank: true, nullable: true, attributes: [title: 'resumen'])
        clave(maxSize: 63, blank: true, nullable: true, attributes: [title: 'clave'])
        path(maxSize: 1024, blank: true, nullable: true, attributes: [title: 'path'])
        descripcion(maxSize: 255, blank: true, nullable: true, attributes: [title: 'descripcion'])
//        fechaLectura(blank: true, nullable: true, attributes: [title: 'fechaLectura'])
    }

    String getResumenCorto() {
        if (this.resumen) {
            if (this.resumen.size() > 110) {
                def res = ""
                def parts = resumen.split(" ")
                parts.each {
                    if (res.size() < 110)
                        res += it + " "
                }
                return res += "..."
            } else {
                return this.resumen
            }

        } else {
            return ""
        }
    }

}