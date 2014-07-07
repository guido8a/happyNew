package happy.tramites

class OrigenTramite {
    static auditable = true
    TipoPersona tipoPersona
    String cedula
    Date fecha
    String nombre
    String nombreContacto
    String apellidoContacto
    String titulo
    String cargo
    String mail
    String telefono
    static mapping = {
        table 'orgn'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'orgn__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'orgn__id'
            tipoPersona column: 'tppr__id'
            cedula column: 'orgncdla'
            fecha column: 'orgnfcha'
            nombre column: 'orgnnmbr'
            nombreContacto column: 'orgnnbct'
            apellidoContacto column: 'orgnapct'
            titulo column: 'prsntitl'
            cargo column: 'orgncrgo'
            mail column: 'orgnmail'
            telefono column: 'orgntelf'
        }
    }
    static constraints = {
        tipoPersona(blank: true, nullable: true, attributes: [title: 'tipoPersona'])
        cedula(maxSize: 13, blank: false, attributes: [title: 'cedula'])
        fecha(blank: true, nullable: true, attributes: [title: 'fecha'])
        nombre(maxSize: 127, blank: false, attributes: [title: 'nombre'])
        nombreContacto(maxSize: 31, blank: true, nullable: true, attributes: [title: 'nombreContacto'])
        apellidoContacto(maxSize: 31, blank: false, attributes: [title: 'apellidoContacto'])
        titulo(maxSize: 4, blank: true, nullable: true, attributes: [title: 'titulo'])
        cargo(maxSize: 127, blank: true, nullable: true, attributes: [title: 'cargo'])
        mail(maxSize: 63, blank: true, nullable: true, attributes: [title: 'mail'])
        telefono(maxSize: 63, blank: true, nullable: true, attributes: [title: 'telefono'])
    }
}