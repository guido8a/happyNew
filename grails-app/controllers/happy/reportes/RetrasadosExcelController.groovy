package happy.reportes

import happy.seguridad.Persona
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

class RetrasadosExcelController {






    def reporteRetrasadosDetalle(){
//        params.detalle=1
//        params.prsn=session.usuario.id
        println "detallado aaa    "+params
        def estadoR= EstadoTramite.findByCodigo("E004")
        def estadoE= EstadoTramite.findByCodigo("E003")
        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCopia = RolPersonaTramite.findByCodigo("R002")
        def now = new Date()
        now=now.plus(2)

        def datos = [:]
        def usuario = null
        def deps = []
        def puedeVer = []
        def extraPersona ="and "
        if(params.prsn){
            usuario = Persona.get(params.prsn)
            extraPersona+="persona="+usuario.id+" "
            if(usuario.esTriangulo)
                extraPersona="and (persona=${usuario.id} or departamento = ${usuario.departamento.id})"
            def padre = usuario.departamento.padre
            while(padre){
                deps.add(padre)
                padre=padre.padre
            }
            deps.add(usuario.departamento)
            puedeVer.add(usuario.departamento)
            def hi = Departamento.findAllByPadre(usuario.departamento)
            while(hi.size()>0){
                puedeVer +=hi
                hi=Departamento.findAllByPadreInList(hi)
            }

        }
        if(params.dpto){
            def departamento = Departamento.get(params.dpto)
            println "DPTO "+departamento.codigo+"  "+departamento.descripcion
            def padre = departamento.padre
            while(padre){
                deps.add(padre)
                padre=padre.padre
            }
            deps.add(departamento)
            puedeVer.add(departamento)
            def hi = Departamento.findAllByPadre(departamento)
            while(hi.size()>0){
                puedeVer +=hi
                hi=Departamento.findAllByPadreInList(hi)
            }
        }
//        println "deps "+deps+"  puede ver  "+puedeVer
        def tramites = Tramite.findAll("from Tramite where externo!='1' or externo is null")
        tramites.each {t->
            def pdt = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite where tramite=${t.id} and fechaEnvio is not null and rolPersonaTramite in (${rolPara.id},${rolCopia.id}) and estado in (${estadoR.id},${estadoE.id}) ${usuario?extraPersona:''} ")
            if(pdt){
                pdt.each {pd->
                    def resp = Tramite.findAllByAQuienContesta(pd)
                    if(resp.size()==0){
                        if(pd.fechaLimite<now || (!pd.fechaRecepcion))
                            datos=jerarquia(datos,pd)
                    }

                }
            }

        }


        def hijos = datos["hijos"]


//        hijos.each{lvl->
////            println "hijo ${lvl['objeto']}  ${lvl['objeto'].id}   "+puedeVer.id
//            if(puedeVer.size()==0 || (puedeVer.id.contains(lvl["objeto"].id))){
////            println "desp "+deps+"   "+lvl["objeto"]+"   "+(deps.id.contains(lvl["objeto"].id))
//
//
//                def par = new Paragraph("-"+lvl["objeto"], times12bold)
////                par.setIndentationLeft((lvl["nivel"]-1)*20)
//                document.add(par)
//                def par2= new Paragraph("", times8normal)
//                par2.setSpacingBefore(4)
//                def par3= new Paragraph("", times8normal)
//                par3.setSpacingBefore(4)
////                println "wtf "+lvl["triangulos"]
//
//                if(lvl["tramites"].size()>0){
////                        par = new Paragraph("Trámites:", times10bold)
////                        par.setIndentationLeft(lvl["nivel"]*20+10)
////                        document.add(par)
//                    lvl["triangulos"].each{t->
//                        par = new Paragraph("Usuario: ${t.departamento.codigo}:"+t+" - ${t.login} (oficina) - [ Sin Recepción: "+lvl["retrasados"]+" , Retrasados: ${lvl['rezagados']} ]", times8bold)
////                        par.setIndentationLeft((lvl["nivel"]-1)*20+10)
//                        document.add(par)
//                    }
//
//                }
//                if(params.detalle){
//                    tablaTramites = new PdfPTable(7);
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
////                    par = new Paragraph("Para", times8bold)
////                    cell = new PdfPCell(par);
////                    tablaTramites.addCell(cell);
//                    par = new Paragraph("F. Envío", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("F. Recepcíon", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("F. Límite", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    lvl["tramites"].each{t->
////                        par2.setIndentationLeft((lvl["nivel"]-1)*20+10)
//                        par = new Paragraph("${t.tramite.codigo} ${t.rolPersonaTramite.codigo=='R002'?'[CC]':''}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("${t.tramite.fechaCreacion.format('dd-MM-yyyy HH:mm')}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        if(t.tramite.deDepartamento){
//                            par = new Paragraph("${t.tramite.deDepartamento.codigo}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                        }else{
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
//                        par = new Paragraph("${(t.fechaRecepcion)?t.fechaRecepcion?.format('dd-MM-yyyy hh:mm'):''}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("${(t.fechaLimiteRespuesta)?t.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm'):''}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//
//                    }
//                    if(lvl["tramites"].size()>0){
//                        par2.add(tablaTramites)
//                        document.add(par2)
//                    }
//                }
////                    if(lvl["personas"].size()>0){
////                        par = new Paragraph("Usuarios:", times10bold)
////                        par.setIndentationLeft(lvl["nivel"]*20+10)
////                        document.add(par)
////
////                    }
//                lvl["personas"].each{p->
////                println "\t\t "+p["objeto"]+ "  "+  p["objeto"].departamento
//                    par3=null
//                    par3= new Paragraph("", times8normal)
//                    par3.setSpacingBefore(4)
//                    par = new Paragraph("Usuario: ${p[ "objeto"].departamento.codigo}:"+p["objeto"]+" - ${p['objeto'].login} - [ Sin Recepción: "+p["retrasados"]+" , Retrasados: ${p['rezagados']} ]", times8bold)
////                    par.setIndentationLeft((lvl["nivel"]-1)*20+20)
//                    document.add(par)
////                    par3.setIndentationLeft((lvl["nivel"]-1)*20+20)
//
//                    if(params.detalle){
//                        tablaTramites = new PdfPTable(7);
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
//                        p["tramites"].each{t->
////                            par2.setIndentationLeft((lvl["nivel"]-1)*20+10)
//                            par = new Paragraph("${t.tramite.codigo} ${t.rolPersonaTramite.codigo=='R002'?'[CC]':''}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                            par = new Paragraph("${t.tramite.fechaCreacion.format('dd-MM-yyyy HH:mm')}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                            if(t.tramite.deDepartamento){
//                                par = new Paragraph("${t.tramite.deDepartamento.codigo}", times8normal)
//                                cell = new PdfPCell(par);
//                                tablaTramites.addCell(cell);
//                            }else{
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
//                            par = new Paragraph("${(t.fechaRecepcion)?t.fechaRecepcion?.format('dd-MM-yyyy hh:mm'):''}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                            par = new Paragraph("${(t.fechaLimiteRespuesta)?t.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm'):''}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                        }
//                        if(p["tramites"].size()>0){
//                            par3.add(tablaTramites)
//                            document.add(par3)
//                        }
//                    }
//                }
//
//
//
//            }
//            imprimeHijosPdf(lvl,document,tablaTramites,params,usuario,deps,puedeVer)
//
//        }

//        return  [tramites:tramites,datos:datos]
    }




//    def imprimeHijosPdf(arr,contenido,tablaTramites,params,usuario,deps,puedeVer){
//        def datos = arr["hijos"]
//        datos.each{lvl->
////            println  "\t "+lvl["objeto"]
////            println "\t\t Tramites:"
////            println "hijo funcion ${lvl['objeto']} "+lvl["objeto"].id+"    "+puedeVer.id
//
//            if(puedeVer.size()==0 || (puedeVer.id.contains(lvl["objeto"].id))){
//                def par = new Paragraph("-"+lvl["objeto"], times12bold)
////                par.setIndentationLeft((lvl["nivel"]-1)*20)
//                contenido.add(par)
//                def par2= new Paragraph("", times8normal)
//                par2.setSpacingBefore(4)
//                def par3= new Paragraph("", times8normal)
//                par3.setSpacingBefore(4)
//
//                if(lvl["tramites"].size()>0){
//                    lvl["triangulos"].each{t->
//                        par = new Paragraph("Usuario: ${t.departamento.codigo}:"+t+" - ${t.login} (oficina) - [ Sin Recepción: "+lvl["retrasados"]+" , Retrasados: ${lvl['rezagados']} ]", times8bold)
////                        par.setIndentationLeft((lvl["nivel"]-1)*20+10)
//                        contenido.add(par)
//                    }
//
//                }
//                if(params.detalle){
//                    tablaTramites = new PdfPTable(7);
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
////                    par = new Paragraph("Para", times8bold)
////                    cell = new PdfPCell(par);
////                    tablaTramites.addCell(cell);
//                    par = new Paragraph("F. Envío", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("F. Recepcíon", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    par = new Paragraph("F. Límite", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
//                    lvl["tramites"].each{t->
////                        par2.setIndentationLeft((lvl["nivel"]-1)*20+10)
//                        par = new Paragraph("${t.tramite.codigo} ${t.rolPersonaTramite.codigo=='R002'?'[CC]':''}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("${t.tramite.fechaCreacion.format('dd-MM-yyyy HH:mm')}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        if(t.tramite.deDepartamento){
//                            par = new Paragraph("${t.tramite.deDepartamento.codigo}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                        }else{
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
//                        par = new Paragraph("${(t.fechaRecepcion)?t.fechaRecepcion?.format('dd-MM-yyyy hh:mm'):''}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//                        par = new Paragraph("${(t.fechaLimiteRespuesta)?t.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm'):''}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
//
//                    }
//                    if(lvl["tramites"].size()>0){
//                        par2.add(tablaTramites)
//                        contenido.add(par2)
//                    }
//                }
////                    if(lvl["personas"].size()>0){
////                        par = new Paragraph("Usuarios:", times10bold)
////                        par.setIndentationLeft(lvl["nivel"]*20+10)
////                        contenido.add(par)
////
////                    }
//                lvl["personas"].each{p->
////                println "\t\t "+p["objeto"]+ "  "+  p["objeto"].departamento
//                    par3=null
//                    par3= new Paragraph("", times8normal)
//                    par3.setSpacingBefore(4)
////                    par3.setIndentationLeft((lvl["nivel"]-1)*20+10)
//                    par = new Paragraph("Usuario: ${p["objeto"].departamento.codigo}:"+p["objeto"]+" - ${p[ 'objeto'].login} - [ Sin Recepción: "+p["retrasados"]+" , Retrasados: ${p['rezagados']} ]", times8bold)
////                    par.setIndentationLeft((lvl["nivel"]-1)*20+10)
//                    contenido.add(par)
//                    if(params.detalle){
//                        tablaTramites=null
//                        tablaTramites = new PdfPTable(7);
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
//                        p["tramites"].each{t->
////                            par2.setIndentationLeft((lvl["nivel"]-1)*20+10)
//                            par = new Paragraph("${t.tramite.codigo} ${t.rolPersonaTramite.codigo=='R002'?'[CC]':''}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                            par = new Paragraph("${t.tramite.fechaCreacion.format('dd-MM-yyyy HH:mm')}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                            if(t.tramite.deDepartamento){
//                                par = new Paragraph("${t.tramite.deDepartamento.codigo}", times8normal)
//                                cell = new PdfPCell(par);
//                                tablaTramites.addCell(cell);
//                            }else{
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
//                            par = new Paragraph("${(t.fechaRecepcion)?t.fechaRecepcion?.format('dd-MM-yyyy hh:mm'):''}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                            par = new Paragraph("${(t.fechaLimiteRespuesta)?t.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm'):''}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
//                        }
//                        if(p["tramites"].size()>0){
//                            par3.add(tablaTramites)
//                            contenido.add(par3)
//                        }
//                    }
//                }
//            }
//
//
//
//            if(lvl["hijos"].size()>0)
//                imprimeHijosPdf(lvl,contenido,tablaTramites,params,usuario,deps,puedeVer)
//        }
//    }





