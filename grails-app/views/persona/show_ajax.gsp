<%@ page import="happy.seguridad.Persona" %>

<g:if test="${!personaInstance}">
    <elm:notFound elem="Persona" genero="o"/>
</g:if>
<g:else>
    <div class="media">
        <g:set var="width" value="2"/>
        <g:if test="${personaInstance.foto}">
            <a class="pull-left" href="#">
                <img class="media-object thumbnail" id="foto" src="${resource(dir: 'images/perfiles/', file: personaInstance.foto)}"/>
            </a>
            <g:set var="width" value="3"/>
        </g:if>

        <div class="media-body">
            <g:if test="${personaInstance?.departamento}">
                <div class="row">
                    <div class="col-md-${width} text-info">
                        Departamento
                    </div>

                    <div class="col-md-7">
                        ${personaInstance?.departamento?.descripcion}
                    </div>

                </div>
            </g:if>

            <g:if test="${personaInstance?.login}">
                <div class="row">
                    <div class="col-md-${width} text-info">
                        Usuario
                    </div>

                    <div class="col-md-7">
                        <g:fieldValue bean="${personaInstance}" field="login"/>
                    </div>

                </div>
            </g:if>



            <g:if test="${personaInstance?.nombre}">
                <div class="row">
                    <div class="col-md-${width} text-info">
                        Nombre
                    </div>

                    <div class="col-md-7">
                        <g:fieldValue bean="${personaInstance}" field="nombre"/>
                    </div>

                </div>
            </g:if>

            <g:if test="${personaInstance?.apellido}">
                <div class="row">
                    <div class="col-md-${width} text-info">
                        Apellido
                    </div>

                    <div class="col-md-7">
                        <g:fieldValue bean="${personaInstance}" field="apellido"/>
                    </div>

                </div>
            </g:if>

            <g:if test="${personaInstance?.mail}">
                <div class="row">
                    <div class="col-md-${width} text-info">
                        E-mail
                    </div>

                    <div class="col-md-7">
                        <g:fieldValue bean="${personaInstance}" field="mail"/>
                    </div>

                </div>
            </g:if>

            <g:if test="${personaInstance?.telefono}">
                <div class="row">
                    <div class="col-md-${width} text-info">
                        Teléfonos
                    </div>

                    <div class="col-md-7">
                        <g:fieldValue bean="${personaInstance}" field="telefono"/>
                    </div>

                </div>
            </g:if>


        %{--<g:if test="${personaInstance?.jefe}">--}%
%{--
            <div class="row">
                <div class="col-md-${width} text-info">
                    Autoridad
                </div>

                <div class="col-md-7">
                    ${personaInstance.jefe == 1 ? "SI" : "NO"}
                </div>

            </div>
--}%
            %{--</g:if>--}%

            %{--<g:if test="${personaInstance?.activo}">--}%
%{--
            <div class="row">
                <div class="col-md-${width} text-info">
                    Activo
                </div>

                <div class="col-md-7">
                    ${personaInstance.activo == 1 ? "SI" : "NO"}
                </div>

            </div>
--}%
            %{--</g:if>--}%

%{--
            <g:if test="${personaInstance?.titulo}">
                <div class="row">
                    <div class="col-md-${width} text-info">
                        Título
                    </div>

                    <div class="col-md-7">
                        <g:fieldValue bean="${personaInstance}" field="titulo"/>
                    </div>

                </div>
            </g:if>
--}%

        </div>
    </div>

</g:else>