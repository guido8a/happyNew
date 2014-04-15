package happy.tramites



class BloqueosJob {
    static triggers = {
        simple name: 'bloqueoBandejaSalida', startDelay: 1000*60, repeatInterval: 1000*60*5
    }

    def execute() {
        // execute job
        /*todo cambiar esto*/
        def ahora = new Date()
        println "bloqueo bandeja salida "+ahora
        def bloquear = []
        def warning = []
        PersonaDocumentoTramite.findAllByFechaEnvioIsNotNullAndFechaRecepcionIsNull().each {pdt->
            def fechaBloqueo = pdt.tramite.fechaBloqueo
            if(fechaBloqueo && fechaBloqueo<ahora){
//                println "PDT "+pdt.id+" tramite "+pdt.tramite.id +" : "+pdt.tramite.codigo+" envio "+pdt.fechaEnvio.format("dd-MM-yyyy hh:mm")+" bloqueo "+pdt.tramite.fechaBloqueo?.format("dd-MM-yyyy hh:mm")
//                println "add bloquear "+pdt.tramite.de.departamento.codigo
                bloquear.add(pdt.tramite.de.departamento)
                if(pdt.departamento)
                    warning.add(pdt.departamento)
            }
        }
        Departamento.list().each {dep->
            if(bloquear.id.contains(dep.id)){
                println "bloqueando "+dep
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
        println "fin bloqueo bandeja salida "+new Date()
    }
}