    def reporteRetrasadosConsolidado() {
        println "con excel    "+params
        def estadoR= EstadoTramite.findByCodigo("E004")
        def estadoE= EstadoTramite.findByCodigo("E003")
        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCopia = RolPersonaTramite.findByCodigo("R002")
        def now = new Date()
        now=now.plus(2)

        def datos = [:]
        def usuario = null
        def deps = []
        def puedeVer = []
        def extraPersona ="and "
        if(params.prsn){
            usuario = Persona.get(params.prsn)
            extraPersona+="persona="+usuario.id+" "
            if(usuario.esTriangulo)
                extraPersona="and (persona=${usuario.id} or departamento = ${usuario.departamento.id})"
            def padre = usuario.departamento.padre
            while(padre){
                deps.add(padre)
                padre=padre.padre
            }
            deps.add(usuario.departamento)
            puedeVer.add(usuario.departamento)
            def hi = Departamento.findAllByPadre(usuario.departamento)
            while(hi.size()>0){
                puedeVer +=hi
                hi=Departamento.findAllByPadreInList(hi)
            }

        }
        if(params.dpto){
            def departamento = Departamento.get(params.dpto)
            println "DPTO "+departamento.codigo+"  "+departamento.descripcion
            def padre = departamento.padre
            while(padre){
                deps.add(padre)
                padre=padre.padre
            }
            deps.add(departamento)
            puedeVer.add(departamento)
            def hi = Departamento.findAllByPadre(departamento)
            while(hi.size()>0){
                puedeVer +=hi
                hi=Departamento.findAllByPadreInList(hi)
            }
        }
//        println "deps "+deps+"  puede ver  "+puedeVer
        def tramites = Tramite.findAll("from Tramite where externo!='1' or externo is null")
        tramites.each {t->
            def pdt = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite where tramite=${t.id} and fechaEnvio is not null and rolPersonaTramite in (${rolPara.id},${rolCopia.id}) and estado in (${estadoR.id},${estadoE.id}) ${usuario?extraPersona:''} ")
            if(pdt){
                pdt.each {pd->
                    def resp = Tramite.findAllByAQuienContesta(pd)
                    if(resp.size()==0){
                        if(pd.fechaLimite<now || (!pd.fechaRecepcion))
                            datos=jerarquia(datos,pd)
                    }

                }
            }

        }


        def path = servletContext.getRealPath("/") + "xls/"
        new File(path).mkdirs()
        //esto crea un archivo temporal que puede ser siempre el mismo para no ocupar espacio
        String filename = path + "text.xlsx";
        String sheetName = "Sheet1";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName);
        CreationHelper createHelper = wb.getCreationHelper();

        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setItalic(true);
        font.setBold(true);
        font.setColor(HSSFColor.GREEN.index);

        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFont(font)


