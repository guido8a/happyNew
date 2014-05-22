package happy.reportes

import com.lowagie.text.HeaderFooter
import com.lowagie.text.PageSize
import com.lowagie.text.Phrase
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
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

import java.awt.Color

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

    def reporteRetrasadosDetalle(){
//        params.detalle=1
//        params.prsn=session.usuario.id
        println "detallado "+params
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
        Document document = reportesPdfService.crearDocumento([top: 2.5, right: 2.5, bottom: 2.5, left: 3])
        def pdfw = PdfWriter.getInstance(document, baos);
        reportesPdfService.documentoFooter(document, "", true, [top: false, right: false, bottom: false, left: false], Element.ALIGN_CENTER)
        document.open();
        reportesPdfService.propiedadesDocumento(document, "reporteTramitesRetrasados")
        Paragraph headers = new Paragraph();
        headers.setAlignment(Element.ALIGN_CENTER);
        headers.add(new Paragraph("Reporte de trámites retrasados", times18bold));
//        headers.add(new Paragraph(""+session.departamento+"", times12bold));
        headers.add(new Paragraph("Al: " + now.format("dd-MM-yyyy hh:mm"), times12bold));
        headers.add(new Paragraph("\n", times12bold))
        def contenido = new Paragraph();
//        contenido.add(new Paragraph("-"+datos["objeto"], times12bold))
//        if(datos["tramites"].size()>0){
//            contenido.add(new Paragraph("Trámites:", times10bold))
//            datos["triangulos"].each{t->
//                par = new Paragraph(""+t, times8bold)
//                par.setIndentationLeft(lvl["nivel"]*20+20)
//                contenido.add(par)
//            }
//        }
//        datos["tramites"].each{t->
//            contenido.add(new Paragraph("${t.codigo}", times8normal))
//        }
        def hijos = datos["hijos"]
        def profundidad = 0
        document.add(headers);
//        document.add(contenido)
        PdfPTable tablaTramites

        hijos.each{lvl->
//            println "hijo ${lvl['objeto']}  ${lvl['objeto'].id}   "+puedeVer.id
            if(puedeVer.size()==0 || (puedeVer.id.contains(lvl["objeto"].id))){
//            println "desp "+deps+"   "+lvl["objeto"]+"   "+(deps.id.contains(lvl["objeto"].id))


                def par = new Paragraph("-"+lvl["objeto"], times12bold)
                par.setIndentationLeft((lvl["nivel"]-1)*20)
                document.add(par)
                def par2= new Paragraph("", times8normal)
                par2.setSpacingBefore(4)
                def par3= new Paragraph("", times8normal)
                par3.setSpacingBefore(4)
//                println "wtf "+lvl["triangulos"]

                    if(lvl["tramites"].size()>0){
//                        par = new Paragraph("Trámites:", times10bold)
//                        par.setIndentationLeft(lvl["nivel"]*20+10)
//                        document.add(par)
                        lvl["triangulos"].each{t->
                            par = new Paragraph("Usuario: ${t.departamento.codigo}:"+t+" - ${t.login} - [ Sin Recepción: "+lvl["retrasados"]+" , Retrasados: ${lvl['rezagados']} ]", times8bold)
                            par.setIndentationLeft((lvl["nivel"]-1)*20+10)
                            document.add(par)
                        }

                    }
                    if(params.detalle){
                        tablaTramites = new PdfPTable(5);
                        tablaTramites.setWidthPercentage(100);
                        par = new Paragraph("Número", times8bold)
                        PdfPCell cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("De", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
//                    par = new Paragraph("Para", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
                        par = new Paragraph("Envío", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph(" Recepcíon", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("Límite respuesta", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        lvl["tramites"].each{t->
                            par2.setIndentationLeft((lvl["nivel"]-1)*20+10)
                            par = new Paragraph("${t.tramite.codigo}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            if(t.tramite.deDepartamento){
                                par = new Paragraph("${t.tramite.deDepartamento.codigo}", times8normal)
                                cell = new PdfPCell(par);
                                tablaTramites.addCell(cell);
                            }else{
                                par = new Paragraph("${t.tramite.de}", times8normal)
                                cell = new PdfPCell(par);
                                tablaTramites.addCell(cell);
                            }
//                        par = new Paragraph("${t.departamento}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
                            par = new Paragraph("${t.fechaEnvio.format('dd-MM-yyyy hh:mm')}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("${(t.fechaRecepcion)?t.fechaRecepcion?.format('dd-MM-yyyy hh:mm'):''}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("${(t.fechaLimiteRespuesta)?t.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm'):''}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);

                        }
                        if(lvl["tramites"].size()>0){
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
                    lvl["personas"].each{p->
//                println "\t\t "+p["objeto"]+ "  "+  p["objeto"].departamento
                        par3=null
                        par3= new Paragraph("", times8normal)
                        par3.setSpacingBefore(4)
                        par = new Paragraph("Usuario: ${p[ "objeto"].departamento.codigo}:"+p["objeto"]+" - ${p['objeto'].login} - [ Sin Recepción: "+p["retrasados"]+" , Retrasados: ${p['rezagados']} ]", times8bold)
                        par.setIndentationLeft((lvl["nivel"]-1)*20+20)
                        document.add(par)
                        par3.setIndentationLeft((lvl["nivel"]-1)*20+20)

                        if(params.detalle){
                            tablaTramites = null
                            tablaTramites = new PdfPTable(5);
                            tablaTramites.setWidthPercentage(100);
                            par = new Paragraph("Número", times8bold)
                            PdfPCell cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("De", times8bold)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
//                        par = new Paragraph("Para", times8bold)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
                            par = new Paragraph("Envío", times8bold)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("Recepcíon", times8bold)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("Límite respuesta", times8bold)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            p["tramites"].each{t->
                                par = new Paragraph("${t.tramite.codigo}", times8normal)
                                cell = new PdfPCell(par);
                                tablaTramites.addCell(cell);
                                if(t.tramite.deDepartamento){
                                    par = new Paragraph("${t.tramite.deDepartamento.codigo}", times8normal)
                                    cell = new PdfPCell(par);
                                    tablaTramites.addCell(cell);
                                }else{
                                    par = new Paragraph("${t.tramite.de}", times8normal)
                                    cell = new PdfPCell(par);
                                    tablaTramites.addCell(cell);
                                }
//                            par = new Paragraph("${t.persona}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
                                par = new Paragraph("${t.fechaEnvio.format('dd-MM-yyyy hh:mm')}", times8normal)
                                cell = new PdfPCell(par);
                                tablaTramites.addCell(cell);
                                par = new Paragraph("${(t.fechaRecepcion)?t.fechaRecepcion?.format('dd-MM-yyyy hh:mm'):''}", times8normal)
                                cell = new PdfPCell(par);
                                tablaTramites.addCell(cell);
                                par = new Paragraph("${(t.fechaLimiteRespuesta)?t.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm'):''}", times8normal)
                                cell = new PdfPCell(par);
                                tablaTramites.addCell(cell);
                            }
                            if(p["tramites"].size()>0){
                                par3.add(tablaTramites)
                                document.add(par3)
                            }
                        }
                    }



            }
            imprimeHijosPdf(lvl,document,tablaTramites,params,usuario,deps,puedeVer)

        }


        document.close();
        pdfw.close()
        byte[] b = baos.toByteArray();
        response.setContentType("application/pdf")
        response.setHeader("Content-disposition", "attachment; filename=" + name)
        response.setContentLength(b.length)
        response.getOutputStream().write(b)
//        return  [tramites:tramites,datos:datos]
    }




    def imprimeHijosPdf(arr,contenido,tablaTramites,params,usuario,deps,puedeVer){
        def datos = arr["hijos"]
        datos.each{lvl->
//            println  "\t "+lvl["objeto"]
//            println "\t\t Tramites:"
//            println "hijo funcion ${lvl['objeto']} "+lvl["objeto"].id+"    "+puedeVer.id

                if(puedeVer.size()==0 || (puedeVer.id.contains(lvl["objeto"].id))){
                def par = new Paragraph("-"+lvl["objeto"], times12bold)
                par.setIndentationLeft((lvl["nivel"]-1)*20)
                contenido.add(par)
                def par2= new Paragraph("", times8normal)
                par2.setSpacingBefore(4)
                def par3= new Paragraph("", times8normal)
                par3.setSpacingBefore(4)

                    if(lvl["tramites"].size()>0){
//                        par = new Paragraph("Trámites:", times10bold)
//                        par.setIndentationLeft(lvl["nivel"]*20+10)
//                        contenido.add(par)
                        lvl["triangulos"].each{t->
                            par = new Paragraph("Usuario: ${t.departamento.codigo}:"+t+" - ${t.login} - [ Sin Recepción: "+lvl["retrasados"]+" , Retrasados: ${lvl['rezagados']} ]", times8bold)
                            par.setIndentationLeft((lvl["nivel"]-1)*20+10)
                            contenido.add(par)
                        }

                    }
                    if(params.detalle){
                        tablaTramites = new PdfPTable(5);
                        tablaTramites.setWidthPercentage(100);
                        par = new Paragraph("Número", times8bold)
                        def cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("De", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
//                    par = new Paragraph("Para", times8bold)
//                    cell = new PdfPCell(par);
//                    tablaTramites.addCell(cell);
                        par = new Paragraph("Envío", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("Recepcíon", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        par = new Paragraph("Límite respuesta", times8bold)
                        cell = new PdfPCell(par);
                        tablaTramites.addCell(cell);
                        lvl["tramites"].each{t->
                            par2.setIndentationLeft((lvl["nivel"]-1)*20+10)
                            par = new Paragraph("${t.tramite.codigo}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            if(t.tramite.deDepartamento){
                                par = new Paragraph("${t.tramite.deDepartamento.codigo}", times8normal)
                                cell = new PdfPCell(par);
                                tablaTramites.addCell(cell);
                            }else{
                                par = new Paragraph("${t.tramite.de}", times8normal)
                                cell = new PdfPCell(par);
                                tablaTramites.addCell(cell);
                            }
//                        par = new Paragraph("${t.departamento}", times8normal)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
                            par = new Paragraph("${t.fechaEnvio.format('dd-MM-yyyy hh:mm')}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("${(t.fechaRecepcion)?t.fechaRecepcion?.format('dd-MM-yyyy hh:mm'):''}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("${(t.fechaLimiteRespuesta)?t.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm'):''}", times8normal)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);

                        }
                        if(lvl["tramites"].size()>0){
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
                    lvl["personas"].each{p->
//                println "\t\t "+p["objeto"]+ "  "+  p["objeto"].departamento
                        par3=null
                        par3= new Paragraph("", times8normal)
                        par3.setSpacingBefore(4)
                        par3.setIndentationLeft((lvl["nivel"]-1)*20+10)
                        par = new Paragraph("Usuario: ${p["objeto"].departamento.codigo}:"+p["objeto"]+" - ${p[ 'objeto'].login} - [ Sin Recepción: "+p["retrasados"]+" , Retrasados: ${p['rezagados']} ]", times8bold)
                        par.setIndentationLeft((lvl["nivel"]-1)*20+10)
                        contenido.add(par)
                        if(params.detalle){
                            tablaTramites=null
                            tablaTramites = new PdfPTable(5);
                            tablaTramites.setWidthPercentage(100);
                            par = new Paragraph("Número", times8bold)
                            PdfPCell cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("De", times8bold)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
//                        par = new Paragraph("Para", times8bold)
//                        cell = new PdfPCell(par);
//                        tablaTramites.addCell(cell);
                            par = new Paragraph("Envío", times8bold)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("Recepcíon", times8bold)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
                            par = new Paragraph("Límite respuesta", times8bold)
                            cell = new PdfPCell(par);
                            tablaTramites.addCell(cell);
//                            par3.setIndentationLeft(lvl["nivel"]*20+30)
                            p["tramites"].each{t->
                                par = new Paragraph("${t.tramite.codigo}", times8normal)
                                cell = new PdfPCell(par);
                                tablaTramites.addCell(cell);
                                if(t.tramite.deDepartamento){
                                    par = new Paragraph("${t.tramite.deDepartamento.codigo}", times8normal)
                                    cell = new PdfPCell(par);
                                    tablaTramites.addCell(cell);
                                }else{
                                    par = new Paragraph("${t.tramite.de}", times8normal)
                                    cell = new PdfPCell(par);
                                    tablaTramites.addCell(cell);
                                }

//                            par = new Paragraph("${t.persona}", times8normal)
//                            cell = new PdfPCell(par);
//                            tablaTramites.addCell(cell);
                                par = new Paragraph("${t.fechaEnvio.format('dd-MM-yyyy hh:mm')}", times8normal)
                                cell = new PdfPCell(par);
                                tablaTramites.addCell(cell);
                                par = new Paragraph("${(t.fechaRecepcion)?t.fechaRecepcion?.format('dd-MM-yyyy hh:mm'):''}", times8normal)
                                cell = new PdfPCell(par);
                                tablaTramites.addCell(cell);
                                par = new Paragraph("${(t.fechaLimiteRespuesta)?t.fechaLimiteRespuesta?.format('dd-MM-yyyy hh:mm'):''}", times8normal)
                                cell = new PdfPCell(par);
                                tablaTramites.addCell(cell);
                            }
                            if(p["tramites"].size()>0){
                                par3.add(tablaTramites)
                                contenido.add(par3)
                            }
                        }
                    }
                }



            if(lvl["hijos"].size()>0)
                imprimeHijos(lvl,contenido,tablaTramites)
        }
    }
    def imprimeHijos(arr){
        def datos = arr["hijos"]
        datos.each{lvl->
            println  "\t\t\t "+lvl["objeto"]
            println "\t\t\t\t Tramites:"
            lvl["tramites"].each{t->
                println "\t\t\t\t\t "+t
            }
            println "\t\t\t\t Personas:"
            lvl["personas"].each{p->
                println "\t\t\t\t "+p["objeto"]
                p["tramites"].each{t->
                    println "\t\t\t "+t
                }
            }
            if(lvl["hijos"].size()>0)
                imprimeHijos(lvl)
        }
    }

    def jerarquia(arr,pdt){
//        println "______________jerarquia______________"
//        println "datos ini  -----   ${pdt.id} dep   "+pdt.departamento+"   prsn "+pdt.persona
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
//                println "\t actual "+l
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

                lvl= lvl[0]["hijos"]
//                println "lvl ? "+lvl
                nivel++
//                println "fin add actual "+temp+"  nivel "+nivel
//                println "######################"
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

    def arreglaTramites(){
        Tramite.list().each {
            def hijos = Tramite.findAllByPadre(it)
            if(hijos.size()>0){
                it.estado="C"
                it.save()
            }
        }
    }
}
