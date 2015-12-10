<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 14/03/14
  Time: 11:18 AM
--%>

<%@ page import="happy.seguridad.Persona; happy.tramites.Tramite" contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Búsqueda de Trámites</title>

        <style type="text/css">

        .container-celdas {
            width    : 1070px;
            height   : 310px;
            float    : left;
            overflow : auto;
        }

        .alinear {

            text-align : center !important;
        }
        </style>

    </head>

    <body>
        %{--<h3>Búsqueda de Trámites</h3>--}%
        <div style="margin-top: 0px;" class="vertical-container">
            <p class="css-vertical-text" style="margin-top: -10px;">Buscar</p>

            <div class="linea"></div>

            <div style="margin-bottom: 20px">
                <div class="col-md-2">
                    <label>Documento</label>
                    <g:textField name="memorando" value="" maxlength="20" class="form-control allCaps" style="width: 180px;margin-left: -20px"/>
                </div>

                <div class="col-md-2">
                    <label>Asunto</label>
                    <g:textField name="asunto" value="" style="width: 300px" maxlength="30" class="form-control"/>
                </div>

                <div class="col-md-2" style="margin-left: 150px">
                    <label>Fecha Creación</label>
                    <elm:datepicker name="fechaRecepcion" class="datepicker form-control" value=""/>
                </div>


                <div class="col-md-2" style="margin-left: 15px">
                    <label>Fecha Envio</label>
                    <elm:datepicker name="fechaBusqueda" class="datepicker form-control" value=""/>
                </div>


                <div style="padding-top: 25px">
                    <a href="#" name="busqueda" class="btn btn-success btnBusqueda btn-ajax"><i
                            class="fa fa-check-square-o"></i> Buscar</a>

                    <a href="#" name="borrar" class="btn btn-primary btnBorrar"><i
                            class="fa fa-eraser"></i> Limpiar</a>

                </div>

            </div>

        </div>

        %{--//bandeja--}%

        <div style="margin-top: 30px; min-height: 460px" class="vertical-container" id="divBandeja">

            <p class="css-vertical-text">Resultado - Buscar trámites</p>

            <div class="linea"></div>

            <div id="bandeja">

            </div>

        </div>

        <div><strong>Nota</strong>: Si existen muchos registros que coinciden con el criterio de búsqueda, se retorna como máximo 20 <span class="text-info" style="margin-left: 40px">Se ordena por tipo de documento y fecha</span>
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


        <script>
            $(function () {
                var cellWidth = 150;
                var celHegth = 25;
                var select = null;
                var headerTop = $(".header-columnas");
//        var headerLeft=$(".header-filas");

                $(".h-A").resizable({
                    handles    : "e",
                    minWidth   : 30,
                    alsoResize : ".A"
                });
                $(".container-celdas").scroll(function () {
//            $("#container-filas").scrollTop($(".container-celdas").scrollTop());
                    $("#container-cols").scrollLeft($(".container-celdas").scrollLeft());
                });

            });
        </script>

        <script type="text/javascript">


            function loading(div) {
                y = 0;
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

            $(".btnBusqueda").click(function () {
                $("#bandeja").html("").append($("<div style='width:100%; text-align: center;'/>").append(spinnerSquare64));
                var memorando = $("#memorando").val();
                var asunto = $("#asunto").val();
                var fecha = $("#fechaBusqueda_input").val();
                var fechaRecepcion = $("#fechaRecepcion_input").val();

                var datos = "memorando=" + memorando + "&asunto=" + asunto + "&fecha=" + fecha + "&fechaRecepcion=" + fechaRecepcion;

                $.ajax({
                    type    : "POST",
                    url     : "${g.createLink(controller: 'buscarTramite', action: 'tablaBusquedaTramite')}",
                    data    : datos,
                    success : function (msg) {
//                clearInterval(interval)
                        $("#bandeja").html(msg);
                    },
                    error   : function (msg) {
                        $("#bandeja").html("Ha ocurrido un error");
                    }
                });

            });

            $("input").keyup(function (ev) {
                if (ev.keyCode == 13) {
                    $("#bandeja").html("").append($("<div style='width:100%; text-align: center;'/>").append(spinnerSquare64));
                    var memorando = $("#memorando").val();
                    var asunto = $("#asunto").val();
                    var fecha = $("#fechaBusqueda_input").val();
                    var fechaRecepcion = $("#fechaRecepcion_input").val();

                    var datos = "memorando=" + memorando + "&asunto=" + asunto + "&fecha=" + fecha + "&fechaRecepcion=" + fechaRecepcion

                    $.ajax({
                        type    : "POST",
                        url     : "${g.createLink(controller: 'buscarTramite', action: 'tablaBusquedaTramite')}",
                        data    : datos,
                        success : function (msg) {
                            $("#bandeja").html(msg);
                        },
                        error   : function (msg) {
                            $("#bandeja").html("Ha ocurrido un error");
                        }
                    });
                }
            });

            var padre;

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
                var anulados = $tr.attr("anulados");
                var padre = $tr.attr("padre");
                var de = $tr.attr("de");
                var archivo = $tr.attr("departamento") + "/" + $tr.attr("anio") + "/" + $tr.attr("codigo");
                var idPxt = $tr.attr("prtr");
                var valAnexo = $tr.attr("anexo");

                var dptoId = $tr.data("de");

                var remitenteParts = $tr.attr("de").split("_");
                var remitenteTipo = remitenteParts[0];
                var remitenteId = remitenteParts[1];

                var porRecibir = $tr.hasClass("porRecibir");
                var sinRecepcion = $tr.hasClass("sinRecepcion");
                var recibido = $tr.hasClass("recibido");
                var retrasado = $tr.hasClass("retrasado");
                var externo = $tr.hasClass("externo");
                var externoCC = $tr.hasClass("externoCC");
                var conAnexo = $tr.hasClass("conAnexo");
                var conPadre = $tr.hasClass("padre");
                var esPrincipal = $tr.hasClass("principal");
                var anulado = $tr.hasClass("estado");
                var enviado = $tr.hasClass("enviado"); //enviado

                var esMio = $tr.hasClass("mio");

                var depId = $tr.attr("dep");

                var tienePrincipal = $tr.attr("principal").toString() != '0' && $tr.attr("principal").toString() != $tr.attr("id");

                var paraMiDep = false;
                var para = $tr.attr("para");
                var respuestas = $tr.attr("respuestas");
                var paras = para.split(",");
                for (var i = 0; i < paras.length; i++) {
                    var p = parseInt(paras[i]);
                    if (p == ${session.usuario.departamentoId}) {
                        paraMiDep = true;
                    }
                }

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

                var arbol = {
                    label  : 'Cadena del trámite',
                    icon   : "fa fa-sitemap",
                    action : function (e) {
                        location.href = '${createLink(controller: 'tramite3', action: 'arbolTramite')}/' + id + "?b=bqt"
                    }
                };

                var detalles = {
                    label  : 'Detalles',
                    icon   : "fa fa-search",
                    action : function (e) {
                        $("#dialog-body").html(spinner);
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
                };

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

                %{--var contestar = {--}%
                %{--label  : "Agregar documento al trámite",--}%
                %{--icon   : "fa fa-paste",--}%
                %{--action : function () {--}%
                %{--$.ajax({--}%
                %{--type    : 'POST',--}%
                %{--url     : '${createLink(controller: 'buscarTramite', action: 'verificarAgregarDoc')}',--}%
                %{--data    : {--}%
                %{--id : id--}%
                %{--},--}%
                %{--success : function (msg) {--}%
                %{--if (msg == "OK") {--}%
                %{--<g:if test="${session.usuario.esTriangulo}">--}%
                %{--location.href = '${createLink(controller: "tramite2", action: "crearTramiteDep")}?padre=' + id + "&buscar=1";--}%
                %{--</g:if>--}%
                %{--<g:else>--}%
                %{--location.href = '${createLink(controller: "tramite", action: "crearTramite")}?padre=' + id + "&buscar=1";--}%
                %{--</g:else>--}%
                %{--} else {--}%
                %{--bootbox.alert("No puede agregar documentos a este trámite");--}%
                %{--}--}%
                %{--}--}%
                %{--});--}%
                %{--}--}%
                %{--};--}%

                var administrar = {
                    label  : "Administrar trámite",
                    icon   : "fa fa-cogs",
                    action : function () {
                        location.href = '${createLink(controller: "tramiteAdmin", action: "arbolAdminTramite")}?id=' + id;
                    }
                };

                var anexos = {
                    label  : 'Anexos',
                    icon   : "fa fa-paperclip",
                    action : function (e) {
                        location.href = '${createLink(controller: 'documentoTramite', action: 'verAnexos')}/' + id
                    }
                };

                var ampliarPlazo = {
                    label  : "Ampliar plazo",
                    icon   : "fa fa-arrows-h",
                    action : function (e) {
                        $.ajax({
                            type    : 'POST',
                            url     : '${createLink(controller: 'buscarTramite', action: 'ampliarPlazoUI_ajax')}',
                            data    : {
                                id : id
                            },
                            success : function (msg) {
                                bootbox.dialog({
                                    title   : "Ampliar plazo",
                                    message : msg,
                                    class   : "long",
                                    buttons : {
                                        cancelar : {
                                            label     : "Cancelar",
                                            className : "btn-primary",
                                            callback  : function () {
                                            }
                                        },
                                        guardar  : {
                                            label     : "<i class='fa fa-save'></i> Guardar",
                                            className : "btn-success",
                                            callback  : function () {
                                                var $frm = $("#frm-ampliar");
                                                var $txt = $("#aut");
                                                if ($frm.valid()) {
//                                                    if (validaAutorizacion($txt)) {
                                                    openLoader("Ampliando plazo");
                                                    $.ajax({
                                                        type    : "POST",
                                                        url     : $frm.attr("action"),
                                                        data    : $frm.serialize(),
                                                        success : function (msg) {
                                                            var parts = msg.split("_");
                                                            log(parts[1], parts[0] == "OK" ? "success" : "error"); // log(msg, type, title, hide)
                                                            closeLoader();
                                                        }
                                                    });
//                                                    } else {
//                                                        return false;
//                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                };

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

                items.infoRemitente = infoRemitente;

                items.header.label = "Acciones";
                <g:if test="${session.usuario.getPuedeVer()}">
                items.detalles = detalles;
                items.arbol = arbol;
                </g:if>
                <g:if test="${session.usuario.getPuedeAdmin()}">
                items.administrar = administrar;
                </g:if>
//                if (conPadre || tienePrincipal || esPrincipal) {
                if (esMio) {
                    if (conPadre) {
                        items.crearHermano = crearHermano;
                    } else {
                        items.crearHijo = crearHijo;
                    }
                }

                %{--if (externo || externoCC) {--}%
                %{--<g:if test="${puedeAgregarExternos}">--}%
//                items.asociarExterno = agregarPadre;
                %{--</g:if>--}%
                %{--}--}%

//                if (esPrincipal) {
//                    items.contestar = contestar;
//                }

                %{--<g:if test="${session.usuario.getPuedeJefe()}">--}%
                %{----}%
                %{--items.plazo = ampliarPlazo;--}%
                %{--</g:if>--}%
                %{--console.log(!externo && recibido && parseInt(anulados) == 0 && ${session.usuario.getPuedePlazo()} && parseInt("${session.usuario.departamentoId}") == parseInt(depId));--}%

                %{--if (recibido && parseInt(anulados) == 0 && ${session.usuario.getPuedePlazo()} && parseInt("${session.usuario.departamentoId}") == parseInt(depId)) {--}%
                %{--items.plazo = ampliarPlazo;--}%
                %{--}--}%

                %{--console.log(paras, ${session.usuario.departamentoId}, paraMiDep, respuestas, recibido, ${session.usuario.getPuedePlazo()});--}%

                /* cambiado el 01-07-2015 de la version anterior ^ */
                %{--if (recibido && parseInt(respuestas) == 0 && ${session.usuario.puedePlazo} && paraMiDep) {--}%
                    %{--items.plazo = ampliarPlazo;--}%
                %{--}--}%

                if (enviado || recibido) {
                    <g:if test="${session.usuario.getPuedeCopiar()}">
                    if (esMio) {
                        items.copia = copia;
                    }
                    </g:if>
                }


/*
                <g:if test="${session.usuario.getPuedeCopiar()}">
                if (esMio) {
                    items.copia = copia;
                }
                </g:if>
*/

                return items
            }

            $(".btnBorrar").click(function () {

                $("#memorando").val("");
                $("#asunto").val("");
                $("#fechaRecepcion_input").val('');
                $("#fechaBusqueda_input").val('')

            });


        </script>

    </body>
</html>