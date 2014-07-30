package happy.reportes
import com.lowagie.text.pdf.DefaultFontMapper
import com.lowagie.text.pdf.PdfContentByte
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfTemplate

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

class BloqueadosController {
    def reportesPdfService

    Font times12bold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
    Font times18bold = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
    Font times10bold = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    Font times8bold = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
    Font times8normal = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL)
    Font times10boldWhite = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
    Font times8boldWhite = new Font(Font.TIMES_ROMAN, 8, Font.BOLD)
    def datosGrafico

    def reporteConsolidado() {
//        params.detalle=1
//        params.prsn=session.usuario.id
//        println "con aaa    " + params
//        datosGrafico = [:]
//        def estadoR = EstadoTramite.findByCodigo("E004")
        if(!params.dpto)
            params.dpto=session.usuario.departamentoId
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
        def pdt = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite where" +
                " fechaEnvio is not null " +
                "and rolPersonaTramite in (${rolPara.id},${rolCopia.id}) " +
                "and estado in (${estadoE.id}) ${usuario ? extraPersona : ''} ")
        def ahora = new Date();

        if (pdt) {
            pdt.each { pd ->
                if(pd.tramite.externo!="1" || pd.tramite==null){
                    def resp = Tramite.findAllByAQuienContesta(pd)
                    if (resp.size() == 0) {
                        if (!pd.fechaRecepcion) {
                            def fechaBloqueo = pd.fechaBloqueo
                            if (fechaBloqueo && fechaBloqueo < ahora) {
                                datos = jerarquia(datos, pd)
                            }
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
        reportesPdfService.crearEncabezado(document, "Reporte resumido de Trámites sin recepción")
        def contenido = new Paragraph();
        def total = 0
        def totalSr = 0
        def hijos = datos["hijos"]

        PdfPTable tablaTramites
        tablaTramites = new PdfPTable(3);
        tablaTramites.setWidths(10, 66, 24)
        tablaTramites.setWidthPercentage(100);
        def parH = new Paragraph("", times8bold)
        def cell = new PdfPCell(parH);
        cell.setBorderColor(Color.WHITE)
        tablaTramites.addCell(cell);
        cell = new PdfPCell(parH);
        cell.setBorderColor(Color.WHITE)
        tablaTramites.addCell(cell);

        parH = new Paragraph("Sin recepción", times8bold)
        cell = new PdfPCell(parH);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
        cell.setBorderColor(Color.WHITE)
        tablaTramites.addCell(cell);

        hijos.each { lvl ->
            if (puedeVer.size() == 0 || (puedeVer.id.contains(lvl["objeto"].id))) {
                if (lvl["tramites"].size() > 0 || lvl["personas"].size() > 0) {
                    //datosGrafico.put(lvl["objeto"].toString(), [:])
//                    def dg = datosGrafico[lvl["objeto"].toString()]
//                    dg.put("rezagados", [:])
//                    dg.put("retrasados", [:])
//                    dg.put("totalRz", 0)
//                    dg.put("totalRs", 0)
//                    dg.put("objeto", lvl["objeto"])
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
//                                dg["rezagados"].put("Oficina", lvl["rezagados"].toInteger())
                            }
                            if (totalNodeSr == 0) {
                                totalNodeSr += lvl["retrasados"].toInteger()
//                                dg["retrasados"].put("Oficina", lvl["retrasados"].toInteger())
                            }
                        }
                    }
                    lvl["personas"].each { p ->
                        usuarios += "${p['objeto']} \n"
                        totales += "${p["rezagados"]} \n"
                        totalesSr += "" + p["retrasados"] + " \n"
//                        dg["rezagados"].put(p['objeto'], p["rezagados"].toInteger())
//                        dg["retrasados"].put(p['objeto'], p["retrasados"].toInteger())
                        totalNode += p["rezagados"].toInteger()
                        totalNodeSr += p["retrasados"].toInteger()
                    }

                    tablaTramites.addCell(cellNombre);
//                    par = new Paragraph("" + totalNode, times8bold)
//                    def cellTotal = new PdfPCell(par);
//                    cellTotal.setBorderColor(Color.WHITE)
//                    cellTotal.setHorizontalAlignment(Element.ALIGN_RIGHT)
//                    tablaTramites.addCell(cellTotal);
                    par = new Paragraph("" + totalNodeSr, times8bold)
                    def cellTotalSr = new PdfPCell(par);
                    cellTotalSr.setBorderColor(Color.WHITE)
                    cellTotalSr.setHorizontalAlignment(Element.ALIGN_RIGHT)
                    tablaTramites.addCell(cellTotalSr);
//                    dg["totalRz"] = totalNode
//                    dg["totalRs"] = totalNodeSr
                    par = new Paragraph("Usuario:", times8bold)
                    cell = new PdfPCell(par);
                    cell.setBorderColor(Color.WHITE)
                    tablaTramites.addCell(cell);
                    par = new Paragraph(usuarios, times8normal)
                    cell = new PdfPCell(par);
                    cell.setBorderColor(Color.WHITE)
                    tablaTramites.addCell(cell);
//                    par = new Paragraph(totales, times8normal)
//                    cell = new PdfPCell(par);
//                    cell.setBorderColor(Color.WHITE)
//                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
//                    tablaTramites.addCell(cell);
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
//        par = new Paragraph("" + total, times8bold)
//        cell = new PdfPCell(par);
//        cell.setBorderColor(Color.WHITE)
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
//        tablaTramites.addCell(cell);
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
//                datosGrafico.put(lvl["objeto"].toString(), [:])
//                def dg = datosGrafico[lvl["objeto"].toString()]
//                dg.put("rezagados", [:])
//                dg.put("retrasados", [:])
//                dg.put("totalRz", 0)
//                dg.put("totalRs", 0)
//                dg.put("objeto", lvl["objeto"])
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
                            //dg["rezagados"].put("Oficina", lvl["rezagados"].toInteger())
                        }
                        if (totalNodeSr == 0) {
                            totalNodeSr += lvl["retrasados"].toInteger()
                            // dg["retrasados"].put("Oficina", lvl["retrasados"].toInteger())
                        }
                    }
                }
                lvl["personas"].each { p ->
                    usuarios += "${p['objeto']} \n"
                    totales += "${p["rezagados"]} \n"
                    totalesSr += "" + p["retrasados"] + " \n"
                    //dg["rezagados"].put(p['objeto'], p["rezagados"].toInteger())
                    //dg["retrasados"].put(p['objeto'], p["retrasados"].toInteger())
                    totalNode += p["rezagados"].toInteger()
                    totalNodeSr += p["retrasados"].toInteger()
                }

                tablaTramites.addCell(cellNombre);
//                par = new Paragraph("" + totalNode, times8bold)
//                def cellTotal = new PdfPCell(par);
//                cellTotal.setBorderColor(Color.WHITE)
//                cellTotal.setHorizontalAlignment(Element.ALIGN_RIGHT)
//                tablaTramites.addCell(cellTotal);
                par = new Paragraph("" + totalNodeSr, times8bold)
                def cellTotalSr = new PdfPCell(par);
                cellTotalSr.setBorderColor(Color.WHITE)
                cellTotalSr.setHorizontalAlignment(Element.ALIGN_RIGHT)
//                dg["totalRz"] = totalNode
//                dg["totalRs"] = totalNodeSr
                tablaTramites.addCell(cellTotalSr);
                par = new Paragraph("Usuario:", times8bold)
                cell = new PdfPCell(par);
                cell.setBorderColor(Color.WHITE)
                tablaTramites.addCell(cell);
                par = new Paragraph(usuarios, times8normal)
                cell = new PdfPCell(par);
                cell.setBorderColor(Color.WHITE)
                tablaTramites.addCell(cell);
//                par = new Paragraph(totales, times8normal)
//                cell = new PdfPCell(par);
//                cell.setBorderColor(Color.WHITE)
//                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
//                tablaTramites.addCell(cell);
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
                def res = imprimeHijosPdfConsolidado(lvl, contenido, tablaTramites, params, usuario, deps, puedeVer, total, totalSr,datosGrafico)
                total += res[0]
                totalSr += res[1]
            }
//            println "total des dentro "+total+"   "
        }
        return [total, totalSr]
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

}
