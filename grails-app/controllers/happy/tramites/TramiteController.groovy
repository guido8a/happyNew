package happy.tramites

import happy.seguridad.Persona


class TramiteController extends happy.seguridad.Shield {

//    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def redactar() {

    }

    def crearTramite() {
//        println("params " + params)
        session.usuario = new happy.seguridad.Persona()
        session.usuario.nombre = "Juan"
        def padre = Tramite.get(params.id)
        def campos = ["codigo": ["Código", "string"], "nombre": ["Descripción", "string"]]
        def de = session.usuario
        def fecha = new Date()
        [de: de, fecha: fecha, campos: campos, padre: padre ]
    }

    def cargaUsuarios() {
        def dir = Departamento.get(params.dir)
        def users = happy.seguridad.Persona.findAllByDepartamento(dir)
//        println "users "+users
        for (int i = users.size() - 1; i > -1; i--) {
            println " (${users[i].id}) " + users[i].estaActivo() + "  " + users[i].puedeRecibir()
            if (!(users[i].estaActivo() && users[i].puedeRecibir())) {
                users.remove(i)
            }
        }
//        println "users 2 "+users
        render g.select(from: users, name: "usuario", id: "usuario", class: "many-to-one form-control required", optionKey: "id")
//        <g:select name="usuario" id="usuario" class="many-to-one form-control" from="" value="" ></g:select>
    }


    def save() {
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

def alertRecibidos () {

    def usuario = session.usuario

    def persona = Persona.get(usuario.id)

    def recibidos = EstadoTramite.get(4)

//    def tramitesRecibidos = Tramite.findAllByEstadoTramite(recibidos).size()

    def tramites = Tramite.findAllByEstadoTramite(recibidos)

    def fechaIngreso
    def prioridad

    def hora  = 3600000  //milisegundos

    def totalPrioridad = 0
    def fecha

    Date nuevaFecha

    def tramitesRecibidos = 0

    def idTramites = []

    tramites.each {

        fechaIngreso = it.fechaIngreso

        prioridad = TipoPrioridad.get(it.prioridad.id).tiempo

        totalPrioridad = hora*prioridad

        fecha = fechaIngreso.getTime()

        nuevaFecha = new Date(fecha + totalPrioridad)

        if(!nuevaFecha.before(new Date())){

            tramitesRecibidos++
            idTramites.add(it.id)

        }

    }



    return [tramitesRecibidos: tramitesRecibidos, idTramites: idTramites]

}

def alertaPendientes () {

    def usuario = session.usuario

    def persona = Persona.get(usuario.id)

    def pendientes = EstadoTramite.get(8)

    def tramitesPendientes = Tramite.findAllByEstadoTramite(pendientes).size()

    def totalPendientes = Tramite.findAllByEstadoTramite(pendientes)

    def dosHoras = 6200000

    def fechaEnvio
    def fecha
    def fechaRoja

    def tramitesPendientesRojos = 0
    def idRojos = []

    totalPendientes.each {

        if(it.fechaEnvio){
            fechaEnvio = it.fechaEnvio
            fecha = fechaEnvio.getTime()
            fechaRoja = new Date(fecha + dosHoras)


            if(fechaRoja.before(new Date())){

                tramitesPendientesRojos++
                idRojos.add(it.id)

            }

        }

    }

//    if(tramitesPendientesRojos > 0) {
//
//        return[tramitesPendientesRojos: tramitesPendientesRojos, idRojos: idRojos,redirect:true]
//
//    }else {
//
//        return [, redirect: false]
//
//    }

  return[tramitesPendientesRojos: tramitesPendientesRojos,tramitesPendientes : tramitesPendientes, idRojos: idRojos]


}


def alertaRetrasados () {

    def recibidos = EstadoTramite.get(4)

    def tramites = Tramite.findAllByEstadoTramite(recibidos)

    def fechaIngreso
    def prioridad

    def hora  = 3600000  //milisegundos

    def totalPrioridad = 0
    def fecha

    Date nuevaFecha

    def tramitesAtrasados = 0

    def idTramites = []

    tramites.each {

        fechaIngreso = it.fechaIngreso

        prioridad = TipoPrioridad.get(it.prioridad.id).tiempo

        totalPrioridad = hora*prioridad

        fecha = fechaIngreso.getTime()

        nuevaFecha = new Date(fecha + totalPrioridad)

        if(nuevaFecha.before(new Date())){

            tramitesAtrasados++
            idTramites.add(it.id)

        }

//        println("fecha:" + nuevaFecha.after(new Date()))
    }

//    println("-->" + tramitesAtrasados)


    return [tramitesAtrasados : tramitesAtrasados, idTramites: idTramites]
}



    def rojoPendiente () {


        def usuario = session.usuario

        def persona = Persona.get(usuario.id)

        def pendientes = EstadoTramite.get(8)

        def tramitesPendientes = Tramite.findAllByEstadoTramite(pendientes).size()


        return [tramitesPendientes : tramitesPendientes]

    }



    def bandejaEntrada() {

        def usuario = session.usuario

        def persona = Persona.get(usuario.id)

        def recibidos = EstadoTramite.get(4)

        def tramitesRecibidos = Tramite.findAllByEstadoTramite(recibidos).size()

        return [persona: persona, tramitesRecibidos: tramitesRecibidos]


    }





    def tablaBandeja() {
        def idTramitesRetrasados = alertaRetrasados().idTramites

        def idTramitesRecibidos = alertRecibidos().idTramites

        def idRojos = alertaPendientes().idRojos

        def tramites = Tramite.list()
        return [tramites: tramites, idTramitesRetrasados: idTramitesRetrasados, idTramitesRecibidos: idTramitesRecibidos, idRojos: idRojos]
    }


    def tablaBandejaSalida () {

        def estadoBorrador = EstadoTramite.get(1)
        def estadoRevisado = EstadoTramite.get(2)
        def estadoPendiente = EstadoTramite.get(8)

        def tramites = Tramite.findAllByEstadoTramiteOrEstadoTramiteOrEstadoTramite(estadoBorrador,estadoRevisado,estadoPendiente);

        return [tramites: tramites]

    }



    def bandejaSalida () {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)
        return[persona : persona]

    }




    def observaciones () {

        def tramite = Tramite.get(params.id)

        return [tramite: tramite]

    }


    def guardarObservacion () {

//        println("paramsObservaciones" + params)

        def tramite = Tramite.get(params.id)

//        println("tramite:" + tramite)

        tramite.observaciones = params.texto

        if(!tramite.save(flush: true)){

            render "Ocurrió un error al guardar"
        }else{

            render "Observación guardada correctamente"
        }

    }





    def busquedaBandeja() {
        if (params.fecha) {
            params.fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        }
//        println("params: " + params)

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


    def archivados() {

        def usuario = session.usuario
        def persona = Persona.get(usuario.id)

        return [persona: persona]


    }


    def tablaArchivados() {


        def archivados = EstadoTramite.get(5)

        def tramites = Tramite.findAllByEstadoTramite(archivados)



        return [tramites: tramites]


    }

    def busquedaArchivados() {


        if (params.fecha) {
            params.fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        }
//        println("params: " + params)

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


}
