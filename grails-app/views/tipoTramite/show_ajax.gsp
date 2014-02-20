
<%@ page import="happy.tramites.TipoTramite" %>

<g:if test="${!tipoTramiteInstance}">
    <elm:notFound elem="TipoTramite" genero="o" />
</g:if>
<g:else>

    <g:if test="${tipoTramiteInstance?.codigo}">
        <div class="row">
            <div class="col-md-2 text-info">
                Código
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${tipoTramiteInstance}" field="codigo"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${tipoTramiteInstance?.descripcion}">
        <div class="row">
            <div class="col-md-2 text-info">
                Descripción
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${tipoTramiteInstance}" field="descripcion"/>
            </div>
            
        </div>
    </g:if>
    
</g:else>