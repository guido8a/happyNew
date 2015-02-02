<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 21/04/14
  Time: 12:23 PM
--%>

<%@ page import="happy.tramites.DocumentoTramite; happy.tramites.Tramite" %>
<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>

<div style="height: 450px" class="container-celdas">
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

                <g:set var="type" value=""/>
                <g:set var="clase" value=""/>

                <g:if test="${tramite.fechaRecepcion}">
                    <g:if test="${tramite.fechaLimiteRespuesta < now}">
                        <g:set var="clase" value="retrasado"/>
                        <g:if test="${tramite.respuestasVivas.size() > 0}">
                            <g:set var="clase" value="recibido"/>
                        </g:if>
                    </g:if>
                    <g:else>
                        <g:set var="clase" value="recibido"/>
                    </g:else>
                </g:if>
                <g:else>
                    <g:if test="${tramite.fechaBloqueo < now}">
                        <g:set var="clase" value="sinRecepcion"/>
                    </g:if>
                    <g:else>
                        <g:set var="clase" value="blanco porRecibir"/>
                    </g:else>
                </g:else>
                <g:if test="${tramite?.tramite?.anexo == 1 && DocumentoTramite.countByTramite(tramite.tramite) > 0}">
                    <g:set var="clase" value="${clase + ' conAnexo'}"/>
                </g:if>
                <g:else>
                    <g:set var="clase" value="${clase + ' sinAnexo'}"/>
                </g:else>
                <g:set var="clase" value="${clase + ' ' + tramite.rolPersonaTramite.codigo}"/>
                        <tr data-id="${tramite.tramite?.id}" codigo="${tramite?.tramite?.codigo}"
                            departamento="${tramite?.tramite?.de?.departamento?.codigo}"
                            prtr="${tramite?.id}"
                            de="${tramite.tramite.tipoDocumento.codigo == 'DEX' ? 'E_' + tramite.tramiteId :
                                    (tramite.tramite?.deDepartamento ? 'D_' + tramite.tramite?.deDepartamento?.id : 'P_' + tramite.tramite?.de?.id)}"
                            class="${clase} ${type} ${(tramite?.tramite?.estadoTramiteExterno) ? 'estadoExterno' : ''}">

                            <td title="${tramite.tramite.asunto}">
                                     <g:if test="${tramite?.tramite?.anexo == 1}">
                                    <g:if test="${tramite?.tramite?.tipoTramite?.codigo == 'C'}">
                                        <i class="fa fa-eye-slash" style="margin-left: 10px"></i>
                                    </g:if>
                                    ${tramite?.tramite?.codigo}
                                    <g:if test="${DocumentoTramite.countByTramite(tramite.tramite) > 0}">
                                        <i class="fa fa-paperclip" style="margin-left: 10px"></i>
                                    </g:if>
                                </g:if>
                                <g:else>
                                    ${tramite?.tramite?.codigo}
                                </g:else>
                            </td>
                            <td>${tramite?.fechaEnvio?.format('dd-MM-yyyy HH:mm')}</td>
                            <td>${tramite?.fechaRecepcion?.format("dd-MM-yyyy HH:mm")}</td>
                            <g:if test="${tramite.tramite.tipoDocumento.codigo == 'DEX'}">
                                <td>EXT</td>
                            </g:if>
                            <g:else>
                                <td title="${tramite?.tramite?.de?.departamento?.descripcion}">${tramite?.tramite?.de?.departamento?.codigo}</td>
                            </g:else>
                            <g:if test="${tramite.tramite.tipoDocumento.codigo == 'DEX'}">
                                <td>${tramite.tramite.paraExterno}</td>
                            </g:if>
                            <g:else>
                                <td title="${tramite?.tramite?.de}">${tramite?.tramite?.de?.login ?: tramite?.tramite?.de?.toString()}</td>
                            </g:else>
                            <g:if test="${tramite?.persona}">
                                <td>${tramite?.persona}</td>
                            </g:if>
                            <g:else>
                                <td title="${tramite?.departamento?.descripcion}">${tramite?.departamento?.codigo}</td>
                            </g:else>
                            <td>${tramite.tramite.prioridad.descripcion}</td>
                            <td>${tramite?.fechaLimiteRespuesta?.format("dd-MM-yyyy HH:mm")}</td>
                            <td>${tramite?.rolPersonaTramite?.descripcion}</td>
                        </tr>
            </g:each>
        </tbody>
    </table>
</div>

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