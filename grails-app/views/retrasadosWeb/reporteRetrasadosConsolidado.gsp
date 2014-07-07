<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 07/07/14
  Time: 12:02 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Reporte web de trámites retrasados</title>

        <script src="${resource(dir: 'js/plugins/jquery.jqplot.1.0.8r1250', file: 'jquery.jqplot.min.js')}"></script>
        <script src="${resource(dir: 'js/plugins/jquery.jqplot.1.0.8r1250/plugins', file: 'jqplot.pieRenderer.min.js')}"></script>
        <link href="${resource(dir: 'js/plugins/jquery.jqplot.1.0.8r1250', file: 'jquery.jqplot.min.css')}" rel="stylesheet">

        <style type="text/css">
        .titulo {
            font-weight : bold;
        }

        .numero {
            text-align : right;
        }
        </style>
    </head>

    <body>

        <elm:flashMessage tipo="${flash.tipo}" clase="${flash.clase}">${flash.message}</elm:flashMessage>

        <!-- botones -->
        <div class="btn-toolbar toolbar">
            <div class="btn-group">
                <a href="#" class="btn btn-default" id="btnCerrar">
                    <i class="fa fa-times"></i> Cerrar esta ventana
                </a>
            </div>

        </div>

        <util:renderHTML html="${tabla}"/>

        <div id="rzData" class="hidden">
            ${grafRzData}
        </div>

        <div id="rsData" class="hidden">
            ${grafRsData}
        </div>

        <div id="chartRs" style="height:400px;width:400px; "></div>

        <div id="chartRz" style="height:400px;width:400px; "></div>

        <script type="text/javascript">

            function getData(tipo) {
                var deps = 0;
                $("tr").each(function () {
                    var $tr = $(this);
                    var tipo = $tr.data(tipo);
                    if (tipo) {
                        if ($tr.data("tipo") == "dep") {
                            deps++;
                        }
                        console.log($tr.data("tipo"), tipo, $tr.data("value"));
                    }
                });
                console.log("Hay " + deps + " deps");
            }

            $(function () {
                $("#btnCerrar").click(function () {
                    window.close();
                    return false;
                });

                getData("rz");

//                var a = $("#rzData").text();
                %{--var a = ${grafRzData.encodeAsHTML()};--}%
                %{--console.log(a);--}%
                %{--console.log("${util.renderHTML(html: grafRzData)}");--}%
                %{--console.log("${util.renderJson(obj: grafRzData)}");--}%
                %{--<g:each in="${grafRzData}" var="d">--}%
                %{--console.log("${d}", "${d.toString()}");--}%
                %{--</g:each>--}%

                var data = [
                    ['Heavy Industry', 12],
                    ['Retail', 9],
                    ['Light Industry', 14],
                    ['Out of home', 16],
                    ['Commuting', 7],
                    ['Orientation', 9]
                ];
                %{--var chartRs = $.jqplot('chartRs', [${grafRzData.decodeHTML()}],--}%
                %{--{--}%
                %{--title          : '${util.renderHTML(html: tituloRs)}',--}%
                %{--seriesDefaults : {--}%
                %{--// Make this a pie chart.--}%
                %{--renderer        : jQuery.jqplot.PieRenderer,--}%
                %{--rendererOptions : {--}%
                %{--// Put data labels on the pie slices.--}%
                %{--// By default, labels show the percentage of the slice.--}%
                %{--showDataLabels : true--}%
                %{--}--}%
                %{--},--}%
                %{--legend         : {--}%
                %{--show     : true,--}%
                %{--location : 'e'--}%
                %{--}--}%
                %{--}--}%
                %{--);--}%
            });
        </script>
    </body>
</html>