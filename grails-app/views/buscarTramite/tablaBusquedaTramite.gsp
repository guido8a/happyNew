<%@ page import="happy.tramites.Tramite" %>
<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 14/03/14
  Time: 11:20 AM
--%>
<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js/plugins/lzm.context/js', file: 'lzm.context-0.5.js')}"></script>
<link href="${resource(dir: 'js/plugins/lzm.context/css', file: 'lzm.context-0.5.css')}" rel="stylesheet">

<div style="height: 70px"  class="container-celdas">
    <span class="grupo">
        <table class="table table-bordered table-condensed table-hover">
            <thead>
            <tr>
                <th class="alinear" style="width: 110px">Documento</th>
                <th class="alinear" style="width: 100px">Fecha Creación</th>
                <th class="alinear" style="width: 150px">De</th>
                <th class="alinear" style="width: 150px">Para</th>
                <th class="alinear" style="width: 100px">Asunto</th>
                <th class="alinear" style="width: 60px">Prioridad</th>
                <th class="alinear" style="width: 90px">Envia</th>
                <th class="alinear" style="width: 110px">Fecha Envio</th>
                <th class="alinear" style="width: 110px">Fecha Recepción</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>

    </span>

</div>




<div style="height: 350px" class="container-celdas">
    <span class="grupo">
        <table class="table table-bordered table-condensed table-hover">
            <thead>
                <tr>
                    %{--<th class="alinear">Documento</th>--}%
                    %{--<th class="alinear">Fecha Creación</th>--}%
                    %{--<th class="alinear">De</th>--}%
                    %{--<th class="alinear">Para</th>--}%
                    %{--<th class="alinear">Asunto</th>--}%
                    %{--<th class="alinear">Prioridad</th>--}%
                    %{--<th class="alinear">Envia</th>--}%
                    %{--<th class="alinear">Fecha Envio</th>--}%
                    %{--<th class="alinear">Fecha Recepción</th>--}%
                </tr>

            </thead>
            <tbody>
                <g:each in="${tramites}" var="tramite">

                    <g:set var="padre" value=""/>
                    <g:set var="clase" value="${'nada'}"/>

                    <g:set var="tramiteActual" value="${Tramite.get(tramite?.trmt__id)}"/>

                    <g:if test="${tramiteActual.de?.id == session.usuario.id || tramiteActual.deDepartamento?.id == session.usuario.departamentoId}">
                        <g:set var="clase" value="${'principal'}"/>
                        <g:if test="${tramiteActual.padre}">
                            <g:set var="padre" value="${tramiteActual.padre?.id}"/>
                            <g:set var="clase" value="${'padre'}"/>
                        </g:if>
                    </g:if>

                    <g:if test="${tramite.trmtanxo == 1}">
                        <g:set var="clase" value="${clase + ' conAnexo'}"/>
                    </g:if>

                    <g:if test="${tramite?.fc_recp}">
                        <g:set var="clase" value="${clase + ' recibido'}"/>
                    </g:if>

                    <tr id="${tramite?.trmt__id}" data-id="${tramite?.trmt__id}" padre="${padre}" class="${clase}">
                        <td style="width: 110px">
                            ${tramite?.trmtcdgo}
                            <g:if test="${tramite.trmtanxo == 1}">
                                <i class="fa fa-paperclip fa-fw" style="margin-left: 10px"></i>
                            </g:if>
                            <g:if test="${tramite?.es_extr == 1}">
                                (ext)
                            </g:if>
                        </td>
                        <td style="width: 100px">${tramite?.fc_trmt?.format('dd-MM-yyyy HH:mm')}</td>
                        <g:if test="${tramite?.de_prsn}">
                            <td style="width: 150px">${tramite?.de_prsn}</td>
                        </g:if>
                        <g:elseif test="${tramite?.de_dpto}">
                            <td style="width: 150px">${tramite?.de_dpto}</td>
                        </g:elseif>
                        <g:else>
                            <td style="width: 150px"></td>
                        </g:else>

                        <td style="width: 150px">
                            <g:if test="${tramite?.pr_prsn}">
                                ${tramite?.pr_prsn}
                            </g:if>
                            <g:elseif test="${tramite?.pr_dpto}">
                                ${tramite?.pr_dpto}
                            </g:elseif>
                            <g:if test="${tramite?.es_extr == 1 && tramite?.pr_extr}">
                                (${tramite?.pr_extr})
                            </g:if>
                        </td>


                        <td style="width: 100px">${tramite?.trmtasnt}</td>

                        <td style="width: 60px">${tramite?.tppddscr}</td>

                        <td style="width: 90px">${tramite?.en_prsn}</td>

                        <g:if test="${tramite?.fc_envi}">
                            <td style="width: 110px">${tramite?.fc_envi?.format('dd-MM-yyyy HH:mm')}</td>
                        </g:if>
                        <g:else>
                            <td style="width: 110px"></td>
                        </g:else>
                        <g:if test="${tramite?.fc_recp}">
                            <td style="width: 110px">${tramite?.fc_recp?.format('dd-MM-yyyy HH:mm')}</td>
                        </g:if>
                        <g:else>
                            <td style="width: 110px"></td>
                        </g:else>
                    </tr>
                </g:each>

            </tbody>
        </table>

    </span>

</div>


<script type="text/javascript">
    $(function () {
        $("tr").contextMenu({
            items  : createContextMenu,
            onShow : function ($element) {
                $element.addClass("trHighlight");
            },
            onHide : function ($element) {
                $(".trHighlight").removeClass("trHighlight");
            }
        });
    });
</script>
