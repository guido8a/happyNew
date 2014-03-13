package happy.tramites



class BloqueosJob {
    static triggers = {
        simple name: 'bloqueoBandejaSalida', startDelay: 1000*60, repeatInterval: 1000*60
    }

    def execute() {
        // execute job
        println "bloqueo bandeja salida "+new Date()
        def ahora = new Date()
        def bloquear = []
        PersonaDocumentoTramite.findAllByFechaEnvioIsNotNullAndFechaRecepcionIsNull().each {pdt->
            println "pdt --> "+pdt.id+" tramite "+pdt.tramite.id+" - ${pdt.tramite.de.departamento.descripcion} "+pdt.fechaEnvio+"  "+pdt.departamento+"   "+pdt.persona
            println "fecha bloqueo "+pdt.tramite.fechaBloqueo
            def fechaBloqueo = pdt.tramite.fechaBloqueo
            if(fechaBloqueo && fechaBloqueo<ahora){
                println "add bloquear "+pdt.tramite.de.departamento.codigo
                bloquear.add(pdt.tramite.de.departamento)
            }
        }
        Departamento.list().each {dep->
            if(!bloquear.contains(dep)){
                if(dep.estado=="B"){
                    dep.estado=""
                    dep.save(flush: true)
                }
            }else{
                println "bloqueando "
                dep.estado="B"
                dep.save(flush: true)
            }
        }
    }
}
