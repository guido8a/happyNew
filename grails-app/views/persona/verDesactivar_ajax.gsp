%{--<%@ page import="happy.seguridad.Persona" %>--}%
%{--<g:select name="cmbRedirect" from="${Persona.withCriteria {--}%
%{--eq("departamento", session.departamento)--}%
%{--}.findAll { it.estaActivo }}" class="form-control"/>--}%

<p>¿Está seguro que desea desactivar la persona seleccionada?</p>
<g:if test="${tramites > 0}">
    <p>${tramites} trámite${tramites == 1 ? '' : 's'} será${tramites == 1 ? '' : 'n'}
    redireccionados de su bandeja de entrada personal.</p>
</g:if>
<g:else>
    <p>No tiene trámites en su bandeja de entrada personal.</p>
</g:else>