
<%@ page import="happy.tramites.PersonaDocumentoTramite" %>

<g:if test="${!personaDocumentoTramiteInstance}">
    <elm:notFound elem="PersonaDocumentoTramite" genero="o" />
</g:if>
<g:else>

    <g:if test="${personaDocumentoTramiteInstance?.rolPersonaTramite}">
        <div class="row">
            <div class="col-md-2 text-info">
                Rol Persona Tramite
            </div>
            
            <div class="col-md-3">
                ${personaDocumentoTramiteInstance?.rolPersonaTramite?.encodeAsHTML()}
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${personaDocumentoTramiteInstance?.persona}">
        <div class="row">
            <div class="col-md-2 text-info">
                Persona
            </div>
            
            <div class="col-md-3">
                ${personaDocumentoTramiteInstance?.persona?.encodeAsHTML()}
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${personaDocumentoTramiteInstance?.tramite}">
        <div class="row">
            <div class="col-md-2 text-info">
                Tramite
            </div>
            
            <div class="col-md-3">
                ${personaDocumentoTramiteInstance?.tramite?.encodeAsHTML()}
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${personaDocumentoTramiteInstance?.fecha}">
        <div class="row">
            <div class="col-md-2 text-info">
                Fecha
            </div>
            
            <div class="col-md-3">
                <g:formatDate date="${personaDocumentoTramiteInstance?.fecha}" format="dd-MM-yyyy" />
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${personaDocumentoTramiteInstance?.observaciones}">
        <div class="row">
            <div class="col-md-2 text-info">
                Observaciones
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${personaDocumentoTramiteInstance}" field="observaciones"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${personaDocumentoTramiteInstance?.permiso}">
        <div class="row">
            <div class="col-md-2 text-info">
                Permiso
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${personaDocumentoTramiteInstance}" field="permiso"/>
            </div>
            
        </div>
    </g:if>
    
</g:else>