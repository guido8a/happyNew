<%@ page import="happy.tramites.OrigenTramite; happy.tramites.TipoPrioridad; happy.tramites.TipoDocumento" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            <g:if test="${tramite.id}">
                Modificar datos del trámite
            </g:if>
            <g:else>
                Creación de trámites o documentos principales
            </g:else>
        </title>
        <style>

        option.selected {
            background : #DDD;
            color      : #999;
        }

        li {
        }

        .selectable li {
            cursor        : pointer;
            border-bottom : solid 1px #0088CC;
            margin-left   : 20px;
        }

        .selectable li:hover {
            background : #B5D1DF;
        }

        .selectable li.selected {
            background : #81B5CF;
            color      : #0A384F;
        }

        .fieldLista {
            width   : 450px;
            height  : 250px;
            border  : 1px solid #0088CC;
            margin  : 10px 10px 20px 10px;
            padding : 15px;
            float   : left;
        }

        .divBotones {
            width      : 30px;
            height     : 130px;
            margin-top : 75px;
            float      : left;
        }

        .vertical-container {
            padding-bottom : 10px;;
        }

        .texto {
            max-height : 80px;
            overflow   : auto;
            background : #EFE4D1;
            padding    : 3px;
        }
        </style>
    </head>

    <body>
        <elm:flashMessage tipo="${flash.tipo}" clase="${flash.clase}">${flash.message}</elm:flashMessage>

        <!-- botones -->
        <div class="btn-toolbar toolbar">
            <div class="btn-group">
                <g:link action="redactar" class="btn btn-azul btnSave">
                    <i class="fa fa-save"></i> Guardar y empezar a redactar
                </g:link>
            %{--<a href="#" class="btn btn-azul" id="guardar">--}%
            %{--<i class="fa fa-save"></i> ${(tramite) ? "Guardar " : "Guardar y empezar a redactar"}--}%
            %{--</a>--}%
            %{--<g:link action="bandejaEntrada" class="btn btn-azul btnRegresar">--}%
            %{--<i class="fa fa-list-ul"></i> Bandeja de Entrada--}%
            %{--</g:link>--}%
                <g:link controller="tramite3" action="bandejaEntradaDpto" class="btn btn-azul btnRegresar">
                    <i class="fa fa-list-ul"></i> Bandeja de Entrada
                </g:link>
                <g:if test="${tramite.padre || tramite.id}">
                    <a href="#" class="btn btn-azul" id="btnDetalles">
                        <i class="fa fa-search"></i> Detalles
                    </a>
                </g:if>
            %{--<g:if test="${padre}">--}%
                <g:link controller="tramite3" action="bandejaEntradaDpto" class="btn btn-default btnRegresar">
                    <i class="fa fa-times"></i> Cancelar
                </g:link>
            %{--</g:if>--}%
            </div>

        </div>


    %{--<g:form class="frmTramite" controller="tramite3" action="save">--}%
        <g:form class="frmTramite" action="saveDep">
            <g:hiddenField name="tramite.padre.id" value="${padre?.id}"/>
            <g:hiddenField name="tramite.id" value="${tramite?.id}"/>
            <g:hiddenField name="tramite.hiddenCC" id="hiddenCC" value="${cc}"/>
        %{--<g:hiddenField name="dpto" id="hiddenCC" value="${dpto}"/>--}%
        <g:if test="${padre}">
            <div style="margin-top: 30px; min-height: 100px;font-size: 11px" class="vertical-container">

                <p class="css-vertical-text">D. Principal</p>
                <div class="linea"></div>
                <div class="row">
                    <div class="col-xs-1 negrilla">Documento:</div>
                    <div class="col-xs-2">${principal.codigo}</div>
                    <div class="col-xs-1 negrilla" style="width: 55px">Fecha:</div>
                    <div class="col-xs-2">${principal.fechaCreacion.format("dd-MM-yyyy")}</div>
                    <div class="col-xs-1 negrilla" style="width: 32px">De:</div>
                    <div class="col-xs-3">${principal.deDepartamento ? principal.deDepartamento.codigo :""+principal.de.departamento.codigo+":"+principal.de.nombre + ' ' + principal.de.apellido}</div>
                </div>

                <div class="row ">
                    <div class="col-xs-10">
                        <g:each in="${happy.tramites.PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteNotInList(principal,rolesNo)}" var="pdt" status="j">
                            <span style="font-weight: bold">${pdt.rolPersonaTramite.descripcion}:</span>
                            <span style="margin-right: 10px">
                                ${(pdt.departamento)?pdt.departamento:""+pdt.persona.departamento.codigo+":"+pdt.persona}
                                ${pdt.fechaRecepcion?"("+pdt.fechaRecepcion.format("dd-MM-yyyy")+")":""}
                            </span>
                        </g:each>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-1 negrilla">Asunto:</div>

                    <div class="col-md-11">${principal.asunto}</div>
                </div>
                <g:if test="${principal.personaPuedeLeer(session.usuario) && principal.texto?.trim() != ''}">
                    <div class="row">
                        <div class="col-md-1 negrilla">Texto:</div>

                        <div class="col-md-11 texto">
                            <util:renderHTML html="${principal.texto}"/>
                        </div>
                    </div>
                </g:if>

                <g:if test="${principal.observaciones && principal.observaciones?.trim() != ''}">
                    <div class="row">
                        <div class="col-md-1 negrilla">Obs:</div>

                        <div class="col-md-11">${principal.observaciones}</div>
                    </div>
                </g:if>

            </div>
            <g:if test="${padre != principal}">
                <div style="margin-top: 30px; min-height: 100px;font-size: 11px" class="vertical-container">

                    <p class="css-vertical-text">Contesta a</p>

                    <div class="linea"></div>

                    <div class="row">
                        <div class="col-xs-1 negrilla">Documento:</div>
                        <div class="col-xs-2">${padre.codigo}</div>
                        <div class="col-xs-1 negrilla" style="width: 55px">Fecha:</div>
                        <div class="col-xs-2">${padre.fechaCreacion.format("dd-MM-yyyy")}</div>
                        <div class="col-xs-1 negrilla" style="width: 32px">De:</div>
                        <div class="col-xs-3">${padre.deDepartamento ? padre.deDepartamento.codigo :""+padre.de.departamento.codigo+":"+padre.de.nombre + ' ' + padre.de.apellido}</div>
                    </div>

                    <div class="row ">
                        <div class="col-xs-10">
                            <g:each in="${happy.tramites.PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteNotInList(padre,rolesNo)}" var="pdt" status="j">
                                <span style="font-weight: bold">${pdt.rolPersonaTramite.descripcion}:</span>
                                <span style="margin-right: 10px">
                                    ${(pdt.departamento)?pdt.departamento:""+pdt.persona.departamento.codigo+":"+pdt.persona}
                                    ${pdt.fechaRecepcion?"("+pdt.fechaRecepcion.format("dd-MM-yyyy")+")":""}
                                </span>
                            </g:each>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-1 negrilla">Asunto:</div>

                        <div class="col-md-11">${padre.asunto}</div>
                    </div>

                    <g:if test="${padre.personaPuedeLeer(session.usuario) && padre.texto?.trim() != ''}">
                        <div class="row">
                            <div class="col-md-1 negrilla">Texto:</div>

                            <div class="col-md-11 texto">
                                <util:renderHTML html="${padre.texto}"/>
                            </div>
                        </div>
                    </g:if>

                    <g:if test="${padre.observaciones && padre.observaciones?.trim() != ''}">
                        <div class="row">
                            <div class="col-md-1 negrilla">Obs:</div>

                            <div class="col-md-11">${padre.observaciones}</div>
                        </div>
                    </g:if>
                </div>
            </g:if>
        </g:if>

            <div style="margin-top: 30px;" class="vertical-container">

                <p class="css-vertical-text">Trámite</p>

                <div class="linea"></div>

                %{--<g:if test="${padre}">--}%
                %{--<div class="alert alert-info">--}%
                %{--<p>--}%
                %{--<b>Trámite principal:</b>--}%
                %{--${padre.codigo} - ${padre.asunto}--}%
                %{--<g:link controller="tramite3" action="seguimientoTramite" id="${padre.id}" params="[prev: 'crearTramite']" class="alert-link pull-right">--}%
                %{--Seguimiento del trámite--}%
                %{--</g:link>--}%
                %{--</p>--}%
                %{--</div>--}%
                %{--</g:if>--}%

                <div class="row">
                    <div class="col-xs-4">
                        <b style="margin-right: 5px">De:</b>

                        <div class="uneditable-input label-shared" id="de" title="${de.departamento?.descripcion}">
                            ${de.departamento?.codigo}
                        </div>
                    </div>

                    %{--<g:if test="${padre}">--}%
                    %{--<div class="col-xs-3 negrilla">--}%
                    %{--Padre:--}%
                    %{--<input type="text" name="padre" class="form-control label-shared" id="padre" value="${padre?.codigo}" disabled/>--}%
                    %{--</div>--}%
                    %{--</g:if>--}%

                    <div class="col-xs-3" style="margin-top: -25px">
                        <b>Tipo de documento:</b>
                        <elm:comboTipoDoc id="tipoDocumento" name="tramite.tipoDocumento.id" class="many-to-one form-control required"
                                          value="${tramite.tipoDocumentoId}" tramite="${tramite}"/>
                    </div>


                    <div class="col-xs-4 negrilla hide" id="divPara" style="margin-top: -10px">
                        %{--<elm:comboPara name="tramite.para" id="para" style="width:310px;" value="${session.usuario?.departamento?.id * -1}"--}%
                        %{--class="form-control label-shared required"/>--}%
                        %{--<g:select name="tramite.para" id="para" from="${disponibles}" optionKey="id" optionValue="label"--}%
                        %{--style="width:310px;" class="form-control label-shared required" value="${persona?.departamento?.id * -1}"/>--}%
                        %{--<g:select name="tramite.origenTramite.id" id="paraExt" from="${OrigenTramite.list([sort: 'nombre'])}" optionKey="id"--}%
                        %{--optionValue="nombre" style="width:310px;" class="form-control label-shared required"/>--}%
                    </div>

                    %{--<div class="col-xs-1 negrilla hide" id="divBotonInfo">--}%
                    %{--<a href="#" id="btnInfoPara" class="btn btn-sm btn-info">--}%
                    %{--<i class="fa fa-search"></i>--}%
                    %{--</a>--}%
                    %{--</div>--}%
                    %{--<div class="col-xs-2 negrilla hide" id="divConfidencial">--}%
                    %{--<label for="confi"><input type="checkbox" name="confi" id="confi"/> Confidencial</label>--}%
                    %{--</div>--}%

                    %{--<div class="col-xs-2 negrilla hide" id="divAnexos">--}%
                    %{--<label for="anexo"><input type="checkbox" name="anexo" id="anexo"/> Con anexos</label>--}%
                    %{--</div>--}%
                </div>

                <div class="row">
                    %{--<div class="col-xs-3">--}%
                    %{--<b>Tipo de documento:</b>--}%
                    %{--<elm:select id="tipoDocumento" name="tramite.tipoDocumento.id" class="many-to-one form-control required"--}%
                    %{--from="${session.usuario.tiposDocumento}"--}%
                    %{--value="${tramite.tipoDocumentoId}" optionKey="id" optionValue="descripcion"--}%
                    %{--optionClass="codigo" noSelection="['': 'Seleccione el tipo de documento']"/>--}%
                    %{--</div>--}%

                    <div class="col-xs-2 ">
                        <b>Prioridad:</b>
                        %{--<g:select name="tramite.prioridad.id" class="many-to-one form-control required" from="${happy.tramites.TipoPrioridad.list(['sort': 'tiempo', order: 'desc'])}" value="" optionKey="id" optionValue="descripcion"></g:select>--}%
                        <elm:select name="tramite.prioridad.id" id="prioridad" class="many-to-one form-control required" from="${TipoPrioridad.list()}"
                                    value="${tramite.prioridadId ?: 3}" optionKey="id" optionValue="descripcion" optionClass="tiempo"/>
                    </div>

                    %{--<div class="col-xs-3 negrilla">--}%
                    %{--<span class="grupo">--}%
                    %{--Fecha límite de respuesta:--}%
                    %{--<elm:datetimepicker name="fechaLimiteRespuesta" title="Fecha límite de respuesta " class="datepicker form-control required"--}%
                    %{--value="${tramite.fechaLimiteRespuesta?.format('dd-MM-yyyy')}"/>--}%
                    %{--</span>--}%
                    %{--</div>--}%

                    <div class="col-xs-2 ">
                        <b>Creado el:</b>
                        <input type="text" name="tramite.fecha" class="form-control required label-shared" id="creado" maxlength="30"
                               value="${tramite.fechaCreacion.format('dd-MM-yyyy HH:mm')}" disabled style="width: 150px"/>
                    </div>

                    <div class="col-xs-2 ">
                        <b>Respuesta esperada:</b>
                        <span id="respuesta" class="uneditable-input">FECHA</span>
                    </div>

                    <div class="col-xs-2 negrilla" style="margin-top: 20px;" id="divCc">
                        <label for="cc"><input type="checkbox" name="cc" id="cc" ${cc != '' ? 'checked' : ''}/>
                            Con copia
                        </label>
                    </div>

                    <g:if test="${!tramite.padre}">
                        <div class="col-xs-2 negrilla hide" id="divConfidencial" style="margin-top: 20px;">
                            <label for="confi"><input type="checkbox" name="confi" id="confi" ${tramite.tipoTramite?.codigo == 'C' ? 'checked' : ''}/>
                                Confidencial
                            </label>
                        </div>
                    </g:if>

                    <div class="col-xs-2 negrilla hide" id="divAnexos" style="margin-top: 20px;">
                        <label for="anexo"><input type="checkbox" name="anexo" id="anexo" ${tramite.anexo == 1 ? 'checked' : ''}/>
                            Con anexos
                        </label>
                    </div>

                </div>

                <div class="row">
                    <div class="col-xs-12 ">
                        <span class="grupo">
                            <b>Asunto:</b>
                            <input type="text" name="tramite.asunto" class="form-control required" id="asunto" maxlength="1023"
                                   style="width: 900px;display: inline" value="${tramite.asunto}"/>
                        </span>
                    </div>
                </div>
            </div>

            <div style="float: left;width: 100%" class="vertical-container hide" id="divOrigen">
                <p class="css-vertical-text">Origen</p>

                <div class="linea"></div>

                <g:set var="origen" value="${tramite.origenTramite}"/>

                <div class="row">
                    <div class="col-xs-3 ">
                        <span class="grupo">
                            <b>Institución/Remitente:</b>
                            <g:textField name="origen.nombre" id="nombreOrigen" class="origenTram form-control required" maxlength="127"
                                         value="${origen?.nombre}"/>
                        </span>
                    </div>

                    <div class="col-xs-3 ">
                        <span class="grupo">
                            <b>Teléfono:</b>

                            <div class="input-group">
                                <g:textField id="telefonoOrigen" name="origen.telefono" class="origenTram form-control " maxlength="63"
                                             value="${origen?.telefono}"/>
                                <span class="input-group-addon"><i class="fa fa-phone"></i></span>
                            </div>
                        </span>
                    </div>
                    %{--<div class="col-xs-3 ">--}%
                    %{--<span class="grupo">--}%
                    %{--<b>Tipo de Persona:</b>--}%
                    %{--<g:select id="tipoPersonaOrigen" name="origen.tipoPersona.id" optionKey="id" optionValue="descripcion" class="origenTram form-control origen required"--}%
                    %{--from="${happy.tramites.TipoPersona.list([sort: 'descripcion'])}" value="${origen?.tipoPersonaId}"/>--}%
                    %{--</span>--}%
                    %{--</div>--}%

                    <div class="col-xs-3 ">
                        <span class="grupo">
                            <b>Cédula/R.U.C.:</b>
                            <g:textField name="origen.cedula" id="cedulaOrigen" class="origenTram form-control required" maxlength="13"
                                         value="${origen?.cedula}"/>
                        </span>
                    </div>


                    <div class="col-xs-3 ">
                        <span class="grupo">
                            <b>Nombre contacto:</b>
                            <g:textField id="nombreContactoOrigen" name="origen.nombreContacto" class="origenTram form-control " maxlength="31"
                                         value="${origen?.nombreContacto}"/>
                        </span>
                    </div>
                </div>

                <div class="row">
                    <div class="col-xs-3 ">
                        <span class="grupo">
                            <b>Apellido contacto:</b>
                            <g:textField id="apellidoContactoOrigen" name="origen.apellidoContacto" class="origenTram form-control " maxlength="31"
                                         value="${origen?.apellidoContacto}"/>
                        </span>
                    </div>

                    %{--<div class="col-xs-3 ">--}%
                    %{--<span class="grupo">--}%
                    %{--<b>Título:</b>--}%
                    %{--<g:textField id="tituloOrigen" name="origen.titulo" class="origenTram form-control " maxlength="4"--}%
                    %{--value="${origen?.titulo}"/>--}%
                    %{--</span>--}%
                    %{--</div>--}%

                    %{--<div class="col-xs-3 ">--}%
                    %{--<span class="grupo">--}%
                    %{--<b>Cargo:</b>--}%
                    %{--<g:textField id="cargoOrigen" name="origen.cargo" class="origenTram form-control" maxlength="127"--}%
                    %{--value="${origen?.cargo}"/>--}%
                    %{--</span>--}%
                    %{--</div>--}%

                    %{--<div class="col-xs-3 ">--}%
                    %{--<span class="grupo">--}%
                    %{--<b>E-mail:</b>--}%

                    %{--<div class="input-group">--}%
                    %{--<g:textField id="mailOrigen" name="origen.mail" class="origenTram form-control" maxlength="63"--}%
                    %{--value="${origen?.mail}"/>--}%
                    %{--<span class="input-group-addon"><i class="fa fa-envelope"></i></span>--}%
                    %{--</div>--}%
                    %{--</span>--}%
                    %{--</div>--}%
                </div>

                %{--<div class="row">--}%
                %{--</div>--}%

            </div>
        </g:form>

        <div style="float: left;width: 100%" class="vertical-container hide" id="divCopia">
            <p class="css-vertical-text" id="tituloCopia">Con copia / Circular</p>

            <div class="linea"></div>

            <fieldset class="ui-corner-all fieldLista">
                <legend style="margin-bottom: 1px">
                    Disponibles
                </legend>

                <ul id="ulDisponibles" style="margin-left:0;max-height: 195px; overflow: auto;" class="fa-ul selectable">
                    <g:each in="${disponibles}" var="disp">
                        <g:if test="${disp.id.toInteger() < 0}">
                            <li data-id="${disp.id}">
                                <i class="fa fa-li fa-building-o"></i> ${disp.label}
                            </li>
                        </g:if>
                        <g:else>
                            <li data-id="${disp.id}">
                                <i class="fa fa-li fa-user"></i> ${disp.label}
                            </li>
                        </g:else>
                    </g:each>
                </ul>
            </fieldset>

            <div class="divBotones">
                <div class="btn-group-vertical">
                    <a href="#" class="btn btn-default" title="Agregar todos" id="btnAddAll">
                        <i class="fa fa-angle-double-right"></i>
                    </a>
                    <a href="#" class="btn btn-default" title="Agregar seleccionados" id="btnAddSelected">
                        <i class="fa fa-angle-right"></i>
                    </a>
                    <a href="#" class="btn btn-default" title="Quitar seleccionados" id="btnRemoveSelected">
                        <i class="fa fa-angle-left"></i>
                    </a>
                    <a href="#" class="btn btn-default" title="Quitar todos" id="btnRemoveAll">
                        <i class="fa fa-angle-double-left"></i>
                    </a>
                </div>
            </div>

            <fieldset class="ui-corner-all fieldLista">
                <legend style="margin-bottom: 1px">
                    Seleccionados
                </legend>

                <ul id="ulSeleccionados" style="margin-left:0;max-height: 195px; overflow: auto;" class="fa-ul selectable">
                    <g:if test="${tramite.id}">
                        <g:each in="${tramite.copias}" var="disp">
                            <g:if test="${disp.persona}">
                                <li data-id="${disp.persona.id}">
                                    <i class="fa fa-li fa-user"></i> ${disp.persona.toString()}
                                </li>
                            </g:if>
                            <g:else>
                                <li data-id="-${disp.departamento.id}">
                                    <i class="fa fa-li fa-building-o"></i> ${disp.departamento.descripcion}
                                </li>
                            </g:else>
                        </g:each>
                    </g:if>
                </ul>
            </fieldset>

        </div>

        <div class="modal fade " id="dialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title">Detalles</h4>
                    </div>

                    <div class="modal-body" id="dialog-body" style="padding: 15px">

                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cerrar</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div>
        <script type="text/javascript">

            function destinatarioExiste(tipo, id) {
                var total = 0;
                $("#ulDestinatarios").children("li").each(function () {
                    if ($(this).data("tipo") == tipo && $(this).data("id") == id) {
                        total++;
                    }
                });
                return total;
            }

            function validarTipoDoc() {
                var $tipoDoc = $("#tipoDocumento");
                var $divPara = $("#divPara");
                var $divCopia = $("#divCopia");
                var $divCc = $("#divCc");
                var $divOrigen = $("#divOrigen");
                var $cc = $("#cc");
                var $tituloCopia = $("#tituloCopia");
                var $divConfidencial = $("#divConfidencial");
                var $divAnexos = $("#divAnexos");
                var $divBotonInfo = $("#divBotonInfo");
                var $chkAnexos = $("#anexo");

                var cod = $tipoDoc.find("option:selected").attr("class");
//                $("#ulSeleccionados li").removeClass("selected").appendTo($("#ulDisponibles"));
                <g:if test="${tramite.id && tramite.copias.size() == 0}">
                $cc.prop('checked', false);
                </g:if>
                $tituloCopia.text("Con copia");
                $divOrigen.addClass("hide");

                $divPara.html(spinner);
                $divBotonInfo.remove();
                $.ajax({
                    type    : "POST",
                    url     : "${createLink(controller:'tramite', action:'getPara_ajax')}",
                    data    : {
                        tramite : "${tramite.id}",
                        tipo    : 'departamento',
                        doc     : $tipoDoc.val()
                    },
                    success : function (msg) {
                        $divPara.replaceWith(msg);
                    }
                });
                $chkAnexos.prop("checked", false);

                switch (cod) {
                    case "CIR":
//                        $divPara.html("");
//                        $divPara.addClass("hide");
//                        $divBotonInfo.addClass("hide");
                        $divCopia.removeClass("hide");
                        $divCc.addClass("hide");
                        $("#ulDisponibles li").removeClass("selected").appendTo($("#ulSeleccionados"));
                        $tituloCopia.text("Circular");
                        $divConfidencial.addClass("hide");
                        $divAnexos.addClass("hide");
                        break;
                    case "OFI":
//                        $divPara.html($selParaExt).prepend("Para:");
//                        $divPara.removeClass("hide");
//                        $divBotonInfo.removeClass("hide");
                        $divCopia.addClass("hide");
                        $divCc.addClass("hide");
                        $divConfidencial.addClass("hide");
                        $divAnexos.removeClass("hide");
                        break;
                    case "DEX":
//                        $divPara.html($selPara).prepend("Para: ");
//                        $divPara.removeClass("hide");
//                        $divBotonInfo.removeClass("hide");
                        $divCopia.addClass("hide");
                        $divCc.addClass("hide");
                        $divOrigen.removeClass("hide");
                        $divConfidencial.addClass("hide");
                        $divAnexos.removeClass("hide");
                        $chkAnexos.prop("checked", true);
                        break;
                    case "SUM":
//                        $divPara.html($selPara).prepend("Para: ");
//                        $divPara.removeClass("hide");
//                        $divBotonInfo.removeClass("hide");
                        $divCopia.addClass("hide");
                        $divCc.removeClass("hide");
                        $divConfidencial.addClass("hide");
                        $divAnexos.addClass("hide");
                        break;
                    default :
//                        $divPara.html($selPara).prepend("Para: ");
//                        $divPara.removeClass("hide");
//                        $divBotonInfo.removeClass("hide");
                        $divCopia.addClass("hide");
                        $divCc.removeClass("hide");
                        $divConfidencial.removeClass("hide");
                        $divAnexos.removeClass("hide");
                }
                if (!cod) {
//                    $divPara.html("");
//                    $divPara.addClass("hide");
//                    $divBotonInfo.addClass("hide");
                    $divCopia.addClass("hide");
                    $divCc.addClass("hide");
                    $divConfidencial.addClass("hide");
                    $divAnexos.addClass("hide");
                }
            }

            function validarCheck() {
                var checked = $("#cc").is(":checked") && $("#cc").is(":visible");
                if (checked) {
                    $("#divCopia").removeClass("hide");
                } else {
                    $("#divCopia").addClass("hide");
                }
            }

            function addItem($combo, tipo) {
                var id = $combo.val();
                if (destinatarioExiste(tipo, id) == 0) {
                    var $selected = $combo.find("option:selected");
                    $selected.addClass("selected");
                    var text = $combo.find("option:selected").text();
                    var $ul = $("#ulDestinatarios");
                    var $del = $('<a href="#" class="btn btn-danger btn-xs pull-right"><i class="fa fa-times"></i></a>');
                    var $li = $("<li data-tipo='" + tipo + "' data-id='" + id + "'></li>");
                    var icon = "";
                    switch (tipo) {
                        case "usuario":
                            icon = "<i class='fa-li fa fa-user'></i>";
                            break;
                        case "direccion":
                            icon = "<i class='fa-li fa fa-building-o'></i>";
                            break;
                    }
                    $li.append(icon);
                    $li.append(text);
                    $li.append($del);
                    $ul.prepend($li);
                    $li.effect({
                        effect   : "highlight",
                        duration : 800
                    });
                    $del.click(function () {
                        $li.hide({
                            effect   : "blind",
                            complete : function () {
                                $li.remove();
                                $selected.removeClass("selected");
                            }
                        });
                        return false;
                    });
                }
            }

            function validarTiempos() {
                $.ajax({
                    type    : "POST",
                    url     : "${createLink(controller: 'tramite', action:'tiempoRespuestaEsperada_ajax')}",
                    data    : {
                        fecha     : "${tramite.fechaCreacion.format('dd-MM-yyyy HH:mm')}",
                        prioridad : $("#prioridad").val()
                    },
                    success : function (msg) {
                        var parts = msg.split("_")
                        if (parts[0] == "OK") {
                            $('#respuesta').text(parts[1]);
                        }
                    }
                });

//                var tiempo = parseInt($("#prioridad").find("option:selected").attr("class"));
//                var fecha = $("#creado").val();
//                fecha = Date.parse(fecha);
//                var limite = fecha.clone();
//                var maxHoy = fecha.clone().set({ hour : 12, minute : 30 });
//                if (tiempo > 4) {
//                    limite.add(tiempo).hours();
//                } else {
//                    var comp = fecha.compareTo(maxHoy); //-1: maxHoy=future, 0: igual, 1: maxHoy=past
//                    if (comp > -1) {
//                        limite.add(tiempo + 15).hours().add(30).minutes();
//                    } else {
//                        limite.add(tiempo).hours();
//                    }
//                }
//                $('#respuesta').text(limite.toString("dd-MM-yyyy HH:mm"));
            }

            function moveSelected($from, $to, muevePara) {
                var para = $("#para").val();
                $from.find("li.selected").removeClass("selected").each(function () {
                    var id = $(this).data("id");
                    if ((id == para && muevePara) || id != para) {
                        $(this).appendTo($to);
                    }
                });
            }

            $(function () {

                <g:if test="${bloqueo}">
                $("#modalTabelGray").css({marginTop : "-20px", zIndex : "999"}).show()
                </g:if>
                var $dir = $("#direccion");
                var $selPrioridad = $("#prioridad");
//                var $selPara = $("#para").clone(true);
//                var $selParaExt = $("#paraExt").clone(true);

                $("#btnInfoPara").click(function () {
                    var para = $("#para").val();
                    var paraExt = $("#paraExt").val();
                    var id;
                    var url = "";
                    if (para) {
                        if (parseInt(para) > 0) {
                            url = "${createLink(controller: 'persona', action: 'show_ajax')}";
                            id = para;
                        } else {
                            url = "${createLink(controller: 'departamento', action: 'show_ajax')}";
                            id = parseInt(para) * -1;
                        }
                    }
                    if (paraExt) {
                        url = "${createLink(controller: 'origenTramite', action: 'show_ajax')}";
                        id = paraExt;
                    }
                    $.ajax({
                        type    : "POST",
                        url     : url,
                        data    : {
                            id : id
                        },
                        success : function (msg) {
                            bootbox.dialog({
                                title   : "Información",
                                message : msg,
                                buttons : {
                                    aceptar : {
                                        label     : "Aceptar",
                                        className : "btn-primary",
                                        callback  : function () {
                                        }
                                    }
                                }
                            });
                        }
                    });
                    return false;
                });

                $selPrioridad.change(function () {
                    validarTiempos();
                }).change();

                $dir.change(function () {
                    var id = $(this).val();
                    var $div = $("#divBtnDir");
                    if (id != "" && $div.children().length == 0) {
                        var $btn = $("<a href='#' class='btn btn-xs btn-primary'>Agregar dirección</a>");
                        $div.html($btn);

                        $btn.click(function () {
                            addItem($dir, "direccion");
                            return false;
                        });
                    }
                    if (id == "") {
                        $div.html("");
                    }
                });

                $("#btnDetalles").click(function () {
                    $.ajax({
                        type    : 'POST',
                        url     : '${createLink(controller: 'tramite3', action: 'detalles')}',
                        data    : {
                            id : "${tramite.id?:tramite.padreId}"
                        },
                        success : function (msg) {
                            $("#dialog-body").html(msg)
                        }
                    });
                    $("#dialog").modal("show")
                    return false;
                });

                $("#cc").click(function () {
                    validarCheck();
                });

                $("#anexo").click(function () {
                    var tipoDoc = $("#tipoDocumento").find("option:checked").attr("class");
                    if (tipoDoc == "DEX") {
                        $(this).prop("checked", true);
                    }
                });

                $("#tipoDocumento").change(function () {
                    validarTipoDoc();
                }).change();

                $("#cedulaOrigen").blur(function () {
                    var ci = $.trim($(this).val());
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(controller: 'origenTramite', action:'getDatosByCedula_ajax')}",
                        data    : {
                            cedula : ci
                        },
                        success : function (msg) {
                            if (msg == "NO") {
                                $(".origenTram").not("#cedulaOrigen").val("");
                            } else {
                                $.each(msg, function (key, val) {
                                    if (key != "tipoPersona") {
                                        $("#" + key + "Origen").val(val);
                                    } else {
                                        $("#tipoPersonaOrigen").val(val.id);
                                    }
                                });
                            }
                        }
                    });
                });

                validarCheck();

                $(".selectable li").click(function () {
                    $(this).toggleClass("selected");
                });
                $("#btnAddAll").click(function () {
                    var $ul = $("#ulDisponibles");
                    $ul.find("li").addClass("selected");
                    moveSelected($ul, $("#ulSeleccionados"), false);
                });
                $("#btnAddSelected").click(function () {
                    moveSelected($("#ulDisponibles"), $("#ulSeleccionados"), false);
                });
                $("#btnRemoveSelected").click(function () {
                    moveSelected($("#ulSeleccionados"), $("#ulDisponibles"), true);
                });
                $("#btnRemoveAll").click(function () {
                    var $ul = $("#ulSeleccionados");
                    $ul.find("li").addClass("selected");
                    moveSelected($ul, $("#ulDisponibles"), true);
                });
                <g:if test="${!bloqueo}">
                $(".btnSave").click(function () {
                    if ($(".frmTramite").valid()) {
                        var cc = "";
                        $("#ulSeleccionados li").each(function () {
                            cc += $(this).data("id") + "_";
                        });
                        $("#hiddenCC").val(cc);
                        $(".frmTramite").submit();
                    }
                    return false;
                });
                </g:if>
                var validator = $(".frmTramite").validate({
                    errorClass     : "help-block",
                    errorPlacement : function (error, element) {
                        if (element.parent().hasClass("input-group")) {
                            error.insertAfter(element.parent());
                        } else {
                            error.insertAfter(element);
                        }
                        element.parents(".grupo").addClass('has-error');
                    },
                    success        : function (label) {
                        label.parents(".grupo").removeClass('has-error');
                    },
                    rules          : {
                        cedulaOrigen : {
                            required : {
                                depends : function (element) {
                                    return  $("#tipoDocumento").find("option:selected").hasClass("DEX");
                                }
                            }
                        },
                        nombreOrigen : {
                            required : {
                                depends : function (element) {
                                    return  $("#tipoDocumento").find("option:selected").hasClass("DEX");
                                }
                            }
                        }
                    }
                });
            });
        </script>

    </body>
</html>