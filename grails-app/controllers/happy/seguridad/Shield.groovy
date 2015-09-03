package happy.seguridad


import happy.tramites.Departamento


class Shield {
    def beforeInterceptor = [action: this.&auth, except: 'login']

    /**
     * Verifica si se ha iniciado una sesión
     * Verifica si el usuario actual tiene los permisos para ejecutar una acción
     */
    def auth() {
//        println "an " + actionName + " cn " + controllerName + "  "
//        println "shield sesión: " + session.usuario
        session.an = actionName
        session.cn = controllerName
        session.pr = params
//        return true

//        def para = alertaNoRecibidos().tramitesPasados

        /** **************************************************************************/

        if(session.an == 'saveTramite' && session.cn == 'tramite'){
//            println("entro")
            return true

        }else{
            if (!session.usuario || !session.perfil) {
//                            println "1"
//
//                if(session.an == 'saveTramite' && session.cn == 'tramite'){
//                    println("entro")
//                    return true
//                }else{
                    redirect(controller: 'login', action: 'login')
                    session.finalize()
                    return false
//                }

//            return true
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
                def usu = Persona.get(session.usuario.id)
//                println("usuario activo: " + usu.estaActivo)
                if (usu.estaActivo) {

//                println "AQUI??????"
//                println "controlador: $controllerName acción: $actionName"

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


}
 
