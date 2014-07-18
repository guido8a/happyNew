package happy.tramites

import happy.seguridad.Persona
import happy.utilitarios.Parametros

class PersonaDocumentoTramite {
    static auditable = true
    Tramite tramite

    Persona persona                         // persona q envia/recibe el tramite
    Departamento departamento
    // departamento q recibe el tramite (para la bandeja de entrada de los triangulos)

    RolPersonaTramite rolPersonaTramite     // rol de la persona/departamento (para, envia, recibe, copia, imprimir)
    //      envia    triangulo o circulo que envió
    //      recibe   triangulo o circulo que recibió
    //      para     triangulo o circulo que debe recibir, puede ser persona o dpto = debe salir en la bandeja de entrada
    //      copia    triangulo o circulo que recibe copia puede ser persona o dpto = debe salir en la bandeja de entrada
    //      imprimir circulo que puede ver, imprimir y enviar el tramite = debe salir en la bandeja de salida

    String observaciones                    // observaciones al momento de enviar o recibir

    Date fechaEnvio                         // la misma fecha que fechaEnvio del tramite
    Date fechaRecepcion                     // fecha de recepcion del doc fisico
    Date fechaLimiteRespuesta
    // segun la prioridad, se setea el mismo rato que fechaRecepcion (fechaRecepcion + horas segun prioridad)
    Date fechaRespuesta                     // fecha en la q se crea el tramite hijo de respuesta
    Date fechaArchivo
    // fecha en la q se archivo el doc fisico, no corre ningun timer, no necesita respuesta el tramite
    Date fechaAnulacion
    // fecha en la q se anulo el doc fisico, no corre ningun timer, no necesita respuesta el tramite

    EstadoTramite estado
    def diasLaborablesService
    static mapping = {
        table 'prtr'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prtr__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prtr__id'
            rolPersonaTramite column: 'rltr__id'
            persona column: 'prsn__id'
            departamento column: 'dpto__id'
            tramite column: 'trmt__id'
            observaciones column: 'prtrobsr'

            fechaEnvio column: 'prtrfcen'
            fechaRecepcion column: 'prtrfcrc'
            fechaLimiteRespuesta column: 'prtrfclr'
            fechaRespuesta column: 'prtrfcrs'
            fechaArchivo column: 'prtrfcar'
            fechaAnulacion column: 'prtrfcan'

            estado column: 'edtr__id'
        }
    }
    static constraints = {
        rolPersonaTramite(blank: false, nullable: false, attributes: [title: 'rolPersonaTramite'])
        persona(blank: true, nullable: true, attributes: [title: 'persona'])
        departamento(blank: true, nullable: true, attributes: [title: 'departamento'])
        tramite(blank: false, nullable: false, attributes: [title: 'Tramite'])
        observaciones(maxSize: 1023, blank: true, nullable: true, attributes: [title: 'observaciones'])

        fechaEnvio(nullable: true, blank: true)
        fechaRecepcion(nullable: true, blank: true)
        fechaLimiteRespuesta(nullable: true, blank: true)
        fechaRespuesta(nullable: true, blank: true)
        fechaArchivo(nullable: true, blank: true)
        fechaAnulacion(nullable: true, blank: true)

        estado(blank: true, nullable: true, attributes: [title: 'estadoTramite'])
    }

    def beforeValidate(List propertiesBeingValidated) {
        // do pre validation work based on propertiesBeingValidated
       // println "before validate"
        if(this.departamento==null && this.persona==null){
            println "dos nulos "+this.id
            this.departamento=this.getPersistentValue("departamento")
            this.persona=this.getPersistentValue("persona")
          //  println "como quedo ? "
            println "-->fin "+this.persona+"  "+this.departamento
            if(this.departamento==null && this.persona==null){
                this.tramite=null;
                this.rolPersonaTramite=null
                return false
            }

        }
        return true
    }

    def getFechaLimite() {
        def limite = this.fechaEnvio
        if (limite) {
//            def diaLaborableService
//            if(this.tramite.externo=="1")
//                return null
//            else{
//                def fechaLimite = diasLaborablesService?.fechaMasTiempo(limite, 0)
////                println " fl "+fechaLimite
//                if (fechaLimite[0]) {
//                    return fechaLimite[1]
//                } else {
////                println fechaLimite[1]
//                    return null
//                }
//            }

            return this.fechaLimiteRespuesta
        }
        return null
    }

    def getFechaBloqueo() {

        if (this.fechaRecepcion)
            return null
        else {
            if (this.tramite.externo == "1")
                return null
            def limite = this.fechaEnvio
            def par = Parametros.list([sort: "id", order: "desc"])
            def tiempoBloqueo = 1
            if (par.size() > 0) {
                par = par.pop()
                tiempoBloqueo = par.bloqueo
            }
//            println "tiempo Bloqueo " + tiempoBloqueo
            def fechaLimite = diasLaborablesService?.fechaMasTiempo(limite, tiempoBloqueo)
//            println fechaLimite
            if (fechaLimite[0]) {
                return fechaLimite[1]
            } else {
                return null
            }
        }


    }

    def getFechaCreacion() {
        return this.tramite.fechaCreacion
    }
}