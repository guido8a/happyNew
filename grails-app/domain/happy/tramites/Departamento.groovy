package happy.tramites

import happy.seguridad.Persona

class Departamento {
    static auditable = true
    TipoDepartamento tipoDepartamento
    Departamento padre
    String codigo
    String descripcion
    String telefono
    String extension
    String direccion
    String estado /*para controlar los bloqueos*/

    Integer externo = 0 //1->externo, 0->interno

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
            externo column: 'dptoextr'
        }
    }
    static constraints = {
        tipoDepartamento(blank: true, nullable: true, attributes: [title: 'TipoDepartamento'])
        padre(blank: true, nullable: true, attributes: [title: 'padre'])
        codigo(size: 1..15, unique: false, blank: false, attributes: [title: 'codigo'])
        descripcion(size: 1..511, blank: false, attributes: [title: 'descripcion'])
        telefono(size: 1..62, blank: true, nullable: true, attributes: [title: 'telefono'])
        extension(maxSize: 7, blank: true, nullable: true, attributes: [title: 'extension'])
        direccion(maxSize: 255, blank: true, nullable: true, attributes: [title: 'direccion'])
        estado(blank: true, nullable: true, size: 1..1)
    }

    String toString() {
        return "${this.descripcion}"
    }

    def getEstaActivo() {
        return this.activo == 1
    }

    def getTriangulos() {
        def triangulos = []
        Persona.findAllByDepartamento(this).each { pr ->
            if (pr.estaActivo && pr.esTriangulo) {
                triangulos.add(pr)
//                def prm = PermisoUsuario.findAllByPersonaAndPermisoTramite(pr, PermisoTramite.findByCodigo("E001")).findAll {
//                    it.estaActivo
//                }
//                if (prm.size() > 0) {
//                    triangulos.add(pr)
//                }
            }

        }
        return triangulos
    }

    def getJefes() {
        def jefes = []
        Persona.findAllByDepartamentoAndJefe(this, 1).each {
            if (it.estaActivo)
                jefes.add(it)
        }
        return jefes
    }
}