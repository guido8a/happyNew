
<%@ page import="happy.tramites.EstadoTramiteExterno" %>

<g:if test="${!estadoTramiteExternoInstance}">
    <elm:notFound elem="EstadoTramiteExterno" genero="o" />
</g:if>
<g:else>

    <g:if test="${estadoTramiteExternoInstance?.codigo}">
        <div class="row">
            <div class="col-md-2 text-info">
                Codigo
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${estadoTramiteExternoInstance}" field="codigo"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${estadoTramiteExternoInstance?.descripcion}">
        <div class="row">
            <div class="col-md-2 text-info">
                Descripcion
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${estadoTramiteExternoInstance}" field="descripcion"/>
            </div>
            
        </div>
    </g:if>
    
</g:else>