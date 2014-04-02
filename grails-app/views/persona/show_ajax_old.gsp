<%@ page import="happy.seguridad.Persona" %>

<g:if test="${!personaInstance}">
    <elm:notFound elem="Persona" genero="o"/>
</g:if>
<g:else>

    <div style="height: ${Math.max(h, 300) + 30}px;" class="pull-left">
        <div class="thumbnail" style="float: left;">
            <img id="foto" src="${resource(dir: 'images/perfiles/', file: personaInstance.foto)}"/>
        </div>
    </div>

    <g:if test="${personaInstance?.departamento}">
        <div class="row">
            <div class="col-md-3 text-info">
                Departamento
            </div>

            <div class="col-md-5">
                ${personaInstance?.departamento?.descripcion}
            </div>

        </div>
    </g:if>

    <g:if test="${personaInstance?.cedula}">
        <div class="row">
            <div class="col-md-3 text-info">
                Cédula
            </div>

            <div class="col-md-5">
                <g:fieldValue bean="${personaInstance}" field="cedula"/>
            </div>

        </div>
    </g:if>

    <g:if test="${personaInstance?.titulo}">
        <div class="row">
            <div class="col-md-3 text-info">
                Título
            </div>

            <div class="col-md-5">
                <g:fieldValue bean="${personaInstance}" field="titulo"/>
            </div>

        </div>
    </g:if>

    <g:if test="${personaInstance?.nombre}">
        <div class="row">
            <div class="col-md-3 text-info">
                Nombre
            </div>

            <div class="col-md-5">
                <g:fieldValue bean="${personaInstance}" field="nombre"/>
            </div>

        </div>
    </g:if>

    <g:if test="${personaInstance?.apellido}">
        <div class="row">
            <div class="col-md-3 text-info">
                Apellido
            </div>

            <div class="col-md-5">
                <g:fieldValue bean="${personaInstance}" field="apellido"/>
            </div>

        </div>
    </g:if>

    <g:if test="${personaInstance?.sigla}">
        <div class="row">
            <div class="col-md-3 text-info">
                Sigla
            </div>

            <div class="col-md-5">
                <g:fieldValue bean="${personaInstance}" field="sigla"/>
            </div>

        </div>
    </g:if>

    <g:if test="${personaInstance?.fechaNacimiento}">
        <div class="row">
            <div class="col-md-3 text-info">
                Fecha Nacimiento
            </div>

            <div class="col-md-5">
                <g:formatDate date="${personaInstance?.fechaNacimiento}" format="dd-MM-yyyy"/>
            </div>

        </div>
    </g:if>

    <g:if test="${personaInstance?.fechaInicio}">
        <div class="row">
            <div class="col-md-3 text-info">
                Fecha Inicio
            </div>

            <div class="col-md-5">
                <g:formatDate date="${personaInstance?.fechaInicio}" format="dd-MM-yyyy"/>
            </div>

        </div>
    </g:if>

    <g:if test="${personaInstance?.fechaFin}">
        <div class="row">
            <div class="col-md-3 text-info">
                Fecha Fin
            </div>

            <div class="col-md-5">
                <g:formatDate date="${personaInstance?.fechaFin}" format="dd-MM-yyyy"/>
            </div>

        </div>
    </g:if>

    <g:if test="${personaInstance?.cargo}">
        <div class="row">
            <div class="col-md-3 text-info">
                Cargo
            </div>

            <div class="col-md-5">
                <g:fieldValue bean="${personaInstance}" field="cargo"/>
            </div>

        </div>
    </g:if>

    <g:if test="${personaInstance?.codigo}">
        <div class="row">
            <div class="col-md-3 text-info">
                Código
            </div>

            <div class="col-md-5">
                <g:fieldValue bean="${personaInstance}" field="codigo"/>
            </div>

        </div>
    </g:if>

    <g:if test="${personaInstance?.mail}">
        <div class="row">
            <div class="col-md-3 text-info">
                E-mail
            </div>

            <div class="col-md-5">
                <g:fieldValue bean="${personaInstance}" field="mail"/>
            </div>

        </div>
    </g:if>

    <g:if test="${personaInstance?.telefono}">
        <div class="row">
            <div class="col-md-3 text-info">
                Teléfono
            </div>

            <div class="col-md-5">
                <g:fieldValue bean="${personaInstance}" field="telefono"/>
            </div>

        </div>
    </g:if>

    <g:if test="${personaInstance?.celular}">
        <div class="row">
            <div class="col-md-3 text-info">
                Celular
            </div>

            <div class="col-md-5">
                <g:fieldValue bean="${personaInstance}" field="celular"/>
            </div>

        </div>
    </g:if>

    <g:if test="${personaInstance?.login}">
        <div class="row">
            <div class="col-md-3 text-info">
                Login
            </div>

            <div class="col-md-5">
                <g:fieldValue bean="${personaInstance}" field="login"/>
            </div>

        </div>
    </g:if>

%{--<g:if test="${personaInstance?.jefe}">--}%
    <div class="row">
        <div class="col-md-3 text-info">
            Es jefe
        </div>

        <div class="col-md-5">
            ${personaInstance.jefe == 1 ? "SI" : "NO"}
        </div>

    </div>
%{--</g:if>--}%

%{--<g:if test="${personaInstance?.activo}">--}%
    <div class="row">
        <div class="col-md-3 text-info">
            Activo
        </div>

        <div class="col-md-5">
            ${personaInstance.activo == 1 ? "SI" : "NO"}
        </div>

    </div>
%{--</g:if>--}%

%{--<g:if test="${personaInstance?.fechaCambioPass}">--}%
%{--<div class="row">--}%
%{--<div class="col-md-3 text-info">--}%
%{--Fecha Cambio Pass--}%
%{--</div>--}%
%{----}%
%{--<div class="col-md-5">--}%
%{--<g:formatDate date="${personaInstance?.fechaCambioPass}" format="dd-MM-yyyy" />--}%
%{--</div>--}%
%{----}%
%{--</div>--}%
%{--</g:if>--}%

    <g:if test="${personaInstance?.foto}">
        <div class="row">
            <div class="col-md-3 text-info">
                Foto
            </div>

            <div class="col-md-5">
                <g:fieldValue bean="${personaInstance}" field="foto"/>
            </div>

        </div>
    </g:if>

</g:else>