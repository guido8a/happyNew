package happy.seguridad

import happy.tramites.Departamento
import happy.tramites.PermisoTramite
import happy.tramites.PermisoUsuario
import happy.tramites.TipoDocumento
import happy.tramites.TipoDocumentoDepartamento
import org.apache.commons.lang.WordUtils

class Persona {
    Departamento departamento
//    String cedula
    String nombre
    String apellido
//    Date fechaNacimiento
    Date fechaInicio
    Date fechaFin
//    String sigla
//    String titulo
//    String cargo
    String mail
    String login
    String password
    int activo
//    String autorizacion
    Date fechaCambioPass
    String telefono
//    int jefe                //este ya no se usa: en 0 en la base de datos, comentado en persona/form_ajax, persona/list, departamento/arbol
//    String celular
    String foto
//    String codigo
    String connect
    String estado
    static hasMany = [perfiles: Sesn]

    Departamento departamentoDesde

    /*
    * Campos no persistentes para el control de permisos
    */

    def permisos = []
    static transients = ['permisos']
    static auditable = true
    /*Fin*/

    static mapping = {
        table 'prsn'
        cache usage: 'read-write', include: 'non-lazy'
        id column: 'prsn__id'
        id generator: 'identity'
        version false
        columns {
            id column: 'prsn__id'
            departamento column: 'dpto__id'
//            cedula column: 'prsncdla'
            nombre column: 'prsnnmbr'
            apellido column: 'prsnapll'
//            fechaNacimiento column: 'prsnfcna'
            fechaInicio column: 'prsnfcin'
            fechaFin column: 'prsnfcfn'
//            sigla column: 'prsnsgla'
//            titulo column: 'prsntitl'
//            cargo column: 'prsncrgo'
            mail column: 'prsnmail'
            login column: 'prsnlogn'
            password column: 'prsnpass'
            activo column: 'prsnactv'
//            autorizacion column: 'prsnatrz'
            fechaCambioPass column: 'prsnfcps'
            telefono column: 'prsntelf'
//            jefe column: 'prsnjefe'
//            celular column: 'prsntfcl'
            foto column: 'prsnfoto'
//            codigo column: 'prsncdgo'
            connect column: 'prsncnec'
            estado column: 'prsnetdo'
            departamentoDesde column: 'dptodsde'
        }
    }
    static constraints = {
        departamento(blank: true, nullable: true, attributes: [title: 'departamento'])
//        cedula(maxSize: 10, nullable: true, unique: true, blank: true, attributes: [title: 'cedula'])
        nombre(maxSize: 31, blank: false, nullable: false, attributes: [title: 'nombre'])
        apellido(maxSize: 31, blank: false, nullable: false, attributes: [title: 'apellido'])
//        fechaNacimiento(blank: true, nullable: true, attributes: [title: 'fechaNacimiento'])
        fechaInicio(blank: true, nullable: true, attributes: [title: 'fechaInicio'])
        fechaFin(blank: true, nullable: true, attributes: [title: 'fechaFin'])
//        sigla(maxSize: 4, blank: true, nullable: true, attributes: [title: 'sigla'])
//        titulo(maxSize: 4, blank: true, nullable: true, attributes: [title: 'titulo'])
//        cargo(maxSize: 127, blank: true, nullable: true, attributes: [title: 'cargo'])
        mail(maxSize: 63, unique: true, blank: false, nullable: false, attributes: [title: 'mail'])
        login(size: 1..30, unique: true, blank: true, nullable: true, attributes: [title: 'login'])
        password(maxSize: 63, blank: true, nullable: true, attributes: [title: 'password'])
        activo(blank: false, attributes: [title: 'activo'])
//        autorizacion(maxSize: 63, blank: true, nullable: true, attributes: [title: 'autorizacion'])
        fechaCambioPass(blank: true, nullable: true, attributes: [title: 'fechaCambioPass'])
        telefono(size: 1..63, blank: true, nullable: true, attributes: [title: 'telefono'])
//        jefe(blank: false, attributes: [title: 'jefe'])
//        celular(size: 1..63, blank: true, nullable: true, attributes: [title: 'celular'])
        foto(maxSize: 255, blank: true, nullable: true, attributes: [title: 'foto'])
//        codigo(size: 1..50, unique: true, blank: true, nullable: true, attributes: [title: 'codigo'])
        connect(nullable: true, blank: true, size: 1..512)
        estado(nullable: true, blank: true, size: 1..1)
        departamentoDesde(nullable: true, blank: true)
    }

