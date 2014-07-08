package happy.seguridad

import kerberos.Krbs

class AuditoriaController extends Shield{

    def audt(){
        def operaciones = ["UPDATE":"Update","Insert":"INSERT","DELETE":"Delete","-1":"Todas"]
        def domains = grailsApplication.getArtefacts("Domain")*.fullName
        def comboDomain = [:]
        domains=domains.sort{it.split("\\.")[(it.split("\\.").size()-1)]}
        domains.each {
            if(!(it =~"kerberos") && !(it =~"ErrorLog") && !(it =~"Sistema")){
                def texto = it.split("\\.")
                comboDomain.put(it,texto[2])
            }
        }
        [operaciones:operaciones,domains:comboDomain]

    }

    def tablaAudt(){
        def desde = new Date().parse("dd-MM-yyyy HH:mm:ss",params.desde+" 00:00:01")
        def hasta = new Date().parse("dd-MM-yyyy HH:mm:ss",params.hasta+" 23:59:59")
        def dominio = params.domain.split("\\.")[(params.domain.split("\\.").size()-1)]
//        println "!! desde "+desde+" hasta "+hasta+" op "+params.operacion+"  dom "+params.domain+" usu "+params.usuario
        def res = Krbs.withCriteria {
            between("fecha",desde,hasta)
            if(params.operacion!='-1')
                eq("operacion",params.operacion)
            if(params.usuario && params.usuario!="")
                ilike("usuario","%"+params.usuario+"%")
            eq("dominio","class "+params.domain)
        }
        //println "res "+res
        [res:res,dominio:dominio]


    }

}
