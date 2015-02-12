package happy.reportes


import com.lowagie.text.pdf.DefaultFontMapper
import com.lowagie.text.pdf.PdfContentByte
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfTemplate
import happy.seguridad.Shield
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

class RetrasadosController extends Shield {
    def reportesPdfService
    def maxLvl = null
    def maxLvl2 = null

    static scope = "session"

    Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
    Font times18bold = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
    Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
    Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
    Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
    def datosGrafico = [:]


    def reporteRetrasadosDetalle() {
//        println "aqui"
        maxLvl = null
        def estadoR = EstadoTramite.findByCodigo("E004")
        def estadoE = EstadoTramite.findByCodigo("E003")
        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCopia = RolPersonaTramite.findByCodigo("R002")
        def now = new Date()

        def datos = [:]
        def usuario = null
        def deps = []
        def puedeVer = []
        def extraPersona = "and "
        def depStr = ""
        if (params.prsn) {
            usuario = Persona.get(params.prsn)
            extraPersona += "persona=" + usuario.id + " "
            if (usuario.esTriangulo)
                extraPersona = "and (persona=${usuario.id} or departamento = ${usuario.departamento.id})"
            def padre = usuario.departamento.padre
            while (padre) {
                deps.add(padre)
                padre = padre.padre
            }
            deps.add(usuario.departamento)
            puedeVer.add(usuario.departamento)
            def hi = Departamento.findAllByPadre(usuario.departamento)
            while (hi.size() > 0) {
                puedeVer += hi
                hi = Departamento.findAllByPadreInList(hi)
            }

        }

        if (params.dpto) {
            def departamento = Departamento.get(params.dpto)
            def padre = departamento.padre
            while (padre) {
                deps.add(padre)
                padre = padre.padre
            }
            deps.add(departamento)
            puedeVer.add(departamento)
            def hi = Departamento.findAllByPadre(departamento)
            while (hi.size() > 0) {
                puedeVer += hi
                hi = Departamento.findAllByPadreInList(hi)
            }
        }

        def pdt = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite where" +
                " fechaEnvio is not null " +
                "and rolPersonaTramite in (${rolPara.id},${rolCopia.id}) " +
                "and estado in (${estadoR.id},${estadoE.id}) ${usuario ? extraPersona : ''} ")

        if (pdt) {
            pdt.each { pd ->
                pd.refresh()
                if (pd.tramite.externo != "1" || pd.tramite == null) {
                    def resp = Tramite.findAllByAQuienContesta(pd)
                    if (resp.size() == 0) {
                        if (pd.fechaLimite < now || (!pd.fechaRecepcion))
                            datos = reportesPdfService.jerarquia(datos, pd)
                    }
                }
            }
        }

        def baos = new ByteArrayOutputStream()
        def name = "reporteTramitesRetrasados_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeaderHoja1 = [border: Color.WHITE, bordeTop: "1", bordeBot: "1"]
        times8boldWhite.setColor(Color.WHITE)
        times10boldWhite.setColor(Color.WHITE)
        Document document = reportesPdfService.crearDocumento("svt", [top: 2, right: 2, bottom: 1.5, left: 2.5])
        session.tituloReporte = "Reporte detallado de Trámites Retrasados y sin recepción"
        def pdfw = PdfWriter.getInstance(document, baos);
        reportesPdfService.membrete(document)
        document.open();
        reportesPdfService.propiedadesDocumento(document, "reporteTramitesRetrasados")

        def contenido = new Paragraph();
        def hijos = datos["hijos"]
        if (datos) {
            if ((puedeVer.id.contains(datos["objeto"].id))) {
                maxLvl = datos
            }
        }

