package happy.reportes

import happy.seguridad.Persona
import happy.seguridad.Shield
import happy.tramites.Departamento
import happy.tramites.EstadoTramite
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite
import org.apache.poi.hssf.usermodel.HSSFFont
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

class RetrasadosExcelController extends Shield {
    def maxLvl = null
    def reportesPdfService
    def reportesTramitesRetrasadosService
    def dbConnectionService

    static scope = "session"

    private int creaRegistros(sheet, id, res, num, jefe) {
        num = creaTituloDep(sheet, id, res.lvl, res.totalRet, res.totalNoRec, num, jefe)
        num = creaTablaTramites(sheet, res.trams, num)
        res.deps.each { k, v ->
            num = creaRegistros(sheet, k, v, num, jefe)
        }
        return num
    }

    private int creaTituloDep(sheet, id, lvl, totalRet, totalNoRec, num, jefe) {
        num += 1
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

        def row = sheet.createRow((short) num);
        row.createCell((int) 0).setCellValue(stars + str)
        if (str != "TOTAL") {
            row.createCell((int) 1).setCellValue(dep.descripcion + " ($dep.codigo)")
        }
        num++
        row = sheet.createRow((short) num);
        row.createCell((int) 0).setCellValue("Total retrasados: ")
        row.createCell((int) 1).setCellValue(tr)
        row.createCell((int) 2).setCellValue("Total sin recepción: ")
        row.createCell((int) 3).setCellValue(tn)

        return num + 1
    }

    private int creaTituloPersona(sheet, nombre, totalRet, totalNoRec, num) {
        num += 1
        def tr = totalRet ?: 0
        def tn = totalNoRec ?: 0
        def str = " Usuario "

        def row = sheet.createRow((short) num);
        row.createCell((int) 0).setCellValue(str)
        row.createCell((int) 1).setCellValue(nombre)
        num++
        row = sheet.createRow((short) num);
        row.createCell((int) 0).setCellValue("Total retrasados: ")
        row.createCell((int) 1).setCellValue(tr)
        row.createCell((int) 2).setCellValue("Total sin recepción: ")
        row.createCell((int) 3).setCellValue(tn)

        return num + 1
    }

    private int creaHeaderTablaTramites(sheet, num) {
        def row = sheet.createRow((short) num);
        row.createCell((int) 0).setCellValue("Trámite No.")
        row.createCell((int) 1).setCellValue("De")
        row.createCell((int) 2).setCellValue("Creado Por")
        row.createCell((int) 3).setCellValue("Para")
        row.createCell((int) 4).setCellValue("Fecha Envío")
        row.createCell((int) 5).setCellValue("Fecha Recepción")
        row.createCell((int) 6).setCellValue("Fecha Límite")
        row.createCell((int) 7).setCellValue("Tiempo Envio - Recepción")
        row.createCell((int) 8).setCellValue("Tipo")
        return num + 1
    }

    def headerTablaRetrasados (sheet, num){
        def row = sheet.createRow((short) num);
        row.createCell((int) 0).setCellValue("Trámite No.")
        row.createCell((int) 1).setCellValue("De")
        row.createCell((int) 2).setCellValue("Creado Por")
//        row.createCell((int) 3).setCellValue("Para")
        row.createCell((int) 3).setCellValue("Fecha Envío")
        row.createCell((int) 4).setCellValue("Fecha Recepción")
        row.createCell((int) 5).setCellValue("Fecha Límite")
        row.createCell((int) 6).setCellValue("Tiempo de Retraso")
//        row.createCell((int) 8).setCellValue("Tipo")
    }

    def headerTramiteNoRecibidos(sheet, num){
        def row = sheet.createRow((short) num);
        row.createCell((int) 0).setCellValue("Trámite No.")
        row.createCell((int) 1).setCellValue("De")
//        row.createCell((int) 2).setCellValue("Creado Por")
        row.createCell((int) 2).setCellValue("Para")
        row.createCell((int) 3).setCellValue("Fecha Envío")
        row.createCell((int) 4).setCellValue("Tiempo de Retraso")
//        row.createCell((int) 6).setCellValue("Tipo")
    }

    private int llenaTablaTramites(sheet, res, num) {
        res.each { row ->
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
            def tipo
            if (row.tipo == "ret") {
                tipo = "Retrasado"
            } else {
                tipo = "Sin recepción"
            }

            def fila = sheet.createRow((short) num);
            fila.createCell((int) 0).setCellValue(tipo)
            fila.createCell((int) 1).setCellValue(row.trmtcdgo)
            fila.createCell((int) 2).setCellValue(row.trmtfccr.format("dd-MM-yyyy HH:mm:ss"))
            fila.createCell((int) 3).setCellValue(deDp)
            fila.createCell((int) 4).setCellValue(dePr)
            fila.createCell((int) 5).setCellValue(para)
            fila.createCell((int) 6).setCellValue(row.trmtfcen.format("dd-MM-yyyy HH:mm:ss"))
            fila.createCell((int) 7).setCellValue(rec)
            fila.createCell((int) 8).setCellValue(lim)
            fila.createCell((int) 9).setCellValue(ret)
            num++
        }
        return num
    }


    def llenaTablaRetrasados (sheet, num, it, entre, tipo) {
        def fila = sheet.createRow((short) num);
        fila.createCell((int) 0).setCellValue(it?.trmtcdgo)
        fila.createCell((int) 1).setCellValue(it?.deprdpto)
        fila.createCell((int) 2).setCellValue(it?.deprlogn)
//        fila.createCell((int) 3).setCellValue(it?.prtrdpto)
        fila.createCell((int) 3).setCellValue(it.trmtfcen?.format("dd-MM-yyyy HH:mm:ss"))
        fila.createCell((int) 4).setCellValue(it.trmtfcrc?.format("dd-MM-yyyy HH:mm:ss"))
        fila.createCell((int) 5).setCellValue(it.trmtfclr?.format("dd-MM-yyyy HH:mm:ss"))
        fila.createCell((int) 6).setCellValue(entre)
//        fila.createCell((int) 8).setCellValue(tipo)
    }


    def llenaTablaNoRecibidos (sheet, num, it, entre){
        def fila = sheet.createRow((short) num);
        fila.createCell((int) 0).setCellValue(it?.trmtcdgo)
        fila.createCell((int) 1).setCellValue(it?.deprdpto)
//        fila.createCell((int) 2).setCellValue(it?.deprdscr)
        if(it.prtrprsn){
            fila.createCell((int) 2).setCellValue(it?.prtrprsn)
        }else{
            fila.createCell((int) 2).setCellValue(it?.prtrdpto)
        }
        fila.createCell((int) 3).setCellValue(it.trmtfcen.format("dd-MM-yyyy HH:mm:ss"))
        fila.createCell((int) 4).setCellValue(entre)
//        fila.createCell((int) 6).setCellValue("No recibido")
    }

    private int creaTablaTramites(sheet, res, num) {

        if (res.size() > 0) {
//            println("res " + res.oficina)
            num = creaHeaderTablaTramites(sheet, num)
            if(res.oficina){
                num = llenaTablaTramites(sheet, res.oficina.trams, num)
                res.each { k, tram ->
                    if (k != "oficina") {
                        def tr = tram.totalRet
                        def tn = tram.totalNoRec
                        num = creaTituloPersona(sheet, tram.nombre, tr, tn, num)
                        num = creaHeaderTablaTramites(sheet, num)
                        num = llenaTablaTramites(sheet, tram.trams, num)
                    }
                }
            }
        }
        return num
    }

