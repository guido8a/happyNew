package happy.seguridad

import happy.Departamento


class Persona implements Serializable {
    Departamento departamento
    int codigo
    String cedula
    String nombre
    String apellido
    Date fechaNacimiento
    Date fechaInicio
    Date fechaFin
    String sigla
    String titulo
    String cargo

    String login
    String password
    String autorizacion
    String email

    Integer activo
    Date fechaActualizacionPass

    String firma

    //static hasMany = [sesiones: Sesn, accesos: Accs, alertas: happy.alertas.Alerta]
    static hasMany = [sesiones: Sesn, accesos: Accs]
    static auditable = [ignore: ['password']]


    static mapping = {
        table 'prsn'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prsn__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prsn__id'
            departamento column: 'dpto__id'
            codigo column: 'prsncdgo'
            cedula column: 'prsncdla'
            nombre column: 'prsnnmbr'
            apellido column: 'prsnapll'
            fechaNacimiento column: 'prsnfcna'
            fechaInicio column: 'prsnfcin'
            fechaFin column: 'prsnfcfn'
            sigla column: 'prsnsgla'
            titulo column: 'prsntitl'
            cargo column: 'prsncrgo'

            login column: 'prsnlogn'
            password column: 'prsnpass'
            autorizacion column: 'prsnatrz'
            email column: 'prsnmail'

            activo column: 'prsnactv'
            fechaActualizacionPass column: 'prsnfcps'

            firma column: 'prsnfrma'
        }
    }
    static constraints = {
        cedula(size: 1..10)
        nombre(size: 1..30)
        apellido(size: 1..30)
        codigo(blank: true, nullable: true)
        fechaNacimiento(blank: true, nullable: true)
        departamento(blank: true, nullable: true)
        fechaInicio(blank: true, nullable: true)
        fechaFin(blank: true, nullable: true)
        sigla(size: 1..3, blank: true, nullable: true)
        titulo(size: 1..4, blank: true, nullable: true)
        cargo(size: 1..50, blank: true, nullable: true)

        login(size: 1..16, blank: false, nullable: false)
        password(size: 1..63, blank: false, nullable: false)
        autorizacion(size: 1..63, blank: true, nullable: true)
        email(blank: true, nullable: true, email: true)

        activo(blank: false, nullable: false)
        fechaActualizacionPass(blank: true, nullable: true)

        firma(blank: true, nullable: true)
    }

    String toString() {
        return this.titulo + " " + this.nombre + " " + this.apellido
    }
}