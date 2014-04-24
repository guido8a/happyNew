
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
                <g:fieldValue bean="${parametrosInstance}" field="horaInicio"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.minutoInicio}">
        <div class="row">
            <div class="col-md-2 text-info">
                Minuto Inicio
            </div>
            
            <div class="col-md-3">
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
                <g:fieldValue bean="${parametrosInstance}" field="horaFin"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.minutoFin}">
        <div class="row">
            <div class="col-md-2 text-info">
                Minuto Fin
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${parametrosInstance}" field="minutoFin"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.ipLDAP}">
        <div class="row">
            <div class="col-md-2 text-info">
                Ip LDAP
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${parametrosInstance}" field="ipLDAP"/>
            </div>
            
        </div>
    </g:if>
    
    <g:if test="${parametrosInstance?.ouPrincipal}">
        <div class="row">
            <div class="col-md-2 text-info">
                Ou Principal
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
            
            <div class="col-md-3">
                <g:fieldValue bean="${parametrosInstance}" field="textoCn"/>
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