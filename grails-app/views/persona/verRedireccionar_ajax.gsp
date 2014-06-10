<%@ page import="happy.seguridad.Persona" %>

<g:if test="${tramites > 0}">
    <p style="font-size: larger;">
        ${tramites} trámite${tramites == 1 ? '' : 's'} será${tramites == 1 ? '' : 'n'}
        redireccionado${tramites == 1 ? '' : 's'} de la bandeja de entrada personal
        de <strong>${persona.nombre} ${persona.apellido}</strong> a la bandeja seleccionada.
    </p>

    <p>
        <g:select name="cmbRedirect" from="${Persona.withCriteria {
            eq("departamento", persona.departamento)
            ne("id", persona.id)
            order("apellido", "asc")
        }.findAll {
            it.estaActivo
        }}" class="form-control" optionKey="id" optionValue="${{ it.apellido + ' ' + it.nombre }}"
                  noSelection="['-': persona.departamento.descripcion]"/>
    </p>
</g:if>
<g:else>
    <p>
        No tiene trámites en su bandeja de entrada personal.
    </p>
</g:else>