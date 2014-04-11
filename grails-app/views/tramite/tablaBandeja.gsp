<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>

<table class="table table-bordered table-condensed table-hover">
    <thead>
    <tr>
        <th class="cabecera">Documento</th>
        <th class="cabecera">Fecha Envío</th>
        <th class="cabecera">Fecha Recepción</th>
        <th class="cabecera">De</th>
        <th class="cabecera">Para</th>
        <th class="cabecera">Prioridad</th>
        <th class="cabecera">Fecha Respuesta</th>
        <th class="cabecera">Rol</th>
    </tr>

    </thead>
    <tbody>
    <g:each in="${tramites}" var="tramite">
        <g:set var="now" value="${new java.util.Date()}"/>
        <tr data-id="${tramite?.tramite?.id}" class=" ${(tramite.fechaRecepcion)?((tramite.tramite.fechaMaximoRespuesta<now)?'retrasado':'E004'):((tramite.tramite.fechaLimite<now)?'pendiente':'E003')}  "
            codigo="${tramite.tramite.codigo}" departamento="${tramite?.tramite?.de?.departamento?.codigo}">
            <td title="${tramite?.tramite?.asunto}">${tramite?.tramite?.codigo}</td>
            <td>${tramite?.fechaEnvio?.format('dd-MM-yyyy HH:mm')}</td>
            <td>${tramite?.fechaRecepcion?.format('dd-MM-yyyy HH:mm')}</td>
            <td title="${tramite?.tramite?.de?.departamento?.descripcion}">${tramite?.tramite?.de?.departamento?.codigo}</td>
            <g:if test="${tramite?.persona}">
                <td>${tramite?.persona}</td>
            </g:if>
            <g:else>
                <td title="${tramite?.departamento?.descripcion}">${tramite?.departamento?.codigo}</td>
            </g:else>
            <td>${tramite?.tramite?.prioridad?.descripcion}</td>
            <td>${tramite?.tramite?.fechaMaximoRespuesta?.format('dd-MM-yyyy HH:mm')}</td>
            <td>${tramite?.rolPersonaTramite?.descripcion}</td>
        </tr>
    </g:each>

    </tbody>
</table>
