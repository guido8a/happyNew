
<%@ page import="happy.tramites.Departamento" %>


<g:if test="${!departamentoInstance}">
    <elm:notFound elem="Departamento" genero="o" />
</g:if>
<g:else>
    <g:if test="${departamentoInstance?.tipoDepartamento}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Tipo Departamento
            </div>
            
            <div class="col-md-3">
                ${departamentoInstance?.tipoDepartamento?.encodeAsHTML()}</g:link>
            </div>
            
            
    </g:if>
    
<g:if test="${!departamentoInstance}">
    <elm:notFound elem="Departamento" genero="o" />
</g:if>
<g:else>
    <g:if test="${departamentoInstance?.padre}">
        
            <div class="col-md-2 text-info">
                Padre
            </div>
            
            <div class="col-md-3">
                ${departamentoInstance?.padre?.encodeAsHTML()}</g:link>
            </div>
            
            
    </g:if>
    
<g:if test="${!departamentoInstance}">
    <elm:notFound elem="Departamento" genero="o" />
</g:if>
<g:else>
    <g:if test="${departamentoInstance?.codigo}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Codigo
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${departamentoInstance}" field="codigo"/>
            </div>
            
            
        </div>
        
    </g:if>
    
<g:if test="${!departamentoInstance}">
    <elm:notFound elem="Departamento" genero="o" />
</g:if>
<g:else>
    <g:if test="${departamentoInstance?.descripcion}">
        
            <div class="col-md-2 text-info">
                Descripcion
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${departamentoInstance}" field="descripcion"/>
            </div>
            
            
    </g:if>
    
<g:if test="${!departamentoInstance}">
    <elm:notFound elem="Departamento" genero="o" />
</g:if>
<g:else>
    <g:if test="${departamentoInstance?.telefono}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Telefono
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${departamentoInstance}" field="telefono"/>
            </div>
            
            
        </div>
        
    </g:if>
    
<g:if test="${!departamentoInstance}">
    <elm:notFound elem="Departamento" genero="o" />
</g:if>
<g:else>
    <g:if test="${departamentoInstance?.extension}">
        
            <div class="col-md-2 text-info">
                Extension
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${departamentoInstance}" field="extension"/>
            </div>
            
            
    </g:if>
    
<g:if test="${!departamentoInstance}">
    <elm:notFound elem="Departamento" genero="o" />
</g:if>
<g:else>
    <g:if test="${departamentoInstance?.direccion}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Direccion
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${departamentoInstance}" field="direccion"/>
            </div>
            
            
        </div>
        
    </g:if>
    
</g:else>