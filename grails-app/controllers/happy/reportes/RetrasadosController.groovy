package happy.reportes

import com.lowagie.text.HeaderFooter
import com.lowagie.text.PageSize
import com.lowagie.text.Phrase
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.DefaultFontMapper
import com.lowagie.text.pdf.PdfContentByte
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfTemplate
import happy.tramites.Departamento
import happy.tramites.EstadoTramite
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite
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
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.labels.StandardPieSectionLabelGenerator
import org.jfree.chart.plot.PiePlot
import org.jfree.data.general.DefaultPieDataset

import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D

class RetrasadosController {
    def reportesPdfService

    def index() {}
    Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
    Font times18bold = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
    Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
    Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
    Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
    def datosGrafico = [:]


    def reporteRetrasadosDetalle() {
//        params.detalle=1
//        params.prsn=session.usuario.id
//        println "detallado aaa    " + params
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
        def depStr=""
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
            // println "DPTO " + departamento.codigo + "  " + departamento.descripcion
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
        //println "deps "+deps+"  puede ver  "+puedeVer
        def tramites = Tramite.findAll("from Tramite where externo!='1' or externo is null ${depStr}")
        tramites.each { t ->
            def pdt = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite where tramite=${t.id} " +
                    "and fechaEnvio is not null " +
                    "and rolPersonaTramite in (${rolPara.id},${rolCopia.id}) " +
                    "and estado in (${estadoR.id},${estadoE.id}) ${usuario ? extraPersona : ''} ")

            if (pdt) {
                pdt.each { pd ->
                    def resp = Tramite.findAllByAQuienContesta(pd)
                    if (resp.size() == 0) {
                        if (pd.fechaLimite < now || (!pd.fechaRecepcion))
                            datos = jerarquia(datos, pd)
                    }

                }
            }

        }

//        println "tramites "+datos
//        jerarquia(datos)
//        println datos["objeto"]
//        datos["hijos"].each{
//            println "\t--> "+it["objeto"]
//            println "\t\t tramites "+it["tramites"]
//            println "\t\t personas "+it["personas"]
//            println "\t\t\t tramites "+it["personas"]["tramites"]
//            println "\t\thijos ------  "
//            imprimeHijos(it)
//            println "\t\t ------------!!------  "
//        }


        def baos = new ByteArrayOutputStream()
        def name = "reporteTramitesRetrasados_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        def prmsHeaderHoja = [border: Color.WHITE]
        def prmsHeaderHoja1 = [border: Color.WHITE, bordeTop: "1", bordeBot: "1"]
        times8boldWhite.setColor(Color.WHITE)
        times10boldWhite.setColor(Color.WHITE)
        Document document = reportesPdfService.crearDocumento("svt", [top: 2, right: 2, bottom: 1.5, left: 2.5])

        def pdfw = PdfWriter.getInstance(document, baos);
        reportesPdfService.membrete(document)
        document.open();
        reportesPdfService.propiedadesDocumento(document, "reporteTramitesRetrasados")

        reportesPdfService.crearEncabezado(document, "Reporte detallado de Trámites Retrasados y sin recepción")

        def contenido = new Paragraph();

        def hijos = datos["hijos"]

        def total = 0
        def totalSr = 0
        PdfPTable tablaTramites

