package happy.reportes

import com.lowagie.text.Chunk
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
import happy.tramites.Departamento
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
    def tramitesService

    Font font = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
    Font fontBold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    Font fontSmall = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
    Font fontSmallBold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);

    def prmsHeaderHoja = [border: Color.WHITE]
    def prmsHeaderHoja2 = [border: Color.WHITE, bordeBot: "1"]
    def prmsHeaderHoja3 = [border: Color.WHITE, bordeBot: "1", colspan: 6]
    def prmsHeaderHoja9 = [border: Color.WHITE, colspan: 9]

    def reporteGestion3 () {

        params.id = params.id?:655

        if (params.id) {
            def departamento = Departamento.get(params.id)

            def fileName = "reporte_gestion_${departamento.codigo}"

            def baos = new ByteArrayOutputStream()
            def name = fileName + "_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

            Document document = reportesPdfService.crearDocumento('h',[top: 2, right: 2, bottom: 2, left: 2.5])
            //crea el doc A4, vertical con margenes de top:2.5, right:2.5, bottom:2.5, left:2.5
            def pdfw = PdfWriter.getInstance(document, baos);

            reportesPdfService.membrete(document)
            document.open();
            reportesPdfService.propiedadesDocumento(document, "trámite")
            reportesPdfService.crearEncabezado(document, "Reporte de gestión del departamento ${departamento.codigo}")

            def desde = new Date().parse("dd-MM-yyyy", params.desde)
            def hasta= new Date().parse("dd-MM-yyyy", params.hasta)

            if (departamento) {
                def tramites = PersonaDocumentoTramite.withCriteria {
//                isNotNull('fechaEnvio')
                    eq("departamento", departamento)
                    or{
                        eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R001'))
                        eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R002'))
                    }
                    tramite {
                        ge('fechaCreacion', desde)
                        le('fechaCreacion', hasta)
                    }
                }

                tramites.each{tram ->

                    def tramite = tram.tramite

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

                    PdfPTable tablaTramites = new PdfPTable(9);
                    tablaTramites.setWidthPercentage(100);
                    tablaTramites.setWidths(arregloEnteros([22, 20, 18, 20, 15, 15, 15, 15, 15]))
                    tablaTramites.setSpacingBefore(5)

                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph("DOC PRINCIPAL :", fontBold), prmsHeaderHoja2)
                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph(Tramite.get(principal?.id).codigo, font), prmsHeaderHoja2)
                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph("ASUNTO :", fontBold), prmsHeaderHoja2)
                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph(Tramite.get(principal?.id).asunto, font), prmsHeaderHoja3)

                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph("", fontBold), prmsHeaderHoja9)

