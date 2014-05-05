<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 25/02/14
  Time: 04:52 PM
--%>

<div style="margin-top: 10px; height: 450px"  class="container-celdas">
    <span class="grupo">
        <table class="table table-bordered table-striped table-condensed table-hover">
            <thead>
            <tr>
                <th class="cabecera">Documento</th>
                <th>De</th>
                <th class="cabecera">Fec. Creación</th>
                <th class="cabecera">Para</th>
                <th class="cabecera">Destinatario</th>
                <th class="cabecera">Prioridad</th>
                <th class="cabecera">Fecha Envio</th>
                <th class="cabecera">F Límite Recepción</th>
                <th class="cabecera">Estado</th>
                <th class="cabecera">Enviar</th>
            </tr>

            </thead>
            <tbody>
            <g:each in="${tramites}" var="tramite">

                <g:set var="limite" value="${tramite.getFechaLimite()}"/>
                <g:set var="padre" value=""/>

                <g:set var="esImprimir" value="${false}"/>
                <g:if test="${(happy.tramites.PersonaDocumentoTramite.findAllByPersonaAndTramite(session.usuario, tramite).findAll {
                    it.rolPersonaTramite.codigo == 'I005'
                }).size() > 0}">
                    <g:set var="esImprimir" value="${true}"/>
                </g:if>

                <g:if test="${tramite?.anexo == 1}">
                    <g:set var="anexo" value="${'conAnexo'}"/>
                </g:if>
                <g:else>
                    <g:set var="anexo" value="${'sinAnexo'}"/>
                </g:else>

                <g:if test="${tramite?.tipoDocumento?.codigo == 'SUM'}">
                    <g:set var="clase" value="${'sumilla' + ' ' + anexo}"/>
                </g:if>
                <g:else>
                    <g:set var="clase" value="${'sinSumilla' + ' ' + anexo}"/>
                </g:else>

                <g:if test="${tramite.padre}">
                    <g:set var="clase" value="${clase + ' conPadre'}"/>
                    <g:set var="padre" value="${tramite.padreId}"/>
                </g:if>



                <tr data-id="${tramite?.id}" class="${type}">
                    <td>${tramite?.codigo}</td>
                    <td>?</td>
                    <td>${tramite?.de}</td>
                    <td>${tramite?.de?.departamento?.descripcion}</td>
                    <td></td>
                    <td></td>
                    <td>${tramite?.estadoTramite?.descripcion}</td>
                    <td>?</td>
                    <td>${tramite?.padre}</td>

                </tr>




            </g:each>

            </tbody>
        </table>

    </span>

</div>