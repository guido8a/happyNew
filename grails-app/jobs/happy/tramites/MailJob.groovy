package happy.tramites

import happy.seguridad.Persona

/**
 * Created by svt on 7/3/2014.
 */
class MailJob {
    static triggers = {
        simple name: 'mail', startDelay: 1000*60, repeatInterval: 1000*60*10000
    }

    def mailService

    def execute() {
//        println "mandar mail"
//        mailService.sendMail {
//            to "valentinsvt@hotmail.com"
//            from "noreply@battle.com"
//            subject "Dame tus datos battle net"
//            body 'this is some text'
//        }
//        def estadoR = EstadoTramite.findByCodigo("E004")
//        def estadoE = EstadoTramite.findByCodigo("E003")
//        def rolPara = RolPersonaTramite.findByCodigo("R001")
//        def rolCopia = RolPersonaTramite.findByCodigo("R002")
//        def now = new Date()
//        def datos = []
//        def deps = Departamento.findAllByPadreIsNull()
//        deps.each {dep->
//            println "dep "+dep+" !!!!!!!!!!!!!!!!!!!!!!!!!   "
//           procesaDep(dep,estadoR,estadoE,rolPara,rolCopia,now)
//        }
    }

    def procesaDep(dep,estadoR,estadoE,rolPara,rolCopia,now){
        def hijos = Departamento.findAllByPadre(dep)
        hijos.each {h->
            procesaDep(h,estadoR,estadoE,rolPara,rolCopia,now)
        }
        def jefes = dep.getJefes()
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
                personas+="${p.id},"
            }
        }
        if(personas!=""){
            personas=personas.substring(0,personas.size()-2)
            println "personas "+personas
            def sql = "from PersonaDocumentoTramite where fechaEnvio is not null and  (persona in (${personas}) or departamento = ${dep.id}) and rolPersonaTramite in (${rolPara.id},${rolCopia.id}) and estado in (${estadoR.id},${estadoE.id}) "
            println "sql "+sql


            def pdt = PersonaDocumentoTramite.findAll(sql)
            if (pdt) {
                pdt.each { pd ->
                    if(pd.tramite.externo!="1" && pd.tramite.externo!=null){
                        def resp = Tramite.findAllByAQuienContesta(pd)
                        if (resp.size() == 0) {
                            if (pd.fechaLimite < now || (!pd.fechaRecepcion))
                                println "algo deberia hacer aqui"
                        }
                    }

                }
            }
        }



    }
}
