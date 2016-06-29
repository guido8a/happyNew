package happy.reportes

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfWriter
import happy.seguridad.Persona;
import happy.seguridad.Shield
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite;

class ReportesPersonalesController extends Shield {


    def reportesPdfService
    def dbConnectionService

    Font font = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL);
    Font fontBold = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
    def prmsHeaderHoja = [align: Element.ALIGN_CENTER]
    def prmsHeaderHojaLeft = [align: Element.ALIGN_RIGHT]
    def prmsTablaHojaCenter = [align: Element.ALIGN_CENTER]
    def prmsTablaHoja = []


    def personal() {
        def usu = Persona.get(session.usuario.id)
        return [persona: usu]
    }


    def reporteAIP () {

//        def desde = new Date().parse("dd-MM-yyyy", params.desde)
//        def hasta = new Date().parse("dd-MM-yyyy", params.hasta)
//
//        desde = desde.format("yyyy/MM/dd")
//        hasta = hasta.format("yyyy/MM/dd")
//
//        def desdeF = new Date().parse("dd-MM-yyyy", params.desde)
//        def hastaF = new Date().parse("dd-MM-yyyy", params.hasta)
//        desdeF = desdeF.format("dd-MM-yyyy")
//        hastaF = hastaF.format("dd-MM-yyyy")


        def baos = new ByteArrayOutputStream()
        def tablaCabeceraRetrasados = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([100]), 10,0)
        def tablaCabeceraRetrasadosUs = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([100]), 10,0)
        def tablaTramite = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([12,15,12,26,12,14,19,10]), 15, 0)
        def tablaTramiteUs = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([6, 15, 6, 4]), 15, 0)
        def tablaTramiteNoRecibidos = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([10, 5, 20, 10, 13]), 15, 0)
        def tablaCabecera = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([100]), 10,0)
        def tablaTotalesRetrasados = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([100]),0,0)
        def tablaTotalesRetrasadosUs = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([100]),0,0)
        def tablaTotalesNoRecibidos = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([100]),0,0)
        def name = "tramitesAIP_" + new Date().format("ddMMyyyy_HHmm") + ".pdf";
        def results = []
        def fechaRecepcion = new Date().format("yyyy/MM/dd HH:mm:ss")
        def ahora = new Date()
        def cn = dbConnectionService.getConnection()
        def cn2 = dbConnectionService.getConnection()


        Document document = reportesPdfService.crearDocumento("l", [top: 2, right: 2, bottom: 1.5, left: 2.5])

        def pdfw = PdfWriter.getInstance(document, baos);
        session.tituloReporte = "Reporte de trámites AIP"

        def entre
        def hasta
        def dias

        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Código", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("De", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Doc. Externo", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Asunto", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Fecha Recepción", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Fecha Vencimiento", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Ingresado a", fontBold), prmsHeaderHoja)
        reportesPdfService.addCellTabla(tablaTramite, new Paragraph("Estado", fontBold), prmsHeaderHoja)

        def tramitesAIP = Tramite.findAllByAip("S")
        def prtr
        def rol = RolPersonaTramite.findByDescripcion("PARA")

        tramitesAIP.each {
            reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.codigo, font), prmsTablaHoja)
            reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.paraExterno, font), prmsTablaHoja)
            reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.numeroDocExterno, font), prmsTablaHoja)
            reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.asunto, font), prmsTablaHoja)
            prtr = PersonaDocumentoTramite.findByTramiteAndRolPersonaTramite(it,rol)
            hasta ="select * from tmpo_hasta('${prtr.fechaRecepcion}', 10)"
            println "hasta: $hasta"
            reportesPdfService.addCellTabla(tablaTramite, new Paragraph(prtr?.fechaRecepcion?.format("dd-MM-yyyy"), font), prmsTablaHoja)
            reportesPdfService.addCellTabla(tablaTramite, new Paragraph("" + cn2.firstRow(hasta.toString()).tmpo_hasta.format("dd-MM-yyyy HH:mm"), font), prmsTablaHoja)
            reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.departamento?.descripcion, font), prmsTablaHoja)
            reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.estadoTramiteExterno?.descripcion, font), prmsTablaHoja)
        }

        reportesPdfService.membrete(document)
        document.open();
        reportesPdfService.propiedadesDocumento(document, "reporteTramitesAIP")
        document.add(tablaCabeceraRetrasados);
        document.add(tablaTramite);
        document.add(tablaTotalesRetrasados);
        document.add(tablaCabecera);
        document.add(tablaCabeceraRetrasadosUs);
        document.add(tablaTramiteUs);
        document.add(tablaTotalesRetrasadosUs);

        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)

    }
}
