<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 14/04/14
  Time: 03:41 PM
--%>
<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js/plugins/lzm.context/js', file: 'lzm.context-0.5.js')}"></script>
<link href="${resource(dir: 'js/plugins/lzm.context/css', file: 'lzm.context-0.5.css')}" rel="stylesheet">


<div style="height: 450px"  class="container-celdas">
    <span class="grupo">
        <table class="table table-bordered table-condensed table-hover">
            <thead>
            <tr>
                <th class="alinear">Documento</th>
                <th class="alinear">Para</th>
                <th class="alinear">Asunto</th>
                <th class="alinear">Prioridad</th>
                <th class="alinear">De</th>
                <th class="alinear">Fecha Creación</th>
                <th class="alinear">Fecha Envio</th>
                <th class="alinear">Estado</th>
            </tr>

            </thead>
            <tbody>
            <g:each in="${tramites}" var="tramite">

             <g:set var="padre" value=""/>
             <g:set var="clase" value="${'nada'}"/>

             <g:if test="${tramite?.de?.id == session.usuario.id}">
                 <g:if test="${tramite?.padre}">
                     <g:set var="padre" value="${tramite?.padre?.id}"/>
                     <g:set var="clase" value="${'padre'}"/>
                 </g:if>
             </g:if>

                <tr id="${tramite?.id}" data-id="${tramite.id}" padre="${padre}" class="${clase}">
                    <td width="100px;">${tramite?.codigo}</td>
                    <g:if test="${tramite?.para?.persona}">
                        <td>${tramite?.para?.persona?.nombre + " " + tramite?.para?.persona?.apellido}</td>
                    </g:if>
                    <g:else>
                        <td>${tramite?.para?.departamento?.descripcion}</td>
                    </g:else>
                    <td>${tramite?.asunto}</td>
                    <td>${tramite?.prioridad?.descripcion}</td>
                    <g:if test="${tramite?.de}">
                        <td>${tramite?.de?.nombre + " " + tramite?.de?.apellido}</td>
                    </g:if>
                    <g:else>
                        <td>${tramite?.deDepartamento?.descripcion}</td>
                    </g:else>
                    <td>${tramite?.fechaCreacion.format('dd-MM-yyyy HH:mm')}</td>
                    <g:if test="${tramite?.fechaEnvio}">
                        <td>${tramite?.fechaEnvio.format('dd-MM-yyyy HH:mm')}</td>
                    </g:if>
                    <g:else>
                        <td></td>
                    </g:else>
                    <td>${tramite?.estadoTramite?.descripcion}</td>
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

