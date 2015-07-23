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

        <link href="${resource(dir: 'css/custom', file: 'reporteRetrasados.css')}" rel="stylesheet">
    </head>

    <body>
        <h2 class="title">
            S.A.D. Web - Trámites Retrasados y Sin Recepción - ${dep.descripcion} (${dep.codigo})

        </h2>


        <div class="btn-toolbar toolbar" style="margin-left: 100px; margin-top: 20px;">
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

        <div class="chartContainer hidden" style="margin-left: 130px;">
            <div id="chart_ret" class="divChart hidden"></div>

            <div id="chart_norec" class="divChart hidden"></div>
        </div>

        <div class="tableContainer ">
            <util:renderHTML html="${tabla}"/>
        </div>

        <script type="text/javascript">

            var jsonGraph = ${raw(jsonGraph)};

            function getData(tipo) {
//                console.log(jsonGraph);

                var limiteCant = 15;
                var limitePorc = 5;

                var title = tipo == "norec" ? "<p class='text-danger'><strong> Trámites sin recepción " : "<p class='text-warning'><strong>Trámites retrasados";
                var json = jsonGraph[tipo];
//                    console.log(tipo, data, json, Object.keys(json), Object.keys(json).length);
                var dataOk = [];
                var cant = Object.keys(json).length;

                <g:if test="${dep}">
                title += " de ${dep.codigo}";
                </g:if>
                <g:else>
                title += (cant > 1 ? " por departamento </strong></p>" : "");
                </g:else>

                var totalOtros = 0;
                var conOtros = false;
                if (cant > limiteCant) {
                    conOtros = true;
                }

                var total = $(".prefectura").find("." + tipo).text();

                $.each(json, function (k, v) {
//                        console.log(k, v, v.codigo);
                    var prct = (v.total * 100) / total;
                    if (cant > 1) {
                        if (conOtros && prct < limitePorc) {
                            totalOtros += v.total;
                        } else {
                            var arr = [v.codigo, v.total];
                            dataOk.push(arr);
                        }
                    } else {
                        cant = Object.keys(v.det).length;
                        totalOtros = 0;
                        conOtros = false;
                        if (cant > limiteCant) {
                            conOtros = true;
                        }
                        $.each(v.det, function (k1, v1) {
                            prct = (v1.total * 100) / total;
                            var n = v1.nombre;
                            if (n == "Oficina ") {
                                n += v.codigo;
                            }
                            if (conOtros && prct < limitePorc) {
                                totalOtros += v1.total;
                            } else {
                                var arr = [n, v1.total];
                                dataOk.push(arr);
                            }
                        });
                    }
                });

                if (conOtros && totalOtros > 0) {
                    var arr = ["Otros (<" + limitePorc + "%)", totalOtros];
                    dataOk.push(arr);
                }

//                console.log(">>>>>>", dataOk);
                return {
                    data  : dataOk,
                    title : title
                }
            }

            function makeChart(tipo) {
                var data = getData(tipo);
                if (data.data.length > 0) {
                    var $chart = $("#chart_" + tipo);
                    $(".chartContainer").removeClass("hidden");
                    $chart.removeClass("hidden");
                    $.jqplot('chart_' + tipo, [data.data],
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
                    $chart.bind('jqplotDataHighlight', function (ev, seriesIndex, pointIndex, data) {
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
                    }).bind('jqplotDataUnhighlight', function (ev, seriesIndex, pointIndex, data) {
                        var $this = $(this);
                        $this.attr('title', "");

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

                makeChart("ret");
                makeChart("norec");
            });
        </script>
    </body>
</html>