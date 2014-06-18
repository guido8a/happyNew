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
        def fecha = desde.format("dd-MM-yyyy")
        def fecha2 = hasta.format("dd-MM-yyyy")

//        sql = 'select prtr__id from prtr where (prsn__id = (' + params.id + ') and (rltr__id=1 or rltr__id=2)) and ( between (' + fecha + ') and (' + fecha2 + ') )'
        sql = 'select prtr__id from prtr where prsn__id = (' + params.id + ') and (rltr__id=1 or rltr__id=2)'

//        sql = 'select i.prtr__id\n' +
//              'FROM prtr i INNER JOIN trmt j ON i.trmt__id = j.trmt__id \n'+
//               'WHERE (i.prsn__id = (' + params.id + ' ) and (i.rltr__id=1 or i.rltr__id=2)) and (j.trmtfccr >= (' + desde + ') and j.trmtfccr <= (' + hasta + '))'




//        def sql = "SELECT i.itemcdgo codigo, i.itemnmbr item, u.unddcdgo unidad, sum(v.voitcntd) cantidad, \n" +
//                "v.voitpcun punitario, v.voittrnp transporte, v.voitpcun + v.voittrnp  costo, \n" +
//                "sum((v.voitpcun + v.voittrnp) * v.voitcntd)  total, g.grpodscr grupo, g.grpo__id grid \n" +
//                "FROM vlobitem v INNER JOIN item i ON v.item__id = i.item__id\n" +
//                "INNER JOIN undd u ON i.undd__id = u.undd__id\n" +
//                "INNER JOIN dprt d ON i.dprt__id = d.dprt__id\n" +
//                "INNER JOIN sbgr s ON d.sbgr__id = s.sbgr__id\n" +
//                "INNER JOIN grpo g ON s.grpo__id = g.grpo__id AND g.grpo__id IN (${params.tipo}) \n" +
//                "WHERE v.obra__id = ${params.id} and v.voitcntd >0 \n" + wsp +
//                "group by i.itemcdgo, i.itemnmbr, u.unddcdgo, v.voitpcun, v.voittrnp, v.voitpcun, \n" +
//                "g.grpo__id, g.grpodscr " +
//                "ORDER BY g.grpo__id ASC, i.itemcdgo"
//

        println("sql " + sql)

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
        def prmsHeaderHoja1 = [border: Color.WHITE, bordeTop: "1", bordeBot: "1"]
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

        PdfPTable tablaTramites = new PdfPTable(7);
        tablaTramites.setWidthPercentage(100);
        tablaTramites.setWidths(arregloEnteros([15, 15, 15, 15, 15, 15, 15]))

        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
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

            addCellTabla(tablaTramites, new Paragraph("DOC PRINCIPAL", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("ASUNTO", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja1)

            addCellTabla(tablaTramites, new Paragraph(Tramite.get(principal?.id).codigo, times8normal), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph(Tramite.get(principal?.id).asunto, times8normal), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja1)

            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)

            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)

            addCellTabla(tablaTramites, new Paragraph("TRÁMITE NRO", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("FECHA CREACIÓN", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("DE OFICINA", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("CREADO POR", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("FECHA DE ENVIO", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("FECHA DE RECEPCIÓN", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("FECHA DE LÍMITE", times8bold), prmsHeaderHoja1)

            addCellTabla(tablaTramites, new Paragraph(tramite?.codigo, times8normal), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph(tramite?.fechaCreacion.format("dd-MM-yyyy"), times8normal), prmsHeaderHoja1)
            if(tramite?.deDepartamento){
                addCellTabla(tablaTramites, new Paragraph(tramite?.deDepartamento?.descripcion, times8normal), prmsHeaderHoja1)
            }else{
                addCellTabla(tablaTramites, new Paragraph((tramite?.de?.nombre + ' ' + tramite?.de?.apellido) ?: '', times8normal), prmsHeaderHoja1)
            }
            addCellTabla(tablaTramites, new Paragraph(tramite?.de?.login, times8normal), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph(prtr?.fechaEnvio?.format("dd-MM-yyyy"), times8normal), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph(prtr?.fechaRecepcion?.format("dd-MM-yyyy"), times8normal), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph(prtr?.fechaLimite?.format("dd-MM-yyyy"), times8normal), prmsHeaderHoja1)

            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)


            addCellTabla(tablaTramites, new Paragraph("TRÁMITE CONTEST NRO", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("FECHA CREACIÓN", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("DE OFICINA", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("CREADO POR", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("FECHA DE ENVIO", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("FECHA DE RECEPCIÓN", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("FECHA DE LÍMITE", times8bold), prmsHeaderHoja1)

            addCellTabla(tablaTramites, new Paragraph(tramiteContes?.codigo, times8normal), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph(tramiteContes?.fechaCreacion?.format("dd-MM-yyyy"), times8normal), prmsHeaderHoja1)
            if(tramite?.deDepartamento){
                addCellTabla(tablaTramites, new Paragraph(tramiteContes?.deDepartamento?.descripcion, times8normal), prmsHeaderHoja1)
            }else{
                addCellTabla(tablaTramites, new Paragraph((tramiteContes?.de?.nombre + ' ' + tramiteContes?.de?.apellido) ?: '', times8normal), prmsHeaderHoja1)
            }
            addCellTabla(tablaTramites, new Paragraph(tramiteContes?.de?.login, times8normal), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph(prtrContes?.fechaEnvio?.format("dd-MM-yyyy"), times8normal), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph(prtrContes?.fechaRecepcion?.format("dd-MM-yyyy"), times8normal), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph(prtrContes?.fechaLimite?.format("dd-MM-yyyy"), times8normal), prmsHeaderHoja1)

            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)

            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)

            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)

            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja)

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
