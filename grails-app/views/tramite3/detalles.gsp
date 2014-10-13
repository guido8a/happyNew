<%@ page import="happy.tramites.DocumentoTramite; happy.seguridad.Persona" %>
<div style="max-height: 500px; overflow-y: auto; overflow-x: hidden;font-size: 11px">
    <g:each in="${tramites}" var="t" status="i">
        <g:if test="${i == 0}">
            <div style="margin-bottom: 20px;min-height: 140px" class="vertical-container">
                <p class="css-vertical-text">D. Principal</p>

                <div class="linea"></div>

                <div class="row">
                    <div class="col-xs-1 negrilla">No:</div>

                    <div class="col-xs-3">${t.codigo}</div>

                    <div class="col-xs-1 negrilla">Fecha:</div>

                    <div class="col-xs-3">${t.fechaCreacion.format("dd-MM-yyyy HH:mm")}</div>
                </div>
                <g:if test="${t.tipoTramite.codigo == 'C'}">
                    <div class="row">
                        <div class="col-xs-2 negrilla">Confidencial</div>
                    </div>
                </g:if>
                <div class="row">
                    <div class="col-xs-1 negrilla">De:</div>

                    <div class="col-xs-3">
                        <g:if test="${t.tipoDocumento.codigo == 'DEX'}">
                            ${t.paraExterno} (EXT)
                        </g:if>
                        <g:else>
                            ${t.deDepartamento ? t.deDepartamento.codigo : "" + t.de.departamento.codigo + ":" + t.de.nombre + ' ' + t.de.apellido}
                        </g:else>
                    </div>

                    <div class="col-xs-6">
                        <g:each in="${happy.tramites.PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteNotInList(t, rolesNo, [sort: 'rolPersonaTramite'])}" var="pdt" status="j">
                        %{--${pdt.estado.descripcion}${pdt.id}${pdt.estado?.codigo}--}%
                            <g:set var="fecha" value=""></g:set>
                            <g:set var="estado" value=""></g:set>
                            <g:if test="${pdt?.estado?.codigo == 'E006'}">
                                <g:set var="estado" value="ANULADO"></g:set>
                                <g:set var="fecha" value="${pdt.fechaAnulacion?.format('dd-MM-yyyy HH:mm')}"></g:set>
                            </g:if>
                            <g:else>
                                <g:if test="${pdt?.estado?.codigo == 'E003' && pdt.fechaEnvio}">
                                    <g:set var="estado" value="ENVIADO"></g:set>
                                    <g:set var="fecha" value="${pdt.fechaEnvio?.format('dd-MM-yyyy HH:mm')}"></g:set>
                                </g:if>
                                <g:else>
                                    <g:if test="${pdt?.estado?.codigo == 'E004' && pdt.fechaRecepcion}">

                                        <g:set var="estado" value="RECIBIDO"></g:set>
                                        <g:set var="fecha" value="${pdt.fechaRecepcion?.format('dd-MM-yyyy HH:mm')}"></g:set>
                                    </g:if>
                                    <g:else>

                                        <g:set var="estado" value="CREADO"></g:set>
                                        <g:set var="fecha" value="${pdt.tramite.fechaCreacion?.format('dd-MM-yyyy HH:mm')}"></g:set>
                                    </g:else>
                                </g:else>
                            </g:else>

                            <span style="font-weight: bold">${pdt.rolPersonaTramite.descripcion}:</span>
                            <g:if test="${t.tipoDocumento.codigo == 'OFI' && pdt.rolPersonaTramite.codigo == 'R001'}">
                                ${t.paraExterno} (EXT)
                            </g:if>
                            <g:else>
                                ${(pdt.departamento) ? pdt?.departamento?.codigo : "" + pdt.persona?.departamento?.codigo + ":" + pdt.persona?.login}
                            </g:else>

                            <b><span style="${pdt?.estado?.codigo == 'E006' ? 'color:red' : ''}">${estado}</span>
                            </b> el ${fecha} <br>

                        %{--${pdt.fechaRecepcion ? "(" + pdt.fechaRecepcion.format("dd-MM-yyyy") + ")" : ""}--}%
                        </g:each>
                    </div>
                </div>

                <div class="row">
                    <div class="col-xs-1 negrilla">Asunto:</div>

                    <div class="col-xs-8">${t.asunto}</div>
                </div>

                <g:if test="${t.personaPuedeLeer(session.usuario) && t.texto?.size() > 2}">
                    <div class="row" style="margin-bottom: 10px">
                        <div class="col-xs-1 negrilla">Texto:</div>

                        <div class="col-xs-10" style="background: #dedede; max-height: 300px; overflow: auto;">
                            %{--<g:each in="${0..5}" var="i">--}%
                            <util:renderHTML html="${t.texto}"/>
                            %{--</g:each>--}%
                        </div>
                    </div>
                </g:if>
                <g:if test="${t.observaciones}">
                    <div class="row" style="margin-bottom: 10px">
                        <div class="col-xs-1 negrilla">Obser:</div>

                        <div class="col-xs-10">${t.observaciones}</div>
                    </div>
                </g:if>
                <g:if test="${t.anexo == 1}">
                    <g:if test="${t.personaPuedeLeerAnexo(session.usuario)}">
                        <div class="row" style="margin-bottom: 10px;margin-left: 2px">
                            <g:each in="${DocumentoTramite.findAllByTramite(t)}" var="anexo" status="k">
                                <span style='color: #327BBA'>Archivo:</span>
                                ${anexo.path}
                                <a href='#' class='btn btn-success bajar' style='margin-right: 15px' title="Descargar Archivo" iden="${anexo.id}">
                                    <i class="fa fa-download"></i>
                                </a>
                                ${anexo.descripcion}
                                <br>
                            </g:each>
                        </div>
                    </g:if>
                </g:if>
            </div>
        </g:if>
        <g:else>
            <div style="margin-bottom: 20px" class="vertical-container">
                <p class="css-vertical-text">Trámite</p>

                <div class="linea"></div>

                <div class="row">
                    <div class="col-xs-1 negrilla">No:</div>

                    <div class="col-xs-3">${t.codigo}</div>

                    <div class="col-xs-1 negrilla">Fecha:</div>

                    <div class="col-xs-3">${t.fechaCreacion.format("dd-MM-yyyy HH:mm")}</div>
                </div>
                <g:if test="${t.tipoTramite.codigo == 'C'}">
                    <div class="row">
                        <div class="col-xs-2 negrilla">Confidencial</div>
                    </div>
                </g:if>
                <div class="row">
                    <div class="col-xs-1 negrilla">De:</div>

                    <div class="col-xs-3">
                        <g:if test="${t.tipoDocumento.codigo == 'DEX'}">
                            ${t.paraExterno} (EXT)
                        </g:if>
                        <g:else>
                            ${t.deDepartamento ? t.deDepartamento.codigo : "" + t.de.departamento.codigo + ":" + t.de.nombre + ' ' + t.de.apellido}
                        </g:else>
                    </div>

                    <div class="col-xs-6">
                        <g:each in="${happy.tramites.PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteNotInList(t, rolesNo, [sort: 'rolPersonaTramite'])}" var="pdt" status="j">
                        %{--${pdt.estado.descripcion}${pdt.id}${pdt.estado?.codigo}--}%
                            <g:set var="fecha" value=""></g:set>
                            <g:set var="estado" value=""></g:set>
                            <g:if test="${pdt?.estado?.codigo == 'E006'}">
                                <g:set var="estado" value="ANULADO"></g:set>
                                <g:set var="fecha" value="${pdt.fechaAnulacion?.format('dd-MM-yyyy HH:mm')}"></g:set>
                            </g:if>
                            <g:else>
                                <g:if test="${pdt?.estado?.codigo == 'E003' && pdt.fechaEnvio}">
                                    <g:set var="estado" value="ENVIADO"></g:set>
                                    <g:set var="fecha" value="${pdt.fechaEnvio?.format('dd-MM-yyyy HH:mm')}"></g:set>
                                </g:if>
                                <g:else>
                                    <g:if test="${pdt?.estado?.codigo == 'E004' && pdt.fechaRecepcion}">

                                        <g:set var="estado" value="RECIBIDO"></g:set>
                                        <g:set var="fecha" value="${pdt.fechaRecepcion?.format('dd-MM-yyyy HH:mm')}"></g:set>
                                    </g:if>
                                    <g:else>

                                        <g:set var="estado" value="CREADO"></g:set>
                                        <g:set var="fecha" value="${pdt.tramite.fechaCreacion?.format('dd-MM-yyyy HH:mm')}"></g:set>
                                    </g:else>
                                </g:else>
                            </g:else>

                            <span style="font-weight: bold">${pdt.rolPersonaTramite.descripcion}:</span>
                            <g:if test="${t.tipoDocumento.codigo == 'OFI' && pdt.rolPersonaTramite.codigo == 'R001'}">
                                ${t.paraExterno} (EXT)
                            </g:if>
                            <g:else>
                                ${(pdt.departamento) ? pdt?.departamento?.codigo : "" + pdt.persona?.departamento?.codigo + ":" + pdt.persona?.login}
                            </g:else>

                            <b><span style="${pdt?.estado?.codigo == 'E006' ? 'color:red' : ''}">${estado}</span>
                            </b> el ${fecha} <br>

                        %{--${pdt.fechaRecepcion ? "(" + pdt.fechaRecepcion.format("dd-MM-yyyy") + ")" : ""}--}%
                        </g:each>
                    </div>
                </div>

                <div class="row">
                    <div class="col-xs-1 negrilla">Asunto:</div>

                    <div class="col-xs-8">${t.asunto}</div>
                </div>

                <g:if test="${t.personaPuedeLeer(session.usuario) && t.texto?.size() > 2}">
                    <div class="row" style="margin-bottom: 10px">
                        <div class="col-xs-1 negrilla">Texto:</div>

                        <div class="col-xs-10" style="background: #dedede; max-height: 300px; overflow: auto;">
                            %{--<g:each in="${0..5}" var="i">--}%
                            <util:renderHTML html="${t.texto}"/>
                            %{--</g:each>--}%
                        </div>
                    </div>
                </g:if>

                <g:if test="${t.observaciones}">
                    <div class="row" style="margin-bottom: 10px">
                        <div class="col-xs-1 negrilla">Obser:</div>

                        <div class="col-xs-10">${tramite.observaciones}</div>
                    </div>
                </g:if>
                <g:if test="${t.anexo == 1}">
                    <g:if test="${t.personaPuedeLeer(session.usuario)}">
                        <div class="row" style="margin-bottom: 10px;margin-left: 2px">
                            <g:each in="${happy.tramites.DocumentoTramite.findAllByTramite(t)}" var="anexo" status="k">
                                <span style='color: #327BBA'>Archivo:</span>
                                ${anexo.path}
                                <a href='#' class='btn btn-success bajar' style='margin-right: 15px' title="Descargar Archivo" iden="${anexo.id}">
                                    <i class="fa fa-download"></i>
                                </a>
                                ${anexo.descripcion}
                                <br>
                            </g:each>
                        </div>
                    </g:if>
                </g:if>
            </div>
        </g:else>

    </g:each>

</div>
<script type="text/javascript">
    $(".bajar").click(function () {
        var id = $(this).attr("iden");
        openLoader();
        $.ajax({
            type    : "POST",
            url     : "${g.createLink(controller: 'documentoTramite',action: 'generateKey')}",
            data    : "id=" + id,
            success : function (msg) {
                closeLoader();
                if (msg == "ok") {
                    location.href = "${g.createLink(controller: 'documentoTramite', action: 'descargarDoc')}/" + id
                } else {
                    bootbox.alert("No se ha encontrado el archivo solicitado");
                }
            }
        });
    })
</script>