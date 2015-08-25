package happy.tramites

import groovy.json.JsonBuilder
import happy.seguridad.Persona

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import static java.awt.RenderingHints.KEY_INTERPOLATION
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC


class DocumentoTramiteController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def validarAnexosDEX() {
        def tramite = Tramite.get(params.id)
        if (tramite.tipoDocumento.codigo == "DEX") {
            render DocumentoTramite.countByTramite(tramite) > 0
        } else {
            render true
        }
    }

    def anexo() {
        def tramite = Tramite.get(params.id)
        if (tramite) {
            if (tramite.anexo == 1) {
                return [tramite: tramite]
            } else {
                redirect(controller: 'tramite', action: 'redactar', params: params)
            }
        } else {
            response.sendError(404)
        }
    }

    def verAnexos() {
        def tramite = Tramite.get(params.id)
        if (tramite) {

            return [tramite: tramite]

        } else {
            response.sendError(404)
        }
    }

    def cargaDocs() {
        def tramite = Tramite.get(params.id)
        if (tramite) {
            if (tramite.anexo == 1) {
                def docs = DocumentoTramite.findAllByTramite(tramite)
                def editable = false
                if (tramite.estadoTramite.codigo == "E001" || tramite.estadoTramite.codigo == "E002") {
                    editable = true
                }
                if (tramite.tipoDocumento.codigo == "DEX") {
                    editable = true
                }
                if (params.ver) {
                    editable = false
                }
                return [tramite: tramite, docs: docs, editable: editable]
            }
        }
    }


    def borrarDoc() {

        if (request.getMethod() == "POST") {

            def doc = DocumentoTramite.get(params.id)
            def departamento = doc.tramite.deDepartamento
            if (!departamento) {
                departamento = doc.tramite.de.departamento
            }
            if (doc.tramite.estadoTramite.codigo == "E001" || doc.tramite.estadoTramite.codigo == "E002") {
                def band = true
                try {
                    def path = servletContext.getRealPath("/") + "anexos/${departamento.codigo}/" + doc.tramite.codigo + "/" + doc.path
                    def file = new File(path)
                    file.delete()
                } catch (e) {
                    println "error borrar " + e
                    band = false
                }
                if (band) {
                    doc.delete(flush: true)
                    render "ok"
                } else {
//                    render "error_No se pudo eliminar el archivo."
                    render "no_No se pudo eliminar el archivo."
                }
            }
        } else {
            response.sendError(403)
        }

    }

    def borrarDocNoFile() {
        if (request.getMethod() == "POST") {
            def doc = DocumentoTramite.get(params.id)
            doc.delete(flush: true)
            render "ok"
        } else {
            render "no_No se pudo borrar el archivo anexo"
        }

    }

    def generateKey() {
        if (request.getMethod() == "POST") {
            def doc = DocumentoTramite.get(params.id)
            def departamento = doc.tramite.deDepartamento
            if(!departamento) {
                departamento = doc.tramite.de.departamento
            }
            def anio = doc.fecha.format("yyyy")
            try {
                def path = servletContext.getRealPath("/") + "anexos/${departamento.codigo}/${anio}/" + doc.tramite.codigo + "/" + doc.path
                def file = new File(path)
                def b = file.getBytes()
                session.key = doc?.path.size() + doc.descripcion?.encodeAsMD5()?.substring(0, 10)
                render "ok"
            } catch (e) {
                e.printStackTrace()
                render "error"
            }

        } else {
            response.sendError(403)
        }
    }

    def descargarDoc() {
        def doc = DocumentoTramite.get(params.id)
        def departamento = doc.tramite.deDepartamento
        def anio = doc.fecha.format("yyyy")
        if (!departamento) {
            departamento = doc.tramite.de.departamento
        }
        if (session.key == (doc.path.size() + doc.descripcion?.encodeAsMD5().substring(0, 10))) {
            session.key = null
            def path = servletContext.getRealPath("/") + "anexos/${departamento.codigo}/${anio}/" + doc.tramite.codigo + "/" + doc.path
            def tipo = doc.path.split("\\.")
            tipo = tipo[1]
            switch (tipo) {
                case "jpeg":
                case "gif":
                case "jpg":
                case "bmp":
                case "png":
                    tipo = "application/image"
                    break;
                case "pdf":
                    tipo = "application/pdf"
                    break;
                case "doc":
                case "docx":
                case "odt":
                    tipo = "application/msword"
                    break;
                case "xls":
                case "xlsx":
                    tipo = "application/vnd.ms-excel"
                    break;
                default:
                    tipo = "application/pdf"
                    break;
            }
            try {
                def file = new File(path)
                def b = file.getBytes()
                response.setContentType(tipo)
                response.setHeader("Content-disposition", "attachment; filename=" + (doc.path))
                response.setContentLength(b.length)
                response.getOutputStream().write(b)
            } catch (e) {
                response.sendError(404)
            }
        } else {
            response.sendError(403)
        }
    }


    def uploadSvt() {
        def tramite = Tramite.get(params.id)
        def anio = new Date().format("yyyy")
        def path = servletContext.getRealPath("/") + "anexos/${session.departamento.codigo}/" + anio + "/" + tramite.codigo + "/"
        //web-app/archivos
        new File(path).mkdirs()
        def f = request.getFile('file')  //archivo = name del input type file
        def imageContent = ['image/png': "png", 'image/jpeg': "jpeg", 'image/jpg': "jpg"]
        def okContents = [
                'image/png'                                                                : "png",
                'image/jpeg'                                                               : "jpeg",
                'image/jpg'                                                                : "jpg",

                'application/pdf'                                                          : 'pdf',
                'application/download'                                                     : 'pdf',
                'application/vnd.ms-pdf'                                                   : 'pdf',

                'application/excel'                                                        : 'xls',
                'application/vnd.ms-excel'                                                 : 'xls',
                'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'        : 'xlsx',

                'application/mspowerpoint'                                                 : 'pps',
                'application/vnd.ms-powerpoint'                                            : 'pps',
                'application/powerpoint'                                                   : 'ppt',
                'application/x-mspowerpoint'                                               : 'ppt',
                'application/vnd.openxmlformats-officedocument.presentationml.slideshow'   : 'ppsx',
                'application/vnd.openxmlformats-officedocument.presentationml.presentation': 'pptx',

                'application/msword'                                                       : 'doc',
                'application/vnd.openxmlformats-officedocument.wordprocessingml.document'  : 'docx',

                'application/vnd.oasis.opendocument.text'                                  : 'odt',

                'application/vnd.oasis.opendocument.presentation'                          : 'odp',

                'application/vnd.oasis.opendocument.spreadsheet'                           : 'ods'
        ]

        if (f && !f.empty) {
            def fileName = f.getOriginalFilename() //nombre original del archivo
            def ext

            def parts = fileName.split("\\.")
            fileName = ""
            parts.eachWithIndex { obj, i ->
                if (i < parts.size() - 1) {
                    fileName += obj
                }
            }

            if (okContents.containsKey(f.getContentType())) {
                ext = okContents[f.getContentType()]
                fileName = fileName.size() < 40 ? fileName : fileName[0..39]
                fileName = fileName.tr(/áéíóúñÑÜüÁÉÍÓÚàèìòùÀÈÌÒÙÇç .!¡¿?&#°"'/, "aeiounNUuAEIOUaeiouAEIOUCc_")

                def nombre = fileName + "." + ext
                def pathFile = path + nombre
                def fn = fileName
                def src = new File(pathFile)
                def i = 1
                while (src.exists()) {
                    nombre = fn + "_" + i + "." + ext
                    pathFile = path + nombre
                    src = new File(pathFile)
                    i++
                }
                try {
                    f.transferTo(new File(pathFile)) // guarda el archivo subido al nuevo path
                } catch (e) {
                    println "????????\n" + e + "\n???????????"
                }

                if (imageContent.containsKey(f.getContentType())) {
                    /* RESIZE */
                    def img = ImageIO.read(new File(pathFile))

                    def scale = 0.5

                    def minW = 200
                    def minH = 200

                    def maxW = minW * 4
                    def maxH = minH * 4

                    def w = img.width
                    def h = img.height

                    if (w > maxW || h > maxH) {
                        int newW = w * scale
                        int newH = h * scale
                        int r = 1
                        if (w > h) {
                            r = w / maxW
                            newW = maxW
                            newH = h / r
                        } else {
                            r = h / maxH
                            newH = maxH
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
                    /* fin resize */
                } //si es imagen hace resize para que no exceda 800x800
//                println "llego hasta aca"
                def docTramite = new DocumentoTramite([
                        tramite    : tramite,
                        fecha      : new Date(),
                        resumen    : params.resumen,
                        clave      : params.clave,
                        descripcion: params.descripcion,
                        path       : nombre
                ])
                def data
                if (docTramite.save(flush: true)) {
                    data = [
                            files: [
                                    [
                                            name: nombre,
                                            url : resource(dir: "anexos/${session.departamento.codigo}/" + tramite.codigo, file: nombre),
                                            size: f.getSize(),
                                            url : pathFile
                                    ]
                            ]
                    ]
                } else {
                    println "error al guardar: " + docTramite.errors
                    data = [
                            files: [
                                    [
                                            name : nombre,
                                            size : f.getSize(),
                                            error: "Ha ocurrido un error al guardar: " + renderErrors(bean: docTramite)
                                    ]
                            ]
                    ]
                }
                def json = new JsonBuilder(data)
                render json
                return
            } //ok contents
            else {
                println "llego else no se acepta"
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
            }
        } //f && !f.empty

    }

    def uploadFile() {
        println "UPLOAD: params: $params"
        println "params.file:" + params.file
        def tramite = Tramite.get(params.id)
        def path = servletContext.getRealPath("/") + "anexos/" + tramite.id + "/"    //web-app/archivos
        new File(path).mkdirs()

        def f = request.getFile('file')  //archivo = name del input type file

        def imageContent = ['image/png': "png", 'image/jpeg': "jpeg", 'image/jpg': "jpg"]

        def okContents = [
                'image/png'                                                                : "png",
                'image/jpeg'                                                               : "jpeg",
                'image/jpg'                                                                : "jpg",

                'application/pdf'                                                          : 'pdf',

                'application/excel'                                                        : 'xls',
                'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'        : 'xlsx',

                'application/mspowerpoint'                                                 : 'pps',
                'application/vnd.ms-powerpoint'                                            : 'pps',
                'application/powerpoint'                                                   : 'ppt',
                'application/x-mspowerpoint'                                               : 'ppt',
                'application/vnd.openxmlformats-officedocument.presentationml.slideshow'   : 'ppsx',
                'application/vnd.openxmlformats-officedocument.presentationml.presentation': 'pptx',

                'application/msword'                                                       : 'doc',
                'application/vnd.openxmlformats-officedocument.wordprocessingml.document'  : 'docx',

                'application/vnd.oasis.opendocument.text'                                  : 'odt',

                'application/vnd.oasis.opendocument.presentation'                          : 'odp',

                'application/vnd.oasis.opendocument.spreadsheet'                           : 'ods'
        ]

        if (f && !f.empty) {
            def fileName = f.getOriginalFilename() //nombre original del archivo
            def ext

            def parts = fileName.split("\\.")
            fileName = ""
            parts.eachWithIndex { obj, i ->
                if (i < parts.size() - 1) {
                    fileName += obj
                }
            }

            if (okContents.containsKey(f.getContentType())) {
                ext = okContents[f.getContentType()]
                fileName = fileName.size() < 40 ? fileName : fileName[0..39]
                fileName = fileName.tr(/áéíóúñÑÜüÁÉÍÓÚàèìòùÀÈÌÒÙÇç .!¡¿?&#°"'/, "aeiounNUuAEIOUaeiouAEIOUCc_")

                def nombre = fileName + "." + ext
                def pathFile = path + nombre
                def fn = fileName
                def src = new File(pathFile)
                def i = 1
                while (src.exists()) {
                    nombre = fn + "_" + i + "." + ext
                    pathFile = path + nombre
                    src = new File(pathFile)
                    i++
                }
                try {
                    f.transferTo(new File(pathFile)) // guarda el archivo subido al nuevo path
                    //println pathFile
                } catch (e) {
                    println "????????\n" + e + "\n???????????"
                }

                if (imageContent.containsKey(f.getContentType())) {
                    /* RESIZE */
                    def img = ImageIO.read(new File(pathFile))

                    def scale = 0.5

                    def minW = 200
                    def minH = 200

                    def maxW = minW * 4
                    def maxH = minH * 4

                    def w = img.width
                    def h = img.height

                    if (w > maxW || h > maxH) {
                        int newW = w * scale
                        int newH = h * scale
                        int r = 1
                        if (w > h) {
                            r = w / maxW
                            newW = maxW
                            newH = h / r
                        } else {
                            r = h / maxH
                            newH = maxH
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
                    /* fin resize */
                } //si es imagen hace resize para que no exceda 800x800

                def docTramite = new DocumentoTramite([
                        tramite    : tramite,
                        fecha      : new Date(),
                        resumen    : params.resumen,
                        clave      : params.clave,
                        descripcion: params.descripcion,
                        path       : nombre
                ])
                def data
                if (docTramite.save(flush: true)) {
                    data = [
                            files: [
                                    [
                                            name: nombre,
                                            url : resource(dir: 'anexos/' + tramite.id, file: nombre),
                                            size: f.getSize(),
                                            url : pathFile
                                    ]
                            ]
                    ]
                } else {
                    println "error al guardar: " + docTramite.errors
                    data = [
                            files: [
                                    [
                                            name : nombre,
                                            size : f.getSize(),
                                            error: "Ha ocurrido un error al guardar: " + renderErrors(bean: docTramite)
                                    ]
                            ]
                    ]
                }
                def json = new JsonBuilder(data)
                render json
                return
            } //ok contents
            else {
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
            }
        } //f && !f.empty

        println params
        render "OK"
    }

    def cargaTramites() {
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def tramites = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite as p  inner join fetch p.tramite as tramites where p.persona=${session.usuario.id} and  p.rolPersonaTramite in (${rolPara.id + "," + rolCopia.id/* + "," + rolImprimir.id*/}) and p.fechaEnvio is not null and tramites.estadoTramite in (3,4) order by p.fechaEnvio desc ")
        return [tramites: tramites]
    }

    def adjuntarTramites() {
        println "adj tramite " + params
        def tramite = Tramite.get(params.tramite)
        def parts = params.ids.split(";")
        println "parts " + parts
        parts.each {
            if (it && it != "") {
                def doc = new DocumentoTramite()
                doc.tramite = tramite
                doc.anexo = Tramite.get(it)
                doc.fecha = new Date()
                if (!doc.save(flush: true)) {
                    println "error save " + doc.errors
                }
            }
        }
        render "ok"
        return
    }


}
