package happy.tramites

import happy.seguridad.Persona
import happy.seguridad.Shield

class BusquedaExternosController extends Shield {

    def index() {}

    def buscarExternos() {
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
            if (para.persona) {
                def rolRecibe = RolPersonaTramite.findByCodigo("E003")
                def recibio = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(tram, rolRecibe)
                if (recibio.size() >= 1) {
                    recibio = recibio.last()
                    prsnPara = recibio.last().persona
                }else{
                    def triangulo = para.departamento.triangulos.first()
                    prsnPara = triangulo
                }

            } else {
                def rolRecibe = RolPersonaTramite.findByCodigo("E003")
                def recibio = PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramite(tram, rolRecibe)
                if (recibio.size() >= 1) {
                    recibio = recibio.last()
                    prsnPara = recibio.persona
                } else {
                    def triangulo = para.departamento.triangulos.first()
                    prsnPara = triangulo
                }
            }
            strPara = /* (prsnPara.titulo ? prsnPara.titulo + " " : "") +*/   (prsnPara.nombre + " " + prsnPara.apellido)
            def dptoPadre = prsnPara.departamento.padre ?: prsnPara.departamento
            def directores = [], dptoDirector
            Persona.findAllByDepartamento(prsnPara.departamento).each {p->
                if(p.estaActivo && p.puedeDirector) {
                    directores+=(p.nombre+" "+p.apellido)
                    dptoDirector = prsnPara.departamento
                }
            }
            if(directores.size() == 0) {
                Persona.findAllByDepartamento(dptoPadre).each {p->
                    if(p.estaActivo && p.puedeDirector) {
                        directores+=(p.nombre+" "+p.apellido)
                        dptoDirector = dptoPadre
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
                msg += "se encuentra en manos del funcionario: <strong><em>${strPara}</em></strong></p>"
                msg += "<p>Quien labora en: <strong><em>${prsnPara.departamento.descripcion ?: ''}</em></strong></p>"
                msg += "<p>Teléfono: <strong><em>${prsnPara.departamento.telefono ?: ' '}</em></strong></p>"
                msg += "<p>Ubicación: <strong><em>${prsnPara.departamento.direccion ?: ''}</em></strong></p>"
                msg += "<hr style='border-color:#999 !important;'/>"
                if(directores){
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
