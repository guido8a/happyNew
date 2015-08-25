package happy.seguridad

class AccionesController extends happy.seguridad.Shield {

    def dbConnectionService

    def index = {
        redirect(action: "acciones")
    }

    /**
     * OJO. el módulo de noAsignadas es de ID = 0
     */
    def acciones = {

        if(session.usuario.puedeAdmin) {
            def modulos = Modulo.list([sort: 'orden'])
            return [modulos: modulos]
        } else {
            flash.message = "Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será reportada"
            response.sendError(403)
        }
    }

    def ajaxAcciones = {
        def titulos = []
        def resultado = []
        def mdlo = params.mdlo
        def tipo = params.tipo
        if (params.mdlo?.size() > 0) mdlo = params.mdlo
        def cn = dbConnectionService.getConnection()
        def tx = ""
        tx = poneSQL(tipo, mdlo)
        if (tipo == '1') {
            titulos[0] = ['Permisos'] + ['Acción'] + ['Menú'] + ['Controlador']
        } else {
            titulos[0] = ['Permisos'] + ['Acción'] + ['Proceso'] + ['Controlador']
        }
        cn.eachRow(tx) { d ->
            resultado.add([d.accn__id] + [d.accnnmbr] + [d.accndscr] + [d.ctrlnmbr] + [d.mdlo__id])
        }
        if (resultado.size() == 0) {
            resultado[0] = ['0'] + ['no hay acciones'] + [''] + [''] + ['']
        }
        cn.close()
        return [datos: resultado, mdlo__id: mdlo, tpac__id: tipo, titulos: titulos]
    }

    def grabaAccn = {
        def id = params.id
        def dscr = params.dscr
        def cn = dbConnectionService.getConnection()
        def tx = "update accn set accndscr = '" + dscr + "' where accn__id = " + id
        try {
            cn.execute(tx)
        }
        catch (Exception ex) {
            println "Error al insertar:" + ex.getMessage()
        }
        cn.close()
        render("la accion '${dscr}' ha sido modificada")
    }


    def moverAccn = {
        def mdlo = params.mdlo
        def ids = params.ids
        if (params.ids?.size() > 0) ids = params.ids; else ids = "null"
        def cn = dbConnectionService.getConnection()
        def tx = "update accn set mdlo__id = " + mdlo + " where accn__id in (" + ids + ")"
        try {
            cn.execute(tx)
        }
        catch (Exception ex) {
            println "Error al insertar:" + ex.getMessage()
        }
        cn.close()
        render("Acciones movidas")
    }

    def sacarAccn = {
        def ids = params.ids
        if (params.ids?.size() > 0) ids = params.ids; else ids = "null"
        def cn = dbConnectionService.getConnection()
        def tx = ""
        tx = "update accn set mdlo__id = 0 where accn__id in (" + ids + ")"
        try {
            cn.execute(tx)
        }
        catch (Exception ex) {
            println "Error al insertar:" + ex.getMessage()
        }
        cn.close()
        redirect(action: "ajaxAcciones", params: params)
    }

    def cambiaAccn = {
        def ids = params.ids
        def tipo = params.tipo
        def tp = 0
        if (tipo == '1') {
            tp = 2
        } else {
            tp = 1
        }
        if (params.ids?.size() > 0) ids = params.ids; else ids = "null"
        def cn = dbConnectionService.getConnection()
        def modulo = Modulo.findByDescripcionLike("noAsignado")
        def tx = "update accn set mdlo__id = " + modulo.id + ", tpac__id = " + tp + " where accn__id in (" + ids + ")"
        try {
            cn.execute(tx)
        }
        catch (Exception ex) {
            println "Error al insertar:" + ex.getMessage()
        }
        cn.close()
        redirect(action: "ajaxAcciones", params: params)
    }


    def configurarAcciones = {
        if (session.usuario.puedeAdmin) {
            def modulos = Modulo.list([sort: "orden"])
            def controladores = []
            def cn = dbConnectionService.getConnection()
            cn.eachRow("select distinct ctrlnmbr from ctrl where ctrl__id in (select  ctrl__id from accn where mdlo__id is not null)") {
                controladores.add(it.ctrlnmbr)
            }
            def tp = 1
            cn.eachRow("select tpac__id from tpac where upper(tpacdscr) like '%MENU%'") {
                tp = it.tpac__id
            }
            [modulos: modulos, controladores: controladores, tipo_tpac: tp, titulo: 'Men&uacute;s por M&oacute;dulo']
        } else {
            flash.message = "Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será registrada."
            response.sendError(403)
        }
    }

    def procesos = {
        if (session.usuario.puedeAdmin) {
            def modulos = Modulo.list([sort: "orden"])
            def controladores = []
            def cn = dbConnectionService.getConnection()
            cn.eachRow("select distinct ctrlnmbr from ctrl where ctrl__id in (select  ctrl__id from accn where mdlo__id is not null)") {
                controladores.add(it.ctrlnmbr)
            }
            def tp = 2  //hallar el valor desde la BD
            cn.eachRow("select tpac__id from tpac where upper(tpacdscr) like '%PROC%'") {
                tp = it.tpac__id
            }
            render(view: "configurarAcciones", model: [modulos: modulos, controladores: controladores, tipo_tpac: tp,
                                                       titulo : 'Procesos por M&oacute;dulo'])
        } else {
            flash.message = "Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será registrada."
            response.sendError(403)
        }
    }


