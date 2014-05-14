package happy.tramites

import happy.seguridad.Persona


class BloqueosJob {
    static triggers = {
        simple name: 'bloqueoBandejaSalida', startDelay: 1000*60, repeatInterval: 1000*60*3
    }

    def execute() {
        // execute job

        def ahora = new Date()
        println "----------------------------------"
        println "bloqueo bandeja salida!!! "+ahora
        def bloquear = []
        def bloquearUsu = []
        def warning = []
        def warningUsu = []
        PersonaDocumentoTramite.findAllByFechaEnvioIsNotNullAndFechaRecepcionIsNull().each {pdt->
//            println "PDT "+pdt.id+" tramite "+pdt.tramite.id +" : "+pdt.tramite.codigo+" envio "+pdt.fechaEnvio.format("dd-MM-yyyy hh:mm")+" bloqueo "+pdt.fechaBloqueo?.format("dd-MM-yyyy hh:mm")+"   "+pdt.rolPersonaTramite.codigo
            def fechaBloqueo = pdt.fechaBloqueo
            if(fechaBloqueo && fechaBloqueo<ahora){
                if(pdt.rolPersonaTramite.codigo!="E004" && pdt.rolPersonaTramite.codigo!="I005" ){
                    if(pdt.tramite.deDepartamento){
                        if(!warning.id.contains(pdt.tramite.deDepartamento.id))
                            warning.add(pdt.tramite.deDepartamento)
                    }else{
                        if(!warningUsu.id.contains(pdt.tramite.de.id))
                            warningUsu.add(pdt.tramite.de)
                    }

                    if(pdt.persona){
//                       println "add bloquear "+pdt.persona
                        if(!bloquearUsu.id.contains(pdt.persona.id))
                            bloquearUsu.add(pdt.persona)
                    }else{
//                        println "add bloquear "+pdt.departamento
                        if(!bloquear.id.contains(pdt.departamento.id))
                            bloquear.add(pdt.departamento)
                    }
                }

            }
        }
        Departamento.list().each {dep->
            if(bloquear.id.contains(dep.id)){
//                println "bloqueando dep "+dep
                dep.estado="B"
                if(!dep.save(flush: true))
                    println "errores save dep "+dep.errors

            }else{
                if(warning.id.contains(dep.id)){
                    dep.estado="W"
                    dep.save(flush: true)
                }else{
                    if(dep.estado!=""){
                        dep.estado=""
                        dep.save(flush: true)
                    }
                }

            }
        }
        Persona.findAllByEstadoInList(["B","W"]).each {
            it.estado=""
            it.save()
        }
        bloquearUsu.each {
//            println "bloqueando usu "+it
            it.estado="B"
            it.save()
        }
        warningUsu.each {
            it.estado="B"
            it.save()
        }


        println "fin bloqueo bandeja salida "+new Date()
    }
    def executeRecibir(){
        def ahora = new Date()
        println "----------------------------------"
        println "bloqueo bandeja recibir!!! "+ahora
        def bloquear = []
        def bloquearUsu = []
        def warning = []
        def warningUsu = []
        PersonaDocumentoTramite.findAllByFechaEnvioIsNotNullAndFechaRecepcionIsNull().each {pdt->
            def fechaBloqueo = pdt.fechaBloqueo
            if(fechaBloqueo && fechaBloqueo<ahora){
                if(pdt.rolPersonaTramite.codigo!="E004" && pdt.rolPersonaTramite.codigo!="I005" ){
//                    println "PDT "+pdt.id+" tramite "+pdt.tramite.id +" : "+pdt.tramite.codigo+" envio "+pdt.fechaEnvio.format("dd-MM-yyyy hh:mm")+" bloqueo "+pdt.tramite.fechaBloqueo?.format("dd-MM-yyyy hh:mm")

                    if(pdt.tramite.deDepartamento){
                        if(!warning.id.contains(pdt.tramite.deDepartamento.id))
                            warning.add(pdt.tramite.deDepartamento)
                    }else{
                        if(!warningUsu.id.contains(pdt.tramite.de.id))
                            warningUsu.add(pdt.tramite.de)
                    }

                    if(pdt.persona){
//                        println "add bloquear "+pdt.persona
                        if(!bloquearUsu.id.contains(pdt.persona.id))
                            bloquearUsu.add(pdt.persona)
                    }else{
//                        println "add bloquear "+pdt.departamento
                        if(!bloquear.id.contains(pdt.departamento.id))
                            bloquear.add(pdt.departamento)
                    }
                }

            }
        }
        Departamento.list().each {dep->
            if(bloquear.id.contains(dep.id)){
//                println "bloqueando dep "+dep
                dep.estado="B"
                if(!dep.save(flush: true))
                    println "errores save dep "+dep.errors

            }else{
                if(warning.id.contains(dep.id)){
                    dep.estado="W"
                    dep.save(flush: true)
                }else{
                    if(dep.estado!=""){
                        dep.estado=""
                        dep.save(flush: true)
                    }
                }

            }
        }
        Persona.findAllByEstadoInList(["B","W"]).each {
            it.estado=""
            it.save()
        }
        bloquearUsu.each {
            println "bloqueando usu "+it
            it.estado="B"
            it.save()
        }
        warningUsu.each {
            it.estado="B"
            it.save()
        }


        println "fin bloqueo bandeja salida recibir "+new Date()
    }
}
