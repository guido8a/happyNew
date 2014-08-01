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
    function createContextMenu(node) {
        var nodeId = node.id;
        var $node = $("#" + nodeId);
        var tramiteId = $node.data("jstree").tramite;
        var items = {
            header : {
                label  : "Sin Acciones",
                header : true
            }
        };
        %{--var items = {--}%

        %{--detalles : {--}%
        %{--label  : "Detalles",--}%
        %{--icon   : "fa fa-search",--}%
        %{--action : function () {--}%
        %{--$.ajax({--}%
        %{--type    : 'POST',--}%
        %{--url     : '${createLink(controller: 'tramite3', action: 'detalles')}',--}%
        %{--data    : {--}%
        %{--id : tramiteId--}%
        %{--},--}%
        %{--success : function (msg) {--}%
        %{--$("#dialog-body").html(msg)--}%
        %{--}--}%
        %{--});--}%
        %{--$("#dialog").modal("show")--}%
        %{--}--}%
        %{--}--}%
        %{--};--}%
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
        <g:if test="${session.usuario.getPuedeVer()}">
        items.header.label = "Acciones";
        items.detalles = detalles;
        </g:if>
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
                principal : {
                    icon : "fa fa-edit text-info"
                },
                para      : {
                    icon : "fa fa-file-o text-info"
                },
                copia     : {
                    icon : "fa fa-files-o text-info"
                },
                anulado   : {
                    icon : "fa fa-ban text-muted"
                }
            }
        });
    });
</script>

</body>
</html>