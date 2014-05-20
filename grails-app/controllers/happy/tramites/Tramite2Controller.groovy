package happy.tramites

import groovy.time.TimeCategory
import happy.alertas.Alerta
import happy.seguridad.Persona
import happy.utilitarios.DiaLaborable
import org.w3c.dom.Document
import org.xhtmlrenderer.extend.FontResolver
import org.xhtmlrenderer.pdf.ITextRenderer

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class Tramite2Controller extends happy.seguridad.Shield {

    def diasLaborablesService
    def enviarService

    def verTramite() {
        /*comentar esto*/
        def tramite = Tramite.get(params.id)
        /*Aqui controlar los permisos para ver el tramite por el usuario*/

        /*fin permisos*/

        return [tramite: tramite]
    }

    def revision() {

        def tramite = Tramite.get(params.id).refresh()

        /*Todo hacer la validacion para determinar si es el jefe*/

        return [tramite: tramite]
    }

    def saveNotas() {
        def tramite = Tramite.get(params.tramite)
        tramite.nota = params.notas
        if (tramite.save(flush: true))
            render "ok"
        else
            render "error"

    }

    def revisar() {

        if (request.getMethod() == "POST") {
            def tramite = Tramite.get(params.id)
            /*validaciones*/
            def user = Persona.get(session.usuario.id)
            def msg = ""
            def band = true
            def per = PermisoUsuario.findByPersonaAndPermisoTramite(user, PermisoTramite.findByCodigo("P005"))
            if (tramite.de.departamento.id != user.departamento.id) {
                band = false
            }
            if (user.jefe != 1 && !per)
                band = false
            if (band) {
                if (tramite.estadoTramite.codigo == "E001") {
                    tramite.estadoTramite = EstadoTramite.findByCodigo("E002")
                }
                if (tramite.save(flush: true))
                    render "ok"
                else
                    render "error"
            } else {
                msg = "Usted no tiene autorización para revisar este tramite"
                render "error_" + msg
            }

        } else {
            response.sendError(403)
        }
    }

    def bandejaSalidaDep() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def revisar = false
        def bloqueo = false
        def triangulo = PermisoTramite.findByCodigo("E001")
//        if(session.departamento.estado=="B")
//            bloqueo=true
//        println "bloqueo "+bloqueo
        def tienePermiso = PermisoUsuario.withCriteria {
            eq("persona", persona)
            eq("permisoTramite", triangulo)
            le("fechaInicio", new Date())
            or {
                ge("fechaFin", new Date())
                isNull("fechaFin")
            }
        }
        println "tiene " + tienePermiso + " jefe " + persona.jefe
        if (tienePermiso.size() == 0 && persona.jefe != 1) {
            flash.message = "El usuario no tiene los permisos necesarios para acceder a la bandeja de salida del departamento. Ha sido redireccionado a su bandeja de salida personal."
            flash.tipo = "error"

            redirect(controller: "tramite2", action: "bandejaSalida")
            return
        }
        if (persona.jefe == 1)
            revisar = true
        else {
            def per = PermisoUsuario.findByPersonaAndPermisoTramite(persona, PermisoTramite.findByCodigo("P005"))
            if (per)
                revisar = true
        }
        return [persona: persona, revisar: revisar, bloqueo: bloqueo]

    }

    def bandejaSalidaDep_old() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def revisar = false
        def bloqueo = false
        def triangulo = PermisoTramite.findByCodigo("E001")
