package happy.utilitarios

import happy.seguridad.Persona
import happy.tramites.Departamento
import happy.tramites.EstadoTramite
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite

class TestBeforeController {
    def mailService
    def index() {
        def estadoR = EstadoTramite.findByCodigo("E004")
        def estadoE = EstadoTramite.findByCodigo("E003")
        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCopia = RolPersonaTramite.findByCodigo("R002")
        def now = new Date()
        def datos = [:]
        def deps = Departamento.findAllByPadreIsNull()
        deps.each {dep->
            datos.put("dep",dep)
            datos.put("hijos",[])
            datos.put("tramites",[])
            datos.put("total",0)
            procesaDep(dep,estadoR,estadoE,rolPara,rolCopia,now,datos)
        }
        //println "datos "+datos
        def count = getCount(datos,0)
//        println datos["dep"]
//        println "retrasados: "+datos["tramites"]+"  "+datos["total"]


//        def mensaje ="Correo electronico generado automaticamente.\nTramites retrasados el ${now.format('dd-MM-yyyy')}."
//        mensaje+="${datos['dep']} [ "+datos["total"]+" ] tramites."
//        Persona.findAllByActivo(1).each {pr->
//            if(pr.puedeDirector){
////                println "es director "+pr
////                println "mensaje "+mensaje
////                mailService.sendMail {
////                    //to pr.mail
////                    to "valentinsvt@hotmail.com"
////                    from "correo@correo.com"
////                    subject "Reporte de tramites retrasados del ${now.format('dd-MM-yyyy')}."
////                    body mensaje
////                }
//            }
//        }

        datos["hijos"].each {d->
            imprimeDatos(d,count)
        }
        render "ok"
    }

    def procesaDep(dep,estadoR,estadoE,rolPara,rolCopia,now,datos){

        def lvl = datos["hijos"]
        def res = ["dep":dep]
        res.put("hijos",[])
        res.put("tramites",[])
        res.put("total",0)

        def cursor = res
        def hijos = Departamento.findAllByPadre(dep,[sort:"id"])
        hijos.each {h->
            procesaDep(h,estadoR,estadoE,rolPara,rolCopia,now,cursor)
        }

        //def tramites = Tramite.findAll("from Tramite where deDepartamento = ${dep.id} and (externo!='1' or externo is null)")
//        def tramites  = Tramite.withCriteria {
//            or {
//                eq("deDepartamento", dep)
//                de {
//                    eq("departamento", dep)
//                }
//            }
//            or {
//                ne("externo", '1')
//                isNull("externo")
//            }
//        }
        def personas =""
        Persona.findAllByDepartamento(dep).each {p->
            if(p.estaActivo){
               // println "esta activo  ${p.departamento.codigo}  "+p+"  "+p.id
                personas+="${p.id},"
            }
        }
        if(personas!=""){
            personas=personas.substring(0,personas.size()-1)
           // println "dep con personas "+personas+" !!!!!!!!!!!!!!!!!!!!!!!!!   "
            //println "personas "+personas
            def sql = "from PersonaDocumentoTramite where fechaEnvio is not null and fechaRecepcion is not null and  (persona in (${personas}) or departamento = ${dep.id}) and rolPersonaTramite in (${rolPara.id},${rolCopia.id}) and estado in (${estadoR.id}) "
            //println "sql "+sql


            def pdt = PersonaDocumentoTramite.findAll(sql)
            if (pdt) {
                pdt.each { pd ->
                    if(pd.tramite.externo!="1" && pd.tramite.externo!=null){
                        def resp = Tramite.findAllByAQuienContesta(pd)
                        if (resp.size() == 0) {
                            if (pd.fechaLimiteRespuesta<now)
                                cursor["tramites"].add(pd)
                        }
                    }

                }
            }
        }

        lvl.add(res)

    }

    def getCount(lvl,count){
        // println "get count  ${lvl['dep']}  "+count
        def actual = 0
        lvl["hijos"].each {h->
            actual+=getCount(h,count)
        }

        if(lvl["tramites"].size()>0){
//            count+=lvl["tramites"].size()
//            println "sumo "+lvl["tramites"].size()
//            lvl["tramites"].each {
//                println " "+it.tramite.codigo+"  "+it.persona+"  "+it.departamento
//            }
            actual= lvl["tramites"].size()
        }
        //println "---> return ${lvl['dep']} "+count
        lvl["total"]=actual
        return actual

    }

    def imprimeDatos(lvl,count){
        def texto = ""
        def now=new Date();
        lvl["hijos"].each {h->
            texto=imprimeDatos(h,count)+"\n"+texto
        }
        if(lvl["total"]>0) {
            println "Enviar email con " + "${lvl['dep']} [ " + lvl["total"] + " ] tramites.<br/>"
            def jefes = lvl['dep'].getJefes()
            def mensaje ="Correo electronico generado automaticamente.\nTramites retrasados para el ${now.format('dd-MM-yyyy')}.\n"
            texto="" + "${lvl['dep']} [ " + lvl["total"] + " ] tramites.\n"+texto
            jefes.each{j->
//                mailService.sendMail {
//                    //to pr.mail
//                    to "valentinsvt@hotmail.com","guido8a@gmail.com"
//                    from "correo@correo.com"
//                    subject "Reporte de tramites retrasados para el ${now.format('dd-MM-yyyy')}."
//                    body mensaje+texto
//                }
            }
//            lvl["tramites"].sort{it.persona}
//            lvl["tramites"].each{t->
//                println " "+t.tramite.codigo+" "+t.persona?.departamento?.codigo+":"+t.persona?.login+"  "+t.departamento+"  "+t.fechaEnvio+"  "+t.fechaRecepcion+"  "+t.estado.descripcion
//
//            }
//            println "aqui va email para ${lvl['dep']} "
//            println texto
//            println "--------------------------------  "
        }
        return texto

    }

    def fixTramites(){
        Tramite.list().each {t->
            def codigo = t.codigo
            def parts = codigo.split("-")
            //println "parts 2 "+parts[2]+" "+t.codigo
            def dep = Departamento.findByCodigo(parts[2])
            //println "encontro "+dep
            t.departamento=dep
            t.save(flush: true)
        }
    }

    def fixDepartamentos(){
        def deps = Departamento.list([sort:"id"])
        deps.eachWithIndex {d,i->
            if(d.codigo=~"N.A" || d.codigo=~"COD")
            d.codigo="COD-"+i
            d.save(flush: true)
        }
    }


    def testValidate(){
//        def pdt = PersonaDocumentoTramite.get(968)
//        pdt.persona=null
//        pdt.departamento=null
//        println "validate " +pdt.validate()
//        println "errors "+pdt.errors
//        pdt.save(flush: true)
//        println "-----------------------------"
//        pdt=new PersonaDocumentoTramite()
//        pdt.tramite=Tramite.get(282)
//        pdt.rolPersonaTramite=RolPersonaTramite.get(1)
//        pdt.save(flush: true)
        println "inicio"
        def t = Tramite.get(311)
        t.aQuienContesta=null
        t.save(flush: true)
        println "fin "
        render "ok"

    }

}
