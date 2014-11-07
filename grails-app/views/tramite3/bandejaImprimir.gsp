<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 18/02/14
  Time: 12:52 PM
--%>


<%@ page import="org.apache.commons.lang.WordUtils; happy.tramites.EstadoTramite; happy.tramites.PermisoTramite" contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Bandeja de documentos por imprimir</title>

        <style type="text/css">

        body {
            background-color : #ebe0f5; /*#fef8e1 */;
        }

        .etiqueta {
            float       : left;
            /*width: 100px;*/
            margin-left : 5px;
            /*margin-top: 5px;*/
        }

        .alert {
            padding : 0;
        }

        .alert-blanco {
            color            : #666;
            background-color : #ffffff;
            border-color     : #d0d0d0;
        }

        /*.alertas {*/
        /*float       : left;*/
        /*width       : 100px;*/
        /*height      : 40px;*/
        /*margin-left : 20px;*/
        /*cursor      : pointer;*/
        /*}*/

        .cabecera {
            text-align : center;
            font-size  : 13px;
        }

        .container-celdas {
            width      : 1070px;
            height     : 310px;
            float      : left;
            overflow   : auto;
            overflow-y : auto;
        }

        .enviado {
            background-color : #e0e0e0;
            border           : 1px solid #a5a5a5;
        }

        .borrador {
            background-color : #FFFFCC;
            border           : 1px solid #eaeab7;
        }

        .table-hover tbody tr:hover td, .table-hover tbody tr:hover th {
            background-color : #FFBD4C;
        }

        tr.E002, tr.revisadoColor td {
            background-color : #DFF0D8 ! important;
        }

        tr.E001, tr.borrador td {
            background-color : #FFFFCC ! important;
        }

        tr.E003, tr.enviado td {
            background-color : #e0e0e0 ! important;
        }

        tr.alerta, tr.alerta td {
            background-color : #dbd0d5;
            font-weight      : bold;
        }

        .alertas {
            float       : left;
            /*width       : 100px;*/
            /*height      : 40px;*/
            margin-left : 20px;
            padding     : 10px;
            cursor      : pointer;
            /*margin-top: -5px;*/
        }

        .letra {

            /*font-family: "Arial Black", arial-black;*/
            /*background-color: #7eb75e;*/
            /*background-color:#faebc9;*/
            background-color : #c5a1ca;
        }

        #c99671
        </style>
        <link href="${resource(dir: 'css', file: 'custom/loader.css')}" rel="stylesheet">
    </head>

    <body>
        <div class="row" style="margin-top: 0px; margin-left: 1px">
            <span class="grupo">
                <label class="well well-sm letra" style="text-align: center">
                    BANDEJA DE TRÁMITES POR IMPRIMIR
                </label>
            </span>


            <span class="grupo">
                <label class="well well-sm" style="text-align: center">
                    Usuario:
                    ${persona?.nombre + " " + persona?.apellido + " - " + persona?.departamento?.descripcion}
                </label>
            </span>
        </div>

        <elm:flashMessage tipo="${flash.tipo}" clase="${flash.clase}">${flash.message}</elm:flashMessage>

        %{--Es editor: ${esEditor}--}%

        <div class="btn-toolbar toolbar" style="margin-top: 10px !important">
            <div class="btn-group">
                %{--<a href="#" class="btn btn-primary btnBuscar"><i class="fa fa-book"></i> Buscar</a>--}%

                <g:link action="" class="btn btn-success btnActualizar">
                    <i class="fa fa-refresh"></i> Actualizar
                </g:link>
                <g:if test="${!esEditor}">
                    <g:link action="" class="btn btn-info btnEnviar">
                        <i class="fa fa-pencil"></i> Enviar
                    </g:link>
                </g:if>
            </div>

            <div style="float: right">
                <div data-type="" class="alert borrador alertas" clase="E001">
                    (<span id="numBor"></span>)
                ${WordUtils.capitalizeFully(EstadoTramite.findByCodigo('E001').descripcion)}
                </div>

                %{--<div id="alertaEnviados">--}%
                %{--<div data-type="enviado" class="alert enviado alertas" clase="E003">--}%
                %{--(<span id="numEnv"></span>)--}%
                %{--${WordUtils.capitalizeFully(EstadoTramite.findByCodigo('E003').descripcion)}--}%
                %{--</div>--}%
                %{--</div>--}%

                %{--<div id="alertaNoRecibidos">--}%
                %{--<div data-type="noRecibido" class="alert alert-danger alertas" clase="alerta">--}%
                %{--(<span id="numNoRec"></span>)--}%
                %{--Sin Recepción--}%
                %{--</div>--}%
                %{--</div>--}%
            </div>
        </div>


        <div class="buscar" hidden="hidden" style="margin-bottom: 20px">

            <fieldset>
                <legend>Búsqueda</legend>

                <div>
                    <div class="col-md-2">
                        <label>Documento</label>
                        <g:textField name="memorando" value="" maxlength="15" class="form-control"/>
                    </div>

                    <div class="col-md-2">
                        <label>Asunto</label>
                        <g:textField name="asunto" value="" style="width: 300px" maxlength="30" class="form-control"/>
                    </div>

                    <div class="col-md-2" style="margin-left: 130px">
                        <label>Fecha Envío</label>
                        <elm:datepicker name="fechaBusqueda" class="datepicker form-control" value=""/>
                    </div>


                    <div style="padding-top: 25px">
                        <a href="#" name="busqueda" class="btn btn-success btnBusqueda"><i
                                class="fa fa-check-square-o"></i> Buscar</a>

                        <a href="#" name="salir" class="btn btn-danger btnSalir"><i class="fa fa-times"></i> Cerrar</a>
                    </div>

                </div>

            </fieldset>

        </div>


        %{--//bandeja--}%


        <g:select from="${personal}" name="selector" optionKey="id" class="form-control hide"
                  style="width: 300px; margin-left: 130px; margin-top: -30px"/>

        <div id="" style=";height: 600px;overflow: auto;position: relative">
            <div class="modalTabelGray" id="bloqueo-salida"></div>

            <div id="bandeja"></div>

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

            function cargarBandeja(band) {
                $(".qtip").hide();
                $("#bandeja").html("").append($("<div style='width:100%; text-align: center;'/>").append(spinnerSquare64));
                $.ajax({
                    type    : "POST",
                    url     : "${g.createLink(controller: 'tramite3',action:'tablaBandejaImprimir')}",
                    data    : "",
                    async   : false,
                    success : function (msg) {
                        $("#bandeja").html(msg).show("slide");
                        cargarAlertas();
                        if (band) {
//                    bootbox.alert("Datos actualizados")
                            log('Datos actualizados', 'success');
                        }
                    }
                });
            }

            function cargarAlertas() {
                cargarAlertaRevisados();
                cargarAlertaEnviados();
                cargarAlertaNoRecibidos();
                cargarBorrador();
            }

            function cargarAlertaRevisados() {
                $("#numRev").html($(".E002").size())
            }

            function cargarAlertaEnviados() {
                $("#numEnv").html($(".E003").size())
            }

            function cargarAlertaNoRecibidos() {
                $("#numNoRec").html($(".alerta").size())
            }
            function cargarBorrador() {
//        console.log($(".E001"),$(".E001").size())
                $("#numBor").html($(".E001").size())
            }

            var selPersonal = '<p id="seleccionar"> </p>';
            var $sel = $("#selector").clone();

            function createContextMenu(node) {
                var $tr = $(node);

                var items = {
                    header : {
                        label  : "Sin Acciones",
                        header : true
                    }
                };

                <g:if test="${!bloqueo}">
                var id = $tr.data("id");
                var codigo = $tr.attr("codigo");
                var estado = $tr.attr("estado");
                var padre = $tr.attr("padre");
                var de = $tr.attr("de");
                var archivo = $tr.attr("departamento") + "/" + $tr.attr("anio") + "/" + $tr.attr("codigo");

                var esSumilla = $tr.hasClass("sumilla");

                var ver = {
                    label  : "Ver - Imprimir",
                    icon   : "fa fa-search",
                    action : function () {
                        $.ajax({
                            type    : 'POST',
                            url     : '${createLink(controller: 'tramite3', action: 'verificarEstado')}',
                            data    : {
                                id : id
                            },
                            success : function (msg) {
                                if (msg == "ok")
                                    window.open("${resource(dir:'tramites')}/" + archivo + ".pdf");
                                else
                                    bootbox.alert("El documento esta anulado, por favor refresque su bandeja de salida.")
                            }
                        });

                    }
                }; //ver

                items.header.label = "Acciones";
                if (!esSumilla) {
                    items.ver = ver;
                }
                </g:if>
                return items;
            }

            $(function () {

                $("input").keyup(function (ev) {
                    if (ev.keyCode == 13) {
                        $("#bandeja").html("").append($("<div style='width:100%; text-align: center;'/>").append(spinnerSquare64));
                        var memorando = $("#memorando").val();
                        var asunto = $("#asunto").val();
                        var fecha = $("#fechaBusqueda_input").val();
                        var datos = "memorando=" + memorando + "&asunto=" + asunto + "&fecha=" + fecha;
                        $.ajax({
                            type    : "POST",
                            url     : "${g.createLink(controller: 'tramite2', action: 'busquedaBandejaSalida')}",
                            data    : datos,
                            success : function (msg) {
                                $("#bandeja").html(msg);
                            }
                        });
                    }
                });

                <g:if test="${bloqueo}">
                $("#bloqueo-salida").show();
                </g:if>

                $(".alertas").click(function () {

                    var clase = $(this).attr("clase");
                    $("tr").each(function () {
                        if ($(this).hasClass(clase)) {
                            if ($(this).hasClass("trHighlight"))
                                $(this).removeClass("trHighlight");
                            else
                                $(this).addClass("trHighlight")
                        } else {
                            $(this).removeClass("trHighlight")
                        }
                    });

                });

                $(".btnBuscar").click(function () {
                    $(".buscar").attr("hidden", false)
                });

                $(".btnSalir").click(function () {
                    $(".buscar").attr("hidden", true);
                    $("#memorando").val("");
                    $("#asunto").val("");
                    $("#fechaBusqueda_input").val("");
                    $("#fechaBusqueda_day").val("");
                    $("#fechaBusqueda_month").val("");
                    $("#fechaBusqueda_year").val("");
                    cargarBandeja();

                });
                $(".btnActualizar").click(function () {
                    openLoader();
                    cargarBandeja(true);
                    closeLoader();
                    return false;
                });

                $(".btnEnviar").click(function () {
                    var trId = [];
                    var strIds = "";
                    $(".combo").each(function () {
                        if ($(this).prop('checked') == false) {
                        } else {
                            trId.push($(this).attr('tramite'));
                            if (strIds != "") {
                                strIds += ",";
                            }
                            strIds += $(this).attr('tramite');
                        }
                    });
                    if (strIds == '') {
                        log("No se ha seleccionado ningun trámite", 'error');
                    } else {
                        var id;
                        var b = bootbox.dialog({
                            id      : "dlgGuia",
                            title   : 'Impresión de la guía de envío de trámites',
                            message : 'Desea imprimir la guía de envío para los trámites seleccionados?',
                            buttons : {
                                cancelar : {
                                    label : 'Cancelar'
                                },
                                no       : {
                                    label    : 'No Imprimir',
                                    callback : function () {
                                        doEnviar(false, strIds);
                                    }
                                },
                                si       : {
                                    label    : '<i class="fa fa-print"></i> Imprimir',
                                    callback : function () {
                                        doEnviar(true, strIds);
                                    }
                                }
                            }
                        });
                    }
                    return false;
                });

                function doEnviar(imprimir, strIds) {
                    $.ajax({
                        type    : "POST",
                        url     : "${g.createLink(controller: 'tramite2',action: 'enviarVarios')}",
                        data    : {
                            ids    : strIds,
                            enviar : '1',
                            type   : 'download'
                        },
                        success : function (msg) {
                            closeLoader();
//                                                console.log(msg);
                            var parts = msg.split("_");
                            if (parts[0] == 'ok') {
                                cargarBandeja(true);
                                log('Trámites Enviados'+parts[1], 'success');
                                if (imprimir && parts[1] != "") {
                                    openLoader();
                                    location.href = "${g.createLink(controller: 'tramiteExport' ,action: 'imprimirGuia')}?ids=" + strIds + "&departamento=" + '${persona?.departamento?.descripcion}';
                                    closeLoader();
                                }
                            } else {
                                cargarBandeja(true);
//                                log('Ocurrió un error al enviar los trámites seleccionados!', 'error');
                                %{--location.href = "${g.createLink(action: 'errores1')}";--}%

//                                closeLoader();
                            }
                        }
                    });
                }

                cargarBandeja(false);

//                setInterval(function () {
//                    openLoader();
//                    cargarBandeja(false);
//                    closeLoader();
//                    $(".qtip").hide();
//                }, 300000);

                $(".btnBusqueda").click(function () {
                    $("#bandeja").html("").append($("<div style='width:100%; text-align: center;'/>").append(spinnerSquare64));
                    var memorando = $("#memorando").val();
                    var asunto = $("#asunto").val();
                    var fecha = $("#fechaBusqueda_input").val();
                    var datos = "memorando=" + memorando + "&asunto=" + asunto + "&fecha=" + fecha;
                    $.ajax({
                        type    : "POST",
                        url     : "${g.createLink(controller: 'tramite2', action: 'busquedaBandejaSalida')}",
                        data    : datos,
                        success : function (msg) {
                            $("#bandeja").html(msg);
                        }
                    });
                });
            });
        </script>

    </body>
</html>