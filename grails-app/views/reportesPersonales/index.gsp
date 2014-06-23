<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 23/06/14
  Time: 10:50 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Reportes personales</title>

        <style type="text/css">
        .info {
            font-weight : bold;
        }
        </style>
    </head>

    <body>
        <div class="panel-group" id="accordion">
            <div class="panel panel-default">
                <g:set var="reporte" value="retrasados"/>
                <div class="panel-heading">
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#accordion" href="#collapse_${reporte}">
                            Trámites retrasados
                            %{--<small>afsdfa</small>--}%
                        </a>
                    </h4>
                </div>

                <div id="collapse_${reporte}" class="panel-collapse collapse in">
                    <div class="panel-body">
                        <form class="form-horizontal">
                            <div class="alert alert-info">
                                Se generará un reporte <span class="info" id="${reporte}_tipo">general</span>
                                de sus trámites retrasados en formato <span class="info" id="${reporte}_formato">PDF</span>.
                            </div>

                            <div class="btn-group" data-toggle="buttons">
                                <label class="btn btn-primary active toggle" data-tipo="tipo" data-valor="general" data-reporte="${reporte}">
                                    <input type="radio" name="options" id="${reporte}_general"> General
                                </label>
                                <label class="btn btn-primary toggle" data-tipo="tipo" data-valor="detallado" data-reporte="${reporte}">
                                    <input type="radio" name="options" id="${reporte}_detallado"> Detallado
                                </label>
                            </div>

                            <div class="btn-group" data-toggle="buttons">
                                <label class="btn btn-primary active toggle" data-tipo="formato" data-valor="PDF" data-reporte="${reporte}">
                                    <input type="radio" name="options" id="${reporte}_pdf"><i class="fa fa-file-pdf-o"></i> PDF
                                </label>
                                <label class="btn btn-primary toggle" data-tipo="formato" data-valor="Excel" data-reporte="${reporte}">
                                    <input type="radio" name="options" id="${reporte}_xls"><i class="fa fa-file-excel-o"></i> Excel
                                </label>
                            </div>

                            <a href="#" id="generar_${reporte}" class="btn btn-success generar"
                               data-reporte="${reporte}" data-formato="PDF" data-tipo="general">
                                <i class="fa fa-check"></i> Generar
                            </a>
                        </form>
                    </div>
                </div>
            </div>

            <div class="panel panel-default">
                <g:set var="reporte" value="generados"/>
                <div class="panel-heading">
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#accordion" href="#collapse_${reporte}">
                            Documentos generados
                            %{--<small>afsdfa</small>--}%
                        </a>
                    </h4>
                </div>

                <div id="collapse_${reporte}" class="panel-collapse collapse">
                    <div class="panel-body">
                        <form class="form-horizontal">
                            <div class="alert alert-info">
                                Se generará un reporte <span class="info" id="${reporte}_tipo">general</span>
                                de sus documentos generados en formato <span class="info" id="${reporte}_formato">PDF</span>
                                desde <span class="info" id="${reporte}_desde"></span> hasta  <span class="info" id="${reporte}_hasta"></span>.
                            </div>

                            <div class="row">
                                <div class="col-md-1">
                                    <p class="form-control-static">Desde</p>
                                </div>

                                <div class="col-md-2">
                                    <elm:datepicker name="desde_${reporte}" class="form-control date" value="${new Date() - 15}"
                                                    extra="data-tipo='desde' data-reporte='${reporte}'" onChangeDate="updateFecha"/>
                                </div>

                                <div class="col-md-1">
                                    <p class="form-control-static">Hasta</p>
                                </div>

                                <div class="col-md-2">
                                    <elm:datepicker name="hasta_${reporte}" class="form-control date" value="${new Date()}" maxDate="+0"
                                                    extra="data-tipo='hasta' data-reporte='${reporte}'" onChangeDate="updateFecha"/>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-12">
                                    <div class="btn-group" data-toggle="buttons">
                                        <label class="btn btn-primary active toggle" data-tipo="tipo" data-valor="general" data-reporte="${reporte}">
                                            <input type="radio" name="options" id="${reporte}_general"> General
                                        </label>
                                        <label class="btn btn-primary toggle" data-tipo="tipo" data-valor="detallado" data-reporte="${reporte}">
                                            <input type="radio" name="options" id="${reporte}_detallado"> Detallado
                                        </label>
                                    </div>

                                    <div class="btn-group" data-toggle="buttons">
                                        <label class="btn btn-primary active toggle" data-tipo="formato" data-valor="PDF" data-reporte="${reporte}">
                                            <input type="radio" name="options" id="${reporte}_pdf"><i class="fa fa-file-pdf-o"></i> PDF
                                        </label>
                                        <label class="btn btn-primary toggle" data-tipo="formato" data-valor="Excel" data-reporte="${reporte}">
                                            <input type="radio" name="options" id="${reporte}_xls"><i class="fa fa-file-excel-o"></i> Excel
                                        </label>
                                    </div>

                                    <a href="#" id="generar_${reporte}" class="btn btn-success generar"
                                       data-reporte="${reporte}" data-formato="PDF" data-tipo="general">
                                        <i class="fa fa-check"></i> Generar
                                    </a>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <div class="panel panel-default">
                <g:set var="reporte" value="gestion"/>
                <div class="panel-heading">
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#accordion" href="#collapse_${reporte}">
                            Gestión de trámites
                            %{--<small>afsdfa</small>--}%
                        </a>
                    </h4>
                </div>

                <div id="collapse_${reporte}" class="panel-collapse collapse">
                    <div class="panel-body">
                        <form class="form-horizontal">
                            <div class="alert alert-info">
                                Se generará un reporte
                                de la gestión de sus trámites en formato <span class="info" id="${reporte}_formato">PDF</span>
                                desde <span class="info" id="${reporte}_desde"></span> hasta  <span class="info" id="${reporte}_hasta"></span>.
                            </div>

                            <div class="row">
                                <div class="col-md-1">
                                    <p class="form-control-static">Desde</p>
                                </div>

                                <div class="col-md-2">
                                    <elm:datepicker name="desde_${reporte}" class="form-control date" value="${new Date() - 15}"
                                                    extra="data-tipo='desde' data-reporte='${reporte}'" onChangeDate="updateFecha"/>
                                </div>

                                <div class="col-md-1">
                                    <p class="form-control-static">Hasta</p>
                                </div>

                                <div class="col-md-2">
                                    <elm:datepicker name="hasta_${reporte}" class="form-control date" value="${new Date()}" maxDate="+0"
                                                    extra="data-tipo='hasta' data-reporte='${reporte}'" onChangeDate="updateFecha"/>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-12">
                                    %{--<div class="btn-group" data-toggle="buttons">--}%
                                    %{--<label class="btn btn-primary active toggle" data-tipo="tipo" data-valor="general" data-reporte="${reporte}">--}%
                                    %{--<input type="radio" name="options" id="${reporte}_general"> General--}%
                                    %{--</label>--}%
                                    %{--<label class="btn btn-primary toggle" data-tipo="tipo" data-valor="detallado" data-reporte="${reporte}">--}%
                                    %{--<input type="radio" name="options" id="${reporte}_detallado"> Detallado--}%
                                    %{--</label>--}%
                                    %{--</div>--}%

                                    <div class="btn-group" data-toggle="buttons">
                                        <label class="btn btn-primary active toggle" data-tipo="formato" data-valor="PDF" data-reporte="${reporte}">
                                            <input type="radio" name="options" id="${reporte}_pdf"><i class="fa fa-file-pdf-o"></i> PDF
                                        </label>
                                        <label class="btn btn-primary toggle" data-tipo="formato" data-valor="Excel" data-reporte="${reporte}">
                                            <input type="radio" name="options" id="${reporte}_xls"><i class="fa fa-file-excel-o"></i> Excel
                                        </label>
                                    </div>

                                    <a href="#" id="generar_${reporte}" class="btn btn-success generar"
                                       data-reporte="${reporte}" data-formato="PDF" data-tipo="general">
                                        <i class="fa fa-check"></i> Generar
                                    </a>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

        </div>

        <script type="application/javascript">
            function updateFecha($elm, e) {
//                console.log($elm); //el objeto jquery del datepicker, el textfield
                var tipo = $elm.data("tipo");
                var valor = $elm.val();
                var reporte = $elm.data("reporte");
                $("#" + reporte + "_" + tipo).text(valor);
            }

            $(function () {
                $(".date").each(function () {
                    var tipo = $(this).data("tipo");
                    var valor = $(this).val();
                    var reporte = $(this).data("reporte");
                    $("#" + reporte + "_" + tipo).text(valor);
                });

                $(".toggle").click(function () {
                    var tipo = $(this).data("tipo");
                    var valor = $(this).data("valor");
                    var reporte = $(this).data("reporte");
                    $("#generar_" + reporte).data(tipo, valor);
                    $("#" + reporte + "_" + tipo).text(valor);
                });

                $(".generar").click(function () {
                    var reporte = $(this).data("reporte");
                    var tipo = $(this).data("tipo");
                    var formato = $(this).data("formato");

                    switch (reporte) {
                        case "retrasados":
                            if (formato == "PDF") {
                                if (tipo == "detallado") {
                                    location.href = "${g.createLink(controller: 'retrasados',action: 'reporteRetrasadosDetalle')}?prsn=${persona.id}&detalle=1"
                                } else {
                                    location.href = "${g.createLink(controller: 'retrasados',action: 'reporteRetrasadosConsolidado')}?prsn=${persona.id}";
                                }
                            } else {
                                if (tipo == "detallado") {
                                    location.href = "${g.createLink(controller: 'retrasadosExcel',action: 'reporteRetrasadosDetalle')}?prsn=${persona.id}&detalle=1"
                                } else {
                                    location.href = "${g.createLink(controller: 'retrasadosExcel',action: 'reporteRetrasadosConsolidado')}?prsn=${persona.id}";
                                }
                            }
                            break;
                        case "generados":
                            if (formato == "PDF") {
                                if (tipo == "detallado") {
                                    location.href = "${g.createLink(controller: 'documentosGenerados',action: 'reporteDetalladoPdf')}/${persona.id}?desde=" + $("#desde_generados_input").val() + "&hasta=" + $("#hasta_generados_input").val() + "&tipo=prsn";
                                } else {
                                    location.href = "${g.createLink(controller: 'documentosGenerados',action: 'reporteGeneralPdf')}/${persona.id}?desde=" + $("#desde_generados_input").val() + "&hasta=" + $("#hasta_generados_input").val() + "&tipo=prsn";
                                }
                            } else {
                                if (tipo == "detallado") {
                                    location.href = "${g.createLink(controller: 'documentosGenerados',action: 'reporteDetalladoXls')}/${persona.id}?desde=" + $("#desde_generados_input").val() + "&hasta=" + $("#hasta_generados_input").val() + "&tipo=prsn";
                                } else {
                                    location.href = "${g.createLink(controller: 'documentosGenerados',action: 'reporteGeneralXlsx')}/${persona.id}?desde=" + $("#desde_generados_input").val() + "&hasta=" + $("#hasta_generados_input").val() + "&tipo=prsn";
                                }
                            }
                            break;
                        case "gestion":
                            if (formato == "PDF") {
                                location.href = "${g.createLink(controller: 'reporteGestion',action: 'reporteGestion')}/${persona.id}?desde=" + $("#desde_gestion_input").val() + "&hasta=" + $("#hasta_gestion_input").val();
                            } else {
                                location.href = "${g.createLink(controller: 'reporteGestion',action: 'reporteGestionXlsx')}/${persona.id}?desde=" + $("#desde_gestion_input").val() + "&hasta=" + $("#hasta_gestion_input").val();
                            }
                            break;
                    }

                });
            });
        </script>

    </body>
</html>