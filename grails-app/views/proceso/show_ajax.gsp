
<%@ page import="happy.Proceso" %>


<g:if test="${!procesoInstance}">
    <elm:notFound elem="Proceso" genero="o" />
</g:if>
<g:else>
    <g:if test="${procesoInstance?.tipoProceso}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Tipo Proceso
            </div>
            
            <div class="col-md-3">
                ${procesoInstance?.tipoProceso?.encodeAsHTML()}</g:link>
            </div>
            
            
    </g:if>
    
<g:if test="${!procesoInstance}">
    <elm:notFound elem="Proceso" genero="o" />
</g:if>
<g:else>
    <g:if test="${procesoInstance?.nombre}">
        
            <div class="col-md-2 text-info">
                Nombre
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${procesoInstance}" field="nombre"/>
            </div>
            
            
    </g:if>
    
<g:if test="${!procesoInstance}">
    <elm:notFound elem="Proceso" genero="o" />
</g:if>
<g:else>
    <g:if test="${procesoInstance?.descripcion}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Descripcion
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${procesoInstance}" field="descripcion"/>
            </div>
            
            
        </div>
        
    </g:if>
    
<g:if test="${!procesoInstance}">
    <elm:notFound elem="Proceso" genero="o" />
</g:if>
<g:else>
    <g:if test="${procesoInstance?.numero}">
        
            <div class="col-md-2 text-info">
                Numero
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${procesoInstance}" field="numero"/>
            </div>
            
            
    </g:if>
    
<g:if test="${!procesoInstance}">
    <elm:notFound elem="Proceso" genero="o" />
</g:if>
<g:else>
    <g:if test="${procesoInstance?.tiempo}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Tiempo
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${procesoInstance}" field="tiempo"/>
            </div>
            
            
        </div>
        
    </g:if>
    
<g:if test="${!procesoInstance}">
    <elm:notFound elem="Proceso" genero="o" />
</g:if>
<g:else>
    <g:if test="${procesoInstance?.fecha}">
        
            <div class="col-md-2 text-info">
                Fecha
            </div>
            
            <div class="col-md-3">
                <g:formatDate date="${procesoInstance?.fecha}" format="dd-MM-yyyy" />
            </div>
            
            
    </g:if>
    
<g:if test="${!procesoInstance}">
    <elm:notFound elem="Proceso" genero="o" />
</g:if>
<g:else>
    <g:if test="${procesoInstance?.observaciones}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Observaciones
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${procesoInstance}" field="observaciones"/>
            </div>
            
            
        </div>
        
    </g:if>
    
</g:else>