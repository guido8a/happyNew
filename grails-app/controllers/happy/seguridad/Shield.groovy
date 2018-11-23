package happy.seguridad

import groovy.sql.Sql
import groovy.time.TimeCategory
import happy.tramites.Departamento


class Shield {
    def dataSource

    def beforeInterceptor = [action: this.&auth, except: 'login']

    /**
     * Verifica si se ha iniciado una sesión
     * Verifica si el usuario actual tiene los permisos para ejecutar una acción
     */
    def auth() {
//        println "an " + actionName + " cn " + controllerName + "  "
        session.an = actionName
        session.cn = controllerName
        session.pr = params
//        return true

        /* si está en redactar guarda el texto */
        if(session.an == 'saveTramite' && session.cn == 'tramite'){
            return true
        }else{
            if (!session.usuario || !session.perfil) {
                if(controllerName != "inicio" && actionName != "index") {
                    flash.message = "Usted ha superado el tiempo de inactividad máximo de la sesión"
                }
                render "<script type='text/javascript'> window.location.href = '${createLink(controller:'login', action:'login')}'; </script>"
                session.finalize()
                return false

            } else {
                def now = new Date()
                def band = true
//            use(groovy.time.TimeCategory) {
//                def duration = now - session.time
//                if(duration.minutes>4){
//                    session.usuario=null
//                    session.finalize();
//                    band = false
//                }else{
//                    session.time=now;
//                }
//            }
//            if(!band) {
////                redirect(controller: 'login', action: 'logout')
////                render "<script type='text/javascript'> window.location.href = " + createLink(controller: "login", action: "login") + "; location.reload(true); </script>"
//                redirect(controller: 'login', action: 'finDeSesion')
//                return false
//            }


//                println "---> $controllerName / $actionName"

//                /* *** inicio de recgistro de actividad del usaurio *** */
//                Sql sql = new Sql(dataSource)
//                def sale = new Date().format("yyyy-MM-dd HH:mm:ss.SSS")
//                def fcha = new Date().format("yyyy-MM-dd HH:mm:ss.SSS")
//                def tx = "select accn__id, tpac__id from accn, ctrl where accnnmbr ilike '${actionName}' and " +
//                        "ctrl.ctrl__id = accn.ctrl__id and ctrlnmbr ilike '${controllerName}'"
//                //println "---> ${entero(session.id)}"
//                def accn__id, tipo
//                sql.eachRow(tx.toString()) { d ->
//                    accn__id = d.accn__id
//                    tipo     = d.tpac__id
//                }
//
//                tx = "select accn__id, usst__id from usst where usstsesn = ${entero(session.id)} " +
//                        "order by usst__id desc limit 1"
//                def accndsde, id
//                sql.eachRow(tx.toString()) { d ->
//                    accndsde = d.accn__id
//                    id       = d.usst__id
//                }
//
//                //println "accn: actual ${accn__id}, anterior: ${accndsde}, tipo: $tipo"
//                if((accn__id != accndsde) && tipo == 1) {
//                    tx = "update usst set usstfcsa = '${sale}' where usst__id = ${id}"
//                    sql.execute(tx.toString())
//
//                    tx = "insert into usst(prfl__id, accn__id, accndsde, prsn__id, usstfcen, usstsesn) values (" +
//                            "${session.perfil.id}, ${accn__id}, ${accndsde}, ${session.usuario.id}, '${fcha}', " +
//                            "${entero(session.id)})"
//                    sql.execute(tx.toString())
//                }
//                def fin = new Date()
//                //println "${TimeCategory.minus(fin, now)}"
//                /* fin de recgistro de actividad del usaurio */


                def usu = Persona.get(session.usuario.id)
//                println("usuario activo: " + usu.estaActivo)
                if (usu.estaActivo) {
                    session.departamento = Departamento.get(session.departamento.id).refresh()
                    def perms = session.usuario.permisos
                    session.usuario = Persona.get(session.usuario.id).refresh()
                    session.usuario.permisos = perms
                    if (session.usuario.esTriangulo()) {
                        if (session.departamento.estado == "B") {
                            if (isAllowedBloqueo()) {
                                return true
                            } else {
                                redirect(controller: 'shield', action: 'bloqueo', params: ["dep": true])
                                return false
                            }
                        } else {
                            if (!isAllowed()) {
                                redirect(controller: 'shield', action: 'unauthorized')
                                return false
                            }

                        }
                    } else {
                        if (session.usuario.estado == "B") {
                            if (isAllowedBloqueo()) {
                                return true
                            } else {
//                            redirect(controller: 'shield', action: 'bloqueo')
                                redirect(controller: 'shield', action: 'unauthorized')
                                return false
                            }
                        } else {
                            if (!isAllowed()) {
                                redirect(controller: 'shield', action: 'unauthorized')
                                return false
                            }
                        }
                    }

//                return true
                } else {
                println "session.flag shield "+session.flag
                    if (!session.flag || session.flag < 1) {
//                    println "menor que cero "+session.flag
                        session.usuario = null
                        session.perfil = null
                        session.permisos = null
                        session.menu = null
                        session.an = null
                        session.cn = null
                        session.invalidate()
                        session.flag = null
                        session.finalize()
                        redirect(controller: 'login', action: 'login')
                        return false
                    } else {
                        session.flag = session.flag - 1
                        session.departamento = Departamento.get(session.departamento.id).refresh()
                        return true
                    }
                }
            }
            /*************************************************************************** */
        }
    }


    boolean isAllowed() {
        try {
            if (request.method == "POST") {
//                println "es post no audit"
                return true
            }
//            println "is allowed Accion: ${actionName.toLowerCase()} ---  Controlador: ${controllerName.toLowerCase()} --- Permisos de ese controlador: "+session.permisos[controllerName.toLowerCase()]
            if (!session.permisos[controllerName.toLowerCase()]) {
                return false
            } else {
                if (session.permisos[controllerName.toLowerCase()].contains(actionName.toLowerCase())) {
                    return true
                } else {
                    return false
                }
            }

        } catch (e) {
            println "Shield execption e: " + e
            return false
        }
//            return false
//        return true

    }

    boolean isAllowedBloqueo() {
        def permitidas = [
                "inicio"          : ["index"],
                "tramite"         : ["bandejaEntrada", "tablaBandeja", "busquedaBandeja", "revisarConfidencial", "revisarHijos", "archivar", "saveTramite"],
                "tramite3"        : ["detalles", "arbolTramite", "recibirTramite", "bandejaEntradaDpto", "tablaBandejaEntradaDpto", "enviarTramiteJefe", "infoRemitente", "busquedaBandeja"],
                "documentoTramite": ["verAnexos", "cargaDocs"],
                "alertas"         : ["list", "revisar"],
                "persona"         : ["show_ajax"],
                "departamento"    : ["show_ajax"],
                "tramiteExport"   : ["crearPdf"]
        ]

        try {

            if (!permitidas[controllerName]) {
                return false
            }
            if (permitidas[controllerName].contains(actionName)) {
                return true
            }
        } catch (e) {
            println "Shield execption e: " + e
            return false
        }
        return false
    }


    def entero(s) {
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int num = (int) c;
            val += num;
        }
        val
    }


}
 