    private String drawStars(lvl) {
        def stars = ""
        (lvl - 1).times {
            stars += " "
        }
        lvl.times {
            stars += "*"
        }
        return stars
    }

    def reporteRetrasadosDetalle() {

//        println("params ret excel " + params)
        def jefe = params.jefe == '1'
        def ttl = ""
        def results = []
        /*OJO---------------------------*/
//        def idUsario = session.usuario.id
        def idUsario = params.id
        def sqls
        def sqlEntre
        def sqlSalida
        def sqlEntreSalida
        def entre
        def entreSalida
        def fechaRececion = new Date().format("yyyy/MM/dd HH:mm:ss")
        def totalRetrasados = 0
        def totalSin = 0
        def totalRetrasadosPer = 0
        def totalSinPer = 0
        def totalNoRecibidosPer = 0
        def totalNoRecibidos = 0
        def ahora = new Date()
        def enviaRecibe = RolPersonaTramite.findAllByCodigoInList(['R001', 'R002'])
        def per = Persona.get(idUsario)

        def path = servletContext.getRealPath("/") + "xls/"
        new File(path).mkdirs()
        //esto crea un archivo temporal que puede ser siempre el mismo para no ocupar espacio
        String filename = path + "text.xlsx";
        def name = "reporteTramitesRetrasados_" + new Date().format("ddMMyyyy_hhmm") + ".xlsx";
        String sheetName = "Retrasados";
        String sheetName2 = "No Recibidos";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName);
        XSSFSheet sheet2 = wb.createSheet(sheetName2);
        sheet2.setColumnWidth(0, 4000)
        sheet2.setColumnWidth(1, 3000)
        sheet2.setColumnWidth(2, 6000)
        sheet2.setColumnWidth(3, 4000)
        sheet2.setColumnWidth(4, 8000)
        sheet2.setColumnWidth(5, 8000)
        sheet2.setColumnWidth(6, 4000)
        sheet2.setColumnWidth(7, 4000)
        sheet2.setColumnWidth(8, 4000)
        sheet2.setColumnWidth(9, 2500)

        CreationHelper createHelper = wb.getCreationHelper();

        sheet.setAutobreaks(true);
        XSSFRow rowHead = sheet.createRow((short) 0);
        rowHead.setHeightInPoints(14)
        sheet.setColumnWidth(0, 4000)
        sheet.setColumnWidth(1, 3000)
        sheet.setColumnWidth(2, 4000)
        sheet.setColumnWidth(3, 4000)
        sheet.setColumnWidth(4, 8000)
        sheet.setColumnWidth(5, 8000)
        sheet.setColumnWidth(6, 4000)
        sheet.setColumnWidth(7, 4000)
        sheet.setColumnWidth(8, 4000)
        sheet.setColumnWidth(9, 2500)
        rowHead.createCell((int) 1).setCellValue("GAD DE LA PROVINCIA DE PICHINCHA")
        rowHead = sheet.createRow((short) 1);
        rowHead.createCell((int) 1).setCellValue("SISTEMA DE ADMINISTRACION DOCUMENTAL")
        rowHead = sheet.createRow((short) 2);
        rowHead.createCell((int) 1).setCellValue("Reporte detallado de Trámites Retrasados y No recibidos")
        rowHead = sheet.createRow((short) 3);
        rowHead.createCell((int) 1).setCellValue("del usuario $per.nombre $per.apellido ($per.login)")
        rowHead = sheet.createRow((short) 4);
        rowHead.createCell((int) 1).setCellValue("" + new Date().format('dd-MM-yyyy HH:mm'))
        rowHead = sheet.createRow((short) 5);
        rowHead.createCell((int) 1).setCellValue("Nota: El tiempo en días corresponde a una jornada de trabajo diaria")
        def num = 6
        def numTab2 = 2

//        if (params.dpto) {
//            def dep = Departamento.get(params.dpto.toLong())
//            ttl += "\ndel dpto. $dep.descripcion ($dep.codigo)"
//            results = reportesTramitesRetrasadosService.datos(params.dpto).res
//        } else if (params.prsn) {
//            def per = Persona.get(params.prsn.toLong())


