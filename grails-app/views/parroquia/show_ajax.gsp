
<%@ page import="happy.geografia.Parroquia" %>


<g:if test="${!parroquiaInstance}">
    <elm:notFound elem="Parroquia" genero="o" />
</g:if>
<g:else>
    <g:if test="${parroquiaInstance?.canton}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Canton
            </div>
            
            <div class="col-md-3">
                ${parroquiaInstance?.canton?.encodeAsHTML()}</g:link>
            </div>
            
            
    </g:if>
    
<g:if test="${!parroquiaInstance}">
    <elm:notFound elem="Parroquia" genero="o" />
</g:if>
<g:else>
    <g:if test="${parroquiaInstance?.codigo}">
        
            <div class="col-md-2 text-info">
                Codigo
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${parroquiaInstance}" field="codigo"/>
            </div>
            
            
    </g:if>
    
<g:if test="${!parroquiaInstance}">
    <elm:notFound elem="Parroquia" genero="o" />
</g:if>
<g:else>
    <g:if test="${parroquiaInstance?.nombre}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Nombre
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${parroquiaInstance}" field="nombre"/>
            </div>
            
            
        </div>
        
    </g:if>
    
</g:else>