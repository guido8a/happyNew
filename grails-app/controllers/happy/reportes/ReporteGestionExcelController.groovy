package happy.reportes

import groovy.time.TimeCategory
import happy.seguridad.Persona
import happy.seguridad.Shield
import happy.tramites.Departamento
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class ReporteGestionExcelController extends Shield {

    def diasLaborablesService
    def reportesPdfService
    def dbConnectionService

    def reporteGestion() {
        def desde = new Date().parse("dd-MM-yyyy", params.desde)
        def hasta = new Date().parse("dd-MM-yyyy", params.hasta)
        def departamento = Departamento.get(params.id)
        def departamentos = [departamento]

        desde = desde.format("yyyy/MM/dd")
        hasta = hasta.format("yyyy/MM/dd")

        def fileName = "gestion_" + departamento?.codigo
        def titulo = "Reporte de gestión de trámites del dpto. ${departamento?.descripcion} del ${params.desde} al ${params.hasta}"

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
        cellTitle.setCellValue(titulo);
        rowTitle = sheet.createRow((short) 3);
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue("Reporte generado el " + new Date().format("dd-MM-yyyy HH:mm"));
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue("Nota: El tiempo en días corresponde a una jornada de trabajo diaria");

        def tramiteitor = reportesPdfService.reporteGestion(desde, hasta, departamento.id)

//        if (departamento) {

            def wFechas = 5000
            def wTiempos = 6000

            sheet.setColumnWidth(0, 6000)
            sheet.setColumnWidth(1, 6000)
            sheet.setColumnWidth(2, wFechas)
            sheet.setColumnWidth(3, wFechas)
            sheet.setColumnWidth(4, 6000)
            sheet.setColumnWidth(5, wFechas)
            sheet.setColumnWidth(6, wTiempos)
            sheet.setColumnWidth(7, 8000)
            sheet.setColumnWidth(8, 15000)
            sheet.setColumnWidth(9, 8000)
            sheet.setColumnWidth(10, wTiempos)

            def i = 6

            def tramitesPrincipales = []

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

//            println("tramites " + tramitesPrincipales)

//            tramitesPrincipales.each { principal ->
//                def pdts = PersonaDocumentoTramite.withCriteria {
//                    eq("tramite", principal)
//                    or {
//                        eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R001'))
//                        eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R002'))
//                    }

//                }

                def j = 0
                XSSFRow row = sheet.createRow((short) i);
                Cell cell = row.createCell((short) j);
//                cell.setCellValue("DOC PRINCIPAL: ");
//                j += 2
//                cell = row.createCell((short) j);
//                cell.setCellValue(principal.codigo);
//                j += 2
//                cell = row.createCell((short) j);
//                cell.setCellValue("ASUNTO: ");
//                j++
//                cell = row.createCell((short) j);
//                cell.setCellValue(principal.asunto);
//
//                i++

                j = 0
                row = sheet.createRow((short) i);
                cell = row.createCell((short) j);
                cell.setCellValue("Trámite Principal");
                j++
                cell = row.createCell((short) j);
                cell.setCellValue("Trámite n°");
                j++
                cell = row.createCell((short) j);
                cell.setCellValue("F. creación");
                j++
                cell = row.createCell((short) j);
                cell.setCellValue("F. envío");
                j++
                cell = row.createCell((short) j);
                cell.setCellValue("T. creacion-envio");
                j++
                cell = row.createCell((short) j);
                cell.setCellValue("F. recepción");
                j++
                cell = row.createCell((short) j);
                cell.setCellValue("T. envío - recepción");
                j++
                cell = row.createCell((short) j);
                cell.setCellValue("De");
                j++
                cell = row.createCell((short) j);
                cell.setCellValue("Asunto");
                j++
                cell = row.createCell((short) j);
                cell.setCellValue("Para");
                j++
                cell = row.createCell((short) j);
                cell.setCellValue("T. recepción - respuesta");
                i++

                tramiteitor.each {
                    llenaTablaGestionExcel(sheet,it,i)
                    i++
                    }




//                pdts.each { prtr ->
//                        i = rowTramite(prtr, sheet, i)
//                    i = llenaTablaTramite(prtr, departamentos, sheet, i)
//                }
                i += 2
//            }
//        }

        FileOutputStream fileOut = new FileOutputStream(filename);
        wb.write(fileOut);
        fileOut.close();
        String disHeader = "Attachment;Filename=\"${downloadName}\"";
        response.setHeader("Content-Disposition", disHeader);
        File desktopFile = new File(filename);
        PrintWriter pw = response.getWriter();
        FileInputStream fileInputStream = new FileInputStream(desktopFile);
        int jt;

        while ((jt = fileInputStream.read()) != -1) {
            pw.write(jt);
        }
        fileInputStream.close();
        response.flushBuffer();
        pw.flush();
        pw.close();
    }

    def llenaTablaGestionExcel ( XSSFSheet sheet, it, int i) {

        def j = 0
        def row = sheet.createRow((short) i);
        def cell = row.createCell((short) j);
        cell.setCellValue(it.trmtpdre ?: it.trmtcdgo);
        j++
        cell = row.createCell((short) j);
        cell.setCellValue(it?.trmtcdgo);
        j++
        cell = row.createCell((short) j);
        cell.setCellValue(it?.trmtfccr ? it?.trmtfccr?.format('dd-MM-yyyy HH:mm') : "");
        j++
        cell = row.createCell((short) j);
        cell.setCellValue(it?.trmtfcen  ? it?.trmtfcen?.format('dd-MM-yyyy HH:mm') : "");
        j++
        cell = row.createCell((short) j);
        cell.setCellValue(it?.trmttmce);
        j++
        cell = row.createCell((short) j);
        cell.setCellValue(it?.trmtfcrc ? it?.trmtfcrc?.format('dd-MM-yyyy HH:mm') : "");
        j++
        cell = row.createCell((short) j);
        cell.setCellValue(it?.trmttmer);
        j++
        cell = row.createCell((short) j);
        cell.setCellValue(it?.trmt__de);
        j++
        cell = row.createCell((short) j);
        cell.setCellValue(it?.trmtasnt);
        j++
        cell = row.createCell((short) j);
        cell.setCellValue(it?.trmtpara);
        j++
        cell = row.createCell((short) j);
        cell.setCellValue(it?.trmttmrr);
        /* ********************************************************************************************* */
        i++

    }


    def rowTramite(PersonaDocumentoTramite pdt, XSSFSheet sheet, int i) {
        def tramite = pdt.tramite

//        println "rowTRamite    " + tramite.codigo + "       i=" + i

        def de, dias, para = "", codigo = tramite.codigo
        if (tramite.deDepartamento) {
            de = tramite.deDepartamento.codigo
        } else {
            de = tramite.de.login + " (${tramite.de.departamento.codigo})"
        }

        def dif
        if (pdt.fechaEnvio) {
            def pruebasInicio = new Date()
            def pruebasFin

            if (pdt.fechaRecepcion) {
                dif = diasLaborablesService.tiempoLaborableEntre(pdt.fechaRecepcion, pdt.fechaEnvio)
                pruebasFin = new Date()
                println "tiempo ejecución tiempoLaborableEntre if: ${TimeCategory.minus(pruebasFin, pruebasInicio)}"
            } else {
                dif = diasLaborablesService.tiempoLaborableEntre(pdt.fechaEnvio, new Date())
                pruebasFin = new Date()
                println "tiempo ejecución tiempoLaborableEntre else: ${TimeCategory.minus(pruebasFin, pruebasInicio)}"
            }
//            println("diffff " + dif)
            if (dif[0]) {
                def d = dif[1]
                if (d.dias > 0) {
                    dias = "${d.dias} día${d.dias == 1 ? '' : 's'}, "
                } else {
                    dias = ""
                }
                dias += "${d.horas} hora${d.horas == 1 ? '' : 's'}, ${d.minutos} minuto${d.minutos == 1 ? '' : 's'}"
            } else {
                println "error: 1 " + dif
            }
        } else {
            dias = "No enviado"
        }

        ////
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
                dif2 = diasLaborablesService.tiempoLaborableEntre(pdt.fechaRecepcion, respuesta.fechaCreacion)
            }
        } else {
            if (pdt.fechaRecepcion) {
                dif2 = diasLaborablesService.tiempoLaborableEntre(pdt.fechaRecepcion, new Date())
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
                println "error: 2 " + dif2
            }
        } else {
            contestacionRetraso = "No recibido"
        }

        /* ********************************************************************************************* */
        def j = 0
        def row = sheet.createRow((short) i);
        def cell = row.createCell((short) j);
        cell.setCellValue(codigo);
        j++
        cell = row.createCell((short) j);
        cell.setCellValue(tramite.fechaCreacion ? tramite.fechaCreacion.format('dd-MM-yyyy HH:mm') : "");
        j++
        cell = row.createCell((short) j);
        cell.setCellValue(pdt.fechaEnvio ? pdt.fechaEnvio.format('dd-MM-yyyy HH:mm') : "");
        j++
        cell = row.createCell((short) j);
        cell.setCellValue(pdt.fechaRecepcion ? pdt.fechaRecepcion.format('dd-MM-yyyy HH:mm') : "");
        j++
        cell = row.createCell((short) j);
        cell.setCellValue(dias);
        j++
        cell = row.createCell((short) j);
        cell.setCellValue(de);
        j++
        cell = row.createCell((short) j);
        cell.setCellValue(tramite.asunto);
        j++
        cell = row.createCell((short) j);
        cell.setCellValue(para);
        j++
        cell = row.createCell((short) j);
        cell.setCellValue(contestacionRetraso);
        /* ********************************************************************************************* */
        i++
        return i
    }

    def llenaTablaTramite(PersonaDocumentoTramite prtr, departamentos, XSSFSheet sheet, int i) {
        def respuestas = Tramite.withCriteria {
            eq("aQuienContesta", prtr)
            order("fechaCreacion", "asc")
        }

//        def respuestas = Tramite.findAll("from Tramite where aQuienContesta = ${prtr.id} order by fechaCreacion")

//        println("respuestas " + respuestas)
        if (respuestas.size() > 0) {
            respuestas.each { h ->
                def rolPara = RolPersonaTramite.findByCodigo("R001")
                def rolCc = RolPersonaTramite.findByCodigo("R002")

                def paras = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(h, rolPara)
                def ccs = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(h, rolCc)
                (paras + ccs).each { pdt ->

//                    if ((departamentos.id).contains(pdt.departamentoId) || (departamentos.id).contains(pdt.persona?.departamentoId) ||
//                            (departamentos.id).contains(pdt.tramite.departamentoId)) {
                        i = rowTramite(pdt, sheet, i)
//                    }
                    i = llenaTablaTramite(pdt, departamentos, sheet, i)
                }
            }
        }
        return i
    }


    def reporteTiempoRespuesta () {

//        println("params " + params)

        def desde = new Date().parse("dd-MM-yyyy", params.desde)
        def hasta = new Date().parse("dd-MM-yyyy", params.hasta)

        desde = desde.format("yyyy/MM/dd")
        hasta = hasta.format("yyyy/MM/dd")

        def departamento = Departamento.get(params.id)


        def sql
        def cn = dbConnectionService.getConnection()
        sql = "select * from rp_tiempos ("+ "'" + desde + "'" + "," +  "'" + hasta + "'" + "," + params.id + "," + null +")"

//        println("sql " + sql)

        def fileName = "tiempo_respuesta"
        def titulo = "REPORTE DE TIEMPOS DE RESPUESTA"

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
        cellTitle.setCellValue(titulo);
        rowTitle = sheet.createRow((short) 3);
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue("Desde ${params.desde} hasta ${params.hasta}");
        rowTitle = sheet.createRow((short) 4);
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue("Oficina: ${departamento?.descripcion}" );


        sheet.setColumnWidth(0, 8000)
        sheet.setColumnWidth(1, 5000)
        sheet.setColumnWidth(2, 5000)
        sheet.setColumnWidth(3, 5000)
        sheet.setColumnWidth(4, 5000)
        sheet.setColumnWidth(5, 5000)


        def i = 6
        def j = 0
        def totales
        XSSFRow row = sheet.createRow((short) i);
        Cell cell = row.createCell((short) j);

        j = 0
        row = sheet.createRow((short) i);
        cell = row.createCell((short) j);
        cell.setCellValue("Usuario");
        j++
        cell = row.createCell((short) j);
        cell.setCellValue("De 0 a 5 días");
        j++
        cell = row.createCell((short) j);
        cell.setCellValue("De 6 a 15 días");
        j++
        cell = row.createCell((short) j);
        cell.setCellValue("De 16 a 50 días");
        j++
        cell = row.createCell((short) j);
        cell.setCellValue("Más de 50 días");
        j++
        cell = row.createCell((short) j);
        cell.setCellValue("Total");
        j++
        i++

        cn.eachRow(sql.toString()){
            totales = 0
            XSSFRow row2 = sheet.createRow((short) i)
            row2.createCell((int) 0).setCellValue(it?.prsn)
            row2.createCell((int) 1).setCellValue(it?.tmpo0005)
            row2.createCell((int) 2).setCellValue(it?.tmpo0615)
            row2.createCell((int) 3).setCellValue(it?.tmpo1650)
            row2.createCell((int) 4).setCellValue(it?.tmpo50dd)
            totales = it?.tmpo0005 + it?.tmpo0615 + it?.tmpo1650 + it?.tmpo50dd
            row2.createCell((int) 5).setCellValue(totales)

            i++
        }

        FileOutputStream fileOut = new FileOutputStream(filename);
        wb.write(fileOut);
        fileOut.close();
        String disHeader = "Attachment;Filename=\"${downloadName}\"";
        response.setHeader("Content-Disposition", disHeader);
        File desktopFile = new File(filename);
        PrintWriter pw = response.getWriter();
        FileInputStream fileInputStream = new FileInputStream(desktopFile);
        int jt;

        while ((jt = fileInputStream.read()) != -1) {
            pw.write(jt);
        }
        fileInputStream.close();
        response.flushBuffer();
        pw.flush();
        pw.close();

    }


    def reporteTiempoRespuestaUsuario () {


//        println("params " + params)

        def desde = new Date().parse("dd-MM-yyyy", params.desde)
        def hasta = new Date().parse("dd-MM-yyyy", params.hasta)

        desde = desde.format("yyyy/MM/dd")
        hasta = hasta.format("yyyy/MM/dd")

        def usuario = Persona.get(params.id)


        def sql
        def cn = dbConnectionService.getConnection()
        sql = "select * from rp_tiempos ("+ "'" + desde + "'" + "," +  "'" + hasta + "'" + "," + null + "," + params.id +")"

//        println("sql " + sql)

        def fileName = "tiempo_respuesta"
        def titulo = "REPORTE DE TIEMPOS DE RESPUESTA"

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
        cellTitle.setCellValue(titulo);
        rowTitle = sheet.createRow((short) 3);
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue("Desde ${params.desde} hasta ${params.hasta}");
        rowTitle = sheet.createRow((short) 4);
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue("Oficina: ${usuario?.departamento?.descripcion}" );
        rowTitle = sheet.createRow((short) 5);
        cellTitle = rowTitle.createCell((short) 0);
        cellTitle.setCellValue("Usuario: ${usuario?.nombre + " " + usuario?.apellido}" );


        sheet.setColumnWidth(0, 8000)
        sheet.setColumnWidth(1, 8000)
        sheet.setColumnWidth(2, 5000)
        sheet.setColumnWidth(3, 5000)
        sheet.setColumnWidth(4, 5000)
        sheet.setColumnWidth(5, 5000)


        def i = 7
        def j = 0
        def totales

        XSSFRow row = sheet.createRow((short) i);
        Cell cell = row.createCell((short) j);

        j = 0
        row = sheet.createRow((short) i);
        cell = row.createCell((short) j);
        cell.setCellValue("Tiempo de Respuesta");
        j++
        cell = row.createCell((short) j);
        cell.setCellValue("Total de contestados");
        j++
        i++



        def contador = 0
        def arre
        cn.eachRow(sql.toString()){

            XSSFRow row2 = sheet.createRow((short) i);
            Cell cell2 = row2.createCell((short) j);
            j = 0
            row2 = sheet.createRow((short) i);
            cell2 = row2.createCell((short) j);
            cell2.setCellValue("DE 0 A 5 DÍAS");
            j++
            cell2 = row2.createCell((short) j);
            cell2.setCellValue(it?.tmpo0005);
            i++

            XSSFRow row3 = sheet.createRow((short) i);
            Cell cell3 = row3.createCell((short) j);
            j = 0
            row3 = sheet.createRow((short) i);
            cell3 = row3.createCell((short) j);
            cell3.setCellValue("DE 6 A 15 DIAS");
            j++
            cell3 = row3.createCell((short) j);
            cell3.setCellValue(it?.tmpo0615);
            i++

            XSSFRow row4 = sheet.createRow((short) i);
            Cell cell4 = row4.createCell((short) j);
            j = 0
            row4 = sheet.createRow((short) i);
            cell4 = row4.createCell((short) j);
            cell4.setCellValue("DE 16 A 50 DÍAS");
            j++
            cell4 = row4.createCell((short) j);
            cell4.setCellValue(it?.tmpo1650);
            i++

            XSSFRow row5 = sheet.createRow((short) i);
            Cell cell5 = row5.createCell((short) j);
            j = 0
            row5 = sheet.createRow((short) i);
            cell5 = row5.createCell((short) j);
            cell5.setCellValue("MAS DE 50 DÍAS");
            j++
            cell5 = row5.createCell((short) j);
            cell5.setCellValue(it?.tmpo50dd);
            i++


            totales = it?.tmpo0005 + it?.tmpo0615 + it?.tmpo1650 + it?.tmpo50dd

            XSSFRow row6 = sheet.createRow((short) i);
            Cell cell6 = row6.createCell((short) j);
            j = 0
            row6 = sheet.createRow((short) i);
            cell6 = row6.createCell((short) j);
            cell6.setCellValue("TOTAL");
            j++
            cell6 = row6.createCell((short) j);
            cell6.setCellValue(totales);
            i++

        }

        FileOutputStream fileOut = new FileOutputStream(filename);
        wb.write(fileOut);
        fileOut.close();
        String disHeader = "Attachment;Filename=\"${downloadName}\"";
        response.setHeader("Content-Disposition", disHeader);
        File desktopFile = new File(filename);
        PrintWriter pw = response.getWriter();
        FileInputStream fileInputStream = new FileInputStream(desktopFile);
        int jt;

        while ((jt = fileInputStream.read()) != -1) {
            pw.write(jt);
        }
        fileInputStream.close();
        response.flushBuffer();
        pw.flush();
        pw.close();


    }


}
