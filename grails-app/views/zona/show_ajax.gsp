
<%@ page import="happy.geografia.Zona" %>


<g:if test="${!zonaInstance}">
    <elm:notFound elem="Zona" genero="o" />
</g:if>
<g:else>
    <g:if test="${zonaInstance?.numero}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Numero
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${zonaInstance}" field="numero"/>
            </div>
            
            
    </g:if>
    
<g:if test="${!zonaInstance}">
    <elm:notFound elem="Zona" genero="o" />
</g:if>
<g:else>
    <g:if test="${zonaInstance?.nombre}">
        
            <div class="col-md-2 text-info">
                Nombre
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${zonaInstance}" field="nombre"/>
            </div>
            
            
        </div>
        
    </g:if>
    
</g:else>