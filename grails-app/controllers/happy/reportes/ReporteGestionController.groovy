package happy.reportes

import com.lowagie.text.Document
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
import happy.tramites.EstadoTramite
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite
import org.apache.poi.hssf.usermodel.HSSFFont
import org.apache.poi.hssf.util.CellRangeAddress
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import java.awt.Color


class ReporteGestionController extends happy.seguridad.Shield {

    def index() {}

    def dbConnectionService
    def reportesPdfService
    def diasLaborablesService


    def reporteGestion () {

        println("params " + params)


        def persona = Persona.get(params.id)

        println("triangulo " + Persona.get(params.id).esTriangulo())

        def sql = ''
        def result = []
        def cn = dbConnectionService.getConnection();
        def tramite2
        def tramiteContes
        def prtr
        def prtrContes
        def principal

        def desde = new Date().parse("dd-MM-yyyy", params.desde)
        def hasta= new Date().parse("dd-MM-yyyy", params.hasta)
        def fecha = desde.format("dd/MM/yyyy")
        def fecha2 = hasta.format("dd/MM/yyyy")


        if(Persona.get(params.id).esTriangulo()){

            result = PersonaDocumentoTramite.withCriteria {
//                isNotNull('fechaEnvio')
                eq("departamento", Persona.get(params.id)?.departamento)
                or{
                    eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R001'))
                    eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R002'))
                }
                tramite {
                    ge('fechaCreacion', desde)
                    le('fechaCreacion', hasta)
                }
            }

        }else{
            result = PersonaDocumentoTramite.withCriteria {
//                isNotNull('fechaEnvio')
                eq("persona", Persona.get(params.id))
                or{
                    eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R001'))
                    eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R002'))
                }

                tramite {
                    ge('fechaCreacion', desde)
                    le('fechaCreacion', hasta)
                }
            }

        }





//        println("sql " + sql)

//        cn.eachRow(sql) { r ->
//            result.add(r.toRowResult())
//        }

//        println("result" + result)


//        def tramiteC
//
//
//        result.each {
//            tramite = PersonaDocumentoTramite.get(it.prtr__id).tramite
//            tramiteC = Tramite.findAllByPadreAndEstadoTramiteNotEqual(tramite,EstadoTramite.findByCodigo('E006'))
//
//            if (tramite) {
//               principal = tramite
//                if (tramite.padre) {
//                    principal = tramite.padre
//                    while (true) {
//                        if (!principal.padre)
//                            break
//                        else {
//                            principal = principal.padre
//                        }
//                    }
//                }
//
//            }
//        }


        println("result" + result)

        //pdf

        def baos = new ByteArrayOutputStream()
        def name = "gestion_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times18bold = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeaderHoja6 = [border: Color.WHITE, colspan: 6]
        def prmsHeaderHoja1 = [border: Color.WHITE, bordeTop: "1", bordeBot: "1"]
        def prmsHeaderHoja2 = [border: Color.WHITE, bordeBot: "1"]
        def prmsHeaderHoja3 = [border: Color.WHITE, bordeBot: "1", colspan: 3]
        def prmsHeaderHoja4 = [border: Color.WHITE, bordeBot: "1", colspan: 2]
        def prmsHeaderHoja7 = [border: Color.WHITE, bordeBot: "1", colspan: 4]
        def prmsHeaderHoja5 = [border: Color.WHITE, colspan: 2]
        times8boldWhite.setColor(Color.WHITE)
        times10boldWhite.setColor(Color.WHITE)
        def fonts = [times12bold     : times12bold, times10bold: times10bold, times8bold: times8bold,
                times10boldWhite: times10boldWhite, times8boldWhite: times8boldWhite, times8normal: times8normal, times18bold: times18bold]

//        com.lowagie.text.Document document
//        document = new com.lowagie.text.Document(PageSize.A4);
        Document document = reportesPdfService.crearDocumento([top: 2.5, right: 2.5, bottom: 1.5, left: 3])
        def pdfw = PdfWriter.getInstance(document, baos);
//        HeaderFooter footer1 = new HeaderFooter(new Phrase('', times8normal), true);
//        footer1.setBorder(Rectangle.NO_BORDER);
//        footer1.setAlignment(Element.ALIGN_CENTER);
////        document.setFooter(footer1);
        reportesPdfService.membrete(document)
        document.open();

        reportesPdfService.crearEncabezado(document, "REPORTE DE GESTIÓN DE TRÁMITES DEL USUARIO:  ${persona}")

        PdfPTable tablaTramites = new PdfPTable(6);
        tablaTramites.setWidthPercentage(100);
        tablaTramites.setWidths(arregloEnteros([15, 15, 15, 15, 15, 15]))

        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)


