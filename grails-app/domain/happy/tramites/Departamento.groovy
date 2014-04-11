package happy.tramites

import happy.seguridad.Persona

class Departamento {
    TipoDepartamento tipoDepartamento
    Departamento padre
    String codigo
    String descripcion
    String telefono
    String extension
    String direccion
    String estado /*para controlar los bloqueos*/

    Integer activo //1-> activo 0-> inactivo
    static mapping = {
        table 'dpto'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'dpto__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'dpto__id'
            tipoDepartamento column: 'tpdp__id'
            padre column: 'dptopdre'
            codigo column: 'dptocdgo'
            descripcion column: 'dptodscr'
            telefono column: 'dptotelf'
            extension column: 'dptoextn'
            direccion column: 'dptodire'
            estado column: 'dptoetdo'
            activo column: 'dptoactv'
        }
    }
    static constraints = {
        tipoDepartamento(blank: true, nullable: true, attributes: [title: 'TipoDepartamento'])
        padre(blank: true, nullable: true, attributes: [title: 'padre'])
        codigo(maxSize: 6, unique: false, blank: false, attributes: [title: 'codigo'])
        descripcion(maxSize: 63, blank: false, attributes: [title: 'descripcion'])
        telefono(maxSize: 15, blank: true, nullable: true, attributes: [title: 'telefono'])
        extension(maxSize: 7, blank: true, nullable: true, attributes: [title: 'extension'])
        direccion(maxSize: 255, blank: true, nullable: true, attributes: [title: 'direccion'])
        estado(blank: true, nullable: true, size: 1..1)
    }

    String toString() {
        return "${this.descripcion}"
    }

    def getTriangulos() {
        def triangulos = []
        Persona.findAllByDepartamento(this).each { pr ->
            def prm = PermisoUsuario.findAllByPersonaAndPermisoTramite(pr, PermisoTramite.findByCodigo("E001")).findAll {
                it.estaActivo
            }
            if (prm.size() > 0) {
                triangulos.add(pr)
            }
        }
        return triangulos
    }
}