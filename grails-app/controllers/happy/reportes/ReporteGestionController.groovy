package happy.reportes

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.Phrase
import com.lowagie.text.pdf.PdfWriter
import happy.tramites.Departamento
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite

class ReporteGestionController extends happy.seguridad.Shield {

    def index() {}

    def reportesPdfService
    def diasLaborablesService

    Font font = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
    Font fontBold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);

    def prmsTablaHoja = []
    def prmsTablaHojaCenter = [align: Element.ALIGN_CENTER]
    def prmsHeaderHoja = []
    def prmsHeaderHoja2 = [colspan: 2]
    def prmsHeaderHoja5 = [colspan: 5]
    def prmsHeaderHoja6 = [colspan: 6]
    def prmsHeaderHoja9 = [colspan: 9]

    def reporteGestion5() {

        def departamento = Departamento.get(params.id)
        def departamentos = [departamento]

        def desde = new Date().parse("dd-MM-yyyy", params.desde)
        def hasta = new Date().parse("dd-MM-yyyy", params.hasta)

        desde = desde.format("yyyy/MM/dd")
        hasta = hasta.format("yyyy/MM/dd")

        def baos = new ByteArrayOutputStream()
        def name = "gestion_" + departamento.codigo + "_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Document document = reportesPdfService.crearDocumento('h', [top: 2, right: 2, bottom: 1.5, left: 2])
        def pdfw = PdfWriter.getInstance(document, baos);
        def titulo = "Reporte de gestión de trámites del dpto. ${departamento.descripcion} del ${params.desde} al ${params.hasta}"

        session.tituloReporte = titulo
        reportesPdfService.membrete(document)
        document.open();
        reportesPdfService.propiedadesDocumento(document, "gestion")

        def tramiteitor = reportesPdfService.reporteGestion(desde, hasta, departamento.id)

        def tablaTramite = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([10, 10, 7, 7, 7, 7, 10, 10, 29, 10, 8]), 15, 0)
        rowHeaderTramite(tablaTramite, false)

        tramiteitor.each {
                llenaTablaGestion(it, tablaTramite)
                tablaTramite.setKeepTogether(true)
        }

        document.add(tablaTramite);


//                reportesPdfService.addCellTabla(tablaTramite, new Paragraph("DOC PRINCIPAL :", fontBold), prmsHeaderHoja)
//                reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it.trmtcdgo, font), prmsHeaderHoja2)
//                reportesPdfService.addCellTabla(tablaTramite, new Paragraph("ASUNTO :", fontBold), prmsHeaderHoja)
//                reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.trmtasnt, font), prmsHeaderHoja5)

        //los tramites dirigidos al dpto (para y copia)
//        if (departamento) {
//            def tramitesPrincipales = []

//            PersonaDocumentoTramite.withCriteria {
//                inList("departamento", departamentos)
//                or {
//                    eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R001'))
//                    eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R002'))
//                }
//                tramite {
//                    ge('fechaCreacion', desde)
//                    le('fechaCreacion', hasta)
//                }
//            }.each { prtr ->
//                def tramite = prtr.tramite
//                def principal = tramite
//                if (tramite.padre) {
//                    principal = tramite.padre
//                    while (true) {
//                        if (!principal.padre)
//                            break
//                        else {
//                            principal = principal.padre
//                        }
//                    }
//                }
//                if (!tramitesPrincipales.contains(principal)) {
//                    tramitesPrincipales += principal
//                }
//            }

//            tramitesPrincipales.each { principal ->
//                def pdts = PersonaDocumentoTramite.withCriteria {
//                    eq("tramite", principal)
//                    or {
//                        eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R001'))
//                        eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R002'))
//                    }
//                }
//                def tablaTramite = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([12, 7, 7, 7, 10, 10, 29, 10, 8]), 15, 0)
//                reportesPdfService.addCellTabla(tablaTramite, new Paragraph("DOC PRINCIPAL :", fontBold), prmsHeaderHoja)
//                reportesPdfService.addCellTabla(tablaTramite, new Paragraph(principal.codigo, font), prmsHeaderHoja2)
//                reportesPdfService.addCellTabla(tablaTramite, new Paragraph("ASUNTO :", fontBold), prmsHeaderHoja)
//                reportesPdfService.addCellTabla(tablaTramite, new Paragraph(principal.asunto, font), prmsHeaderHoja5)
//                rowHeaderTramite(tablaTramite, false)
//
//                pdts.each { prtr ->
//                    if ((departamentos.id).contains(prtr.departamentoId)) {
//                        rowTramite(prtr, tablaTramite)
//                    }
//                    llenaTablaTramite(prtr, tablaTramite, departamentos)
//                }
//                tablaTramite.setKeepTogether(true)
//                document.add(tablaTramite)
//            }


