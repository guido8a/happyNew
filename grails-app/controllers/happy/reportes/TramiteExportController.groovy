package happy.reportes

import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.HeaderFooter
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.Phrase
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import happy.seguridad.Persona
import happy.tramites.Departamento
import happy.tramites.Tramite
import org.xhtmlrenderer.extend.FontResolver

//import com.lowagie.text.Document
//import com.lowagie.text.Element
//import com.lowagie.text.Font
//import com.lowagie.text.Paragraph
//import com.lowagie.text.pdf.PdfWriter
//import com.lowagie.text.DocumentException;
//import happy.tramites.PersonaDocumentoTramite
//import happy.tramites.RolPersonaTramite
//import happy.tramites.Tramite

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import java.awt.Color
import java.io.*;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.w3c.dom.Document;

class TramiteExportController {

//    def reportesPdfService

    def crearPdf() {
        println "crear pdf"
        println params

        def tramite = Tramite.get(params.id.toLong())
        def usuario = Persona.get(session.usuario.id)

        if (params.editorTramite) {
            tramite.texto = params.editorTramite
            tramite.asunto = params.asunto
            tramite.fechaModificacion = new Date()
            if (tramite.save(flush: true)) {
                def para = tramite.para
                if(params.para) {
                    if (params.para.toLong() > 0) {
                        para.persona = Persona.get(params.para.toLong())
                    } else {
                        para.departamento = Departamento.get(params.para.toLong() * -1)
                    }
                    if (para.save(flush: true)) {
//                println "OK_Trámite guardado exitosamente"
                    } else {
                        println "NO_Ha ocurrido un error al guardar el destinatario: " + renderErrors(bean: para)
                    }
                }
            } else {
                println "NO_Ha ocurrido un error al guardar el trámite: " + renderErrors(bean: tramite)
            }
        }

        tramite.refresh()

        def realPath = servletContext.getRealPath("/")
        def pathImages = realPath + "images/"
        def path = pathImages + "redactar/" + usuario.id + "/"

        new File(path).mkdirs()

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        FontResolver resolver = renderer.getFontResolver();

        renderer.getFontResolver().addFont(realPath + "fontsPdf/comic.ttf", true);
        resolver.addFont(realPath + "fontsPdf/OpenSans-Bold.ttf", true);
        resolver.addFont(realPath + "fontsPdf/OpenSans-BoldItalic.ttf", true);
        resolver.addFont(realPath + "fontsPdf/OpenSans-ExtraBold.ttf", true);
        resolver.addFont(realPath + "fontsPdf/OpenSans-ExtraBoldItalic.ttf", true);
        resolver.addFont(realPath + "fontsPdf/OpenSans-Italic.ttf", true);
        resolver.addFont(realPath + "fontsPdf/OpenSans-Light.ttf", true);
        resolver.addFont(realPath + "fontsPdf/OpenSans-LightItalic.ttf", true);
        resolver.addFont(realPath + "fontsPdf/OpenSans-Regular.ttf", true);
        resolver.addFont(realPath + "fontsPdf/OpenSans-Semibold.ttf", true);
        resolver.addFont(realPath + "fontsPdf/OpenSans-SemiboldItalic.ttf", true);

        def text = tramite.texto
//        text = util.clean(str: text)
        text=text.decodeHTML()

//        println "html:" + tramite.texto.decodeHTML()
//        println "\n\n" + text

        text = text.replaceAll(~"\\?\\_debugResources=y\\&n=[0-9]*", "")
        text = text.replaceAll(message(code: 'pathImages'), pathImages)

        def content = "<!DOCTYPE HTML>\n<html>\n"
        content += "<head>\n"
        content += "<link href=\"${realPath + 'font/open/stylesheet.css'}\" rel=\"stylesheet\"/>"
        content += "<style language='text/css'>\n"
        content += "  @page {\n" +
                "            size   : 21cm 29.7cm;  /*width height */\n" +
                "            margin : 4.5cm 2.5cm 2.5cm 3cm;\n" +
                "        }\n" +
                ".hoja {\n" +
//                "            background  : #123456;\n" +
//                "            width       : 15.5cm; /*21-2.5-3*/\n" +
                "            font-family : arial;\n" +
                "            font-size   : 12pt;\n" +
                "        }\n" +
                ".titulo-horizontal {\n" +
                "    padding-bottom : 15px;\n" +
                "    border-bottom  : 1px solid #000000;\n" +
                "    text-align     : center;\n" +
                "    width          : 105%;\n" +
                "}\n" +
                ".titulo-azul {\n" +
//                "    color       : #0088CC;\n" +
//                "    border      : 0px solid red;\n" +
                "    white-space : nowrap;\n" +
                "    display     : block;\n" +
                "    width       : 98%;\n" +
                "    height      : 30px;\n" +
                "    font-family : 'open sans condensed';\n" +
                "    font-weight : bold;\n" +
                "    font-size   : 25px;\n" +
                "    margin-top  : 10px;\n" +
                "    line-height : 20px;\n" +
                "}\n" +
                ".tramiteHeader {\n" +
                "   width        : 100%;\n" +
                "   border-bottom: solid 1px black;\n" +
                "}\n" +
                "p{\n" +
                "   text-align: justify;\n" +
                "}\n"
        content += "</style>\n"
        content += "</head>\n"
        content += "<body>\n"
        content += "<div class='hoja'>\n"
        content += elm.headerTramite(tramite: tramite, pdf: true)
        content += text
        content += "</div>\n"
        content += "</body>\n"
        content += "</html>"

        def file = new File(path + tramite.tipoDocumento.descripcion + "_" + tramite.codigo + "_source_" + (new Date().format("yyyyMMdd_HH:mm:ss")) + ".html")
        file.write(content)

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(file);
        renderer.setDocument(doc, null);
        renderer.layout();
        renderer.createPDF(baos);
        byte[] b = baos.toByteArray();

        file.delete()

        if (params.enviar == "1") {
            def pathPdf = realPath + "tramites/"
            new File(pathPdf).mkdirs()
            def fileSave = new File(pathPdf + tramite.codigo + ".pdf")
            OutputStream os = new FileOutputStream(fileSave);
            renderer.layout();
            renderer.createPDF(os);
            os.close();
        }

        if (params.type == "download") {
            println("entro!!!!!")
            render "OK*" + tramite.codigo + ".pdf"
            return
        } else {
            response.setContentType("application/pdf")
            response.setHeader("Content-disposition", "attachment; filename=" + (params.filename ?: tramite.tipoDocumento.descripcion + "_" + tramite.codigo + ".pdf"))
            response.setContentLength(b.length)
            response.getOutputStream().write(b)
        }
    }

