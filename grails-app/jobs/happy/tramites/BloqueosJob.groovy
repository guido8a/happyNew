package happy.tramites

import happy.seguridad.Persona


class BloqueosJob {
    static triggers = {
        simple name: 'bloqueoBandejaSalida', startDelay: 1000*60, repeatInterval: 1000*60*5
    }

    def execute() {
        // execute job

        def ahora = new Date()
//        println "----------------------------------"
//        println "bloqueo bandeja salida!!!!! "+ahora.format("dd-MM-yyyy hh:mm:ss")
        def bloquear = []
        def bloquearUsu = []
        def warning = []
        def warningUsu = []
        def rolEnvia = RolPersonaTramite.findByCodigo("E004")
        def rolRecibe = RolPersonaTramite.findByCodigo("I005")
        def anulado = EstadoTramite.findByCodigo("E006")

//        PersonaDocumentoTramite.findAllByFechaEnvioIsNotNullAndFechaRecepcionIsNull()
        PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite where fechaEnvio is not null and " +
                "fechaRecepcion is null and (estado is null or estado != ${anulado.id}) and " +
                "rolPersonaTramite not in (${rolEnvia.id},${rolRecibe.id})").each { pdt ->

            //println "PDT "+pdt.id+" tramite ${pdt.departamento? pdt.departamento.codigo : pdt.persona.login} ${pdt.tramite.externo}" +
            // pdt.tramite.id +" : " + pdt.tramite.codigo + " envio " + pdt.fechaEnvio.format("dd-MM-yyyy hh:mm") +
            // " bloqueo " + pdt.fechaBloqueo?.format("dd-MM-yyyy hh:mm") + "   " + pdt.rolPersonaTramite.codigo

            /* 1-- si viene de un departamento externo no se bloquea --> trámite externo = 1
            *  2-- si recibe   un departamento externo no se bloquea --> trámite externo = 1
            *  en ambos casos el trámite es marcado como externo*/

            if(pdt.tramite.externo.toString() != "1"){  // no se bloquea trámites externos
//                println "no es externo ${pdt.tramite.codigo} -- ${pdt.tramite.externo}"
              //  if(!pdt.persona && !pdt.departamento)

                def fechaBloqueo = pdt.fechaBloqueo

                if(fechaBloqueo && (fechaBloqueo < ahora)){
//                    println "pdt "+pdt.id+" "+pdt.departamento+" "+pdt.persona+"  "+pdt.tramite.codigo+"  "+pdt.tramite.de+" "+pdt.rolPersonaTramite.descripcion
                    if(pdt.tramite.deDepartamento){
                        if(!warning?.id?.contains(pdt.tramite.deDepartamento.id))
                            warning.add(pdt.tramite.deDepartamento)
                    } else {
                        if(!warningUsu?.id?.contains(pdt.tramite.de.id))
                            warningUsu.add(pdt.tramite.de)
                    }

                    if(pdt.persona){
//                        println "add bloquear "+pdt.persona+"  "+pdt.persona.login
                        if(!bloquearUsu?.id?.contains(pdt.persona.id))
                            bloquearUsu.add(pdt.persona)
                    }else{
//                        println "add bloquear dep "+pdt.departamento+" "+pdt.id
//                        println "bloquear ???"+bloquear+" "+bloquear?.id
                        if(!bloquear?.id?.contains(pdt.departamento?.id))
                            bloquear.add(pdt.departamento)
                    }
//                    println "---------------------"
                }
            }
        }

//        println "departamentos a bloquear: $bloquear"

