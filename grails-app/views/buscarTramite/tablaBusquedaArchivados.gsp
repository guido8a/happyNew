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

<style type="text/css">
table {
    font-size : 9pt;
}
</style>

<div style="height: 30px; overflow: hidden;" class="container-celdas">
    <span class="grupo">
        <table class="table table-bordered table-condensed table-hover">
            <thead>
                <tr>
                    <th class="cabecera" style="width: 145px">Documento</th>
                    <th class="cabecera" style="width: 190px">De</th>
                    <th class="cabecera" style="width: 200px">Para</th>
                    <th class="cabecera" style="width: 190px">Asunto</th>
                    <th class="cabecera" style="width: 115px">Fecha Envio</th>
                    <th class="cabecera" style="width: 110px">Doc. Padre</th>
                    <th class="cabecera" style="width: 67px">Estado</th>

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
                </tr>
            </thead>
            <tbody>
            %{--<g:each in="${pxtTramites}" var="pxt">--}%
                <g:each in="${tramites}" var="tramite">
                %{--<g:if test="${pxt?.id == tramite?.id}">--}%
                    <tr id="${tramite?.tramite?.id}" data-id="${tramite?.tramite?.id}">
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
                        <g:if test="${tramite?.tramite?.deDepartamento}">
                            <td style="width: 190px">${tramite?.tramite?.deDepartamento?.descripcion}</td>
                        </g:if>
                        <g:else>
                            <td style="width: 190px">${tramite?.tramite?.de?.nombre + " " + tramite?.tramite?.de?.apellido}</td>
                        </g:else>
                        <g:if test="${tramite?.departamento}">
                            <td style="width: 200px">${tramite?.departamento?.descripcion + ' [' + tramite?.rolPersonaTramite?.descripcion + '] '}</td>
                        </g:if>
                        <g:else>
                            <td style="width: 200px">${tramite?.persona?.nombre + " " + tramite?.persona?.apellido + ' [' + tramite?.rolPersonaTramite?.descripcion + ' ] '}</td>
                        </g:else>
                        <td style="width: 190px">${tramite?.tramite?.asunto}</td>
                        <td style="width: 115px">${tramite?.fechaEnvio?.format('dd-MM-yyyy HH:mm')}</td>
                        <g:if test="${tramite?.rolPersonaTramite?.codigo == 'R002'}">
                            <g:if test="${tramite?.tramite?.tramitePrincipal}">
                                <td style="width: 110px">${Tramite.get(tramite?.tramite?.tramitePrincipal).codigo}</td>
                            </g:if>
                            <g:else>
                                <td style="width: 110px"></td>
                            </g:else>
                        </g:if>
                        <g:else>
                            <g:if test="${tramite?.tramite?.padre}">
                                <td style="width: 110px">${tramite?.tramite?.padre?.codigo}</td>
                            </g:if>
                            <g:else>
                                <td style="width: 110px">Trámite Padre</td>
                            </g:else>
                        </g:else>

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
    });
</script>
