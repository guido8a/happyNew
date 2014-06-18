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


        def persona = Persona.get(session.usuario.id)

//        def pxtPara = PersonaDocumentoTramite.withCriteria {
//
//            eq("persona", persona)
//            eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R001'))
//            eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R002'))
//
//        }


        def sql = ''
        def result = []
        def cn = dbConnectionService.getConnection();
        def tramite
        def prtr
        def principal

        sql = 'select prtr__id from prtr where prsn__id = (' + session.usuario.id + ') and (rltr__id=1 or rltr__id=2) between '

        cn.eachRow(sql) { r ->
//            println(">>>>>" + r)
            result.add(r.toRowResult())
        }

//        println("result" + result)

        result.each {
            tramite = PersonaDocumentoTramite.get(it.prtr__id).tramite
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

//        Paragraph headers = new Paragraph();
//        headers.setAlignment(Element.ALIGN_CENTER);
//        headers.add(new Paragraph("", times18bold));
//        headers.add(new Paragraph(params.departamento, times12bold));
//        headers.add(new Paragraph("Fecha: " + new Date().format("dd-MM-yyyy"), times12bold));
//        headers.add(new Paragraph("", times12bold));

        reportesPdfService.crearEncabezado(document, "REPORTE DE GESTIÓN DE TRÁMITES DEL USUARIO:  ${persona}")


        PdfPTable tablaTramites = new PdfPTable(7);
        tablaTramites.setWidthPercentage(100);
        tablaTramites.setWidths(arregloEnteros([15, 15, 15, 15, 15, 15, 15]))

//        PdfPTable tablaPrincipal = new PdfPTable(7);
//        tablaPrincipal.setWidthPercentage(100);
//        tablaPrincipal.setWidths(arregloEnteros([80, 80, 10, 10, 10, 10, 10]))

        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)


        result.each {
            tramite = PersonaDocumentoTramite.get(it.prtr__id).tramite
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
                addCellTabla(tablaTramites, new Paragraph(tramite?.de?.nombre, times8normal), prmsHeaderHoja1)
            }
            addCellTabla(tablaTramites, new Paragraph("", times8normal), prmsHeaderHoja1)
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

            addCellTabla(tablaTramites, new Paragraph("NRO DE DÍAS TRANSCURRIDOS ENTRE RECEPCIÓN Y CONTESTACIÓN: ", times8bold), prmsHeaderHoja1)
            if(prtr?.fechaRespuesta){
                addCellTabla(tablaTramites, new Paragraph(prtr?.fechaRespuesta?.format("dd-MM-yyyy") - prtr?.fechaRecepcion.format("dd-MM-yyyy"), times8normal), prmsHeaderHoja1)
            }else{
             addCellTabla(tablaTramites, new Paragraph("Sin respuesta", times8normal), prmsHeaderHoja1)
            }
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja1)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja1)

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
