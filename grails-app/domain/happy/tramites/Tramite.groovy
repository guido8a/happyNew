package happy.tramites

import groovy.time.TimeCategory
import happy.seguridad.Persona
import happy.utilitarios.Parametros

//import happy.utilitarios.DiasLaborablesService

class Tramite {
    static auditable = true
    Anio anio
    Tramite padre
    TipoDocumento tipoDocumento
    Persona de
    Departamento deDepartamento
    Departamento departamento
    String paraExterno
    TipoPrioridad prioridad
    EstadoTramite estadoTramite
    TipoTramite tipoTramite
//    OrigenTramite origenTramite
    String codigo
    Integer numero
    String asunto
    Integer anexo                       //1 indica que hay anexos, 0 que no hay anexos
    String texto
    int ampliacionPlazo = 0
    String externo                      //1 indica que es externo, 0 que es interno
    String nota                         //para guardar las observaciones de revision
    String estado
    String observaciones
    Date fechaCreacion                  //fecha en la q se crea el tramite
    Date fechaModificacion              //ultima modificacion realizada
    Date fechaRevision                  //ultima revision realizada --> estado cambiado a revisado
    Date fechaEnvio                     //ultimo envio realizado --> estado cambiado a enviado
//    Integer guia
    PersonaDocumentoTramite aQuienContesta      //el per doc tram q contesto

    /* Para los tramites externos dex:
            la institucion se guarda en paraExterno
            contacto y telefono agregados aqui
    */
    String contacto
    String telefono
    String numeroDocExterno

    EstadoTramiteExterno estadoTramiteExterno

    Integer esRespuesta = 0
    String conMembrete
    Integer tramitePrincipal = 0

    String esRespuestaNueva = "N"

    Tramite agregadoA = null

    String mail
    /*Texto para colocar al lado del PARA*/
    String textoPara

    String persona
    String departamentoNombre
    String departamentoSigla


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
            de column: 'prsn__de'              //el trámite envía una persona
            paraExterno column: 'trmtprex'
            prioridad column: 'tppd__id'
            estadoTramite column: 'edtr__id'
            tipoTramite column: 'tptr__id'
//            origenTramite column: 'orgn__id'
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
            observaciones type: 'text'
            fechaCreacion column: 'trmtfccr'
            fechaModificacion column: 'trmtfcmd'
            fechaRevision column: 'trmtfcrv'
            fechaEnvio column: 'trmtfcen'
            deDepartamento column: 'dpto__de'  //el trámite envía el departamento
//            guia column: 'trmtguia'

            aQuienContesta column: 'prtrcnts'

            contacto column: 'trmtcntc'
            telefono column: 'trmttfct'
            numeroDocExterno column: 'trmtndex'

            estadoTramiteExterno column: 'edtx__id'

            esRespuesta column: 'trmtesrs'

            conMembrete column: 'trmtcnmm'
            departamento column: 'dpto__id'

            tramitePrincipal column: 'trmttrpr'

            esRespuestaNueva column: 'trmtesrn'

