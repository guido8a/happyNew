package happy.tramites

import happy.*
import happy.geografia.Comunidad
import happy.seguridad.Persona

class Tramite {
    Anio anio
    Tramite padre
    TipoDocumento tipoDocumento
    Persona de
    Persona para
    TipoPrioridad tipoPrioridad
    Persona ingresa
    EstadoTramite estadoTramite
    Proceso proceso
    PasoProceso pasoProceso
    TipoTramite tipoTramite
    Comunidad comunidad
    String numero
    Date fecha
    Date fechaLimiteRespuesta
    String asunto
    String observaciones
    String anexo
    String texto
    int ampliacionPlazo
    Date fechaRespuesta
    Date fechaIngreso
    Date fechaModificacion
    Date fechaLectura
    String externo
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
            para column: 'prsnpara'
            tipoPrioridad column: 'tppr__id'
            ingresa column: 'prsningr'
            estadoTramite column: 'edtr__id'
            proceso column: 'prcs__id'
            pasoProceso column: 'pspc__id'
            tipoTramite column: 'tptr__id'
            comunidad column: 'cmnd__id'
            numero column: 'trmtnmro'
            fecha column: 'trmtfcha'
            fechaLimiteRespuesta column: 'trmtfclr'
            asunto column: 'trmtasnt'
            observaciones column: 'trmtobsr'
            anexo column: 'trmtanxo'
            texto column: 'trmttxto'
            ampliacionPlazo column: 'trmtampz'
            fechaRespuesta column: 'trmtfcrp'
            fechaIngreso column: 'trmtfcig'
            fechaModificacion column: 'trmtfcmd'
            fechaLectura column: 'trmtfcrv'
            externo column: 'trmtextr'
        }
    }
    static constraints = {
        anio(blank: true, nullable: true)
        padre(blank: true, nullable: true)
        tipoDocumento(blank: true, nullable: true)
        de(blank: true, nullable: true)
        para(blank: true, nullable: true)
        tipoPrioridad(blank: true, nullable: true)
        ingresa(blank: true, nullable: true)
        estadoTramite(blank: true, nullable: true)
        proceso(blank: true, nullable: true)
        pasoProceso(blank: true, nullable: true)
        tipoTramite(blank: true, nullable: true)
        comunidad(blank: true, nullable: true)
        numero(maxSize: 20, blank: false)
        fecha(blank: true, nullable: true)
        fechaLimiteRespuesta(blank: true, nullable: true)
        asunto(blank: true, nullable: true)
        observaciones(blank: true, nullable: true)
        anexo(maxSize: 255, blank: true, nullable: true)
        texto(blank: true, nullable: true)
        ampliacionPlazo(blank: true, nullable: true)
        fechaRespuesta(blank: true, nullable: true)
        fechaIngreso(blank: true, nullable: true)
        fechaModificacion(blank: true, nullable: true)
        fechaLectura(blank: true, nullable: true)
        externo(maxSize: 1, blank: true, nullable: true)
    }
}