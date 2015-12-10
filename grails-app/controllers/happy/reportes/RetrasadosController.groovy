package happy.reportes

import com.itextpdf.text.BaseColor
import com.lowagie.text.pdf.DefaultFontMapper
import com.lowagie.text.pdf.GrayColor
import com.lowagie.text.pdf.PdfContentByte
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfTable
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
import java.security.Timestamp

class RetrasadosController extends Shield {
    def reportesPdfService
    def reportesTramitesRetrasadosService
    def maxLvl = null
    def maxLvl2 = null
    def dbConnectionService

    static scope = "session"

    Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
    Font times18bold = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
    Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
    Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
    Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
    def datosGrafico = [:]
    Font font = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL);
    Font fontBold = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
    def prmsHeaderHoja = [align: Element.ALIGN_CENTER]
    def prmsHeaderHojaLeft = [align: Element.ALIGN_RIGHT]
    def prmsTablaHojaCenter = [align: Element.ALIGN_CENTER]
    def prmsTablaHoja = []

    def fontNombreDep = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
    def fontTotalesDep = new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC)
    def fontNombrePers = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    def fontTotalesPers = new Font(Font.TIMES_ROMAN, 8, Font.BOLDITALIC)
    def fontHeaderTabla = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
    def fontTabla = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)

    def fontPrefectura = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    def fontDireccion = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
    def fontDepartamento = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
    def fontPersona = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

    private void creaRegistros(Document document, String id, LinkedHashMap res, Boolean jefe) {
        creaTituloDep(document, id, res.lvl, res.totalRet, res.totalNoRec, jefe)
        creaTablaTramites(document, res.lvl, res.trams, jefe)
        res.deps.each { rr ->
            creaRegistros(document, rr.key, rr.value, jefe)
        }
    }

    private void creaCeldaHeader(PdfPTable tabla, String titulo) {
        creaCelda(tabla, titulo, fontHeaderTabla, new GrayColor(0.7))
    }

    private void creaCelda(PdfPTable tabla, String cont, Font font, Color bg, Color border) {
        def par = new Paragraph(cont, font)
        PdfPCell cell = new PdfPCell(par);
        if (bg) {
            cell.setBackgroundColor(bg);
        }
        if (border) {
            cell.setBorderColor(border)
        }
        tabla.addCell(cell);
    }

    private void creaCelda(PdfPTable tabla, String cont, Font font, Color bg) {
        creaCelda(tabla, cont, font, bg, null)
    }

    private void creaCeldaBlanca(PdfPTable tabla, String cont, Font font) {
        creaCelda(tabla, cont, font, null, Color.WHITE)
    }

    private void creaCeldaBlanca(PdfPTable tabla, String cont, bg, Font font) {
        creaCelda(tabla, cont, font, bg, Color.WHITE)
    }

    private PdfPTable creaHeaderTablaTramites() {
        def tablaTramites = new PdfPTable(10);
        tablaTramites.setWidthPercentage(100);
        tablaTramites.setSpacingBefore(10)
        tablaTramites.setHeaderRows(1)
//        tablaTramites.setKeepTogether(true)
        creaCeldaHeader(tablaTramites, "Nro.")
        creaCeldaHeader(tablaTramites, "F. Creación")
        creaCeldaHeader(tablaTramites, "De")
        creaCeldaHeader(tablaTramites, "Creado por")
        creaCeldaHeader(tablaTramites, "Para")
        creaCeldaHeader(tablaTramites, "F. Envío")
        creaCeldaHeader(tablaTramites, "F. Recepción")
        creaCeldaHeader(tablaTramites, "F. Límite")
        creaCeldaHeader(tablaTramites, "Retraso (días)")
        creaCeldaHeader(tablaTramites, "Tipo")

        return tablaTramites
    }

    private void creaTablaTramites(Document document, lvl, res, jefe) {
        if (res.size() > 0) {
            def tablaTramitesDep = creaHeaderTablaTramites()
            llenaTablaTramites(tablaTramitesDep, res.oficina?.trams)
            document.add(tablaTramitesDep)
            res.each { k, tram ->
                if (k != "oficina") {
                    def tr = tram.totalRet
                    def tn = tram.totalNoRec
                    creaTituloPersona(document, lvl, tram.nombre, tr, tn)
                    def tablaTramitesPers = creaHeaderTablaTramites()
                    llenaTablaTramites(tablaTramitesPers, tram.trams)
                    document.add(tablaTramitesPers)
                }
            }
        }
    }

    private void llenaTablaTramites(PdfPTable tabla, ArrayList res) {
        res.each { row ->
//            row.each { k, v ->
//                println k + "\t" + v
//            }
//            println "**************************************************************************************************"
            def deDp = row.dptodecd
            def dePr = row.prsn__de
            def para = row.prsnpara ?: row.dptopads

            if (row.trmtcdgo.toString().startsWith("DEX")) {
                def tram = Tramite.get(row.trmt__id.toLong())
                deDp = "EXT"
                dePr = tram.paraExterno
            }

            def rec = row.trmtfcrc ? row.trmtfcrc.format("dd-MM-yyyy HH:mm:ss") : ""
            def lim = row.trmtfclr ? row.trmtfclr.format("dd-MM-yyyy HH:mm:ss") : ""
            def ret = ""
            if (lim != "") {
                ret = new Date() - row.trmtfclr
            }
            def tipo, bg
            if (row.tipo == "ret") {
                tipo = "Retrasado"
                bg = new GrayColor(0.9)
            } else {
                tipo = "Sin recepción"
                bg = Color.WHITE
            }

            creaCelda(tabla, row.trmtcdgo, fontTabla, (Color) bg)
            creaCelda(tabla, row.trmtfccr.format("dd-MM-yyyy HH:mm:ss"), fontTabla, (Color) bg)
            creaCelda(tabla, deDp, fontTabla, (Color) bg)
            creaCelda(tabla, dePr, fontTabla, (Color) bg)
            creaCelda(tabla, para, fontTabla, (Color) bg)
            creaCelda(tabla, row.trmtfcen.format("dd-MM-yyyy HH:mm:ss"), fontTabla, (Color) bg)
            creaCelda(tabla, rec, fontTabla, (Color) bg)
            creaCelda(tabla, lim, fontTabla, (Color) bg)
            creaCelda(tabla, "" + ret, fontTabla, (Color) bg)
            creaCelda(tabla, tipo, fontTabla, (Color) bg)
        }
    }

    private void creaTituloPersona(Document document, lvl, nombre, totalRet, totalNoRec) {
        def tr = totalRet ?: 0
        def tn = totalNoRec ?: 0

        def stars = drawStars(lvl)

        def par = new Paragraph(stars + " " + nombre, fontNombrePers)
        document.add(par)

        par = new Paragraph("Total de trámites retrasados: " + tr + ", Total de trámites sin recepción: " + tn, fontTotalesPers)
        document.add(par)
    }

    private void creaTituloDep(Document document, id, lvl, totalRet, totalNoRec, jefe) {
        def dep = Departamento.get(id)
        def tr = totalRet ?: 0
        def tn = totalNoRec ?: 0
        def str = " Departamento "
        if (lvl == 0) {
            if (jefe) {
                str = "TOTAL"
            } else {
                str = " Prefectura "
            }
        } else if (lvl == 1) {
            str = " Dirección "
        }
        if (jefe) {
            lvl -= 1
        }
        def stars = drawStars(lvl)
        if (str != "TOTAL") {
            str += dep.descripcion + " ($dep.codigo)"
        }
        def par = new Paragraph(stars + str, fontNombreDep)
        document.add(par)

        par = new Paragraph("Total de trámites retrasados: " + tr + ", Total de trámites sin recepción: " + tn, fontTotalesDep)
        document.add(par)
    }

    private String drawStars(lvl) {
        def stars = ""
//        (lvl - 1).times {
//            stars += " "
//        }
        lvl.times {
            stars += "*"
        }
        return stars
    }

    private void creaRegistrosConsolidado(PdfPTable tabla, id, res, jefe) {
        creaFilaDep(tabla, id, res.lvl, res.totalRet, res.totalNoRec, jefe)
        creaFilaPers(tabla, res.lvl + 1, res.trams)
        res.deps.each { k, v ->
            creaRegistrosConsolidado(tabla, k, v, jefe)
        }
    }

    private void creaFilaPers(PdfPTable tabla, lvl, res) {
        if (res.size() > 0) {
            def bg = new GrayColor(0.95)
            def stars = drawStars(lvl)
            res.each { k, tram ->
                creaCeldaBlanca(tabla, stars + " Usuario", bg, fontPersona)
                creaCeldaBlanca(tabla, tram.nombre, fontPersona)
                creaCeldaBlanca(tabla, "" + (tram.totalRet ?: 0), bg, fontPersona)
                creaCeldaBlanca(tabla, "" + (tram.totalNoRec ?: 0), bg, fontPersona)
            }
        }
    }

    private void creaFilaDep(PdfPTable tabla, id, lvl, totalRet, totalNoRec, jefe) {
        def dep = Departamento.get(id)
        def font = fontDepartamento
        def stars = drawStars(lvl)
        def str = " Departamento"
        def bg = new GrayColor(0.9)
        if (lvl == 0) {
            if (jefe) {
                str = "TOTAL"
            } else {
                str = " Prefectura "
            }
            font = fontPrefectura
            bg = new GrayColor(0.7)
        } else if (lvl == 1) {
            str = " Dirección"
            font = fontDireccion
            bg = new GrayColor(0.8)
        }
        def nombre = stars + str
        creaCeldaBlanca(tabla, nombre, bg, font)
        if (str != "TOTAL") {
            creaCeldaBlanca(tabla, dep.descripcion + " ($dep.codigo)", bg, font)
        } else {
            creaCeldaBlanca(tabla, "", bg, font)
        }
        creaCeldaBlanca(tabla, "" + (totalRet ?: 0), bg, font)
        creaCeldaBlanca(tabla, "" + (totalNoRec ?: 0), bg, font)
    }

    def reporteRetrasadosDetalle() {
//        println "reporteRetrasadosDetalle de: " + session.usuario.id
//        println("params " + params)

        //OJO°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°||||
//        def idUsario = session.usuario.id
        def idUsario = params.id
        def per = Persona.get(params.id)
        def enviaRecibe = RolPersonaTramite.findAllByCodigoInList(['R001', 'R002'])
        def baos = new ByteArrayOutputStream()
        def tablaCabeceraRetrasados = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([100]), 10,0)
        def tablaTramite = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([12, 6, 8, 10, 10, 10, 15]), 15, 0)
        def tablaTramiteNoRecibidos = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([10, 5, 20, 10, 13]), 15, 0)
        def tablaCabecera = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([100]), 10,0)
        def tablaTotalesRetrasados = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([100]),0,0)
        def tablaTotalesNoRecibidos = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([100]),0,0)
        def sqls
        def sqlEntre
        def sqlSalida
        def sqlEntreSalida
        def entre
        def entreSalida
        def totalRetrasados = 0
        def totalSin = 0
        def totalRetrasadosPer = 0
        def totalSinPer = 0
        def totalNoRecibidosPer = 0
        def totalNoRecibidos = 0
        def name = "reporteTramitesRetrasados_" + new Date().format("ddMMyyyy_HHmm") + ".pdf";
        def jefe = params.jefe == '1'
        def results = []
        def fechaRecepcion = new Date().format("yyyy/MM/dd HH:mm:ss")
        def ahora = new Date()
        Document document = reportesPdfService.crearDocumento("v", [top: 2, right: 2, bottom: 1.5, left: 2.5])

        def pdfw = PdfWriter.getInstance(document, baos);
        session.tituloReporte = "Reporte detallado de Trámites Retrasados y No Recibidos"

