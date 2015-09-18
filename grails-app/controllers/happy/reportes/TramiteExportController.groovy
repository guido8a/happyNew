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
import happy.seguridad.Shield

import java.awt.Color
import java.io.*;

import happy.seguridad.Persona
import happy.tramites.Departamento
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite

class TramiteExportController extends Shield{

    def reportesPdfService
    def enviarService

    def arbolPdf() {
        if (params.id) {
            def tramite = Tramite.get(params.id)
            def codigo = tramite.codigo

            def fileName = "reporte_tramite_${codigo}"

            def baos = new ByteArrayOutputStream()
            def name = fileName + "_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

            Document document = reportesPdfService.crearDocumento([top: 2, right: 2, bottom: 1.5, left: 2.5])
            //crea el doc A4, vertical con margenes de top:2.5, right:2.5, bottom:2.5, left:2.5
            def pdfw = PdfWriter.getInstance(document, baos);

            //pone en el footer el nombre de tramite q es y el numero de pagina
            reportesPdfService.membrete(document)
            document.open();
            reportesPdfService.propiedadesDocumento(document, "trámite")
            //pone las propiedades: title, subject, keywords, author, creator

            //crea el encabezado que quieren estos manes con el titulo que se le mande
            reportesPdfService.crearEncabezado(document, "Reporte del trámite ${codigo}")

            if (tramite) {
                def principal = tramite
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
                makeTreeExtended(document, principal, 0)
            }

            document.close();
            pdfw.close()
            byte[] b = baos.toByteArray();
            response.setContentType("application/pdf")
            response.setHeader("Content-disposition", "attachment; filename=" + name)
            response.setContentLength(b.length)
            response.getOutputStream().write(b)
        } else {
            render "<div class='alert alert-danger'>No ha seleccionado un trámite</div>"
        }
    }

    def makeTreeExtended(Document document, Tramite principal, Integer espacio) {
        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCc = RolPersonaTramite.findByCodigo("R002")
        def paras = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(principal, rolPara)
        def ccs = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(principal, rolCc)

        //esto muestra una hoja por destinatario
        paras.each { para ->
            makeLeaf(document, para, espacio)
        }

        //el para y las copias son hermanos
        ccs.each { para ->
            makeLeaf(document, para, espacio)
        }
    }

    def makeLeaf(Document document, PersonaDocumentoTramite pdt, Integer espacio) {
        def hijos = Tramite.findAllByAQuienContesta(pdt, [sort: "fechaCreacion", order: "asc"])

        Paragraph paragraphTramite = new Paragraph();

        def nivel = (espacio / 10) + 1
        def phraseInfo = tramiteInfo(pdt, nivel.toInteger())
        paragraphTramite.setIndentationLeft(espacio);
        paragraphTramite.add(phraseInfo)
        document.add(paragraphTramite)

        if (hijos.size() > 0) {
            hijos.each { hijo ->
                makeTreeExtended(document, hijo, espacio + 10)
            }
        }
    }

    private static Phrase tramiteInfo(PersonaDocumentoTramite tramiteParaInfo, Integer nivel) {
        Font font = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
        Font fontBold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font fontSmall = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
        Font fontSmallBold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);

        if (tramiteParaInfo.fechaAnulacion) {
            font.setColor(Color.GRAY);
            fontBold.setColor(Color.GRAY);
            fontSmall.setColor(Color.GRAY);
            fontSmallBold.setColor(Color.GRAY);
        } else {
            font.setColor(Color.BLACK);
            fontBold.setColor(Color.BLACK);
            fontSmall.setColor(Color.BLACK);
            fontSmallBold.setColor(Color.BLACK);
        }

        def rol = tramiteParaInfo.rolPersonaTramite

