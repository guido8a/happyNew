<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 07/03/14
  Time: 11:44 AM
--%>

<%@ page import="happy.seguridad.Persona" contentType="text/html;charset=UTF-8" %>
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

        .cabecera.sortable {
            cursor : pointer;
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
                       style="text-align: center; float: left">Departamento: ${persona?.departamento?.descripcion}</label>
            </span>

            <div class="btn-group" style="margin-left: 30px">

                <g:link controller="tramite2" action="crearTramiteDep" class="btn btn-default btnCrearTramite">
                    <i class="fa fa-edit"></i> Crear Trámite
                </g:link>

            </div>
        </div>

        <div class="btn-toolbar toolbar">
            <div class="btn-group">
                <a href="#" class="btn btn-primary btnBuscar"><i class="fa fa-book"></i> Buscar</a>

                <a href="#" class="btn btn-primary btnArchivados">
                    <i class="fa fa-folder"></i> Archivados
                </a>
                <a href="#" class="btn btn-success btnActualizar">
                    <i class="fa fa-refresh"></i> Actualizar
                </a>
            </div>

            <div data-type="pendiente" class="alert alert-blanco alertas">
                <span id="spanPendientes" class="counter" data-class="porRecibir">(0)</span>
                Por recibir
            </div>

            <div data-type="noRecibido" class="alert alert-otroRojo alertas">
                <span id="spanNoRecibidos" class="counter" data-class="sinRecepcion">(0)</span>
                Sin Recepción
            </div>

            <div data-type="recibido" class="alert alert-info alertas">
                <span id="spanRecibidos" class="counter" data-class="recibido">(0)</span>
                Recibidos
            </div>

            <div data-type="retrasado" class="alert alert-danger alertas">
                <span id="spanRetrasados" class="counter" data-class="retrasado">(0)</span>
                Retrasados
            </div>

            %{--<div data-type="jefe" class="alert alert-azul alertas">--}%
            %{--<span id="spanJefe" class="counter" data-class="jefe">(0)</span> Doc. env. jefe--}%
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
        %{--//bandeja--}%

        <div>
            <div class="modalTabelGray" id="bloqueo-salida"></div>

            <div id="bandeja"></div>
        </div>

        <script type="text/javascript">

            $("input").keyup(function (ev) {
                if (ev.keyCode == 13) {
                    var memorando = $("#memorando").val();
                    var asunto = $("#asunto").val();
                    var fecha = $("#fechaBusqueda_input").val();
                    var datos = "memorando=" + memorando + "&asunto=" + asunto + "&fecha=" + fecha

                    $.ajax({ type : "POST", url : "${g.createLink(controller: 'tramite3', action: 'busquedaBandeja')}",
                        data      : datos,
                        success   : function (msg) {
                            $("#bandeja").html(msg);

                        }
                    });
                }
            });

            var intervalBandeja;

            function cargarBandeja(band, datos) {
                if (!datos) {
                    datos = {};
                }
                if (band) {
                    openLoader();
                }
                $.ajax({
                    type    : "POST",
                    url     : "${g.createLink(controller: 'tramite3',action:'tablaBandejaEntradaDpto')}",
                    data    : datos,
                    success : function (msg) {
                        resetTimer();
                        $("#bandeja").html(msg);
                        if (band) {
                            closeLoader();
                            log("Datos actualizados", "success");
                        }
                        $(".counter").each(function () {
                            var clase = $(this).data("class");
                            var cant = $("tr." + clase).size();
                            $(this).text("(" + cant + ")");
                        });
                    }
                });
            }

            $(function () {
                //hace reload cada 5 min

                <g:if test="${bloqueo}">
                $("#bloqueo-salida").show()
                </g:if>

                intervalBandeja = setInterval(function () {
                    cargarBandeja();
                }, 1000 * 60 * 5);
                var id, codigo;

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
                        var $tr = $(e.target).parents("tr");
                        $tr.addClass("trHighlight");
                        id = $tr.data("id");
                        idPxt = $tr.attr("prtr");
                        codigo = $tr.attr("codigo");
                        archivo = $tr.attr("departamento") + "/" + $tr.attr("codigo")
                    }
                });

                var arbol = {
                    text   : 'Cadena del trámite',
                    icon   : "<i class='fa fa-sitemap'></i>",
                    action : function (e) {
                        location.href = '${createLink(controller: 'tramite3', action: 'arbolTramite')}/' + id + "?b=bed"
                    }
                };

                var contestar = {
                    text   : 'Contestar Documento',
                    icon   : "<i class='fa fa-external-link'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();
                        location.href = '${createLink(controller: 'tramite2', action: 'crearTramiteDep')}?padre=' + id +  "&pdt=" + idPxt;
                    }
                };

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
                            url     : '${createLink(controller: 'tramite' ,action: 'revisarConfidencial')}/' + id,
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
//                        var b = bootbox.dialog({
//                            id      : "dlgRecibido",
//                            title   : "Trámite a ser recibido",
//                            message : msg,
//                            buttons : {
//                                cancelar : {
//                                    label     : '<i class="fa fa-times"></i> Cancelar',
//                                    className : 'btn-danger',
//                                    callback  : function () {
//                                    }
//                                },
//                                recibir  : {
//                                    id        : 'btnRecibir',
//                                    label     : '<i class="fa fa-thumbs-o-up"></i> Recibir',
//                                    className : 'btn-success',
//                                    callback  : function () {
                        $.ajax({
                            type    : 'POST',
                            %{--url     : '${createLink(action: 'guardarRecibir')}/' + id,--}%
                            url     : '${createLink(action: 'recibirTramite')}/' + id + "?source=bed",
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
                        });