//                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph("TRÁMITE N°.", fontBold), prmsHeaderHoja)
//                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph("F.CREACIÓN ", fontBold), prmsHeaderHoja)
//                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph("F.ENVIO", fontBold), prmsHeaderHoja)
//                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph("F.RECEPCIÓN", fontBold), prmsHeaderHoja)
//                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph("T.TRANSCURRIDO", fontBold), prmsHeaderHoja)
//                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph("DE", fontBold), prmsHeaderHoja)
//                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph("ASUNTO", fontBold), prmsHeaderHoja)
//                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph("PARA", fontBold), prmsHeaderHoja)
//                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph("CONTESTACIÓN-RETRASO", fontBold), prmsHeaderHoja)

                    makeTableExtended(tablaTramites, principal, departamento,0)

                    document.add(tablaTramites)

                }

            }

            document.close();
            pdfw.close()
            byte[] b = baos.toByteArray();
            response.setContentType("application/pdf")
            response.setHeader("Content-disposition", "attachment; filename=" + name)
            response.setContentLength(b.length)
            response.getOutputStream().write(b)
        } else {
            render "<div class='alert alert-danger'>No ha seleccionado un departamento</div>"
        }

    }

    def makeTableExtended(PdfPTable tabla, Tramite principal, Departamento departamento, int s) {
        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCc = RolPersonaTramite.findByCodigo("R002")

        def paras = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(principal, rolPara)
        def ccs = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(principal, rolCc)

        //esto muestra una hoja por destinatario
        paras.each { para ->
            makeRow(tabla, para, departamento,s)
        }

        //el para y las copias son hermanos
        ccs.each { para ->
            makeRow(tabla, para, departamento,s)
        }
    }


    def makeRow(PdfPTable tablaTramites, PersonaDocumentoTramite pdt, Departamento departamento, int s) {

        def hijos = Tramite.findAllByAQuienContesta(pdt, [sort: "fechaCreacion", order: "asc"])

        def diasTrans = ''

        if((pdt?.departamento?.codigo == departamento?.codigo) || (Persona.get(pdt?.persona?.id)?.departamento?.codigo == departamento?.codigo)){
            if(pdt?.fechaRecepcion && pdt?.fechaEnvio){
                def diasTrans2 = diasLaborablesService.diasLaborablesEntre((pdt?.fechaRecepcion).clearTime(), (pdt?.fechaEnvio).clearTime())
                def diasC2 = 0
                if(diasTrans2[0]){
                    diasC2 = diasTrans2[1]
                }else{
                    println("error dias " +  diasTrans2[1])
                }
//                addCellTabla(tablaTramites, new Paragraph(g.formatNumber(number: (prtr?.fechaRecepcion - prtr?.fechaEnvio)*24, format: "###.##", locale: "ec") + " horas",times7bold), prmsHeaderHoja)
//            addCellTabla(tablaTramites, new Paragraph(g.formatNumber(number: (diasC2)*24, format: "###.##", locale: "ec") + " horas",times7bold), prmsHeaderHoja)
                diasTrans = g.formatNumber(number: (diasC2)*24, format: "###.##", locale: "ec") + " horas"
            }else{
                if(pdt?.fechaEnvio){
                    def diasTrans3 = diasLaborablesService.diasLaborablesEntre((pdt?.fechaEnvio).clearTime(), new Date().clearTime())
                    def diasC3 = 0
                    if(diasTrans3[0]){
                        diasC3 = diasTrans3[1]
                    }else{
                        println("error dias " +  diasTrans3[1])
                    }
//                addCellTabla(tablaTramites, new Paragraph(g.formatNumber(number: (diasC3)*24, format: "###.##", locale: "ec") + " horas",times7bold), prmsHeaderHoja)
                    diasTrans = g.formatNumber(number: (diasC3)*24, format: "###.##", locale: "ec") + " horas"
                }else{
//                addCellTabla(tablaTramites, new Paragraph("No enviado",times7bold), prmsHeaderHoja)
                    diasTrans = "No enviado"
                }
            }


            if(s == 1){
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("CONTESTADO CON", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("F.CREACIÓN ", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("F.ENVIO", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("F.RECEPCIÓN", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("T.TRANSCURRIDO", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("DE", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("ASUNTO", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("PARA", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph(" ", fontBold), prmsHeaderHoja)


                reportesPdfService.addCellTabla(tablaTramites, new Paragraph(pdt.tramite.codigo, font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph(pdt.fechaCreacion?pdt.fechaCreacion.format("dd-MM-yyyy HH:mm"):"", font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph(pdt.fechaEnvio?pdt.fechaEnvio.format("dd-MM-yyyy HH:mm"):"", font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph(pdt.fechaRecepcion?pdt.fechaRecepcion.format("dd-MM-yyyy HH:mm"):"", font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph(diasTrans, fontBold), prmsHeaderHoja)
                if(pdt?.tramite?.deDepartamento){
                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph((pdt?.tramite?.deDepartamento?.codigo ?: ''), font), prmsHeaderHoja)
                }else{
                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph((pdt?.tramite?.de?.nombre ?: '') + ' ' + (pdt?.tramite?.de?.apellido ?: ''), font), prmsHeaderHoja)
                }

//                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("DE", font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph(pdt.tramite.asunto, font), prmsHeaderHoja)
                if(pdt?.departamento){
                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph(pdt?.departamento?.codigo, font), prmsHeaderHoja)
                }else{
                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph((pdt?.persona?.departamento?.codigo) + ' - ' + pdt?.persona?.login, font), prmsHeaderHoja)
                }
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("", font), prmsHeaderHoja)

            }else{
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("TRÁMITE N°.", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("F.CREACIÓN ", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("F.ENVIO", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("F.RECEPCIÓN", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("T.TRANSCURRIDO", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("DE", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("ASUNTO", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("PARA", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("CONTESTACIÓN-RETRASO", fontBold), prmsHeaderHoja)


                reportesPdfService.addCellTabla(tablaTramites, new Paragraph(pdt.tramite.codigo, font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph(pdt.fechaCreacion?pdt.fechaCreacion.format("dd-MM-yyyy HH:mm"):"", font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph(pdt.fechaEnvio?pdt.fechaEnvio.format("dd-MM-yyyy HH:mm"):"", font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph(pdt.fechaRecepcion?pdt.fechaRecepcion.format("dd-MM-yyyy HH:mm"):"", font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph(diasTrans, fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("DE", font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph(pdt.tramite.asunto, font), prmsHeaderHoja)
                if(pdt?.departamento){
                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph(pdt?.departamento?.codigo, font), prmsHeaderHoja)
                }else{
                    reportesPdfService.addCellTabla(tablaTramites, new Paragraph((pdt?.persona?.departamento?.codigo) + ' - ' + pdt?.persona?.login, font), prmsHeaderHoja)
                }
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("", font), prmsHeaderHoja)

            }



            if (hijos.size() > 0) {
                hijos.each { hijo ->
                    makeTableExtended(tablaTramites, hijo, departamento,1)
                }
            }
        }

    }

    private Phrase tramiteInfo(PersonaDocumentoTramite tramiteParaInfo) {
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
            paraStr = tramiteParaInfo.departamento ?
                    tramiteParaInfo.departamento.descripcion + ", " :
                    tramiteParaInfo.persona.departamento.codigo + ":" + tramiteParaInfo.persona.login + ", "
        }
        def phraseInfo = new Phrase()
        phraseInfo.add(new Chunk("", fontSmallBold))
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


    def reporteGestion4 () {
        def departamento = Departamento.get(params.id)

        def desde = new Date().parse("dd-MM-yyyy", params.desde)
        def hasta= new Date().parse("dd-MM-yyyy", params.hasta)

        def baos = new ByteArrayOutputStream()
        def name = "gestion_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Document document = reportesPdfService.crearDocumento('h', [top: 2, right: 2, bottom: 1.5, left: 2.5])
        def pdfw = PdfWriter.getInstance(document, baos);
        reportesPdfService.membrete(document)
        document.open();

        reportesPdfService.crearEncabezado(document, "REPORTE DE GESTIÓN DE TRÁMITES DEL DPTO:  ${departamento?.descripcion}")

        //los tramites dirigidos al dpto (para y copia)
        if (departamento) {
            PersonaDocumentoTramite.withCriteria {
//                isNotNull('fechaEnvio')
                eq("departamento", departamento)
                or{
                    eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R001'))
                    eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R002'))
                }
                tramite {
                    ge('fechaCreacion', desde)
                    le('fechaCreacion', hasta)
                }
            }.each { tram->
                def tramite = tram.tramite

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

                PdfPTable tablaTramites = new PdfPTable(9);
                tablaTramites.setWidthPercentage(100);
                tablaTramites.setWidths(arregloEnteros([22, 20, 18, 20, 15, 15, 15, 15, 15]))
                tablaTramites.setSpacingBefore(5)

                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("DOC PRINCIPAL :", fontBold), prmsHeaderHoja2)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph(Tramite.get(principal?.id).codigo, font), prmsHeaderHoja2)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("ASUNTO :", fontBold), prmsHeaderHoja2)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph(Tramite.get(principal?.id).asunto, font), prmsHeaderHoja3)

                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("", fontBold), prmsHeaderHoja9)

                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("TRÁMITE N°.", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("F.CREACIÓN ", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("F.ENVIO", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("F.RECEPCIÓN", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("T.TRANSCURRIDO", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("DE", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("ASUNTO", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("PARA", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("CONTESTACIÓN-RETRASO", fontBold), prmsHeaderHoja)

                def de = ""
                if(tramite.deDepartamento) {
                    de = tramite.deDepartamento.codigo
                } else {
                    de = tramite.de.login
                }

                reportesPdfService.addCellTabla(tablaTramites, new Paragraph(tramite.codigo, font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph(tramite.fechaCreacion?tramite.fechaCreacion.format('dd-MM-yyyy HH:mm'):"", font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("F.ENVIO", font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("F.RECEPCIÓN", font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("T.TRANSCURRIDO", font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph(de, font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph(tramite.asunto, font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("PARA", font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTramites, new Paragraph("CONTESTACIÓN-RETRASO", font), prmsHeaderHoja)



                document.add(tablaTramites)
            }
        }



        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)

    }



    def reporteGestion () {

//        println("params " + params)
//        def persona = Persona.get(params.id)
        def departamento = Departamento.get(params.id)
//        println("triangulo " + Persona.get(params.id).esTriangulo())

        def sql = ''
        def result = []
        def cn = dbConnectionService.getConnection();
        def tramite2
        def tramiteContes
        def prtr
        def prtrContes
        def principal
        def recursivo

        def desde = new Date().parse("dd-MM-yyyy", params.desde)
        def hasta= new Date().parse("dd-MM-yyyy", params.hasta)
        def fecha = desde.format("dd/MM/yyyy")
        def fecha2 = hasta.format("dd/MM/yyyy")


//        if(Persona.get(params.id).esTriangulo()){


        result = PersonaDocumentoTramite.withCriteria {
//                isNotNull('fechaEnvio')
            eq("departamento", departamento)
            or{
                eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R001'))
                eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R002'))
            }
            tramite {
                ge('fechaCreacion', desde)
                le('fechaCreacion', hasta)
            }
        }

//        }else{
//            result = PersonaDocumentoTramite.withCriteria {
////                isNotNull('fechaEnvio')
//                eq("persona", Persona.get(params.id))
//                or{
//                    eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R001'))
//                    eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R002'))
//                }
//
//                tramite {
//                    ge('fechaCreacion', desde)
//                    le('fechaCreacion', hasta)
//                }
//            }
//
//        }

//        println("result" + result)

        //pdf

        def baos = new ByteArrayOutputStream()
        def name = "gestion_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times18bold = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times7bold = new Font(Font.TIMES_ROMAN, 7, Font.BOLD)
        Font times6bold = new Font(Font.TIMES_ROMAN, 6, Font.BOLD)
        Font times5bold = new Font(Font.TIMES_ROMAN, 5, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font times7normal = new Font(Font.TIMES_ROMAN, 7, Font.NORMAL)
        Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeaderHoja6 = [border: Color.WHITE, colspan: 9]
        def prmsHeaderHoja9 = [border: Color.WHITE, colspan: 9]

        def prmsHeaderHoja1 = [border: Color.WHITE, bordeTop: "1", bordeBot: "1"]
        def prmsHeaderHoja2 = [border: Color.WHITE, bordeBot: "1"]
        def prmsHeaderHoja3 = [border: Color.WHITE, bordeBot: "1", colspan: 6]
        def prmsHeaderHoja4 = [border: Color.WHITE, bordeBot: "1", colspan: 2]
        def prmsHeaderHoja7 = [border: Color.WHITE, bordeBot: "1", colspan: 4]
        def prmsHeaderHoja5 = [border: Color.WHITE, colspan: 2]
        times8boldWhite.setColor(Color.WHITE)
        times10boldWhite.setColor(Color.WHITE)
        def fonts = [times12bold     : times12bold, times10bold: times10bold, times8bold: times8bold,
                times10boldWhite: times10boldWhite, times8boldWhite: times8boldWhite, times8normal: times8normal, times18bold: times18bold]

//        com.lowagie.text.Document document
//        document = new com.lowagie.text.Document(PageSize.A4);
        Document document = reportesPdfService.crearDocumento([top: 2, right: 2, bottom: 2, left: 2.5])
        def pdfw = PdfWriter.getInstance(document, baos);
//        HeaderFooter footer1 = new HeaderFooter(new Phrase('', times8normal), true);
//        footer1.setBorder(Rectangle.NO_BORDER);
//        footer1.setAlignment(Element.ALIGN_CENTER);
////        document.setFooter(footer1);
        reportesPdfService.membrete(document)
        document.open();

        reportesPdfService.crearEncabezado(document, "REPORTE DE GESTIÓN DE TRÁMITES DEL DPTO:  ${departamento?.descripcion}")

        PdfPTable tablaTramites = new PdfPTable(9);
        tablaTramites.setWidthPercentage(100);
        tablaTramites.setWidths(arregloEnteros([22, 20, 18, 20, 15, 15, 15, 15, 15]))

        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja9)


        def filtrado
        def hijos

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

            if(it?.fechaRecepcion){

                addCellTabla(tablaTramites, new Paragraph("DOC PRINCIPAL :", times7bold), prmsHeaderHoja2)
                addCellTabla(tablaTramites, new Paragraph(Tramite.get(principal?.id).codigo, times7normal), prmsHeaderHoja2)
                addCellTabla(tablaTramites, new Paragraph("ASUNTO :", times7bold), prmsHeaderHoja2)
                addCellTabla(tablaTramites, new Paragraph(Tramite.get(principal?.id).asunto, times7normal), prmsHeaderHoja3)

                addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)

                addCellTabla(tablaTramites, new Paragraph("TRÁMITE N°.", times7bold), prmsHeaderHoja)
                addCellTabla(tablaTramites, new Paragraph("F.CREACIÓN ", times7bold), prmsHeaderHoja)
                addCellTabla(tablaTramites, new Paragraph("F.ENVIO", times7bold), prmsHeaderHoja)
                addCellTabla(tablaTramites, new Paragraph("F.RECEPCIÓN", times7bold), prmsHeaderHoja)
                addCellTabla(tablaTramites, new Paragraph("T.TRANSCURRIDO", times6bold), prmsHeaderHoja)
                addCellTabla(tablaTramites, new Paragraph("DE", times7bold), prmsHeaderHoja)
                addCellTabla(tablaTramites, new Paragraph("ASUNTO", times7bold), prmsHeaderHoja)
                addCellTabla(tablaTramites, new Paragraph("PARA", times7bold), prmsHeaderHoja)
                addCellTabla(tablaTramites, new Paragraph("CONTESTACIÓN-RETRASO", times6bold), prmsHeaderHoja)

                addCellTabla(tablaTramites, new Paragraph(tramite2?.codigo, times7normal), prmsHeaderHoja)
                addCellTabla(tablaTramites, new Paragraph(prtr?.fechaCreacion?.format("dd-MM-yyyy HH:mm"), times7normal), prmsHeaderHoja)
                addCellTabla(tablaTramites, new Paragraph(prtr?.fechaEnvio?.format("dd-MM-yyyy HH:mm"), times7normal), prmsHeaderHoja)
                addCellTabla(tablaTramites, new Paragraph(prtr?.fechaRecepcion?.format("dd-MM-yyyy HH:mm"), times7normal), prmsHeaderHoja)

                if(prtr?.fechaRecepcion && prtr?.fechaEnvio){
                    def diasTrans2 = diasLaborablesService.diasLaborablesEntre((prtr?.fechaRecepcion).clearTime(), (prtr?.fechaEnvio).clearTime())
                    def diasC2 = 0
                    if(diasTrans2[0]){
                        diasC2 = diasTrans2[1]
                    }else{
                        println("error dias " +  diasTrans2[1])
                    }
//                addCellTabla(tablaTramites, new Paragraph(g.formatNumber(number: (prtr?.fechaRecepcion - prtr?.fechaEnvio)*24, format: "###.##", locale: "ec") + " horas",times7bold), prmsHeaderHoja)
                    addCellTabla(tablaTramites, new Paragraph(g.formatNumber(number: (diasC2)*24, format: "###.##", locale: "ec") + " horas",times7bold), prmsHeaderHoja)
                }else{
                    if(prtr?.fechaEnvio){
                        def diasTrans3 = diasLaborablesService.diasLaborablesEntre((prtr?.fechaEnvio).clearTime(), new Date().clearTime())
                        def diasC3 = 0
                        if(diasTrans3[0]){
                            diasC3 = diasTrans3[1]
                        }else{
                            println("error dias " +  diasTrans3[1])
                        }
                        addCellTabla(tablaTramites, new Paragraph(g.formatNumber(number: (diasC3)*24, format: "###.##", locale: "ec") + " horas",times7bold), prmsHeaderHoja)
                    }else{
                        addCellTabla(tablaTramites, new Paragraph("No enviado",times7bold), prmsHeaderHoja)
                    }
                }

                if(tramite2?.deDepartamento){
                    addCellTabla(tablaTramites, new Paragraph((tramite2?.deDepartamento?.codigo) + ' - ' + tramite2?.de?.login, times7normal), prmsHeaderHoja)
                }else{
                    addCellTabla(tablaTramites, new Paragraph((Persona.get(tramite2?.de?.id).departamento?.codigo ?: '') + ' - ' + (tramite2?.de?.login ?: ''), times7normal), prmsHeaderHoja)
                }

                addCellTabla(tablaTramites, new Paragraph(tramite2?.asunto, times7normal), prmsHeaderHoja)

                if(prtr?.departamento){
                    if(prtr?.persona){
                        addCellTabla(tablaTramites, new Paragraph((prtr?.departamento?.codigo) + ' - ' + (prtr?.persona?.login ?: ''), times7normal), prmsHeaderHoja)
                    }else{
                        addCellTabla(tablaTramites, new Paragraph((prtr?.departamento?.codigo), times7normal), prmsHeaderHoja)
                    }
                }  else{
                    addCellTabla(tablaTramites, new Paragraph((Persona.get(prtr?.persona?.id).departamento?.codigo ?: '') + ' - ' + (prtr?.persona?.login ?: ''), times7normal), prmsHeaderHoja)
                }

                addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja9)
                addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja9)

                if(tramiteContes){

                    tramiteContes.each {

//                    println("-->" + it)


                        filtrado = PersonaDocumentoTramite.findByTramiteAndRolPersonaTramite(it,RolPersonaTramite.findByCodigo('R001'))

//                    println("filtrado "  + filtrado)


                        addCellTabla(tablaTramites, new Paragraph("CONTESTADO CON", times7bold), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph("F.CREACIÓN", times7bold), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph("F.ENVIO", times7bold), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph("F.RECEPCIÓN", times7bold), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph("T.TRANSCURRIDO", times6bold), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph("DE", times7bold), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph("ASUNTO", times7bold), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph("PARA", times7bold), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph("", times7bold), prmsHeaderHoja)

                        addCellTabla(tablaTramites, new Paragraph(it?.codigo, times7normal), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph(it?.fechaCreacion?.format("dd-MM-yyyy HH:mm"), times7normal), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph(filtrado?.fechaEnvio?.format("dd-MM-yyyy HH:mm"), times7normal), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph(filtrado?.fechaRecepcion?.format("dd-MM-yyyy HH:mm"), times7normal), prmsHeaderHoja)

                        if(filtrado?.fechaRecepcion && filtrado?.fechaEnvio){

                            def diasTrans = diasLaborablesService.diasLaborablesEntre((filtrado?.fechaRecepcion).clearTime(), (filtrado?.fechaEnvio).clearTime())
                            def diasC = 0
                            if(diasTrans[0]){
                                diasC = diasTrans[1]
                            }else{
                                println("error dias " +  diasTrans[1])
                            }
//                        addCellTabla(tablaTramites, new Paragraph(g.formatNumber(number: (filtrado?.fechaRecepcion -  filtrado?.fechaEnvio)*24, format: "###.##", locale: "ec") + " horas",times7bold), prmsHeaderHoja)
                            addCellTabla(tablaTramites, new Paragraph(g.formatNumber(number: (diasC)*24, format: "###.##", locale: "ec") + " horas",times7bold), prmsHeaderHoja)
                        }else{
                            if(filtrado?.fechaEnvio){
                                def diasTrans1 = diasLaborablesService.diasLaborablesEntre((filtrado?.fechaEnvio).clearTime(), new Date().clearTime())
                                def diasC1 = 0
                                if(diasTrans1[0]){
                                    diasC1 = diasTrans1[1]
                                }else{
                                    println("error dias " +  diasTrans1[1])
                                }
//                        addCellTabla(tablaTramites, new Paragraph(g.formatNumber(number: ((new Date()) -  filtrado?.fechaEnvio)*24, format: "###.##", locale: "ec") + " horas",times7bold), prmsHeaderHoja)
                                addCellTabla(tablaTramites, new Paragraph(g.formatNumber(number: (diasC1)*24, format: "###.##", locale: "ec") + " horas",times7bold), prmsHeaderHoja)
                            }else
                            {
                                addCellTabla(tablaTramites, new Paragraph("No enviado", times7normal), prmsHeaderHoja)
                            }
                        }

                        if(it?.deDepartamento){
                            addCellTabla(tablaTramites, new Paragraph((it?.deDepartamento?.codigo ?: ''), times7normal), prmsHeaderHoja)
                        }else{
                            addCellTabla(tablaTramites, new Paragraph((it?.de?.nombre ?: '') + ' ' + (it?.de?.apellido ?: ''), times7normal), prmsHeaderHoja)
                        }
                        addCellTabla(tablaTramites, new Paragraph(it?.asunto, times7normal), prmsHeaderHoja)

                        if(filtrado?.departamento){
                            addCellTabla(tablaTramites, new Paragraph(filtrado?.departamento?.codigo, times7normal), prmsHeaderHoja)
                        }else{
                            addCellTabla(tablaTramites, new Paragraph(filtrado?.persona?.login, times7normal), prmsHeaderHoja)
                        }

                        addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                        addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                        addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                        addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                        addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)


                        //hijos

                        def res = false

                        hijos = Tramite.findAllByAQuienContesta(filtrado)

                        hijos.each{

                            if(!res){
                                def roles = [RolPersonaTramite.findByCodigo("R001"),RolPersonaTramite.findByCodigo("R002")]

                                def rec = PersonaDocumentoTramite.findByTramiteAndRolPersonaTramiteInList(it,roles)


                                rec.each{r ->
                                    if(r){
                                        if((r?.departamento?.codigo == departamento?.codigo) || (Persona.get(r?.persona?.id)?.departamento?.codigo == departamento?.codigo)){
                                            addCellTabla(tablaTramites, new Paragraph("TRAMITE", times7bold), prmsHeaderHoja)
                                            addCellTabla(tablaTramites, new Paragraph("F.CREACIÓN", times7bold), prmsHeaderHoja)
                                            addCellTabla(tablaTramites, new Paragraph("F.ENVIO", times7bold), prmsHeaderHoja)
                                            addCellTabla(tablaTramites, new Paragraph("F.RECEPCIÓN", times7bold), prmsHeaderHoja)
                                            addCellTabla(tablaTramites, new Paragraph("T.TRANSCURRIDO", times6bold), prmsHeaderHoja)
                                            addCellTabla(tablaTramites, new Paragraph("DE", times7bold), prmsHeaderHoja)
                                            addCellTabla(tablaTramites, new Paragraph("ASUNTO", times7bold), prmsHeaderHoja)
                                            addCellTabla(tablaTramites, new Paragraph("PARA", times7bold), prmsHeaderHoja)
                                            addCellTabla(tablaTramites, new Paragraph("", times7bold), prmsHeaderHoja)


                                            addCellTabla(tablaTramites, new Paragraph(r?.tramite?.codigo, times7normal), prmsHeaderHoja)
                                            addCellTabla(tablaTramites, new Paragraph('', times7bold), prmsHeaderHoja)
                                            addCellTabla(tablaTramites, new Paragraph('', times7bold), prmsHeaderHoja)
                                            addCellTabla(tablaTramites, new Paragraph('', times7bold), prmsHeaderHoja)
                                            addCellTabla(tablaTramites, new Paragraph('', times7bold), prmsHeaderHoja)
                                            addCellTabla(tablaTramites, new Paragraph('', times7bold), prmsHeaderHoja)
                                            addCellTabla(tablaTramites, new Paragraph('', times7bold), prmsHeaderHoja)
                                            addCellTabla(tablaTramites, new Paragraph('', times7bold), prmsHeaderHoja)
                                            if(r?.departamento){
                                                addCellTabla(tablaTramites, new Paragraph(r?.departamento?.codigo, times7normal), prmsHeaderHoja)
                                            }else{
                                                addCellTabla(tablaTramites, new Paragraph(r?.persona?.login, times7normal), prmsHeaderHoja)
                                            }

                                            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                                            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                                            addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                                        }



                                        res=false

                                    }else{
                                        res = true
                                    }

                                }

                            }

                        }

                    }

                }else{

//                addCellTabla(tablaTramites, new Paragraph("TRÁMITE CONTESTA N°.", times8bold), prmsHeaderHoja2)
//                addCellTabla(tablaTramites, new Paragraph("FECHA CREACIÓN", times8bold), prmsHeaderHoja2)
//                addCellTabla(tablaTramites, new Paragraph("DE OFICINA", times8bold), prmsHeaderHoja2)
//                addCellTabla(tablaTramites, new Paragraph("CREADO POR", times8bold), prmsHeaderHoja2)
//                addCellTabla(tablaTramites, new Paragraph("ASUNTO", times8bold), prmsHeaderHoja2)
//                addCellTabla(tablaTramites, new Paragraph("FECHA DE ENVIO", times8bold), prmsHeaderHoja2)

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



                    addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                    addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                    addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                    addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                    addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                    addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                    addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                }

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


    def reporteGestion2 () {

        def departamento = Departamento.get(params.id)


        def desde = new Date().parse("dd-MM-yyyy", params.desde)
        def hasta= new Date().parse("dd-MM-yyyy", params.hasta)
        def fecha = desde.format("dd/MM/yyyy")
        def fecha2 = hasta.format("dd/MM/yyyy")
        def result

        result = PersonaDocumentoTramite.withCriteria {
            eq("departamento", departamento)
            or{
                eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R001'))
                eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R002'))
            }
            tramite {
                ge('fechaCreacion', desde)
                le('fechaCreacion', hasta)
            }
        }


        //pdf

        def baos = new ByteArrayOutputStream()
        def name = "gestion_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
        Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font times18bold = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
        Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        Font times7bold = new Font(Font.TIMES_ROMAN, 7, Font.BOLD)
        Font times6bold = new Font(Font.TIMES_ROMAN, 6, Font.BOLD)
        Font times5bold = new Font(Font.TIMES_ROMAN, 5, Font.BOLD)
        Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
        Font times7normal = new Font(Font.TIMES_ROMAN, 7, Font.NORMAL)
        Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
        Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeaderHoja6 = [border: Color.WHITE, colspan: 9]
        def prmsHeaderHoja9 = [border: Color.WHITE, colspan: 9]

        def prmsHeaderHoja1 = [border: Color.WHITE, bordeTop: "1", bordeBot: "1"]
        def prmsHeaderHoja2 = [border: Color.WHITE, bordeBot: "1"]
        def prmsHeaderHoja3 = [border: Color.WHITE, bordeBot: "1", colspan: 6]
        def prmsHeaderHoja4 = [border: Color.WHITE, bordeBot: "1", colspan: 2]
        def prmsHeaderHoja7 = [border: Color.WHITE, bordeBot: "1", colspan: 4]
        def prmsHeaderHoja5 = [border: Color.WHITE, colspan: 2]
        times8boldWhite.setColor(Color.WHITE)
        times10boldWhite.setColor(Color.WHITE)
        def fonts = [times12bold     : times12bold, times10bold: times10bold, times8bold: times8bold,
                times10boldWhite: times10boldWhite, times8boldWhite: times8boldWhite, times8normal: times8normal, times18bold: times18bold]
        Document document = reportesPdfService.crearDocumento([top: 2, right: 2, bottom: 2, left: 2.5])
        def pdfw = PdfWriter.getInstance(document, baos);
        reportesPdfService.membrete(document)
        document.open();

        reportesPdfService.crearEncabezado(document, "REPORTE DE GESTIÓN DE TRÁMITES DEL DPTO:  ${departamento?.descripcion}")

        PdfPTable tablaTramites = new PdfPTable(9);
        tablaTramites.setWidthPercentage(100);
        tablaTramites.setWidths(arregloEnteros([22, 20, 18, 20, 15, 15, 15, 15, 15]))

        addCellTabla(tablaTramites, new Paragraph(" ", times8bold), prmsHeaderHoja9)


        def tramite2
        def prtr
        def principal
        def prtr2


        result.each {

            tramite2 = PersonaDocumentoTramite.get(it.id).tramite
            prtr = PersonaDocumentoTramite.get(it.id)

//            tramiteContes = Tramite.findAllByPadreAndEstadoTramiteNotEqual(tramite2,EstadoTramite.findByCodigo('E006'))




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

                //2

                def rolPara = RolPersonaTramite.findByCodigo("R001")
                def rolCc = RolPersonaTramite.findByCodigo("R002")

                def paras = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(principal, rolPara)
                def ccs = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(principal, rolCc)

                //esto muestra una hoja por destinatario
                paras.each { para ->
                    def hijos = Tramite.findAllByAQuienContesta(para, [sort: "fechaCreacion", order: "asc"])

                    def prtr3 = PersonaDocumentoTramite.get(para?.id)

                    def trmt3 = Tramite.get(prtr3?.tramite?.id)

                    def principal2

                    //pdf

                    if (trmt3) {
                        principal2 = trmt3
                        if (trmt3.padre) {
                            principal2 = trmt3.padre
                            while (true) {
                                if (!principal2.padre)
                                    break
                                else {
                                    principal2 = principal2.padre
                                }
                            }
                        }
                    }


                    if(para?.fechaRecepcion){

                        addCellTabla(tablaTramites, new Paragraph("DOC PRINCIPAL :", times7bold), prmsHeaderHoja2)
                        addCellTabla(tablaTramites, new Paragraph(Tramite.get(principal2?.id).codigo, times7normal), prmsHeaderHoja2)
                        addCellTabla(tablaTramites, new Paragraph("ASUNTO :", times7bold), prmsHeaderHoja2)
                        addCellTabla(tablaTramites, new Paragraph(Tramite.get(principal2?.id).asunto, times7normal), prmsHeaderHoja3)

                        addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)
                        addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja6)

                        addCellTabla(tablaTramites, new Paragraph("TRÁMITE N°.", times7bold), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph("F.CREACIÓN ", times7bold), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph("F.ENVIO", times7bold), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph("F.RECEPCIÓN", times7bold), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph("T.TRANSCURRIDO", times6bold), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph("DE", times7bold), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph("ASUNTO", times7bold), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph("PARA", times7bold), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph("CONTESTACIÓN-RETRASO", times6bold), prmsHeaderHoja)

                        addCellTabla(tablaTramites, new Paragraph(trmt3?.codigo, times7normal), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph(prtr3?.fechaCreacion?.format("dd-MM-yyyy HH:mm"), times7normal), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph(prtr3?.fechaEnvio?.format("dd-MM-yyyy HH:mm"), times7normal), prmsHeaderHoja)
                        addCellTabla(tablaTramites, new Paragraph(prtr3?.fechaRecepcion?.format("dd-MM-yyyy HH:mm"), times7normal), prmsHeaderHoja)

                        if(prtr3?.fechaRecepcion && prtr3?.fechaEnvio){
                            def diasTrans2 = diasLaborablesService.diasLaborablesEntre((prtr3?.fechaRecepcion).clearTime(), (prtr3?.fechaEnvio).clearTime())
                            def diasC2 = 0
                            if(diasTrans2[0]){
                                diasC2 = diasTrans2[1]
                            }else{
                                println("error dias " +  diasTrans2[1])
                            }
                            addCellTabla(tablaTramites, new Paragraph(g.formatNumber(number: (diasC2)*24, format: "###.##", locale: "ec") + " horas",times7bold), prmsHeaderHoja)
                        }else{
                            if(prtr?.fechaEnvio){
                                def diasTrans3 = diasLaborablesService.diasLaborablesEntre((prtr3?.fechaEnvio).clearTime(), new Date().clearTime())
                                def diasC3 = 0
                                if(diasTrans3[0]){
                                    diasC3 = diasTrans3[1]
                                }else{
                                    println("error dias " +  diasTrans3[1])
                                }
                                addCellTabla(tablaTramites, new Paragraph(g.formatNumber(number: (diasC3)*24, format: "###.##", locale: "ec") + " horas",times7bold), prmsHeaderHoja)
                            }else{
                                addCellTabla(tablaTramites, new Paragraph("No enviado",times7bold), prmsHeaderHoja)
                            }
                        }

                        if(trmt3?.deDepartamento){
                            addCellTabla(tablaTramites, new Paragraph((trmt3?.deDepartamento?.codigo) + ' - ' + trmt3?.de?.login, times7normal), prmsHeaderHoja)
                        }else{
                            addCellTabla(tablaTramites, new Paragraph((Persona.get(trmt3?.de?.id).departamento?.codigo ?: '') + ' - ' + (trmt3?.de?.login ?: ''), times7normal), prmsHeaderHoja)
                        }

                        addCellTabla(tablaTramites, new Paragraph(trmt3?.asunto, times7normal), prmsHeaderHoja)

                        if(prtr3?.departamento){
                            if(prtr3?.persona){
                                addCellTabla(tablaTramites, new Paragraph((prtr3?.departamento?.codigo) + ' - ' + (prtr3?.persona?.login ?: ''), times7normal), prmsHeaderHoja)
                            }else{
                                addCellTabla(tablaTramites, new Paragraph((prtr3?.departamento?.codigo), times7normal), prmsHeaderHoja)
                            }
                        }  else{
                            addCellTabla(tablaTramites, new Paragraph((Persona.get(prtr3?.persona?.id).departamento?.codigo ?: '') + ' - ' + (prtr3?.persona?.login ?: ''), times7normal), prmsHeaderHoja)
                        }

                        addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja9)
                        addCellTabla(tablaTramites, new Paragraph("", times10bold), prmsHeaderHoja9)

                    }



                    if (hijos.size() > 0) {
                        hijos.each { hijo ->
//                            makeTreeExtended(document, hijo, espacio + 10)
                        }
                    }



                }

                //el para y las copias son hermanos
                ccs.each { para ->
                    def hijos = Tramite.findAllByAQuienContesta(para, [sort: "fechaCreacion", order: "asc"])




                }



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


    def recursiva (Document document, Tramite principal) {

        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCc = RolPersonaTramite.findByCodigo("R002")

        def paras = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(principal, rolPara)
        def ccs = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(principal, rolCc)

        //esto muestra una hoja por destinatario
        paras.each { para ->
            recursiva2(document, para)
        }

        //el para y las copias son hermanos
        ccs.each { para ->
            recursiva2(document, para)
        }

    }


    def recursiva2 (Document document, PersonaDocumentoTramite pdt) {

        def hijos = Tramite.findAllByAQuienContesta(pdt, [sort: "fechaCreacion", order: "asc"])





        if (hijos.size() > 0) {
            hijos.each { hijo ->
                recursiva(document, hijo)
            }
        }



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

            if(it?.fechaRecepcion){

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



                        if(it?.fechaCreacion && prtr?.fechaRecepcion){


                            XSSFRow rowHeadD = sheet.createRow((short) indexHeadD);
                            rowHeadD.setHeightInPoints(14)

                            Cell cellD = rowHeadD.createCell((int) 0)
                            cellD.setCellValue("Días transcurridos hasta la contestación")
//                    cellD.setCellStyle(styleHeaders)
                            sheet.setColumnWidth(0, 12000)

                            cellD = rowHeadD.createCell((int) 1)



                            def diasTrans = diasLaborablesService.diasLaborablesEntre((prtr?.fechaRecepcion).clearTime(), (it?.fechaCreacion).clearTime())
                            def diasC = 0
                            if(diasTrans[0]){
                                diasC = diasTrans[1]
                            }else{
                                println("error dias " +  diasTrans[1])
                            }
                            cellD.setCellValue("${diasC}")
                        }else{
//                        if(prtr?.fechaRecepcion){
//                            def diasTrans1 = diasLaborablesService.diasLaborablesEntre((new Date()).clearTime(), (prtr?.fechaRecepcion).clearTime())
//                            def diasS = 0
//                            if(diasTrans1[0]){
//                                diasS = diasTrans1[1]
//                            }else{
//                                println("error dias " +  diasTrans1[1])
//                            }
//                            cellD.setCellValue("${diasS}")
//                        }else{
//                            cellD.setCellValue("0 Días")
//                        }

                            cellD.setCellValue("Sin contestación")

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
                    cellD.setCellValue("Sin contestación")
//                cellD.setCellStyle(styleHeaders)
                    sheet.setColumnWidth(0, 12000)

                    cellD = rowHeadD.createCell((int) 1)


//                if(prtr?.fechaRecepcion){
//                    def diasTrans2 = diasLaborablesService.diasLaborablesEntre((new Date()).clearTime(), (prtr?.fechaRecepcion).clearTime())
//                    def diasS1 = 0
//                    if(diasTrans2[0]){
//                        diasS1 = diasTrans2[1]
//                    }else{
//                        println("error dias " +  diasTrans2[1])
//                    }
//                    cellD.setCellValue("${diasS1}")
//                }else{
//                    cellD.setCellValue("0 Días")
//                }

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
