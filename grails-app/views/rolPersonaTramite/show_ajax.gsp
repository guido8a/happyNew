
<%@ page import="happy.tramites.RolPersonaTramite" %>

<g:if test="${!rolPersonaTramiteInstance}">
    <elm:notFound elem="RolPersonaTramite" genero="o" />
</g:if>
<g:else>

    <g:if test="${rolPersonaTramiteInstance?.codigo}">
        <div class="row">
            <div class="col-md-2 text-info">
                Código
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${rolPersonaTramiteInstance}" field="codigo"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${rolPersonaTramiteInstance?.descripcion}">
        <div class="row">
            <div class="col-md-2 text-info">
                Descripción
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${rolPersonaTramiteInstance}" field="descripcion"/>
            </div>
            
        </div>
    </g:if>
    
</g:else>