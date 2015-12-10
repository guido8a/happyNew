<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 3/17/14
  Time: 3:13 PM
--%>

<%@ page import="happy.tramites.Departamento" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Departamentos</title>

    <script src="${resource(dir: 'js/plugins/jstree-e22db21/dist', file: 'jstree.min.js')}"></script>
    <link href="${resource(dir: 'js/plugins/jstree-e22db21/dist/themes/default', file: 'style.min.css')}" rel="stylesheet">

    <style type="text/css">

    #list-cuenta {
        width : 950px;
    }

    #tree {
        background : #DEDEDE;
        overflow-y : auto;
        height     : 600px;
    }

    .jstree-search {
        color : #5F87B2 !important;
    }

    .leyenda {
        background    : #ddd;
        border        : solid 1px #aaa;
        padding-left  : 5px;
        padding-right : 5px;
    }
    </style>

</head>

<body>
<g:set var="iconActivar" value="fa-hdd-o"/>
<g:set var="iconDesactivar" value="fa-power-off"/>

<div id="list-cuenta">

    <!-- botones -->
    <div class="btn-toolbar toolbar">
        %{--
                        <div class="btn-group">
                            <g:link controller="inicio" action="parametros" class="btn btn-default">
                                <i class="fa fa-arrow-left"></i> Regresar
                            </g:link>
                        </div>
        --}%
        <div class="btn-group" style="margin-top: 4px;">
            <p style="font-size: 18px; font-weight: bold; margin-right: 40px;">Reportes del Sistema</p>
        </div>

        <div class="btn-group" style="margin-top: 4px;">
            <g:link action="arbolReportes" params="[sort: 'nombre']" class="btn btn-sm btn-info">
                <i class="fa fa-sort-alpha-asc"></i> Ordenar por nombre
            </g:link>
            <g:link action="arbolReportes" params="[sort: 'apellido']" class="btn btn-sm btn-info">
                <i class="fa fa-sort-alpha-asc"></i> Ordenar por apellido
            </g:link>
        </div>

        <div class="btn-group col-md-3" style="margin-top: 4px;">
            <div class="input-group">
                <g:textField name="search" class="form-control input-sm"/>
                <span class="input-group-btn">
                    <a href="#" id="btnSearch" class="btn btn-sm btn-info" type="button">
                        <i class="fa fa-search"></i>&nbsp;
                    </a>
                </span>
            </div><!-- /input-group -->
        </div>

        <div class="btn-group pull-right ui-corner-all leyenda">
            <i class="fa fa-user text-info"></i> Usuario activo&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <i class="fa fa-user text-warning"></i> Jefe<br/>
            <i class="fa fa-user text-muted"></i> Usuario inactivo&nbsp;&nbsp;&nbsp;
            <i class="fa fa-user text-danger"></i> Director<br/>
        </div>
    </div>

    <div id="loading" class="text-center">
        <p>
            Cargando los departamentos
        </p>

        <p>
            <img src="${resource(dir: 'images/spinners', file: 'loading_new.GIF')}" alt='Cargando...'/>
        </p>

        <p>
            Por favor espere
        </p>
    </div>

    <div id="tree" class="hide">

    </div>
</div>

<elm:select name="selDptoOrig" from="${Departamento.findAllByActivo(1, [sort: 'descripcion'])}"
            optionKey="id" optionValue="descripcion" optionClass="id" class="form-control hide"/>

