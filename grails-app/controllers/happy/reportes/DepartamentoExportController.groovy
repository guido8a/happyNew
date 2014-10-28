package happy.reportes

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfWriter
import happy.seguridad.Persona
import happy.tramites.Departamento;
import org.apache.commons.lang.WordUtils
import happy.seguridad.Shield;

import java.awt.Color

class DepartamentoExportController extends Shield {

    def reportesPdfService

    Font fontDpto = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    Font fontUsu = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);

    def reporteSinUsuarios() {
        redirect(action: "crearPdf", params: [usu: false])
    }
    def reporteConUsuarios() {
        redirect(action: "crearPdf", params: [usu: true, inactivos: true])
    }
    def reporteUsuariosActivos() {
        redirect(action: "crearPdf", params: [usu: true, inactivos: false])
    }

    def crearPdf() {
        params.inactivos=params.inactivos?:true
        params.sort = params.sort ?: "apellido"
        def conUsuarios = params.usu ? (params.usu == "true") : "true"
        def departamentoInicial = params.id?.toLong() > -1 ? Departamento.get(params.id.toLong()) : null

        def strTitulo
        if (conUsuarios) {
            if (Departamento.countByPadre(departamentoInicial) > 0) {
                strTitulo = "Departamentos y Usuarios"
            } else {
                strTitulo = "Usuarios"
            }
            if (params.inactivos.toBoolean() == false) {
                strTitulo += " activos"
            }
        } else {
            strTitulo = "Departamentos"
        }
        def strHeader = strTitulo
        if (departamentoInicial) {
            strTitulo += "\nde " + departamentoInicial.descripcion
            strHeader += " de " + departamentoInicial.descripcion
        } else {
            strTitulo += "\nregistrados en el sistema"
            strHeader += " registrados en el sistema"
        }

        def fileName = "departamentos"

        def baos = new ByteArrayOutputStream()
        def name = fileName + "_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
//            println "name "+name

        if (!conUsuarios) {
            fontDpto = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
        }

        Document document = reportesPdfService.crearDocumento([top: 2, right: 2, bottom: 1.5, left: 2.5])
        //crea el doc A4, vertical con margenes de top:4.5, right:2.5, bottom:2.5, left:2.5
        def pdfw = PdfWriter.getInstance(document, baos);
        //pone en el footer el tipo de tramite q es y el numero de pagina
        session.tituloReporte = strTitulo
        reportesPdfService.membrete(document)

        document.open();
        reportesPdfService.propiedadesDocumento(document, "departamentos")
        //pone las propiedades: title, subject, keywords, author, creator
        arbolDpto(document, departamentoInicial, "", conUsuarios, params.inactivos, params.sort)
        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def arbolDpto(document, padre, esp, conUsuarios, inactivos, sort) {
        inactivos = inactivos.toBoolean()
        def departamentos = Departamento.withCriteria {
            eq("activo", 1)
            if (padre == null) {
                isNull("padre")
            } else {
                eq("padre", padre)
            }
            order("descripcion", "asc")
        }
        if (padre && conUsuarios) {
//            println "AQUI " + padre
            Persona.withCriteria {
//                eq("activo", 1)
                eq("departamento", padre)
                order(sort, "asc")
            }.each { pers ->
                if (inactivos || (!inactivos && pers.estaActivo)) {
                    def esp2 = esp
                    if (esp2 != "") {
                        esp2 += " "
                    }
                    esp2 = esp
                    if (!padre) {
                        esp2 = esp + "" + esp + "    "
                    }
                    def descP = esp2
                    if (!pers.estaActivo) {
                        fontUsu.setColor(Color.GRAY);
                        descP += " Inactivo - "
                    } else {
                        fontUsu.setColor(Color.BLACK);
                    }
                    if (sort == "nombre") {
                        descP += WordUtils.capitalizeFully("${pers.puedeJefe ? '*' : ''} ${pers.nombre} ${pers.apellido}")
                    } else {
                        descP += WordUtils.capitalizeFully("${pers.puedeJefe ? '*' : ''} ${pers.apellido} ${pers.nombre}")
                    }
                    if (pers.login || pers.telefono || pers.mail) {
                        descP += " ("
                        if (pers.login) {
                            descP += ("usuario: ${pers.login}").toLowerCase()
                        }
                        if (pers.telefono) {
                            if (descP != " (") {
                                descP += ", "
                            }
                            descP += "teléfono: ${pers.telefono}"
                        }
                        if (pers.mail) {
                            if (descP != " (") {
                                descP += ", "
                            }
                            descP += ("e-mail: ${pers.mail}").toLowerCase()
                        }
                        descP += ")"
                    }
                    document.add(new Paragraph(descP, fontUsu));
                }
            }
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
            if (Departamento.countByPadre(dpto) > 0 || Persona.countByDepartamento(dpto) > 0) {
                arbolDpto(document, dpto, esp + "    ", conUsuarios, inactivos, sort)
            }
        }
    }

    def index() {}
}
