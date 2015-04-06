<%@ page import="happy.tramites.Tramite; happy.seguridad.Persona" %>

<g:if test="${!departamentoInstance}">
    <elm:notFound elem="Departamento" genero="o"/>
</g:if>
<g:else>
    <div class="media">
        <g:set var="width" value="2"/>
        <g:if test="${personal}">
            <g:set var="envia" value="${new Persona()}"/>
            <g:if test="${params.tramite}">
                <g:set var="envia" value="${Tramite.get(params.tramite).de}"/>
            </g:if>
            <g:if test="${envia.foto}">
                <a class="pull-left" href="#">
                    <img class="media-object thumbnail" id="foto" src="${resource(dir: 'images/perfiles/', file: envia.foto)}"/>
                </a>
                <g:set var="width" value="3"/>
            </g:if>
        </g:if>

        <div class="media-body">

            <g:if test="${envia?.nombre}">
                <div class="row">
                    <div class="col-md-${width} text-info">
                        Enviado por
                    </div>

                    <div class="col-md-7">
                        ${envia.nombre} ${envia.apellido} (${envia.login})
                    </div>

                </div>
            </g:if>

            <g:if test="${departamentoInstance?.codigo}">
                <div class="row">
                    <div class="col-md-3 text-info">
                        Código
                    </div>

                    <div class="col-md-9">
                        <g:fieldValue bean="${departamentoInstance}" field="codigo"/>
                    </div>

                </div>
            </g:if>

            <g:if test="${departamentoInstance?.descripcion}">
                <div class="row">
                    <div class="col-md-3 text-info">
                        Descripción
                    </div>

                    <div class="col-md-9">
                        <g:fieldValue bean="${departamentoInstance}" field="descripcion"/>
                    </div>

                </div>
            </g:if>

        %{--<g:if test="${departamentoInstance?.padre}">--}%
        %{--<div class="row">--}%
        %{--<div class="col-md-3 text-info">--}%
        %{--Dirección/Secretaría--}%
        %{--</div>--}%

        %{--<div class="col-md-9">--}%
        %{--${departamentoInstance?.padre?.encodeAsHTML()}--}%
        %{--</div>--}%

        %{--</div>--}%
        %{--</g:if>--}%


            <g:if test="${departamentoInstance?.telefono}">
                <div class="row">
                    <div class="col-md-3 text-info">
                        Teléfono
                    </div>

                    <div class="col-md-9">
                        <g:fieldValue bean="${departamentoInstance}" field="telefono"/>
                    </div>

                </div>
            </g:if>

            <g:if test="${departamentoInstance?.extension}">
                <div class="row">
                    <div class="col-md-3 text-info">
                        Extensión
                    </div>

                    <div class="col-md-9">
                        <g:fieldValue bean="${departamentoInstance}" field="extension"/>
                    </div>

                </div>
            </g:if>

            <g:if test="${departamentoInstance?.direccion}">
                <div class="row">
                    <div class="col-md-3 text-info">
                        Dirección
                    </div>

                    <div class="col-md-9">
                        <g:fieldValue bean="${departamentoInstance}" field="direccion"/>
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