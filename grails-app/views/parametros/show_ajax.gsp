
<%@ page import="happy.utilitarios.Parametros" %>

<g:if test="${!parametrosInstance}">
    <elm:notFound elem="Parametros" genero="o" />
</g:if>
<g:else>


    <g:if test="${parametrosInstance?.institucion}">
        <div class="row">
            <div class="col-md-2 text-info">
                Institución
            </div>

            <div class="col-md-5">
                <g:fieldValue bean="${parametrosInstance}" field="institucion"/>
            </div>

        </div>
    </g:if>

    <div class="row">
        <g:if test="${parametrosInstance?.horaInicio}">
                <div class="col-md-3 text-info">
                    Hora Inicio de la Jornada de Trabajo
                </div>

                <div class="col-md-3">
                    <g:fieldValue bean="${parametrosInstance}" field="horaInicio"/> :
                    <g:fieldValue bean="${parametrosInstance}" field="minutoInicio"/>
                </div>
        </g:if>
        <g:if test="${parametrosInstance?.horaFin}">
                <div class="col-md-3 text-info">
                    Hora Fin dela Jornada de Trabajo
                </div>

                <div class="col-md-3">
                    <g:fieldValue bean="${parametrosInstance}" field="horaFin"/> :
                    <g:fieldValue bean="${parametrosInstance}" field="minutoFin"/>
                </div>
        </g:if>
    </div>



    <div class="row">
        <div class="col-md-12 text-info" style="margin-bottom: 10px; margin-left: 190px">Parámetros para el LDAP</div>

        <g:if test="${parametrosInstance?.ipLDAP}">
                <div class="col-md-3 text-info">
                    IP LDAP y puerto:
                </div>
                <div class="col-md-3">
                    <g:fieldValue bean="${parametrosInstance}" field="ipLDAP"/>
                </div>

        </g:if>
        <g:if test="${parametrosInstance?.passAdm}">
                <div class="col-md-3 text-info">
                    Pass Adm:
                </div>

                <div class="col-md-3">
                    <g:fieldValue bean="${parametrosInstance}" field="passAdm"/>
                </div>
        </g:if>
    </div>

    <g:if test="${parametrosInstance?.ouPrincipal}">
    <div class="row">
        <div class="col-md-3 text-info">
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
    

    
    <g:if test="${parametrosInstance?.imagenes}">
        <div class="row">
            <div class="col-md-2 text-info">
                Imágenes
            </div>
            
            <div class="col-md-3">
                <g:fieldValue bean="${parametrosInstance}" field="imagenes"/>
            </div>
        </div>
    </g:if>

    <div class="row">
        <g:if test="${parametrosInstance?.bloqueo}">

                <div class="col-md-3 text-info">
                    Bloqueo en Horas por no recepción
                </div>

                <div class="col-md-3">
                    <g:fieldValue bean="${parametrosInstance}" field="bloqueo"/>
                </div>

        </g:if>

        %{--<g:if test="${parametrosInstance?.validaLDAP}">--}%

                <div class="col-md-3 text-info">
                    Validar usuarios contra LDAP
                </div>

                <div class="col-md-3">
                    <g:if test="${parametrosInstance?.validaLDAP == 1}">SI</g:if>
                    <g:else>NO</g:else>
                    %{--<g:fieldValue bean="${parametrosInstance}" field="validaLDAP"/>--}%
                </div>

        %{--</g:if>--}%
    </div>
    <div class="row">
        <g:if test="${parametrosInstance?.telefono}">
            <div class="col-md-3 text-info">
                Teléfono para trámites Externos
            </div>

            <div class="col-md-3">
                <g:fieldValue bean="${parametrosInstance}" field="telefono"/>
            </div>
        </g:if>
    </div>








</g:else>