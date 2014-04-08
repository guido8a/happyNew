package happy.utilitarios

class Parametros {

    Integer horaInicio
    Integer minutoInicio

    Integer horaFin
    Integer minutoFin

    String ipLDAP
    String ouPrincipal

    static mapping = {
        table 'prmt'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prmt__id'
        id generator: 'identity'
        version false
        columns {
            horaInicio column: 'prmthrin'
            minutoInicio column: 'prmtmnin'
            horaFin column: 'prmthrfn'
            minutoFin column: 'prmtmnfn'
            ipLDAP column: 'prmtldap'
            ouPrincipal column: 'prmt__ou'
        }
    }
    static constraints = {
        horaInicio(blank: false, nullable: false, attributes: [title: 'Hora de inicio de la jornada'])
        minutoInicio(blank: false, nullable: false, attributes: [title: 'Minuto de inicio de la jornada'])
        horaFin(blank: false, nullable: false, attributes: [title: 'Hora de finalización de la jornada'])
        minutoFin(blank: false, nullable: false, attributes: [title: 'Minuto de finalización de la jornada'])
        ipLDAP(blank: false, nullable: false, attributes: [title: 'dirección IP del servidor LDAP'])
        ouPrincipal(blank: false, nullable: false, attributes: [title: 'Unidad organizacional principal: LDAP'])
    }

    def getInicioJornada() {
        return this.horaInicio.toString().padLeft(2, '0') + ":" + this.minutoInicio.toString().padLeft(2, '0')
    }

    def getFinJornada() {
        return this.horaFin.toString().padLeft(2, '0') + ":" + this.minutoFin.toString().padLeft(2, '0')
    }
}