    def verPdf() {
        def tramite = Tramite.get(params.id)
        def usuarioEnvia = tramite.deId
        def realPath = servletContext.getRealPath("/") + "tramites/" + tramite.codigo + ".pdf"

    }

//    def crearPdf_old() {
//        def tramite = Tramite.get(params.id)
//        def tipoTramite = tramite.tipoDocumento.descripcion
//        def codigo = tramite.codigo
//
//        def rolPara = RolPersonaTramite.findByCodigo('R001')
//        def rolCC = RolPersonaTramite.findByCodigo('R002')
//
//        def para = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(tramite, rolPara)
//        def cc = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(tramite, rolCC)
//
//        def fileName = tipoTramite.toLowerCase() + "_" + codigo
//
//        def texto = tramite.texto
//
//        def baos = new ByteArrayOutputStream()
//        def name = fileName + "_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
////            println "name "+name
//        Font titleFont = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
//        Font titleFont3 = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
//        Font titleFont2 = new Font(Font.TIMES_ROMAN, 16, Font.BOLD);
//        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
//
//        Font fontTh = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
//        Font fontTd = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
//
//        Document document = reportesPdfService.crearDocumento([top: 4.5, right: 2.5, bottom: 2.5, left: 3])
//        //crea el doc A4, vertical con margenes de top:4.5, right:2.5, bottom:2.5, left:2.5
//        def pdfw = PdfWriter.getInstance(document, baos);
//
//        reportesPdfService.documentoFooter(document, tipoTramite, true)
//        //pone en el footer el tipo de tramite q es y el numero de pagina
//
//        document.open();
//        reportesPdfService.propiedadesDocumento(document, tipoTramite)
//        //pone las propiedades: title, subject, keywords, author, creator
//
////        println titulo
//        Paragraph headersTitulo = new Paragraph();
//        headersTitulo.setAlignment(Element.ALIGN_CENTER);
//        headersTitulo.add(new Paragraph(tipoTramite.toUpperCase(), titleFont2));
//
//        document.add(headersTitulo)
//
//        def paramsBorderTop = [bct: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bcb: Color.WHITE, bwt: 0.1, bwl: 0, bwr: 0, bwb: 0, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, colspan: 2]
//        def paramsBorderBottom = [bcb: Color.BLACK, bcl: Color.WHITE, bcr: Color.WHITE, bct: Color.WHITE, bwb: 0.1, bwl: 0, bwr: 0, bwt: 0, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
//        def paramsNoBorder = [borderWidth: 0.1, borderColor: Color.WHITE, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
//
//        def tablaHeader = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([15, 85]), 25, 5)
//        reportesPdfService.addCellTabla(tablaHeader, new Paragraph("No.  " + codigo, fontTh), paramsBorderTop)
//
//        reportesPdfService.addCellTabla(tablaHeader, new Paragraph("", fontTh), paramsNoBorder)
//        reportesPdfService.addCellTabla(tablaHeader, new Paragraph("", fontTd), paramsNoBorder)
//
//        reportesPdfService.addCellTabla(tablaHeader, new Paragraph("DE", fontTh), paramsNoBorder)
//        reportesPdfService.addCellTabla(tablaHeader, new Paragraph(tramite.de.departamento.descripcion, fontTd), paramsNoBorder)
//
//        if (para) {
//            def strPara = ""
//            para.each { p ->
//                if (p.persona) {
//                    if (strPara != "") {
//                        strPara += ", "
//                    }
//                    strPara += util.nombrePersona(persona: p.persona)
//                }
//                if (p.departamento) {
//                    if (strPara != "") {
//                        strPara += ", "
//                    }
//                    strPara += p.departamento.descripcion
//                }
//            }
//            reportesPdfService.addCellTabla(tablaHeader, new Paragraph("PARA", fontTh), paramsNoBorder)
//            reportesPdfService.addCellTabla(tablaHeader, new Paragraph(strPara, fontTd), paramsNoBorder)
//        }
//        reportesPdfService.addCellTabla(tablaHeader, new Paragraph("FECHA", fontTh), paramsNoBorder)
//        reportesPdfService.addCellTabla(tablaHeader, new Paragraph(util.fechaConFormato(fecha: tramite.fechaCreacion, ciudad: "Quito").toString(), fontTd), paramsNoBorder)
//
//        reportesPdfService.addCellTabla(tablaHeader, new Paragraph("ASUNTO", fontTh), paramsBorderBottom)
//        reportesPdfService.addCellTabla(tablaHeader, new Paragraph(tramite.asunto, fontTd), paramsBorderBottom)
//
//        document.add(tablaHeader)
//
////        def sql = "SELECT DISTINCT\n" +
//////                "  v.voit__id id,\n" +
//////                "  i.item__id iid,\n" +
////                "  i.itemcdgo codigo,\n" +
////                "  i.itemnmbr item,\n" +
////                "  v.voitcoef aporte,\n" +
////                "  v.voitpcun precio,\n" +
////                "  g.grpodscr grupo\n" +
////                "FROM vlobitem v\n" +
////                "  INNER JOIN item i ON v.item__id = i.item__id\n" +
////                "  INNER JOIN grpo g ON v.voitgrpo = g.grpo__id\n" +
////                "WHERE v.obra__id = ${params.id}\n" +
////                "      AND voitgrpo IN (1, 2)\n" + //cambiar aqui si hay que filtrar solo mano de obra o no: 1:formula polinomica, 2:mano de obra
////                "ORDER BY g.grpodscr, i.itemnmbr;"
////
////        def tablaDatos = new PdfPTable(3);
////        tablaDatos.setWidthPercentage(100);
////        tablaDatos.setWidths(arregloEnteros([15, 77, 8]))
////
////        addCellTabla(tablaDatos, new Paragraph("Item", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
////        addCellTabla(tablaDatos, new Paragraph("Descripción", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
////        addCellTabla(tablaDatos, new Paragraph("Aporte", fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE])
////
////        def grupo = "null"
////
////        def cn = dbConnectionService.getConnection()
////        cn.eachRow(sql.toString()) { row ->
////            if (row.grupo != grupo) {
////                grupo = row.grupo
////                addCellTabla(tablaDatos, new Paragraph(row.grupo, fontTh), [border: Color.BLACK, align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 3])
////            }
////            addCellTabla(tablaDatos, new Paragraph(row.codigo, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
////            addCellTabla(tablaDatos, new Paragraph(row.item, fontTd), [border: Color.BLACK, align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE])
////            addCellTabla(tablaDatos, new Paragraph(numero(row.aporte, 5), fontTd), [border: Color.BLACK, align: Element.ALIGN_RIGHT, valign: Element.ALIGN_MIDDLE])
////        }
////
////        document.add(tablaDatos)
//
//        document.close();
//        pdfw.close()
//        byte[] b = baos.toByteArray();
//        response.setContentType("application/pdf")
//        response.setHeader("Content-disposition", "attachment; filename=" + name)
//        response.setContentLength(b.length)
//        response.getOutputStream().write(b)
//    }

