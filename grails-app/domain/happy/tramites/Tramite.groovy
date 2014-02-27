package happy.tramites

import happy.seguridad.Persona

class Tramite {
    Anio anio
    Tramite padre
    TipoDocumento tipoDocumento
    Persona de
    TipoPrioridad prioridad
    EstadoTramite estadoTramite
    TipoTramite tipoTramite
    OrigenTramite origenTramite
    String codigo
    Integer numero
    Date fecha
    Date fechaLimiteRespuesta
    String asunto
    String anexo
    String texto
    int ampliacionPlazo = 0
    Date fechaRespuesta
    Date fechaIngreso
    Date fechaModificacion
    Date fechaLectura
    String externo
    String nota
    String estado
    String observaciones
    Date fechaEnvio
    static mapping = {
        table 'trmt'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'trmt__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'trmt__id'
            anio column: 'anio__id'
            padre column: 'trmtpdre'
            tipoDocumento column: 'tpdc__id'
            de column: 'prsn__de'
            prioridad column: 'tppd__id'
            estadoTramite column: 'edtr__id'
            tipoTramite column: 'tptr__id'
            origenTramite column: 'orgn__id'
            codigo column: 'trmtcdgo'
            numero column: 'trmtnmro'
            fecha column: 'trmtfcha'
            fechaLimiteRespuesta column: 'trmtfclr'
            asunto column: 'trmtasnt'
            anexo column: 'trmtanxo'
            texto column: 'trmttxto'
            ampliacionPlazo column: 'trmtampz'
            fechaRespuesta column: 'trmtfcrp'
            fechaIngreso column: 'trmtfcig'
            fechaModificacion column: 'trmtfcmd'
            fechaLectura column: 'trmtfcrv'
            externo column: 'trmtextr'
            nota column: 'trmtnota'
            estado column: 'trmtetdo'
            observaciones column: 'trmtobsr'
            fechaEnvio column: 'trmtfcen'
        }
    }
    static constraints = {
        anio(blank: true, nullable: true, attributes: [title: 'anio'])
        padre(blank: true, nullable: true, attributes: [title: 'padre'])
        tipoDocumento(blank: true, nullable: true, attributes: [title: 'tipoDocumento'])
        de(blank: true, nullable: true, attributes: [title: 'de'])
        prioridad(blank: false, nullable: false, attributes: [title: 'tipoPersona'])
        estadoTramite(blank: true, nullable: true, attributes: [title: 'estadoTramite'])
        tipoTramite(blank: true, nullable: true, attributes: [title: 'tipoTramite'])
        origenTramite(blank: true, nullable: true, attributes: [title: 'origenTRamite'])
        codigo(maxSize: 20, blank: true, nullable: true, attributes: [title: 'codigo'])
        numero(blank: false, attributes: [title: 'numero'])
        fecha(blank: true, nullable: true, attributes: [title: 'fecha'])
        fechaLimiteRespuesta(blank: true, nullable: true, attributes: [title: 'fechaLimiteRespuesta'])
        asunto(maxSize: 1023, blank: true, nullable: true, attributes: [title: 'asunto'])
        anexo(maxSize: 1023, blank: true, nullable: true, attributes: [title: 'anexo'])
        texto(blank: true, nullable: true, attributes: [title: 'texto'])
        ampliacionPlazo(blank: true, nullable: true, attributes: [title: 'ampliacionPlazo'])
        fechaRespuesta(blank: true, nullable: true, attributes: [title: 'fechaRespuesta'])
        fechaIngreso(blank: true, nullable: true, attributes: [title: 'fechaIngreso'])
        fechaModificacion(blank: true, nullable: true, attributes: [title: 'fechaModificacion'])
        fechaLectura(blank: true, nullable: true, attributes: [title: 'fechaLectura'])
        externo(maxSize: 1, blank: true, nullable: true, attributes: [title: 'externo'])
        nota(maxSize: 1023, blank: true, nullable: true, attributes: [title: 'nota'])
        estado(maxSize: 1, blank: true, nullable: true, attributes: [title: 'estado'])
        observaciones(maxSize: 255, blank: true, nullable: true, attributes: [title: 'observaciones'])
        fechaEnvio(blank: true, nullable: true, attributes: [title: 'fechaEnvio'])

    }
}