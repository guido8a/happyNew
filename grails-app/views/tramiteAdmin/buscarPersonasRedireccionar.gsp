<table class="table table table-bordered table-condensed">
    <thead>
        <tr>
            <th>Nombre</th>
            <th>Apellido</th>
            <th>Usuario</th>
            <th>Departamento</th>
            <th>Redireccionar trámites</th>
            <th>Trámites</th>
        </tr>
    </thead>
    <tbody>
        <g:each in="${personas.persona}" var="per">
            <tr>
                <td>
                    ${per.nombre}
                </td>
                <td>
                    ${per.apellido}
                </td>
                <td>
                    ${per.login}
                </td>
                <td>
                    <g:if test="${per.departamento}">
                        ${per.departamento?.descripcion} (${per.departamento?.codigo})
                    </g:if>
                </td>
                <td>
                    <g:link class="btn btn-success" controller="tramiteAdmin" action="redireccionarTramites" id="${per.id}">
                        <i class="fa fa-link"></i>
                    </g:link>
                </td>
                <td>
                    %{--${per.id}--}%
%{--
                    <g:if test="${happy.tramites.PersonaDocumentoTramite.findAllByPersonaAndRolPersonaTramite(happy.seguridad.Persona.get(per.id),
                            happy.tramites.RolPersonaTramite.findAllByCodigoOrCodigo('R001', 'R002'))}">
                        Tiene trámites
                    </g:if>
--}%
                    %{--"${personas.tieneTrmt[0]}"--}%
                    <g:if test="${personas.tieneTrmt[0] == 'S'}">
                        Si Tiene trámites
                    </g:if>
                    <g:else>
                        No tiene trámites
                    </g:else>
                </td>
            </tr>
        </g:each>
    </tbody>
</table>