//        if (params.dpto) {
//            def dep = Departamento.get(params.dpto.toLong())
//            session.tituloReporte += "\ndel dpto. $dep.descripcion ($dep.codigo)"
//        } else if (params.prsn) {
//            def per = Persona.get(params.prsn.toLong())
//            def per = Persona.get(params.id)

            def esTriangulo = per.esTrianguloOff()
            session.tituloReporte += "\ndel usuario $per.nombre $per.apellido ($per.login)"

             /*INICIO RETRASADOS DPTO*/
            if (esTriangulo) {
                sqls = "select * from entrada_dpto(" + idUsario + ")"

                def cn = dbConnectionService.getConnection()
                def cn2 = dbConnectionService.getConnection()
                def tipo

                reportesPdfService.addCellTabla(tablaCabeceraRetrasados, new Paragraph("Trámites Retrasados", fontBold), prmsHeaderHoja)

                rowHeaderTramite(tablaTramite)

                cn.eachRow(sqls.toString()){

                    if(it?.trmtfcbq < new Date() && it?.trmtfcrc == null){
//                        tipo = "Sin Recepción"
//                        llenaTablaRetrasados(it, tablaTramite, entre.toString(), tipo)
//                        totalSin += 1
                    }else{
                        if(it.trmtfclr){
                            if(it.trmtfclr < ahora) {
                                sqlEntre = "select * from tmpo_entre('${it?.trmtfclr}' , cast('${fechaRecepcion.toString()}' as timestamp without time zone))"
                                cn2.eachRow(sqlEntre.toString()){ d ->
                                    entre = "${d.dias} días ${d.hora} horas ${d.minu} minutos"
                                }
                                cn2.close()
                                llenaTablaRetrasados(it, tablaTramite, entre.toString(), tipo)
                                totalRetrasados += 1
                            }
                        }
                    }
                }
                cn.close()

                reportesPdfService.addCellTabla(tablaTotalesRetrasados, new Paragraph("Total trámites Retrasados: " + totalRetrasados, fontBold), prmsHeaderHojaLeft)
                /******************************************************************************/
                /*INICIO TRAMITES NO RECIBIDOS DPTO*/

                sqlSalida = "select * from salida_dpto(" + idUsario+ ")"
                def cn3 = dbConnectionService.getConnection()
                def cn5 = dbConnectionService.getConnection()
                def tramiteSalidaDep
                def prtrSalidaDep

                reportesPdfService.addCellTabla(tablaCabecera, new Paragraph("Trámites No Recibidos", fontBold), prmsHeaderHoja)
                rowHeaderTramiteNoRecibidos(tablaTramiteNoRecibidos)

                cn3.eachRow(sqlSalida.toString()){sal->

                    if(sal.edtrcdgo == 'E004' || sal.edtrcdgo == 'E003'){  //estados enviado:E003 y recibido: E004
                        tramiteSalidaDep = Tramite.get(sal?.trmt__id)
                        prtrSalidaDep = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteInListAndFechaRecepcionIsNull(tramiteSalidaDep, enviaRecibe)
                        prtrSalidaDep.each {
                                sqlEntreSalida="select * from tmpo_entre('${sal?.trmtfcen}' , cast('${fechaRecepcion.toString()}' as timestamp without time zone))"
                                cn5.eachRow(sqlEntreSalida.toString()){ d ->
                                    entreSalida = "${d.dias} días ${d.hora} horas ${d.minu} minutos"
                                }
                                cn5.close()
                                llenaTablaNoRecibidos(sal, tablaTramiteNoRecibidos,entreSalida.toString())
                                totalNoRecibidos += 1
                        }
                    }
                }
                cn3.close()
                reportesPdfService.addCellTabla(tablaTotalesNoRecibidos, new Paragraph("Total trámites No Recibidos : " + totalNoRecibidos, fontBold), prmsHeaderHojaLeft)
        /*************************************************************************/
            } else {

                /*INICIO RETRASADOS PRSN PDF*/
                sqls = "select * from entrada_prsn(" + idUsario + ")"
                def cn = dbConnectionService.getConnection()
                def cn2 = dbConnectionService.getConnection()
                def tipo

                reportesPdfService.addCellTabla(tablaCabeceraRetrasados, new Paragraph("Trámites Retrasados", fontBold), prmsHeaderHoja)
                rowHeaderTramite(tablaTramite)

                cn.eachRow(sqls.toString()){
                    if(it?.trmtfcbq < new Date() && it?.trmtfcrc == null){
//                        tipo = "Sin Recepción"
//                        llenaTablaRetrasados(it, tablaTramite, entre.toString(), tipo)
//                        totalSin += 1
                    }else{
                        if(it.trmtfclr){
                            if(it.trmtfclr < ahora) {
                                sqlEntre = "select * from tmpo_entre('${it?.trmtfclr}' , cast('${fechaRecepcion.toString()}' as timestamp without time zone))"
                                cn2.eachRow(sqlEntre.toString()){ d ->
                                    entre = "${d.dias} días ${d.hora} horas ${d.minu} minutos"
                                }
                                cn2.close()
                                llenaTablaRetrasados(it, tablaTramite, entre.toString(), tipo)
                                totalRetrasadosPer += 1
                            }
                        }
                    }
                }

                reportesPdfService.addCellTabla(tablaTotalesRetrasados, new Paragraph("Total trámites Retrasados : " + totalRetrasadosPer, fontBold), prmsHeaderHojaLeft)
         /********************************************************************************/
         /*INICIO NO RECIBIDOS PRSN PDF*/
                sqlSalida = "select * from salida_prsn(" + idUsario+ ")"
                def cn4 = dbConnectionService.getConnection()
                def cn6 = dbConnectionService.getConnection()
                def tramiteSalida
                def prtrSalida

                reportesPdfService.addCellTabla(tablaCabecera, new Paragraph("Trámites No Recibidos", fontBold), prmsHeaderHoja)
                rowHeaderTramiteNoRecibidos(tablaTramiteNoRecibidos)

                cn4.eachRow(sqlSalida.toString()){sal->

                    if(sal.edtrcdgo == 'E004' || sal.edtrcdgo == 'E003'){
                        tramiteSalida = Tramite.get(sal?.trmt__id)
                        prtrSalida = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteInListAndFechaRecepcionIsNull(tramiteSalida, enviaRecibe)
                        prtrSalida.each {
                                sqlEntreSalida="select * from tmpo_entre('${sal?.trmtfcen}' , cast('${fechaRecepcion.toString()}' as timestamp without time zone))"
                                cn6.eachRow(sqlEntreSalida.toString()){ d ->
                                    entreSalida = "${d.dias} días ${d.hora} horas ${d.minu} minutos"
                                }
                                cn6.close()
                                llenaTablaNoRecibidos(sal, tablaTramiteNoRecibidos,entreSalida.toString())
                                totalNoRecibidosPer += 1
                        }
                    }
                }
                reportesPdfService.addCellTabla(tablaTotalesNoRecibidos, new Paragraph("Total trámites No Recibidos : " + totalNoRecibidosPer, fontBold), prmsHeaderHojaLeft)
            }

        reportesPdfService.membrete(document)
        document.open();
        reportesPdfService.propiedadesDocumento(document, "reporteTramitesRetrasados")
        document.add(tablaCabeceraRetrasados);
        document.add(tablaTramite);
        document.add(tablaTotalesRetrasados);
        document.add(tablaCabecera);
        document.add(tablaTramiteNoRecibidos);
        document.add(tablaTotalesNoRecibidos);

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }


    def rowHeaderTramite(tablaTramite) {
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Trámite No.", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("De", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Creado Por", fontBold), prmsHeaderHoja)
//        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Para", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Fecha Envío", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Fecha Recepción", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Fecha Límite", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Tiempo de Retraso", fontBold), prmsHeaderHoja)
//        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Tipo", fontBold), prmsHeaderHoja)
    }


    def rowHeaderTramiteNoRecibidos(tablaTramite) {
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Trámite No.", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("De", fontBold), prmsHeaderHoja)
//        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Creado Por", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Para", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Fecha Envío", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Tiempo de Retraso", fontBold), prmsHeaderHoja)
    }

    def llenaTablaRetrasados (it, tablaTramite, entre, tipo){
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it.trmtcdgo, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.deprdpto, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.deprlogn, font), prmsTablaHoja)
//        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.prtrdpto, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.trmtfcen ? it?.trmtfcen?.format("dd-MM-yyyy HH:mm") : "", font), prmsTablaHojaCenter)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.trmtfcrc ? it?.trmtfcrc?.format("dd-MM-yyyy HH:mm") : "", font), prmsTablaHojaCenter)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.trmtfclr ? it?.trmtfclr?.format("dd-MM-yyyy HH:mm") : "", font), prmsTablaHojaCenter)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(entre, font), prmsTablaHojaCenter)
//        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(tipo, font), prmsTablaHojaCenter)
    }

    def llenaTablaNoRecibidos (it, tablaTramite, entreSalida){
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it.trmtcdgo, font), prmsTablaHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.deprdpto, font), prmsTablaHoja)
//        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.deprdscr, font), prmsTablaHoja)
        if(it?.prtrprsn){
            reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.prtrprsn, font), prmsTablaHoja)
        } else {
            reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.prtrdpto, font), prmsTablaHoja)
        }
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.trmtfcen ? it?.trmtfcen?.format("dd-MM-yyyy HH:mm") : "", font), prmsTablaHojaCenter)
//        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.trmtfcrc ? it?.trmtfcrc?.format("dd-MM-yyyy HH:mm") : "", font), prmsTablaHojaCenter)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph(entreSalida, font), prmsTablaHojaCenter)
    }

