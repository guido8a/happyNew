package happy.tramites

import groovy.json.JsonBuilder

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import static java.awt.RenderingHints.KEY_INTERPOLATION
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC


class DocumentoTramiteController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

//    def index() {
//        redirect(action: "list", params: params)
//    } //index
//
//    def getLista(params, all) {
//        params = params.clone()
//        if (all) {
//            params.remove("offset")
//            params.remove("max")
//        }
//        def lista
//        if (params.search) {
//            def c = DocumentoTramite.createCriteria()
//            lista = c.list(params) {
//                or {
//                    ilike("resumen", "%" + params.search + "%")
//                    ilike("clave", "%" + params.search + "%")
//                    ilike("descripcion", "%" + params.search + "%")
//                }
//            }
//        } else {
//            lista = DocumentoTramite.list(params)
//        }
//        return lista
//    }
//
//    def list() {
//        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
//        def documentoTramiteInstanceList = getLista(params, false)
//        def documentoTramiteInstanceCount = getLista(params, true).size()
//        if (documentoTramiteInstanceList.size() == 0 && params.offset && params.max) {
//            params.offset = params.offset - params.max
//        }
//        documentoTramiteInstanceList = getLista(params, false)
//        return [documentoTramiteInstanceList: documentoTramiteInstanceList, documentoTramiteInstanceCount: documentoTramiteInstanceCount, params: params]
//    } //list
//
//    def show_ajax() {
//        if (params.id) {
//            def documentoTramiteInstance = DocumentoTramite.get(params.id)
//            if (!documentoTramiteInstance) {
//                notFound_ajax()
//                return
//            }
//            return [documentoTramiteInstance: documentoTramiteInstance]
//        } else {
//            notFound_ajax()
//        }
//    } //show para cargar con ajax en un dialog
//
//    def form_ajax() {
//        def documentoTramiteInstance = new DocumentoTramite(params)
//        if (params.id) {
//            documentoTramiteInstance = DocumentoTramite.get(params.id)
//            if (!documentoTramiteInstance) {
//                notFound_ajax()
//                return
//            }
//        }
//        return [documentoTramiteInstance: documentoTramiteInstance]
//    } //form para cargar con ajax en un dialog
//
//    def save_ajax() {
//        params.each { k, v ->
//            if (v != "date.struct" && v instanceof java.lang.String) {
//                params[k] = v.toUpperCase()
//            }
//        }
//        def documentoTramiteInstance = new DocumentoTramite()
//        if (params.id) {
//            documentoTramiteInstance = DocumentoTramite.get(params.id)
//            if (!documentoTramiteInstance) {
//                notFound_ajax()
//                return
//            }
//        } //update
//        documentoTramiteInstance.properties = params
//        if (!documentoTramiteInstance.save(flush: true)) {
//            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} DocumentoTramite."
//            msg += renderErrors(bean: documentoTramiteInstance)
//            render msg
//            return
//        }
//        render "OK_${params.id ? 'Actualización' : 'Creación'} de DocumentoTramite exitosa."
//    } //save para grabar desde ajax
//
//    def delete_ajax() {
//        if (params.id) {
//            def documentoTramiteInstance = DocumentoTramite.get(params.id)
//            if (documentoTramiteInstance) {
//                try {
//                    documentoTramiteInstance.delete(flush: true)
//                    render "OK_Eliminación de DocumentoTramite exitosa."
//                } catch (e) {
//                    render "NO_No se pudo eliminar DocumentoTramite."
//                }
//            } else {
//                notFound_ajax()
//            }
//        } else {
//            notFound_ajax()
//        }
//    } //delete para eliminar via ajax
//
//    protected void notFound_ajax() {
//        render "NO_No se encontró DocumentoTramite."
//    } //notFound para ajax

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
    def cargaDocs(){

        def tramite = Tramite.get(params.id)
        println "carga docs "+params+ " "+tramite.anexo
        if (tramite) {
            if (tramite.anexo == 1) {
                def docs = DocumentoTramite.findAllByTramite(tramite)
                println "dosc "+docs
                def editable=false
                if(tramite.estadoTramite.codigo=="E001" || tramite.estadoTramite.codigo=="E002"  )
                    editable=true
                return [tramite: tramite,docs:docs,editable:true]
            }
        }
    }


    def borrarDoc(){
        if(request.getMethod()=="POST"){
            def doc = DocumentoTramite.get(params.id)
            if(doc.tramite.estadoTramite.codigo=="E001" || doc.tramite.estadoTramite.codigo=="E002"  ){
                def band =true
                try{
                    def path = servletContext.getRealPath("/") + "anexos/" + doc.tramite.id+ "/"+doc.path
                    def file = new File(path)
                    file.delete()
                }catch (e){
                    println "error borrar "+e
                    band=false
                }
                if(band){
                    doc.delete(flush: true)
                    render "ok"
                }else{
                    render "error_No se pudo eliminar el archivo."
                }
            }
        }else{
            response.sendError(403)
        }

    }

    def generateKey(){
        if(request.getMethod()=="POST"){
            def doc = DocumentoTramite.get(params.id)
            session.key = doc?.path.size()+doc.resumen?.encodeAsMD5().substring(0,10)
            render "ok"
        }else{
            response.sendError(403)
        }
    }

    def descargarDoc(){
        def doc = DocumentoTramite.get(params.id)
        if(session.key == (doc.path.size()+doc.resumen?.encodeAsMD5().substring(0,10))){
            session.key = null
            def path = servletContext.getRealPath("/") + "anexos/" + doc.tramite.id+ "/"+doc.path
            println "path "+doc.path+" "+doc.path.split("\\.")
            def tipo = doc.path.split("\\.")
            tipo = tipo[1]
            println "tipo "+tipo
            switch (tipo){
                case "jpeg":
                case "gif":
                case "jpg":
                case "bmp":
                case "png":
                    tipo="application/image"
                    break;
                case "pdf":
                    tipo = "application/pdf"
                    break;
                case "doc":
                case "docx":
                case "odt":
                    tipo="application/msword"
                    break;
                case "xls":
                case "xlsx":
                    tipo="application/vnd.ms-excel"
                    break;
                default:
                    tipo ="application/pdf"
                    break;
            }
            def file = new File(path)
            def b = file.getBytes()
            response.setContentType(tipo)
            response.setHeader("Content-disposition", "attachment; filename=" + (doc.path))
            response.setContentLength(b.length)
            response.getOutputStream().write(b)
        }else{
            response.sendError(403)
        }
    }


    def uploadSvt(){
        println "updaload svt "+params
        def tramite = Tramite.get(params.id)
        def path = servletContext.getRealPath("/") + "anexos/" + tramite.id+ "/"    //web-app/archivos
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
//                println "llego hasta mas aca"
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
//                    println "llego hasta mas mas aca"
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
//                println "llego json"
                def json = new JsonBuilder(data)
                    //println json.toPrettyString()
                render json
                return
//                println "return ?"
            } //ok contents
            else {
                println "llego else no se acepta"
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
            }
        } //f && !f.empty

    }

    def uploadFile() {
        println "UPLOAD"
        println params
        println params.file
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
//                    //println json.toPrettyString()
                render json
                return
            } //ok contents
            else {
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
            }
        } //f && !f.empty

        println params
        render "OK"
    }

}
