package happy.tramites

import groovy.json.JsonBuilder
import groovy.time.TimeCategory
import happy.seguridad.Persona


class TramiteController extends happy.seguridad.Shield {

//    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def redactar() {
        def tramite = Tramite.get(params.id)
        return [tramite: tramite]
    }

    def saveTramite() {
        /*
         ['editorTramite':'<p>s asdf asdfasd asdf</p>\n', 'tramite':'4', 'action':'saveTramite', 'format':null, 'controller':'tramiteImagenes']
         */
        def tramite = Tramite.get(params.id)
        tramite.texto = params.editorTramite
        tramite.fechaModificacion = new Date()
        if (tramite.save(flush: true)) {
            render "OK_Trámite guardado exitosamente"
        } else {
            render "NO_Ha ocurrido un error al guardar el trámite: " + renderErrors(bean: tramite)
        }
    }

    def crearTramite() {
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

    def cargaUsuarios() {
        def dir = Departamento.get(params.dir)
        def users = Persona.findAllByDepartamento(dir)
        for (int i = users.size() - 1; i > -1; i--) {
            if (!(users[i].estaActivo && users[i].puedeRecibir)) {
                users.remove(i)
            }
        }
        return [users: users]
    }


    def save_bck() {
        /*todo comentar esto*/
        params.fechaLimiteRespuesta_hour = "13"
        params.fechaLimiteRespuesta_minutes = "26"
        println " save tramite " + params
        def estadoTramite = EstadoTramite.get(1)
        def tramite
        def error = false
        if (params.tramite.id) {
            tramite = Tramite.get(params.tramite.id)
        } else {
            tramite = new Tramite()
        }
        if (params.fechaLimiteRespuesta_day.size() == 1)
            params.fechaLimiteRespuesta_day = "0" + params.fechaLimiteRespuesta_day
        if (params.fechaLimiteRespuesta_month.size() == 1)
            params.fechaLimiteRespuesta_month = "0" + params.fechaLimiteRespuesta_month
        def fechaLimite = params.fechaLimiteRespuesta_day + "-" + params.fechaLimiteRespuesta_month + "-" + params.fechaLimiteRespuesta_year + " " + params.fechaLimiteRespuesta_hour + ":" + params.fechaLimiteRespuesta_minutes
        println "fecha limite " + fechaLimite
        fechaLimite = new Date().parse("dd-MM-yyyy HH:mm", fechaLimite)
        params.tramite.fechaLimiteRespuesta = fechaLimite
        /*Aqui falta generar el numero de tramite*/
        params.tramite.numero = "MEMPRUEBA-0001"
        tramite.properties = params.tramite
        tramite.estadoTramite = estadoTramite
        if (!tramite.save(flush: true)) {
            println "error save tramite " + tramite.errors
            flash.message = "Ha ocurrido un error al grabar el tramite, por favor, verifique la información ingresada"
            redirect(action: "crearTramite")
            return
        } else {
            def departamentos = []
            def parts = params.data.split("%")
            println "parts " + parts + " data " + params.data
            parts.each() { p ->
                if (p != "") {
                    def datos = p.split(";")
                    println "datos " + datos
                    def user = happy.seguridad.Persona.get(session.usuario.id)
                    /*persona que recibe ya sea para o copia*/
                    def prsn = happy.seguridad.Persona.get(datos[1])
                    def rol = RolPersonaTramite.get(datos[2])
                    if (user.departamento.id != prsn.departamento.id) {
                        println "necesita puerta de entrada y saldia "
                        if (!departamentos.contains(prsn.departamento.id)) {
                            departamentos.add(prsn.departamento.id)
                            println "insert puerta para  " + prsn.departamento.descripcion + "  " + prsn.departamento.id
                            /*crea registros para puerta de salida*/
                            def salida
                            def permisoEnv = PermisoTramite.findByCodigo("E002")
                            def rolSalida = RolPersonaTramite.findByCodigo("E004")
                            def entrada
                            def permisoRec = PermisoTramite.findByCodigo("E001")
                            def rolIngreso = RolPersonaTramite.findByCodigo("E003")
                            println "permiso salida " + permisoEnv.id
                            println "permiso entrada " + permisoRec.id
                            /*Busco puertas de salida*/
                            happy.seguridad.Persona.findAllByDepartamento(user.departamento).each { pr ->
                                println "buscando " + pr.id
                                def usu = PermisoUsuario.findByPersonaAndPermisoTramite(pr, permisoEnv)
                                if (usu)
                                    salida = usu
                            }
                            println "usuario de salida " + salida?.persona?.nombre + " " + salida?.persona?.id
                            if (!salida) {
                                println "error no hay puerta de salida al departamento"
                                flash.message = "Ha ocurrido un error al procesar el tramite, el Departamento ${prsn.departamento.descripcion} no tiene asignado un usuario para el envio de documentos"
                                redirect(action: "crearTramite", id: tramite.id)
                                return
                            } else {
                                def des = new PersonaDocumentoTramite()
                                des.persona = salida.persona
                                des.tramite = tramite
                                des.permiso = "E"
                                des.rolPersonaTramite = rolSalida
                                if (!des.save(flush: true))
                                    println "error destinatario " + des.errors
                            }
                            /*crea registros para puertas de entrada al departamento*/
                            happy.seguridad.Persona.findAllByDepartamento(prsn.departamento).each { pr ->
                                def usu = PermisoUsuario.findByPersonaAndPermisoTramite(pr, permisoRec)
                                if (usu)
                                    entrada = usu
                            }
                            println "usuario de entrada " + entrada?.persona?.nombre + " " + entrada?.persona?.id
                            if (!entrada) {
                                println "error no hay puerta de entrada al departamento"
                                flash.message = "Ha ocurrido un error al procesar el tramite, el Departamento ${prsn.departamento.descripcion} no tiene asignado un usuario para la recepción de documentos"
                                redirect(action: "crearTramite", id: tramite.id)
                                return
                            } else {
                                def des = new PersonaDocumentoTramite()
                                des.persona = entrada.persona
                                des.tramite = tramite
                                des.permiso = "R"
                                des.rolPersonaTramite = rolIngreso
                                if (!des.save(flush: true))
                                    println "error destinatario " + des.errors
                            }
                        }
                    }
                    /*creo registros de para y copia*/
                    def des = new PersonaDocumentoTramite()
                    des.persona = prsn
                    des.tramite = tramite
                    des.permiso = "PO"
                    des.rolPersonaTramite = rol
                    println "creo recipiente " + des.persona.nombre + "  " + des.persona.id + "  " + des.rolPersonaTramite.descripcion
                    if (!des.save(flush: true))
                        println "error destinatario para o copia " + des.errors
                }
            }
        }
        /*Fechas en nulo.. eso se llena en enviar*/
        redirect(action: "redactarTramite", id: tramite.id)
    }

    def redactarTramite() {
        def tramite = Tramite.get(params.id)
        if (tramite.de.id.toInteger() != session.usuario.id.toInteger()) {
            response.sendError(403)
            return
        } else {
            [tramite: tramite]
        }
    }


    def randomDep() {
        return
        def rand = new Random()
        def numPer
        def permisos = PermisoTramite.findAllByCodigoNotLike("E%")
        def perRec = PermisoTramite.findByCodigo("E001")
        def perEnv = PermisoTramite.findByCodigo("E002")
        def maxPer = permisos.size() - 1
        def deps = []
        happy.seguridad.Persona.findAll("from Persona where id>4 order by id").each { per ->
            def dep = null
            def num
            while (dep == null) {
                num = rand.nextInt(13)
                dep = Departamento.get(num + 2)
            }
            println "prsn " + per.id + " dep " + dep.id + " num " + (num + 2) + " " + per
            per.departamento = dep
            if (per.cedula == "1111111111")
                per.cedula = "22" + rand.nextInt(9) + "3" + rand.nextInt(9) + rand.nextInt(9) + rand.nextInt(9) + rand.nextInt(9) + rand.nextInt(9) + rand.nextInt(9)
            if (!per.save(flush: true))
                println "error " + per.errors
            println "despues " + per.departamento.id + " " + per.cedula
            if (!deps.contains(dep)) {
                deps.add(dep)
                def pr = new PermisoUsuario()
                pr.persona = per
                pr.fechaInicio = new Date()
                pr.permisoTramite = perRec
                if (!pr.save())
                    println "error save perm " + pr.errors
                pr = new PermisoUsuario()
                pr.persona = per
                pr.fechaInicio = new Date()
                pr.permisoTramite = perEnv
                if (!pr.save())
                    println "error save perm " + pr.errors
            } else {
                numPer = rand.nextInt(5)
                numPer.times { t ->
                    def pr = new PermisoUsuario()
                    pr.persona = per
                    pr.fechaInicio = new Date()
                    num = rand.nextInt(maxPer)
                    pr.permisoTramite = permisos[num]
                    if (!pr.save())
                        println "error save perm " + pr.errors
                }
            }

        }

    }

    //ALERTAS BANDEJA ENTRADA

    def alertRecibidos() {

        def usuario = session.usuario

        def persona = Persona.get(usuario.id)

        def recibidos = EstadoTramite.get(4)

//        def pxt = PersonaDocumentoTramite.findAllByPersona(persona)

        //------------------------------------------------

        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def rolImprimir = RolPersonaTramite.findByCodigo('I005')

        def pxtTodos = []
        def pxtTramites = []

        def pxtPara = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolPara)
        def pxtCopia = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolCopia)
        def pxtImprimir = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona,rolImprimir)

        pxtTodos = pxtPara
        pxtTodos += pxtCopia
        pxtTodos += pxtImprimir

        pxtTodos.each {
            if(it?.tramite?.estadoTramite?.codigo == 'E003' || it?.tramite?.estadoTramite?.codigo == 'E004'){
                pxtTramites.add(it)
            }
        }
        //------------------------------------------------

        def tramites = []

        pxtTramites.each {
            if (it.tramite.estadoTramite == recibidos) {
                tramites.add(it.tramite)
            }
        }

        def fechaEnvio
        def prioridad

        def hora = 3600000  //milisegundos

        def totalPrioridad = 0
        def fecha

        Date nuevaFecha

        def tramitesRecibidos = 0

        def idTramites = []

        tramites.each {

//            println("tramite" + it)
////
//            println("--->>" + it?.getFechaMaximoRespuesta())

            fechaEnvio = it.fechaEnvio

            prioridad = TipoPrioridad.get(it?.prioridad?.id).tiempo

            totalPrioridad = hora * prioridad

            fecha = fechaEnvio.getTime()

            nuevaFecha = new Date(fecha + totalPrioridad)

//            nuevaFecha = it?.getfechaMaximoRespuesta

            if (!nuevaFecha.before(new Date())) {

                tramitesRecibidos++
                idTramites.add(it.id)
            }


        }

        return [tramitesRecibidos: tramitesRecibidos, idTramites: idTramites]

    }

    def alertaPendientes() {

        def usuario = session.usuario

        def persona = Persona.get(usuario.id)

        def pendientes = EstadoTramite.get(8)

        def tramitesPendientes = 0
        def totalPendientes = []
//        def pxt = PersonaDocumentoTramite.findAllByPersona(persona)

        //------------------------------------------------
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def rolImprimir = RolPersonaTramite.findByCodigo('I005')

        def pxtTodos = []
        def pxtTramites = []

        def pxtPara = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolPara)
        def pxtCopia = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolCopia)
        def pxtImprimir = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona,rolImprimir)

        pxtTodos = pxtPara
        pxtTodos += pxtCopia
        pxtTodos += pxtImprimir

        pxtTodos.each {
            if(it?.tramite?.estadoTramite?.codigo == 'E003' || it?.tramite?.estadoTramite?.codigo == "E004"){
                pxtTramites.add(it)
            }
        }


        //------------------------------------------------


        pxtTramites.each {
            if (it.tramite.estadoTramite == pendientes) {
                totalPendientes.add(it.tramite)
            }
        }

        tramitesPendientes = totalPendientes.size()

        def dosHoras = 6200000

        def fechaEnvio
        def fecha
        def fechaRoja

        def tramitesPendientesRojos = 0
        def idRojos = []

        totalPendientes.each {

//            println("__<<<<____" +  it?.getFechaLimite)

            if (it.fechaEnvio) {
                println("fecha envio" + it.fechaEnvio)
                fechaEnvio = it.fechaEnvio
                fecha = fechaEnvio.getTime()
                fechaRoja = new Date(fecha + dosHoras)
//                fechaRoja = it?.getFechaLimite


                if (fechaRoja.before(new Date())) {
                    tramitesPendientesRojos++
                    idRojos.add(it.id)
                }
            }
        }

        return [tramitesPendientesRojos: tramitesPendientesRojos, tramitesPendientes: tramitesPendientes, idRojos: idRojos]

    }


    def rojoPendiente() {


        def usuario = session.usuario

        def persona = Persona.get(usuario.id)

        def pendientes = EstadoTramite.get(8)

        def tramitesPendientes = 0
        def totalPendientes = []
//        def pxt = PersonaDocumentoTramite.findAllByPersona(persona)

        //------------------------------------------------

        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def rolImprimir = RolPersonaTramite.findByCodigo('I005')

        def pxtTodos = []
        def pxtTramites = []

        def pxtPara = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolPara)
        def pxtCopia = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolCopia)
        def pxtImprimir = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona,rolImprimir)

        pxtTodos = pxtPara
        pxtTodos += pxtCopia
        pxtTodos += pxtImprimir

        pxtTodos.each {
            if(it?.tramite?.estadoTramite?.codigo == 'E003' || it?.tramite?.estadoTramite?.codigo == "E004"){
                pxtTramites.add(it)
            }
        }

        //------------------------------------------------


        pxtTramites.each {
            if (it.tramite.estadoTramite == pendientes) {
                totalPendientes.add(it.tramite)
            }
        }
        tramitesPendientes = totalPendientes.size()
        return [tramitesPendientes: tramitesPendientes]
    }


    def alertaRetrasados() {


        def usuario = session.usuario

        def persona = Persona.get(usuario.id)

//        def pxt = PersonaDocumentoTramite.findAllByPersona(persona)


        //------------------------------------------------
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def rolImprimir = RolPersonaTramite.findByCodigo('I005')

        def pxtTodos = []
        def pxtTramites = []

        def pxtPara = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolPara)
        def pxtCopia = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolCopia)
        def pxtImprimir = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona,rolImprimir)

        pxtTodos = pxtPara
        pxtTodos += pxtCopia
        pxtTodos += pxtImprimir

        pxtTodos.each {
            if(it?.tramite?.estadoTramite?.codigo == 'E003' || it?.tramite?.estadoTramite?.codigo == "E004"){
                pxtTramites.add(it)
            }
        }


        //------------------------------------------------

        def recibidos = EstadoTramite.get(4)

        def tramitesRetrasados = []

        pxtTramites.each {
            if (it.tramite.estadoTramite == recibidos) {
//                tramitesRetrasados.add(it.tramite)
                tramitesRetrasados += it.tramite

            }
        }

        def fechaEnvio
        def prioridad

        def hora = 3600000  //milisegundos

        def totalPrioridad = 0
        def fecha

        Date nuevaFecha

        def tramitesAtrasados = 0

        def idTramites = []

        def para

        tramitesRetrasados.each {

            fechaEnvio = it.fechaEnvio

            prioridad = TipoPrioridad.get(it?.prioridad?.id).tiempo

            totalPrioridad = hora * prioridad

            fecha = fechaEnvio.getTime()

            nuevaFecha = new Date(fecha + totalPrioridad)

            if (nuevaFecha.before(new Date())) {

                tramitesAtrasados++
                idTramites.add(it.id)

            }
        }

        return [tramitesAtrasados: tramitesAtrasados, idTramites: idTramites]
    }

    //fin alertas bandeja entrada

    def bandejaSalidaDepartamento (){
        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        return [persona: persona]
    }

    def tablaBandejaSalidaDepartamento () {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def departamento = persona?.departamento
        def estados = EstadoTramite.findAllByCodigoInList(["E001","E002","E003"])
        def personasDepartamento = Persona.findAllByDepartamento(departamento)
//        def tramites = Tramite.findAllByDeAndEstadoTramiteInList(persona,estados,[sort:"fechaCreacion",order:"desc"])
        def tramites = Tramite.findAllByDeInListAndEstadoTramiteInList(personasDepartamento,estados,[sort:"fechaCreacion",order:"desc"])

//        println("tramites salida departamento" + tramites)
        return [persona: persona, tramites: tramites]
    }


    //BANDEJA PERSONAL


    def bandejaEntrada() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        return [persona: persona]

    }


    def tablaBandeja() {

        def idTramitesRetrasados = alertaRetrasados().idTramites
        def idTramitesRecibidos = alertRecibidos().idTramites
        def idRojos = alertaPendientes().idRojos

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def rolImprimir = RolPersonaTramite.findByCodigo('I005')

//        def estadoEnviado = EstadoTramite.findByCodigo('E003')

        def pxtTodos = []
        def pxtTramites = []

        def pxtPara = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolPara)
        def pxtCopia = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolCopia)
        def pxtImprimir = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona,rolImprimir)


        pxtTodos = pxtPara
        pxtTodos += pxtCopia
        pxtTodos += pxtImprimir

        pxtTodos.each {
            if(it?.tramite?.estadoTramite?.codigo == 'E003' || it?.tramite?.estadoTramite?.codigo == 'E004'){
                pxtTramites.add(it)
            }
        }