    def imprimirGuia () {

//        println("params:" + params)

        def cantidadTramites = params.ids


        def baos = new ByteArrayOutputStream()
        def name = "guia_tramites_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times18bold = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeaderHoja1 = [border: Color.WHITE,  bordeTop: "1", bordeBot: "1"]
        times8boldWhite.setColor(Color.WHITE)
        times10boldWhite.setColor(Color.WHITE)
        def fonts = [times12bold: times12bold, times10bold: times10bold, times8bold: times8bold,
                times10boldWhite: times10boldWhite, times8boldWhite: times8boldWhite, times8normal: times8normal, times18bold: times18bold]

        com.lowagie.text.Document document
        document = new com.lowagie.text.Document(PageSize.A4);
        def pdfw = PdfWriter.getInstance(document, baos);
        HeaderFooter footer1 = new HeaderFooter(new Phrase('', times8normal), true);
        footer1.setBorder(Rectangle.NO_BORDER);
        footer1.setAlignment(Element.ALIGN_CENTER);
        document.setFooter(footer1);
        document.open();

        Paragraph headers = new Paragraph();
//        addEmptyLine(headers, 1);
        headers.setAlignment(Element.ALIGN_CENTER);
        headers.add(new Paragraph("Guía de Envio de Trámites", times18bold));
//        addEmptyLine(headers, 1);
        headers.add(new Paragraph(params.departamento, times12bold));
        headers.add(new Paragraph("Fecha: " + new Date().format("dd-MM-yyyy"), times12bold));


        PdfPTable tablaTramites = new PdfPTable(4);
        tablaTramites.setWidthPercentage(100);
        tablaTramites.setWidths(arregloEnteros([30, 25, 15, 15]))

        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)

