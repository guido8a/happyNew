
<%@ page import="happy.geografia.Provincia" %>


<g:if test="${!provinciaInstance}">
    <elm:notFound elem="Provincia" genero="o" />
</g:if>
<g:else>
    <g:if test="${provinciaInstance?.zona}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Zona
            </div>
            
            <div class="col-md-3">
                ${provinciaInstance?.zona?.encodeAsHTML()}</g:link>
            </div>
            
            
    </g:if>
    
<g:if test="${!provinciaInstance}">
    <elm:notFound elem="Provincia" genero="o" />
</g:if>
<g:else>
    <g:if test="${provinciaInstance?.numero}">
        
            <div class="col-md-2 text-info">
                Numero
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${provinciaInstance}" field="numero"/>
            </div>
            
            
    </g:if>
    
<g:if test="${!provinciaInstance}">
    <elm:notFound elem="Provincia" genero="o" />
</g:if>
<g:else>
    <g:if test="${provinciaInstance?.nombre}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Nombre
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${provinciaInstance}" field="nombre"/>
            </div>
            
            
        </div>
        
    </g:if>
    
</g:else>