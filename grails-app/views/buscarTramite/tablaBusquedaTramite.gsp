<%@ page import="happy.tramites.DocumentoTramite; happy.tramites.RolPersonaTramite; happy.tramites.EstadoTramite; happy.tramites.PersonaDocumentoTramite; happy.seguridad.Persona; happy.tramites.Tramite" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js/plugins/lzm.context/js', file: 'lzm.context-0.5.js')}"></script>
<link href="${resource(dir: 'js/plugins/lzm.context/css', file: 'lzm.context-0.5.css')}" rel="stylesheet">

<script type="text/javascript" src="${resource(dir: 'js/plugins/fixed-header-table-1.3', file: 'jquery.fixedheadertable.min.js')}"></script>
<link href="${resource(dir: 'js/plugins/fixed-header-table-1.3/css', file: 'defaultTheme.css')}" rel="stylesheet">

<style type="text/css">
table {
    font-size : 9pt;
}
</style>

%{--<div style="height: 30px; overflow: hidden;" class="container-celdas">--}%
%{--<span class="grupo">--}%
%{--<table class="table table-bordered table-condensed table-hover">--}%
%{--<thead>--}%
%{--<tr>--}%
%{--<th class="alinear" style="width: 165px">Documento</th>--}%
%{--<th class="alinear" style="width: 100px">Fecha Creación</th>--}%
%{--<th class="alinear" style="width: 150px">De</th>--}%
%{--<th class="alinear" style="width: 150px">Para</th>--}%
%{--<th class="alinear" style="width: 100px">Asunto</th>--}%
%{--<th class="alinear" style="width: 60px">Prioridad</th>--}%
%{--<th class="alinear" style="width: 90px">Envia</th>--}%
%{--<th class="alinear" style="width: 110px">Fecha Envio</th>--}%
%{--<th class="alinear" style="width: 110px">Fecha Recepción</th>--}%
%{--</tr>--}%
%{--</thead>--}%
%{--<tbody>--}%

%{--</tbody>--}%
%{--</table>--}%
%{--</span>--}%
%{--</div>--}%
%{--${msje}--}%


