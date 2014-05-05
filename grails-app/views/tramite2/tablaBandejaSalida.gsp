<%@ page import="happy.tramites.RolPersonaTramite; happy.tramites.PersonaDocumentoTramite" %>
<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>

<table class="table table-bordered  table-condensed table-hover">
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
    <tbody id="tabla_salida">
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

            <tr id="${tramite?.id}" data-id="${tramite?.id}"
                class="${esImprimir ? 'imprimir' : ''}
                ${(limite) ? ((limite < new Date()) ? 'alerta' + ' ' + clase : tramite.estadoTramite.codigo) : tramite.estadoTramite.codigo + ' ' + clase}
                ${tramite.fechaEnvio /*&& tramite.noRecibido*/ ? 'desenviar' + ' ' + clase : ''}"
                estado="${tramite.estadoTramite.codigo}" de="${tramite.de.id}" codigo="${tramite.codigo}"
                departamento="${tramite.de?.departamento?.codigo}" anio="${tramite.fechaCreacion.format('yyyy')}" padre="${padre}">
                <g:if test="${tramite?.anexo == 1}">
                    <td title="${tramite.asunto}">
                        ${tramite?.codigo} <i class="fa fa-paperclip"></i>
                    </td>
                </g:if>
                <g:else>
                    <td title="${tramite.asunto}">
                        ${tramite?.codigo}
                    </td>
                </g:else>
                <td title="${tramite.de.departamento}">${(tramite.deDepartamento) ? tramite.deDepartamento.codigo : tramite.de}</td>
                <td>${tramite.fechaCreacion?.format("dd-MM-yyyy")}</td>
                <g:set var="para" value="${tramite.getPara()}"/>
                <td>${para?.departamento?.codigo}</td>
                <g:set var="infoExtra" value=""/>
                <g:each in="${PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteInList(tramite, [RolPersonaTramite.findByCodigo('R001'), RolPersonaTramite.findByCodigo('R002')])}" var="pdt">
                    <g:if test="${infoExtra != ''}">
                        <g:set var="infoExtra" value="${infoExtra + '<br/>'}"/>
                    </g:if>
                    <g:set var="infoExtra" value="${infoExtra + pdt.rolPersonaTramite.descripcion}: "/>
                    <g:if test="${pdt.departamento}">
                        <g:set var="infoExtra" value="${infoExtra + pdt.departamento.codigo}"/>
                    </g:if>
                    <g:else>
                        <g:set var="infoExtra" value="${infoExtra + pdt.persona.login}"/>
                    </g:else>
                    <g:if test="${pdt.fechaEnvio}">
                        <g:if test="${pdt.fechaRecepcion}">
                            <g:set var="infoExtra" value="${infoExtra + ' (recibido el ' + pdt.fechaRecepcion.format('dd-MM-yyyy HH:mm') + ')'}"/>
                        </g:if>
                        <g:else>
                            <g:set var="infoExtra" value="${infoExtra + ' (no recibido)'}"/>
                        </g:else>
                    </g:if>
                </g:each>
                <td title="${infoExtra}">
                    <g:if test="${tramite.origenTramite}">
                        ${tramite.origenTramite?.nombre}
                    </g:if>
                    <g:else>
                        <g:if test="${para}">
                            <g:if test="${para.persona}">
                                ${para?.persona}
                            </g:if>
                            <g:else>
                                ${para?.departamento?.triangulos && para?.departamento?.triangulos.size() > 0 ? para?.departamento?.triangulos.first() : ''}
                            </g:else>
                        </g:if>
                    </g:else>
                </td>
                <td>${tramite?.prioridad.descripcion}</td>
                <td>${tramite.fechaEnvio?.format("dd-MM-yyyy HH:mm")}</td>
                <td>${limite ? limite.format("dd-MM-yyyy HH:mm") : ''}</td>
                <td>
                    ${tramite?.estadoTramite.descripcion}
                    <g:if test="${tramite.nota && tramite.nota != ''}">
                        <span class="badge pull-right">
                            <g:link controller="tramite" action="redactar" id="${tramite.id}" title="Con notas de revisión">
                                <i class="fa fa-pencil text-white"></i>
                            </g:link>
                        </span>
                    </g:if>
                </td>
                <td id="${tramite?.id}" class="ck text-center">
                    <g:if test="${tramite.estadoTramite.codigo == 'E001'}">
                        <g:checkBox name="porEnviar" tramite="${tramite?.id}" style="margin-left: 20px" class="form-control combo" checked="false"/>
                    </g:if>
                </td>
            </tr>
        </g:each>
    </tbody>
</table>