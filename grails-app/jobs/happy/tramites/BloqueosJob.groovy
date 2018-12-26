package happy.tramites

import groovy.time.TimeCategory
import happy.seguridad.Persona
import happy.utilitarios.Parametros


class BloqueosJob {

    def diasLaborablesService
    def dbConnectionService
    def bloqueado = "B"     /*** poner "B" para habilitar bloqueos y comentar componeEstado() ***/

    static triggers = {
        null    // no ejecuta los bloqueos
        simple name: 'bloqueoBandejaSalida', startDelay: 1000 * 60 * 1, repeatInterval: 1000 * 60 * 5
//        simple name: 'bloqueoBandejaSalida', startDelay: 1000 * 10, repeatInterval: 1000 * 60 * 3
    }

    def execute() {   /*********** execute job ************/

        /*** *** nuevo *** ***/
        println "inicia bloqueo nuevo: ${new Date()}"
        def cn = dbConnectionService.getConnection()
        def sql = 'select * from bloqueo(null, null)'
        cn.execute(sql.toString())
        println "fin bloqueo nuevo: ${new Date()}"
        /*** fin nuevo ***/


/*
        def bloquear = []
        def bloquearUsu = []
        def warning = []
        def warningUsu = []
        def rolEnvia = RolPersonaTramite.findByCodigo("E004")  //envia id: 4
        def rolRecibe = RolPersonaTramite.findByCodigo("I005") //imprime id:5
        def anulado = EstadoTramite.findByCodigo("E006")

        println "bloqueos C ${new Date()}"

        borraBloqueos()

        PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite where fechaEnvio is not null and " +
                "fechaRecepcion is null and (estado is null or estado != ${anulado.id}) and " +
                "rolPersonaTramite not in (${rolEnvia.id}, ${rolRecibe.id})").each { pdt ->

            if ((pdt.tramite.externo.toString() != "1")) {  // no se bloquea trámites externos ni remotos
                if (pdt.fechaBloqueo) {
                    if (pdt.tramite.deDepartamento) {
                        if (!warning?.id?.contains(pdt.tramite.deDepartamento.id)) {
                            warning.add(pdt.tramite.deDepartamento)
                        }
                    } else {
                        if (!warningUsu?.id?.contains(pdt.tramite.de.id)) {
                            warningUsu.add(pdt.tramite.de)
                        }
                    }

                    if (pdt.persona) {
                        if (!bloquearUsu?.id?.contains(pdt.persona.id)) {
                            bloquearUsu.add(pdt.persona)
                        }
                        registraBloqueo(pdt.tramite.id, pdt.departamento?.id, pdt.persona?.id, pdt.fechaEnvio,
                                pdt.rolPersonaTramite?.id, pdt.tramite.codigo)
                    } else {
                        registraBloqueo(pdt.tramite.id, pdt.departamento?.id, pdt.persona?.id, pdt.fechaEnvio,
                                pdt.rolPersonaTramite?.id, pdt.tramite.codigo)
                        if (!bloquear?.id?.contains(pdt.departamento?.id)) {
                            bloquear.add(pdt.departamento)
                        }
                    }
                }
            }
        }

        Departamento.list().each { dep ->
            dep.estado = ""
            if (bloquear.id.contains(dep.id)) {
                dep.estado = bloqueado
            } else {
                if (warning.id.contains(dep.id)) {
                    if (dep.estado != bloqueado) {
                        dep.estado = "W"
                    }
                }
            }
            if (!dep.save(flush: true)) {
                println "errores save dep " + dep.errors
            }
        }

        Persona.findAllByEstadoInList([bloqueado, "W"]).each {
            it.estado = ""
            if (!it.save(flush: true)) {
                println "error desbloq prsn " + it.errors
            }
        }

        bloquearUsu.each {
            if (!(it.puedeAdmin)) {
                it.estado = bloqueado
                if (!it.save(flush: true)) {
                    println "error bloq usu"
                }
            }
        }

        warningUsu.each {
            if (it.estado != bloqueado) {
                it.estado = "W"
                it.save(flush: true)
            }
        }
        println "Fin bloqueo C "+new Date().format("dd-MM-yyyy hh:mm:ss")
//        componeEstado()
*/

    }