<!-- Modal -->
<div class="modal fade" id="modalFechas" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="modalFecha_title"></h4>
            </div>

            <div class="modal-body">
                <form class="form-horizontal" role="form" id="formFechas">
                    <div class="row">
                        <div class="col-md-2">
                            <p class="form-control-static">
                                Desde
                            </p>
                        </div>

                        <div class="col-md-4" style="z-index: 1500">
                            <elm:datepicker name="desde" class="form-control required"/>
                        </div>

                        <div class="col-md-2">
                            <p class="form-control-static">
                                Hasta
                            </p>
                        </div>

                        <div class="col-md-4" style="z-index: 1500">
                            <elm:datepicker name="hasta" class="form-control required" maxDate="+0"/>
                        </div>
                    </div>
                </form>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
                <button type="button" class="btn btn-primary" id="btnPrint"><i class="fa fa-print"></i> Generar
                </button>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">

    var index = 0;

    var $btnCloseModal = $('<button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>');
    var $btnSave = $('<button type="button" class="btn btn-success"><i class="fa fa-save"></i> Guardar</button>');

    function createContextMenu(node) {
        var nodeStrId = node.id;
        var $node = $("#" + nodeStrId);
        var nodeId = nodeStrId.split("_")[1];
        var nodeType = $node.data("jstree").type;
        var $parent = $node.parent().parent();
        var parentStrId = $parent.attr("id");
        var parentId = parentStrId.split("_")[1];

        var nodeHasChildren = $node.hasClass("hasChildren");
        var nodeOcupado = $node.hasClass("ocupado");

        var nodeTramites = $node.data("tramites");

        var items = {};

        if (nodeType != "root" && !nodeType.match("inactivo") && !nodeType.match("Inactivo")) {
            %{--console.log("${session.usuario.puedeJefe}", "${session.usuario.puedeDirector}", "${session.usuario.departamentoId}");--}%
            %{--if (("${session.usuario.puedeJefe}" == "true" && "${session.usuario.departamentoId}" == nodeId.toString()) ||--}%
            %{--"${session.usuario.puedeDirector}" == "true") {--}%
            items.retrasadosWeb = {
                %{--label  : "Trámites retrasados",--}%
                %{--icon   : " fa fa-globe",--}%
                %{--action : function (e, e2) {--}%
                %{--if (nodeType.match("padre") || nodeType.match("hijo")) {--}%
                %{--location.href = "${g.createLink(controller: 'retrasadosWeb',action: 'reporteRetrasadosConsolidado')}?dpto=" + nodeId;--}%
                %{--} else {--}%
                %{--location.href = "${g.createLink(controller: 'retrasadosWeb',action: 'reporteRetrasadosConsolidado')}?prsn=" + nodeId;--}%
                %{--}--}%
                %{--}--}%

                label   : "Documentos retrasados",
                icon    : "fa fa-globe",
                submenu : {
                    pdf : {
                        label   : "PDF",
                        icon    : "fa fa-file-pdf-o",
                        action : function () {
                            if (nodeType.match("padre") || nodeType.match("hijo")) {
                                location.href = "${g.createLink(controller: 'retrasados',action: 'reporteRetrasadosArbol')}/" + nodeId + "?tipo=dpto";
                            }
                            else {
                                location.href = "${g.createLink(controller: 'retrasados',action: 'reporteRetrasadosDetalle')}/" + nodeId + "?tipo=prsn&dpto=" + parentId;
                            }
                        }
                    },
                    xls : {
                        label   : "EXCEL",
                        icon   : "fa fa-file-excel-o",
                        action : function () {



                            if (nodeType.match("padre") || nodeType.match("hijo")) {
                                location.href = "${g.createLink(controller: 'retrasadosExcel',action: 'reporteRetrasadosArbolExcel')}/" + nodeId
                            } else {
                                location.href = "${g.createLink(controller: 'retrasadosExcel',action: 'reporteRetrasadosDetalle')}/" + nodeId
                            }
                        }
                    }
                }

            };

            items.documentos = {
                label   : "Documentos generados",
                icon    : "fa fa-file-pdf-o",
                submenu : {
                    pdf : {
                        label   : "PDF",
                        icon    : "fa fa-file-pdf-o",
                        action : function () {
                            $('#modalFechas').modal('show');
                            $("#btnPrint").unbind("click").click(function () {
                                if ($("#formFechas").valid()) {
                                    if (nodeType.match("padre") || nodeType.match("hijo")) {
                                        location.href = "${g.createLink(controller: 'retrasados',action: 'reporteGeneradosArbol')}/" +
                                        nodeId + "?desde=" + $("#desde_input").val() + "&hasta=" + $("#hasta_input").val() + "&tipo=dpto";
                                    } else {
                                        location.href = "${g.createLink(controller: 'documentosGenerados',action: 'reporteDetalladoPdf')}/" +
                                        nodeId + "?desde=" + $("#desde_input").val() + "&hasta=" + $("#hasta_input").val() + "&tipo=prsn&dpto=" + parentId;
                                    }
                                    $('#modalFechas').modal('hide');
                                }
                            });
                        }




                        %{--submenu : {--}%
                        %{--detallado   : {--}%
                        %{--label  : "Detallado",--}%
                        %{--icon   : "fa fa-files-o",--}%
                        %{--action : function () {--}%
                        %{--$('#modalFechas').modal('show');--}%
                        %{--$("#btnPrint").unbind("click").click(function () {--}%
                        %{--if ($("#formFechas").valid()) {--}%
                        %{--if (nodeType.match("padre") || nodeType.match("hijo")) {--}%
                        %{--location.href = "${g.createLink(controller: 'documentosGenerados',action: 'reporteDetalladoPdf')}/" + nodeId + "?desde=" + $("#desde_input").val() + "&hasta=" + $("#hasta_input").val() + "&tipo=dpto";--}%
                        %{--} else {--}%
                        %{--location.href = "${g.createLink(controller: 'documentosGenerados',action: 'reporteDetalladoPdf')}/" + nodeId + "?desde=" + $("#desde_input").val() + "&hasta=" + $("#hasta_input").val() + "&tipo=prsn&dpto=" + parentId;--}%
                        %{--}--}%
                        %{--$('#modalFechas').modal('hide');--}%
                        %{--}--}%
                        %{--});--}%
                        %{--}--}%
                        %{--},--}%
                        %{--noDetallado : {--}%
                        %{--label  : "Resumen",--}%
                        %{--icon   : "fa fa-files-o",--}%
                        %{--action : function () {--}%
                        %{--$('#modalFechas').modal('show');--}%
                        %{--$("#btnPrint").unbind("click").click(function () {--}%
                        %{--if ($("#formFechas").valid()) {--}%
                        %{--if (nodeType.match("padre") || nodeType.match("hijo")) {--}%
                        %{--location.href = "${g.createLink(controller: 'documentosGenerados',action: 'reporteGeneralPdf')}/" + nodeId + "?desde=" + $("#desde_input").val() + "&hasta=" + $("#hasta_input").val() + "&tipo=dpto";--}%
                        %{--} else {--}%
                        %{--location.href = "${g.createLink(controller: 'documentosGenerados',action: 'reporteGeneralPdf')}/" + nodeId + "?desde=" + $("#desde_input").val() + "&hasta=" + $("#hasta_input").val() + "&tipo=prsn&dpto=" + parentId;--}%
                        %{--}--}%
                        %{--$('#modalFechas').modal('hide');--}%
                        %{--}--}%
                        %{--});--}%
                        %{--}--}%
                        %{--}--}%
                        %{--}--}%
                    },
                    xls : {
                        label   : "EXCEL",
                        icon   : "fa fa-file-excel-o",
                        action : function () {
                                    $('#modalFechas').modal('show');
                                    $("#btnPrint").unbind("click").click(function () {
                                        if ($("#formFechas").valid()) {
//
                            if (nodeType.match("padre") || nodeType.match("hijo")) {
                                location.href = "${g.createLink(controller: 'retrasadosExcel',action: 'reporteGeneradosArbolExcel')}/" + nodeId + "?desde=" + $("#desde_input").val() + "&hasta=" + $("#hasta_input").val();
                            } else {
                                location.href = "${g.createLink(controller: 'documentosGenerados',action: 'reporteDetalladoXlsx')}/" + nodeId + "?desde=" + $("#desde_input").val() + "&hasta=" + $("#hasta_input").val() + "&tipo=prsn&dpto=" + parentId;
                            }

                                          $('#modalFechas').modal('hide');
                                        }
                                    });
                        }
                        %{--submenu : {--}%
                        %{--detallado   : {--}%
                        %{--label  : "Detallado",--}%
                        %{--icon   : "fa fa-files-o",--}%
                        %{--action : function () {--}%
                        %{--$('#modalFechas').modal('show');--}%
                        %{--$("#btnPrint").unbind("click").click(function () {--}%
                        %{--if ($("#formFechas").valid()) {--}%
                        %{--if (nodeType.match("padre") || nodeType.match("hijo")) {--}%
                        %{--location.href = "${g.createLink(controller: 'documentosGenerados',action: 'reporteDetalladoXlsx')}/" + nodeId + "?desde=" + $("#desde_input").val() + "&hasta=" + $("#hasta_input").val() + "&tipo=dpto";--}%
                        %{--} else {--}%
                        %{--location.href = "${g.createLink(controller: 'documentosGenerados',action: 'reporteDetalladoXlsx')}/" + nodeId + "?desde=" + $("#desde_input").val() + "&hasta=" + $("#hasta_input").val() + "&tipo=prsn&dpto=" + parentId;--}%
                        %{--}--}%
                        %{--$('#modalFechas').modal('hide');--}%
                        %{--}--}%
                        %{--});--}%
                        %{--}--}%
                        %{--},--}%
                        %{--noDetallado : {--}%
                        %{--label  : "Resumen",--}%
                        %{--icon   : "fa fa-files-o",--}%
                        %{--action : function () {--}%
                        %{--$('#modalFechas').modal('show');--}%
                        %{--$("#btnPrint").unbind("click").click(function () {--}%
                        %{--if ($("#formFechas").valid()) {--}%
                        %{--if (nodeType.match("padre") || nodeType.match("hijo")) {--}%
                        %{--location.href = "${g.createLink(controller: 'documentosGenerados',action: 'reporteGeneralXlsx')}/" + nodeId + "?desde=" + $("#desde_input").val() + "&hasta=" + $("#hasta_input").val() + "&tipo=dpto";--}%
                        %{--} else {--}%
                        %{--location.href = "${g.createLink(controller: 'documentosGenerados',action: 'reporteGeneralXlsx')}/" + nodeId + "?desde=" + $("#desde_input").val() + "&hasta=" + $("#hasta_input").val() + "&tipo=prsn&dpto=" + parentId;--}%
                        %{--}--}%
                        %{--$('#modalFechas').modal('hide');--}%
                        %{--}--}%
                        %{--});--}%
                        %{--}--}%
                        %{--}--}%
                        %{--}--}%
                    }
                }
            };
//                    }

            if (!nodeType.match("usuario") && !nodeType.match("jefe")) {
                items.gestion = {
                    label   : "Gestión de trámites",
                    icon    : "fa fa-file-text",
                    submenu : {
                        pdf : {
                            label  : "PDF",
                            icon   : "fa fa-file-pdf-o",
                            action : function () {
                                $('#modalFechas').modal('show');
                                $("#btnPrint").unbind("click").click(function () {
                                    if ($("#formFechas").valid()) {
                                        location.href = "${g.createLink(controller: 'reporteGestion',action: 'reporteGestion5')}/" + nodeId + "?desde=" + $("#desde_input").val() + "&hasta=" + $("#hasta_input").val();
                                        $('#modalFechas').modal('hide');
                                    }
                                });
                            }
                        },
                        xls : {
                            label  : "Excel",
                            icon   : "fa fa-file-excel-o",
                            action : function () {
                                $('#modalFechas').modal('show');
                                $("#btnPrint").unbind("click").click(function () {
                                    if ($("#formFechas").valid()) {
                                        location.href = "${g.createLink(controller: 'reporteGestionExcel',action: 'reporteGestion')}/" + nodeId + "?desde=" + $("#desde_input").val() + "&hasta=" + $("#hasta_input").val();
                                        $('#modalFechas').modal('hide');
                                    }
                                });
                            }
                        }
                    }
                };
            }
        }

        return items;
    }

    $(function () {

        $("#formFechas").validate({
            errorClass     : "help-block",
            errorPlacement : function (error, element) {
                if (element.parent().hasClass("input-group")) {
                    error.insertAfter(element.parent());
                } else {
                    error.insertAfter(element);
                }
                element.parents(".grupo").addClass('has-error');
            },
            success        : function (label) {
                label.parents(".grupo").removeClass('has-error');
            }
        });

        $("#modalFechas").modal({
            show : false
        });

        $('#tree').on("loaded.jstree", function () {
            $("#loading").hide();
            $("#tree").removeClass("hide").show();
        }).on("select_node.jstree", function (node, selected, event) {
//                    $('#tree').jstree('toggle_node', selected.selected[0]);
        }).jstree({
            plugins     : ["types", "state", "contextmenu", "wholerow", "search"],
            core        : {
                multiple       : false,
                check_callback : true,
                themes         : {
                    variant : "small",
                    dots    : true,
                    stripes : true
                },
                data           : {
                    async : false,
                    url   : '${createLink(action:"loadTreePart")}',
                    data  : function (node) {
                        return {
                            id    : node.id,
                            sort  : "${params.sort?:'apellido'}",
                            order : "${params.order?:'asc'}"
                        };
                    }
                }
            },
            contextmenu : {
                show_at_node : false,
                items        : createContextMenu
            },
            state       : {
                key : "departamentosReportes"
            },
            search      : {
                fuzzy             : false,
                show_only_matches : true,
                ajax              : {
                    url     : "${createLink(action:'arbolSearch_ajax')}",
                    success : function (msg) {
                        var json = $.parseJSON(msg);
                        $.each(json, function (i, obj) {
                            $('#tree').jstree("open_node", obj);
                        });
                    }
                }
            },
            types       : {
                root                      : {
                    icon : "fa fa-folder text-warning"
                },
                padreActivo               : {
                    icon : "fa fa-building-o text-info"
                },
                padreInactivo             : {
                    icon : "fa fa-building-o text-muted"
                },
                padreExternoActivo        : {
                    icon : "fa fa-paper-plane text-info"
                },
                padreExternoInactivo      : {
                    icon : "fa fa-paper-plane text-muted"
                },
                hijoActivo                : {
                    icon : "fa fa-home text-success"
                },
                hijoInactivo              : {
                    icon : "fa fa-home text-muted"
                },
                hijoExternoActivo         : {
                    icon : "fa fa-paper-plane-o text-success"
                },
                hijoExternoInactivo       : {
                    icon : "fa fa-paper-plane-o text-muted"
                },
                usuarioActivo             : {
                    icon : "fa fa-user text-info"
                },
                usuarioInactivo           : {
                    icon : "fa fa-user text-muted"
                },
                jefeActivo                : {
                    icon : "fa fa-user text-warning"
                },
                jefeInactivo              : {
                    icon : "fa fa-user text-muted"
                },
                directorActivo            : {
                    icon : "fa fa-user text-danger"
                },
                directorInactivo          : {
                    icon : "fa fa-user text-muted"
                },
                usuarioTrianguloActivo    : {
                    icon : "fa fa-download text-info"
                },
                usuarioTrianguloInactivo  : {
                    icon : "fa fa-download text-muted"
                },
                jefeTrianguloActivo       : {
                    icon : "fa fa-cloud-download text-warning"
                },
                jefeTrianguloInactivo     : {
                    icon : "fa fa-cloud-download text-muted"
                },
                directorTrianguloActivo   : {
                    icon : "fa fa-cloud-download text-danger"
                },
                directorTrianguloInactivo : {
                    icon : "fa fa-cloud-download text-muted"
                }
            }
        });

        $('#btnSearch').click(function () {
            $('#tree').jstree(true).search($.trim($("#search").val()));
            return false;
        });
        $("#search").keypress(function (ev) {
            if (ev.keyCode == 13) {
                $('#tree').jstree(true).search($.trim($("#search").val()));
                return false;
            }
        });

    });
</script>

</body>
</html>
