package happy.tramites

import groovy.time.TimeCategory
import happy.seguridad.Persona

//import happy.utilitarios.DiasLaborablesService

class Tramite {
    Anio anio
    Tramite padre
    TipoDocumento tipoDocumento
    Persona de
    Departamento deDepartamento
    TipoPrioridad prioridad
    EstadoTramite estadoTramite
    TipoTramite tipoTramite
    OrigenTramite origenTramite
    String codigo
    Integer numero
    String asunto
    Integer anexo                       //1 indica que hay anexos, 0 que no hay anexos
    String texto
    int ampliacionPlazo = 0
    String externo
    String nota                         //para guardar las observaciones de revision
    String estado
    String observaciones
    Date fechaCreacion                  //fecha en la q se crea el tramite
    Date fechaModificacion              //ultima modificacion realizada
    Date fechaRevision                  //ultima revision realizada --> estado cambiado a revisado
    Date fechaEnvio                     //ultimo envio realizado --> estado cambiado a enviado

    def diasLaborablesService

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
            asunto column: 'trmtasnt'
            anexo column: 'trmtanxo'
            texto column: 'trmttxto'
            ampliacionPlazo column: 'trmtampz'
            externo column: 'trmtextr'
            nota column: 'trmtnota'
            estado column: 'trmtetdo'
            observaciones column: 'trmtobsr'
            fechaCreacion column: 'trmtfccr'
            fechaModificacion column: 'trmtfcmd'
            fechaRevision column: 'trmtfcrv'
            fechaEnvio column: 'trmtfcen'
            deDepartamento column: 'dpto__de'
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
        asunto(maxSize: 1023, blank: true, nullable: true, attributes: [title: 'asunto'])
        anexo(maxSize: 1023, blank: true, nullable: true, attributes: [title: 'anexo'])
        texto(blank: true, nullable: true, attributes: [title: 'texto'])
        ampliacionPlazo(blank: true, nullable: true, attributes: [title: 'ampliacionPlazo'])
        externo(maxSize: 1, blank: true, nullable: true, attributes: [title: 'externo'])
        nota(maxSize: 1023, blank: true, nullable: true, attributes: [title: 'nota'])
        estado(maxSize: 1, blank: true, nullable: true, attributes: [title: 'estado'])
        observaciones(maxSize: 255, blank: true, nullable: true, attributes: [title: 'observaciones'])
        deDepartamento(blank: true, nullable: true)
        fechaCreacion(blank: true, nullable: true, attributes: [title: 'fechaCreacion'])
        fechaModificacion(blank: true, nullable: true, attributes: [title: 'fechaModificacion'])
        fechaRevision(blank: true, nullable: true, attributes: [title: 'fechaRevision'])
        fechaEnvio(blank: true, nullable: true, attributes: [title: 'fechaEnvio'])

    }

    def getPara() {
//        def para = PersonaDocumentoTramite.findByTramiteAndRolPersonaTramite(this, RolPersonaTramite.findByCodigo("R001"))
//        if (para) {
//            return [persona: para.persona, departamento: para.departamento]
//
//        }
//        return [persona: null, departamento: null]
        return PersonaDocumentoTramite.findByTramiteAndRolPersonaTramite(this, RolPersonaTramite.findByCodigo("R001"))
    }

//    def getParaObj() {
//        return PersonaDocumentoTramite.findByTramiteAndRolPersonaTramite(this, RolPersonaTramite.findByCodigo("R001"))
//    }

    def getCopias() {
        def copias = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(this, RolPersonaTramite.findByCodigo("R002"))
        if (para) {
            return copias
        }
        return []
    }

    def getImprime() {
        def impirme = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(this, RolPersonaTramite.findByCodigo("I005"))
        if (para) {
            return impirme
        }
        return []
    }


    def getFechaLimite() {
        def limite = this.fechaEnvio
        if (limite) {
//            def diaLaborableService
            def fechaLimite = diasLaborablesService?.fechaMasTiempo(limite, 2)
            if (fechaLimite[0]) {
                return fechaLimite[1]
            } else {
//                println fechaLimite[1]
                return null
            }
//            use(TimeCategory) {
//                if (limite.hours > 14 || (limite.hours >= 14 && limite.minutes > 30))
//                    limite = limite + 2.hours + 15.hours + 30.minutes
//                else
//                    limite = limite + 2.hours
//            }
//            return limite
        }
        return null
    }

    def getFechaMaximoRespuesta() {
        def fechaRecepcion = this.para?.fechaRecepcion
        if (fechaRecepcion) {
            def limite = fechaRecepcion
            def fechaLimite = diasLaborablesService.fechaMasTiempo(limite, this.prioridad.tiempo.hours)
            if (fechaLimite[0]) {
                return fechaLimite[1]
            } else {
                println fechaLimite[1]
                return null
            }
//            use(TimeCategory) {
//                if (limite.hours > 12 || (limite.hours >= 12 && limite.minutes > 30))
//                    limite = limite + this.prioridad.tiempo.hours + 15.hours + 30.minutes
//                else
//                    limite = limite + this.prioridad.tiempo.hours
//            }
//            return limite
        }
        return null
    }

    def getEstadoBandeja(persona) {
        def prtr = PersonaDocumentoTramite.findByTramiteAndPersona(this, persona)
        if (prtr?.fechaRecepcion) {
            return "E004"
        } else {
            return "E003"
        }
    }

    def getFechaBloqueo() {
        if (this.estadoTramite.codigo != "E003") {
            return null
        } else {
            def limite = this.getFechaLimite()
//            println limite
            def fechaLimite = diasLaborablesService.fechaMasDia(limite, 2)
            if (fechaLimite[0]) {
                return fechaLimite[1]
            } else {
//                println fechaLimite[1]
                return null
            }
//            use(TimeCategory) {
//                limite = limite + 48.hours
//            }
//            return limite
        }
    }

    def getDeTexto() {
        if (this.deDepartamento)
            return ["codigo": this.deDepartamento.codigo, "nombre": this.deDepartamento.descripcion]
        else
            return ["codigo": this.de.login, "nombre": this.de.toString()]
    }

}