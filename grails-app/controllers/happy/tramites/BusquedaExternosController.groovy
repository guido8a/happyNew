package happy.tramites

import happy.seguridad.Persona

class BusquedaExternosController {

    def index() {}

    def buscarExternos() {
    }

    def tablaBusquedaExternos() {
        println("params:" + params)
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
            filtrados = filtrados.first()

            def tram = ultimoHijo(filtrados)

            def prsnPara, strPara, strJefe = "- Sin jefe asignado -", strDirector = "- Sin director asignado -"
            def para = tram.para
            if (para.persona) {
//                strPara = (para.persona.titulo ? para.persona.titulo + " " : "") +
//                        (para.persona.nombre + " " + para.persona.apellido)
                prsnPara = para.persona
            } else {
                def rolRecibe = RolPersonaTramite.findByCodigo("E003")
                def recibio = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(tram, rolRecibe)
                if (recibio.size() >= 1) {
                    recibio = recibio.first()
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
            strPara = (prsnPara.titulo ? prsnPara.titulo + " " : "") +
                    (prsnPara.nombre + " " + prsnPara.apellido)
            def jefe = prsnPara.jefePersona
            if (jefe) {
                strJefe = (jefe.titulo ? jefe.titulo + " " : "") +
                        (jefe.nombre + " " + jefe.apellido)
            }
            def dptoPadre = prsnPara.departamento.padre ?: prsnPara.departamento
            def director = Persona.withCriteria {
                eq("departamento", dptoPadre)
                eq("jefe", 1)
            }
            if (director) {
                strDirector = (director.titulo ? director.titulo + " " : "") +
                        (director.nombre + " " + director.apellido)
            }
            def msg = "<div class='well well-lg text-left'>"
            msg+="<h4>Trámite ${filtrados.codigo}</h4>"
            msg += "<p>El estado de su trámite es: <strong><em>${tram.estadoTramiteExterno?.descripcion}</em></strong></p>"
            msg += "<p>El documento se encuentra en manos del funcionario: <strong><em>${strPara}</em></strong></p>"
            msg += "<p>Quien labora en el departamento: <strong><em>${prsnPara.departamento.descripcion}</em></strong></p>"
            msg += "<p>Teléfono: <strong><em>${prsnPara.departamento.telefono}</em></strong></p>"
            msg += "<p>Jefe inmediato superior: <strong><em>${strJefe}</em></strong></p>"
            msg += "<p>Nombre del director: <strong><em>${strDirector} (dpto: ${dptoPadre.descripcion})</em></strong></p>"
            msg += "</div>"
            render msg
        } else {
//            filtrados = null
            render "<div class=\"alert alert-info\">\n" +
                    "<i class='icon-ghost fa-3x pull-left text-shadow'></i>" +
                    "        <p class='lead'>No se encontró ningún trámite que coincida con los parámetros ingresados</p>\n" +
                    "    </div>"
        }
//        return [tramite: filtrados]
    }

    def ultimoHijo(Tramite tramite) {
//        println tramite
        def ret = tramite
        Tramite.findAllByAQuienContesta(tramite.para).each { tr ->
            ret = tr
            ultimoHijo(tr)
        }
        return ret
    }

}