        def paraStr, deStr
        if (tramiteParaInfo.tramite.tipoDocumento.codigo == "DEX") {
            deStr = tramiteParaInfo.tramite.paraExterno + " (EXT), "
        } else {
            deStr = tramiteParaInfo.tramite.deDepartamento ?
                    tramiteParaInfo.tramite.deDepartamento.codigo + ", " :
                    tramiteParaInfo.tramite.de.departamento.codigo + ":" + tramiteParaInfo.tramite.de.login + ", "
        }
        if (tramiteParaInfo.tramite.tipoDocumento.codigo == "OFI") {
            paraStr = tramiteParaInfo.tramite.paraExterno + " (EXT), "
        } else {
            paraStr = ""
            if (tramiteParaInfo.departamento) {
                paraStr = tramiteParaInfo.departamento.descripcion + ", "
            } else if (tramiteParaInfo.persona) {
                paraStr = tramiteParaInfo.persona.departamento.codigo + ":" + tramiteParaInfo.persona.login + ", "
            }
        }
        def phraseInfo = new Phrase()
        phraseInfo.add(new Chunk("<${nivel}> ", fontSmallBold))
        if (rol.codigo == "R002") {
            phraseInfo.add(new Chunk("[CC] ", fontSmall))
        }
        phraseInfo.add(new Chunk(tramiteParaInfo.tramite.codigo + " ", fontBold))
        phraseInfo.add(new Chunk("(", fontSmall))
        phraseInfo.add(new Chunk("DE: ", fontSmallBold))
        phraseInfo.add(new Chunk(deStr, fontSmall))
        phraseInfo.add(new Chunk("${rol.descripcion}: ", fontSmallBold))
        phraseInfo.add(new Chunk(paraStr, fontSmall))
        phraseInfo.add(new Chunk("ASUNTO: ", fontSmallBold))
        phraseInfo.add(new Chunk((tramiteParaInfo.tramite.asunto ?: "") + ", ", fontSmall))
        phraseInfo.add(new Chunk("creado ", fontSmallBold))
        phraseInfo.add(new Chunk("el " + tramiteParaInfo.tramite.fechaCreacion.format("dd-MM-yyyy HH:mm"), fontSmall))
        if (tramiteParaInfo.fechaEnvio) {
            phraseInfo.add(new Chunk(", ", fontSmall))
            phraseInfo.add(new Chunk("enviado ", fontSmallBold))
            phraseInfo.add(new Chunk("el " + tramiteParaInfo.fechaEnvio.format("dd-MM-yyyy HH:mm"), fontSmall))
        }
        if (tramiteParaInfo.fechaRecepcion) {
            phraseInfo.add(new Chunk(", ", fontSmall))
            phraseInfo.add(new Chunk("recibido ", fontSmallBold))
            phraseInfo.add(new Chunk("el " + tramiteParaInfo.fechaRecepcion.format("dd-MM-yyyy HH:mm"), fontSmall))
        }
        if (tramiteParaInfo.fechaArchivo) {
            phraseInfo.add(new Chunk(", ", fontSmall))
            phraseInfo.add(new Chunk("archivado ", fontSmallBold))
            phraseInfo.add(new Chunk("el " + tramiteParaInfo.fechaArchivo.format("dd-MM-yyyy HH:mm"), fontSmall))
        }
        if (tramiteParaInfo.fechaAnulacion) {
            phraseInfo.add(new Chunk(", ", fontSmall))
            phraseInfo.add(new Chunk("anulado ", fontSmallBold))
            phraseInfo.add(new Chunk("el " + tramiteParaInfo.fechaAnulacion.format("dd-MM-yyyy HH:mm"), fontSmall))
        }
        phraseInfo.add(new Chunk(")", fontSmall))

        if (tramiteParaInfo.tramite.estadoTramiteExterno) {
            phraseInfo.add(new Chunk(" - " + tramiteParaInfo.tramite.estadoTramiteExterno.descripcion, fontSmall))
        }

