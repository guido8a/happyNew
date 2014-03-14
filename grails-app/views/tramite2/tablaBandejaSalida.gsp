<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>

<table class="table table-bordered  table-condensed table-hover">
    <thead>
    <tr>
        <th class="cabecera">Documento</th>
        <th>De</th>
        <th class="cabecera">Fec. Creación</th>
        <th class="cabecera">Para</th>
        <th class="cabecera">Destinatario</th>
        <th class="cabecera">Prioridad</th>
        <th class="cabecera">Fecha Envio</th>
        <th class="cabecera">Fecha Límite</th>
        <th class="cabecera">Estado</th>
    </tr>
    </thead>
    <tbody>
    <g:each in="${tramites}" var="tramite">
        <g:set var="limite" value="${tramite.getFechaLimite()}"/>
        <tr  id="${tramite?.id}" data-id="${tramite?.id}" class="${(limite)?((limite<new Date())?'alerta':tramite.estadoTramite.codigo):tramite.estadoTramite.codigo}" estado="${tramite.estadoTramite.codigo}" de="${tramite.de.id}">
            <td title="${tramite.asunto}">${tramite?.codigo}</td>
            <td>${tramite.de}</td>
            <td>${tramite.fechaCreacion?.format("dd-MM-yyyy")}</td>
            <g:set var="para" value="${tramite.getPara()}"/>
            <td >${para?.departamento?.codigo}</td>
            <td>${para?.persona}</td>
            <td>${tramite?.prioridad.descripcion}</td>
            <td>${tramite.fechaEnvio?.format("dd-MM-yyyy HH:mm")}</td>
            <td>${limite?limite.format("dd-MM-yyyy HH:mm"):''}</td>
            <td>${tramite?.estadoTramite.descripcion}</td>
        </tr>
    </g:each>
    </tbody>
</table>

