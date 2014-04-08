package happy.tramites

import groovy.time.TimeCategory
import happy.seguridad.Persona

class Tramite2Controller extends happy.seguridad.Shield {

    def diasLaborablesService

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
            flash.tipo="error"

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
        def estados = EstadoTramite.findAllByCodigoInList(["E001", "E002", "E003"])
        tramites = Tramite.findAllByDeDepartamentoAndEstadoTramiteInList(persona.departamento, estados, [sort: "fechaCreacion", order: "desc"])

        return [persona: persona, tramites: tramites]
    }

    def bandejaSalida() {

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

    //alertas

    def enviar() {
        println "method "+request.getMethod()
        println "PARAMS "+params
        /*todo sin validacion alguna... que envie no mas cualquiera*/
        if (request.getMethod() == "POST") {
            println "\t1"
            def msg = ""
            def tramite = Tramite.get(params.id)
            def envio = new Date()

            PersonaDocumentoTramite.findAllByTramite(tramite).each { t ->
                t.fechaEnvio = envio
                t.save(flush: true)
            }
            println "\t2"
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
                println "\t3"
                render "ok"
            } else {
                println "\t4"
                println tramite.errors
                render "no: "+renderErrors(bean:tramite)
            }
        } else {
            println "\t5"
//            response.sendError(403)
            render "403"
        }
        println "\t6"
        render "nadad"
    }


    def busquedaBandejaSalida() {


        if (params.fecha) {
            params.fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        }

        def res = Tramite.withCriteria {

            if (params.fecha) {
                eq('fechaIngreso', params.fecha)
            }
            if (params.asunto) {
                ilike('asunto', '%' + params.asunto + '%')
            }
            if (params.memorando) {

                ilike('numero', '%' + params.memorando + '%')

            }
        }

        return [tramites: res]


    }

    def verRezagados() {
        def dep = session.departamento
        def tramites = []
//        def ahora = new Date().plus(2)
        def ahora = new Date()
        PersonaDocumentoTramite.findAllByFechaEnvioIsNotNullAndFechaRecepcionIsNull().each { pdt ->
            if (pdt.tramite.de.departamento.id == dep.id)
                println "pdt --> " + pdt.id + " tramite " + pdt.tramite.id + " - ${pdt.tramite.de.departamento.descripcion} " + pdt.fechaEnvio + "  " + pdt.departamento + "   " + pdt.persona
            println "fecha bloqueo " + pdt.tramite.fechaBloqueo
            def fechaBloqueo = pdt.tramite.fechaBloqueo
            if (fechaBloqueo && fechaBloqueo < ahora) {
                println "add tramites " + pdt
                tramites.add(pdt)
            }
        }
        [tramites: tramites]
    }


    def crearTramiteDep() {
//        println("params " + params)
        def padre = null
        def tramite = new Tramite(params)
        if (params.padre) {
            padre = Tramite.get(params.padre)
        }
        if (params.id) {
            tramite = Tramite.get(params.id)
            padre = tramite.padre
        } else {
            tramite.fechaCreacion = new Date()
        }

        def persona = Persona.get(session.usuario.id)

        def de = session.usuario
        def disp, disponibles = []

        if (persona.puedeTramitar) {
            disp = Departamento.list([sort: 'descripcion'])
        } else {
            disp = [persona.departamento]
        }
        disp.each { dep ->
            disponibles.add([id: dep.id * -1, label: dep.descripcion, obj: dep])
            if (dep.id == persona.departamentoId) {
                def users = Persona.findAllByDepartamento(dep)
                for (int i = users.size() - 1; i > -1; i--) {
                    if (!(users[i].estaActivo && users[i].puedeRecibir)) {
                        users.remove(i)
                    } else {
                        disponibles.add([id: users[i].id, label: users[i].toString(), obj: users[i]])
                    }
                }
            }
        }
        return [de: de, padre: padre, disponibles: disponibles, tramite: tramite]
    }