    def vaciarPermisos() {
        this.permisos = []
    }

    def puedePermiso(String codigoPermiso) {
//        if (id == 5513L) {
//        println "puede permiso " + codigoPermiso + "    " + this.permisos + "   " + this.permisos.codigo
//        }
        if (this.permisos.size() > 0) {

//            if (id == 5513L) {
//            println "AQUI"
//            }
            def perm = null
            this.permisos.each {
                if (!perm) {
                    if (it.codigo == codigoPermiso) {
                        perm = PermisoUsuario.findAllByPermisoTramiteAndPersona(it, this)
                        def perm2 = null
                        perm.each { pr ->
//                            if (id == 5513L) {
//                            println "${pr.id} " + pr.permisoTramite.descripcion + "   " + pr.estaActivo
//                            }
                            if (!pr.estaActivo) {
                                perm2 = null
                            } else {
                                perm2 = pr
                            }
                        }
                        if (perm2) {
                            perm = perm2
                        }
                    }
                }
            }
            if (perm) {
                if (perm.estaActivo) {
//                    if (id == 5513L) {
//                    println "\ttrue"
//                    }
                    return true
                } else {
//                    if (id == 5513L) {
//                    println "\tfalse"
//                    }
                    return false
                }
            } else {
//                if (id == 5513L) {
//                println "\tfalse"
//                }
                return false
            }
        } else {
            def perm = PermisoUsuario.withCriteria {
                eq("persona", this)
                eq("permisoTramite", PermisoTramite.findByCodigo(codigoPermiso))
            }
            def perms = perm.findAll { it.estaActivo }
//            if (id == 5513L) {
//            perm.each {
//                println "\t\t" + it.permisoTramite.descripcion + "  " + it.fechaInicio + " - " + it.fechaFin + "   " + it.estaActivo
//            }
//            }
//            if (id == 5513L) {
//            println "\t" + (perms.size() > 0)
//          d
            return perms.size() > 0
        }
    }

    def getEstaActivo() {
        if (this.activo != 1) {
            return false
        }
        def now = new Date()
        def accs = Accs.findAllByUsuarioAndAccsFechaFinalGreaterThanEquals(this, now)
//        println "accs "+accs?.accsFechaInicial+"  "+accs?.accsFechaFinal
        def res = true
        accs.each {
//            println "it  "+it.accsFechaInicial.format('dd-MM-yyyy')+"  "+(it.accsFechaInicial >= now)+"  "+now.format('dd-MM-yyyy')
            if (res) {
                if (it.accsFechaInicial <= now) {
//                println "ret false"
                    res = false
                }
            }

        }
        return res
    }

    def getPuedeRecibir() {
        return this.puedePermiso("P010")
    }

    def getPuedeTramitar() {
        return this.puedePermiso("P006")
    }

    def getPuedeArchivar() {
        return this.puedePermiso("P011")
    }

    def getPuedeAnular() {
        return this.puedePermiso("P009")
    }

    def getPuedeReactivar() {
        return this.puedePermiso("P012")
    }

    def getPuedeRedireccionar() {
        return this.puedePermiso("P008")
    }

    def getPuedeExternos() {
        return this.puedePermiso("P015")
    }

    def getPuedeVer() {
        return this.puedePermiso("P004")
    }

    def getPuedeCopiar() {
        return this.puedePermiso("P003")
    }

    def getPuedeJefe() {
        return this.puedePermiso("P002")
    }