//    def reporteRetrasadosConsolidado() {
//        def baos = new ByteArrayOutputStream()
//        def name = "reporteConsolidadoTramitesRetrasados_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
//        def jefe = params.jefe == '1'
//        def results = [], resGraph = [], dep = null, per = null
//        Document document = reportesPdfService.crearDocumento("vert", [top: 2.5, right: 2.5, bottom: 1.5, left: 3])
//        def pdfw = PdfWriter.getInstance(document, baos)
//        session.tituloReporte = "Reporte consolidado de Trámites Retrasados y No Recibidos"
//        if (params.dpto) {
//            def dep1 = Departamento.get(params.dpto.toLong())
//            session.tituloReporte += "\ndel dpto. $dep1.descripcion ($dep1.codigo)"
//            def res = reportesTramitesRetrasadosService.datos(params.dpto)
//            results = res.res
//            resGraph = res.resGraph
//            dep = Departamento.get(params.dpto.toLong())
//        } else if (params.prsn) {
//            per = Persona.get(params.prsn)
//            session.tituloReporte += "\ndel usuario $per.nombre $per.apellido ($per.login)"
//            if (per.esTrianguloOff()) {
//                session.tituloReporte += "\n[Bandeja de entrada del departamento]"
//                def res = reportesTramitesRetrasadosService.datos(per.departamentoId, params.prsn)
//                results = res.res
//                resGraph = res.resGraph
//            } else {
//                def res = reportesTramitesRetrasadosService.datosPersona(params.prsn)
//                results = res.res
//                resGraph = res.resGraph
//            }
//        }
//        reportesPdfService.membrete(document)
//        document.open()
//        reportesPdfService.propiedadesDocumento(document, "reporteTramitesRetrasados")
//
//        PdfPTable tablaTramites = reportesPdfService.crearTabla([18, 60, 10, 12] as int[], 10, 10)
//
//        creaCeldaBlanca(tablaTramites, "", fontHeaderTabla)
//        creaCeldaBlanca(tablaTramites, "", fontHeaderTabla)
//        creaCeldaBlanca(tablaTramites, "Retrasados", fontHeaderTabla)
//        creaCeldaBlanca(tablaTramites, "Sin recepción", fontHeaderTabla)
//
//
//
//
//        //calculo totales retrasados y no recibidos----------------------------------------------
//
//
//
//        def idUsario = session.usuario.id
//        def enviaRecibe = RolPersonaTramite.findAllByCodigoInList(['R001', 'R002'])
//        def sqls
//        def sqlSalida
//        def totalRetrasados = 0
//        def totalRetrasadosPer = 0
//        def totalNoRecibidosPer = 0
//        def totalNoRecibidos = 0
//        def ahora = new Date()
//        def esTriangulo =  Persona.get(session.usuario.id).esTrianguloOff()
//
//
//        if (esTriangulo) {
//
//            sqls = "select * from entrada_dpto(" + idUsario + ")"
//            def cn = dbConnectionService.getConnection()
//
//            cn.eachRow(sqls.toString()){
//                if(it.trmtfclr < ahora) {
//                    totalRetrasados += 1
//                }
//            }
//            cn.close()
//
//            sqlSalida = "select * from salida_dpto(" + idUsario+ ")"
//            def cn3 = dbConnectionService.getConnection()
//            def tramiteSalidaDep
//            def prtrSalidaDep
//
//            cn3.eachRow(sqlSalida.toString()){sal->
//                if(sal.edtrcdgo == 'E004' || sal.edtrcdgo == 'E003'){  //estados enviado:E003 y recibido: E004
//                    tramiteSalidaDep = Tramite.get(sal?.trmt__id)
//                    prtrSalidaDep = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteInListAndFechaRecepcionIsNull(tramiteSalidaDep, enviaRecibe)
//                    prtrSalidaDep.each {
//                        totalNoRecibidos += 1
//                    }
//                }
//            }
//            cn3.close()
//        } else {
//            sqls = "select * from entrada_prsn(" + idUsario + ")"
//            def cn = dbConnectionService.getConnection()
//            cn.eachRow(sqls.toString()){
//                if(it.trmtfclr < ahora) {
////                   totalRetrasadosPer += 1
//                   totalRetrasados += 1
//                }
//            }
//
//            sqlSalida = "select * from salida_prsn(" + idUsario+ ")"
//            def cn4 = dbConnectionService.getConnection()
//            def tramiteSalida
//            def prtrSalida
//
//            cn4.eachRow(sqlSalida.toString()){sal->
//
//                if(sal.edtrcdgo == 'E004' || sal.edtrcdgo == 'E003'){
//
//                    tramiteSalida = Tramite.get(sal?.trmt__id)
//                    prtrSalida = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteInListAndFechaRecepcionIsNull(tramiteSalida, enviaRecibe)
//                    prtrSalida.each {
////                        totalNoRecibidosPer += 1
//                        totalNoRecibidos += 1
//                    }
//                }
//            }
//        }
//
//        println("Retrasados " + totalRetrasados)
//        println("No recibidos " + totalNoRecibidos)
//
//
//
//        /* ************************************************* Graficos *************************** */
//        boolean conGraficos
//        try {
//            conGraficos = true
//            def width = 550
//            def height = 250
//            PdfContentByte contentByte = pdfw.getDirectContent();
//            PdfTemplate templateSinRecepcion = contentByte.createTemplate(width, height);
//            Graphics2D graphics2dSinRecepcion = templateSinRecepcion.createGraphics(width, height, new DefaultFontMapper());
//            PdfTemplate templateRetrasados = contentByte.createTemplate(width, height);
//            Graphics2D graphics2dRetrasados = templateRetrasados.createGraphics(width, height, new DefaultFontMapper());
//            Rectangle2D rectangle2dSinRecepcion = new Rectangle2D.Double(0, 0, width, height);
//            Rectangle2D rectangle2dRetrasados = new Rectangle2D.Double(0, 0, width, height);
//
//////        PARA GRAFICO PASTEL
//            DefaultPieDataset dataSetRet = new DefaultPieDataset();
//            DefaultPieDataset dataSetNoRec = new DefaultPieDataset();
//            def ttl = " por departamento"
//            def existeSinRecepcion = false
//            def existeRetrasados = false
//
//            def limiteCant = 15
//            def limitePorc = 5
//            def totalOtrosRet = 0
//            def totalOtrosNorec = 0
//            def conOtrosRet = false
//            def conOtrosNorec = false
//
//            def cantRet = resGraph.ret.size()
//            def cantNorec = resGraph.norec.size()
//
//            if (cantRet > limiteCant) {
//                conOtrosRet = true;
//            }
//            if (cantNorec > limiteCant) {
//                conOtrosNorec = true;
//            }
//
//            def totalRet = results["11"].totalRet
//            def totalNorec = results["11"].totalNoRec
//
//
//
//
//
//            resGraph.ret.each { k, v ->
//                def prct = (v.total * 100) / totalRet
//                if (cantRet > 1) {
//                    if (conOtrosRet && prct < limitePorc) {
//                        totalOtrosRet += v.total
//                    } else {
//                        dataSetRet.setValue(v.codigo, v.total)
//                    }
//                    existeRetrasados = true
//                } else {
//                    cantRet = v.det.size()
//                    totalOtrosRet = 0
//                    conOtrosRet = false
//                    if (cantRet > limiteCant) {
//                        conOtrosRet = true
//                    }
//                    v.det.each { k1, v1 ->
//                        prct = (v1.total * 100) / totalRet
//                        def n = v1.nombre
//                        if (n == "Oficina ") {
//                            n += v.codigo
//                        }
//                        if (conOtrosRet && prct < limitePorc) {
//                            totalOtrosRet += v1.total
//                        } else {
//                            dataSetRet.setValue(n, v1.total)
//                        }
//                        existeRetrasados = true
//                    }
//                }
//            }
////            resGraph.norec.each { k, v ->
////                def prct = (v.total * 100) / totalNorec
////                if (cantNorec > 1) {
////                    if (conOtrosNorec && prct < limitePorc) {
////                        totalOtrosNorec += v.total
////                    } else {
////                        dataSetNoRec.setValue(v.codigo, v.total)
////                    }
////                    existeSinRecepcion = true
////                } else {
////                    cantNorec = v.det.size()
////                    totalOtrosNorec = 0
////                    conOtrosNorec = false
////                    if (cantNorec > limiteCant) {
////                        conOtrosNorec = true
////                    }
////                    v.det.each { k1, v1 ->
////                        prct = (v1.total * 100) / totalNorec
////                        def n = v1.nombre
////                        if (n == "Oficina ") {
////                            n += v.codigo
////                        }
////                        if (conOtrosNorec && prct < limitePorc) {
////                            totalOtrosNorec += v1.total
////                        } else {
////                            dataSetNoRec.setValue(n, v1.total)
////                        }
////                        existeSinRecepcion = true
////                    }
////                }
////            }
//
//            if (conOtrosRet && totalOtrosRet > 0) {
//                dataSetRet.setValue("Otros (<$limitePorc%)", totalOtrosRet)
//            }
//            if (conOtrosNorec && totalOtrosNorec > 0) {
//                dataSetNoRec.setValue("Otros (<$limitePorc%)", totalOtrosNorec)
//            }
//
////            results.each { k, v ->
////                def tr = v.totalRet ?: 0
////                def tn = v.totalNoRec ?: 0
////                if (tr > 0) {
////                    existeRetrasados = true
////                }
////                if (tn > 0) {
////                    existeSinRecepcion = true
////                }
////                if (v.deps.size() > 1) {
////                    band = true
////                    v.deps.each { dId, trams ->
////                        def dep = Departamento.get(dId.toLong())
////                        if (trams.totalRet && trams.totalRet > 0) {
////                            dataSetRet.setValue(dep.codigo, trams.totalRet)
////                        }
////                        if (trams.totalNoRec && trams.totalNoRec > 0) {
////                            dataSetNoRec.setValue(dep.codigo, trams.totalNoRec)
////                        }
////                    }
////                }
////            }
////
////            if (!band) {
////                def resGraph = reportesTramitesRetrasadosService.datosGraph(params.dpto, params.prsn)
////            }
//
//
//            dataSetRet.setValue("Retrasados", totalRetrasados)
//
//            JFreeChart chartSinRecepcion = ChartFactory.createPieChart("Documentos sin recepción" + ttl, dataSetNoRec, true, true, false);
//            chartSinRecepcion.setTitle(
//                    new org.jfree.chart.title.TextTitle("Documentos sin recepción" + ttl,
//                            new java.awt.Font("SansSerif", java.awt.Font.BOLD, 15)
//                    )
//            );
//            JFreeChart chartRetrasados = ChartFactory.createPieChart("Documentos retrasados", dataSetRet, true, true, false);
//            chartRetrasados.setTitle(
//                    new org.jfree.chart.title.TextTitle("Documentos retrasados" + ttl,
//                            new java.awt.Font("SansSerif", java.awt.Font.BOLD, 15)
//                    )
//            );
//
//            /* getPlot method of JFreeChart class returns the PiePlot object back to us */
//            PiePlot ColorConfigurator = (PiePlot) chartSinRecepcion.getPlot(); /* get PiePlot object for changing */
//            PiePlot ColorConfigurator2 = (PiePlot) chartRetrasados.getPlot(); /* get PiePlot object for changing */
//            /* A format mask specified to display labels. Here {0} is the section name, and {1} is the value.
//            We can also use {2} which will display a percent value */
//            ColorConfigurator.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{1} docs. ({2})"));
//            ColorConfigurator2.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{1} docs. ({2})"));
//            /* Set color of the label background on the pie chart */
//            ColorConfigurator.setLabelBackgroundPaint(new Color(220, 220, 220));
//            ColorConfigurator2.setLabelBackgroundPaint(new Color(220, 220, 220));
//
//            chartSinRecepcion.draw(graphics2dSinRecepcion, rectangle2dSinRecepcion);
//            chartRetrasados.draw(graphics2dRetrasados, rectangle2dRetrasados);
//
//            graphics2dSinRecepcion.dispose();
//            graphics2dRetrasados.dispose();
//
//            def posyGraf1 = 450
//            def posyGraf2 = 180
//            if (existeRetrasados) {
//                contentByte.addTemplate(templateRetrasados, 30, posyGraf1);
//                if (existeSinRecepcion) {
//                    contentByte.addTemplate(templateSinRecepcion, 30, posyGraf2);
//                }
//            } else {
//                if (existeSinRecepcion) {
//                    contentByte.addTemplate(templateSinRecepcion, 30, posyGraf1);
//                }
//            }
//
//        } catch (e) {
//            println "ERROR GRAFICOS::::::: "
//            e.printStackTrace();
//            conGraficos = false
//        }
//        if (conGraficos) {
//            document.newPage()
//        }
//        /* ************************************************* FIN Graficos *********************** */
//
//        /* ************************************************* Arbol ****************************** */
////        results.each() { k, v ->
////            creaRegistrosConsolidado(tablaTramites, k, v, jefe)
////        }
////        document.add(tablaTramites)
//        /* ************************************************* FIN Arbol *************************** */
//
//        document.close();
//        pdfw.close()
//        byte[] b = baos.toByteArray();
//        response.setContentType("application/pdf")
//        response.setHeader("Content-disposition", "attachment; filename=" + name)
//        response.setContentLength(b.length)
//        response.getOutputStream().write(b)
//    }

    def reporteRetrasadosConsolidado() {

        def fileName = "documentos_retrasados_"
        def title2 = "Documentos retrasados por "
        def pers = Persona.get(session.usuario.id)
        def title = "Documentos de " + pers?.nombre + " " + pers?.apellido + " (" + pers?.login + ")"
        def baos = new ByteArrayOutputStream()
        def name = fileName + "_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Document document = reportesPdfService.crearDocumento([top: 2, right: 2, bottom: 1.5, left: 2.5])
        def pdfw = PdfWriter.getInstance(document, baos);

        session.tituloReporte = title
        reportesPdfService.membrete(document)
        document.open();
        reportesPdfService.propiedadesDocumento(document, "trámite")
        def paramsCenter = [align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def paramsLeft = [align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def prmsHeaderHojaRight = [align: Element.ALIGN_RIGHT]
        def prmsHeaderHoja = [align: Element.ALIGN_CENTER]
        def totalResumenGenerado = 0
        def totalRecibido = 0
        def usuario = Persona.get(session.usuario.id)
        def departamentoUsuario = usuario?.departamento?.id
        def sqlGen
        def sql
        def cn2 = dbConnectionService.getConnection()

        def idUsario = session.usuario.id
        def enviaRecibe = RolPersonaTramite.findAllByCodigoInList(['R001', 'R002'])
        def sqls
        def sqlSalida
        def totalRetrasados = 0
        def totalRetrasadosPer = 0
        def totalNoRecibidosPer = 0
        def totalNoRecibidos = 0
        def ahora = new Date()
        def esTriangulo =  Persona.get(session.usuario.id).esTrianguloOff()

    /*INICIO RETRASADOS CONSOLIDADO DPTO PDF*/
        if (esTriangulo) {

            sqls = "select * from entrada_dpto(" + idUsario + ")"
            def cn = dbConnectionService.getConnection()

            cn.eachRow(sqls.toString()){
                if(it?.trmtfcbq < new Date() && it?.trmtfcrc == null){

                }else{
                    if(it.trmtfclr < ahora) {
                        totalRetrasados += 1
                    }
                }
            }
            cn.close()
    /*********************************************/
    /*INICIO NO RECIBIDOS CONSOLIDADO DPTO PDF*/
            sqlSalida = "select * from salida_dpto(" + idUsario+ ")"
            def cn3 = dbConnectionService.getConnection()
            def tramiteSalidaDep
            def prtrSalidaDep

            cn3.eachRow(sqlSalida.toString()){sal->
                if(sal.edtrcdgo == 'E004' || sal.edtrcdgo == 'E003'){  //estados enviado:E003 y recibido: E004
                    tramiteSalidaDep = Tramite.get(sal?.trmt__id)
                    prtrSalidaDep = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteInListAndFechaRecepcionIsNull(tramiteSalidaDep, enviaRecibe)
                    prtrSalidaDep.each {
                        totalNoRecibidos += 1
                    }
                }
            }
            cn3.close()
    /***********************************************/
        } else {
    /*INICIO RETRASADOS CONSOLIDADO PRSN PDF*/
            sqls = "select * from entrada_prsn(" + idUsario + ")"
            def cn = dbConnectionService.getConnection()
            cn.eachRow(sqls.toString()){
                if(it?.trmtfcbq < new Date() && it?.trmtfcrc == null){

                }else{
                    if(it.trmtfclr < ahora) {
                        totalRetrasados += 1
                    }
                }
            }
    /******************************************/
    /*INICIO NO RECIBIDOS CONSOLIDADO PRSN PDF*/
            sqlSalida = "select * from salida_prsn(" + idUsario+ ")"
            def cn4 = dbConnectionService.getConnection()
            def tramiteSalida
            def prtrSalida

            cn4.eachRow(sqlSalida.toString()){sal->
                if(sal.edtrcdgo == 'E004' || sal.edtrcdgo == 'E003'){
                    tramiteSalida = Tramite.get(sal?.trmt__id)
                    prtrSalida = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteInListAndFechaRecepcionIsNull(tramiteSalida, enviaRecibe)
                    prtrSalida.each {
                        totalNoRecibidos += 1
                    }
                }
            }
        }


        def tablaTotalesRetrasados = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([50,20,15,15]),0,0)

        reportesPdfService.addCellTabla(tablaTotalesRetrasados, new Paragraph("Usuario", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTotalesRetrasados, new Paragraph("Perfil", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTotalesRetrasados, new Paragraph("Retrasados", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTotalesRetrasados, new Paragraph("No Recibidos", fontBold), prmsHeaderHoja)

        reportesPdfService.addCellTabla(tablaTotalesRetrasados, new Paragraph(pers?.nombre + " " + pers?.apellido + "  (" + pers?.login + ")", font), paramsLeft)
        reportesPdfService.addCellTabla(tablaTotalesRetrasados, new Paragraph(" " + session?.perfil, font), paramsLeft)
        reportesPdfService.addCellTabla(tablaTotalesRetrasados, new Paragraph(" " + totalRetrasados, font), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTotalesRetrasados, new Paragraph(" " + totalNoRecibidos, font), prmsHeaderHoja)

        document.add(tablaTotalesRetrasados)

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

    /* ************ HACIA ABAJO: REPORTES ANTIGUOS CON EL METODO ANTERIOR Q DEMORABA UN MONTON ******************/
//    def reporteRetrasadosDetalle_old() {
////        println ">>>>aqui<<<<"
//        maxLvl = null
//        def estadoR = EstadoTramite.findByCodigo("E004")
//        def estadoE = EstadoTramite.findByCodigo("E003")
//        def rolPara = RolPersonaTramite.findByCodigo("R001")
//        def rolCopia = RolPersonaTramite.findByCodigo("R002")
//        def now = new Date()
//
//        def datos = [:]
//        def usuario = null
//        def deps = []
//        def puedeVer = []
//        def extraPersona = "and "
//        def depStr = ""
//        if (params.prsn) {
//            usuario = Persona.get(params.prsn)
//            extraPersona += "persona=" + usuario.id + " "
//            if (usuario.esTriangulo) {
//                extraPersona = "and (persona=${usuario.id} or departamento = ${usuario.departamento.id})"
//            }
//            def padre = usuario.departamento.padre
//            while (padre) {
//                deps.add(padre)
//                padre = padre.padre
//            }
//            deps.add(usuario.departamento)
//            puedeVer.add(usuario.departamento)
//            def hi = Departamento.findAllByPadre(usuario.departamento)
//            while (hi.size() > 0) {
//                puedeVer += hi
//                hi = Departamento.findAllByPadreInList(hi)
//            }
//
//        }
//
//        if (params.dpto) {
//            def departamento = Departamento.get(params.dpto)
//            def padre = departamento.padre
//            while (padre) {
//                deps.add(padre)
//                padre = padre.padre
//            }
//            deps.add(departamento)
//            puedeVer.add(departamento)
//            def hi = Departamento.findAllByPadre(departamento)
//            while (hi.size() > 0) {
//                puedeVer += hi
//                hi = Departamento.findAllByPadreInList(hi)
//            }
//        }
//
//        def pdt = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite where" +
//                " fechaEnvio is not null " +
//                "and rolPersonaTramite in (${rolPara.id},${rolCopia.id}) " +
//                "and estado in (${estadoR.id},${estadoE.id}) ${usuario ? extraPersona : ''} ")
//
//        if (pdt) {
//            pdt.each { pd ->
//                pd.refresh()
//                if (pd.tramite.externo != "1" || pd.tramite == null) {
//                    def resp = Tramite.findAllByAQuienContesta(pd)
//                    if (resp.size() == 0) {
//                        if (pd.fechaLimite < now || (!pd.fechaRecepcion)) {
//                            datos = reportesPdfService.jerarquia(datos, pd)
//                        }
//                    }
//                }
//            }
//        }
//
//        def baos = new ByteArrayOutputStream()
//        def name = "reporteTramitesRetrasados_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
//
//        def prmsHeaderHoja = [border: Color.WHITE]
//        def prmsHeaderHoja1 = [border: Color.WHITE, bordeTop: "1", bordeBot: "1"]
//        times8boldWhite.setColor(Color.WHITE)
//        times10boldWhite.setColor(Color.WHITE)
//        Document document = reportesPdfService.crearDocumento("svt", [top: 2, right: 2, bottom: 1.5, left: 2.5])
//        session.tituloReporte = "Reporte detallado de Trámites Retrasados y Sin Recepción"
//        def pdfw = PdfWriter.getInstance(document, baos);
//        reportesPdfService.membrete(document)
//        document.open();
//        reportesPdfService.propiedadesDocumento(document, "reporteTramitesRetrasados")
//
//        def contenido = new Paragraph();
//        def hijos = datos["hijos"]
//        if (datos) {
//            if ((puedeVer.id.contains(datos["objeto"].id))) {
//                maxLvl = datos
//            }
//        }
//
//        def total = 0
//        def totalSr = 0
//        PdfPTable tablaTramites
//        hijos.each { lvl ->
//            if (puedeVer.size() == 0 || (puedeVer.id.contains(lvl["objeto"].id))) {
//                if (maxLvl == null) {
//                    maxLvl = lvl
//                }
//                def totalNode = 0
//                def totalNodeSr = 0
//                def par = new Paragraph("-" + lvl["objeto"], times12bold)
//                document.add(par)
//                def par2 = new Paragraph("", times8normal)
//                par2.setSpacingBefore(4)
//                def par3 = new Paragraph("", times8normal)
//                par3.setSpacingBefore(4)
//                if (lvl["tramites"].size() > 0) {
//                    lvl["triangulos"].each { t ->
//                        par = new Paragraph("Usuario: ${t.departamento.codigo}:" + t + " - Trámites de oficina  - [  Retrasados: ${lvl['ofiRs']}, Sin Recepción: " + lvl["ofiRz"] + " ]", times8bold)
//                        if (totalNode == 0) {
//                            totalNode += lvl["rezagados"]
//                        }
//                        if (totalNodeSr == 0) {
//                            totalNodeSr += lvl["retrasados"]
//                        }
//                        document.add(par)
//                    }
//
//                }
//                if (params.detalle) {
//                    tablaTramites = new PdfPTable(9);
//                    tablaTramites.setWidthPercentage(100);
//                    par = new Paragraph("Nro.", times8bold)
//                    PdfPCell cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("F. Creación", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("De", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("Creado por", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("F. Envío", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("F. Recepcíon", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("F. Límite", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("Retraso (días)", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("Tipo", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    lvl["tramites"].each { t ->
//                        par = new Paragraph("${t.tramite.codigo} ${t.rolPersonaTramite.codigo == 'R002' ? '[CC]' : ''}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("${t.tramite.fechaCreacion.format('dd-MM-yyyy HH:mm')}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        if (t.tramite.deDepartamento) {
//                            par = new Paragraph("${t.tramite.deDepartamento.codigo}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                        } else {
//                            par = new Paragraph("${t.tramite.de.departamento.codigo}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                        }
//                        par = new Paragraph("${t.tramite.de.login}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("${t.fechaEnvio.format('dd-MM-yyyy hh:mm')}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("${(t.fechaRecepcion) ? t.fechaRecepcion?.format('dd-MM-yyyy hh:mm') : ''}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("${(t.fechaLimiteRespuesta) ? t.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm') : ''}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("${(t.fechaLimiteRespuesta) ? (now - t.fechaLimiteRespuesta) : ''}", times8normal)
//                        cell = new PdfPCell(par);
//                        cell.setHorizontalAlignment(1)
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("${(t.fechaLimiteRespuesta) ? 'Retrasado' : 'No recibido'}", times8normal)
//                        cell = new PdfPCell(par);
//                        cell.setHorizontalAlignment(1)
//                        tablaTramites.addCell(cell);
//
//                    }
//                    if (lvl["tramites"].size() > 0) {
//                        par2.add(tablaTramites)
//                        document.add(par2)
//                    }
//                }
//                lvl["personas"].each { p ->
//                    par3 = null
//                    par3 = new Paragraph("", times8normal)
//                    par3.setSpacingBefore(0.001)
//                    par = new Paragraph("Usuario: ${p["objeto"].departamento.codigo}:" + p["objeto"] + " - ${p['objeto'].login} - [ Retrasados: ${p['rezagados']}, Sin Recepción: " + p["retrasados"] + " ]", times8bold)
//                    par.setSpacingBefore(17)
//                    document.add(par)
//                    totalNode += p["rezagados"]
//                    totalNodeSr += p["retrasados"]
//
//                    if (params.detalle) {
//                        tablaTramites = new PdfPTable(9);
//                        tablaTramites.setWidthPercentage(100);
//                        par = new Paragraph("Nro.", times8bold)
//                        PdfPCell cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("F. Creación", times8bold)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("De", times8bold)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("Creado por", times8bold)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("F. Envío", times8bold)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("F. Recepcíon", times8bold)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("F. Límite", times8bold)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("Retraso (días)", times8bold)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("Tipo", times8bold)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        p["tramites"].each { t ->
//                            par = new Paragraph("${t.tramite.codigo} ${t.rolPersonaTramite.codigo == 'R002' ? '[CC]' : ''}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                            par = new Paragraph("${t.tramite.fechaCreacion.format('dd-MM-yyyy HH:mm')}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                            if (t.tramite.deDepartamento) {
//                                par = new Paragraph("${t.tramite.deDepartamento.codigo}", times8normal)
//                                cell = new PdfPCell(par);
//                                tablaTramites.addCell(cell);
//                            } else {
//                                par = new Paragraph("${t.tramite.de.departamento.codigo}", times8normal)
//                                cell = new PdfPCell(par);
//                                tablaTramites.addCell(cell);
//                            }
//                            par = new Paragraph("${t.tramite.de.login}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                            par = new Paragraph("${t.fechaEnvio.format('dd-MM-yyyy hh:mm')}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                            par = new Paragraph("${(t.fechaRecepcion) ? t.fechaRecepcion?.format('dd-MM-yyyy hh:mm') : ''}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                            par = new Paragraph("${(t.fechaLimiteRespuesta) ? t.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm') : ''}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                            par = new Paragraph("${(t.fechaLimiteRespuesta) ? (now - t.fechaLimiteRespuesta) : ''}", times8normal)
//                            cell = new PdfPCell(par);
//                            cell.setHorizontalAlignment(1)
//                            tablaTramites.addCell(cell);
//                            par = new Paragraph("${(t.fechaLimiteRespuesta) ? 'Retrasado' : 'Sin recepción'}", times8normal)
//                            cell = new PdfPCell(par);
//                            cell.setHorizontalAlignment(1)
//                            tablaTramites.addCell(cell);
//                        }
//                        if (p["tramites"].size() > 0) {
//                            par3.add(tablaTramites)
//                            document.add(par3)
//                        }
//                    }
//                }
//                total += totalNode
//                totalSr += totalNodeSr
//
//            }
//            def res = imprimeHijosPdf(lvl, document, tablaTramites, params, usuario, deps, puedeVer)
//            total += res[0]
//            totalSr += res[1]
//
//        }
//        def par = new Paragraph(" ", times12bold)
//        document.add(par);
//        if (maxLvl) {
//            par = new Paragraph("Gran Total                                                                                                                                          Retrasados: ${maxLvl['rezagados']}       Sin Recepción: ${maxLvl['retrasados']}     ", times12bold)
//            document.add(par);
//        }
////        println "maxlvl " + maxLvl
//        document.close();
//        pdfw.close()
//        byte[] b = baos.toByteArray();
//        response.setContentType("application/pdf")
//        response.setHeader("Content-disposition", "attachment; filename=" + name)
//        response.setContentLength(b.length)
//        response.getOutputStream().write(b)
//    }
//
//    def imprimeHijosPdf(arr, contenido, tablaTramites, params, usuario, deps, puedeVer) {
//        def total = 0
//        def totalSr = 0
//        def datos = arr["hijos"]
//        def now = new Date()
//        datos.each { lvl ->
//            if (puedeVer.size() == 0 || (puedeVer.id.contains(lvl["objeto"].id))) {
//                if (maxLvl == null) {
//                    maxLvl = lvl
//                }
//                def par = new Paragraph("-" + lvl["objeto"], times12bold)
//                def totalNode = 0
//                def totalNodeSr = 0
//                contenido.add(par)
//                def par2 = new Paragraph("", times8normal)
//                par2.setSpacingBefore(4)
//                def par3 = new Paragraph("", times8normal)
//                par3.setSpacingBefore(4)
//
//                if (lvl["tramites"].size() > 0) {
//                    lvl["triangulos"].each { t ->
//                        par = new Paragraph("Usuario: ${t.departamento.codigo}:" + t + " - Trámites de oficina - [ Retrasados: ${lvl['ofiRz']}, Sin Recepción: " + lvl["ofiRs"] + " ]", times8bold)
//                        contenido.add(par)
//                        if (totalNode == 0) {
//                            totalNode += lvl["rezagados"]
//                        }
//                        if (totalNodeSr == 0) {
//                            totalNodeSr += lvl["retrasados"]
//                        }
//                    }
//
//                }
//                if (params.detalle) {
//                    tablaTramites = new PdfPTable(9);
//                    tablaTramites.setWidthPercentage(100);
//                    par = new Paragraph("Nro.", times8bold)
//                    PdfPCell cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("F. Creación", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("De", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("Creado por", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("F. Envío", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("F. Recepcíon", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("F. Límite", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("Retraso (días)", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("Tipo", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    lvl["tramites"].each { t ->
//                        par = new Paragraph("${t.tramite.codigo} ${t.rolPersonaTramite.codigo == 'R002' ? '[CC]' : ''}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("${t.tramite.fechaCreacion.format('dd-MM-yyyy HH:mm')}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        if (t.tramite.deDepartamento) {
//                            par = new Paragraph("${t.tramite.deDepartamento.codigo}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                        } else {
//                            par = new Paragraph("${t.tramite.de.departamento.codigo}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                        }
//                        par = new Paragraph("${t.tramite.de.login}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("${t.fechaEnvio.format('dd-MM-yyyy hh:mm')}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("${(t.fechaRecepcion) ? t.fechaRecepcion?.format('dd-MM-yyyy hh:mm') : ''}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("${(t.fechaLimiteRespuesta) ? t.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm') : ''}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("${(t.fechaLimiteRespuesta) ? (now - t.fechaLimiteRespuesta) : ''}", times8normal)
//                        cell = new PdfPCell(par);
//                        cell.setHorizontalAlignment(1)
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("${(t.fechaLimiteRespuesta) ? 'Retrasado' : 'No recibido'}", times8normal)
//                        cell = new PdfPCell(par);
//                        cell.setHorizontalAlignment(1)
//                        tablaTramites.addCell(cell);
//
//                    }
//                    if (lvl["tramites"].size() > 0) {
//                        par2.add(tablaTramites)
//                        contenido.add(par2)
//                    }
//                }
//                lvl["personas"].each { p ->
//                    par3 = null
//                    par3 = new Paragraph("", times8normal)
//                    par3.setSpacingBefore(0.001)
//                    par = new Paragraph("Usuario: ${p["objeto"].departamento.codigo}:" + p["objeto"] + " - ${p['objeto'].login} - [  Retrasados: ${p['rezagados']}, Sin Recepción: " + p["retrasados"] + " ]", times8bold)
//                    par.setSpacingBefore(17)
//                    totalNode += p["rezagados"]
//                    totalNodeSr += p["retrasados"]
//                    contenido.add(par)
//                    if (params.detalle) {
//                        tablaTramites = null
//                        tablaTramites = new PdfPTable(9);
//                        tablaTramites.setWidthPercentage(100);
//                        par = new Paragraph("Nro.", times8bold)
//                        PdfPCell cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("F. Creación", times8bold)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("De", times8bold)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("Creado por", times8bold)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("F. Envío", times8bold)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("F. Recepcíon", times8bold)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("F. Límite", times8bold)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("Retraso (días)", times8bold)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("Tipo", times8bold)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        p["tramites"].each { t ->
//                            par = new Paragraph("${t.tramite.codigo} ${t.rolPersonaTramite.codigo == 'R002' ? '[CC]' : ''}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                            par = new Paragraph("${t.tramite.fechaCreacion.format('dd-MM-yyyy HH:mm')}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                            if (t.tramite.deDepartamento) {
//                                par = new Paragraph("${t.tramite.deDepartamento.codigo}", times8normal)
//                                cell = new PdfPCell(par);
//                                tablaTramites.addCell(cell);
//                            } else {
//                                par = new Paragraph("${t.tramite.de.departamento.codigo}", times8normal)
//                                cell = new PdfPCell(par);
//                                tablaTramites.addCell(cell);
//                            }
//                            par = new Paragraph("${t.tramite.de.login}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                            par = new Paragraph("${t.fechaEnvio.format('dd-MM-yyyy hh:mm')}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                            par = new Paragraph("${(t.fechaRecepcion) ? t.fechaRecepcion?.format('dd-MM-yyyy hh:mm') : ''}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                            par = new Paragraph("${(t.fechaLimiteRespuesta) ? t.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm') : ''}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                            par = new Paragraph("${(t.fechaLimiteRespuesta) ? (now - t.fechaLimiteRespuesta) : ''}", times8normal)
//                            cell = new PdfPCell(par);
//                            cell.setHorizontalAlignment(1)
//                            tablaTramites.addCell(cell);
//                            par = new Paragraph("${(t.fechaLimiteRespuesta) ? 'Retrasado' : 'No recibido'}", times8normal)
//                            cell = new PdfPCell(par);
//                            cell.setHorizontalAlignment(1)
//                            tablaTramites.addCell(cell);
//                        }
//                        if (p["tramites"].size() > 0) {
//                            par3.add(tablaTramites)
//                            contenido.add(par3)
//                        }
//                    }
//                }
//                total += totalNode
//                totalSr += totalNodeSr
//            }
//
//
//
//            if (lvl["hijos"].size() > 0) {
//                def res = imprimeHijosPdf(lvl, contenido, tablaTramites, params, usuario, deps, puedeVer)
//                total += res[0]
//                totalSr += res[1]
//            }
//        }
//        return [total, totalSr]
//    }
//
//    def reporteRetrasadosConsolidado_old() {
//        maxLvl = null
//        datosGrafico = [:]
//        def estadoR = EstadoTramite.findByCodigo("E004")
//        def estadoE = EstadoTramite.findByCodigo("E003")
//        def rolPara = RolPersonaTramite.findByCodigo("R001")
//        def rolCopia = RolPersonaTramite.findByCodigo("R002")
//        def now = new Date()
//        def datos = [:]
//        def usuario = null
//        def deps = []
//        def puedeVer = []
//        def extraPersona = "and "
//        if (params.prsn) {
//            usuario = Persona.get(params.prsn)
//            extraPersona += "persona=" + usuario.id + " "
//            if (usuario.esTriangulo) {
//                extraPersona = "and (persona=${usuario.id} or departamento = ${usuario.departamento.id})"
//            }
//            def padre = usuario.departamento.padre
//            while (padre) {
//                deps.add(padre)
//                padre = padre.padre
//            }
//            deps.add(usuario.departamento)
//            puedeVer.add(usuario.departamento)
//            def hi = Departamento.findAllByPadre(usuario.departamento)
//            while (hi.size() > 0) {
//                puedeVer += hi
//                hi = Departamento.findAllByPadreInList(hi)
//            }
//
//        }
//        def depStr = ""
//        if (params.dpto) {
//            def departamento = Departamento.get(params.dpto)
//            def padre = departamento.padre
//            while (padre) {
//                deps.add(padre)
//                padre = padre.padre
//            }
//            deps.add(departamento)
//            puedeVer.add(departamento)
//            def hi = Departamento.findAllByPadre(departamento)
//            while (hi.size() > 0) {
//                puedeVer += hi
//                hi = Departamento.findAllByPadreInList(hi)
//            }
//        }
//        def pdt = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite where" +
//                " fechaEnvio is not null " +
//                "and rolPersonaTramite in (${rolPara.id},${rolCopia.id}) " +
//                "and estado in (${estadoR.id},${estadoE.id}) ${usuario ? extraPersona : ''} ")
//
//        if (pdt) {
//            pdt.each { pd ->
//                pd.refresh()
//                if (pd.tramite.externo != "1" || pd.tramite == null) {
//                    def resp = Tramite.findAllByAQuienContesta(pd)
//                    if (resp.size() == 0) {
//                        if (pd.fechaLimite < now || (!pd.fechaRecepcion)) {
//                            datos = reportesPdfService.jerarquia(datos, pd)
//                        }
//                    }
//                }
//            }
//        }
//
//        def baos = new ByteArrayOutputStream()
//        def name = "reporteTramitesRetrasados_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";
//
//        def prmsHeaderHoja = [border: Color.WHITE]
//        def prmsHeaderHoja1 = [border: Color.WHITE, bordeTop: "1", bordeBot: "1"]
//        times8boldWhite.setColor(Color.WHITE)
//        times10boldWhite.setColor(Color.WHITE)
//        Document document = reportesPdfService.crearDocumento("vert", [top: 2.5, right: 2.5, bottom: 1.5, left: 3])
//
//        def pdfw = PdfWriter.getInstance(document, baos)
//
//        session.tituloReporte = "Reporte resumido de Trámites Retrasados y sin recepción"
//
//        reportesPdfService.membrete(document)
//        document.open()
//        reportesPdfService.propiedadesDocumento(document, "reporteTramitesRetrasados")
//        def contenido = new Paragraph();
//        def total = 0
//        def totalSr = 0
//        def hijos = datos["hijos"]
//        if (datos["objeto"]) {
//            if ((puedeVer.id.contains(datos["objeto"].id))) {
//                maxLvl = datos
//            }
//        }
//
//        PdfPTable tablaTramites
//        tablaTramites = new PdfPTable(4);
//        tablaTramites.setWidths(14, 62, 12, 12)
//        tablaTramites.setWidthPercentage(100);
//        def parH = new Paragraph("", times8bold)
//        def cell = new PdfPCell(parH);
//        cell.setBorderColor(Color.WHITE)
//        tablaTramites.addCell(cell);
//        cell = new PdfPCell(parH);
//        cell.setBorderColor(Color.WHITE)
//        tablaTramites.addCell(cell);
//        parH = new Paragraph("Retrasados", times8bold)
//        cell = new PdfPCell(parH);
//        cell.setBorderColor(Color.WHITE)
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
//        tablaTramites.addCell(cell);
//        parH = new Paragraph("Sin recepción", times8bold)
//        cell = new PdfPCell(parH);
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
//        cell.setBorderColor(Color.WHITE)
//        tablaTramites.addCell(cell);
//
//        hijos.each { lvl ->
//            if (puedeVer.size() == 0 || (puedeVer.id.contains(lvl["objeto"].id))) {
//                if (lvl["tramites"].size() > 0 || lvl["personas"].size() > 0) {
//                    if (maxLvl == null) {
//                        maxLvl = lvl
//                    }
//                    datosGrafico.put(lvl["objeto"].toString(), [:])
//                    def dg = datosGrafico[lvl["objeto"].toString()]
//                    dg.put("rezagados", [:])
//                    dg.put("retrasados", [:])
//                    dg.put("totalRz", 0)
//                    dg.put("totalRs", 0)
//                    dg.put("objeto", lvl["objeto"])
//                    def par = new Paragraph("Dirección", times8bold)
//                    cell = new PdfPCell(par);
//                    cell.setBorderColor(Color.WHITE)
//                    tablaTramites.addCell(cell);
//                    def totalNode = 0
//                    def totalNodeSr = 0
//                    par = new Paragraph("" + lvl["objeto"], times8normal)
//                    def cellNombre = new PdfPCell(par);
//                    cellNombre.setBorderColor(Color.WHITE)
//
//                    def usuarios = ""
//                    def totales = ""
//                    def totalesSr = ""
//
//                    if (lvl["tramites"].size() > 0) {
//                        lvl["triangulos"].each { t ->
//                            usuarios += "${t} (Oficina)\n"
//                            totales += "${lvl["ofiRz"]} \n"
//                            totalesSr += "" + lvl["ofiRs"] + " \n"
//                            if (totalNode == 0) {
//                                totalNode += lvl["ofiRz"].toInteger()
//                                dg["rezagados"].put("Oficina", lvl["ofiRz"].toInteger())
//                            }
//                            if (totalNodeSr == 0) {
//                                totalNodeSr += lvl["ofiRs"].toInteger()
//                                dg["retrasados"].put("Oficina", lvl["ofiRs"].toInteger())
//                            }
//                        }
//                    }
//                    lvl["personas"].each { p ->
//                        usuarios += "${p['objeto']} \n"
//                        totales += "${p["rezagados"]} \n"
//                        totalesSr += "" + p["retrasados"] + " \n"
//                        dg["rezagados"].put(p['objeto'], p["rezagados"].toInteger())
//                        dg["retrasados"].put(p['objeto'], p["retrasados"].toInteger())
//                        totalNode += p["rezagados"].toInteger()
//                        totalNodeSr += p["retrasados"].toInteger()
//                    }
//
//                    tablaTramites.addCell(cellNombre);
//                    par = new Paragraph("" + lvl["rezagados"], times8bold)
//                    def cellTotal = new PdfPCell(par);
//                    cellTotal.setBorderColor(Color.WHITE)
//                    cellTotal.setHorizontalAlignment(Element.ALIGN_RIGHT)
//                    tablaTramites.addCell(cellTotal);
//                    par = new Paragraph("" + lvl["retrasados"], times8bold)
//                    def cellTotalSr = new PdfPCell(par);
//                    cellTotalSr.setBorderColor(Color.WHITE)
//                    cellTotalSr.setHorizontalAlignment(Element.ALIGN_RIGHT)
//                    tablaTramites.addCell(cellTotalSr);
//                    dg["totalRz"] = lvl["rezagados"]
//                    dg["totalRs"] = lvl["retrasados"]
//                    par = new Paragraph("Usuario:", times8bold)
//                    cell = new PdfPCell(par);
//                    cell.setBorderColor(Color.WHITE)
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph(usuarios, times8normal)
//                    cell = new PdfPCell(par);
//                    cell.setBorderColor(Color.WHITE)
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph(totales, times8normal)
//                    cell = new PdfPCell(par);
//                    cell.setBorderColor(Color.WHITE)
//                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph(totalesSr, times8normal)
//                    cell = new PdfPCell(par);
//                    cell.setBorderColor(Color.WHITE)
//                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
//                    tablaTramites.addCell(cell);
//                    total += totalNode
//                    totalSr += totalNodeSr
//                }
//            }
//            def res = imprimeHijosPdfConsolidado(lvl, document, tablaTramites, params, usuario, deps, puedeVer, total, totalSr, datosGrafico)
//            total += res[0]
//            totalSr += res[1]
//
//        }
//        if (maxLvl) {
//            def par = new Paragraph("", times8bold)
//            cell = new PdfPCell(par);
//            cell.setBorderColor(Color.WHITE)
//            tablaTramites.addCell(cell);
//            par = new Paragraph("Gran Total", times8bold)
//            cell = new PdfPCell(par);
//            cell.setBorderColor(Color.WHITE)
//            tablaTramites.addCell(cell);
//            par = new Paragraph("" + maxLvl["rezagados"], times8bold)
//            cell = new PdfPCell(par);
//            cell.setBorderColor(Color.WHITE)
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
//            tablaTramites.addCell(cell);
//            par = new Paragraph("" + maxLvl["retrasados"], times8bold)
//            cell = new PdfPCell(par);
//            cell.setBorderColor(Color.WHITE)
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
//            tablaTramites.addCell(cell);
//        }
//        contenido.add(tablaTramites)
//        boolean conGraficos = false
//
//        try {
//            conGraficos = true
//            def width = 550
//            def height = 250
//            PdfContentByte contentByte = pdfw.getDirectContent();
//            PdfTemplate templateSinRecepcion = contentByte.createTemplate(width, height);
//            Graphics2D graphics2dSinRecepcion = templateSinRecepcion.createGraphics(width, height, new DefaultFontMapper());
//            PdfTemplate templateRetrasados = contentByte.createTemplate(width, height);
//            Graphics2D graphics2dRetrasados = templateRetrasados.createGraphics(width, height, new DefaultFontMapper());
//            Rectangle2D rectangle2dSinRecepcion = new Rectangle2D.Double(0, 0, width, height);
//            Rectangle2D rectangle2dRetrasados = new Rectangle2D.Double(0, 0, width, height);
//
//////        PARA GRAFICO PASTEL
//            DefaultPieDataset dataSetRs = new DefaultPieDataset();
//            DefaultPieDataset dataSetRz = new DefaultPieDataset();
//            def ttl = " por departamento"
//            def existeSinRecepcion = false
//            def existeRetrasados = false
////            println "DATOS GRAFICO"
////            println datosGrafico
//            datosGrafico.each { dep, valores ->
//                if (datosGrafico.size() > 1) {
//                    if (valores.totalRs > 0) {
//                        existeSinRecepcion = true
//                        dataSetRs.setValue(valores.objeto.codigo, valores.totalRs);
//                    }
//                    if (valores.totalRz > 0) {
//                        existeRetrasados = true
//                        dataSetRz.setValue(valores.objeto.codigo, valores.totalRz);
//                    }
//                } else {
//                    ttl = " de " + valores.objeto.descripcion
//                    valores.rezagados.each { k, v ->
//                        if (v > 0) {
//                            if (k instanceof java.lang.String) {
//                                existeRetrasados = true
//                                dataSetRz.setValue(k, v);
//                            } else {
//                                existeRetrasados = true
//                                dataSetRz.setValue(k.login, v);
//                            }
//                        }
//                    }
//                    valores.retrasados.each { k, v ->
//                        if (v > 0) {
//                            if (k instanceof java.lang.String) {
//                                existeSinRecepcion = true
//                                dataSetRs.setValue(k, v);
//                            } else {
//                                existeSinRecepcion = true
//                                dataSetRs.setValue(k.login, v);
//                            }
//                        }
//                    }
//                }
//            }
//
//            JFreeChart chartSinRecepcion = ChartFactory.createPieChart("Documentos sin recepción" + ttl, dataSetRs, true, true, false);
//            chartSinRecepcion.setTitle(
//                    new org.jfree.chart.title.TextTitle("Documentos sin recepción" + ttl,
//                            new java.awt.Font("SansSerif", java.awt.Font.BOLD, 15)
//                    )
//            );
//            JFreeChart chartRetrasados = ChartFactory.createPieChart("Documentos retrasados" + ttl, dataSetRz, true, true, false);
//            chartRetrasados.setTitle(
//                    new org.jfree.chart.title.TextTitle("Documentos retrasados" + ttl,
//                            new java.awt.Font("SansSerif", java.awt.Font.BOLD, 15)
//                    )
//            );
//
//            /* getPlot method of JFreeChart class returns the PiePlot object back to us */
//            PiePlot ColorConfigurator = (PiePlot) chartSinRecepcion.getPlot(); /* get PiePlot object for changing */
//            PiePlot ColorConfigurator2 = (PiePlot) chartRetrasados.getPlot(); /* get PiePlot object for changing */
//            /* A format mask specified to display labels. Here {0} is the section name, and {1} is the value.
//            We can also use {2} which will display a percent value */
//            ColorConfigurator.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{1} docs. ({2})"));
//            ColorConfigurator2.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{1} docs. ({2})"));
//            /* Set color of the label background on the pie chart */
//            ColorConfigurator.setLabelBackgroundPaint(new Color(220, 220, 220));
//            ColorConfigurator2.setLabelBackgroundPaint(new Color(220, 220, 220));
//
//            chartSinRecepcion.draw(graphics2dSinRecepcion, rectangle2dSinRecepcion);
//            chartRetrasados.draw(graphics2dRetrasados, rectangle2dRetrasados);
//
//            graphics2dSinRecepcion.dispose();
//            graphics2dRetrasados.dispose();
//
//            def posyGraf1 = 450
//            def posyGraf2 = 180
//            if (existeSinRecepcion) {
//                contentByte.addTemplate(templateSinRecepcion, 30, posyGraf1);
//                if (existeRetrasados) {
//                    contentByte.addTemplate(templateRetrasados, 30, posyGraf2);
//                }
//            } else {
//                if (existeRetrasados) {
//                    contentByte.addTemplate(templateRetrasados, 30, posyGraf1);
//                }
//            }
//        } catch (Exception e) {
//            println "ERROR GRAFICOS::::::: "
//            e.printStackTrace();
//            conGraficos = false
//        }
//
//        if (conGraficos) {
//            document.newPage()
//        }
//        document.add(contenido)
//
//        document.close();
//        pdfw.close()
//        byte[] b = baos.toByteArray();
//        response.setContentType("application/pdf")
//        response.setHeader("Content-disposition", "attachment; filename=" + name)
//        response.setContentLength(b.length)
//        response.getOutputStream().write(b)
//    }
//
//    def imprimeHijosPdfConsolidado(arr, contenido, tablaTramites, params, usuario, deps, puedeVer, total, totalSr, datosGrafico) {
//        total = 0
//        totalSr = 0
//        def datos = arr["hijos"]
//        datos.each { lvl ->
//            if (puedeVer.size() == 0 || (puedeVer.id.contains(lvl["objeto"].id))) {
//                if (maxLvl == null) {
//                    maxLvl = lvl
//                }
//                datosGrafico.put(lvl["objeto"].toString(), [:])
//                def dg = datosGrafico[lvl["objeto"].toString()]
//                dg.put("rezagados", [:])
//                dg.put("retrasados", [:])
//                dg.put("totalRz", 0)
//                dg.put("totalRs", 0)
//                dg.put("objeto", lvl["objeto"])
//                def par = new Paragraph("- Departamento:", times8bold)
//                PdfPCell cell = new PdfPCell(par);
//                cell.setBorderColor(Color.WHITE)
//                tablaTramites.addCell(cell);
//                def totalNode = 0
//                def totalNodeSr = 0
//                par = new Paragraph("" + lvl["objeto"], times8normal)
//                def cellNombre = new PdfPCell(par);
//                cellNombre.setBorderColor(Color.WHITE)
//
//                def usuarios = ""
//                def totales = ""
//                def totalesSr = ""
//
//                if (lvl["tramites"].size() > 0) {
//                    lvl["triangulos"].each { t ->
//                        usuarios += "${t} (Oficina)\n"
//                        totales += "${lvl["ofiRz"]} \n"
//                        totalesSr += "" + lvl["ofiRs"] + " \n"
//                        if (totalNode == 0) {
//                            totalNode += lvl["ofiRz"].toInteger()
//                            dg["rezagados"].put("Oficina", lvl["ofiRz"].toInteger())
//                        }
//                        if (totalNodeSr == 0) {
//                            totalNodeSr += lvl["ofiRs"].toInteger()
//                            dg["retrasados"].put("Oficina", lvl["ofiRs"].toInteger())
//                        }
//                    }
//                }
//                lvl["personas"].each { p ->
//                    usuarios += "${p['objeto']} \n"
//                    totales += "${p["rezagados"]} \n"
//                    totalesSr += "" + p["retrasados"] + " \n"
//                    dg["rezagados"].put(p['objeto'], p["rezagados"].toInteger())
//                    dg["retrasados"].put(p['objeto'], p["retrasados"].toInteger())
//                    totalNode += p["rezagados"].toInteger()
//                    totalNodeSr += p["retrasados"].toInteger()
//                }
//
//                tablaTramites.addCell(cellNombre);
//                par = new Paragraph("" + lvl["rezagados"], times8bold)
//                def cellTotal = new PdfPCell(par);
//                cellTotal.setBorderColor(Color.WHITE)
//                cellTotal.setHorizontalAlignment(Element.ALIGN_RIGHT)
//                tablaTramites.addCell(cellTotal);
//                par = new Paragraph("" + lvl["retrasados"], times8bold)
//                def cellTotalSr = new PdfPCell(par);
//                cellTotalSr.setBorderColor(Color.WHITE)
//                cellTotalSr.setHorizontalAlignment(Element.ALIGN_RIGHT)
//                dg["totalRz"] = lvl["rezagados"]
//                dg["totalRs"] = lvl["retrasados"]
//                tablaTramites.addCell(cellTotalSr);
//                par = new Paragraph("--Usuario:", times8bold)
//                cell = new PdfPCell(par);
//                cell.setBorderColor(Color.WHITE)
//                tablaTramites.addCell(cell);
//                par = new Paragraph(usuarios, times8normal)
//                cell = new PdfPCell(par);
//                cell.setBorderColor(Color.WHITE)
//                tablaTramites.addCell(cell);
//                par = new Paragraph(totales, times8normal)
//                cell = new PdfPCell(par);
//                cell.setBorderColor(Color.WHITE)
//                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
//                tablaTramites.addCell(cell);
//                par = new Paragraph(totalesSr, times8normal)
//                cell = new PdfPCell(par);
//                cell.setBorderColor(Color.WHITE)
//                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
//                tablaTramites.addCell(cell);
//                total += totalNode
//                totalSr += totalNodeSr
//            }
//
//            if (lvl["hijos"].size() > 0) {
//                def res = imprimeHijosPdfConsolidado(lvl, contenido, tablaTramites, params, usuario, deps, puedeVer, total, totalSr, datosGrafico)
//                total += res[0]
//                totalSr += res[1]
//            }
//        }
//        return [total, totalSr]
//    }
//
//    def imprimeHijos(arr) {
//        def datos = arr["hijos"]
//        datos.each { lvl ->
//            println "\t\t\t " + lvl["objeto"]
//            println "\t\t\t\t Tramites:"
//            lvl["tramites"].each { t ->
//                println "\t\t\t\t\t " + t
//            }
//            println "\t\t\t\t Personas:"
//            lvl["personas"].each { p ->
//                println "\t\t\t\t " + p["objeto"]
//                p["tramites"].each { t ->
//                    println "\t\t\t " + t
//                }
//            }
//            if (lvl["hijos"].size() > 0) {
//                imprimeHijos(lvl)
//            }
//        }
//    }
//
//    def arreglaTramites() {
//        Tramite.list().each {
//            def hijos = Tramite.findAllByPadre(it)
//            if (hijos.size() > 0) {
//                it.estado = "C"
//                it.save()
//            }
//        }
//    }


    def reporteRetrasadosArbol() {

//        def desde = new Date().parse("dd-MM-yyyy HH:mm", params.desde + " 00:00")
//        def hasta = new Date().parse("dd-MM-yyyy HH:mm", params.hasta + " 23:59")


        def desdeNuevo = new Date().format("yyyy/MM/dd")
        def hastaNuevo = new Date().format("yyyy/MM/dd")

        def fileName = "documentos_retrasados_"
        def title = "Documentos retrasados de "
        def title2 = "Documentos retrasados por "

        def pers = Persona.get(params.id.toLong())
        if (params.tipo == "prsn") {
            def dpto = Departamento.get(params.dpto)
            if (!dpto) {
                dpto = pers.departamento
            }
            fileName += pers.login + "_" + dpto.codigo
            title += "${pers.nombre} ${pers.apellido}\nen el departamento ${dpto.descripcion}\nentre el ${params.desde} y el ${params.hasta}"
        } else {
            def dep = Departamento.get(params.id.toLong())
            fileName += dep.codigo
            title += "${dep.descripcion}"
        }

        def baos = new ByteArrayOutputStream()
        def name = fileName + "_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Document document = reportesPdfService.crearDocumento([top: 2, right: 2, bottom: 1.5, left: 2.5])
        def pdfw = PdfWriter.getInstance(document, baos);

        session.tituloReporte = title
        reportesPdfService.membrete(document)
        document.open();
        reportesPdfService.propiedadesDocumento(document, "trámite")
        def paramsCenter = [align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def paramsLeft = [align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def prmsHeaderHojaRight = [align: Element.ALIGN_RIGHT]
        def prmsHeaderHoja = [align: Element.ALIGN_CENTER]
        def totalResumenGenerado = 0
        def totalRecibido = 0
        def totalRetrasado = 0
        def usuario = Persona.get(session.usuario.id)
        def departamentoUsuario = usuario?.departamento?.id
        def sqlGen
        def sql
        def cn2 = dbConnectionService.getConnection()
        def cn = dbConnectionService.getConnection()
//        desde = desde.format("yyyy/MM/dd")
//        hasta = hasta.format("yyyy/MM/dd")

        def dptoPadre = Departamento.get(params.id)
        def dptosHijos = Departamento.findAllByPadreAndActivo(dptoPadre, 1).id

        def tablaTotalesRecibidos
        def tablaTitulo
        def totalRetDpto = 0
        def totalRecDpto = 0

        tablaTotalesRecibidos = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([40,30,15,15]),0,0)


        if(dptosHijos.size() > 0 && params.id != '11'){

            //PADRE

            tablaTotalesRecibidos = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([40,30,15,15]),0,10)
            tablaTitulo = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([100]),0,0)

            reportesPdfService.addCellTabla(tablaTitulo, new Paragraph(Departamento.get(params.id).descripcion, fontBold), prmsHeaderHoja)

            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("Usuario", fontBold), prmsHeaderHoja)
            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("Perfil", fontBold), prmsHeaderHoja)
            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("Retrasados", fontBold), prmsHeaderHoja)
            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("No Recibidos", fontBold), prmsHeaderHoja)

            sqlGen = "select * from retrasados("+ params.id +"," + "'"  + desdeNuevo + "'" + "," +  "'" + hastaNuevo + "'" + ") order by retrasados desc"
            cn2.eachRow(sqlGen.toString()){

                reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(it?.usuario, font), paramsLeft)
                reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(it?.perfil, font), paramsLeft)
                reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(" " + it?.retrasados, font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(" " + it?.no_recibidos, font), prmsHeaderHoja)


                if(it?.perfil == 'RECEPCIÓN DE OFICINA'){
                    totalRetDpto = it?.retrasados
                    totalRecDpto = it?.no_recibidos
                }else{
                    totalRetrasado += it?.retrasados
                    totalRecibido += it?.no_recibidos
                }



                totalResumenGenerado += 1
            }

            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(" ", font), prmsHeaderHoja)
            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("Total", fontBold), prmsHeaderHoja)
            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(" " + (totalRetrasado + totalRetDpto), fontBold), prmsHeaderHoja)
            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(" " + (totalRecibido + totalRecDpto), fontBold), prmsHeaderHoja)

            document.add(tablaTitulo)
            document.add(tablaTotalesRecibidos)

            //HIJOS
            dptosHijos.each { hij->

                totalResumenGenerado = 0
                totalRecibido = 0
                totalRetrasado = 0
                totalRetDpto = 0
                totalRecDpto = 0

                tablaTotalesRecibidos = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([40,30,15,15]),0,10)
                tablaTitulo = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([100]),0,0)

                reportesPdfService.addCellTabla(tablaTitulo, new Paragraph(Departamento.get(hij).descripcion, fontBold), prmsHeaderHoja)

                reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("Usuario", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("Perfil", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("Retrasados", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("No Recibidos", fontBold), prmsHeaderHoja)

                sqlGen = "select * from retrasados("+ hij +"," + "'"  + desdeNuevo + "'" + "," +  "'" + hastaNuevo + "'" + ") order by retrasados desc"
                cn2.eachRow(sqlGen.toString()){

                    reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(it?.usuario, font), paramsLeft)
                    reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(it?.perfil, font), paramsLeft)
                    reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(" " + it?.retrasados, font), prmsHeaderHoja)
                    reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(" " + it?.no_recibidos, font), prmsHeaderHoja)

