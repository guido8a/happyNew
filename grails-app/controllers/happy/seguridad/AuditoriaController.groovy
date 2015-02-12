package happy.seguridad

import happy.tramites.DocumentoTramite
import happy.tramites.ObservacionTramite
import happy.tramites.OrigenTramite
import happy.tramites.PersonaDocumentoTramite
import happy.tramites.RolPersonaTramite
import happy.tramites.Tramite
import kerberos.Krbs

//import kerberos.Krbs

class AuditoriaController extends Shield {

    def audt() {
        if (session.usuario.puedeAdmin) {
            def operaciones = ["UPDATE": "Actualización", "INSERT": "Inserción", "DELETE": "Eliminación"]
            def domains = grailsApplication.getArtefacts("Domain")*.fullName
            def comboDomain = [:]
            domains = domains.sort { it.split("\\.")[(it.split("\\.").size() - 1)] }
            domains.each {
                if (!(it =~ "kerberos") && !(it =~ "ErrorLog") && !(it =~ "Sistema")) {
                    def texto = it.split("\\.")
                    comboDomain.put(it, texto[2])
                }
            }
            return [operaciones: operaciones, domains: comboDomain]
        } else {
            flash.message = "Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será registrada."
            response.sendError(403)
        }
    }

    def tablaAudt() {
        println params
        def operaciones = ["UPDATE": "Actualización", "INSERT": "Inserción", "DELETE": "Eliminación"]
        def desde = new Date().parse("dd-MM-yyyy HH:mm:ss", params.desde + " 00:00:01")
        def hasta = new Date().parse("dd-MM-yyyy HH:mm:ss", params.hasta + " 23:59:59")
        def dominio = null
        def max = 100
        def show = 20
        def offset = 0
        def resFin
        def maxView = 0
        if (params.maxView && params.maxView != "") {
            maxView = params.maxView.toInteger()
        }
        if (params.offset && params.offset != "") {
            offset = params.offset.toInteger()
        }
        if (params.domain != "-1") {
            dominio = params.domain.split("\\.")[(params.domain.split("\\.").size() - 1)]
        }
        def res = []
        if (params.tramite) {
            def tramites = Tramite.findAllByCodigoIlike("%" + params.tramite.trim() + "%")
            def tramId = tramites.id*.toInteger()
            def pdtIds = PersonaDocumentoTramite.findAllByTramiteInList(tramites).id*.toInteger()
            def docsIds = DocumentoTramite.findAllByTramiteInList(tramites).id*.toInteger()
            def obsIds = ObservacionTramite.findAllByTramiteInList(tramites).id*.toInteger()

            def dominios = ["class happy.tramites.Tramite"         : tramId, "class happy.tramites.PersonaDocumentoTramite": pdtIds,
                            "class happy.tramites.DocumentoTramite": docsIds, "class happy.tramites.ObservacionTramite": obsIds]

            dominios.each { dom, ids ->
//                println "DOM: " + dom
//                println "\tIDS: " + ids
                def c = Krbs.createCriteria()
                res += c.list(max: max + offset, offset: offset) {
                    between("fecha", desde, hasta)
                    or {
                        neProperty("old_value", "new_value")
                        isNull("new_value")
                    }
                    if (params.operacion != '-1') {
                        eq("operacion", params.operacion)
                    }
                    if (params.usuario && params.usuario != "") {
                        ilike("usuario", "%" + params.usuario + "%")
                    }
                    inList("dominio", dom)
                    if (ids.size() > 0) {
                        inList("registro", ids)
                    }
//                    order("fecha", "desc")
                }
            }
            res = res.sort { it.fecha }
            res = res.reverse()
        } else {
            def c = Krbs.createCriteria()
            res = c.list(max: max + offset, offset: offset) {
                between("fecha", desde, hasta)
                or {
                    neProperty("old_value", "new_value")
                    isNull("new_value")
                }
                if (params.operacion != '-1') {
                    eq("operacion", params.operacion)
                }
                if (params.usuario && params.usuario != "") {
                    ilike("usuario", "%" + params.usuario + "%")
                }
                if (dominio) {
                    eq("dominio", "class " + params.domain)
                }
                order("fecha", "desc")
            }
        }


        if (maxView == 0)
            maxView = res.size()
        else {
            if (res.size() > 80)
                maxView += res.size() - maxView
        }
        if (res.size() > show)
            resFin = res[1..show]
        else
            resFin = res
        def rango = maxView / show
        rango = Math.ceil(rango)
        if (rango < 1) {
            rango = 1
        }
        rango = 1..rango.toInteger()
        def heigth = 35
        if (rango.size() > 28)
            heigth = (Math.ceil(rango.size() / 28)) * 36


        return [res  : resFin, dominio: dominio, maxView: maxView, show: show, offset: offset, desde: params.desde,
                hasta: params.hasta, usuario: params.usuario, domain: params.domain, operacion: params.operacion,
                rango: rango, heigth: heigth, operaciones: operaciones]
    }

    def audt_old() {
        if (session.usuario.puedeAdmin) {
            def operaciones = ["UPDATE": "Update", "INSERT": "Insert", "DELETE": "Delete", "-1": "Todas"]
            def domains = grailsApplication.getArtefacts("Domain")*.fullName
            def comboDomain = [:]
            domains = domains.sort { it.split("\\.")[(it.split("\\.").size() - 1)] }
            comboDomain.put("-1", "Todos")
            domains.each {
                if (!(it =~ "kerberos") && !(it =~ "ErrorLog") && !(it =~ "Sistema")) {
                    def texto = it.split("\\.")
                    comboDomain.put(it, texto[2])
                }
            }
            [operaciones: operaciones, domains: comboDomain]
        } else {
            flash.message = "Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será registrada."
            response.sendError(403)
        }
    }

    def tablaAudt_old() {
        def desde = new Date().parse("dd-MM-yyyy HH:mm:ss", params.desde + " 00:00:01")
        def hasta = new Date().parse("dd-MM-yyyy HH:mm:ss", params.hasta + " 23:59:59")
        def dominio = null
        def max = 100
        def show = 20
        def offset = 0
        def resFin
        def maxView = 0
        if (params.maxView && params.maxView != "")
            maxView = params.maxView.toInteger()
        if (params.offset && params.offset != "")
            offset = params.offset.toInteger()
        if (params.domain != "-1") {
            dominio = params.domain.split("\\.")[(params.domain.split("\\.").size() - 1)]
        }

        def c = Krbs.createCriteria()
        def res = c.list(max: max + offset, offset: offset) {
            between("fecha", desde, hasta)
            if (params.operacion != '-1')
                eq("operacion", params.operacion)
            if (params.usuario && params.usuario != "")
                ilike("usuario", "%" + params.usuario + "%")
            if (dominio)
                eq("dominio", "class " + params.domain)
            order("fecha", "desc")
        }
        if (maxView == 0)
            maxView = res.size()
        else {
            if (res.size() > 80)
                maxView += res.size() - maxView
        }
        if (res.size() > show)
            resFin = res[1..show]
        else
            resFin = res
        def rango = maxView / show
        rango = Math.ceil(rango)
        rango = 1..rango.toInteger()
        def heigth = 35
        if (rango.size() > 28)
            heigth = (Math.ceil(rango.size() / 28)) * 36

        [res: resFin, dominio: dominio, maxView: maxView, show: show, offset: offset, desde: params.desde, hasta: params.hasta, usuario: params.usuario, domain: params.domain, operacion: params.operacion, rango: rango, heigth: heigth]


    }

}