<div style="height: ${msje == '' ? 560 : 520}px" class="container-celdas">
    <div style="width: 100%"><util:renderHTML html="${msje}"/></div>
    <span class="grupo">
        <table class="table table-bordered table-condensed table-hover" width="1140px">
            <thead>
                <tr>
                    <th class="alinear" style="width: 110px">Documento</th>
                    <th class="alinear" style="width: 75px">Creación</th>
                    <th class="alinear" style="width: 150px">De</th>
                    <th class="alinear" style="width: 150px">Para</th>
                    <th class="alinear" style="width: 295px">Asunto</th>
                    <th class="alinear" style="width: 60px">Prioridad</th>
                    <th class="alinear" style="width: 150px">Envia</th>
                    <th class="alinear" style="width: 75px">Envio</th>
                    <th class="alinear" style="width: 75px">Recepción</th>
                </tr>
            </thead>
            <tbody>
                <g:set var="estadoAnulado" value="${EstadoTramite.findByCodigo('E006')}"/>
                <g:set var="estadoRecibido" value="${EstadoTramite.findByCodigo('E004')}"/>

                <g:set var="rolRecibe" value="${RolPersonaTramite.findByCodigo('E003')}"/>
                <g:set var="rolEnvia" value="${RolPersonaTramite.findByCodigo('E004')}"/>
                <g:set var="rolPara" value="${RolPersonaTramite.findByCodigo('R001')}"/>

                <g:each in="${tramites}" var="tramite" status="z">

                    <g:set var="recibe" value="${PersonaDocumentoTramite.findByTramiteAndRolPersonaTramite(tramite, rolRecibe)}"/>
                    <g:set var="envia" value="${PersonaDocumentoTramite.findByTramiteAndRolPersonaTramite(tramite, rolEnvia)}"/>
                    <g:set var="receptoresAnulados" value="${(tramite.allCopias + tramite.para).findAll {
                        it?.estado == estadoAnulado
                    }}"/>

                    <g:set var="padre" value=""/>
                    <g:set var="clase" value="${'nada'}"/>
                    <g:set var="de" value="${tramite?.deDepartamentoId ?: tramite?.de?.departamentoId}"/>

                    <g:if test="${tramite.de?.id == session.usuario.id ||
                            tramite.deDepartamento?.id == session.usuario.departamentoId ||
                            (session.usuario.esTriangulo && de == session.usuario.departamentoId)}">
                        <g:set var="clase" value="${'principal'}"/>
                        <g:if test="${tramite.padre}">
                            <g:set var="padre" value="${tramite.padre?.id}"/>
                            <g:set var="clase" value="${'padre'}"/>
                        </g:if>
                    </g:if>

                    <g:if test="${tramite.anexo == 1}">
                        <g:set var="clase" value="${clase + ' conAnexo'}"/>
                    </g:if>

                    <g:if test="${recibe && recibe.fechaRecepcion}">
                        <g:set var="clase" value="${clase + ' recibido'}"/>
                    </g:if>

                    <g:set var="copiasExternas" value="${tramite.copias.findAll { it.departamento?.externo == 1 }}"/>
                    <g:set var="externo" value=""/>
                    <g:if test="${tramite.externo == '1' || tramite.tipoDocumento.codigo == 'DEX'}">
                        <g:set var="externo" value="externo"/>
                    </g:if>

                    <g:if test="${copiasExternas.estado.codigo.contains('E003')}">
                        <g:set var="externo" value="${externo} externoCC"/>
                    </g:if>

                    %{--<g:if test="${tramite?.deId == session.usuario.id || (tramite?.departamento?.id == session.departamento.id && session.usuario.esTriangulo)}">--}%
                    <g:if test="${(params.dgsg == 'DGSG') || tramite?.deId == session.usuario.id || (tramite?.departamento?.id == session.departamento.id && session.usuario.esTriangulo)}">
                        <g:set var="clase" value="${clase + ' mio'}"/>
                    </g:if>


                    <g:set var="para" value="${tramite.para.persona ? tramite.para.persona?.departamentoId : tramite.para?.departamentoId}"/>
                    <g:each in="${tramite.copias}" var="copia">
                        <g:set var="para" value="${para + ',' + (copia.persona ? copia.persona?.departamentoId : copia?.departamentoId)}"/>
                    </g:each>

                    <g:set var="respuestas" value="${tramite.respuestas.size()}"/>

                    <g:if test="${tramite.fechaEnvio}">
                        <g:set var="clase" value="${clase + ' enviado'}"/>
                    </g:if>

                    <tr id="${tramite.id}" data-id="${tramite.id}" padre="${padre}" class="${clase} ${externo}" anulados="${receptoresAnulados.size()}"
                        dep="${tramite?.de?.departamentoId}" principal="${tramite.tramitePrincipal}" para="${para}" respuestas="${respuestas}"
                        de="${tramite.tipoDocumento.codigo == 'DEX' ? 'E_' + tramite.id :
                                (tramite.deDepartamento ? 'D_' + tramite.deDepartamento?.id : 'P_' + tramite.de?.id)}">
                        %{--<td style="width: 110px">--}%
                        %{--<g:if test="${tramite.tipoTramite.codigo == 'C'}">--}%
                        %{--<i class="fa fa-eye-slash" style="margin-left: 10px"></i>--}%
                        %{--</g:if>--}%
                        %{--${tramite.codigo}--}%
                        %{--<g:if test="${tramite.anexo == 1 && DocumentoTramite.countByTramite(tramite) > 0}">--}%
                        %{--<i class="fa fa-paperclip fa-fw" style="margin-left: 10px"></i>--}%
                        %{--</g:if>--}%
                        %{--<g:if test="${tramite.externo == '1' || tramite.tipoDocumento.codigo == 'DEX'}">--}%
                        %{--(ext)--}%
                        %{--</g:if>--}%
                        %{--</td>--}%
                        <td class="codigo">
                        %{--${tramite.deId} ${session.usuario.id} ${tramite.deId == session.usuario.id}<br/>--}%
                            <g:if test="${tramite?.tipoTramite?.codigo == 'C'}">
                                <i class="fa fa-eye-slash"></i>
                            </g:if>
                            <g:if test="${tramite?.anexo == 1 && DocumentoTramite.countByTramite(tramite) > 0}">
                                <i class="fa fa-paperclip"></i>
                            </g:if>
                            ${tramite?.codigo}
                            <g:if test="${tramite.externo == '1' || tramite.tipoDocumento.codigo == 'DEX'}">
                                (ext)
                            </g:if>
                        </td>

                        <td class="creacion">
                            ${tramite.fechaCreacion.format('dd-MM-yyyy HH:mm')}
                        </td>

                        <td class="de" style="width: 150px">
                            <g:if test="${tramite.tipoDocumento.codigo == 'DEX'}">
                                ${tramite.paraExterno} (ext)
                            </g:if>
                            <g:else>
                                <g:if test="${tramite.deDepartamento}">
                                    ${tramite.deDepartamento.descripcion}
                                </g:if>
                                <g:elseif test="${tramite.de}">
                                    %{--${tramite.de.nombre} ${tramite.de.apellido} (${tramite.de.departamento.codigo})--}%
                                    ${tramite.de.nombre} ${tramite.de.apellido} (${tramite.departamentoSigla})
                                </g:elseif>
                            </g:else>
                        </td>

                        <td class="para">
                            <g:if test="${tramite.tipoDocumento.codigo == 'OFI'}">
                                ${tramite.paraExterno} (ext)
                            </g:if>
                            <g:else>
                                <g:if test="${tramite.para}">
                                    <g:if test="${tramite.para.persona}">
                                        ${tramite.para.persona.nombre} ${tramite.para.persona.apellido} (${tramite.para.persona.departamento?.codigo})
                                    </g:if>
                                    <g:elseif test="${tramite.para.departamento}">
                                        ${tramite.para.departamento.descripcion}
                                    </g:elseif>
                                </g:if>
                                <g:if test="${tramite.copias && tramite.copias.size() > 0}">
                                    <span class="small">
                                        <strong>CC:</strong>
                                        <g:each in="${tramite.copias}" var="c" status="i">
                                            <g:if test="${c.persona}">
                                                ${c.persona.nombre} ${c.persona.apellido} (${c.persona.departamento?.codigo})${i < tramite.copias.size() - 1 ? ', ' : ''}
                                            </g:if>
                                            <g:elseif test="${c.departamento}">
                                                ${c.departamento.codigo}${i < tramite.copias.size() - 1 ? ', ' : ''}
                                            </g:elseif>
                                        </g:each>
                                    </span>
                                </g:if>
                            </g:else>
                        </td>


                        <td class="asunto">
                            ${tramite.asunto}
                        </td>

                        <td class="prioridad">
                            ${tramite.prioridad.descripcion}
                        </td>

                        <td class="envia">
                            <g:if test="${envia}">
                                ${envia.persona.nombre} ${envia.persona.apellido}
                            </g:if>
                        </td>
                        <td class="envio">
                            <g:if test="${tramite.fechaEnvio}">
                                ${tramite.fechaEnvio.format('dd-MM-yyyy HH:mm')}
                            </g:if>
                        </td>

                        <td class="recepcion">
                            <g:if test="${recibe && recibe.fechaRecepcion && tramite.estadoTramite == estadoRecibido}">
                                ${recibe.fechaRecepcion.format('dd-MM-yyyy HH:mm')}
                            </g:if>
                        </td>
                    </tr>
                </g:each>

            </tbody>
        </table>

    </span>

</div>


<script type="text/javascript">
    $(function () {
        $("tr").contextMenu({
            items  : createContextMenu,
            onShow : function ($element) {
                $element.addClass("trHighlight");
            },
            onHide : function ($element) {
                $(".trHighlight").removeClass("trHighlight");
            }
        });

        $('.table').fixedHeaderTable({
            height : ${msg == '' ? 550 : 500}
        });
    });
</script>
