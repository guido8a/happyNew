<%@ page import="happy.seguridad.Persona; happy.tramites.Tramite; happy.tramites.Departamento" %>

<g:if test="${!departamentoInstance}">
    <elm:notFound elem="Departamento" genero="o"/>
</g:if>
<g:else>

    <style type="text/css">
    .outer {
        overflow-x : auto;
        height     : 240px;
    }

    .selected {
        background : #FCF8E3 !important;
    }
    </style>

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

%{--
    <g:if test="${departamentoInstance?.tipoDepartamento}">
        <div class="row">
            <div class="col-md-3 text-info">
                Tipo Departamento
            </div>

            <div class="col-md-9">
                ${departamentoInstance?.tipoDepartamento?.descripcion}
            </div>

        </div>
    </g:if>
--}%

    <g:if test="${departamentoInstance?.padre}">
        <div class="row">
            <div class="col-md-3 text-info">
                Dirección/Secretaría
            </div>

            <div class="col-md-9">
                ${departamentoInstance?.padre?.encodeAsHTML()}
            </div>

        </div>
    </g:if>


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

    <g:if test="${personal}">
        <g:set var="envia" value="${new Persona()}"/>
        <g:if test="${params.tramite}">
            <g:set var="envia" value="${Tramite.get(params.tramite).de}"/>
        </g:if>
        <div class="row">
            <div class="col-md-3 text-info">
                Recepción (${personal.size()})
            </div>

            <div class="col-md-9 outer">
                <g:set var="band" value="${false}"/>
                <g:each in="${personal}" var="persona">
                    <div class="col-md-4">
                        <g:if test="${persona?.foto}">
                            <div class="thumbnail ${persona.id == envia.id ? 'selected' : ''}">
                                <img class="media-object thumbnail" src="${resource(dir: 'images/perfiles/', file: persona.foto)}"/>

                                <div class="caption">
                                    <g:fieldValue bean="${persona}" field="nombre"/> <g:fieldValue bean="${persona}" field="apellido"/>
                                    <g:if test="${persona.id == envia.id}">
                                        <strong>(Envió)</strong>
                                        <g:set var="band" value="${true}"/>
                                    </g:if>
                                </div>
                            </div>
                        </g:if>
                        <g:else>
                            <g:fieldValue bean="${persona}" field="nombre"/> <g:fieldValue bean="${persona}" field="apellido"/>
                            <g:if test="${persona.id == envia.id}">
                                <strong>(Envió)</strong>
                                <g:set var="band" value="${true}"/>
                            </g:if>
                        </g:else>
                    </div>
                </g:each>
            </div>
        </div>

        <g:if test="${!band && envia.id}">
            <div class="row">
                <div class="col-md-3 text-info">
                    Trámite enviado por
                </div>

                <div class="col-md-4">
                    <g:if test="${envia?.foto}">
                        <div class="thumbnail selected">
                            <img class="media-object thumbnail"  src="${resource(dir: 'images/perfiles/', file: envia.foto)}"/>

                            <div class="caption">
                                <g:fieldValue bean="${envia}" field="nombre"/> <g:fieldValue bean="${envia}" field="apellido"/>
                            </div>
                        </div>
                    </g:if>
                    <g:else>
                        <g:fieldValue bean="${envia}" field="nombre"/> <g:fieldValue bean="${envia}" field="apellido"/>
                    </g:else>
                </div>
            </div>
        </g:if>
    </g:if>

</g:else>