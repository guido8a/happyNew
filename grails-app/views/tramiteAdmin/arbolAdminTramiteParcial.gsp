<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 28/03/17
  Time: 11:15
--%>
<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 4/30/14
  Time: 1:20 PM
--%>

<%@ page import="happy.seguridad.Persona; happy.tramites.Departamento" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Administración de trámite</title>
    <script src="${resource(dir: 'js/plugins/jstree-e22db21/dist', file: 'jstree.min.js')}"></script>
    <link href="${resource(dir: 'js/plugins/jstree-e22db21/dist/themes/default', file: 'style.min.css')}" rel="stylesheet">

    <style type="text/css">
    #jstree {
        background : #DEDEDE;
        overflow-y : auto;
        height     : 600px;
    }

    .esMio {
        background : #DFD7C3 !important;
    }
    </style>

</head>

<body>

<div class="btn-toolbar toolbar" style="margin-top: 10px !important">
    <div class="btn-group">

        <a href="javascript: history.go(-1)" class="btn btn-primary regresar">
            <i class="fa fa-arrow-left"></i> Regresar
        </a>

        <g:link controller="tramiteExport" action="arbolPdf" id="${tramite?.id}" class="btn btn-default">
            <i class="fa fa-print"></i> Imprimir
        </g:link>

    </div>
</div>

<g:if test="${tramite}">
    <div id="jstree">
        <util:renderHTML html="${html2}"/>
    </div>
</g:if>
<g:else>
    <div class="alert alert-danger">
        No ha seleccionado un trámite
    </div>
