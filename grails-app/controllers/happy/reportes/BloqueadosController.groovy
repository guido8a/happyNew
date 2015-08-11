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
import happy.seguridad.Shield;

class BloqueadosController extends Shield {
    def reportesPdfService
    def dbConnectionService

    Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
    Font times18bold = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
    Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
    Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
    Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
    def datosGrafico

    def reporteWeb() {
        if (!params.dpto) {
            params.dpto = session.usuario.departamentoId
        }

        def dep = Departamento.get(params.dpto)
        def deps = []
        deps = getHijos(dep)
        def total = 0
        def tabla = "<table class='table table-bordered table-condensed table-hover'><thead><tr><th>Departamento</th><th>Usuario</th></tr></thead><tbody>"
        deps.each { d ->
            if (d.estado == "B") {
                tabla += "<tr>"
                tabla += "<td>${d}</td>"
                tabla += "<td>(Oficina)</td>"
                tabla += "</tr>"
                total++
            }
            Persona.findAllByDepartamentoAndEstado(d, "B").each { p ->
                tabla += "<tr>"
                tabla += "<td>${d}</td>"
                tabla += "<td>${p}</td>"
                tabla += "</tr>"
                total++
            }
        }
        tabla += "<tr><td style='font-weight:bold'>TOTAL</td><td style='text-align: right;font-weight:bold'>${total}</td></tr>"
        tabla += "</tbody></table>"
        return [tabla: tabla]
    }


