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
    Font times8bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD)
    Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
    Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)

    def reporteRetrasados(){
        def estadoR= EstadoTramite.findByCodigo("E004")
        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def now = new Date()
        now=now.plus(2)
        def tramites = Tramite.findAll("from Tramite where estadoTramite=${estadoR.id} and (estado !='C' or estado is null)")
        def datos = [:]

        tramites.each {t->
            if(t.fechaMaximoRespuesta < now){
                def pdt = PersonaDocumentoTramite.findByTramiteAndRolPersonaTramite(t,rolPara)
                if(!pdt){
                    println "wtf "+t.id
                }else{
//                    println "pdt "+pdt.id+" "+pdt.departamento+"  "+pdt.persona+" "+pdt.persona?.departamento
                    datos=jerarquia(datos,pdt)

                }
            }
        }


//        println "tramites "+datos
//        jerarquia(datos)



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
        contenido.add(new Paragraph("-"+datos["objeto"], times12bold))
        if(datos["tramites"].size()>0){
            contenido.add(new Paragraph("Traimites:", times10bold))
        }
        datos["tramites"].each{t->
            contenido.add(new Paragraph("${t.codigo}", times8normal))
        }
        def hijos = datos["hijos"]
        def profundidad = 0
        document.add(headers);
        document.add(contenido)
        PdfPTable tablaTramites
        /*TODO quitar el para... talvez reemplazar con algo*/
        hijos.each{lvl->
            def par = new Paragraph("-"+lvl["objeto"], times12bold)
            par.setIndentationLeft(lvl["nivel"]*20)
            document.add(par)
            def par2= new Paragraph("", times8normal)
            par2.setSpacingBefore(4)
            def par3= new Paragraph("", times8normal)
            par3.setSpacingBefore(4)
            if(lvl["tramites"].size()>0){
                par = new Paragraph("Traimites:", times10bold)
                par.setIndentationLeft(lvl["nivel"]*20+10)
                document.add(par)
                tablaTramites = new PdfPTable(4);
                tablaTramites.setWidthPercentage(100);
                par = new Paragraph("Número", times8bold)
                PdfPCell cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
                par = new Paragraph("De", times8bold)
                cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
                par = new Paragraph("Para", times8bold)
                cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
                par = new Paragraph("Fec. Envío", times8bold)
                cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
            }
            lvl["tramites"].each{t->
                par2.setIndentationLeft(lvl["nivel"]*20+20)
                par = new Paragraph("${t.tramite.codigo}", times8normal)
                PdfPCell cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
                par = new Paragraph("${t.tramite.de}", times8normal)
                cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
                par = new Paragraph("${t.departamento}", times8normal)
                cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
                par = new Paragraph("${t.fechaEnvio.format('dd-mm-yyyy hh:mm')}", times8normal)
                cell = new PdfPCell(par);
                tablaTramites.addCell(cell);

            }
            if(lvl["tramites"].size()>0){
                par2.add(tablaTramites)
                document.add(par2)
            }
            if(lvl["personas"].size()>0){
                par = new Paragraph("Usuarios:", times10bold)
                par.setIndentationLeft(lvl["nivel"]*20+10)
                document.add(par)
                tablaTramites = new PdfPTable(4);
                tablaTramites.setWidthPercentage(100);
                par = new Paragraph("Número", times8bold)
                PdfPCell cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
                par = new Paragraph("De", times8bold)
                cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
                par = new Paragraph("Para", times8bold)
                cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
                par = new Paragraph("Fec. Envío", times8bold)
                cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
            }
            lvl["personas"].eachp{p->
//                println "\t\t "+p["objeto"]+ "  "+  p["objeto"].departamento
                par3= new Paragraph("", times8normal)
                par3.setSpacingBefore(4)
                par = new Paragraph(""+p["objeto"], times8bold)
                par.setIndentationLeft(lvl["nivel"]*20+20)
                document.add(par)
                par3.setIndentationLeft(lvl["nivel"]*20+30)
                p["tramites"].each{t->
                    par = new Paragraph("${t.tramite.codigo}", times8normal)
                    PdfPCell cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("${t.tramite.de}", times8normal)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("${t.persona}", times8normal)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("${t.fechaEnvio.format('dd-mm-yyyy hh:mm')}", times8normal)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                }
                if(p["tramites"].size()>0){
                    par3.add(tablaTramites)
                    document.add(par3)
                }
            }
            imprimeHijosPdf(lvl,document,tablaTramites)
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


    def imprimeHijosPdf(arr,contenido,tablaTramites){
        def datos = arr["hijos"]
        datos.each{lvl->
//            println  "\t "+lvl["objeto"]
//            println "\t\t Tramites:"
            def par = new Paragraph("-"+lvl["objeto"], times12bold)
            par.setIndentationLeft(lvl["nivel"]*20)
            contenido.add(par)
            def par2= new Paragraph("", times8normal)
            par2.setSpacingBefore(4)
            def par3= new Paragraph("", times8normal)
            par3.setSpacingBefore(4)
            if(lvl["tramites"].size()>0){
                par = new Paragraph("Traimites:", times10bold)
                par.setIndentationLeft(lvl["nivel"]*20+10)
                contenido.add(par)
                tablaTramites = new PdfPTable(4);
                tablaTramites.setWidthPercentage(100);
                par = new Paragraph("Número", times8bold)
                PdfPCell cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
                par = new Paragraph("De", times8bold)
                cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
                par = new Paragraph("Para", times8bold)
                cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
                par = new Paragraph("Fec. Envío", times8bold)
                cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
            }
            lvl["tramites"].each{t->
                par2.setIndentationLeft(lvl["nivel"]*20+20)
                par = new Paragraph("${t.tramite.codigo}", times8normal)
                PdfPCell cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
                par = new Paragraph("${t.tramite.de}", times8normal)
                cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
                par = new Paragraph("${t.departamento}", times8normal)
                cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
                par = new Paragraph("${t.fechaEnvio.format('dd-mm-yyyy hh:mm')}", times8normal)
                cell = new PdfPCell(par);
                tablaTramites.addCell(cell);

            }
            if(lvl["tramites"].size()>0){
                par2.add(tablaTramites)
                contenido.add(par2)
            }
            if(lvl["personas"].size()>0){
                par = new Paragraph("Usuarios:", times10bold)
                par.setIndentationLeft(lvl["nivel"]*20+10)
                contenido.add(par)
                tablaTramites = new PdfPTable(4);
                tablaTramites.setWidthPercentage(100);
                par = new Paragraph("Número", times8bold)
                PdfPCell cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
                par = new Paragraph("De", times8bold)
                cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
                par = new Paragraph("Para", times8bold)
                cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
                par = new Paragraph("Fec. Envío", times8bold)
                cell = new PdfPCell(par);
                tablaTramites.addCell(cell);
            }
            lvl["personas"].each{p->
//                println "\t\t "+p["objeto"]+ "  "+  p["objeto"].departamento
                par = new Paragraph(""+p["objeto"], times8bold)
                par.setIndentationLeft(lvl["nivel"]*20+20)
                contenido.add(par)
                par3.setIndentationLeft(lvl["nivel"]*20+30)
                p["tramites"].each{t->
                    par = new Paragraph("${t.tramite.codigo}", times8normal)
                    PdfPCell cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("${t.tramite.de}", times8normal)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("${t.persona}", times8normal)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                    par = new Paragraph("${t.fechaEnvio.format('dd-mm-yyyy hh:mm')}", times8normal)
                    cell = new PdfPCell(par);
                    tablaTramites.addCell(cell);
                }
                if(p["tramites"].size()>0){
                    par3.add(tablaTramites)
                    contenido.add(par3)
                }
            }
            if(lvl["hijos"].size()>0)
                imprimeHijos(lvl,contenido,tablaTramites)
        }
    }
    def imprimeHijos(arr){
        def datos = arr["hijos"]
        datos.each{lvl->
            println  "\t "+lvl["objeto"]
            println "\t\t Tramites:"
            lvl["tramites"].each{t->
                println "\t "+t
            }
            println "\t\t Personas:"
            lvl["personas"].each{p->
                println "\t\t "+p["objeto"]
                p["tramites"].each{t->
                    println "\t "+t
                }
            }
            if(lvl["hijos"].size()>0)
                imprimeHijos(lvl)
        }
    }

    def jerarquia(arr,pdt){
//        println "______________jerarquia______________"
//        println "datos ini  ----- dep   "+pdt.departamento+"   prsn "+pdt.persona
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
            datos.put("nivel",0)
        }
        lvl = datos["hijos"]
        def cod=""
        def actual=null
//        println "padres each "+padres
        padres.each {p->
//            println "p.each "+p
            lvl.each{l->
                if(l["id"]==p.id.toString()){
                    actual=l
                }
            }
//            println "actual --> "+actual
            if(actual){
//                println "p--> "+p
                if(pdt.departamento){

                    if(actual["id"]==pdt.departamento.id.toString()){
//                        println "es el mismo add tramites"
                        actual["tramites"].add(pdt)
                    }

                }else{
                    if(actual["id"]==pdt.persona.departamento.id.toString()){
                        if(actual["personas"].size()==0)
                            actual["personas"].add(["id":pdt.persona.id.toString(),"objeto":pdt.persona,"tramites":[pdt]])
                        else{
                            def per = null
                            actual["personas"].each{pe->
                                if(pe["id"]==pdt.persona.id.toString()){
                                    per=pe
                                }
                            }
                            if(per){
                                per["tramites"].add(pdt)
                            }else{
                                actual["personas"].add(["id":pdt.persona.id.toString(),"objeto":pdt.persona,"tramites":[pdt]])
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
                if(pdt.departamento){
                    temp["tramites"].add(pdt)
                }else{
                    temp["personas"].add(["id":pdt.persona.id.toString(),"objeto":pdt.persona,"tramites":[pdt]])
                }
                temp.put("nivel",nivel)
                lvl.add(temp)
                lvl= lvl["hijos"]
            }

            actual=null
        }


//        println "cod "+cod
////        println "lvl "+lvl
//        println "datos fun "+datos
//
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
