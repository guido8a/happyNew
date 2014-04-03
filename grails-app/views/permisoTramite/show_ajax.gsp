
<%@ page import="happy.tramites.PermisoTramite" %>

<g:if test="${!permisoTramiteInstance}">
    <elm:notFound elem="PermisoTramite" genero="o" />
</g:if>
<g:else>

    <g:if test="${permisoTramiteInstance?.codigo}">
        <div class="row">
            <div class="col-md-2 text-info">
                Codigo
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${permisoTramiteInstance}" field="codigo"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${permisoTramiteInstance?.descripcion}">
        <div class="row">
            <div class="col-md-2 text-info">
                Descripcion
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${permisoTramiteInstance}" field="descripcion"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${permisoTramiteInstance?.texto}">
        <div class="row">
            <div class="col-md-2 text-info">
                Texto
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${permisoTramiteInstance}" field="texto"/>
            </div>
            
        </div>
    </g:if>
    
</g:else>