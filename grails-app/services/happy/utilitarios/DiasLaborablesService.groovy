package happy.utilitarios

import groovy.time.TimeCategory


class DiasLaborablesService {
    def dbConnectionService
    static transactional = false

    def getParametros() {
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
                return null
            }
        } else {
            parametros = parametros.first()
            return parametros
        }
    }

    /**
     * fechaMasTiempo
     *      retorna una fecha con horas de la fecha enviada mas el tiempo enviado
     *
     * @param fecha la fecha inicial
     * @param horas las horas a sumar
     * @param minutos los minutos a sumar
     * @param noLaborables true: si una de las fechas es no laborable pasa al primer dia laborable futuro.
     *                      false: si una de las fechas es no laborable retorna false
     *                      Por default pasa true
     * @return array        en posicion 0: boolean true:  hizo el calculo correctamente
     *                                             false: hubo un error
     *                                  1: la fecha cuando el calculo fue correcto
     *                                     el error si hubo error
     *                                  2: si hubo algun mensaje aunque haya hecho el calculo
     *                                     si hubo error (no hay los dias laborables), el año para configurar los días laborables
     */
    def fechaMasTiempo(Date fecha, int horas, int minutos, boolean noLaborables) {
//        def fecha = paramsFecha.clone().clearTime()
//        println "****"
//        println "params.fecha " + fecha
//        println "params.horas " + horas
//        println "params.minutos " + minutos
//        println "params.noLaborables " + noLaborables
//        println "****"
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
        def mensaje = ""
        def dia = DiaLaborable.findAllByFecha(fecha.clone().clearTime())
        def setInicioJornada = false
        if (dia.size() == 1) {
            dia = dia.first()

            def iniciaH = dia.horaInicio > -1 ? dia.horaInicio : parametros.horaInicio
            def iniciaM = dia.minutoInicio > -1 ? dia.minutoInicio : parametros.minutoInicio
            def finH = dia.horaFin > -1 ? dia.horaFin : parametros.horaFin
            def finM = dia.minutoFin > -1 ? dia.minutoFin : parametros.minutoFin

            def fechaLimiteManana = new Date().parse("dd-MM-yyyy HH:mm", dia.fecha.format("dd-MM-yyyy") + " " + iniciaH + ":" + iniciaM)
            def fechaLimiteTarde = new Date().parse("dd-MM-yyyy HH:mm", dia.fecha.format("dd-MM-yyyy") + " " + finH + ":" + finM)

            /* si la fecha inicial tiene una hora previa a la fecha limite de la manana,
                    se pone en hora la hora de inicio de la jornada */
//            println "fechaLimiteManana: " + fechaLimiteManana
//            println "fechaLimiteTarde: " + fechaLimiteTarde
            /* si la fecha inicial tiene una hora posterior a la fecha limite de la tarde,
                    se suma un dia y se pone en hora la hora de inicio de la jornada*/

            if (fecha > fechaLimiteTarde) {
                use(TimeCategory) {
                    fecha = fecha + 1.days
                }
                setInicioJornada = true
//                println "\t\tfecha: " + fecha
            } else if (fecha < fechaLimiteManana) {
                setInicioJornada = true
//                println "\t\tfecha: " + fecha
            }
            dia = DiaLaborable.findAllByFecha(fecha.clone().clearTime())
            if (dia.size() == 1) {
                dia = dia.first()
            } else if (dia.size() == 0) {
                return [false, "No se encontró el registro de días laborables para la fecha " + fecha.format("dd-MM-yyyy"), fecha.format("yyyy")]
            } else {
                return [false, "Se encontraron varios registros de días laborables para la fecha " + fecha.format("dd-MM-yyyy"), fecha.format("yyyy")]
            }

//            println "**************************************"
            def ord = dia.ordinal
            if (ord == 0) {
                if (noLaborables) {
                    def nuevaFecha = fecha.clone()
                    while (ord == 0) {
//                        println "while1"
                        nuevaFecha++
                        dia = DiaLaborable.findAllByFecha(nuevaFecha.clone().clearTime())
                        if (dia.size() == 1) {
                            dia = dia.first()
                        } else if (dia.size() == 0) {
                            return [false, "No se encontró el registro de días laborables para la fecha " + nuevaFecha.format("dd-MM-yyyy"), nuevaFecha.format("yyyy")]
                        } else {
                            return [false, "Se encontraron varios registros de días laborables para la fecha " + nuevaFecha.format("dd-MM-yyyy"), nuevaFecha.format("yyyy")]
                        }
                        ord = dia.ordinal
                    }
                    mensaje += "<li>La fecha " + fecha.format("dd-MM-yyyy") + " no es un día laborable. Se utilizó " + nuevaFecha.format("dd-MM-yyyy") + "</li>"
//                    println mensaje
                    fecha = nuevaFecha
                } else {
                    return [false, "La fecha " + fecha.format("dd-MM-yyyy") + " no es un día laborable. Para calcular con el siguiente dia laborable pasar true como 3r parametro"]
//                    return false
                }
            }

            if (setInicioJornada) {
                def strFecha = fecha.format("dd-MM-yyyy") + " "
                strFecha += (dia.horaInicio > -1 ? dia.horaInicio : parametros.horaInicio) + ":"
                strFecha += (dia.minutoInicio > -1 ? dia.minutoInicio : parametros.minutoInicio)
//                println "\t\t..." + strFecha
                fecha = new Date().parse("dd-MM-yyyy HH:mm", strFecha)
//                println "\t\t..." + fecha
                finH = dia.horaFin > -1 ? dia.horaFin : parametros.horaFin
                finM = dia.minutoFin > -1 ? dia.minutoFin : parametros.minutoFin

                fechaLimiteTarde = new Date().parse("dd-MM-yyyy HH:mm", dia.fecha.format("dd-MM-yyyy") + " " + finH + ":" + finM)

            }
//            println "diaLaborable (ord) " + dia.toString() + "   (" + ord + ")"
//            println "inicio dia " + iniciaH + ":" + iniciaM
//            println "fin dia " + finH + ":" + finM
//            println "fechaLimiteTarde " + fechaLimiteTarde

            def fechaFin = fecha

            if (horas >= 24) {
                def dias = (horas / 24).toInteger()
//                println "Sumar ${dias} dias"

                def nuevoOrdinal = ord + dias
                def anio = dia.anio
                def diasAnio = DiaLaborable.countByAnio(anio)
                if (nuevoOrdinal > diasAnio) {
                    nuevoOrdinal = nuevoOrdinal - diasAnio
                    anio = anio + 1
                }

                def nuevoDiaLaborable = DiaLaborable.findAllByOrdinalAndAnio(nuevoOrdinal, anio)
                if (nuevoDiaLaborable.size() == 1) {
                    nuevoDiaLaborable = nuevoDiaLaborable.first()
                } else if (nuevoDiaLaborable.size() == 0) {
                    return [false, "No se encontró el registro de días laborables para el ordinal ${nuevoOrdinal} del año ${anio}.", anio]
                } else {
                    return [false, "Se encontraron varios registros de días laborables para el ordinal ${nuevoOrdinal} del año ${anio}", anio]
                }

                def strFecha = nuevoDiaLaborable.fecha.format("dd-MM-yyyy") + " " + fecha.format("HH:mm")
                fechaFin = new Date().parse("dd-MM-yyyy HH:mm", strFecha)

                return [true, fechaFin, mensaje != "" ? "<ul>" + mensaje + "</ul>" : ""]
            } else {
                use(TimeCategory) {
                    fechaFin = fechaFin + horas.hours + minutos.minutes
                }
//                println "fecha fin " + fechaFin
                def difference
                use(TimeCategory) {
                    difference = fechaFin - fechaLimiteTarde
                }
//                println "difference: days: ${difference.days}, Hours: ${difference.hours}, Minutes: ${difference.minutes}"

                if (difference.hours <= 0 && difference.minutes <= 0) {
                    return [true, fechaFin, mensaje != "" ? "<ul>" + mensaje + "</ul>" : ""]
                } else {
                    //el siguiente dia laborable:
                    def anio = dia.anio
                    def nuevoOrdinal = ord + 1
                    def diasAnio = DiaLaborable.countByAnio(anio)
                    if (nuevoOrdinal > diasAnio) {
                        nuevoOrdinal = nuevoOrdinal - diasAnio
                        anio = anio + 1
                    }
                    def siguiente = DiaLaborable.findAllByOrdinalAndAnio(nuevoOrdinal, anio)
                    if (siguiente.size() == 1) {
                        siguiente = siguiente.first()
                    } else if (siguiente.size() == 0) {
                        return [false, "No se encontró el registro de días laborables para el ordinal ${nuevoOrdinal} del año ${anio}.", anio]
                    } else {
                        return [false, "Se encontraron varios registros de días laborables para el ordinal ${nuevoOrdinal} del año ${anio}", anio]
                    }
                    def strSiguiente = siguiente.fecha.format("dd-MM-yyyy") + " "
                    strSiguiente += (siguiente.horaInicio > -1 ? siguiente.horaInicio : parametros.horaInicio) + ":"
                    strSiguiente += (siguiente.minutoInicio > -1 ? siguiente.minutoInicio : parametros.minutoInicio)
                    def fechaSiguiente = new Date().parse("dd-MM-yyyy HH:mm", strSiguiente)
//                    println "fecha siguiente: " + fechaSiguiente
                    use(TimeCategory) {
                        fechaSiguiente = fechaSiguiente + (difference.hours).hours + (difference.minutes).minutes
                    }
//                    println "fecha siguiente con hora " + fechaSiguiente
                    return [true, fechaSiguiente, mensaje != "" ? "<ul>" + mensaje + "</ul>" : ""]
                }
            }
        } else if (dia.size() == 0) {
            return [false, "No se encontró el registro de días laborables para la fecha " + fecha.format("dd-MM-yyyy"), fecha.format("yyyy")]
//            return false
        } else {
            return [false, "Se encontraron varios registros de días laborables para la fecha " + fecha.format("dd-MM-yyyy"), fecha.format("yyyy")]
//            return false
        }
//        println "**************************************"
    }

    /**
     * fechaMasTiempo
     *      retorna una fecha con horas de la fecha enviada mas el tiempo enviado.
     *      Pasa por default 0 minutos y true (para que si la fecha enviada no es laborable use la siguiente
     *          fecha laborable)
     * @param fecha la fecha inicial
     * @param horas las horas a sumar
     * @return array        en posicion 0: boolean true:  hizo el calculo correctamente
     *                                             false: hubo un error
     *                                  1: la fecha cuando el calculo fue correcto
     *                                     el error si hubo error
     *                                  2: si hubo algun mensaje aunque haya hecho el calculo
     *                                     si hubo error (no hay los dias laborables), el año para configurar los días laborables
     */
    def fechaMasTiempo(Date fecha, int horas) {
        return fechaMasTiempo(fecha, horas, 0, true)
    }

    /**
     * fechaMasDias
     *      retorna una fecha con horas de la fecha enviada mas el numero de dias.
     *      Pasa por default 0 minutos y true (para que si la fecha enviada no es laborable use la siguiente
     *          fecha laborable)
     * @param fecha la fecha inicial
     * @param dias el numero de dias a sumar
     * @return array        en posicion 0: boolean true:  hizo el calculo correctamente
     *                                             false: hubo un error
     *                                  1: la fecha cuando el calculo fue correcto
     *                                     el error si hubo error
     *                                  2: si hubo algun mensaje aunque haya hecho el calculo
     *                                     si hubo error (no hay los dias laborables), el año para configurar los días laborables
     */
    def fechaMasDia(Date fecha, int dias) {
        return fechaMasTiempo(fecha, dias * 24, 0, true)
    }

    /**
     * diasLaborablesEntre()
     *      retorna el numero de dias laborables entre 2 fechas
     * @param fecha1 la una fecha
     * @param fecha2 la otra fecha
     * @param noLaborables true: si una de las fechas es no laborable pasa al primer dia laborable futuro.
     *                      false: si una de las fechas es no laborable retorna false
     *                      Por default pasa true
     * @return array        en posicion 0: boolean true:  hizo el calculo correctamente
     *                                             false: hubo un error
     *                                  1: el numero de dias cuando el calculo fue correcto
     *                                     el error si hubo error
     *                                  2: si hubo algun mensaje aunque haya hecho el calculo
     *                                     si hubo error (no hay los dias laborables), el año para configurar los días laborables
     */
    def diasLaborablesEntre(Date paramsFecha1, Date paramsFecha2, boolean noLaborables) {
        def fecha1 = paramsFecha1.clone().clearTime()
        def fecha2 = paramsFecha2.clone().clearTime()
//        println "****"
//        println fecha1
//        println fecha2
//        println noLaborables
//        println "****"
        def mensaje = ""
        def dl1 = DiaLaborable.findAllByFecha(fecha1)
        if (dl1.size() == 1) {
            dl1 = dl1[0]
            def ord1 = dl1.ordinal
            if (ord1 == 0) {
                if (noLaborables) {
                    def nuevaFecha1 = fecha1
                    while (ord1 == 0) {
                        println "while1"
                        nuevaFecha1++
                        dl1 = DiaLaborable.findByFecha(nuevaFecha1)
                        ord1 = dl1.ordinal
                    }
                    mensaje += "<li>La fecha " + fecha1.format("dd-MM-yyyy") + " no es un día laborable. Se utilizó " + nuevaFecha1.format("dd-MM-yyyy") + "</li>"
                    println mensaje
                } else {
                    return [false, "La fecha " + fecha1.format("dd-MM-yyyy") + " no es un día laborable. Para calcular con el siguiente dia laborable pasar true como 3r parametro"]
//                    return false
                }
            }
            def dl2 = DiaLaborable.findAllByFecha(fecha2)
            if (dl2.size() == 1) {
                dl2 = dl2[0]
                def ord2 = dl2.ordinal
                if (ord2 == 0) {
                    if (noLaborables) {
                        def nuevaFecha2 = fecha2
                        while (ord2 == 0) {
                            println "while2"
                            nuevaFecha2++
                            dl2 = DiaLaborable.findByFecha(nuevaFecha2)
                            ord2 = dl2.ordinal
                        }
                        mensaje += "<li>La fecha " + fecha2.format("dd-MM-yyyy") + " no es un día laborable. Se utilizó " + nuevaFecha2.format("dd-MM-yyyy") + "</li>"
                        println mensaje
                    } else {
                        return [false, "La fecha " + fecha2.format("dd-MM-yyyy") + " no es un día laborable. Para calcular con el siguiente dia laborable pasar true como 3r parametro"]
//                        return false
                    }
                }
                return [true, Math.abs(ord2 - ord1), mensaje != "" ? "<ul>" + mensaje + "</ul>" : ""]
            } else if (dl2.size() == 0) {
                return [false, "No se encontró el registro de días laborables para la fecha " + fecha2.format("dd-MM-yyyy"), fecha2.format("yyyy")]
//                return false
            } else {
                return [false, "Se encontraron varios registros de días laborables para la fecha " + fecha2.format("dd-MM-yyyy"), fecha2.format("yyyy")]
//                return false
            }
        } else if (dl1.size() == 0) {
            return [false, "No se encontró el registro de días laborables para la fecha " + fecha1.format("dd-MM-yyyy"), fecha1.format("yyyy")]
//            return false
        } else {
            return [false, "Se encontraron varios registros de días laborables para la fecha " + fecha1.format("dd-MM-yyyy"), fecha1.format("yyyy")]
//            return false
        }
    }

    def diasLaborablesEntre(Date fecha1, Date fecha2) {
        return diasLaborablesEntre(fecha1, fecha2, true)
    }

    /**
     * diasLaborablesDesde
     *      retorna la fecha n dias laborables despues de una fecha
     * @param fecha la fecha
     * @param dias el numero de dias
     * @param noLaborables true: si la fecha es no laborable pasa al primer dia laborable futuro.
     *                      false: si la fecha es no laborable retorna false
     *                      Por default pasa true
     * @return array        en posicion 0: boolean true:  hizo el calculo correctamente
     *                                             false: hubo un error
     *                                  1: la fecha (Date) cuando el calculo fue correcto
     *                                     el error si hubo error
     *                                  2: si hizo el calculo, la fecha en string con format dd-MM-yyyy
     *                                     si hubo error (no hay los dias laborables), el año para configurar los días laborables
     *                                  3: si hubo algun mensaje aunque haya hecho el calculo
     */
    def diasLaborablesDesde(Date paramsFecha, int dias, boolean noLaborables) {
        def fecha = paramsFecha.clone().clearTime()
        def mensaje = ""
        def dl = DiaLaborable.findAllByFecha(fecha)
        if (dl.size() == 1) {
            dl = dl[0]
            def ord = dl.ordinal
            if (ord == 0) {
                if (noLaborables) {
                    def nuevaFecha = fecha
                    while (ord == 0) {
                        nuevaFecha++
                        dl = DiaLaborable.findByFecha(nuevaFecha)
                        ord = dl.ordinal
                    }
                    mensaje += "<li>La fecha " + fecha.format("dd-MM-yyyy") + " no es un día laborable. Se utilizó " + nuevaFecha.format("dd-MM-yyyy") + "</li>"
                    println mensaje
                } else {
                    return [false, "La fecha " + fecha.format("dd-MM-yyyy") + " no es un día laborable. Para calcular con el siguiente dia laborable pasar true como 3r parametro"]
//                    return false
                }
            }
            def nuevoOrd = ord + dias

            def anioFecha = fecha.format("yyyy").toInteger()
            def c = DiaLaborable.createCriteria()
            def diaMaxAnio = c.get {
                eq("anio", anioFecha)
                projections {
                    max "ordinal"
                }
            }
//            println "0 anio: " + anioFecha
//            println "0 ordinal nuevo dia: " + nuevoOrd
//            println "0 dias max anio: " + diaMaxAnio
//            println "0 ord fecha inicio: " + ord
            if (nuevoOrd <= diaMaxAnio) {
                def nuevoDia = DiaLaborable.withCriteria {
                    eq("anio", anioFecha)
                    eq("ordinal", nuevoOrd)
                }
//                println ">>>>>>>>>>>>>" + nuevoDia
                if (nuevoDia.size() == 1) {
                    return [true, nuevoDia[0].fecha, nuevoDia[0].fecha.format("dd-MM-yyyy"), mensaje != "" ? "<ul>" + mensaje + "</ul>" : ""]
                } else if (nuevoDia.size() == 0) {
                    return [false, "No se encontró el registro del día laborable n. ${nuevoOrd} del año ${anioFecha}", anioFecha]
                } else {
                    return [false, "Se encontraron ${nuevoDia.size()} registros para día laborable n. ${nuevoOrd} del año ${anioFecha}", anioFecha]
                }
            } else {
                def anioAct = anioFecha + 1
                def diasRestantesAnio = diaMaxAnio - ord
                def ordAct = nuevoOrd - diasRestantesAnio
                c = DiaLaborable.createCriteria()
                def nuevoDiaMax = c.get {
                    eq("anio", anioAct)
                    projections {
                        max "ordinal"
                    }
                }
                def cont = ordAct > nuevoDiaMax

//                println "1 anio: " + anioAct
//                println "1 ordinal nuevo dia: " + ordAct
//                println "1 dias max anio: " + nuevoDiaMax
//                println "1 dias rest anio: " + diasRestantesAnio
//                println "1 continua? " + cont

                while (cont) {
                    if (nuevoDiaMax) {
                        anioAct++
                        ordAct = ordAct - nuevoDiaMax
                        c = DiaLaborable.createCriteria()
                        nuevoDiaMax = c.get {
                            eq("anio", anioAct)
                            projections {
                                max "ordinal"
                            }
                        }
                        cont = ordAct > nuevoDiaMax
//                        println "\tanio: " + anioAct
//                        println "\tordinal nuevo dia: " + ordAct
//                        println "\tdias max anio: " + nuevoDiaMax
//                        println "\tcontinua? " + cont
                    } else {
                        return [false, "No se encontraron registros para días laborables del año ${anioAct}", anioAct]
                    }
                }

                def nuevoDia = DiaLaborable.withCriteria {
                    eq("anio", anioAct)
                    eq("ordinal", ordAct)
                }
//                println ">>>>>>>>>>>>>" + nuevoDia
                if (nuevoDia.size() == 1) {
                    return [true, nuevoDia[0].fecha, nuevoDia[0].fecha.format("dd-MM-yyyy"), mensaje != "" ? "<ul>" + mensaje + "</ul>" : ""]
                } else if (nuevoDia.size() == 0) {
                    return [false, "No se encontró el registro del día laborable n. ${nuevoOrd} del año ${anioFecha}", anioFecha]
                } else {
                    return [false, "Se encontraron ${nuevoDia.size()} registros para día laborable n. ${nuevoOrd} del año ${anioFecha}", anioFecha]
                }

//                println "OK anio: " + anioAct
//                println "OK ordinal nuevo dia: " + ordAct
//                println "OK dias max anio:" + nuevoDiaMax

//                println "AQUI"
            }

        } else if (dl.size() == 0) {
            return [false, "No se encontró el registro de días laborables para la fecha " + fecha.format("dd-MM-yyyy"), fecha.format("yyyy")]
//            return false
        } else {
            return [false, "Se encontraron varios registros de días laborables para la fecha " + fecha.format("dd-MM-yyyy"), fecha.format("yyyy")]
//            return false
        }
    }

    def diasLaborablesDesde(Date fecha, int dias) {
        return diasLaborablesDesde(fecha, dias, true)
    }


    def tiempoLaborableEntre(Date fecha1, Date fecha2, boolean noLaborables) {

        if (fecha2 < fecha1) {
            def fechaTemp = fecha1
            fecha1 = fecha2
            fecha2 = fechaTemp
        }

        def soloFecha1 = fecha1.clone().clearTime()
        def soloFecha2 = fecha2.clone().clearTime()

        def soloHora1 = fecha1.format("HH").toInteger()
        def soloMin1 = fecha1.format("mm").toInteger()
        def soloSec1 = fecha1.format("ss").toInteger()
        def soloHora2 = fecha2.format("HH").toInteger()
        def soloMin2 = fecha2.format("mm").toInteger()
        def soloSec2 = fecha2.format("ss").toInteger()

        def dl1 = DiaLaborable.findAllByFecha(soloFecha1)
        def dl2 = DiaLaborable.findAllByFecha(soloFecha2)

        def parametros = getParametros()

//        println fecha1
//        println soloFecha1
//        println fecha2
//        println soloFecha2

        def dias = 0, difHoras = 0, difMins = 0

        if (dl1.size() == 1 && dl2.size() == 1) {  // se hallan en la tabla ddlb
            dl1 = dl1.first()
            def validar = validarLaborable(dl1, noLaborables)
            if (validar[0]) {
                if (validar.size() == 3) {
                    println validar[2]
                }
                dl1 = validar[1]
                fecha1 = dl1.fecha.clone()
                fecha1.set(second: soloSec1, minute: soloMin1, hourOfDay: soloHora1)
                soloFecha1 = fecha1.clone().clearTime()
            } else {
                return validar
            }

            dl2 = dl2.first()
            validar = validarLaborable(dl2, noLaborables)
            if (validar[0]) {
                if (validar.size() == 3) {
                    println validar[2]
                }
                dl2 = validar[1]
                fecha2 = dl2.fecha.clone()
                fecha2.set(second: soloSec2, minute: soloMin2, hourOfDay: soloHora2)
                soloFecha2 = fecha2.clone().clearTime()
            } else {
                return validar
            }

//            println "*" + fecha1
//            println "*" + fecha2

            //1ro saco los dias laborables entre las fechas
            def diasEntre = diasLaborablesEntre(fecha1, fecha2)
//            println "retorna dias entre: $diasEntre"
            if (diasEntre[0]) {
                dias = diasEntre[1]
                if (diasEntre.size() == 3) {
                    println diasEntre[2]
                }
//                println "DIAS: " + dias
                def hoy1 = new Date()
                hoy1.set(second: soloSec1, minute: soloMin1, hourOfDay: soloHora1)
                def hoy2 = new Date()
                hoy2.set(second: soloSec2, minute: soloMin2, hourOfDay: soloHora2)

                if (hoy2 < hoy1) {
                    /* si hoy2 es menor q hoy1:
                 *      reducir un dia
                 *      calcular el tiempo entre la hora de fecha1 y el final de la jornada laboral de dl1
                 *      calcular el tiempo entre el inicio de la jornada laboral de dl2 y la hora de fecha2
                 */
                    dias -= 1
                    def horaFinDl1 = dl1.horaFin > -1 ?: parametros.horaFin
                    def minFinDl1 = dl1.minutoFin > -1 ?: parametros.minutoFin

                    def horaIniDl2 = dl2.horaInicio > -1 ?: parametros.horaInicio
                    def minIniDl2 = dl2.minutoInicio > -1 ?: parametros.minutoInicio

                    def dif1 = diferenciaHoras(soloHora1, soloMin1, horaFinDl1, minFinDl1)
                    def dif2 = diferenciaHoras(horaIniDl2, minIniDl2, soloHora2, soloMin2)

                    difHoras = dif1.horas + dif2.horas
                    difMins = dif1.minutos + dif2.minutos
                    if (difMins >= 60) {
                        difMins -= 60
                        difHoras += 1
                    }
                } else {
                    /* si hoy2 es mayor que hoy1:
                     *      calcular la diferencia de tiempo entre las horas de hoy2 y de hoy1 (hoy2 - hoy1)
                     */
                    def dif = diferenciaHoras(soloHora1, soloMin1, soloHora2, soloMin2)
                    difHoras = dif.horas
                    difMins = dif.minutos
//                    println ">>>> " + difHoras
//                    println ">>>> " + difMins
                }

//                println "\tDIAS FINAL: " + dias
//                println "\tHORAS FINAL: " + difHoras
//                println "\tMINUTOS FINAL: " + difMins
                return [true, [dias: dias, horas: difHoras, minutos: difMins]]
            } else {
                return diasEntre
            }
        } else if (dl1.size() == 0 || dl2.size() == 0) {
            if (dl1.size() == 0) {
                return [false, "No se encontró el registro de días laborables para la fecha " + soloFecha1.format("dd-MM-yyyy"), soloFecha1.format("yyyy")]
            }
            if (dl2.size() == 0) {
                return [false, "No se encontró el registro de días laborables para la fecha " + soloFecha2.format("dd-MM-yyyy"), soloFecha2.format("yyyy")]
            }
        } else if (dl1.size() > 1 || dl2.size() > 1) {
            if (dl1.size() > 1) {
                return [false, "Se encontraron varios registros de días laborables para la fecha " + soloFecha1.format("dd-MM-yyyy"), soloFecha1.format("yyyy")]
            }
            if (dl2.size() > 1) {
                return [false, "Se encontraron varios registros de días laborables para la fecha " + soloFecha2.format("dd-MM-yyyy"), soloFecha2.format("yyyy")]
            }
        }

    }



    def tiempoLaborableEntre(Date fecha1, Date fecha2) {
        return tiempoLaborableEntre(fecha1, fecha2, true)
    }

    def tmpoLaborableEntre(Date fcin, Date fcfn) {
//        println "procesa tiempo entre: $fcin y $fcfn"
        def prmt = Parametros.list([sort: "id"]).last()
        // si fcfn < fcin se cambia el orde para que la diferencia sea positiva
        if (fcfn < fcin) {
//            println " se cambia <<<<<<<< a >>>>>>>>"
            def fechaTemp = fcin
            fcin = fcfn
            fcfn = fechaTemp
        }


        def fchafcin = new Date()
        def fchafcfn = new Date()
        fchafcin = corrigeHora(fcin, prmt)
        fchafcfn = corrigeHora(fcfn, prmt)

        def fc01 = fechaHoy(fchafcin, prmt).time
        def fc02 = fechaHoy(fchafcfn, prmt).time

//        println "inicio: $fc01, fin: $fc02"

//        def horas = Math.round((fchafcfn.time - fchafcin.time)/(1000*3600))
        def horas = 0
        def minutos = 0

        if(fc01 > fc02) { //la hora de la fecha inicial es superior a la de fcfn
//            println ">>>> inicio: ${horaParametros(fc02, 'inicio', prmt).time}, fin: ${horaParametros(fc01, 'fin', prmt).time}"
            minutos = Math.round((horaParametros(fc01, 'fin', prmt).time.time - fc01.time)/(1000*60))
            minutos += Math.round((fc02.time - horaParametros(fc02, 'inicio', prmt).time.time)/(1000*60))
//            println "mayor: minutos: $minutos"
        } else {
            minutos = Math.round((fc02.time - fc01.time)/(1000*60))
//            println "menor: minutos: $minutos"
        }

//        println "invoca a corrigeHora con $fcin y retorna: $fchafcin, para $fcfn retorna: $fchafcfn"

//        def minutos = ((fchafcfn.time - fchafcin.time)/(1000*60)).toInteger() % 60
//        println "horas calc: ${(int) minutos/60}, minutos: ${minutos % 60}"


        horas = (int) minutos/60
        minutos = minutos % 60

        // borra la pare te horas y muntos.
        def diaIni = DiaLaborable.executeQuery("select min(ordinal) from DiaLaborable where fecha >= :f and ordinal > 0", [f: fchafcin.clone().clearTime()])[0]
        def diaFin = DiaLaborable.executeQuery("select min(ordinal) from DiaLaborable where fecha >= :f and ordinal > 0", [f: fchafcfn.clone().clearTime()])[0]
        def diaFf = DiaLaborable.findByFechaGreaterThanEqualsAndOrdinalGreaterThan(fchafcin.clone().clearTime(), 0)
//        println "dias: inicio: $diaIni, fin: $diaFin, ... $diaFf.ordinal"
        def dias = (diaFin - diaIni -1) < 0 ? 0 : diaFin - diaIni -1

//        println "dias: $dias, despues de clear time: tiempo fchafcfn: ${fchafcfn.getTime()} y fchafcin: ${fchafcin.getTime()}"
        /** todo: sacar fracion de horas que no supere las 8 de la jornada, expresando fcin como fcfn -1 (fraccion del día anterior)
         *  fcfn - 8:00 ()fraccion de d¿ia actual
         */

//        println "retorna: dias: $dias, horas: $horas, minutos: $minutos"
        return [true, [dias: dias, horas: horas, minutos: minutos]]
    }



    def validarLaborable(DiaLaborable diaLaborable, boolean noLaborables) {
//        println "**" + diaLaborable
        def mensaje = ""
        def fecha = diaLaborable.fecha
        def ordinal = diaLaborable.ordinal
        if (ordinal == 0) {
            if (noLaborables) {
                def nuevaFecha = fecha
                while (ordinal == 0) {
                    println "while1"
                    nuevaFecha++
//                    println "\t" + nuevaFecha
                    diaLaborable = DiaLaborable.findByFecha(nuevaFecha)
//                    println "\t\t" + diaLaborable
                    ordinal = diaLaborable.ordinal
                }
                mensaje += "<li>La fecha " + fecha.format("dd-MM-yyyy") + " no es un día laborable. Se utilizó " + nuevaFecha.format("dd-MM-yyyy") + "</li>"
                return [true, diaLaborable, mensaje]
            } else {
                return [false, "La fecha " + fecha.format("dd-MM-yyyy") + " no es un día laborable. Para calcular con el siguiente dia laborable pasar true como 3r parametro"]
//                    return false
            }
        } else {
            return [true, diaLaborable]
        }
    }

    /**
     * diferenciahoras
     *      retorna un objeto con el resultado de restar las horas ingresadas
     *              hace hora2 - hora1 y min2 - min1
     * @param hora1 (int)
     * @param min1 (int)
     * @param hora2 (int)
     * @param min2 (int)
     * @return objeto [horas:h, minutos:m]
     */
    def diferenciaHoras(int hora1, int min1, int hora2, int min2) {
//        println "----1 " + hora1 + ":" + min1
//        println "----2 " + hora2 + ":" + min2

        def difHoras = hora2 - hora1
        def difMins = min2 - min1
        if (difMins < 0) {
            difHoras -= 1
            difMins = 60 + difMins
        }

//        def horas1 = hora1 + (min1 / 60)
//        def horas2 = hora2 + (min2 / 60)
//        def difTiempo = horas2 - horas1
//        def difHoras = (int) difTiempo
//        def difMins = ((difTiempo - difHoras) * 60).toInteger()
//        println "----------dt " + difTiempo
//        println "----------dh " + difHoras
//        println "----------dh " + (difTiempo - difHoras)
//        println "----------dm " + difMins
        return [horas: difHoras, minutos: difMins]
    }


    // nuevo
    def corrigeHora(fcha, prmt){
//        println "corrigeHora: llega: $fcha"
        def anio = fcha.format("yyyy").toInteger()
        def mes  = fcha.format("MM").toInteger() -1
        def dias = fcha.format("dd").toInteger()

        def horaIni = horaParametros(fcha, 'inicio', prmt)
        def horaFin = horaParametros(fcha, 'fin', prmt)

        def cal = Calendar.instance
        cal.set(anio, mes, dias, fcha.format("HH").toInteger(), fcha.format("mm").toInteger(), 0)

        def fecha = Calendar.instance
        fecha.time = fcha

        if(fecha < horaIni) fecha = horaIni
        if (fecha > horaFin) fecha = horaFin

        fecha.time
    }

    def horaParametros(fcha, tipo, prmt){
//        def prmt = Parametros.list([sort: "id"]).last()
        def anio = fcha.format("yyyy").toInteger()
        def mes  = fcha.format("MM").toInteger() -1
        def dias = fcha.format("dd").toInteger()

        def hora = Calendar.instance
        if(tipo == 'inicio'){
            hora.set(anio, mes, dias, prmt.horaInicio, prmt.minutoInicio, 0)
        }
        if(tipo == 'fin'){
            hora.set(anio, mes, dias, prmt.horaFin, prmt.minutoFin, 0)
        }
        hora
    }

    def fechaHoy(fcha, prmt){
//        def prmt = Parametros.list([sort: "id"]).last()
        def hoy  = new Date()
        def anio = hoy.format("yyyy").toInteger()
        def mes  = hoy.format("MM").toInteger() -1
        def dias = hoy.format("dd").toInteger()

        def hora = Calendar.instance
        hora.set(anio, mes, dias, fcha.format("HH").toInteger(), fcha.format("mm").toInteger(), 0)
        hora
    }

    def tiempoEntre(fechaInicio, fechaFin) {

//         println("fechas " + fechaInicio)
        def cn = dbConnectionService.getConnection()
        def sql = "select * from tmpo_entre('" + fechaInicio + "', '" + fechaFin + "') "
        //println "sql " + sql
        def result
        result = cn.firstRow(sql.toString())
        return result
    }



}