        sheet.setAutobreaks(true);
        def total = 0
        def totalSr = 0
        def hijos = datos["hijos"]

        XSSFRow rowHead = sheet.createRow((short) 0);
        rowHead.setHeightInPoints(14)
//        headers.add(new Paragraph("GAD DE LA PROVINCIA DE PICHINCHA", times12bold));
//        headers.add(new Paragraph("SISTEMA DE ADMINISTRACION DOCUMENTAL", times12bold));
//        headers.add(new Paragraph("Reporte de trámites retrasados", times12bold));
//        headers.add(new Paragraph(""+new Date().format('dd-MM-yyyy HH:mm'), times12bold));
        sheet.setColumnWidth(1, 15000)
        Cell cell = rowHead.createCell((int) 1).setCellValue("GAD DE LA PROVINCIA DE PICHINCHA")
        rowHead=sheet.createRow((short) 1);
        cell = rowHead.createCell((int) 1).setCellValue("SISTEMA DE ADMINISTRACION DOCUMENTAL")
        rowHead=sheet.createRow((short) 2);
        cell = rowHead.createCell((int) 1).setCellValue("Reporte de Trámites Retrasados")
        rowHead=sheet.createRow((short) 3);
        cell = rowHead.createCell((int) 1).setCellValue(""+new Date().format('dd-MM-yyyy HH:mm'))
        def row = sheet.createRow((short) 4);


