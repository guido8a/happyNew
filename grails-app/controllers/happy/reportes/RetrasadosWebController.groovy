package happy.reportes

import groovy.json.JsonBuilder
import happy.seguridad.Persona
import happy.tramites.Departamento
import happy.tramites.EstadoTramite
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite

class RetrasadosWebController extends happy.seguridad.Shield {

    def reporteRetrasadosConsolidado() {
        println("params retra " + params)
        def datosGrafico = [:]
        def estadoR = EstadoTramite.findByCodigo("E004")
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
            //depStr="and departamento = ${departamento.id}"
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
                "and estado in (${estadoR.id},${estadoE.id}) ${usuario ? extraPersona : ''} ")

        if (pdt) {
            pdt.each { pd ->
                if(pd.tramite.externo!="1" || pd.tramite==null){
                    def resp = Tramite.findAllByAQuienContesta(pd)
                    if (resp.size() == 0) {
                        if (pd.fechaLimite < now || (!pd.fechaRecepcion))
                            datos = jerarquia(datos, pd)
                    }
                }
            }
        }

        def total = 0
        def totalSr = 0
        def hijos = datos["hijos"]

        def tabla = "<table class='table table-bordered table-condensed table-hover'>"
        tabla += "<thead>"
        tabla += "<tr>"
        tabla += "<th width='10%'></th>"
        tabla += "<th width='66%'></th>"
        tabla += "<th width='12%'>Retrasados</th>"
        tabla += "<th width='12%'>Sin recepción</th>"
        tabla += "</tr>"
        tabla += "</thead>"

        tabla += "<tbody>"

        hijos.each { lvl ->
            if (puedeVer.size() == 0 || (puedeVer.id.contains(lvl["objeto"].id))) {
                if (lvl["tramites"].size() > 0 || lvl["personas"].size() > 0) {

                    datosGrafico.put(lvl["objeto"].toString(), [:])
                    def dg = datosGrafico[lvl["objeto"].toString()]
                    dg.put("rezagados", [:])
                    dg.put("retrasados", [:])
                    dg.put("totalRz", 0)
                    dg.put("totalRs", 0)
                    dg.put("objeto", lvl["objeto"])

                    def totalNode = 0
                    def totalNodeSr = 0

                    def usuarios = ""
                    def totales = ""
                    def totalesSr = ""
                    def datosLuz = []

                    if (lvl["tramites"].size() > 0) {
                        lvl["triangulos"].each { t ->
                            usuarios += "${t} (Oficina)<br/>"
                            totales += "${lvl["rezagados"]} <br/>"
                            totalesSr += "" + lvl["retrasados"] + " <br/>"
                            datosLuz.add(["${t} (Oficina)", "${lvl.rezagados}", "${lvl.retrasados}", "Oficina"])
                            if (totalNode == 0) {
                                totalNode += lvl["rezagados"].toInteger()
                                dg["rezagados"].put("Oficina", lvl["rezagados"].toInteger())
                            }
                            if (totalNodeSr == 0) {
                                totalNodeSr += lvl["retrasados"].toInteger()
                                dg["retrasados"].put("Oficina", lvl["retrasados"].toInteger())
                            }
                        }
                    }
                    lvl["personas"].each { p ->
                        usuarios += "${p['objeto']} <br/>"
                        totales += "${p["rezagados"]} <br/>"
                        totalesSr += "" + p["retrasados"] + " <br/>"
                        dg["rezagados"].put(p['objeto'], p["rezagados"].toInteger())
                        datosLuz.add(["${p.objeto}", "${p.rezagados}", "${p.retrasados}", "${p.objeto.login}"])
                        dg["retrasados"].put(p['objeto'], p["retrasados"].toInteger())
                        totalNode += p["rezagados"].toInteger()
                        totalNodeSr += p["retrasados"].toInteger()
                    }

                    tabla += "<tr class='data dep ${totalNode > 0 ? 'rz' : ''} ${totalNodeSr > 0 ? 'rs' : ''}' data-tipo='dep' data-value='${lvl.objeto.codigo}' data-rz='${totalNode}' data-rs='${totalNodeSr}'>"
                    tabla += "<td class='titulo'>Dirección</td>"
                    tabla += "<td class='titulo'>${lvl.objeto} (${lvl.objeto.codigo})</td>"
                    tabla += "<td class='titulo numero'>${totalNode}</td>"
                    tabla += "<td class='titulo numero'>${totalNodeSr}</td>"
                    tabla += "</tr>"

//                    tabla += "<tr>"
//                    tabla += "<td class='titulo'>Usuario</td>"
//                    tabla += "<td>${usuarios}</td>"
//                    tabla += "<td class='numero'>${totales}</td>"
//                    tabla += "<td class='numero'>${totalesSr}</td>"
//                    tabla += "</tr>"
                    tabla += "<tr>"
                    tabla += "<td class='titulo' rowspan='${datosLuz.size() + 1}'>Usuario</td>"
                    tabla += "</tr>"
                    datosLuz.each { d ->
                        tabla += "<tr class='data per ${d[1] > 0 ? 'rz' : ''} ${d[2] > 0 ? 'rs' : ''}' data-tipo='per' data-value='${d[3]}' data-rz='${d[1]}' data-rs='${d[2]}'>"
                        tabla += "<td>${d[0]}</td>"
                        tabla += "<td class='numero'>${d[1]}</td>"
                        tabla += "<td class='numero'>${d[2]}</td>"
                        tabla += "</tr>"
                    }

                    dg["totalRz"] = totalNode
                    dg["totalRs"] = totalNodeSr

                    total += totalNode
                    totalSr += totalNodeSr
                }
            }
            def res = imprimeHijosPdfConsolidado(lvl, params, usuario, deps, puedeVer, total, totalSr, datosGrafico)
            total += res[0]
            totalSr += res[1]
            tabla += res[2]
        }
        tabla += "</tbody>"

