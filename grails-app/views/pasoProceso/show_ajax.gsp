
<%@ page import="happy.PasoProceso" %>


<g:if test="${!pasoProcesoInstance}">
    <elm:notFound elem="PasoProceso" genero="o" />
</g:if>
<g:else>
    <g:if test="${pasoProcesoInstance?.proceso}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Proceso
            </div>
            
            <div class="col-md-3">
                ${pasoProcesoInstance?.proceso?.encodeAsHTML()}</g:link>
            </div>
            
            
    </g:if>
    
<g:if test="${!pasoProcesoInstance}">
    <elm:notFound elem="PasoProceso" genero="o" />
</g:if>
<g:else>
    <g:if test="${pasoProcesoInstance?.padre}">
        
            <div class="col-md-2 text-info">
                Padre
            </div>
            
            <div class="col-md-3">
                ${pasoProcesoInstance?.padre?.encodeAsHTML()}</g:link>
            </div>
            
            
    </g:if>
    
<g:if test="${!pasoProcesoInstance}">
    <elm:notFound elem="PasoProceso" genero="o" />
</g:if>
<g:else>
    <g:if test="${pasoProcesoInstance?.nombre}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Nombre
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${pasoProcesoInstance}" field="nombre"/>
            </div>
            
            
        </div>
        
    </g:if>
    
<g:if test="${!pasoProcesoInstance}">
    <elm:notFound elem="PasoProceso" genero="o" />
</g:if>
<g:else>
    <g:if test="${pasoProcesoInstance?.orden}">
        
            <div class="col-md-2 text-info">
                Orden
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${pasoProcesoInstance}" field="orden"/>
            </div>
            
            
    </g:if>
    
<g:if test="${!pasoProcesoInstance}">
    <elm:notFound elem="PasoProceso" genero="o" />
</g:if>
<g:else>
    <g:if test="${pasoProcesoInstance?.tiempo}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Tiempo
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${pasoProcesoInstance}" field="tiempo"/>
            </div>
            
            
        </div>
        
    </g:if>
    
<g:if test="${!pasoProcesoInstance}">
    <elm:notFound elem="PasoProceso" genero="o" />
</g:if>
<g:else>
    <g:if test="${pasoProcesoInstance?.funciones}">
        
            <div class="col-md-2 text-info">
                Funciones
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${pasoProcesoInstance}" field="funciones"/>
            </div>
            
            
        </div>
        
    </g:if>
    
</g:else>