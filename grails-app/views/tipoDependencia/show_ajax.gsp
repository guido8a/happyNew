
<%@ page import="happy.tramites.TipoDependencia" %>


<g:if test="${!tipoDependenciaInstance}">
    <elm:notFound elem="TipoDependencia" genero="o" />
</g:if>
<g:else>
    <g:if test="${tipoDependenciaInstance?.codigo}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Codigo
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${tipoDependenciaInstance}" field="codigo"/>
            </div>
            
            
    </g:if>
    
<g:if test="${!tipoDependenciaInstance}">
    <elm:notFound elem="TipoDependencia" genero="o" />
</g:if>
<g:else>
    <g:if test="${tipoDependenciaInstance?.descripcion}">
        
            <div class="col-md-2 text-info">
                Descripcion
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${tipoDependenciaInstance}" field="descripcion"/>
            </div>
            
            
        </div>
        
    </g:if>
    
</g:else>