package happy.reportes

import com.lowagie.text.Document
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfWriter
import happy.tramites.Departamento
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite

class ReporteGestionController extends happy.seguridad.Shield {

    def index() {}

    def reportesPdfService
    def diasLaborablesService

    Font font = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL);
    Font fontBold = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);

    def prmsTablaHoja = []
    def prmsHeaderHoja = []
    def prmsHeaderHoja2 = []
    def prmsHeaderHoja6 = [colspan: 6]
    def prmsHeaderHoja9 = [colspan: 9]

    def reporteGestion4() {
        def departamento = Departamento.get(params.id)

        def desde = new Date().parse("dd-MM-yyyy", params.desde)
        def hasta = new Date().parse("dd-MM-yyyy", params.hasta)

        def baos = new ByteArrayOutputStream()
        def name = "gestion_" + departamento.codigo + "_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Document document = reportesPdfService.crearDocumento('h', [top: 2, right: 2, bottom: 1.5, left: 2])
        def pdfw = PdfWriter.getInstance(document, baos);
        reportesPdfService.membrete(document)
        document.open();

        reportesPdfService.crearEncabezado(document, "REPORTE DE GESTIÓN DE TRÁMITES DEL DPTO:  ${departamento?.descripcion}")

        //los tramites dirigidos al dpto (para y copia)
        if (departamento) {
            def tramitesPrincipales = []

            PersonaDocumentoTramite.withCriteria {
//                isNotNull('fechaEnvio')
                eq("departamento", departamento)
                or {
                    eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R001'))
                    eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R002'))
                }
                tramite {
                    ge('fechaCreacion', desde)
                    le('fechaCreacion', hasta)
                }
            }.each { prtr ->
                def tramite = prtr.tramite
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
                if (!tramitesPrincipales.contains(principal)) {
                    tramitesPrincipales += principal
                }
            }
//            println tramitesPrincipales

            tramitesPrincipales.each { principal ->
//                if (principal.numero == 11) {
//                    println principal.codigo
                def pdts = PersonaDocumentoTramite.withCriteria {
                    eq("tramite", principal)
                    or {
                        eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R001'))
                        eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R002'))
                    }
                }
                def tablaTramite = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([22, 20, 18, 20, 15, 15, 15, 15, 15]), 15, 0)

                reportesPdfService.addCellTabla(tablaTramite, new Paragraph("DOC PRINCIPAL :", fontBold), prmsHeaderHoja2)
                reportesPdfService.addCellTabla(tablaTramite, new Paragraph(principal.codigo, font), prmsHeaderHoja2)
                reportesPdfService.addCellTabla(tablaTramite, new Paragraph("ASUNTO :", fontBold), prmsHeaderHoja2)
                reportesPdfService.addCellTabla(tablaTramite, new Paragraph(principal.asunto, font), prmsHeaderHoja6)

                pdts.each { prtr ->
//                    if (principal.id != prtr.tramite.id) {
                    if (prtr.departamentoId == departamento.id) {
                        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("", fontBold), prmsHeaderHoja9)
                        rowHeaderTramite(tablaTramite, false)
                        rowTramite(prtr, tablaTramite)
                    }
                    llenaTablaTramite(prtr, tablaTramite, departamento, 0)
                }
                document.add(tablaTramite)
//                }
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

    def llenaTablaTramite(PersonaDocumentoTramite prtr, tablaTramite, Departamento departamento, int lvl) {
        def respuestas = Tramite.withCriteria {
            eq("aQuienContesta", prtr)
//            eq("departamento", departamento)
            order("fechaCreacion", "asc")
        }
        if (respuestas.size() > 0) {
            respuestas.each { h ->
                def rolPara = RolPersonaTramite.findByCodigo("R001")
                def rolCc = RolPersonaTramite.findByCodigo("R002")

                def paras = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(h, rolPara)
                def ccs = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(h, rolCc)

                (paras + ccs).each { pdt ->
                    def esInterno = pdt.tramite.departamentoId == departamento.id
                    if (pdt.departamentoId == departamento.id || pdt.persona?.departamentoId == departamento.id) {
                        rowHeaderTramite(tablaTramite, esInterno)
                        rowTramite(pdt, tablaTramite)
                    }
                    llenaTablaTramite(pdt, tablaTramite, departamento, lvl + 1)
                    if (pdt.departamentoId == departamento.id || pdt.persona?.departamentoId == departamento.id) {
                        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Fin ${pdt.tramite.codigo} (regresa a ${prtr.tramite.codigo})", fontBold), prmsHeaderHoja9)
                    }
                }
            }
        } else {
//            if (prtr.departamentoId == departamento.id || prtr.persona?.departamentoId == departamento.id) {
//                reportesPdfService.addCellTabla(tablaTramite, new Paragraph("fin ${prtr.tramite.codigo}", fontBold), prmsHeaderHoja9)
//            }
        }
    }

    def rowHeaderTramite(tablaTramite, respuesta) {
        if (respuesta) {
            reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Contestado con", fontBold), prmsHeaderHoja)
        } else {
            reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Trámite n°.", fontBold), prmsHeaderHoja)
        }
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("F. creación", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("F. envío", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("F. recepción", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("T. transcurrido", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("De", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Asunto", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Para", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Contestación-retraso", fontBold), prmsHeaderHoja)
    }

    def rowTramite(PersonaDocumentoTramite pdt, tablaTramite) {
        def tramite = pdt.tramite

        def de, dias, para = "", codigo = tramite.codigo
        if (tramite.deDepartamento) {
            de = tramite.deDepartamento.codigo
        } else {
            de = tramite.de.login + " (${tramite.de.departamento.codigo})"
        }

        if (pdt.fechaRecepcion && pdt.fechaEnvio) {
            def diasTrans2 = diasLaborablesService.diasLaborablesEntre((pdt.fechaRecepcion).clearTime(), (pdt?.fechaEnvio).clearTime())
            def diasC2 = 0
            if (diasTrans2[0]) {
                diasC2 = diasTrans2[1]
            } else {
                println("error dias " + diasTrans2[1])
            }
            dias = g.formatNumber(number: (diasC2) * 24, format: "###.##", locale: "ec") + " horas"
        } else {
            if (pdt.fechaEnvio) {
                def diasTrans3 = diasLaborablesService.diasLaborablesEntre((pdt?.fechaEnvio).clearTime(), new Date().clearTime())
                def diasC3 = 0
                if (diasTrans3[0]) {
                    diasC3 = diasTrans3[1]
                } else {
                    println("error dias " + diasTrans3[1])
                }
                dias = g.formatNumber(number: (diasC3) * 24, format: "###.##", locale: "ec") + " horas"
            } else {
                dias = "No enviado"
            }
        }

        if (pdt.departamento) {
            para = pdt.departamento.codigo
        } else if (pdt.persona) {
            para = pdt.persona.login + " (${pdt.persona.departamento.codigo})"
        }

        if (pdt.rolPersonaTramite.codigo == "R002") {
            codigo += " [CC]"
        }

        def contestacionRetraso = 0
        // f. recep - fecha actual (no hay contestacion)
        // f.recep - fecha creacion contestacion

        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(codigo, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(tramite.fechaCreacion ? tramite.fechaCreacion.format('dd-MM-yyyy HH:mm') : "", font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(pdt.fechaEnvio ? pdt.fechaEnvio.format("dd-MM-yyyy HH:mm") : "", font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(pdt.fechaRecepcion ? pdt.fechaRecepcion.format("dd-MM-yyyy HH:mm") : "", font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(dias, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(de, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(tramite.asunto, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(para, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("contestacionRetraso", font), prmsTablaHoja)
    }
}
