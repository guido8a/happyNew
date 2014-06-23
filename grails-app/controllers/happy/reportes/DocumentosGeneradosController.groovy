package happy.reportes

import org.jfree.chart.labels.StandardPieSectionLabelGenerator
import org.jfree.chart.plot.PiePlot
import org.jfree.data.general.DefaultPieDataset

import java.awt.geom.Rectangle2D
import com.lowagie.text.Chunk
import com.lowagie.text.Phrase
import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Paragraph
import com.lowagie.text.Font
import com.lowagie.text.pdf.DefaultFontMapper
import com.lowagie.text.pdf.PdfContentByte
import com.lowagie.text.pdf.PdfTemplate
import com.lowagie.text.pdf.PdfWriter

import happy.seguridad.Persona
import happy.tramites.Departamento
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite

import org.apache.poi.hssf.usermodel.HSSFFont
import org.apache.poi.hssf.util.CellRangeAddress
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.ss.usermodel.IndexedColors

//import org.apache.poi.ss.usermodel.Font
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.Color
import java.awt.Graphics2D
import java.io.*;

class DocumentosGeneradosController {

    def reportesPdfService

    def reporteGeneralPdf() {
        Font font = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
        Font fontBold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 11, Font.BOLD);

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

            def total = [:]
            def depActual = personas.first().departamentoId
            def dep = personas.first().departamento

            personas.each { persona ->
                if (persona.estaActivo) {
                    def tramites = Tramite.withCriteria {
                        eq("de", persona)
                        ge("fechaCreacion", desde)
                        le("fechaCreacion", hasta)
                        order("fechaCreacion", "asc")
                    }

                    if (tramites.size() > 0) {
                        if (!total[persona.departamento]) {
                            total[persona.departamento] = [:]
                            total[persona.departamento].total = 0
                            total[persona.departamento].detalle = [:]
                        }
                        total[persona.departamento].total += tramites.size()
                        total[persona.departamento].detalle[persona] = tramites.size()

                        if (persona.departamentoId != depActual) {
                            reportesPdfService.addCellTabla(tabla, new Paragraph("${dep.descripcion} (${dep.codigo})", fontBold), paramsCenter)
                            reportesPdfService.addCellTabla(tabla, new Paragraph("TOTAL", fontBold), paramsCenter)
                            reportesPdfService.addCellTabla(tabla, new Paragraph("${total[dep].total}", fontBold), paramsCenter)
                            depActual = persona.departamentoId
                            dep = persona.departamento
                        }
                        reportesPdfService.addCellTabla(tabla, new Paragraph("${persona.departamento.descripcion} (${persona.departamento.codigo})", font), paramsLeft)
                        reportesPdfService.addCellTabla(tabla, new Paragraph("${persona.nombre} ${persona.apellido} (${persona.login})", font), paramsLeft)
                        reportesPdfService.addCellTabla(tabla, new Paragraph("${tramites.size()}", font), paramsCenter)
                    }
                }
            }
            reportesPdfService.addCellTabla(tabla, new Paragraph("${dep.descripcion} (${dep.codigo})", fontBold), paramsCenter)
            reportesPdfService.addCellTabla(tabla, new Paragraph("TOTAL", fontBold), paramsCenter)
            reportesPdfService.addCellTabla(tabla, new Paragraph("${total[dep].total}", fontBold), paramsCenter)

//            println total
//            println total.size()
            def tot = 0
            total.each { k, v ->
                tot += v.total
            }

            reportesPdfService.addCellTabla(tabla, new Paragraph("TOTAL", fontBold), paramsCenter)
            reportesPdfService.addCellTabla(tabla, new Paragraph("TOTAL", fontBold), paramsCenter)
            reportesPdfService.addCellTabla(tabla, new Paragraph("${tot}", fontBold), paramsCenter)
            document.add(tabla)