        return phraseInfo
    }

    def crearPdf() {

        def tramite = Tramite.get(params.id.toLong())
        def usuario = Persona.get(session.usuario.id)
        def realPath = servletContext.getRealPath("/")
        def mensaje = message(code: 'pathImages').toString()

        if (params.editorTramite) {
            def paratr = tramite.para
            def copiastr = tramite.copias
            def enviado = false
            (copiastr + paratr).each {c->
                if(c?.estado?.codigo == "E003") {
                    enviado = true
                }
            }
            if(!enviado) {
                tramite.texto = (params.editorTramite).replaceAll("\\n", "")
                tramite.fechaModificacion = new Date()
                if (tramite.save(flush: true)) {
                    def para = tramite.para
                    if (params.para) {
                        if (params.para.toLong() > 0) {
                            para.persona = Persona.get(params.para.toLong())
                        } else {
                            para.departamento = Departamento.get(params.para.toLong() * -1)
                        }
                        if (para.save(flush: true)) {
                        } else {
                            println "NO_Ha ocurrido un error al guardar el destinatario: " + renderErrors(bean: para)
                        }
                    }
                } else {
                    println "NO_Ha ocurrido un error al guardar el trámite: " + renderErrors(bean: tramite)
                }
            }
        }
        render enviarService.crearPdf(tramite, usuario, params.enviar.toString(), params.type.toString(), realPath.toString(), mensaje)
    }

    def verPdf() {
        def tramite = Tramite.get(params.id)
        def usuarioEnvia = tramite.deId
        def realPath = servletContext.getRealPath("/") + "tramites/" + tramite.codigo + ".pdf"

    }


    def imprimirGuia() {

        def cantidadTramites = params.ids
        def personaDocumento
        def pxt = []

        cantidadTramites.split(',').each {
            def tramite = Tramite.get(it)
            pxt += PersonaDocumentoTramite.findAllByTramite(tramite)
        }


        def baos = new ByteArrayOutputStream()
        def name = "guia_tramites_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
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

//        com.lowagie.text.Document document
//        document = new com.lowagie.text.Document(PageSize.A4);
//        def pdfw = PdfWriter.getInstance(document, baos);
//        HeaderFooter footer1 = new HeaderFooter(new Phrase('', times8normal), true);
//        footer1.setBorder(Rectangle.NO_BORDER);
//        footer1.setAlignment(Element.ALIGN_CENTER);
//        document.setFooter(footer1);
//        document.open();

        Document document = reportesPdfService.crearDocumento([top: 2, right: 2, bottom: 1.5, left: 2.5])
        def pdfw = PdfWriter.getInstance(document, baos);
        HeaderFooter footer1 = new HeaderFooter(new Phrase('', times8normal), true);
        footer1.setBorder(Rectangle.NO_BORDER);
        footer1.setAlignment(Element.ALIGN_CENTER);
        document.setFooter(footer1);
        document.open();

        Paragraph headers = new Paragraph();
        headers.setAlignment(Element.ALIGN_CENTER);
//        headers.add(new Paragraph("", times18bold));
//        headers.add(new Paragraph("", times18bold));
//        headers.add(new Paragraph("", times18bold));
//        headers.add(new Paragraph("", times18bold));
//        headers.add(new Paragraph("", times18bold));
//        headers.add(new Paragraph("", times18bold));
//        headers.add(new Paragraph("", times18bold));
        headers.add(new Paragraph("Guía de Envio de Trámites", times18bold));
        headers.add(new Paragraph(params.departamento, times12bold));
        headers.add(new Paragraph("Fecha: " + new Date().format("dd-MM-yyyy"), times12bold));
        headers.add(new Paragraph("", times12bold));


        PdfPTable tablaTramites = new PdfPTable(5);
        tablaTramites.setWidthPercentage(100);
        tablaTramites.setWidths(arregloEnteros([28, 7, 25, 12, 12]))

        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)

        addCellTabla(tablaTramites, new Paragraph("DOCUMENTO", times10bold), prmsHeaderHoja1)
        addCellTabla(tablaTramites, new Paragraph("ROL", times10bold), prmsHeaderHoja1)
        addCellTabla(tablaTramites, new Paragraph("PARA", times10bold), prmsHeaderHoja1)
        addCellTabla(tablaTramites, new Paragraph("RECIBE", times10bold), prmsHeaderHoja1)
        addCellTabla(tablaTramites, new Paragraph("FIRMA", times10bold), prmsHeaderHoja1)

        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)

        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)

        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)

        def tramite = ""
        pxt.each {
            if (it.rolPersonaTramite.codigo != 'E004') {
                if (it.fechaEnvio) {
                    if(it.tramite.aQuienContesta) {
                        tramite = "${it.tramite.codigo} contesta a: ${it.tramite.aQuienContesta?.tramite?.codigo}"
                    } else {
                        tramite = "${it.tramite.codigo}"
                    }
//                    addCellTabla(tablaTramites, new Paragraph(it.tramite.codigo, times10bold), prmsHeaderHoja)
                    addCellTabla(tablaTramites, new Paragraph(tramite, times10bold), prmsHeaderHoja)
                    addCellTabla(tablaTramites, new Paragraph(it.rolPersonaTramite.descripcion, times10bold), prmsHeaderHoja)
                    if (it?.departamento) {

                        addCellTabla(tablaTramites, new Paragraph(it?.departamento?.descripcion ?: '', times8bold), prmsHeaderHoja)
                    } else {
                        addCellTabla(tablaTramites, new Paragraph((it?.persona?.nombre ?: '') + " " + (it?.persona?.apellido ?: ''), times8bold), prmsHeaderHoja)
                    }
                    addCellTabla(tablaTramites, new Paragraph("_______________", times8bold), prmsHeaderHoja)
                    addCellTabla(tablaTramites, new Paragraph("_______________", times8bold), prmsHeaderHoja)
                }
            }

            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja)

            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja)

            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph("", times8bold), prmsHeaderHoja)

            addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
            addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja)
        }

        document.add(headers);
        document.add(tablaTramites)
        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
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
