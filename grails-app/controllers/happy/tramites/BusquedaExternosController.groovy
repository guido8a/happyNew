package happy.tramites

import happy.seguridad.Persona

class BusquedaExternosController {

    def index() {}

    def buscarExternos() {
    }

    def tablaBusquedaExternos() {
//        println("params:" + params)
        def res
        def filtrados = []
        if (!params.contacto && !params.numero && !params.codigo && !params.institucion) {
            res = []
        } else {
            res = Tramite.withCriteria {
                if (params.contacto) {
                    ilike('contacto', '%' + params.contacto.trim() + '%')
                }
                if (params.codigo) {
                    ilike('codigo', params.codigo.trim())
                }
                if (params.numero) {
                    ilike('numeroDocExterno', '%' + params.numero.trim() + '%')
                }
                if (params.institucion) {
                    ilike('paraExterno', '%' + params.institucion.trim() + '%')
                }

            }
        }
//            println("res:" + res)
        if (res) {
            res.each {
//                    println("externo:" + it.externo)
                if (it?.externo == '1') {
                    filtrados += it
                }

            }
        }
//        println("filtrados:" + filtrados)
        if (filtrados.size() >= 1) {
            Tramite externo = filtrados.first()

            def principal = externo
            while (principal.padre) {
                principal = principal.padre
            }

            def todos = [principal] + todosHijos(principal)
//            println todos.codigo
//            println todos.fechaCreacion*.format("dd-MM-yyyy HH:mm")
            todos = todos.sort { it.fechaCreacion }
//            println todos.codigo
//            println todos.fechaCreacion*.format("dd-MM-yyyy HH:mm")

            def tram = todos.last()
//            def todosHijos = Tramite.findAllByPadre(principal, [sort: "fechaCreacion", order: "desc"])
//            println todosHijos.codigo
//            def tram = ultimoHijo(hijoNuevo)
//            def tram = ultimoHijo(externo)
//            println "TRAM: " + tram.codigo

            def prsnPara, strPara, strJefe = "- Sin jefe asignado -", strDirector = " "
            def para = tram.para
            if (para.persona) {
//                prsnPara = para.persona
//                println("-" + para.persona)
                def rolRecibe = RolPersonaTramite.findByCodigo("E003")
                def recibio = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(tram, rolRecibe)
//                println("rec " + recibio)
                if (recibio.size() >= 1) {
//                    recibio = recibio.first()
                    recibio = recibio.last()
//                    println("--->" + recibio.last().persona)
                    prsnPara = recibio.last().persona
                }else{
                    def triangulo = para.departamento.triangulos.first()
                    prsnPara = triangulo
                }

            } else {
                def rolRecibe = RolPersonaTramite.findByCodigo("E003")
                def recibio = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(tram, rolRecibe)
//                println("rec " + recibio)
                if (recibio.size() >= 1) {
//                    recibio = recibio.first()
                    recibio = recibio.last()
//                    println("--->" + recibio.last().persona)

//                    strPara = (recibio.persona.titulo ? recibio.persona.titulo + " " : "") +
//                            (recibio.persona.nombre + " " + recibio.persona.apellido)
                    prsnPara = recibio.persona
                } else {
                    def triangulo = para.departamento.triangulos.first()
//                    strPara = (triangulo.titulo ? triangulo.titulo + " " : "") +
//                            (triangulo.nombre + " " + triangulo.apellido)
                    prsnPara = triangulo
                }
            }
            strPara = /* (prsnPara.titulo ? prsnPara.titulo + " " : "") +*/   (prsnPara.nombre + " " + prsnPara.apellido)
            def jefe = prsnPara.jefePersona2
            println("jefes " + jefe)
            def jefes = []
            if (jefe.size >= 1) {
                jefe.each{j->
                    jefes += (j.nombre + " " + j.apellido)
                }
//                strJefe = /*(jefe.titulo ? jefe.titulo + " " : "") + */ (jefe.nombre + " " + jefe.apellido)
            }

            def dptoPadre = prsnPara.departamento.padre ?: prsnPara.departamento
            def director = Persona.withCriteria {
                eq("departamento", dptoPadre)
                eq("jefe", 1)
            }
            if (director) {
                strDirector = /*(director.titulo ? director.titulo + " " : "") + */ (director.nombre + " " + director.apellido)
            }
            def msg = "<div class='well well-lg text-left'>"
            msg += "<h4>Trámite ${externo.codigo}</h4>"
            if (tram.para.estado?.codigo == "E005") { //Archivado
                msg += "<p>El estado de su trámite es: <strong><em>ARCHIVADO</em></strong></p>"
            } else {
                msg += "<p>El estado de su trámite es: <strong><em>${tram.estadoTramiteExterno?.descripcion ?: ''}</em></strong></p>"
            }
            if (tram.tipoDocumento.codigo == "OFI") {
                msg += "Contestación enviada con trámite externo <strong><em>${tram.codigo}</em></strong> el " +
                        "<strong><em>${tram.fechaEnvio.format('dd-MMM-yyyy HH:mm')}</em></strong> para " +
                        "<strong><em>${tram.paraExterno}</em></strong>."
            } else {
                msg += "<p>Con documento: <strong><em>${tram.codigo}</em></strong> "
                msg += "desde el <strong><em>${tram.fechaEnvio.format('dd-MMM-yyyy HH:mm')}</em></strong> "
                msg += "se encuentra en manos del funcionario: <strong><em>${strPara}</em></strong></p>"
                msg += "<p>Quien labora en: <strong><em>${prsnPara.departamento.descripcion}</em></strong></p>"
                msg += "<p>Teléfono: <strong><em>${prsnPara.departamento.telefono}</em></strong></p>"
//                msg += "<p>Jefe inmediato superior: <strong><em>${strJefe}</em></strong></p>"

                msg += "<p>Jefe inmediato superior: <strong><em>${jefes.join(', ')}</em></strong></p>"

                msg += "<p>Nombre del director: <strong><em>${strDirector} (${dptoPadre.descripcion})</em></strong></p>"
            }
            msg += "</div>"
            render msg
        } else {
//            filtrados = null
            render "<div class=\"alert alert-info\">\n" +
                    "<p class='lead'>El código de trámite ingresado no existe en el sistema.</p>" +
//                    "<i class='icon-ghost fa-3x pull-left text-shadow'></i>" +
//                    "        <p class='lead'>No se encontró ningún trámite que coincida con los parámetros ingresados</p>\n" +
                    "    </div>"
        }
//        return [tramite: filtrados]
    }

