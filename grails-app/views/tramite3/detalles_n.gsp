<%@ page import="happy.tramites.PersonaDocumentoTramite; happy.tramites.DocumentoTramite; happy.seguridad.Persona" %>

<style type="text/css">
.claseMin {
    max-height: 60px;
    overflow: auto;
}
.paraTam {
    max-height: 80px;
    overflow: auto;
}

.textoRecibido{
    font-style: italic;
    color: #000000;
}


</style>

<g:set var="recibe" value="${happy.tramites.RolPersonaTramite.get(3)}"/>
<g:set var="trmtpara" value="${happy.tramites.RolPersonaTramite.get(1)}"/>

<div style="max-height: 500px; overflow-y: auto; overflow-x: hidden;font-size: 11px">
    <g:if test="${tp}">
        <div style="margin-bottom: 20px;min-height: 240px" class="vertical-container">
            <p class="css-vertical-text">T. Principal</p>

            <div class="linea"></div>

            <div class="row">
                <div class="col-xs-1 negrilla">No:</div>

                <div class="col-xs-5">${tp.codigo}</div>

                <div class="col-xs-1 negrilla">Fecha:</div>

                <div class="col-xs-5">${tp.fechaCreacion.format("dd-MM-yyyy HH:mm")}</div>
            </div>
            <g:if test="${tp.tipoTramite.codigo == 'C'}">
                <div class="row">
                    <div class="col-xs-2 negrilla">Confidencial</div>
                </div>
            </g:if>

            <div class="row">
                <div class="col-xs-1 negrilla">De:</div>

                <div class="col-xs-5">
                    <g:if test="${tp.tipoDocumento.codigo == 'DEX'}">
                        ${tp.paraExterno} (EXT)
                    </g:if>
                    <g:else>
                        ${tp.deDepartamento ? tp.deDepartamento.codigo : "" + tp.de.departamento.codigo + ":" + tp.de.nombre + ' ' + tp.de.apellido}
                    </g:else>
                </div>


                <div class="col-xs-6 claseMin">
                    <g:each in="${happy.tramites.PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteNotInList(tp, rolesNo, [sort: 'rolPersonaTramite'])}" var="pdt" status="j">
                    %{--${pdt?.estado?.descripcion}--}%
                    %{--${pdt?.id}--}%
                    %{--${pdt?.estado?.codigo}--}%
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
                                <g:if test="${pdt?.estado?.codigo == 'E005' && pdt.fechaArchivo}">
                                    <g:set var="estado" value="ARCHIVADO"></g:set>
                                    <g:set var="fecha" value="${pdt.fechaArchivo?.format('dd-MM-yyyy HH:mm')}"></g:set>
                                </g:if>
                            </g:else>
                        </g:else>

                        <span style="font-weight: bold">${pdt.rolPersonaTramite.descripcion}:</span>
                        <g:if test="${tp.tipoDocumento.codigo == 'OFI' && pdt.rolPersonaTramite.codigo == 'R001'}">
                            ${tp.paraExterno} (EXT)
                        </g:if>
                        <g:else>
                            ${(pdt.departamento) ? pdt?.departamento?.codigo : "" + pdt.persona?.departamento?.codigo + ":" + pdt.persona?.login}
                        </g:else>

                        <b><span style="${pdt?.estado?.codigo == 'E006' || pdt?.estado?.codigo == 'E005' ? 'color:red' : ''}">${estado}</span>
                        </b> el ${fecha} <br>

                    %{--${pdt.fechaRecepcion ? "(" + pdt.fechaRecepcion.format("dd-MM-yyyy") + ")" : ""}--}%
                    </g:each>
                </div>
            </div>




            <div class="row">
                <div class="col-xs-1 negrilla text-warning">Asunto:</div>

                <div class="col-xs-8 text-info">${tp.asunto.decodeHTML()}</div>


                %{--<div class="col-xs-4 alert alert-warning negrilla">--}%
                %{--No: ${t.codigo}--}%
                %{--</div>--}%


            </div>

            <g:if test="${tp.personaPuedeLeer(session.usuario) && tp.texto?.size() > 2}">
                <div class="row" style="margin-bottom: 10px">
                    <div class="col-xs-1 negrilla">Texto:</div>

                    <div class="col-xs-10" style="background: #dedede; max-height: 300px; overflow: auto;">
                        %{--<g:each in="${0..5}" var="i">--}%
                        <util:renderHTML html="${tp.texto}"/>
                        %{--</g:each>--}%
                    </div>
                </div>
            </g:if>
            <g:if test="${tp.observaciones}">
                <div class="row" style="margin-bottom: 10px">
                    <div class="col-xs-1 negrilla">Obser:</div>

                    <div class="col-xs-10  claseMin">${tp.observaciones}</div>
                </div>
            </g:if>
            <g:if test="${tp.anexo == 1}">
                <g:if test="${tramite.estadoTramite.codigo == 'E004'}">
                    <g:if test="${session?.usuario?.id == tramite?.para?.persona?.id}">
                        <g:if test="${tp.personaPuedeLeerAnexo(session.usuario)}">
                            <div class="row" style="margin-bottom: 10px;margin-left: 2px">
                                <g:each in="${DocumentoTramite.findAllByTramite(tp)}" var="anexo" status="k">
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
                </g:if>


            </g:if>
        </div>
    </g:if>
    <g:each in="${tramites}" var="t" status="i">
        <g:if test="${i == 0}">
            <div style="margin-bottom: 20px;min-height: 240px;" class="vertical-container">
                <p class="css-vertical-text">Documento Principal</p>

                <div class="linea"></div>

                <div class="row">
                    <div class="col-xs-12 alert alert-warning" style="font-size: 14px; color: #000000">
                        <strong style="margin-right: 30px">${t.codigo} </strong>
                        <i class="fa fa-envelope"></i> ASUNTO: ${t.asunto.decodeHTML()}
                    %{--</div>--}%
                    %{--<div class="col-xs-3 alert alert-danger negrilla">--}%
                    </div>
                </div>

                <g:if test="${t.tipoTramite.codigo == 'C'}">
                    <div class="row">
                        <div class="col-xs-2 negrilla">Confidencial</div>
                    </div>
                </g:if>
                <div class="row" style="margin-top: -20px">
                    <div class="col-xs-9 alert alert-success negrilla"><i class="fa fa-user"></i> DE:
                    <g:if test="${t.tipoDocumento.codigo == 'DEX'}">
                        ${t.paraExterno} (EXT)
                    </g:if>
                    <g:else>
                        ${(t?.departamentoNombre ?: '') + ":" + (t?.persona ?: '')}
                    </g:else>
                    </div>
                    <div class="col-xs-3 negrilla alert alert-success">
                        <strong style="color: #000000">Enviado: ${t.fechaCreacion.format("dd-MM-yyyy HH:mm")}</strong>
                    </div>

                    <g:if test="${t.tipoDocumento.codigo == 'DEX'}">
                        <div class="col-xs-12 alert alert-success negrilla">
                            <div class="col-xs-4"> <strong style="color: #000000">Num. Doc.:</strong> ${t.numeroDocExterno} </div>
                            <div class="col-xs-4"> <strong style="color: #000000">Teléfono:</strong> ${t?.telefono}</div>
                            <div class="col-xs-4"> <strong style="color: #000000">Contacto:</strong> ${t?.contacto}</div>
                            %{--<div class="col-xs-3"> <strong style="color: #000000">Email:</strong> ${t?.mail} </div>--}%
                        </div>
                    </g:if>
                </div>

                <div class="row" style="margin-top: 0px">
                    <div class="col-xs-11 paraTam alert alert-info negrilla">
                        <g:each in="${happy.tramites.PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteNotInList(t, rolesNo, [sort: 'rolPersonaTramite'])}" var="pdt" status="j">
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
                                    <g:if test="${pdt?.estado?.codigo == 'E005' && pdt.fechaArchivo}">
                                        <g:set var="estado" value="ARCHIVADO"></g:set>
                                        <g:set var="fecha" value="${pdt.fechaArchivo?.format('dd-MM-yyyy HH:mm')}"></g:set>
                                    </g:if>
                                </g:else>
                            </g:else>

                            <i class="fa fa-arrow-circle-right fa-2x"></i> <strong style="${pdt?.rolPersonaTramite?.codigo == 'R002' ? 'color: #9a53af' : ''}"> ${pdt.rolPersonaTramite.descripcion}: </strong>
                            <g:if test="${t.tipoDocumento.codigo == 'OFI' && pdt.rolPersonaTramite.codigo == 'R001'}">
                                ${t.paraExterno} (EXT)
                            </g:if>
                            <g:else>
                                <g:if test="${pdt?.persona}">
                                    ${pdt?.persona}
                                </g:if>
                                <g:else>
                                    ${pdt?.departamentoNombre}
                                </g:else>
                            </g:else>

                            <b><span style="${pdt?.estado?.codigo == 'E006' || pdt?.estado?.codigo == 'E005' ? 'color:red' : 'color:green'}"><i class="fa ${pdt?.estado?.codigo == 'E006' || pdt?.estado?.codigo == 'E005' ? 'fa-close' : 'fa-check'}"></i> ${estado}</span>
                            </b> el ${fecha} <br>

                            <g:set var="recibidoVar2" value="${happy.tramites.PersonaDocumentoTramite.findByTramiteAndRolPersonaTramite(pdt?.tramite, recibe)?.personaNombre}"/>

                            <g:if test="${recibidoVar2 && tramite.estadoTramite.codigo == 'E004'}">
                                <div class="col-xs-2"></div>
                                <div class="col-xs-2 negrilla textoRecibido">RECIBIDO POR: </div>
                                <div class="col-xs-8 textoRecibido">${recibidoVar2}</div>
                            </g:if>
                        </g:each>
                    </div>
                </div>


                <g:if test="${t.personaPuedeLeer(session.usuario) && t.texto?.size() > 2}">
                    <p>
                        <a class="btn btn-primary" data-toggle="collapse" href="#collapseExample" role="button" aria-expanded="false" aria-controls="collapseExample">
                            Texto
                        </a>
                        <g:if test="${t.observaciones}">
                            <button class="btn btn-info" type="button" data-toggle="collapse" data-target="#collapseExample_${t?.id}" aria-expanded="false" aria-controls="collapseExample_${t?.id}">
                                Observaciones
                            </button>
                        </g:if>
                    </p>
                    <div class="collapse" id="collapseExample">
                        <div class="card card-body">
                            <div class="row" style="margin-bottom: 10px">
                                <div class="col-xs-1 negrilla">Texto:</div>
                                <div class="col-xs-10" style="background: #dedede; max-height: 300px; overflow: auto;">
                                    <g:each in="${0..5}">
                                        <util:renderHTML html="${t.texto}"/>
                                    </g:each>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="collapse" id="collapseExample_${t?.id}">
                        <div class="card card-body">
                            <g:if test="${t.observaciones}">
                                <div class="row" style="margin-bottom: 10px">
                                    <div class="col-xs-1 negrilla">Obser:</div>
                                    <div class="col-xs-10  claseMin">${t.observaciones}</div>
                                </div>
                            </g:if>
                            <g:if test="${t.anexo == 1}">
                                <g:if test="${tramite.estadoTramite.codigo == 'E004'}">
                                    <g:if test="${session?.usuario?.id == tramite?.para?.persona?.id}">
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
                                </g:if>
                            </g:if>
                        </div>
                    </div>
                </g:if>
                <g:else>
                    <div class="collapse" id="collapseExample_${t?.id}">
                        <div class="card card-body">
                            <g:if test="${t.anexo == 1}">
                                <g:if test="${tramite.estadoTramite.codigo == 'E004'}">
                                    <g:if test="${session?.usuario?.id == tramite?.para?.persona?.id}">
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
                                </g:if>
                            </g:if>
                        </div>
                    </div>
                </g:else>
            </div>
        </g:if>
        <g:else>
            <div style="margin-bottom: 20px" class="vertical-container">
                <p class="css-vertical-text">Trámite</p>

                <div class="linea"></div>

                <div class="row">
                    <div class="col-xs-1 negrilla">No:</div>

                    <div class="col-xs-5">${t.codigo}</div>

                    <div class="col-xs-1 negrilla">Fecha:</div>

                    <div class="col-xs-5">${t.fechaCreacion.format("dd-MM-yyyy HH:mm")}</div>
                </div>
                <g:if test="${t.tipoTramite.codigo == 'C'}">
                    <div class="row">
                        <div class="col-xs-2 negrilla">Confidencial</div>
                    </div>
                </g:if>
                <div class="row">
                    <div class="col-xs-1 negrilla">De:</div>

                    <div class="col-xs-5">
                        <g:if test="${t.tipoDocumento.codigo == 'DEX'}">
                            ${t.paraExterno} (EXT)
                        </g:if>
                        <g:else>
                        %{--${t.deDepartamento ? t.deDepartamento.codigo : "" + t.de.departamento.codigo + ":" + t.de.nombre + ' ' + t.de.apellido}--}%
                            ${(t?.departamentoNombre ?: '') + ":" + (t?.persona ?: '')}
                        </g:else>
                    </div>

                    <div class="col-xs-6 claseMin">
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
                                    <g:if test="${pdt?.estado?.codigo == 'E005' && pdt.fechaArchivo}">
                                        <g:set var="estado" value="ARCHIVADO"></g:set>
                                        <g:set var="fecha" value="${pdt.fechaArchivo?.format('dd-MM-yyyy HH:mm')}"></g:set>
                                    </g:if>
                                </g:else>
                            </g:else>

                            <span style="font-weight: bold">${pdt.rolPersonaTramite.descripcion}:</span>
                            <g:if test="${t.tipoDocumento.codigo == 'OFI' && pdt.rolPersonaTramite.codigo == 'R001'}">
                                ${t.paraExterno} (EXT)
                            </g:if>
                            <g:else>
                            %{--${(pdt.departamento) ? pdt?.departamento?.codigo : "" + pdt.persona?.departamento?.codigo + ":" + pdt.persona?.login}--}%
                            %{--${(pdt.departamento) ? pdt?.departamento?.codigo : " " + pdt.departamentoPersona?.codigo + ":" + pdt.persona?.login}--}%
                                <g:if test="${pdt?.persona}">
                                    ${pdt?.persona}
                                </g:if>
                                <g:else>
                                    ${pdt?.departamentoNombre}
                                </g:else>
                            </g:else>

                            <b>
                                <span style="${pdt?.estado?.codigo == 'E006' || pdt?.estado?.codigo == 'E005' ? 'color:red' : ''}">${estado}</span>
                            </b> el ${fecha} <br>

                        %{--<g:set var="recibidoVar" value="${happy.tramites.PersonaDocumentoTramite.findByAllTramiteAndRolPersonaTramite(pdt?.tramite, recibe)?.personaNombre}"/>--}%
                            <g:set var="recibidoVar" value="${happy.tramites.PersonaDocumentoTramite.findByTramiteAndRolPersonaTramiteAndDepartamentoPersona(pdt?.tramite,
                                    recibe, pdt?.departamento)?.personaNombre}"/>

                            <g:if test="${recibidoVar && tramite.estadoTramite.codigo == 'E004'}">
                            %{--<div class="row">--}%
                                <div class="col-xs-4 negrilla">RECIBIDO POR: </div>
                                <div class="col-xs-8">${recibidoVar}</div>
                            %{--</div>--}%
                            </g:if>

                        %{--${pdt.fechaRecepcion ? "(" + pdt.fechaRecepcion.format("dd-MM-yyyy") + ")" : ""}--}%
                        </g:each>
                    </div>
                </div>

            %{--<div class="row">--}%
            %{--<div class="col-xs-1 negrilla">De Original:</div>--}%

            %{--<div class="col-xs-3">--}%
            %{--<g:if test="${t.tipoDocumento.codigo == 'DEX'}">--}%
            %{--${t.paraExterno} (EXT)--}%
            %{--</g:if>--}%
            %{--<g:else>--}%
            %{--<g:if test="${t?.departamentoSigla && t?.persona}">--}%
            %{--${(t?.departamentoSigla ?: '') + ":" + (t?.persona ?: '')}--}%
            %{--</g:if>--}%
            %{--</g:else>--}%
            %{--</div>--}%
            %{--</div>--}%


                <g:if test="${t.tipoDocumento.codigo == 'DEX'}">
                    <div class="row">
                        %{--<div class="col-xs-1 negrilla">Institución:</div>--}%
                        %{--<div class="col-xs-3">${t?.paraExterno}--}%
                        %{--</div>--}%

                        <div class="col-xs-2 negrilla">Num. Doc.:</div>
                        <div class="col-xs-3">${t.numeroDocExterno} </div>

                        <div class="col-xs-1 negrilla">Teléfono:</div>
                        <div class="col-xs-3">${t?.telefono}
                        </div>

                    </div>
                    <div class="row" style="align-content: flex-start">

                        <div class="col-xs-2 negrilla">Contacto:</div>
                        <div class="col-xs-3">${t?.contacto}
                        </div>

                        <div class="col-xs-1 negrilla">Email:</div>
                        <div class="col-xs-3">${t?.mail} </div>
                    </div>
                %{--<div class="row">--}%
                %{----}%
                %{--</div>--}%
                </g:if>


                <div class="row">
                    <div class="col-xs-1 negrilla text-info">Asunto:</div>

                    <div class="col-xs-11 text-info">${t.asunto}</div>
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

                        <div class="col-xs-10  claseMin">${t.observaciones}</div>
                    </div>
                </g:if>
                <g:if test="${t.anexo == 1}">
                    <g:if test="${t.personaPuedeLeerAnexo(session.usuario)}">
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

    $(function() {
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
        });
    });
</script>