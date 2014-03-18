package happy.seguridad

import groovy.json.JsonBuilder
import happy.tramites.Departamento
import happy.tramites.PermisoUsuario
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import org.fusesource.jansi.Ansi

import static java.awt.RenderingHints.*
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


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
                                                url: resource(dir: 'images/perfiles/', file: nombre),
                                                size: f.getSize(),
                                                url: pathFile
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
                                                name: nombre,
                                                size: f.getSize(),
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
                                            url: resource(dir: 'images/perfiles/', file: nombre),
                                            size: f.getSize(),
                                            url: pathFile
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
                                        name: fileName + "." + ext,
                                        size: f.getSize(),
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
            return [personaInstance: personaInstance]
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

    def save_ajax() {
        def validarDpto = false
        params.each { k, v ->
            if (v != "date.struct" && v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }

        def personaInstance = new Persona()
        if (params.id) {
            personaInstance = Persona.get(params.id)
            if (!personaInstance) {
                notFound_ajax()
                return
            }
            validarDpto = true
        } //update
        else {
            //llena la parte de usuario si se esta creando la persona
            params.fechaInicio = new Date()
            def p = params.nombre.split(" ")
            params.login = ""
            p.each {
                params.login += it[0]
            }
            p = params.apellido.split(" ")
            params.login += p[0]

            def cantLogin = Persona.countByLogin(params.login)
            if (cantLogin > 0) {
                params.login = params.login + (cantLogin + 1)
            }
            cantLogin = Persona.countByLogin(params.login)
            def i = cantLogin
            while (cantLogin > 0) {
                params.login = params.login + (i + 1)
                cantLogin = Persona.countByLogin(params.login)
                i++
            }

            params.password = params.cedula.toString().encodeAsMD5()
            params.activo = 0
            params.fechaCambioPass = new Date() + 30
            params.jefe = 0
            params.codigo = Departamento.get(params.departamento.id).codigo + "_" + params.login
        } //create
        def msgDpto = ""
        if (validarDpto) {
            if (params.departamento.id != personaInstance.departamentoId) {
                def rolPara = RolPersonaTramite.findByCodigo('R001');
                def rolCopia = RolPersonaTramite.findByCodigo('R002');
                def rolImprimir = RolPersonaTramite.findByCodigo('I005')

                def tramites = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite as p  inner join fetch p.tramite as tramites where p.persona=${session.usuario.id} and  p.rolPersonaTramite in (${rolPara.id + "," + rolCopia.id + "," + rolImprimir.id}) and p.fechaEnvio is not null and tramites.estadoTramite in (3,4) order by p.fechaEnvio desc ")
                def cantTramites = tramites.size()
                msgDpto = "<h3 class='text-warning text-shadow'>Está cambiando a la persona de departamento.</h3>" +
                        "<p>Se redireccionará${cantTramites == 1 ? '' : 'n'} ${cantTramites} trámite${cantTramites == 1 ? '' : 's'} " +
                        "de su bandeja de entrada personal a la bandeja de entrada de la oficina agregando una observacion de" +
                        "notificación de esta acción.</p>" +
                        "<p>Para continuar con el cambio presione el botón 'Continuar'.<br/>" +
                        "Para cancelar el cambio presione el botón 'Cancelar'.<br/>" +
                        "Para ver los trámites que se redireccionarán presione el botón 'Ver trámites'.</p>"
//                render "DPTO_Está cambiando a la persona de departamento."
//                return
                params.departamento.id = personaInstance.departamentoId
            }
        }
        personaInstance.properties = params

        if (!personaInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} Persona."
            msg += renderErrors(bean: personaInstance)
            render msg
            return
        }
        if (msgDpto != "") {
            render "DPTO_" + msgDpto
        } else {
            render "OK_${params.id ? 'Actualización' : 'Creación'} de Persona exitosa."
        }
    } //save para grabar desde ajax

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

}