        tabla += "<tfoot>"
        tabla += "<tr>"
        tabla += "<th colspan='2' class='titulo'>TOTAL</th>"
        tabla += "<th class='titulo numero'>${total}</th>"
        tabla += "<th class='titulo numero'>${totalSr}</th>"
        tabla += "</tr>"
        tabla += "</tfoot>"

        tabla += "</table>"

        params.detalle = 1

        return [tabla: tabla, params: params]
    }

    def imprimeHijosPdfConsolidado(arr, params, usuario, deps, puedeVer, total, totalSr, datosGrafico) {
        def tabla = ""
        total = 0
        totalSr = 0
        def datos = arr["hijos"]
        datos.each { lvl ->
            if (puedeVer.size() == 0 || (puedeVer.id.contains(lvl["objeto"].id))) {
                datosGrafico.put(lvl["objeto"].toString(), [:])
                def dg = datosGrafico[lvl["objeto"].toString()]
                dg.put("rezagados", [:])
                dg.put("retrasados", [:])
                dg.put("totalRz", 0)
                dg.put("totalRs", 0)
                dg.put("objeto", lvl["objeto"])

                def totalNode = 0
                def totalNodeSr = 0

                def usuarios = ""
                def totales = ""
                def totalesSr = ""
                def datosLuz = []

                if (lvl["tramites"].size() > 0) {
                    lvl["triangulos"].each { t ->
                        usuarios += "${t} (Oficina)<br/>"
                        totales += "${lvl["rezagados"]} <br/>"
                        totalesSr += "" + lvl["retrasados"] + " <br/>"
                        datosLuz.add(["${t} (Oficina)", "${lvl.rezagados}", "${lvl.retrasados}", "Oficina"])
                        if (totalNode == 0) {
                            totalNode += lvl["rezagados"].toInteger()
                            dg["rezagados"].put("Oficina", lvl["rezagados"].toInteger())
                        }
                        if (totalNodeSr == 0) {
                            totalNodeSr += lvl["retrasados"].toInteger()
                            dg["retrasados"].put("Oficina", lvl["retrasados"].toInteger())
                        }
                    }
                }
                lvl["personas"].each { p ->
                    usuarios += "${p['objeto']} <br/>"
                    totales += "${p["rezagados"]} <br/>"
                    totalesSr += "" + p["retrasados"] + " <br/>"
                    datosLuz.add(["${p.objeto}", "${p.rezagados}", "${p.retrasados}", "${p.objeto.login}"])
                    dg["rezagados"].put(p['objeto'], p["rezagados"].toInteger())
                    dg["retrasados"].put(p['objeto'], p["retrasados"].toInteger())
                    totalNode += p["rezagados"].toInteger()
                    totalNodeSr += p["retrasados"].toInteger()
                }

                tabla += "<tr class='data dep ${totalNode > 0 ? 'rz' : ''} ${totalNodeSr > 0 ? 'rs' : ''}' data-tipo='dep' data-value='${lvl.objeto.codigo}' data-rz='${totalNode}' data-rs='${totalNodeSr}'>"
                tabla += "<td class='titulo'>Dirección</td>"
                tabla += "<td class='titulo'>${lvl.objeto} (${lvl.objeto.codigo})</td>"
                tabla += "<td class='titulo numero'>${totalNode}</td>"
                tabla += "<td class='titulo numero'>${totalNodeSr}</td>"
                tabla += "</tr>"

//                tabla += "<tr>"
//                tabla += "<td class='titulo'>Usuario</td>"
//                tabla += "<td>${usuarios}</td>"
//                tabla += "<td class='numero'>${totales}</td>"
//                tabla += "<td class='numero'>${totalesSr}</td>"
//                tabla += "</tr>"
                tabla += "<tr>"
                tabla += "<td class='titulo' rowspan='${datosLuz.size() + 1}'>Usuario</td>"
                tabla += "</tr>"
                datosLuz.each { d ->
                    tabla += "<tr class='data per ${d[1] > 0 ? 'rz' : ''} ${d[2] > 0 ? 'rs' : ''}' data-tipo='per' data-value='${d[3]}' data-rz='${d[1]}' data-rs='${d[2]}'>"
                    tabla += "<td>${d[0]}</td>"
                    tabla += "<td class='numero'>${d[1]}</td>"
                    tabla += "<td class='numero'>${d[2]}</td>"
                    tabla += "</tr>"
                }

                total += totalNode
                totalSr += totalNodeSr
//                println "total "+total+"   "+totalNode
            }

            if (lvl["hijos"].size() > 0) {
                def res = imprimeHijosPdfConsolidado(lvl, params, usuario, deps, puedeVer, total, totalSr)
                total += res[0]
                totalSr += res[1]
                tabla += res[2]
            }
//            println "total des dentro "+total+"   "
        }
        return [total, totalSr, tabla]
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
        }
        lvl = datos["hijos"]
        def cod = ""
        def actual = null
