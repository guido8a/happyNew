package happy.seguridad

import com.sun.xml.internal.bind.v2.TODO

class PrflController extends happy.seguridad.Shield {

    def dbConnectionService
    def loginService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST", delete: "GET"]

    def index = {
        redirect(action: "list", params: params)
    }

    def modulos = {
        if (session.usuario.puedeAdmin) {
//       println "recibe de parametros: ${params.id}"
            def prflInstance = Prfl.get(params.id)
            if (!prflInstance) {
                flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'prfl.label', default: 'Perfil'), params.id])}"
                redirect(action: "list")
            } else {
                def lstacmbo = lstaModulos(params.id)
//          println "modulos:----- " + lstacmbo
                render(view: "modulos", model: [prflInstance: prflInstance, lstacmbo: lstacmbo])
            }
        } else {
            flash.message = "Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será registrada."
            response.sendError(403)
        }
    }

    def permisos = {  /* permisos apra trámites */
//       println "recibe de parametros: ${params.id}"
        def prflInstance = Prfl.get(params.id)
        if (!prflInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'prfl.label', default: 'Perfil'), params.id])}"
            redirect(action: "list")
        } else {
            def lstacmbo = lstaModulos(params.id)
//          println "modulos:----- " + lstacmbo
            render(view: "permisos", model: [prflInstance: prflInstance, lstacmbo: lstacmbo])
        }
    }


    def verMenu = {
        def prfl = params.prfl.toInteger()
//    println "verMenu: ---------parametros: ${params}"
        render(g.generarMenuPreview(perfil: prfl))
        //render(g.generarMenu( perfil: "${prfl}" ))
    }

    def ajaxPermisoTramite = {
        def prfl = params.prfl.toInteger()
        def tpac = params.tpac
//        println "---------parametros: ${params}"
        def resultado = []
        def i = 0
        def ids = params.ids
        if (params.menu?.size() > 0) ids = params.menu
        if (params.grabar) {
            //println "a grabar... ${prfl}, ${ids}"
        }
        def cn = dbConnectionService.getConnection()
        def tx = ""
        // selecciona los permisos consedidos
        tx = "select perm.perm__id, permdscr, prpf.perm__id perm, permtxto " +
                "from (perm left join prpf on prpf.perm__id = perm.perm__id and prfl__id = ${prfl}) " +
                "order by permdscr"
//        println "ajaxPermisos SQL: ${tx}"
        cn.eachRow(tx) { d ->
            resultado[i] = [d.perm__id] + [d.permdscr] + [d.permtxto] + [d.perm]
            i++
        }
        cn.close()
//        println "-------------------------" + resultado

        return [datos: resultado, mdlo__id: ids, tpac__id: tpac]
    }


    def ajaxPermisos = {
        def prfl = params.prfl.toInteger()
        def tpac = params.tpac
        //println "---------parametros: ${params}"
        def resultado = []
        def i = 0
        def ids = params.ids
        if (params.menu?.size() > 0) ids = params.menu
        if (params.grabar) {
            //println "a grabar... ${prfl}, ${ids}"
        }
        def cn = dbConnectionService.getConnection()
        def tx = ""
        // selecciona las acciones que no se han consedido permisos
        tx = "select accn.accn__id, accndscr, accnnmbr, ctrlnmbr, prms.accn__id prms " +
                "from (accn left join prms on prms.accn__id = accn.accn__id and prfl__id = ${prfl}), ctrl " +
                "where mdlo__id in (" + ids + ") and " +
                "accn.ctrl__id = ctrl.ctrl__id and tpac__id = " + tpac + " order by ctrlnmbr, accndscr"
//        println "ajaxPermisos SQL: ${tx}"
        cn.eachRow(tx) { d ->
            resultado[i] = [d.accn__id] + [d.accndscr] + [d.accnnmbr] + [d.ctrlnmbr] + [d.prms]
            i++
        }
        cn.close()
//        println "-------------------------" + resultado

        return [datos: resultado, mdlo__id: ids, tpac__id: tpac]
    }

    def creaMdlo = {
        def mdloInstance = new Modulo()
        //prflInstance.properties = params
        render(view: 'creaMdlo', model: ['mdloInstance': mdloInstance])
    }

    def editMdlo = {
//      println "------editMdlo: " + params
        def mdloInstance = Modulo.get(params.id)
        //println mdloInstance
        render(view: 'creaMdlo', model: ['mdloInstance': mdloInstance])
    }

    def grabaMdlo = {
//    println "+++++parametros: ${params}"
        if (!params.id) {
            def mdloInstance = new Modulo()
            params.controllerName = controllerName
            params.actionName = "saveMdlo"
//      mdloInstance = kerberosoldService.save(params, Modulo, session.perfil, session.usuario)
            mdloInstance.properties = params
            mdloInstance.save()
            if (mdloInstance.properties.errors.getErrorCount() > 0) {
                //println "---- save ${bancoInstance}"
                render("El módulo no ha podido crearse")
            } else {
                render("El módulo ${params.nombre} ha sido grabado en el sistema")
            }
        } else {
//      println "<<< Update >>> : ${params.id}"
            def mdloInstance = Modulo.get(params.id)
            params.controllerName = controllerName
            params.actionName = "UpdateMdlo"
//      mdloInstance = kerberosoldService.save(params, Modulo, session.perfil, session.usuario)
            mdloInstance.properties = params
            mdloInstance.save(flush: true)
            if (mdloInstance.properties.errors.getErrorCount() > 0) {
//        println "---- save ${mdloInstance}"
                render("El módulo no se ha podido actualizar")
            } else {
                render("ok")
            }
        }
    }

    def borraMdlo = {
//      println "------borrarMdlo: " + params



   if(session.usuario.puedeAdmin){
       Modulo.get(params.id).delete()
    } else {
        flash.message = "Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será registrada."
        response.sendError(403)
    }

/*
      params.controllerName = controllerName
      params.actionName = "delete"

      kerberosoldService.delete(params, Modulo, session.perfil, session.usuario)
*/
        render('borrado: ${params.id}')
    }

    def creaPrfl = {
        def prflInstance = new Prfl()
        //prflInstance.properties = params
        render(view: 'crear', model: ['prflInstance': prflInstance])
    }

    def grabaPrfl = {
        //println "+++++parametros: ${params}"
        //println "+++++parametros: ${params.codigo}"
        if (!params.id) {
            def prflInstance = new Prfl()
            params.controllerName = controllerName
            params.actionName = "save"
            prflInstance.properties = params
            prflInstance.save()
            render("ok")
            if (prflInstance.properties.errors.getErrorCount() > 0) {
                //println "---- save ${bancoInstance}"
                println("El perfil no ha podido crearse: " + prflInstance.properties.errors)
            } else {
                if (prflInstance.padre) {
                    def prms = Prms.findAllByPerfil(prflInstance.padre)
                    def prmsNuevo = new Prms()
                    prms.each {
                        prmsNuevo = new Prms(['perfil.id': prflInstance.id, 'accion.id': it.accion.id])
                        prmsNuevo.save(failOnError: true)
                    }
                }
                render("El perfil ${params.nmbr} ha sido grabado en el sistema")
            }
        } else {
//        println "<<< Update >>> : ${params.id}"
            def prflInstance = Prfl.get(params.id)
            params.controllerName = controllerName
            params.actionName = "Update"
            prflInstance.properties = params
            prflInstance.save()
            //prflInstance = kerberosoldService.save(params, Prfl, session.perfil, session.usuario)
            if (prflInstance.properties.errors.getErrorCount() > 0) {
//          println "---- save ${prflInstance}"
                render("El perfil no ha podido actualizar")
            } else {
                render("ok")
            }
        }
    }

    def editPrfl = {
//      println "------editPrfl: " + params
        def prflInstance = Prfl.get(params.id)
        render(view: 'crear', model: ['prflInstance': prflInstance])
    }

    def borraPrfl = {
//      println "------editPrfl: " + params
        params.controllerName = controllerName
        params.actionName = "delete"
        Prfl.get(params.id).save()
        //kerberosoldService.delete(params, Prfl, session.perfil, session.usuario)
        render('borrado: ${params.id}')
    }

    /* TODO: revisar grabar.
    * **/

    def grabar = {
//      println "parametros grabar: ${params}"
        def ids = params.ids
        def modulo = params.menu
        def prfl = params.prfl
        def tx1 = ""
        def exst = []
        def actl = []

        if (ids.size() < 1) ids = '1000000' // este valor no existe como accn__id, y sirve para el IN del SQL
        // eliminar los permisos que no estén chequeados
        def cn = dbConnectionService.getConnection()
        def tx = ""
        tx = "select prms__id from prms, accn where accn.accn__id = prms.accn__id and " +
                "mdlo__id = ${modulo} and " +
                "prms.accn__id not in (select accn__id " +
                "from accn where mdlo__id = " + modulo + " and  " +
                "accn__id in (${ids})) and prfl__id = ${prfl}"
//
//      println "grabar SQL: ${tx}"
        cn.eachRow(tx) { d ->
            Prms.get(d.prms__id).delete()
        }
        //println "-------------borrado de permisos----------"
        // se debe barrer tosos los menús señalados y si está chequeado añadir a prms.
        tx = "select prms.accn__id from prms, accn where accn.accn__id = prms.accn__id and " +
                "mdlo__id = ${modulo} and " +
                "prms.accn__id in (select accn__id " +
                "from accn where mdlo__id = " + modulo + " and accn__id in (${ids})) and prfl__id = ${prfl}"
        //println "grabar IN SQL: ${tx}"
        exst = []
        cn.eachRow(tx) { d ->
            exst.add(d.accn__id)
        }
        tx = "select accn__id " +
                "from accn where mdlo__id = " + modulo + " and accn__id in (${ids})"
        //println "grabar señalados SQL: ${tx}"
        actl = []
        cn.eachRow(tx) { d ->
            actl.add(d.accn__id)
        }
        //println "insercion  Actual: ${actl} \n Exst: ${exst}}"
        (actl - exst).each {
            tx1 = "insert into prms(prfl__id, accn__id) values (${prfl.toInteger()}, ${it})"
            try {
                cn.execute(tx1)
                //insertaKerveros(prfl.toInteger(), session.usuario, session.perfil)
//          println "insertando.... ${tx1}"
            }
            catch (Exception ex) {
                println ex.getMessage()
            }
            //resp += "<br>" + tx1
        }
        cn.close()
        //println "recibido:" + params
        //render(resp + "<br>Existe:" + exst + "<br>Actual:" + actl + "<br>a insertar: ${actl-exst}")
        redirect(action: 'ajaxPermisos', params: params)
    }


    def grabar_perm = {
//        println "parametros grabar: ${params}"
        def ids = params.ids
        def modulo = params.menu
        def prfl = params.prfl
        def tx1 = ""
        def tx2 = ""
        def error = ""
        def exst = []
        def actl = []
        def prsn = []
        def borra = ""
        def fcha = new Date().format('yyyy-MM-dd')

        if (ids.size() < 1) ids = '1000000' // este valor no existe como accn__id, y sirve para el IN del SQL
        // eliminar los permisos que no estén chequeados
        def cn = dbConnectionService.getConnection()
        def cn1 = dbConnectionService.getConnection()
        def tx = ""

        /* inicia borrado de permisos que se eliminan del perfil: saca los ids de perm para borrar */
        tx = "select prpf__id, prpf.perm__id from prpf, perm where perm.perm__id = prpf.perm__id and " +
                "prpf.perm__id not in (select perm__id " +
                "from perm where perm__id in (${ids})) and prfl__id = ${prfl}"
//
//        println "grabar SQL: ${tx}"
        borra = "("
        cn.eachRow(tx) { d ->
            if (borra == "(") borra += d.perm__id else borra += "," + d.perm__id
            Prpf.get(d.prpf__id).delete()
        }
        borra += ")"
        println borra

//        println "-------------fin de borrado de permisos----------"

        /* se debe barrer todos los permisosseñalados y si está chequeado añadir a prpf. */
        tx = "select prpf.perm__id from prpf, perm where perm.perm__id = prpf.perm__id and " +
                "prpf.perm__id in (select perm__id " +
                "from perm where perm__id in (${ids})) and prfl__id = ${prfl}"
//        println "grabar IN SQL: ${tx}"
        exst = []
        cn.eachRow(tx) { d ->
            exst.add(d.perm__id)
        }

        tx = "select perm__id from perm where perm__id in (${ids})"
        //println "grabar señalados SQL: ${tx}"
        actl = []
        cn.eachRow(tx.toString()) { d ->
            actl.add(d.perm__id)
        }

//        println "insercion  Actual: ${actl} y Exst: ${exst}"
//        println "a insertar:" + actl - exst
        (actl - exst).each {
            tx1 = "insert into prpf(prfl__id, perm__id) values (${prfl.toInteger()}, ${it.toInteger()})"
            try {
                cn.execute(tx1)
                //insertaKerveros(prfl.toInteger(), session.usuario, session.perfil)
//                println "insertando.... ${tx1}"
            }
            catch (Exception ex) {
                println ex.getMessage()
            }
            //resp += "<br>" + tx1
        }


        /* actualiza PRUS de los usuarios con el perfil actual */
        /* para cada persona, pone fecha de fin a los permisos que no son parte delperfil y añade los que no tenga*/
        /*TODO */
        tx = "select prsn__id from sesn where prfl__id = ${prfl}"
        prsn = []
        cn.eachRow(tx.toString()) { d ->
            prsn.add(d.prsn__id)
        }

//        println "inicia actuliación de prpf de las personas: $prsn"
        prsn.each {
            if (borra != "()") {
                /* verifica el permiso borrado no se halñle en los otros perfiles que tiene el usuario */
                tx = "select distinct perm__id from prpf where perm__id in ${borra} and perm__id not in (" +
                        "select perm__id from prpf, sesn where sesn.prsn__id = ${it} and sesn.prfl__id = prpf.prfl__id and " +
                        "sesn.prfl__id <> ${prfl})"
                prsn = []
                cn.eachRow(tx.toString()) { d ->
                    /* pone fecha de fin a los permisos que se eliminan del perfil */
                    cn1.execute("update prus set prusfcfn = '${fcha}', prsnmdfc = ${session.usuario.id} " +
                            "where prsn__id = ${it} and perm__id = ${d.perm__id}".toString())
                }

            }

            tx2 = "insert into prus(prsn__id, perm__id, prusfcin, prsnasgn) select ${it}, perm__id, '${fcha}', " +
                    "${session.usuario.id} from prpf where prfl__id = ${prfl} and perm__id not in " +
                    "(select perm__id from prus where prsn__id = ${it} and prusfcfn is null);"
//            println "-- update: $tx2"
            try {
                cn.execute(tx2.toString())  /* añade permisos nuevos */
            }
            catch (Exception ex) {
                println ex.getMessage()
                error += ex.getMessage()
            }
            //resp += "<br>" + tx1
        }


        cn.close()
        cn1.close()
        println "errores:" + error.size() + ".."

        if (error.size() > 1)
            render("NO_Errores:\n ${error}")
        else
            render("OK_Proceso realizado con éxito")

//        redirect(action: 'ajaxPermisos', params: params)
    }

    //---------------------------------

    def list() {
        if (session.usuario.puedeAdmin) {
            params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
            def prflInstanceList = Prfl.list(params)
            def prflInstanceCount = Prfl.count()
            if (prflInstanceList.size() == 0 && params.offset && params.max) {
                params.offset = params.offset - params.max
            }
            prflInstanceList = Prfl.list(params)
            return [prflInstanceList: prflInstanceList, prflInstanceCount: prflInstanceCount]
        } else {
            flash.message = "Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será registrada."
            response.sendError(403)
        }

    } //list
    def show_ajax() {
        if (params.id) {
            def numeroInstance = Prfl.get(params.id)
            if (!numeroInstance) {
                notFound_ajax()
                return
            }
            return [numeroInstance: numeroInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def prflInstance = new Prfl(params)
        if (params.id) {
            prflInstance = Prfl.get(params.id)
            if (!prflInstance) {
                notFound_ajax()
                return
            }
        }
        return [prflInstance: prflInstance]
    } //form para cargar con ajax en un dialog

    def save_ajax() {
        params.each { k, v ->
            if (v != "date.struct" && v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }
        def prflInstance = new Prfl()
        if (params.id) {
            prflInstance = Prfl.get(params.id)
            if (!prflInstance) {
                notFound_ajax()
                return
            }
        } //update
        prflInstance.properties = params
        if (!prflInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} Prfl."
            msg += renderErrors(bean: prflInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de Prfl exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def prflInstance = Prfl.get(params.id)
            if (prflInstance) {
                try {
                    prflInstance.delete(flush: true)
                    render "OK_Eliminación de Prfl exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar Prfl."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró Numero."
    } //notFound para ajax

    def form = {

        if (session.usuario.puedeAdmin) {

            def title
            def prflInstance

            if (params.source == "create") {
                prflInstance = new Prfl()
                prflInstance.properties = params
                title = g.message(code: "prfl.create", default: "Create Prfl")
            } else if (params.source == "edit") {
                prflInstance = Prfl.get(params.id)
                if (!prflInstance) {
                    flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'prfl.label', default: 'Prfl'), params.id])}"
                    redirect(action: "list")
                }
                title = g.message(code: "prfl.edit", default: "Edit Prfl")
            }

            return [prflInstance: prflInstance, title: title, source: params.source]
        } else {
            flash.message = "Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será registrada."
            response.sendError(403)
        }



    }

    def create = {
        params.source = "create"
        redirect(action: "form", params: params)
    }

    def save = {
        def title
        if (params.id) {
            title = g.message(code: "prfl.edit", default: "Edit Prfl")
            def prflInstance = Prfl.get(params.id)
            if (prflInstance) {
                prflInstance.properties = params
                if (!prflInstance.hasErrors() && prflInstance.save(flush: true)) {
                    flash.message = "${message(code: 'default.updated.message', args: [message(code: 'prfl.label', default: 'Prfl'), prflInstance.id])}"
                    redirect(action: "show", id: prflInstance.id)
                } else {
                    render(view: "form", model: [prflInstance: prflInstance, title: title, source: "edit"])
                }
            } else {
                flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'prfl.label', default: 'Prfl'), params.id])}"
                redirect(action: "list")
            }
        } else {
            title = g.message(code: "prfl.create", default: "Create Prfl")
            def prflInstance = new Prfl(params)
            if (prflInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.created.message', args: [message(code: 'prfl.label', default: 'Prfl'), prflInstance.id])}"
                redirect(action: "show", id: prflInstance.id)
            } else {
                render(view: "form", model: [prflInstance: prflInstance, title: title, source: "create"])
            }
        }
    }

    def update = {
        def prflInstance = Prfl.get(params.id)
        if (prflInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (prflInstance.version > version) {

                    prflInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'prfl.label', default: 'Prfl')] as Object[], "Another user has updated this Prfl while you were editing")
                    render(view: "edit", model: [prflInstance: prflInstance])
                    return
                }
            }
            prflInstance.properties = params
            if (!prflInstance.hasErrors() && prflInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'prfl.label', default: 'Prfl'), prflInstance.id])}"
                redirect(action: "show", id: prflInstance.id)
            } else {
                render(view: "edit", model: [prflInstance: prflInstance])
            }
        } else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'prfl.label', default: 'Prfl'), params.id])}"
            redirect(action: "list")
        }
    }

    def show = {
        def prflInstance = Prfl.get(params.id)
        if (!prflInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'prfl.label', default: 'Prfl'), params.id])}"
            redirect(action: "list")
        } else {

            def title = g.message(code: "prfl.show", default: "Show Prfl")

            [prflInstance: prflInstance, title: title]
        }
    }

    def edit = {
        params.source = "edit"
        redirect(action: "form", params: params)
    }

    def delete = {
        def prflInstance = Prfl.get(params.id)
        if (prflInstance) {
            try {
                prflInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'prfl.label', default: 'Prfl'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'prfl.label', default: 'Prfl'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        } else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'prfl.label', default: 'Prfl'), params.id])}"
            redirect(action: "list")
        }
    }

    List lstaModulos(prfl) {
        def resultado = []
        def cn = dbConnectionService.getConnection()
        cn.eachRow("select mdlo__id, mdlonmbr from mdlo order by mdloordn") { d ->
            resultado.add([d.mdlo__id] + [d.mdlonmbr])
        }
        cn.close()
        return resultado
    }


}
