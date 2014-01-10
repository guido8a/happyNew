
<%@ page import="happy.TipoProceso" %>


<g:if test="${!tipoProcesoInstance}">
    <elm:notFound elem="TipoProceso" genero="o" />
</g:if>
<g:else>
    <g:if test="${tipoProcesoInstance?.codigo}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Codigo
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${tipoProcesoInstance}" field="codigo"/>
            </div>
            
            
    </g:if>
    
<g:if test="${!tipoProcesoInstance}">
    <elm:notFound elem="TipoProceso" genero="o" />
</g:if>
<g:else>
    <g:if test="${tipoProcesoInstance?.descripcion}">
        
            <div class="col-md-2 text-info">
                Descripcion
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${tipoProcesoInstance}" field="descripcion"/>
            </div>
            
            
        </div>
        
    </g:if>
    
</g:else>