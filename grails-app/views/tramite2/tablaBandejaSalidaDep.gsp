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
    <tbody>
        <g:each in="${tramites}" var="tramite">

            <g:if test="${tramite.tipoDocumento.codigo != 'DEX' || (tramite.tipoDocumento.codigo == 'DEX' && tramite.estadoTramite.codigo == 'E001')}">
                <g:set var="limite" value="${tramite.getFechaLimite()}"/>
                <g:set var="padre" value=""/>
                <g:set var="clase" value=""/>

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
                    class="${(limite) ? ((limite < new Date()) ? 'alerta' + ' ' + clase : tramite.estadoTramite.codigo) : tramite.estadoTramite.codigo + " " + clase}
                    ${tramite.fechaEnvio && tramite.noRecibido ? 'desenviar' + ' ' + clase : ''}"
                    codigo="${tramite.codigo}" departamento="${tramite.de?.departamento?.codigo}"
                    estado="${tramite.estadoTramite.codigo}" de="${tramite.de.id}"
                    anio="${tramite.fechaCreacion.format('yyyy')}" padre="${padre}">
                    <g:if test="${tramite?.anexo == 1}">
                        <td title="${tramite.asunto}">${tramite?.codigo}<i class="fa fa-paperclip" style="margin-left: 10px"></i>
                        </td>
                    </g:if>
                    <g:else>
                        <td title="${tramite.asunto}">${tramite?.codigo}</td>
                    </g:else>
                    <td title="${tramite.deTexto.codigo}">${tramite.deTexto.codigo}</td>
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
                    <td>${tramite?.estadoTramite.descripcion}</td>
                    <td id="${tramite?.id}" class="ck text-center">
                        <g:if test="${tramite.estadoTramite.codigo == 'E001'}">
                            <g:checkBox name="porEnviar" tramite="${tramite?.id}" style="margin-left: 30px" class="form-control combo" checked="false"/>
                        </g:if>
                    </td>
                </tr>
            </g:if>
        </g:each>
    </tbody>
</table>

