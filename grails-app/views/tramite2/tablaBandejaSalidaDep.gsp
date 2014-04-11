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
            <th class="cabecera">Enviar</th>
        </tr>
    </thead>
    <tbody>
        <g:each in="${tramites}" var="tramite">
            <g:set var="limite" value="${tramite.getFechaLimite()}"/>
            <tr id="${tramite?.id}" data-id="${tramite?.id}"
                class="${(limite) ? ((limite < new Date()) ? 'alerta' : tramite.estadoTramite.codigo) : tramite.estadoTramite.codigo}
                ${tramite.fechaEnvio && tramite.noRecibido ? 'desenviar' : ''}"
                estado="${tramite.estadoTramite.codigo}" de="${tramite.de.id}">
                <td title="${tramite.asunto}">${tramite?.codigo}</td>
                <td title="${tramite.deTexto.codigo}">${tramite.deTexto.codigo}</td>
                <td>${tramite.fechaCreacion?.format("dd-MM-yyyy")}</td>
                <g:set var="para" value="${tramite.getPara()}"/>
                <td>${para?.departamento?.codigo}</td>
                <td>${para?.persona}</td>
                <td>${tramite?.prioridad.descripcion}</td>
                <td>${tramite.fechaEnvio?.format("dd-MM-yyyy HH:mm")}</td>
                <td>${limite ? limite.format("dd-MM-yyyy HH:mm") : ''}</td>
                <td>${tramite?.estadoTramite.descripcion}</td>
                <td id="${tramite?.id}" class="ck text-center">
                    <g:if test="${tramite.estadoTramite.codigo == 'E001'}">
                        <g:checkBox name="porEnviar" tramite="${tramite?.id}" style="margin-left: 30px" class="form-control combo" checked="false"/>
                    </g:if>
                </td>
            </tr>
        </g:each>
    </tbody>
</table>

