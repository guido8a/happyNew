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
        <div id="jstree">
            <util:renderHTML html="${html}"/>
        </div>

        <script type="text/javascript">
            function createContextMenu(node) {
//                var nodeStrId = node.id;
                var $node = $("#" + nodeStrId);
                var nodeId = node.id;

                var nodeHasChildren = $node.hasClass("hasChildren");
                var nodeOcupado = $node.hasClass("ocupado");

                var nodeTramites = $node.data("tramites");

                console.log(nodeId);

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
                            icon : "fa fa-copy text-info"
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