        def total = 0
        def totalSr = 0
        PdfPTable tablaTramites
        hijos.each { lvl ->
            if (puedeVer.size() == 0 || (puedeVer.id.contains(lvl["objeto"].id))) {
                if (maxLvl == null) {
                    maxLvl = lvl
                }
                def totalNode = 0
                def totalNodeSr = 0
                def par = new Paragraph("-" + lvl["objeto"], times12bold)
                document.add(par)
                def par2 = new Paragraph("", times8normal)
                par2.setSpacingBefore(4)
                def par3 = new Paragraph("", times8normal)
                par3.setSpacingBefore(4)
                if (lvl["tramites"].size() > 0) {
                    lvl["triangulos"].each { t ->
                        par = new Paragraph("Usuario: ${t.departamento.codigo}:" + t + " - Trámites de oficina  - [  Retrasados: ${lvl['ofiRs']}, Sin Recepción: " + lvl["ofiRz"] + " ]", times8bold)
                        if (totalNode == 0)
                            totalNode += lvl["rezagados"]
                        if (totalNodeSr == 0)
                            totalNodeSr += lvl["retrasados"]
                        document.add(par)
                    }

                }
                if (params.detalle) {
                    tablaTramites = new PdfPTable(9);
                    tablaTramites.setWidthPercentage(100);
                    par = new Paragraph("Nro.", times8bold)
                    PdfPCell cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("F. Creación", times8bold)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("De", times8bold)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("Creado por", times8bold)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("F. Envío", times8bold)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("F. Recepcíon", times8bold)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("F. Límite", times8bold)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("Retraso (días)", times8bold)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("Tipo", times8bold)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    lvl["tramites"].each { t ->
                        par = new Paragraph("${t.tramite.codigo} ${t.rolPersonaTramite.codigo == 'R002' ? '[CC]' : ''}", times8normal)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("${t.tramite.fechaCreacion.format('dd-MM-yyyy HH:mm')}", times8normal)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        if (t.tramite.deDepartamento) {
                            par = new Paragraph("${t.tramite.deDepartamento.codigo}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                        } else {
                            par = new Paragraph("${t.tramite.de.departamento.codigo}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                        }
                        par = new Paragraph("${t.tramite.de.login}", times8normal)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("${t.fechaEnvio.format('dd-MM-yyyy hh:mm')}", times8normal)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("${(t.fechaRecepcion) ? t.fechaRecepcion?.format('dd-MM-yyyy hh:mm') : ''}", times8normal)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("${(t.fechaLimiteRespuesta) ? t.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm') : ''}", times8normal)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("${(t.fechaLimiteRespuesta) ? (now - t.fechaLimiteRespuesta) : ''}", times8normal)
                        cell = new PdfPCell(par);
                        cell.setHorizontalAlignment(1)
                        tablaTramites.addCell(cell);
                        par = new Paragraph("${(t.fechaLimiteRespuesta) ? 'Retrasado' : 'No recibido'}", times8normal)
                        cell = new PdfPCell(par);
                        cell.setHorizontalAlignment(1)
                        tablaTramites.addCell(cell);

                    }
                    if (lvl["tramites"].size() > 0) {
                        par2.add(tablaTramites)
                        document.add(par2)
                    }
                }
                lvl["personas"].each { p ->
                    par3 = null
                    par3 = new Paragraph("", times8normal)
                    par3.setSpacingBefore(0.001)
                    par = new Paragraph("Usuario: ${p["objeto"].departamento.codigo}:" + p["objeto"] + " - ${p['objeto'].login} - [ Retrasados: ${p['rezagados']}, Sin Recepción: " + p["retrasados"] + " ]", times8bold)
                    par.setSpacingBefore(17)
                    document.add(par)
                    totalNode += p["rezagados"]
                    totalNodeSr += p["retrasados"]

                    if (params.detalle) {
                        tablaTramites = new PdfPTable(9);
                        tablaTramites.setWidthPercentage(100);
                        par = new Paragraph("Nro.", times8bold)
                        PdfPCell cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("F. Creación", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("De", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("Creado por", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("F. Envío", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("F. Recepcíon", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("F. Límite", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("Retraso (días)", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("Tipo", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        p["tramites"].each { t ->
                            par = new Paragraph("${t.tramite.codigo} ${t.rolPersonaTramite.codigo == 'R002' ? '[CC]' : ''}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("${t.tramite.fechaCreacion.format('dd-MM-yyyy HH:mm')}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            if (t.tramite.deDepartamento) {
                                par = new Paragraph("${t.tramite.deDepartamento.codigo}", times8normal)
                                cell = new PdfPCell(par);
                                tablaTramites.addCell(cell);
                            } else {
                                par = new Paragraph("${t.tramite.de.departamento.codigo}", times8normal)
                                cell = new PdfPCell(par);
                                tablaTramites.addCell(cell);
                            }
                            par = new Paragraph("${t.tramite.de.login}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("${t.fechaEnvio.format('dd-MM-yyyy hh:mm')}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("${(t.fechaRecepcion) ? t.fechaRecepcion?.format('dd-MM-yyyy hh:mm') : ''}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("${(t.fechaLimiteRespuesta) ? t.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm') : ''}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("${(t.fechaLimiteRespuesta) ? (now - t.fechaLimiteRespuesta) : ''}", times8normal)
                            cell = new PdfPCell(par);
                            cell.setHorizontalAlignment(1)
                            tablaTramites.addCell(cell);
                            par = new Paragraph("${(t.fechaLimiteRespuesta) ? 'Retrasado' : 'Sin recepción'}", times8normal)
                            cell = new PdfPCell(par);
                            cell.setHorizontalAlignment(1)
                            tablaTramites.addCell(cell);
                        }
                        if (p["tramites"].size() > 0) {
                            par3.add(tablaTramites)
                            document.add(par3)
                        }
                    }
                }
                total += totalNode
                totalSr += totalNodeSr

            }
            def res = imprimeHijosPdf(lvl, document, tablaTramites, params, usuario, deps, puedeVer)
            total += res[0]
            totalSr += res[1]

        }
        def par = new Paragraph(" ", times12bold)
        document.add(par);
        if (maxLvl) {
            par = new Paragraph("Gran Total                                                                                                                                          Retrasados: ${maxLvl['rezagados']}       Sin Recepción: ${maxLvl['retrasados']}     ", times12bold)
            document.add(par);
        }
        println "maxlvl " + maxLvl
        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }


    def imprimeHijosPdf(arr, contenido, tablaTramites, params, usuario, deps, puedeVer) {
        def total = 0
        def totalSr = 0
        def datos = arr["hijos"]
        def now = new Date()
        datos.each { lvl ->
            if (puedeVer.size() == 0 || (puedeVer.id.contains(lvl["objeto"].id))) {
                if (maxLvl == null)
                    maxLvl = lvl
                def par = new Paragraph("-" + lvl["objeto"], times12bold)
                def totalNode = 0
                def totalNodeSr = 0
                contenido.add(par)
                def par2 = new Paragraph("", times8normal)
                par2.setSpacingBefore(4)
                def par3 = new Paragraph("", times8normal)
                par3.setSpacingBefore(4)

                if (lvl["tramites"].size() > 0) {
                    lvl["triangulos"].each { t ->
                        par = new Paragraph("Usuario: ${t.departamento.codigo}:" + t + " - Trámites de oficina - [ Retrasados: ${lvl['ofiRz']}, Sin Recepción: " + lvl["ofiRs"] + " ]", times8bold)
                        contenido.add(par)
                        if (totalNode == 0)
                            totalNode += lvl["rezagados"]
                        if (totalNodeSr == 0)
                            totalNodeSr += lvl["retrasados"]
                    }

                }
                if (params.detalle) {
                    tablaTramites = new PdfPTable(9);
                    tablaTramites.setWidthPercentage(100);
                    par = new Paragraph("Nro.", times8bold)
                    PdfPCell cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("F. Creación", times8bold)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("De", times8bold)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("Creado por", times8bold)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("F. Envío", times8bold)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("F. Recepcíon", times8bold)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("F. Límite", times8bold)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("Retraso (días)", times8bold)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("Tipo", times8bold)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    lvl["tramites"].each { t ->
                        par = new Paragraph("${t.tramite.codigo} ${t.rolPersonaTramite.codigo == 'R002' ? '[CC]' : ''}", times8normal)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("${t.tramite.fechaCreacion.format('dd-MM-yyyy HH:mm')}", times8normal)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        if (t.tramite.deDepartamento) {
                            par = new Paragraph("${t.tramite.deDepartamento.codigo}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                        } else {
                            par = new Paragraph("${t.tramite.de.departamento.codigo}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                        }
                        par = new Paragraph("${t.tramite.de.login}", times8normal)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("${t.fechaEnvio.format('dd-MM-yyyy hh:mm')}", times8normal)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("${(t.fechaRecepcion) ? t.fechaRecepcion?.format('dd-MM-yyyy hh:mm') : ''}", times8normal)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("${(t.fechaLimiteRespuesta) ? t.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm') : ''}", times8normal)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("${(t.fechaLimiteRespuesta) ? (now - t.fechaLimiteRespuesta) : ''}", times8normal)
                        cell = new PdfPCell(par);
                        cell.setHorizontalAlignment(1)
                        tablaTramites.addCell(cell);
                        par = new Paragraph("${(t.fechaLimiteRespuesta) ? 'Retrasado' : 'No recibido'}", times8normal)
                        cell = new PdfPCell(par);
                        cell.setHorizontalAlignment(1)
                        tablaTramites.addCell(cell);

                    }
                    if (lvl["tramites"].size() > 0) {
                        par2.add(tablaTramites)
                        contenido.add(par2)
                    }
                }
                lvl["personas"].each { p ->
                    par3 = null
                    par3 = new Paragraph("", times8normal)
                    par3.setSpacingBefore(0.001)
                    par = new Paragraph("Usuario: ${p["objeto"].departamento.codigo}:" + p["objeto"] + " - ${p['objeto'].login} - [  Retrasados: ${p['rezagados']}, Sin Recepción: " + p["retrasados"] + " ]", times8bold)
                    par.setSpacingBefore(17)
                    totalNode += p["rezagados"]
                    totalNodeSr += p["retrasados"]
                    contenido.add(par)
                    if (params.detalle) {
                        tablaTramites = null
                        tablaTramites = new PdfPTable(9);
                        tablaTramites.setWidthPercentage(100);
                        par = new Paragraph("Nro.", times8bold)
                        PdfPCell cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("F. Creación", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("De", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("Creado por", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("F. Envío", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("F. Recepcíon", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("F. Límite", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("Retraso (días)", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("Tipo", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        p["tramites"].each { t ->
                            par = new Paragraph("${t.tramite.codigo} ${t.rolPersonaTramite.codigo == 'R002' ? '[CC]' : ''}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("${t.tramite.fechaCreacion.format('dd-MM-yyyy HH:mm')}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            if (t.tramite.deDepartamento) {
                                par = new Paragraph("${t.tramite.deDepartamento.codigo}", times8normal)
                                cell = new PdfPCell(par);
                                tablaTramites.addCell(cell);
                            } else {
                                par = new Paragraph("${t.tramite.de.departamento.codigo}", times8normal)
                                cell = new PdfPCell(par);
                                tablaTramites.addCell(cell);
                            }
                            par = new Paragraph("${t.tramite.de.login}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("${t.fechaEnvio.format('dd-MM-yyyy hh:mm')}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("${(t.fechaRecepcion) ? t.fechaRecepcion?.format('dd-MM-yyyy hh:mm') : ''}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("${(t.fechaLimiteRespuesta) ? t.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm') : ''}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("${(t.fechaLimiteRespuesta) ? (now - t.fechaLimiteRespuesta) : ''}", times8normal)
                            cell = new PdfPCell(par);
                            cell.setHorizontalAlignment(1)
                            tablaTramites.addCell(cell);
                            par = new Paragraph("${(t.fechaLimiteRespuesta) ? 'Retrasado' : 'No recibido'}", times8normal)
                            cell = new PdfPCell(par);
                            cell.setHorizontalAlignment(1)
                            tablaTramites.addCell(cell);
                        }
                        if (p["tramites"].size() > 0) {
                            par3.add(tablaTramites)
                            contenido.add(par3)
                        }
                    }
                }
                total += totalNode
                totalSr += totalNodeSr
            }



            if (lvl["hijos"].size() > 0) {
                def res = imprimeHijosPdf(lvl, contenido, tablaTramites, params, usuario, deps, puedeVer)
                total += res[0]
                totalSr += res[1]
            }
        }
        return [total, totalSr]
    }


    def reporteRetrasadosConsolidado() {
        maxLvl = null
        datosGrafico = [:]
        def estadoR = EstadoTramite.findByCodigo("E004")
        def estadoE = EstadoTramite.findByCodigo("E003")
        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCopia = RolPersonaTramite.findByCodigo("R002")
        def now = new Date()
        def datos = [:]
        def usuario = null
        def deps = []
        def puedeVer = []
        def extraPersona = "and "
        if (params.prsn) {
            usuario = Persona.get(params.prsn)
            extraPersona += "persona=" + usuario.id + " "
            if (usuario.esTriangulo)
                extraPersona = "and (persona=${usuario.id} or departamento = ${usuario.departamento.id})"
            def padre = usuario.departamento.padre
            while (padre) {
                deps.add(padre)
                padre = padre.padre
            }
            deps.add(usuario.departamento)
            puedeVer.add(usuario.departamento)
            def hi = Departamento.findAllByPadre(usuario.departamento)
            while (hi.size() > 0) {
                puedeVer += hi
                hi = Departamento.findAllByPadreInList(hi)
            }

        }
        def depStr = ""
        if (params.dpto) {
            def departamento = Departamento.get(params.dpto)
            def padre = departamento.padre
            while (padre) {
                deps.add(padre)
                padre = padre.padre
            }
            deps.add(departamento)
            puedeVer.add(departamento)
            def hi = Departamento.findAllByPadre(departamento)
            while (hi.size() > 0) {
                puedeVer += hi
                hi = Departamento.findAllByPadreInList(hi)
            }
        }
        def pdt = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite where" +
                " fechaEnvio is not null " +
                "and rolPersonaTramite in (${rolPara.id},${rolCopia.id}) " +
                "and estado in (${estadoR.id},${estadoE.id}) ${usuario ? extraPersona : ''} ")

        if (pdt) {
            pdt.each { pd ->
                pd.refresh()
                if (pd.tramite.externo != "1" || pd.tramite == null) {
                    def resp = Tramite.findAllByAQuienContesta(pd)
                    if (resp.size() == 0) {
                        if (pd.fechaLimite < now || (!pd.fechaRecepcion))
                            datos = reportesPdfService.jerarquia(datos, pd)
                    }
                }
            }
        }

        def baos = new ByteArrayOutputStream()
        def name = "reporteTramitesRetrasados_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeaderHoja1 = [border: Color.WHITE, bordeTop: "1", bordeBot: "1"]
        times8boldWhite.setColor(Color.WHITE)
        times10boldWhite.setColor(Color.WHITE)
        Document document = reportesPdfService.crearDocumento("vert", [top: 2.5, right: 2.5, bottom: 1.5, left: 3])

        def pdfw = PdfWriter.getInstance(document, baos)

        session.tituloReporte = "Reporte resumido de Trámites Retrasados y sin recepción"

        reportesPdfService.membrete(document)
        document.open()
        reportesPdfService.propiedadesDocumento(document, "reporteTramitesRetrasados")
        def contenido = new Paragraph();
        def total = 0
        def totalSr = 0
        def hijos = datos["hijos"]
        if (datos["objeto"]) {
            if ((puedeVer.id.contains(datos["objeto"].id))) {
                maxLvl = datos
            }
        }

        PdfPTable tablaTramites
        tablaTramites = new PdfPTable(4);
        tablaTramites.setWidths(14, 62, 12, 12)
        tablaTramites.setWidthPercentage(100);
        def parH = new Paragraph("", times8bold)
        def cell = new PdfPCell(parH);
        cell.setBorderColor(Color.WHITE)
        tablaTramites.addCell(cell);
        cell = new PdfPCell(parH);
        cell.setBorderColor(Color.WHITE)
        tablaTramites.addCell(cell);
        parH = new Paragraph("Retrasados", times8bold)
        cell = new PdfPCell(parH);
        cell.setBorderColor(Color.WHITE)
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
        tablaTramites.addCell(cell);
        parH = new Paragraph("Sin recepción", times8bold)
        cell = new PdfPCell(parH);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
        cell.setBorderColor(Color.WHITE)
        tablaTramites.addCell(cell);

        hijos.each { lvl ->
            if (puedeVer.size() == 0 || (puedeVer.id.contains(lvl["objeto"].id))) {
                if (lvl["tramites"].size() > 0 || lvl["personas"].size() > 0) {
                    if (maxLvl == null)
                        maxLvl = lvl
                    datosGrafico.put(lvl["objeto"].toString(), [:])
                    def dg = datosGrafico[lvl["objeto"].toString()]
                    dg.put("rezagados", [:])
                    dg.put("retrasados", [:])
                    dg.put("totalRz", 0)
                    dg.put("totalRs", 0)
                    dg.put("objeto", lvl["objeto"])
                    def par = new Paragraph("Dirección", times8bold)
                    cell = new PdfPCell(par);
                    cell.setBorderColor(Color.WHITE)
                    tablaTramites.addCell(cell);
                    def totalNode = 0
                    def totalNodeSr = 0
                    par = new Paragraph("" + lvl["objeto"], times8normal)
                    def cellNombre = new PdfPCell(par);
                    cellNombre.setBorderColor(Color.WHITE)

                    def usuarios = ""
                    def totales = ""
                    def totalesSr = ""

                    if (lvl["tramites"].size() > 0) {
                        lvl["triangulos"].each { t ->
                            usuarios += "${t} (Oficina)\n"
                            totales += "${lvl["ofiRz"]} \n"
                            totalesSr += "" + lvl["ofiRs"] + " \n"
                            if (totalNode == 0) {
                                totalNode += lvl["ofiRz"].toInteger()
                                dg["rezagados"].put("Oficina", lvl["ofiRz"].toInteger())
                            }
                            if (totalNodeSr == 0) {
                                totalNodeSr += lvl["ofiRs"].toInteger()
                                dg["retrasados"].put("Oficina", lvl["ofiRs"].toInteger())
                            }
                        }
                    }
                    lvl["personas"].each { p ->
                        usuarios += "${p['objeto']} \n"
                        totales += "${p["rezagados"]} \n"
                        totalesSr += "" + p["retrasados"] + " \n"
                        dg["rezagados"].put(p['objeto'], p["rezagados"].toInteger())
                        dg["retrasados"].put(p['objeto'], p["retrasados"].toInteger())
                        totalNode += p["rezagados"].toInteger()
                        totalNodeSr += p["retrasados"].toInteger()
                    }

                    tablaTramites.addCell(cellNombre);
                    par = new Paragraph("" + lvl["rezagados"], times8bold)
                    def cellTotal = new PdfPCell(par);
                    cellTotal.setBorderColor(Color.WHITE)
                    cellTotal.setHorizontalAlignment(Element.ALIGN_RIGHT)
                    tablaTramites.addCell(cellTotal);
                    par = new Paragraph("" + lvl["retrasados"], times8bold)
                    def cellTotalSr = new PdfPCell(par);
                    cellTotalSr.setBorderColor(Color.WHITE)
                    cellTotalSr.setHorizontalAlignment(Element.ALIGN_RIGHT)
                    tablaTramites.addCell(cellTotalSr);
                    dg["totalRz"] = lvl["rezagados"]
                    dg["totalRs"] = lvl["retrasados"]
                    par = new Paragraph("Usuario:", times8bold)
                    cell = new PdfPCell(par);
                    cell.setBorderColor(Color.WHITE)
                    tablaTramites.addCell(cell);
                    par = new Paragraph(usuarios, times8normal)
                    cell = new PdfPCell(par);
                    cell.setBorderColor(Color.WHITE)
                    tablaTramites.addCell(cell);
                    par = new Paragraph(totales, times8normal)
                    cell = new PdfPCell(par);
                    cell.setBorderColor(Color.WHITE)
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                    tablaTramites.addCell(cell);
                    par = new Paragraph(totalesSr, times8normal)
                    cell = new PdfPCell(par);
                    cell.setBorderColor(Color.WHITE)
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                    tablaTramites.addCell(cell);
                    total += totalNode
                    totalSr += totalNodeSr
                }
            }
            def res = imprimeHijosPdfConsolidado(lvl, document, tablaTramites, params, usuario, deps, puedeVer, total, totalSr, datosGrafico)
            total += res[0]
            totalSr += res[1]

        }
        if (maxLvl) {
            def par = new Paragraph("", times8bold)
            cell = new PdfPCell(par);
            cell.setBorderColor(Color.WHITE)
            tablaTramites.addCell(cell);
            par = new Paragraph("Gran Total", times8bold)
            cell = new PdfPCell(par);
            cell.setBorderColor(Color.WHITE)
            tablaTramites.addCell(cell);
            par = new Paragraph("" + maxLvl["rezagados"], times8bold)
            cell = new PdfPCell(par);
            cell.setBorderColor(Color.WHITE)
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
            tablaTramites.addCell(cell);
            par = new Paragraph("" + maxLvl["retrasados"], times8bold)
            cell = new PdfPCell(par);
            cell.setBorderColor(Color.WHITE)
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
            tablaTramites.addCell(cell);
        }
        contenido.add(tablaTramites)
        boolean conGraficos = false

        try {
            conGraficos = true
            def width = 550
            def height = 250
            PdfContentByte contentByte = pdfw.getDirectContent();
            PdfTemplate templateSinRecepcion = contentByte.createTemplate(width, height);
            Graphics2D graphics2dSinRecepcion = templateSinRecepcion.createGraphics(width, height, new DefaultFontMapper());
            PdfTemplate templateRetrasados = contentByte.createTemplate(width, height);
            Graphics2D graphics2dRetrasados = templateRetrasados.createGraphics(width, height, new DefaultFontMapper());
            Rectangle2D rectangle2dSinRecepcion = new Rectangle2D.Double(0, 0, width, height);
            Rectangle2D rectangle2dRetrasados = new Rectangle2D.Double(0, 0, width, height);

////        PARA GRAFICO PASTEL
            DefaultPieDataset dataSetRs = new DefaultPieDataset();
            DefaultPieDataset dataSetRz = new DefaultPieDataset();
            def ttl = " por departamento"
            def existeSinRecepcion = false
            def existeRetrasados = false
            datosGrafico.each { dep, valores ->
                if (datosGrafico.size() > 1) {
                    if (valores.totalRs > 0) {
                        existeSinRecepcion = true
                        dataSetRs.setValue(valores.objeto.codigo, valores.totalRs);
                    }
                    if (valores.totalRz > 0) {
                        existeRetrasados = true
                        dataSetRz.setValue(valores.objeto.codigo, valores.totalRz);
                    }
                } else {
                    ttl = " de " + valores.objeto.descripcion
                    valores.rezagados.each { k, v ->
                        if (v > 0) {
                            if (k instanceof java.lang.String) {
                                existeRetrasados = true
                                dataSetRz.setValue(k, v);
                            } else {
                                existeRetrasados = true
                                dataSetRz.setValue(k.login, v);
                            }
                        }
                    }
                    valores.retrasados.each { k, v ->
                        if (v > 0) {
                            if (k instanceof java.lang.String) {
                                existeSinRecepcion = true
                                dataSetRs.setValue(k, v);
                            } else {
                                existeSinRecepcion = true
                                dataSetRs.setValue(k.login, v);
                            }
                        }
                    }
                }
            }

            JFreeChart chartSinRecepcion = ChartFactory.createPieChart("Documentos sin recepción" + ttl, dataSetRs, true, true, false);
            chartSinRecepcion.setTitle(
                    new org.jfree.chart.title.TextTitle("Documentos sin recepción" + ttl,
                            new java.awt.Font("SansSerif", java.awt.Font.BOLD, 15)
                    )
            );
            JFreeChart chartRetrasados = ChartFactory.createPieChart("Documentos retrasados" + ttl, dataSetRz, true, true, false);
            chartRetrasados.setTitle(
                    new org.jfree.chart.title.TextTitle("Documentos retrasados" + ttl,
                            new java.awt.Font("SansSerif", java.awt.Font.BOLD, 15)
                    )
            );

            /* getPlot method of JFreeChart class returns the PiePlot object back to us */
            PiePlot ColorConfigurator = (PiePlot) chartSinRecepcion.getPlot(); /* get PiePlot object for changing */
            PiePlot ColorConfigurator2 = (PiePlot) chartRetrasados.getPlot(); /* get PiePlot object for changing */
            /* A format mask specified to display labels. Here {0} is the section name, and {1} is the value.
            We can also use {2} which will display a percent value */
            ColorConfigurator.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{1} docs. ({2})"));
            ColorConfigurator2.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{1} docs. ({2})"));
            /* Set color of the label background on the pie chart */
            ColorConfigurator.setLabelBackgroundPaint(new Color(220, 220, 220));
            ColorConfigurator2.setLabelBackgroundPaint(new Color(220, 220, 220));

            chartSinRecepcion.draw(graphics2dSinRecepcion, rectangle2dSinRecepcion);
            chartRetrasados.draw(graphics2dRetrasados, rectangle2dRetrasados);

            graphics2dSinRecepcion.dispose();
            graphics2dRetrasados.dispose();

            def posyGraf1 = 450
            def posyGraf2 = 180
            if (existeSinRecepcion) {
                contentByte.addTemplate(templateSinRecepcion, 30, posyGraf1);
                if (existeRetrasados) {
                    contentByte.addTemplate(templateRetrasados, 30, posyGraf2);
                }
            } else {
                if (existeRetrasados) {
                    contentByte.addTemplate(templateRetrasados, 30, posyGraf1);
                }
            }
        } catch (Exception e) {
            println "ERROR GRAFICOS::::::: "
            e.printStackTrace();
            conGraficos = false
        }

        if (conGraficos) {
            document.newPage()
        }
        document.add(contenido)

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }


    def imprimeHijosPdfConsolidado(arr, contenido, tablaTramites, params, usuario, deps, puedeVer, total, totalSr, datosGrafico) {
        total = 0
        totalSr = 0
        def datos = arr["hijos"]
        datos.each { lvl ->
            if (puedeVer.size() == 0 || (puedeVer.id.contains(lvl["objeto"].id))) {
                if (maxLvl == null)
                    maxLvl = lvl
                datosGrafico.put(lvl["objeto"].toString(), [:])
                def dg = datosGrafico[lvl["objeto"].toString()]
                dg.put("rezagados", [:])
                dg.put("retrasados", [:])
                dg.put("totalRz", 0)
                dg.put("totalRs", 0)
                dg.put("objeto", lvl["objeto"])
                def par = new Paragraph("- Departamento:", times8bold)
                PdfPCell cell = new PdfPCell(par);
                cell.setBorderColor(Color.WHITE)
                tablaTramites.addCell(cell);
                def totalNode = 0
                def totalNodeSr = 0
                par = new Paragraph("" + lvl["objeto"], times8normal)
                def cellNombre = new PdfPCell(par);
                cellNombre.setBorderColor(Color.WHITE)

                def usuarios = ""
                def totales = ""
                def totalesSr = ""

                if (lvl["tramites"].size() > 0) {
                    lvl["triangulos"].each { t ->
                        usuarios += "${t} (Oficina)\n"
                        totales += "${lvl["ofiRz"]} \n"
                        totalesSr += "" + lvl["ofiRs"] + " \n"
                        if (totalNode == 0) {
                            totalNode += lvl["ofiRz"].toInteger()
                            dg["rezagados"].put("Oficina", lvl["ofiRz"].toInteger())
                        }
                        if (totalNodeSr == 0) {
                            totalNodeSr += lvl["ofiRs"].toInteger()
                            dg["retrasados"].put("Oficina", lvl["ofiRs"].toInteger())
                        }
                    }
                }
                lvl["personas"].each { p ->
                    usuarios += "${p['objeto']} \n"
                    totales += "${p["rezagados"]} \n"
                    totalesSr += "" + p["retrasados"] + " \n"
                    dg["rezagados"].put(p['objeto'], p["rezagados"].toInteger())
                    dg["retrasados"].put(p['objeto'], p["retrasados"].toInteger())
                    totalNode += p["rezagados"].toInteger()
                    totalNodeSr += p["retrasados"].toInteger()
                }

                tablaTramites.addCell(cellNombre);
                par = new Paragraph("" + lvl["rezagados"], times8bold)
                def cellTotal = new PdfPCell(par);
                cellTotal.setBorderColor(Color.WHITE)
                cellTotal.setHorizontalAlignment(Element.ALIGN_RIGHT)
                tablaTramites.addCell(cellTotal);
                par = new Paragraph("" + lvl["retrasados"], times8bold)
                def cellTotalSr = new PdfPCell(par);
                cellTotalSr.setBorderColor(Color.WHITE)
                cellTotalSr.setHorizontalAlignment(Element.ALIGN_RIGHT)
                dg["totalRz"] = lvl["rezagados"]
                dg["totalRs"] = lvl["retrasados"]
                tablaTramites.addCell(cellTotalSr);
                par = new Paragraph("--Usuario:", times8bold)
                cell = new PdfPCell(par);
                cell.setBorderColor(Color.WHITE)
                tablaTramites.addCell(cell);
                par = new Paragraph(usuarios, times8normal)
                cell = new PdfPCell(par);
                cell.setBorderColor(Color.WHITE)
                tablaTramites.addCell(cell);
                par = new Paragraph(totales, times8normal)
                cell = new PdfPCell(par);
                cell.setBorderColor(Color.WHITE)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                tablaTramites.addCell(cell);
                par = new Paragraph(totalesSr, times8normal)
                cell = new PdfPCell(par);
                cell.setBorderColor(Color.WHITE)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                tablaTramites.addCell(cell);
                total += totalNode
                totalSr += totalNodeSr
            }

            if (lvl["hijos"].size() > 0) {
                def res = imprimeHijosPdfConsolidado(lvl, contenido, tablaTramites, params, usuario, deps, puedeVer, total, totalSr, datosGrafico)
                total += res[0]
                totalSr += res[1]
            }
        }
        return [total, totalSr]
    }

    def imprimeHijos(arr) {
        def datos = arr["hijos"]
        datos.each { lvl ->
            println "\t\t\t " + lvl["objeto"]
            println "\t\t\t\t Tramites:"
            lvl["tramites"].each { t ->
                println "\t\t\t\t\t " + t
            }
            println "\t\t\t\t Personas:"
            lvl["personas"].each { p ->
                println "\t\t\t\t " + p["objeto"]
                p["tramites"].each { t ->
                    println "\t\t\t " + t
                }
            }
            if (lvl["hijos"].size() > 0)
                imprimeHijos(lvl)
        }
    }

    def arreglaTramites() {
        Tramite.list().each {
            def hijos = Tramite.findAllByPadre(it)
            if (hijos.size() > 0) {
                it.estado = "C"
                it.save()
            }
        }
    }
}
