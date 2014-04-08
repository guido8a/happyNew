<g:if test="${tramites}">
    <table class="table table-bordered table-condensed table-hover">
        <thead>
        <tr>
            <th class="cabecera">Documento</th>
            <th class="cabecera">De</th>
            <th class="cabecera">Para</th>
            <th class="cabecera">Asunto</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${tramites}" var="tramite">
            <g:set var="now" value="${new java.util.Date()}"/>
            <tr data-id="${tramite?.tramite?.id}">
                <td>${tramite?.tramite?.codigo}</td>
                <g:if test="${tramite.tramite.deDepartamento}">
                    <td title="${tramite?.tramite?.de?.departamento?.descripcion}">${tramite?.tramite?.de?.departamento?.codigo}</td>
                </g:if>
                <g:else>
                    <td title="${tramite?.tramite?.de}">${tramite?.tramite?.de}</td>
                </g:else>
                <g:if test="${tramite.departamento}">
                    <td title="${tramite.departamento?.descripcion}">${tramite.departamento?.codigo}</td>
                </g:if>
                <g:else>
                    <td title="${tramite?.persona}">${tramite?.persona}</td>
                </g:else>
                <td>${tramite?.tramite?.asunto}</td>
                <td>
                    <input type="checkbox" class="chk" iden="${tramite.tramite.id}">
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</g:if>
<g:else>
    No hay trámites elegibles para adjuntar
</g:else>