        hijos.each { lvl ->
            //println "hijo ${lvl} ||  ${lvl['objeto']}  ${lvl['objeto'].id}   "
            if (puedeVer.size() == 0 || (puedeVer.id.contains(lvl["objeto"].id))) {
//            println "desp "+deps+"   "+lvl["objeto"]+"   "+(deps.id.contains(lvl["objeto"].id))

                def totalNode = 0
                def totalNodeSr = 0
                def par = new Paragraph("-" + lvl["objeto"], times12bold)
//                par.setIndentationLeft((lvl["nivel"]-1)*20)
                document.add(par)
                def par2 = new Paragraph("", times8normal)
                par2.setSpacingBefore(4)
                def par3 = new Paragraph("", times8normal)
                par3.setSpacingBefore(4)
//                println "wtf "+lvl["triangulos"]

                if (lvl["tramites"].size() > 0) {
//                        par = new Paragraph("Trámites:", times10bold)
//                        par.setIndentationLeft(lvl["nivel"]*20+10)
//                        document.add(par)
                    lvl["triangulos"].each { t ->
                        par = new Paragraph("Usuario: ${t.departamento.codigo}:" + t + " - Trámites de oficina  - [ Sin Recepción: " + lvl["retrasados"] + " , Retrasados: ${lvl['rezagados']} ]", times8bold)
//                        par.setIndentationLeft((lvl["nivel"]-1)*20+10)
                        if (totalNode == 0)
                            totalNode += lvl["rezagados"]
                        if (totalNodeSr == 0)
                            totalNodeSr += lvl["retrasados"]
                        document.add(par)
                    }

                }
                if (params.detalle) {
                    tablaTramites = new PdfPTable(8);
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
//                    par = new Paragraph("Para", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
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
                    lvl["tramites"].each { t ->
//                        par2.setIndentationLeft((lvl["nivel"]-1)*20+10)
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

                    }
                    if (lvl["tramites"].size() > 0) {
                        par2.add(tablaTramites)
                        document.add(par2)
                    }
                }
//                    if(lvl["personas"].size()>0){
//                        par = new Paragraph("Usuarios:", times10bold)
//                        par.setIndentationLeft(lvl["nivel"]*20+10)
//                        document.add(par)
//
//                    }
                lvl["personas"].each { p ->
//                println "\t\t "+p["objeto"]+ "  "+  p["objeto"].departamento
                    par3 = null
                    par3 = new Paragraph("", times8normal)
                    par3.setSpacingBefore(0.001)
                    par = new Paragraph("Usuario: ${p["objeto"].departamento.codigo}:" + p["objeto"] + " - ${p['objeto'].login} - [ Sin Recepción: " + p["retrasados"] + " , Retrasados: ${p['rezagados']} ]", times8bold)
                    par.setSpacingBefore(17)
//                    par.setIndentationLeft((lvl["nivel"]-1)*20+20)
                    document.add(par)
//                    par3.setIndentationLeft((lvl["nivel"]-1)*20+20)
                    totalNode += p["rezagados"]
                    totalNodeSr += p["retrasados"]

                    if (params.detalle) {
                        tablaTramites = new PdfPTable(8);
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
                        p["tramites"].each { t ->
//                            par2.setIndentationLeft((lvl["nivel"]-1)*20+10)
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
                            par = new Paragraph("${(t.fechaLimiteRespuesta) ? (now -t.fechaLimiteRespuesta): ''}", times8normal)
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
                total+=totalNode
                totalSr+=totalNodeSr

            }
            def res = imprimeHijosPdf(lvl, document, tablaTramites, params, usuario, deps, puedeVer)
            total+= res[0]
            totalSr+= res[1]

        }

        def par = new Paragraph("Gran Total                                                                                                                                          Retrasados: ${total}       Sin Recepción: ${totalSr}     ", times12bold)
        document.add(par);
        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
//        return  [tramites:tramites,datos:datos]
    }


    def imprimeHijosPdf(arr, contenido, tablaTramites, params, usuario, deps, puedeVer) {
        def total = 0
        def totalSr = 0
        def datos = arr["hijos"]
        def now = new Date()
        datos.each { lvl ->
//            println  "\t "+lvl["objeto"]
//            println "\t\t Tramites:"
//            println "hijo funcion ${lvl['objeto']} "+lvl["objeto"].id+"    "+puedeVer.id

            if (puedeVer.size() == 0 || (puedeVer.id.contains(lvl["objeto"].id))) {
                def par = new Paragraph("-" + lvl["objeto"], times12bold)
//                par.setIndentationLeft((lvl["nivel"]-1)*20)
                def totalNode = 0
                def totalNodeSr = 0
                contenido.add(par)
                def par2 = new Paragraph("", times8normal)
                par2.setSpacingBefore(4)
                def par3 = new Paragraph("", times8normal)
                par3.setSpacingBefore(4)

                if (lvl["tramites"].size() > 0) {
                    lvl["triangulos"].each { t ->
                        par = new Paragraph("Usuario: ${t.departamento.codigo}:" + t + " - Trámites de oficina - [ Sin Recepción: " + lvl["retrasados"] + " , Retrasados: ${lvl['rezagados']} ]", times8bold)
//                        par.setIndentationLeft((lvl["nivel"]-1)*20+10)
                        contenido.add(par)
                        if (totalNode == 0)
                            totalNode += lvl["rezagados"]
                        if (totalNodeSr == 0)
                            totalNodeSr += lvl["retrasados"]
                    }

                }
                if (params.detalle) {
                    tablaTramites = new PdfPTable(8);
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
//                    par = new Paragraph("Para", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
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
                    lvl["tramites"].each { t ->
//                        par2.setIndentationLeft((lvl["nivel"]-1)*20+10)
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
                        par = new Paragraph("${(t.fechaLimiteRespuesta) ?(now - t.fechaLimiteRespuesta) : ''}", times8normal)
                        cell = new PdfPCell(par);
                        cell.setHorizontalAlignment(1)
                        tablaTramites.addCell(cell);

                    }
                    if (lvl["tramites"].size() > 0) {
                        par2.add(tablaTramites)
                        contenido.add(par2)
                    }
                }
//                    if(lvl["personas"].size()>0){
//                        par = new Paragraph("Usuarios:", times10bold)
//                        par.setIndentationLeft(lvl["nivel"]*20+10)
//                        contenido.add(par)
//
//                    }
                lvl["personas"].each { p ->
//                println "\t\t "+p["objeto"]+ "  "+  p["objeto"].departamento
                    par3 = null
                    par3 = new Paragraph("", times8normal)
                    par3.setSpacingBefore(0.001)
//                    par3.setIndentationLeft((lvl["nivel"]-1)*20+10)
                    par = new Paragraph("Usuario: ${p["objeto"].departamento.codigo}:" + p["objeto"] + " - ${p['objeto'].login} - [ Sin Recepción: " + p["retrasados"] + " , Retrasados: ${p['rezagados']} ]", times8bold)
                    par.setSpacingBefore(17)
                    totalNode += p["rezagados"]
                    totalNodeSr += p["retrasados"]
//                    par.setIndentationLeft((lvl["nivel"]-1)*20+10)
                    contenido.add(par)
                    if (params.detalle) {
                        tablaTramites = null
                        tablaTramites = new PdfPTable(8);
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
                        p["tramites"].each { t ->
//                            par2.setIndentationLeft((lvl["nivel"]-1)*20+10)
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
                            par = new Paragraph("${(t.fechaLimiteRespuesta) ?(now - t.fechaLimiteRespuesta) : ''}", times8normal)
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
                totalSr +=totalNodeSr
            }



            if (lvl["hijos"].size() > 0) {
                def res = imprimeHijosPdf(lvl, contenido, tablaTramites, params, usuario, deps, puedeVer)
                total += res[0]
                totalSr += res[1]
            }
        }
//        println " return ${datos['objeto']} "+total+" "+totalSr
        return [total,totalSr]
    }


    def reporteRetrasadosConsolidado() {
//        params.detalle=1
//        params.prsn=session.usuario.id
//        println "con aaa    " + params
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
        def depStr=""
        if (params.dpto) {
            def departamento = Departamento.get(params.dpto)
            //depStr=" and departamento = ${departamento.id}"
//            println "DPTO " + departamento.codigo + "  " + departamento.descripcion
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
//        println "deps "+deps+"  puede ver  "+puedeVer
        def tramites = Tramite.findAll("from Tramite where externo!='1' or externo is null ${depStr}")
        tramites.each { t ->
            def pdt = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite where tramite=${t.id} and fechaEnvio is not null and rolPersonaTramite in (${rolPara.id},${rolCopia.id}) and estado in (${estadoR.id},${estadoE.id}) ${usuario ? extraPersona : ''} ")
            if (pdt) {
                pdt.each { pd ->
                    def resp = Tramite.findAllByAQuienContesta(pd)
                    if (resp.size() == 0) {
                        if (pd.fechaLimite < now || (!pd.fechaRecepcion)){

                            datos = jerarquia(datos, pd)
                        }
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

        def pdfw = PdfWriter.getInstance(document, baos);
        reportesPdfService.membrete(document)
        document.open();
        reportesPdfService.propiedadesDocumento(document, "reporteTramitesRetrasados")
        reportesPdfService.crearEncabezado(document, "Reporte resumido de Trámites Retrasados  y sin recepción")
        def contenido = new Paragraph();
        def total = 0
        def totalSr = 0
        def hijos = datos["hijos"]

        PdfPTable tablaTramites
        tablaTramites = new PdfPTable(4);
        tablaTramites.setWidths(10, 66, 12, 12)
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
                            totales += "${lvl["rezagados"]} \n"
                            totalesSr += "" + lvl["retrasados"] + " \n"
                            if (totalNode == 0) {
                                totalNode += lvl["rezagados"].toInteger()
                                dg["rezagados"].put("Oficina", lvl["rezagados"].toInteger())
                            }
                            if (totalNodeSr == 0) {
                                totalNodeSr += lvl["retrasados"].toInteger()
                                dg["retrasados"].put("Oficina", lvl["retrasados"].toInteger())
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
                    par = new Paragraph("" + totalNode, times8bold)
                    def cellTotal = new PdfPCell(par);
                    cellTotal.setBorderColor(Color.WHITE)
                    cellTotal.setHorizontalAlignment(Element.ALIGN_RIGHT)
                    tablaTramites.addCell(cellTotal);
                    par = new Paragraph("" + totalNodeSr, times8bold)
                    def cellTotalSr = new PdfPCell(par);
                    cellTotalSr.setBorderColor(Color.WHITE)
                    cellTotalSr.setHorizontalAlignment(Element.ALIGN_RIGHT)
                    tablaTramites.addCell(cellTotalSr);
                    dg["totalRz"] = totalNode
                    dg["totalRs"] = totalNodeSr
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
        def par = new Paragraph("", times8bold)
        cell = new PdfPCell(par);
        cell.setBorderColor(Color.WHITE)
        tablaTramites.addCell(cell);
        par = new Paragraph("Gran Total", times8bold)
        cell = new PdfPCell(par);
        cell.setBorderColor(Color.WHITE)
        tablaTramites.addCell(cell);
        par = new Paragraph("" + total, times8bold)
        cell = new PdfPCell(par);
        cell.setBorderColor(Color.WHITE)
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
        tablaTramites.addCell(cell);
        par = new Paragraph("" + totalSr, times8bold)
        cell = new PdfPCell(par);
        cell.setBorderColor(Color.WHITE)
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
        tablaTramites.addCell(cell);
        contenido.add(tablaTramites)

        document.add(contenido)
//
//        println datosGrafico
//        println datosGrafico.size()
//        datosGrafico.each { dep, valores ->
//            println dep
//            println "Retrasados"
//            valores.retrasados.each { k, v ->
//                println "\t" + k + "   " + v
//            }
//            println "Rezagados"
//            valores.rezagados.each { k, v ->
//                println "\t" + k + "   " + v
//            }
//        }

        try {
            document.newPage()
            def width = 550
            def height = 250
            PdfContentByte contentByte = pdfw.getDirectContent();
            PdfTemplate template = contentByte.createTemplate(width, height);
            Graphics2D graphics2d = template.createGraphics(width, height, new DefaultFontMapper());
            PdfTemplate template2 = contentByte.createTemplate(width, height);
            Graphics2D graphics2d2 = template2.createGraphics(width, height, new DefaultFontMapper());
            Rectangle2D rectangle2d = new Rectangle2D.Double(0, 0, width, height);
            Rectangle2D rectangle2d2 = new Rectangle2D.Double(0, 0, width, height);

////        PARA GRAFICO PASTEL
            DefaultPieDataset dataSetRs = new DefaultPieDataset();
            DefaultPieDataset dataSetRz = new DefaultPieDataset();
            def ttl = " por departamento"
            datosGrafico.each { dep, valores ->
                if (datosGrafico.size() > 1) {
                    if (valores.totalRs > 0) {
                        dataSetRs.setValue(valores.objeto.codigo, valores.totalRs);
                    }
                    if (valores.totalRz > 0) {
                        dataSetRz.setValue(valores.objeto.codigo, valores.totalRz);
                    }
                } else {
                    ttl = " de " + valores.objeto.descripcion
                    valores.rezagados.each { k, v ->
                        if (v > 0) {
                            if (k instanceof java.lang.String) {
                                dataSetRz.setValue(k, v);
                            } else {
                                dataSetRz.setValue(k.login, v);
                            }
                        }
                    }
                    valores.retrasados.each { k, v ->
                        if (v > 0) {
                            if (k instanceof java.lang.String) {
                                dataSetRs.setValue(k, v);
                            } else {
                                dataSetRs.setValue(k.login, v);
                            }
                        }
                    }
                }
            }
            JFreeChart chart = ChartFactory.createPieChart("Documentos sin recepción" + ttl, dataSetRs, true, true, false);
            chart.setTitle(
                    new org.jfree.chart.title.TextTitle("Documentos sin recepción" + ttl,
                            new java.awt.Font("SansSerif", java.awt.Font.BOLD, 15)
                    )
            );
            JFreeChart chart2 = ChartFactory.createPieChart("Documentos retrasados" + ttl, dataSetRz, true, true, false);
            chart2.setTitle(
                    new org.jfree.chart.title.TextTitle("Documentos retrasados" + ttl,
                            new java.awt.Font("SansSerif", java.awt.Font.BOLD, 15)
                    )
            );

            /* getPlot method of JFreeChart class returns the PiePlot object back to us */
            PiePlot ColorConfigurator = (PiePlot) chart.getPlot(); /* get PiePlot object for changing */
            PiePlot ColorConfigurator2 = (PiePlot) chart2.getPlot(); /* get PiePlot object for changing */
            /* A format mask specified to display labels. Here {0} is the section name, and {1} is the value.
            We can also use {2} which will display a percent value */
            ColorConfigurator.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{1} docs. ({2})"));
            ColorConfigurator2.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{1} docs. ({2})"));
            /* Set color of the label background on the pie chart */
            ColorConfigurator.setLabelBackgroundPaint(new Color(220, 220, 220));
            ColorConfigurator2.setLabelBackgroundPaint(new Color(220, 220, 220));

            chart.draw(graphics2d, rectangle2d);
            chart2.draw(graphics2d2, rectangle2d2);

            graphics2d.dispose();
            graphics2d2.dispose();
            contentByte.addTemplate(template, 30, 500);
            contentByte.addTemplate(template2, 30, 230);

        } catch (Exception e) {
            println "ERROR GRAFICOS::::::: "
            e.printStackTrace();
        }

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


    def imprimeHijosPdfConsolidado(arr, contenido, tablaTramites, params, usuario, deps, puedeVer, total, totalSr, datosGrafico) {
        total = 0
        totalSr = 0
        def datos = arr["hijos"]
        datos.each { lvl ->
            if (puedeVer.size() == 0 || (puedeVer.id.contains(lvl["objeto"].id))) {
                datosGrafico.put(lvl["objeto"].toString(), [:])
                def dg = datosGrafico[lvl["objeto"].toString()]
                dg.put("rezagados", [:])
                dg.put("retrasados", [:])
                dg.put("totalRz", 0)
                dg.put("totalRs", 0)
                dg.put("objeto", lvl["objeto"])
                def par = new Paragraph("Dirección", times8bold)
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
                        totales += "${lvl["rezagados"]} \n"
                        totalesSr += "" + lvl["retrasados"] + " \n"
                        if (totalNode == 0) {
                            totalNode += lvl["rezagados"].toInteger()
                            dg["rezagados"].put("Oficina", lvl["rezagados"].toInteger())
                        }
                        if (totalNodeSr == 0) {
                            totalNodeSr += lvl["retrasados"].toInteger()
                            dg["retrasados"].put("Oficina", lvl["retrasados"].toInteger())
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
                par = new Paragraph("" + totalNode, times8bold)
                def cellTotal = new PdfPCell(par);
                cellTotal.setBorderColor(Color.WHITE)
                cellTotal.setHorizontalAlignment(Element.ALIGN_RIGHT)
                tablaTramites.addCell(cellTotal);
                par = new Paragraph("" + totalNodeSr, times8bold)
                def cellTotalSr = new PdfPCell(par);
                cellTotalSr.setBorderColor(Color.WHITE)
                cellTotalSr.setHorizontalAlignment(Element.ALIGN_RIGHT)
                dg["totalRz"] = totalNode
                dg["totalRs"] = totalNodeSr
                tablaTramites.addCell(cellTotalSr);
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
//                println "total "+total+"   "+totalNode
            }

            if (lvl["hijos"].size() > 0) {
                def res = imprimeHijosPdfConsolidado(lvl, contenido, tablaTramites, params, usuario, deps, puedeVer, total, totalSr)
                total += res[0]
                totalSr += res[1]
            }
//            println "total des dentro "+total+"   "
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

    def jerarquia(arr, pdt) {
//        println "______________jerarquia______________"
//        println "datos ini  ----- ${pdt.tramite.codigo}  ${pdt.id} dep   "+pdt.departamento+"   prsn "+pdt.persona
        def datos = arr
        def dep
        if (pdt.departamento) {
            dep = pdt.departamento
        } else {
            dep = pdt.persona.departamento
        }
        def padres = []
        padres.add(dep)
        while (dep.padre) {
            padres.add(dep.padre)
            dep = dep.padre
        }
//        println "padres "+padres
        def first = padres.pop()
        padres = padres.reverse()
        def nivel = padres.size()
        def lvl
        if (datos["id"] != first.id.toString()) {
//            println "no padre lvl 0"
            datos.put("id", first.id.toString())
            datos.put("objeto", first)
            datos.put("tramites", [])
            datos.put("hijos", [])
            datos.put("personas", [])
            datos.put("triangulos", first.getTriangulos())
            datos.put("nivel", 0)
            datos.put("retrasados", 0)
            datos.put("rezagados", 0)
            datos.put("totalRz",0)
            datos.put("totalSr",0)
        }
        lvl = datos["hijos"]
        def actual = null
//        println "padres each "+padres

//                println "puede ver "+puedeVer+"  "+p
        if (!pdt.fechaRecepcion)
            datos["totalSr"]++
        else
            datos["totalRz"]++


        padres.each { p ->
//            println "p.each "+p+"  nivel  "+nivel
//            println "buscando........"

            lvl.each { l ->
//                println "\t lvl each --> "+l
                if (l["id"] == p.id.toString()) {
                    actual = l
                }
            }
//            println "fin buscando ..............."
//            println "actual --> "+actual
            if (actual) {
//                println "p--> "+p
                if (pdt.departamento) {

                    if (actual["id"] == pdt.departamento.id.toString()) {
//                        println "es el mismo add tramites"
                        if (!pdt.fechaRecepcion) {
                            actual["retrasados"]++

                        }else {
                            actual["rezagados"]++

                        }
                        actual["tramites"].add(pdt)
                        actual["tramites"] = actual["tramites"].sort { it.fechaEnvio }


                    }

                } else {
                    if (actual["id"] == pdt.persona.departamento.id.toString()) {
                        if (actual["personas"].size() == 0) {
                            if (!pdt.fechaRecepcion) {
                                actual["personas"].add(["id": pdt.persona.id.toString(), "objeto": pdt.persona, "tramites": [pdt], "retrasados": 1, "rezagados": 0])

                            }else {
                                actual["personas"].add(["id": pdt.persona.id.toString(), "objeto": pdt.persona, "tramites": [pdt], "retrasados": 0, "rezagados": 1])

                            }
                            actual["personas"] = actual["personas"].sort { it.objeto.nombre }
//                            actual["personas"].add(["id":pdt.persona.id.toString(),"objeto":pdt.persona,"tramites":[pdt],"retrasados":0,"rezagados":0])
                        } else {
                            def per = null
                            actual["personas"].each { pe ->
                                if (pe["id"] == pdt.persona.id.toString()) {
                                    per = pe
                                }
                            }
                            if (per) {
                                if (!pdt.fechaRecepcion) {
                                    per["retrasados"]++
                                    datos["totalSr"]++
                                }else {
                                    per["rezagados"]++
                                    datos["totalRz"]++
                                }
                                per["tramites"].add(pdt)
                                per["tramites"] = per["tramites"].sort { it.fechaEnvio }
                            } else {
                                if (!pdt.fechaRecepcion) {
                                    actual["personas"].add(["id": pdt.persona.id.toString(), "objeto": pdt.persona, "tramites": [pdt], "retrasados": 1, "rezagados": 0])

                                }else {
                                    actual["personas"].add(["id": pdt.persona.id.toString(), "objeto": pdt.persona, "tramites": [pdt], "retrasados": 0, "rezagados": 1])

                                }
                                actual["personas"] = actual["personas"].sort { it.objeto.nombre }
                                //println actual["personas"]
//                                actual["personas"].add(["id":pdt.persona.id.toString(),"objeto":pdt.persona,"tramites":[pdt],"retrasados":0,"rezagados":0])
                            }
                        }
                    }
                }
                lvl = actual["hijos"]
            } else {
//                println "no actual add lvl "+lvl
                def temp = [:]
                temp.put("id", p.id.toString())
                temp.put("objeto", p)
                temp.put("tramites", [])
                temp.put("hijos", [])
                temp.put("personas", [])
                temp.put("triangulos", p.getTriangulos())
                temp.put("retrasados", 0)
                temp.put("rezagados", 0)
                def depto = (pdt.departamento) ? pdt.departamento : pdt.persona.departamento
                if (depto == p) {
                    if (pdt.departamento) {
                        temp["tramites"].add(pdt)
                        temp["tramites"] = temp["tramites"].sort { it.fechaEnvio }
                        if (!pdt.fechaRecepcion) {
                            temp["retrasados"]++

                        }else {
                            temp["rezagados"]++

                        }
                    } else {
                        if (!pdt.fechaRecepcion) {
                            temp["personas"].add(["id": pdt.persona.id.toString(), "objeto": pdt.persona, "tramites": [pdt], "retrasados": 1, "rezagados": 0])

                        }else {
                            temp["personas"].add(["id": pdt.persona.id.toString(), "objeto": pdt.persona, "tramites": [pdt], "retrasados": 0, "rezagados": 1])

                        }
                        temp["personas"] = temp["personas"].sort { it.objeto.nombre }
//                    temp["personas"].add(["id":pdt.persona.id.toString(),"objeto":pdt.persona,"tramites":[pdt],"retrasados":0,"rezagados":0])
                    }
                }

                temp.put("nivel", nivel)

                lvl.add(temp)
//                println "fin add actual "+temp+"  nivel "+nivel
//                println "asi quedo lvl "+lvl
//                println "######################"
                if (lvl.size() == 1) {
                    lvl = lvl[0]["hijos"]
                } else {
                    lvl = lvl[lvl.size() - 1]["hijos"]
                }
//                println "lvl ? "+lvl
                nivel++

            }

            actual = null
        }

//        println "cod "+cod
////        println "lvl "+lvl
//        println "datos fun "+datos
////
//        println "---------------------fin datos---------------------------------------"
        return datos
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