    def reporteConsolidado() {
        if (!params.dpto) {
            params.dpto = session.usuario.departamentoId
        }

        def datos = []
        def dep = Departamento.get(params.dpto)
        def deps = []
        deps = getHijos(dep)
        def baos = new ByteArrayOutputStream()
        def name = "reporteUsuariosBloqueados_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeaderHoja1 = [border: Color.WHITE, bordeTop: "1", bordeBot: "1"]
        times8boldWhite.setColor(Color.WHITE)
        times10boldWhite.setColor(Color.WHITE)
        Document document = reportesPdfService.crearDocumento("vert", [top: 2.5, right: 2.5, bottom: 1.5, left: 3])

        def pdfw = PdfWriter.getInstance(document, baos);
        session.tituloReporte = "Reporte de Usuarios Bloqueados"
        reportesPdfService.membrete(document)
        document.open();
        reportesPdfService.propiedadesDocumento(document, "reporteUsuariosBloqueados")
        def contenido = new Paragraph();
        def total = 0

        PdfPTable tablaTramites
//        tablaTramites = new PdfPTable(3);
//        tablaTramites.setWidths(35, 35, 35)

        tablaTramites = new PdfPTable(1);
        tablaTramites.setWidths(100)

        tablaTramites.setWidthPercentage(100);
        def parH = new Paragraph("Departamento", times10bold)
        def cell = new PdfPCell(parH);
//        cell.setBorderColor(Color.WHITE)
//        tablaTramites.addCell(cell);
//        cell = new PdfPCell(new Paragraph("Usuario", times10bold));
//        cell.setBorderColor(Color.WHITE)
//        tablaTramites.addCell(cell);
//        cell = new PdfPCell(new Paragraph("Trámite", times10bold));
//        cell.setBorderColor(Color.WHITE)
//        tablaTramites.addCell(cell);
        def par
        deps.each { d ->
            if (d.estado == "B" && d.codigo != 'X-EXT' && d.remoto == 0) {

//                par = new Paragraph("" + d, times8normal)
//                cell = new PdfPCell(par);
//                cell.setBorderColor(Color.WHITE)
//                tablaTramites.addCell(cell);
//                par = new Paragraph("(Oficina)", times8normal)
//                cell = new PdfPCell(par);
//                cell.setBorderColor(Color.WHITE)
//                tablaTramites.addCell(cell);

                par = new Paragraph("" + d + " - (Oficina)", times8bold)
                cell = new PdfPCell(par);
                cell.setBorderColor(Color.WHITE)
                tablaTramites.addCell(cell);

                def triangulos = []
                Persona.findAllByDepartamento(d).each {
                    if (it?.esTriangulo() && it.activo == 1) {
                        triangulos += it.id
                    }
                }
                def cnDpto = dbConnectionService.getConnection();
                def sqlDpto = ""
                def resultDpto = []
                def cadenaTramites = ''
                sqlDpto = "select * from  entrada_dpto(" + triangulos.first() + ") where trmtfcbq < now() and trmtfcrc is NULL"
                def ind = 0
                cnDpto.eachRow(sqlDpto) { row ->

                    if(ind == 0){
                        cadenaTramites += (row?.trmtcdgo + " (" + row?.trmtfcen?.format("dd-MM-yyyy HH:mm") + ")")
                    }else{
                        cadenaTramites += (' - ' +row?.trmtcdgo + " (" + row?.trmtfcen?.format("dd-MM-yyyy HH:mm") + ")")
                    }


//                           resultDpto.add(dp.toRowResult())
//                    if (ind == 0) {
//                        par = new Paragraph(row?.trmtcdgo, times8normal)
//                        cell = new PdfPCell(par);
//                        cell.setBorderColor(Color.WHITE)
//                        tablaTramites.addCell(cell);
//                    } else {
//                        par = new Paragraph('', times8normal)
//                        cell = new PdfPCell(par);
//                        cell.setBorderColor(Color.WHITE)
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph('', times8normal)
//                        cell = new PdfPCell(par);
//                        cell.setBorderColor(Color.WHITE)
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph(row?.trmtcdgo, times8normal)
//                        cell = new PdfPCell(par);
//                        cell.setBorderColor(Color.WHITE)
//                        tablaTramites.addCell(cell);
//                    }
                    ind++
                }

                        par = new Paragraph(cadenaTramites, times8normal)
                        cell = new PdfPCell(par);
                        cell.setBorderColor(Color.WHITE)
                        cell.setPaddingLeft(15)
                        tablaTramites.addCell(cell);
                total++
            }

            Persona.findAllByDepartamentoAndEstado(d, "B").each { p ->
//                par = new Paragraph("" + d, times8normal)
//                cell = new PdfPCell(par);
//                cell.setBorderColor(Color.WHITE)
//                tablaTramites.addCell(cell);
//                par = new Paragraph("" + p, times8normal)
//                cell = new PdfPCell(par);
//                cell.setBorderColor(Color.WHITE)
//                tablaTramites.addCell(cell);

                par = new Paragraph("" + d + ' - ' + p, times8bold)
                cell = new PdfPCell(par);
                cell.setBorderColor(Color.WHITE)
                tablaTramites.addCell(cell);

                def cn = dbConnectionService.getConnection();
                def sql = ""
                def result = []
                sql = "select * from  entrada_prsn(" + p?.id + ") where trmtfcbq < now() and trmtfcrc is NULL"
                def ind2 = 0
                def cadenaPersona = ""
                cn.eachRow(sql) { re ->
                    result.add(re.toRowResult())
                }

                result.eachWithIndex { pers, j ->

                    if(j==0){
                        cadenaPersona += (pers?.trmtcdgo + " (" + pers?.trmtfcen?.format("dd-MM-yyyy HH:mm") + ")")
                    }else{
                        cadenaPersona += (" - " + pers?.trmtcdgo + " (" + pers?.trmtfcen?.format("dd-MM-yyyy HH:mm") + ")")

                    }

//                    if (j == 0) {
//                        par = new Paragraph(pers?.trmtcdgo, times8normal)
//                        cell = new PdfPCell(par);
//                        cell.setBorderColor(Color.WHITE)
//                        tablaTramites.addCell(cell);
//                    } else {
//                        par = new Paragraph('', times8normal)
//                        cell = new PdfPCell(par);
//                        cell.setBorderColor(Color.WHITE)
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph('', times8normal)
//                        cell = new PdfPCell(par);
//                        cell.setBorderColor(Color.WHITE)
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph(pers?.trmtcdgo, times8normal)
//                        cell = new PdfPCell(par);
//                        cell.setBorderColor(Color.WHITE)
//                        tablaTramites.addCell(cell);
//                    }
                }

                        par = new Paragraph(cadenaPersona, times8normal)
                        cell = new PdfPCell(par);
                        cell.setBorderColor(Color.WHITE)
                        cell.setPaddingLeft(15)
                        tablaTramites.addCell(cell);

                total++
            }
        }

        par = new Paragraph("Gran Total: " + total + " bandejas bloqueadas.", times10bold)
        cell = new PdfPCell(par);
        cell.setBorderColor(Color.WHITE)
        tablaTramites.addCell(cell);
//        par = new Paragraph("" + total, times8bold)
//        cell = new PdfPCell(par);
//        cell.setBorderColor(Color.WHITE)
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
//        tablaTramites.addCell(cell);
        contenido.add(tablaTramites)
        document.add(contenido)
        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def getHijos(dep) {
        def res = [dep]
        def hijos = Departamento.findAllByPadre(dep)
        if (hijos.size() > 0) {
            hijos.each { h ->
                res += getHijos(h)
            }
        }
        return res

    }

}
