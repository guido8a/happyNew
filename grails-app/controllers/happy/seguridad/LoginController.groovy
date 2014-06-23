package happy.seguridad

import happy.alertas.Alerta
import happy.tramites.PermisoUsuario
import happy.tramites.Tramite
import happy.utilitarios.Parametros
import org.apache.directory.groovyldap.LDAP
import org.apache.directory.groovyldap.SearchScope


class LoginController {

    def mail

    def conecta(user, pass) {

        def prmt = Parametros.findAll()[0]
//        println "conecta "+user+" pass  "+pass
        def connect = true
        try {
//            println "connect    "+user.getConnectionString()
//            LDAP ldap = LDAP.newInstance('ldap://192.168.0.60:389',"${user.getConnectionString()}","${pass}")
            LDAP ldap = LDAP.newInstance('ldap://' + prmt.ipLDAP, "${user.getConnectionString()}", "${pass}")
//            println "connect    " + user.getConnectionString() + "\n ldap://" + prmt.ipLDAP
            println " " + prmt.textoCn
            /*No borrar esta linea println */
            println "  exist " + ldap.exists("${prmt.textoCn}")
//            assert ! ldap.exists("${prmt.textoCn}")
//            def results = ldap.search('(objectClass=*)', 'dc=pichincha,dc=local', SearchScope.ONE)
        } catch (e) {
            println "no se conecto error: " + e
            connect = false
        }
        return connect
    }

    def index() {
        redirect(action: 'login')
    }

    def cambiarPass() {
        def usu = Persona.get(session.usuario.id)
        return [usu: usu]
    }

    def validarPass() {
        println params
        render "No puede ingresar este valor"
    }

    def guardarPass() {
        def usu = Persona.get(params.id)
        usu.password = params.pass.toString().encodeAsMD5()
        usu.fechaCambioPass = new Date() + 30
        if (!usu.save(flush: true)) {
            println "Error: " + usu.errors
            flash.message = "Ha ocurrido un error al guardar su nuevo password"
            flash.tipo = "error"
            redirect(action: 'cambiarPass')
        } else {
            redirect(controller: "inicio", action: "index")
        }
    }

    def validarSesion() {
//        println session
//        println session.usuario
        if (session.usuario) {
            render "OK"
        } else {
            flash.message = "Su sesión ha caducado, por favor ingrese nuevamente."
            render "NO"
        }
    }

    def olvidoPass() {
        def mail = params.email
        def personas = Persona.findAllByMail(mail)
        def msg
        if (personas.size() == 0) {
            flash.message = "No se encontró un usuario con ese email"
        } else if (personas.size() > 1) {
            flash.message = "Ha ocurrido un error grave (n)"
        } else {
            def persona = personas[0]

            def random = new Random()
            def chars = []
            ['A'..'Z', 'a'..'z', '0'..'9', ('!@$%^&*' as String[]).toList()].each { chars += it }
            def newPass = (1..8).collect { chars[random.nextInt(chars.size())] }.join()

            persona.password = newPass.encodeAsMD5()
            if (persona.save(flush: true)) {
                sendMail {
                    to mail
                    subject "Recuperación de contraseña"
                    body 'Hola ' + persona.login + ", tu nueva contraseña es " + newPass + "."
                }
                msg = "OK*Se ha enviado un email a la dirección " + mail + " con una nueva contraseña."
            } else {
                msg = "NO*Ha ocurrido un error al crear la nueva contraseña. Por favor vuelva a intentar."
            }
        }
        redirect(action: 'login')
    }

    def login() {
        def usu = session.usuario
        def cn = "inicio"
        def an = "index"
        if (usu) {
//            if (session.cn && session.an) {
//                cn = session.cn
//                an = session.an
//            }
            redirect(controller: cn, action: an)
        }
    }

