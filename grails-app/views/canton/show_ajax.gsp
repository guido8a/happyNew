
<%@ page import="happy.geografia.Canton" %>


<g:if test="${!cantonInstance}">
    <elm:notFound elem="Canton" genero="o" />
</g:if>
<g:else>
    <g:if test="${cantonInstance?.provincia}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Provincia
            </div>
            
            <div class="col-md-3">
                ${cantonInstance?.provincia?.encodeAsHTML()}</g:link>
            </div>
            
            
    </g:if>
    
<g:if test="${!cantonInstance}">
    <elm:notFound elem="Canton" genero="o" />
</g:if>
<g:else>
    <g:if test="${cantonInstance?.numero}">
        
            <div class="col-md-2 text-info">
                Numero
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${cantonInstance}" field="numero"/>
            </div>
            
            
    </g:if>
    
<g:if test="${!cantonInstance}">
    <elm:notFound elem="Canton" genero="o" />
</g:if>
<g:else>
    <g:if test="${cantonInstance?.nombre}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Nombre
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${cantonInstance}" field="nombre"/>
            </div>
            
            
        </div>
        
    </g:if>
    
</g:else>