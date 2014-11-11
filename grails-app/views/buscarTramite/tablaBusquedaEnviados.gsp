<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 14/04/14
  Time: 03:41 PM
--%>
<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js/plugins/lzm.context/js', file: 'lzm.context-0.5.js')}"></script>
<link href="${resource(dir: 'js/plugins/lzm.context/css', file: 'lzm.context-0.5.css')}" rel="stylesheet">

<div style="height: 55px" class="container-celdas">
    <span class="grupo">
        <table class="table table-bordered table-condensed table-hover">
            <thead>
                <tr>
                    <th class="alinear" style="width: 110px">Documento</th>
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
                    %{--<th class="alinear">Para</th>--}%
                    %{--<th class="alinear">Asunto</th>--}%
                    %{--<th class="alinear">Prioridad</th>--}%
                    %{--<th class="alinear">De</th>--}%
                    %{--<th class="alinear">Fecha Creación</th>--}%
                    %{--<th class="alinear">Fecha Envio</th>--}%
                    %{--<th class="alinear">Estado</th>--}%
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

                    <tr id="${tramite?.tramite?.id}" data-id="${tramite?.tramite?.id}" padre="${padre}" principal="${tramite?.tramite?.tramitePrincipal}"
                        class="${(limite) ? ((limite < new Date()) ? 'alerta' + ' ' + clase : tramite?.tramite?.estadoTramite?.codigo) : tramite?.tramite?.estadoTramite?.codigo + " " + clase}" estado="${tramite?.tramite?.estadoTramite?.codigo}">
                        <td style="width: 110px">${tramite?.tramite?.codigo}</td>
                        <g:if test="${tramite?.tramite?.deDepartamento}">
                            <td style="width: 180px">${tramite?.tramite?.deDepartamento?.descripcion}</td>
                        </g:if>
                        <g:else>
                            <td style="width: 180px">${tramite?.tramite?.de?.nombre + " " + tramite?.tramite?.de?.apellido}</td>
                        </g:else>
                        <g:if test="${tramite?.tramite?.para?.persona}">
                            <td style="width: 180px">${tramite?.tramite?.para?.persona?.nombre + " " + tramite?.tramite?.para?.persona?.apellido}</td>
                        </g:if>
                        <g:else>
                            <td style="width: 180px">${tramite?.tramite?.para?.departamento?.descripcion}</td>
                        </g:else>
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
    });
</script>

