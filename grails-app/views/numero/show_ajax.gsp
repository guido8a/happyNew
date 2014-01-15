
<%@ page import="happy.tramites.Numero" %>

<g:if test="${!numeroInstance}">
    <elm:notFound elem="Numero" genero="o" />
</g:if>
<g:else>

    <g:if test="${numeroInstance?.departamento}">
        <div class="row">
            <div class="col-md-2 text-info">
                Departamento
            </div>
            
            <div class="col-md-3">
                ${numeroInstance?.departamento?.encodeAsHTML()}
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${numeroInstance?.tipoDocumento}">
        <div class="row">
            <div class="col-md-2 text-info">
                Tipo Documento
            </div>
            
            <div class="col-md-3">
                ${numeroInstance?.tipoDocumento?.encodeAsHTML()}
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${numeroInstance?.valor}">
        <div class="row">
            <div class="col-md-2 text-info">
                Valor
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${numeroInstance}" field="valor"/>
            </div>
            
        </div>
    </g:if>
    
</g:else>