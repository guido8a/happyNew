<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 1/21/14
  Time: 1:01 PM
--%>


<div style="height: 450px"  class="container-celdas">
    <span class="grupo">
        <table class="table table-bordered table-condensed table-hover">
            <thead>
            <tr>
            <tr>
                <th class="cabecera sortable ${params.sort == 'codigo' ? (params.order) : ''}" data-domain="tramite" data-sort="codigo" data-order="${params.order}">Documento</th>
                <th class="cabecera sortable ${params.sort == 'fechaEnvio' ? (params.order) : ''}" data-domain="persDoc" data-sort="fechaEnvio" data-order="${params.order}">Fecha Envío</th>
                <th class="cabecera sortable ${params.sort == 'fechaRecepcion' ? (params.order) : ''}" data-domain="persDoc" data-sort="fechaRecepcion" data-order="${params.order}">Fecha Recepción</th>
                <th class="cabecera sortable ${params.sort == 'de' ? (params.order) : ''}" data-domain="tramite" data-sort="de" data-order="${params.order}">De</th>
                <th class="cabecera" data-domain="tramite" data-sort="creadoPor" data-order="${params.order}">Creado por</th>
                <th class="cabecera">Para</th>
                <th class="cabecera sortable ${params.sort == 'prioridad' ? (params.order) : ''}" data-domain="tramite" data-sort="prioridad" data-order="${params.order}">Prioridad</th>
                <th class="cabecera sortable ${params.sort == 'fechaLimiteRespuesta' ? (params.order) : ''}" data-domain="persDoc" data-sort="fechaLimiteRespuesta" data-order="${params.order}">Fecha Límite</th>
                <th class="cabecera sortable ${params.sort == 'rolPersonaTramite' ? (params.order) : ''}" data-domain="persDoc" data-sort="rolPersonaTramite" data-order="${params.order}">Rol</th>
            </tr>
            </tr>

            </thead>
            <tbody>
            <g:each in="${tramites}" var="tramite">

                <g:set var="now" value="${new java.util.Date()}"/>

                <g:set var="clase" value=""/>
                <g:if test="${tramite.fechaRecepcion}">
                    <g:if test="${tramite.fechaLimiteRespuesta < now}">
                        <g:set var="clase" value="retrasado"/>
                        <g:if test="${happy.tramites.Tramite.countByPadre(tramite.tramite) > 0}">
                            <g:set var="clase" value="recibido"/>
                        </g:if>
                    </g:if>
                    <g:else>
                        <g:set var="clase" value="recibido"/>
                    </g:else>
                </g:if>
                <g:else>
                    <g:if test="${tramite.tramite.fechaLimite < now}">
                        <g:set var="clase" value="sinRecepcion"/>
                    </g:if>
                    <g:else>
                        <g:set var="clase" value="porRecibir"/>
                    </g:else>
                </g:else>


               <g:each in="${pxtTramites}" var="pxt">
                    <g:if test="${tramite?.id == pxt?.id}">
                        <tr data-id="${tramite?.tramite?.id}"
                            class="${clase}"
                            codigo="${tramite.tramite.codigo}" departamento="${tramite?.tramite?.de?.departamento?.codigo}">

                            <td title="${tramite?.tramite?.asunto}">${tramite?.tramite?.codigo}</td>
                            <td>${tramite?.tramite?.fechaEnvio?.format('dd-MM-yyyy HH:mm')}</td>
                            <td>${tramite?.fechaRecepcion?.format('dd-MM-yyyy HH:mm')}</td>
                            <td title="${tramite?.tramite?.de?.departamento?.descripcion}">${tramite?.tramite?.de?.departamento?.codigo}</td>
                            <td title="${tramite?.tramite?.de}">${tramite?.tramite?.de?.login ?: tramite?.tramite?.de?.toString()}</td>
                            <g:if test="${tramite?.persona}">
                                <td>${tramite?.persona}</td>
                            </g:if>
                            <g:else>
                                <td title="${tramite?.departamento?.descripcion}">${tramite?.departamento?.codigo}</td>
                            </g:else>
                            <td>${tramite?.tramite?.prioridad?.descripcion}%{-- - ${tramite.tramite.estadoTramite.descripcion}--}%</td>
                            <td>${tramite?.fechaLimiteRespuesta?.format("dd-MM-yyyy HH:mm")}</td>
                            <td>${tramite?.rolPersonaTramite?.descripcion}</td>

                        </tr>

                    </g:if>
                </g:each>



            </g:each>

            </tbody>
        </table>

    </span>

</div>

<script type="text/javascript">
    function clean() {
        $(".recibidoColor").removeClass("recibidoColor");
        $(".retrasadoColor").removeClass("retrasadoColor");
        $(".pendienteColor").removeClass("pendienteColor");
        $(".pendienteRojoColor").removeClass("pendienteRojoColor");
    }
    function getRows(clase) {
        clean();
        $("."+clase).addClass(clase+"Color");
    }

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