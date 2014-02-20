
<%@ page import="happy.tramites.TipoPrioridad" %>

<g:if test="${!tipoPrioridadInstance}">
    <elm:notFound elem="TipoPrioridad" genero="o" />
</g:if>
<g:else>

    <g:if test="${tipoPrioridadInstance?.codigo}">
        <div class="row">
            <div class="col-md-2 text-info">
                Código
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${tipoPrioridadInstance}" field="codigo"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${tipoPrioridadInstance?.descripcion}">
        <div class="row">
            <div class="col-md-2 text-info">
                Descripción
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${tipoPrioridadInstance}" field="descripcion"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${tipoPrioridadInstance?.tiempo}">
        <div class="row">
            <div class="col-md-2 text-info">
                Tiempo
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${tipoPrioridadInstance}" field="tiempo"/>
            </div>
            
        </div>
    </g:if>
    
</g:else>