        /*INCIO RETRASADOS DPTO EXCEL*/
        if (per.esTrianguloOff()) {
            sqls = "select * from entrada_dpto(" + idUsario + ")"
            def cn = dbConnectionService.getConnection()
            def cn2 = dbConnectionService.getConnection()
            def tipo

            headerTablaRetrasados(sheet, num)
            num++

            cn.eachRow(sqls.toString()){
//                    if(it.trmtfcrc){
//                        sqlEntre="select * from tmpo_entre(" + "'" + it?.trmtfcen + "'" + "," + "'" + it?.trmtfcrc + "'"+ ")"
//                    }else{
//                        sqlEntre="select * from tmpo_entre('${it?.trmtfcen}' , cast('${fechaRececion.toString()}' as timestamp without time zone))"
//                    }

//                    entre = cn2.firstRow(sqlEntre)

                if(it?.trmtfcbq < new Date() && it?.trmtfcrc == null){
//                        tipo = "Sin Recepción"
//                        llenaTablaRetrasados(sheet, num, it, entre.toString(), tipo)
//                        totalSin += 1
//                        num++
                }else {
                    if(it.trmtfclr){
                        if(it.trmtfclr < ahora) {
                            sqlEntre = "select * from tmpo_entre('${it?.trmtfclr}' , cast('${fechaRececion.toString()}' as timestamp without time zone))"
                            cn2.eachRow(sqlEntre) { d ->
                                entre = "${d.dias} días ${d.hora} horas ${d.minu} minutos"
                            }
                            llenaTablaRetrasados(sheet, num, it, entre.toString(), tipo)
                            totalRetrasados += 1
                            num++
                        }
                    }
                }
            }
            rowHead = sheet.createRow((short) num);
            rowHead.createCell((int) 0).setCellValue("Total trámites Retrasados: " + totalRetrasados)
            num++
            cn.close()
            num = num+1

    /*********************************************************/
            /*INICIO NO RECIBIDOS DPTO EXCEL*/
            sqlSalida = "select * from salida_dpto(" + idUsario+ ")"
            def cn3 = dbConnectionService.getConnection()
            def cn5 = dbConnectionService.getConnection()
            def tramiteSalidaDep
            def prtrSalidaDep

            rowHead = sheet2.createRow((short) numTab2);
            rowHead.createCell((int) 0).setCellValue("Reporte de Trámites No Recibidos")
            numTab2++

            headerTramiteNoRecibidos(sheet2, numTab2)
            numTab2++

            cn3.eachRow(sqlSalida.toString()){sal->

                if(sal.edtrcdgo == 'E004' || sal.edtrcdgo == 'E003'){
                    tramiteSalidaDep = Tramite.get(sal?.trmt__id)
                    prtrSalidaDep = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteInListAndFechaRecepcionIsNull(tramiteSalidaDep, enviaRecibe)
                    prtrSalidaDep.each {
                        sqlEntreSalida="select * from tmpo_entre('${sal?.trmtfcen}' , cast('${fechaRececion.toString()}' as timestamp without time zone))"
                        cn5.eachRow(sqlEntreSalida.toString()){ d ->
                            entreSalida = "${d.dias} días ${d.hora} horas ${d.minu} minutos"
                        }
                        llenaTablaNoRecibidos (sheet2, numTab2, sal, entreSalida.toString())
                        totalNoRecibidos += 1
                        numTab2++
                    }

                }
            }

            rowHead = sheet2.createRow((short) numTab2);
            rowHead.createCell((int) 0).setCellValue("Total trámites No Recibidos: " + totalNoRecibidos)
            cn3.close()
    /*************************************************************************/
        } else {

            /*INICIO RETRASADOS PRSN EXCEL*/

            sqls = "select * from entrada_prsn(" + idUsario + ")"
            def cn = dbConnectionService.getConnection()
            def cn2 = dbConnectionService.getConnection()
            def tipo

            headerTablaRetrasados(sheet, num)
            num++

            cn.eachRow(sqls.toString()){
//                if(it.trmtfcrc){
//                    sqlEntre="select * from tmpo_entre(" + "'" + it?.trmtfcen + "'" + "," + "'" + it?.trmtfcrc + "'"+ ")"
//                }else{
//                    sqlEntre="select * from tmpo_entre('${it?.trmtfcen}' , cast('${fechaRececion.toString()}' as timestamp without time zone))"
//                }

                if(it?.trmtfcbq < new Date() && it?.trmtfcrc == null){
//                        tipo = "Sin Recepción"
//                        llenaTablaRetrasados(sheet, num, it, entre.toString(), tipo)
//                        totalSinPer += 1
//                        num++
                }else {
                    if(it.trmtfclr){
                        if (it.trmtfclr < ahora) {
                            sqlEntre = "select * from tmpo_entre('${it?.trmtfclr}' , cast('${fechaRececion.toString()}' as timestamp without time zone))"
                            cn2.eachRow(sqlEntre) { d ->
                                entre = "${d.dias} días ${d.hora} horas ${d.minu} minutos"
                            }
                            llenaTablaRetrasados(sheet, num, it, entre.toString(), tipo)
                            totalRetrasadosPer += 1
                            num++
                        }
                    }
                }
            }

            rowHead = sheet.createRow((short) num);
            rowHead.createCell((int) 0).setCellValue("Total trámites Retrasados: " + totalRetrasadosPer)
            num++

            cn.close()
            num = num+1
    /************************************************************************/
    /*INICIO NO RECIBIDOS PRSN EXCEL*/
            sqlSalida = "select * from salida_prsn(" + idUsario+ ")"
            def cn4 = dbConnectionService.getConnection()
            def cn6 = dbConnectionService.getConnection()
            def tramiteSalida
            def prtrSalida

            rowHead = sheet2.createRow((short) numTab2);
            rowHead.createCell((int) 0).setCellValue("Reporte de Trámites No Recibidos")
            numTab2++

            headerTramiteNoRecibidos(sheet2, numTab2)
            numTab2++

            cn4.eachRow(sqlSalida.toString()){sal->

                if(sal.edtrcdgo == 'E004' || sal.edtrcdgo == 'E003'){
                    tramiteSalida = Tramite.get(sal?.trmt__id)
                    prtrSalida = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteInListAndFechaRecepcionIsNull(tramiteSalida, enviaRecibe)
                    prtrSalida.each {
                        sqlEntreSalida="select * from tmpo_entre('${sal?.trmtfcen}' , cast('${fechaRececion.toString()}' as timestamp without time zone))"
                        cn6.eachRow(sqlEntreSalida.toString()){ d ->
                            entreSalida = "${d.dias} días ${d.hora} horas ${d.minu} minutos"
                        }
                        llenaTablaNoRecibidos (sheet2, numTab2, sal, entreSalida.toString())
                        totalNoRecibidosPer +=1
                        numTab2++
                    }
                }
            }
            cn4.close()
            rowHead = sheet2.createRow((short) numTab2);
            rowHead.createCell((int) 0).setCellValue("Total trámites No Recibidos: " + totalNoRecibidosPer)
        }

        FileOutputStream fileOut = new FileOutputStream(filename);
        wb.write(fileOut);
        fileOut.close();
        String disHeader = 'Attachment;Filename="' + name + '"';
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

//    def reporteRetrasadosConsolidado() {
//        def jefe = params.jefe == '1'
//        def ttl = ""
//        def results = []
//
//        if (params.dpto) {
//            def dep = Departamento.get(params.dpto.toLong())
//            ttl += "\ndel dpto. $dep.descripcion ($dep.codigo)"
//            results = reportesTramitesRetrasadosService.datos(params.dpto).res
//        } else if (params.prsn) {
//            def per = Persona.get(params.prsn.toLong())
//            ttl += "\ndel usuario $per.nombre $per.apellido ($per.login)"
//            if (per.esTrianguloOff()) {
//                ttl += "\n[Bandeja de entrada del departamento]"
//                results = reportesTramitesRetrasadosService.datos(per.departamentoId, params.prsn).res
//            } else {
//                results = reportesTramitesRetrasadosService.datosPersona(params.prsn).res
//            }
//        }
//        def path = servletContext.getRealPath("/") + "xls/"
//        new File(path).mkdirs()
//        //esto crea un archivo temporal que puede ser siempre el mismo para no ocupar espacio
//        String filename = path + "text.xlsx";
//        def name = "reporteConsolidadoTramitesRetrasados_" + new Date().format("ddMMyyyy_hhmm") + ".xlsx";
//        String sheetName = "SAD-WEB Reporte";
//        XSSFWorkbook wb = new XSSFWorkbook();
//        XSSFSheet sheet = wb.createSheet(sheetName);
//        CreationHelper createHelper = wb.getCreationHelper();
//
//        sheet.setAutobreaks(true);
//        XSSFRow rowHead = sheet.createRow((short) 0);
//        rowHead.setHeightInPoints(14)
//        sheet.setColumnWidth(0, 5000)
//        sheet.setColumnWidth(1, 15000)
//        rowHead.createCell((int) 1).setCellValue("GAD DE LA PROVINCIA DE PICHINCHA")
//        rowHead = sheet.createRow((short) 1);
//        rowHead.createCell((int) 1).setCellValue("SISTEMA DE ADMINISTRACION DOCUMENTAL")
//        rowHead = sheet.createRow((short) 2);
//        rowHead.createCell((int) 1).setCellValue("Reporte consolidado de Trámites Retrasados y sin recepción")
//        rowHead = sheet.createRow((short) 3);
//        rowHead.createCell((int) 1).setCellValue(ttl)
//        rowHead = sheet.createRow((short) 4);
//        rowHead.createCell((int) 1).setCellValue("" + new Date().format('dd-MM-yyyy HH:mm'))
//
//        def rowNode = sheet.createRow((short) 5);
//        rowNode.createCell((int) 2).setCellValue("Retrasados");
//        rowNode.createCell((int) 3).setCellValue("Sin recepción");
//
//        def num = 7
//
//        results.each { k, v ->
//            num = creaRegistrosConsolidado(sheet, k, v, num, jefe)
//        }
//
//        FileOutputStream fileOut = new FileOutputStream(filename);
//        wb.write(fileOut);
//        fileOut.close();
//        String disHeader = 'Attachment;Filename="' + name + '"';
//        response.setHeader("Content-Disposition", disHeader);
//        File desktopFile = new File(filename);
//        PrintWriter pw = response.getWriter();
//        FileInputStream fileInputStream = new FileInputStream(desktopFile);
//        int j;
//
//        while ((j = fileInputStream.read()) != -1) {
//            pw.write(j);
//        }
//        fileInputStream.close();
//        response.flushBuffer();
//        pw.flush();
//        pw.close();
//    }



