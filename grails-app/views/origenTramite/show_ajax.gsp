
<%@ page import="happy.tramites.OrigenTramite" %>

<g:if test="${!origenTramiteInstance}">
    <elm:notFound elem="OrigenTramite" genero="o" />
</g:if>
<g:else>

    <g:if test="${origenTramiteInstance?.tipoPersona}">
        <div class="row">
            <div class="col-md-3 text-info">
                Tipo Persona
            </div>
            
            <div class="col-md-4">
                ${origenTramiteInstance?.tipoPersona?.descripcion}
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${origenTramiteInstance?.cedula}">
        <div class="row">
            <div class="col-md-3 text-info">
                Cedula
            </div>
            
            <div class="col-md-4">
                <g:fieldValue bean="${origenTramiteInstance}" field="cedula"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${origenTramiteInstance?.fecha}">
        <div class="row">
            <div class="col-md-3 text-info">
                Fecha
            </div>
            
            <div class="col-md-4">
                <g:formatDate date="${origenTramiteInstance?.fecha}" format="dd-MM-yyyy" />
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${origenTramiteInstance?.nombre}">
        <div class="row">
            <div class="col-md-3 text-info">
                Nombre
            </div>
            
            <div class="col-md-4">
                <g:fieldValue bean="${origenTramiteInstance}" field="nombre"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${origenTramiteInstance?.nombreContacto}">
        <div class="row">
            <div class="col-md-3 text-info">
                Nombre Contacto
            </div>
            
            <div class="col-md-4">
                <g:fieldValue bean="${origenTramiteInstance}" field="nombreContacto"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${origenTramiteInstance?.apellidoContacto}">
        <div class="row">
            <div class="col-md-3 text-info">
                Apellido Contacto
            </div>
            
            <div class="col-md-4">
                <g:fieldValue bean="${origenTramiteInstance}" field="apellidoContacto"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${origenTramiteInstance?.titulo}">
        <div class="row">
            <div class="col-md-3 text-info">
                Titulo
            </div>
            
            <div class="col-md-4">
                <g:fieldValue bean="${origenTramiteInstance}" field="titulo"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${origenTramiteInstance?.cargo}">
        <div class="row">
            <div class="col-md-3 text-info">
                Cargo
            </div>
            
            <div class="col-md-4">
                <g:fieldValue bean="${origenTramiteInstance}" field="cargo"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${origenTramiteInstance?.mail}">
        <div class="row">
            <div class="col-md-3 text-info">
                Mail
            </div>
            
            <div class="col-md-4">
                <g:fieldValue bean="${origenTramiteInstance}" field="mail"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${origenTramiteInstance?.telefono}">
        <div class="row">
            <div class="col-md-3 text-info">
                Telefono
            </div>
            
            <div class="col-md-4">
                <g:fieldValue bean="${origenTramiteInstance}" field="telefono"/>
            </div>
            
        </div>
    </g:if>
    
</g:else>