    def executeRecibir(depar, persona) {

        /*** *** nuevo ***
         * se impementó en TramitesService.ejecutaRecibir
         * fin nuevo ***/

/*
        def bloquear = []
        def bloquearUsu = []
        def warning = []
        def warningUsu = []
        def rolEnvia = RolPersonaTramite.findByCodigo("E004")    //envía
        def rolRecibe = RolPersonaTramite.findByCodigo("I005")   //imprime
        def anulado = EstadoTramite.findByCodigo("E006")

        def deps = [depar]

        PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite where fechaEnvio is not null and " +
                "fechaRecepcion is null and (departamento = ${depar.id} or persona = ${persona.id}) and " +
                "(estado is null or estado != ${anulado.id}) and " +
                "rolPersonaTramite not in (${rolEnvia.id}, ${rolRecibe.id})").each { pdt ->

            if (pdt.tramite.externo != "1") {

                if (pdt.fechaBloqueo) {
                    if (pdt.rolPersonaTramite.codigo != "E004" && pdt.rolPersonaTramite.codigo != "I005") {
                        if (pdt.tramite.deDepartamento) {
                            if (!warning?.id?.contains(pdt.tramite.deDepartamento.id)) {
                                warning.add(pdt.tramite.deDepartamento)
                            }
                        } else {
                            if (!warningUsu?.id?.contains(pdt.tramite.de.id)) {
                                warningUsu.add(pdt.tramite.de)
                            }
                        }

                        if (pdt.persona) {
                            if (!bloquearUsu?.id?.contains(pdt.persona.id)) {
                                bloquearUsu.add(pdt.persona)
                            }
                        } else {
                            if (!bloquear?.id?.contains(pdt.departamento?.id)) {
                                bloquear.add(pdt.departamento)
                            }
                        }
                    }
                }
            }
        }
        deps.each { dep ->
            dep.estado = ""
            if (bloquear.id.contains(dep.id)) {
                dep.estado = bloqueado
            } else {
                if (warning.id.contains(dep.id)) {
                    if (dep.estado != bloqueado) {
                        dep.estado = "W"
                    }
                }
            }
            if (!dep.save(flush: true)) {
                println "errores save dep " + dep.errors
            }
        }
        Persona.findAllByEstadoInListAndDepartamento([bloqueado, "W"], depar).each {
            it.estado = ""
            it.save(flush: true)
        }
        bloquearUsu.each {
            if (!(it.getPuedeAdminOff())) {
                it.estado = bloqueado
                it.save(flush: true)
            }
        }
        warningUsu.each {
            if (it.estado != bloqueado) {
                it.estado = "W"
                it.save(flush: true)
            }
        }
//        componeEstado()
*/
    }

    def borraBloqueos() {
        def cn = dbConnectionService.getConnection()
        def sql = "delete from blqo"
        cn.execute(sql.toString())
    }

    def registraBloqueo(trmt, dpto, prsn, fcha, rltr, cdgo) {
        def fecha
        if(fcha) fecha = fcha.format('yyyy-MM-dd hh:mm:ss')
        def cn = dbConnectionService.getConnection()
        def sql = "insert into blqo(dpto__id, prsn__id, trmt__id, trmtfcen, rltr__id, trmtcdgo) " +
                "values(${dpto}, $prsn, $trmt, '$fecha', $rltr, '${cdgo}')"
        cn.execute(sql.toString())
    }


    def componeEstado() {
        def cnta = 0
        Departamento.findAllByEstado("B").each { dep ->
            dep.estado = bloqueado
            cnta++
            if (!dep.save(flush: true)) {
                println "error estado a C " + dep.errors
            }
        }
        println "compuesto dpto $cnta"
        cnta = 0
        Persona.findAllByEstado("B").each { pr ->
            pr.estado = bloqueado
            cnta++
            if (!pr.save(flush: true)) {
                println "error prsn estado a C " + pr.errors
            }
        }
        println "compuesto prsn $cnta"
    }



}