    def getPuedeDirector() {
        return this.puedePermiso("P001")
    }

    def getPuedeEditor() {
        return this.puedePermiso("P016")
    }


    def getPuedeAdmin() {
        return this.puedePermiso("P013")
    }

    def getPuedeCopia() {
        return this.puedePermiso("C001")
    }

    def getPuedePlazo() {
        return this.puedePermiso("P007")
    }

    def getPuedeAsociar() {
        return this.puedePermiso("P014")
    }

//    def getJefePersona(){
//        def jefe = []
//        Persona.findAllByDepartamento(this.departamento).each {p->
//            if(p.puedeJefe && p.estaActivo){
//                jefe += p
//            }
//        }
////        println("jefes " + jefe)
//        if(jefe.size() > 1) {
//            println "Hay "+jefe.size()+" jefes en el departamento "+this.departamento.descripcion
//        }
//        if(jefe.size() == 0) {
//            return null
//        }
//        return jefe.first()
//    }

//    def getJefePersona2(){
//        def jefe = []
//        Persona.findAllByDepartamento(this.departamento).each {p->
//            if(p.puedeJefe && p.estaActivo){
//                jefe += p
//            }
//        }
////        println("jefes " + jefe)
//        if(jefe.size() > 1) {
//            println "Hay "+jefe.size()+" jefes en el departamento "+this.departamento.descripcion
//        }
//        if(jefe.size() == 0) {
//            return null
//        }
//        return jefe
//    }

//    def getJefePersona() {
//        def personas = Persona.withCriteria {
//            eq("departamento", this.departamento)
//            eq("jefe", 1)
//        }
//        if (personas.size() == 1) {
//            return personas.first()
//        } else if (personas.size() > 1) {
//            println "Se encontraron ${personas.size()} jefes en el departamento ${this.departamento.descripcion}: ${personas}"
//            return personas.first()
//        } else {
//            println "No se encontraron jefes en el departamento ${this.departamento.descripcion}"
//            return null
//        }
//    }

    def getTiposDocumento() {
//        def lista = TipoDocumento.list(['sort': 'descripcion'])
        def lista = TipoDocumentoDepartamento.findAllByDepartamentoAndEstado(this.departamento, 1).tipo
        lista.sort { it.descripcion }
        if (!this.puedeExternos) {
            lista.remove(TipoDocumento.findByCodigo("DEX"))
        }
        return lista
    }

    def getConnectionString() {
        // LDAP ldap = LDAP.newInstance('ldap://192.168.0.60:389','CN=Guido Prueba,OU=GSTI,OU=GADPP,DC=pichincha,DC=local', 'prueba.prueba')
        return this.connect
    }

    def esTriangulo() {
        return this.puedePermiso("E001")
    }

    def esTrianguloOff() {
        def perm = PermisoUsuario.withCriteria {
            eq("persona", this)
            eq("permisoTramite", PermisoTramite.findByCodigo("E001"))
        }
//            println "perm "+perm
        def perms = perm.findAll { it.estaActivo }
//            println "perms "+perms
        return perms.size() > 0
    }

    def getPuedeRecibirOff() {
        def perm = PermisoUsuario.withCriteria {
            eq("persona", this)
            eq("permisoTramite", PermisoTramite.findByCodigo("P010"))
        }
//            println "perm "+perm
        def perms = perm.findAll { it.estaActivo }
//            println "perms "+perms
        return perms.size() > 0
    }

    def getPuedeAdminOff() {
        def perm = PermisoUsuario.withCriteria {
            eq("persona", this)
            eq("permisoTramite", PermisoTramite.findByCodigo("P013"))
        }
//            println "perm "+perm
        def perms = perm.findAll { it.estaActivo }
//            println "perms "+perms
        return perms.size() > 0
    }

    def getEsTriangulo() {
        return this.esTriangulo()
    }

    String toString() {
        return "${WordUtils.capitalizeFully(this.nombre)} ${WordUtils.capitalizeFully(this.apellido)}"
    }
}