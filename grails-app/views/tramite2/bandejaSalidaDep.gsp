<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 18/02/14
  Time: 12:52 PM
--%>


<%@ page import="happy.tramites.EstadoTramite; org.apache.commons.lang.WordUtils" contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Bandeja de Salida Oficina</title>

        <style type="text/css">


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

        .alertas {
            float       : left;
            /*width       : 100px;*/
            /*height      : 40px;*/
            margin-left : 20px;
            padding     : 10px;
            cursor      : pointer;
            /*margin-top: -5px;*/
        }

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
            background-color : #f2c1b9;
            font-weight      : bold;
        }
        </style>
        <link href="${resource(dir: 'css', file: 'custom/loader.css')}" rel="stylesheet">
    </head>

    <body>

        <div class="row" style="margin-top: 0px; margin-left: 1px">
            <span class="grupo">
                <label class="well well-sm" style="text-align: center; float: left">
                    Departamento: ${persona?.departamento?.descripcion}
                </label>
            </span>
        </div>

        <div class="btn-toolbar toolbar" style="margin-top: 10px !important">
            <div class="btn-group">

                <a href="#" class="btn btn-primary btnBuscar"><i class="fa fa-book"></i> Buscar</a>

                <g:link action="" class="btn btn-success btnActualizar">
                    <i class="fa fa-refresh"></i> Actualizar
                </g:link>
                <g:link action="" class="btn btn-info btnEnviar">
                    <i class="fa fa-pencil"></i> Enviar
                </g:link>
            </div>

            <div data-type="" class="alert borrador alertas" clase="E001">
                (<span id="numBor"></span>)
            ${WordUtils.capitalizeFully(EstadoTramite.findByCodigo('E001').descripcion)}
            </div>

            <div data-type="enviado" class="alert enviado alertas" clase="E003">
                (<span id="numEnv"></span>)
            ${WordUtils.capitalizeFully(EstadoTramite.findByCodigo('E003').descripcion)}
            </div>

            <div data-type="noRecibido" class="alert alert-danger alertas" clase="alerta">
                (<span id="numNoRec"></span>)
            No recibidos
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
                        <label>Fecha</label>
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


            $("input").keyup(function (ev) {
                if (ev.keyCode == 13) {
                    var memorando = $("#memorando").val();
                    var asunto = $("#asunto").val();
                    var fecha = $("#fechaBusqueda").val();
                    var datos = "memorando=" + memorando + "&asunto=" + asunto + "&fecha=" + fecha;
                    $.ajax({
                        type    : "POST",
                        url     : "${g.createLink(controller: 'tramite2', action: 'busquedaBandejaSalidaDep')}",
                        data    : datos,
                        success : function (msg) {
                            $("#bandeja").html(msg);
                        }

                    });
                }
            });

            function cargarBandeja(band) {
                $("#bandeja").html("").append($("<div style='width:100%; text-align: center;'/>").append(spinnerSquare64));
                $.ajax({
                    type    : "POST",
                    url     : "${g.createLink(controller: 'tramite2',action:'tablaBandejaSalidaDep')}",
                    data    : "",
                    async   : false,
                    success : function (msg) {
                        $("#bandeja").html(msg).show("slide");
                        cargarAlertas();
                        if (band) {
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

            $(function () {

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

                var estado, de, id, codigo, archivo, padre;
                context.settings({
                    onShow : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        var $tr = $(e.target).parents("tr");
                        $tr.addClass("trHighlight");
                        id = $tr.data("id");
                        codigo = $tr.attr("codigo");
                        estado = $tr.attr("estado");
                        de = $tr.attr("de");
                        padre = $tr.attr("padre");
                        archivo = $tr.attr("departamento") + "/" + $tr.attr("anio") + "/" + $tr.attr("codigo")
                    }
                });
                <g:if test="${!bloqueo}">

                var ver = {
                    text   : 'Ver',
                    icon   : "<i class='fa fa-search'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        %{--location.href="${g.createLink(action: 'seguimientoTramite',controller: 'tramite3')}/"+id--}%
                        window.open("${resource(dir:'tramites')}/" + archivo + ".pdf");
                    }
                };

                var arbol = {
                    text   : 'Cadena del trámite',
                    icon   : "<i class='fa fa-sitemap'></i>",
                    action : function (e) {
                        location.href = '${createLink(controller: 'tramite3', action: 'arbolTramite')}/' + id + "?b=bsd"
                    }
                };

                var crearHermano = {
                    text   : "Agregar documento al trámite",
                    icon   : "<i class='fa fa-paste'></i>",
                    action : function () {
                        location.href = '${createLink(controller: "tramite2", action: "crearTramiteDep")}?padre=' + padre;
                    }
                };

                var detalles = {
                    text   : 'Detalles',
                    icon   : "<i class='fa fa-search'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();
                        $.ajax({
                            type    : 'POST',
                            url     : '${createLink(controller: 'tramite3', action: 'detalles')}',
                            data    : {
                                id : id
                            },
                            success : function (msg) {
                                $("#dialog-body").html(msg)
                            }
                        });
                        $("#dialog").modal("show")
                    }
                };
                var editar = {
                    text   : 'Editar',
                    icon   : "<i class='fa fa-pencil'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        location.href = "${g.createLink(action: 'redactar',controller: 'tramite')}/" + id
                    }
                };

                var anexos = {
                    text   : 'Anexos',
                    icon   : "<i class='fa fa-paperclip'></i>",
                    action : function (e) {
                        location.href = '${createLink(controller: 'documentoTramite', action: 'verAnexos')}/' + id
                    }
                };

                var desenviar = {
                    text   : 'Quitar el enviado',
                    icon   : "<i class='fa fa-magic text-danger'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        bootbox.dialog({
                            title   : "Alerta",
                            message : "<i class='fa fa-magic fa-3x pull-left text-danger text-shadow'></i><p>" +
                                      "¿Está seguro que desea quitar el enviado del trámite seleccionado?<br/>Esta acción no se puede deshacer.</p>",
                            buttons : {
                                cancelar  : {
                                    label     : "Cancelar",
                                    className : "btn-primary",
                                    callback  : function () {
                                    }
                                },
                                desenviar : {
                                    label     : "<i class='fa fa-magic'></i> Quitar enviado",
                                    className : "btn-danger",
                                    callback  : function () {
                                        openLoader("Quitando el enviado");
                                        $.ajax({
                                            type    : "POST",
                                            url     : '${createLink(action:'desenviar_ajax')}',
                                            data    : {
                                                id : id
                                            },
                                            success : function (msg) {
                                                var parts = msg.split("_");
                                                log(parts[1], parts[0] == "OK" ? "success" : "error"); // log(msg, type, title, hide)
                                                if (parts[0] == "OK") {
                                                    location.reload(true);
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                };

                context.attach(".E001", [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    arbol,
                    editar,

                    %{--<g:if test="${revisar}">,--}%
                    %{--{--}%
                    %{--text   : 'Revisar',--}%
                    %{--icon   : "<i class='fa fa-check'></i>",--}%
                    %{--action : function (e) {--}%
                    %{--$("tr.trHighlight").removeClass("trHighlight");--}%
                    %{--location.href = "${g.createLink(action: 'revision',controller: 'tramite2')}/" + id--}%
                    %{--}--}%

                    %{--}--}%
                    %{--</g:if>--}%
                ]);

                context.attach(".E001.conPadre", [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    arbol,
                    editar,
                    crearHermano
                    %{--<g:if test="${revisar}">,--}%
                    %{--{--}%
                    %{--text   : 'Revisar',--}%
                    %{--icon   : "<i class='fa fa-check'></i>",--}%
                    %{--action : function (e) {--}%
                    %{--$("tr.trHighlight").removeClass("trHighlight");--}%
                    %{--location.href = "${g.createLink(action: 'revision',controller: 'tramite2')}/" + id--}%
                    %{--}--}%

                    %{--}--}%
                    %{--</g:if>--}%
                ]);
                context.attach(".E003", [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    detalles,
                    arbol
                ]);
                context.attach(".E003.conPadre", [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    detalles,
                    arbol,
                    crearHermano
                ]);
                context.attach(".E004", [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    detalles,
                    arbol,
                ]);
                context.attach(".E004.conPadre", [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    detalles,
                    arbol,
                    crearHermano
                ]);
                context.attach(".alerta", [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    detalles,
                    arbol,
                ]);
                context.attach(".alerta.conPadre", [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    detalles,
                    arbol,
                    crearHermano
                ]);
                context.attach(".desenviar", [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    detalles,
                    arbol,
//                    editar,
                    desenviar
                ]);
                context.attach(".desenviar.conPadre", [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    detalles,
                    arbol,
                    crearHermano,
//                    editar,
                    desenviar
                ]);

                context.attach(".E001.sinSumilla", [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    arbol,
                    editar
                ]);

                context.attach(".E001.sinSumilla.conPadre", [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    arbol,
                    editar,
                    crearHermano
                ]);

                context.attach(".E001.sumilla", [
                    {
                        header : 'Sin Acciones'
                    }
                ]);

                context.attach(".E001.sumilla.conPadre", [
                    {
                        header : 'Acciones'
                    },
                    crearHermano
                ]);
                context.attach(".E003.desenviar.sumilla", [
                    {
                        header : 'Acciones'
                    },
                    detalles,
                    arbol,
                    desenviar
                ]);
                context.attach(".alerta.desenviar.sumilla", [
                    {
                        header : 'Acciones'
                    },
                    detalles,
                    arbol,
                    desenviar
                ]);
                context.attach(".alerta.desenviar.sumilla.conPadre", [
                    {
                        header : 'Acciones'
                    },
                    detalles,
                    arbol,
                    crearHermano,
                    desenviar
                ]);

                context.attach(".alerta.desenviar.sumilla.sinAnexo", [
                    {
                        header : 'Acciones'
                    },
                    detalles,
                    arbol,
                    desenviar
                ]);

                context.attach(".alerta.desenviar.sumilla.sinAnexo.conPadre", [
                    {
                        header : 'Acciones'
                    },
                    detalles,
                    arbol,
                    crearHermano,
                    desenviar
                ]);

                context.attach(".E001.sinSumilla.conAnexo", [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    detalles,
                    arbol,
                    editar,
                    anexos
                ]);

                context.attach(".E001.sinSumilla.conAnexo.conPadre", [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    detalles,
                    arbol,
                    editar,
                    anexos,
                    crearHermano
                ]);

                context.attach(".alerta.sinSumilla.conAnexo", [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    detalles,
                    arbol,
                    anexos
                ]);

                context.attach(".alerta.sinSumilla.conAnexo.conPadre", [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    detalles,
                    arbol,
                    anexos,
                    crearHermano
                ]);

                </g:if>
                $(".btnBuscar").click(function () {
                    $(".buscar").attr("hidden", false)
                });

                $(".btnSalir").click(function () {
                    $(".buscar").attr("hidden", true)
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
                            title   : 'Impresión de la guía de envio de trámites',
                            message : 'Desea imprimir la guía de envio para los trámites seleccionados?',
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
                            if (msg == 'ok') {
                                cargarBandeja(true);
                                log('Trámites Enviados', 'success');
                                if (imprimir) {
                                    openLoader();
                                    location.href = "${g.createLink(controller: 'tramiteExport' ,action: 'imprimirGuia')}?ids=" + strIds + "&departamento=" + '${persona?.departamento?.descripcion}';
                                    closeLoader();
                                }
                            } else {
                                log('Ocurrió un error al enviar los trámites seleccionados!', 'error');
                            }
                        }
                    });
                }

                cargarBandeja(false);

                setInterval(function () {
                    openLoader();
                    cargarBandeja(false);
                    closeLoader();
                }, 300000);

                $(".btnBusqueda").click(function () {
                    var memorando = $("#memorando").val();
                    var asunto = $("#asunto").val();
                    var fecha = $("#fechaBusqueda").val();
                    var datos = "memorando=" + memorando + "&asunto=" + asunto + "&fecha=" + fecha;
                    $.ajax({
                        type    : "POST",
                        url     : "${g.createLink(controller: 'tramite2', action: 'busquedaBandejaSalidaDep')}",
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