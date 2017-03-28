<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 28/03/17
  Time: 11:07
--%>
<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 4/30/14
  Time: 1:20 PM
--%>

<%@ page import="happy.tramites.RolPersonaTramite" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Árbol de trámite</title>
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
        <a href="javascript: history.go(-1)" class="btn btn-primary regresar">
            <i class="fa fa-arrow-left"></i> Regresar
        </a>
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

    function comprobar(node){

        var respuesta = true;

        $.ajax({
            type    : 'POST',
            async: false,
            url     : '${createLink(controller: 'tramite3', action: 'comprobar')}',
            data    : {
                id : node
            },
            success : function (msg) {
                if(msg == 'true'){
                    respuesta = true
                }else{
                    respuesta = false
                }
            }
        });

        return respuesta
    }


    function comprobarRecibido(node){

        var recibio = true;

        $.ajax({
            type    : 'POST',
            async: false,
            url     : '${createLink(controller: 'tramite3', action: 'comprobarRecibido')}',
            data    : {
                id : node
            },
            success : function (msg) {
                if(msg == 'true'){
                    recibio = true
                }else{
                    recibio = false
                }
            }
        });

        return recibio

    }


    function createContextMenu(node) {
        var nodeId = node.id;

        var $node = $("#" + nodeId);
        var nodeType = $node.data("jstree").type;
        var tramiteId = $node.data("jstree").tramite;
        var prtrId =  $node.data("prtr").prtrId;


//        console.log("data " + $node.data("prtr").prtrId);

        var items = {
//                    header : {
//                        label  : "Sin Acciones",
//                        header : true
//                    }
        };
        if (!nodeType.contains("tramite")) {
            var detalles = {
                label  : 'Detalles',
                icon   : "fa fa-search",
                action : function (e) {
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
//            var plazo = {
//                label  : 'Ampliar Plazo',
//                icon   : "fa fa-check",
//                action : function (e) {
//
//                  }
//            };



            var plazo = {
                label  : "Ampliar plazo",
                icon   : "fa fa-arrows-h",
                action : function (e) {
                    $.ajax({
                        type    : 'POST',
                        url     : '${createLink(controller: 'buscarTramite', action: 'nuevoAmpliarPlazo_ajax')}',
                        data    : {
                            id : prtrId
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



            <g:if test="${session.usuario.getPuedeVer()}">
//                    items.header.label = "Acciones";
            items.detalles = detalles;


            <g:if test="${session.usuario.puedePlazo}">
            if(!comprobar(nodeId)){
                if(comprobarRecibido(nodeId)){
                    items.plazo = plazo;
                }
            }
            </g:if>
            </g:if>
        }
        return items
    }

    $(function () {

        $(".regresar").click(function () {
            history.go(-1)
        });
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
                tramitePrincipal : {
                    icon : "fa fa-file text-success"
                },
                tramite          : {
                    icon : "fa fa-file text-info"
                },
                principal        : {
                    icon : "fa fa-edit text-info"
                },
                para             : {
                    icon : "fa fa-file-o text-info"
                },
                copia            : {
                    icon : "fa fa-files-o text-info"
                },
                anulado          : {
                    icon : "fa fa-ban text-muted"
                }
            }
        });
    });
</script>

</body>
</html>