package happy.tramites

import happy.tramites.TipoDependencia

class Departamento {
    TipoDependencia tipoDependencia
    Departamento padre
    String codigo
    String descripcion
    String telefono
    String extension
    String direccion
    static mapping = {
        table 'dpto'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dpto__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'dpto__id'
            tipoDependencia column: 'tpdp__id'
            padre column: 'dptopdre'
            codigo column: 'dptocdgo'
            descripcion column: 'dptodscr'
            telefono column: 'dptotelf'
            extension column: 'dptoextn'
            direccion column: 'dptodire'
        }
    }
    static constraints = {
        tipoDependencia(blank: true, nullable: true, attributes: [title: 'TipoDependencia'])
        padre(blank: true, nullable: true, attributes: [title: 'padre'])
        codigo(maxSize: 6, blank: false, attributes: [title: 'codigo'])
        descripcion(maxSize: 63, blank: false, attributes: [title: 'descripcion'])
        telefono(maxSize: 15, blank: true, nullable: true, attributes: [title: 'telefono'])
        extension(maxSize: 7, blank: true, nullable: true, attributes: [title: 'extension'])
        direccion(maxSize: 255, blank: true, nullable: true, attributes: [title: 'direccion'])
    }
}