    def perfiles = {
        if (session.usuario.puedeAdmin) {
            def modulos = Modulo.list([sort: "orden"])
            [modulos: modulos]
        } else {
            flash.message = "Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será registrada."
            response.sendError(403)
        }
    }


    def guardarPermisos = {
        def perfil = Prfl.get(params.perfil)
        def permisos = Prms.findAllByPerfil(perfil).accion
        if (params.chk) {
            params.chk.each {

                def accn = Accn.get(it)
                if (!(permisos.contains(accn))) {
                    def perm = new Prms([accion: accn, perfil: perfil])
                    perm.save(flush: true)
                    println "errors guardarPermisos " + perm.errors
                }
            }
            permisos.each {
                if (!(params.chk.toList().contains(it.id.toString()))) {
                    def perm = Prms.findByAccionAndPerfil(it, perfil).delete(flush: true)
                }
            }
        } else {
            permisos = Prms.findAllByPerfil(perfil)
            permisos.each {
                it.delete(flush: true)
            }
        }


        redirect(action: "perfiles")
    }

    def cargarAccionesPerfil = {
        def perfil = Prfl.get(params.perfil)
        def permisos = Prms.findAllByPerfil(perfil).accion.id
        def modulos = Modulo.list()
        [modulos: modulos, permisos: permisos, perfil: perfil]
    }

    def cambiarTipo = {
        def accn = Accn.get(params.accn)
        accn.tipo = Tpac.get(params.val)
        render "ok"
    }

    def cambiarModulo = {
        def accn = Accn.get(params.accn)
        accn.modulo = Modulo.get(params.val)
        render "ok"
    }

    def cambiarModuloControlador = {
        def ctrl = Ctrl.findByCtrlNombre(params.ctrl)
        def acs = Accn.findAllByControl(ctrl)
        def modulo = Modulo.get(params.val)
        acs.each {
            it.modulo = modulo
            it.save(flush: true)
        }
        render "ok"
    }

    def cargarControladores = {
        def i = 0
        grailsApplication.controllerClasses.each {
            def ctr = Ctrl.findByCtrlNombre(it.getName())
            if (!ctr) {
                ctr = new Ctrl([ctrlNombre: it.getName()])
                if (!ctr.save(flush: true)) {
                    println "error controladores: " + ctr.errors
                } else {
                    i++
                }
            }
        }
        render("Se han agregado ${i} Controladores")
    }

    def cargarAcciones = {
        if (session.usuario.puedeAdmin) {
            def ac = []
            def accs = [:]
            def i = 0
            def ignore = ["afterInterceptor", "beforeInterceptor"]
            grailsApplication.controllerClasses.each { ct ->
                def t = []
                ct.getURIs().each {
                    def s = it.split("/")
                    println "* cargarAcciones   " + s
                    if (s.size() > 2)
                        if (!t.contains(s[2]))
                            if (!ignore.contains(s[2]))
                                if (!(s[2] =~ "Service")) {

                                    def accn = Accn.findByAccnNombreAndControl(s[2], Ctrl.findByCtrlNombre(ct.getName()))

                                    if (accn == null) {
                                        accn = new Accn()
                                        accn.accnNombre = s[2]
                                        accn.control = Ctrl.findByCtrlNombre(ct.getName())
                                        accn.accnDescripcion = s[2]
                                        accn.accnAuditable = 1
                                        if (s[2] =~ "save" || s[2] =~ "update" || s[2] =~ "delete" || s[2] =~ "guardar")
                                            accn.tipo = Tpac.get(2)
                                        else
                                            accn.tipo = Tpac.get(1)
                                        accn.modulo = Modulo.findByDescripcionIlike("no%asignado")
                                        if (!accn.save(flush: true)) {
                                            println "errores accn" + accn.errors
                                        } else {
                                            i++
                                        }
                                    }
                                    t.add(s.getAt(2))
                                }

                }
                accs.put(ct.getName(), t)
                t = null
            }
            render("Se han agregado ${i} acciones")

        } else {
            flash.message = "Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será registrada."
            response.sendError(403)
        }

    }

    def poneSQL(tipo, mdlo) {
        return "select accn.accn__id, accnnmbr, accndscr, ctrlnmbr, accn.mdlo__id " +
                "from accn, mdlo, ctrl where mdlo.mdlo__id = accn.mdlo__id and " +
                "mdlo.mdlo__id = ${mdlo} and accn.ctrl__id = ctrl.ctrl__id and " +
                "tpac__id = ${tipo} order by ctrlnmbr, accnnmbr"
    }

    def poneSQLnull(tipo) {
        return "select accn.accn__id, accnnmbr, accndscr, ctrlnmbr, accn.mdlo__id " +
                "from accn, ctrl where mdlo__id is null and accn.ctrl__id = ctrl.ctrl__id and " +
                "tpac__id = ${tipo} order by ctrlnmbr, accnnmbr"
    }


}