//        println("todos:" + pxtTramites)

        def tramitesRecibidos = pxtTramites.size()


        return [tramites: pxtTramites, idTramitesRetrasados: idTramitesRetrasados, idTramitesRecibidos: idTramitesRecibidos, idRojos: idRojos]
    }


    //alertas

    def alertaRevisados() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def revisados = EstadoTramite.get(2)
        def tramitesRevisados = []
        def tramites = []
        def pxt = PersonaDocumentoTramite.findAllByPersona(persona)
        pxt.each {
            if (it?.tramite?.de?.id == usuario?.id) {
                tramites.add(it.tramite)
            }
        }

        tramites.each {
            if (it.estadoTramite == revisados) {
                tramitesRevisados.add(it)
            }
        }

        return [tramites: tramitesRevisados.size()]
    }


    def alertaEnviados() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def enviados = EstadoTramite.get(3)
        def tramites = []
        def tramitesEnviados = []
        def idTramitesEnviados = []
        def cantidadEnviados = 0
        def pxt = PersonaDocumentoTramite.findAllByPersona(persona)
        pxt.each {
            if (it?.tramite?.de?.id == usuario?.id) {
                tramites.add(it.tramite)
            }
        }
        tramites.each {
            if (it?.estadoTramite == enviados) {
                tramitesEnviados.add(it)
            }
        }

        def fechaEnvio
        def dosHoras = 7200000  //milisegundos
        def fecha
        Date nuevaFecha

        tramitesEnviados.each {
            println("fecha:" + it?.getFechaLimite())
            fechaEnvio = it?.fechaEnvio
            fecha = fechaEnvio.getTime()
            nuevaFecha = new Date(fecha + dosHoras)
            if (!nuevaFecha.before(new Date())) {
                cantidadEnviados++
                idTramitesEnviados.add(it.id)
            }
        }

        return [tramites: cantidadEnviados, idTramitesEnviados: idTramitesEnviados]
    }


    def alertaNoRecibidos() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def enviados = EstadoTramite.get(3)
        def listaNoRecibidos = []
        def tramites = []

        def pxt = PersonaDocumentoTramite.findAllByPersona(persona)
        pxt.each {
            if (it?.tramite?.de?.id == usuario?.id) {
                listaNoRecibidos.add(it?.tramite)
            }
        }
        listaNoRecibidos.each {

            if (it?.estadoTramite == enviados) {
                tramites.add(it)
            }
        }

        def fechaEnvio
        def dosHoras = 7200000  //milisegundos
        def ch = 172800000

        def fecha
        Date nuevaFecha
        Date fechaLimite

        def tramitesNoRecibidos = 0
        def idTramitesNoRecibidos = []

        def tramitesPasados = 0

        tramites.each {
            fechaEnvio = it?.fechaEnvio
            fecha = fechaEnvio.getTime()
            nuevaFecha = new Date(fecha + dosHoras)
            fechaLimite = new Date(fecha + ch)
            if (nuevaFecha.before(new Date())) {
                tramitesNoRecibidos++
                idTramitesNoRecibidos.add(it.id)
            }
            if (fechaLimite.before(new Date())) {

                tramitesPasados++
            }
        }

        return [tramitesNoRecibidos: tramitesNoRecibidos, idTramitesNoRecibidos: idTramitesNoRecibidos, tramitesPasados: tramitesPasados]
    }


    def observaciones() {

        def tramite = Tramite.get(params.id)

        return [tramite: tramite]

    }


    def guardarObservacion() {

        def tramite = Tramite.get(params.id)
        tramite.observaciones = params.texto

        if (!tramite.save(flush: true)) {
            render "Ocurrió un error al guardar"
        } else {
            render "Observación guardada correctamente"
        }

    }

  def observacionArchivado () {

    def tramite = Tramite.get(params.id)

    def observacion = ObservacionTramite.findByTramite(tramite)

     return [tramite: tramite, observacion: observacion]

  }




    def recibir () {

        def tramite = Tramite.get(params.id)
        return [tramite: tramite]


    }

    def guardarRecibir () {

        def persona = session.usuario


        def tramite = Tramite.get(params.id)
        def para = tramite.getPara().persona

        def estado = EstadoTramite.get(4)
        def pxt = PersonaDocumentoTramite.findByTramiteAndPersona(tramite,persona)

        if(persona.id == para.id){
            tramite.estadoTramite = estado
        }

        pxt.fechaRecepcion = new Date()

        tramite.save(flush: true)
        pxt.save(flush: true)

        if (!tramite.save(flush: true)) {
            render "Ocurrió un error al recibir"
        } else {
            render "Trámite recibido correctamente"
        }


    }






    def busquedaBandeja() {



        def idTramitesRetrasados = alertaRetrasados().idTramites
        def idTramitesRecibidos = alertRecibidos().idTramites
        def idRojos = alertaPendientes().idRojos

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def rolImprimir = RolPersonaTramite.findByCodigo('I005')

//        def estadoEnviado = EstadoTramite.findByCodigo('E003')

        def pxtTodos = []
        def pxtTramites = []

        def pxtPara = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolPara)
        def pxtCopia = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolCopia)
        def pxtImprimir = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona,rolImprimir)


        pxtTodos = pxtPara
        pxtTodos += pxtCopia
        pxtTodos += pxtImprimir

        pxtTodos.each {
            if(it?.tramite?.estadoTramite?.codigo == 'E003' || it?.tramite?.estadoTramite?.codigo == 'E004'){
                pxtTramites += it
            }
        }


        if (params.fecha) {
            params.fechaIni = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha+" 00:00:00")
            params.fechaFin = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha+" 23:59:59")
        }