</g:else>

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
    function findAllHijos($node) {
        var str = "";
        $node.children("ul").children("li").each(function () {
            str += "<li>" + $(this).data("jstree").codigo + " (" + $(this).data("jstree").de + ", " + $(this).data("jstree").para + ")</li>";
            str += findAllHijos($(this));
        });
        return str;
    }

    function createContextMenu(node) {
        var nodeId = node.id;
        var $node = $("#" + nodeId);
        var $padre = $node.parent("ul").parent("li");

        var nodeTipo = $node.data("jstree").type;

        var tramiteId = $node.data("jstree").tramite;
        var tramiteCodigo = $node.data("jstree").codigo;
        var tramiteDe = $node.data("jstree").de;
        var tramitePara = $node.data("jstree").para;

        var padreId = $node.data("jstree").padre;

        var tramiteInfo = tramiteCodigo + " (" + tramiteDe + ", " + tramitePara + ")";

        var esCircular = $node.hasClass("CIR");
        var esCopia = nodeTipo.indexOf("copia") > -1;

        var estaAnulado = $node.hasClass("anulado");
        var estaArchivado = $node.hasClass("archivado");
        var estaEnviado = $node.hasClass("enviado");
        var estaRecibido = $node.hasClass("recibido");

        var tieneHijos = $node.hasClass("tieneHijos");
        var tienePadre = $node.hasClass("tienePadre");

        var duenio = $node.data("jstree").duenio;
        var esMio = $node.hasClass("esMio");
        var esExterno = $node.hasClass("externo");

        var padreEstaRecibido = $padre.hasClass("recibido");
        var padreEstaArchivado = $padre.hasClass("archivado");
        var padreEstaAnulado = $padre.hasClass("anulado");

        var puedeDesanular = true;

        var esAgregado = $node.hasClass("agregado");

        if (esCopia) {
            var para = $(".para.t" + tramiteId);
            if (para.hasClass("anulado")) {
                puedeDesanular = false;
            }
        }

        %{--console.log("tramite " + "${tramite?.de?.departamento}");--}%
        %{--console.log("tramite " + '${Persona.findAllByDepartamento(Departamento.get(tramite?.de?.departamento?.id))}')--}%
        %{--console.log("departamento " + ${Departamento.get(tramite?.deDepartamentoId)});--}%
        %{--console.log("personas " + ${Persona.findAllByDepartamento(Departamento.get(tramite?.deDepartamentoId))});--}%

//                console.log("-->" + tramiteDe)

//        console.log(padreEstaAnulado);

        var items = {};
        if (!nodeTipo.contains("tramite")) {
            items.detalles = {
                label  : "Detalles",
                icon   : "fa fa-search",
                action : function () {
                    $.ajax({
                        type    : 'POST',
                        url     : '${createLink(controller: 'tramite3', action: 'detalles')}',
                        data    : {
                            id : tramiteId
                        },
                        success : function (msg) {
                            $("#dialog-body").html(msg)
                        }
                    });
                    $("#dialog").modal("show")
                }
            };

            if (!estaAnulado && !estaArchivado) {
                if (esMio) {
//                        if (estaEnviado) {
                    %{--items.copia = {--}%
                    %{--separator_before : true,--}%
                    %{--label            : "Copia para",--}%
                    %{--icon             : "fa fa-files-o",--}%
                    %{--action           : function () {--}%
                    %{--$.ajax({--}%
                    %{--type    : "POST",--}%
                    %{--url     : "${createLink(controller: 'tramiteAdmin', action:'copiaParaLista_ajax')}",--}%
                    %{--data    : {--}%
                    %{--id : nodeId--}%
                    %{--},--}%
                    %{--success : function (msg) {--}%
                    %{--bootbox.dialog({--}%
                    %{--id      : "dlgCopiaPara",--}%
                    %{--title   : '<i class="fa fa-files-o"></i> Copia para',--}%
                    %{--class   : "long",--}%
                    %{--message : msg,--}%
                    %{--buttons : {--}%
                    %{--cancelar : {--}%
                    %{--label     : '<i class="fa fa-times"></i> Cancelar',--}%
                    %{--className : 'btn-danger',--}%
                    %{--callback  : function () {--}%
                    %{--}--}%
                    %{--},--}%
                    %{--enviar   : {--}%
                    %{--id        : 'btnEnviarCopia',--}%
                    %{--label     : '<i class="fa fa-check"></i> Enviar copias',--}%
                    %{--className : "btn-success",--}%
                    %{--callback  : function () {--}%
                    %{--var cc = "";--}%
                    %{--$("#ulSeleccionados li").not(".disabled").each(function () {--}%
                    %{--cc += $(this).data("id") + "_";--}%
                    %{--});--}%
                    %{--openLoader("Enviando copias");--}%
                    %{--$.ajax({--}%
                    %{--type    : "POST",--}%
                    %{--url     : "${createLink(controller: 'tramiteAdmin', action:'enviarCopias_ajax')}",--}%
                    %{--data    : {--}%
                    %{--id     : nodeId,--}%
                    %{--copias : cc--}%
                    %{--},--}%
                    %{--success : function (msg) {--}%
                    %{--var parts = msg.split("*");--}%
                    %{--if (parts[0] == 'OK') {--}%
                    %{--log("Copias enviadas exitosamente", 'success');--}%
                    %{--setTimeout(function () {--}%
                    %{--location.reload(true);--}%
                    %{--}, 500);--}%
                    %{--} else if (msg == 'NO') {--}%
                    %{--closeLoader();--}%
                    %{--log(parts[1], 'error');--}%
                    %{--}--}%
                    %{--}--}%
                    %{--});--}%
                    %{--}--}%
                    %{--}--}%
                    %{--}--}%
                    %{--});--}%
                    %{--}--}%
                    %{--});--}%
                    %{--}--}%
                    %{--};--}%
//                        }
                    %{--if (tienePadre) {--}%
                    %{--items.crearHermano = {--}%
                    %{--label  : "Agregar documento al trámite",--}%
                    %{--icon   : "fa fa-paste",--}%
                    %{--action : function () {--}%
                    %{--<g:if test="${session.usuario.esTriangulo()}">--}%
                    %{--location.href = '${createLink(controller: "tramite2", action: "crearTramiteDep")}?padre=' + padreId + '&hermano=' + tramiteId;--}%
                    %{--</g:if>--}%
                    %{--<g:else>--}%
                    %{--location.href = '${createLink(controller: "tramite", action: "crearTramite")}?padre=' + padreId + '&hermano=' + tramiteId;--}%
                    %{--</g:else>--}%
                    %{--}--}%
                    %{--};--}%
                    %{--}--}%
                }
                if (!tienePadre && ${session.usuario.getPuedeAsociar()}) {

                    items.agregarPadre = {
                        label  : "Asociar trámite",
                        icon   : "fa fa-gift",
                        action : function () {
                            var $container = $("<div>");
                            $container.append("<i class='fa fa-gift fa-3x pull-left text-shadow'></i>");
                            var $p = $("<p class='lead'>");
                            $p.html("Está por asociar un trámite al trámite <br/><strong>" + tramiteInfo + "</strong>");
                            $container.append($p);

                            var $alert = $("<div class='alert alert-info'>");
                            $alert.html("Para poder asociar un trámite a otro se deben cumplir las siguientes condiciones:");
                            var $ul = $("<ul>");
                            $ul.append($("<li>La fecha de creación del trámite " + tramiteCodigo + " debe ser posterior " +
                                "a la fecha de envío del trámite al que se lo quiere asociar.</li>"));
                            $ul.append($("<li>El creador del trámite " + tramiteCodigo + " debe ser el destinatario del " +
                                "trámite al que se lo quiere asociar.</li>"));
                            $ul.append($("<li>El trámite " + tramiteCodigo + " debe estar recibido. </li>"));
                            $ul.append($("<li>El trámite al que se quiere asociar el " + tramiteCodigo + " NO debe tener hijos. </li>"));
                            $alert.append($ul);
                            $container.append($alert);

                            var $row = $("<div class='row'>");
                            var $col = $("<div class='col-md-6'>");
                            $col.append("<label for='nuevoPadre'>Código trámite padre:</label>");
                            var $inputGroup = $("<div class='input-group'>");
                            var $input = $("<input type='text' name='nuevoPadre' id='nuevoPadre' class='form-control allCaps'/>");
                            $inputGroup.append($input);
                            var $span = $("<span class='input-group-btn'>");
                            var $btn = $("<a href='#' class='btn btn-azul' id='btnBuscar'><i class='fa fa-search'></i>&nbsp;</a>");
                            $span.append($btn);
                            $inputGroup.append($span);
                            $col.append($inputGroup);
                            $row.append($col);
                            $container.append($row);
                            var $res = $("<div>").css({
                                marginTop : 5,
                                maxHeight : 200,
                                overflow  : "auto"
                            });
                            $container.append($res);

                            function buscarAsociar() {
                                $res.html(spinner);
                                var np = $.trim($input.val());
                                $.ajax({
                                    type    : "POST",
                                    url     : "${createLink(action:'asociarTramite_ajax')}",
                                    data    : {
                                        codigo   : np,
                                        original : nodeId
                                    },
                                    success : function (msg) {
                                        $res.html(msg);
                                    }
                                });
                            }

                            $input.keyup(function (e) {
                                if (e.keyCode == 13) {
                                    buscarAsociar();
                                }
                            });

                            $btn.click(function () {
                                buscarAsociar();
                                return false;
                            });

                            bootbox.dialog({
                                id      : "dlgAsociar",
                                title   : '<i class="fa fa-gift"></i> Asociar Trámite',
                                message : $container,
                                buttons : {
                                    cancelar : {
                                        label     : '<i class="fa fa-times"></i> Aceptar',
                                        className : 'btn-default',
                                        callback  : function () {
                                        }
                                    }
//                                        asociar  : {
//                                            id        : 'btnAsociar',
//                                            label     : '<i class="fa fa-check"></i> Asociar',
//                                            className : "btn-success",
//                                            callback  : function () {
//
//                                            }
//                                        }
                                }
                            });
                        }
                    };
                }
                if (!esMio && !tienePadre && ${session.usuario.getPuedeAsociar()}) {
                    items.agregarPadre.separator_before = true;
                }

//                    if (!tieneHijos && estaRecibido) {
                %{--items.archivar = {--}%
                %{--separator_before : true,--}%
                %{--label            : "Archivar",--}%
                %{--icon             : "fa fa-folder-open-o",--}%
                %{--action           : function () {--}%
                %{--var msg = "<i class='fa fa-folder-open-o fa-3x pull-left text-warning text-shadow'></i>" +--}%
                %{--"<p class='lead'>El trámite <strong>" + tramiteInfo + "</strong> está por ser archivado.</p>" +--}%
                %{--"<label for='observacionArchivar'>Observaciones:</label>" +--}%
                %{--'<textarea id="observacionArchivar" style="resize: none; height: 150px;" ' +--}%
                %{--'class="form-control" maxlength="255" name="observacionArchivar"></textarea>';--}%
                %{--bootbox.dialog({--}%
                %{--id      : "dlgArchivar",--}%
                %{--title   : '<span class="text-warning"><i class="fa fa-folder-open-o"></i> Archivar Tramite</span>',--}%
                %{--message : msg,--}%
                %{--buttons : {--}%
                %{--cancelar : {--}%
                %{--label     : '<i class="fa fa-times"></i> Cancelar',--}%
                %{--className : 'btn-danger',--}%
                %{--callback  : function () {--}%
                %{--}--}%
                %{--},--}%
                %{--archivar : {--}%
                %{--id        : 'btnArchivar',--}%
                %{--label     : '<i class="fa fa-check"></i> Archivar',--}%
                %{--className : "btn-success",--}%
                %{--callback  : function () {--}%
                %{--openLoader("Archivando");--}%
                %{--$.ajax({--}%
                %{--type    : 'POST',--}%
                %{--url     : '${createLink(controller: "tramite", action: "archivar")}',--}%
                %{--data    : {--}%
                %{--id    : nodeId,--}%
                %{--texto : $("#observacionArchivar").val()--}%
                %{--},--}%
                %{--success : function (msg) {--}%
                %{--if (msg == 'ok') {--}%
                %{--log("Trámite archivado correctamente", 'success');--}%
                %{--setTimeout(function () {--}%
                %{--location.reload(true);--}%
                %{--}, 500);--}%
                %{--} else if (msg == 'no') {--}%
                %{--closeLoader();--}%
                %{--log("Error al archivar el trámite el trámite", 'error');--}%
                %{--}--}%
                %{--}--}%
                %{--});--}%
                %{--}--}%
                %{--}--}%
                %{--}--}%
                %{--});--}%
                %{--}--}%
                %{--};--}%
//                    }
            }
            if (!estaAnulado && !estaArchivado) {
                if (esCircular) {
                    if (estaEnviado) {

                        %{--items.anularCircular = {--}%
                        %{--label  : "Anular todo",--}%
                        %{--icon   : "fa fa-ban",--}%
                        %{--action : function () {--}%
                        %{--var hijosAnular = findAllHijos($node);--}%
                        %{--if (hijosAnular != "") {--}%
                        %{--hijosAnular = "<p>Se anularán todos los hijos asociados a la circular</p>"--}%
                        %{--}--}%

                        %{--$.ajax({--}%
                        %{--type    : "POST",--}%
                        %{--url     : "${createLink(controller: 'tramiteAdmin', action: 'dialogAdmin')}",--}%
                        %{--url     : "${createLink(controller: 'tramiteAdmin', action: 'dialogAnulados')}",--}%
                        %{--data    : {--}%
                        %{--id  : tramiteId,--}%
                        %{--msg : "<p class='lead'>El trámite <strong>" + tramiteInfo + "</strong> está por ser anulado.</p>" +--}%
                        %{--hijosAnular,--}%
                        %{--icon : "fa-ban"--}%
                        %{--},--}%
                        %{--success : function (msg) {--}%
                        %{--bootbox.dialog({--}%
                        %{--id      : "dlgAnular",--}%
                        %{--title   : '<span class="text-danger"><i class="fa fa-ban"></i> Anular Tramite</span>',--}%
                        %{--message : msg,--}%
                        %{--buttons : {--}%
                        %{--cancelar : {--}%
                        %{--label     : '<i class="fa fa-times"></i> Cancelar',--}%
                        %{--className : 'btn-danger',--}%
                        %{--callback  : function () {--}%
                        %{--}--}%
                        %{--},--}%
                        %{--anular   : {--}%
                        %{--id        : 'btnArchivar',--}%
                        %{--label     : '<i class="fa fa-check"></i> Anular',--}%
                        %{--className : "btn-success",--}%
                        %{--callback  : function () {--}%
                        %{--var $txt = $("#aut");--}%
                        %{--if (validaAutorizacion($txt)) {--}%
                        %{--openLoader("Anulando");--}%
                        %{--$.ajax({--}%
                        %{--type    : 'POST',--}%
                        %{--url     : '${createLink(controller: "tramiteAdmin", action: "anularCircular")}',--}%
                        %{--data    : {--}%
                        %{--id    : nodeId,--}%
                        %{--texto : $("#observacion").val(),--}%
                        %{--aut   : $txt.val()--}%
                        %{--},--}%
                        %{--success : function (msg) {--}%
                        %{--var parts = msg.split("*");--}%
                        %{--if (parts[0] == 'OK') {--}%
                        %{--log("Trámite anulado correctamente", 'success');--}%
                        %{--setTimeout(function () {--}%
                        %{--location.reload(true);--}%
                        %{--}, 500);--}%
                        %{--} else if (parts[0] == 'NO') {--}%
                        %{--closeLoader();--}%
                        %{--log("Error al anular el trámite!", 'error');--}%
                        %{--}--}%
                        %{--}--}%
                        %{--});--}%
                        %{--} else {--}%
                        %{--return false;--}%
                        %{--}--}%
                        %{--}--}%
                        %{--}--}%
                        %{--}--}%
                        %{--});--}%
                        %{--}--}%
                        %{--});--}%
                        %{--}--}%
                        %{--};--}%
                    }
                }
                //quitado el 21-07-2015
//                        if (estaEnviado) {
                items.anular = {
                    label  : "Anular",
                    icon   : "fa fa-ban",
                    action : function () {
                        var $parent = $node.parent().parent();
                        var hijosAnular = "";
                        if (nodeTipo.indexOf("copia") == -1) {
                            if ($parent.data("jstree").type == "tramitePrincipal") {
                                $parent.parent().children().each(function () {
                                    hijosAnular += findAllHijos($(this));
                                });
                            }
                        } else {
                            hijosAnular = findAllHijos($node);
                        }
                        if (hijosAnular != "") {
                            hijosAnular = "<p>Los siguientes trámites derivados también serán anulados:</p>" +
                                "<ul style='max-height:100px; overflow: auto;'>" + hijosAnular + "</ul>";
                        }
                        $.ajax({
                            type    : "POST",
                            %{--url     : "${createLink(controller: 'tramiteAdmin', action: 'dialogAdmin')}",--}%
                            url     : "${createLink(controller: 'tramiteAdmin', action: 'dialogAnulados')}",
                            data    : {
                                id   : tramiteId,
                                msg  : "<p class='lead'>El trámite <strong>" + tramiteInfo + "</strong> está por ser anulado.</p>" +
                                hijosAnular,
                                icon : "fa-ban"
                            },
                            success : function (msg) {
                                bootbox.dialog({
                                    id      : "dlgAnular",
                                    title   : '<span class="text-danger"><i class="fa fa-ban"></i> Anular Tramite</span>',
                                    message : msg,
                                    buttons : {
                                        cancelar : {
                                            label     : '<i class="fa fa-times"></i> Cancelar',
                                            className : 'btn-danger',
                                            callback  : function () {
                                            }
                                        },
                                        anular   : {
                                            id        : 'btnArchivar',
                                            label     : '<i class="fa fa-check"></i> Anular',
                                            className : "btn-success",
                                            callback  : function () {
                                                var $txt = $("#aut");
                                                if (validaAutorizacion($txt)) {
                                                    openLoader("Anulando");
                                                    $.ajax({
                                                        type    : 'POST',
                                                        url     : '${createLink(controller: "tramiteAdmin", action: "anular")}',
                                                        data    : {
                                                            id    : nodeId,
                                                            texto : $("#observacion").val(),
                                                            aut   : $txt.val()
                                                        },
                                                        success : function (msg) {
                                                            var parts = msg.split("*");
                                                            if (parts[0] == 'OK') {
                                                                log("Trámite anulado correctamente", 'success');
                                                                setTimeout(function () {
                                                                    location.reload(true);
                                                                }, 500);
                                                            } else if (parts[0] == 'NO') {
                                                                closeLoader();
                                                                log("Error al anular el trámite!", 'error');
                                                                setTimeout(function () {
                                                                    location.reload(true);
                                                                }, 600);
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    return false;
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                };
//                        }
            }
            if (!estaAnulado && !estaArchivado) {
                if (esExterno) {
                    items.externo = {
                        label  : "Cambiar estado",
                        icon   : "fa fa-exchange",
                        action : function () {
                            $.ajax({
                                type    : "POST",
                                url     : "${createLink(controller: 'tramiteAdmin', action: 'cambiarEstado')}",
                                data    : {
                                    id          : tramiteId,
                                    tramiteInfo : tramiteInfo
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
                                                            id     : tramiteId,
                                                            prtr   : nodeId,
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
                                                                setTimeout(function () {
                                                                    location.reload(true);
                                                                }, 700);
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
                    };
                }
            }
//                    console.log("anulado", estaAnulado, "tiene padre", tienePadre, "padre archivado", padreEstaArchivado, "padre recibido", padreEstaRecibido, "padre anulado", padreEstaAnulado);
//                    console.log("anulado", estaAnulado, "!tiene padre", !tienePadre, "padre archivado or padre recibido", padreEstaArchivado || padreEstaRecibido, "!padre anulado", !padreEstaAnulado);
//                    console.log("anulado", estaAnulado, "!tiene padre or (padre archivado or padre recibido)", !tienePadre || (padreEstaArchivado || padreEstaRecibido), "!padre anulado", !padreEstaAnulado);
//                    console.log("anulado", estaAnulado, "!tiene padre or ((padre archivado or padre recibido) and !padre anulado)", !tienePadre || ((padreEstaArchivado || padreEstaRecibido) && !padreEstaAnulado));
//                    console.log("anulado and (!tiene padre or ((padre archivado or padre recibido) and !padre anulado))", estaAnulado && (!tienePadre || ((padreEstaArchivado || padreEstaRecibido) && !padreEstaAnulado)));
            if (estaAnulado && ((!tienePadre || ((padreEstaArchivado || padreEstaRecibido) && !padreEstaAnulado)) || esAgregado)) {
                if (puedeDesanular) {
                    items.desAnular = {
                        separator_before : true,
                        label            : "Quitar anulado",
                        icon             : "fa fa-magic",
                        action           : function () {
                            $.ajax({
                                type    : "POST",
                                url     : "${createLink(controller: 'tramiteAdmin', action: 'dialogAdmin')}",
                                data    : {
                                    id   : tramiteId,
                                    copia: esCopia,
                                    prtr: nodeId,
                                    msg  : "<p class='lead'>Está por quitar el anulado del trámite<br/><strong>" + tramiteInfo + "</strong>.</p>",
                                    icon : "fa-magic"
                                },
                                success : function (msg) {
                                    bootbox.dialog({
                                        id      : "dlgDesanular",
                                        title   : '<i class="fa fa-magic"></i> Quitar anulado del Trámite',
                                        message : msg,
                                        buttons : {
                                            cancelar  : {
                                                label     : '<i class="fa fa-times"></i> Cancelar',
                                                className : 'btn-danger',
                                                callback  : function () {
                                                }
                                            },
                                            desanular : {
                                                id        : 'btnDesanular',
                                                label     : '<i class="fa fa-check"></i> Quitar anulado',
                                                className : "btn-success",
                                                callback  : function () {
                                                    var $txt = $("#aut");
                                                    if (validaAutorizacion($txt)) {
                                                        openLoader("Quitando el anulado");
                                                        $.ajax({
                                                            type    : 'POST',
                                                            url     : '${createLink(controller: "tramiteAdmin", action: "desanular")}',
                                                            data    : {
                                                                id    : nodeId,
                                                                texto : $("#observacion").val(),
                                                                aut   : $txt.val()
                                                            },
                                                            success : function (msg) {
                                                                openLoader();
                                                                closeLoader();
                                                                var parts = msg.split("*");
                                                                if (parts[0] == 'OK') {
                                                                    log("Quitado el anulado del trámite correctamente", 'success');
                                                                    setTimeout(function () {
                                                                        location.reload(true);
                                                                    }, 500);
                                                                } else if (parts[0] == 'NO') {
                                                                    closeLoader();
                                                                    log("Error al quitar el anulado del trámite", 'error');
                                                                    setTimeout(function () {
                                                                        location.reload(true);
                                                                    }, 500);
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        return false;
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        }
                        %{--action           : function () {--}%
                        %{--var msg = "<i class='fa fa-magic fa-3x pull-left text-danger text-shadow'></i>" +--}%
                        %{--"<p class='lead'>Está por quitar el anulado del trámite<br/><strong>" + tramiteInfo + "</strong>.</p>" +--}%
                        %{--'<div class="row">' +--}%
                        %{--'<div class="col-md-3"><strong>Solicitado por</strong></div>' +--}%
                        %{--'<div class="col-md-9">' +--}%
                        %{--'<input type="text" class="form-control" id="autDesanular"/>' +--}%
                        %{--'</div>' +--}%
                        %{--'</div>' +--}%
                        %{--"<label for='observacionDesanular'>Observaciones:</label>" +--}%
                        %{--'<textarea id="observacionDesanular" style="resize: none; height: 150px;" ' +--}%
                        %{--'class="form-control" maxlength="255" name="observacionDesanular"></textarea>';--}%
                        %{--bootbox.dialog({--}%
                        %{--id      : "dlgAnular",--}%
                        %{--title   : '<i class="fa fa-magic"></i> Quitar anulado del Trámite',--}%
                        %{--message : msg,--}%
                        %{--buttons : {--}%
                        %{--cancelar  : {--}%
                        %{--label     : '<i class="fa fa-times"></i> Cancelar',--}%
                        %{--className : 'btn-danger',--}%
                        %{--callback  : function () {--}%
                        %{--}--}%
                        %{--},--}%
                        %{--desanular : {--}%
                        %{--id        : 'btnDesanular',--}%
                        %{--label     : '<i class="fa fa-check"></i> Quitar anulado',--}%
                        %{--className : "btn-success",--}%
                        %{--callback  : function () {--}%
                        %{--var $txt = $("#autDesanular");--}%
                        %{--if (validaAutorizacion($txt)) {--}%
                        %{--openLoader("Procesando");--}%
                        %{--$.ajax({--}%
                        %{--type    : 'POST',--}%
                        %{--url     : '${createLink(controller: "tramiteAdmin", action: "desanular")}',--}%
                        %{--data    : {--}%
                        %{--id    : nodeId,--}%
                        %{--texto : $("#observacionDesanular").val(),--}%
                        %{--aut   : $txt.val()--}%
                        %{--},--}%
                        %{--success : function (msg) {--}%
                        %{--var parts = msg.split("*");--}%
                        %{--if (parts[0] == 'OK') {--}%
                        %{--log("Quitado el anulado del trámite correctamente", 'success');--}%
                        %{--setTimeout(function () {--}%
                        %{--location.reload(true);--}%
                        %{--}, 500);--}%
                        %{--} else if (parts[0] == 'NO') {--}%
                        %{--closeLoader();--}%
                        %{--log("Error al quitar el anulado del trámite el trámite", 'error')--}%
                        %{--}--}%
                        %{--}--}%
                        %{--});--}%
                        %{--} else {--}%
                        %{--return false;--}%
                        %{--}--}%
                        %{--}--}%
                        %{--}--}%
                        %{--}--}%
                        %{--});--}%
                        %{--}--}%
                    };
                }
            }
            if (estaArchivado && !estaAnulado) {
                items.desArchivar = {
                    separator_before : true,
                    label            : "Quitar archivado",
                    icon             : "fa fa-magic",
                    action           : function () {
                        $.ajax({
                            type    : "POST",
                            url     : "${createLink(controller: 'tramiteAdmin', action: 'dialogAdmin')}",
                            data    : {
                                id   : tramiteId,
                                msg  : "<p class='lead'>Está por quitar el archivado del trámite<br/><strong>" + tramiteInfo + "</strong>.</p>",
                                icon : "fa-magic"
                            },
                            success : function (msg) {
                                bootbox.dialog({
                                    id      : "dlgArchivar",
                                    title   : '<i class="fa fa-magic"></i> Quitar archivado del Trámite',
                                    message : msg,
                                    buttons : {
                                        cancelar    : {
                                            label     : '<i class="fa fa-times"></i> Cancelar',
                                            className : 'btn-danger',
                                            callback  : function () {
                                            }
                                        },
                                        desarchivar : {
                                            id        : 'btnArchivar',
                                            label     : '<i class="fa fa-check"></i> Quitar archivado',
                                            className : "btn-success",
                                            callback  : function () {
                                                var $txt = $("#aut");
                                                if (validaAutorizacion($txt)) {
                                                    openLoader("Quitando el archivado");
                                                    $.ajax({
                                                        type    : 'POST',
                                                        url     : '${createLink(controller: "tramiteAdmin", action: "desarchivar")}',
                                                        data    : {
                                                            id    : nodeId,
                                                            texto : $("#observacion").val(),
                                                            aut   : $txt.val()
                                                        },
                                                        success : function (msg) {
                                                            openLoader();
                                                            closeLoader();
                                                            var parts = msg.split("*");
                                                            if (parts[0] == 'OK') {
                                                                log("Quitado el archivado del trámite correctamente", 'success');
                                                                setTimeout(function () {
                                                                    location.reload(true);
                                                                }, 500);
                                                            } else if (parts[0] == 'NO') {
                                                                closeLoader();
                                                                log("Error al quitar el archivado del trámite", 'error');
                                                                setTimeout(function () {
                                                                    location.reload(true);
                                                                }, 500);
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    return false;
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                };
            }
            if (estaRecibido && !estaAnulado && !estaArchivado && !tieneHijos) {
                items.desRecibir = {
                    separator_before : true,
                    label            : "Quitar recibido",
                    icon             : "fa fa-magic",
                    action           : function () {
                        $.ajax({
                            type    : "POST",
                            %{--url     : "${createLink(controller: 'tramiteAdmin', action: 'dialogAdmin')}",--}%
                            url     : "${createLink(controller: 'tramiteAdmin', action: 'dialogAnulados')}",
                            data    : {
                                id   : tramiteId,
                                msg  : "<p class='lead'>Está por quitar el recibido del trámite<br/><strong>" + tramiteInfo + "</strong>.</p>",
                                icon : "fa-magic"
                            },
                            success : function (msg) {
                                bootbox.dialog({
                                    id      : "dlgDesrecibir",
                                    title   : '<i class="fa fa-magic"></i> Quitar recibido del Trámite',
                                    message : msg,
                                    buttons : {
                                        cancelar   : {
                                            label     : '<i class="fa fa-times"></i> Cancelar',
                                            className : 'btn-danger',
                                            callback  : function () {
                                            }
                                        },
                                        desrecibir : {
                                            id        : 'btnDesrecibir',
                                            label     : '<i class="fa fa-check"></i> Quitar recibido',
                                            className : "btn-success",
                                            callback  : function () {
                                                var $txt = $("#aut");
                                                if (validaAutorizacion($txt)) {
                                                    openLoader("Quitando el recibido");
                                                    $.ajax({
                                                        type    : 'POST',
                                                        url     : '${createLink(controller: "tramiteAdmin", action: "desrecibir")}',
                                                        data    : {
                                                            id    : nodeId,
                                                            texto : $("#observacion").val(),
                                                            aut   : $txt.val()
                                                        },
                                                        success : function (msg) {
                                                            var parts = msg.split("*");
                                                            if (parts[0] == 'OK') {
                                                                log("Quitado el recibido del trámite correctamente", 'success');
                                                                setTimeout(function () {
                                                                    location.reload(true);
                                                                }, 500);
                                                            } else if (parts[0] == 'NO') {
                                                                log("Error al quitar el recibido del trámite : " + parts[1], 'error');
                                                                closeLoader();
                                                                setTimeout(function () {
                                                                    location.reload(true);
                                                                }, 500);

                                                            }
                                                        }
                                                    });
                                                } else {
                                                    return false;
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                };
            }

            if(!tieneHijos && !estaArchivado && !estaAnulado && estaRecibido){
                items.archivar = {
                    separator_before : true,
                    label            : "Archivar",
                    icon             : "fa fa-folder",
                    action           : function () {
                        $.ajax({
                            type    : "POST",
                            %{--url     : "${createLink(controller: 'tramiteAdmin', action: 'dialogAdmin')}",--}%
                            url     : "${createLink(controller: 'tramiteAdmin', action: 'dialogAnulados')}",
                            data    : {
                                id   : tramiteId,
                                msg  : "<p class='lead'>Está por archivar el trámite<br/><strong>" + tramiteInfo + "</strong>.</p>",
                                icon : "fa-magic"
                            },
                            success : function (msg) {
                                bootbox.dialog({
                                    id      : "dlgDesrecibir",
                                    title   : '<i class="fa fa-magic"></i> Archivar Trámite',
                                    message : msg,
                                    buttons : {
                                        cancelar   : {
                                            label     : '<i class="fa fa-times"></i> Cancelar',
                                            className : 'btn-danger',
                                            callback  : function () {
                                            }
                                        },
                                        desrecibir : {
                                            id        : 'btnDesrecibir',
                                            label     : '<i class="fa fa-check"></i> Archivar trámite',
                                            className : "btn-success",
                                            callback  : function () {
                                                var $txt = $("#aut");
                                                if (validaAutorizacion($txt)) {
                                                    openLoader("Archivando");
                                                    $.ajax({
                                                        type    : 'POST',
                                                        url     : '${createLink(controller: "tramite", action: "archivar")}',
                                                        data    : {
                                                            id    : nodeId,
                                                            texto : $("#observacion").val(),
                                                            aut   : $txt.val()
                                                        },
                                                        success : function (msg) {
                                                            if (msg == 'ok') {
                                                                log("Archivado correctamente", 'success');
                                                                setTimeout(function () {
                                                                    location.reload(true);
                                                                }, 500);
                                                            } else {
                                                                log("Error al archivar el trámite : " + parts[1], 'error');
                                                                closeLoader();
                                                                setTimeout(function () {
                                                                    location.reload(true);
                                                                }, 500);

                                                            }
                                                        }
                                                    });
                                                } else {
                                                    return false;
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                };
            }

        }
        return items
    }

    $(function () {
        $(".regresar").click(function () {
            history.go(-1)
        });

        $('#jstree').jstree({
            plugins     : ["types", "state", "contextmenu", "wholerow", "search"],
            core        : {
                multiple       : false,
                check_callback : true,
                themes         : {
                    variant : "small",
                    dots    : true,
                    stripes : true
                }
            },
            state       : {
                key : "tramiteAdmin"
            },
            contextmenu : {
                show_at_node : false,
                items        : createContextMenu
            },
            types       : {
                tramitePrincipal : {
                    icon : "fa fa-file text-success"
                },
                tramite          : {
                    icon : "fa fa-file text-info"
                },
                para             : {
                    icon : "fa fa-file-o"
                },
                paraEnviado      : {
                    icon : "fa fa-file-o text-info"
                },
                paraArchivado    : {
                    icon : "fa fa-file-o text-warning"
                },
                paraAnulado      : {
                    icon : "fa fa-ban text-muted"
                },
                paraRecibido     : {
                    icon : "fa fa-file-o text-success"
                },

                copia          : {
                    icon : "fa fa-files-o"
                },
                copiaEnviado   : {
                    icon : "fa fa-files-o text-info"
                },
                copiaArchivado : {
                    icon : "fa fa-files-o text-warning"
                },
                copiaAnulado   : {
                    icon : "fa fa-ban text-muted"
                },
                copiaRecibido  : {
                    icon : "fa fa-files-o text-success"
                }
            }
        });
    });
</script>

</body>
</html>