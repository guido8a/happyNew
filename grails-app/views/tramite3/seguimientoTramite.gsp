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
        </style>
    </head>

    <body>
        <div class="btn-toolbar toolbar">
            <div class="btn-group">
                <g:link action="bandejaEntrada" controller="tramite" class="btn btn-primary">
                    <i class="fa fa-list"></i> Bandeja de entrada
                </g:link>
            </div>
        </div>


        <div style="margin-top: 30px;padding-bottom: 10px" class="vertical-container">
            <p class="css-vertical-text">Trámite principal</p>

            <div class="linea"></div>

            <h3>${tramite.tipoDocumento?.descripcion} ${tramite.codigo} (prioridad: ${tramite.prioridad?.descripcion})</h3>

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
                    ${tramite.para.persona ? tramite.para.persona.nombre + " " + tramite.para.persona.apellido : tramite.para.departamento.descripcion}
                    <g:if test="${tramite.para.fechaRecepcion}">
                        <span class="text-success">
                            (recibido el ${tramite.para.fechaRecepcion.format("dd-MM-yyyy HH:mm")})
                        </span>
                    </g:if>
                    <g:else>
                        <span class="text-danger">
                            (no recibido)
                        </span>
                    </g:else>
                </div>
            </div>

            <div class="row">
                <g:if test="${tramite.copias.size() > 0}">
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
                </g:if>
            </div>

            <g:if test="${tramite.observaciones}">
                <div class="row">
                    <div class="col-xs-1 negrilla">
                        Observaciones:
                    </div>

                    <div class="col-xs-11 text-primary" style="padding: 0">
                        ${tramite.observaciones}
                    </div>
                </div>
            </g:if>

            <div class="row">
                <div class="col-xs-1 negrilla">
                    Asunto:
                </div>

                <div class="col-xs-11 text-primary" style="padding: 0">
                    ${tramite.asunto}
                </div>
            </div>
        </div>

        <div style="margin-top: 30px;" class="vertical-container">
            <p class="css-vertical-text">Seguimiento</p>

            <div class="linea"></div>

            <div id="detalle" style="width: 95%;height: 400px;overflow: auto;margin-left:18px ;margin-top: 20px;margin-bottom: 20px;border: 1px solid #000000">
                <util:renderHTML html="${html}"/>
            </div>
        </div>
    </body>
</html>