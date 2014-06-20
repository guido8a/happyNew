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
import happy.tramites.EstadoTramite
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite

import java.awt.Color


class ReporteGestionController extends happy.seguridad.Shield {

    def index() {}

    def dbConnectionService
    def reportesPdfService


    def reporteGestion () {

        println("params " + params)


        def persona = Persona.get(params.id)

        def sql = ''
        def result = []
        def cn = dbConnectionService.getConnection();
        def tramite
        def tramiteContes
        def prtr
        def prtrContes
        def principal

        def desde = new Date().parse("dd-MM-yyyy", params.desde)
        def hasta= new Date().parse("dd-MM-yyyy", params.hasta)
        def fecha = desde.format("dd/MM/yyyy")
        def fecha2 = hasta.format("dd/MM/yyyy")

//        sql = 'select prtr__id from prtr where prsn__id = (' + params.id + ') and (rltr__id=1 or rltr__id=2)'

//        sql = 'select i.prtr__id\n' +
//              'FROM prtr i INNER JOIN trmt j ON i.trmt__id = j.trmt__id \n'+
//               'WHERE (i.prsn__id = (' + params.id + ' ) and (i.rltr__id=1 or i.rltr__id=2)) and (j.trmtfccr >= (' + fecha + ') and j.trmtfccr <= (' + fecha2 + '))'
//
        sql = 'select i.prtr__id\n' +
                'FROM prtr i INNER JOIN trmt j ON i.trmt__id = j.trmt__id \n'+
                'WHERE (i.prsn__id = (' + params.id + ' ) and (i.rltr__id=1 or i.rltr__id=2)) and (j.trmtfccr between  ' + "'" + fecha + "'"  + '  and  ' + "'" + fecha2 + "'" + ' )'


//        println("sql " + sql)

        cn.eachRow(sql) { r ->
//            println(">>>>>" + r)
            result.add(r.toRowResult())
        }

//        println("result" + result)

        result.each {
            tramite = PersonaDocumentoTramite.get(it.prtr__id).tramite
//            tramiteContes = PersonaDocumentoTramite.get(it.prtr__id).tramite.aQuienContesta.tramite
            if (tramite) {
               principal = tramite
                if (tramite.padre) {
                    principal = tramite.padre
                    while (true) {
                        if (!principal.padre)
                            break
                        else {
                            principal = principal.padre
                        }
                    }
                }

            }
        }




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

        com.lowagie.text.Document document
        document = new com.lowagie.text.Document(PageSize.A4);
        def pdfw = PdfWriter.getInstance(document, baos);
        HeaderFooter footer1 = new HeaderFooter(new Phrase('', times8normal), true);
        footer1.setBorder(Rectangle.NO_BORDER);
        footer1.setAlignment(Element.ALIGN_CENTER);
        document.setFooter(footer1);
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


        result.each {
            tramite = PersonaDocumentoTramite.get(it.prtr__id).tramite
            tramiteContes = PersonaDocumentoTramite.get(it.prtr__id)?.tramite?.aQuienContesta?.tramite
            prtrContes = PersonaDocumentoTramite.get(it.prtr__id).tramite.aQuienContesta
            prtr = PersonaDocumentoTramite.get(it.prtr__id)
            if (tramite) {
                principal = tramite
                if (tramite.padre) {
                    principal = tramite.padre
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
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
//            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
//            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)


            addCellTabla(tablaTramites, new Paragraph("TRÁMITE N°.", times8bold), prmsHeaderHoja2)
            addCellTabla(tablaTramites, new Paragraph("FECHA DE RECEPCIÓN", times8bold), prmsHeaderHoja2)
            addCellTabla(tablaTramites, new Paragraph("DE OFICINA", times8bold), prmsHeaderHoja2)
            addCellTabla(tablaTramites, new Paragraph("CREADO POR", times8bold), prmsHeaderHoja2)
            addCellTabla(tablaTramites, new Paragraph("ASUNTO", times8bold), prmsHeaderHoja4)


            addCellTabla(tablaTramites, new Paragraph(tramite?.codigo, times8normal), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph(prtr?.fechaRecepcion?.format("dd-MM-yyyy"), times8normal), prmsHeaderHoja)
            if(tramite?.deDepartamento){
                addCellTabla(tablaTramites, new Paragraph(tramite?.deDepartamento?.descripcion, times8normal), prmsHeaderHoja)
            }else{
                addCellTabla(tablaTramites, new Paragraph((tramite?.de?.nombre + ' ' + tramite?.de?.apellido) ?: '', times8normal), prmsHeaderHoja)
            }
            addCellTabla(tablaTramites, new Paragraph(tramite?.de?.login, times8normal), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph(tramite?.asunto, times8normal), prmsHeaderHoja5)


            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)

            addCellTabla(tablaTramites, new Paragraph("TRÁMITE CONTESTA N°.", times8bold), prmsHeaderHoja2)
            addCellTabla(tablaTramites, new Paragraph("FECHA CREACIÓN", times8bold), prmsHeaderHoja2)
            addCellTabla(tablaTramites, new Paragraph("DE OFICINA", times8bold), prmsHeaderHoja2)
            addCellTabla(tablaTramites, new Paragraph("CREADO POR", times8bold), prmsHeaderHoja2)
            addCellTabla(tablaTramites, new Paragraph("ASUNTO", times8bold), prmsHeaderHoja2)
            addCellTabla(tablaTramites, new Paragraph("FECHA DE ENVIO", times8bold), prmsHeaderHoja2)

            addCellTabla(tablaTramites, new Paragraph(tramiteContes?.codigo, times8normal), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph(tramiteContes?.fechaCreacion?.format("dd-MM-yyyy"), times8normal), prmsHeaderHoja)
            if(tramite?.deDepartamento){
                addCellTabla(tablaTramites, new Paragraph(tramiteContes?.deDepartamento?.descripcion, times8normal), prmsHeaderHoja)
            }else{
                addCellTabla(tablaTramites, new Paragraph((tramiteContes?.de?.nombre + ' ' + tramiteContes?.de?.apellido) ?: '', times8normal), prmsHeaderHoja)
            }
            addCellTabla(tablaTramites, new Paragraph(tramiteContes?.de?.login, times8normal), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph(tramiteContes?.asunto, times8normal), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph(prtrContes?.fechaEnvio?.format("dd-MM-yyyy"), times8normal), prmsHeaderHoja)


            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
//            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
//            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)


            addCellTabla(tablaTramites, new Paragraph("Días transcurridos hasta contestación: ", times8bold), prmsHeaderHoja4)


            if(prtrContes?.fechaEnvio && prtr?.fechaRecepcion){
                addCellTabla(tablaTramites, new Paragraph(g.formatNumber(number: (prtr?.fechaRecepcion - prtrContes?.fechaEnvio), format: "###.##", locale: "ec") + " Días",times8bold), prmsHeaderHoja7)
            }else{
                addCellTabla(tablaTramites, new Paragraph('', times8bold), prmsHeaderHoja7)
            }


            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)

        }


        document.add(tablaTramites)



        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)

//        println("-->" + result + "us " + session.usuario.id)




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
