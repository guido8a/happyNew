package happy.seguridad

import groovy.json.JsonBuilder
import happy.tramites.Departamento
import happy.tramites.PermisoUsuario
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import org.apache.commons.lang.WordUtils
import org.fusesource.jansi.Ansi
import static java.awt.RenderingHints.*
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import org.apache.directory.groovyldap.LDAP
import org.apache.directory.groovyldap.SearchScope


class PersonaController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def getLista(params, all) {
        params = params.clone()
        if (all) {
            params.remove("offset")
            params.remove("max")
        }
        def lista
        if (params.search) {
            def c = Persona.createCriteria()
            lista = c.list(params) {
                or {
                    ilike("cedula", "%" + params.search + "%")
                    ilike("nombre", "%" + params.search + "%")
                    ilike("apellido", "%" + params.search + "%")
                    ilike("cargo", "%" + params.search + "%")
                    ilike("login", "%" + params.search + "%")
                    ilike("codigo", "%" + params.search + "%")
                    departamento {
                        or {
                            ilike("descripcion", "%" + params.search + "%")
                        }
                    }
                }
            }
        } else {
            lista = Persona.list(params)
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

//            def parts = fileName.split("\\.")
//            fileName = ""
//            parts.eachWithIndex { obj, i ->
//                if (i < parts.size() - 1) {
//                    fileName += obj
//                } else {
//                    ext = obj
//                }
//            }

//            if (extOk.contains(ext)) {
            if (okContents.containsKey(f.getContentType())) {
//                //println "filename: " + fileName
//                //println "ext: " + ext
//                //println f.getContentType()
//                fileName = fileName.tr(/áéíóúñÑÜüÁÉÍÓÚàèìòùÀÈÌÒÙÇç .!¡¿?&#°"'/, "aeiounNUuAEIOUaeiouAEIOUCc_")
//
//                def fn = fileName
                ext = okContents[f.getContentType()]
                fileName = usuario.id + "." + ext

                def pathFile = path + fileName
//                def src = new File(pathFile)
                def nombre = fileName
//
//                def i = 1
//                while (src.exists()) {
//                    nombre = fn + "_" + i + "." + ext
//                    pathFile = path + nombre
//                    src = new File(pathFile)
//                    i++
//                }

                try {
                    f.transferTo(new File(pathFile)) // guarda el archivo subido al nuevo path
                    //println pathFile
                } catch (e) {
                    println "????????\n" + e + "\n???????????"
                }
                /* RESIZE */
                def img = ImageIO.read(new File(pathFile))

                def scale = 0.5

                def minW = 200
                def minH = 300

                def maxW = minW * 3
                def maxH = minH * 3

                def w = img.width
                def h = img.height

                if (w > maxW || h > maxH || w < minW || h < minH) {
                    int newW = w * scale
                    int newH = h * scale
                    int r = 1
                    if (w > h) {
                        if (w > maxW) {
                            r = w / maxW
                            newW = maxW
                        }
                        if (w < minW) {
                            r = minW / w
                            newW = minW
                        }
                        newH = h / r
                    } else {
                        if (h > maxH) {
                            r = h / maxH
                            newH = maxH
                        }
                        if (h < minH) {
                            r = minH / h
                            newH = minH
                        }
                        newW = w / r
                    }

                    new BufferedImage(newW, newH, img.type).with { j ->
                        createGraphics().with {
                            setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC)
                            drawImage(img, 0, 0, newW, newH, null)
                            dispose()
                        }
                        ImageIO.write(j, ext, new File(pathFile))
                    }
                }
                //println ">" + pathFile
                //println ">>" + new File(pathFile).exists()

                /* fin resize */

                if (!usuario.foto || usuario.foto != nombre) {
                    usuario.foto = nombre
                    if (usuario.save(flush: true)) {
                        //println "OK"
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
//                    //println json.toPrettyString()
                        render json
                        return
                    } else {
                        //println "NOPE: " + usuario.errors
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
//                    //println json.toPrettyString()
                        render json
                        return
                    }
                } else {
                    //println "()()()()"
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
//                    //println json.toPrettyString()
                    render json
                    return
                }
            } else {
//                render "NO_No se acepta esa extensión"

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
//                //println json.toPrettyString()
                render json
                return

                /*
                {"files": [
                  {
                    "name": "picture1.jpg",
                    "size": 902604,
                    "url": "http:\/\/example.org\/files\/picture1.jpg",
                    "thumbnailUrl": "http:\/\/example.org\/files\/thumbnail\/picture1.jpg",
                    "deleteUrl": "http:\/\/example.org\/files\/picture1.jpg",
                    "deleteType": "DELETE"
                  },
                  {
                    "name": "picture2.jpg",
                    "size": 841946,
                    "url": "http:\/\/example.org\/files\/picture2.jpg",
                    "thumbnailUrl": "http:\/\/example.org\/files\/thumbnail\/picture2.jpg",
                    "deleteUrl": "http:\/\/example.org\/files\/picture2.jpg",
                    "deleteType": "DELETE"
                  }
                ]}
                {"files": [
                  {
                    "name": "picture1.jpg",
                    "size": 902604,
                    "error": "Filetype not allowed"
                  },
                  {
                    "name": "picture2.jpg",
                    "size": 841946,
                    "error": "Filetype not allowed"
                  }
                ]}
                 */

//                def json = new JsonBuilder(data)
//                //println json.toPrettyString()

            }

        }

        render "OK"
    }

    def resizeCropImage() {
        //println params
        def usuario = Persona.get(session.usuario.id)
        def path = servletContext.getRealPath("/") + "images/perfiles/"    //web-app/archivos
        def fileName = usuario.foto
        def ext = fileName.split("\\.").last()
        def pathFile = path + fileName
        /* RESIZE */
        def img = ImageIO.read(new File(pathFile))

        def oldW = img.getWidth()
        def oldH = img.getHeight()

        int newW = 200
        int newH = 300
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
        return [usuario: usuario, params: params]
    }

    def loadFoto() {
        def usuario = Persona.get(session.usuario.id)
        def path = servletContext.getRealPath("/") + "images/perfiles/" //web-app/archivos
        def img = ImageIO.read(new File(path + usuario.foto));
        return [usuario: usuario, w: img.getWidth(), h: img.getHeight()]
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

    def config() {
        def usu = Persona.get(params.id)
        def perfilesUsu = Sesn.findAllByUsuario(usu).perfil.id
        def permisosUsu = PermisoUsuario.findAllByPersona(usu).permisoTramite.id
        return [usuario: usu, perfilesUsu: perfilesUsu, permisosUsu: permisosUsu]
    }

    def savePermisos_ajax() {
        //println params
        params.asignadoPor = session.usuario
        def perm = new PermisoUsuario(params)
        //println perm
        if (!perm.save(flush: true)) {
            //println "error accesos: " + perm.errors
            render "NO_" + g.renderErrors(bean: perm)
        } else {
            //println "OK"
            render "OK_Permiso agregado"
        }
        //println perm.errors
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
        params.asignadoPor = session.usuario
        def accs = new Accs(params)
        if (!accs.save(flush: true)) {
            //println "error accesos: " + accs.errors
            render "NO_" + g.renderErrors(bean: accs)
        } else {
            render "OK_Restricción agregada"
        }
    }

    def terminarAcceso_ajax() {
        def accs = Accs.get(params.id)
        def now = new Date().clearTime()
        if (accs.accsFechaFinal <= now) {
            render "INFO_La restricción ya ha terminado, no puede terminarla de nuevo."
        } else {
            if (accs.accsFechaInicial <= now && (accs.accsFechaFinal >= now || !accs.accsFechaFinal)) {
                accs.accsFechaFinal = now
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
                    accs.delete(flush: true)
                    render "OK_Restricción eliminada."
                } catch (e) {
                    render "NO_Ha ocurrido un error al eliminar la restricción."
                }
            }
        }
    }

    def savePerfiles_ajax() {
        def usu = Persona.get(params.id)
        def perfilesUsu = Sesn.findAllByUsuario(usu).perfil.id*.toString()
//        //println "**************"
//        //println Sesn.findAllByUsuario(usu)
//        //println Sesn.findAllByUsuario(usu).id
//        //println Sesn.findAllByUsuario(usu).id*.toString()
//        //println "**************"
        def arrRemove = perfilesUsu, arrAdd = []
        def errores = ""

        if (params.perfil instanceof java.lang.String) {
            params.perfil = [params.perfil]
        }

        params.perfil.each { pid ->
            if (perfilesUsu.contains(pid)) {
                //ya tiene este perfil: le quito de la lista de los de eliminar
                arrRemove.remove(pid)
            } else {
                //no tiene este perfil: le pongo en la lista de agregar
                arrAdd.add(pid)
            }
        }
//        //println "params: " + params
//        //println "perfilesUsu: " + perfilesUsu
//        //println "add: " + arrAdd
//        //println "remove: " + arrRemove
        arrRemove.each { pid ->
            def perf = Prfl.get(pid)
            def sesn = Sesn.findByUsuarioAndPerfil(usu, perf)
            try {
                sesn.delete(flush: true)
            } catch (e) {
                //println "erorr al eliminar perfil: " + e
                errores += "<li>No se puedo remover el perfil ${perf.nombre}</li>"
            }
        }
        arrAdd.each { pid ->
            def perf = Prfl.get(pid)
            def sesn = new Sesn([usuario: usu, perfil: perf])
            if (!sesn.save(flush: true)) {
                //println "error al asignar perfil: " + sesn.errors
                errores += "<li>No se puedo remover el perfil ${perf.nombre}</li>"
            }
        }

        if (errores == "") {
            render "OK_Cambios efectuados exitosamente"
        } else {
            render "<ul>" + errores + "</ul>"
        }
    }

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 15, 100)
        params.sort = params.sort ?: "apellido"
        def personaInstanceList = getLista(params, false)
        def personaInstanceCount = getLista(params, true).size()
        if (personaInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        personaInstanceList = getLista(params, false)
        return [personaInstanceList: personaInstanceList, personaInstanceCount: personaInstanceCount, params: params]
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
                def img = ImageIO.read(new File(path + personaInstance.foto));
                w = img.getWidth()
                h = img.getHeight()
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
            "NO_Ha ocurrido un error: " + renderErrors(bean: persona)
        }
    }

    def desactivar_ajax() {
//        println "cambio dpto"
        def persona = Persona.get(params.id)
        def dpto = persona.departamento
        persona.activo = 0
        persona.fechaFin = new Date()
        if (persona.save(flush: true)) {
//            println "Persona.dpto save ok"
            def rolPara = RolPersonaTramite.findByCodigo('R001');
            def rolCopia = RolPersonaTramite.findByCodigo('R002');
            def rolImprimir = RolPersonaTramite.findByCodigo('I005')

            def tramites = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite as p  inner join fetch p.tramite as tramites where p.persona=${params.id} and  p.rolPersonaTramite in (${rolPara.id + "," + rolCopia.id + "," + rolImprimir.id}) and p.fechaEnvio is not null and tramites.estadoTramite in (3,4) order by p.fechaEnvio desc ")
            def errores = "", ok = 0
            tramites.each { pr ->
                if (pr.rolPersonaTramite.codigo == "I005") {
                    pr.delete(flush: true)
                } else {
                    pr.persona = null
                    pr.departamento = dpto
                    def tramite = pr.tramite
                    tramite.observaciones = (tramite.observaciones ?: "") + "Trámite antes dirigido a " + persona.nombre + " " + persona.apellido
                    if (tramite.save(flush: true)) {
//                        println "tr.save ok"
                    } else {
                        errores += renderErrors(bean: tramite)
                        println tramite.errors
                    }
                    if (pr.save(flush: true)) {
//                        println "pr save ok"
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
//                println "OK"
                render "OK_Cambio realizado exitosamente"
            }
        } else {
            render "NO_Ha ocurrido un error al cambiar el departamento de la persona.<br/>" + renderErrors(bean: persona)
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

    def validarCedula_ajax() {
        params.cedula = params.cedula.toString().trim()
        if (params.id) {
            def prsn = Persona.get(params.id)
            if (prsn.cedula == params.cedula) {
                render true
                return
            } else {
                render Persona.countByCedula(params.cedula) == 0
                return
            }
        } else {
            render Persona.countByCedula(params.cedula) == 0
            return
        }
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
//        params.each { k, v ->
//            if (v != "date.struct" && v instanceof java.lang.String) {
//                params[k] = v.toUpperCase()
//            }
//        }
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

                def tramites = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite as p  inner join fetch p.tramite as tramites where p.persona=${params.id} and  p.rolPersonaTramite in (${rolPara.id + "," + rolCopia.id + "," + rolImprimir.id}) and p.fechaEnvio is not null and tramites.estadoTramite in (3,4) order by p.fechaEnvio desc ")
                def cantTramites = tramites.size()
                if (params.departamento.id != personaInstance.departamentoId)
                    msgDpto = "<h4 class='text-warning text-shadow'>Está cambiando a ${personaInstance.toString()} de departamento," +
                            "de ${WordUtils.capitalizeFully(personaInstance.departamento.descripcion)} a " +
                            "${WordUtils.capitalizeFully(Departamento.get(params.departamento.id.toLong()).descripcion)}</h4>" +
                            "<p style='font-size:larger;'>Se redireccionará${cantTramites == 1 ? '' : 'n'} ${cantTramites} trámite${cantTramites == 1 ? '' : 's'} " +
                            "de su bandeja de entrada personal a la bandeja de entrada de la oficina agregando una observación de " +
                            "notificación de esta acción.</p>" +
                            g.select("data-dpto": params.departamento.id, name: "selWarning", class: 'form-control', optionKey: "key", optionValue: "value",
                                    from: [0: "Cancelar el cambio", 1: "Cambiar y efectuar el redireccionamiento"])
                params.departamento.id = personaInstance.departamentoId
            }
        } //update
        else {
            //llena la parte de usuario si se esta creando la persona
//            params.fechaInicio = new Date()
//            def p = params.nombre.split(" ")
//            params.login = ""
//            p.each {
//                params.login += it[0]
//            }
//            p = params.apellido.split(" ")
//            params.login += p[0]
//
//            def cantLogin = Persona.countByLogin(params.login)
//            if (cantLogin > 0) {
//                params.login = params.login + (cantLogin + 1)
//            }
//            cantLogin = Persona.countByLogin(params.login)
//            def i = cantLogin
//            while (cantLogin > 0) {
//                params.login = params.login + (i + 1)
//                cantLogin = Persona.countByLogin(params.login)
//                i++
//            }

            params.password = params.cedula.toString().encodeAsMD5()
            params.activo = 0
//            params.fechaCambioPass = new Date() + 30
            params.jefe = 0
            params.codigo = Departamento.get(params.departamento.id).codigo + "_" + params.login
        } //create
        personaInstance.properties = params

//        println "AAAAAAAAA"

        if (!personaInstance.save(flush: true)) {
            println "ERROR"
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} Persona."
            msg += renderErrors(bean: personaInstance)
            render msg
            return
        } else {
//            println "******************"
//            println personaInstance
//            println personaInstance.id
//            println "******************"
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
//        println "cambio dpto"
        def persona = Persona.get(params.id)
        def dpto = Departamento.get(params.dpto)
        def dptoOld = persona.departamento
        persona.departamento = dpto
        if (persona.save(flush: true)) {
//            println "Persona.dpto save ok"
            def rolPara = RolPersonaTramite.findByCodigo('R001');
            def rolCopia = RolPersonaTramite.findByCodigo('R002');
            def rolImprimir = RolPersonaTramite.findByCodigo('I005')

            def tramites = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite as p  inner join fetch p.tramite as tramites where p.persona=${params.id} and  p.rolPersonaTramite in (${rolPara.id + "," + rolCopia.id + "," + rolImprimir.id}) and p.fechaEnvio is not null and tramites.estadoTramite in (3,4) order by p.fechaEnvio desc ")
            def errores = "", ok = 0
            tramites.each { pr ->
                if (pr.rolPersonaTramite.codigo == "I005") {
                    pr.delete(flush: true)
                } else {
                    pr.persona = null
                    pr.departamento = dptoOld
                    def tramite = pr.tramite
                    tramite.observaciones = (tramite.observaciones ?: "") + "Trámite antes dirigido a " + persona.nombre + " " + persona.apellido
                    if (tramite.save(flush: true)) {
//                        println "tr.save ok"
                    } else {
                        errores += renderErrors(bean: tramite)
                        println tramite.errors
                    }
                    if (pr.save(flush: true)) {
//                        println "pr save ok"
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
//                println "OK"
                render "OK_Cambio realizado exitosamente"
            }
        } else {
            render "NO_Ha ocurrido un error al cambiar el departamento de la persona.<br/>" + renderErrors(bean: persona)
        }
    } //cambio dpto

    def delete_ajax() {
        if (params.id) {
            def personaInstance = Persona.get(params.id)
            if (personaInstance) {
                try {
                    personaInstance.delete(flush: true)
                    render "OK_Eliminación de Persona exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar Persona."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró Persona."
    } //notFound para ajax

    def cargarUsuariosLdap() {
//        def realPath = servletContext.getRealPath("/")
//        def pathImages = realPath + "images/"
//        def file = new File(pathImages+"/users")


        LDAP ldap = LDAP.newInstance('ldap://192.168.0.60:389', 'cn=AdminSAD SAD,ou=GSTI,ou=GADPP,dc=pichincha,dc=local', 'SADmaster')
        println "conectado " + ldap.class
        println "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"
//        def results = ldap.search('(objectClass=*)', 'ou=GADPP,dc=pichincha,dc=local', SearchScope.SUB)
//        def users = []
//        def registrados = Persona.list()
//        def encontrados = []
//        println "${results.size} entradas halladas para el nivel UNO desde la base:"
//        for (entry in results) {
//            if (entry && entry.displayname && !entry.objectclass.contains("computer")) {
//                def logn = entry["samaccountname"]
//                println "buscando " + logn
//                def prsn = Persona.findByLogin(logn)
//                if (!prsn) {
//                    println "no encontro nuevo usuario"
//                    def parts = entry["cn"].split("&")
////                    def nombres = parts[0].split(" ")
//                    def nombres = WordUtils.capitalizeFully(entry["givenname"])
//                    //println nombres+" " + entry["givenname"]
//                    def mail = entry["mail"]
//                    //def nombre = nombres[0]+" "+nombres[1]
//                    def apellido = WordUtils.capitalizeFully(entry["sn"])
////                    if(nombres.size()==3)
////                        apellido = nombres[2]
////                    if(nombres.size()==4)
////                        apellido = nombres[2]+" "+nombres[3]
////                    if(nombres.size()==5)
////                        apellido = nombres[2]+" "+nombres[3]+" "+nombres[4]
//                    if (!apellido)
//                        apellido = "sin apellido"
//
//                    prsn = new Persona()
//                    prsn.nombre = nombres
//                    prsn.apellido = apellido
//                    prsn.mail = mail
//                    prsn.login = logn
//                    prsn.password = "123".encodeAsMD5()
//                    prsn.departamento = Departamento.get(20)
//                    if (!prsn.save(flush: true)) {
//                        println "error save prns " + prsn.errors
//                    } else {
//                        users.add(prsn)
//                        def sesn = new Sesn()
//                        sesn.perfil = Prfl.findByCodigo("USU")
//                        sesn.usuario = prsn
//                        sesn.save(flush: true)
//                    }
//                } else {
//                    println "encontro"
//                    encontrados.add(prsn)
//                }
//            }
//        }
//        println "encontrados " + encontrados
//        def noReg = registrados - encontrados
//        return [users: users, noReg: noReg]


        def results = ldap.search('(objectClass=*)', 'ou=GADPP,dc=pichincha,dc=local', SearchScope.ONE )
        def cont =0
        for (entry in results) {
            println "__==> "+entry["ou"]
            println "----------------------------"
            def ou = entry["ou"]
            if(ou){
                println "*********************************\n"
                def searchString =  'ou='+ou+',ou=GADPP,dc=pichincha,dc=local'
                println "search String "+searchString
                def res2 = ldap.search('(objectClass=*)',searchString, SearchScope.SUB )
                for(e2 in res2){
                    println "E2--> "+e2["ou"]+"  -  "+e2["givenname"]
                    def ou2 = e2["ou"]
                    def gn = e2["givenname"]
                    if(gn){
                        cont++
                    }
                }

                println "*********************************\n"
            }
            else{
                println "E1 "+entry["givenname"]
                cont++
            }

            println "-------------------------- \n"
        }
        println "--------------------"

        println "hay "+cont+ " usuarios"



    }

    def cambiarNombresUsuarios() {
        Persona.list().each { p ->
            p.nombre = WordUtils.capitalizeFully(p.nombre)
            p.apellido = WordUtils.capitalizeFully(p.apellido)
            p.save(flush: true)
        }
    }


}
