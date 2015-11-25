package happy

import grails.transaction.Transactional
import happy.seguridad.Persona
import happy.tramites.Departamento

@Transactional
class ReportesTramitesRetrasadosService {

    def dbConnectionService
    static transactional = false

    def departamentosPuedeVer(depId) {
        def depStr = ""
        if (depId) {
            def departamento = Departamento.get(depId)
//            def padre = departamento.padre
//            while (padre) {
//                padre = padre.padre
//            }
//            println "padre: " + padre
            depStr += departamento.id
            def hi = Departamento.findAllByPadre(departamento)
            while (hi.size() > 0) {
                depStr += "," + hi.id.join(",")
                hi = Departamento.findAllByPadreInList(hi)
            }
        }
        return depStr
    }

    def armaSqls(depStr, perId) {
        def sqlRet = null, sqlNoRec = null
        if (depStr && !perId) {
            // la lista de tramites retrasados se hace por el que recibe: dptopara, prsnpaid
            sqlRet = "SELECT * FROM trmt_retrasado() WHERE dptopara IN ($depStr)"


            // la lista de trámites no recibidos se hace por quien envio: dpto__de, prsndeid y dptodedp = null
            sqlNoRec = "SELECT * FROM trmt_no_recibidos() WHERE dpto__de IN ($depStr)"
        } else if (perId && !depStr) {
            // la lista de tramites retrasados se hace por el que recibe: dptopara, prsnpaid
            sqlRet = "SELECT * FROM trmt_retrasado() WHERE prsnpaid = $perId"
            // la lista de trámites no recibidos se hace por quien envio: dpto__de, prsndeid y dptodedp = null
            sqlNoRec = "SELECT * FROM trmt_no_recibidos() WHERE prsndeid = $perId AND dptodedp IS NULL"
        } else if (depStr && perId) {
            // la lista de tramites retrasados se hace por el que recibe: dptopara, prsnpaid
            sqlRet = "SELECT * FROM trmt_retrasado() WHERE (dptopara IN ($depStr) AND prsnpara = 'Oficina') OR prsnpaid = $perId"
            // la lista de trámites no recibidos se hace por quien envio: dpto__de, prsndeid y dptodedp = null
            sqlNoRec = "SELECT * FROM trmt_no_recibidos() WHERE (dpto__de IN ($depStr) AND dptodedp IS NOT NULL) OR (prsndeid = $perId AND dptodedp IS NULL)"
        }

//        println("sql " + sqlRet)

        return [ret: sqlRet, norec: sqlNoRec]
    }

//    def datosGraph(depId, perId) {
//        def depStr = null
//
//        if (depId) {
//            depStr = departamentosPuedeVer(depId)
//        }
//
//        def sqls = armaSqls(depStr, perId)
//
//        def cn = dbConnectionService.getConnection()
//
////        println "GRAPH"
////        println sqls.ret
////        println sqls.norec
//
//        def resGraph = [:]
//        resGraph.ret = [:]
//        resGraph.norec = [:]
//        if (sqls.ret) {
//            cn.eachRow(sqls.ret.toString()) { r2 ->
//                def r = r2.toRowResult()
//                r.tipo = "ret"
//                // la lista de tramites retrasados se hace por el que recibe: dptopara
//                def dptoPara = "" + r.dptopara
//                def dep = null
//                // la lista de tramites retrasados se hace por el que recibe:  dptopara, dptopacd, dptopads, prsnpaid, prsnpara
//                if (!resGraph.ret[dptoPara]) {
//                    dep = Departamento.get(dptoPara.toLong())
//                    resGraph.ret[dptoPara] = [:]
//                    resGraph.ret[dptoPara].nombre = dep.descripcion
//                    resGraph.ret[dptoPara].codigo = dep.codigo
//                    resGraph.ret[dptoPara].total = 0
//                    resGraph.ret[dptoPara].det = [:]
//                }
//                resGraph.ret[dptoPara].total++
//                if (r.dptopacd) {
//                    //es para la oficina
//                    if (!resGraph.ret[dptoPara].det.oficina) {
//                        resGraph.ret[dptoPara].det.oficina = [:]
//                        resGraph.ret[dptoPara].det.oficina.nombre = "Oficina "
//                        resGraph.ret[dptoPara].det.oficina.total = 0
//                    }
//                    resGraph.ret[dptoPara].det.oficina.total++
//                } else {
//                    //es para persona
//                    def prsnPara = "" + r.prsnpaid
//                    if (!resGraph.ret[dptoPara].det[prsnPara]) {
//                        def prsn = Persona.get(prsnPara.toLong())
//                        resGraph.ret[dptoPara].det[prsnPara] = [:]
//                        resGraph.ret[dptoPara].det[prsnPara].nombre = prsn.login
//                        resGraph.ret[dptoPara].det[prsnPara].total = 0
//                    }
//                    resGraph.ret[dptoPara].det[prsnPara].total++
//                }
//            }
//        }
//
//        if (sqls.norec) {
//            cn.eachRow(sqls.norec.toString()) { r2 ->
//                def r = r2.toRowResult()
//                r.tipo = "norec"
//                // la lista de trámites no recibidos se hace por quien envio: dpto__de
//                def dptoDe = "" + r.dpto__de
//                def dep = null
//                // la lista de trámites no recibidos se hace por quien envio: dptodedp, dpto__de, dptodecd, dptodeds, prsndeid, prsn__de
//                if (!resGraph.norec[dptoDe]) {
//                    dep = Departamento.get(dptoDe.toLong())
//                    resGraph.norec[dptoDe] = [:]
//                    resGraph.norec[dptoDe].nombre = dep.descripcion
//                    resGraph.norec[dptoDe].codigo = dep.codigo
//                    resGraph.norec[dptoDe].total = 0
//                    resGraph.norec[dptoDe].det = [:]
//                }
//                resGraph.norec[dptoDe].total++
//                if (r.dptodedp) {
//                    //es del dpto
//                    if (!resGraph.norec[dptoDe].det.oficina) {
//                        resGraph.norec[dptoDe].det.oficina = [:]
//                        resGraph.norec[dptoDe].det.oficina.nombre = "Oficina "
//                        resGraph.norec[dptoDe].det.oficina.total = 0
//                    }
//                    resGraph.norec[dptoDe].det.oficina.total++
//                } else {
//                    //es de persona
//                    def prsnDe = "" + r.prsndeid
//                    if (!resGraph.norec[dptoDe].det[prsnDe]) {
//                        def prsn = Persona.get(prsnDe.toLong())
//                        resGraph.norec[dptoDe].det[prsnDe] = [:]
//                        resGraph.norec[dptoDe].det[prsnDe].nombre = prsn.login
//                        resGraph.norec[dptoDe].det[prsnDe].total = 0
//                    }
//                    resGraph.norec[dptoDe].det[prsnDe].total++
//                }
//            }
//        }
//
//        cn.close()
//        return resGraph
//    }


