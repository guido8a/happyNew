<%@ page import="happy.seguridad.Persona" %>


<p>¿Está seguro que desea desactivar la persona seleccionada?</p>
<g:if test="${tramites > 0}">
    <p style="font-size: larger;">
        ${tramites} trámite${tramites == 1 ? '' : 's'} será${tramites == 1 ? '' : 'n'}
        redireccionados de su bandeja de entrada personal a la bandeja seleccionada.
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