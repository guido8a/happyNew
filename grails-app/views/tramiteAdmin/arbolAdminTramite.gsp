<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 4/30/14
  Time: 1:20 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
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
        </style>

    </head>

    <body>

        <div class="btn-toolbar toolbar" style="margin-top: 10px !important">
            <div class="btn-group">
                <g:if test="${url}">
                    <a href="${url}" class="btn btn-primary btnBuscar">
                        <i class="fa fa-arrow-left"></i> Regresar
                    </a>
                </g:if>
            </div>
        </div>

        <div id="jstree">
            <util:renderHTML html="${html2}"/>
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
            function createContextMenu(node) {
                var nodeId = node.id;
                var $node = $("#" + nodeId);
                var tramiteId = $node.data("jstree").tramite;
                var tramiteCodigo = $node.data("jstree").codigo;

                var estaAnulado = $node.hasClass("anulado");
                var estaArchivado = $node.hasClass("archivado");
                var estaRecibido = $node.hasClass("recibido");

                var duenio = $node.data("jstree").duenio;
                var soyDuenio = duenio.toString() == "${session.usuario.id}";

                var items = {
                    detalles : {
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
                    }
                };

                if (!estaAnulado && !estaArchivado) {
                    if (soyDuenio) {
                        items.copia = {
                            separator_before : true,
                            label            : "Copia para",
                            icon             : "fa fa-files-o",
                            action           : function () {

                            }
                        };
                        items.crearHermano = {
                            label  : "Agregar documento al trámite",
                            icon   : "fa fa-paste",
                            action : function () {

                            }
                        };
                    }
                    items.agregarPadre = {
                        label  : "Asociar trámite",
                        icon   : "fa fa-gift",
                        action : function () {

                        }
                    };
                    if (!soyDuenio) {
                        items.agregarPadre.separator_before = true;
                    }
                    items.archivar = {
                        separator_before : true,
                        label            : "Archivar",
                        icon             : "fa fa-folder-open-o",
                        action           : function () {
                            $.ajax({
                                type    : "POST",
                                url     : "${createLink(controller: 'tramite', action: "revisarHijos")}",
                                data    : {
                                    id   : nodeId,
                                    tipo : "archivar"
                                },
                                success : function (msg) {
                                    var b = bootbox.dialog({
                                        id      : "dlgArchivar",
                                        title   : '<span class="text-warning"><i class="fa fa-folder-open-o"></i> Archivar Tramite</span>',
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
                                                        url     : '${createLink(controller: "tramite", action: "archivar")}/' + nodeId,
                                                        data    : {
                                                            texto : $("#observacionArchivar").val()
                                                        },
                                                        success : function (msg) {
                                                            openLoader();
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
                                    });
                                    b.find(".modal-header").addClass("bg-warning");
                                }
                            });
                        }
                    };
                    items.anular = {
                        label  : "Anular",
                        icon   : "fa fa-ban",
                        action : function () {

                        }
                    };
                }
                if (estaAnulado) {
                    items.desAnular = {
                        separator_before : true,
                        label            : "Quitar anulado",
                        icon             : "fa fa-magic",
                        action           : function () {

                        }
                    };
                }
                if (estaArchivado) {
                    items.desArchivar = {
                        separator_before : true,
                        label            : "Quitar archivado",
                        icon             : "fa fa-magic",
                        action           : function () {

                        }
                    };
                }
                if (estaRecibido && !estaAnulado && !estaArchivado) {
                    items.desRecibir = {
                        separator_before : true,
                        label            : "Quitar recibido",
                        icon             : "fa fa-magic",
                        action           : function () {
                            var msg = "<i class='fa fa-magic fa-3x pull-left text-danger text-shadow'></i>" +
                                      "<p>¿Está seguro que desea quitar el recibido del trámite " + tramiteCodigo + "? Esta acción no se puede deshacer.</p>";
                            bootbox.dialog({
                                id      : "dlgArchivar",
                                title   : 'Quitar recibido del Tramite',
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
                                        label     : '<i class="fa fa-check"></i> Quitar recibido',
                                        className : "btn-success",
                                        callback  : function () {

                                            $.ajax({
                                                type    : 'POST',
                                                url     : '${createLink(controller: "tramiteAdmin", action: "desrecibir")}',
                                                data    : {
                                                    id    : nodeId,
                                                    texto : $("#observacionArchivar").val()
                                                },
                                                success : function (msg) {
                                                    openLoader();
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
                            });
                        }
                    };
                }

                return items
            }

            $(function () {
                $('#jstree').jstree({
                    plugins     : [ "types", "state", "contextmenu", "wholerow" , "search"],
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
                        key : "tramites"
                    },
                    contextmenu : {
                        show_at_node : false,
                        items        : createContextMenu
                    },
                    types       : {
                        para          : {
                            icon : "fa fa-file-o"
                        },
                        paraEnviado   : {
                            icon : "fa fa-file-o text-info"
                        },
                        paraArchivado : {
                            icon : "fa fa-file-o text-warning"
                        },
                        paraAnulado   : {
                            icon : "fa fa-file-o text-danger"
                        },
                        paraRecibido  : {
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
                            icon : "fa fa-files-o text-danger"
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