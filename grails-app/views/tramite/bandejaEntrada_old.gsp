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
        <title>Bandeja de Entrada</title>

        <style type="text/css">

        body {
            background-color : #DFD;
        }

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

        .letra {

            /*font-family: "Arial Black", arial-black;*/
            /*background-color: #7eb75e;*/
            background-color : #8fe6c3;
        }

        #7aaedb

        </style>

    </head>

    <body>

        <div class="row" style="margin-top: 0px; margin-left: 1px">
            <span class="grupo">
                <label class="well well-sm letra" style="text-align: center">
                    BANDEJA DE ENTRADA PERSONAL
                </label>
            </span>


            <span class="grupo">
                <label class="well well-sm"
                       style="text-align: center;">
                    Usuario: ${persona?.nombre + " " + persona?.apellido + " - " +
                            persona?.departamento?.descripcion}
                </label>
            </span>

        </div>

        <elm:flashMessage tipo="${flash.tipo}" clase="${flash.clase}">${flash.message}</elm:flashMessage>


        <div class="btn-toolbar toolbar">
            <div class="btn-group">

                <a href="#" class="btn btn-primary btnBuscar"><i class="fa fa-book"></i> Buscar</a>
            %{--<g:link action="archivados" class="btn btn-primary btnArchivados" controller="tramite">--}%
            %{--<i class="fa fa-folder"></i> Archivados--}%
            %{--</g:link>--}%

                <g:link action="" class="btn btn-success btnActualizar">
                    <i class="fa fa-refresh"></i> Actualizar
                </g:link>

                <g:link action="crearTramite" class="btn btn-default btnCrearTramite" style="margin-left: 10px">
                    <i class="fa fa-edit"></i> Crear Trámite Principal
                </g:link>

            </div>

            <div style="float: right">
                %{--<div>--}%
                <div data-type="pendiente" class="alert alert-blanco alertas" clase="porRecibir">
                    (<span id="numEnv"></span>)
                Por recibir
                </div>
                %{--</div>--}%


                %{--<div>--}%
                <div data-type="pendiente" class="alert alert-otroRojo alertas" clase="sinRecepcion">
                    (<span id="numPen"></span>)
                Sin Recepción
                </div>
                %{--</div>--}%

                %{--<div>--}%
                <div data-type="recibido" class="alert alert-info alertas" clase="recibido">
                    (<span id="numRec"></span>)
                Recibidos
                </div>
                %{--</div>--}%

                %{--<div>--}%
                <div data-type="retrasado" class="alert alert-danger alertas" clase="retrasado">
                    (<span id="numRet"></span>)
                Retrasados
                </div>
                %{--</div>--}%
            </div>

        </div>


        <div class="buscar" hidden="hidden" style="margin-bottom: 20px;">

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
                $(".qtip").hide();
                $("#bandeja").html("").append($("<div style='width:100%; text-align: center;'/>").append(spinnerSquare64));
                if (!datos) {
                    datos = {};
                }
                if (band) {
                    openLoader();
                }
                $.ajax({
                    type    : "POST",
                    url     : "${g.createLink(controller: 'tramite',action:'tablaBandeja_old')}",
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
                $("#numPen").html($(".sinRecepcion").size()); //sinRecepcion
                $("#numRet").html($(".retrasado").size()); //retrasado
                $("#numEnv").html($(".porRecibir").size()); //porRecibir
                $("#numRec").html($(".recibido").size()); //recibido
            }

            //nuevo contextMenu

            function createContextMenu(node) {
                var $tr = $(node);

                var items = {
                    header : {
                        label  : "Sin Acciones",
                        header : true
                    }
                };

                var id = $tr.data("id");
                var codigo = $tr.attr("codigo");
                var estado = $tr.attr("estado");
                var padre = $tr.attr("padre");
                var de = $tr.attr("de");
                var archivo = $tr.attr("departamento") + "/" + $tr.attr("anio") + "/" + $tr.attr("codigo");
                var idPxt = $tr.attr("prtr");
                var valAnexo = $tr.attr("anexo");
                var externo = $tr.hasClass("1");
                var esExterno = $tr.hasClass("estadoExterno");
                var esCopia = $tr.hasClass("R002");
                var remitenteParts = $tr.attr("de").split("_");
                var remitenteTipo = remitenteParts[0];
                var remitenteId = remitenteParts[1];

                var porRecibir = $tr.hasClass("porRecibir");
                var sinRecepcion = $tr.hasClass("sinRecepcion");
                var recibido = $tr.hasClass("recibido");
                var retrasado = $tr.hasClass("retrasado");
                var conAnexo = $tr.hasClass("conAnexo");
//                console.log("por porRecibir",porRecibir)

                var infoRemitente = {
                    label           : 'Información remitente',
                    icon            : "fa fa-search",
                    separator_afetr : true,
                    action          : function (e) {
                        var url = "", title = "";
                        switch (remitenteTipo) {
                            case "D":
                                url = "${createLink(controller: 'departamento', action: 'show_ajax')}";
                                title = "Información del departamento";
                                break;
                            case "P":
                                url = "${createLink(controller: 'persona', action: 'show_ajax')}";
                                title = "Información de la persona";
                                break;
                            case "E":
                                title = "Información de entidad externa";
                                url = "${createLink(controller:'tramite3', action:'infoRemitente')}";
                                break;
                        }
                        $.ajax({
                            type    : 'POST',
                            url     : url,
                            data    : {
                                id      : remitenteId,
                                tramite : id
                            },
                            success : function (msg) {
                                bootbox.dialog({
                                    title   : title,
                                    message : msg,
                                    class   : "medium",
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
                    }
                };

                var contestar = {
                    label : 'Contestar Documento',
                    icon  : "fa fa-external-link",
                    url   : "${g.createLink(action: 'crearTramite')}/?padre=" + id + "&pdt=" + idPxt + "&esRespuesta=1&esRespuestaNueva=S"
                };

                var ver = {
                    label  : 'Ver',
                    icon   : "fa fa-search",
                    action : function (e) {

                        location.href = "${g.createLink(action: 'verPdf',controller: 'tramiteExport')}/" + id;
                        location.href = "${resource(dir:'tramites')}/" + archivo + ".pdf";

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
                    label  : 'Recibir Documento',
                    icon   : "fa fa-check-square-o",
                    action : function (e) {

                        $.ajax({
                            type    : 'POST',
                            %{--url     : '${createLink(action: 'guardarRecibir')}/' + id,--}%
                            url     : '${createLink(controller: 'tramite3', action: 'recibirTramite')}/' + id + "?source=bep",
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
                    } //action
                };

                var seguimiento = {
                    label  : 'Seguimiento Trámite',
                    icon   : "fa fa-sitemap",
                    action : function (e) {

                        location.href = "${g.createLink(controller: 'tramite3', action: 'seguimientoTramite')}/" + id + "?pers=1";
                    }
                };

                var detalles = {
                    label  : 'Detalles',
                    icon   : "fa fa-search",
                    action : function (e) {

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
                    label  : 'Anexos',
                    icon   : "fa fa-paperclip",
                    action : function (e) {
                        location.href = '${createLink(controller: 'documentoTramite', action: 'verAnexos')}/' + id
                    }
                };

                var arbol = {
                    label  : 'Cadena del trámite',
                    icon   : "fa fa-sitemap",
                    action : function (e) {
                        location.href = '${createLink(controller: 'tramite3', action: 'arbolTramite')}/' + id + "?b=bep"
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

                var archivar = {
                    label  : 'Archivar Documentos',
                    icon   : "fa fa-folder-open-o",
                    action : function (e) {

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
                                                openLoader();
                                                cargarBandeja();
                                                closeLoader();
                                            }
                                        },
                                        archivar : {
                                            id        : 'btnArchivar',
                                            label     : '<i class="fa fa-check"></i> Archivar',
                                            className : "btn-success",
                                            callback  : function () {
                                                var $txt = $("#aut");
//                                                if (validaAutorizacion($txt)) {
                                                openLoader();
                                                $.ajax({
                                                    type    : 'POST',
                                                    url     : '${createLink(action: 'archivar')}/' + idPxt,
                                                    data    : {
                                                        texto : $("#observacionArchivar").val()/*,
                                                         aut   : $txt.val()*/
                                                    },
                                                    success : function (msg) {
                                                        cargarBandeja();
                                                        closeLoader();
                                                        if (msg == 'ok') {
                                                            log("Trámite archivado correctamente", 'success')
                                                        } else if (msg == 'no') {
                                                            log("Error al archivar el trámite", 'error')
                                                        }
                                                    }
                                                });
//                                                } else {
//                                                    return false;
//                                                }
                                            }
                                        }
                                    }
                                });
                                setTimeout(function () {
                                    if (msg.indexOf("error") > -1) {
                                        b.find(".btn-success").remove();
                                        b.find(".btn-danger").removeClass("btn-danger").addClass("btn-default").html("Cerrar");
                                    }
                                }, 300);
                            }

                        });
                    }

                };

                var distribuir = {
                    label  : 'Distribuir a Jefes',
                    icon   : "fa fa-eye",
                    action : function (e) {

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

                %{--var anular = {--}%
                %{--label  : 'Anular Trámite',--}%
                %{--icon   : "fa fa-flash",--}%
                %{--action : function (e) {--}%

                %{--$.ajax({--}%
                %{--type    : "POST",--}%
                %{--url     : "${createLink(controller: 'tramite', action: "revisarHijos")}",--}%
                %{--data    : {--}%
                %{--id   : id,--}%
                %{--tipo : "anular"--}%
                %{--},--}%
                %{--success : function (msg) {--}%
                %{--var b = bootbox.dialog({--}%
                %{--id      : "dlgAnular",--}%
                %{--title   : 'Anular Trámite',--}%
                %{--message : msg,--}%
                %{--buttons : {--}%
                %{--cancelar : {--}%
                %{--label     : '<i class="fa fa-times"></i> Cancelar',--}%
                %{--className : 'btn-danger',--}%
                %{--callback  : function () {--}%

                %{--}--}%
                %{--},--}%
                %{--anular   : {--}%
                %{--id        : 'btnAnular',--}%
                %{--label     : '<i class="fa fa-check"></i> Anular',--}%
                %{--className : "btn-success",--}%
                %{--callback  : function () {--}%

                %{--$.ajax({--}%
                %{--type    : 'POST',--}%
                %{--url     : '${createLink(action: 'anular')}/' + id,--}%
                %{--data    : {--}%
                %{--texto : $("#observacionArchivar").val()--}%
                %{--},--}%
                %{--success : function (msg) {--}%
                %{--openLoader();--}%
                %{--cargarBandeja();--}%
                %{--closeLoader();--}%
                %{--if (msg == 'ok') {--}%
                %{--log("Trámite anulado correctamente", 'success')--}%
                %{--} else if (msg == 'no') {--}%
                %{--log("Error al anular el trámite", 'error')--}%
                %{--}--}%
                %{--}--}%
                %{--});--}%
                %{--}--}%
                %{--}--}%
                %{--}--}%
                %{--})--}%
                %{--}--}%
                %{--});--}%
                %{--}--}%
                %{--};--}%

                items.header.label = "Acciones";

                items.infoRemitente = infoRemitente;

                var idSession = ${session.usuario.id};

//                if(idSession == remitenteId){
                <g:if test="${session.usuario.getPuedeVer()}">
                items.detalles = detalles;
                </g:if>
//                }

                <g:if test="${session.usuario.getPuedeVer()}">
                items.arbol = arbol;
                </g:if>

                if (conAnexo && recibido) {
                    items.anexo = anexos
                }
                if (retrasado) {
                    items.contestar = contestar
                }
                if (sinRecepcion) {
                    items.recibir = recibir
                }

                if (porRecibir) {

                    items.recibir = recibir
                }
//
//                if(externo){
//                    delete items.recibir
//                    items.recibirExt = recibirExt
//                }

                if (recibido || retrasado) {
                    <g:if test="${session.usuario.getPuedeVer()}">
                    items.arbol = arbol;
                    </g:if>
                    items.contestar = contestar;
                    <g:if test="${session.usuario.getPuedeArchivar()}">
                    items.archivar = archivar;
                    </g:if>
                    <g:else>
                    if (esCopia) {
                        items.archivar = archivar;
                    }
                    </g:else>
                    items.observaciones = observaciones;
                }

                var estado1 = {
                    label  : "Cambiar estado",
                    icon   : "fa fa-exchange",
                    action : function () {
                        $.ajax({
                            type    : "POST",
                            url     : "${createLink(controller: 'tramiteAdmin', action: 'cambiarEstado')}",
                            data    : {
                                id          : id,
                                tramiteInfo : ""
                            },
                            success : function (msg) {
                                bootbox.dialog({
                                    id      : "dlgExterno",
                                    title   : '<span class="text-default"><i class="fa fa-exchange"></i> Cambiar estado de trámite externo</span>',
                                    message : msg,
                                    buttons : {
                                        cancelar : {
                                            label     : '<i class="fa fa-times"></i> Cancelar',
                                            className : 'btn-danger',
                                            callback  : function () {
                                            }
                                        },
                                        cambiar  : {
                                            id        : 'btnCambiar',
                                            label     : '<i class="fa fa-check"></i> Cambiar estado',
                                            className : "btn-success",
                                            callback  : function () {
                                                var nuevoEstado = $("#estadoExterno").val();
                                                openLoader("Cambiando estado");
                                                $.ajax({
                                                    type    : 'POST',
                                                    url     : '${createLink(controller: "tramiteAdmin", action: "guardarEstado")}',
                                                    data    : {
                                                        id     : id,
                                                        estado : nuevoEstado
                                                    },
                                                    success : function (msg) {
                                                        var parts = msg.split("*");
                                                        if (parts[0] == 'OK') {
                                                            log(parts[1], 'success');
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
                    }
                }

                if (recibido) {
                    if (esExterno) {
                        items.externo = estado1
                    }
                }

                return items
            }

            //old
            $(function () {

                $(".btnBuscar").click(function () {
                    $(".buscar").attr("hidden", false);
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
//                    openLoader();
                    cargarBandeja(false);
//                    closeLoader();
                    return false;
                });

                cargarBandeja();

//                setInterval(function () {
//                    openLoader();
//                    cargarBandeja(false);
//                    closeLoader();
//                    $(".qtip").hide();
//                }, 1000 * 60 * 3);

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