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
        row.createCell((int) 0).setCellValue("Tipo")
        row.createCell((int) 1).setCellValue("Nro.")
        row.createCell((int) 2).setCellValue("F. Creación")
        row.createCell((int) 3).setCellValue("De")
        row.createCell((int) 4).setCellValue("Creado por")
        row.createCell((int) 5).setCellValue("Para")
        row.createCell((int) 6).setCellValue("F. Envío")
        row.createCell((int) 7).setCellValue("F. Recepción")
        row.createCell((int) 8).setCellValue("F. Límite")
        row.createCell((int) 9).setCellValue("Retraso (días)")
        return num + 1
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
        def jefe = params.jefe == '1'
        def ttl = ""
        def results = []

        if (params.dpto) {
            def dep = Departamento.get(params.dpto.toLong())
            ttl += "\ndel dpto. $dep.descripcion ($dep.codigo)"
            results = reportesTramitesRetrasadosService.datos(params.dpto).res
        } else if (params.prsn) {
            def per = Persona.get(params.prsn.toLong())
            ttl += "\ndel usuario $per.nombre $per.apellido ($per.login)"
            if (per.esTrianguloOff()) {
                ttl += "\n[Bandeja de entrada del departamento]"
                results = reportesTramitesRetrasadosService.datos(per.departamentoId, params.prsn).res
            } else {
                results = reportesTramitesRetrasadosService.datosPersona(params.prsn).res
            }
        }

        def path = servletContext.getRealPath("/") + "xls/"
        new File(path).mkdirs()
        //esto crea un archivo temporal que puede ser siempre el mismo para no ocupar espacio
        String filename = path + "text.xlsx";
        def name = "reporteTramitesRetrasados_" + new Date().format("ddMMyyyy_hhmm") + ".xlsx";
        String sheetName = "SAD-WEB Reporte";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName);
        CreationHelper createHelper = wb.getCreationHelper();

        sheet.setAutobreaks(true);
        XSSFRow rowHead = sheet.createRow((short) 0);
        rowHead.setHeightInPoints(14)
        sheet.setColumnWidth(0, 4000)
        sheet.setColumnWidth(1, 4000)
        sheet.setColumnWidth(2, 4000)
        sheet.setColumnWidth(3, 2000)
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
        rowHead.createCell((int) 1).setCellValue("Reporte detallado de Trámites Retrasados y sin recepción")
        rowHead = sheet.createRow((short) 3);
        rowHead.createCell((int) 1).setCellValue(ttl)
        rowHead = sheet.createRow((short) 4);
        rowHead.createCell((int) 1).setCellValue("" + new Date().format('dd-MM-yyyy HH:mm'))
        def num = 6

        results.each { k, v ->
            num = creaRegistros(sheet, k, v, num, jefe)
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

    def reporteRetrasadosConsolidado() {
        def jefe = params.jefe == '1'
        def ttl = ""
        def results = []

        if (params.dpto) {
            def dep = Departamento.get(params.dpto.toLong())
            ttl += "\ndel dpto. $dep.descripcion ($dep.codigo)"
            results = reportesTramitesRetrasadosService.datos(params.dpto).res
        } else if (params.prsn) {
            def per = Persona.get(params.prsn.toLong())
            ttl += "\ndel usuario $per.nombre $per.apellido ($per.login)"
            if (per.esTrianguloOff()) {
                ttl += "\n[Bandeja de entrada del departamento]"
                results = reportesTramitesRetrasadosService.datos(per.departamentoId, params.prsn).res
            } else {
                results = reportesTramitesRetrasadosService.datosPersona(params.prsn).res
            }
        }
        def path = servletContext.getRealPath("/") + "xls/"
        new File(path).mkdirs()
        //esto crea un archivo temporal que puede ser siempre el mismo para no ocupar espacio
        String filename = path + "text.xlsx";
        def name = "reporteConsolidadoTramitesRetrasados_" + new Date().format("ddMMyyyy_hhmm") + ".xlsx";
        String sheetName = "SAD-WEB Reporte";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName);
        CreationHelper createHelper = wb.getCreationHelper();

        sheet.setAutobreaks(true);
        XSSFRow rowHead = sheet.createRow((short) 0);
        rowHead.setHeightInPoints(14)
        sheet.setColumnWidth(0, 5000)
        sheet.setColumnWidth(1, 15000)
        rowHead.createCell((int) 1).setCellValue("GAD DE LA PROVINCIA DE PICHINCHA")
        rowHead = sheet.createRow((short) 1);
        rowHead.createCell((int) 1).setCellValue("SISTEMA DE ADMINISTRACION DOCUMENTAL")
        rowHead = sheet.createRow((short) 2);
        rowHead.createCell((int) 1).setCellValue("Reporte consolidado de Trámites Retrasados y sin recepción")
        rowHead = sheet.createRow((short) 3);
        rowHead.createCell((int) 1).setCellValue(ttl)
        rowHead = sheet.createRow((short) 4);
        rowHead.createCell((int) 1).setCellValue("" + new Date().format('dd-MM-yyyy HH:mm'))

        def rowNode = sheet.createRow((short) 5);
        rowNode.createCell((int) 2).setCellValue("Retrasados");
        rowNode.createCell((int) 3).setCellValue("Sin recepción");

        def num = 7

        results.each { k, v ->
            num = creaRegistrosConsolidado(sheet, k, v, num, jefe)
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
}
