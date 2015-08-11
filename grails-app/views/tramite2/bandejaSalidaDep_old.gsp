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
        body {
            background-color : #D1DDE8;
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

        .letra {

            /*font-family: "Arial Black", arial-black;*/
            /*background-color : #eacb89;*/
            background-color : #8fc6f3;
        }

        .ancho1 {
            width : 360px;
        }

        </style>
        <link href="${resource(dir: 'css', file: 'custom/loader.css')}" rel="stylesheet">
    </head>

    <body>

        <div class="row" style="margin-top: 0px; margin-left: 1px">

            <span class="grupo">
                <label class="well well-sm letra" style="text-align: center">
                    BANDEJA DE SALIDA DEPARTAMENTO
                </label>
            </span>

            <span class="grupo">
                <label class="well well-sm" style="text-align: center">
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

            <div style="float: right">
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
        </div>

        <div class="buscar" hidden="hidden" style="margin-bottom: 20px">
            <fieldset>
                <legend>Búsqueda</legend>

                <div>
                    <div class="col-md-2">
                        <label>Documento</label>
                        <g:textField name="memorando" value="" maxlength="15" class="form-control allCaps"/>
                    </div>

                    <div class="col-md-2">
                        <label>Asunto</label>
                        <g:textField name="asunto" value="" style="width: 300px" maxlength="30" class="form-control"/>
                    </div>

                    <div class="col-md-2" style="margin-left: 130px">
                        <label>Fecha envío</label>
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

            <div id="bandeja">
                <script type="text/javascript" src="${resource(dir: 'js/plugins/lzm.context/js', file: 'lzm.context-0.5.js')}"></script>
                <link href="${resource(dir: 'js/plugins/lzm.context/css', file: 'lzm.context-0.5.css')}" rel="stylesheet">

                <table class="table table-bordered  table-condensed table-hover">
                    <thead>
                        <tr>
                            <th class="cabecera">Documento</th>
                            <th>De</th>
                            <th class="cabecera">Fec. Creación</th>
                            <th class="cabecera">Para</th>
                            <th class="cabecera ancho1">Destinatario</th>
                            <th class="cabecera">Prioridad</th>
                            <th class="cabecera">Fecha Envio</th>
                            <th class="cabecera">F. Límite Recepción</th>
                            <th class="cabecera">Estado</th>
                            <th class="cabecera">Enviar</th>
                        </tr>
                    </thead>
                    <tbody id="tablaTramites">
                    </tbody>
                </table>

            </div>
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

            var actual = 0;
            var max = 20;
            var lastSize = 0;
            var nowSize = 0;
            var times = 0;
            var check = false;
            var salto = 40
            var breakingPoint = false
            var cargando = false
            var externalSource = false
            function resetValues() {
//        console.log("reset values",cargando)
                if (cargando) {
                    breakingPoint = true;
                } else {
                    lastSize = 0
                    nowSize = 0
                    times = 0
                    max = 20
                    salto = 40
                    actual = 0
                    check = false
                    breakingPoint = false
                    $(".trTramite").remove()
                    cargarBandeja(true)
                }

            }

            %{--function cargarBandeja(band){--}%
                %{--$.ajax({--}%
                    %{--type: "POST",--}%
                    %{--url: "${g.createLink(controller: 'tramite2',action:'tablaBandejaSalidaDep')}",--}%
                    %{--data: '',--}%
                    %{--success: function (msg) {--}%

                        %{--$("#tablaTramites").append(msg);--}%

                        %{--cargarAlertas();--}%
                    %{--}--}%
                %{--})--}%
            %{--};--}%

            function cargarBandeja(band) {
                $(".qtip").hide();
//        $("#bandeja").html("").append($("<div style='width:100%; text-align: center;'/>").append(spinnerSquare64));
//        console.log("cargar bandeja ",band,breakingPoint)
                cargando = true

//        console.log("pass "+pass)
                if (!breakingPoint) {
//            console.log("paso")
                    $.ajax({
                        type    : "POST",
                        url     : "${g.createLink(controller: 'tramite2',action:'tablaBandejaSalidaDep_old')}",
                        data    : {
                            actual : actual,
                            max    : max
                        },
                        async   : true,
                        success : function (msg) {
                            times++
                            if (!breakingPoint)
                                $("#tablaTramites").append(msg);

                            cargarAlertas();
                            nowSize = $(".trTramite").size()

                            actual += max
//                console.log("cargar bandeja ",nowSize,actual)

                            if (lastSize != 0) {
                                if (nowSize > lastSize) {
                                    if (max > salto) {
                                        max = max - salto
                                        check = false
                                    }
                                    lastSize = nowSize
//                                    console.log("last " + lastSize)
                                    cargarBandeja(false)
                                } else {
//                        console.log("tiempo del check "+check)
                                    if (!check) {
                                        check = true
                                        max = max + salto
                                        cargarBandeja(false)
//                            actual+=salto
//                            console.log("max "+max)
                                    } else {

                                        cargando = false
                                        if (breakingPoint) {
                                            resetValues()
                                        }

                                    }

                                }
                            } else {
                                lastSize = nowSize
//                    actual=nowSize
                                cargarBandeja(false)
                            }

                        }
                    });
                } else {
                    lastSize = 0
                    nowSize = 0
                    times = 0
                    max = 15
                    salto = 40
                    actual = 0
                    check = false
                    cargando = false
                    breakingPoint = false
                    if (!externalSource)
                        cargarBandeja(true)
                    else
                        externalSource = false
                }
            }



            $("input").keyup(function (ev) {
                if (ev.keyCode == 13) {
                    if (cargando) {
                        breakingPoint = true
                        externalSource = true
                    }
                    $(".trTramite").remove()
                    $(".trTramite").remove()
                    var memorando = $("#memorando").val();
                    var asunto = $("#asunto").val();
                    var fecha = $("#fechaBusqueda").val();
                    var datos = "memorando=" + memorando + "&asunto=" + asunto + "&fecha=" + fecha;
                    $.ajax({
                        type    : "POST",
                        url     : "${g.createLink(controller: 'tramite2', action: 'busquedaBandejaSalidaDep')}",
                        data    : datos,
                        success : function (msg) {
//                    $("#bandeja").html(msg);
                            $("#tablaTramites").append(msg);
                        }

                    });
                }
            });

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

                var porEnviar = $tr.hasClass("E001"); //por enviar
                var revisado = $tr.hasClass("E002"); //revisado
                var enviado = $tr.hasClass("E003"); //enviado
                var recibido = $tr.hasClass("E004"); //recibido

                var esSumilla = $tr.hasClass("sumilla");
                var esExterno = $tr.hasClass("externo");
                var esExternoCC = $tr.hasClass("externoCC");
                var esOficio = $tr.hasClass("OFI");
                var tieneEstado = $tr.hasClass("estado");
                var esDex = $tr.hasClass("DEX");
                var tienePadre = $tr.hasClass("conPadre");
                var tieneAlerta = $tr.hasClass("alerta");
                var tieneAnexo = $tr.hasClass("conAnexo");

                var tienePrincipal = $tr.attr("principal").toString() != '0' && $tr.attr("principal").toString() != $tr.attr("id");

                var puedeImprimir = $tr.hasClass("imprimir");
                var puedeDesenviar = $tr.hasClass("desenviar");

                var esRespuestaNueva = $tr.attr("ern");

                var copia = {
                    separator_before : true,
                    label            : "Crear Copia",
                    icon             : "fa fa-files-o",
                    action           : function () {
                        $.ajax({
                            type    : 'POST',
                            url     : '${createLink(controller: 'tramite3', action: 'verificarEstado')}',
                            data    : {
                                id : id
                            },
                            success : function (msg) {
                                if (msg == "ok") {
                                    $.ajax({
                                        type    : "POST",
                                        url     : "${createLink(controller: 'tramiteAdmin', action:'copiaParaLista_ajax')}",
                                        data    : {
                                            tramite : id
                                        },
                                        success : function (msg) {
                                            bootbox.dialog({
                                                id      : "dlgCopiaPara",
                                                title   : '<i class="fa fa-files-o"></i> Copia para',
                                                class   : "long",
                                                message : msg,
                                                buttons : {
                                                    cancelar : {
                                                        label     : '<i class="fa fa-times"></i> Cancelar',
                                                        className : 'btn-danger',
                                                        callback  : function () {
                                                        }
                                                    },
                                                    enviar   : {
                                                        id        : 'btnEnviarCopia',
                                                        label     : '<i class="fa fa-check"></i> Enviar copias',
                                                        className : "btn-success",
                                                        callback  : function () {
                                                            var cc = "";
                                                            $("#ulSeleccionados li").not(".disabled").each(function () {
                                                                cc += $(this).data("id") + "_";
                                                            });
                                                            openLoader("Enviando copias");
                                                            $.ajax({
                                                                type    : "POST",
                                                                url     : "${createLink(controller: 'tramiteAdmin', action:'enviarCopias_ajax')}",
                                                                data    : {
                                                                    tramite : id,
                                                                    copias  : cc
                                                                },
                                                                success : function (msg) {
                                                                    var parts = msg.split("*");
                                                                    if (parts[0] == 'OK') {
                                                                        log("Copias enviadas exitosamente", 'success');
                                                                        setTimeout(function () {
                                                                            location.reload(true);
                                                                        }, 500);
                                                                    } else if (parts[0] == 'NO') {
                                                                        closeLoader();
                                                                        log(parts[1], 'error');
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    });

                                } else
                                    bootbox.alert("El documento esta anulado, por favor refresque su bandeja de salida.")
                            }
                        });
                    }
                };

                var recibirExterno_old = {
                    label  : 'Confirmar recepción',
                    icon   : "fa fa-check-square-o",
                    action : function (e) {
                        $.ajax({
                            type    : 'POST',
                            %{--url     : '${createLink(action: 'guardarRecibir')}/' + id,--}%
                            url     : '${createLink(controller: 'externos', action: 'recibirTramiteExterno')}/' + id,
                            success : function (msg) {
                                var parts = msg.split('_');
                                openLoader();
                                resetValues();
//                        cargarBandeja(true);
                                closeLoader();
                                if (parts[0] == 'NO') {
                                    log(parts[1], "error");
                                } else if (parts[0] == "OK") {
                                    log(parts[1], "success")
                                } else if (parts[0] == "ERROR") {
                                    bootbox.alert(parts[1]);
                                }
                            }
                        }); //ajax

                    } //action
                };

                var recibirExterno = {
                    label  : 'Confirmar recepción destinatarios externos',
                    icon   : "fa fa-check-square-o",
                    action : function (e) {
                        $.ajax({
                            type    : "POST",
                            url     : '${createLink(action:'recibirExternoLista_ajax')}',
                            data    : {
                                id : id
                            },
                            success : function (msg) {
                                //s.indexOf("oo") > -1
                                var buttons = {};
                                if (msg.indexOf("No puede") > -1) {
                                    buttons.aceptar = {
                                        label     : "Aceptar",
                                        className : "btn-primary",
                                        callback  : function () {
                                            openLoader();
                                            location.reload(true);
                                        }
                                    }
                                } else {
                                    buttons.cancelar = {
                                        label     : "Cancelar",
                                        className : "btn-primary",
                                        callback  : function () {
                                        }
                                    };
                                    buttons.desenviar = {
                                        label     : "<i class='fa fa-check-square-o'></i> Confirmar recepción",
                                        className : "btn-default",
                                        callback  : function () {
                                            var ids = "";
                                            $(".chkOne").each(function () {
                                                if ($(this).hasClass("fa-check-square")) {
                                                    if (ids != "") {
                                                        ids += "_"
                                                    }
                                                    ids += $(this).attr("id");
                                                }
                                            });
                                            if (ids) {
                                                openLoader("");
                                                $.ajax({
                                                    type    : "POST",
                                                    url     : '${createLink(controller: 'externos', action:'recibirTramitesExternos_ajax')}',
                                                    data    : {
                                                        id  : id,
                                                        ids : ids
                                                    },
                                                    success : function (msg) {
                                                        var parts = msg.split("_");
                                                        log(parts[1], parts[0] == "OK" ? "success" : "error"); // log(msg, type, title, hide)
                                                        if (parts[0] == "OK") {
                                                            setTimeout(function () {
                                                                $("#bloqueo-warning").hide();
                                                                location.href = "${createLink(controller: "tramite2", action: "bandejaSalidaDep")}";
                                                            }, 1000);
                                                            cargarBandeja();
                                                        } else {
                                                            resetValues();
                                                            closeLoader();
                                                        }
                                                    }
                                                });
                                            } else {
                                                log('No seleccionó ninguna persona', 'error');
                                            }
                                        }
                                    };
                                }

                                bootbox.dialog({
                                    title   : "Alerta",
                                    message : msg,
                                    buttons : buttons
                                });
                            }
                        });
                    } //action
                };

                var enviarDex = {
                    label  : 'Enviar y recibir',
                    icon   : "fa fa-check-square-o",
                    action : function (e) {
                        $.ajax({
                            type    : 'POST',
                            %{--url     : '${createLink(action: 'guardarRecibir')}/' + id,--}%
                            url     : '${createLink(controller: 'tramite', action: 'saveDEX')}/' + id,
                            success : function (msg) {
                                var parts = msg.split('_');
                                openLoader();
                                resetValues()
//                        cargarBandeja(true);
                                closeLoader();
                                if (parts[0] == 'NO') {
                                    log(parts[1], "error");
                                } else if (parts[0] == "OK") {
                                    log(parts[1], "success")
                                } else if (parts[0] == "ERROR") {
                                    bootbox.alert(parts[1]);
                                }
                            }
                        }); //ajax

                    } //action
                };

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

                var detalles = {
                    label  : "Detalles",
                    icon   : "fa fa-search",
                    action : function () {
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
                        $("#dialog").modal("show");
                    }
                }; //detalles

                var arbol = {
                    label : "Cadena del trámite",
                    icon  : "fa fa-sitemap",
                    url   : '${createLink(controller: 'tramite3', action: 'arbolTramite')}/' + id + "?b=bsp"
                }; //arbol

                %{--var crearHermano = {--}%
                %{--label : "Agregar documento al trámite",--}%
                %{--icon  : "fa fa-paste",--}%
                %{--url   : '${createLink(controller: "tramite2", action: "crearTramiteDep")}?padre=' + padre + '&hermano=' + id + "&buscar=1&esRespuestaNueva=N"--}%
                %{--}; //crear hermano--}%

                var crearHermano = {
                    label  : "Agregar documento al trámite",
                    icon   : "fa fa-paste",
                    action : function () {
                        $.ajax({
                            type    : 'POST',
                            url     : '${createLink(controller: 'buscarTramite', action: 'verificarAgregarDoc')}',
                            data    : {
                                id : id
                            },
                            success : function (msg) {
                                if (msg == "OK") {
                                    <g:if test="${session.usuario.esTriangulo}">
                                    location.href = '${createLink(controller: "tramite2", action: "crearTramiteDep")}?padre=' + padre + "&hermano=" + id + "&buscar=1&esRespuestaNueva=N";
                                    </g:if>
                                    <g:else>
                                    location.href = '${createLink(controller: "tramite", action: "crearTramite")}?padre=' + padre + "&hermano=" + id + "&buscar=1&esRespuestaNueva=N";
                                    </g:else>
                                } else {
                                    bootbox.alert("No puede agregar documentos a este trámite");
                                }
                            }
                        });
                    }
                };

                var crearHijo = {
                    label  : "Agregar documento al trámite",
                    icon   : "fa fa-paste",
                    action : function () {
                        $.ajax({
                            type    : 'POST',
                            url     : '${createLink(controller: 'buscarTramite', action: 'verificarAgregarDoc')}',
                            data    : {
                                id : id
                            },
                            success : function (msg) {
                                if (msg == "OK") {
                                    <g:if test="${session.usuario.esTriangulo}">
                                    location.href = '${createLink(controller: "tramite2", action: "crearTramiteDep")}?hermano=' + id + "&buscar=1&esRespuestaNueva=N";
                                    </g:if>
                                    <g:else>
                                    location.href = '${createLink(controller: "tramite", action: "crearTramite")}?hermano=' + id + "&buscar=1&esRespuestaNueva=N";
                                    </g:else>
                                } else {
                                    bootbox.alert("No puede agregar documentos a este trámite");
                                }
                            }
                        });
                    }
                };

                var editar = {
                    label : "Editar",
                    icon  : "fa fa-pencil",
                    url   : "${g.createLink(action: 'redactar',controller: 'tramite')}/" + id
                }; //editar

                var editarSumilla = {
                    label : "Editar",
                    icon  : "fa fa-pencil",
                    url   : "${g.createLink(controller: 'tramite2', action: 'crearTramiteDep')}/" + id + "?esRespuestaNueva=" + esRespuestaNueva
                }; //editar sumilla

                var anexos = {
                    label : "Anexos",
                    icon  : "fa fa-paperclip",
                    url   : '${createLink(controller: 'documentoTramite', action: 'verAnexos')}/' + id
                }; //anexos

                var desenviar = {
                    label  : "Quitar el enviado",
                    icon   : "fa fa-magic text-danger",
                    action : function () {
                        $.ajax({
                            type    : "POST",
                            url     : '${createLink(action:'desenviarLista_ajax')}',
                            data    : {
                                id : id
                            },
                            success : function (msg) {
                                //s.indexOf("oo") > -1
                                var buttons = {};
                                if (msg.indexOf("No puede quitar el enviado") > -1) {
                                    buttons.aceptar = {
                                        label     : "Aceptar",
                                        className : "btn-primary",
                                        callback  : function () {
                                            openLoader();
                                            location.reload(true);
                                        }
                                    }
                                } else {
                                    buttons.cancelar = {
                                        label     : "Cancelar",
                                        className : "btn-primary",
                                        callback  : function () {
                                        }
                                    };
                                    buttons.desenviar = {
                                        label     : "<i class='fa fa-magic'></i> Quitar enviado",
                                        className : "btn-danger",
                                        callback  : function () {
                                            var ids = "";
                                            $(".chkOne").each(function () {
                                                if ($(this).hasClass("fa-check-square")) {
                                                    if (ids != "") {
                                                        ids += "_"
                                                    }
                                                    ids += $(this).attr("id");
                                                }
                                            });
                                            if (ids) {
                                                openLoader("Quitando enviado");
                                                $.ajax({
                                                    type    : "POST",
                                                    url     : '${createLink(action:'desenviar_ajax')}',
                                                    data    : {
                                                        id  : id,
                                                        ids : ids
                                                    },
                                                    success : function (msg) {
                                                        var parts = msg.split("_");
                                                        log(parts[1], parts[0] == "OK" ? "success" : "error"); // log(msg, type, title, hide)
                                                        if (parts[0] == "OK") {
                                                            setTimeout(function () {
//                                                                location.reload(true);
                                                                $("#bloqueo-warning").hide();
                                                                location.href = "${createLink(controller: "tramite2", action: "bandejaSalidaDep")}";
                                                            }, 1000);
                                                            cargarBandeja();
                                                        } else {
                                                            resetValues()
//                                                        cargarBandeja(true)
                                                            log("Envío del trámite cancelado", 'error')
                                                            closeLoader();
                                                        }
                                                    }
                                                });
                                            } else {
                                                log('No seleccionó ninguna persona', 'error')
//
                                            }
                                        }
                                    };
                                }

                                bootbox.dialog({
                                    title   : "Alerta",
                                    message : msg,
                                    buttons : buttons
                                });
                            }
                        });
                    }
                };

                var observaciones = {
                    label  : 'Añadir observaciones al trámite',
                    icon   : "fa fa-eye",
                    action : function (e) {

                        var b = bootbox.dialog({
                            id      : "dlgJefe",
                            title   : "Añadir observaciones al trámite",
                            message : "¿Está seguro de querer añadir observaciones al trámite <b>" + codigo + "</b>?</br><br/>" +
                                      "Escriba las observaciones: " +
                                      "<textarea id='txaObsJefe' style='height: 130px;' class='form-control'></textarea>",
                            buttons : {
                                cancelar : {
                                    label     : '<i class="fa fa-times"></i> Cancelar',
                                    className : 'btn-danger',
                                    callback  : function () {
                                    }
                                },
                                recibir  : {
                                    id        : 'btnEnviar',
                                    label     : '<i class="fa fa-thumbs-o-up"></i> Guardar',
                                    className : 'btn-success',
                                    callback  : function () {
                                        var obs = $("#txaObsJefe").val();
                                        openLoader();
                                        $.ajax({
                                            type    : 'POST',
                                            url     : '${createLink(controller: 'tramite3', action: 'enviarTramiteJefe')}',
                                            data    : {
                                                id  : id,
                                                obs : obs
                                            },
                                            success : function (msg) {
                                                var parts = msg.split("_");
                                                cargarBandeja();
                                                closeLoader();
                                                log(parts[1], parts[0] == "NO" ? "error" : "success");
                                            }
                                        });
                                    }
                                }
                            }
                        })
                    }
                };

                //                if (!revisado) {
                items.header.label = "Acciones";
                if (!esSumilla) {
                    items.ver = ver;
                }
                <g:if test="${session.usuario.getPuedeVer()}">
                items.detalles = detalles;
                items.arbol = arbol;
                </g:if>
                if (porEnviar) {
                    if (esSumilla || esDex) {
                        items.editar = editarSumilla;
                    } else {
                        items.editar = editar;
                    }
                }
                if (tienePadre) {
                    items.hermano = crearHermano;
                } else {
                    items.hijo = crearHijo;
                }
                if (tieneAnexo) {
                    items.anexos = anexos;
                }
                if (puedeDesenviar) {
                    items.desenviar = desenviar;
                }
                //                }
                if (esDex && porEnviar) {
                    items.enviarDex = enviarDex
                }
                if ((esExterno && (enviado || tieneAlerta)) || esExternoCC) {
                    items.recibirExterno = recibirExterno
                }
                if (enviado || recibido || tieneAlerta) {
                    <g:if test="${session.usuario.getPuedeCopiar()}">
                    items.copia = copia;
                    </g:if>
                }

                if (esOficio) {
                    delete items.copia;
                }

                items.observaciones = observaciones;
                </g:if>
                return items;
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
                    resetValues()
//            cargarBandeja(true);
                });

                $(".btnActualizar").click(function () {
                    if (!cargando) {
                        openLoader();
                        resetValues()
//                cargarBandeja(true);
                        closeLoader();
                        return false;
                    } else {
                        log('La bandeja se esta cargando, no se puede actualizar en este momento', 'info');
                        return false
                    }

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
//            console.log("enviar????")
                    openLoader("Enviando trámites");
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
//                            console.log(msg);
                            var parts = msg.split('_');
                            if (parts[0] == 'ok') {
                                if (!imprimir)
                                    resetValues();
//                        cargarBandeja(true);
                                log('Trámites Enviados', 'success');
                                if (imprimir) {
                                    openLoader();
                                    location.href = "${g.createLink(controller: 'tramiteExport' ,action: 'imprimirGuia')}?ids=" + strIds + "&departamento=" + '${persona?.departamento?.descripcion}';
                                    closeLoader();
//                                    console.log("llego")
                                    resetValues()
                                }
                            } else {
                                log('Ocurrió un error al enviar los trámites seleccionados!', 'error');
                                resetValues()
//                        cargarBandeja(true);
                                %{--location.href = "${g.createLink(action: 'errores1')}";--}%
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
                    openLoader();
//            console.log("buscar")
                    if (cargando) {
                        breakingPoint = true
                        externalSource = true
                    }
                    $(".trTramite").remove()

                    var memorando = $("#memorando").val();
                    var asunto = $("#asunto").val();
                    var fecha = $("#fechaBusqueda_input").val();
                    var datos = "memorando=" + memorando + "&asunto=" + asunto + "&fecha=" + fecha;
                    $.ajax({
                        type    : "POST",
                        url     : "${g.createLink(controller: 'tramite2', action: 'busquedaBandejaSalidaDep')}",
                        data    : datos,
                        success : function (msg) {
//                    console.log(msg)
//                    $("#bandeja").html(msg);
                            $("#tablaTramites").append(msg);
                            closeLoader()
                        }

                    });
                });
            });
        </script>

    </body>
</html>