package happy.reportes

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.DocumentException
import happy.seguridad.Persona
import happy.tramites.Departamento;
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite
import org.apache.commons.lang.WordUtils

import java.awt.Color

class DepartamentoExportController {

    def reportesPdfService

    def crearPdf() {

        def fileName = "departamentos"

        def baos = new ByteArrayOutputStream()
        def name = fileName + "_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
//            println "name "+name
        Font titleFont = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font titleFont3 = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font titleFont2 = new Font(Font.TIMES_ROMAN, 16, Font.BOLD);
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

        Font fontDpto = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontUsu = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

        Document document = reportesPdfService.crearDocumento([top: 4.5, right: 2.5, bottom: 2.5, left: 3])
        //crea el doc A4, vertical con margenes de top:4.5, right:2.5, bottom:2.5, left:2.5
        def pdfw = PdfWriter.getInstance(document, baos);

        reportesPdfService.documentoFooter(document, "", true)
        //pone en el footer el tipo de tramite q es y el numero de pagina

        document.open();
        reportesPdfService.propiedadesDocumento(document, "departamentos")
        //pone las propiedades: title, subject, keywords, author, creator

//        println titulo
        Paragraph headersTitulo = new Paragraph();
        headersTitulo.setAlignment(Element.ALIGN_CENTER);
        headersTitulo.add(new Paragraph("Departamentos y usuarios", titleFont2));
        headersTitulo.setSpacingAfter(10f)

        document.add(headersTitulo)

        arbolDpto(document, fontDpto, fontUsu, null, "")

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def arbolDpto(document, fontDpto, fontUsu, padre, esp) {
        def departamentos = Departamento.withCriteria {
            eq("activo", 1)
            if (padre == null) {
                isNull("padre")
            } else {
                eq("padre", padre)
            }
            order("descripcion", "asc")
        }
        departamentos.each { dpto ->
            def esp2 = esp
            if (esp2 != "") {
                esp2 += " "
            }
            def desc = esp2 + "${dpto.descripcion} (${dpto.codigo})"
            if (dpto.telefono || dpto.extension || dpto.direccion) {
                desc += ": "
                if (dpto.telefono) {
                    desc += dpto.telefono
                    if (dpto.extension) {
                        desc += " " + dpto.extension
                    }
                }
                if (dpto.direccion) {
                    if (dpto.telefono) {
                        desc += ", "
                    }
                    desc += dpto.direccion
                }
            }
            document.add(new Paragraph(desc, fontDpto));
            Persona.withCriteria {
                eq("activo", 1)
                eq("departamento", dpto)
                order("apellido", "asc")
            }.each { pers ->
                esp2 = esp + esp
                if (!padre) {
                    esp2 = esp + "" + esp + "    "
                }
                def descP = esp2 + " ${pers.nombre} ${pers.apellido}"
                if (pers.login) {
                    descP += " (${pers.login})"
                }
                descP = WordUtils.capitalizeFully(descP)
                document.add(new Paragraph(descP, fontUsu));
            }
            if (Departamento.countByPadre(dpto) > 0) {
                arbolDpto(document, fontDpto, fontUsu, dpto, esp + "    ")
            }
        }
    }

    def index() {}
}
