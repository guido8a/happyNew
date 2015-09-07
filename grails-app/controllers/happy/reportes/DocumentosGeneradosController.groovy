package happy.reportes

import happy.seguridad.Shield
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

class DocumentosGeneradosController extends Shield{

    def reportesPdfService

    Font font = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
    Font fontBold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    Font fontTh = new Font(Font.TIMES_ROMAN, 11, Font.BOLD);
    Font fontTotalDep = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
    Font fontGranTotal = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);

    def reporteGeneralPdf() {

        def desde = new Date().parse("dd-MM-yyyy HH:mm", params.desde + " 00:00")
        def hasta = new Date().parse("dd-MM-yyyy HH:mm", params.hasta + " 23:59")

        def fileName = "documentos_generados_"
        def title = "Documentos generados de "
        def title2 = "Documentos generados por "

        if (params.tipo == "prsn") {
            def pers = Persona.get(params.id.toLong())
            def dpto = Departamento.get(params.dpto)
            if (!dpto) {
                dpto = pers.departamento
            }
            fileName += pers.login + "_" + dpto.codigo
            title += "${pers.nombre} ${pers.apellido}\nen el departamento ${dpto.descripcion}\nentre el ${params.desde} y el ${params.hasta}"
            title2 += "el usuario ${pers.nombre} ${pers.apellido} (${pers.login}) en el departamento ${dpto.descripcion} entre el ${params.desde} y el ${params.hasta}"
        } else {
            def dep = Departamento.get(params.id.toLong())
            fileName += dep.codigo
            title += "${dep.descripcion}\nde ${params.desde} a ${params.hasta}"
            title2 += "los usuarios del departamento ${dep.descripcion} (${dep.codigo}) entre ${params.desde} y ${params.hasta}"
        }

        def baos = new ByteArrayOutputStream()
        def name = fileName + "_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Document document = reportesPdfService.crearDocumento([top: 2, right: 2, bottom: 1.5, left: 2.5])
        def pdfw = PdfWriter.getInstance(document, baos);

        session.tituloReporte = title
        reportesPdfService.membrete(document)
        document.open();
        reportesPdfService.propiedadesDocumento(document, "trámite")
        def paramsCenter = [align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def paramsLeft = [align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]

        if (params.tipo == "prsn") {
            def pers = Persona.get(params.id.toLong())
            def dpto = Departamento.get(params.dpto)
            if (!dpto) {
                dpto = pers.departamento
            }
            def tramites = 0
            if (pers.estaActivo) {
                if (desde == hasta) {
                    def trams = Tramite.withCriteria {
                        eq("de", pers)
                        eq("departamento", dpto)
                        eq("fechaCreacion", desde)
                        order("fechaCreacion", "asc")
                    }
                    tramites = trams.size()
                } else {
                    def trams = Tramite.withCriteria {
                        eq("de", pers)
                        eq("departamento", dpto)
                        ge("fechaCreacion", desde)
                        le("fechaCreacion", hasta)
                        order("fechaCreacion", "asc")
                    }
                    tramites = trams.size()
                }
//
            }
            def phrase = new Phrase()
            phrase.add(new Chunk("El usuario ", font))
            phrase.add(new Chunk("${pers.nombre} ${pers.apellido} (${pers.login}) ", fontBold))
            if (tramites > 0) {
                phrase.add(new Chunk("generó ", font))
                phrase.add(new Chunk("${tramites} documento${tramites == 1 ? '' : 's'} ", fontBold))
            } else {
                phrase.add(new Chunk("no generó documentos ", fontBold))
            }
            phrase.add(new Chunk("entre el ", font))
            phrase.add(new Chunk("${params.desde} y el ${params.hasta}", fontBold))
            phrase.add(new Chunk(" en el departamento ", font))
            phrase.add(new Chunk(dpto.descripcion + ".", fontBold))

            Paragraph paragraph = new Paragraph();
            paragraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(phrase);
            document.add(paragraph)
        } else if (params.tipo == "dpto") {
            def dep = Departamento.get(params.id.toLong())
            def hijosDep = reportesPdfService.todosDep(dep)
            def tramites = [:]

            Tramite.withCriteria {
                inList("departamento", hijosDep)
                ge("fechaCreacion", desde)
                le("fechaCreacion", hasta)
                order("departamento", "asc")
                order("fechaCreacion", "asc")
            }.each { tr ->
                def pers = tr.de
                def dpto = tr.departamento
                def deDpto = tr.deDepartamento

                if (!tramites[dpto.id]) {
                    tramites[dpto.id] = [:]
                    tramites[dpto.id].departamento = dpto
                    tramites[dpto.id].personas = [:]
                }
                if (deDpto) {
                    if (!tramites[dpto.id].personas[pers.id + "_o"]) {
                        tramites[dpto.id].personas[pers.id + "_o"] = [:]
                        tramites[dpto.id].personas[pers.id + "_o"].tramites = 0
                        tramites[dpto.id].personas[pers.id + "_o"].persona = pers
                        tramites[dpto.id].personas[pers.id + "_o"].de = "${pers.nombre} ${pers.apellido} (Oficina)"
                        if (!pers.estaActivo) {
                            tramites[dpto.id].personas[pers.id + "_o"].de += " <Inactivo>"
                        }
                        tramites[dpto.id].personas[pers.id + "_o"].deGraf = "${pers.login} (Oficina)"
                    }
                    tramites[dpto.id].personas[pers.id + "_o"].tramites++
                } else {
                    if (!tramites[dpto.id].personas[pers.id]) {
                        tramites[dpto.id].personas[pers.id] = [:]
                        tramites[dpto.id].personas[pers.id].tramites = 0
                        tramites[dpto.id].personas[pers.id].persona = pers
                        tramites[dpto.id].personas[pers.id].de = "${pers.nombre} ${pers.apellido} (${pers.login})"
                        if (!pers.estaActivo) {
                            tramites[dpto.id].personas[pers.id].de += " <Inactivo>"
                        }
                        tramites[dpto.id].personas[pers.id].deGraf = "${pers.login}"
                    }
                    tramites[dpto.id].personas[pers.id].tramites++
                }
            }

            def tabla = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([42, 42, 16]), 25, 5)
            reportesPdfService.addCellTabla(tabla, new Paragraph("Departamento", fontTh), paramsCenter)
            reportesPdfService.addCellTabla(tabla, new Paragraph("Usuario", fontTh), paramsCenter)
            reportesPdfService.addCellTabla(tabla, new Paragraph("No. trámites", fontTh), paramsCenter)

            Paragraph paragraph = new Paragraph();
            paragraph.setAlignment(Element.ALIGN_LEFT);
            paragraph.add(new Phrase(title2, fontBold));
            document.add(paragraph)

            def granTotal = 0
            def totalGraf = [:]

            tramites.each { depId, depMap ->
                def depTotal = 0
                totalGraf[depMap.departamento] = [:]
                totalGraf[depMap.departamento].total = 0
                totalGraf[depMap.departamento].detalle = [:]

                depMap.personas = depMap.personas.sort { it.value.de }

                depMap.personas.each { persId, persMap ->
                    reportesPdfService.addCellTabla(tabla, new Paragraph("${depMap.departamento.descripcion} (${depMap.departamento.codigo})", font), paramsLeft)
                    reportesPdfService.addCellTabla(tabla, new Paragraph("${persMap.de}", font), paramsLeft)
                    reportesPdfService.addCellTabla(tabla, new Paragraph("${persMap.tramites}", font), paramsCenter)
                    depTotal += persMap.tramites
                    totalGraf[depMap.departamento].detalle[persMap.deGraf] = persMap.tramites
                }
                reportesPdfService.addCellTabla(tabla, new Paragraph("${depMap.departamento.descripcion} (${depMap.departamento.codigo})", fontBold), paramsCenter)
                reportesPdfService.addCellTabla(tabla, new Paragraph("TOTAL", fontBold), paramsCenter)
                reportesPdfService.addCellTabla(tabla, new Paragraph("${depTotal}", fontBold), paramsCenter)
                granTotal += depTotal
                totalGraf[depMap.departamento].total = depTotal
            }
            reportesPdfService.addCellTabla(tabla, new Paragraph("GRAN TOTAL", fontBold), [align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, colspan: 2])
            reportesPdfService.addCellTabla(tabla, new Paragraph("${granTotal}", fontBold), paramsCenter)

            def conGrafico = false

            try {
                conGrafico = true
                def width = 550
                def height = 250
                PdfContentByte contentByte = pdfw.getDirectContent();
                PdfTemplate template = contentByte.createTemplate(width, height);
                Graphics2D graphics2d = template.createGraphics(width, height, new DefaultFontMapper());
                Rectangle2D rectangle2d = new Rectangle2D.Double(0, 0, width, height);

                def ttl = " por departamento de ${dep.descripcion}"
                DefaultPieDataset dataSet = new DefaultPieDataset();
                totalGraf.each { k, v ->
                    if (totalGraf.size() > 1) {
                        dataSet.setValue(k.codigo, v.total)
                    } else {
                        ttl = " por usuario de ${dep.descripcion}"
                        v.detalle.each { kk, vv ->
                            dataSet.setValue(kk, vv);
                        }
                    }
                }
                JFreeChart chart = ChartFactory.createPieChart("Documentos generados" + ttl, dataSet, true, true, false);
                chart.setTitle(
                        new org.jfree.chart.title.TextTitle("Documentos generados" + ttl,
                                new java.awt.Font("SansSerif", java.awt.Font.BOLD, 15)
                        )
                );
                PiePlot ColorConfigurator = (PiePlot) chart.getPlot();
                ColorConfigurator.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{1} docs. ({2})"));
                ColorConfigurator.setLabelBackgroundPaint(new Color(220, 220, 220));
                chart.draw(graphics2d, rectangle2d);
                graphics2d.dispose();
                contentByte.addTemplate(template, 30, 450);
            } catch (Exception e) {
                conGrafico = false
                println "ERROR GRAFICOS::::::: "
                e.printStackTrace();
            }
            if (conGrafico) {
                document.newPage()
            }
            document.add(tabla)
        }

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def reporteDetalladoPdf() {
        def desde = new Date().parse("dd-MM-yyyy HH:mm", params.desde + " 00:00")
        def hasta = new Date().parse("dd-MM-yyyy HH:mm", params.hasta + " 23:59")

        def fileName = "detalle_documentos_generados_"
        def title = "Detalle de los documentos generados de "
        def title2 = "Detalle de los documentos generados por "
        def title3 = ""

        def tramites = [:], trams = []

        if (params.tipo == "prsn") {
            def persona = Persona.get(params.id.toLong())
            def dpto = Departamento.get(params.dpto)
            if (!dpto) {
                dpto = persona.departamento
            }
            fileName += persona.login
            title += "${persona.nombre} ${persona.apellido}\nen el departamento ${dpto.descripcion}\nentre el ${params.desde} y el ${params.hasta}"
            title2 += "el usuario ${persona.nombre} ${persona.apellido} (${persona.login}) en el departamento ${dpto.descripcion} entre el ${params.desde} y el ${params.hasta}"
            title3 += "${persona.nombre} ${persona.apellido} en el departamento ${dpto.descripcion}"
            trams = Tramite.withCriteria {
                eq("de", persona)
                eq("departamento", dpto)
                ge("fechaCreacion", desde)
                le("fechaCreacion", hasta)
                order("fechaCreacion", "asc")
            }

        } else if (params.tipo == "dpto") {
            def dep = Departamento.get(params.id.toLong())
            def hijosDep = reportesPdfService.todosDep(dep)
            fileName += dep.codigo
            title += "${dep.descripcion}\nde ${params.desde} a ${params.hasta}"
            title2 += "los usuarios del departamento ${dep.descripcion} (${dep.codigo}) entre ${params.desde} y ${params.hasta}"
            title3 += dep.descripcion
            trams = Tramite.withCriteria {
                inList("departamento", hijosDep)
                ge("fechaCreacion", desde)
                le("fechaCreacion", hasta)
                order("departamento", "asc")
                order("fechaCreacion", "asc")
            }
        }

        trams.each { tr ->
            def pers = tr.de
            def dpto = tr.departamento
            def deDpto = tr.deDepartamento

            if (!tramites[dpto.id]) {
                tramites[dpto.id] = [:]
                tramites[dpto.id].departamento = dpto
                tramites[dpto.id].personas = [:]
            }
            if (deDpto) {
                if (!tramites[dpto.id].personas[pers.id + "_o"]) {
                    tramites[dpto.id].personas[pers.id + "_o"] = [:]
                    tramites[dpto.id].personas[pers.id + "_o"].tramites = []
                    tramites[dpto.id].personas[pers.id + "_o"].persona = pers
                    tramites[dpto.id].personas[pers.id + "_o"].de = "${pers.nombre} ${pers.apellido} (Oficina)"
                    if (!pers.estaActivo) {
                        tramites[dpto.id].personas[pers.id + "_o"].de += " <Inactivo>"
                    }
                    tramites[dpto.id].personas[pers.id + "_o"].deGraf = "${pers.login} (Oficina)"
                }
                tramites[dpto.id].personas[pers.id + "_o"].tramites += tr
            } else {
                if (!tramites[dpto.id].personas[pers.id]) {
                    tramites[dpto.id].personas[pers.id] = [:]
                    tramites[dpto.id].personas[pers.id].tramites = []
                    tramites[dpto.id].personas[pers.id].persona = pers
                    tramites[dpto.id].personas[pers.id].de = "${pers.nombre} ${pers.apellido} (${pers.login})"
                    if (!pers.estaActivo) {
                        tramites[dpto.id].personas[pers.id].de += " <Inactivo>"
                    }
                    tramites[dpto.id].personas[pers.id].deGraf = "${pers.login}"
                }
                tramites[dpto.id].personas[pers.id].tramites += tr
            }
        }
        def baos = new ByteArrayOutputStream()
        def name = fileName + "_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Document document = reportesPdfService.crearDocumento("h", [top: 2.5, right: 2.5, bottom: 1.5, left: 3])
        def pdfw = PdfWriter.getInstance(document, baos);

        session.tituloReporte = title
        reportesPdfService.membrete(document)
        document.open();
        reportesPdfService.propiedadesDocumento(document, "trámite")

        def paramsCenter = [align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE, bg: Color.WHITE]
        def paramsLeft = [align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE, bg: Color.WHITE]

        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(Element.ALIGN_LEFT);
        paragraph.add(new Phrase(title2, fontBold));
        document.add(paragraph)

        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCopia = RolPersonaTramite.findByCodigo("R002")

        def granTotal = 0

        def tabla = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([17, 13, 25, 19, 13, 13]), 10, 5)

        tramites.each { depId, depMap ->
            def totalDep = 0
            def header = depMap.departamento.descripcion + " (${depMap.departamento.codigo})"
            Paragraph paragraphDep = new Paragraph();
            paragraphDep.setAlignment(Element.ALIGN_LEFT);
            paragraphDep.add(new Phrase(header, fontBold));
            paragraphDep.setSpacingBefore(15)
            document.add(paragraphDep)
            depMap.personas.each { persId, persMap ->
                header = persMap.de + " (${depMap.departamento.codigo})" +
                        ": ${persMap.tramites.size()} documento${persMap.tramites.size() == 1 ? '' : 's'} generado${tramites == 1 ? '' : 's'} sin incluir copias"
                Paragraph paragraphUsu = new Paragraph();
                paragraphUsu.setAlignment(Element.ALIGN_LEFT);
                paragraphUsu.add(new Phrase(header, fontBold));
                document.add(paragraphUsu)

                totalDep += persMap.tramites.size()

                tabla = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([17, 13, 25, 19, 13, 13]), 10, 5)
                tabla.setHeaderRows(1)
                reportesPdfService.addCellTabla(tabla, new Paragraph("No.", fontTh), paramsCenter)
                reportesPdfService.addCellTabla(tabla, new Paragraph("Fecha creación", fontTh), paramsCenter)
                reportesPdfService.addCellTabla(tabla, new Paragraph("Para oficina", fontTh), paramsCenter)
                reportesPdfService.addCellTabla(tabla, new Paragraph("Destinatario", fontTh), paramsCenter)
                reportesPdfService.addCellTabla(tabla, new Paragraph("Fecha envío", fontTh), paramsCenter)
                reportesPdfService.addCellTabla(tabla, new Paragraph("Fecha recepción", fontTh), paramsCenter)

                persMap.tramites.eachWithIndex { tr, j ->
                    if (j % 2 == 0) {
                        paramsLeft.bg = Color.WHITE
                        paramsCenter.bg = Color.WHITE
                    } else {
                        paramsLeft.bg = Color.LIGHT_GRAY
                        paramsCenter.bg = Color.LIGHT_GRAY
                    }
                    def prtr = PersonaDocumentoTramite.withCriteria {
                        eq("tramite", tr)
                        inList("rolPersonaTramite", [rolPara, rolCopia])
                        order("fechaEnvio", "asc")
                        order("rolPersonaTramite", "asc")
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
                document.add(tabla)
            }
            if (params.tipo == "dpto") {
                document.add(new Paragraph("TOTAL ${depMap.departamento.descripcion} (${depMap.departamento.codigo}): ${totalDep} documento${totalDep == 1 ? '' : 's'} generado${granTotal == 1 ? '' : 's'}", fontTotalDep))
            }
            granTotal += totalDep
        }

        document.add(new Paragraph("   ", fontGranTotal))
        document.add(new Paragraph("GRAN TOTAL ${title3}: ${granTotal} documento${granTotal == 1 ? '' : 's'} generado${granTotal == 1 ? '' : 's'}", fontGranTotal))

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def reporteGeneralXlsx() {
        def desde = new Date().parse("dd-MM-yyyy HH:mm", params.desde + " 00:00")
        def hasta = new Date().parse("dd-MM-yyyy HH:mm", params.hasta + " 23:59")

        def fileName = "documentos_generados_"
        def title = ["Reporte de documentos generados"]
        def title2 = ""

        def trams

        if (params.tipo == "prsn") {
            def pers = Persona.get(params.id.toLong())
            def dpto = Departamento.get(params.dpto)
            if (!dpto) {
                dpto = pers.departamento
            }
            fileName += pers.login + "_" + dpto.codigo
            title += ["${pers.nombre} ${pers.apellido}"]
            title += ["entre el ${params.desde} y el ${params.hasta}"]
            title2 += "${pers.nombre} ${pers.apellido} en ${dpto.descripcion}"
            trams = Tramite.withCriteria {
                eq("de", pers)
                eq("departamento", dpto)
                ge("fechaCreacion", desde)
                le("fechaCreacion", hasta)
                order("fechaCreacion", "asc")
            }
        } else {
            def dep = Departamento.get(params.id.toLong())
            fileName += dep.codigo
            def hijosDep = reportesPdfService.todosDep(dep)
            title += ["${dep.descripcion}"]
            title += ["entre el ${params.desde} y el ${params.hasta}"]
            title2 += dep.descripcion
            trams = Tramite.withCriteria {
                inList("departamento", hijosDep)
                ge("fechaCreacion", desde)
                le("fechaCreacion", hasta)
                order("departamento", "asc")
                order("fechaCreacion", "asc")
            }
        }

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

        XSSFRow rowTitle = sheet.createRow((short) 0);
        Cell cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue("GAD DE LA PROVINCIA DE PICHINCHA");
        rowTitle = sheet.createRow((short) 1);
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue("SISTEMA DE ADMINISTRACION DOCUMENTAL");
        rowTitle = sheet.createRow((short) 2);
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue(title[0]);
        rowTitle = sheet.createRow((short) 3);
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue(title[1]);
        rowTitle = sheet.createRow((short) 4);
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue(title[2]);

        def index = 6
        XSSFRow rowHead = sheet.createRow((short) index);
        rowHead.setHeightInPoints(14)

        Cell cell = rowHead.createCell((int) 0)
        cell.setCellValue("Departamento")
        sheet.setColumnWidth(0, 15000)

        cell = rowHead.createCell((int) 1)
        cell.setCellValue("Usuario")
        sheet.setColumnWidth(1, 10000)

        cell = rowHead.createCell((int) 2)
        cell.setCellValue("N. trámites")
        sheet.setColumnWidth(2, 3000)
        index++

        def dep = Departamento.get(params.id.toLong())
        def hijosDep = reportesPdfService.todosDep(dep)
        def tramites = [:]

        trams.each { tr ->
            def pers = tr.de
            def dpto = tr.departamento
            def deDpto = tr.deDepartamento

            if (!tramites[dpto.id]) {
                tramites[dpto.id] = [:]
                tramites[dpto.id].departamento = dpto
                tramites[dpto.id].personas = [:]
            }
            if (deDpto) {
                if (!tramites[dpto.id].personas[pers.id + "_o"]) {
                    tramites[dpto.id].personas[pers.id + "_o"] = [:]
                    tramites[dpto.id].personas[pers.id + "_o"].tramites = 0
                    tramites[dpto.id].personas[pers.id + "_o"].persona = pers
                    tramites[dpto.id].personas[pers.id + "_o"].de = "${pers.nombre} ${pers.apellido} (Oficina)"
                    if (!pers.estaActivo) {
                        tramites[dpto.id].personas[pers.id + "_o"].de += " <Inactivo>"
                    }
                    tramites[dpto.id].personas[pers.id + "_o"].deGraf = "${pers.login} (Oficina)"
                }
                tramites[dpto.id].personas[pers.id + "_o"].tramites++
            } else {
                if (!tramites[dpto.id].personas[pers.id]) {
                    tramites[dpto.id].personas[pers.id] = [:]
                    tramites[dpto.id].personas[pers.id].tramites = 0
                    tramites[dpto.id].personas[pers.id].persona = pers
                    tramites[dpto.id].personas[pers.id].de = "${pers.nombre} ${pers.apellido} (${pers.login})"
                    if (!pers.estaActivo) {
                        tramites[dpto.id].personas[pers.id].de += " <Inactivo>"
                    }
                    tramites[dpto.id].personas[pers.id].deGraf = "${pers.login}"
                }
                tramites[dpto.id].personas[pers.id].tramites++
            }
        }

        def granTotal = 0
        tramites.each { depId, depMap ->
            def depTotal = 0
            depMap.personas = depMap.personas.sort { it.value.de }
            depMap.personas.each { persId, persMap ->
                XSSFRow row = sheet.createRow((short) index)
                row.createCell((int) 0).setCellValue("${depMap.departamento.descripcion} (${depMap.departamento.codigo})")
                row.createCell((int) 1).setCellValue("${persMap.de}")
                row.createCell((int) 2).setCellValue(persMap.tramites)
                index++
                depTotal += persMap.tramites
            }
            if (params.tipo == "dpto") {
                XSSFRow row = sheet.createRow((short) index)
                row.createCell((int) 0).setCellValue("${depMap.departamento.descripcion} (${depMap.departamento.codigo})")
                row.createCell((int) 1).setCellValue("TOTAL")
                row.createCell((int) 2).setCellValue(depTotal)
                index++
            }
            granTotal += depTotal
        }
        XSSFRow row = sheet.createRow((short) index + 2)
        row.createCell((int) 0).setCellValue("GRAN TOTAL ${title2}")
        row.createCell((int) 2).setCellValue(granTotal)
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

    def reporteDetalladoXlsx() {

        def desde = new Date().parse("dd-MM-yyyy HH:mm", params.desde + " 00:00")
        def hasta = new Date().parse("dd-MM-yyyy HH:mm", params.hasta + " 23:59")
        def fileName = "detalle_documentos_generados_"
        def title = ["Reporte de documentos generados"]
        def title2 = ""
        def tramites = [:], trams = []

        if (params.tipo == "prsn") {
            def pers = Persona.get(params.id.toLong())
            def dpto = Departamento.get(params.dpto)
            if (!dpto) {
                dpto = pers.departamento
            }
            fileName += pers.login + "_" + dpto.codigo
            title += ["${pers.nombre} ${pers.apellido}"]
            title += ["entre el ${params.desde} y el ${params.hasta}"]
            title2 += "${pers.nombre} ${pers.apellido} en ${dpto.descripcion}"
            trams = Tramite.withCriteria {
                eq("de", pers)
                eq("departamento", dpto)
                ge("fechaCreacion", desde)
                le("fechaCreacion", hasta)
                order("fechaCreacion", "asc")
            }

        } else if (params.tipo == "dpto") {
            def dep = Departamento.get(params.id.toLong())
            def hijosDep = reportesPdfService.todosDep(dep)
            fileName += dep.codigo
            title += ["${dep.descripcion}"]
            title += ["entre el ${params.desde} y el ${params.hasta}"]
            title2 += dep.descripcion
            trams = Tramite.withCriteria {
                inList("departamento", hijosDep)
                ge("fechaCreacion", desde)
                le("fechaCreacion", hasta)
                order("departamento", "asc")
                order("fechaCreacion", "asc")
            }
        }

        trams.each { tr ->
            def pers = tr.de
            def dpto = tr.departamento
            def deDpto = tr.deDepartamento

            if (!tramites[dpto.id]) {
                tramites[dpto.id] = [:]
                tramites[dpto.id].departamento = dpto
                tramites[dpto.id].personas = [:]
            }
            if (deDpto) {
                if (!tramites[dpto.id].personas[pers.id + "_o"]) {
                    tramites[dpto.id].personas[pers.id + "_o"] = [:]
                    tramites[dpto.id].personas[pers.id + "_o"].tramites = []
                    tramites[dpto.id].personas[pers.id + "_o"].persona = pers
                    tramites[dpto.id].personas[pers.id + "_o"].de = "${pers.nombre} ${pers.apellido} (Oficina)"
                    if (!pers.estaActivo) {
                        tramites[dpto.id].personas[pers.id + "_o"].de += " <Inactivo>"
                    }
                    tramites[dpto.id].personas[pers.id + "_o"].deGraf = "${pers.login} (Oficina)"
                }
                tramites[dpto.id].personas[pers.id + "_o"].tramites += tr
            } else {
                if (!tramites[dpto.id].personas[pers.id]) {
                    tramites[dpto.id].personas[pers.id] = [:]
                    tramites[dpto.id].personas[pers.id].tramites = []
                    tramites[dpto.id].personas[pers.id].persona = pers
                    tramites[dpto.id].personas[pers.id].de = "${pers.nombre} ${pers.apellido} (${pers.login})"
                    if (!pers.estaActivo) {
                        tramites[dpto.id].personas[pers.id].de += " <Inactivo>"
                    }
                    tramites[dpto.id].personas[pers.id].deGraf = "${pers.login}"
                }
                tramites[dpto.id].personas[pers.id].tramites += tr
            }
        }
        def downloadName = fileName + "_" + new Date().format("ddMMyyyy_hhmm") + ".xlsx";

        def path = servletContext.getRealPath("/") + "xls/"
        new File(path).mkdirs()
        //esto crea un archivo temporal que puede ser siempre el mismo para no ocupar espacio
        String filename = path + "text.xlsx";
        String sheetName = "Resumen";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName);
        CreationHelper createHelper = wb.getCreationHelper();

        CellStyle styleDate = wb.createCellStyle();
        styleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy HH:mm"));

        sheet.setAutobreaks(true);

        XSSFRow rowTitle = sheet.createRow((short) 0);
        Cell cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue("GAD DE LA PROVINCIA DE PICHINCHA");
        rowTitle = sheet.createRow((short) 1);
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue("SISTEMA DE ADMINISTRACION DOCUMENTAL");
        rowTitle = sheet.createRow((short) 2);
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue(title[0]);
        rowTitle = sheet.createRow((short) 3);
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue(title[1]);
        rowTitle = sheet.createRow((short) 4);
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue(title[2]);

        def index = 6
        XSSFRow rowHead = sheet.createRow((short) index);
        rowHead.setHeightInPoints(14)

        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCopia = RolPersonaTramite.findByCodigo("R002")

        def granTotal = 0
        XSSFRow row
        Cell cell
        def wFechas = 4000
        row = sheet.createRow((short) index);
        cell = row.createCell((int) 0)
        cell.setCellValue("Dep.")
        cell = row.createCell((int) 1)
        cell.setCellValue("Usuario")
        cell = row.createCell((int) 2)
        cell.setCellValue("No.")
        sheet.setColumnWidth(2, 4000)
        cell = row.createCell((int) 3)
        cell.setCellValue("Fecha creación")
        sheet.setColumnWidth(3, wFechas)

        cell = row.createCell((int) 4)
        cell.setCellValue("Para oficina")
        sheet.setColumnWidth(4, 15000)

        cell = row.createCell((int) 5)
        cell.setCellValue("Destinatario")
        sheet.setColumnWidth(5, 10000)

        cell = row.createCell((int) 6)
        cell.setCellValue("Fecha envío")
        sheet.setColumnWidth(6, wFechas)

        cell = row.createCell((int) 7)
        cell.setCellValue("Fecha recepción")
        sheet.setColumnWidth(7, wFechas)
        index++
        tramites.each { depId, depMap ->
            def totalDep = 0
            def dep = depMap.departamento.codigo
            depMap.personas.each { persId, persMap ->
                def usu = persMap.de
                totalDep += persMap.tramites.size()
                persMap.tramites.eachWithIndex { tr, j ->
                    def prtr = PersonaDocumentoTramite.withCriteria {
                        eq("tramite", tr)
                        inList("rolPersonaTramite", [rolPara, rolCopia])
                        order("fechaEnvio", "asc")
                        order("rolPersonaTramite", "asc")
                    }
                    prtr.each { persDoc ->
                        def paraOficina = persDoc.persona ? (persDoc.persona.departamento.descripcion + " (" + persDoc.persona.departamento.codigo + ")") : (persDoc.departamento.descripcion + " (" + persDoc.departamento.codigo + ")")
                        def para = persDoc.persona ? (persDoc.persona.nombre + " " + persDoc.persona.apellido + " (" + persDoc.persona.login + ")") : persDoc.departamento.codigo
                        def cod = tr.codigo + (persDoc.rolPersonaTramite.codigo == "R002" ? "  [CC]" : "")
                        index++
                        row = sheet.createRow((short) index);
                        cell = row.createCell((int) 0)
                        cell.setCellValue(dep)
                        cell = row.createCell((int) 1)
                        cell.setCellValue(usu)
                        row.createCell((int) 2).setCellValue(cod)

                        cell = row.createCell((int) 3)
                        cell.setCellValue(tr.fechaCreacion)
                        cell.setCellStyle(styleDate)

                        row.createCell((int) 4).setCellValue(paraOficina)
                        row.createCell((int) 5).setCellValue(para)

                        cell = row.createCell((int) 6)
                        cell.setCellValue(persDoc.fechaEnvio)
                        cell.setCellStyle(styleDate)

                        cell = row.createCell((int) 7)
                        cell.setCellValue(persDoc.fechaRecepcion)
                        cell.setCellStyle(styleDate)
                    }
                }
            }
            if (params.tipo == "dpto") {
                row = sheet.createRow((short) index)
                row.createCell((int) 0).setCellValue("${depMap.departamento.descripcion} (${depMap.departamento.codigo})")
                row.createCell((int) 4).setCellValue("TOTAL")
                row.createCell((int) 5).setCellValue(totalDep)
                index++
            }
            granTotal += totalDep
        }

        row = sheet.createRow((short) index + 1)
        row.createCell((int) 0).setCellValue("GRAN TOTAL ${title2}")
        row.createCell((int) 5).setCellValue(granTotal)

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
