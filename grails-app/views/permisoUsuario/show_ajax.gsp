
<%@ page import="happy.tramites.PermisoUsuario" %>


<g:if test="${!permisoUsuarioInstance}">
    <elm:notFound elem="PermisoUsuario" genero="o" />
</g:if>
<g:else>
    <g:if test="${permisoUsuarioInstance?.persona}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Persona
            </div>
            
            <div class="col-md-3">
                ${permisoUsuarioInstance?.persona?.encodeAsHTML()}
            </div>
            
            
    </g:if>
</g:else>

<g:if test="${!permisoUsuarioInstance}">
    <elm:notFound elem="PermisoUsuario" genero="o" />
</g:if>
<g:else>
    <g:if test="${permisoUsuarioInstance?.permisoTramite}">
        
            <div class="col-md-2 text-info">
                Permiso Tramite
            </div>
            
            <div class="col-md-3">
                ${permisoUsuarioInstance?.permisoTramite?.encodeAsHTML()}
            </div>
            
            
    </g:if>
</g:else>

<g:if test="${!permisoUsuarioInstance}">
    <elm:notFound elem="PermisoUsuario" genero="o" />
</g:if>
<g:else>
    <g:if test="${permisoUsuarioInstance?.fechaInicio}">
        
        <div class="row">
            
            <div class="col-md-2 text-info">
                Fecha Inicio
            </div>
            
            <div class="col-md-3">
                <g:formatDate date="${permisoUsuarioInstance?.fechaInicio}" format="dd-MM-yyyy" />
            </div>
            
            
        </div>
        
    </g:if>
</g:else>
<g:if test="${!permisoUsuarioInstance}">
    <elm:notFound elem="PermisoUsuario" genero="o" />
</g:if>
<g:else>
    <g:if test="${permisoUsuarioInstance?.fechaFin}">
        
            <div class="col-md-2 text-info">
                Fecha Fin
            </div>
            
            <div class="col-md-3">
                <g:formatDate date="${permisoUsuarioInstance?.fechaFin}" format="dd-MM-yyyy" />
            </div>
            
            
        </div>
        
    </g:if>
    
</g:else>