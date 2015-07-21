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
        <title>Dir Reporte de trámites retrasados y sin recepción</title>

        <script src="${resource(dir: 'js/plugins/jquery.jqplot.1.0.8r1250', file: 'jquery.jqplot.min.js')}"></script>
        <script src="${resource(dir: 'js/plugins/jquery.jqplot.1.0.8r1250/plugins', file: 'jqplot.pieRenderer.min.js')}"></script>
        <script src="${resource(dir: 'js/plugins/jquery.jqplot.1.0.8r1250/plugins', file: 'jqplot.highlighter.min.js')}"></script>
        <link href="${resource(dir: 'js/plugins/jquery.jqplot.1.0.8r1250', file: 'jquery.jqplot.min.css')}" rel="stylesheet">

        <style type="text/css">
        .titulo {
            font-weight : bold;
            color       : #222;
        }

        .titl {
            font-family : 'open sans condensed';
            font-weight : bold;
            text-shadow : -2px 2px 1px rgba(0, 0, 0, 0.25);
            color       : #0070B0;
            margin-top  : 20px;
        }

        .numero {
            text-align : right;
        }

        .divChart {
            height       : 360px;
            width        : 360px;
            float        : left;
            margin-right : 10px;
        }

        .tableContainer {
            width : 920px;
        }

        .chartContainer {
            height : 360px;
        }

        th {
            border-color     : #444;
            background-color : #e4e4e8;
        }

        .dir {
            background : #aaa;
        }
        </style>
    </head>

    <body>
        <div style="margin-left:230px;"><h2 class="titl">S.A.D. Web - Trámites Retrasados y Sin Recepción</h2>
        </div>
        %{--<g:if test="${!inicio}">--}%
        <div class="btn-toolbar toolbar" style="margin-left: 100px; margin-top: 20px;">
            %{--
                        <div class="btn-group">
                            <a href="#" class="btn btn-default" id="btnCerrar">
                                <i class="fa fa-times"></i> Cerrar esta ventana
                            </a>
                        </div>
            --}%

            <div class="btn-group">
                <g:link class="btn btn-default" controller="retrasados" action="reporteRetrasadosDetalle" params="${params}">
                    <i class="fa fa-file-pdf-o"></i> Reporte detallado pdf
                </g:link>
                <g:link class="btn btn-default" controller="retrasados" action="reporteRetrasadosConsolidado" params="${params}">
                    <i class="fa fa-file-pdf-o"></i> Reporte resumido pdf
                </g:link>
            </div>

            <div class="btn-group">
                <g:link class="btn btn-primary" controller="retrasadosExcel" action="reporteRetrasadosDetalle" params="${params}">
                    <i class="fa fa-file-excel-o"></i> Reporte detallado Excel
                </g:link>
                <g:link class="btn btn-primary" controller="retrasadosExcel" action="reporteRetrasadosConsolidado" params="${params}">
                    <i class="fa fa-file-excel-o"></i> Reporte resumido Excel
                </g:link>
            </div>
        </div>
        %{--</g:if>--}%
        <div class="chartContainer hidden" style="margin-left: 130px;">
            <div id="chart_rz" class="divChart hidden"></div>

            <div id="chart_rs" class="divChart hidden"></div>
        </div>

        <div class="tableContainer ">
            <util:renderHTML html="${tabla}"/>
        </div>

        <script type="text/javascript">

            function getData(tipo) {
                var data = [], arr = [];
                var deps = $(".data.dir." + tipo).size();
                var totalDirs = $(".data.dir").size();

                var title = tipo == "rs" ? "<p class='text-danger'><strong> Trámites sin recepción " : "<p class='text-warning'><strong>Trámites retrasados";
                title += (totalDirs > 1 ? " por dirección </strong></p>" : "");

                $("tr").each(function () {
                    var $tr = $(this);
                    var valor = $tr.data(tipo);
                    if (valor) {
                        if (totalDirs == 1) {
                            if ($tr.data("tipo") == "per") {
                                arr = [$tr.data("value"), valor];
                                data.push(arr);
                            } else {
                                title += " de " + $tr.data("value");
                                arr = [$tr.data("value"), valor];
                                data.push(arr);
                            }
                        } else {
                            if ($tr.data("tipo") == "dir") {
                                arr = [$tr.data("value"), valor];
                                data.push(arr);
                            }
                        }
                    }
                });
                return {
                    data  : data,
                    title : title
                }
            }

            function makeChart(tipo) {
                var data = getData(tipo);
                if (data.data.length > 0) {
                    $(".chartContainer").removeClass("hidden");
                    $("#chart_" + tipo).removeClass("hidden");

                    var plot = $.jqplot('chart_' + tipo, [data.data],
                            {
                                title          : data.title,
                                seriesDefaults : {
                                    // Make this a pie chart.
                                    renderer        : $.jqplot.PieRenderer,
                                    rendererOptions : {
                                        // Put data labels on the pie slices.
                                        // By default, labels show the percentage of the slice.
                                        showDataLabels : true,
                                        sliceMargin    : 5
                                    },
                                    highlighter     : {
                                        show              : true,
                                        formatString      : '%s',
                                        tooltipLocation   : 'sw',
                                        useAxesFormatters : false
                                    }
                                },
                                legend         : {
                                    show     : true,
                                    location : 'e'
                                }
                            }
                    );
                    $("#chart_" + tipo).bind('jqplotDataHighlight', function (ev, seriesIndex, pointIndex, data) {
                        var $this = $(this);
                        $this.qtip({
                            show     : {
                                ready : true
                            },
                            position : {
                                my     : 'bottom center',  // Position my top left...
                                at     : 'top center', // at the bottom right of...
                                target : "mouse",
                                adjust : {
                                    mouse : false
                                }
                            },
                            content  : data[0] + ": " + data[1] + " doc" + (data[1] == 1 ? '' : 's') + "."
                        });
                    });
                }
            }

            $(function () {
                /*
                 $("#btnCerrar").click(function () {
                 window.close();
                 return false;
                 });
                 */

                makeChart("rs");
                makeChart("rz");
            });
        </script>
    </body>
</html>