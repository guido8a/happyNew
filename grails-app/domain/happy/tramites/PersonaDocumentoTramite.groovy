package happy.tramites

import happy.seguridad.Persona

class PersonaDocumentoTramite {
    RolPersonaTramite rolPersonaTramite
    Persona persona
    Tramite tramite
    Date fecha
    Date fechaRecibido
    String observaciones
    String permiso /*R recibir (puertas de entrada al departamento), L lectura , P impresion, LP ambos dos*/
    Date fechaEnvio
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
            tramite column: 'trmt__id'
            fecha column: 'prtrfcha'
            observaciones column: 'prtrobsr'
            permiso column: 'prtrprms'
            fechaRecibido column: 'prtrfcrc'
            fechaEnvio column: 'prtrfcen'
        }
    }
    static constraints = {
        rolPersonaTramite(blank: false, nullable: false, attributes: [title: 'rolPersonaTramite'])
        persona(blank: false, nullable: false, attributes: [title: 'persona'])
        tramite(blank: false, nullable: false, attributes: [title: 'Tramite'])
        fecha(blank: true, nullable: true, attributes: [title: 'fecha'])
        observaciones(maxSize: 1023, blank: true, nullable: true, attributes: [title: 'observaciones'])
        permiso(maxSize: 4, blank: true, nullable: true, attributes: [title: 'permiso'])
        fechaRecibido(nullable: true,blank:true)
        fechaEnvio(nullable: true,blank:true)
    }
}