        def rowNode = sheet.createRow((short) 5);
        row.createCell((int) 2).setCellValue("Retrasados");
        row.createCell((int) 3).setCellValue("Sin recepción");
        def num = 6
        row = sheet.createRow((short) num);
        row.setHeightInPoints(14)
        rowNode.setHeightInPoints(14)
        hijos.each{lvl->
            if(puedeVer.size()==0 || (puedeVer.id.contains(lvl["objeto"].id))){
                if(lvl["tramites"].size()>0) {
                    rowNode.createCell((int) 0).setCellValue("Dirección:");
                    rowNode.createCell((int) 1).setCellValue(""+lvl["objeto"]);
                    def totalNode = 0
                    def totalNodeSr

                    if (lvl["tramites"].size() > 0) {
                        row.createCell((int) 0).setCellValue("Usuarios");
                        lvl["triangulos"].each { t ->
                            row.createCell((int) 1).setCellValue("${t} (Oficina)");
                            row.createCell((int) 2).setCellValue(lvl["rezagados"]);
                            row.createCell((int) 3).setCellValue(lvl["retrasados"]);
                            num++
                            row=sheet.createRow((short) num);
                            if (totalNode == 0)
                                totalNode += lvl["rezagados"]
                            if (totalNodeSr == 0)
                                totalNodeSr += lvl["retrasados"]

                        }
                    }
                    lvl["personas"].each { p ->
                        row.createCell((int) 1).setCellValue("${p['objeto']}");
                        row.createCell((int) 2).setCellValue(p['rezagados']);
                        row.createCell((int) 3).setCellValue(p['retrasados']);
                        num++
                        row=sheet.createRow((short) num);
                        totalNode += p["rezagados"]
                        totalNodeSr += p["retrasados"]
                    }
                    rowNode.createCell((int) 2).setCellValue(totalNode);
                    rowNode.createCell((int) 3).setCellValue(totalNodeSr);
                    total += totalNode
                }
            }
            def res = imprimeHijosXlsConsolidado(lvl,sheet,num,params,usuario,deps,puedeVer,total)
            total+= res[0]
            totalSr+= res[2]
            num=res[1]
        }

