
<%@ page import="happy.tramites.TipoDepartamento" %>

<g:if test="${!tipoDepartamentoInstance}">
    <elm:notFound elem="TipoDepartamento" genero="o" />
</g:if>
<g:else>

    <g:if test="${tipoDepartamentoInstance?.codigo}">
        <div class="row">
            <div class="col-md-2 text-info">
                Código
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${tipoDepartamentoInstance}" field="codigo"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${tipoDepartamentoInstance?.descripcion}">
        <div class="row">
            <div class="col-md-2 text-info">
                Descripción
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${tipoDepartamentoInstance}" field="descripcion"/>
            </div>
            
        </div>
    </g:if>
    
</g:else>