        def filtrado

        result.each {
            tramite2 = PersonaDocumentoTramite.get(it.id).tramite
            prtr = PersonaDocumentoTramite.get(it.id)

            tramiteContes = Tramite.findAllByPadreAndEstadoTramiteNotEqual(tramite2,EstadoTramite.findByCodigo('E006'))

//
//            println("tramite " + tramite2)
//            println("-->" + tramiteContes)
//
//
//            tramiteContes.each {
//
//             filtrado = PersonaDocumentoTramite.findByTramiteAndRolPersonaTramite(it,RolPersonaTramite.findByCodigo('E004'))
//
//                println("filtrado "  + filtrado)
//
//            }


            if (tramite2) {
                principal = tramite2
                if (tramite2.padre) {
                    principal = tramite2.padre
                    while (true) {
                        if (!principal.padre)
                            break
                        else {
                            principal = principal.padre
                        }
                    }
                }
            }

            addCellTabla(tablaTramites, new Paragraph("DOC PRINCIPAL :", times8bold), prmsHeaderHoja2)
            addCellTabla(tablaTramites, new Paragraph(Tramite.get(principal?.id).codigo, times8normal), prmsHeaderHoja2)
            addCellTabla(tablaTramites, new Paragraph("ASUNTO :", times8bold), prmsHeaderHoja2)
            addCellTabla(tablaTramites, new Paragraph(Tramite.get(principal?.id).asunto, times8normal), prmsHeaderHoja3)

            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)

            addCellTabla(tablaTramites, new Paragraph("TRÁMITE N°.", times8bold), prmsHeaderHoja2)
            addCellTabla(tablaTramites, new Paragraph("FECHA DE RECEPCIÓN", times8bold), prmsHeaderHoja2)
            addCellTabla(tablaTramites, new Paragraph("DE OFICINA", times8bold), prmsHeaderHoja2)
            addCellTabla(tablaTramites, new Paragraph("CREADO POR", times8bold), prmsHeaderHoja2)
            addCellTabla(tablaTramites, new Paragraph("ASUNTO", times8bold), prmsHeaderHoja4)