        row=sheet.createRow((short) num);
        row.createCell((int) 1).setCellValue("TOTAL");
        row.createCell((int) 2).setCellValue(total);
        row.createCell((int) 3).setCellValue(totalSr);
        FileOutputStream fileOut = new FileOutputStream(filename);
        wb.write(fileOut);
        fileOut.close();
        String disHeader = 'Attachment;Filename="reporte.xlsx"';
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



    def imprimeHijosXlsConsolidado(arr,sheet,num,params,usuario,deps,puedeVer,total){
        total = 0
        def totalSr = 0
        def datos = arr["hijos"]
        def rowNode = sheet.createRow((short) num);
        num++
        def row = sheet.createRow((short) num);
        row.setHeightInPoints(14)
        rowNode.setHeightInPoints(14)
        datos.each { lvl ->
            if (puedeVer.size() == 0 || (puedeVer.id.contains(lvl["objeto"].id))) {
                if (lvl["tramites"].size() > 0) {
                    rowNode.createCell((int) 0).setCellValue("Oficina:");
                    rowNode.createCell((int) 1).setCellValue("" + lvl["objeto"]);
                    def totalNode = 0
                    def totalNodeSr = 0

                    if (lvl["tramites"].size() > 0) {
                        row.createCell((int) 0).setCellValue("Usuarios");
                        lvl["triangulos"].each { t ->
                            row.createCell((int) 1).setCellValue("${t} (Oficina)");
                            row.createCell((int) 2).setCellValue(lvl["rezagados"]);
                            row.createCell((int) 3).setCellValue(lvl["retrasados"]);
                            num++
                            row = sheet.createRow((short) num);
                            if (totalNode == 0)
                                totalNode += lvl["rezagados"]
                            if (totalNodeSr == 0)
                                totalNodeSr += lvl["retrasados"]
                        }
                    }
                    lvl["personas"].each { p ->
                        row.createCell((int) 1).setCellValue("${p['objeto']}");
                        row.createCell((int) 2).setCellValue(p['rezagados']);
                        row.createCell((int) 3).setCellValue(p['retrasados']);
                        num++
                        row = sheet.createRow((short) num);
                        totalNode += p["rezagados"]
                        totalNodeSr += p["retrasados"]
                    }
                    rowNode.createCell((int) 2).setCellValue(totalNode);
                    rowNode.createCell((int) 3).setCellValue(totalNodeSr);
                    total += totalNode
                    totalSr +=totalNodeSr
                }
            }

            if (lvl["hijos"].size() > 0) {
                def res = imprimeHijosXlsConsolidado(lvl, sheet, num, params, usuario, deps, puedeVer,total)
                total += res[0]
                totalSr += res[2]
                num = res[1]
            }
//            println "total des dentro "+total+"   "
        }
        return [total,num,totalSr]
    }



