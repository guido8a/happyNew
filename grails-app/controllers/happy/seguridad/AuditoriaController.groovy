package happy.seguridad

//import kerberos.Krbs

class AuditoriaController extends Shield{

    def audt(){
        if (session.usuario.puedeAdmin) {
            def operaciones = ["UPDATE": "Update", "INSERT": "Insert", "DELETE": "Delete", "-1": "Todas"]
            def domains = grailsApplication.getArtefacts("Domain")*.fullName
            def comboDomain = [:]
            domains = domains.sort { it.split("\\.")[(it.split("\\.").size() - 1)] }
            comboDomain.put("-1","Todos")
            domains.each {
                if (!(it =~ "kerberos") && !(it =~ "ErrorLog") && !(it =~ "Sistema")) {
                    def texto = it.split("\\.")
                    comboDomain.put(it, texto[2])
                }
            }
            [operaciones: operaciones, domains: comboDomain]
        }else{
            flash.message = "Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será registrada."
            response.sendError(403)
        }

    }

    def tablaAudt(){
        //println "params "+params
        def desde = new Date().parse("dd-MM-yyyy HH:mm:ss",params.desde+" 00:00:01")
        def hasta = new Date().parse("dd-MM-yyyy HH:mm:ss",params.hasta+" 23:59:59")
        def dominio =null
        def max = 100
        def show = 20
        def offset = 0
        def resFin
        def maxView = 0
        if(params.maxView && params.maxView!="")
            maxView=params.maxView.toInteger()
        if(params.offset && params.offset!="")
            offset=params.offset.toInteger()
        if(params.domain!="-1"){
            dominio= params.domain.split("\\.")[(params.domain.split("\\.").size()-1)]
        }
//        println "!! desde "+desde+" hasta "+hasta+" op "+params.operacion+"  dom "+params.domain+" usu "+params.usuario
        def c = Krbs.createCriteria()
        //println "max "+(max+offset)
        def res =c.list (max: max+offset, offset: offset) {
            between("fecha",desde,hasta)
            if(params.operacion!='-1')
                eq("operacion",params.operacion)
            if(params.usuario && params.usuario!="")
                ilike("usuario","%"+params.usuario+"%")
            if(dominio)
                eq("dominio","class "+params.domain)
            order("fecha","desc")
        }
        //println "size "+res.size()
        if(maxView==0)
            maxView=res.size()
        else {
            if (res.size() > 80)
                maxView+=res.size()-maxView
        }
        if(res.size()>show)
            resFin = res[1..show]
        else
            resFin=res
       // println "maxView "+maxView+" show  "+show+" offset "+offset
        def rango = maxView/show
        rango=Math.ceil(rango)
        //println "div "+rango
        rango=1..rango.toInteger()
        def heigth = 35
        if(rango.size()>28)
            heigth=(Math.ceil(rango.size()/28))*36
        //println " h ${heigth} ${(Math.ceil(rango.size()/28))}  ${rango.size()} rango "+rango


        [res:resFin,dominio:dominio,maxView:maxView,show:show,offset: offset,desde:params.desde,hasta:params.hasta,usuario:params.usuario,domain:params.domain,operacion:params.operacion,rango:rango,heigth:heigth]


    }

}
