<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 1/16/14
  Time: 11:31 AM
--%>

<%@ page import="happy.seguridad.Persona" contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Bandeja de tramites externos</title>

        <style type="text/css">
        .etiqueta {
            float       : left;
            /*width: 100px;*/
            margin-left : 5px;
            /*margin-top: 5px;*/

        }

        /*.alert {*/
        /*padding: 0;*/
        /*!important;*/
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

        .cabecera.sortable {
            cursor : pointer;
        }

        .container-celdas {
            width      : 1070px;
            height     : 310px;
            float      : left;
            overflow   : auto;
            overflow-y : auto;
        }

        .table-hover tbody tr:hover td, .table-hover tbody tr:hover th {
            background-color : #FFBD4C;
        }

        tr.recibido {
            background-color : #D9EDF7 ! important;
        }

        tr.porRecibir {
            background-color : transparent;
        }

        tr.sinRecepcion {
            /*background-color: #FFFFCC! important;*/
            background-color : #FC2C04 ! important;
            color            : #ffffff
        }

        tr.retrasado {
            /*background-color: #fc2c04! important;*/
            background-color : #F2DEDE ! important;
            /*color: #ffffff;*/
        }
        </style>

    </head>

    <body>

        <div class="row" style="margin-top: 0px; margin-left: 1px">
            <span class="grupo">
                <label class="well well-sm"
                       style="text-align: center; float: left">
                    Usuario: ${(persona?.titulo ?: '') + " " + persona?.nombre + " " + persona?.apellido + " - " +
                            persona?.departamento?.descripcion}
                </label>
            </span>

            <div class="btn-group" style="margin-left: 30px">

            </div>

        </div>

        <elm:flashMessage tipo="${flash.tipo}" clase="${flash.clase}">${flash.message}</elm:flashMessage>


        <div class="btn-toolbar toolbar">
            <div class="btn-group">
                <g:link action="" class="btn btn-success btnActualizar">
                    <i class="fa fa-refresh"></i> Actualizar
                </g:link>

            </div>

            <div>
                <div data-type="pendiente" class="alert alert-blanco alertas" clase="porRecibir">
                    (<span id="numEnv"></span>)
                Por recibir
                %{--Pendientes--}%
                </div>
            </div>


            %{--<div>--}%
            %{--<div data-type="pendiente" class="alert alert-otroRojo alertas" clase="sinRecepcion">--}%
            %{--(<span id="numPen"></span>)--}%
            %{--Sin Recepción--}%
            %{--No recibidos--}%
            %{--</div>--}%
            %{--</div>--}%

            <div>
                <div data-type="recibido" class="alert alert-info alertas" clase="recibido">
                    (<span id="numRec"></span>)
                Recibidos
                </div>
            </div>

            %{--<div>--}%
            %{--<div data-type="retrasado" class="alert alert-danger alertas" clase="retrasado">--}%
            %{--(<span id="numRet"></span>)--}%
            %{--Retrasados--}%
            %{--</div>--}%
            %{--</div>--}%
        </div>


        <div class="buscar" hidden="hidden" style="margin-bottom: 20px;">

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


        <div>
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

            var valAnexo

            $("input").keyup(function (ev) {
                if (ev.keyCode == 13) {
//                    submitForm($(".btnBusqueda"));
                    var memorando = $("#memorando").val();
                    var asunto = $("#asunto").val();
                    var fecha = $("#fechaBusqueda_input").val();
                    var datos = "memorando=" + memorando + "&asunto=" + asunto + "&fecha=" + fecha

                    $.ajax({
                        type    : "POST", url : "${g.createLink(controller: 'tramite', action: 'busquedaBandeja')}",
                        data    : datos,
                        success : function (msg) {
                            openLoader();
                            $("#bandeja").html(msg);
                            closeLoader();
                        }
                    });
                }
            });

            function cargarBandeja(band, datos) {
                $("#bandeja").html("").append($("<div style='width:100%; text-align: center;'/>").append(spinnerSquare64));
                if (!datos) {
                    datos = {};
                }
                if (band) {
                    openLoader();
                }
                $.ajax({
                    type    : "POST",
                    url     : "${g.createLink(controller: 'externos',action:'tablaBandeja')}",
                    data    : datos,
                    async   : false,
                    success : function (msg) {
                        $("#bandeja").html(msg);
                        cargarAlertas();
                        if (band) {
                            closeLoader();
                            log("Datos actualizados", "success");
                        }
                    }
                });
            }

            function cargarAlertas() {
//        $("#numPen").html($(".sinRecepcion").size()); //sinRecepcion
//        $("#numRet").html($(".retrasado").size()); //retrasado
                $("#numEnv").html($(".porRecibir").size()); //porRecibir
                $("#numRec").html($(".recibido").size()); //recibido
            }

            $(function () {
                <g:if test="${bloqueo}">
                $("#bloqueo-salida").show();
                </g:if>

                %{--var contestar = {--}%
                %{--text   : 'Contestar Documento',--}%
                %{--icon   : "<i class='fa fa-external-link'></i>",--}%
                %{--action : function (e) {--}%
                %{--$("tr.trHighlight").removeClass("trHighlight");--}%
                %{--e.preventDefault();--}%
                %{--location.href = "${g.createLink(action: 'crearTramite')}/?padre=" + id + "&pdt=" + idPxt;--}%
                %{--}--}%
                %{--};--}%

                var ver = {
                    text   : 'Ver',
                    icon   : "<i class='fa fa-search'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();
                        %{--location.href="${g.createLink(action: 'verPdf',controller: 'tramiteExport')}/"+id;--}%
                        %{--location.href = "${resource(dir:'tramites')}/"+archivo+".pdf";--}%

                        $.ajax({
                            type    : 'POST',
                            url     : '${createLink(action: 'revisarConfidencial')}/' + id,
                            success : function (msg) {
                                if (msg == 'ok') {
                                    window.open("${resource(dir:'tramites')}/" + archivo + ".pdf");
                                } else if (msg == 'no') {
//                                    log("No tiene permiso para ver este trámite", 'danger')
                                    bootbox.alert('No tiene permiso para ver el PDF de este trámite')
                                }
                            }

                        });
                    }
                };

                var recibir = {
                    text   : 'Recibir Documento',
                    icon   : "<i class='fa fa-check-square-o'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();
                        %{--$.ajax({--}%
                        %{--type    : 'POST',--}%
                        %{--url     : "${createLink(action: 'recibir')}/" + id,--}%
                        %{--success : function (msg) {--}%
                        %{--var b = bootbox.dialog({--}%
                        %{--id      : "dlgRecibido",--}%
                        %{--title   : "Trámite a ser recibido",--}%
                        %{--message : msg,--}%
                        %{--buttons : {--}%
                        %{--cancelar : {--}%
                        %{--label     : '<i class="fa fa-times"></i> Cancelar',--}%
                        %{--className : 'btn-danger',--}%
                        %{--callback  : function () {--}%
                        %{--}--}%
                        %{--},--}%
                        %{--recibir  : {--}%
                        %{--id        : 'btnRecibir',--}%
                        %{--label     : '<i class="fa fa-thumbs-o-up"></i> Recibir',--}%
                        %{--className : 'btn-success',--}%
                        %{--callback  : function () {--}%
                        $.ajax({
                            type    : 'POST',
                            %{--url     : '${createLink(action: 'guardarRecibir')}/' + id,--}%
                            url     : '${createLink(controller: 'externos', action: 'recibirTramiteExterno')}/?pdt=' + idPxt + "&source=bep",
                            success : function (msg) {
                                var parts = msg.split('_')
                                openLoader();
                                cargarBandeja();
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
//                                            }
//                                        }
//                                    }
//                                })
//                            }
//                        });
                    } //action
                };

                var seguimiento = {
                    text   : 'Seguimiento Trámite',
                    icon   : "<i class='fa fa-sitemap'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();
                        location.href = "${g.createLink(controller: 'tramite3', action: 'seguimientoTramite')}/" + id + "?pers=1";
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

                var anexos = {
                    text   : 'Anexos',
                    icon   : "<i class='fa fa-paperclip'></i>",
                    action : function (e) {
                        location.href = '${createLink(controller: 'documentoTramite', action: 'verAnexos')}/' + id
                    }
                };

                var arbol = {
                    text   : 'Cadena del trámite',
                    icon   : "<i class='fa fa-sitemap'></i>",
                    action : function (e) {
                        location.href = '${createLink(controller: 'tramite3', action: 'arbolTramite')}/' + id + "?b=bep"
                    }
                };

                var archivar = {
                    text   : 'Archivar Documentos',
                    icon   : "<i class='fa fa-folder-open-o'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();
                        $.ajax({
                            type    : "POST",
                            url     : "${createLink(controller: 'tramite', action: "revisarHijos")}",
                            data    : {
                                id   : idPxt,
//                                id   : id,
                                tipo : "archivar"
                            },
                            success : function (msg) {
                                var b = bootbox.dialog({
                                    id      : "dlgArchivar",
                                    title   : 'Archivar Tramite',
                                    message : msg,
                                    buttons : {
                                        cancelar : {
                                            label     : '<i class="fa fa-times"></i> Cancelar',
                                            className : 'btn-danger',
                                            callback  : function () {

                                            }
                                        },
                                        archivar : {
                                            id        : 'btnArchivar',
                                            label     : '<i class="fa fa-check"></i> Archivar',
                                            className : "btn-success",
                                            callback  : function () {

                                                $.ajax({
                                                    type    : 'POST',
                                                    url     : '${createLink(controller: 'tramite',action: 'archivar')}/' + idPxt,
                                                    data    : {
                                                        texto : $("#observacionArchivar").val()
                                                    },
                                                    success : function (msg) {
                                                        openLoader();
                                                        cargarBandeja();
                                                        closeLoader();
                                                        if (msg == 'ok') {
                                                            log("Trámite archivado correctamente", 'success')
                                                        } else if (msg == 'no') {
                                                            log("Error al archivar el trámite", 'error')
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                })

                            }

                        });
                    }

                };

                var distribuir = {
                    text   : 'Distribuir a Jefes',
                    icon   : "<i class='fa fa-eye'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();
                        $.ajax({
                            type    : "POST",
                            url     : "${createLink(action: 'observaciones')}/" + id,
                            success : function (msg) {
                                var b = bootbox.dialog({
                                    id      : "dlgObservaciones",
                                    title   : "Distribución al Jefe: Observaciones",
                                    message : msg,
                                    buttons : {
                                        cancelar : {
                                            label     : "Cancelar",
                                            className : 'btn-danger',
                                            callback  : function () {
                                            }
                                        },
                                        guardar  : {
                                            id        : 'btnSave',
                                            label     : '<i class="fa fa-save"></i> Guardar',
                                            className : "btn-success",
                                            callback  : function () {

                                                $.ajax({
                                                    type    : 'POST',
                                                    url     : '${createLink(action: 'guardarObservacion')}/' + id,
                                                    data    : {
                                                        texto : $("#observacion").val()
                                                    },
                                                    success : function (msg) {
                                                        bootbox.alert(msg)
                                                    }
                                                });
                                            }
                                        }
                                    }
                                })
                            }
                        });
                    }
                };

                var anular = {
                    text   : 'Anular Trámite',
                    icon   : "<i class='fa fa-flash'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();
                        $.ajax({
                            type    : "POST",
                            url     : "${createLink(controller: 'tramite', action: "revisarHijos")}",
                            data    : {
                                id   : id,
                                tipo : "anular"
                            },
                            success : function (msg) {
                                var b = bootbox.dialog({
                                    id      : "dlgAnular",
                                    title   : 'Anular Trámite',
                                    message : msg,
                                    buttons : {
                                        cancelar : {
                                            label     : '<i class="fa fa-times"></i> Cancelar',
                                            className : 'btn-danger',
                                            callback  : function () {

                                            }
                                        },
                                        archivar : {
                                            id        : 'btnAnular',
                                            label     : '<i class="fa fa-check"></i> Anular',
                                            className : "btn-success",
                                            callback  : function () {

                                                $.ajax({
                                                    type    : 'POST',
                                                    url     : '${createLink(action: 'anular')}/' + id,
                                                    data    : {
                                                        texto : $("#observacionArchivar").val()
                                                    },
                                                    success : function (msg) {
                                                        openLoader();
                                                        cargarBandeja();
                                                        closeLoader();
                                                        if (msg == 'ok') {
                                                            log("Trámite anulado correctamente", 'success')
                                                        } else if (msg == 'no') {
                                                            log("Error al anular el trámite", 'error')
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                })
                            }
                        });
                    }
                };

                var archivo;
//                var valAnexo;
//        context.settings({
//            onShow : function (e) {
//                $("tr.trHighlight").removeClass("trHighlight");
//                var $tr = $(e.target).parents("tr");
//                $tr.addClass("trHighlight");
//                id = $tr.data("id");
//                idPxt = $tr.attr("prtr");
//                archivo = $tr.attr("departamento") + "/" + $tr.attr("codigo")
//                valAnexo = $tr.attr("anexo");
//            }
//        });

                %{--context.attach('.porRecibir, .sinRecepcion', [--}%
                %{--{--}%
                %{--header : 'Acciones'--}%
                %{--},--}%
                %{--detalles,--}%
                %{--arbol,--}%
                %{--<g:if test="${Persona.get(session.usuario.id).puedeVer}">--}%
                %{--//                    ver,--}%
                %{--//                    seguimiento,--}%
                %{--</g:if>--}%
                %{--recibir--}%

                %{--]);--}%
                %{--context.attach('.recibido, .retrasado', [--}%
                %{--{--}%
                %{--header : 'Acciones'--}%
                %{--},--}%
                %{--detalles,--}%
                %{--arbol,--}%
                %{--archivar--}%
                %{--</g:if>--}%

                %{--]);--}%
                %{--context.attach('.conAnexo.porRecibir, .conAnexo.sinRecepcion', [--}%
                %{--{--}%
                %{--header : 'Acciones'--}%
                %{--},--}%
                %{--detalles,--}%
                %{--arbol,--}%
                %{--recibir,--}%
                %{--anexos--}%
                %{--]);--}%

                %{--context.attach('.conAnexo.recibido, .conAnexo.retrasado', [--}%
                %{--{--}%
                %{--header : 'Acciones'--}%
                %{--},--}%
                %{--detalles,--}%
                %{--arbol,--}%
                %{--<g:if test="${Persona.get(session.usuario.id).puedeArchivar}">--}%
                %{--archivar,--}%
                %{--</g:if>--}%
                %{--anexos--}%

                %{--]);--}%

                $(".btnBuscar").click(function () {
                    $(".buscar").attr("hidden", false);
                });

                $(".btnSalir").click(function () {
                    $(".buscar").attr("hidden", true);
                    cargarBandeja();

                });

                $(".btnActualizar").click(function () {
//                    openLoader();
                    cargarBandeja(false);
//                    closeLoader();
                    return false;
                });

                cargarBandeja();

                setInterval(function () {

                    openLoader();
                    cargarBandeja(false);
                    closeLoader();

                }, 300000);

                $(".alertas").click(function () {
                    var clase = $(this).attr("clase");
                    $("tr").each(function () {
                        if ($(this).hasClass(clase)) {
                            if ($(this).hasClass("trHighlight"))
                                $(this).removeClass("trHighlight")
                            else
                                $(this).addClass("trHighlight")
                        } else {
                            $(this).removeClass("trHighlight")
                        }
                    });

                });

                $(".btnBusqueda").click(function () {
                    $("#bandeja").html("").append($("<div style='width:100%; text-align: center;'/>").append(spinnerSquare64));
                    var memorando = $("#memorando").val();
                    var asunto = $("#asunto").val();
                    var fecha = $("#fechaBusqueda_input").val();
                    var datos = "memorando=" + memorando + "&asunto=" + asunto + "&fecha=" + fecha

                    $.ajax({
                        type    : "POST", url : "${g.createLink(controller: 'tramite', action: 'busquedaBandeja')}",
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