
<%@ page import="happy.utilitarios.Parametros" %>

<g:if test="${!parametrosInstance}">
    <elm:notFound elem="Parametros" genero="o" />
</g:if>
<g:else>

    <g:if test="${parametrosInstance?.horaInicio}">
        <div class="row">
            <div class="col-md-2 text-info">
                Hora Inicio
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${parametrosInstance}" field="horaInicio"/> :
                <g:fieldValue bean="${parametrosInstance}" field="minutoInicio"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.horaFin}">
        <div class="row">
            <div class="col-md-2 text-info">
                Hora Fin
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${parametrosInstance}" field="horaFin"/> :
                <g:fieldValue bean="${parametrosInstance}" field="minutoFin"/>
            </div>
            
        </div>
    </g:if>

    <g:if test="${parametrosInstance?.ipLDAP}">
        <div class="row">
            <div class="col-md-2 text-info">
                IP LDAP y puerto:
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${parametrosInstance}" field="ipLDAP"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.ouPrincipal}">
        <div class="row">
            <div class="col-md-2 text-info">
                OU Principal
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${parametrosInstance}" field="ouPrincipal"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.textoCn}">
        <div class="row">
            <div class="col-md-2 text-info">
                Texto Cn
            </div>
            
            <div class="col-md-3" style="font-family: 'Courier New', Courier, monospace; font-size: 12px;">
                %{--<g:fieldValue bean="${parametrosInstance}" field="textoCn"/>--}%
                ${parametrosInstance.textoCn}
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.passAdm}">
        <div class="row">
            <div class="col-md-2 text-info">
                Pass Adm
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${parametrosInstance}" field="passAdm"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.imagenes}">
        <div class="row">
            <div class="col-md-2 text-info">
                Imagenes
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${parametrosInstance}" field="imagenes"/>
            </div>
            
        </div>
    </g:if>
    
</g:else>