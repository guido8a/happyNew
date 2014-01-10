
<%@ page import="happy.geografia.Comunidad" %>


<g:if test="${!comunidadInstance}">
    <elm:notFound elem="Comunidad" genero="o" />
</g:if>
<g:else>
    <g:if test="${comunidadInstance?.parroquia}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Parroquia
            </div>
            
            <div class="col-md-3">
                ${comunidadInstance?.parroquia?.encodeAsHTML()}</g:link>
            </div>
            
            
    </g:if>
    
<g:if test="${!comunidadInstance}">
    <elm:notFound elem="Comunidad" genero="o" />
</g:if>
<g:else>
    <g:if test="${comunidadInstance?.numero}">
        
            <div class="col-md-2 text-info">
                Numero
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${comunidadInstance}" field="numero"/>
            </div>
            
            
    </g:if>
    
<g:if test="${!comunidadInstance}">
    <elm:notFound elem="Comunidad" genero="o" />
</g:if>
<g:else>
    <g:if test="${comunidadInstance?.nombre}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Nombre
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${comunidadInstance}" field="nombre"/>
            </div>
            
            
        </div>
        
    </g:if>
    
</g:else>