//        if(session.departamento.estado=="B")
//            bloqueo=true
//        println "bloqueo "+bloqueo
        def tienePermiso = PermisoUsuario.withCriteria {
            eq("persona", persona)
            eq("permisoTramite", triangulo)
            le("fechaInicio", new Date())
            or {
                ge("fechaFin", new Date())
                isNull("fechaFin")
            }
        }
        println "tiene " + tienePermiso + " jefe " + persona.jefe
        if (tienePermiso.size() == 0 && persona.jefe != 1) {
            flash.message = "El usuario no tiene los permisos necesarios para acceder a la bandeja de salida del departamento. Ha sido redireccionado a su bandeja de salida personal."
            flash.tipo = "error"

            redirect(controller: "tramite2", action: "bandejaSalida")
            return
        }
        if (persona.jefe == 1)
            revisar = true
        else {
            def per = PermisoUsuario.findByPersonaAndPermisoTramite(persona, PermisoTramite.findByCodigo("P005"))
            if (per)
                revisar = true
        }
        return [persona: persona, revisar: revisar, bloqueo: bloqueo]

    }

    def tablaBandejaSalidaDep() {
//        println "carga bandeja"
        def persona = Persona.get(session.usuario.id)
        def tramites = []
//        def estados = EstadoTramite.findAllByCodigoInList(["E001", "E002", "E003", "E004"])

        def porEnviar = EstadoTramite.findByCodigo("E001")
        def revisado = EstadoTramite.findByCodigo("E002")
        def enviado = EstadoTramite.findByCodigo("E003")
        def recibido = EstadoTramite.findByCodigo("E004")

        def para = RolPersonaTramite.findByCodigo("R001")
        def cc = RolPersonaTramite.findByCodigo("R002")
//        tramites = Tramite.findAllByDeDepartamentoAndEstadoTramiteInList(persona.departamento, estados, [sort: "fechaCreacion", order: "desc"])

        def trams = Tramite.withCriteria {
            eq("deDepartamento", persona.departamento)
            inList("estadoTramite", [porEnviar, revisado, enviado, recibido])
            order("fechaCreacion", "desc")
        }
//        println "tramites "+trams.codigo
        trams.each { tr ->
            def pxd = PersonaDocumentoTramite.withCriteria {
                eq("tramite", tr)
                inList("rolPersonaTramite", [para, cc])
                isNull("fechaRecepcion")
            }
            if (pxd.size() > 0) {
                tramites += tr
            }
        }

        return [persona: persona, tramites: tramites]
    }

    def tablaBandejaSalidaDep_old() {
//        println "carga bandeja"
        def persona = Persona.get(session.usuario.id)
        def tramites = []
        def estados = EstadoTramite.findAllByCodigoInList(["E001", "E002", "E003"])
        tramites = Tramite.findAllByDeDepartamentoAndEstadoTramiteInList(persona.departamento, estados, [sort: "fechaCreacion", order: "desc"])

        return [persona: persona, tramites: tramites]
    }

    def desenviar_ajax() {

        println("params des" + params)

        def tramite = Tramite.get(params.id)
        def porEnviar = EstadoTramite.findByCodigo("E001")
        def ids

        if (params.ids) {
            ids = params.ids
        } else {
            ids = null
        }


        def errores = ""

        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCc = RolPersonaTramite.findByCodigo("R002")

        //esta quitando el enviado a estos


        (ids.split("_")).each { id ->
            def persDoc = PersonaDocumentoTramite.get(id.toLong())
            //cambia la fecha de envio, el estadoy las obs
            persDoc.fechaEnvio = null
            persDoc.estado = porEnviar
            persDoc.observaciones = (persDoc.observaciones ?: '') + " Cancelado el envío por el usuario ${session.usuario.login} " +
                    "el ${new Date().format('dd-MM-yyyy HH:mm')} " +
                    "(previa fecha de envío: " +
                    "${persDoc.fechaEnvio ? persDoc.fechaEnvio.format('dd-MM-yyyy HH:mm') : tramite.fechaEnvio?.format('dd-MM-yyyy HH:mm')})"
            if (persDoc.save(flush: true)) {
                def alerta
                if (persDoc.persona)
                    alerta = Alerta.findByPersonaAndTramite(persDoc.persona, persDoc.tramite)
                else
                    alerta = Alerta.findByDepartamentoAndTramite(persDoc.departamento, persDoc.tramite)
                if (alerta) {
                    alerta.mensaje += " - Tramite cambiado de estado"
                    alerta.fechaRecibido = new Date()
                    alerta.save(flush: true)
                }
            } else {
                println "ERROR AL CAMBIAR PERS DOC TRAM: " + persDoc.errors
                errores += "<li>" + renderErrors(bean: persDoc) + "</li>"
            }
        }

        //originalmente era para todos estos: verifico si ninguno ha recibido le cambio el estado al tramite a borrador
        def recibidos = 0
        PersonaDocumentoTramite.withCriteria {
            eq("tramite", tramite)
            inList("rolPersonaTramite", [rolPara, rolCc])
        }.each { persDoc ->
            if (persDoc.fechaRecepcion) {
                recibidos++
            }
        }
        if (recibidos == 0) {
            tramite.observaciones = (tramite.observaciones ?: '') + " Cancelado el envío por el usuario ${session.usuario.login} " +
                    "el ${new Date().format('dd-MM-yyyy HH:mm')} " +
                    "(previa fecha de envío: ${tramite.fechaEnvio.format('dd-MM-yyyy HH:mm')})"
            tramite.estadoTramite = porEnviar
            tramite.fechaEnvio = null
        }
        if (!tramite.save(flush: true)) {
            println "ERROR AL CAMBIAR ESTADO TRAMITE: " + tramite.errors
            errores += "<li>" + renderErrors(bean: tramite) + "</li>"
        }

        if (errores == "") {
            render "OK_Envío del trámite cancelado correctamente"
        } else {
            render "NO_Ha ocurrido un error al cancelar el envío del trámite: " + errores
        }

        //ESTA PARTE DESENVIABA EL TRAMITE ENTERO: YA NO SE USA
//        tramite.observaciones = (tramite.observaciones ?: '') + " Cancelado el envío por el usuario ${session.usuario.login} " +
//                "el ${new Date().format('dd-MM-yyyy HH:mm')} " +
//                "(previa fecha de envío: ${tramite.fechaEnvio.format('dd-MM-yyyy HH:mm')})"
//        tramite.estadoTramite = porEnviar
//        tramite.fechaEnvio = null
//
//        if (tramite.save(flush: true)) {
//            def personas = PersonaDocumentoTramite.findAllByTramite(tramite)
//            def errores = ""
//            personas.each {
//                if (it.rolPersonaTramite.codigo != 'E004') {
//                    def alerta
//                    if (it.persona)
//                        alerta = Alerta.findByPersonaAndTramite(it.persona, it.tramite)
//                    else
//                        alerta = Alerta.findByDepartamentoAndTramite(it.departamento, it.tramite)
//                    if (alerta) {
//                        alerta.mensaje += " - Tramite cambiado de estado"
//                        alerta.fechaRecibido = new Date()
//                        alerta.save(flush: true)
//                    }
//                    it.fechaEnvio = null
//                    if (!it.save(flush: true)) {
//                        errores += "<li>" + renderErrors(bean: it) + "</li>"
//                    }
//                } //no es el que envia
//            } //persona doc tramite . each
//            if (errores == "") {
//                render "OK_Envío del trámite cancelado correctamente"
//            } else {
//                render "NO_Ha ocurrido un error al cancelar el envío del trámite: " + errores
//            }
//        } else {
//            render "NO_Ha ocurrido un error al cancelar el envío del trámite: " + renderErrors(bean: tramite)
//        }
    }

    def desenviarLista_ajax() {
        def tramite = Tramite.get(params.id)

        def rolPara = RolPersonaTramite.findByCodigo("R001")
        def rolCc = RolPersonaTramite.findByCodigo("R002")

        def paras = PersonaDocumentoTramite.withCriteria {
            eq("tramite", tramite)
            eq("rolPersonaTramite", rolPara)
        }
        def ccs = PersonaDocumentoTramite.withCriteria {
            eq("tramite", tramite)
            eq("rolPersonaTramite", rolCc)
        }

        return [tramite: tramite, paras: paras, ccs: ccs]
    }

    def bandejaSalida() {
        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def revisar = false
        def bloqueo = false
//        if (session.departamento.estado == "B") {
//            bloqueo = true
//        }
//        println "bloqueo " + bloqueo
        if (persona.jefe == 1)
            revisar = true
        else {
            def per = PermisoUsuario.findByPersonaAndPermisoTramite(persona, PermisoTramite.findByCodigo("P005"))
            if (per) {
                revisar = true
            }
        }
        def departamento = Persona.get(usuario.id).departamento
        def personal = Persona.findAllByDepartamento(departamento)
        def personalActivo = []
        personal.each {
            if (it?.estaActivo && it?.id != usuario.id) {
                personalActivo += it
            }
        }
        return [persona: persona, revisar: revisar, bloqueo: bloqueo, personal: personalActivo]
    }

    def bandejaSalida_old() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def revisar = false
        def bloqueo = false
        if (session.departamento.estado == "B")
            bloqueo = true
        println "bloqueo " + bloqueo
        if (persona.jefe == 1)
            revisar = true
        else {
            def per = PermisoUsuario.findByPersonaAndPermisoTramite(persona, PermisoTramite.findByCodigo("P005"))
            if (per)
                revisar = true
        }

        def departamento = Persona.get(usuario.id).departamento

        def personal = Persona.findAllByDepartamento(departamento)

        def personalActivo = []

        personal.each {
            if (it?.activo == 1 && it?.id != usuario.id) {
                personalActivo += it
            }
        }

        return [persona: persona, revisar: revisar, bloqueo: bloqueo, personal: personalActivo]

    }

    def tablaBandejaSalida() {
//        println "carga bandeja"

        def porEnviar = EstadoTramite.findByCodigo("E001")
        def revisado = EstadoTramite.findByCodigo("E002")
        def enviado = EstadoTramite.findByCodigo("E003")
        def recibido = EstadoTramite.findByCodigo("E004")

        def para = RolPersonaTramite.findByCodigo("R001")
        def cc = RolPersonaTramite.findByCodigo("R002")
        def rolImprimir = RolPersonaTramite.findByCodigo('I005')

        def persona = Persona.get(session.usuario.id)
        def tramites = []
//        def estados = EstadoTramite.findAllByCodigoInList(["E001", "E002", "E003", "E004"])
        def estados = [porEnviar, revisado, enviado, recibido]
        if (persona.jefe == 1) {
            Persona.findAllByDepartamento(persona.departamento).each { p ->
                def t = Tramite.findAllByDeAndEstadoTramiteInList(p, estados, [sort: "fechaCreacion", order: "desc"])

//                def t = Tramite.withCriteria {
//                    eq("de", p)
//                    isNull("deDepartamento")
//                    inList("estadoTramite", estados)
//                    order("fechaCreacion", "desc")
//                }
                if (t.size() > 0)
                    tramites += t
                t = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(p, rolImprimir).tramite
                if (t.size() > 0)
                    tramites += t
            }
            def t = Tramite.findAllByDeDepartamentoAndEstadoTramiteInList(persona.departamento, estados, [sort: "fechaCreacion", order: "desc"])
            if (t.size() > 0)
                tramites += t
        } else {
//            tramites = Tramite.findAllByDeAndEstadoTramiteInList(persona, estados, [sort: "fechaCreacion", order: "desc"])
            tramites = Tramite.withCriteria {
                eq("de", persona)
                isNull("deDepartamento")
                inList("estadoTramite", estados)
                order("fechaCreacion", "desc")
            }
            def t = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolImprimir).tramite
            if (t.size() > 0)
                tramites += t
        }
        tramites?.sort { it.fechaCreacion }
        tramites = tramites?.reverse()

        def trams = []
        def trams2 = []

        tramites.each { tr ->
            def pxd = PersonaDocumentoTramite.withCriteria {
                eq("tramite", tr)
                inList("rolPersonaTramite", [para, cc])
                isNull("fechaRecepcion")
            }
            if (pxd.size() > 0) {
                trams += tr
            }
        }

        return [persona: persona, tramites: trams]
    }

    def tablaBandejaSalida_old() {
//        println "carga bandeja"
        def persona = Persona.get(session.usuario.id)
        def tramites = []
        def estados = EstadoTramite.findAllByCodigoInList(["E001", "E002", "E003"])
        def rolImprimir = RolPersonaTramite.findByCodigo('I005')
        if (persona.jefe == 1) {
            Persona.findAllByDepartamento(persona.departamento).each { p ->
                def t = Tramite.findAllByDeAndEstadoTramiteInList(p, estados, [sort: "fechaCreacion", order: "desc"])
                if (t.size() > 0)
                    tramites += t
                t = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(p, rolImprimir).tramite
                if (t.size() > 0)
                    tramites += t
            }
            def t = Tramite.findAllByDeDepartamentoAndEstadoTramiteInList(persona.departamento, estados, [sort: "fechaCreacion", order: "desc"])
            if (t.size() > 0)
                tramites += t
        } else {
            tramites = Tramite.findAllByDeAndEstadoTramiteInList(persona, estados, [sort: "fechaCreacion", order: "desc"])
            def t = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolImprimir).tramite
            if (t.size() > 0)
                tramites += t
        }
        tramites?.sort { it.fechaCreacion }
        tramites = tramites?.reverse()
        return [persona: persona, tramites: tramites]
    }


    def enviar() {
        println "method " + request.getMethod()
        println "PARAMS " + params
        /*todo sin validacion alguna... que envie no mas cualquiera*/
        if (request.getMethod() == "POST") {
            def msg = ""
            def tramite = Tramite.get(params.id)
            def envio = new Date()

            PersonaDocumentoTramite.findAllByTramite(tramite).each { t ->
                t.fechaEnvio = envio
                t.save(flush: true)
            }
            def pdt = new PersonaDocumentoTramite()
            pdt.tramite = tramite
            pdt.persona = session.usuario
            pdt.departamento = session.departamento
            pdt.fechaEnvio = envio
            pdt.rolPersonaTramite = RolPersonaTramite.findByCodigo("E004")
            pdt.save(flush: true)
            tramite.fechaEnvio = envio
            tramite.estadoTramite = EstadoTramite.findByCodigo('E003')
            if (tramite.save(flush: true)) {
                //CREAR PDF

//             redirect(controller: 'tramiteExport', action: 'crearPdf', params: params)

                //
//                render "ok"
            } else {
                println tramite.errors
                render "no: " + renderErrors(bean: tramite)
            }
        } else {
//            response.sendError(403)
            render "403"
        }
    }

    //enviar varios

    def enviarVarios() {
//        println "method "+request.getMethod()
        println "PARAMS " + params
        def usuario = Persona.get(session.usuario.id)

        if (request.getMethod() == "POST") {
            def msg = ""
            def error = ""
            def tramite
            def tramites = []
            def ids = params.ids
//            println("--->>>" + ids)
//            println(ids.class)
            ids = ids.split(',')
//            println ids
//            render "no"
//            return
//            if(ids instanceof java.lang.String){
//                ids = [ids]
//            }
            ids.each { d ->
//                println('\t'  + d)
                def envio = new Date();
                tramite = Tramite.get(d)
                PersonaDocumentoTramite.findAllByTramite(tramite).each { t ->
//                    println("llllll" + t)
                    t.fechaEnvio = envio
                    t.estado = EstadoTramite.findByCodigo("E003")
                    if (t.save(flush: true)) {
                        if (t.rolPersonaTramite?.codigo == "R001" || t.rolPersonaTramite?.codigo == "R002") {
                            def alerta = new Alerta()
                            alerta.mensaje = "${session.departamento.codigo}:${session.usuario} te ha enviado un trámite."
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
                }
                def pdt = new PersonaDocumentoTramite()
                pdt.tramite = tramite
                pdt.persona = session.usuario
                pdt.departamento = session.departamento
                pdt.fechaEnvio = envio
                pdt.rolPersonaTramite = RolPersonaTramite.findByCodigo("E004")
                pdt.save(flush: true)
                tramite.fechaEnvio = envio
                tramite.estadoTramite = EstadoTramite.findByCodigo('E003')
                if (tramite.save(flush: true)) {
                    def realPath = servletContext.getRealPath("/")
                    def mensaje = message(code: 'pathImages').toString();
                    enviarService.crearPdf(tramite, usuario, "1", 'download', realPath, mensaje);
                } else {
                    println tramite.errors
                    error += renderErrors(bean: tramite)
                }

                println("-->" + pdt)

            }
            if (error == "") {
                render "ok"
            } else {
                render "no_" + error
            }
        } else {
            render "403"
        }
    }


    def busquedaBandejaSalida() {

        def porEnviar = EstadoTramite.findByCodigo("E001")
        def revisado = EstadoTramite.findByCodigo("E002")
        def enviado = EstadoTramite.findByCodigo("E003")
        def recibido = EstadoTramite.findByCodigo("E004")

        def para = RolPersonaTramite.findByCodigo("R001")
        def cc = RolPersonaTramite.findByCodigo("R002")
        def rolImprimir = RolPersonaTramite.findByCodigo('I005')

        def persona = Persona.get(session.usuario.id)
        def tramites = []
//        def estados = EstadoTramite.findAllByCodigoInList(["E001", "E002", "E003", "E004"])
        def estados = [porEnviar, revisado, enviado, recibido]
        if (persona.jefe == 1) {
            Persona.findAllByDepartamento(persona.departamento).each { p ->
                def t = Tramite.findAllByDeAndEstadoTramiteInList(p, estados, [sort: "fechaCreacion", order: "desc"])

//                def t = Tramite.withCriteria {
//                    eq("de", p)
//                    isNull("deDepartamento")
//                    inList("estadoTramite", estados)
//                    order("fechaCreacion", "desc")
//                }
                if (t.size() > 0)
                    tramites += t
                t = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(p, rolImprimir).tramite
                if (t.size() > 0)
                    tramites += t
            }
            def t = Tramite.findAllByDeDepartamentoAndEstadoTramiteInList(persona.departamento, estados, [sort: "fechaCreacion", order: "desc"])
            if (t.size() > 0)
                tramites += t
        } else {
//            tramites = Tramite.findAllByDeAndEstadoTramiteInList(persona, estados, [sort: "fechaCreacion", order: "desc"])
            tramites = Tramite.withCriteria {
                eq("de", persona)
                isNull("deDepartamento")
                inList("estadoTramite", estados)
                order("fechaCreacion", "desc")
            }
            def t = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolImprimir).tramite
            if (t.size() > 0)
                tramites += t
        }
        tramites?.sort { it.fechaCreacion }
        tramites = tramites?.reverse()

        def trams = []
        def trams2 = []

        tramites.each { tr ->
            def pxd = PersonaDocumentoTramite.withCriteria {
                eq("tramite", tr)
                inList("rolPersonaTramite", [para, cc])
                isNull("fechaRecepcion")
            }
            if (pxd.size() > 0) {
                trams += tr
            }
        }

//busqueda

        if (params.fecha) {
            params.fechaIni = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 00:00:00")
            params.fechaFin = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 23:59:59")
        }

        def res = PersonaDocumentoTramite.withCriteria {

            if (params.fecha) {
                gt('fechaEnvio', params.fechaIni)
                lt('fechaEnvio', params.fechaFin)
            }

            tramite {
                if (params.asunto) {
                    ilike('asunto', '%' + params.asunto + '%')
                }
                if (params.memorando) {
                    ilike('codigo', '%' + params.memorando + '%')
                }
            }
        }

//        println("-->" + trams)
//        println("DD" + res.tramite.id.unique())

        return [tramites: res.tramite.unique(), pxtTramites: trams]


    }

    def verRezagados() {
        def dep = session.departamento
        def tramites = []
//        def ahora = new Date().plus(2)
        def ahora = new Date()
        PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite  where fechaEnvio is not null and fechaRecepcion is null and departamento=${dep.id} and persona is null and rolPersonaTramite not in (4,5) order by fechaEnvio ").each { pdt ->

//            println "fecha bloqueo " + pdt.tramite.fechaBloqueo+"  id "+pdt.id
            def fechaBloqueo = pdt.tramite.fechaBloqueo
            if (fechaBloqueo && fechaBloqueo < ahora) {
                if (!tramites.tramite.id.contains(pdt.tramite.id)) {
                    println "add tramites " + pdt
                    tramites.add(pdt)
                }
            }


        }
        return [tramites: tramites]
    }

    def verRezagadosUsu() {
        def tramites = []
//        def ahora = new Date().plus(2)
        def ahora = new Date()
        PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite  where fechaEnvio is not null and fechaRecepcion is null and persona=${session.usuario.id} and rolPersonaTramite not in (4,5)  order by fechaEnvio").each { pdt ->
//            println "fecha bloqueo " + pdt.tramite.fechaBloqueo
            println "pdt " + pdt.id + "  bloq " + pdt.tramite.fechaBloqueo
            def fechaBloqueo = pdt.tramite.fechaBloqueo
            if (fechaBloqueo && fechaBloqueo < ahora) {
                println "add tramites pdt " + pdt.id
                tramites.add(pdt)
            }
        }
        return [tramites: tramites]
    }


    def crearTramiteDep() {

        if (Persona.get(session.usuario.id).tiposDocumento.size() == 0) {
            flash.message = "No puede crear ningún tipo de documento. Contáctese con el administrador."
            redirect(controller: 'tramite', action: "errores")
            return
        }

        def anio = Anio.findAllByNumero(new Date().format("yyyy"), [sort: "id"])
        if (anio.size() == 0) {
            flash.message = "El año ${new Date().format('yyyy')} no está creado, no puede crear trámites nuevos. Contáctese con el administrador."
            redirect(controller: 'tramite', action: "errores")
            return
        } else if (anio.size() > 1) {
            println "HAY MAS DE 1 ANIO ${new Date().format('yyyy')}!!!!!: ${anio}"
        }

        if (anio.findAll { it.estado == 1 }.size() == 0) {
            flash.message = "El año ${new Date().format('yyyy')} no está activado, no puede crear trámites nuevos. Contáctese con el administrador."
            redirect(controller: 'tramite', action: "errores")
            return
        }

        def dias = DiaLaborable.countByAnio(anio.first())
        if (dias < 365) {
            flash.message = "No se encontraron los registros de días laborables del año ${new Date().format('yyyy')}, no puede crear trámites nuevos. Contáctese con el administrador."
            redirect(controller: 'tramite', action: "errores")
            return
        }

//        println("params " + params)
        def rolesNo = [RolPersonaTramite.findByCodigo("E004"), RolPersonaTramite.findByCodigo("E003")]
        def padre = null
        def cc = ""
        def principal = null
        def tramite = new Tramite(params)
        if (params.padre) {
            padre = Tramite.get(params.padre)
            principal = padre
            while (true) {
                if (!principal.padre) {
                    break
                } else {
                    principal = principal.padre
                }
            }
        }
        if (params.id) {
            tramite = Tramite.get(params.id)
            padre = tramite.padre
            principal = padre
            if (principal) {
                while (true) {
                    if (!principal.padre) {
                        break
                    } else {
                        principal = principal.padre
                    }
                }
            }
            (tramite.copias).each { c ->
                if (cc != '') {
                    cc += "_"
                }
                if (c.departamento) {
                    cc += ("-" + c.departamentoId)
                } else {
                    cc += c.personaId
                }
            }
        } else {
            tramite.fechaCreacion = new Date()
        }

        def persona = Persona.get(session.usuario.id)

        def de = session.usuario
        def disp, disponibles = []
        def disp2 = []
        def todos = []
        def users = []

        if (persona.puedeTramitar) {
            disp = Departamento.list([sort: 'descripcion'])
        } else {
            disp = [persona.departamento]
        }

        //original
//        disp.each { dep ->
//            disponibles.add([id: dep.id * -1, label: dep.descripcion, obj: dep])
//            if (dep.id == persona.departamentoId) {
//                def users = Persona.findAllByDepartamento(dep)
//                for (int i = users.size() - 1; i > -1; i--) {
//                    if (!(users[i].estaActivo && users[i].puedeRecibir)) {
//                        users.remove(i)
//                    } else {
//                        disponibles.add([id: users[i].id, label: users[i].toString(), obj: users[i]])
//                    }
//                }
//            }
//        }

        //modificado
        disp.each { dep ->
            if (dep.id == persona.departamento?.id) {
                def usuarios = Persona.findAllByDepartamento(dep)
                usuarios.each {
                    if (it.id != de.id) {
                        users += it
                    }

                }

                for (int i = users.size() - 1; i > -1; i--) {
                    if (!(users[i].estaActivo && users[i].puedeRecibir)) {
                        users.remove(i)
                    } else {
                        if (params.id) {
                            if (!(tramite.copias.persona.id*.toLong()).contains(users[i].id.toLong())) {
                                disponibles.add([id     : users[i].id,
                                                 label  : users[i].toString(),
                                                 obj    : users[i],
                                                 externo: false],)
                            }
                        } else {
                            disponibles.add([id     : users[i].id,
                                             label  : users[i].toString(),
                                             obj    : users[i],
                                             externo: false])
                        }
                    }
                }
            }
        }

        def idDepartamento = (Persona.get(session.usuario.id)?.departamento?.id)
        def negativo = idDepartamento * -1
        def lista = new ArrayList()

        disp.each { dep ->
            if (dep.id * -1 != negativo) {
                if (params.id) {
                    if (!(tramite.copias.departamento.id*.toLong()).contains(dep.id.toLong())) {
                        if (dep.triangulos.size() > 0) {
                            disp2.add([id     : dep.id * -1,
                                       label  : dep.descripcion,
                                       obj    : dep,
                                       externo: dep.externo == 1])
                        }
                    }
                } else {
                    if (dep.triangulos.size() > 0) {
                        disp2.add([id     : dep.id * -1,
                                   label  : dep.descripcion,
                                   obj    : dep,
                                   externo: dep.externo == 1])
                    }
                }
            }
        }

        todos = disponibles + disp2
        def bloqueo = false
        if (session.departamento.estado == "B") {
            bloqueo = true
        }

        def pdt = null
        if (params.pdt) {
            pdt = params.pdt
        } else if (params.hermano) {
            def herm = Tramite.get(params.hermano)
            pdt = herm.aQuienContesta.id
        }
        return [de     : de, padre: padre, principal: principal, disponibles: todos, tramite: tramite,
                bloqueo: bloqueo, cc: cc, rolesNo: rolesNo, pxt: pdt]
    }
