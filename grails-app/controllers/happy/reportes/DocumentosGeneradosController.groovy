package happy.reportes

import com.lowagie.text.Chunk
import com.lowagie.text.Phrase
import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfWriter
import happy.seguridad.Persona
import happy.tramites.Departamento
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite
import org.apache.poi.hssf.usermodel.HSSFFont
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Color
import java.io.*;

class DocumentosGeneradosController {

    def reportesPdfService

    def reporteGeneralPdf() {
        Font font = new com.lowagie.text.Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
        Font fontBold = new com.lowagie.text.Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTh = new com.lowagie.text.Font(Font.TIMES_ROMAN, 11, Font.BOLD);

        def personas = []

        def fileName = "documentos_generados_"
        def title = "Documentos generados de "
        def title2 = "Documentos generados por "

        if (params.tipo == "prsn") {
            personas = [Persona.get(params.id.toLong())]

            fileName += personas[0].login
            title += "${personas[0].nombre} ${personas[0].apellido} (de ${params.desde} a ${params.hasta})"
            title2 += "el usuario ${personas[0].nombre} ${personas[0].apellido} (${personas[0].login}) entre ${params.desde} y ${params.hasta}"
        } else if (params.tipo == "dpto") {
            def dep = Departamento.get(params.id.toLong())
            def hijosDep = todosDep(dep)
            fileName += dep.codigo
            title += "${dep.descripcion} (de ${params.desde} a ${params.hasta})"
            title2 += "los usuarios del departamento ${dep.descripcion} (${dep.codigo}) entre ${params.desde} y ${params.hasta}"
            personas = Persona.withCriteria {
                inList("departamento", hijosDep)
                departamento {
                    order("id", "asc")
                }
            }
        }

        def desde = new Date().parse("dd-MM-yyyy", params.desde)
        def hasta = new Date().parse("dd-MM-yyyy", params.hasta)

        def baos = new ByteArrayOutputStream()
        def name = fileName + "_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Document document = reportesPdfService.crearDocumento([top: 2.5, right: 2.5, bottom: 2.5, left: 3])
        //crea el doc A4, vertical con margenes de top:2.5, right:2.5, bottom:2.5, left:2.5
        def pdfw = PdfWriter.getInstance(document, baos);

        reportesPdfService.documentoFooter(document, "${title}        pág. ", true)
        //pone en el footer el nombre de tramite q es y el numero de pagina

        document.open();
        reportesPdfService.propiedadesDocumento(document, "trámite")
        //pone las propiedades: title, subject, keywords, author, creator

        reportesPdfService.crearEncabezado(document, title)
        //crea el encabezado que quieren estos manes con el titulo que se le mande

        def paramsCenter = [align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def paramsLeft = [align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]

        if (params.tipo == "dpto") {
            def tabla = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([42, 42, 16]), 25, 5)
            reportesPdfService.addCellTabla(tabla, new Paragraph("Departamento", fontTh), paramsCenter)
            reportesPdfService.addCellTabla(tabla, new Paragraph("Usuario", fontTh), paramsCenter)
            reportesPdfService.addCellTabla(tabla, new Paragraph("No. trámites", fontTh), paramsCenter)

            Paragraph paragraph = new Paragraph();
            paragraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(new Phrase(title2, fontBold));
            document.add(paragraph)

            personas.each { persona ->
                if (persona.estaActivo) {
                    def tramites = Tramite.withCriteria {
                        eq("de", persona)
                        ge("fechaCreacion", desde)
                        le("fechaCreacion", hasta)
                        order("fechaCreacion", "asc")
                    }

                    if (tramites.size() > 0) {
                        reportesPdfService.addCellTabla(tabla, new Paragraph("${persona.departamento.descripcion} (${persona.departamento.codigo})", font), paramsLeft)
                        reportesPdfService.addCellTabla(tabla, new Paragraph("${persona.nombre} ${persona.apellido} (${persona.login})", font), paramsLeft)
                        reportesPdfService.addCellTabla(tabla, new Paragraph("${tramites.size()}", font), paramsCenter)
                    }
                }
            }
            document.add(tabla)
        } else {
            def tramites = Tramite.withCriteria {
                eq("de", personas[0])
                ge("fechaCreacion", desde)
                le("fechaCreacion", hasta)
                order("fechaCreacion", "asc")
            }

            def phrase = new Phrase()
            phrase.add(new Chunk("El usuario ", font))
            phrase.add(new Chunk("${personas[0].nombre} ${personas[0].apellido} (${personas[0].login}) ", fontBold))
            if (tramites.size() > 0) {
                phrase.add(new Chunk("generó ", font))
                phrase.add(new Chunk("${tramites.size()} documento${tramites.size() == 1 ? '' : 's'} ", fontBold))
            } else {
                phrase.add(new Chunk("no generó documentos ", fontBold))
            }
            phrase.add(new Chunk("entre ", font))
            phrase.add(new Chunk("${params.desde} y ${params.hasta}", fontBold))

            Paragraph paragraph = new Paragraph();
            paragraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(phrase);
            document.add(paragraph)
        }

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def todosDep(Departamento departamento) {
        def arr = []

        arr += departamento
        Departamento.findAllByPadre(departamento).each { dep ->
            arr += todosDep(dep)
        }

        return arr
    }

    def reporteDetalladoPdf() {

        Font font = new com.lowagie.text.Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
        Font fontBold = new com.lowagie.text.Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTh = new com.lowagie.text.Font(Font.TIMES_ROMAN, 11, Font.BOLD);

        def personas = []

        def fileName = "detalle_documentos_generados_"
        def title = "Detalle de los documentos generados de "
        def title2 = "Detalle de los documentos generados por "

        if (params.tipo == "prsn") {
            personas = [Persona.get(params.id.toLong())]

            fileName += personas[0].login
            title += "${personas[0].nombre} ${personas[0].apellido} (de ${params.desde} a ${params.hasta})"
            title2 += "el usuario ${personas[0].nombre} ${personas[0].apellido} (${personas[0].login}) entre ${params.desde} y ${params.hasta}"
        } else if (params.tipo == "dpto") {
            def dep = Departamento.get(params.id.toLong())
            def hijosDep = todosDep(dep)
            fileName += dep.codigo
            title += "${dep.descripcion} (de ${params.desde} a ${params.hasta})"
            title2 += "los usuarios del departamento ${dep.descripcion} (${dep.codigo}) entre ${params.desde} y ${params.hasta}"
            personas = Persona.withCriteria {
                inList("departamento", hijosDep)
                departamento {
                    order("id", "asc")
                }
            }
        }
//        println "PERSONAS: " + personas
        def desde = new Date().parse("dd-MM-yyyy", params.desde)
        def hasta = new Date().parse("dd-MM-yyyy", params.hasta)

        def baos = new ByteArrayOutputStream()
        def name = fileName + "_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Document document = reportesPdfService.crearDocumento("h", [top: 2.5, right: 2.5, bottom: 2.5, left: 3])
        //crea el doc A4, vertical con margenes de top:2.5, right:2.5, bottom:2.5, left:2.5
        def pdfw = PdfWriter.getInstance(document, baos);

        reportesPdfService.documentoFooter(document, "${title}        pág. ", true)
        //pone en el footer el nombre de tramite q es y el numero de pagina

        document.open();
        reportesPdfService.propiedadesDocumento(document, "trámite")
        //pone las propiedades: title, subject, keywords, author, creator

        reportesPdfService.crearEncabezado(document, title)
        //crea el encabezado que quieren estos manes con el titulo que se le mande

        def paramsCenter = [align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def paramsLeft = [align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]

        def paramsDpto = [align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, bg: Color.GRAY, colspan: 6, height: 20]
        def paramsUsuario = [align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, bg: Color.LIGHT_GRAY, colspan: 6, height: 20]

        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(Element.ALIGN_LEFT);
        paragraph.add(new Phrase(title2, fontBold));
        document.add(paragraph)

        def tabla = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([15, 15, 20, 20, 15, 15]), 10, 5)
        reportesPdfService.addCellTabla(tabla, new Paragraph("No.", fontTh), paramsCenter)
        reportesPdfService.addCellTabla(tabla, new Paragraph("Fecha creación", fontTh), paramsCenter)
        reportesPdfService.addCellTabla(tabla, new Paragraph("Para oficina", fontTh), paramsCenter)
        reportesPdfService.addCellTabla(tabla, new Paragraph("Destinatario", fontTh), paramsCenter)
        reportesPdfService.addCellTabla(tabla, new Paragraph("Fecha envío", fontTh), paramsCenter)
        reportesPdfService.addCellTabla(tabla, new Paragraph("Fecha recepción", fontTh), paramsCenter)

        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCopia = RolPersonaTramite.findByCodigo("R002")

        def depActual = null

        personas.each { persona ->

            def tramites = Tramite.withCriteria {
                eq("de", persona)
                ge("fechaCreacion", desde)
                le("fechaCreacion", hasta)
                order("fechaCreacion", "asc")
            }
            if (persona.estaActivo && tramites.size() > 0) {
                if (params.tipo == "dpto" && persona.departamentoId != depActual) {
                    depActual = persona.departamentoId
                    def header = persona.departamento.descripcion
                    reportesPdfService.addCellTabla(tabla, new Paragraph(header, fontBold), paramsDpto)
                }

                if (params.tipo == "dpto") {
                    def header = persona.nombre + " " + persona.apellido + " (" + persona.login + "): " +
                            "${tramites.size()} documento${tramites.size() == 1 ? '' : 's'}"
                    reportesPdfService.addCellTabla(tabla, new Paragraph(header, fontBold), paramsUsuario)
                }
                tramites.each { tr ->
                    def prtr = PersonaDocumentoTramite.withCriteria {
                        eq("tramite", tr)
                        inList("rolPersonaTramite", [rolPara, rolCopia])
                        order("fechaEnvio", "asc")
                    }
                    prtr.each { persDoc ->
                        def paraOficina = persDoc.persona ? (persDoc.persona.departamento.descripcion + " (" + persDoc.persona.departamento.codigo + ")") : (persDoc.departamento.descripcion + " (" + persDoc.departamento.codigo + ")")
                        def para = persDoc.persona ? (persDoc.persona.nombre + " " + persDoc.persona.apellido + " (" + persDoc.persona.login + ")") : persDoc.departamento.codigo
                        def cod = tr.codigo + (persDoc.rolPersonaTramite.codigo == "R002" ? "  [CC]" : "")
                        reportesPdfService.addCellTabla(tabla, new Paragraph(cod, font), paramsLeft)
                        reportesPdfService.addCellTabla(tabla, new Paragraph(tr.fechaCreacion.format("dd-MM-yyyy HH:mm"), font), paramsCenter)
                        reportesPdfService.addCellTabla(tabla, new Paragraph(paraOficina, font), paramsLeft)
                        reportesPdfService.addCellTabla(tabla, new Paragraph(para, font), paramsLeft)
                        reportesPdfService.addCellTabla(tabla, new Paragraph(persDoc.fechaEnvio ? persDoc.fechaEnvio.format("dd-MM-yyyy HH:mm") : "", font), paramsCenter)
                        reportesPdfService.addCellTabla(tabla, new Paragraph(persDoc.fechaRecepcion ? persDoc.fechaRecepcion.format("dd-MM-yyyy HH:mm") : "", font), paramsCenter)
                    }
                }
            }
        }

        document.add(tabla)

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def reporteGeneralXlsx() {

        def downloadName = "Reporte.xlsx"

        def path = servletContext.getRealPath("/") + "xls/"
        new File(path).mkdirs()
        //esto crea un archivo temporal que puede ser siempre el mismo para no ocupar espacio
        String filename = path + "text.xlsx";
        String sheetName = "Sheet1";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName);
        CreationHelper createHelper = wb.getCreationHelper();
        sheet.setAutobreaks(true);

        XSSFRow rowHead = sheet.createRow((short) 0);

        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setItalic(true);
        font.setBold(true);
        font.setColor(HSSFColor.GREEN.index);

        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFont(font)

        rowHead.setHeightInPoints(14)

        Cell cell = rowHead.createCell((int) 0)
        cell.setCellValue("Index")
        cell.setCellStyle(style)
        sheet.setColumnWidth(0, 3000)

        cell = rowHead.createCell((int) 1)
        cell.setCellValue("Name")
        cell.setCellStyle(style)

        cell = rowHead.createCell((int) 2)
        cell.setCellValue("Code")
        cell.setCellStyle(style)

        cell = rowHead.createCell((int) 3)
        cell.setCellValue("Salary")
        cell.setCellStyle(style)

        cell = rowHead.createCell((int) 4)
        cell.setCellValue("City")
        cell.setCellStyle(style)

        cell = rowHead.createCell((int) 5)
        cell.setCellValue("State")
        cell.setCellStyle(style)

        cell = rowHead.createCell((int) 6)
        cell.setCellValue("Number")
        cell.setCellStyle(style)

        cell = rowHead.createCell((int) 7)
        cell.setCellValue("Date")
        cell.setCellStyle(style)
        sheet.setColumnWidth(7, 5000)

        int i = 0, index = 0;
        for (i = 0; i < 6; i++) {
            index++;
            XSSFRow row = sheet.createRow((short) index);
            row.createCell((int) 0).setCellValue(index);
            row.createCell((int) 1).setCellValue("Name -- " + index);
            row.createCell((int) 2).setCellValue(createHelper.createRichTextString("Name " + index));
            row.createCell((int) 3).setCellValue("4500" + index);
            row.createCell((int) 4).setCellValue("City -- " + index);
            row.createCell((int) 5).setCellValue("State -- " + index);
            row.createCell((int) 6).setCellValue(1.2);

            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setDataFormat(
                    createHelper.createDataFormat().getFormat("dd-mm-yyyy h:mm"));
            Cell c = row.createCell((int) 7);
            c.setCellValue(new Date());
            c.setCellStyle(cellStyle);
        }

        FileOutputStream fileOut = new FileOutputStream(filename);
        wb.write(fileOut);
        fileOut.close();
        String disHeader = "Attachment;Filename=\"${downloadName}\"";
        response.setHeader("Content-Disposition", disHeader);
        File desktopFile = new File(filename);
        PrintWriter pw = response.getWriter();
        FileInputStream fileInputStream = new FileInputStream(desktopFile);
        int j;

        while ((j = fileInputStream.read()) != -1) {
            pw.write(j);
        }
        fileInputStream.close();
        response.flushBuffer();
        pw.flush();
        pw.close();

    }

}