        Departamento.list().each {dep ->
            dep.estado = ""
//            println "iter dep "+dep.codigo+"  "+dep.estado
            if(bloquear.id.contains(dep.id)){
//                println "bloqueando dep "+dep
                dep.estado = "B"
            } else {
                if(warning.id.contains(dep.id)){
                    if(dep.estado!="B") {
//                        println "warning dep "+dep
                        dep.estado = "W"

                    }
                }
            }
            if(!dep.save(flush: true))
                println "errores save dep "+dep.errors
        }
        Persona.findAllByEstadoInList(["B","W"]).each {
           // println "desbloq "+it.login
            it.estado=""
            if(!it.save(flush: true))
                println "error desbloq prsn "+it.errors
//            else println "si desbloq !!! "+it.estado+"  "+it.id+"   "+it.errors
        }
        bloquearUsu.each {
//            println "bloqueando usu "+it+"   puede admin "+it.puedeAdmin
            if(!(it.puedeAdmin)){
//                println "entro"
                it.estado="B"
                if(!it.save(flush: true))
                    println "error bloq usu"
            }
        }
        warningUsu.each {
//            println("----->>>>>>" + it?.estado)
            if(it.estado!="B"){
                it.estado="W"
                it.save(flush: true)
            }
        }
//        println "fin bloqueo bandeja salida "+new Date().format("dd-MM-yyyy hh:mm:ss")
    }


    def executeRecibir(depar,persona){
        def ahora = new Date()
//        println "----------------------------------"
//        println "bloqueo bandeja recibir!!! "+ahora
        def bloquear = []
        def bloquearUsu = []
        def warning = []
        def warningUsu = []
        def deps = [depar]
        def rolEnvia = RolPersonaTramite.findByCodigo("E004")
        def rolRecibe = RolPersonaTramite.findByCodigo("I005")
        def anulado = EstadoTramite.findByCodigo("E006")
//        PersonaDocumentoTramite.findAllByFechaEnvioIsNotNullAndFechaRecepcionIsNull()

        PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite where fechaEnvio is not null and fechaRecepcion is null and (departamento=${depar.id} or persona=${persona.id}) and (estado is null or estado!=${anulado.id}) and rolPersonaTramite not in (${rolEnvia.id},${rolRecibe.id})").each {pdt->
            if(pdt.tramite.externo!="1"){
                def fechaBloqueo = pdt.fechaBloqueo
                if(fechaBloqueo && fechaBloqueo<ahora){
                    if(pdt.rolPersonaTramite.codigo!="E004" && pdt.rolPersonaTramite.codigo!="I005" ){
//                    println "PDT "+pdt.id+" tramite "+pdt.tramite.id +" : "+pdt.tramite.codigo+" envio "+pdt.fechaEnvio.format("dd-MM-yyyy hh:mm")+" bloqueo "+pdt.tramite.fechaBloqueo?.format("dd-MM-yyyy hh:mm")

                        if(pdt.tramite.deDepartamento){
                            if(!warning?.id?.contains(pdt.tramite.deDepartamento.id))
                                warning.add(pdt.tramite.deDepartamento)
                        }else{
                            if(!warningUsu?.id?.contains(pdt.tramite.de.id))
                                warningUsu.add(pdt.tramite.de)
                        }

                        if(pdt.persona){
//                        println "add bloquear "+pdt.persona
                            if(!bloquearUsu?.id?.contains(pdt.persona.id))
                                bloquearUsu.add(pdt.persona)
                        }else{
//                        println "add bloquear "+pdt.departamento
                            if(!bloquear?.id?.contains(pdt.departamento.id))
                                bloquear.add(pdt.departamento)
                        }
                    }

                }
            }
        }
        deps.each {dep->
            dep.estado=""
//            println "iter dep "+dep.codigo+"  "+dep.estado
            if(bloquear.id.contains(dep.id)){
//                println "bloqueando dep "+dep
                dep.estado="B"


            }else{
                if(warning.id.contains(dep.id)){
                    if(dep.estado!="B") {
//                        println "warning dep "+dep
                        dep.estado = "W"

                    }
                }
            }
            if(!dep.save(flush: true))
                println "errores save dep "+dep.errors
        }
        Persona.findAllByEstadoInListAndDepartamento(["B","W"],depar).each {
            it.estado=""
            it.save(flush: true)
        }
        bloquearUsu.each {
//            println "bloqueando usu recibir "+it
            if(!(it.getPuedeAdminOff() )) {
//                println "entro"
                it.estado = "B"
                it.save(flush: true)
            }
        }
        warningUsu.each {
//            println "warning usu "+it
            if(it.estado!="B") {
                it.estado = "W"
                it.save(flush: true)
            }
        }


//        println "fin bloqueo bandeja salida recibir "+new Date()
    }
}
