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
        <script src="${resource(dir: 'js/plugins/jquery.jqplot.1.0.8r1250/plugins', file: 'jqplot.highlighter.min.js')}"></script>
        <link href="${resource(dir: 'js/plugins/jquery.jqplot.1.0.8r1250', file: 'jquery.jqplot.min.css')}" rel="stylesheet">

        <style type="text/css">
        .titulo {
            font-weight : bold;
        }

        .numero {
            text-align : right;
        }

        .divChart {
            height       : 450px;
            width        : 450px;
            float        : left;
            margin-right : 10px;
        }

        .tableContainer {
            width : 920px;
        }

        .chartContainer {
            height : 450px;
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

        <div class="chartContainer">
            <div id="chart_rs" class="divChart"></div>

            <div id="chart_rz" class="divChart"></div>
        </div>

        <div class="tableContainer ">
            <util:renderHTML html="${tabla}"/>
        </div>

        <script type="text/javascript">

            function getData(tipo) {
                var data = [], arr = [];
                var deps = $(".data.dep." + tipo).size();

                var title = tipo == "rs" ? "Documentos sin recepción " : "Documentos retrasados";
                title += (deps > 1 ? " por departamento" : "");

                $("tr").each(function () {
                    var $tr = $(this);
                    var valor = $tr.data(tipo);
                    if (valor) {

                        if (deps == 1) {
                            if ($tr.data("tipo") == "per") {
                                arr = [ $tr.data("value"), valor];
                                data.push(arr);
                            } else {
                                title += " de " + $tr.data("value");
                            }
                        } else {
                            if ($tr.data("tipo") == "dep") {
                                arr = [ $tr.data("value"), valor];
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
                            ready  : true
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

            $(function () {
                $("#btnCerrar").click(function () {
                    window.close();
                    return false;
                });

                makeChart("rs");
                makeChart("rz");
            });
        </script>
    </body>
</html>