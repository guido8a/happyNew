package happy.reportes
import com.lowagie.text.pdf.DefaultFontMapper
import com.lowagie.text.pdf.PdfContentByte
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfTemplate

import happy.tramites.EstadoTramite

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfWriter

import happy.seguridad.Persona
import happy.tramites.Departamento;
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite

import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.labels.StandardPieSectionLabelGenerator
import org.jfree.chart.plot.PiePlot
import org.jfree.data.general.DefaultPieDataset

import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D

class BloqueadosController {
    def reportesPdfService

    Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
    Font times18bold = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
    Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
    Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
    Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
    def datosGrafico

    def reporteConsolidado() {
//        params.detalle=1
//        params.prsn=session.usuario.id
//        println "con aaa    " + params
//        datosGrafico = [:]
//        def estadoR = EstadoTramite.findByCodigo("E004")
        if(!params.dpto)
            params.dpto=session.usuario.departamentoId

        def datos = []
        def dep = Departamento.get(params.dpto)
//        dep = dep.padre?.padre
        def deps = []
        deps = getHijos(dep)

//        println "deps "+deps


        def baos = new ByteArrayOutputStream()
        def name = "reporteUsuariosBloqueados_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeaderHoja1 = [border: Color.WHITE, bordeTop: "1", bordeBot: "1"]
        times8boldWhite.setColor(Color.WHITE)
        times10boldWhite.setColor(Color.WHITE)
        Document document = reportesPdfService.crearDocumento("vert", [top: 2.5, right: 2.5, bottom: 1.5, left: 3])

        def pdfw = PdfWriter.getInstance(document, baos);
        reportesPdfService.membrete(document)
        document.open();
        reportesPdfService.propiedadesDocumento(document, "reporteUsuariosBloqueados")
        reportesPdfService.crearEncabezado(document, "Reporte de usuarios bloqueados")
        def contenido = new Paragraph();
        def total = 0

        PdfPTable tablaTramites
        tablaTramites = new PdfPTable(2);
        tablaTramites.setWidths(50,50)
        tablaTramites.setWidthPercentage(100);
        def parH = new Paragraph("Departamento", times8bold)
        def cell = new PdfPCell(parH);
        cell.setBorderColor(Color.WHITE)
        tablaTramites.addCell(cell);
        cell = new PdfPCell(new Paragraph("Usuario", times8bold));
        cell.setBorderColor(Color.WHITE)
        tablaTramites.addCell(cell);
        def par
       deps.each {d->
           Persona.findAllByDepartamentoAndEstado(d,"B").each {p->
               par = new Paragraph(""+d, times8normal)
               cell = new PdfPCell(par);
               cell.setBorderColor(Color.WHITE)
               tablaTramites.addCell(cell);
               par = new Paragraph(""+p, times8normal)
               cell = new PdfPCell(par);
               cell.setBorderColor(Color.WHITE)
               tablaTramites.addCell(cell);
               total++
           }
       }




        par = new Paragraph("Gran Total", times8bold)
        cell = new PdfPCell(par);
        cell.setBorderColor(Color.WHITE)
        tablaTramites.addCell(cell);
        par = new Paragraph("" + total, times8bold)
        cell = new PdfPCell(par);
        cell.setBorderColor(Color.WHITE)
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
        tablaTramites.addCell(cell);
        contenido.add(tablaTramites)

        document.add(contenido)




        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
//        println "datos grafico "+datosGrafico
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
//        return  [tramites:tramites,datos:datos]
    }



    def getHijos(dep){
        def res = [dep]
        def hijos =  Departamento.findAllByPadre(dep)
        if(hijos.size()>0){
            hijos.each {h->
                res+=getHijos(h)
            }
        }
        return res

    }

}
