<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 07/03/14
  Time: 11:44 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Bandeja de Entrada Departamento</title>

        <style type="text/css">

        .etiqueta {
            float       : left;
            /*width: 100px;*/
            margin-left : 5px;
            /*margin-top: 5px;*/
        }

        /*.alert {*/
        /*padding : 0 !important;*/
        /*}*/

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
            font-size  : 13px !important;
        }

        .container-celdas {
            width      : 1070px;
            height     : 310px;
            float      : left;
            overflow   : auto;
            overflow-y : auto;
        }

        .tituloChevere {
            color       : #0088CC;
            border      : 0 solid red;
            white-space : nowrap;
            display     : block;
            /*width       : 98%;*/
            height      : 25px;
            font-family : 'open sans condensed';
            font-weight : bold;
            font-size   : 16px;
            line-height : 18px;
        }

        .table-hover tbody tr:hover td, .table-hover tbody tr:hover th {
            background-color : #FFBD4C;
        }

        tr.recibido, tr.recibido td {
            background-color : #D9EDF7;
        }

        tr.retrasado, tr.retrasado td {
            background-color : #F2DEDE;
        }

        tr.pendiente, tr.pendiente td {
            /*background-color : #FFFFCC;*/
        }
        </style>
    </head>

    <body>
        <div class="row" style="margin-top: 0px; margin-left: 1px">
            <span class="grupo">
                <label class="well well-sm"
                       style="text-align: center; float: left">Departamento: ${persona?.departamento?.descripcion}</label>
            </span>
        </div>

        <div class="btn-toolbar toolbar">
            <div class="btn-group">
                <a href="#" class="btn btn-primary btnBuscar"><i class="fa fa-book"></i> Buscar</a>
                <g:link action="archivados" class="btn btn-primary btnArchivados" controller="tramite">
                    <i class="fa fa-folder"></i> Archivados
                </g:link>
                <a href="#" class="btn btn-success btnActualizar">
                    <i class="fa fa-refresh"></i> Actualizar
                </a>
            </div>

            <div data-type="pendiente" class="alert alert-blanco alertas">
                <span id="spanPendientes" class="counter" data-class="pendiente">(0)</span> Doc. Pendientes
            </div>

            <div data-type="noRecibido" class="alert alert-otroRojo alertas">
                <span id="spanNoRecibidos" class="counter" data-class="noRecibido">(0)</span> Doc. No Recibidos
            </div>

            <div data-type="recibido" class="alert alert-info alertas">
                <span id="spanRecibidos" class="counter" data-class="recibido">(0)</span> Doc. Recibidos
            </div>

            <div data-type="retrasado" class="alert alert-danger alertas">
                <span id="spanRetrasados" class="counter" data-class="retrasado">(0)</span> Doc. Retrasados
            </div>

            <div data-type="jefe" class="alert alert-azul alertas">
                <span id="spanJefe" class="counter" data-class="jefe">(0)</span> Doc. env. jefe
            </div>
        </div>

        <div class="buscar" hidden="hidden" style="margin-bottom: 20px;">
            <fieldset>
                <legend>Búsqueda</legend>

                <div>
                    <div class="col-md-2">
                        <label># Memorando</label>
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

        <div id="bandeja">

        </div>

        <script type="text/javascript">

            var intervalBandeja;

            $(function () {
                //hace reload cada 5 min
                intervalBandeja = setInterval(function () {
                    cargarBandeja();
                }, 1000 * 60 * 5);
                var id;

                $(".alertas").click(function () {
                    if (!$(this).hasClass("trHighlight")) {
                        var clase = $(this).data("type");
                        $(".trHighlight").removeClass("trHighlight");
                        $("tr." + clase).addClass("trHighlight");
                        $(this).addClass("trHighlight");
                    } else {
                        $(".trHighlight").removeClass("trHighlight");
                    }
                });

                context.settings({
                    onShow : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        var $tr = $(e.target).parent();
                        $tr.addClass("trHighlight");
                        id = $tr.data("id");
                    }
                });

                var contestar = {
                    text   : 'Contestar trámite',
                    icon   : "<i class='fa fa-external-link'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();
                        var b = bootbox.dialog({
                            id      : "dlgContestar",
                            title   : "Contestar trámite",
                            message : "¿Está seguro de querer contestar este trámite?",
                            buttons : {
                                cancelar : {
                                    label     : '<i class="fa fa-times"></i> Cancelar',
                                    className : 'btn-danger',
                                    callback  : function () {
                                    }
                                },
                                recibir  : {
                                    id        : 'btnEnviar',
                                    label     : '<i class="fa fa-thumbs-o-up"></i> Contestar',
                                    className : 'btn-success',
                                    callback  : function () {
                                        openLoader();
                                        location.href = '${createLink(controller: 'tramite', action: 'crearTramite')}?padre=' + id;
                                    }
                                }
                            }
                        })
                    }
                };
                var archivar = {
                    text   : 'Archivar trámite',
                    icon   : "<i class='fa fa-folder-open-o'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();
                        var b = bootbox.dialog({
                            id      : "dlgArchivar",
                            title   : "Archivar trámite",
                            message : "¿Está seguro de querer archivar este trámite?<br/>" +
                                      "Una vez archivado no podrá utilizarlo de ninguna manera.",
                            buttons : {
                                cancelar : {
                                    label     : '<i class="fa fa-times"></i> Cancelar',
                                    className : 'btn-danger',
                                    callback  : function () {
                                    }
                                },
                                recibir  : {
                                    id        : 'btnEnviar',
                                    label     : '<i class="fa fa-thumbs-o-up"></i> Archivar',
                                    className : 'btn-success',
                                    callback  : function () {
                                        var obs = $("#txaObsJefe").val();
                                        openLoader();
                                        $.ajax({
                                            type    : 'POST',
                                            url     : '${createLink(controller: 'tramite', action: 'archivar')}',
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

                context.attach(".pendiente,.noRecibido", [
                    {
                        header : 'Acciones'
                    },
                    {
                        text   : 'Recibir Documento',
                        icon   : "<i class='fa fa-check-square-o'></i>",
                        action : function (e) {
                            $("tr.trHighlight").removeClass("trHighlight");
                            e.preventDefault();
                            var b = bootbox.dialog({
                                id      : "dlgRecibido",
                                title   : "Recibir trámite",
                                message : "¿Está seguro de querer recibir este trámite?",
                                buttons : {
                                    cancelar : {
                                        label     : '<i class="fa fa-times"></i> Cancelar',
                                        className : 'btn-danger',
                                        callback  : function () {
                                        }
                                    },
                                    recibir  : {
                                        id        : 'btnRecibir',
                                        label     : '<i class="fa fa-thumbs-o-up"></i> Recibir',
                                        className : 'btn-success',
                                        callback  : function () {
                                            openLoader();
                                            $.ajax({
                                                type    : 'POST',
                                                url     : '${createLink(action: 'recibirTramite')}',
                                                data    : {
                                                    id : id
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
                            });
                        }
                    }
                ]);

                context.attach(".recibido,.retrasado", [
                    {
                        header : 'Acciones'
                    },
                    {
                        text   : 'Enviar a jefe',
                        icon   : "<i class='fa fa-eye'></i>",
                        action : function (e) {
                            $("tr.trHighlight").removeClass("trHighlight");
                            e.preventDefault();
                            var b = bootbox.dialog({
                                id      : "dlgJefe",
                                title   : "Enviar trámite a jefe",
                                message : "¿Está seguro de querer enviar este trámite al jefe?</br><br/>" +
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
                                        label     : '<i class="fa fa-thumbs-o-up"></i> Enviar',
                                        className : 'btn-success',
                                        callback  : function () {
                                            var obs = $("#txaObsJefe").val();
                                            openLoader();
                                            $.ajax({
                                                type    : 'POST',
                                                url     : '${createLink(action: 'enviarTramiteJefe')}',
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
                    },
                    contestar,
                    archivar
                ]);

                context.attach(".jefe", [
                    contestar,
                    archivar
                ]);

                %{--context.attach('th', [--}%
                %{--{--}%
                %{--header : 'Acciones'--}%
                %{--},--}%
                %{--{--}%
                %{--text   : 'Recibir Documento',--}%
                %{--icon   : "<i class='fa fa-check-square-o'></i>",--}%
                %{--action : function (e) {--}%
                %{--$("tr.trHighlight").removeClass("trHighlight");--}%
                %{--e.preventDefault();--}%
                %{--}--}%
                %{--},--}%
                %{--{--}%
                %{--text   : 'Contestar Documento',--}%
                %{--icon   : "<i class='fa fa-external-link'></i>",--}%
                %{--action : function (e) {--}%
                %{--$("tr.trHighlight").removeClass("trHighlight");--}%
                %{--e.preventDefault();--}%

                %{--location.href = "${g.createLink(action: 'crearTramite')}/" + id;--}%
                %{--}--}%
                %{--},--}%
                %{--{--}%
                %{--text   : 'Archivar Documentos',--}%
                %{--icon   : "<i class='fa fa-folder-open-o'></i>",--}%
                %{--action : function (e) {--}%
                %{--$("tr.trHighlight").removeClass("trHighlight");--}%
                %{--e.preventDefault();--}%
                %{--//                    createEditRow(id);--}%
                %{--}--}%
                %{--},--}%
                %{--{--}%
                %{--text   : 'Distribuir a Jefes',--}%
                %{--icon   : "<i class='fa fa-eye'></i>",--}%
                %{--action : function (e) {--}%
                %{--$("tr.trHighlight").removeClass("trHighlight");--}%
                %{--e.preventDefault();--}%
                %{--$.ajax({--}%
                %{--type    : "POST",--}%
                %{--url     : "${createLink(action: 'observaciones')}/" + id,--}%
                %{--//                        data  : id,--}%
                %{--success : function (msg) {--}%
                %{--var b = bootbox.dialog({--}%
                %{--id      : "dlgObservaciones",--}%
                %{--title   : "Distribución al Jefe: Observaciones",--}%
                %{--message : msg,--}%
                %{--buttons : {--}%
                %{--cancelar : {--}%
                %{--label     : "Cancelar",--}%
                %{--className : 'btn-danger',--}%
                %{--callback  : function () {--}%
                %{--}--}%
                %{--},--}%
                %{--guardar  : {--}%
                %{--id        : 'btnSave',--}%
                %{--label     : '<i class="fa fa-save"></i> Guardar',--}%
                %{--className : "btn-success",--}%
                %{--callback  : function () {--}%
                %{--$.ajax({--}%
                %{--type    : 'POST',--}%
                %{--url     : '${createLink(action: 'guardarObservacion')}/' + id,--}%
                %{--data    : {--}%
                %{--texto : $("#observacion").val()--}%
                %{--},--}%
                %{--success : function (msg) {--}%
                %{--bootbox.alert(msg)--}%
                %{--}--}%
                %{--});--}%
                %{--}--}%
                %{--}--}%
                %{--}--}%
                %{--})--}%
                %{--}--}%
                %{--});--}%
                %{--}--}%
                %{--}--}%
                %{--]);--}%
            });

            $(".btnBuscar").click(function () {
                $(".buscar").attr("hidden", false)
            });

            $(".btnActualizar").click(function () {
                cargarBandeja();
                clearInterval(intervalBandeja);
                intervalBandeja = setInterval(function () {
                    cargarBandeja();
                }, 1000 * 60 * 5);
                log('Tabla de trámites y alertas actualizadas!', "success");
                return false;
            });

            function loading(div) {
                var y = 0;
                $("#" + div).html("<div class='tituloChevere' id='loading'>Cargando, Espere por favor</div>")
                var interval = setInterval(function () {
                    if (y == 30) {
                        $("#detalle").html("<div class='tituloChevere' id='loading'>Cargando, Espere por favor</div>")
                        y = 0
                    }
                    $("#loading").append(".");
                    y++
                }, 500);
                return interval
            }

            function cargarBandeja() {
                var interval = loading("bandeja");
                $.ajax({type : "POST", url : "${g.createLink(controller: 'tramite3',action:'tablaBandejaEntradaDpto')}",
                    success  : function (msg) {
                        resetTimer();
                        clearInterval(interval);
                        $("#bandeja").html(msg);
                        $(".counter").each(function () {
                            var clase = $(this).data("class");
                            var cant = $("tr." + clase).size();
                            $(this).text("(" + cant + ")");
                        });
                    }
                });
            }

            cargarBandeja();

            %{--function cargarAlertaRecibidos() {--}%
            %{--var interval = loading("alertaRecibido")--}%
            %{--var datos = ""--}%
            %{--$.ajax({type : "POST", url : "${g.createLink(controller: 'tramite',action:'alertRecibidos')}",--}%
            %{--data     : datos,--}%
            %{--success  : function (msg) {--}%
            %{--clearInterval(interval)--}%
            %{--$("#alertaRecibido").html(msg);--}%
            %{--}--}%
            %{--});--}%
            %{--}--}%

            //    cargarAlertaRecibidos();

            //    setInterval(function () {
            //
            //
            //        cargarAlertaRecibidos();
            //        cargarAlertaPendientes();
            //        cargarAlertaRetrasados();
            //
            //    },300000);

            %{--function cargarAlertaPendientes() {--}%
            %{--//                var interval = loading("alertaPendientes")--}%
            %{--var datos = ""--}%
            %{--$.ajax({type : "POST", url : "${g.createLink(controller: 'tramite',action:'alertaPendientes')}",--}%
            %{--data     : datos,--}%
            %{--success  : function (msg) {--}%
            %{--clearInterval(interval)--}%
            %{--$("#alertaPendientes").html(msg);--}%
            %{--}--}%
            %{--});--}%
            %{--}--}%

            //    cargarAlertaPendientes();

            %{--function cargarAlertaRetrasados() {--}%
            %{--var interval = loading("alertaRetrasados")--}%
            %{--var datos = ""--}%
            %{--$.ajax({type : "POST", url : "${g.createLink(controller: 'tramite',action:'alertaRetrasados')}",--}%
            %{--data     : datos,--}%
            %{--success  : function (msg) {--}%
            %{--clearInterval(interval)--}%
            %{--$("#alertaRetrasados").html(msg);--}%
            %{--}--}%
            %{--});--}%
            %{--}--}%

            //    cargarAlertaRetrasados();

            %{--function cargarRojoPendiente() {--}%
            %{--var interval = loading("alertaPendientes")--}%
            %{--var datos = ""--}%
            %{--$.ajax({--}%
            %{--type    : 'POST',--}%
            %{--url     : "${g.createLink(controller: 'tramite', action: 'rojoPendiente')}",--}%
            %{--datos   : datos,--}%
            %{--success : function (msg) {--}%
            %{--clearInterval(interval)--}%
            %{--$("#alertaPendientes").html(msg);--}%
            %{--}--}%
            %{--});--}%
            %{--}--}%

            %{--$(".btnBusqueda").click(function () {--}%
            %{--var interval = loading("bandeja")--}%
            %{--var memorando = $("#memorando").val();--}%
            %{--var asunto = $("#asunto").val();--}%
            %{--var fecha = $("#fechaBusqueda_input").val();--}%
            %{--var datos = "memorando=" + memorando + "&asunto=" + asunto + "&fecha=" + fecha--}%
            %{--$.ajax({ type : "POST", url : "${g.createLink(controller: 'tramite', action: 'busquedaBandeja')}",--}%
            %{--data      : datos,--}%
            %{--success   : function (msg) {--}%
            %{--clearInterval(interval)--}%
            %{--$("#bandeja").html(msg);--}%
            %{--}--}%
            %{--});--}%
            %{--});--}%
        </script>
    </body>
</html>