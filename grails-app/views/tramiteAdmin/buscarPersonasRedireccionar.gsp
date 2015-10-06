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
        <g:each in="${personas}" var="per">
            <tr>
                <td>
                    ${per.persona.nombre}
                </td>
                <td>
                    ${per.persona.apellido}
                </td>
                <td>
                    ${per.persona.login}
                </td>
                <td>
                    <g:if test="${per.departamento}">
                        ${per.persona.departamento?.descripcion} (${per.persona.departamento?.codigo})
                    </g:if>
                </td>
                <td>
                    <g:link class="btn btn-success" controller="tramiteAdmin" action="redireccionarTramites" id="${per.persona.id}">
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
                    Bandeja de entrada: ${per.tieneTrmt} trámites<br/>
                    Bandeja de salida:  ${per.bandejaSalida} trámites<br/>
                </td>
            </tr>
        </g:each>
    </tbody>
</table>