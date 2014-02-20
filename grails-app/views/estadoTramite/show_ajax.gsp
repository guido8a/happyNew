
<%@ page import="happy.tramites.EstadoTramite" %>

<g:if test="${!estadoTramiteInstance}">
    <elm:notFound elem="EstadoTramite" genero="o" />
</g:if>
<g:else>

    <g:if test="${estadoTramiteInstance?.codigo}">
        <div class="row">
            <div class="col-md-2 text-info">
                Código
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${estadoTramiteInstance}" field="codigo"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${estadoTramiteInstance?.descripcion}">
        <div class="row">
            <div class="col-md-2 text-info">
                Descripción
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${estadoTramiteInstance}" field="descripcion"/>
            </div>
            
        </div>
    </g:if>
    
</g:else>