    def reporteRetrasadosConsolidado () {

        def fileName = "documentos_retrasados_"
        def title = ["Reporte de documentos retrasados y no recibidos"]
        def totalResumenGenerado = 0
        def totalRecibido = 0
        def usuario = Persona.get(session.usuario.id)
        def departamentoUsuario = usuario?.departamento?.id

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

        def index = 6
        XSSFRow rowHead = sheet.createRow((short) index);
        rowHead.setHeightInPoints(14)

        Cell cell = rowHead.createCell((int) 0)
        cell.setCellValue("Usuario")
        sheet.setColumnWidth(0, 13000)

        cell = rowHead.createCell((int) 1)
        cell.setCellValue("Perfil")
        sheet.setColumnWidth(1, 10000)

        cell = rowHead.createCell((int) 2)
        cell.setCellValue("Retrasados")
        sheet.setColumnWidth(2, 3000)

        cell = rowHead.createCell((int) 3)
        cell.setCellValue("No Recibidos")
        sheet.setColumnWidth(3, 3000)
        index++

        def sqlGen
        def sql

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

        /*INICIO RETRASADOS CONSOLIDADO DPTO EXCEL*/
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
        /***********************************************************/
        /*INICIO NO RECIBIDOS CONSOLIDADO DPTO EXCEL*/
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

        } else {
         /*INICIO RETRASADOS CONSOLIDADO PRSN EXCEL*/
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
         /************************************************************/
         /*INICIO NO RECIBIDOS CONSOLIDADO PRSN EXCEL*/
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

        XSSFRow row2 = sheet.createRow((short) index)
        row2.createCell((int) 0).setCellValue("${usuario?.nombre}" + "  " + "${usuario?.apellido}" + " (" +   "${usuario?.login}" + ")")
        row2.createCell((int) 1).setCellValue("${session?.perfil}")
        row2.createCell((int) 2).setCellValue(" " + totalRetrasados)
        row2.createCell((int) 3).setCellValue(" " + totalNoRecibidos)
        index++

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

    private int creaRegistrosConsolidado(sheet, id, res, num, jefe) {
        num = creaFilaDep(sheet, id, res.lvl, res.totalRet, res.totalNoRec, num, jefe)
        num = creaFilaPers(sheet, res.lvl + 1, res.trams, num)
        res.deps.each { k, v ->
            num = creaRegistrosConsolidado(sheet, k, v, num, jefe)
        }
        return num
    }

    private int creaFilaPers(sheet, lvl, res, num) {
        if (res.size() > 0) {
            def stars = drawStars(lvl)
            res.each { k, tram ->
                def row = sheet.createRow((short) num);
                row.createCell((int) 0).setCellValue(stars + " Usuario")
                row.createCell((int) 1).setCellValue(tram.nombre)
                row.createCell((int) 2).setCellValue(tram.totalRet ?: 0)
                row.createCell((int) 3).setCellValue(tram.totalNoRec ?: 0)
                num++
            }
        }
        return num
    }

    private int creaFilaDep(sheet, id, lvl, totalRet, totalNoRec, num, jefe) {
        def dep = Departamento.get(id.toLong())
        def stars = drawStars(lvl)
        def str = " Departamento"
        if (lvl == 0) {
            if (jefe) {
                str = "TOTAL"
            } else {
                str = " Prefectura"
            }
        } else if (lvl == 1) {
            str = " Dirección"
        }
        if (jefe) {
            lvl -= 1
        }
        def nombre = stars + str

        def row = sheet.createRow((short) num);
        row.createCell((int) 0).setCellValue(nombre)
        if (str != "TOTAL") {
            row.createCell((int) 1).setCellValue(dep.descripcion + " ($dep.codigo)")
        }
        row.createCell((int) 2).setCellValue(totalRet ?: 0)
        row.createCell((int) 3).setCellValue(totalNoRec ?: 0)

        return num + 1
    }

    /* ************ HACIA ABAJO: REPORTES ANTIGUOS CON EL METODO ANTERIOR Q DEMORABA UN MONTON ******************/
//    def reporteRetrasadosDetalle_old() {
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
//        maxLvl = null
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
//        if (pdt) {
//            pdt.each { pd ->
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
//
//        def hijos = datos["hijos"]
//        if (datos['objeto']) {
//            if ((puedeVer.id.contains(datos["objeto"].id))) {
//                maxLvl = datos
//            }
//        }
//        def path = servletContext.getRealPath("/") + "xls/"
//        new File(path).mkdirs()
//        //esto crea un archivo temporal que puede ser siempre el mismo para no ocupar espacio
//        String filename = path + "text.xlsx";
//        String sheetName = "SAD-WEB Reporte";
//        XSSFWorkbook wb = new XSSFWorkbook();
//        XSSFSheet sheet = wb.createSheet(sheetName);
//        CreationHelper createHelper = wb.getCreationHelper();
//
//        Font font = wb.createFont();
//        font.setFontHeightInPoints((short) 12);
//        font.setFontName(HSSFFont.FONT_ARIAL);
//        font.setItalic(true);
//        font.setBold(true);
//        font.setColor(HSSFColor.GREEN.index);
//        CellStyle style = wb.createCellStyle();
//        style.setAlignment(CellStyle.ALIGN_CENTER);
//        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//        style.setFont(font)
//        sheet.setAutobreaks(true);
//        XSSFRow rowHead = sheet.createRow((short) 0);
//        rowHead.setHeightInPoints(14)
//        sheet.setColumnWidth(0, 8000)
//        sheet.setColumnWidth(1, 8000)
//        sheet.setColumnWidth(2, 8000)
//        sheet.setColumnWidth(3, 8000)
//        sheet.setColumnWidth(4, 8000)
//        sheet.setColumnWidth(5, 8000)
//        sheet.setColumnWidth(6, 8000)
//        sheet.setColumnWidth(7, 8000)
//        sheet.setColumnWidth(8, 8000)
//        Cell cell = rowHead.createCell((int) 0).setCellValue("GAD DE LA PROVINCIA DE PICHINCHA")
//        rowHead = sheet.createRow((short) 1);
//        cell = rowHead.createCell((int) 0).setCellValue("SISTEMA DE ADMINISTRACION DOCUMENTAL")
//        rowHead = sheet.createRow((short) 2);
//        cell = rowHead.createCell((int) 0).setCellValue("Reporte detallado de Trámites Retrasados y sin recepción")
//        rowHead = sheet.createRow((short) 3);
//        cell = rowHead.createCell((int) 0).setCellValue("" + new Date().format('dd-MM-yyyy HH:mm'))
//        def num = 5
//        def row = sheet.createRow((short) num);
//        row.setHeightInPoints(14)
//
//
//        hijos.each { lvl ->
//            if (puedeVer.size() == 0 || (puedeVer.id.contains(lvl["objeto"].id))) {
//                if (maxLvl == null) {
//                    maxLvl = lvl
//                }
//                row.createCell((int) 0).setCellValue("" + lvl["objeto"])
//                num++
//                row = sheet.createRow((short) num);
//                row.createCell((int) 0).setCellValue("Tipo")
//                row.createCell((int) 1).setCellValue("Usuario")
//                row.createCell((int) 2).setCellValue("Nro.")
//                row.createCell((int) 3).setCellValue("F. Creación")
//                row.createCell((int) 4).setCellValue("De")
//                row.createCell((int) 5).setCellValue("Creado por")
//                row.createCell((int) 6).setCellValue("F. Envío")
//                row.createCell((int) 7).setCellValue("F. Recepcíon")
//                row.createCell((int) 8).setCellValue("F. Límite")
//                if (lvl["tramites"].size() > 0) {
//                    def triangulo = ""
//                    if (lvl["triangulos"] && lvl["triangulos"].size() > 0) {
//                        triangulo = lvl["triangulos"].get(0).toString() + " (Oficina)"
//                    } else {
//                        triangulo = "Oficina"
//                    }
//
//                    lvl["tramites"].each { t ->
//                        num++
//                        row = sheet.createRow((short) num);
//                        row.createCell((int) 1).setCellValue("${(t.fechaRecepcion) ? 'Retrasado' : 'Sin recepción'}")
//                        row.createCell((int) 0).setCellValue(triangulo)
//                        row.createCell((int) 2).setCellValue("${t.tramite.codigo} ${t.rolPersonaTramite.codigo == 'R002' ? '[CC]' : ''}")
//                        row.createCell((int) 3).setCellValue("${t.tramite.fechaCreacion.format('dd-MM-yyyy HH:mm')}")
//                        if (t.tramite.deDepartamento) {
//                            row.createCell((int) 4).setCellValue("${t.tramite.deDepartamento.codigo}")
//                        } else {
//                            row.createCell((int) 4).setCellValue("${t.tramite.de.departamento.codigo}")
//
//                        }
//                        row.createCell((int) 5).setCellValue("${t.tramite.de.login}")
//                        row.createCell((int) 6).setCellValue("${t.fechaEnvio.format('dd-MM-yyyy hh:mm')}")
//                        row.createCell((int) 7).setCellValue("${(t.fechaRecepcion) ? t.fechaRecepcion?.format('dd-MM-yyyy hh:mm') : ''}")
//                        row.createCell((int) 8).setCellValue("${(t.fechaLimiteRespuesta) ? t.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm') : ''}")
//                    }
//                }
//
//                lvl["personas"].each { p ->
//                    p["tramites"].each { t ->
//                        println("--> " + t)
//                        num++
//                        row = sheet.createRow((short) num);
//                        row.createCell((int) 0).setCellValue("${(t?.fechaRecepcion) ? 'Retrasado' : 'Sin recepción'}")
//                        row.createCell((int) 1).setCellValue("${p['objeto']?.login}")
//                        row.createCell((int) 2).setCellValue("${t?.tramite?.codigo} ${t?.rolPersonaTramite?.codigo == 'R002' ? '[CC]' : ''}")
//                        row.createCell((int) 3).setCellValue("${t?.tramite?.fechaCreacion?.format('dd-MM-yyyy HH:mm')}")
//                        if (t.tramite.deDepartamento) {
//                            row.createCell((int) 4).setCellValue("${t?.tramite?.deDepartamento?.codigo}")
//                        } else {
//                            row.createCell((int) 4).setCellValue("${t?.tramite?.de?.departamento?.codigo}")
//
//                        }
//                        row.createCell((int) 5).setCellValue("${t?.tramite?.de?.login}")
//                        row.createCell((int) 6).setCellValue("${t?.fechaEnvio?.format('dd-MM-yyyy hh:mm')}")
//                        row.createCell((int) 7).setCellValue("${(t?.fechaRecepcion) ? t?.fechaRecepcion?.format('dd-MM-yyyy hh:mm') : ''}")
//                        row.createCell((int) 8).setCellValue("${(t?.fechaLimiteRespuesta) ? t?.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm') : ''}")
//                    }
//                }
//            }
//            num = imprimeHijosPdf(lvl, sheet, num, params, usuario, deps, puedeVer)
//            num++
//        }
//        if (maxLvl) {
//            num++
//            num++
//            row = sheet.createRow((short) num);
//            row.createCell((int) 0).setCellValue("");
//            row.createCell((int) 1).setCellValue("Retrasados");
//            row.createCell((int) 2).setCellValue("Sin recepción");
//            num++
//            row = sheet.createRow((short) num);
//            row.createCell((int) 0).setCellValue("TOTAL");
//            row.createCell((int) 1).setCellValue(maxLvl["rezagados"]);
//            row.createCell((int) 2).setCellValue(maxLvl["retrasados"]);
//        }
//        FileOutputStream fileOut = new FileOutputStream(filename);
//        wb.write(fileOut);
//        fileOut.close();
//        String disHeader = 'Attachment;Filename="reporte.xlsx"';
//        response.setHeader("Content-Disposition", disHeader);
//        File desktopFile = new File(filename);
//        PrintWriter pw = response.getWriter();
//        FileInputStream fileInputStream = new FileInputStream(desktopFile);
//        int j;
//
//        while ((j = fileInputStream.read()) != -1) {
//            pw.write(j);
//        }
//        fileInputStream.close();
//        response.flushBuffer();
//        pw.flush();
//        pw.close();
//    }
//
//    def imprimeHijosPdf(arr, sheet, num, params, usuario, deps, puedeVer) {
//        def datos = arr["hijos"]
//        def row
//        datos.each { lvl ->
//            println " " + lvl
//            if (puedeVer.size() == 0 || (puedeVer.id.contains(lvl["objeto"].id))) {
//                println "puede ver"
//                if (maxLvl == null) {
//                    maxLvl = lvl
//                }
//                num++
//                row = sheet.createRow((short) num);
//                row.createCell((int) 0).setCellValue("" + lvl["objeto"])
//                num++
//                row = sheet.createRow((short) num);
//                row.createCell((int) 0).setCellValue("Tipo")
//                row.createCell((int) 1).setCellValue("Usuario")
//                row.createCell((int) 2).setCellValue("Nro.")
//                row.createCell((int) 3).setCellValue("F. Creación")
//                row.createCell((int) 4).setCellValue("De")
//                row.createCell((int) 5).setCellValue("Creado por")
//                row.createCell((int) 6).setCellValue("F. Envío")
//                row.createCell((int) 7).setCellValue("F. Recepcíon")
//                row.createCell((int) 8).setCellValue("F. Límite")
//                if (lvl["tramites"].size() > 0) {
//                    def triangulo = ""
//                    if (lvl["triangulos"] && lvl["triangulos"].size() > 0) {
//                        triangulo = lvl["triangulos"].get(0).toString() + " (Oficina)"
//                    } else {
//                        triangulo = "Oficina"
//                    }
//
//                    lvl["tramites"].each { t ->
//                        num++
//                        row = sheet.createRow((short) num);
//                        row.createCell((int) 0).setCellValue("${(t.fechaRecepcion) ? 'Retrasado' : 'Sin recepción'}")
//                        row.createCell((int) 1).setCellValue(triangulo)
//                        row.createCell((int) 2).setCellValue("${t.tramite.codigo} ${t.rolPersonaTramite.codigo == 'R002' ? '[CC]' : ''}")
//                        row.createCell((int) 3).setCellValue("${t.tramite.fechaCreacion.format('dd-MM-yyyy HH:mm')}")
//                        if (t.tramite.deDepartamento) {
//                            row.createCell((int) 4).setCellValue("${t.tramite.deDepartamento.codigo}")
//                        } else {
//                            row.createCell((int) 4).setCellValue("${t.tramite.de.departamento.codigo}")
//
//                        }
//                        row.createCell((int) 5).setCellValue("${t.tramite.de.login}")
//                        row.createCell((int) 6).setCellValue("${t.fechaEnvio.format('dd-MM-yyyy hh:mm')}")
//                        row.createCell((int) 7).setCellValue("${(t.fechaRecepcion) ? t.fechaRecepcion?.format('dd-MM-yyyy hh:mm') : ''}")
//                        row.createCell((int) 8).setCellValue("${(t.fechaLimiteRespuesta) ? t.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm') : ''}")
//                    }
//
//                }
//                lvl["personas"].each { p ->
//                    p["tramites"].each { t ->
//                        num++
//                        row = sheet.createRow((short) num);
//                        row.createCell((int) 0).setCellValue("${(t.fechaRecepcion) ? 'Retrasado' : 'Sin recepción'}")
//                        row.createCell((int) 1).setCellValue("${p['objeto'].login}")
//                        row.createCell((int) 2).setCellValue("${t.tramite.codigo} ${t.rolPersonaTramite.codigo == 'R002' ? '[CC]' : ''}")
//                        row.createCell((int) 3).setCellValue("${t.tramite.fechaCreacion.format('dd-MM-yyyy HH:mm')}")
//                        if (t.tramite.deDepartamento) {
//                            row.createCell((int) 4).setCellValue("${t.tramite.deDepartamento.codigo}")
//                        } else {
//                            row.createCell((int) 4).setCellValue("${t.tramite.de.departamento.codigo}")
//                        }
//                        row.createCell((int) 5).setCellValue("${t.tramite.de.login}")
//                        row.createCell((int) 6).setCellValue("${t.fechaEnvio.format('dd-MM-yyyy hh:mm')}")
//                        row.createCell((int) 7).setCellValue("${(t.fechaRecepcion) ? t.fechaRecepcion?.format('dd-MM-yyyy hh:mm') : ''}")
//                        row.createCell((int) 8).setCellValue("${(t.fechaLimiteRespuesta) ? t.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm') : ''}")
//                    }
//                }
//            }
//            if (lvl["hijos"].size() > 0) {
//                num = imprimeHijosPdf(lvl, sheet, num, params, usuario, deps, puedeVer)
//            }
//        }
//        return num
//    }
//
//    def reporteRetrasadosConsolidado_old() {
//        maxLvl = null
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
//
//        def path = servletContext.getRealPath("/") + "xls/"
//        new File(path).mkdirs()
//        //esto crea un archivo temporal que puede ser siempre el mismo para no ocupar espacio
//        String filename = path + "text.xlsx";
//        String sheetName = "Sheet1";
//        XSSFWorkbook wb = new XSSFWorkbook();
//        XSSFSheet sheet = wb.createSheet(sheetName);
//        CreationHelper createHelper = wb.getCreationHelper();
//
//        Font font = wb.createFont();
//        font.setFontHeightInPoints((short) 12);
//        font.setFontName(HSSFFont.FONT_ARIAL);
//        font.setItalic(true);
//        font.setBold(true);
//        font.setColor(HSSFColor.GREEN.index);
//
//        CellStyle style = wb.createCellStyle();
//        style.setAlignment(CellStyle.ALIGN_CENTER);
//        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//        style.setFont(font)
//
//        sheet.setAutobreaks(true);
//        def total = 0
//        def totalSr = 0
//        def hijos = datos["hijos"]
//        println("hijos " + hijos)
//        if (datos["objeto"]) {
//            if ((puedeVer.id.contains(datos["objeto"].id))) {
//                maxLvl = datos
//            }
//        }
//
//        XSSFRow rowHead = sheet.createRow((short) 0);
//        rowHead.setHeightInPoints(14)
//        sheet.setColumnWidth(1, 15000)
//        Cell cell = rowHead.createCell((int) 1).setCellValue("GAD DE LA PROVINCIA DE PICHINCHA")
//        rowHead = sheet.createRow((short) 1);
//        cell = rowHead.createCell((int) 1).setCellValue("SISTEMA DE ADMINISTRACION DOCUMENTAL")
//        rowHead = sheet.createRow((short) 2);
//        cell = rowHead.createCell((int) 1).setCellValue("Reporte resumido de Trámites Retrasados y sin recepción")
//        rowHead = sheet.createRow((short) 3);
//        cell = rowHead.createCell((int) 1).setCellValue("" + new Date().format('dd-MM-yyyy HH:mm'))
//        def row = sheet.createRow((short) 4);
//
//        def rowNode = sheet.createRow((short) 5);
//        row.createCell((int) 2).setCellValue("Retrasados");
//        row.createCell((int) 3).setCellValue("Sin recepción");
//        def num = 6
//        row = sheet.createRow((short) num);
//        row.setHeightInPoints(14)
//        rowNode.setHeightInPoints(14)
//        hijos.each { lvl ->
//            if (puedeVer.size() == 0 || (puedeVer.id.contains(lvl["objeto"].id))) {
//                if (maxLvl == null) {
//                    maxLvl = lvl
//                }
//                //println "imprime departamento padre"+lvl["objeto"] +"  en "+(num-1)
//                rowNode.createCell((int) 0).setCellValue("Dirección:");
//                rowNode.createCell((int) 1).setCellValue("" + lvl["objeto"]);
//                def totalNode = 0
//                def totalNodeSr
//
//                if (lvl["tramites"].size() > 0) {
//                    row.createCell((int) 0).setCellValue("Usuarios");
//                    lvl["triangulos"].each { t ->
//                        row.createCell((int) 1).setCellValue("${t} (Oficina)");
//                        row.createCell((int) 2).setCellValue(lvl["ofiRz"]);
//                        row.createCell((int) 3).setCellValue(lvl["ofiRs"]);
//                        num++
//                        row = sheet.createRow((short) num);
//                        if (totalNode == 0) {
//                            totalNode += lvl["ofiRz"]
//                        }
//                        if (totalNodeSr == 0) {
//                            totalNodeSr += lvl["ofiRs"]
//                        }
//
//                    }
//                }
//                lvl["personas"].each { p ->
//                    row.createCell((int) 1).setCellValue("${p['objeto']}");
//                    row.createCell((int) 2).setCellValue(p['rezagados']);
//                    row.createCell((int) 3).setCellValue(p['retrasados']);
//                    num++
//                    row = sheet.createRow((short) num);
//                    totalNode += p["rezagados"]
//                    if (totalNodeSr && p["retrasados"]) {
//                        totalNodeSr += p["retrasados"]
//                    }
//                }
//                rowNode.createCell((int) 2).setCellValue(lvl["rezagados"]);
//                rowNode.createCell((int) 3).setCellValue(lvl["retrasados"]);
//                total += totalNode
//                rowNode = sheet.createRow((short) num);
//                num++
//
//            }
//            def res = imprimeHijosXlsConsolidado(lvl, sheet, num, params, usuario, deps, puedeVer, total)
//            total += res[0]
//            totalSr += res[2]
//            num = res[1]
//        }
//        if (maxLvl) {
//            row = sheet.createRow((short) num);
//            row.createCell((int) 1).setCellValue("TOTAL");
//            row.createCell((int) 2).setCellValue(maxLvl["rezagados"]);
//            row.createCell((int) 3).setCellValue(maxLvl["retrasados"]);
//        }
//
//        FileOutputStream fileOut = new FileOutputStream(filename);
//        wb.write(fileOut);
//        fileOut.close();
//        String disHeader = 'Attachment;Filename="reporte.xlsx"';
//        response.setHeader("Content-Disposition", disHeader);
//        File desktopFile = new File(filename);
//        PrintWriter pw = response.getWriter();
//        FileInputStream fileInputStream = new FileInputStream(desktopFile);
//        int j;
//
//        while ((j = fileInputStream.read()) != -1) {
//            pw.write(j);
//        }
//        fileInputStream.close();
//        response.flushBuffer();
//        pw.flush();
//        pw.close();
//
//    }
//
//    def imprimeHijosXlsConsolidado(arr, sheet, num, params, usuario, deps, puedeVer, total) {
//        total = 0
//        def totalSr = 0
//        def datos = arr["hijos"]
//        def rowNode = sheet.createRow((short) num);
//        num++
//        def row = sheet.createRow((short) num);
//        row.setHeightInPoints(14)
//        rowNode.setHeightInPoints(14)
//        datos.each { lvl ->
//            if (puedeVer.size() == 0 || (puedeVer.id.contains(lvl["objeto"].id))) {
//                if (maxLvl == null) {
//                    maxLvl = lvl
//                }
//                rowNode.createCell((int) 0).setCellValue("Departamento:");
//                rowNode.createCell((int) 1).setCellValue("" + lvl["objeto"]);
//                def totalNode = 0
//                def totalNodeSr = 0
//
//                if (lvl["tramites"].size() > 0) {
//                    row.createCell((int) 0).setCellValue("Usuarios");
//                    lvl["triangulos"].each { t ->
//                        row.createCell((int) 1).setCellValue("${t} (Oficina)");
//                        row.createCell((int) 2).setCellValue(lvl["ofiRz"]);
//                        row.createCell((int) 3).setCellValue(lvl["ofiRs"]);
//                        num++
//                        row = sheet.createRow((short) num);
//                        if (totalNode == 0) {
//                            totalNode += lvl["ofiRz"]
//                        }
//                        if (totalNodeSr == 0) {
//                            totalNodeSr += lvl["ofiRs"]
//                        }
//                    }
//                }
//                lvl["personas"].each { p ->
//                    row.createCell((int) 1).setCellValue("${p['objeto']}");
//                    row.createCell((int) 2).setCellValue(p['rezagados']);
//                    row.createCell((int) 3).setCellValue(p['retrasados']);
//                    num++
//                    row = sheet.createRow((short) num);
//                    totalNode += p["rezagados"]
//                    totalNodeSr += p["retrasados"]
//                }
//                rowNode.createCell((int) 2).setCellValue(lvl["rezagados"]);
//                rowNode.createCell((int) 3).setCellValue(lvl["retrasados"]);
//                total += totalNode
//                totalSr += totalNodeSr
//
//            }
//
//            if (lvl["hijos"].size() > 0) {
//                def res = imprimeHijosXlsConsolidado(lvl, sheet, num, params, usuario, deps, puedeVer, total)
//                total += res[0]
//                totalSr += res[2]
//                num = res[1]
//            }
//        }
//        return [total, num, totalSr]
//    }


    def reporteRetrasadosArbolExcel () {


//        def desde = new Date().parse("dd-MM-yyyy HH:mm", params.desde + " 00:00")
//        def hasta = new Date().parse("dd-MM-yyyy HH:mm", params.hasta + " 23:59")

        def fileName = "documentos_retrasados_"
        def title = ["Reporte de documentos retrasados"]
        def title2 = ""

        def trams

        def totalResumenGenerado = 0
        def totalRecibido = 0
        def usuario = Persona.get(session.usuario.id)
        def departamentoUsuario = usuario?.departamento?.id

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
        cellTitle.setCellValue( '');
//        rowTitle = sheet.createRow((short) 4);
//        cellTitle = rowTitle.createCell((short) 0);
//        cellTitle.setCellValue("desde " + desde.format("dd-MM-yyyy") + " hasta " + hasta.format("dd-MM-yyyy"));

        def index = 6




        def sqlGen
        def sql
        def cn2 = dbConnectionService.getConnection()
        def cn = dbConnectionService.getConnection()
//        desde = desde.format("yyyy/MM/dd")
//        hasta = hasta.format("yyyy/MM/dd")

        def desdeNuevo = new Date().format("yyyy/MM/dd")
        def hastaNuevo = new Date().format("yyyy/MM/dd")


        def dptoPadre = Departamento.get(params.id)
        def dptosHijos = Departamento.findAllByPadreAndActivo(dptoPadre, 1).id

        def totalRetrasados = 0
        def totalNoRecibidos = 0
        def totalRetDpto = 0
        def totalRecDpto = 0



        if(dptosHijos.size() > 0 && params.id != '11'){

            XSSFRow rowHead4 = sheet.createRow((short) index);
            rowHead4.setHeightInPoints(14)

            Cell cell4 = rowHead4.createCell((int) 0)
            cell4.setCellValue("" + Departamento.get(params.id).descripcion)
            sheet.setColumnWidth(0, 13000)

            index++


        XSSFRow rowHead2 = sheet.createRow((short) index);
        rowHead2.setHeightInPoints(14)

        Cell cell2 = rowHead2.createCell((int) 0)
        cell2.setCellValue("Usuario")
        sheet.setColumnWidth(0, 13000)

        cell2 = rowHead2.createCell((int) 1)
        cell2.setCellValue("Perfil")
        sheet.setColumnWidth(1, 10000)

        cell2 = rowHead2.createCell((int) 2)
        cell2.setCellValue("Retrasados")
        sheet.setColumnWidth(2, 3000)

        cell2 = rowHead2.createCell((int) 3)
        cell2.setCellValue("No Recibidos")
        sheet.setColumnWidth(3, 3000)
        index++

            sqlGen = "select * from retrasados("+ params.id +"," + "'"  + desdeNuevo + "'" + "," +  "'" + hastaNuevo + "'" + ")"
            cn2.eachRow(sqlGen.toString()) {
                XSSFRow row3 = sheet.createRow((short) index)
                row3.createCell((int) 0).setCellValue("" + it?.usuario)
                row3.createCell((int) 1).setCellValue("" + (it?.perfil ?: ''))
                row3.createCell((int) 2).setCellValue(" " + it?.retrasados)
                row3.createCell((int) 3).setCellValue(" " + it?.no_recibidos)

//                totalRetrasados += it?.retrasados
//                totalNoRecibidos += it?.no_recibidos

                if(it?.perfil == 'RECEPCIÓN DE OFICINA'){
                    totalRetDpto = it?.retrasados
                    totalRecDpto = it?.no_recibidos

                }else{
                    totalRetrasados += it?.retrasados
                    totalNoRecibidos += it?.no_recibidos
                }

                index++
            }

            XSSFRow row3 = sheet.createRow((short) index)
            row3.createCell((int) 0).setCellValue("")
            row3.createCell((int) 1).setCellValue("Total")
            row3.createCell((int) 2).setCellValue(" " + (totalRetrasados + totalRetDpto))
            row3.createCell((int) 3).setCellValue("" + (totalNoRecibidos + totalRecDpto))

            index++

            dptosHijos.each{hij->


                totalRetrasados = 0
                totalNoRecibidos = 0
                totalRetDpto = 0
                totalRecDpto = 0



                XSSFRow rowHead5 = sheet.createRow((short) index);
                rowHead5.setHeightInPoints(14)

                Cell cell5 = rowHead5.createCell((int) 0)
                cell5.setCellValue("" + Departamento.get(hij).descripcion)
                sheet.setColumnWidth(0, 13000)

                index++


                XSSFRow rowHead = sheet.createRow((short) index);
                rowHead.setHeightInPoints(14)

                Cell cell = rowHead.createCell((int) 0)
                cell.setCellValue("Usuario")
                sheet.setColumnWidth(0, 13000)

                cell = rowHead.createCell((int) 1)
                cell.setCellValue("Perfil")
                sheet.setColumnWidth(1, 10000)

                cell = rowHead.createCell((int) 2)
                cell.setCellValue("Retrasados")
                sheet.setColumnWidth(2, 3000)

                cell = rowHead.createCell((int) 3)
                cell.setCellValue("No Recibidos")
                sheet.setColumnWidth(3, 3000)
                index++


                sqlGen = "select * from retrasados("+ hij +"," + "'"  + desdeNuevo + "'" + "," +  "'" + hastaNuevo + "'" + ")"
                cn2.eachRow(sqlGen.toString()) {
                    XSSFRow row2 = sheet.createRow((short) index)
                    row2.createCell((int) 0).setCellValue("" + it?.usuario)
                    row2.createCell((int) 1).setCellValue("" + (it?.perfil ?: ''))
                    row2.createCell((int) 2).setCellValue(" " + it?.retrasados)
                    row2.createCell((int) 3).setCellValue(" " + it?.no_recibidos)

//                    totalRetrasados += it?.retrasados
//                    totalNoRecibidos += it?.no_recibidos

                    if(it?.perfil == 'RECEPCIÓN DE OFICINA'){
                        totalRetDpto = it?.retrasados
                        totalRecDpto = it?.no_recibidos

                    }else{
                        totalRetrasados += it?.retrasados
                        totalNoRecibidos += it?.no_recibidos
                    }

                    index++
                }

                XSSFRow row2 = sheet.createRow((short) index)
                row2.createCell((int) 0).setCellValue("")
                row2.createCell((int) 1).setCellValue("Total")
                row2.createCell((int) 2).setCellValue(" " + (totalRetrasados + totalRetDpto))
                row2.createCell((int) 3).setCellValue("" + (totalNoRecibidos + totalRecDpto))
                index++
            }
        }else{

            XSSFRow rowHead5 = sheet.createRow((short) index);
            rowHead5.setHeightInPoints(14)

            Cell cell5 = rowHead5.createCell((int) 0)
            cell5.setCellValue("" + Departamento.get(params.id).descripcion)
            sheet.setColumnWidth(0, 13000)

            index++

            XSSFRow rowHead3 = sheet.createRow((short) index);
            rowHead3.setHeightInPoints(14)

            Cell cell3 = rowHead3.createCell((int) 0)
            cell3.setCellValue("Usuario")
            sheet.setColumnWidth(0, 13000)

            cell3 = rowHead3.createCell((int) 1)
            cell3.setCellValue("Perfil")
            sheet.setColumnWidth(1, 10000)

            cell3 = rowHead3.createCell((int) 2)
            cell3.setCellValue("Retrasados")
            sheet.setColumnWidth(2, 3000)

            cell3 = rowHead3.createCell((int) 3)
            cell3.setCellValue("No Recibidos")
            sheet.setColumnWidth(3, 3000)
            index++


            sqlGen = "select * from retrasados("+ params.id +"," + "'"  + desdeNuevo + "'" + "," +  "'" + hastaNuevo + "'" + ")"
            cn2.eachRow(sqlGen.toString()) {
                XSSFRow row2 = sheet.createRow((short) index)
                row2.createCell((int) 0).setCellValue("" + it?.usuario)
                row2.createCell((int) 1).setCellValue("" + (it?.perfil ?: ''))
                row2.createCell((int) 2).setCellValue(" " + it?.retrasados)
                row2.createCell((int) 3).setCellValue(" " + it?.no_recibidos)
//
//                totalRetrasados += it?.retrasados
//                totalNoRecibidos += it?.no_recibidos

                if(it?.perfil == 'RECEPCIÓN DE OFICINA'){
                    totalRetDpto = it?.retrasados
                    totalRecDpto = it?.no_recibidos
                }else{
                    totalRetrasados += it?.retrasados
                    totalNoRecibidos += it?.no_recibidos
                }

                index++
            }

            XSSFRow row2 = sheet.createRow((short) index)
            row2.createCell((int) 0).setCellValue("")
            row2.createCell((int) 1).setCellValue("Total")
            row2.createCell((int) 2).setCellValue(" " + (totalRetrasados + totalRetDpto))
            row2.createCell((int) 3).setCellValue("" + (totalNoRecibidos + totalRecDpto))
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



    def reporteGeneradosArbolExcel () {



        def desde = new Date().parse("dd-MM-yyyy HH:mm", params.desde + " 00:00")
        def hasta = new Date().parse("dd-MM-yyyy HH:mm", params.hasta + " 23:59")

        def fileName = "documentos_generados_"
        def title = ["Reporte de documentos generados"]
        def title2 = ""

        def trams

        def totalResumenGenerado = 0
        def totalRecibido = 0
        def usuario = Persona.get(session.usuario.id)
        def departamentoUsuario = usuario?.departamento?.id

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
        cellTitle.setCellValue( usuario?.nombre + usuario?.apellido );
        rowTitle = sheet.createRow((short) 4);
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue("desde " + desde.format("dd-MM-yyyy") + " hasta " + hasta.format("dd-MM-yyyy"));

        def index = 6
        XSSFRow rowHead = sheet.createRow((short) index);
        rowHead.setHeightInPoints(14)

        Cell cell = rowHead.createCell((int) 0)
        cell.setCellValue("Usuario")
        sheet.setColumnWidth(0, 13000)

        cell = rowHead.createCell((int) 1)
        cell.setCellValue("Perfil")
        sheet.setColumnWidth(1, 10000)

        cell = rowHead.createCell((int) 2)
        cell.setCellValue("Generados")
        sheet.setColumnWidth(2, 3000)

        cell = rowHead.createCell((int) 3)
        cell.setCellValue("Enviados")
        sheet.setColumnWidth(3, 3000)

        cell = rowHead.createCell((int) 4)
        cell.setCellValue("Recibidos")
        sheet.setColumnWidth(3, 3000)
        index++


        def sqlGen
        def sql
        def cn2 = dbConnectionService.getConnection()
        def cn = dbConnectionService.getConnection()
        desde = desde.format("yyyy/MM/dd")
        hasta = hasta.format("yyyy/MM/dd")


        sqlGen = "select * from retrasados("+ params.id +"," + "'"  + desde + "'" + "," +  "'" + hasta + "'" + ")"
        cn2.eachRow(sqlGen.toString()) {
            XSSFRow row2 = sheet.createRow((short) index)
            row2.createCell((int) 0).setCellValue("" + it?.usuario)
            row2.createCell((int) 1).setCellValue("" + (it?.perfil ?: ''))
            row2.createCell((int) 2).setCellValue(" " + it?.generados)
            row2.createCell((int) 3).setCellValue(" " + it?.enviados)
            row2.createCell((int) 4).setCellValue(" " + it?.recibidos)
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


}
