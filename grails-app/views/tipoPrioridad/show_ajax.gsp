
<%@ page import="happy.tramites.TipoPrioridad" %>


<g:if test="${!tipoPrioridadInstance}">
    <elm:notFound elem="TipoPrioridad" genero="o" />
</g:if>
<g:else>
    <g:if test="${tipoPrioridadInstance?.codigo}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Codigo
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${tipoPrioridadInstance}" field="codigo"/>
            </div>
            
            
    </g:if>
    
<g:if test="${!tipoPrioridadInstance}">
    <elm:notFound elem="TipoPrioridad" genero="o" />
</g:if>
<g:else>
    <g:if test="${tipoPrioridadInstance?.descripcion}">
        
            <div class="col-md-2 text-info">
                Descripcion
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${tipoPrioridadInstance}" field="descripcion"/>
            </div>
            
            
        </div>
        
    </g:if>
    
</g:else>