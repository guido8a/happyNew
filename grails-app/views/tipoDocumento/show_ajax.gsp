
<%@ page import="happy.tramites.TipoDocumento" %>

<g:if test="${!tipoDocumentoInstance}">
    <elm:notFound elem="TipoDocumento" genero="o" />
</g:if>
<g:else>

    <g:if test="${tipoDocumentoInstance?.codigo}">
        <div class="row">
            <div class="col-md-2 text-info">
                Código
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${tipoDocumentoInstance}" field="codigo"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${tipoDocumentoInstance?.descripcion}">
        <div class="row">
            <div class="col-md-2 text-info">
                Descripción
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${tipoDocumentoInstance}" field="descripcion"/>
            </div>
            
        </div>
    </g:if>
    
</g:else>