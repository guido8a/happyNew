package happy.utilitarios

import groovy.json.JsonBuilder
import happy.tramites.Anio
import happy.tramites.Numero
import happy.tramites.Tramite
import org.springframework.dao.DataIntegrityViolationException

class DiaLaborableController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def diasLaborablesService

    def pruebas() {

        def fecha1 = new Date().parse("dd-MM-yyyy HH:mm", "02-07-2014 09:05")
        def fecha2 = new Date().parse("dd-MM-yyyy HH:mm", "02-07-2014 00:00")
        println diasLaborablesService.tiempoLaborableEntre(fecha1, fecha2)
//         fecha1 = new Date().parse("dd-MM-yyyy HH:mm", "22-07-2014 10:30")
//         fecha2 = new Date().parse("dd-MM-yyyy HH:mm", "24-07-2014 10:30")
//        println diasLaborablesService.tiempoLaborableEntre(fecha1, fecha2)

//        def fecha1 = new Date().parse("dd-MM-yyyy HH:mm", "22-07-2014 10:30")
//        def fecha2 = new Date().parse("dd-MM-yyyy HH:mm", "29-07-2014 14:15")
//
//        println diasLaborablesService.tiempoLaborableEntre(fecha1, fecha2)

//        fecha1 = new Date().parse("dd-MM-yyyy HH:mm", "22-07-2014 15:00")
//        fecha2 = new Date().parse("dd-MM-yyyy HH:mm", "23-07-2014 11:00")
//
//        println diasLaborablesService.tiempoLaborableEntre(fecha1, fecha2)

//        def tramite = Tramite.get(31)
//        tramite.fechaEnvio = new Date()
//        println tramite.fechaLimite
//        println tramite.fechaBloqueo

//        def fecha = new Date().parse("dd-MM-yyyy HH:mm", "31-03-2014 11:00")
//        println "fecha: "
//        println fecha
//        println "2 horas"
//        println diasLaborablesService.fechaMasTiempo(fecha.clone(), 2)
//        println "4 horas"
//        println diasLaborablesService.fechaMasTiempo(fecha.clone(), 4)
//        println "24 horas"
//        println diasLaborablesService.fechaMasTiempo(fecha.clone(), 24)
//        println "1 dia"
//        println diasLaborablesService.fechaMasDia(fecha.clone(), 1)
//        println "48 horas"
//        println diasLaborablesService.fechaMasTiempo(fecha.clone(), 48)
//        println "72 horas"
//        println diasLaborablesService.fechaMasTiempo(fecha.clone(), 72)
    }

    def calculador() {

    }

    def calcEntre() {
        def fecha1 = new Date().parse("dd-MM-yyyy", params.fecha1)
        def fecha2 = new Date().parse("dd-MM-yyyy", params.fecha2)

        def ret = diasLaborablesService.diasLaborablesEntre(fecha1, fecha2)
        def json = new JsonBuilder(ret)
        render json
    }

    def calcDias() {
        def fecha = new Date().parse("dd-MM-yyyy", params.fecha)
        def dias = params.dias.toInteger()

        def ret = diasLaborablesService.diasLaborablesDesde(fecha, dias)
        def json = new JsonBuilder(ret)
        render json
    }

    def saveCalendario() {
        def parametros = Parametros.list()
        if (parametros.size() == 0) {
            parametros = new Parametros([
                    horaInicio  : 8,
                    minutoInicio: 00,
                    horaFin     : 16,
                    minutoFin   : 30
            ])
            if (!parametros.save(flush: true)) {
                println "error al guardar params: " + parametros.errors
            }
        } else {
            parametros = parametros.first()
        }

        def errores = 0
        params.dia.each { dia ->
//            println dia
            def parts = dia.split(":")
//            println parts
//            println parts.size()
//            println "********************************************"
            if (parts.size() == 3 || parts.size() == 7) {
                def id = parts[0].toLong()
                def fecha = new Date().parse("dd-MM-yyyy", parts[1])
                def cont = parts[2].toInteger()
//                println id + "     " + fecha.format("dd-MM-yyyy") + "    " + cont
                def diaLaborable = DiaLaborable.get(id)
                if (diaLaborable.fecha == fecha &&
                        (cont != diaLaborable.ordinal ||
                                diaLaborable.horaInicio != parts[3].toInteger() ||
                                diaLaborable.minutoInicio != parts[4].toInteger() ||
                                diaLaborable.horaFin != parts[5].toInteger() ||
                                diaLaborable.minutoFin != parts[6].toInteger())
                ) {
                    diaLaborable.ordinal = cont
                    if (parts.size() == 7) {
                        // si las horas fueron cambiadas, es decir no es parametros.horaInicio o los minutos fueron cambiados
                        // grabo la hora y minutos de inicio
                        if (parts[3].toString() != parametros.horaInicio.toString() ||
                                parts[4].toString() != parametros.minutoInicio.toString()) {
//                            println "parts[3]=" + parts[3] + "      parts[4]=" + parts[4]
                            diaLaborable.horaInicio = parts[3].toInteger()
                            diaLaborable.minutoInicio = parts[4].toInteger()
                        } else {
                            if (diaLaborable.horaInicio != -1) {
                                diaLaborable.horaInicio = -1
                            }
                            if (diaLaborable.minutoInicio != -1) {
                                diaLaborable.minutoInicio = -1
                            }
                        }
                        // si las horas fueron cambiadas, es decir no es parametros.horaFin o los minutos fueron cambiados
                        // grabo la hora y minutos de fin
                        if (parts[5].toString() != parametros.horaFin.toString() ||
                                parts[6].toString() != parametros.minutoFin.toString()) {
//                            println "parts[5]=" + parts[5] + "      parts[6]=" + parts[6]
                            diaLaborable.horaFin = parts[5].toInteger()
                            diaLaborable.minutoFin = parts[6].toInteger()
                        } else {
                            if (diaLaborable.horaFin != -1) {
                                diaLaborable.horaFin = -1
                            }
                            if (diaLaborable.minutoFin != -1) {
                                diaLaborable.minutoFin = -1
                            }
                        }
                    } else {
                        diaLaborable.horaInicio = diaLaborable.horaInicio ?: -1
                        diaLaborable.minutoInicio = diaLaborable.minutoInicio ?: -1
                        diaLaborable.horaFin = diaLaborable.horaFin ?: -1
                        diaLaborable.minutoFin = diaLaborable.minutoFin ?: -1
                    }
                    if (!diaLaborable.save(flush: true)) {
                        errores++
                        println "error al guardar dia laborable ${id}: " + diaLaborable.errors
                    } /*else {
                        println "saved ${id}"
                    }*/
                }
            }
        }
        if (errores == 0) {
            render "OK"
        } else {
            render "NO_Ha${errores == 1 ? '' : 'n'} ocurrido ${errores} error${errores == 1 ? '' : 'es'}"
        }
    }

    def desactivar() {
        // ******************************** DESACTIVA EL ANIO **************************************************** //
        def anio = Anio.get(params.id)
        def anioRedirect = anio.numero
        anio.estado = 0
        if (!anio.save(flush: true)) {
            println "errores: " + anio.errors
        } else {
            // ******************************** CREA EL ANIO SIGUIENTE **************************************************** //
            def intAnioNext = (anio.numero.toInteger() + 1).toString()
            anioRedirect = intAnioNext
            def anioNext = Anio.findAllByNumero(intAnioNext, [sort: "id"])
            if (anioNext.size() > 1) {
                println "Hay mas de un registro de año ${intAnioNext}!!!! ${anioNext}"
                anioNext = anioNext.first()
            } else if (anioNext.size() == 1) {
                anioNext = anioNext.first()
                anioNext.estado = 1
                if (!anioNext.save(flush: true)) {
                    println "erores: " + anioNext.errors
                }
            } else {
                anioNext = new Anio([
                        numero: intAnioNext,
                        estado: 1
                ])
                if (!anioNext.save(flush: true)) {
                    println "erores: " + anioNext.errors
                }
            }

            // ******************************** RESETEA NUMERACIONES **************************************************** //
            Numero.list().each { num ->
                num.valor = 0
                if (!num.save(flush: true)) {
                    println "error: " + num.errors
                }
            }
        }
        redirect(action: "calendario", params: [anio: anioRedirect])
    }

    def error() {
        return [params: params]
    }

    def calendario() {
        if (session.usuario.puedeAdmin) {
            def parametros = Parametros.list()
            if (parametros.size() == 0) {
                parametros = new Parametros([
                        horaInicio  : 8,
                        minutoInicio: 00,
                        horaFin     : 16,
                        minutoFin   : 30
                ])
                if (!parametros.save(flush: true)) {
                    println "error al guardar params: " + parametros.errors
                }
            } else {
                parametros = parametros.first()
            }

            if (!params.anio) {
                params.anio = new Date().format('yyyy').toInteger()
            }

            def anio = Anio.findAllByNumeroAndEstado(params.anio, 1, [sort: "id"])
            if (anio.size() > 1) {
                flash.message = "Se encontraron ${anio.size()} registros para el año ${params.anio}. Por favor póngase en contacto con el administrador."
                redirect(action: "error")
                return
            } else if (anio.size() == 0) {
                flash.message = "No se encontraron registros para el año ${params.anio}. Por favor póngase en contacto con el administrador."
                redirect(action: "error")
                return
            }
            anio = anio.first()

            if (anio.estado == 0) {
                flash.message = "El año ${params.anio} se encuentra desactivado. Por favor póngase en contacto con el administrador."
                redirect(action: "error")
                return
            }

            def dias = DiaLaborable.withCriteria {
                eq("anio", anio)
                order("fecha", "asc")
            }

            if (dias.size() < 365) {
                if (DiaLaborable.countByAnio(anio) == 0) {
                    def js = "<script type='text/javascript'>"
                    js += '$(".btnInit").click(function() {' +
                            'openLoader("Inicializando ' + anio.numero + '");' +
                            '});'
                    js += "</script>"
                    flash.message = "<p>No se encontraron registros de días laborables. Para inicializar el calendario haga click en el botón Inicializar.</p>" +
                            "<p>" +
                            g.link(action: "inicializar", class: "btn btn-success btnInit", params: [anio: anio.numero]) {
                                "<i class='fa fa-check'></i> Inicializar"
                            } +
                            "</p>" + js
                    redirect(action: "error")
                    return
                } else {
                    flash.message = "No se encontraron registros para los días laborables del año ${params.anio}. Por favor póngase en contacto con el administrador."
                    redirect(action: "error")
                    return
                }
            }

            def meses = ["", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"]

            return [anio: anio, dias: dias, meses: meses, params: params]
        } else {
            flash.message = "Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será reportada"
            response.sendError(403)
        }

    }

    def inicializar() {
        params.anio = params.anio ?: new Date().format("yyyy")

        def anio = Anio.findAllByNumero(params.anio)

        if (anio.size() > 1) {
            flash.message = "Se encontraron ${anio.size()} registros para el año ${params.anio}. Por favor póngase en contacto con el administrador."
            redirect(action: "error")
            return
        } else if (anio.size() == 1) {
            anio = anio.first()
            anio.estado = 1
        } else {
            anio = new Anio([
                    numero: params.anio,
                    estado: 1
            ])
        }
        if (!anio.save(flush: true)) {
            flash.message = "Ha ocurrido un error al crear el año ${params.anio}. Por favor póngase en contacto con el administrador.<br/>" + g.renderErrors(bean: anio)
            redirect(action: "error")
            return
        }
        def parametros = Parametros.list()
        if (parametros.size() == 0) {
            parametros = new Parametros([
                    horaInicio  : 8,
                    minutoInicio: 00,
                    horaFin     : 16,
                    minutoFin   : 30
            ])
            if (!parametros.save(flush: true)) {
                println "error al guardar params: " + parametros.errors
            }
        } else {
            parametros = parametros.first()
        }
        def meses = ["", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"]
        def enero01 = new Date().parse("dd-MM-yyyy", "01-01-" + params.anio)
        def diciembre31 = new Date().parse("dd-MM-yyyy", "31-12-" + params.anio)

        def dias = DiaLaborable.withCriteria {
            eq("anio", anio)
            order("fecha", "asc")
        }
        if (dias.size() < 365) {
            println "No hay todos los dias para ${params.anio}: hay " + dias.size()

            def fecha = enero01
            def cont = 1
            def fds = ["sat", "sun"]
            def fmt = new java.text.SimpleDateFormat("EEE", new Locale("en"))

            def diasSem = [
                    "mon": 1,
                    "tue": 2,
                    "wed": 3,
                    "thu": 4,
                    "fri": 5,
                    "sat": 6,
                    "sun": 0,
            ]
            def guardados = 0
            while (fecha <= diciembre31) {
                def dia = fmt.format(fecha).toLowerCase()
                def ordinal = 0
                if (!fds.contains(dia)) {
                    ordinal = cont
                    cont++
                }
                def diaExiste = DiaLaborable.withCriteria {
                    eq("fecha", fecha)
                }
                if (!diaExiste) {
                    def diaLaborable = new DiaLaborable([
                            fecha       : fecha,
                            dia         : diasSem[dia],
                            anio        : anio,
                            ordinal     : ordinal,
                            horaInicio  : -1,
                            minutoInicio: -1,
                            horaFin     : -1,
                            minutoFin   : -1
                    ])
                    if (!diaLaborable.save(flush: true)) {
                        println "error al guardar el dia laborable ${fecha.format('dd-MM-yyyy')}: " + diaLaborable.errors
                    } else {
                        guardados++
//                    println "guardado: " + fecha.format("dd-MM-yyyy") + "   " + dia + " ordinal:" + ordinal
                    }
                }
                fecha++
            }
            println "Guardados ${guardados} dias"
        }
        redirect(action: "calendario", params: params)
    }

    def calendario_old() {

        def parametros = Parametros.list()
        if (parametros.size() == 0) {
            parametros = new Parametros([
                    horaInicio  : 8,
                    minutoInicio: 00,
                    horaFin     : 16,
                    minutoFin   : 30
            ])
            if (!parametros.save(flush: true)) {
                println "error al guardar params: " + parametros.errors
            }
        } else {
            parametros = parametros.first()
        }

        def anio = new Date().format('yyyy').toInteger()

        if (!params.anio) {
            params.anio = anio
        }

        def anioObj = Anio.findAllByNumero(params.anio, [sort: "id"])
        if (anioObj.size() > 1) {
            println "Hay mas de un registro de año ${params.anio}!!!! ${anioObj}"
            anioObj = anioObj.first()
        } else if (anioObj.size() == 1) {
            anioObj = anioObj.first()
            if (anioObj.estado == 0) {
                response.sendError(404)
            }
        } else {
            if (new Date().format("yyyy") == params.anio.toString() || (new Date().format("yyyy").toInteger() - 1 == params.anio.toInteger() && new Date().format("mm") == "12" && (new Date().format("dd").toInteger() >= 26))) {
                anioObj = new Anio([
                        numero: params.anio,
                        estado: 0
                ])
                if (!anioObj.save(flush: true)) {
                    println "ERROR: " + anioObj.errors
                }
            } else {
                response.sendError(404)
            }
        }
        def meses = ["", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"]
        def enero01 = new Date().parse("dd-MM-yyyy", "01-01-" + params.anio)
        def diciembre31 = new Date().parse("dd-MM-yyyy", "31-12-" + params.anio)

//        def dias = DiaLaborable.withCriteria {
//            ge("fecha", enero01)
//            le("fecha", diciembre31)
//            order("fecha", "asc")
//        }
        def dias = DiaLaborable.withCriteria {
            eq("anio", anioObj)
            order("fecha", "asc")
        }

        if (dias.size() < 365) {
            println "No hay todos los dias para ${params.anio}: hay " + dias.size()

            def fecha = enero01
            def cont = 1
            def fds = ["sat", "sun"]
            def fmt = new java.text.SimpleDateFormat("EEE", new Locale("en"))

            def diasSem = [
                    "mon": 1,
                    "tue": 2,
                    "wed": 3,
                    "thu": 4,
                    "fri": 5,
                    "sat": 6,
                    "sun": 0,
            ]

            while (fecha <= diciembre31) {
                def dia = fmt.format(fecha).toLowerCase()
                def ordinal = 0
                if (!fds.contains(dia)) {
                    ordinal = cont
                    cont++
                }
                def diaExiste = DiaLaborable.withCriteria {
                    eq("fecha", fecha)
                }
                if (!diaExiste) {
                    def diaLaborable = new DiaLaborable([
                            fecha       : fecha,
                            dia         : diasSem[dia],
                            anio        : anioObj,
                            ordinal     : ordinal,
                            horaInicio  : -1,
                            minutoInicio: -1,
                            horaFin     : -1,
                            minutoFin   : -1
                    ])
                    if (!diaLaborable.save(flush: true)) {
                        println "error al guardar el dia laborable ${fecha.format('dd-MM-yyyy')}: " + diaLaborable.errors
                    } else {
//                    println "guardado: " + fecha.format("dd-MM-yyyy") + "   " + dia + " ordinal:" + ordinal
                    }
                }
                fecha++
            }
            dias = DiaLaborable.withCriteria {
                ge("fecha", enero01)
                le("fecha", diciembre31)
                order("fecha", "asc")
            }
            println "Guardados ${dias.size()} dias"
        }

        return [anio: anio, dias: dias, meses: meses, params: params, anioObj: anioObj]
    }

    def index() {
        redirect(action: "calendario", params: params)
    } //index

    def list() {
        [diaLaborableInstanceList: DiaLaborable.list(params), params: params]
    } //list

    def form_ajax() {
        def diaLaborableInstance = new DiaLaborable(params)
        if (params.id) {
            diaLaborableInstance = DiaLaborable.get(params.id)
            if (!diaLaborableInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Dia Laborable con id " + params.id
                redirect(action: "list")
                return
            } //no existe el objeto
        } //es edit
        return [diaLaborableInstance: diaLaborableInstance]
    } //form_ajax

    def save() {
        def diaLaborableInstance
        if (params.id) {
            diaLaborableInstance = DiaLaborable.get(params.id)
            if (!diaLaborableInstance) {
                flash.clase = "alert-error"
                flash.message = "No se encontró Dia Laborable con id " + params.id
                redirect(action: 'list')
                return
            }//no existe el objeto
            diaLaborableInstance.properties = params
        }//es edit
        else {
            diaLaborableInstance = new DiaLaborable(params)
        } //es create
        if (!diaLaborableInstance.save(flush: true)) {
            flash.clase = "alert-error"
            def str = "<h4>No se pudo guardar Dia Laborable " + (diaLaborableInstance.id ? diaLaborableInstance.id : "") + "</h4>"

            str += "<ul>"
            diaLaborableInstance.errors.allErrors.each { err ->
                def msg = err.defaultMessage
                err.arguments.eachWithIndex { arg, i ->
                    msg = msg.replaceAll("\\{" + i + "}", arg.toString())
                }
                str += "<li>" + msg + "</li>"
            }
            str += "</ul>"

            flash.message = str
            redirect(action: 'list')
            return
        }

        if (params.id) {
            flash.clase = "alert-success"
            flash.message = "Se ha actualizado correctamente Dia Laborable " + diaLaborableInstance.id
        } else {
            flash.clase = "alert-success"
            flash.message = "Se ha creado correctamente Dia Laborable " + diaLaborableInstance.id
        }
        redirect(action: 'list')
    } //save

    def show_ajax() {
        def diaLaborableInstance = DiaLaborable.get(params.id)
        if (!diaLaborableInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Dia Laborable con id " + params.id
            redirect(action: "list")
            return
        }
        [diaLaborableInstance: diaLaborableInstance]
    } //show

    def delete() {
        def diaLaborableInstance = DiaLaborable.get(params.id)
        if (!diaLaborableInstance) {
            flash.clase = "alert-error"
            flash.message = "No se encontró Dia Laborable con id " + params.id
            redirect(action: "list")
            return
        }

        try {
            diaLaborableInstance.delete(flush: true)
            flash.clase = "alert-success"
            flash.message = "Se ha eliminado correctamente Dia Laborable " + diaLaborableInstance.id
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.clase = "alert-error"
            flash.message = "No se pudo eliminar Dia Laborable " + (diaLaborableInstance.id ? diaLaborableInstance.id : "")
            redirect(action: "list")
        }
    } //delete
} //fin controller