//        }
        document.add(new Phrase("  "))
        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    def llenaTablaTramite(PersonaDocumentoTramite prtr, tablaTramite, departamentos) {
        def respuestas = Tramite.withCriteria {
            eq("aQuienContesta", prtr)
            order("fechaCreacion", "asc")
        }
        if (respuestas.size() > 0) {
            respuestas.each { h ->
                def rolPara = RolPersonaTramite.findByCodigo("R001")
                def rolCc = RolPersonaTramite.findByCodigo("R002")

                def paras = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(h, rolPara)
                def ccs = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(h, rolCc)

                (paras + ccs).each { pdt ->
                    def esInterno = false
                    if ((departamentos.id).contains(pdt.departamentoId) || (departamentos.id).contains(pdt.persona?.departamentoId) ||
                            (departamentos.id).contains(pdt.tramite.departamentoId)) {
                        rowTramite(pdt, tablaTramite)
                    }
                    llenaTablaTramite(pdt, tablaTramite, departamentos)
                }
            }
        } else {
        }
    }

    def rowHeaderTramite(tablaTramite, respuesta) {

        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Trámite Principal.", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Trámite n°.", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("F. creación", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("F. envío", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("T. creación-envio", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("F. recepción", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("T. envío-recepción", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("De", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Asunto", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Para", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("T. recepción-respuesta", fontBold), prmsHeaderHoja)
    }


    def llenaTablaGestion (it, tablaTramite){

        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it.trmtpdre ?: it.trmtcdgo, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it.trmtcdgo, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.trmtfccr ? it.trmtfccr.format('dd-MM-yyyy HH:mm') : "", font), prmsTablaHojaCenter)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.trmtfcen ? it?.trmtfcen?.format("dd-MM-yyyy HH:mm") : "", font), prmsTablaHojaCenter)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.trmttmce, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.trmtfcrc ? it?.trmtfcrc?.format("dd-MM-yyyy HH:mm") : "", font), prmsTablaHojaCenter)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.trmttmer, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.trmt__de, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.trmtasnt, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.trmtpara, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.trmttmrr, font), prmsTablaHoja)
    }

    def rowTramite(PersonaDocumentoTramite pdt, tablaTramite) {
//        println "rowTramite..........prueba de nueva función tmpoLaborableEntre...."
        def tramite = pdt.tramite

        def de, dias, para = "", codigo = tramite.codigo
        if (tramite.deDepartamento) {
            de = tramite.deDepartamento.codigo
        } else {
            de = tramite.de.login + " (${tramite.de.departamento.codigo})"
        }

        def dif
        if (pdt.fechaEnvio) {
            if (pdt.fechaRecepcion) {
//                dif = diasLaborablesService.tiempoLaborableEntre(pdt.fechaRecepcion, pdt.fechaEnvio)
                 dif = diasLaborablesService.tmpoLaborableEntre(pdt.fechaRecepcion, pdt.fechaEnvio)
            } else {
//                dif = diasLaborablesService.tiempoLaborableEntre(pdt.fechaEnvio, new Date())
                dif = diasLaborablesService.tmpoLaborableEntre(pdt.fechaEnvio, new Date())
            }
            if (dif[0]) {
                def d = dif[1]
                if (d.dias > 0) {
                    dias = "${d.dias} día${d.dias == 1 ? '' : 's'}, "
                } else {
                    dias = ""
                }
                dias += "${d.horas} hora${d.horas == 1 ? '' : 's'}, ${d.minutos} minuto${d.minutos == 1 ? '' : 's'}"
            } else {
                println "error rowTramite: " + dif
            }
        } else {
            dias = "No enviado"
        }


        if (pdt.departamento) {
            para = pdt.departamento.codigo
        } else if (pdt.persona) {
            para = pdt.persona.login + " (${pdt.persona.departamento.codigo})"
        }

        if (pdt.rolPersonaTramite.codigo == "R002") {
            codigo += " [CC]"
        }

        def contestacionRetraso = "Sin respuesta"
        def respuestas = Tramite.withCriteria {
            eq("aQuienContesta", pdt)
            order("fechaCreacion", "asc")
        }
        def dif2
        if (respuestas.size() > 0) {
            def respuesta = respuestas.last()
            if (pdt.fechaRecepcion && respuesta.fechaCreacion) {
//                dif2 = diasLaborablesService.tiempoLaborableEntre(pdt.fechaRecepcion, respuesta.fechaCreacion)
                dif2 = diasLaborablesService.tmpoLaborableEntre(pdt.fechaRecepcion, respuesta.fechaCreacion)
            }
        } else {
            if (pdt.fechaRecepcion) {
//                dif2 = diasLaborablesService.tiempoLaborableEntre(pdt.fechaRecepcion, new Date())
                dif2 = diasLaborablesService.tmpoLaborableEntre(pdt.fechaRecepcion, new Date())
            }
        }
        if (dif2) {
            if (dif2[0]) {
                def d = dif2[1]
                if (d.dias > 0) {
                    contestacionRetraso = "${d.dias} día${d.dias == 1 ? '' : 's'}, "
                } else {
                    contestacionRetraso = ""
                }
                contestacionRetraso += "${d.horas} hora${d.horas == 1 ? '' : 's'}, ${d.minutos} minuto${d.minutos == 1 ? '' : 's'}"
            } else {
                println "error: rowTramite2" + dif2
            }
        } else {
            contestacionRetraso = "No recibido"
        }

        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(codigo, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(tramite.fechaCreacion ? tramite.fechaCreacion.format('dd-MM-yyyy HH:mm') : "", font), prmsTablaHojaCenter)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(pdt.fechaEnvio ? pdt.fechaEnvio.format("dd-MM-yyyy HH:mm") : "", font), prmsTablaHojaCenter)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(pdt.fechaRecepcion ? pdt.fechaRecepcion.format("dd-MM-yyyy HH:mm") : "", font), prmsTablaHojaCenter)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(dias, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(de, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(tramite.asunto, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(para, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(contestacionRetraso, font), prmsTablaHoja)
    }
}
