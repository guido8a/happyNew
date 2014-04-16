<%@ page import="happy.tramites.Tramite" %>
<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>

<div style="height: 450px" class="container-celdas">
    <table class="table table-bordered table-condensed table-hover">
        <thead>
            <tr>
                <th class="cabecera">Documento</th>
                <th class="cabecera">Fecha Envío</th>
                <th class="cabecera">Fecha Recepción</th>
                <th class="cabecera">De</th>
                <th class="cabecera">Creado Por</th>
                <th class="cabecera">Para</th>
                <th class="cabecera">Prioridad</th>
                <th class="cabecera">Fecha Límite</th>
                <th class="cabecera">Rol</th>
            </tr>
        </thead>
        <tbody>
            <g:each in="${tramites}" var="tramite">
                <g:set var="now" value="${new java.util.Date()}"/>

                <g:set var="type" value=""/>
                <g:set var="clase" value=""/>

            %{--<g:if test="${tramite.fechaRecepcion}">--}%
            %{--<g:set var="type" value="recibido"/>--}%
            %{--<g:set var="clase" value="info"/>--}%
            %{--<g:if test="${tramite.tramite.fechaMaximoRespuesta < ahora}">--}%
            %{--<g:set var="type" value="retrasado"/>--}%
            %{--<g:set var="clase" value="danger"/>--}%
            %{--</g:if>--}%
            %{--</g:if>--}%
            %{--<g:else>--}%
            %{--<g:set var="type" value="pendiente"/>--}%
            %{--<g:set var="clase" value=""/>--}%
            %{--<g:if test="${tramite.tramite.fechaLimite < ahora}">--}%
            %{--<g:set var="type" value="noRecibido"/>--}%
            %{--<g:set var="clase" value="alert-otroRojo"/>--}%
            %{--</g:if>--}%
            %{--</g:else>--}%

                <g:if test="${tramite.fechaRecepcion}">
                    <g:if test="${tramite.fechaLimiteRespuesta < now}">
                        <g:set var="clase" value="retrasado"/>
                        <g:if test="${Tramite.countByPadre(tramite.tramite) > 0}">
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

            %{--<g:if test="${tramite.tramite.estadoTramite.codigo == 'E007'}">--}%
            %{--<g:set var="type" value="${type} jefe"/>--}%
            %{--<g:set var="clase" value="${clase} alert-azul"/>--}%
            %{--</g:if>--}%

                <tr data-id="${tramite?.tramite?.id}" codigo="${tramite?.tramite?.codigo}" departamento="${tramite?.tramite?.de?.departamento?.codigo}"
                    class="${clase} ${type}">
                    <td title="${tramite.tramite.asunto}">${tramite?.tramite?.codigo}</td>
                    <td>${tramite?.tramite?.fechaEnvio?.format("dd-MM-yyyy HH:mm")}</td>
                    <td>${tramite?.fechaRecepcion?.format("dd-MM-yyyy HH:mm")}</td>
                    <td title="${tramite?.tramite?.de?.departamento?.descripcion}">${tramite?.tramite?.de?.departamento?.codigo}</td>
                    <td title="${tramite?.tramite?.de}">${tramite?.tramite?.de?.login ?: tramite?.tramite?.de?.toString()}</td>
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