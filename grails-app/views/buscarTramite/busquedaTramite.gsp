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
            width      : 1070px;
            height     : 310px;
            float      : left;
            overflow   : auto;
            overflow-y : auto;
        }

        .alinear {

            text-align : center !important;
        }

        </style>

    </head>

    <body>

        <div style="margin-top: 0px;" class="vertical-container">

            <p class="css-vertical-text" style="margin-top: -10px;">Buscar</p>

            <div class="linea"></div>

            <div style="margin-bottom: 20px">
                <div class="col-md-2">
                    <label>Documento</label>
                    <g:textField name="memorando" value="" maxlength="15" class="form-control allCaps"/>
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
                    <a href="#" name="busqueda" class="btn btn-success btnBusqueda"><i
                            class="fa fa-check-square-o"></i> Buscar</a>

                    <a href="#" name="borrar" class="btn btn-primary btnBorrar"><i
                            class="fa fa-eraser"></i> Limpiar</a>

                </div>

            </div>

        </div>

        %{--//bandeja--}%

        <div style="margin-top: 30px; min-height: 460px" class="vertical-container" id="divBandeja">

            <p class="css-vertical-text">Resultado</p>

            <div class="linea"></div>

            <div id="bandeja">

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

                    $.ajax({ type : "POST", url : "${g.createLink(controller: 'buscarTramite', action: 'tablaBusquedaTramite')}",
                        data      : datos,
                        success   : function (msg) {
                            $("#bandeja").html(msg);
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
                var estado = $tr.attr("estado");
                var padre = $tr.attr("padre");
                var de = $tr.attr("de");
                var archivo = $tr.attr("departamento") + "/" + $tr.attr("anio") + "/" + $tr.attr("codigo");
                var idPxt = $tr.attr("prtr");
                var valAnexo = $tr.attr("anexo");

                var porRecibir = $tr.hasClass("porRecibir");
                var sinRecepcion = $tr.hasClass("sinRecepcion");
                var recibido = $tr.hasClass("recibido");
                var retrasado = $tr.hasClass("retrasado");
                var conAnexo = $tr.hasClass("conAnexo");
                var conPadre = $tr.hasClass("padre");
                var esPrincipal = $tr.hasClass("principal");

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
                        <g:if test="${session.usuario.esTriangulo}">
                        location.href = '${createLink(controller: "tramite2", action: "crearTramiteDep")}?padre=' + padre + "&hermano=" + id;
                        </g:if>
                        <g:else>
                        location.href = '${createLink(controller: "tramite", action: "crearTramite")}?padre=' + padre + "&hermano=" + id;
                        </g:else>
                    }
                };

                var contestar = {
                    label  : "Agregar documento al trámite",
                    icon   : "fa fa-paste",
                    action : function () {
                        <g:if test="${session.usuario.esTriangulo}">
                        location.href = '${createLink(controller: "tramite2", action: "crearTramiteDep")}?padre=' + id + "&buscar=1";
                        </g:if>
                        <g:else>
                        location.href = '${createLink(controller: "tramite", action: "crearTramite")}?padre=' + id + "&buscar=1";
                        </g:else>
                    }
                };

                var administrar = {
                    label  : "Administrar trámite",
                    icon   : "fa fa-cogs",
                    action : function () {
                        location.href = '${createLink(controller: "tramiteAdmin", action: "arbolAdminTramite")}?id=' + id;
                    }
                };

                var anexos = {
                    text   : 'Anexos',
                    icon   : "<i class='fa fa-paperclip'></i>",
                    action : function (e) {
                        location.href = '${createLink(controller: 'documentoTramite', action: 'verAnexos')}/' + id
                    }
                };

                var ampliarPlazo = {
                    text   : "Ampliar plazo",
                    icon   : "<i class='fa fa-arrows-h'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();
                        $.ajax({
                            type    : 'POST',
                            url     : '${createLink(controller: 'buscarTramite', action: 'ampliarPlazoUI_ajax')}',
                            data    : {
                                id : id
                            },
                            success : function (msg) {
                                bootbox.dialog({
                                    title   : "Ampmliar plazo",
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
                                                if ($frm.valid()) {
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
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                };

                items.header.label = "Acciones";
                items.detalles = detalles;
                items.arbol = arbol;
                <g:if test="${session.usuario.getPuedeAdmin()}">
                items.administrar = administrar;
                </g:if>
                if (conPadre) {
                    items.crearHermano = crearHermano;
                }
                if (esPrincipal) {
                    items.contestar = contestar;
                }

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