<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 14/07/14
  Time: 11:18 AM
--%>

<%@ page import="happy.seguridad.Persona; happy.tramites.DocumentoTramite" contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Redireccionar trámites de la bandeja de entrada personal de ${persona.login}</title>
        <style type="text/css">
        td {
            vertical-align : middle !important;
        }

        th {
            text-align     : center;
            vertical-align : middle !important;
        }

        select.loading {
            background : #aaa !important;
        }

        tr.loading td {
            background : #bbb;;
        }

        .table-hover > tbody > tr.loading:hover > td,
        .table-hover > tbody > tr.loading:hover > th {
            background-color : #ccc;
        }

        .select {
            width : 275px;
        }
        </style>
    </head>

    <body>
        <div class="btn-toolbar toolbar" style="margin-top: 10px !important">
            <div class="btn-group">
                <a href="javascript: history.go(-1)" class="btn btn-primary regresar">
                    <i class="fa fa-arrow-left"></i> Regresar
                </a>
            </div>
        </div>

        <h3>Redireccionar trámites de la bandeja de entrada personal de ${persona.login}</h3>

        <div class="alert alert-info">
            <p>
                Para cada trámite que desea redireccionar, seleccione el nuevo destino y presione
                el botón Enviar (<a href="#" class="btn btn-xs btn-success" title="Enviar">
                <i class="fa fa-plane"></i>&nbsp;
            </a>)
            </p>
        </div>

        <table class="table table-bordered table-condensed table-hover">
            <thead>
                <tr>
                    <th>&nbsp;</th>
                    <th>Trámite</th>
                    <th>Fecha envío</th>
                    <th>Fecha recepción</th>
                    <th>De</th>
                    <th>Creado por</th>
                    <th>Fecha límite</th>
                    <th>Rol</th>
                    <th>Estado</th>
                    <th>Nuevo destino</th>
                    <th>Enviar</th>
                </tr>
            </thead>
            <tbody>
                <g:each in="${tramites}" var="tr" status="i">
                    <g:set var="now" value="${new java.util.Date()}"/>

                    <g:set var="estado" value="Por recibir"/>

                    <g:if test="${tr.fechaRecepcion}">
                        <g:if test="${tr.fechaLimiteRespuesta < now}">
                            <g:set var="estado" value="Retrasado"/>
                            <g:if test="${happy.tramites.Tramite.countByAQuienContesta(tr) > 0}">
                                <g:set var="estado" value="Recibido"/>
                            </g:if>
                        </g:if>
                        <g:else>
                            <g:set var="estado" value="Recibido"/>
                        </g:else>
                    </g:if>
                    <g:else>
                        <g:if test="${tr.fechaBloqueo < now}">
                            <g:set var="estado" value="Sin recepción"/>
                        </g:if>
                        <g:else>
                            <g:set var="estado" value="Por recibir"/>
                        </g:else>
                    </g:else>

                    <tr>
                        <td class="text-center">${i + 1}</td>
                        <td class="text-center">
                            ${tr.tramite.codigo}
                            <g:if test="${tr?.tramite?.anexo == 1 && DocumentoTramite.countByTramite(tr.tramite) > 0}">
                                <i class="fa fa-paperclip fa-fw" style="margin-left: 10px"></i>
                            </g:if>
                        </td>
                        <td class="text-center">${tr.fechaEnvio?.format("dd-MM-yyyy HH:mm")}</td>
                        <td class="text-center">${tr.fechaRecepcion?.format("dd-MM-yyyy HH:mm")}</td>
                        <td class="text-center">${tr.tramite.de?.departamento?.codigo}</td>
                        <td class="text-center">
                            <g:if test="${tr.tramite.deDepartamento}">
                                <i class="fa fa-download"></i>
                            </g:if>
                            <g:else>
                                <i class="fa fa-user"></i>
                            </g:else>
                            ${tr.tramite?.de?.login ?: tr?.tramite?.de?.toString()}
                        </td>
                        <td class="text-center">${tr.fechaLimiteRespuesta?.format("dd-MM-yyyy HH:mm")}</td>
                        <td class="text-center">${tr?.rolPersonaTramite?.descripcion}</td>
                        <td class="text-center">${estado}</td>
                        <td class="text-center">
                            <g:if test="${tr.tramite.deDepartamento}">
                            %{--<g:select class="form-control input-sm select" name="cmbRedirect_${tr.id}" from="${personas}" optionKey="id"/>--}%
                                <g:select class="form-control input-sm select" name="cmbRedirect_${tr.id}" from="${filtradas}" optionKey="id"/>
                            </g:if>
                            <g:else>

                            %{--<g:set var="pers2" value="${personas - tr.tramite.de}"/>--}%
                                <g:set var="pers2" value="${filtradas - tr.tramite.de}"/>
                                <g:select class="form-control input-sm select" name="cmbRedirect_${tr.id}" from="${pers2}" optionKey="id"
                                             noSelection="[('-' + dep.id): dep.descripcion]"/>
                            </g:else>
                        </td>
                        <td class="text-center">
                            <a href="#" class="btn btn-xs btn-success btn-move"
                               data-loading-text="<i class='fa fa-spinner fa-spin'></i>"
                               data-id="${tr.id}" title="Enviar">
                                <i class="fa fa-plane"></i>&nbsp;
                            </a>
                        </td>
                    </tr>
                </g:each>
            </tbody>
        </table>

        <script type="text/javascript">
            $(function () {
                $(".btn-move").click(function () {
                    $(".qtip").hide();
                    var $this = $(this);
                    var $tr = $this.parents("tr");
                    var pr = $this.data("id");
                    var $cmb = $("#cmbRedirect_" + pr);
                    var quien = $cmb.val();

                    $this.button('loading');
                    $tr.addClass("loading");
                    $cmb.addClass("loading").prop('disabled', 'disabled');

//                    console.log(pr, quien);
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(controller: 'tramiteAdmin', action: 'redireccionarTramite_ajax')}",
                        data    : {
                            pr    : pr,
                            quien : quien,
                            id    : "${persona.id}"
                        },
                        success : function (msg) {
                            if (msg == "OK") {
                                $cmb.removeClass("loading").prop('disabled', false);
                                $tr.hide("slow", function () {
                                    $tr.remove();
                                    log("El trámite ha sido redireccionado", "success");
                                });
                            } else {
                                log(msg, "error", "Ha ocurrido un error");
                                $this.button('reset');
                                $tr.removeClass("loading").addClass("danger").effect("pulsate", 3000, function () {
                                    $tr.removeClass("danger");
                                });
                                $cmb.removeClass("loading").prop('disabled', false);
                            }
                        }
                    });
                    return false;
                });
            });
        </script>

    </body>
</html>