//                    totalRecibido += it?.no_recibidos
//                    totalRetrasado += it?.retrasados


                    if(it?.perfil == 'RECEPCIÓN DE OFICINA'){
                        totalRetDpto = it?.retrasados
                        totalRecDpto = it?.no_recibidos
                    }else{
                        totalRetrasado += it?.retrasados
                        totalRecibido += it?.no_recibidos
                    }


                    totalResumenGenerado += 1
                }

                reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(" ", font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("Total", fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(" " + (totalRetrasado + totalRetDpto), fontBold), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(" " + (totalRecibido + totalRecDpto), fontBold), prmsHeaderHoja)

                document.add(tablaTitulo)
                document.add(tablaTotalesRecibidos)
            }

        }else{

            tablaTotalesRecibidos = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([40,30,15,15]),0,0)

            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("Usuario", fontBold), prmsHeaderHoja)
            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("Perfil", fontBold), prmsHeaderHoja)
            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("Retrasados", fontBold), prmsHeaderHoja)
            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("No Recibidos", fontBold), prmsHeaderHoja)

            sqlGen = "select * from retrasados("+ params.id +"," + "'"  + desdeNuevo + "'" + "," +  "'" + hastaNuevo + "'" + ") order by retrasados desc"
            cn2.eachRow(sqlGen.toString()){

                reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(it?.usuario, font), paramsLeft)
                reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(it?.perfil, font), paramsLeft)
                reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(" " + it?.retrasados, font), prmsHeaderHoja)
                reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(" " + it?.no_recibidos, font), prmsHeaderHoja)