//        println("params: " + params)


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

        return [tramites: res, pxtTramites: pxtTramites, idTramitesRetrasados: idTramitesRetrasados, idTramitesRecibidos: idTramitesRecibidos, idRojos: idRojos]

    }


    def archivados() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)

        return [persona: persona]


    }


    def tablaArchivados() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def rolImprimir = RolPersonaTramite.findByCodigo('I005')

//        def estadoEnviado = EstadoTramite.findByCodigo('E003')

        def pxtTodos = []
        def pxtTramites = []

        def pxtPara = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolPara)
        def pxtCopia = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolCopia)
        def pxtImprimir = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona,rolImprimir)


        pxtTodos = pxtPara
        pxtTodos += pxtCopia
        pxtTodos += pxtImprimir

        pxtTodos.each {
            if(it?.tramite?.estadoTramite?.codigo == 'E005' ){
                pxtTramites.add(it)
            }
        }




        return [tramites: pxtTramites]


    }

    def busquedaArchivados() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        def rolPara = RolPersonaTramite.findByCodigo('R001');
        def rolCopia = RolPersonaTramite.findByCodigo('R002');
        def rolImprimir = RolPersonaTramite.findByCodigo('I005')

//        def estadoEnviado = EstadoTramite.findByCodigo('E003')

        def pxtTodos = []
        def pxtTramites = []

        def pxtPara = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolPara)
        def pxtCopia = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona, rolCopia)
        def pxtImprimir = PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(persona,rolImprimir)


        pxtTodos = pxtPara
        pxtTodos += pxtCopia
        pxtTodos += pxtImprimir

        pxtTodos.each {
            if(it?.tramite?.estadoTramite?.codigo == 'E005'){
                pxtTramites += it
            }
        }


        if (params.fecha) {
            params.fechaIni = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha+" 00:00:00")
            params.fechaFin = new Date().parse("dd-MM-yyyy HH:mm:ss", params.fecha+" 23:59:59")
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

        return [tramites: res, pxtTramites: pxtTramites]

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


    def revisarHijos () {

//        println("params" + params)

        def tramite = Tramite.get(params.id)

        def hijos = Tramite.findAllByPadre(tramite)

        if(hijos.size() > 0){

            render "no"
        }else{

            render "ok"
        }


    }

    def archivar () {

        println("params" + params)

        def persona = Persona.get(session.usuario.id)
        def tramite = Tramite.get(params.id)
        def estadoTramite = EstadoTramite.get(5);

        tramite.estadoTramite=estadoTramite

        def observacion = new ObservacionTramite()

        observacion.persona = persona
        observacion.tramite = tramite
        observacion.fecha = new Date()
        observacion.observaciones = params.texto

        observacion.save(flush: true)


        if(!tramite.save(flush: true) || !observacion.save(flush: true)){
            render("Ocurrio un error al archivar el tramite")
        }else {
            render("Tramite archivado correctamente")
        }



    }

}
