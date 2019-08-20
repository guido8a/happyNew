<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 16/08/19
  Time: 9:05
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Reportes Gráficos</title>
    <script src="${resource(dir: 'js', file: 'Chart.min.js')}"></script>
    <style type="text/css">

    .grafico {
        border-style: solid;
        border-color: #606060;
        border-width: 1px;
        width: 100%;
        float: left;
        text-align: center;
        height: auto;
        border-radius: 8px;
        margin: 10px;
    }

    .bajo {
        margin-bottom: 20px;
    }

    .centrado {
        text-align: center;
    }

    canvas {
        -moz-user-select: none;
        -webkit-user-select: none;
        -ms-user-select: none;
    }


    </style>
</head>

<body>


<div class="btn btn-info" id="graficar2" style="margin-left: 2px">
    <i class="fa fa-pie-chart"></i> Estado de trámites
</div>

<a href="#" class="btn btn-info" id="graficar3">
    <i class="fa fa-calendar"></i> Tiempos de respuesta
</a>

<a href="#" class="btn btn-info" id="graficar4">
    <i class="fa fa-calendar"></i> Tiempos de respuesta (Pie)
</a>


%{--<div class="col-md-5"></div>--}%

<div style="background-color: #fdfdff" class="chart-container grafico" id="chart-area" hidden>
    <h3 id="subtitulo"></h3>
    <h3 id="titulo"></h3>

    <div id="graf">
        <canvas id="clases" style="margin-top: 20px"></canvas>
    </div>

</div>


<div style="width: 75%">
    <canvas id="canvas4"></canvas>
</div>


