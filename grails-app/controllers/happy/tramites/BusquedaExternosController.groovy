package happy.tramites

import happy.seguridad.Persona
import happy.seguridad.Shield
import happy.utilitarios.Parametros

class BusquedaExternosController {

    def index() {}

    def buscarExternos() {
        def parametros = Parametros.list().last()

        return [parametros : parametros]

    }

    def tablaBusquedaExternos() {
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
        if (res) {
            res.each {
                if (it?.externo == '1') {
                    filtrados += it
                }
            }
        }
        if (filtrados.size() >= 1) {
            Tramite externo = filtrados.first()
            def principal = externo
            while (principal.padre) {
                principal = principal.padre
            }

            def todos = [principal] + todosHijos(principal)
            todos = todos.sort { it.fechaCreacion }
            def tram = todos.last()
            def prsnPara, strPara, strJefe = "- Sin jefe asignado -", strDirector = " - Sin director asignado - "
            def para = tram.para
            def recibido = false
            if (para.persona) {
                def rolRecibe = RolPersonaTramite.findByCodigo("E003")
                def recibio = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(tram, rolRecibe)
                if (recibio.size() >= 1) {
                    recibio = recibio.last()
                    prsnPara = recibio.persona
                    recibido = true
                } else {
//                    def triangulo = para?.departamento?.triangulos?.first()
//                    prsnPara = triangulo
                    prsnPara = para.persona
                }
            } else {
                def rolRecibe = RolPersonaTramite.findByCodigo("E003")
                def recibio = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(tram, rolRecibe)
                if (recibio.size() >= 1) {
                    recibio = recibio.last()
                    prsnPara = recibio.persona
                    recibido = true
                } else {
                    def triangulo = para?.departamento?.triangulos?.first()
                    prsnPara = triangulo
                }
            }
            strPara = /* (prsnPara.titulo ? prsnPara.titulo + " " : "") +*/ (prsnPara?.nombre + " " + prsnPara?.apellido)
//            strPara += recibido ? " - Recibido " : " - No recibido"

            def dptoPadre = prsnPara?.departamento?.padre ?: prsnPara?.departamento
            if (dptoPadre.codigo == "PRF") {
                dptoPadre = prsnPara?.departamento
            }
            def directores = [], dptoDirector
            Persona.findAllByDepartamento(prsnPara?.departamento).each { p ->
                if (p.estaActivo && p.puedeDirector) {
                    directores += (p.nombre + " " + p.apellido)
                    dptoDirector = prsnPara.departamento
                }
            }
            if (directores.size() == 0) {
                Persona.findAllByDepartamento(dptoPadre).each { p ->
                    if (p.estaActivo && p.puedeDirector) {
                        directores += (p.nombre + " " + p.apellido)
                        dptoDirector = dptoPadre
                    }
                }
            }

            def departamento = prsnPara?.departamento

            def jefes = []
            Persona.findAllByDepartamento(prsnPara?.departamento).each { p ->
                if (p.estaActivo && p.puedeJefe) {
                    jefes += (p.nombre + " " + p.apellido)
                }
            }
            if(jefes.size == 0){
                Persona.findAllByDepartamento(prsnPara?.departamento).each { p ->
                    if (p.estaActivo && p.puedeDirector) {
                        jefes += (p.nombre + " " + p.apellido)
                    }
                }
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
                msg += "está bajo la responsabilidad de: <strong><em>${strPara}</em></strong></p>"
                msg += "<p>Quien labora en: <strong><em>${prsnPara.departamento.descripcion ?: ''}</em></strong></p>"
                msg += "<p>Jefe de la Oficina: <strong><em>${ jefes.join(', ')}</em></strong></p>"
                msg += "<p>Teléfono: <strong><em>${prsnPara.departamento.telefono ?: ' '}</em></strong></p>"
                msg += "<p>Ubicación: <strong><em>${prsnPara.departamento.direccion ?: ''}</em></strong></p>"
                msg += "<hr style='border-color:#999 !important;'/>"
                if (directores.size() > 0) {
                    msg += "<p>Nombre del director: <strong><em>${directores?.join(', ')}</em></strong></p>"
                    msg += "<p>Departamento director: <strong><em>${dptoDirector?.descripcion ?: ''}</em></strong></p>"
                    msg += "<p>Teléfono: <strong><em>${dptoDirector?.telefono ?: ''}</em></strong></p>"
                    msg += "<p>Ubicación: <strong><em>${dptoDirector?.direccion ?: ''}</em></strong></p>"
                }
            }

            msg += "</div>"
            render msg
        } else {
            render "<div class=\"alert alert-info\">\n" +
                    "<p class='lead'>El código de trámite ingresado no existe en el sistema.</p>" +
                    "    </div>"
        }
    }

    def todosHijos(Tramite tramite) {

        def enviado = EstadoTramite.findByCodigo("E003")
        def recibido = EstadoTramite.findByCodigo("E004")
        def archivado = EstadoTramite.findByCodigo("E005")
        def estadosOk = [enviado, recibido, archivado]
        def arreglo = []
        Tramite.findAllByAQuienContestaAndFechaEnvioIsNotNull(tramite.para, [sort: "fechaCreacion", order: "asc"]).each { tr ->
            if (tr.para) {
                if (estadosOk.contains(tr.para.estado)) {
                    arreglo += tr
                    arreglo += todosHijos(tr)
                }
            }

        }
        return arreglo
    }

    def ultimoHijo(Tramite tramite) {
        def ret = tramite
        Tramite.findAllByAQuienContestaAndFechaEnvioIsNotNull(tramite.para).each { tr ->
            ret = ultimoHijo(tr)
            if (!ret) {
                ret = tr
            }
        }
        return ret
    }

    def ultimoHijo_old(Tramite tramite) {
        def ret = tramite
        Tramite.findAllByAQuienContestaAndFechaEnvioIsNotNull(tramite.para).each { tr ->
            ret = ultimoHijo_old(tr)
            if (!ret) {
                ret = tr
            }
        }
        return ret
    }

}
