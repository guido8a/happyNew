package happy.tramites

class BusquedaExternosController {

    def index() {}

    def buscarExternos () {}

    def seguimientoExternos () {


        def tipoDocumento = TipoDocumento.findByCodigo('DEX')

        def res
        def filtrados
        if (params.memorando) {
            res = Tramite.withCriteria {
                    eq('tipoDocumento', tipoDocumento)
                    ilike('codigo', params.memorando)
            }
         if(res){

              res.each {
                  filtrados = it
              }

              def primerTramite = filtrados
              while (primerTramite.padre) {
                  primerTramite = primerTramite.padre
              }

              def html = ""

              html += "<table class='table table-bordered table-condensed'>"
              html += "<thead>"
              html += "<tr>"
              html += "<th>N. trámite</th>"
              html += "<th>Fecha</th>"
              html += "<th>De</th>"
              html += "<th>Creado por</th>"
              html += "<th>Para</th>"
              html += "<th>Prioridad</th>"
              html += "<th>Fecha límite</th>"
              html += "<th>Recepción</th>"
              html += "<th>Estado</th>"
              html += "</tr>"
              html += "</thead>"
              html += "<tbody>"
              html += creaHtmlSeguimiento(primerTramite, filtrados, "62, 100, 141")
              html += "</tbody>"
              html += "</table>"

              return [tramite: filtrados, html: html]
          }
            else{

              def html2 = ""
              html2 += "<script type='text/javascript'>"
              html2 += "log('No se encontraron resultados', 'success')"
              html2 += "</script>"

              render html2
          }

        }else {

            def html2 = ""
            html2 += "<script type='text/javascript'>"
            html2 += "log('No se encontraron resultados', 'success')"
            html2 += "</script>"

            render html2
        }
    }

    def creaHtmlSeguimiento(Tramite tramite, Tramite selected, String colorAnterior) {
        def partsColor = colorAnterior.split(",")
        def nr = partsColor[0].toInteger() + 10
        def ng = partsColor[1].toInteger() + 10
        def nb = partsColor[2].toInteger() + 10
        def nc = nr + "," + ng + "," + nb
        def html = ""
//        def hijos = Tramite.findAllByPadreAndFechaEnvioIsNotNull(tramite)
        def hijos = Tramite.findAllByPadre(tramite)
        hijos.each { h ->
            def hijos2 = Tramite.countByPadreAndFechaEnvioIsNotNull(h)
            def style = ""
            if (hijos2 > 0) {
                style = " style='background: rgb(${nc})' "
            }
            html += "<tr ${style} class='hijo ${hijos2 > 0 ? 'padre' : ''} ${h == selected ? 'current' : ''}' " +
                    "data-id='${h.id}' data-asunto='${h.asunto}' data-observaciones='${h.observaciones}'>"
            html += "<td>${h.codigo}</td>"
            html += "<td>${h.fechaEnvio ? h.fechaEnvio.format('dd-MM-yyyy HH:mm') : 'no enviado'}</td>"
            html += "<td title='${h.de.departamento.descripcion}'>${h.de.departamento.codigo}</td>"
            html += "<td title='${h.de.nombre + ' ' + h.de.apellido}'>${h.de.login}</td>"
            html += "<td title='${h.para.persona ? h.para.persona.nombre + ' ' + h.para.persona.apellido : h.para.departamento.descripcion}'>" +
                    "${h.para.persona ? h.para.persona.login : h.para.departamento.codigo}</td>"
            html += "<td>${h.prioridad.descripcion}</td>"
            html += "<td>${h.fechaMaximoRespuesta ? h.fechaMaximoRespuesta.format('dd-MM-yyyy HH:mm') : 'no recibido'}</td>"
            html += "<td>${h.para.fechaRecepcion ? h.para.fechaRecepcion.format('dd-MM-yyyy HH:mm') : 'no recibido'}</td>"
            html += "<td>${h.estadoTramite.descripcion}</td>"
            html += "</tr>"
            creaHtmlSeguimiento(h, selected, nc)
        }
        return html
    }

}
