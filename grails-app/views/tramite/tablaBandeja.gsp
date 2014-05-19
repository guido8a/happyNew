<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>

<script type="text/javascript" src="${resource(dir: 'js/plugins/lzm.context/js', file: 'lzm.context-0.5.js')}"></script>
<link href="${resource(dir: 'js/plugins/lzm.context/css', file: 'lzm.context-0.5.css')}" rel="stylesheet">

<table class="table table-bordered table-condensed table-hover">
    <thead>
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
                <g:if test="${tramite.fechaLimite < now}">
                    <g:set var="clase" value="sinRecepcion"/>
                </g:if>
                <g:else>
                    <g:set var="clase" value="porRecibir"/>
                </g:else>
            </g:else>

            <g:if test="${tramite?.tramite?.anexo == 1}">
                <g:set var="clase" value="${clase + ' conAnexo'}"/>
            </g:if>
            <g:else>
                <g:set var="clase" value="${clase + ' sinAnexo'}"/>
            </g:else>


            <tr data-id="${tramite?.tramite?.id}"
                class="${clase}"
                codigo="${tramite.tramite.codigo}" departamento="${tramite?.tramite?.de?.departamento?.codigo}"
                anexo="${anexo}" prtr="${tramite?.id}">
                <td title="${tramite?.tramite?.asunto}">
                    ${tramite?.tramite?.codigo}
                    <g:if test="${tramite?.tramite?.anexo == 1}">
                        <i class="fa fa-paperclip fa-fw" style="margin-left: 10px"></i>
                    </g:if>
                </td><!-- documento -->
                <td>${tramite?.tramite?.fechaEnvio?.format('dd-MM-yyyy HH:mm')}</td> <!-- fecha envio -->
                <td>${tramite?.fechaRecepcion?.format('dd-MM-yyyy HH:mm')}</td> <!-- fecha recepcion -->
                <td title="${tramite?.tramite?.de?.departamento?.descripcion}">
                    <g:if test="${tramite.tramite.tipoDocumento.codigo == 'DEX'}">
                        DEX
                    </g:if>
                    <g:else>
                        ${tramite?.tramite?.de?.departamento?.codigo}
                    </g:else>
                </td> <!-- de -->
                <td title="${tramite?.tramite?.de}">
                    <g:if test="${tramite.tramite.tipoDocumento.codigo == 'DEX'}">
                        ${tramite.tramite.paraExterno}
                    </g:if>
                    <g:else>
                        ${tramite?.tramite?.de?.login ?: tramite?.tramite?.de?.toString()}
                    </g:else>
                </td> <!-- creado por -->
                <g:if test="${tramite?.persona}">
                    <td>${tramite?.persona}</td> <!-- para persona-->
                </g:if>
                <g:else>
                    <g:if test="${tramite.tramite.tipoDocumento.codigo == 'OFI'}">
                        <td>
                            ${tramite?.tramite.paraExterno} (ext.)
                        </td> <!-- para externo-->
                    </g:if>
                    <g:else>
                        <td title="${tramite?.departamento?.descripcion}">
                            ${tramite?.departamento?.codigo}
                        </td> <!-- para departamento -->
                    </g:else>
                </g:else>
                <td>${tramite?.tramite?.prioridad?.descripcion}%{-- - ${tramite.tramite.estadoTramite.descripcion}--}%</td> <!-- prioridad -->
                <td>${tramite?.fechaLimiteRespuesta?.format("dd-MM-yyyy HH:mm")}</td> <!-- fecha limite -->
                <td>${tramite?.rolPersonaTramite?.descripcion}</td> <!-- rol -->
            </tr>
        </g:each>

    </tbody>
</table>

<script type="text/javascript">
    $(function () {
        $(".cabecera").click(function () {
            var $col = $(this);
            var order = "";
            if ($col.data("order") == "asc") {
                order = "desc";
            } else if ($col.data("order") == "desc") {
                order = "asc";
            }
            var data = {
                domain : $col.data("domain"),
                sort   : $col.data("sort"),
                order  : order
            };
            cargarBandeja(false, data);
        });

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