/*
        paramsTramite.deDepartamento = persona.departamento
        paramsTramite.deDepartamento.id = persona.departamento.id
 */

    def saveDep() {

        def persona = Persona.get(session.usuario.id)
        def estadoTramiteBorrador = EstadoTramite.findByCodigo("E001");

        def paramsOrigen = params.remove("origen")
        def paramsTramite = params.remove("tramite")

        def tipoTramite
        if (params.confi == "on") {
            tipoTramite = TipoTramite.findByCodigo("C")
        } else {
            tipoTramite = TipoTramite.findByCodigo("N")
        }
        paramsTramite.tipoTramite = tipoTramite
        if (params.anexo == "on") {
            paramsTramite.anexo = 1
        } else {
            paramsTramite.anexo = 0
        }
        if (params.externo == "on") {
            paramsTramite.externo = 1
        } else {
            paramsTramite.externo = 0
        }

        if (paramsTramite.externo == '1' || paramsTramite.externo == 1) {
            paramsTramite.estadoTramiteExterno = EstadoTramiteExterno.findByCodigo("EX03") //pendiente
        }

        if (params.paraExt) {
            paramsTramite.paraExterno = params.paraExt
        } else {
            paramsTramite.paraExterno = null
        }
        if (params.paraExt2) {
            paramsTramite.paraExterno = params.paraExt2
        }

        paramsTramite.de = persona
        paramsTramite.deDepartamento = persona.departamento
        paramsTramite.deDepartamento.id = persona.departamento.id
        paramsTramite.estadoTramite = estadoTramiteBorrador
        if (paramsTramite.id) {
            paramsTramite.fechaModificacion = new Date()
        } else {
            paramsTramite.fechaCreacion = new Date()
            paramsTramite.anio = Anio.findByNumero(paramsTramite.fechaCreacion.format("yyyy"))
            /* CODIGO DEL TRAMITE:
         *      tipoDoc.codigo-secuencial-dtpoEnvia.codigo-anio(yy)
         *      INF-1-DGCP-14       MEM-10-CEV-13
         */
            //el numero del ultimo tramite del anio, por tipo doc y dpto
//        def num = Tramite.withCriteria {
//            eq("anio", paramsTramite.anio)
//            eq("tipoDocumento", TipoDocumento.get(paramsTramite.tipoDocumento.id))
//            de {
//                eq("departamento", persona.departamento)
//            }
//            projections {
//                max "numero"
//            }
//        }
//        if (num && num.size() > 0) {
//            num = num.first()
//        } else {
//            num = 0
//        }
//        if (!num) {
//            num = 0
//        }
//        num = num + 1
//        paramsTramite.numero = num
//        paramsTramite.codigo = TipoDocumento.get(paramsTramite.tipoDocumento.id).codigo + "-" + num + "-" + persona.departamento.codigo + "-" + paramsTramite.anio.numero[2..3]

            def num = 1
            Numero objNum
            def numero = Numero.withCriteria {
                eq("departamento", persona.departamento)
                eq("tipoDocumento", TipoDocumento.get(paramsTramite.tipoDocumento.id))
                order("valor", "desc")
            }
            if (numero.size() == 0) {
                objNum = new Numero([
                        departamento : persona.departamento,
                        tipoDocumento: TipoDocumento.get(paramsTramite.tipoDocumento.id)
                ])
            } else {
                objNum = numero.first()
                num = objNum.valor + 1
            }
            objNum.valor = num
            if (!objNum.save(flush: true)) {
                println "Error al crear Numero: " + objNum.errors
            }
            paramsTramite.numero = num
            paramsTramite.codigo = TipoDocumento.get(paramsTramite.tipoDocumento.id).codigo + "-" + num + "-" + persona.departamento.codigo + "-" + paramsTramite.anio.numero[2..3]
        }
        def tramite
        def error = false
        if (paramsTramite.id) {
            tramite = Tramite.get(paramsTramite.id)
            if (tramite.padre && tramite.padre.tipoTramite.codigo == "C") {
                tramite.tipoTramite = TipoTramite.findByCodigo("C")
            }
        } else {
            tramite = new Tramite()
        }
        println "ANTES DEL SAVE " + paramsTramite

        tramite.properties = paramsTramite

        if (!tramite.save(flush: true)) {
            println "error save tramite " + tramite.errors
            flash.tipo = "error"
            flash.message = "Ha ocurrido un error al grabar el tramite, por favor, verifique la información ingresada"
            redirect(controller: "tramite2", action: "crearTramiteDep", id: tramite.id)
            return
        } else {

            if (tramite.padre) {
                tramite.padre.estado = "C"
                tramite.aQuienContesta = PersonaDocumentoTramite.get(paramsTramite.aQuienContesta.id)
                tramite.padre.save(flush: true)
                if (tramite.padre.estadoTramiteExterno) {
                    tramite.estadoTramiteExterno = tramite.padre.estadoTramiteExterno

                }
                tramite.save(flush: true)
            }

            /*
             * para/cc: si es negativo el id > es a la bandeja de entrada del departamento
             *          si es positivo es una persona
             */
            if (paramsTramite.para || tramite.tipoDocumento.codigo == "OFI") {
                def rolPara = RolPersonaTramite.findByCodigo('R001')
                def para
                if (paramsTramite.para) {
                    para = paramsTramite.para.toInteger()
                } else {
                    para = session.usuario.departamento.id.toInteger() * -1
                }
                def paraDocumentoTramite = PersonaDocumentoTramite.withCriteria {
                    eq("tramite", tramite)
                    eq("rolPersonaTramite", rolPara)
                }
                if (paraDocumentoTramite.size() == 0) {
                    paraDocumentoTramite = new PersonaDocumentoTramite()
                    paraDocumentoTramite.tramite = tramite //******
                    paraDocumentoTramite.rolPersonaTramite = rolPara
                } else if (paraDocumentoTramite.size() == 1) {
                    paraDocumentoTramite = paraDocumentoTramite.first()
                } else {
                    paraDocumentoTramite.each {
                        it.delete(flush: true)
                    }
                    paraDocumentoTramite = new PersonaDocumentoTramite()
                    paraDocumentoTramite.tramite = tramite //*****
                    paraDocumentoTramite.rolPersonaTramite = rolPara
                }
                if (para > 0) {
                    //persona
                    paraDocumentoTramite.persona = Persona.get(para)
                    paraDocumentoTramite.departamento = null
                } else {
                    //departamento
                    paraDocumentoTramite.persona = null
                    paraDocumentoTramite.departamento = Departamento.get(para * -1)
                }
                if (!paraDocumentoTramite.save(flush: true)) {
                    println "error para: " + paraDocumentoTramite.errors
                }
            } else {
                def paraOld = PersonaDocumentoTramite.withCriteria {
                    eq("tramite", tramite)
                    eq("rolPersonaTramite", RolPersonaTramite.findByCodigo('R001'))
                }
                if (paraOld.size() > 0) {
                    println "Habian ${paraOld.size()} paras que fueron borrados"
                    paraOld.each {
                        it.delete(flush: true)
                    }
                }
            }
            def rolCc = RolPersonaTramite.findByCodigo('R002')

            PersonaDocumentoTramite.withCriteria {
                eq("tramite", tramite)
                eq("rolPersonaTramite", rolCc)
            }.each {
                it.delete(flush: true)
            }
            if (paramsTramite.hiddenCC.toString().size() > 0) {
                (paramsTramite.hiddenCC.split("_")).each { cc ->
                    def ccDocumentoTramite = new PersonaDocumentoTramite()
                    ccDocumentoTramite.tramite = tramite
                    ccDocumentoTramite.rolPersonaTramite = rolCc
                    if (cc.toInteger() > 0) {
                        //persona
                        ccDocumentoTramite.persona = Persona.get(cc.toInteger())
                    } else {
                        //departamento
                        ccDocumentoTramite.departamento = Departamento.get(cc.toInteger() * -1)
                    }
                    if (!ccDocumentoTramite.save(flush: true)) {
                        println "error cc: " + ccDocumentoTramite.errors
                    }
                }
            }
//            if (params.cc == "on") {
            def tipoDoc
            if (paramsTramite.id) {
                tipoDoc = tramite.tipoDocumento
            } else {
                tipoDoc = TipoDocumento.get(paramsTramite.tipoDocumento.id)
            }
            if (tipoDoc.codigo == "DEX") {
//                paramsOrigen.tramite = tramite
//                paramsOrigen.fecha = new Date()
//                def origen = OrigenTramite.findAllByCedula(paramsOrigen.cedula)
//                if (origen.size() == 0) {
//                    origen = new OrigenTramite(paramsOrigen)
//                } else {
//                    println "Hay ${origen.size()} filas de origenTramite con cedula ${paramsOrigen.cedula}"
//                    origen = origen.first()
//                    origen.properties = paramsOrigen
//                }
//                if (!origen.save(flush: true)) {
//                    println "error origen tramite: " + origen.errors
//                }
//                tramite.origenTramite = origen
//                if (!tramite.save(flush: true)) {
//                    println "ERROR AAAAA: " + tramite.errors
//                }
            } else {
                if (tipoDoc.codigo != "OFI") {
//                    def origen = OrigenTramite.findAllByCedula(paramsOrigen.cedula)
//                    if (origen.size() > 0) {
//                        println "Hay ${origen.size()} filas de origenTramite con cedula ${paramsOrigen.cedula} que se eliminan"
//                        origen.each {
//                            it.delete(flush: true)
//                        }
//                    }
                }
            }
        }
        if (tramite.tipoDocumento.codigo == "SUM") {
            redirect(controller: "tramite2", action: "bandejaSalidaDep", id: tramite.id)
        } else {
            if (params.anexo == "on") {
                redirect(controller: "documentoTramite", action: "anexo", id: tramite.id)
            } else {
                redirect(controller: "tramite", action: "redactar", id: tramite.id)
            }
        }
    }

    //asignar permiso imprimir

    def permisoImprimir() {

//        println("params" + params)

        def persona = Persona.get(params.persona)
        def tramite = Tramite.get(params.id)
        def personaDoc = new PersonaDocumentoTramite();
        def rol = RolPersonaTramite.findByCodigo('I005')

        personaDoc.tramite = tramite
        personaDoc.persona = persona
        personaDoc.observaciones = params.observaciones
        personaDoc.rolPersonaTramite = rol
        personaDoc.fechaEnvio = new Date()

        if (!personaDoc.save(flush: true)) {

            render "Ocurrió un error al otorgar el permiso"
        } else {

            render "Permiso de impresión otorgado correctamente"
        }

//        return render

    }


    def busquedaBandejaSalidaDep() {

        def persona = Persona.get(session.usuario.id)
        def tramites = []
//        def estados = EstadoTramite.findAllByCodigoInList(["E001", "E002", "E003", "E004"])

        def porEnviar = EstadoTramite.findByCodigo("E001")
        def revisado = EstadoTramite.findByCodigo("E002")
        def enviado = EstadoTramite.findByCodigo("E003")
        def recibido = EstadoTramite.findByCodigo("E004")

        def para = RolPersonaTramite.findByCodigo("R001")
        def cc = RolPersonaTramite.findByCodigo("R002")
//        tramites = Tramite.findAllByDeDepartamentoAndEstadoTramiteInList(persona.departamento, estados, [sort: "fechaCreacion", order: "desc"])

        def trams = Tramite.withCriteria {
            eq("deDepartamento", persona.departamento)
            inList("estadoTramite", [porEnviar, revisado, enviado, recibido])
            order("fechaCreacion", "desc")
        }
//        println "tramites "+trams.codigo
        trams.each { tr ->
            def pxd = PersonaDocumentoTramite.withCriteria {
                eq("tramite", tr)
                inList("rolPersonaTramite", [para, cc])
                isNull("fechaRecepcion")
            }
            if (pxd.size() > 0) {
                tramites += tr
            }
        }

//busqueda

        if (params.fecha) {
            params.fechaIni = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 00:00:00")
            params.fechaFin = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha + " 23:59:59")
        }

        def res = PersonaDocumentoTramite.withCriteria {

            if (params.fecha) {
                gt('fechaEnvio', params.fechaIni)
                lt('fechaEnvio', params.fechaFin)
            }

            tramite {
                if (params.asunto) {
                    ilike('asunto', '%' + params.asunto + '%')
                }
                if (params.memorando) {
                    ilike('codigo', '%' + params.memorando + '%')
                }
            }
        }
//
//        println("->" + res.tramite.unique())
//        println("pxt" + tramites)


        return [tramites: res.tramite.unique(), pxtTramites: tramites]


    }


}
