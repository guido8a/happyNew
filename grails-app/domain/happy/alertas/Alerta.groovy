package happy.alertas

import happy.seguridad.Persona
import happy.tramites.Departamento

class Alerta {

    Persona persona
    Departamento departamento
    String mensaje
    String datos
    String accion
    String controlador
    Date fechaCreacion =  new Date()
    Date fechaRecibido

    static mapping = {
        table 'alrt'
        cache usage: 'read-write', include: 'non-lazy'
        version false
        id generator: 'identity'
        columns {
            id column: 'alrt__id'
            persona column: 'prsn__id'
            departamento column: 'dpto__id'
            mensaje column: 'alrtmesg'
            datos column: 'alrtdato'
            accion column: 'altraccn'
            controlador column: 'altrcntl'
            fechaCreacion column: 'altrfccr'
            fechaRecibido column: 'altrfcrc'
        }
    }

    static constraints = {
        persona(nullable: true,blank:true)
        departamento(nullable: true,blank:true)
        mensaje(size:1..512,nullable: false,blank: false)
        datos(size: 1..20,nullable: true,blank: true)
        accion(size: 1..50,nullable: true,blank: true)
        controlador(size: 1..30,nullable: true,blank: true)
        fechaRecibido(nullable: true,blank:true)
        fechaCreacion(nullable: false,blank:false)


    }

    String toString() {
        "${this.persona} - ${this.mensaje} "
    }
}