/*
        paramsTramite.deDepartamento = persona.departamento
        paramsTramite.deDepartamento.id = persona.departamento.id
 */

    def saveDep() {

        /*
            [
                tramite:[
                    padre.id:,
                    padre:[id:],
                    id:,
                    hiddenCC:,
                    origenTramite.id:1,
                    origenTramite:[id:1],
                    tipoDocumento.id:2,
                    tipoDocumento:[id:2],
                    prioridad.id:3,
                    prioridad:[id:3],
                    asunto:test oficio con anexos
                ],
                origen:[
                    tipoPersona.id:2,
                    tipoPersona:[id:2],
                    cedula:,
                    nombre:,
                    nombreContacto:,
                    apellidoContacto:,
                    titulo:,
                    cargo:,
                    mail:,
                    telefono:
                ],
                anexo:on,
                confi:on,
                action:save,
                format:null,
                controller:tramite3
            ]
         */

        def persona = Persona.get(session.usuario.id)
        def estadoTramiteBorrador = EstadoTramite.findByCodigo("E001");

        def paramsOrigen = params.remove("origen")
        def paramsTramite = params.remove("tramite")

        def tipoTramite
        if (params.confi == "on") {
            tipoTramite = TipoTramite.findByCodigo("C")
        } else {
            tipoTramite = TipoTramite.findByCodigo("C")
        }
        paramsTramite.tipoTramite = tipoTramite
        if (params.anexo == "on") {
            paramsTramite.anexo = 1
        } else {
            paramsTramite.anexo = 0
        }

        paramsTramite.de = persona
        paramsTramite.deDepartamento = persona.departamento
        paramsTramite.deDepartamento.id = persona.departamento.id
        paramsTramite.estadoTramite = estadoTramiteBorrador
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
                    departamento: persona.departamento,
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

        def tramite
        def error = false
        if (paramsTramite.id) {
            tramite = Tramite.get(paramsTramite.id)
        } else {
            tramite = new Tramite()
        }
        tramite.properties = paramsTramite

        if (!tramite.save(flush: true)) {
            println "error save tramite " + tramite.errors
            flash.tipo = "error"
            flash.message = "Ha ocurrido un error al grabar el tramite, por favor, verifique la información ingresada"
            redirect(action: "crearTramite")
            return
        } else {

            /*
             * para/cc: si es negativo el id > es a la bandeja de entrada del departamento
             *          si es positivo es una persona
             */
            if (paramsTramite.para) {
                def para = paramsTramite.para.toInteger()
                def paraDocumentoTramite = new PersonaDocumentoTramite([
                        tramite: tramite,
                        rolPersonaTramite: RolPersonaTramite.findByCodigo('R001')
                ])
                if (para > 0) {
                    //persona
                    paraDocumentoTramite.persona = Persona.get(para)
                } else {
                    //departamento
                    paraDocumentoTramite.departamento = Departamento.get(para * -1)
                }
                if (!paraDocumentoTramite.save(flush: true)) {
                    println "error para: " + paraDocumentoTramite.errors
                }
            }
            if (paramsTramite.hiddenCC.toString().size() > 0) {
                (paramsTramite.hiddenCC.split("_")).each { cc ->
                    def ccDocumentoTramite = new PersonaDocumentoTramite([
                            tramite: tramite,
                            rolPersonaTramite: RolPersonaTramite.findByCodigo('R002')
                    ])
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
            def tipoDoc = TipoDocumento.get(paramsTramite.tipoDocumento.id)
            if (tipoDoc.codigo == "DEX") {
                paramsOrigen.tramite = tramite
                paramsOrigen.fecha = paramsTramite.fechaCreacion
                def origen = new OrigenTramite(paramsOrigen)
                if (!origen.save(flush: true)) {
                    println "error origen tramite: " + origen.errors
                }
            }
        }
        if (params.anexo == "on") {
            redirect(controller: "tramiteAnexos", action: "anexo", id: tramite.id)
        } else {
            redirect(controller: "tramite", action: "redactar", id: tramite.id)
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



}
