
<%@ page import="happy.tramites.ObservacionTramite" %>

<g:if test="${!observacionTramiteInstance}">
    <elm:notFound elem="ObservacionTramite" genero="o" />
</g:if>
<g:else>

    <g:if test="${observacionTramiteInstance?.tramite}">
        <div class="row">
            <div class="col-md-2 text-info">
                Tramite
            </div>
            
            <div class="col-md-3">
                ${observacionTramiteInstance?.tramite?.encodeAsHTML()}
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${observacionTramiteInstance?.persona}">
        <div class="row">
            <div class="col-md-2 text-info">
                Persona
            </div>
            
            <div class="col-md-3">
                ${observacionTramiteInstance?.persona?.encodeAsHTML()}
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${observacionTramiteInstance?.fecha}">
        <div class="row">
            <div class="col-md-2 text-info">
                Fecha
            </div>
            
            <div class="col-md-3">
                <g:formatDate date="${observacionTramiteInstance?.fecha}" format="dd-MM-yyyy" />
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${observacionTramiteInstance?.observaciones}">
        <div class="row">
            <div class="col-md-2 text-info">
                Observaciones
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${observacionTramiteInstance}" field="observaciones"/>
            </div>
            
        </div>
    </g:if>
    
</g:else>