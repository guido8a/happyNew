package happy.reportes

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfWriter
import grails.converters.JSON
import happy.seguridad.Persona;
import happy.seguridad.Shield
import happy.tramites.Departamento
import happy.tramites.EstadoTramite
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
        def tablaTramite = reportesPdfService.crearTabla(reportesPdfService.arregloEnteros([12,15,12,32,12,14,10,10]), 15, 0)
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
            reportesPdfService.addCellTabla(tablaTramite, new Paragraph(prtr?.fechaRecepcion?.format("dd-MM-yyyy HH:mm"), font), prmsTablaHoja)
            reportesPdfService.addCellTabla(tablaTramite, new Paragraph("" + cn2.firstRow(hasta.toString()).tmpo_hasta.format("dd-MM-yyyy HH:mm"), font), prmsTablaHoja)
            reportesPdfService.addCellTabla(tablaTramite, new Paragraph(it?.departamento?.codigo, font), prmsTablaHoja)
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

    def reporteExcelBusqueda () {

//        println("params ex " + params)

        def fileName = "documentos_busqueda_"
        def title = ["Reporte de documentos buscados"]
        def usuario = Persona.get(session.usuario.id)
        def departamentoUsuario = usuario?.departamento?.id
        def envia
        def recibe
        def rolEnvia = RolPersonaTramite.findByCodigo('E004')
        def rolRecibe = RolPersonaTramite.findByCodigo('E003')
        def estadoRecibido = EstadoTramite.findByCodigo('E004')

        def fechaDesde = ""
        if (params.fcds) {
            fechaDesde = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fcds + " 00:00:00")
        }

        def fechaHasta = ""
        if (params.fchs) {
            fechaHasta = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fchs + " 00:00:00")
        }

        def maximo = (params.registros?.toInteger())?: 20

        def downloadName = fileName + "_" + new Date().format("ddMMyyyy_hhmm") + ".xlsx";

        def path = servletContext.getRealPath("/") + "xls/"
        new File(path).mkdirs()
        //esto crea un archivo temporal que puede ser siempre el mismo para no ocupar espacio
        String filename = path + "text.xlsx";
        String sheetName = "Resumen";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName);
        CreationHelper createHelper = wb.getCreationHelper();
        sheet.setAutobreaks(true);

        XSSFRow rowTitle = sheet.createRow((short) 0);
        Cell cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue("GAD DE LA PROVINCIA DE PICHINCHA");
        rowTitle = sheet.createRow((short) 1);
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue("SISTEMA DE ADMINISTRACION DOCUMENTAL");
        rowTitle = sheet.createRow((short) 2);
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue(title[0]);
        rowTitle = sheet.createRow((short) 3);
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue( usuario?.nombre + " " + usuario?.apellido );
        rowTitle = sheet.createRow((short) 4);
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue( "Fecha de Impresión: " + new Date().format("dd-MM-yyyy") );

        def index = 6
        XSSFRow rowHead = sheet.createRow((short) index);
        rowHead.setHeightInPoints(14)

        Cell cell = rowHead.createCell((int) 0)
        cell.setCellValue("Documento")
        sheet.setColumnWidth(0, 5000)

        cell = rowHead.createCell((int) 1)
        cell.setCellValue("Creación")
        sheet.setColumnWidth(1, 2000)

        cell = rowHead.createCell((int) 2)
        cell.setCellValue("De")
        sheet.setColumnWidth(2, 10000)

        cell = rowHead.createCell((int) 3)
        cell.setCellValue("Para")
        sheet.setColumnWidth(3, 15000)

        cell = rowHead.createCell((int) 4)
        cell.setCellValue("CC")
        sheet.setColumnWidth(3, 3000)

        cell = rowHead.createCell((int) 5)
        cell.setCellValue("Asunto")
        sheet.setColumnWidth(5, 13000)

        cell = rowHead.createCell((int) 6)
        cell.setCellValue("Prioridad")
        sheet.setColumnWidth(6, 2000)

        cell = rowHead.createCell((int) 7)
        cell.setCellValue("Envia")
        sheet.setColumnWidth(7, 8000)

        cell = rowHead.createCell((int) 8)
        cell.setCellValue("Envió")
        sheet.setColumnWidth(8, 3000)

        cell = rowHead.createCell((int) 9)
        cell.setCellValue("Recepción")
        sheet.setColumnWidth(9, 3000)

       index++

        def res


        res = Tramite.withCriteria {
            if (params.fecha) {
                gt('fechaEnvio', params.fechaIni)
                lt('fechaEnvio', params.fechaFin)
            }
            if (params.asunto) {
                ilike('asunto', '%' + params.asunto.trim() + '%')
            }
            if (params.memo) {
                ilike('codigo', '%' + params.memo.trim() + '%')
            }

            if (params.fcds) {
                if(params.fechas == 'fccr') {
                    gt('fechaCreacion', fechaDesde)
                } else {
                    gt('fechaEnvio', fechaDesde)
                }
            }
            if (params.fchs) {
                if(params.fechas == 'fccr') {
                    lt('fechaCreacion', fechaHasta)
                } else {
                    lt('fechaEnvio', fechaHasta)
                }
            }
            if(params.doc){
                ilike('numeroDocExterno', '%' + params.doc.trim() + '%')
            }
            if(params.institucion){
                ilike('paraExterno', '%' + params.institucion.trim() + '%')
            }
            if(params.contacto){
                ilike('contacto', '%' + params.contacto.trim() + '%')
            }
            order('tipoDocumento')
            order('fechaCreacion', 'desc')
            maxResults(maximo + 1)
        }

