package happy.seguridad

import groovy.json.JsonBuilder
import happy.alertas.Alerta
import happy.tramites.Departamento
import happy.tramites.PermisoTramite
import happy.tramites.PermisoUsuario
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite
import happy.utilitarios.Parametros
import org.apache.commons.lang.WordUtils
import static java.awt.RenderingHints.*
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import org.apache.directory.groovyldap.LDAP
import org.apache.directory.groovyldap.SearchScope


class PersonaController extends happy.seguridad.Shield {

    def tramitesService

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def getLista(params, all) {
//        println "PARAMS: " + params
//        String llega = params.search
//        println llega
//        println "iso***" + llega.getBytes('ISO-8859-1')
//        println "utf-8***" + llega.getBytes('UTF-8')
        params.offset = params.offset ?: 0
        if (params.search) {
            def tx = params.search.toList()
            tx.size().times() {
                if (tx[it].toString().getBytes('UTF-8').size() > 1) {
                    println "posibe carácter especial: ${tx[it]} es en utf-8:" + tx[it].toString().getBytes('UTF-8')
                    if (tx[it].toString().getBytes('UTF-8')[1] == -123) {
                        println "llega texto en ISO-8859-1"
                    }
                }
            }
        }

        def prms = params.clone()

        if (prms.sort == "perfil") {
            prms.remove("sort")
        }

        if (all || params.estado == "admin") {
            prms.remove("offset")
            prms.remove("max")
        }
        def permisoAdmin = PermisoTramite.findByCodigo("P013")
        def lista

        def c = Persona.createCriteria()
        lista = c.list(prms) {
            and {
                if (prms.search) {
                    or {
                        ilike("nombre", "%" + prms.search + "%")
                        ilike("apellido", "%" + prms.search + "%")
                        ilike("login", "%" + prms.search + "%")
                        departamento {
                            or {
                                ilike("descripcion", "%" + prms.search + "%")
                            }
                        }
                    }
                }
                if (params.perfil) {
                    perfiles {
                        eq("perfil", Prfl.get(params.perfil.toLong()))
                    }
                }
                if (params.estado) {
                    if (params.estado == "jefe") {
                        eq("jefe", 1)
                    }
                    if (params.estado == "usuario") {
                        eq("activo", 1)
                    }
                    if (params.estado == "inactivo") {
                        eq("activo", 0)
                    }
                }
            }
        }


        if (params.estado == "usuario") {
            lista = lista.findAll { it.estaActivo }
        }
        if (params.estado == "inactivo") {
            lista = lista.findAll { !it.estaActivo }
        }
        if (params.estado == "admin") {
            lista = lista.findAll { it.puedeAdmin }
            if (!all && /*params.offset && */params.max && lista.size() > params.max.toInteger()) {
                def init = params.offset.toInteger()/* * params.max.toInteger()*/
                def fin = init + params.max.toInteger()
                if (fin > lista.size()) {
                    fin = lista.size()
                }
                lista = lista.subList(init, fin)
            }
        }
        return lista
    }