    def todosHijos(Tramite tramite) {
//        println "*" + tramite.codigo
//        def arreglo = [tramite]
//        def todos = Tramite.findAllByPadre(tramite, [sort: "fechaCreacion", order: "desc"])

        def enviado = EstadoTramite.findByCodigo("E003")
        def recibido = EstadoTramite.findByCodigo("E004")
        def archivado = EstadoTramite.findByCodigo("E005")
        def estadosOk = [enviado, recibido, archivado]
        def arreglo = []
        Tramite.findAllByAQuienContestaAndFechaEnvioIsNotNull(tramite.para, [sort: "fechaCreacion", order: "asc"]).each { tr ->
            if(tr.para){
                if (estadosOk.contains(tr.para.estado)) {
                    arreglo += tr
                    arreglo += todosHijos(tr)
                }
            }

        }
        return arreglo
    }

    def ultimoHijo(Tramite tramite) {
//        println tramite.codigo + "\t" + tramite.fechaCreacion
        def ret = tramite
        Tramite.findAllByAQuienContestaAndFechaEnvioIsNotNull(tramite.para).each { tr ->
//            println "\t" + tr.codigo + "\t" + tr.fechaCreacion
            ret = ultimoHijo(tr)
            if (!ret) {
                ret = tr
            }
        }
//        println "RET=" + ret.codigo + "\t" + ret.fechaCreacion
        return ret
    }

    def ultimoHijo_old(Tramite tramite) {
//        println tramite.codigo
        def ret = tramite
        Tramite.findAllByAQuienContestaAndFechaEnvioIsNotNull(tramite.para).each { tr ->
//            println "\t" + tr.codigo
            ret = ultimoHijo_old(tr)
            if (!ret) {
                ret = tr
            }
        }
//        println "RET=" + ret.codigo
        return ret
    }

}