            try {
                document.newPage()
                def width = 550
                def height = 250
                PdfContentByte contentByte = pdfw.getDirectContent();
                PdfTemplate template = contentByte.createTemplate(width, height);
                Graphics2D graphics2d = template.createGraphics(width, height, new DefaultFontMapper());
                Rectangle2D rectangle2d = new Rectangle2D.Double(0, 0, width, height);
//
////        PARA GRAFICO BARRAS
////            DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
////            dataSet.setValue(791, "Population", "1750 AD");
////            dataSet.setValue(978, "Population", "1800 AD");
////            dataSet.setValue(1262, "Population", "1850 AD");
////            dataSet.setValue(1650, "Population", "1900 AD");
////            dataSet.setValue(2519, "Population", "1950 AD");
////            dataSet.setValue(6070, "Population", "2000 AD");
////
////        PARA GRAFICO PASTEL
////            JFreeChart chart = ChartFactory.createBarChart(
////                    "World Population growth", "Year", "Population in millions",
////                    dataSet, PlotOrientation.VERTICAL, false, true, false);
//
                def ttl = " por departamento"
                DefaultPieDataset dataSet = new DefaultPieDataset();
                total.each { k, v ->
                    if (total.size() > 1) {
                        dataSet.setValue(k.codigo, v.total);
                    } else {
                        ttl = " de " + k.descripcion
                        v.detalle.each { kk, vv ->
                            dataSet.setValue(kk.login, vv);
                        }
                    }
                }
//            dataSet.setValue("China", 19.64);
//            dataSet.setValue("India", 17.3);
//            dataSet.setValue("United States", 4.54);
//            dataSet.setValue("Indonesia", 3.4);
//            dataSet.setValue("Brazil", 2.83);
//            dataSet.setValue("Pakistan", 2.48);
//            dataSet.setValue("Bangladesh", 2.38);

                JFreeChart chart = ChartFactory.createPieChart("Documentos generados" + ttl, dataSet, true, true, false);
                chart.setTitle(
                        new org.jfree.chart.title.TextTitle("Documentos generados" + ttl,
                                new java.awt.Font("SansSerif", java.awt.Font.BOLD, 15)
                        )
                );

                /* getPlot method of JFreeChart class returns the PiePlot object back to us */
                PiePlot ColorConfigurator = (PiePlot) chart.getPlot(); /* get PiePlot object for changing */
                /* We can now use setSectionPaint method to change the color of our chart */
//                ColorConfigurator.setSectionPaint("Java", new Color(160, 160, 255));
//                ColorConfigurator.setSectionPaint("C++", Color.RED);
//                ColorConfigurator.setSectionPaint("C", Color.BLUE);
//                ColorConfigurator.setSectionPaint("VB", Color.GREEN);
//                ColorConfigurator.setSectionPaint("Shell Script", Color.YELLOW);
//                /* We specify explode option for the Pie chart using setExplodePercent method */
//                /* This method takes a percentage value and offsets the section of Pie Chart, a percentage value of radius */
//                ColorConfigurator.setExplodePercent("Shell Script", 0.30);
                /* A format mask specified to display labels. Here {0} is the section name, and {1} is the value.
                We can also use {2} which will display a percent value */
                ColorConfigurator.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{1} docs. ({2})"));
                /* Set color of the label background on the pie chart */
                ColorConfigurator.setLabelBackgroundPaint(new Color(220, 220, 220));

                chart.draw(graphics2d, rectangle2d);

                graphics2d.dispose();
                contentByte.addTemplate(template, 30, 500);

            } catch (Exception e) {
                println "ERROR GRAFICOS::::::: "
                e.printStackTrace();
            }

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

        Font font = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
        Font fontBold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontTh = new Font(Font.TIMES_ROMAN, 11, Font.BOLD);

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
//        reportesPdfService.addCellTabla(tabla, new Paragraph("No.", fontTh), paramsCenter)
//        reportesPdfService.addCellTabla(tabla, new Paragraph("Fecha creación", fontTh), paramsCenter)
//        reportesPdfService.addCellTabla(tabla, new Paragraph("Para oficina", fontTh), paramsCenter)
//        reportesPdfService.addCellTabla(tabla, new Paragraph("Destinatario", fontTh), paramsCenter)
//        reportesPdfService.addCellTabla(tabla, new Paragraph("Fecha envío", fontTh), paramsCenter)
//        reportesPdfService.addCellTabla(tabla, new Paragraph("Fecha recepción", fontTh), paramsCenter)

        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCopia = RolPersonaTramite.findByCodigo("R002")

//        def depActual = null

