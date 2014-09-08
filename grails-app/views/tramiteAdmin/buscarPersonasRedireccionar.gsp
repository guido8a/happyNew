<table class="table table table-bordered table-condensed">
    <thead>
        <tr>
            <th>Nombre</th>
            <th>Apellido</th>
            <th>Usuario</th>
            <th>Departamento</th>
            <th>Redireccionar trámites</th>
        </tr>
    </thead>
    <tbody>
        <g:each in="${personas}" var="per">
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
            </tr>
        </g:each>
    </tbody>
</table>