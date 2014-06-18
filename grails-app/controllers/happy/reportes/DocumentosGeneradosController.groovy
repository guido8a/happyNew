package happy.reportes

import com.lowagie.text.Chunk
import com.lowagie.text.HeaderFooter
import com.lowagie.text.PageSize
import com.lowagie.text.Phrase
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfWriter
import happy.seguridad.Persona
import happy.tramites.Departamento
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite

import java.awt.Color
import java.io.*;

class DocumentosGeneradosController {

    def reportesPdfService

    def reporteGeneral() {
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

            personas.each { persona ->
                if (persona.estaActivo) {
                    def tramites = Tramite.withCriteria {
                        eq("de", persona)
                        ge("fechaCreacion", desde)
                        le("fechaCreacion", hasta)
                        order("fechaCreacion", "asc")
                    }

                    if (tramites.size() > 0) {
                        reportesPdfService.addCellTabla(tabla, new Paragraph("${persona.departamento.descripcion} (${persona.departamento.codigo})", font), paramsLeft)
                        reportesPdfService.addCellTabla(tabla, new Paragraph("${persona.nombre} ${persona.apellido} (${persona.login})", font), paramsLeft)
                        reportesPdfService.addCellTabla(tabla, new Paragraph("${tramites.size()}", font), paramsCenter)
                    }
                }
            }
            document.add(tabla)
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

    def reporteDetallado() {

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
        reportesPdfService.addCellTabla(tabla, new Paragraph("No.", fontTh), paramsCenter)
        reportesPdfService.addCellTabla(tabla, new Paragraph("Fecha creación", fontTh), paramsCenter)
        reportesPdfService.addCellTabla(tabla, new Paragraph("Para oficina", fontTh), paramsCenter)
        reportesPdfService.addCellTabla(tabla, new Paragraph("Destinatario", fontTh), paramsCenter)
        reportesPdfService.addCellTabla(tabla, new Paragraph("Fecha envío", fontTh), paramsCenter)
        reportesPdfService.addCellTabla(tabla, new Paragraph("Fecha recepción", fontTh), paramsCenter)

        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCopia = RolPersonaTramite.findByCodigo("R002")

        def depActual = null

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
                    reportesPdfService.addCellTabla(tabla, new Paragraph(header, fontBold), paramsDpto)
                }

                if (params.tipo == "dpto") {
                    def header = persona.nombre + " " + persona.apellido + " (" + persona.login + "): " +
                            "${tramites.size()} documento${tramites.size() == 1 ? '' : 's'}"
                    reportesPdfService.addCellTabla(tabla, new Paragraph(header, fontBold), paramsUsuario)
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
}