        personas.eachWithIndex { persona, i ->

            def tramites = Tramite.withCriteria {
                eq("de", persona)
                ge("fechaCreacion", desde)
                le("fechaCreacion", hasta)
                order("fechaCreacion", "asc")
            }
            if (persona.estaActivo && tramites.size() > 0) {
                if (params.tipo == "dpto" /*&& persona.departamentoId != depActual*/) {
                    if (i > 0) {
                        document.add(tabla)
                    }
//                    depActual = persona.departamentoId
                    def header = persona.departamento.descripcion
//                    reportesPdfService.addCellTabla(tabla, new Paragraph(header, fontBold), paramsDpto)
                    Paragraph paragraphDep = new Paragraph();
                    paragraphDep.setAlignment(Element.ALIGN_LEFT);
                    paragraphDep.add(new Phrase(header, fontBold));
                    paragraphDep.setSpacingBefore(15)
                    document.add(paragraphDep)
                }

                if (params.tipo == "dpto") {
                    def header = persona.nombre + " " + persona.apellido + " (" + persona.login + "): " +
                            "${tramites.size()} documento${tramites.size() == 1 ? '' : 's'}"
                    Paragraph paragraphUsu = new Paragraph();
                    paragraphUsu.setAlignment(Element.ALIGN_LEFT);
                    paragraphUsu.add(new Phrase(header, fontBold));
                    document.add(paragraphUsu)

                    tabla = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([15, 15, 20, 20, 15, 15]), 10, 5)
                    reportesPdfService.addCellTabla(tabla, new Paragraph("No.", fontTh), paramsCenter)
                    reportesPdfService.addCellTabla(tabla, new Paragraph("Fecha creación", fontTh), paramsCenter)
                    reportesPdfService.addCellTabla(tabla, new Paragraph("Para oficina", fontTh), paramsCenter)
                    reportesPdfService.addCellTabla(tabla, new Paragraph("Destinatario", fontTh), paramsCenter)
                    reportesPdfService.addCellTabla(tabla, new Paragraph("Fecha envío", fontTh), paramsCenter)
                    reportesPdfService.addCellTabla(tabla, new Paragraph("Fecha recepción", fontTh), paramsCenter)
//                    reportesPdfService.addCellTabla(tabla, new Paragraph(header, fontBold), paramsUsuario)
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

        def personas = []

        def fileName = "documentos_generados_"
        def title = "Documentos generados de \n"

        if (params.tipo == "prsn") {
            personas = [Persona.get(params.id.toLong())]

            fileName += personas[0].login
            title += "${personas[0].nombre} ${personas[0].apellido}\n(de ${params.desde} a ${params.hasta})"
        } else if (params.tipo == "dpto") {
            def dep = Departamento.get(params.id.toLong())
            def hijosDep = todosDep(dep)
            fileName += dep.codigo
            title += "${dep.descripcion}\n(de ${params.desde} a ${params.hasta})"
            personas = Persona.withCriteria {
                inList("departamento", hijosDep)
                departamento {
                    order("id", "asc")
                }
            }
        }

        def desde = new Date().parse("dd-MM-yyyy", params.desde)
        def hasta = new Date().parse("dd-MM-yyyy", params.hasta)

        def downloadName = fileName + "_" + new Date().format("ddMMyyyy_hhmm") + ".xlsx";

        def path = servletContext.getRealPath("/") + "xls/"
        new File(path).mkdirs()
        //esto crea un archivo temporal que puede ser siempre el mismo para no ocupar espacio
        String filename = path + "text.xlsx";
        String sheetName = "Resumen";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName);
        CreationHelper createHelper = wb.getCreationHelper();
        sheet.setAutobreaks(true);

        org.apache.poi.ss.usermodel.Font fontTitle = wb.createFont();
        fontTitle.setFontHeightInPoints((short) 18);
        fontTitle.setFontName(HSSFFont.FONT_ARIAL);
        fontTitle.setItalic(true);
        fontTitle.setBold(true);
        fontTitle.setColor(HSSFColor.DARK_RED.index);

        CellStyle styleTitle = wb.createCellStyle();
        styleTitle.setAlignment(CellStyle.ALIGN_CENTER);
        styleTitle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        styleTitle.setFont(fontTitle)
        styleTitle.setWrapText(true);

        org.apache.poi.ss.usermodel.Font fontHeaders = wb.createFont();
        fontHeaders.setFontHeightInPoints((short) 12);
        fontHeaders.setFontName(HSSFFont.FONT_ARIAL);
        fontHeaders.setItalic(true);
        fontHeaders.setBold(true);

        CellStyle styleHeaders = wb.createCellStyle();
        styleHeaders.setAlignment(CellStyle.ALIGN_CENTER);
        styleHeaders.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        styleHeaders.setFont(fontHeaders)

        XSSFRow rowTitle = sheet.createRow((short) 0);
        rowTitle.setHeightInPoints(40)
        Cell cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue(title);
        cellTitle.setCellStyle(styleTitle)

        sheet.addMergedRegion(new CellRangeAddress(
                0, //first row (0-based)
                2, //last row  (0-based)
                0, //first column (0-based)
                3  //last column  (0-based)
        ));

        XSSFRow rowHead = sheet.createRow((short) 3);
        rowHead.setHeightInPoints(14)

        Cell cell = rowHead.createCell((int) 0)
        cell.setCellValue("Departamento")
        cell.setCellStyle(styleHeaders)
        sheet.setColumnWidth(0, 15000)

        cell = rowHead.createCell((int) 1)
        cell.setCellValue("Usuario")
        cell.setCellStyle(styleHeaders)
        sheet.setColumnWidth(1, 10000)

        cell = rowHead.createCell((int) 2)
        cell.setCellValue("N. trámites")
        cell.setCellStyle(styleHeaders)
        sheet.setColumnWidth(2, 3000)

        def index = 4

        personas.each { persona ->
            if (persona.estaActivo) {
                def tramites = Tramite.withCriteria {
                    eq("de", persona)
                    ge("fechaCreacion", desde)
                    le("fechaCreacion", hasta)
                    order("fechaCreacion", "asc")
                }

                if (tramites.size() > 0) {
                    XSSFRow row = sheet.createRow((short) index)
                    row.createCell((int) 0).setCellValue("${persona.departamento.descripcion} (${persona.departamento.codigo})")
                    row.createCell((int) 1).setCellValue("${persona.nombre} ${persona.apellido} (${persona.login})")
                    row.createCell((int) 2).setCellValue(tramites.size())
                    index++
                }
            }
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

    def reporteDetalladoXls() {

        def personas = []

        def fileName = "detalle_documentos_generados_"
        def title = "Detalle de los documentos generados de \n"

        if (params.tipo == "prsn") {
            personas = [Persona.get(params.id.toLong())]

            fileName += personas[0].login
            title += "${personas[0].nombre} ${personas[0].apellido}\n(de ${params.desde} a ${params.hasta})"
        } else if (params.tipo == "dpto") {
            def dep = Departamento.get(params.id.toLong())
            def hijosDep = todosDep(dep)
            fileName += dep.codigo
            title += "${dep.descripcion}\n(de ${params.desde} a ${params.hasta})"
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

        def downloadName = fileName + "_" + new Date().format("ddMMyyyy_hhmm") + ".xlsx";

        def path = servletContext.getRealPath("/") + "xls/"
        new File(path).mkdirs()
        //esto crea un archivo temporal que puede ser siempre el mismo para no ocupar espacio
        String filename = path + "text.xlsx";
        String sheetName = "Resumen";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName);
        CreationHelper createHelper = wb.getCreationHelper();
        sheet.setAutobreaks(true);

        org.apache.poi.ss.usermodel.Font fontUsuario = wb.createFont();
        fontUsuario.setFontHeightInPoints((short) 12);
        fontUsuario.setFontName(HSSFFont.FONT_ARIAL);
        fontUsuario.setItalic(true);
        fontUsuario.setBold(true);
//        fontUsuario.setColor(HSSFColor.GREY_40_PERCENT.index);

        org.apache.poi.ss.usermodel.Font fontDep = wb.createFont();
        fontDep.setFontHeightInPoints((short) 14);
        fontDep.setFontName(HSSFFont.FONT_ARIAL);
        fontDep.setItalic(true);
        fontDep.setBold(true);
//        fontDep.setColor(HSSFColor.GREY_80_PERCENT.index);

        org.apache.poi.ss.usermodel.Font fontTitle = wb.createFont();
        fontTitle.setFontHeightInPoints((short) 18);
        fontTitle.setFontName(HSSFFont.FONT_ARIAL);
        fontTitle.setItalic(true);
        fontTitle.setBold(true);
        fontTitle.setColor(HSSFColor.DARK_RED.index);

        org.apache.poi.ss.usermodel.Font fontHeaders = wb.createFont();
        fontHeaders.setFontHeightInPoints((short) 12);
        fontHeaders.setFontName(HSSFFont.FONT_ARIAL);
        fontHeaders.setItalic(true);
        fontHeaders.setBold(true);

        CellStyle styleUsuario = wb.createCellStyle();
        styleUsuario.setAlignment(CellStyle.ALIGN_CENTER);
        styleUsuario.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        styleUsuario.setFont(fontUsuario)
        styleUsuario.setFillBackgroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        styleUsuario.setFillPattern(CellStyle.BIG_SPOTS);
//        styleUsuario.setFillForegroundColor(HSSFColor.DARK_RED.index)
//        styleUsuario.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styleUsuario.setWrapText(true);

        CellStyle styleDep = wb.createCellStyle();
        styleDep.setAlignment(CellStyle.ALIGN_CENTER);
        styleDep.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        styleDep.setFont(fontDep)
        styleDep.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        styleDep.setFillPattern(CellStyle.SOLID_FOREGROUND);
//        styleDep.setFillBackgroundColor(IndexedColors.GREY_80_PERCENT.getIndex())
//        styleDep.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styleDep.setWrapText(true);

        CellStyle styleTitle = wb.createCellStyle();
        styleTitle.setAlignment(CellStyle.ALIGN_CENTER);
        styleTitle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        styleTitle.setFont(fontTitle)
        styleTitle.setWrapText(true);

        CellStyle styleHeaders = wb.createCellStyle();
        styleHeaders.setAlignment(CellStyle.ALIGN_CENTER);
        styleHeaders.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        styleHeaders.setFont(fontHeaders)

        CellStyle styleDate = wb.createCellStyle();
        styleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy hh:mm"));

        XSSFRow rowTitle = sheet.createRow((short) 0);
        rowTitle.setHeightInPoints(40)
        Cell cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue(title);
        cellTitle.setCellStyle(styleTitle)

        sheet.addMergedRegion(new CellRangeAddress(
                0, //first row (0-based)
                2, //last row  (0-based)
                0, //first column (0-based)
                5  //last column  (0-based)
        ));

        def wFechas = 3000

        XSSFRow rowHead = sheet.createRow((short) 3);
        rowHead.setHeightInPoints(14)

        Cell cell = rowHead.createCell((int) 0)
        cell.setCellValue("No.")
        cell.setCellStyle(styleHeaders)
        sheet.setColumnWidth(0, 4000)

        cell = rowHead.createCell((int) 1)
        cell.setCellValue("Fecha creación")
        cell.setCellStyle(styleHeaders)
        sheet.setColumnWidth(1, wFechas)

        cell = rowHead.createCell((int) 2)
        cell.setCellValue("Para oficina")
        cell.setCellStyle(styleHeaders)
        sheet.setColumnWidth(2, 15000)

        cell = rowHead.createCell((int) 3)
        cell.setCellValue("Destinatario")
        cell.setCellStyle(styleHeaders)
        sheet.setColumnWidth(3, 10000)

        cell = rowHead.createCell((int) 4)
        cell.setCellValue("Fecha envío")
        cell.setCellStyle(styleHeaders)
        sheet.setColumnWidth(4, wFechas)

        cell = rowHead.createCell((int) 5)
        cell.setCellValue("Fecha recepción")
        cell.setCellStyle(styleHeaders)
        sheet.setColumnWidth(5, wFechas)

        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCopia = RolPersonaTramite.findByCodigo("R002")

        def depActual = null
        def index = 4

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

                    XSSFRow row = sheet.createRow((short) index)
                    cell = row.createCell((int) 0)
                    cell.setCellValue(header)
                    cell.setCellStyle(styleDep)
                    sheet.addMergedRegion(new CellRangeAddress(
                            index, //first row (0-based)
                            index, //last row  (0-based)
                            0, //first column (0-based)
                            5  //last column  (0-based)
                    ));
                    index++
                }

                if (params.tipo == "dpto") {
                    def header = persona.nombre + " " + persona.apellido + " (" + persona.login + "): " +
                            "${tramites.size()} documento${tramites.size() == 1 ? '' : 's'}"
                    XSSFRow row = sheet.createRow((short) index)
                    cell = row.createCell((int) 0)
                    cell.setCellValue(header)
                    cell.setCellStyle(styleUsuario)
                    sheet.addMergedRegion(new CellRangeAddress(
                            index, //first row (0-based)
                            index, //last row  (0-based)
                            0, //first column (0-based)
                            5  //last column  (0-based)
                    ));
                    index++
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

                        XSSFRow row = sheet.createRow((short) index)
                        row.createCell((int) 0).setCellValue(cod)

                        cell = row.createCell((int) 1)
                        cell.setCellValue(tr.fechaCreacion)
                        cell.setCellStyle(styleDate)

                        row.createCell((int) 2).setCellValue(paraOficina)
                        row.createCell((int) 3).setCellValue(para)

                        cell = row.createCell((int) 4)
                        cell.setCellValue(persDoc.fechaEnvio)
                        cell.setCellStyle(styleDate)

                        cell = row.createCell((int) 5)
                        cell.setCellValue(persDoc.fechaRecepcion)
                        cell.setCellStyle(styleDate)

                        index++
                    }
                }
            }
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
