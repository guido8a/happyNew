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
                <g:if test="${url}">
                    <a href="${url}" class="btn btn-primary btnBuscar">
                        <i class="fa fa-arrow-left"></i> Regresar
                    </a>
                </g:if>
            </div>
        </div>

        <div id="jstree">
            <util:renderHTML html="${html}"/>
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
//                var nodeStrId = node.id;
                var nodeId = node.id;
                var $node = $("#" + nodeId);

                var items = {
                    detalles : {
                        label  : "Detalles",
                        icon   : "fa fa-search",
                        action : function () {
                            $.ajax({
                                type    : 'POST',
                                url     : '${createLink(controller: 'tramite3', action: 'detalles')}',
                                data    : {
                                    id : nodeId
                                },
                                success : function (msg) {
                                    $("#dialog-body").html(msg)
                                }
                            });
                            $("#dialog").modal("show")
                        }
                    }
                };
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
                        padre : {
                            icon : "fa fa-files-o text-info"
                        },
                        hijo  : {
                            icon : "fa fa-file-o text-info"
                        }
                    }
                });
            });
        </script>

    </body>
</html>