    def validar() {
//        println "valida "+params
        def user = Persona.withCriteria {
            eq("login", params.login, [ignoreCase: true])
            eq("activo", 1)
        }

        if (user.size() == 0) {
            flash.message = "No se ha encontrado el usuario"
            flash.tipo = "error"
        } else if (user.size() > 1) {
            flash.message = "Ha ocurrido un error grave"
            flash.tipo = "error"
        } else {
            user = user[0]
            println "sta activo " + user.estaActivo
            if (!user.estaActivo) {
                flash.message = "El usuario ingresado no esta activo."
                flash.tipo = "error"
                redirect(controller: 'login', action: "login")
                return
            } else {
                session.usuario = user
                session.departamento = user.departamento
                session.triangulo = user.esTriangulo()
                def perf = Sesn.findAllByUsuario(user)
                def perfiles = []
                perf.each { p ->
//                    println "verf perfil "+p+" "+p.estaActivo
                    if (p.estaActivo)
                        perfiles.add(p)
                }
                if (perfiles.size() == 0) {
                    flash.message = "No puede ingresar porque no tiene ningun perfil asignado a su usuario. Comuníquese con el administrador."
                    flash.tipo = "error"
                    flash.icon = "icon-warning"
                    session.usuario = null
                } else {
                    def admin = false
                    perfiles.each {
                        if (it.perfil.codigo == "ADM") {
                            admin = true
                        }
                    }
                    if (!admin) {
                        def par = Parametros.list([sort: "id", order: "desc"])
                        if (par.size() > 0) {
                            par = par.pop()
                            if (par.validaLDAP == 0)
                                admin = true
                        }
                    }

                    if (!admin) {
                        if (!conecta(user, params.pass)) {
                            flash.message = "No se pudo validar la información ingresada con el sistema LDAP, contraseña incorrecta o usuario no registrado en el LDAP"
                            flash.tipo = "error"
                            flash.icon = "icon-warning"
                            session.usuario = null
                            session.departamento = null
                            redirect(controller: 'login', action: "login")
                            return
                        }
                    } else {
                        if (params.pass.encodeAsMD5() != user.password) {
                            flash.message = "Contraseña incorrecta"
                            flash.tipo = "error"
                            flash.icon = "icon-warning"
                            session.usuario = null
                            session.departamento = null
                            redirect(controller: 'login', action: "login")
                            return
                        }
                    }
                    if (perfiles.size() == 1) {
                        session.usuario.vaciarPermisos()
                        session.perfil = perfiles.first().perfil
                        def cn = "inicio"
                        def an = "index"
                        def count = Alerta.countByPersonaAndFechaRecibidoIsNull(session.usuario)
                        def permisos = Prpf.findAllByPerfil(session.perfil)
                        permisos.each {
                            def perm = PermisoUsuario.findAllByPersonaAndPermisoTramite(session.usuario, it.permiso)
                            perm.each { pr ->
                                if (pr.estaActivo) {
                                    session.usuario.permisos.add(pr.permisoTramite)
                                }
                            }
                        }
//                    println "add "+session.usuario.permisos
//                    println "puede tramite "+session.usuario.getPuedeRecibir()
                        if (count > 0)
                            redirect(controller: 'alertas', action: 'list')
                        else {
//                        println "count = 0 "+session.usuario.esTriangulo()
                            if (session.usuario.esTriangulo()) {
                                count = Alerta.countByDepartamentoAndFechaRecibidoIsNull(session.departamento)
//                            println "count "+count
                                if (count > 0)
                                    redirect(controller: 'alertas', action: 'list')
                                else
                                    redirect(controller: "inicio", action: "index")
                            } else {
                                redirect(controller: "inicio", action: "index")
                            }

                        }
//                    redirect(controller: cn, action: an)
                        return
                    } else {
                        session.usuario.vaciarPermisos()
                        redirect(action: "perfiles")
                        return
                    }
                }
            }

        }
        redirect(controller: 'login', action: "login")
    }

    def perfiles() {
        def usuarioLog = session.usuario
        def perfilesUsr = Sesn.findAllByUsuario(usuarioLog, [sort: 'perfil'])
        def perfiles = []
        perfilesUsr.each { p ->
            if (p.estaActivo)
                perfiles.add(p)
        }
        return [perfilesUsr: perfiles]
    }

    def savePer() {
        def sesn = Sesn.get(params.prfl)
        def perf = sesn.perfil
        if (perf) {

            def permisos = Prpf.findAllByPerfil(perf)
            permisos.each {
                def perm = PermisoUsuario.findAllByPersonaAndPermisoTramite(session.usuario, it.permiso)
                perm.each { pr ->
                    if (pr.estaActivo) {
                        session.usuario.permisos.add(pr.permisoTramite)
                    }
                }

            }
//            println "permisos " + session.usuario.permisos.id + "  " + session.usuario.permisos
//            println "add " + session.usuario.permisos
//            println "puede recibir " + session.usuario.getPuedeRecibir()
//            println "puede getPuedeVer " + session.usuario.getPuedeVer()
//            println "puede getPuedeAdmin " + session.usuario.getPuedeAdmin()
//            println "puede getPuedeJefe " + session.usuario.getPuedeJefe()
//            println "puede getPuedeDirector " + session.usuario.getPuedeDirector()
//            println "puede getPuedeExternos " + session.usuario.getPuedeExternos()
//            println "puede getPuedeAnular " + session.usuario.getPuedeAnular()
//            println "puede getPuedeTramitar " + session.usuario.getPuedeTramitar()
            session.perfil = perf
//            if (session.an && session.cn) {
//                if (session.an.toString().contains("ajax")) {
//                    redirect(controller: "inicio", action: "index")
//                } else {
//                    redirect(controller: session.cn, action: session.an, params: session.pr)
//                }
//            } else {
            def count = Alerta.countByPersonaAndFechaRecibidoIsNull(session.usuario)
            if (count > 0)
                redirect(controller: 'alertas', action: 'list')
            else {
//                println "count = 0 "+session.usuario.esTriangulo()
                if (session.usuario.esTriangulo()) {
                    count = Alerta.countByDepartamentoAndFechaRecibidoIsNull(session.departamento)
//                    println "count "+count
                    if (count > 0)
                        redirect(controller: 'alertas', action: 'list')
                    else
                        redirect(controller: "inicio", action: "index")
                } else {
                    redirect(controller: "inicio", action: "index")
                }

            }
//            }
        } else {
            redirect(action: "login")
        }
    }

    def logout() {
        session.usuario = null
        session.perfil = null
        session.permisos = null
        session.menu = null
        session.an = null
        session.cn = null
        session.invalidate()
        redirect(controller: 'login', action: 'login')
    }

}