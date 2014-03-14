package happy.tramites



class BloqueosJob {
    static triggers = {
        simple name: 'bloqueoBandejaSalida', startDelay: 1000*60, repeatInterval: 500*60
    }

    def execute() {
        // execute job
        /*todo cambiar esto*/
        def ahora = new Date()
        //println "bloqueo bandeja salida "+ahora
        def bloquear = []
        def warning = []
        PersonaDocumentoTramite.findAllByFechaEnvioIsNotNullAndFechaRecepcionIsNull().each {pdt->
            //println "pdt --> "+pdt.id+" tramite "+pdt.tramite.id+" - ${pdt.tramite.de.departamento.descripcion} "+pdt.fechaEnvio+"  "+pdt.departamento+"   "+pdt.persona
            println "fecha bloqueo "+pdt.tramite.fechaBloqueo
            def fechaBloqueo = pdt.tramite.fechaBloqueo
            if(fechaBloqueo && fechaBloqueo<ahora){
              // println "add bloquear "+pdt.tramite.de.departamento.codigo
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
    }
}
