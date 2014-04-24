<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 3/12/14
  Time: 1:18 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Seguimiento del trámite</title>
        <style>
        .current {
            background : #FFAB1A;
        }

        #detalle tr {
            cursor : pointer;
        }

        #detalle tr:hover, .selected {
            background : #EFDFC2;
        }
        </style>
    </head>

    <body>
        <div class="btn-toolbar toolbar">
            <div class="btn-group">
                <g:if test="${params.pers == '1'}">
                    <g:link action="bandejaEntrada" controller="tramite" class="btn btn-primary">
                        <i class="fa fa-list"></i> Bandeja de entrada
                    </g:link>
                </g:if>
                <g:else>
                    <g:link action="bandejaEntradaDpto" controller="tramite3" class="btn btn-primary">
                        <i class="fa fa-list"></i> Bandeja de entrada
                    </g:link>
                </g:else>
                <g:link action="bandejaSalida" controller="tramite2" class="btn btn-primary">
                    <i class="fa fa-list"></i> Bandeja de salida
                </g:link>
                <g:if test="${params.prev == 'crearTramite'}">
                %{--<util:renderHTML html="${g.link(controller: params.controller, action: params.action, params: [padre:params.padre], class: '') {--}%
                %{--params.lbl--}%
                %{--}}"/>--}%
                    <g:link controller="tramite" action="crearTramite" params="[padre: selected.id]" class="btn btn-primary">
                        Ir a crear trámite
                    </g:link>

                </g:if>
            </div>
        </div>


        <div style="margin-top: 30px;padding-bottom: 10px" class="vertical-container">
            <p class="css-vertical-text">Doc. Principal</p>

            <div class="linea"></div>

            <div class="row"><div class="col-xs-3 negrilla">${tramite.codigo} </div></div>

            <div class="row">
                <div class="col-xs-1 negrilla">
                    Asunto:
                </div>

                <div class="col-xs-11 text-primary" style="padding: 0">
                    ${tramite.asunto}
                </div>
            </div>

            <div class="row">
                <div class="col-xs-1 negrilla">
                    De:
                </div>

                <div class="col-xs-11" style="padding: 0">
                    <span class="text-primary">
                        ${"" + tramite.de.departamento.codigo + ": " + tramite.de}
                    </span>,
                creado el
                    <span class="text-primary">
                        ${tramite.fechaCreacion?.format('dd-MM-yyyy HH:mm')}
                    </span>,
                enviado el
                    <span class="text-primary">
                        ${tramite.fechaEnvio?.format('dd-MM-yyyy HH:mm')}
                    </span>
                </div>
            </div>

            <div class="row">
                <div class="col-xs-1 negrilla">
                    Para:
                </div>

                <div class="col-xs-11 text-primary" style="padding-left: 0">
                    ${tramite.para?.persona ? tramite.para?.persona?.nombre + " " + tramite.para?.persona?.apellido : tramite.para?.departamento?.descripcion}
                    <g:if test="${tramite.para?.fechaRecepcion}">
                        <span class="text-success">
                            (recibido el ${tramite.para?.fechaRecepcion.format("dd-MM-yyyy HH:mm")})
                        </span>
                    </g:if>
                    <g:else>
                        <span class="text-danger">
                            (no recibido)
                        </span>
                    </g:else>
                </div>
            </div>

            <g:if test="${tramite.copias.size() > 0}">
                <div class="row">
                    <div class="col-xs-1  negrilla">
                        CC:
                    </div>

                    <div class="col-xs-8 text-primary" style="padding: 0">
                        <g:each in="${tramite.copias}" var="c" status="i">
                            ${(c.persona ? c.persona.nombre + " " + c.persona.apellido : c.departamento.descripcion)}
                            <g:if test="${c.fechaRecepcion}">
                                <span class="text-success">
                                    (recibido el ${c.fechaRecepcion.format("dd-MM-yyyy HH:mm")})
                                </span>
                            </g:if>
                            <g:else>
                                <span class="text-danger">
                                    (no recibido)
                                </span>
                            </g:else>
                            <g:if test="${i < tramite.copias.size() - 1}">
                                ,
                            </g:if>
                        </g:each>
                    </div>
                </div>
            </g:if>

            <g:if test="${tramite.observaciones}">
                <div class="row">
                    <div class="col-xs-1 negrilla" title="Observaciones">
                        Obs.:
                    </div>

                    <div class="col-xs-11 text-primary" style="padding: 0">
                        ${tramite.observaciones}
                    </div>
                </div>
            </g:if>
        </div>

        <div style="margin-top: 0;" class="vertical-container">
            <p class="css-vertical-text">Seguimiento</p>

            <div class="linea"></div>

            <div id="detalle" style="width: 95%;height: 300px;overflow: auto;margin-left:18px ;margin-top: 20px;margin-bottom: 20px;border: 1px solid #000000">
                <util:renderHTML html="${html}"/>
            </div>

            <div id="info" class="ui-helper-hidden">
                <div class="row">
                    <div class="col-xs-1 negrilla">
                        Asunto:
                    </div>

                    <div class="col-xs-11 text-primary" style="padding: 0" id="divAsunto">
                    </div>
                </div>

                <div class="row">
                    <div class="col-xs-1 negrilla">
                        Observaciones:
                    </div>

                    <div class="col-xs-11 text-primary" style="padding: 0" id="divObservaciones">
                    </div>
                </div>
            </div>
        </div>

        <script type="text/javascript">
            $(function () {
                $("#detalle").find("tr").click(function () {
                    $(".selected").removeClass("selected");
                    $(this).addClass("selected");
                    $("#divAsunto").html($(this).data("asunto"));
                    $("#divObservaciones").html($(this).data("observaciones"));
                    $("#info").show();
                });
            });
        </script>
    </body>
</html>