            agregadoA column: 'trmtagrg'
            mail column: 'trmtmail'
            textoPara column: 'trmtpara'
            persona column: 'trmtprsn'
            departamentoNombre column: 'trmtdpto'
            departamentoSigla  column: 'trmtdpsg'

        }
    }
    static constraints = {
        anio(blank: true, nullable: true, attributes: [title: 'anio'])
        padre(blank: true, nullable: true, attributes: [title: 'padre'])
        tipoDocumento(blank: true, nullable: true, attributes: [title: 'tipoDocumento'])
        paraExterno(blank: true, nullable: true, attributes: [title: 'paraExterno'])
        de(blank: true, nullable: true, attributes: [title: 'de'])
        prioridad(blank: false, nullable: false, attributes: [title: 'tipoPersona'])
        estadoTramite(blank: true, nullable: true, attributes: [title: 'estadoTramite'])
        tipoTramite(blank: true, nullable: true, attributes: [title: 'tipoTramite'])
//        origenTramite(blank: true, nullable: true, attributes: [title: 'origenTRamite'])
        codigo(maxSize: 20, blank: true, nullable: true, attributes: [title: 'codigo'])
        numero(blank: false, attributes: [title: 'numero'])
        asunto(maxSize: 1023, blank: true, nullable: true, attributes: [title: 'asunto'])
        anexo(maxSize: 1023, blank: true, nullable: true, attributes: [title: 'anexo'])
        texto(blank: true, nullable: true, attributes: [title: 'texto'])
        ampliacionPlazo(blank: true, nullable: true, attributes: [title: 'ampliacionPlazo'])
        externo(maxSize: 1, blank: true, nullable: true, attributes: [title: 'externo'])
        nota(maxSize: 1023, blank: true, nullable: true, attributes: [title: 'nota'])
        estado(maxSize: 1, blank: true, nullable: true, attributes: [title: 'estado'])
        observaciones(blank: true, nullable: true, attributes: [title: 'observaciones'])
        deDepartamento(blank: true, nullable: true)
        fechaCreacion(blank: true, nullable: true, attributes: [title: 'fechaCreacion'])
        fechaModificacion(blank: true, nullable: true, attributes: [title: 'fechaModificacion'])
        fechaRevision(blank: true, nullable: true, attributes: [title: 'fechaRevision'])
        fechaEnvio(blank: true, nullable: true, attributes: [title: 'fechaEnvio'])
//        guia(blank: true, nullable: true, attributes: [title: 'guia'])
        departamento(blank: true, nullable: true, attributes: [title: 'departamento'])
        aQuienContesta(blank: true, nullable: true)

        contacto(blank: true, nullable: true, maxSize: 63)
        telefono(blank: true, nullable: true, maxSize: 15)
        numeroDocExterno(blank: true, nullable: true, maxSize: 35)
        estadoTramiteExterno(blank: true, nullable: true)

        conMembrete(blank: true, nullable: true, maxSize: 1)

        esRespuestaNueva(blank: true, nullable: true, maxSize: 1)

        agregadoA(blank: true, nullable: true)

        mail(blank: true, nullable: true, maxSize: 40)

        textoPara(blank: true, nullable: true)
/*
        persona(blank: false, nullable: false)
        departamentoNombre(blank: false, nullable: false)
        departamentoSigla(blank: false, nullable: false)
*/
        persona(blank: true, nullable: true)
        departamentoNombre(blank: true, nullable: true)
        departamentoSigla(blank: true, nullable: true)
    }

    def beforeValidate(List propertiesBeingValidated) {
        // do pre validation work based on propertiesBeingValidated
        // println "before validate"
        if (this) {
            if (this.aQuienContesta == null) {
                def oldValue = this.getPersistentValue("aQuienContesta")
                // println "old Value "+oldValue.class
                if (!oldValue != null && oldValue != "") {
                    //println "es diferente aQuienConstesta wtf "
                    try {
                        this.aQuienContesta = PersonaDocumentoTramite.get(oldValue?.id)
                    } catch (e) {
                        println "error before validate tramite"
                    }
                }

            }

        }
        return true
    }

    def getHermanos() {
        println "get getHermanos is deprecated"
        if (this.padre) {

            return Tramite.findAllByPadre(this.padre)
        } else {
            return []
        }
    }

    def getHermanosRespuesta() {
        println "get hermanosRespuesta is deprecated"
        if (this.padre) {
            return Tramite.findAllByPadreAndEsRespuesta(this.padre, 1)
        } else {
            return []
        }
    }

    def getRespuestas() {
        def hijos = Tramite.findAllByPadreAndEsRespuesta(this, 1)
        def res = []
        hijos.each { t ->
            if (t.para) {
                if (!para.estado) {
                    res.add(t)
                } else {
                    if (t.para.estado?.codigo != "E006") {
                        res.add(t)
                    }
                }
            }
        }
        return res
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

    def getAllCopias() {
        def rolCopia = RolPersonaTramite.findByCodigo("R002")
        def copias = PersonaDocumentoTramite.withCriteria {
            eq("tramite", this)
            eq("rolPersonaTramite", rolCopia)
        }
        return copias
    }

    def getCopias() {
//        def copias = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(this, RolPersonaTramite.findByCodigo("R002"))
        def estadoAnulado = EstadoTramite.findByCodigo("E006")
        def estadoArchivado = EstadoTramite.findByCodigo("E005")
        def estados = [estadoAnulado.id, estadoArchivado.id]
        def rolCopia = RolPersonaTramite.findByCodigo("R002")
        def copias = PersonaDocumentoTramite.withCriteria {
            eq("tramite", this)
            eq("rolPersonaTramite", rolCopia)
            or {
                not {
                    inList("estado", [estadoAnulado, estadoArchivado])
                }
                isNull("estado")
            }
        }
//        println this.codigo
//        println PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(this, rolCopia).estado.codigo
//        println copias.estado.codigo
//        println copias
//        copias.findAll { !estados.contains(it.estadoId) }
//        println copias.estado.codigo
//        println "******"
//        if (para) {
        return copias
//        }
//        return []
    }

    def getImprime() {
        def impirme = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(this, RolPersonaTramite.findByCodigo("I005"))
//        if (para) {
        return impirme
//        }
//        return []
    }


    def getFechaLimite() {
//        println("-->" + this.id)
        def limite = this.fechaEnvio
        if (limite) {
//            def diaLaborableService
//            def fechaLimite = diasLaborablesService?.fechaMasTiempo(limite, 2)
//
//            if (fechaLimite[0]) {
//                return fechaLimite[1]
//            } else {
////                println fechaLimite[1]
//                return null
//            }
            return this.fechaEnvio
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
            def fechaLimite = diasLaborablesService.fechaMasTiempo(limite, this.prioridad.tiempo)
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

    def getNoRecibido() {
        def prtr = PersonaDocumentoTramite.findAllByTramiteAndFechaRecepcionIsNotNull(this)
        if (prtr.size() > 0) {
            return false //al menos una persona y recibio
        } else {
            return true //enviado pero nadie ha recibido aun
        }
    }

    def getFechaBloqueo() {
//        println "get getFechaBloqueo"
        def ahora = new Date()
        def limite = this.getFechaLimite()   //fecha de envio
//        println "--limite "+limite
        if (limite) {
            def par = Parametros.list([sort: "id", order: "desc"])
//            println("-->" + par.bloqueo)

            def tiempoBloqueo = 1
            if (par.size() > 0) {
                par = par.pop()
                tiempoBloqueo = par.bloqueo

            }

//            println "tiempo Bloqueo " + tiempoBloqueo
            def fechaLimite = diasLaborablesService?.fechaMasTiempo(limite, tiempoBloqueo)
//            println "fecha limite "+fechaLimite
            if (fechaLimite[0]) {
                return fechaLimite[1]
            } else {
//                println fechaLimite[1]
                return null
            }
        } else {
            return null
        }

//            use(TimeCategory) {
//                limite = limite + 48.hours
//            }
//            return limite

    }

    def getDeTexto() {
        if (this.deDepartamento) {
            return ["codigo": this.deDepartamento.codigo, "nombre": this.deDepartamento.descripcion]
        } else {
            return ["codigo": this.de.login, "nombre": this.de.toString()]
        }
    }

    def personaPuedeLeer(Persona persona) {
        def tienePermiso = persona.puedeVer
        def departamento = persona.departamento
        if (this.tipoTramite.codigo == "C") {
            return false
        }
        if (this.de == persona) {
            return true
        }
        if (this.deDepartamento == persona.departamento && persona.esTriangulo()) {
            return true
        }
        if (!tienePermiso) {
            return false
        }
        if (this.tipoTramite.codigo == 'N') {
            return true
        } else {
            return false
//            if (this.para?.persona == persona || this.copias.contains(persona)) {
//                return true
//            }
//            if (this.para?.departamento) {
//                if(persona.departamento==this.para?.departamento && persona.esTriangulo())
//                    return true
//                else
//                    return false
//            }
        }
        return false
    }

    def personaPuedeLeerAnexo(Persona persona) {
        def tienePermiso = persona.puedeVer

        if (this.tipoTramite.codigo == "C") {
            def para = this.para
            if (persona.puedeJefe && para.departamento == persona.departamento) {
                return true
            }
            if (!persona.puedeJefe && para.persona == persona) {
                return true
            }
            return false
        }
        if (this.de == persona) {
            return true
        }
        if (this.deDepartamento == persona.departamento && persona.esTriangulo()) {
            return true
        }
        if (!tienePermiso) {
            return false
        }
        if (this.tipoTramite.codigo == 'N') {
            return true
        } else {
            return false
//            if (this.para?.persona == persona || this.copias.contains(persona)) {
//                return true
//            }
//            if (this.para?.departamento) {
//                if(persona.departamento==this.para?.departamento && persona.esTriangulo())
//                    return true
//                else
//                    return false
//            }
        }
        return false
    }

    def departamentoPuedeLeer(Departamento departamento) {
        return this.tipoTramite.codigo == 'N'
    }
}