//
//                totalRecibido += it?.no_recibidos
//                totalRetrasado += it?.retrasados

                if(it?.perfil == 'RECEPCIÓN DE OFICINA'){
                    totalRetDpto = it?.retrasados
                    totalRecDpto = it?.no_recibidos
                }else{
                    totalRetrasado += it?.retrasados
                    totalRecibido += it?.no_recibidos
                }


                totalResumenGenerado += 1
            }

            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(" ", font), prmsHeaderHoja)
            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("Total", fontBold), prmsHeaderHoja)
            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(" " + (totalRetrasado + totalRetDpto), fontBold), prmsHeaderHoja)
            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(" " + (totalRecibido + totalRecDpto), fontBold), prmsHeaderHoja)

            document.add(tablaTotalesRecibidos)

        }

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }


    def reporteGeneradosArbol () {

        def desde = new Date().parse("dd-MM-yyyy HH:mm", params.desde + " 00:00")
        def hasta = new Date().parse("dd-MM-yyyy HH:mm", params.hasta + " 23:59")

        def fileName = "documentos_generados_"
        def title = "Documentos generados de "
        def title2 = "Documentos generados por "
        def pers = Persona.get(params.id.toLong())
        if (params.tipo == "prsn") {

            def dpto = Departamento.get(params.dpto)
            if (!dpto) {
                dpto = pers.departamento
            }
            fileName += pers.login + "_" + dpto.codigo
            title += "${pers.nombre} ${pers.apellido}\nen el departamento ${dpto.descripcion}\nentre el ${params.desde} y el ${params.hasta}"
            title2 += "el usuario ${pers.nombre} ${pers.apellido} (${pers.login}) en el departamento ${dpto.descripcion} entre el ${params.desde} y el ${params.hasta}"
        } else {
            def dep = Departamento.get(params.id.toLong())
            fileName += dep.codigo
            title += "${dep.descripcion}\nde ${params.desde} a ${params.hasta}"
            title2 += "los usuarios del departamento ${dep.descripcion} (${dep.codigo}) entre ${params.desde} y ${params.hasta}"
        }

        def baos = new ByteArrayOutputStream()
        def name = fileName + "_" + new Date().format("ddMMyyyy_hhmm") + ".pdf";

        Document document = reportesPdfService.crearDocumento([top: 2, right: 2, bottom: 1.5, left: 2.5])
        def pdfw = PdfWriter.getInstance(document, baos);

        session.tituloReporte = title
        reportesPdfService.membrete(document)
        document.open();
        reportesPdfService.propiedadesDocumento(document, "trámite")
        def paramsCenter = [align: Element.ALIGN_CENTER, valign: Element.ALIGN_MIDDLE]
        def paramsLeft = [align: Element.ALIGN_LEFT, valign: Element.ALIGN_MIDDLE]
        def prmsHeaderHojaRight = [align: Element.ALIGN_RIGHT]
        def prmsHeaderHoja = [align: Element.ALIGN_CENTER]
        def totalResumenGenerado = 0
        def totalRecibido = 0
        def usuario = Persona.get(session.usuario.id)
        def departamentoUsuario = usuario?.departamento?.id
        def sqlGen
        def sql
        def cn2 = dbConnectionService.getConnection()
        def cn = dbConnectionService.getConnection()
        desde = desde.format("yyyy/MM/dd")
        hasta = hasta.format("yyyy/MM/dd")
        def tablaTotalesRecibidos = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([40,30,11,11,11]),0,0)

        reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("Usuario", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("Perfil", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("Generados", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("Enviados", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph("Recibidos", fontBold), prmsHeaderHoja)

        sqlGen = "select * from retrasados("+ params.id +"," + "'"  + desde + "'" + "," +  "'" + hasta + "'" + ") order by generados desc"
        println "reporteGeneradosArbol: $sqlGen"
        cn2.eachRow(sqlGen.toString()){

            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(it?.usuario, font), paramsLeft)
            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(it?.perfil, font), paramsLeft)
            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(" " + it?.generados, font), prmsHeaderHoja)
            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(" " + it?.enviados, font), prmsHeaderHoja)
            reportesPdfService.addCellTabla(tablaTotalesRecibidos, new Paragraph(" " + it?.recibidos, font), prmsHeaderHoja)

            totalResumenGenerado += 1
        }

        document.add(tablaTotalesRecibidos)

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
    }

}
