
<%@ page import="happy.tramites.DocumentoTramite" %>

<g:if test="${!documentoTramiteInstance}">
    <elm:notFound elem="DocumentoTramite" genero="o" />
</g:if>
<g:else>

    <g:if test="${documentoTramiteInstance?.tramite}">
        <div class="row">
            <div class="col-md-2 text-info">
                Tramite
            </div>
            
            <div class="col-md-3">
                ${documentoTramiteInstance?.tramite?.encodeAsHTML()}
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${documentoTramiteInstance?.anexo}">
        <div class="row">
            <div class="col-md-2 text-info">
                Anexo
            </div>
            
            <div class="col-md-3">
                ${documentoTramiteInstance?.anexo?.encodeAsHTML()}
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${documentoTramiteInstance?.fecha}">
        <div class="row">
            <div class="col-md-2 text-info">
                Fecha
            </div>
            
            <div class="col-md-3">
                <g:formatDate date="${documentoTramiteInstance?.fecha}" format="dd-MM-yyyy" />
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${documentoTramiteInstance?.resumen}">
        <div class="row">
            <div class="col-md-2 text-info">
                Resumen
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${documentoTramiteInstance}" field="resumen"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${documentoTramiteInstance?.clave}">
        <div class="row">
            <div class="col-md-2 text-info">
                Clave
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${documentoTramiteInstance}" field="clave"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${documentoTramiteInstance?.path}">
        <div class="row">
            <div class="col-md-2 text-info">
                Path
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${documentoTramiteInstance}" field="path"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${documentoTramiteInstance?.descripcion}">
        <div class="row">
            <div class="col-md-2 text-info">
                Descripcion
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${documentoTramiteInstance}" field="descripcion"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${documentoTramiteInstance?.fechaLectura}">
        <div class="row">
            <div class="col-md-2 text-info">
                Fecha Lectura
            </div>
            
            <div class="col-md-3">
                <g:formatDate date="${documentoTramiteInstance?.fechaLectura}" format="dd-MM-yyyy" />
            </div>
            
        </div>
    </g:if>
    
</g:else>