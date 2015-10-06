<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 14/07/14
  Time: 11:18 AM
--%>

<%@ page import="happy.tramites.Tramite; happy.seguridad.Persona; happy.tramites.DocumentoTramite" contentType="text/html;charset=UTF-8" %>
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
                <g:each in="${rows}" var="row" status="i">
                    <g:set var="now" value="${new Date()}"/>

                    <g:set var="estado" value="Por recibir"/>

                    <g:if test="${row.trmtfcrc}">%{-- fecha de recepcion --}%
                        <g:if test="${row.trmtfclr < now}">%{-- fecha limite respuesta --}%
                            <g:set var="estado" value="Retrasado"/>
                        </g:if>
                        <g:else>
                            <g:set var="estado" value="Recibido"/>
                        </g:else>
                    </g:if>
                    <g:else>
                        <g:if test="${row.trmtfcbq && row.trmtfcbq < now}">%{-- fecha bloqueo --}%
                            <g:set var="estado" value="Sin recepción"/>
                        </g:if>
                        <g:else>
                            <g:set var="estado" value="Por recibir"/>
                        </g:else>
                    </g:else>

                    <tr>
                        <td class="text-center">${i + 1}</td>
                        <td class="text-center">
                            <g:if test="${row.tptrcdgo == 'C'}">%{-- tipo tramite cdgo --}%
                                <i class="fa fa-eye-slash" style="margin-left: 10px"></i>
                            </g:if>
                            <g:if test="${row.trmtdctr > 0}">%{-- DocumentoTramite.count --}%
                                <i class="fa fa-paperclip"></i>
                            </g:if>
                            ${row.trmtcdgo}%{-- tramite cdgo --}%
                        </td>
                        <td class="text-center">${row.trmtfcen?.format("dd-MM-yyyy HH:mm")}</td>
                        <td class="text-center">${row.trmtfcrc?.format("dd-MM-yyyy HH:mm")}</td>
                        <td class="text-center">${row.deprdpto}</td>
                        <td class="text-center">
                            <g:if test="${row.dpto__de}">
                                <i class="fa fa-download"></i>
                                ${row.deprdpto} (${row.deprlogn})
                            </g:if>
                            <g:else>
                                <i class="fa fa-user"></i>
                                ${row.deprlogn}
                            </g:else>
                        %{--${row.deprlogn ?: row.deprdscr}--}%
                        </td>
                        <td class="text-center">${row.trmtfclr?.format("dd-MM-yyyy HH:mm")}</td>
                        <td class="text-center">${row.rltrdscr}</td>
                        <td class="text-center">${estado}</td>
                        <td class="text-center">
                            <g:if test="${row.dpto__de}">
                            %{--<g:select class="form-control input-sm select" name="cmbRedirect_${tr.id}" from="${personas}" optionKey="id"/>--}%
                                <g:select class="form-control input-sm select" name="cmbRedirect_${row.trmt__id}" from="${filtradas}" optionKey="id"/>
                            </g:if>
                            <g:else>

                            %{--<g:set var="pers2" value="${personas - tr.tramite.de}"/>--}%
                            %{--<g:set var="pers2" value="${filtradas - tr.tramite.de}"/>--}%
                                <g:set var="pers2" value="${filtradas - filtradas.find { it.login == row.deprlogn }}"/>
                                <g:select class="form-control input-sm select" name="cmbRedirect_${row.trmt__id}" from="${pers2}" optionKey="id"
                                          noSelection="[('-' + dep.id): dep.descripcion]"/>
                            </g:else>
                        </td>
                        <td class="text-center">
                            <a href="#" class="btn btn-xs btn-success btn-move"
                               data-loading-text="<i class='fa fa-spinner fa-spin'></i>"
                               data-id="${row.trmt__id}" title="Enviar">
                                <i class="fa fa-plane"></i>&nbsp;
                            </a>
                        </td>
                    </tr>
                </g:each>
            </tbody>
        </table>

    <h3>Trámites en la bandeja de salida</h3>
    <div id="" style=";height: 500px;overflow: auto;position: relative">

        <div id="bandeja">

            <table class="table table-bordered  table-condensed table-hover">
                <thead>
                <tr>
                    <th class="cabecera sortable ${params.sort == 'trmtcdgo' ? (params.order + ' sorted') : ''}" data-sort="trmtcdgo" data-order="${params.order}">Documento</th>
                    <th>De</th>
                    <th class="cabecera sortable ${params.sort == 'trmtfccr' ? (params.order + ' sorted') : ''}" data-sort="trmtfccr" data-order="${params.order}">Fec. Creación</th>
                    <th class="cabecera sortable ${params.sort == 'prtrdpto' ? (params.order + ' sorted') : ''}" data-sort="prtrdpto" data-order="${params.order}">Para</th>
                    <th>Destinatario</th>
                    <th class="cabecera sortable ${params.sort == 'trmttppd' ? (params.order + ' sorted') : ''}" data-sort="trmttppd" data-order="${params.order}">Prioridad</th>
                    <th class="cabecera sortable ${params.sort == 'trmtfcen' ? (params.order + ' sorted') : ''}" data-sort="trmtfcen" data-order="${params.order}">Fecha Envío</th>
                    <th class="cabecera sortable ${params.sort == 'trmtfcbq' ? (params.order + ' sorted') : ''}" data-sort="trmtfcbq" data-order="${params.order}">F. Límite Recepción</th>
                    <th class="cabecera sortable ${params.sort == 'edtrdscr' ? (params.order + ' sorted') : ''}" data-sort="edtrdscr" data-order="${params.order}">Estado</th>
                </tr>
                </thead>
                <tbody>
                    <g:each in="${salida}" var="slda">
                        <tr>
                            <td title="${slda.trmtasnt}" style="width: 145px;">
                                <g:if test="${slda.tptrcdgo == 'C'}">
                                    <i class="fa fa-eye-slash" style="margin-left: 10px"></i>
                                </g:if>
                                <g:if test="${slda.trmtdctr > 0}">
                                    <i class="fa fa-paperclip"></i>
                                </g:if>
                                ${slda.trmtcdgo}
                            </td>
                            <td>
                                ${slda.deprdscr} (${slda.deprdpto})
                            </td>
                            <td style="width: 115px;">
                                ${slda.trmtfccr.format("dd-MM-yyyy HH:mm")}
                            </td>
                            <td>
                                <g:if test="${slda.tpdccdgo == 'OFI'}">
                                    EXT
                                </g:if>
                                <g:else>
                                    ${slda.prtrdpto}
                                </g:else>
                            </td>
                            <td class="titleEspecial"
                                title="<div style='max-height:150px; overflow-y:auto;'>${slda.paratitl}</div>">%{--el title con los destinatarios y si recibieron o no--}%
                                <span class="para">
                                    <g:if test="${slda.prtrprsn}">%{--para persona (squi guarda la persona, interna o externa)--}%
                                        ${slda.prtrprsn}
                                    </g:if>
                                    <g:else>
                                        <g:set var="triangulos" value="${slda.paradpto.split(',')}"/>
                                        <g:each in="${triangulos}" var="t" status="i">%{--para dpto--}%
                                            <i class="fa fa-download"></i>
                                            ${t}${i < triangulos.size() - 1 ? ', ' : ''}
                                        </g:each>
                                    </g:else>
                                </span>
                                <span class="copias">
                                    ${slda.copidpto.replaceAll('cc: *', '[CC] ')}${slda.copidpto && slda.copidpto != "" && slda.copiprsn && slda.copiprsn != "" ? ', ' : ''}
                                    ${slda.copiprsn.replaceAll('cc: *', '[CC] ')}
                                </span>

                                <g:if test="${!((slda.prtrprsn && slda.prtrprsn != '') ||
                                        (slda.paradpto && slda.paradpto != '') ||
                                        (slda.copidpto && slda.copidpto != '') ||
                                        (slda.copiprsn && slda.copiprsn != ''))}">
                                    <span class="label label-danger" style="margin-top: 3px;">
                                        <i class="fa fa-warning"></i> Sin destinatario ni copias
                                    </span>
                                </g:if>
                            </td>
                            <td>
                                ${slda.trmttppd}
                            </td>
                            <td style="width: 115px;">
                                ${slda.trmtfcen?.format('dd-MM-yyyy HH:mm')}
                            </td>
                            <td style="width: 115px;">
                                ${slda.trmtfcbq?.format('dd-MM-yyyy HH:mm')}
                            </td>
                            <td>
                                ${slda.edtrdscr}
                            </td>
                        </tr>
                    </g:each>
                </tbody>
            </table>

        </div>
    </div>

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