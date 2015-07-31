<%@ page import="happy.tramites.DocumentoTramite; happy.tramites.RolPersonaTramite; happy.tramites.PersonaDocumentoTramite" %>
%{--<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>--}%

%{--<script type="text/javascript" src="${resource(dir: 'js/plugins/lzm.context/js', file: 'lzm.context-0.5.js')}"></script>--}%
%{--<link href="${resource(dir: 'js/plugins/lzm.context/css', file: 'lzm.context-0.5.css')}" rel="stylesheet">--}%

        <g:each in="${tramites}" var="tramite">
            %{--<g:each in="${pxtTramites}" var="pxt">--}%
                %{--<g:if test="${tramite?.id == pxt?.id}">--}%
                    <g:if test="${tramite.tipoDocumento.codigo != 'DEX' || (tramite.tipoDocumento.codigo == 'DEX' && tramite.estadoTramite.codigo == 'E001')}">
                        <g:set var="limite" value="${tramite.getFechaBloqueo()}"/>
                        <g:set var="padre" value=""/>
                        <g:set var="clase" value=""/>

                        <g:set var="para" value="${tramite.getPara()}"/>
                        <g:set var="copias" value="${tramite.getCopias()}"/>

                        <g:if test="${tramite?.anexo == 1 && DocumentoTramite.countByTramite(tramite) > 0}">
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


                        <g:set var="copiasExternas" value="${tramite.copias.findAll { it.departamento?.externo == 1 }}"/>
                        <g:set var="externo" value=""/>
                        <g:if test="${tramite.externo == '1'}">
                            <g:if test="${tramite.tipoDocumento.codigo == 'DEX'}">
                                <g:set var="externo" value="DEX"/>
                            </g:if>
                            <g:else>
                                <g:set var="externo" value="externo"/>
                            </g:else>
                        </g:if>

                        <g:if test="${copiasExternas.estado.codigo.contains('E003')}">
                            <g:set var="externo" value="${externo} externoCC"/>
                        </g:if>

                        <tr id="${tramite?.id}" data-id="${tramite?.id}"
                            %{--class="${(limite) ? ((limite < new Date()) ? 'alerta' + ' ' + clase : tramite.estadoTramite.codigo) : tramite.estadoTramite.codigo + " " + clase}--}%
                            %{--${tramite.fechaEnvio /*&& tramite.noRecibido*/ ? 'desenviar' + ' ' + clase : ''} ${tramite.estadoTramiteExterno ? 'estado' : ''} ${tramite.externo == '1' ? ((tramite.tipoDocumento.codigo == 'DEX') ? 'DEX' : 'externo') : ''} "--}%
                            %{--codigo="${tramite.codigo}" departamento="${tramite.de?.departamento?.codigo}"--}%
                            %{--estado="${tramite.estadoTramite.codigo}" de="${tramite.de.id}"--}%
                            %{--anio="${tramite.fechaCreacion.format('yyyy')}" padre="${padre}">--}%
                            class=" trTramite ${(limite) ? ((limite < new Date()) ? 'alerta' + ' ' + clase : tramite.estadoTramite.codigo) : tramite.estadoTramite.codigo + " " + clase}
                            ${tramite.fechaEnvio /*&& tramite.noRecibido*/ ? 'desenviar' + ' ' + clase : ''} ${tramite.estadoTramiteExterno ? 'estado' : ''} ${tramite?.tipoDocumento?.codigo} ${externo}  "
                            codigo="${tramite.codigo}" departamento="${tramite.de?.departamento?.codigo}"
                            estado="${tramite.estadoTramite.codigo}" de="${tramite.de.id}"
                            principal="${tramite.tramitePrincipal}"
                            anio="${tramite.fechaCreacion.format('yyyy')}" padre="${padre}">

                            <g:if test="${tramite?.anexo == 1}">
                                <td title="${tramite.asunto}">${tramite?.codigo}
                                <g:if test="${DocumentoTramite.countByTramite(tramite) > 0}">
                                    <i class="fa fa-paperclip" style="margin-left: 10px"></i>
                                </g:if>
                                </td>
                            </g:if>
                            <g:else>
                                <td title="${tramite.asunto}">${tramite?.codigo}</td>
                            </g:else>
                            <g:if test="${tramite.tipoDocumento.codigo == 'DEX'}">
                                <td>${tramite.paraExterno} (EXT)</td>
                            </g:if>
                            <g:else>
                                <td title="${tramite.deTexto.codigo}">${tramite.deTexto.codigo}</td>
                            </g:else>

                            <td>${tramite.fechaCreacion?.format("dd-MM-yyyy HH:mm")}</td>
                            <td>
                                <g:if test="${tramite.tipoDocumento.codigo == 'OFI'}">
                                    EXT
                                </g:if>
                                <g:else>
                                    <g:if test="${para?.departamento}">
                                        ${para?.departamento?.codigo}
                                    </g:if>
                                    <g:else>
                                        ${para?.persona?.departamento?.codigo}
                                    </g:else>
                                </g:else>
                            </td>
                            <g:set var="infoExtra" value=""/>
                            <g:if test="${tramite.tipoDocumento.codigo == 'OFI'}">
                                <g:set var="infoExtra" value="${tramite.paraExterno}"/>
                            </g:if>
                            <g:else>
                                <g:each in="${[para] + copias}" var="pdt">
                                    <g:if test="${pdt}">
                                        <g:if test="${infoExtra != ''}">
                                            <g:set var="infoExtra" value="${infoExtra + '<br/>'}"/>
                                        </g:if>
                                        <g:set var="infoExtra" value="${infoExtra + pdt.rolPersonaTramite.descripcion}: "/>
                                        <g:if test="${pdt.departamento}">
                                            <g:set var="infoExtra" value="${infoExtra + pdt.departamento.codigo}"/>
                                        </g:if>
                                        <g:else>
                                            <g:if test="${pdt.persona}">
                                                <g:set var="infoExtra" value="${infoExtra + pdt.persona.login}"/>
                                            </g:if>
                                        </g:else>
                                        <g:if test="${pdt.fechaEnvio}">
                                            <g:if test="${pdt.fechaRecepcion}">
                                                <g:set var="infoExtra" value="${infoExtra + ' (recibido el ' + pdt.fechaRecepcion.format('dd-MM-yyyy HH:mm') + ')'}"/>
                                            </g:if>
                                            <g:else>
                                                <g:set var="infoExtra" value="${infoExtra + ' (no recibido)'}"/>
                                            </g:else>
                                        </g:if>
                                    </g:if>
                                </g:each>
                            </g:else>

                            <td title="${infoExtra}">
                                <g:if test="${tramite.tipoDocumento.codigo == 'OFI'}">
                                    ${tramite?.paraExterno}
                                    <g:set var="dest" value="${tramite?.paraExterno ? 1 : 0}"/>
                                </g:if>

                            <g:else>
                                <g:if test="${para}">
                                    <g:if test="${para.persona}">
                                        ${para?.persona}
                                        <g:set var="dest" value="${1}"/>
                                    </g:if>
                                    <g:else>
                                        <g:set var="dest" value="${0}"/>
                                        <g:if test="${para?.departamento?.triangulos}">
                                            <span class="small">
                                                <g:each in="${para?.departamento?.triangulos}" var="t" status="i">
                                                    <g:set var="dest" value="${dest + 1}"/>
                                                    <i class="fa fa-download"></i>
                                                    ${t.nombre} ${t.apellido}${i < para?.departamento?.triangulos.size() - 1 ? ', ' : ''}
                                                </g:each>
                                            </span>
                                        </g:if>
                                    %{--${para?.departamento?.triangulos && para?.departamento?.triangulos.size() > 0 ? para?.departamento?.triangulos.first() : ''}--}%
                                    %{--<g:set var="dest" value="${1}"/>--}%
                                    </g:else>
                                </g:if>
                            %{--<g:else>--}%
                                <span class="small">
                                    <g:if test="${dest == null}">
                                        <g:set var="dest" value="${0}"/>
                                    </g:if>
                                    <g:each in="${copias}" var="copia" status="i">
                                        <g:set var="dest" value="${dest + 1}"/>
                                        [CC]
                                        <g:if test="${copia.departamento}">
                                            ${copia.departamento.codigo}
                                        </g:if>
                                        <g:elseif test="${copia.persona}">
                                            ${copia.persona.login}
                                        </g:elseif>
                                    %{--[CC] ${copia.persona ? copia.persona.login : copia.departamento.codigo}--}%
                                        <g:if test="${i < copias.size() - 1}">
                                            ,
                                        </g:if>
                                    </g:each>
                                </span>
                            %{--</g:else>--}%
                            </g:else>
                            <g:if test="${dest == 0}">
                                <span class="label label-danger" style="margin-top: 3px;">
                                    <i class="fa fa-warning"></i> Sin destinatario ni copias
                                </span>
                            </g:if>

                            <td>${tramite?.prioridad.descripcion}</td>
                            <td>${tramite.fechaEnvio?.format("dd-MM-yyyy HH:mm")}</td>
                            <td>${limite ? limite.format("dd-MM-yyyy HH:mm") : ''}</td>
                            <td>${tramite?.estadoTramite.descripcion}</td>
                            %{--<td id="${tramite?.id}" class="ck text-center">--}%
                                %{--<g:if test="${tramite.estadoTramite.codigo == 'E001'}">--}%
                                    %{--<g:checkBox name="porEnviar" tramite="${tramite?.id}" style="margin-left: 30px" class="form-control combo" checked="false"/>--}%
                                %{--</g:if>--}%
                            %{--</td>--}%
                            <td id="${tramite?.id}" class="ck text-center">
                                <g:if test="${tramite.estadoTramite.codigo == 'E001'}">
                                %{--<g:if test="${PersonaDocumentoTramite.countByTramiteAndRolPersonaTramiteInList(tramite, [RolPersonaTramite.findByCodigoInList(['R001', 'R002'])]) > 0}">--}%
                                    <g:if test="${dest > 0}">
                                        <g:checkBox name="porEnviar" tramite="${tramite?.id}" style="margin-left: 30px" class="form-control combo" checked="false"/>
                                    </g:if>
                                    <g:else>
                                        No tiene destinario
                                    </g:else>
                                </g:if>
                            </td>
                        </tr>
                    </g:if>
                %{--</g:if>--}%
            %{--</g:each>--}%

        </g:each>


<script type="text/javascript">

        $("tr").contextMenu({
            items  : createContextMenu,
            onShow : function ($element) {
                $element.addClass("trHighlight");
            },
            onHide : function ($element) {
                $(".trHighlight").removeClass("trHighlight");
            }
        });

        $('[title!=""]').qtip({
            style    : {
                classes : 'qtip-tipsy'
            },
            position : {
                my : "bottom center",
                at : "top center"
            }
        });
        $('.titleEspecial').qtip({
            style    : {
                classes : 'qtip-tipsy'
            },
            position : {
                my : "bottom center",
                at : "top center"
            },
            show: {
                solo: true
            },
            hide: {
                fixed: true,
                delay: 300
            }
        });

</script>