    def jerarquia(arr,pdt){
//        println "______________jerarquia______________"
//        println "datos ini  ----- ${pdt.tramite.codigo}  ${pdt.id} dep   "+pdt.departamento+"   prsn "+pdt.persona
        def datos =arr
        def dep
        if(pdt.departamento){
            dep= pdt.departamento
        }else{
            dep =pdt.persona.departamento
        }
        def padres = []
        padres.add(dep)
        while (dep.padre){
            padres.add(dep.padre)
            dep=dep.padre
        }
//        println "padres "+padres
        def first = padres.pop()
        padres=padres.reverse()
        def nivel = padres.size()
        def lvl
        if(datos["id"]!=first.id.toString()){
//            println "no padre lvl 0"
            datos.put("id",first.id.toString())
            datos.put("objeto",first)
            datos.put("tramites",[])
            datos.put("hijos",[])
            datos.put("personas",[])
            datos.put("triangulos",first.getTriangulos())
            datos.put("nivel",0)
            datos.put("retrasados",0)
            datos.put("rezagados",0)
        }
        lvl = datos["hijos"]
        def cod=""
        def actual=null
//        println "padres each "+padres
        padres.each {p->
//            println "p.each "+p+"  nivel  "+nivel
//            println "buscando........"
            lvl.each{l->
//                println "\t lvl each --> "+l
                if(l["id"]==p.id.toString()){
                    actual=l
                }
            }
//            println "fin buscando ..............."
//            println "actual --> "+actual
            if(actual){
//                println "p--> "+p
                if(pdt.departamento){

                    if(actual["id"]==pdt.departamento.id.toString()){
//                        println "es el mismo add tramites"
                        if(!pdt.fechaRecepcion)
                            actual["retrasados"]++
                        else
                            actual["rezagados"]++
                        actual["tramites"].add(pdt)
                        actual["tramites"]=actual["tramites"].sort{it.fechaEnvio}
                    }

                }else{
                    if(actual["id"]==pdt.persona.departamento.id.toString()){
                        if(actual["personas"].size()==0){
                            if(!pdt.fechaRecepcion)
                                actual["personas"].add(["id":pdt.persona.id.toString(),"objeto":pdt.persona,"tramites":[pdt],"retrasados":1,"rezagados":0])
                            else
                                actual["personas"].add(["id":pdt.persona.id.toString(),"objeto":pdt.persona,"tramites":[pdt],"retrasados":0,"rezagados":1])
//                            actual["personas"].add(["id":pdt.persona.id.toString(),"objeto":pdt.persona,"tramites":[pdt],"retrasados":0,"rezagados":0])
                        }else{
                            def per = null
                            actual["personas"].each{pe->
                                if(pe["id"]==pdt.persona.id.toString()){
                                    per=pe
                                }
                            }
                            if(per){
                                if(!pdt.fechaRecepcion)
                                    per["retrasados"]++
                                else
                                    per["rezagados"]++
                                per["tramites"].add(pdt)
                                per["tramites"]=per["tramites"].sort{it.fechaEnvio}
                            }else{
                                if(!pdt.fechaRecepcion)
                                    actual["personas"].add(["id":pdt.persona.id.toString(),"objeto":pdt.persona,"tramites":[pdt],"retrasados":1,"rezagados":0])
                                else
                                    actual["personas"].add(["id":pdt.persona.id.toString(),"objeto":pdt.persona,"tramites":[pdt],"retrasados":0,"rezagados":1])
//                                actual["personas"].add(["id":pdt.persona.id.toString(),"objeto":pdt.persona,"tramites":[pdt],"retrasados":0,"rezagados":0])
                            }
                        }
                    }
                }
                lvl= actual["hijos"]
            }else{
//                println "no actual add lvl "+lvl
                def temp = [:]
                temp.put("id",p.id.toString())
                temp.put("objeto",p)
                temp.put("tramites",[])
                temp.put("hijos",[])
                temp.put("personas",[])
                temp.put("triangulos",p.getTriangulos())
                temp.put("retrasados",0)
                temp.put("rezagados",0)
                def depto = (pdt.departamento)?pdt.departamento:pdt.persona.departamento
                if(depto==p){
                    if(pdt.departamento){
                        temp["tramites"].add(pdt)
                        temp["tramites"]=temp["tramites"].sort{it.fechaEnvio}
                        if(!pdt.fechaRecepcion)
                            temp["retrasados"]++
                        else
                            temp["rezagados"]++
                    }else{
                        if(!pdt.fechaRecepcion)
                            temp["personas"].add(["id":pdt.persona.id.toString(),"objeto":pdt.persona,"tramites":[pdt],"retrasados":1,"rezagados":0])
                        else
                            temp["personas"].add(["id":pdt.persona.id.toString(),"objeto":pdt.persona,"tramites":[pdt],"retrasados":0,"rezagados":1])
//                    temp["personas"].add(["id":pdt.persona.id.toString(),"objeto":pdt.persona,"tramites":[pdt],"retrasados":0,"rezagados":0])
                    }
                }

                temp.put("nivel",nivel)

                lvl.add(temp)
//                println "fin add actual "+temp+"  nivel "+nivel
//                println "asi quedo lvl "+lvl
//                println "######################"
                if(lvl.size()==1) {
                    lvl = lvl[0]["hijos"]
                }else{
                    lvl = lvl[lvl.size()-1]["hijos"]
                }
//                println "lvl ? "+lvl
                nivel++

            }

            actual=null
        }


//        println "cod "+cod
////        println "lvl "+lvl
//        println "datos fun "+datos
////
//        println "---------------------fin datos---------------------------------------"
        return datos
    }



}
