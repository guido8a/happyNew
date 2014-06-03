package happy.seguridad

import happy.tramites.Departamento
import happy.tramites.PermisoTramite
import happy.tramites.PermisoUsuario
import happy.tramites.TipoDocumento
import happy.tramites.TipoDocumentoDepartamento
import org.apache.commons.lang.WordUtils

class Persona {
    Departamento departamento
    String cedula
    String nombre
    String apellido
    Date fechaNacimiento
    Date fechaInicio
    Date fechaFin
    String sigla
    String titulo
    String cargo
    String mail
    String login
    String password
    int activo
    String autorizacion
    Date fechaCambioPass
    String telefono
    int jefe
    String celular
    String foto
    String codigo
    String connect
    String estado
    static hasMany = [perfiles: Sesn]

    /*
    * Campos no persistentes para el control de permisos
    */

    def permisos = []
    static transients = ['permisos']
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
            cedula column: 'prsncdla'
            nombre column: 'prsnnmbr'
            apellido column: 'prsnapll'
            fechaNacimiento column: 'prsnfcna'
            fechaInicio column: 'prsnfcin'
            fechaFin column: 'prsnfcfn'
            sigla column: 'prsnsgla'
            titulo column: 'prsntitl'
            cargo column: 'prsncrgo'
            mail column: 'prsnmail'
            login column: 'prsnlogn'
            password column: 'prsnpass'
            activo column: 'prsnactv'
            autorizacion column: 'prsnatrz'
            fechaCambioPass column: 'prsnfcps'
            telefono column: 'prsntelf'
            jefe column: 'prsnjefe'
            celular column: 'prsntfcl'
            foto column: 'prsnfoto'
            codigo column: 'prsncdgo'
            connect column: 'prsncnec'
            estado column: 'prsnetdo'
        }
    }
    static constraints = {
        departamento(blank: true, nullable: true, attributes: [title: 'departamento'])
        cedula(maxSize: 10, nullable: true, unique: true, blank: true, attributes: [title: 'cedula'])
        nombre(maxSize: 31, blank: false,nullable: false, attributes: [title: 'nombre'])
        apellido(maxSize: 31, blank: false,nullable: false, attributes: [title: 'apellido'])
        fechaNacimiento(blank: true, nullable: true, attributes: [title: 'fechaNacimiento'])
        fechaInicio(blank: true, nullable: true, attributes: [title: 'fechaInicio'])
        fechaFin(blank: true, nullable: true, attributes: [title: 'fechaFin'])
        sigla(maxSize: 4, blank: true, nullable: true, attributes: [title: 'sigla'])
        titulo(maxSize: 4, blank: true, nullable: true, attributes: [title: 'titulo'])
        cargo(maxSize: 127, blank: true, nullable: true, attributes: [title: 'cargo'])
        mail(maxSize: 63, unique: true, blank: false, nullable: false, attributes: [title: 'mail'])
        login(size: 1..30, unique: true, blank: true, nullable: true, attributes: [title: 'login'])
        password(maxSize: 63, blank: true, nullable: true, attributes: [title: 'password'])
        activo(blank: false, attributes: [title: 'activo'])
        autorizacion(maxSize: 63, blank: true, nullable: true, attributes: [title: 'autorizacion'])
        fechaCambioPass(blank: true, nullable: true, attributes: [title: 'fechaCambioPass'])
        telefono(size: 1..63, blank: true, nullable: true, attributes: [title: 'telefono'])
        jefe(blank: false, attributes: [title: 'jefe'])
        celular(size: 1..63, blank: true, nullable: true, attributes: [title: 'celular'])
        foto(maxSize: 255, blank: true, nullable: true, attributes: [title: 'foto'])
        codigo(size: 1..50, unique: true, blank: true, nullable: true, attributes: [title: 'codigo'])
        connect(nullable: true, blank: true, size: 1..512)
        estado(nullable: true, blank: true, size: 1..1)
    }

    def vaciarPermisos(){
        this.permisos=[]
    }

    def getEstaActivo() {
        if (this.activo != 1)
            return false
        def now = new Date()
        def accs = Accs.findAllByUsuarioAndAccsFechaFinalGreaterThan(this, now)
        accs.each {
            if (it.accsFechaInicial >= now)
                return false
        }
        return true
    }

    def getPuedeRecibir() {

//        def perm = PermisoUsuario.withCriteria {
//            eq("persona", this)
//            eq("permisoTramite", PermisoTramite.findByCodigo("P010"))
//        }
//        def permisos = perm.findAll { it.estaActivo }
//        return permisos.size() > 0
        if(this.permisos.size()>0) {
            def perm = null
            this.permisos.each {
                if (!perm) {
                    if (it.codigo == "P010") {
                        perm = PermisoUsuario.findByPermisoTramite(it)
//                    println "prus "+perm+" "+perm.estaActivo
                    }
                }
            }
            if (perm) {
                if (perm.estaActivo) {
                    return true
                }
            } else {
                return false
            }
        }else{
//            println "else111  "
            def perm = PermisoUsuario.withCriteria {
                eq("persona", this)
                eq("permisoTramite", PermisoTramite.findByCodigo("P010"))
            }
//            println "perm "+perm
            def perms = perm.findAll { it.estaActivo }
//            println "perms "+perms
            return perms.size() > 0
        }


    }

    def getPuedeTramitar() {
//        println "puede trami "+this.permisos
        if(this.permisos.size()>0) {
            def perm = null
            this.permisos.each {
                if (!perm) {
                    if (it.codigo == "P006") {
                        perm = PermisoUsuario.findByPermisoTramite(it)
//                    println "prus "+perm+" "+perm.estaActivo
                    }
                }
            }
            if (perm) {
                if (perm.estaActivo) {
                    return true
                }
            } else {
                return false
            }
        }else{
//            println "else111  "
            def perm = PermisoUsuario.withCriteria {
                eq("persona", this)
                eq("permisoTramite", PermisoTramite.findByCodigo("P006"))
            }
//            println "perm "+perm
            def perms = perm.findAll { it.estaActivo }
//            println "perms "+perms
            return perms.size() > 0
        }
//        def perm = PermisoUsuario.withCriteria {
//            eq("persona", this)
//            eq("permisoTramite", PermisoTramite.findByCodigo("P006"))
//        }
//        def permisos = perm.findAll { it.estaActivo }
//        return permisos.size() > 0
    }

    def getPuedeArchivar() {
//        def perm = PermisoUsuario.withCriteria {
//            eq("persona", this)
//            eq("permisoTramite", PermisoTramite.findByCodigo("P011"))
//        }
//        def permisos = perm.findAll { it.estaActivo }
//        return permisos.size() > 0
        if(this.permisos.size()>0) {
            def perm = null
            this.permisos.each {
                if (!perm) {
                    if (it.codigo == "P011") {
                        perm = PermisoUsuario.findByPermisoTramite(it)
//                    println "prus "+perm+" "+perm.estaActivo
                    }
                }
            }
            if (perm) {
                if (perm.estaActivo) {
                    return true
                }
            } else {
                return false
            }
        }else{
//            println "else111  "
            def perm = PermisoUsuario.withCriteria {
                eq("persona", this)
                eq("permisoTramite", PermisoTramite.findByCodigo("P011"))
            }
//            println "perm "+perm
            def perms = perm.findAll { it.estaActivo }
//            println "perms "+perms
            return perms.size() > 0
        }
    }

    def getPuedeAnular() {
//        def perm = PermisoUsuario.withCriteria {
//            eq("persona", this)
//            eq("permisoTramite", PermisoTramite.findByCodigo("P009"))
//        }
//        def permisos = perm.findAll { it.estaActivo }
//        return permisos.size() > 0
        if(this.permisos.size()>0) {
            def perm = null
            this.permisos.each {
                if (!perm) {
                    if (it.codigo == "P009") {
                        perm = PermisoUsuario.findByPermisoTramite(it)
//                    println "prus "+perm+" "+perm.estaActivo
                    }
                }
            }
            if (perm) {
                if (perm.estaActivo) {
                    return true
                }
            } else {
                return false
            }
        }else{
//            println "else111  "
            def perm = PermisoUsuario.withCriteria {
                eq("persona", this)
                eq("permisoTramite", PermisoTramite.findByCodigo("P009"))
            }
//            println "perm "+perm
            def perms = perm.findAll { it.estaActivo }
//            println "perms "+perms
            return perms.size() > 0
        }
    }

    def getPuedeReactivar() {
//        def perm = PermisoUsuario.withCriteria {
//            eq("persona", this)
//            eq("permisoTramite", PermisoTramite.findByCodigo("P012"))
//        }
//        def permisos = perm.findAll { it.estaActivo }
//        return permisos.size() > 0
        if(this.permisos.size()>0) {
            def perm = null
            this.permisos.each {
                if (!perm) {
                    if (it.codigo == "P012") {
                        perm = PermisoUsuario.findByPermisoTramite(it)
//                    println "prus "+perm+" "+perm.estaActivo
                    }
                }
            }
            if (perm) {
                if (perm.estaActivo) {
                    return true
                }
            } else {
                return false
            }
        }else{
//            println "else111  "
            def perm = PermisoUsuario.withCriteria {
                eq("persona", this)
                eq("permisoTramite", PermisoTramite.findByCodigo("P012"))
            }
//            println "perm "+perm
            def perms = perm.findAll { it.estaActivo }
//            println "perms "+perms
            return perms.size() > 0
        }

    }

    def getPuedeRedireccionar() {
//        def perm = PermisoUsuario.withCriteria {
//            eq("persona", this)
//            eq("permisoTramite", PermisoTramite.findByCodigo("P008"))
//        }
//        def permisos = perm.findAll { it.estaActivo }
//        return permisos.size() > 0

        if(this.permisos.size()>0) {
            def perm = null
            this.permisos.each {
                if (!perm) {
                    if (it.codigo == "P008") {
                        perm = PermisoUsuario.findByPermisoTramite(it)
//                    println "prus "+perm+" "+perm.estaActivo
                    }
                }
            }
            if (perm) {
                if (perm.estaActivo) {
                    return true
                }
            } else {
                return false
            }
        }else{
//            println "else111  "
            def perm = PermisoUsuario.withCriteria {
                eq("persona", this)
                eq("permisoTramite", PermisoTramite.findByCodigo("P008"))
            }
//            println "perm "+perm
            def perms = perm.findAll { it.estaActivo }
//            println "perms "+perms
            return perms.size() > 0
        }
    }

    def getPuedeExternos() {
//        def perm = PermisoUsuario.withCriteria {
//            eq("persona", this)
//            eq("permisoTramite", PermisoTramite.findByCodigo("P015"))
//        }
//        def permisos = perm.findAll { it.estaActivo }
//        return permisos.size() > 0

        if(this.permisos.size()>0) {
            def perm = null
            this.permisos.each {
                if (!perm) {
                    if (it.codigo == "P015") {
                        perm = PermisoUsuario.findByPermisoTramite(it)
//                    println "prus "+perm+" "+perm.estaActivo
                    }
                }
            }
            if (perm) {
                if (perm.estaActivo) {
                    return true
                }
            } else {
                return false
            }
        }else{
//            println "else111  "
            def perm = PermisoUsuario.withCriteria {
                eq("persona", this)
                eq("permisoTramite", PermisoTramite.findByCodigo("P015"))
            }
//            println "perm "+perm
            def perms = perm.findAll { it.estaActivo }
//            println "perms "+perms
            return perms.size() > 0
        }
    }

    def getPuedeVer() {
//        def perm = PermisoUsuario.withCriteria {
//            eq("persona", this)
//            eq("permisoTramite", PermisoTramite.findByCodigo("P004"))
//        }
//        def permisos = perm.findAll { it.estaActivo }
//        return permisos.size() > 0
        if(this.permisos.size()>0) {
            def perm = null
            this.permisos.each {
                if (!perm) {
                    if (it.codigo == "P004") {
                        perm = PermisoUsuario.findByPermisoTramite(it)
//                    println "prus "+perm+" "+perm.estaActivo
                    }
                }
            }
            if (perm) {
                if (perm.estaActivo) {
                    return true
                }
            } else {
                return false
            }
        }else{
//            println "else111  "
            def perm = PermisoUsuario.withCriteria {
                eq("persona", this)
                eq("permisoTramite", PermisoTramite.findByCodigo("P004"))
            }
//            println "perm "+perm
            def perms = perm.findAll { it.estaActivo }
//            println "perms "+perms
            return perms.size() > 0
        }

    }


    def getPuedeAdmin() {
//        def perm = PermisoUsuario.withCriteria {
//            eq("persona", this)
//            eq("permisoTramite", PermisoTramite.findByCodigo("P013"))
//        }
//        def permisos = perm.findAll { it.estaActivo }
//        return permisos.size() > 0
//        println "puede admin "+this.permisos
        if(this.permisos.size()>0) {
            def perm = null
            this.permisos.each {
                if (!perm) {
                    if (it.codigo == "P013") {
                        perm = PermisoUsuario.findByPermisoTramite(it)
//                    println "prus "+perm+" "+perm.estaActivo
                    }
                }
            }
            if (perm) {
                if (perm.estaActivo) {
                    return true
                }
            } else {
                return false
            }
        }else{
//            println "else111  "
            def perm = PermisoUsuario.withCriteria {
                eq("persona", this)
                eq("permisoTramite", PermisoTramite.findByCodigo("P013"))
            }
//            println "perm "+perm
            def perms = perm.findAll { it.estaActivo }
//            println "perms "+perms
            return perms.size() > 0
        }
    }

    def getJefePersona() {
        def personas = Persona.withCriteria {
            eq("departamento", this.departamento)
            eq("jefe", 1)
        }
        if (personas.size() == 1) {
            return personas.first()
        } else if (personas.size() > 1) {
            println "Se encontraron ${personas.size()} jefes en el departamento ${this.departamento.descripcion}: ${personas}"
            return personas.first()
        } else {
            println "No se encontraron jefes en el departamento ${this.departamento.descripcion}"
            return null
        }
    }

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
        def perm = PermisoUsuario.withCriteria {
            eq("persona", this)
            eq("permisoTramite", PermisoTramite.findByCodigo("E001"))
        }
        def permisos = perm.findAll { it.estaActivo }
        return permisos.size() > 0
    }

    def getEsTriangulo() {
        return this.esTriangulo()
    }

    String toString() {
        return "${WordUtils.capitalizeFully(this.nombre)} ${WordUtils.capitalizeFully(this.apellido)}"
    }
}