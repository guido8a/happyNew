package happy.seguridad

import happy.alertas.Alerta
import happy.tramites.PermisoUsuario
import happy.tramites.Tramite
import happy.utilitarios.Parametros
import org.apache.directory.groovyldap.LDAP
import org.apache.directory.groovyldap.SearchScope


class LoginController {

    def mail

    def dbConnectionService

    private int borrarAlertas() {
        def esTriangulo = session.usuario.esTriangulo()
        def fecha = new Date() - 3

        def fechaStr = fecha.format("yyyy-MM-dd")

        def sqlDeleteRecibidos = ""
        def sqlDeleteAntiguos = ""

        if (esTriangulo) {
            sqlDeleteRecibidos = "DELETE FROM alrt WHERE dpto__id = ${session.departamento.id} AND altrfcrc IS NOT NULL"
            sqlDeleteAntiguos = "DELETE FROM alrt WHERE dpto__id = ${session.departamento.id} AND altrfcrc IS NULL AND altrfccr < '${fechaStr}'"
        } else {
            sqlDeleteRecibidos = "DELETE FROM alrt WHERE prsn__id = ${session.usuario.id} AND altrfcrc IS NOT NULL"
            sqlDeleteAntiguos = "DELETE FROM alrt WHERE prsn__id = ${session.usuario.id} AND altrfcrc IS NULL AND altrfccr < '${fechaStr}'"
        }

//        println sqlDeleteRecibidos
//        println sqlDeleteAntiguos

        def cn = dbConnectionService.getConnection()
        cn.execute(sqlDeleteRecibidos.toString())
        cn.execute(sqlDeleteAntiguos.toString())
        cn.close()
//        Alerta.withCriteria {
//            if (esTriangulo) {
//                eq("departamento", session.departamento)
//            } else {
//                eq("persona", session.usuario)
//            }
//            or {
//                isNotNull("fechaRecibido")
//                and {
//                    isNull("fechaRecibido")
//                    le("fechaCreacion", fecha)
//                }
//            }
//        }.each { al ->
//            al.delete(flush: true)
//        }

        return Alerta.withCriteria {
            if (esTriangulo) {
                eq("departamento", session.departamento)
            } else {
                eq("persona", session.usuario)
            }
            isNull("fechaRecibido")
        }.size()
    }