    def uploadFile() {
        def usuario = Persona.get(session.usuario.id)
        def path = servletContext.getRealPath("/") + "images/perfiles/"    //web-app/archivos
        new File(path).mkdirs()

        def f = request.getFile('file')  //archivo = name del input type file

        def okContents = ['image/png': "png", 'image/jpeg': "jpeg", 'image/jpg': "jpg"]

        if (f && !f.empty) {
            def fileName = f.getOriginalFilename() //nombre original del archivo
            def ext

            if (okContents.containsKey(f.getContentType())) {
                ext = okContents[f.getContentType()]
                fileName = usuario.id + "." + ext
                def pathFile = path + fileName
                def nombre = fileName
                try {
                    f.transferTo(new File(pathFile)) // guarda el archivo subido al nuevo path
                } catch (e) {
                    println "????????\n" + e + "\n???????????"
                }
                /* RESIZE */
                def img = ImageIO.read(new File(pathFile))
                def scale = 0.5
                def minW = 300 * 0.7
                def minH = 400 * 0.7
                def maxW = minW * 3
                def maxH = minH * 3
                def w = img.width
                def h = img.height

                if (w > maxW || h > maxH || w < minW || h < minH) {
                    def newW = w * scale
                    def newH = h * scale
                    def r = 1
                    if (w > h) {
                        if (w > maxW) {
                            r = w / maxW
                            newW = maxW
                            println "w>maxW:    r=" + r + "   newW=" + newW
                        }
                        if (w < minW) {
                            r = minW / w
                            newW = minW
                            println "w<minW:    r=" + r + "   newW=" + newW
                        }
                        newH = h / r
                        println "newH=" + newH
                    } else {
                        if (h > maxH) {
                            r = h / maxH
                            newH = maxH
                            println "h>maxH:    r=" + r + "   newH=" + newH
                        }
                        if (h < minH) {
                            r = minH / h
                            newH = minH
                            println "h<minxH:    r=" + r + "   newH=" + newH
                        }
                        newW = w / r
                        println "newW=" + newW
                    }
                    println newW + "   " + newH

                    newW = Math.round(newW.toDouble()).toInteger()
                    newH = Math.round(newH.toDouble()).toInteger()

                    println newW + "   " + newH

                    new BufferedImage(newW, newH, img.type).with { j ->
                        createGraphics().with {
                            setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC)
                            drawImage(img, 0, 0, newW, newH, null)
                            dispose()
                        }
                        ImageIO.write(j, ext, new File(pathFile))
                    }
                }

                /* fin resize */

                if (!usuario.foto || usuario.foto != nombre) {
                    def fotoOld = usuario.foto
                    if (fotoOld) {
                        def file = new File(path + fotoOld)
                        file.delete()
                    }
                    usuario.foto = nombre
                    if (usuario.save(flush: true)) {
                        def data = [
                                files: [
                                        [
                                                name: nombre,
                                                url : resource(dir: 'images/perfiles/', file: nombre),
                                                size: f.getSize(),
                                                url : pathFile
                                        ]
                                ]
                        ]
                        def json = new JsonBuilder(data)
                        render json
                        return
                    } else {
                        def data = [
                                files: [
                                        [
                                                name : nombre,
                                                size : f.getSize(),
                                                error: "Ha ocurrido un error al guardar"
                                        ]
                                ]
                        ]
                        def json = new JsonBuilder(data)
                        render json
                        return
                    }
                } else {
                    def data = [
                            files: [
                                    [
                                            name: nombre,
                                            url : resource(dir: 'images/perfiles/', file: nombre),
                                            size: f.getSize(),
                                            url : pathFile
                                    ]
                            ]
                    ]
                    def json = new JsonBuilder(data)
                    render json
                    return
                }
            } else {
                def data = [
                        files: [
                                [
                                        name : fileName + "." + ext,
                                        size : f.getSize(),
                                        error: "Extensión no permitida"
                                ]
                        ]
                ]

                def json = new JsonBuilder(data)
                render json
                return
            }
        }
        render "OK"
    }

    def resizeCropImage() {
        def usuario = Persona.get(session.usuario.id)
        def path = servletContext.getRealPath("/") + "images/perfiles/"    //web-app/archivos
        def fileName = usuario.foto
        def ext = fileName.split("\\.").last()
        def pathFile = path + fileName
        /* RESIZE */
        def img = ImageIO.read(new File(pathFile))

        def oldW = img.getWidth()
        def oldH = img.getHeight()

        int newW = 300 * 0.7
        int newH = 400 * 0.7
        int newX = params.x.toInteger()
        int newY = params.y.toInteger()
        def rx = newW / (params.w.toDouble())
        def ry = newH / (params.h.toDouble())

        int resW = oldW * rx
        int resH = oldH * ry
        int resX = newX * rx * -1
        int resY = newY * ry * -1

        new BufferedImage(newW, newH, img.type).with { j ->
            createGraphics().with {
                setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC)
                drawImage(img, resX, resY, resW, resH, null)
                dispose()
            }
            ImageIO.write(j, ext, new File(pathFile))
        }
        /* fin resize */
        render "OK"
    }

    def personal() {
        def usuario = Persona.get(session.usuario.id)
        def dep = usuario.departamento
        def triangulos = dep.getTriangulos()

        def personas = Persona.findAllByDepartamentoAndActivo(dep, 1, [sort: 'apellido', order: 'apellido'])
        def personasFiltradas = []

        personas.each {
            if (it?.estaActivo) {
                personasFiltradas += it
            }

        }

        personasFiltradas.remove(usuario)
        return [usuario: usuario, params: params, triangulos: triangulos, personas: personasFiltradas]
    }

    def personalAdm() {
        if (session.usuario.puedeAdmin) {
            def usuario = Persona.get(params.id)
            def dep = usuario.departamento
            def triangulos = dep.getTriangulos()
            def personas = Persona.findAllByDepartamentoAndActivo(dep, 1)
            personas.remove(usuario)
            return [usuario: usuario, params: params, triangulos: triangulos, personas: personas]
        } else {
            response.sendError(403)
        }

    }

    def personalArbol() {
        def usuario = Persona.get(params.id)
        def dep = usuario.departamento
        def triangulos = dep.getTriangulos()
        def personas = Persona.findAllByDepartamentoAndActivo(dep, 1)
        personas.remove(usuario)
        return [usuario: usuario, params: params, triangulos: triangulos, personas: personas]
    }

    def loadFoto() {
        def usuario = Persona.get(session.usuario.id)
        def path = servletContext.getRealPath("/") + "images/perfiles/" //web-app/archivos
        def img
        def w
        def h
        if (usuario.foto) {
            img = ImageIO.read(new File(path + usuario.foto));
            w = img.getWidth();
            h = img.getHeight();
        } else {
            w = 0
            h = 0
        }
        return [usuario: usuario, w: w, h: h]
    }

    def validarPass_ajax() {
        def usuario = Persona.get(session.usuario.id)
        render usuario.password == params.password_actual.toString().trim().encodeAsMD5()
    }

    def savePass_ajax() {
        def usuario = Persona.get(session.usuario.id)
        if (usuario.password == params.password_actual.toString().trim().encodeAsMD5()) {
            usuario.password = params.password.toString().trim().encodeAsMD5()
            if (usuario.save(flush: true)) {
                render "OK_Password actualizado correctamente"
            } else {
                render "NO_Ha ocurrido un error al actualizar el password: " + renderErrors(bean: usuario)
            }
        } else {
            render "NO_El password actual no coincide"
        }
    }

    def saveTelf() {
        def usuario = Persona.get(session.usuario.id)
        def telefono = params.telefono
        usuario.telefono = params.telefono?.trim()
        if (usuario.save(flush: true)) {
            render "OK_Teléfono actualizado correctamente"
        }
    }

    def accesos() {
        def usu = Persona.get(params.id)
        def accesos = Accs.findAllByUsuario(usu, [sort: 'accsFechaInicial'])
        return [accesos: accesos]
    }

    def permisos() {
        def usu = Persona.get(params.id)
        def permisos = PermisoUsuario.findAllByPersona(usu, [sort: 'fechaInicio'])
        return [permisos: permisos]
    }

    def ausentismo() {
        def usu = Persona.get(params.id)
        return [usuario: usu/*, perfilesUsu: perfilesUsu, permisosUsu: permisosUsu*/]
    }

    def config() {
        def usu = Persona.get(params.id)
        def perfilesUsu = Sesn.findAllByUsuario(usu)
        def pers = []
        perfilesUsu.each {
            if (it.estaActivo) {
                pers.add(it.perfil.id)
            }
        }
        def permisosUsu = PermisoUsuario.findAllByPersona(usu).permisoTramite.id
        return [usuario: usu, perfilesUsu: pers, permisosUsu: permisosUsu]
    }

    def savePermisos_ajax() {
        params.asignadoPor = session.usuario
        def perm = new PermisoUsuario(params)
        if (!perm.save(flush: true)) {
            render "NO_" + g.renderErrors(bean: perm)
        } else {
            render "OK_Permiso agregado"
        }
    }


    def terminarPermiso_ajax() {
        def perm = PermisoUsuario.get(params.id)
        def now = new Date().clearTime()
        if (perm.fechaFin && perm.fechaFin <= now) {
            render "INFO_El permiso ya ha caducado, no puede terminarlo de nuevo."
        } else {
            if (perm.fechaInicio <= now && (perm.fechaFin >= now || !perm.fechaFin)) {
                perm.fechaFin = now
                if (!perm.save(flush: true)) {
                    render "NO_" + renderErrors(bean: perm)
                } else {
                    render "OK_Terminación del permiso exitosa"
                }
            } else {
                render "INFO_No puede terminar un permiso que no ha empezado aún. Puede eliminarlo."
            }
        }
    }

    def eliminarPermiso_ajax() {
        def perm = PermisoUsuario.get(params.id)
        def now = new Date()
        if (perm.fechaFin && perm.fechaFin <= now) {
            render "INFO_El permiso ya ha caducado, no puede ser eliminado."
        } else {
            if (perm.fechaInicio <= now && (perm.fechaFin >= now || !perm.fechaFin)) {
                render "INFO_No puede eliminar un permiso en curso. Puede terminarlo."
            } else {
                try {
                    perm.delete(flush: true)
                    render "OK_Permiso eliminado."
                } catch (e) {
                    render "NO_Ha ocurrido un error al eliminar el permiso."
                }
            }
        }
    }

    def saveAccesos_ajax() {
        println "asig acc " + params
        params.asignadoPor = session.usuario
        def accs = new Accs(params)
        if (!accs.save(flush: true)) {
            render "NO_" + g.renderErrors(bean: accs)
        } else {
            accs.accsFechaFinal = new Date().parse("dd-MM-yyyy HH:mm", accs.accsFechaFinal.format("dd-MM-yyyy") + " 23:55")
            if (params.nuevoTriangulo) {

                def pers = Sesn.findAllByUsuario(accs.usuario).perfil
                def perfil = null
                pers.each { p ->
                    if (!perfil) {
                        Prpf.findAllByPerfil(p).each { pr ->
                            if (pr.permiso.codigo == "E001") {
                                perfil = p
                            }
                        }
                    }
                }
                if (perfil) {
                    def asignado = Persona.get(params.nuevoTriangulo)
                    if (accs.accsObservaciones != null) {
                        accs.accsObservaciones += "; Nuevo receptor: ${asignado.login} del ${accs.accsFechaInicial.format('dd-MM-yyyy')} al ${accs.accsFechaFinal.format('dd-MM-yyyy')}"

                    } else {
                        accs.accsObservaciones = "Nuevo receptor: ${asignado.login} del ${accs.accsFechaInicial.format('dd-MM-yyyy')} al ${accs.accsFechaFinal.format('dd-MM-yyyy')}"
                    }
                    def sesion = new Sesn()
                    sesion.perfil = perfil
                    sesion.usuario = asignado
                    sesion.fechaInicio = accs.accsFechaInicial
                    sesion.fechaFin = accs.accsFechaFinal
                    sesion.save(flush: true)
                    def sesion2 = new Sesn()
                    sesion2.perfil = perfil
                    def usuarioOriginal = Persona.get(session.usuario.id)
                    sesion2.usuario = usuarioOriginal
                    sesion2.fechaInicio = accs.accsFechaFinal
                    sesion2.fechaFin = null
                    sesion2.save(flush: true)
                    Prpf.findAllByPerfil(perfil).each { pr ->
                        def permUsu = new PermisoUsuario()
                        permUsu.persona = asignado
                        permUsu.permisoTramite = pr.permiso
                        permUsu.asignadoPor = session.usuario
                        permUsu.fechaInicio = accs.accsFechaInicial
                        permUsu.fechaFin = accs.accsFechaFinal
                        permUsu.acceso = accs
                        if (!permUsu.save(flush: true)) {
                            println "error save perm nuevo triangulo " + permUsu.errors
                        } else {
                        }
                    }
                    def alerta = new Alerta()
                    alerta.persona = asignado
                    alerta.accion = ""
                    alerta.controlador = ""
                    alerta.fechaCreacion = new Date()
                    alerta.mensaje = "El usuario ${session.usuario.login} te ha asignado como ${perfil} del ${accs.accsFechaInicial.format('dd-MM-yyyy')} al ${accs.accsFechaFinal.format('dd-MM-yyyy')} con motivo de su ausentismo"
                    alerta.save(flush: true)
                } else {
                    println "wtf no hay perfil " + params
                }
            } else {
                def usu = accs.usuario
                def jefes = usu.departamento.getJefes()
                jefes.each {
                    def alerta = new Alerta()
                    alerta.persona = it
                    alerta.accion = ""
                    alerta.controlador = ""
                    alerta.fechaCreacion = new Date()
                    alerta.mensaje = "El usuario ${usu.login} ha registrado un ausentismo del ${accs.accsFechaInicial.format('dd-MM-yyyy')} al ${accs.accsFechaFinal.format('dd-MM-yyyy')}. Por favor reasigne los tramites de dicho usuario durante las fechas mencionadas anteriormente."
                    alerta.save(flush: true)
                }

            }
            accs.save(flush: true)
            if (session.usuario.id == accs?.usuario?.id) {
                if (accs.accsFechaInicial <= new Date()) {

                    session.flag = 2
                    println "session.flag " + session.flag
                    render "OK_Restricción agregada_logout"
                } else {
                    render "OK_Restricción agregada"
                }
            } else {
                render "OK_Restricción agregada"
            }


        }
    }

    def terminarAcceso_ajax() {
        println "terminarAcceso_ajax: $params"
        def accs = Accs.get(params.id)
        def now = new Date()
        if (accs.accsFechaFinal <= now) {
            render "INFO_La restricción ya ha terminado, no puede terminarla de nuevo."
        } else {
            if (accs.accsFechaInicial <= now && (accs.accsFechaFinal >= now || !accs.accsFechaFinal)) {
                accs.accsFechaFinal = now
                accs.accsObservaciones += "; ausentismo terminado por ${session.usuario}"
                def perm = PermisoUsuario.findAllByAcceso(accs)
                def usu
                perm.each {
                    it.fechaFin = now;
                    it.save(flush: true)
                    if (!usu) {
                        usu = it.persona
                    }
                }
                if (usu) {
                    def sesn = Sesn.findAllByUsuarioAndFechaInicio(usu, accs.accsFechaInicial)
                    println "session " + sesn
                    if (sesn.size() > 0) {
                        sesn.each {
                            it.fechaFin = now
                            println "$it"
                            it.save(flush: true)
                        }

                    }
                }

                if (!accs.save(flush: true)) {
                    render "NO_" + renderErrors(bean: accs)
                } else {
                    render "OK_Terminación de la restricción exitosa"
                }
            } else {
                render "INFO_No puede terminar una restricción que no ha empezado aún. Puede eliminarla."
            }
        }
    }

    def eliminarAcceso_ajax() {
        def accs = Accs.get(params.id)
        def now = new Date()
        if (accs.accsFechaFinal <= now) {
            render "INFO_La restricción ya ha terminado, no puede ser eliminada."
        } else {
            if (accs.accsFechaInicial <= now && (accs.accsFechaFinal >= now || !accs.accsFechaFinal)) {
                render "INFO_No puede eliminar una restricción en curso. Puede terminarla."
            } else {
                try {
                    def sesn
                    def usu
                    PermisoUsuario.findAllByAcceso(accs).each {
                        usu = it.persona
                        it.delete(flush: true)
                    }
                    Sesn.findAllByUsuarioAndFechaFinIsNotNull(usu).each {
                        if (it.fechaFin == accs.accsFechaFinal) {
                            it.fechaFin = now
                            it.save()
                        }
                    }
                    accs.delete(flush: true)
                    render "OK_Restricción eliminada."
                } catch (e) {
                    render "NO_Ha ocurrido un error al eliminar la restricción."
                }
            }
        }
    }

    /**
     * los perfiles activos del usaurio deben tener fecha de inicio y fecha de fin en nulo
     * cada vez que se elimina un perfil del usuario se lo borra de la tabla sesn
     * hay que manejar las fechas para cuando se elimina un perfil de susuario y no el borrar sesion
     * validar tambien con el dominio Sesn para los atributos   getEstaActivo
     * @return
     */
    def savePerfiles_ajax() {
        println "save perfiles: " + params
        def usu = Persona.get(params.id)
        def now = new Date()
//        def perfilesUsu = Sesn.findAllByUsuario(usu).perfil.id*.toString()
        def perfilesUsu = Sesn.findAllByUsuarioAndFechaInicioLessThanAndFechaFinIsNull(usu, now).perfil.id*.toString()

        def arrRemove = perfilesUsu, arrAdd = []
        def errores = ""

        if (params.perfil instanceof java.lang.String) {
            params.perfil = [params.perfil]
        }
//        println "params perfil: " + params.perfil
        println "perfiles usu: " + perfilesUsu

        params.perfil.each { pid ->
            if (perfilesUsu.contains(pid)) {
                //ya tiene este perfil: le quito de la lista de los de eliminar
                arrRemove.remove(pid)
            } else {
                //no tiene este perfil: le pongo en la lista de agregar
                arrAdd.add(pid)
            }
        }

        println "ADD " + arrAdd
        println "REMOVE " + arrRemove

        arrRemove.each { pid ->
            def perf = Prfl.get(pid)
            def sesn = Sesn.findByUsuarioAndPerfil(usu, perf)
            try {
                sesn.fechaFin = new Date()
//                sesn.fechaInicio = null
//                sesn.delete(flush: true)
            } catch (e) {
                errores += "<li>No se puedo remover el perfil ${perf.nombre}</li>"
            }
        }
        arrAdd.each { pid ->
            def perf = Prfl.get(pid)
            def sesn = new Sesn([usuario: usu, perfil: perf, fechaInicio: new Date()])
            if (!sesn.save(flush: true)) {
                errores += "<li>No se puedo remover el perfil ${perf.nombre}</li>"
            }
        }

        if (errores == "") {
            def permisosDebeTener = []
            /* *********** actualiza PRUS  ************ */
            Sesn.findAllByUsuarioAndFechaFinIsNull(usu).each {
                def prpf = Prpf.findAllByPerfil(it.perfil)
                permisosDebeTener += prpf.permiso
            }
            permisosDebeTener = permisosDebeTener.unique()
            println "permisos que debe tener: $permisosDebeTener"

            def permisosTiene = PermisoUsuario.findAllByPersonaAndFechaFinIsNull(usu)
            def permisosAgregar = permisosDebeTener.clone()
            def permisosTerminar = []
            permisosTiene.each { actual ->
                if (!permisosDebeTener.contains(actual.permisoTramite)) {
                    permisosTerminar.add(actual)
                    permisosAgregar.remove(actual.permisoTramite)
                }
                if (permisosDebeTener.contains(actual.permisoTramite) && (actual.fechaFin == null)) {
                    permisosAgregar.remove(actual.permisoTramite)
                }
            }
            println "permisos que debe terminar: $permisosTerminar"
            permisosTerminar.each {
                it.fechaFin = new Date()
                if (!it.save(flush: true)) {
                    println "savePerfiles_ajax:" + it.errors
                    errores += "<li>No se pudo terminar permiso ${it.permisoTramite.descripcion}</li>"
                }
            }

            permisosAgregar.each {
                def prus = new PermisoUsuario([
                        persona       : usu,
                        permisoTramite: it,
                        fechaInicio   : new Date(),
                        asignadoPor   : session.usuario
                ])
                if (!prus.save(flush: true)) {
                    println prus.errors
                    errores += "<li>No se pudo asignar permiso ${prus.permisoTramite.descripcion}</li>"
                }
            }

            if (errores == "") {
                render "OK_Cambios efectuados exitosamente"
            } else {
                render "<ul>" + errores + "</ul>"
            }
        } else {
            render "<ul>" + errores + "</ul>"
        }
    }

    def verRedireccionar_ajax() {
        def persona = Persona.get(params.id)
        return [persona: persona, tramites: params.tramites.toInteger()]
    }

    def verDesactivar_ajax() {
        def persona = Persona.get(params.id)
        return [persona: persona, tramites: params.tramites.toInteger()]
    }

    def list() {
        if (session.usuario.puedeAdmin) {
            params.max = Math.min(params.max ? params.max.toInteger() : 15, 100)
            params.sort = params.sort ?: "apellido"
            params.perfil = params.perfil ?: ''
            params.estado = params.estado ?: ''
            def personaInstanceList = getLista(params, false)
            def personaInstanceCount = getLista(params, true).size()
            if (personaInstanceList.size() == 0 && params.offset && params.max) {
                params.offset = params.offset - params.max
                personaInstanceList = getLista(params, false)
            }
            return [personaInstanceList: personaInstanceList, personaInstanceCount: personaInstanceCount, params: params]
        } else {
            flash.message = "Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será registrada."
            response.sendError(403)
        }
    } //list

    def show_ajax() {
        if (params.id) {
            def personaInstance = Persona.get(params.id)
            if (!personaInstance) {
                notFound_ajax()
                return
            }
            def w = 0, h = 0
            if (personaInstance.foto) {
                def path = servletContext.getRealPath("/") + "images/perfiles/" //web-app/archivos
                try {
                    def img = ImageIO.read(new File(path + personaInstance.foto));
                    w = img.getWidth()
                    h = img.getHeight()
                } catch (e) {
                }
            }

            return [personaInstance: personaInstance, w: w, h: h]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def personaInstance = new Persona(params)
        if (params.id) {
            personaInstance = Persona.get(params.id)
            if (!personaInstance) {
                notFound_ajax()
                return
            }
        }
        return [personaInstance: personaInstance]
    } //form para cargar con ajax en un dialog

    def activar_ajax() {
        def persona = Persona.get(params.id)
        persona.activo = 1
        persona.fechaInicio = new Date()
        persona.fechaFin = null
        if (persona.save(flush: true)) {
            render "OK_Persona activada exitosamente"
        } else {
            render "NO_Ha ocurrido un error: " + renderErrors(bean: persona)
        }
    }

    def redireccionarTramites(params) {
        def persona = Persona.get(params.id)
        def dpto = persona.departamento
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def rolImprimir = RolPersonaTramite.findByCodigo('I005')
        def tramites = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite as p  inner join fetch p.tramite as tramites where p.persona=${params.id} and  p.rolPersonaTramite in (${rolPara.id + "," + rolCopia.id + "," + rolImprimir.id}) and p.fechaEnvio is not null and tramites.estadoTramite in (3,4) order by p.fechaEnvio desc ")
        def errores = "", ok = 0
        tramites.each { pr ->
            if (pr.rolPersonaTramite.codigo == "I005") {
                pr.delete(flush: true)
            } else {
                def obs = "Trámite antes dirigido a " + persona.nombre + " " + persona.apellido + ", ${params.razon}"

                def personaAntes = pr.persona
                def dptoAntes = pr.departamento

                if (params.quien == "-") {
                    pr.persona = null
                    pr.departamento = dpto
                    obs += " al departamento ${dpto.descripcion}"
                } else {
                    pr.persona = Persona.get(params.quien)
                    obs += " al usuario ${pr.persona.login}"
                }
                obs += " el ${new Date().format('dd-MM-yyyy HH:mm')} por ${session.usuario.login}"
                def tramite = pr.tramite
                def alerta = new Alerta()
                alerta.mensaje = "entro a redireccionar tramite deprecated!!!!!!"
                alerta.controlador = "personaController"
                alerta.accion = "redireccionarTramites"
                alerta.save(flush: true)

                println "NO DEBERIA IMPRIMIR ESTO NUNCA"
                tramite.observaciones = tramitesService.modificaObservaciones(tramite.observaciones, obs)
                pr.observaciones = tramitesService.modificaObservaciones(pr.observaciones, obs)
                if (tramite.save(flush: true)) {
                } else {
                    errores += renderErrors(bean: tramite)
                    println tramite.errors
                }
                if (!pr.persona && !pr.departamento) {
                    pr.persona = personaAntes
                    pr.departamento = dptoAntes
                    println "NO DEBERIA IMPRIMIR ESTO NUNCA"
                    pr.observaciones = tramitesService.modificaObservaciones(pr.observaciones, "Ocurrió un error al redireccionar (${new Date().format('dd-MM-yyyy HH:mm')}).")
                    errores += "<ul><li>Ha ocurrido un error al redireccionar.</li></ul>"
                }
                if (pr.save(flush: true)) {
                    ok++
                } else {
                    println pr.errors
                    errores += renderErrors(bean: pr)
                }
            }
        }
        if (errores != "") {
            println "NOPE: " + errores
            return "NO_" + errores
        } else {
            return "OK_Cambio realizado exitosamente"
        }
    }

    def redireccionar_ajax() {
        params.razon = "redireccionado"
        render redireccionarTramites(params)
    }

    def desactivar_ajax() {
        def persona = Persona.get(params.id)
        def dpto = persona.departamento
        persona.activo = 0
        persona.fechaFin = new Date()
        if (persona.save(flush: true)) {
            render "OK_Cambio realizado exitosamente"
        } else {
            render "NO_Ha ocurrido un error al desactivar la persona.<br/>" + renderErrors(bean: persona)
        }
    }

    def formUsuario_ajax() {
        def personaInstance = new Persona(params)
        if (params.id) {
            personaInstance = Persona.get(params.id)
            if (!personaInstance) {
                notFound_ajax()
                return
            }
        }
        return [personaInstance: personaInstance]
    }

    def validarMail_ajax() {
        params.mail = params.mail.toString().trim()
        if (params.id) {
            def prsn = Persona.get(params.id)
            if (prsn.mail == params.mail) {
                render true
                return
            } else {
                render Persona.countByMail(params.mail) == 0
                return
            }
        } else {
            render Persona.countByMail(params.mail) == 0
            return
        }
    }

    def validarLogin_ajax() {
        params.login = params.login.toString().trim()
        if (params.id) {
            def prsn = Persona.get(params.id)
            if (prsn.login.toLowerCase() == params.login.toLowerCase()) {
                render true
                return
            } else {
                render Persona.countByLoginIlike(params.login) == 0
                return
            }
        } else {
            render Persona.countByLoginIlike(params.login) == 0
            return
        }
    }

    def save_ajax() {
        def msgDpto = ""
        if (params.password) {
            if (params.password != 'pandagnaros') {
                params.password = params.password.toString().encodeAsMD5()
            } else {
                params.password = Persona.get(params.id).password
            }
        }

        params.mail = params.mail.toString().toLowerCase()
        def personaInstance = new Persona()
        if (params.id) {
            personaInstance = Persona.get(params.id)
            if (!personaInstance) {
                notFound_ajax()
                return
            }

            if (params.departamento.id.toString() != personaInstance.departamentoId.toString()) {
                def rolPara = RolPersonaTramite.findByCodigo('R001');
                def rolCopia = RolPersonaTramite.findByCodigo('R002');
                def rolImprimir = RolPersonaTramite.findByCodigo('I005')

                def tramites = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite as p inner join fetch p.tramite as tramites " +
                        "where p.persona = ${params.id} and p.rolPersonaTramite in (${rolPara.id + "," + rolCopia.id + "," + rolImprimir.id}) and " +
                        "p.fechaEnvio is not null and tramites.estadoTramite in (3,4) order by p.fechaEnvio desc ")
                def cantTramites = tramites.size()
                println "cambioDpto_ajax: $tramites"

                if (params.departamento.id != personaInstance.departamentoId) {
                    msgDpto = "<i class='fa fa-warning fa-3x pull-left text-warning text-shadow'></i>" +
                            "<h4 class='text-warning text-shadow'>Está cambiando a ${personaInstance.toString()} de departamento," +
                            "de ${WordUtils.capitalizeFully(personaInstance.departamento.descripcion)} a " +
                            "${WordUtils.capitalizeFully(Departamento.get(params.departamento.id.toLong()).descripcion)}</h4>" +
                            "<p style='font-size:larger;'>Se redireccionará${cantTramites == 1 ? '' : 'n'} ${cantTramites} trámite${cantTramites == 1 ? '' : 's'} " +
                            "de su bandeja de entrada personal a la bandeja de entrada de la oficina agregando una observación de " +
                            "notificación de esta acción.</p>" +
                            "<div class='row'>" +
                            "<div class='col-md-12'>" +
                            g.select("data-dpto": params.departamento.id, name: "selWarning", class: 'form-control', optionKey: "key", optionValue: "value",
                                    from: [0: "Cancelar el cambio", 1: "Cambiar y efectuar el redireccionamiento"]) +
                            "</div>" +
                            "</div>"
                }
                params.departamento.id = personaInstance.departamentoId
            }
        } //update
        else {
            params.activo = 0
            params.jefe = 0
        } //create
        personaInstance.properties = params


        personaInstance.departamentoDesde = Departamento.get(params.departamento.id)

        if (!personaInstance.save(flush: true)) {
            println "ERROR"
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} Persona."
            msg += renderErrors(bean: personaInstance)
            render msg
            return
        } else {
            def perfiles = Sesn.countByUsuario(personaInstance)
            if (perfiles == 0) {
                def perfilUsuario = Prfl.findByCodigo("USU")
                def sesion = new Sesn([
                        usuario: personaInstance,
                        perfil : perfilUsuario
                ])
                if (!sesion.save(flush: true)) {
                    println "error asignando el perfil usuario"
                }
            }
            if (msgDpto != "") {
                render "INFO_" + msgDpto
            } else {
                render "OK_${params.id ? 'Actualización' : 'Creación'} de Persona exitosa."
            }
        }
    } //save para grabar desde ajax

    def cambioDpto_ajax() {
        def persona = Persona.get(params.id)
        def dpto = Departamento.get(params.dpto)
        def dptoOld = persona.departamento
        persona.departamento = dpto
        if (persona.save(flush: true)) {
            def rolPara = RolPersonaTramite.findByCodigo('R001');
            def rolCopia = RolPersonaTramite.findByCodigo('R002');
            def rolImprimir = RolPersonaTramite.findByCodigo('I005')

            def tramites = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite as p inner join fetch p.tramite as tramites " +
                    "where p.persona=${params.id} and  p.rolPersonaTramite in (${rolPara.id + "," + rolCopia.id + "," + rolImprimir.id}) and " +
                    "p.fechaEnvio is not null and tramites.estadoTramite in (3,4) order by p.fechaEnvio desc ")
            def errores = "", ok = 0
            /**
             * a cada trámite si el usuario cambia de departamento se cambia PRTR eliminando la persona destinaria
             * y haciendo que aparezca su dpto como destinatario
             * todo: revisar para que el trámite quede tal cual y no cambie el destinatario.. ver si se afecta el arbol
             */
            tramites.each { pr ->
                if (pr.rolPersonaTramite.codigo == "I005") {
                    pr.delete(flush: true)
                } else {
                    pr.persona = null
                    pr.departamento = dptoOld
                    def tramite = pr.tramite
                    def observacionOriginal = pr.observaciones
                    def accion = "Cambio de departamento"
                    def solicitadoPor = ""
                    def usuario = session.usuario.login
                    def texto = "Trámite antes dirigido a " + persona.nombre + " " + persona.apellido
                    def nuevaObservacion = ""
                    pr.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)
                    observacionOriginal = tramite.observaciones
                    tramite.observaciones = tramitesService.observaciones(observacionOriginal, accion, solicitadoPor, usuario, texto, nuevaObservacion)

                    if (tramite.save(flush: true)) {
                    } else {
                        errores += renderErrors(bean: tramite)
                        println tramite.errors
                    }
                    if (pr.save(flush: true)) {
                        ok++
                    } else {
                        println pr.errors
                        errores += renderErrors(bean: pr)
                    }
                }
            }
            if (errores != "") {
                println "NOPE: " + errores
                render "NO_" + errores
            } else {
                render "OK_Cambio realizado exitosamente"
            }
        } else {
            render "NO_Ha ocurrido un error al cambiar el departamento de la persona.<br/>" + renderErrors(bean: persona)
        }
    } //cambio dpto

    /*** se puede boirrar el usaurio siempre y cuando no haya registros en:
     *   trmt.prsn__de: Persona.de
     *   prtr.prsn__id: Persona.persona
     *   accs.usro__id: Persona.usuario y Persona.asignadoPor
     */
    def delete_ajax() {
        def mnsj = ""
        if (params.id) {
            def personaInstance = Persona.get(params.id)
            /** comprueba que se pueda borrar **/
            if (Tramite.findByDe(personaInstance)) {
                mnsj += "La persona tiene trámites creados\n"
            }
            if (PersonaDocumentoTramite.findByPersona(personaInstance)) {
                mnsj += "La persona se halla relacionada a trámites\n"
            }
            if (Accs.findByUsuario(personaInstance)) {
                mnsj += "La persona tiene permisos de ausentismo\n"
            }
            if (Accs.findByAsignadoPor(personaInstance)) {
                mnsj += "La persona ha registrado ausentismo\n"
            }
            if (PermisoUsuario.findByAsignadoPor(personaInstance)) {
                mnsj += "La persona ha realizado Asignación de permisos\n"
            }
            if (PermisoUsuario.findByModificadoPor(personaInstance)) {
                mnsj += "La persona ha realizado Modificación de permisos\n"
            }
            if (personaInstance.esTriangulo) {
                mnsj += "La persona es recepcionista de oficina\n"
            }
            if (personaInstance.puedeAdmin) {
                mnsj += "La persona tiene permios de administración\n"
            }
            if (personaInstance.activo) {
                mnsj += "La persona se halla activa\n"
            }

//            println "prsn:" + personaInstance.id + personaInstance
//            println "mnsj:" + mnsj

            if (!mnsj) {
                def prsn = personaInstance.nombre + " " + personaInstance.apellido
                if (personaInstance) {
                    try {
                        Sesn.findAllByUsuario(personaInstance).each { pr ->
                            pr.delete(flush: true)
                        }

                        PermisoUsuario.findAllByPersona(personaInstance).each { pr ->
                            pr.delete(flush: true)
                        }

                        personaInstance.delete(flush: true)
                        render "OK_${prsn} ha sido eliminada(o) del sistema"
                    } catch (e) {
                        render "NO_" + mnsj
                    }
                } else {
                    notFound_ajax()
                }

            } else {
                render "NO_" + mnsj
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró Persona."
    } //notFound para ajax

    def cargarUsuariosLdap() {
        if (session.usuario.puedeAdmin) {
            def prmt = Parametros.findAll()[0]
//        LDAP ldap = LDAP.newInstance('ldap://192.168.0.60:389', 'cn=AdminSAD SAD,OU=GESTION DE SISTEMAS Y TECNOLOGIAS DE INFORMACION,OU=DIRECCION DE GESTION DE TALENTO HUMANO Y ADMINISTRACION,ou=PREFECTURA,ou=GADPP,dc=pichincha,dc=local', 'SADmaster')
            LDAP ldap = LDAP.newInstance('ldap://' + prmt.ipLDAP, prmt.textoCn, prmt.passAdm)
            println "'ldap://192.168.0.60:389', 'cn=AdminSAD SAD,OU=GESTION DE SISTEMAS Y TECNOLOGIAS DE INFORMACION,OU=DIRECCION DE GESTION DE TALENTO HUMANO Y ADMINISTRACION,ou=PREFECTURA,ou=GADPP,dc=pichincha,dc=local', 'SADmaster'"
            println "LADP: " + "ldap://${prmt.ipLDAP}, ${prmt.textoCn}, ${prmt.passAdm}"
            println "conectado " + ldap.class
            println "!!!!!!!######3&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&######!!!!!"

            def registrados = Persona.list()
            def users = []
            def nuevos = []
            def mod = []
            def results = ldap.search('(objectClass=*)', prmt.ouPrincipal, SearchScope.ONE)
            def band = true
            def cont = 0
            def n1 = Departamento.get(11)
            def sinDep = Departamento.get(20)
            def secuencia = 1
            def noNombre = []
            def noApellido = []
            def noMail = []
            for (entry in results) {
                println "----------------------------"
                def ou = entry["ou"]
                if (ou) {
                    //println "es ou lvl1 " + ou
                    //     println "encode hex "+entry["objectguid"]?.encodeAsHex()
//                    println "bytes "+entry["objectguid"].encodeAsMD5Bytes()
//                    println "decode hex "+entry["objectguid"]?.decodeHex()
                    def dep = Departamento.findByDescripcion(ou)
                    if (!dep) {
                        println "no encontro " + ou
                        println "buscando por uid " + entry["objectguid"]?.encodeAsHex()
                        println "busqueda todos " + Departamento.findAllByObjectguid(entry["objectguid"]?.encodeAsHex())
                        dep = Departamento.findByObjectguid(entry["objectguid"]?.encodeAsHex())
                        println "result " + dep
                        if (!dep) {
                            def sec = new Date().format("ss")
                            dep = new Departamento()
                            dep.descripcion = ou
                            dep.codigo = "NUEVO-" + sec + secuencia++
                            dep.activo = 1
                            dep.padre = n1


                            dep.objectguid = entry["objectguid"]?.encodeAsHex()
                            if (!dep.save(flush: true)) {
                                println "errores dep " + dep.errors
                            }
                        } else {
                            println "update del nombre"
                            dep.descripcion = ou
                            dep.save(flush: true)
                        }

                    } else {
                        println "save del objectuid " + entry["objectguid"]?.encodeAsHex() + " en " + dep + "  " + dep.id
                        dep.objectguid = entry["objectguid"]?.encodeAsHex()
                        if (!dep.save(flush: true)) {
                            println "error en el save del uid " + dep.errors
                        }
                    }

                    def searchString = 'ou=' + ou + "," + prmt.ouPrincipal
                    def res2 = ldap.search('(objectClass=*)', searchString, SearchScope.SUB)
                    for (e2 in res2) {
                        def ou2 = e2["ou"]
                        def gn = e2["givenname"]
                        if (gn) {
                            def logn = e2["samaccountname"]
                            def mail = e2["mail"]
                            //  println "buscando e2 " + logn + "  mail " + mail + "     campo mail  " + entry["mail"]
                            if (!mail || mail == "") {
                                noMail.add(["nombre": logn])
                            }

                            def prsn = Persona.findByLogin(logn)
                            if (!prsn) {
                                def nombres = WordUtils.capitalizeFully(e2["givenname"])
                                def apellido = WordUtils.capitalizeFully(e2["sn"])
                                if (!apellido) {
                                    noApellido.add(["nombre": logn])
                                }
                                if (!nombres) {
                                    noNombre.add(["nombre": logn])
                                }
                                prsn = new Persona()
                                prsn.nombre = nombres
                                prsn.apellido = apellido
                                prsn.mail = mail
                                prsn.login = logn
                                prsn.password = "123".encodeAsMD5()
                                prsn.connect = e2["dn"]
                                def datos = e2["dn"].split(",")
                                def dpto = null
                                if (datos.size() > 1) {
                                    dpto = datos[1].split("=")
                                    dpto = Departamento.findByDescripcion(dpto[1])
                                } else {
                                    dpto = null
                                }

                                if (!dpto) {
                                    dpto = sinDep
                                }
                                prsn.departamento = dpto
//                                println "al crear persona pone dptodsde: ${dpto}"
//                                prsn.departamentoDesde = dpto
                                if (!prsn.save(flush: true)) {
//                                    println "error save prns " + prsn.errors
                                } else {
                                    nuevos.add(prsn)
                                    def sesn = new Sesn()
                                    sesn.perfil = Prfl.findByCodigo("USU")
                                    sesn.usuario = prsn
                                    sesn.fechaInicio = new Date()
                                    sesn.save(flush: true)
                                    /* inserta permisos de usuario */
                                    def prpf = Prpf.findAllByPerfil(sesn.perfil)
                                    prpf.each {
                                        def prus = new PermisoUsuario()
                                        prus.asignadoPor = session.usuario
                                        prus.persona = prsn
                                        prus.fechaInicio = new Date()
                                        prus.permisoTramite = PermisoTramite.get(it.permiso.id)
                                        prus.save(flush: true)
                                    }

                                }
                            } else {
                                //println "encontro"
                                if (prsn.nombre != WordUtils.capitalizeFully(e2["givenname"]) || prsn.apellido != WordUtils.capitalizeFully(e2["sn"]) || prsn.mail != e2["mail"] || prsn.connect != e2["dn"] || prsn.departamento == null) {
                                    //    println "update"
                                    prsn.nombre = WordUtils.capitalizeFully(e2["givenname"])
                                    prsn.apellido = WordUtils.capitalizeFully(e2["sn"])
                                    prsn.mail = mail
                                    if (prsn.connect != e2["dn"]) {
                                        prsn.connect = e2["dn"]
                                        prsn.activo = 0
                                    }
                                    def datos = e2["dn"].split(",")
                                    def dpto = null
                                    // println "datos "+datos
                                    if (datos.size() > 1) {
                                        if (datos) {
                                            dpto = datos[1].split("=")
                                        }
                                        //println "dpto "+dpto
                                        if (dpto.size() > 1) {
                                            dpto = Departamento.findByDescripcion(dpto[1])
                                        }
                                        println "departamento(1)   " + dpto

                                    } else {
                                        dpto = null
                                    }
                                    if (!dpto) {
                                        dpto = sinDep
                                    }
                                    if (prsn.departamento != dpto) {
                                        println "actualiza depatamento(1) con ${dpto}, antes: ${prsn.departamento.id}"
                                        prsn.departamentoDesde = prsn.departamento
                                        prsn.departamento = dpto
                                        prsn.activo = 0
                                    }


                                    if (!prsn.apellido) {
                                        prsn.apellido = "N.A."
                                    }
                                    // println "update " + prsn.apellido
                                    if (!prsn.save(flush: true)) {
//                                        println "error save prns " + prsn.errors
                                    } else {
                                        mod.add(prsn)
                                    }
                                }
                            }
                            users.add(prsn)
                            cont++
                        }
                        if (ou2 && ou2 != "Equipo" && ou2 != "EQUIPOS" && ou2 != "Equipos" && ou2 != "EQUIPO") {
                            dep = Departamento.findByDescripcion(ou2)
                            if (!dep) {

                                println "no encontro ou2 " + ou2
                                println "buscando por uid " + e2["objectguid"]?.encodeAsHex()
                                println "busqueda todos " + Departamento.findAllByObjectguid(e2["objectguid"]?.encodeAsHex())
                                dep = Departamento.findByObjectguid(e2["objectguid"]?.encodeAsHex())
                                println "result " + dep
                                if (!dep) {
                                    def sec = new Date().format("ss")
                                    def datos = e2["dn"].split(",")
                                    def padre = null
                                    if (datos) {
                                        padre = datos[1].split("=")
                                    }
                                    padre = Departamento.findByDescripcion(padre[1])
                                    if (!padre) {
                                        padre = n1
                                    }
                                    dep = new Departamento()
                                    dep.descripcion = ou2
                                    dep.codigo = "NUEVO-" + sec + secuencia++
                                    dep.activo = 1
                                    dep.padre = padre
                                    dep.objectguid = e2["objectguid"]?.encodeAsHex()
                                    if (!dep.save(flush: true)) {
                                        println "errores dep " + dep.errors
                                    }
                                } else {
                                    println "update del nombre"
                                    dep.descripcion = ou2
                                    dep.save(flush: true)
                                }


                            } else {
                                println "actualizando uid " + e2["objectguid"]?.encodeAsHex() + " en " + dep + "  " + dep.id
                                dep.objectguid = e2["objectguid"]?.encodeAsHex()
                                if (!dep.save(flush: true)) {
                                    println "error en el save del uid " + dep.errors
                                }
                                def datos = e2["dn"].split(",")
                                def padre = null
                                if (datos) {
                                    padre = datos[1].split("=")
                                }
                                padre = Departamento.findByDescripcion(padre[1])
                                if (!padre) {
                                    padre = n1
                                }
                                if (dep.padre?.id != padre.id) {
                                    dep.padre = padre
                                    dep.save(flush: true)
                                }
                            }
                        }
                    }

                    //println "*********************************\n"
                }
                if (entry["givenname"]) {


                    def logn = entry["samaccountname"]
                    def mail = entry["mail"]
                    if (!mail || mail == "") {
                        noMail.add(["nombre": logn])
                    }
                    def prsn = Persona.findByLogin(logn)
                    if (!prsn) {
                        // println "no encontro nuevo usuario"
                        def nombres = WordUtils.capitalizeFully(entry["givenname"])
                        def apellido = WordUtils.capitalizeFully(entry["sn"])
                        prsn = new Persona()
                        prsn.nombre = nombres
                        prsn.apellido = apellido
                        prsn.mail = mail
                        prsn.login = logn
                        prsn.password = "123".encodeAsMD5()
                        prsn.connect = entry["dn"]
                        def datos = entry["dn"].split(",")
                        def dpto = null
                        if (datos) {
                            dpto = datos[1].split("=")
                        }
                        dpto = Departamento.findByDescripcion(dpto[1])
                        if (!dpto) {
                            dpto = sinDep
                        }
                        prsn.departamento = dpto
//                        println "al crear persona buscado por login... pone dptodsde: ${dpto}"
//                        prsn.departamentoDesde = dpto
                        if (!prsn.save(flush: true)) {
//                            println "error save prns " + prsn.errors
                        } else {
                            nuevos.add(prsn)
                            users.add(prsn)
                            def sesn = new Sesn()
                            sesn.perfil = Prfl.findByCodigo("USU")
                            sesn.usuario = prsn
                            sesn.fechaInicio = new Date()
                            sesn.save(flush: true)
                        }
                    } else {
                        if (prsn.nombre != WordUtils.capitalizeFully(entry["givenname"]) || prsn.apellido != WordUtils.capitalizeFully(entry["sn"]) || prsn.mail != mail || prsn.connect != entry["dn"] || prsn.departamento == null) {
                            if (entry["sn"] && entry["sn"] != "") {
                                prsn.nombre = WordUtils.capitalizeFully(entry["givenname"])
                                prsn.apellido = WordUtils.capitalizeFully(entry["sn"])
                                if (!prsn.apellido) {
                                    prsn.apellido = "N.A."
                                }
                                prsn.mail = entry["mail"]
                                if (prsn.connect != entry["dn"]) {
                                    prsn.connect = entry["dn"]
                                    prsn.activo = 0
                                }
                                def datos = entry["dn"].split(",")
                                def dpto = null
                                if (datos) {
                                    dpto = datos[1].split("=")
                                }
                                dpto = Departamento.findByDescripcion(dpto[1])
                                if (prsn.departamento != dpto) {
                                    println "actualiza depatamento(2) con ${dpto}, antes: ${prsn.departamento.id}"
                                    prsn.departamentoDesde = prsn.departamento
                                    prsn.departamento = dpto
                                    prsn.activo = 0
                                }
                                if (!prsn.save(flush: true)) {
//                                    println "error save prns update " + prsn.errors
                                } else {
                                    mod.add(prsn)
                                }
                            }

                        }
                    }
                    users.add(prsn)
                    cont++
                }
            }
            return [users: users, reg: registrados, nuevos: nuevos, mod: mod, noNombre: noNombre, noMail: noMail, noApellido: noApellido]

        } else {
            flash.message = "Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será registrada."
            response.sendError(403)
        }

    }

    def cambiarNombresUsuarios() {
        Persona.list().each { p ->
            p.nombre = WordUtils.capitalizeFully(p.nombre)
            p.apellido = WordUtils.capitalizeFully(p.apellido)
            p.save(flush: true)
        }
    }


}