<script type="text/javascript">




    var canvas = $("#clases");
    var myChart;

    $("#graficar2").click(function () {

        $(this).addClass("active");
        $("#graficar3, #graficar4").removeClass("active");

        $.ajax({
            type: 'POST',
            url: "${createLink(controller: 'reportesPersonales', action: 'seleccionOficina')}",
            data: '',
            success: function (msg){
                bootbox.dialog({
                    id      : "dlgSeleccionEstado",
                    title   : '<i class="fa fa-files-o"></i> Selección de Departamentos',
//                    class   : "long",
                    message : msg,
                    buttons : {
                        cancelar : {
                            label     : '<i class="fa fa-times"></i> Cancelar',
                            className : 'btn-default',
                            callback  : function () {
                                $("#graficar2").removeClass("active");
                            }
                        },
                        enviar   : {
                            id        : 'btnAceptar',
                            label     : '<i class="fa fa-check"></i> Aceptar',
                            className : "btn-success",
                            callback  : function () {

                                var fi = $("#fechaInicio").val();
                                var ff = $("#fechaFin").val();

                                $.ajax({
                                    type: 'POST',
                                    url: "${createLink(controller: 'reportesPersonales', action: 'comprobarFechas')}",
                                    data: {
                                        fechaInicio: fi,
                                        fechaFin: ff
                                    },
                                    success: function (msg) {
                                        var parts = msg.split("_");
                                        if (parts[0] == 'ok') {

                                            var dialog = cargarLoader(" Graficando...");
                                            $.ajax({
                                                type: 'POST',
                                                url: '${createLink(controller: 'reportesPersonales', action: 'estadoTramites')}',
                                                data: {
                                                    cntn: 2,
                                                    departamento: $("#departamento").val(),
                                                    fechaInicio: $("#fechaInicio").val(),
                                                    fechaFin: $("#fechaFin").val()
                                                },
                                                success: function (json) {

                                                    dialog.modal('hide');
//                                        $("#graficar2").removeClass("active");

                                                    $("#chart-area").removeClass('hidden');
                                                    $("#subtitulo").html(json.cabecera)
                                                    $("#titulo").html(json.titulo)
                                                    $("#clases").remove();
                                                    $("#chart-area").removeAttr('hidden');

                                                    /* se crea dinámicamente el canvas y la función "click" */
                                                    $('#graf').append('<canvas id="clases" style="margin-top: 30px"></canvas>');

                                                    canvas = $("#clases")

                                                    var chartData = {
                                                        type: 'bar',
                                                        data: {
                                                            labels: json.cabecera.split(','),
                                                            datasets: [
                                                                {
                                                                    label: 'Recibidos',
                                                                    backgroundColor: "#b25522",
                                                                    stack: 'Stack 1',
                                                                    data: json.recibidos.split(',')
                                                                },
                                                                {
                                                                    label: 'No Recibidos',
                                                                    backgroundColor: "#af9030",
                                                                    stack: 'Stack 2',
                                                                    data: json.noRecibidos.split(',')
                                                                },
                                                                {
                                                                    label: 'Generados',
                                                                    backgroundColor: "#205060",
                                                                    stack: 'Stack 5',
                                                                    data: json.generados.split(',')
                                                                },
                                                                {
                                                                    label: 'Enviados',
                                                                    backgroundColor: "#d45840",
                                                                    stack: 'Stack 3',
                                                                    data: json.enviados.split(',')
                                                                },
                                                                {
                                                                    label: 'No enviados',
                                                                    backgroundColor: "#00af80",
                                                                    stack: 'Stack 4',
                                                                    data: json.noEnviados.split(',')
                                                                },
                                                                {
                                                                    label: 'Retrasados',
                                                                    backgroundColor: "#af1627",
                                                                    stack: 'Stack 6',
                                                                    data: json.retrasados.split(',')
                                                                }
                                                            ]
                                                        },
                                                        options: {
                                                            legend: {
                                                                display: true,
                                                                labels: {
                                                                    fontColor: 'rgb(20, 80, 100)',
                                                                    fontSize: 14
                                                                },
                                                                pointLabels: {
                                                                    fontSize: 16
                                                                }
                                                            }

                                                        }
                                                    };

                                                    myChart = new Chart(canvas, chartData, 1);
                                                }
                                            });
                                        }else{
                                            bootbox.alert(parts[1]);
                                            return false
                                        }
                                    }
                                })
                            }
                        }
                    }
                });
            }
        });
    });


    $("#graficar3").click(function () {

        $(this).addClass("active");
        $("#graficar2, #graficar4").removeClass("active");

        $.ajax({
            type: 'POST',
            url: "${createLink(controller: 'reportesPersonales', action: 'seleccionOficina')}",
            data: '',
            success: function (msg){
                bootbox.dialog({
                    id      : "dlgSeleccion",
                    title   : '<i class="fa fa-files-o"></i> Selección de Departamentos',
//                    class   : "long",
                    message : msg,
                    buttons : {
                        cancelar : {
                            label     : '<i class="fa fa-times"></i> Cancelar',
                            className : 'btn-default',
                            callback  : function () {
                                $("#graficar3").removeClass("active");
                            }
                        },
                        enviar   : {
                            id        : 'btnAceptar',
                            label     : '<i class="fa fa-check"></i> Aceptar',
                            className : "btn-success",
                            callback  : function () {

                                var fi = $("#fechaInicio").val();
                                var ff = $("#fechaFin").val();

                                $.ajax({
                                    type: 'POST',
                                    url: "${createLink(controller: 'reportesPersonales', action: 'comprobarFechas')}",
                                    data: {
                                        fechaInicio: fi,
                                        fechaFin: ff
                                    },
                                    success: function (msg) {
                                        var parts = msg.split("_");
                                        if (parts[0] == 'ok') {
                                            var dialog = cargarLoader(" Graficando...");
                                            $.ajax({
                                                type: 'POST',
                                                url: '${createLink(controller: 'reportesPersonales', action: 'tiemposRespuesta')}',
                                                data: {
                                                    cntn: 2,
                                                    departamento: $("#departamento").val(),
                                                    fechaInicio: $("#fechaInicio").val(),
                                                    fechaFin: $("#fechaFin").val()
                                                },
                                                success: function (json) {

                                                    dialog.modal('hide');
//                                        $("#graficar3").removeClass("active");

                                                    $("#chart-area").removeClass('hidden');
                                                    $("#subtitulo").html(json.cabecera)
                                                    $("#titulo").html(json.titulo)
                                                    $("#clases").remove();
                                                    $("#chart-area").removeAttr('hidden');

                                                    /* se crea dinámicamente el canvas y la función "click" */
                                                    $('#graf').append('<canvas id="clases" style="margin-top: 30px"></canvas>');

                                                    canvas = $("#clases")

                                                    var chartData = {
                                                        type: 'bar',
                                                        data: {
                                                            labels: json.cabecera.split(','),
                                                            datasets: [
                                                                {
                                                                    label: 'Tiempo hasta 3 días',
                                                                    backgroundColor: "#4ba0a0",
                                                                    stack: 'Stack 1',
                                                                    data: json.tiempo1.split(',')
                                                                },
                                                                {
                                                                    label: 'Tiempo de 4 a 10 días',
                                                                    backgroundColor: "#af9030",
                                                                    stack: 'Stack 2',
                                                                    data: json.tiempo2.split(',')
                                                                },
                                                                {
                                                                    label: 'Tiempo mayor a 11 días',
                                                                    backgroundColor: "#d45840",
                                                                    stack: 'Stack 3',
                                                                    data: json.tiempo3.split(',')
                                                                }
                                                            ]
                                                        },
                                                        options: {
                                                            legend: {
                                                                display: true,
                                                                labels: {
                                                                    fontColor: 'rgb(20, 80, 100)',
                                                                    fontSize: 14
                                                                },
                                                                pointLabels: {
                                                                    fontSize: 16
                                                                }
                                                            }

                                                        }
                                                    };

                                                    myChart = new Chart(canvas, chartData, 1);
                                                }
                                            });
                                        }else{
                                            bootbox.alert(parts[1]);
                                            return false
                                        }
                                    }
                                })
                            }
                        }
                    }
                });
            }
        });
    });

    $("#graficar4").click(function () {

        $(this).addClass("active");
        $("#graficar2, #graficar3").removeClass("active");

        $.ajax({
            type: 'POST',
            url: "${createLink(controller: 'reportesPersonales', action: 'seleccionOficina')}",
            data: '',
//            async: true,
            success: function (msg){
                bootbox.dialog({
                    id      : "dlgSeleccion",
                    title   : '<i class="fa fa-files-o"></i> Selección de Departamentos',
                    message : msg,
                    buttons : {
                        cancelar : {
                            label     : '<i class="fa fa-times"></i> Cancelar',
                            className : 'btn-default',
                            callback  : function () {
                                $("#graficar4").removeClass("active");
                            }
                        },
                        enviar   : {
                            id        : 'btnAceptar',
                            label     : '<i class="fa fa-check"></i> Aceptar',
                            className : "btn-success",
                            callback  : function () {

                                var fi = $("#fechaInicio").val();
                                var ff = $("#fechaFin").val();

                                $.ajax({
                                    type: 'POST',
                                    url: "${createLink(controller: 'reportesPersonales', action: 'comprobarFechas')}",
                                    data:{
                                        fechaInicio: fi,
                                        fechaFin: ff
                                    },
                                    success: function (msg){
                                        var parts = msg.split("_");
                                        if(parts[0] == 'ok'){

                                            var dialog = cargarLoader(" Graficando...");
                                            $.ajax({
                                                type: 'POST',
                                                url: '${createLink(controller: 'reportesPersonales', action: 'tiemposRespuesta')}',
                                                data: {
                                                    cntn: 2,
                                                    departamento: $("#departamento").val(),
                                                    fechaInicio: $("#fechaInicio").val(),
                                                    fechaFin: $("#fechaFin").val()
                                                },
                                                success: function (json) {

                                                    dialog.modal('hide');

                                                    $("#chart-area").removeClass('hidden');
                                                    $("#titulo").html(json.titulo);
                                                    $("#subtitulo").removeClass("hidden")
                                                    $("#subtitulo").html(json.cabecera);
                                                    $("#clases").remove();
                                                    $("#chart-area").removeAttr('hidden');

                                                    /* se crea dinámicamente el canvas y la función "click" */
                                                    $('#graf').append('<canvas id="clases" style="margin-top: 30px"></canvas>');

                                                    canvas = $("#clases")

                                                    var chartData = {
                                                        type: 'pie',
                                                        data: {
                                                            labels: ["Tiempo hasta 3 días", "Tiempo de 4 a 10 días", "Tiempo mayor a 11 días"],
                                                            datasets: [{
                                                                label: ["Tiempo hasta 3 días", "Tiempo de 4 a 10 días", "Tiempo mayor a 11 días"],
                                                                backgroundColor: ["#46a2db", "#4ba0a0", "#cf5354"],
                                                                data: [json.tiempo1, json.tiempo2, json.tiempo3]
                                                            }
                                                            ]
                                                        },
                                                        options: {
                                                            legend: {
                                                                display: true,
                                                                labels: {
                                                                    fontColor: 'rgb(20, 80, 100)',
                                                                    fontSize: 14
                                                                },
                                                                pointLabels: {
                                                                    fontSize: 16
                                                                }
                                                            }

                                                        }
                                                    };
                                                    myChart = new Chart(canvas, chartData, 1);
                                                }
                                            });

                                        }else{
                                            bootbox.alert(parts[1]);
                                            return false
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    });


</script>

</body>
</html>