            addCellTabla(tablaTramites, new Paragraph(tramite2?.codigo, times8normal), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph(prtr?.fechaRecepcion?.format("dd-MM-yyyy"), times8normal), prmsHeaderHoja)
            if(tramite2?.deDepartamento){
                addCellTabla(tablaTramites, new Paragraph(tramite2?.deDepartamento?.descripcion, times8normal), prmsHeaderHoja)
            }else{
                addCellTabla(tablaTramites, new Paragraph((tramite2?.de?.nombre + ' ' + tramite2?.de?.apellido) ?: '', times8normal), prmsHeaderHoja)
            }
            addCellTabla(tablaTramites, new Paragraph(tramite2?.de?.login, times8normal), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph(tramite2?.asunto, times8normal), prmsHeaderHoja5)


            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)



            if(tramiteContes){

                tramiteContes.each {

                    filtrado = PersonaDocumentoTramite.findByTramiteAndRolPersonaTramite(it,RolPersonaTramite.findByCodigo('E004'))

                    addCellTabla(tablaTramites, new Paragraph("TRÁMITE CONTESTA N°.", times8bold), prmsHeaderHoja2)
                    addCellTabla(tablaTramites, new Paragraph("FECHA CREACIÓN", times8bold), prmsHeaderHoja2)
                    addCellTabla(tablaTramites, new Paragraph("DE OFICINA", times8bold), prmsHeaderHoja2)
                    addCellTabla(tablaTramites, new Paragraph("CREADO POR", times8bold), prmsHeaderHoja2)
                    addCellTabla(tablaTramites, new Paragraph("ASUNTO", times8bold), prmsHeaderHoja2)
                    addCellTabla(tablaTramites, new Paragraph("FECHA DE ENVIO", times8bold), prmsHeaderHoja2)

                    addCellTabla(tablaTramites, new Paragraph(it?.codigo, times8normal), prmsHeaderHoja)
                    addCellTabla(tablaTramites, new Paragraph(it?.fechaCreacion?.format("dd-MM-yyyy"), times8normal), prmsHeaderHoja)
                    if(it?.deDepartamento){
                        addCellTabla(tablaTramites, new Paragraph((it?.deDepartamento?.descripcion ?: ''), times8normal), prmsHeaderHoja)
                    }else{
                        addCellTabla(tablaTramites, new Paragraph((it?.de?.nombre ?: '') + ' ' + (it?.de?.apellido ?: ''), times8normal), prmsHeaderHoja)
                    }
                    addCellTabla(tablaTramites, new Paragraph(it?.de?.login, times8normal), prmsHeaderHoja)
                    addCellTabla(tablaTramites, new Paragraph(it?.asunto, times8normal), prmsHeaderHoja)
                    addCellTabla(tablaTramites, new Paragraph(filtrado?.fechaEnvio?.format("dd-MM-yyyy"), times8normal), prmsHeaderHoja)
//                    addCellTabla(tablaTramites, new Paragraph('', times8normal), prmsHeaderHoja)

                    addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                    addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)

                    addCellTabla(tablaTramites, new Paragraph("Días transcurridos hasta contestación: ", times8bold), prmsHeaderHoja4)


                if(it?.fechaCreacion  && prtr?.fechaRecepcion){
//                    addCellTabla(tablaTramites, new Paragraph(g.formatNumber(number: (prtr?.fechaRecepcion - filtrado?.fechaEnvio), format: "###.##", locale: "ec") + " Días",times8bold), prmsHeaderHoja7)

                   def diasTrans = diasLaborablesService.diasLaborablesEntre((prtr?.fechaRecepcion).clearTime(), (it?.fechaCreacion).clearTime())
                   def diasC = 0
                    if(diasTrans[0]){
                        diasC = diasTrans[1]
                    }else{
                        println("error dias " +  diasTrans[1])
                    }

                    addCellTabla(tablaTramites, new Paragraph(g.formatNumber(number: diasC, format: "###.##", locale: "ec") + " Día${diasC == 1 ?'': 's'}",times8bold), prmsHeaderHoja7)
                }else{
                    if(prtr?.fechaRecepcion){
                        def diasCero1 = diasLaborablesService.diasLaborablesEntre((new Date()).clearTime(), (prtr?.fechaRecepcion).clearTime())
                        def diasS1 = 0
                        if(diasCero1[0]){
                            diasS1 = diasCero1[1]
                        }else{
                            println("error dias " +  diasCero1[1])
                        }

                        addCellTabla(tablaTramites, new Paragraph(g.formatNumber(number: diasS1, format: "###.##", locale: "ec") + " Día${diasS1 == 1 ?'': 's'}",times8bold), prmsHeaderHoja7)
                    }else{
                        addCellTabla(tablaTramites, new Paragraph('0 Días', times8bold), prmsHeaderHoja7)
                    }
                }
                    addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                    addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                    addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                    addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                    addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)

                }

            }else{

                addCellTabla(tablaTramites, new Paragraph("TRÁMITE CONTESTA N°.", times8bold), prmsHeaderHoja2)
                addCellTabla(tablaTramites, new Paragraph("FECHA CREACIÓN", times8bold), prmsHeaderHoja2)
                addCellTabla(tablaTramites, new Paragraph("DE OFICINA", times8bold), prmsHeaderHoja2)
                addCellTabla(tablaTramites, new Paragraph("CREADO POR", times8bold), prmsHeaderHoja2)
                addCellTabla(tablaTramites, new Paragraph("ASUNTO", times8bold), prmsHeaderHoja2)
                addCellTabla(tablaTramites, new Paragraph("FECHA DE ENVIO", times8bold), prmsHeaderHoja2)

                addCellTabla(tablaTramites, new Paragraph('', times8normal), prmsHeaderHoja)
                addCellTabla(tablaTramites, new Paragraph('', times8normal), prmsHeaderHoja)
                addCellTabla(tablaTramites, new Paragraph('', times8normal), prmsHeaderHoja)
                addCellTabla(tablaTramites, new Paragraph('', times8normal), prmsHeaderHoja)
                addCellTabla(tablaTramites, new Paragraph('', times8normal), prmsHeaderHoja)
                addCellTabla(tablaTramites, new Paragraph('', times8normal), prmsHeaderHoja)

                addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)

                addCellTabla(tablaTramites, new Paragraph("Días transcurridos hasta contestación: ", times8bold), prmsHeaderHoja4)
                if(prtr?.fechaRecepcion){
                    def diasCero = diasLaborablesService.diasLaborablesEntre((new Date()).clearTime(), (prtr?.fechaRecepcion).clearTime())
                    def diasS = 0
                    if(diasCero[0]){
                        diasS = diasCero[1]
                    }else{
                        println("error dias " +  diasCero[1])
                    }

                    addCellTabla(tablaTramites, new Paragraph(g.formatNumber(number: diasS, format: "###.##", locale: "ec") + " Día${diasS == 1 ?'': 's'}",times8bold), prmsHeaderHoja7)
                }else{
                    addCellTabla(tablaTramites, new Paragraph('0 Días', times8bold), prmsHeaderHoja7)
                }


                addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
            }

        }

        document.add(tablaTramites)
        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }


    def reporteGestionXlsx () {


        def downloadName = "reporteGestion" + "_" + new Date().format("ddMMyyyy_hhmm") + ".xlsx";

        def path = servletContext.getRealPath("/") + "xls/"
        new File(path).mkdirs()

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

        org.apache.poi.ss.usermodel.Font fontTitle2 = wb.createFont();
        fontTitle2.setFontHeightInPoints((short) 16);
        fontTitle2.setFontName(HSSFFont.FONT_ARIAL);
        fontTitle2.setItalic(true);
        fontTitle2.setBold(true);
//        fontTitle2.setColor(HSSFColor.DARK_RED.index);

        CellStyle styleTitle = wb.createCellStyle();
        styleTitle.setAlignment(CellStyle.ALIGN_CENTER);
        styleTitle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        styleTitle.setFont(fontTitle2)
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
        rowTitle.setHeightInPoints(20)

        Cell cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue('Reporte Gestión');
        cellTitle.setCellStyle(styleTitle)
        sheet.setColumnWidth(0,6000)

        XSSFRow rowTitle2 = sheet.createRow((short) 1);
        rowTitle2.createCell((int) 0).setCellValue('Usuario: ' + ' ' + "${Persona?.get(params.id)}" )
//        rowTitle2.setRowStyle(styleTitle)

        def persona = Persona.get(params.id)
        def sql = ''
        def result = []
        def cn = dbConnectionService.getConnection();
        def tramite2
        def tramiteContes
        def prtr
        def prtrContes
        def principal

        def desde = new Date().parse("dd-MM-yyyy", params.desde)
        def hasta= new Date().parse("dd-MM-yyyy", params.hasta)
        def fecha = desde.format("dd/MM/yyyy")
        def fecha2 = hasta.format("dd/MM/yyyy")

        if(Persona.get(params.id).esTriangulo()){

            result = PersonaDocumentoTramite.withCriteria {
//                isNotNull('fechaEnvio')
                eq("departamento", Persona.get(params.id)?.departamento)
                or{
                    eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R001'))
                    eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R002'))
                }
                tramite {
                    ge('fechaCreacion', desde)
                    le('fechaCreacion', hasta)
                }
            }

        }else{
            result = PersonaDocumentoTramite.withCriteria {
//                isNotNull('fechaEnvio')
                eq("persona", Persona.get(params.id))
                or{
                    eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R001'))
                    eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R002'))
                }

                tramite {
                    ge('fechaCreacion', desde)
                    le('fechaCreacion', hasta)
                }
            }

        }



        def indexHeadP = 3
        def indexP = 4

        def indexHead = 5
        def index = 6

        def indexC = 7
        def indexHeadC = 8

        def indexD = 9
        def indexHeadD = 9

        def filtrado

        result.each {

            tramite2 = PersonaDocumentoTramite.get(it.id).tramite
            prtr = PersonaDocumentoTramite.get(it.id)
            tramiteContes = Tramite.findAllByPadreAndEstadoTramiteNotEqual(tramite2,EstadoTramite.findByCodigo('E006'))


            if (tramite2) {
                principal = tramite2
                if (tramite2.padre) {
                    principal = tramite2.padre
                    while (true) {
                        if (!principal.padre)
                            break
                        else {
                            principal = principal.padre
                        }
                    }
                }
            }

            //trámite principal

            XSSFRow rowHeadP = sheet.createRow((short) indexHeadP);
            rowHeadP.setHeightInPoints(14)

            Cell cellP = rowHeadP.createCell((int) 0)
            cellP.setCellValue("Doc. Principal")
//            cellP.setCellStyle(styleHeaders)
            sheet.setColumnWidth(0, 6000)

            cellP = rowHeadP.createCell((int) 1)
            cellP.setCellValue("Asunto")
//            cellP.setCellStyle(styleHeaders)
            sheet.setColumnWidth(1, 6000)

            XSSFRow rowP = sheet.createRow((short) indexP)

            rowP.createCell((int) 0).setCellValue("${Tramite.get(principal?.id).codigo}")
            rowP.createCell((int) 1).setCellValue("${Tramite.get(principal?.id).asunto}")

            //trámite
            XSSFRow rowHead = sheet.createRow((short) indexHead);
            rowHead.setHeightInPoints(14)

            Cell cell = rowHead.createCell((int) 0)
            cell.setCellValue("Trámite N°.")
//            cell.setCellStyle(styleHeaders)
            sheet.setColumnWidth(0, 12000)

            cell = rowHead.createCell((int) 1)
            cell.setCellValue("Fecha de Recepción")
//            cell.setCellStyle(styleHeaders)
            sheet.setColumnWidth(1, 6000)

            cell = rowHead.createCell((int) 2)
            cell.setCellValue("De oficina")
//            cell.setCellStyle(styleHeaders)
            sheet.setColumnWidth(2, 10000)

            cell = rowHead.createCell((int) 3)
            cell.setCellValue("Creado Por")
//            cell.setCellStyle(styleHeaders)
            sheet.setColumnWidth(3, 5000)

            cell = rowHead.createCell((int) 4)
            cell.setCellValue("Asunto")
//            cell.setCellStyle(styleHeaders)
            sheet.setColumnWidth(4, 5000)

            cell = rowHead.createCell((int) 5)
            cell.setCellValue("")
//            cell.setCellStyle(styleHeaders)
            sheet.setColumnWidth(5, 5000)

            XSSFRow row = sheet.createRow((short) index)

            row.createCell((int) 0).setCellValue("${tramite2?.codigo}")

            row.createCell((int) 1).setCellValue("${prtr?.fechaRecepcion?.format("dd-MM-yyyy") ?: ' '}")
            if(tramite2?.deDepartamento){
                row.createCell((int) 2).setCellValue("${tramite2?.deDepartamento}")
            }else{
                row.createCell((int) 2).setCellValue("${tramite2?.de?.nombre + ' ' + tramite2?.de?.apellido}")
            }
            row.createCell((int) 3).setCellValue("${tramite2?.de?.login}")
            row.createCell((int) 4).setCellValue("${tramite2?.asunto}")
            row.createCell((int) 5).setCellValue("${''}")


            //trámite de contestación


            if(tramiteContes){

                tramiteContes.each{

                    filtrado = PersonaDocumentoTramite.findByTramiteAndRolPersonaTramite(it,RolPersonaTramite.findByCodigo('E004'))

                    XSSFRow rowHeadC = sheet.createRow((short) indexC);
                    rowHeadC.setHeightInPoints(14)

                    Cell cellC = rowHeadC.createCell((int) 0)
                    cellC.setCellValue("Trámite Contestación N°.")
//                    cellC.setCellStyle(styleHeaders)
                    sheet.setColumnWidth(0, 12000)

                    cellC = rowHeadC.createCell((int) 1)
                    cellC.setCellValue("Fecha de Creación")
//                    cellC.setCellStyle(styleHeaders)
                    sheet.setColumnWidth(1, 6000)

                    cellC = rowHeadC.createCell((int) 2)
                    cellC.setCellValue("De oficina")
//                    cellC.setCellStyle(styleHeaders)
                    sheet.setColumnWidth(2, 10000)

                    cellC = rowHeadC.createCell((int) 3)
                    cellC.setCellValue("Creado Por")
//                    cellC.setCellStyle(styleHeaders)
                    sheet.setColumnWidth(3, 5000)

                    cellC = rowHeadC.createCell((int) 4)
                    cellC.setCellValue("Asunto")
//                    cellC.setCellStyle(styleHeaders)
                    sheet.setColumnWidth(4, 5000)

                    cell = rowHeadC.createCell((int) 5)
                    cell.setCellValue("Fecha de Envio")
//                    cell.setCellStyle(styleHeaders)
                    sheet.setColumnWidth(5, 5000)


                    XSSFRow rowC = sheet.createRow((short) indexHeadC)

                    rowC.createCell((int) 0).setCellValue("${it?.codigo ?: ''}")

                    rowC.createCell((int) 1).setCellValue("${it?.fechaCreacion?.format("dd-MM-yyyy") ?: ''}")
                    if(it?.deDepartamento){
                        rowC.createCell((int) 2).setCellValue("${it?.deDepartamento ?: ''}")
                    }else{
                        rowC.createCell((int) 2).setCellValue("${(it?.de?.nombre ?: '') + ' ' + (it?.de?.apellido ?: '')}")
                    }
                    rowC.createCell((int) 3).setCellValue("${it?.de?.login ?: ''}")
                    rowC.createCell((int) 4).setCellValue("${it?.asunto ?: ''}")
                    rowC.createCell((int) 5).setCellValue("${filtrado?.fechaEnvio?.format('dd-MM-yyyy') ?: ''}")

                    //Días

                    XSSFRow rowHeadD = sheet.createRow((short) indexHeadD);
                    rowHeadD.setHeightInPoints(14)

                    Cell cellD = rowHeadD.createCell((int) 0)
                    cellD.setCellValue("Días transcurridos hasta la contestación")
//                    cellD.setCellStyle(styleHeaders)
                    sheet.setColumnWidth(0, 12000)

                    cellD = rowHeadD.createCell((int) 1)

                    if(it?.fechaCreacion && prtr?.fechaRecepcion){
                        def diasTrans = diasLaborablesService.diasLaborablesEntre((prtr?.fechaRecepcion).clearTime(), (it?.fechaCreacion).clearTime())
                        def diasC = 0
                        if(diasTrans[0]){
                            diasC = diasTrans[1]
                        }else{
                            println("error dias " +  diasTrans[1])
                        }
                        cellD.setCellValue("${diasC}")
                    }else{
                        if(prtr?.fechaRecepcion){
                            def diasTrans1 = diasLaborablesService.diasLaborablesEntre((new Date()).clearTime(), (prtr?.fechaRecepcion).clearTime())
                            def diasS = 0
                            if(diasTrans1[0]){
                                diasS = diasTrans1[1]
                            }else{
                                println("error dias " +  diasTrans1[1])
                            }
                            cellD.setCellValue("${diasS}")
                        }else{
                            cellD.setCellValue("0 Días")
                        }

                    }
//                    cellD.setCellStyle(styleHeaders)
                    sheet.setColumnWidth(1, 6000)

                    index= index + 9
                    indexHead = indexHead + 9

                    indexC= indexC + 9
                    indexHeadC = indexHeadC + 9

                    indexP= indexP + 9
                    indexHeadP = indexHeadP + 9

                    indexD= indexD + 9
                    indexHeadD = indexHeadD + 9
                }

            }else {
             XSSFRow rowHeadC = sheet.createRow((short) indexC);
                rowHeadC.setHeightInPoints(14)

                Cell cellC = rowHeadC.createCell((int) 0)
                cellC.setCellValue("Trámite Contestación N°.")
//                cellC.setCellStyle(styleHeaders)
                sheet.setColumnWidth(0, 12000)

                cellC = rowHeadC.createCell((int) 1)
                cellC.setCellValue("Fecha de Creación")
//                cellC.setCellStyle(styleHeaders)
                sheet.setColumnWidth(1, 6000)

                cellC = rowHeadC.createCell((int) 2)
                cellC.setCellValue("De oficina")
//                cellC.setCellStyle(styleHeaders)
                sheet.setColumnWidth(2, 10000)

                cellC = rowHeadC.createCell((int) 3)
                cellC.setCellValue("Creado Por")
//                cellC.setCellStyle(styleHeaders)
                sheet.setColumnWidth(3, 5000)

                cellC = rowHeadC.createCell((int) 4)
                cellC.setCellValue("Asunto")
//                cellC.setCellStyle(styleHeaders)
                sheet.setColumnWidth(4, 5000)

                cell = rowHeadC.createCell((int) 5)
                cell.setCellValue("Fecha de Envio")
//                cell.setCellStyle(styleHeaders)
                sheet.setColumnWidth(5, 5000)


                XSSFRow rowC = sheet.createRow((short) indexHeadC)

                rowC.createCell((int) 0).setCellValue("${''}")
                rowC.createCell((int) 1).setCellValue("${''}")
                rowC.createCell((int) 2).setCellValue("${''}")
                rowC.createCell((int) 3).setCellValue("${''}")
                rowC.createCell((int) 4).setCellValue("${''}")
                rowC.createCell((int) 5).setCellValue("${''}")

                //Días

                XSSFRow rowHeadD = sheet.createRow((short) indexHeadD);
                rowHeadD.setHeightInPoints(14)

                Cell cellD = rowHeadD.createCell((int) 0)
                cellD.setCellValue("Días transcurridos hasta la contestación")
//                cellD.setCellStyle(styleHeaders)
                sheet.setColumnWidth(0, 12000)

                cellD = rowHeadD.createCell((int) 1)


                if(prtr?.fechaRecepcion){
                    def diasTrans2 = diasLaborablesService.diasLaborablesEntre((new Date()).clearTime(), (prtr?.fechaRecepcion).clearTime())
                    def diasS1 = 0
                    if(diasTrans2[0]){
                        diasS1 = diasTrans2[1]
                    }else{
                        println("error dias " +  diasTrans2[1])
                    }
                    cellD.setCellValue("${diasS1}")
                }else{
                    cellD.setCellValue("0 Días")
                }

//                cellD.setCellValue("0 Días")

//                cellD.setCellStyle(styleHeaders)
                sheet.setColumnWidth(1, 6000)


                index= index + 9
                indexHead = indexHead + 9

                indexC= indexC + 9
                indexHeadC = indexHeadC + 9

                indexP= indexP + 9
                indexHeadP = indexHeadP + 9

                indexD= indexD + 9
                indexHeadD = indexHeadD + 9


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



    private String printFecha(Date fecha) {
        if (fecha) {
            return (fecha.format("dd") + ' de ' + meses[fecha.format("MM").toInteger()] + ' de ' + fecha.format("yyyy"))
        } else {
            return "Error: no hay fecha que mostrar"
        }
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