        addCellTabla(tablaTramites, new Paragraph("DOCUMENTO", times10bold), prmsHeaderHoja1)
        addCellTabla(tablaTramites, new Paragraph("PARA", times8bold), prmsHeaderHoja1)
        addCellTabla(tablaTramites, new Paragraph("RECIBE", times8bold), prmsHeaderHoja1)
        addCellTabla(tablaTramites, new Paragraph("FIRMA", times8bold), prmsHeaderHoja1)

        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)

        cantidadTramites.split(',').each{
//            println(it)
            addCellTabla(tablaTramites, new Paragraph(Tramite.get(it).codigo, times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph((Tramite.get(it).getPara()?.persona?.nombre ?: '') + " " + (Tramite.get(it).getPara()?.persona?.apellido ?: ''), times8bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("______________________", times8bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("______________________", times8bold), prmsHeaderHoja)
        }

        document.add(headers);
        document.add(tablaTramites)
        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }


    static arregloEnteros(array) {
        int[] ia = new int[array.size()]
        array.eachWithIndex { it, i ->
            ia[i] = it.toInteger()
        }

        return ia
    }


    def addCellTabla(table, paragraph, params) {
        PdfPCell cell = new PdfPCell(paragraph);
//        println "params "+params
        cell.setBorderColor(Color.BLACK);

        if (params.border) {
            if (!params.bordeBot)
                if (!params.bordeTop)
                    cell.setBorderColor(params.border);
        }
        if (params.bg) {
            cell.setBackgroundColor(params.bg);
        }
        if (params.colspan) {
            cell.setColspan(params.colspan);
        }
        if (params.align) {
            cell.setHorizontalAlignment(params.align);
        }
        if (params.valign) {
            cell.setVerticalAlignment(params.valign);
        }
        if (params.w) {
            cell.setBorderWidth(params.w);
        }
        if (params.bordeTop) {
            cell.setBorderWidthTop(1)
            cell.setBorderWidthLeft(0)
            cell.setBorderWidthRight(0)
            cell.setBorderWidthBottom(0)
            cell.setPaddingTop(7);

        }
        if (params.bordeBot) {
            cell.setBorderWidthBottom(1)
            cell.setBorderWidthLeft(0)
            cell.setBorderWidthRight(0)
            cell.setPaddingBottom(7)

            if (!params.bordeTop) {
                cell.setBorderWidthTop(0)
            }
        }
        table.addCell(cell);
    }



}