//                                    }
//                                }
//                            }
//                        })
//                            }//
//                        });
                    }
                };

                var seguimiento = {
                    text   : 'Seguimiento Trámite',
                    icon   : "<i class='fa fa-sitemap'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();
                        location.href = "${g.createLink(controller: 'tramite3', action: 'seguimientoTramite')}/" + id;
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
                                                    url     : '${createLink(controller:'tramite',action: 'archivar')}/' + idPxt,
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

                var observaciones = {
                    text   : 'Añadir observaciones al trámite',
                    icon   : "<i class='fa fa-eye'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();
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
                };

                var anexos = {
                    text   : 'Anexos',
                    icon   : "<i class='fa fa-paperclip'></i>",
                    action : function (e) {
                        location.href = '${createLink(controller: 'documentoTramite', action: 'verAnexos')}/' + id
                    }
                };

                context.attach(".porRecibir,.sinRecepcion", [
                    {
                        header : 'Acciones'
                    },
                    detalles,
                    arbol,
                    <g:if test="${Persona.get(session.usuario.id).puedeVer}">
//                    ver,
//                    seguimiento,
                    </g:if>
                    recibir
                ]);

                context.attach(".recibido,.retrasado", [
                    {
                        header : 'Acciones'
                    },
                    detalles,
                    arbol,
                    <g:if test="${Persona.get(session.usuario.id).puedeVer}">
//                    ver,
//                    seguimiento,
                    </g:if>
                    observaciones,
                    contestar
                    <g:if test="${Persona.get(session.usuario.id).puedeArchivar}">
                    ,
                    archivar
                    </g:if>
                ]);

                context.attach(".jefe", [
                    {
                        header : 'Acciones'
                    },
                    contestar,
                    detalles,
                    arbol,
//                    ver,
                    <g:if test="${Persona.get(session.usuario.id).puedeArchivar}">
                    archivar
                    </g:if>
                ]);

                context.attach(".conAnexo.jefe", [
                    {
                        header : 'Acciones'
                    },
                    contestar,
                    detalles,
                    arbol,
                    <g:if test="${Persona.get(session.usuario.id).puedeArchivar}">
                    archivar,
                    </g:if>
                    anexos
                ]);

                context.attach(".conAnexo.recibido, .conAnexo.retrasado", [
                    {
                        header : 'Acciones'
                    },
                    detalles,
                    arbol,
                    observaciones,
                    contestar,
                    <g:if test="${Persona.get(session.usuario.id).puedeArchivar}">
                    archivar,
                    </g:if>
                    anexos
                ]);

                context.attach(".conAnexo.porRecibir, .conAnexo.sinRecepcion", [
                    {
                        header : 'Acciones'
                    },
                    detalles,
                    arbol,
                    recibir,
                    anexos
                ]);

                $(".btnBuscar").click(function () {
                    $(".buscar").attr("hidden", false)
                });

                $(".btnActualizar").click(function () {

                    cargarBandeja();
                    clearInterval(intervalBandeja);
                    intervalBandeja = setInterval(function () {
                        openLoader();
                        cargarBandeja();
                        closeLoader()
                    }, 1000 * 60 * 5);
                    log('Tabla de trámites y alertas actualizadas!', "success");

                    return false;
                });

                $(".btnArchivados").click(function () {

                    location.href = '${createLink(controller: 'tramite', action: 'archivados')}?dpto=' + 'si';
                });

                cargarBandeja();
            });

            $(".btnSalir").click(function () {
                $(".buscar").attr("hidden", true);
                openLoader();
                cargarBandeja();
                closeLoader()
            });

            $(".btnBusqueda").click(function () {
                openLoader();
                var memorando = $("#memorando").val();
                var asunto = $("#asunto").val();
                var fecha = $("#fechaBusqueda_input").val();
                var datos = "memorando=" + memorando + "&asunto=" + asunto + "&fecha=" + fecha
                $.ajax({ type : "POST", url : "${g.createLink(controller: 'tramite3', action: 'busquedaBandeja')}",
                    data      : datos,
                    success   : function (msg) {

                        $("#bandeja").html(msg);
                        closeLoader();
                    }
                });
            });


        </script>
    </body>
</html>