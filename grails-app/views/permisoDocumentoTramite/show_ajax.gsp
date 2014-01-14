
<%@ page import="happy.tramites.PersonaDocumentoTramite" %>


<g:if test="${!permisoDocumentoTramiteInstance}">
    <elm:notFound elem="PermisoDocumentoTramite" genero="o" />
</g:if>
<g:else>
    <g:if test="${permisoDocumentoTramiteInstance?.tramite}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Tramite
            </div>
            
            <div class="col-md-3">
                ${permisoDocumentoTramiteInstance?.tramite?.encodeAsHTML()}</g:link>
            </div>
            
            
    </g:if>
    
<g:if test="${!permisoDocumentoTramiteInstance}">
    <elm:notFound elem="PermisoDocumentoTramite" genero="o" />
</g:if>
<g:else>
    <g:if test="${permisoDocumentoTramiteInstance?.persona}">
        
            <div class="col-md-2 text-info">
                Persona
            </div>
            
            <div class="col-md-3">
                ${permisoDocumentoTramiteInstance?.persona?.encodeAsHTML()}</g:link>
            </div>
            
            
    </g:if>
    
<g:if test="${!permisoDocumentoTramiteInstance}">
    <elm:notFound elem="PermisoDocumentoTramite" genero="o" />
</g:if>
<g:else>
    <g:if test="${permisoDocumentoTramiteInstance?.permiso}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Permiso
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${permisoDocumentoTramiteInstance}" field="permiso"/>
            </div>
            
            
        </div>
        
    </g:if>
    
</g:else>