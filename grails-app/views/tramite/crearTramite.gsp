<%@ page import="happy.tramites.TipoPrioridad; happy.tramites.TipoDocumento" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Creación de trámites o documentos principales</title>
        <style>

        option.selected {
            background : #DDD;
            color      : #999;
        }

        li {
            border-bottom : solid 1px #0088CC;
            margin-left   : 20px;
        }

        .selectable li {
            cursor : pointer;
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
        </style>
    </head>

    <body>
        <elm:flashMessage tipo="${flash.tipo}" clase="${flash.clase}">${flash.message}</elm:flashMessage>

        <!-- botones -->
        <div class="btn-toolbar toolbar">
            <div class="btn-group">
                <g:link action="redactar" class="btn btn-azul btnSave">
                    <i class="fa fa-save"></i> ${(tramite.id) ? "Guardar " : "Guardar y empezar a redactar"}
                </g:link>
            %{--<a href="#" class="btn btn-azul" id="guardar">--}%
            %{--<i class="fa fa-save"></i> ${(tramite) ? "Guardar " : "Guardar y empezar a redactar"}--}%
            %{--</a>--}%
            </div>
        </div>


        <g:form class="frmTramite" controller="tramite3" action="save">
            <g:hiddenField name="tramite.padre.id" value="${padre?.id}"/>
            <g:hiddenField name="tramite.id" value="${tramite?.id}"/>
            <g:hiddenField name="tramite.hiddenCC" id="hiddenCC" value=""/>
            <div style="margin-top: 30px;" class="vertical-container">

                <p class="css-vertical-text">Tramite</p>

                <div class="linea"></div>

                <div class="row">
                    <div class="col-xs-3 negrilla">
                        De:
                        <input type="text" name="tramite.de" class="form-control required label-shared" id="de" maxlength="30" value="${de.nombre}" title="${de.nombre}" disabled/>
                    </div>

                    <div class="col-xs-4 negrilla" id="divPara">
                        <g:select name="tramite.para" id="para" from="${disponibles}" optionKey="id" optionValue="label" style="width:300px;" class="form-control label-shared required"/>
                    </div>
                </div>

                <div class="row">
                    <div class="col-xs-3 negrilla">
                        Tipo de documento:
                        <elm:select id="tipoDocumento" name="tramite.tipoDocumento.id" class="many-to-one form-control required" from="${TipoDocumento.list(['sort': 'descripcion'])}"
                                    value="${tramite.tipoDocumentoId}" optionKey="id" optionValue="descripcion" optionClass="codigo" noSelection="['': 'Seleccione el tipo de documento']"/>
                    </div>

                    <div class="col-xs-2 negrilla">
                        Prioridad:
                        %{--<g:select name="tramite.prioridad.id" class="many-to-one form-control required" from="${happy.tramites.TipoPrioridad.list(['sort': 'tiempo', order: 'desc'])}" value="" optionKey="id" optionValue="descripcion"></g:select>--}%
                        <g:select name="tramite.prioridad.id" class="many-to-one form-control required" from="${TipoPrioridad.list()}"
                                  value="${tramite.prioridadId ?: 3}" optionKey="id" optionValue="descripcion"/>
                    </div>

                    %{--<div class="col-xs-3 negrilla">--}%
                    %{--<span class="grupo">--}%
                    %{--Fecha límite de respuesta:--}%
                    %{--<elm:datetimepicker name="fechaLimiteRespuesta" title="Fecha límite de respuesta " class="datepicker form-control required"--}%
                    %{--value="${tramite.fechaLimiteRespuesta?.format('dd-MM-yyyy')}"/>--}%
                    %{--</span>--}%
                    %{--</div>--}%

                    <div class="col-xs-2 negrilla">
                        Creado el:
                        <input type="text" name="tramite.fecha" class="form-control required label-shared" id="creado" maxlength="30"
                               value="${tramite.fecha.format('dd-MM-yyyy  HH:mm')}" disabled style="width: 150px"/>
                    </div>

                    <div class="col-xs-2 negrilla" style="margin-top: 20px;" id="divCc">
                        <label for="cc"><input type="checkbox" name="cc" id="cc"/> Con copia</label>
                    </div>
                </div>

                <div class="row">
                    <div class="col-xs-12 negrilla">
                        <span class="grupo">
                            Asunto:
                            <input type="text" name="tramite.asunto" class="form-control required" id="asunto" maxlength="1023"
                                   style="width: 900px;display: inline" value="${tramite.asunto}"/>
                        </span>
                    </div>
                </div>
            </div>

            <div style="float: left;width: 100%" class="vertical-container hide" id="divOrigen">
                <p class="css-vertical-text">Origen</p>

                <div class="linea"></div>

                <div class="row">
                    <div class="col-xs-3 negrilla">
                        <span class="grupo">
                            Tipo de Persona:
                            <g:select name="origen.tipoPersona.id" optionKey="id" optionValue="descripcion" class="form-control"
                                      from="${happy.tramites.TipoPersona.list([sort: 'descripcion'])}"/>
                        </span>
                    </div>

                    <div class="col-xs-3 negrilla">
                        <span class="grupo">
                            Cédula/R.U.C.:
                            <g:textField name="origen.cedula" class="form-control" maxlength="13"/>
                        </span>
                    </div>

                    <div class="col-xs-3 negrilla">
                        <span class="grupo">
                            Nombre:
                            <g:textField name="origen.nombre" class="form-control" maxlength="127"/>
                        </span>
                    </div>

                    <div class="col-xs-3 negrilla">
                        <span class="grupo">
                            Nombre contacto:
                            <g:textField name="origen.nombreContacto" class="form-control" maxlength="31"/>
                        </span>
                    </div>
                </div>

                <div class="row">
                    <div class="col-xs-3 negrilla">
                        <span class="grupo">
                            Apellido contacto:
                            <g:textField name="origen.apellidoContacto" class="form-control" maxlength="31"/>
                        </span>
                    </div>

                    <div class="col-xs-3 negrilla">
                        <span class="grupo">
                            Título:
                            <g:textField name="origen.titulo" class="form-control" maxlength="4"/>
                        </span>
                    </div>

                    <div class="col-xs-3 negrilla">
                        <span class="grupo">
                            Cargo:
                            <g:textField name="origen.cargo" class="form-control" maxlength="127"/>
                        </span>
                    </div>

                    <div class="col-xs-3 negrilla">
                        <span class="grupo">
                            E-mail:
                            <div class="input-group">
                                <g:textField name="origen.mail" class="form-control" maxlength="63"/>
                                <span class="input-group-addon"><i class="fa fa-envelope"></i></span>
                            </div>
                        </span>
                    </div>
                </div>

                <div class="row">
                    <div class="col-xs-3 negrilla">
                        <span class="grupo">
                            Teléfono:
                            <div class="input-group">
                                <g:textField name="origen.telefono" class="form-control" maxlength="63"/>
                                <span class="input-group-addon"><i class="fa fa-phone"></i></span>
                            </div>
                        </span>
                    </div>
                </div>

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

                </ul>
            </fieldset>

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

            function validarTipoDoc($selPara) {
                var $tipoDoc = $("#tipoDocumento");
                var $divPara = $("#divPara");
                var $divCopia = $("#divCopia");
                var $divCc = $("#divCc");
                var $divOrigen = $("#divOrigen");
                var $cc = $("#cc");
                var $tituloCopia = $("#tituloCopia");

                var cod = $tipoDoc.find("option:selected").attr("class");
                $("#ulSeleccionados li").removeClass("selected").appendTo($("#ulDisponibles"));
                $cc.prop('checked', false);
                $tituloCopia.text("Con copia");
                $divOrigen.addClass("hide");
                switch (cod) {
                    case "CIR":
                        $divPara.html("");
                        $divCopia.removeClass("hide");
                        $divCc.addClass("hide");
                        $("#ulDisponibles li").removeClass("selected").appendTo($("#ulSeleccionados"));
                        $tituloCopia.text("Circular");
                        break;
                    case "OFI":
                        $divPara.html("");
                        $divCopia.addClass("hide");
                        $divCc.addClass("hide");
                        break;
                    case "DEX":
                        $divPara.html($selPara).prepend("Para: ");
                        $divCopia.addClass("hide");
                        $divCc.removeClass("hide");
                        $divOrigen.removeClass("hide");
                        break;
                    default :
                        $divPara.html($selPara).prepend("Para: ");
                        $divCopia.addClass("hide");
                        $divCc.removeClass("hide");
                }
                if (!cod) {
                    $divPara.html("");
                    $divCopia.addClass("hide");
                    $divCc.addClass("hide");
                }
            }

            function validarCheck() {
                var checked = $("#cc").is(":checked");
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

            $(function () {
                var $dir = $("#direccion");

                var $selPara = $("#para").clone(true);

                validarCheck();

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

                $("#cc").click(function () {
                    validarCheck();
                });

                $("#tipoDocumento").change(function () {
                    validarTipoDoc($selPara);
                }).change();

                $(".selectable li").click(function () {
                    $(this).toggleClass("selected");
                });
                $("#btnAddAll").click(function () {
                    $("#ulDisponibles li").removeClass("selected").appendTo($("#ulSeleccionados"));
                });
                $("#btnAddSelected").click(function () {
                    $("#ulDisponibles li.selected").removeClass("selected").appendTo($("#ulSeleccionados"));
                });
                $("#btnRemoveSelected").click(function () {
                    $("#ulSeleccionados li.selected").removeClass("selected").appendTo($("#ulDisponibles"));
                });
                $("#btnRemoveAll").click(function () {
                    $("#ulSeleccionados li").removeClass("selected").appendTo($("#ulDisponibles"));
                });

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
                    }
                });
            });
        </script>

    </body>
</html>