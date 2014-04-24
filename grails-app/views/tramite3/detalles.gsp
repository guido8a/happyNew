<%@ page import="happy.seguridad.Persona" %>
<div style="max-height: 500px; overflow-y: auto; overflow-x: hidden;">
    <g:if test="${principal}">
        <div style="margin-bottom: 20px;min-height: 170px" class="vertical-container">
            <p class="css-vertical-text">Doc. Principal</p>

            <div class="linea"></div>

            <div class="row">
                <div class="col-xs-1 negrilla">No:</div>

                <div class="col-xs-8">${principal.codigo}</div>
            </div>

            <div class="row">
                <div class="col-xs-1 negrilla">Fecha:</div>

                <div class="col-xs-8">${principal.fechaCreacion.format("dd-MM-yyyy HH:mm")}</div>
            </div>

            <div class="row">
                <div class="col-xs-1 negrilla">De:</div>

                <div class="col-xs-8">${principal.deDepartamento ? principal.deDepartamento.codigo : principal.de.nombre + ' ' + principal.de.apellido}</div>
            </div>

            <div class="row">
                <div class="col-xs-1 negrilla">Asunto:</div>

                <div class="col-xs-8">${principal.asunto}</div>
            </div>

            <g:if test="${principal.observaciones}">
                <div class="row" style="margin-bottom: 10px">
                    <div class="col-xs-1 negrilla">Obs:</div>

                    <div class="col-xs-8">${principal.observaciones}</div>
                </div>
            </g:if>
        </div>
    </g:if>
    <div style="margin-bottom: 20px" class="vertical-container">
        <p class="css-vertical-text">Tramite</p>

        <div class="linea"></div>

        <div class="row">
            <div class="col-xs-1 negrilla">No:</div>

            <div class="col-xs-8">${tramite.codigo}</div>
        </div>

        <div class="row">
            <div class="col-xs-1 negrilla">Fecha:</div>

            <div class="col-xs-8">${tramite.fechaCreacion.format("dd-MM-yyyy HH:mm")}</div>
        </div>

        <div class="row">
            <div class="col-xs-1 negrilla">De:</div>

            <div class="col-xs-8">${tramite.deDepartamento ? tramite.deDepartamento.codigo : tramite.de.nombre + ' ' + tramite.de.apellido}</div>
        </div>

        <div class="row">
            <div class="col-xs-1 negrilla">Asunto:</div>

            <div class="col-xs-8">${tramite.asunto}</div>
        </div>

        <g:if test="${tramite.personaPuedeLeer(Persona.get(session.usuario.id))}">
            <div class="row" style="margin-bottom: 10px">
                <div class="col-xs-1 negrilla">Texto:</div>

                <div class="col-xs-10" style="background: #dedede; max-height: 300px; overflow: auto;">
                    %{--<g:each in="${0..5}" var="i">--}%
                    <util:renderHTML html="${tramite.texto}"/>
                    %{--</g:each>--}%
                </div>
            </div>
        </g:if>

        <g:if test="${tramite.observaciones}">
            <div class="row" style="margin-bottom: 10px">
                <div class="col-xs-1 negrilla">Obs:</div>

                <div class="col-xs-8">${tramite.observaciones}</div>
            </div>
        </g:if>
    </div>
</div>