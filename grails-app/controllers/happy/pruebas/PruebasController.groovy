package happy.pruebas

import happy.alertas.Alerta
import happy.seguridad.Persona
import happy.tramites.EstadoTramite
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite
import happy.tramites.Tramite2Controller

class PruebasController {

    def index() {}

    def enviaTramite() {
        println "envia tramite " + params + "  " + session.usuario
        def usu = Persona.get(session.usuario.id)
        def tramitesE = Tramite.findAllByDeAndEstadoTramite(usu, EstadoTramite.findByCodigo("E001"))
        println "tramites " + tramitesE?.codigo
//        def roles = [RolPersonaTramite.findByCodigo(""),RolPersonaTramite.findByCodigo("")]
        if (tramitesE.size() > 0) {
            def tram = tramitesE.pop()
            println "enviando el!! " + tram.codigo
            def noPDF = ["DEX", "SUM"]
            def msg = ""
            def error = ""
            def tramite
            def tramites = []
            def ids = "" + tram.id
            ids = ids.split(',')
            def band = true
            ids.each { d ->
                println "d " + d
                def envio = new Date();
                tramite = Tramite.get(d)
                if (tramite.fechaEnvio) {
                    msg += "<br/>El trámite " + tramite.codigo + " ya fue enviado por " +
                            PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(tramite, RolPersonaTramite.findByCodigo("E004")).persona.login.join(", ")
                } else {
                    def pdtEliminar = []
                    PersonaDocumentoTramite.findAllByTramite(tramite).each { t ->
                        if (t.estado?.codigo != "E006" && t.estado?.codigo != "E005") {
                            t.fechaEnvio = envio
                            t.estado = EstadoTramite.findByCodigo("E003")
                            if (t.save(flush: true)) {
                                if (t.rolPersonaTramite?.codigo == "R001" || t.rolPersonaTramite?.codigo == "R002") {
                                    def alerta = new Alerta()
                                    if (t.tramite.tipoDocumento.codigo == "OFI") {
                                        alerta.mensaje = "${t.tramite.paraExterno} te ha enviado un trámite."
                                    } else {
                                        alerta.mensaje = "${session.departamento.codigo}:${session.usuario} te ha enviado un trámite."
                                    }
                                    if (t.persona) {
                                        alerta.controlador = "tramite"
                                        alerta.accion = "bandejaEntrada"
                                        alerta.persona = t.persona
                                    } else {
                                        alerta.departamento = t.departamento
                                        alerta.accion = "bandejaEntradaDpto"
                                        alerta.controlador = "tramite3"
                                    }
                                    alerta.datos = t.id
                                    alerta.tramite = t.tramite
                                    if (!alerta.save(flush: true)) {
                                        println "error save alerta " + alerta.errors
                                    }
                                }
                            }
                        } else {
                            println("entro false")
                            band = false
                        }

                        if (t.rolPersonaTramite.codigo == 'I005') {
                            //si tenia permiso imprimir se elimina
                            pdtEliminar += t.id
                        }
                    }

                    pdtEliminar.each { pdtId ->
                        def pdt = PersonaDocumentoTramite.get(pdtId)
                        pdt.delete(flush: true)
                    }

                    if (band) {
                        def pdt = new PersonaDocumentoTramite()
                        pdt.tramite = tramite
                        pdt.persona = session.usuario
                        pdt.departamento = session.departamento

                        pdt.personaSigla = pdt.persona.login
                        pdt.personaNombre = pdt.persona.nombre + " " + pdt.persona.apellido
                        pdt.departamentoNombre = pdt.departamento.descripcion
                        pdt.departamentoSigla = pdt.departamento.codigo

                        pdt.fechaEnvio = envio
                        pdt.rolPersonaTramite = RolPersonaTramite.findByCodigo("E004")
                        pdt.save(flush: true)
                        tramite.fechaEnvio = envio
                        tramite.estadoTramite = EstadoTramite.findByCodigo('E003')
                        if (tramite.save(flush: true)) {
                            def realPath = servletContext.getRealPath("/")
                            def mensaje = message(code: 'pathImages').toString();
                            if (!noPDF.contains(tramite.tipoDocumento.codigo)) {
//                                enviarService.crearPdf(tramite, usuario, "1", 'download', realPath, mensaje);
                            }
                        } else {
                            println "enviar tramite:" + tramite.errors
                            error += renderErrors(bean: tramite)
                        }
                    } else {
                        band = true
                        error += 'No se pudo enviar!'
                    }
                }
            }
            if (error == "") {
                render "ok_" + msg
            } else {
                render "no_" + error
            }


        } else {
            render "nada que enviar"
        }
    }

//    def recibirTramite() {
//        println "recibe tramite " + params + "  " + session.usuario
//        def usu = Persona.get(session.usuario.id)
//        def roles = [RolPersonaTramite.findByCodigo("R001"), RolPersonaTramite.findByCodigo("R002")]
//        def pdts = PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite  where persona = ${usu.id} and rolPersonaTramite in (${RolPersonaTramite.findByCodigo('R001').id},${RolPersonaTramite.findByCodigo('R002')}) and estado = ${EstadoTramite.findByCodigo('E003')}")
//        if (pdts.size() > 0) {
//
//
//            def tramite = pdts.pop().tramite
//            def porEnviar = EstadoTramite.findByCodigo("E001")
//            def enviado = EstadoTramite.findByCodigo("E003")
//            def recibido = EstadoTramite.findByCodigo("E004")
//            //tambien puede recibir si ya esta en estado recibido (se pone en recibido cuando recibe el PARA)
//            // println tramite.estadoTramite.descripcion
//            if (tramite.estadoTramite != enviado && tramite.estadoTramite != recibido) {
//                render "ERROR_Se ha cancelado el proceso de recepción.<br/>Este trámite no puede ser gestionado."
//                return
//            }
//            def paraDpto = tramite.para?.departamento
//            def paraPrsn = tramite.para?.persona
//
//            def archivado = EstadoTramite.findByCodigo("E005")
//            def anulado = EstadoTramite.findByCodigo("E006")
//            def noRecibe = [archivado, anulado]
//
//            def esCircular = false
//            if (!paraPrsn && !paraDpto) {
//                esCircular = true
//            }
//
//            def rolPara = RolPersonaTramite.findByCodigo("R001")
//            def rolCC = RolPersonaTramite.findByCodigo("R002")
//            def rolImprimir = RolPersonaTramite.findByCodigo("I005")
//            def triangulo = false
//
//            def estadoRecibido = EstadoTramite.findByCodigo('E004') //recibido
//            def estadoAnulado = EstadoTramite.findByCodigo('E006') //recibido
//            def estadoArchivado = EstadoTramite.findByCodigo('E005') //recibido
////            println "es circu "+esCircular+" depto "+triangulo
//            def pxt = PersonaDocumentoTramite.withCriteria {
//                eq("tramite", tramite)
//                if (!esCircular) {
//                    if (triangulo) {
//                        eq("departamento", persona.departamento)
//                    } else {
//                        eq("persona", persona)
//                    }
//                } else {
//                    if (triangulo) {
//                        eq("departamento", persona.departamento)
//                    } else {
//                        eq("persona", persona)
//                    }
////                    or {
////                        eq("departamento", persona.departamento)
////                        eq("persona", persona)
////                    }
//                }
//                or {
//                    eq("rolPersonaTramite", rolPara)
//                    eq("rolPersonaTramite", rolCC)
////                    eq("rolPersonaTramite", rolImprimir)
//                }
//                and {
//                    ne("estado", estadoAnulado)
//                    ne("estado", estadoArchivado)
//                }
//            }//PersonaDocumentoTramite.findByTramiteAndDepartamento(tramite, persona.departamento)
//
//            if (pxt.size() == 0) {
//                render "ERROR_Este trámite no puede ser gestionado. Por favor actualice su bandeja"
//                return
//            }
//
//
//            if (pxt.size() > 1) {
//                pxt.each {
//                    println " " + it.persona + "   " + it.departamento + "   " + it.rolPersonaTramite.descripcion + "  " + it.tramite
//                }
////                flash.message = "ERROR"
//                println "mas de 1 PDT: ${pxt}"
////                redirect(action: "errores")
//                return
//            } else if (pxt.size() == 0) {
//                flash.message = "ERROR"
//                println "0 PDT"
//                redirect(action: "errores")
//            } else {
//                pxt = pxt.first()
//                def recibe = true
//                if (noRecibe.contains(pxt.estado)) {
//                    recibe = false
//                }
//                if (!recibe) {
//                    render "ERROR_El trámite se encuentra anulado o archivado y no puede ser gestionado."
//                    return
//                }
//            }
//
////            println("pxt 2"  + pxt )
////            println "Estado del pdt " + pxt.estado.codigo
//            if (pxt.estado.codigo != "E004") {
//
//                if (paraDpto && persona.departamentoId == paraDpto.id) {
//                    tramite.estadoTramite = estadoRecibido
//                }
//                if (paraPrsn && persona.id == paraPrsn.id) {
//                    tramite.estadoTramite = estadoRecibido
//                }
//
//                def hoy = new Date()
//
//                def limite = hoy
////        use(TimeCategory) {
////            limite = limite + tramite.prioridad.tiempo.hours
////        }
//                limite = diasLaborablesService.fechaMasTiempo(limite, tramite.prioridad.tiempo)
//                if (limite[0]) {
//                    limite = limite[1]
//                } else {
//                    flash.message = "Ha ocurrido un error al calcular la fecha límite: " + limite[1]
//                    redirect(controller: 'tramite', action: 'errores')
//                    return
//                }
////            println "aaa1"
////            println "hoy "+hoy
////            println "pxt "+pxt
//                pxt.fechaRecepcion = hoy
////            println "aaa2"
//                pxt.fechaLimiteRespuesta = limite
//                pxt.estado = EstadoTramite.findByCodigo("E004")
//
//                if (pxt.save(flush: true) && tramite.save(flush: true)) {
//                    def pdt = new PersonaDocumentoTramite()
//                    pdt.tramite = tramite
//                    pdt.persona = persona
//                    pdt.rolPersonaTramite = RolPersonaTramite.findByCodigo("E003")
//                    pdt.fechaRecepcion = hoy
//                    pdt.fechaLimiteRespuesta = limite
//                    def alerta
//                    if (pxt.departamento) {
//                        alerta = Alerta.findByTramiteAndDepartamento(pxt.tramite, pxt.departamento)
//                    }
//                    if (pxt.persona) {
//                        alerta = Alerta.findByTramiteAndPersona(pxt.tramite, pxt.persona)
//                    }
//                    if (alerta) {
//                        if (alerta.fechaRecibido == null) {
//                            alerta.mensaje += " - Recibido"
//                            alerta.fechaRecibido = new Date()
//                            alerta.save()
//                        }
//                    }
//                    if (pdt.save(flush: true)) {
//                        render "OK_Trámite recibido correctamente"
//                    } else {
//                        println "error pdt recibir " + pdt.errors
//                        render "NO_Ocurrió un error al recibir"
//                    }
//
//                } else {
//                    println "pxt error " + pxt.errors
//                    println "error tramite recibir " + tramite.errors
//                    render "NO_Ocurrió un error al recibir"
//                }
//            } else {
//                println "estado 4 " + pxt.id + "  " + pxt.estado.codigo + "   " + pxt.estado.descripcion + "   " + pxt.fechaRecepcion
//                render "NO_Ocurrió un error al recibir"
//            }
//
//
//        } else {
//            render "nada que recibir"
//        }
//    }

}
