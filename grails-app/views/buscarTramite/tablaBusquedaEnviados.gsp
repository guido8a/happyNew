<%@ page import="happy.tramites.DocumentoTramite" %>
<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 14/04/14
  Time: 03:41 PM
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
%{--<th class="alinear" style="width: 145px">Documento</th>--}%
%{--<th class="alinear" style="width: 180px">De</th>--}%
%{--<th class="alinear" style="width: 180px">Para</th>--}%
%{--<th class="alinear" style="width: 70px">Rol</th>--}%
%{--<th class="alinear" style="width: 190px">Asunto</th>--}%
%{--<th class="alinear" style="width: 67px">Prioridad</th>--}%
%{--<th class="alinear" style="width: 110px">Fecha Creación</th>--}%
%{--<th class="alinear" style="width: 110px">Fecha Envio</th>--}%
%{--<th class="alinear" style="width: 67px">Estado</th>--}%
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
                    <th class="alinear" style="width: 145px">Documento</th>
                    <th class="alinear" style="width: 180px">De</th>
                    <th class="alinear" style="width: 180px">Para</th>
                    <th class="alinear" style="width: 70px">Rol</th>
                    <th class="alinear" style="width: 190px">Asunto</th>
                    <th class="alinear" style="width: 67px">Prioridad</th>
                    <th class="alinear" style="width: 110px">Fecha Creación</th>
                    <th class="alinear" style="width: 110px">Fecha Envio</th>
                    <th class="alinear" style="width: 67px">Estado</th>
                </tr>
            </thead>
            <tbody>
                <g:each in="${tramites}" var="tramite">

                    <g:set var="padre" value=""/>
                    <g:set var="clase" value="${'nada'}"/>
                    <g:set var="limite" value="${tramite?.tramite?.getFechaLimite()}"/>

                    <g:if test="${tramite?.tramite?.de?.id == session.usuario.id}">
                        <g:if test="${tramite?.tramite?.padre}">
                            <g:set var="padre" value="${tramite?.tramite?.padre?.id}"/>
                            <g:set var="clase" value="${'padre'}"/>
                        </g:if>
                    </g:if>

                    <g:if test="${tramite.tramite.deId == session.usuario.id}">
                        <g:set var="clase" value="${clase + ' mio'}"/>
                    </g:if>

                    <tr id="${tramite?.tramite?.id}" data-id="${tramite?.tramite?.id}" padre="${padre}" principal="${tramite?.tramite?.tramitePrincipal}"
                        class="${(limite) ? ((limite < new Date()) ? 'alerta' + ' ' + clase : tramite?.tramite?.estadoTramite?.codigo) : tramite?.tramite?.estadoTramite?.codigo + " " + clase}" estado="${tramite?.tramite?.estadoTramite?.codigo}">
                        %{--<td style="width: 110px">${tramite?.tramite?.codigo}</td>--}%
                        <td style="width: 145px">
                            <g:if test="${tramite?.tramite?.tipoTramite?.codigo == 'C'}">
                                <i class="fa fa-eye-slash"></i>
                            </g:if>
                            <g:if test="${tramite?.tramite?.anexo == 1 && DocumentoTramite.countByTramite(tramite.tramite) > 0}">
                                <i class="fa fa-paperclip"></i>
                            </g:if>
                            ${tramite?.tramite?.codigo}
                        </td>
                        <td style="width: 180px">
                            <g:if test="${tramite?.tramite?.deDepartamento}">
                                ${tramite?.tramite?.deDepartamento?.descripcion}
                            </g:if>
                            <g:else>
                                ${tramite?.tramite?.de?.nombre + " " + tramite?.tramite?.de?.apellido}
                            </g:else>
                        </td>
                        <td style="width: 180px">
                            %{--<g:if test="${tramite?.tramite?.para?.persona}">--}%
                            %{--${tramite?.tramite?.para?.persona?.nombre + " " + tramite?.tramite?.para?.persona?.apellido}--}%
                            %{--</g:if>--}%
                            %{--<g:else>--}%
                            %{--${tramite?.tramite?.para?.departamento?.descripcion}--}%
                            %{--</g:else>--}%
                            <g:if test="${tramite.persona}">
                                ${tramite.persona}
                            </g:if>
                            <g:else>
                                ${tramite.departamento}
                            </g:else>
                        </td>
                        <td style="width: 70px">${tramite?.rolPersonaTramite?.descripcion}</td>
                        <td style="width: 190px">${tramite?.tramite?.asunto}</td>
                        <td style="width: 67px">${tramite?.tramite?.prioridad?.descripcion}</td>

                        <td style="width: 110px">${tramite?.tramite?.fechaCreacion?.format('dd-MM-yyyy HH:mm')}</td>
                        <g:if test="${tramite?.tramite?.fechaEnvio}">
                            <td style="width: 110px">${tramite?.tramite?.fechaEnvio?.format('dd-MM-yyyy HH:mm')}</td>
                        </g:if>
                        <g:else>
                            <td></td>
                        </g:else>
                        <td style="width: 67px">${tramite?.tramite?.estadoTramite?.descripcion}</td>
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

        $('.table').fixedHeaderTable({
            height : 450
        });
    });
</script>

