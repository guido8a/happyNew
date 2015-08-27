<%@ page import="happy.tramites.DocumentoTramite; happy.tramites.Tramite" %>
<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 04/06/14
  Time: 12:30 PM
--%>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js/plugins/lzm.context/js', file: 'lzm.context-0.5.js')}"></script>
<link href="${resource(dir: 'js/plugins/lzm.context/css', file: 'lzm.context-0.5.css')}" rel="stylesheet">

<script type="text/javascript" src="${resource(dir: 'js/plugins/fixed-header-table-1.3', file: 'jquery.fixedheadertable.min.js')}"></script>
<link href="${resource(dir: 'js/plugins/fixed-header-table-1.3/css', file: 'defaultTheme.css')}" rel="stylesheet">

<style type="text/css">
table {
    font-size : 9pt;
}
</style>

%{--<div style="height: 30px; overflow: hidden;" class="container-celdas">--}%
%{--<span class="grupo">--}%
%{--<table class="table table-bordered table-condensed table-hover">--}%
%{--<thead>--}%
%{--<tr>--}%
%{--<th class="cabecera" style="width: 145px">Documento</th>--}%
%{--<th class="cabecera" style="width: 190px">De</th>--}%
%{--<th class="cabecera" style="width: 200px">Para</th>--}%
%{--<th class="cabecera" style="width: 190px">Asunto</th>--}%
%{--<th class="cabecera" style="width: 115px">Fecha Envio</th>--}%
%{--<th class="cabecera" style="width: 110px">Doc. Padre</th>--}%
%{--<th class="cabecera" style="width: 67px">Estado</th>--}%

%{--</tr>--}%
%{--</thead>--}%
%{--<tbody>--}%
%{--</tbody>--}%
%{--</table>--}%
%{--</span>--}%
%{--</div>--}%

<div style="height: 450px" class="container-celdas">
    <span class="grupo">
        <table class="table table-bordered table-condensed table-hover">
            <thead>
                <tr>
                    <th class="cabecera" style="width: 160px">Documento</th>
                    <th class="cabecera" style="width: 200px">De</th>
                    <th class="cabecera" style="width: 200px">Para</th>
                    <th class="cabecera" style="width: 200px">Asunto</th>
                    <th class="cabecera" style="width: 115px">Fecha Envio</th>
                    <th class="cabecera" style="width: 110px">Doc. Padre</th>
                    <th class="cabecera" style="width: 67px">Estado</th>

                </tr>
            </thead>
            <tbody>
            %{--<g:each in="${pxtTramites}" var="pxt">--}%
                <g:each in="${tramites}" var="tramite">
                %{--<g:if test="${pxt?.id == tramite?.id}">--}%
                    <tr id="${tramite?.tramite?.id}" data-id="${tramite?.tramite?.id}">
                        %{--<td style="width: 110px">${tramite?.tramite?.codigo}</td>--}%
                        <td style="width: 160px">
                            <g:if test="${tramite?.tramite?.tipoTramite?.codigo == 'C'}">
                                <i class="fa fa-eye-slash"></i>
                            </g:if>
                            <g:if test="${tramite?.tramite?.anexo == 1 && DocumentoTramite.countByTramite(tramite.tramite) > 0}">
                                <i class="fa fa-paperclip"></i>
                            </g:if>
                            ${tramite?.tramite?.codigo}
                        </td>
                        <td style="width: 200px">
                            <g:if test="${tramite?.tramite?.deDepartamento}">
                                ${tramite?.tramite?.deDepartamento?.descripcion}
                            </g:if>
                            <g:else>
                                ${tramite?.tramite?.de?.nombre + " " + tramite?.tramite?.de?.apellido}
                            </g:else>
                        </td>
                        <td style="width: 200px">
                            <g:if test="${tramite?.departamento}">
                                ${tramite?.departamento?.descripcion + ' [' + tramite?.rolPersonaTramite?.descripcion + '] '}
                            </g:if>
                            <g:else>
                                ${tramite?.persona?.nombre + " " + tramite?.persona?.apellido + ' [' + tramite?.rolPersonaTramite?.descripcion + ' ] '}
                            </g:else>
                        </td>
                        <td style="width: 200px">${tramite?.tramite?.asunto}</td>
                        <td style="width: 115px">${tramite?.fechaEnvio?.format('dd-MM-yyyy HH:mm')}</td>
                        <td style="width: 110px">
                            <g:if test="${tramite?.rolPersonaTramite?.codigo == 'R002'}">
                                <g:if test="${tramite?.tramite?.tramitePrincipal}">
                                    ${Tramite.get(tramite?.tramite?.tramitePrincipal).codigo}
                                </g:if>
                            </g:if>
                            <g:else>
                                <g:if test="${tramite?.tramite?.padre}">
                                    ${tramite?.tramite?.padre?.codigo}
                                </g:if>
                                <g:else>
                                    Trámite Padre
                                </g:else>
                            </g:else>
                        </td>
                        <td style="width: 67px">${tramite?.estado?.descripcion}</td>
                    </tr>
                %{--</g:if>--}%
                </g:each>
            %{--</g:each>--}%
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

        $('.table').fixedHeaderTable({
            height : 450
        });
    });
</script>