//        println("res " + res)

        res.each { tramite->

            envia = PersonaDocumentoTramite.findByTramiteAndRolPersonaTramite(tramite, rolEnvia)
            recibe = PersonaDocumentoTramite.findByTramiteAndRolPersonaTramite(tramite, rolRecibe)

            XSSFRow row2 = sheet.createRow((short) index)

            if(tramite?.externo == '1' || tramite?.tipoDocumento?.codigo == 'DEX'){
                row2.createCell((int) 0).setCellValue(tramite?.codigo + " (ext)")
            }else{
                row2.createCell((int) 0).setCellValue(tramite?.codigo)
            }

            row2.createCell((int) 1).setCellValue(tramite?.fechaCreacion?.format("dd-MM-yyyy"))

            if(tramite?.tipoDocumento?.codigo == 'DEX'){
                     row2.createCell((int) 2).setCellValue(tramite.paraExterno + ' (ext)')
            }else{
                if(tramite?.deDepartamento){
                     row2.createCell((int) 2).setCellValue(tramite?.deDepartamento?.descripcion)
                }else{
                    if(tramite?.de){
                      row2.createCell((int) 2).setCellValue(tramite?.de?.nombre + " " + tramite?.de?.apellido + " " + ' (' + tramite?.departamentoSigla + ')')
                    }
                }
            }

            if(tramite?.tipoDocumento?.codigo == 'OFI'){
                row2.createCell((int) 3).setCellValue(tramite?.paraExterno + " (ext)")
            }else{
                if(tramite?.para){
                    if(tramite?.para?.persona){
                        row2.createCell((int) 3).setCellValue(tramite?.para?.persona?.nombre + " " + tramite?.para?.persona?.apellido + " (" + tramite?.para?.persona?.departamento + ")")
                    }else{
                        row2.createCell((int) 3).setCellValue(tramite?.para?.departamento?.descripcion)
                    }
                }
            }

            if(tramite?.copias && tramite?.copias?.size() > 0){
                tramite?.copias?.each {
                    if(it?.persona){
                     row2.createCell((int) 4).setCellValue(it?.persona?.nombre + " " + it?.persona?.apellido + " (" + it?.persona?.departamento?.codigo + ")")
                    }else{
                        if(it?.departamento){
                            row2.createCell((int) 4).setCellValue(it?.departamento?.codigo)
                        }
                    }
                }
            }else{
                row2.createCell((int) 4).setCellValue('')
            }

            row2.createCell((int) 5).setCellValue(tramite?.asunto)
            row2.createCell((int) 6).setCellValue(tramite?.prioridad?.descripcion)
            row2.createCell((int) 7).setCellValue(envia ? (envia?.persona?.nombre + " " + envia?.persona?.apellido) : '')
            row2.createCell((int) 8).setCellValue(tramite?.fechaEnvio?.format('dd-MM-yyyy HH:mm') ?: '')
            if(recibe && recibe?.fechaRecepcion && tramite.estadoTramite == estadoRecibido){
                row2.createCell((int) 9).setCellValue(recibe?.fechaRecepcion?.format("dd-MM-yyyy"))
            }else{
                row2.createCell((int) 9).setCellValue(" " )
            }

            index++
        }


        XSSFRow row = sheet.createRow((short) index + 2)

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

    def reportesGraficos () {

    }

    def estadoTramites () {

//        println("tr " + params)
        def departamento = Departamento.get(params.departamento)

        def cn = dbConnectionService.getConnection()

        def data = [:]
        def retrasados = ""
        def noRecibidos = ""
        def generados = ""
        def enviados = ""
        def recibidos = ""
        def noEnviados = ""

        data.titulo = "Estados de trámites por Departamento"

        def sql = "select * from retrasados(${departamento?.id}, '${params.fechaInicio}', '${params.fechaFin}') order by generados desc limit 1"

//        println("sql " + sql)

        cn.eachRow(sql.toString()) { d ->
            retrasados += retrasados == ''? d.retrasados : "," + d.retrasados
            noRecibidos += noRecibidos == ''? d.no_recibidos : "," + d.no_recibidos
            generados += generados == ''? d.generados : "," + d.generados
            enviados += enviados == ''? d.enviados : "," + d.enviados
            recibidos += recibidos == ''? d.recibidos : "," + d.recibidos
            noEnviados += noEnviados == ''? d.no_enviados : "," + d.no_enviados
        }

//        println "cantones: $sql"
        data.cabecera = departamento?.descripcion ?: ''
        data.retrasados = retrasados
        data.noRecibidos = noRecibidos
        data.generados = generados
        data.enviados = enviados
        data.recibidos = recibidos
        data.noEnviados = noEnviados
//        println "++data: $data"
//        println "++data: ${data as JSON}"

        render data as JSON
    }


    def seleccionOficina () {
        def departamentos = Departamento.findAllByActivo('1').sort{it.descripcion}

        return [departamentos: departamentos]
    }

    def tiemposRespuesta () {

        println("tr " + params)
        def departamento = Departamento.get(params.departamento)

        def cn = dbConnectionService.getConnection()

        def data = [:]
        def tiempo1 = ""
        def tiempo2 = ""
        def tiempo3 = ""

        data.titulo = "Tiempos de Respuesta por Departamento"

        def sql = "select sum(tmpo0003) dias3, sum(tmpo0410) de4a10, sum(tmpo11dd) mas11 from rp_tiempos('${params.fechaInicio}', '${params.fechaFin}', ${departamento?.id}, null);"

//        println("sql " + sql)

        cn.eachRow(sql.toString()) { d ->
            tiempo1 += tiempo1 == ''? d.dias3 : "," + d.dias3
            tiempo2 += tiempo2 == ''? d.de4a10 : "," + d.de4a10
            tiempo3 += tiempo3 == ''? d.mas11 : "," + d.mas11
        }

        data.cabecera = departamento?.descripcion ?: ''
        data.tiempo1 = tiempo1
        data.tiempo2 = tiempo2
        data.tiempo3 = tiempo3

        render data as JSON
    }


}