    def conecta(user, pass) {

        def prmt = Parametros.findAll()[0]
//        println "conecta " + user + " pass  " + pass
        def connect = true
        try {
//            println "connect    "+user.getConnectionString()
//            LDAP ldap = LDAP.newInstance('ldap://192.168.0.60:389',"${user.getConnectionString()}","${pass}")
            LDAP ldap = LDAP.newInstance('ldap://' + prmt.ipLDAP, "${user.getConnectionString()}", "${pass}")
//            println "connect    " + user.getConnectionString() + "\n ldap://" + prmt.ipLDAP
            // println " " + prmt.textoCn

            /*valida usuario en el LDAP */
            ldap.exists("${prmt.textoCn}")
            println "ingresa..${user.login}"

//            assert ! ldap.exists("${prmt.textoCn}")
//            def results = ldap.search('(objectClass=*)', 'dc=pichincha,dc=local', SearchScope.ONE)
        } catch (e) {
            println "no conecta ${user.login} error: " + e
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
//        println params
        render "No puede ingresar este valor"
    }

    def guardarPass() {
        def usu = Persona.get(params.id)
        usu.password = params.pass.toString().encodeAsMD5()
        usu.fechaCambioPass = new Date() + 30
        if (!usu.save(flush: true)) {
            println "Error: guardarPass " + usu.errors
            flash.message = "Ha ocurrido un error al guardar su nuevo password"
            flash.tipo = "error"
            redirect(action: 'cambiarPass')
        } else {
            redirect(controller: "inicio", action: "index")
        }
    }

    def validarSesion() {
        println "sesion creada el:" + new Date(session.getCreationTime()) + " hora actual: " + new Date()
        println "último acceso:" + new Date(session.getLastAccessedTime()) + " hora actual: " + new Date()

        println session.usuario
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
//        println "login " + params
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
//        println "valida " + params
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
//            println "sta activo " + user.estaActivo
            if (!user.estaActivo) {
                flash.message = "El usuario ingresado no esta activo."
                flash.tipo = "error"
                redirect(controller: 'login', action: "login")
                return
            } else {
                session.usuario = user
                session.usuarioKerberos = user.login
                session.time = new Date()
                session.departamento = user.departamento
                session.triangulo = user.esTriangulo()
//                println "pone valores " + session.usuario

                def perf = Sesn.findAllByUsuario(user)
                def perfiles = []
                perf.each { p ->
//                    println "verf perfil "+p+" "+p.estaActivo
                    if (p.estaActivo) {
                        perfiles.add(p)
                    }
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
                            if (par.validaLDAP == 0) {
                                admin = true
                            }
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
                        cargarPermisos()
                        def cn = "inicio"
                        def an = "index"
//                        def count = 0
                        def permisos = Prpf.findAllByPerfil(session.perfil)
                        permisos.each {
                            def perm = PermisoUsuario.findAllByPersonaAndPermisoTramite(session.usuario, it.permiso)
                            perm.each { pr ->
                                if (pr.estaActivo) {
                                    session.usuario.permisos.add(pr.permisoTramite)
                                }
                            }
                        }
//                        if (session.usuario.esTriangulo()) {
//                            count = Alerta.countByDepartamentoAndFechaRecibidoIsNull(session.departamento)
//                        } else {
//                            count = Alerta.countByPersonaAndFechaRecibidoIsNull(session.usuario)
//                        }

                        def count = borrarAlertas()

                        if (count > 0) {
                            redirect(controller: 'alertas', action: 'list')
                        } else {// llama a reporte
                            redirect(controller: 'inicio', action: 'index')

/*
                            if (session.usuario.getPuedeDirector()) {
                                redirect(controller: "retrasadosWeb", action: "reporteRetrasadosConsolidadoDir", params: [dpto: Persona.get(session.usuario.id).departamento.id, inicio: "1", dir: "1"])
                            } else {
                                if (session.usuario.getPuedeJefe()) {
                                    redirect(controller: "retrasadosWeb", action: "reporteRetrasadosConsolidado", params: [dpto: Persona.get(session.usuario.id).departamento.id], inicio: "1")
                                } else {
                                    redirect(controller: "inicio", action: "index")
                                }

                            }
*/

                        }
//                    redirect(controller: cn, action: an)

                        // registra sesion
                        //                println  "sesion ingreso: $session.id  desde ip: ${request.getRemoteAddr()}"  //activo
                        def activo = new SesionActiva()
                        activo.idSesion = session.id
                        activo.fechaInicio = new Date()
                        activo.activo = 'S'
                        activo.dirIP = request.getRemoteAddr()
                        activo.login = user.login
                        activo.save()
                        // pone X en las no .... cerradas del mismo login e ip
                        def abiertas = SesionActiva.findAllByLoginAndDirIPAndFechaFinIsNullAndIdSesionNotEqual(session.usuario.login,
                                request.getRemoteAddr(), session.id)
                        if (abiertas.size() > 0) {
                            abiertas.each { sa ->
                                sa.fechaFin = new Date()
                                sa.activo = 'X'
                                sa.save()
                            }
                        }
                        // -------------------


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
            if (p.estaActivo) {
                perfiles.add(p)
            }
        }
        return [perfilesUsr: perfiles]
    }

    def savePer() {
        def sesn = Sesn.get(params.prfl)
        def perf = sesn.perfil

        if (perf) {

            def permisos = Prpf.findAllByPerfil(perf)
//            println "perfil "+perf.descripcion+"  "+perf.codigo
            permisos.each {
//                println "perm "+it.permiso+"  "+it.permiso.codigo
                def perm = PermisoUsuario.findAllByPersonaAndPermisoTramite(session.usuario, it.permiso)
//                println "***************************** " + perm
                perm.each { pr ->
//                    println pr.permisoTramite.descripcion + "  fechas " + pr.fechaInicio + "  " + pr.fechaFin + " " + pr.id + " " + pr.estaActivo
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
            cargarPermisos()
//            if (session.an && session.cn) {
//                if (session.an.toString().contains("ajax")) {
//                    redirect(controller: "inicio", action: "index")
//                } else {
//                    redirect(controller: session.cn, action: session.an, params: session.pr)
//                }
//            } else {
//            def count = 0
//            if (session.usuario.esTriangulo()) {
//                count = Alerta.countByDepartamentoAndFechaRecibidoIsNull(session.departamento)
//            } else {
//                count = Alerta.countByPersonaAndFechaRecibidoIsNull(session.usuario)
//            }

            def count = borrarAlertas()
            if (count > 0) {
                redirect(controller: 'alertas', action: 'list')
            } else {//
//                if (session.usuario.getPuedeDirector() || session.usuario.getPuedeJefe()) {
//
//                    redirect(controller: "retrasadosWeb", action: "reporteRetrasadosConsolidadoDir", params: [dpto: Persona.get(session.usuario.id).departamento.id, inicio: "1", dir: "1"])
//                } else {
//                    if (session.usuario.getPuedeJefe()) {
//                        redirect(controller: "retrasadosWeb", action: "reporteRetrasadosConsolidado", params: [dpto: Persona.get(session.usuario.id).departamento.id, inicio: "1"])
//                    } else {
                        redirect(controller: "inicio", action: "index")
//                    }

//                }
            }
//            }
        } else {
            redirect(action: "login")
        }
    }

    def logout() {
//        println "sesion out: $session.id"  //activo
        def activo = SesionActiva.findByIdSesion(session.id)
        if (activo) {
            activo.fechaFin = new Date()
            activo.activo = 'N'
            activo.save()
        }

        session.usuario = null
        session.perfil = null
        session.permisos = null
        session.menu = null
        session.an = null
        session.cn = null
        session.invalidate()

        redirect(controller: 'login', action: 'login')
    }

    def finDeSesion() {

    }

    def cargarPermisos() {
        def permisos = Prms.findAllByPerfil(session.perfil)
//        println "CARGAR PERMISOS  perfil: " + session.perfil + "  " + session.perfil.id
//        println "Permisos:    " + permisos
        def hp = [:]
        permisos.each {
//                println(it.accion.accnNombre+ " " + it.accion.control.ctrlNombre)
            if (hp[it.accion.control.ctrlNombre.toLowerCase()]) {
                hp[it.accion.control.ctrlNombre.toLowerCase()].add(it.accion.accnNombre.toLowerCase())
            } else {
                hp.put(it.accion.control.ctrlNombre.toLowerCase(), [it.accion.accnNombre.toLowerCase()])
            }

        }
        session.permisos = hp
//        println "permisos menu " + session.permisos
    }

}