package happy.utilitarios

class Parametros {
    static auditable = true
    Integer horaInicio
    Integer minutoInicio

    Integer horaFin
    Integer minutoFin

    String ipLDAP
    String ouPrincipal
    String textoCn
    String passAdm

    String imagenes

    String institucion
    Integer bloqueo
    Integer validaLDAP
    String telefono
    String departamentos

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
            textoCn column: 'prmt__cn'
            passAdm column: 'prmtpass'
            imagenes column: 'prmtimgn'
            institucion column: 'prmtinst'
            bloqueo column: 'prmtblqo'
            validaLDAP column: 'prmtvlda'
            telefono column: 'prmttelf'
            departamentos column: 'prmtdpto'
        }
    }
    static constraints = {
        horaInicio(blank: false, nullable: false, attributes: [title: 'Hora de inicio de la jornada'])
        minutoInicio(blank: false, nullable: false, attributes: [title: 'Minuto de inicio de la jornada'])
        horaFin(blank: false, nullable: false, attributes: [title: 'Hora de finalización de la jornada'])
        minutoFin(blank: false, nullable: false, attributes: [title: 'Minuto de finalización de la jornada'])
        ipLDAP(blank: false, nullable: false, attributes: [title: 'dirección IP del servidor LDAP'])
        ouPrincipal(blank: false, nullable: false, attributes: [title: 'Unidad organizacional principal: LDAP'])
        textoCn(blank: false, nullable: false, attributes: [title: 'conexión en el LDAP cn'])
        passAdm(blank: false, nullable: false, attributes: [title: 'contraseña de administracion LDAP'])

        imagenes(blank: false, nullable: false, attributes: [title: 'path de las imagenes para los pdfs'])

        institucion(blank: false, nullable: false, attributes: [title: 'Nombre de la Institución'])
        bloqueo(blank: false, nullable: false, attributes: [title: 'Bloqueo de la bandeja de entrada en horas'])
        validaLDAP(blank: false, nullable: false, inList: [1, 0], attributes: [title: 'Validar contra LDAP'])
        telefono(blank: false, nullable: false, size:7..15, attributes: [title: 'Teléfono para consulta de trámites externos'])
        departamentos(blank: true, nullable: true, size: 1..127,attributes: [title: 'Siglas de departamentos para asociar trámites'] )
    }

    def getInicioJornada() {
        return this.horaInicio.toString().padLeft(2, '0') + ":" + this.minutoInicio.toString().padLeft(2, '0')
    }

    def getFinJornada() {
        return this.horaFin.toString().padLeft(2, '0') + ":" + this.minutoFin.toString().padLeft(2, '0')
    }
}
