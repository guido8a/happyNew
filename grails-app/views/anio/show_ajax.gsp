
<%@ page import="happy.tramites.Anio" %>


<g:if test="${!anioInstance}">
    <elm:notFound elem="Anio" genero="o" />
</g:if>
<g:else>
    <g:if test="${anioInstance?.numero}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Numero
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${anioInstance}" field="numero"/>
            </div>
            
            
        </div>
        
    </g:if>
    
</g:else>