//        println "padres each "+padres
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
                        if (!pdt.fechaRecepcion)
                            actual["retrasados"]++
                        else
                            actual["rezagados"]++
                        actual["tramites"].add(pdt)
                        actual["tramites"] = actual["tramites"].sort { it.fechaEnvio }
                    }

                } else {
                    if (actual["id"] == pdt.persona.departamento.id.toString()) {
                        if (actual["personas"].size() == 0) {
                            if (!pdt.fechaRecepcion)
                                actual["personas"].add(["id": pdt.persona.id.toString(), "objeto": pdt.persona, "tramites": [pdt], "retrasados": 1, "rezagados": 0])
                            else
                                actual["personas"].add(["id": pdt.persona.id.toString(), "objeto": pdt.persona, "tramites": [pdt], "retrasados": 0, "rezagados": 1])
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
                                if (!pdt.fechaRecepcion)
                                    per["retrasados"]++
                                else
                                    per["rezagados"]++
                                per["tramites"].add(pdt)
                                per["tramites"] = per["tramites"].sort { it.fechaEnvio }
                            } else {
                                if (!pdt.fechaRecepcion)
                                    actual["personas"].add(["id": pdt.persona.id.toString(), "objeto": pdt.persona, "tramites": [pdt], "retrasados": 1, "rezagados": 0])
                                else
                                    actual["personas"].add(["id": pdt.persona.id.toString(), "objeto": pdt.persona, "tramites": [pdt], "retrasados": 0, "rezagados": 1])
                                actual["personas"] = actual["personas"].sort { it.objeto.nombre }
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
                        if (!pdt.fechaRecepcion)
                            temp["retrasados"]++
                        else
                            temp["rezagados"]++
                    } else {
                        if (!pdt.fechaRecepcion)
                            temp["personas"].add(["id": pdt.persona.id.toString(), "objeto": pdt.persona, "tramites": [pdt], "retrasados": 1, "rezagados": 0])
                        else
                            temp["personas"].add(["id": pdt.persona.id.toString(), "objeto": pdt.persona, "tramites": [pdt], "retrasados": 0, "rezagados": 1])
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
