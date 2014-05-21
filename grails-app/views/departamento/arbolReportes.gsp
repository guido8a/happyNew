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
        <div class="btn-group">
            <g:link controller="inicio" action="parametros" class="btn btn-default">
                <i class="fa fa-arrow-left"></i> Regresar
            </g:link>
        </div>

        <div class="btn-group" style="margin-top: 4px;">
            <g:link action="arbol" params="[sort: 'nombre']" class="btn btn-sm btn-info">
                <i class="fa fa-sort-alpha-asc"></i> Ordenar por nombre
            </g:link>
            <g:link action="arbol" params="[sort: 'apellido']" class="btn btn-sm btn-info">
                <i class="fa fa-sort-alpha-asc"></i> Ordenar por apellido
            </g:link>
        </div>

        <div class="btn-group" style="margin-top: 4px;">
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
            <i class="fa fa-user text-info"></i> Usuario<br/>
            <i class="fa fa-user text-warning"></i> Autoridad
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

<script type="text/javascript">

    var index = 0;

    var $btnCloseModal = $('<button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>');
    var $btnSave = $('<button type="button" class="btn btn-success"><i class="fa fa-save"></i> Guardar</button>');

    function createContextMenu(node) {
        var nodeStrId = node.id;
        var $node = $("#" + nodeStrId);
        var nodeId = nodeStrId.split("_")[1];
        var nodeType = $node.data("jstree").type;

        var nodeHasChildren = $node.hasClass("hasChildren");
        var nodeOcupado = $node.hasClass("ocupado");

        var nodeTramites = $node.data("tramites");

        if (nodeType!="root" && !nodeType.match("inactivo") && !nodeType.match("Inactivo")) {
            var items = {
                detallado : {
                    label   : "Tramites retrasados detallado",
                    icon    : "fa fa-print",
                    action  : function(){
                        if(nodeType.match("padre") || nodeType.match("hijo")){
                            location.href="${g.createLink(controller: 'retrasados',action: 'reporteRetrasadosDetalle')}?dpto="+nodeId
                        }else{
                            location.href="${g.createLink(controller: 'retrasados',action: 'reporteRetrasadosDetalle')}?prsn="+nodeId
                        }
                    }
                },
                noDetallado : {
                    label   : "Tramites retrasados",
                    icon    : "fa fa-print",
                    action  : function(){
                        if(nodeType.match("padre") || nodeType.match("hijo")){
                            location.href="${g.createLink(controller: 'retrasados',action: 'reporteRetrasados')}?dpto="+nodeId
                        }else{
                            location.href="${g.createLink(controller: 'retrasados',action: 'reporteRetrasados')}?prsn="+nodeId
                        }
                    }
                }
            };

        }

        return items;
    }

    $(function () {

        $('#tree').on("loaded.jstree", function () {
            $("#loading").hide();
            $("#tree").removeClass("hide").show();
        }).on("select_node.jstree", function (node, selected, event) {
//                    $('#tree').jstree('toggle_node', selected.selected[0]);
        }).jstree({
            plugins     : [ "types", "state", "contextmenu", "wholerow" , "search"],
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
                key : "departamentos"
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
                root                     : {
                    icon : "fa fa-folder text-warning"
                },
                padreActivo              : {
                    icon : "fa fa-building-o text-info"
                },
                padreInactivo            : {
                    icon : "fa fa-building-o text-muted"
                },
                hijoActivo               : {
                    icon : "fa fa-home text-success"
                },
                hijoInactivo             : {
                    icon : "fa fa-home text-muted"
                },
                usuarioActivo            : {
                    icon : "fa fa-user text-info"
                },
                usuarioInactivo          : {
                    icon : "fa fa-user text-muted"
                },
                jefeActivo               : {
                    icon : "fa fa-user text-warning"
                },
                jefeInactivo             : {
                    icon : "fa fa-user text-muted"
                },
                usuarioTrianguloActivo   : {
                    icon : "fa fa-download text-info"
                },
                usuarioTrianguloInactivo : {
                    icon : "fa fa-download text-muted"
                },
                jefeTrianguloActivo      : {
                    icon : "fa fa-cloud-download text-warning"
                },
                jefeTrianguloInactivo    : {
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