    def buscarHijos (padre) {


        def dptoPadre = Departamento.get(padre)
        def dptosHijos = Departamento.findAllByPadreAndActivo(dptoPadre, 1).id

        return [hijos: dptosHijos]

    }


    def datos(depId) {
        return datos(depId, null)
    }

    def datosPersona(perId) {
        return datos(null, perId)
    }

    def datos(depId, perId) {
        def depStr = null

        if (depId) {
            depStr = departamentosPuedeVer(depId)
        }

        def sqls = armaSqls(depStr, perId)

        def cn = dbConnectionService.getConnection()

//        println "TABLAS"
//        println sqls.ret
//        println sqls.norec

        def resGraph = [:]
        resGraph.ret = [:]
        resGraph.norec = [:]

        def res = [:]
        if (sqls.ret) {
            cn.eachRow(sqls.ret.toString()) { r2 ->
                def r = r2.toRowResult()
                r.tipo = "ret"
//            println r
                /*
                    trmt__id:539,                               trmt__id:543,
                    trmtcdgo:MEM-1-PAT-15,                      trmtcdgo:MEM-17-GSTI-15,
                    dpto__de:750,                               dpto__de:734,
                    dptodecd:PAT,                               dptodecd:GSTI,
                    dptodeds:PATRONATO,                         dptodeds:GESTION DE SISTEMAS Y TECNOLOGIAS DE INFORMACION,
                    prsndeid:5652,                              prsndeid:5519,
                    prsn__de:Sara Silva,                        prsn__de:Margarita Elizabeth Espinosa Burbano,
                    dptopara:750,                               dptopara:734,
                    dptopacd:null,                              dptopacd:null,
                    dptopads:null,                              dptopads:null,
                    prsnpaid:null,                              prsnpaid:null,
                    prsnpara:Oficina,                           prsnpara:Oficina,
                    trmtfccr:2015-06-16 13:32:28.397,           trmtfccr:2015-06-23 16:25:04.728,
                    trmtfcen:2015-06-16 13:32:54.769,           trmtfcen:2015-06-23 16:25:38.07,
                    trmtfcrc:2015-06-23 16:24:02.543,           trmtfcrc:2015-06-24 09:45:01.209,
                    trmtfclr:2015-06-25 16:24:00.0,             trmtfclr:2015-06-26 09:45:00.0,
                    dptopdre:11,                                dptopdre:728,
                    dptoprds:PREFECTURA,                        dptoprds:GESTION DE SISTEMAS Y TECNOLOGIAS DE INFORMACION,
                    rltr:PARA,                                  rltr:COPIA,
                    ruta:11,                                    ruta:11,728,
                    nivel:1                                     nivel:2
                 */

                // parte 1: mapa para hacer las tablas
                def cadena = r.ruta.split(",")
                // la lista de tramites retrasados se hace por el que recibe: dptopara
                cadena += r.dptopara

                def f = cadena.first()
                cadena = cadena[1..cadena.size() - 1]
//            println "de: ${r.dpto__de} (${r.dptodecd})   padre: ${r.dptopdre}   ruta: ${r.ruta}    nivel: ${r.nivel}   cadena: ${cadena}"

                def k = "" + f
                if (!res[k]) {
                    res[k] = [:]
                    res[k].lvl = 0
                    res[k].totalRet = 0
                    res[k].trams = [:]
                    res[k].deps = [:]
                }
                def rr = res[k]
                def resTemp = rr.deps
                cadena.eachWithIndex { p, i ->
                    k = "" + p
                    if (!resTemp[k]) {
                        resTemp[k] = [:]
                        resTemp[k].lvl = i + 1
                        resTemp[k].totalRet = 0
                        resTemp[k].trams = [:]
                        resTemp[k].deps = [:]
                    }
                    rr.totalRet++
                    rr = resTemp[k]
                    resTemp = rr.deps
                }
                rr.totalRet++

                // la lista de tramites retrasados se hace por el que recibe:  dptopara, dptopacd, dptopads, prsnpaid, prsnpara
//            println "\t" + r.dptopara + "  " + r.dptopacd + "  " + r.dptopads + "  " + r.prsnpaid + "  " + r.prsnpara
                if (r.dptopacd) {
                    //es para la oficina
                    if (!rr.trams.oficina) {
                        rr.trams.oficina = [:]
                        rr.trams.oficina.nombre = "Oficina"
                        rr.trams.oficina.totalRet = 0
                        rr.trams.oficina.trams = []
                    }
                    rr.trams.oficina.totalRet++
                    rr.trams.oficina.trams += r
                } else {
                    //es para persona
                    def kp = "" + r.prsnpaid
                    if (!rr.trams[kp]) {
                        rr.trams[kp] = [:]
                        rr.trams[kp].nombre = r.prsnpara
                        rr.trams[kp].totalRet = 0
                        rr.trams[kp].trams = []
                    }
                    rr.trams[kp].totalRet++
                    rr.trams[kp].trams += r
                }
//            rr.trams += r

                //parte 2: mapa para los graficos
                // la lista de tramites retrasados se hace por el que recibe: dptopara
                def dptoPara = "" + r.dptopara
                // la lista de tramites retrasados se hace por el que recibe:  dptopara, dptopacd, dptopads, prsnpaid, prsnpara
                if (!resGraph.ret[dptoPara]) {
                    def dep = Departamento.get(dptoPara.toLong())
                    resGraph.ret[dptoPara] = [:]
                    resGraph.ret[dptoPara].nombre = dep.descripcion
                    resGraph.ret[dptoPara].codigo = dep.codigo
                    resGraph.ret[dptoPara].total = 0
                    resGraph.ret[dptoPara].det = [:]
                }
                resGraph.ret[dptoPara].total++
                if (r.dptopacd) {
                    //es para la oficina
                    if (!resGraph.ret[dptoPara].det.oficina) {
                        resGraph.ret[dptoPara].det.oficina = [:]
                        resGraph.ret[dptoPara].det.oficina.nombre = "Oficina "
                        resGraph.ret[dptoPara].det.oficina.total = 0
                    }
                    resGraph.ret[dptoPara].det.oficina.total++
                } else {
                    //es para persona
                    def prsnPara = "" + r.prsnpaid
                    if (!resGraph.ret[dptoPara].det[prsnPara]) {
//                        def prsn = Persona.get(prsnPara.toLong())
                        resGraph.ret[dptoPara].det[prsnPara] = [:]
                        resGraph.ret[dptoPara].det[prsnPara].nombre = r.prsnpalg
                        resGraph.ret[dptoPara].det[prsnPara].total = 0
                    }
                    resGraph.ret[dptoPara].det[prsnPara].total++
                }
            }
        }

        if (sqls.norec) {
            cn.eachRow(sqls.norec.toString()) { r2 ->
                def r = r2.toRowResult()
                r.tipo = "norec"
//            println r
                /*
                    trmt__id:540,                                               trmt__id:520,
                    trmtcdgo:MEM-4-DGEF-15,                                     trmtcdgo:MEM-8-GSTI-15,
                    dpto__de:740,                                               dpto__de:734,
                    dptodecd:DGEF,                                              dptodecd:GSTI,
                    dptodeds:DIRECCION DE GESTION ECONOMICA Y FINANCIERA,       dptodeds:GESTION DE SISTEMAS Y TECNOLOGIAS DE INFORMACION,
                    dptodedp:740,                                               dptodedp:null,
                    prsndeid:5643,                                              prsndeid:5517,
                    prsn__de:Janeth Elizabeth Pérez Onofa,                      prsn__de:Adriana Michell Cardenas Cueva,
                    dptopara:750,                                               dptopara:null,
                    dptopacd:PAT,                                               dptopacd:null,
                    dptopads:PATRONATO,                                         dptopads:null,
                    prsnpaid:null,                                              prsnpaid:5512,
                    prsnpara:null,                                              prsnpara:Angel Miguel Barragan Ponton,
                    trmtfccr:2015-06-16 16:02:21.315,                           trmtfccr:2015-06-02 11:09:15.821,
                    trmtfcen:2015-06-16 16:02:50.087,                           trmtfcen:2015-06-02 11:09:33.138,
                    trmtfclm:2015-06-17 08:32:00.0,                             trmtfclm:2015-06-02 12:09:33.138,
                    dptopdre:11,                                                dptopdre:728,
                    dptoprds:PREFECTURA,                                        dptoprds:GESTION DE SISTEMAS Y TECNOLOGIAS DE INFORMACION,
                    rltr:PARA,                                                  rltr:PARA,
                    ruta:11,                                                    ruta:11,728,
                    nivel:1                                                     nivel:2
                 */
                //parte 1: mapa para las tablas
                def cadena = r.ruta.split(",")
                // la lista de trámites no recibidos se hace por quien envio: dpto__de
                cadena += r.dpto__de

                def f = cadena.first()
                cadena = cadena[1..cadena.size() - 1]
//            println "de: ${r.dpto__de} (${r.dptodecd})   padre: ${r.dptopdre}   ruta: ${r.ruta}    nivel: ${r.nivel}   cadena: ${cadena}"

                def k = "" + f
                if (!res[k]) {
                    res[k] = [:]
                    res[k].lvl = 0
                    res[k].totalNoRec = 0
                    res[k].trams = [:]
                    res[k].deps = [:]
                } else {
                    if (!res[k].totalNoRec) {
                        res[k].totalNoRec = 0
                    }
                }
                def rr = res[k]
                def resTemp = rr.deps
                cadena.eachWithIndex { p, i ->
                    k = "" + p
                    if (!resTemp[k]) {
                        resTemp[k] = [:]
                        resTemp[k].lvl = i + 1
                        resTemp[k].totalNoRec = 0
                        resTemp[k].trams = [:]
                        resTemp[k].deps = [:]
                    } else {
                        if (!resTemp[k].totalNoRec) {
                            resTemp[k].totalNoRec = 0
                        }
                    }
                    rr.totalNoRec++
                    rr = resTemp[k]
                    resTemp = rr.deps
                }
                rr.totalNoRec++

                // la lista de trámites no recibidos se hace por quien envio: dptodedp, dpto__de, dptodecd, dptodeds, prsndeid, prsn__de
//            println "\t" + r.dptodedp + "  " + r.dpto__de + "  " + r.dptodecd + "  " + r.dptodeds + "  " + r.prsndeid + "  " + r.prsn__de
                if (r.dptodedp) {
                    //es del dpto
                    if (!rr.trams.oficina) {
                        rr.trams.oficina = [:]
                        rr.trams.oficina.nombre = "Oficina"
                        rr.trams.oficina.trams = []
                    }
                    if (!rr.trams.oficina.totalNoRec) {
                        rr.trams.oficina.totalNoRec = 0
                    }
                    rr.trams.oficina.totalNoRec++
                    rr.trams.oficina.trams += r
                } else {
                    //es de persona
                    def kp = "" + r.prsndeid
                    if (!rr.trams[kp]) {
                        rr.trams[kp] = [:]
                        rr.trams[kp].nombre = r.prsn__de
                        rr.trams[kp].trams = []
                    }
                    if (!rr.trams[kp].totalNoRec) {
                        rr.trams[kp].totalNoRec = 0
                    }
                    rr.trams[kp].totalNoRec++
                    rr.trams[kp].trams += r
                }

//            rr.trams += r

                //parte 2: mapa para los graficos
                // la lista de trámites no recibidos se hace por quien envio: dpto__de
                def dptoDe = "" + r.dpto__de
                // la lista de trámites no recibidos se hace por quien envio: dptodedp, dpto__de, dptodecd, dptodeds, prsndeid, prsn__de
                if (!resGraph.norec[dptoDe]) {
                    def dep = Departamento.get(dptoDe.toLong())
                    resGraph.norec[dptoDe] = [:]
                    resGraph.norec[dptoDe].nombre = dep.descripcion
                    resGraph.norec[dptoDe].codigo = dep.codigo
                    resGraph.norec[dptoDe].total = 0
                    resGraph.norec[dptoDe].det = [:]
                }
                resGraph.norec[dptoDe].total++
                if (r.dptodedp) {
                    //es del dpto
                    if (!resGraph.norec[dptoDe].det.oficina) {
                        resGraph.norec[dptoDe].det.oficina = [:]
                        resGraph.norec[dptoDe].det.oficina.nombre = "Oficina "
                        resGraph.norec[dptoDe].det.oficina.total = 0
                    }
                    resGraph.norec[dptoDe].det.oficina.total++
                } else {
                    //es de persona
                    def prsnDe = "" + r.prsndeid
                    if (!resGraph.norec[dptoDe].det[prsnDe]) {
//                        def prsn = Persona.get(prsnDe.toLong())
                        resGraph.norec[dptoDe].det[prsnDe] = [:]
                        resGraph.norec[dptoDe].det[prsnDe].nombre = r.prsndelg
                        resGraph.norec[dptoDe].det[prsnDe].total = 0
                    }
                    resGraph.norec[dptoDe].det[prsnDe].total++
                }
            }
        }
//        println res
        cn.close()
        return [res: res, resGraph: resGraph]
    }
}
