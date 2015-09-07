<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <link href='${resource(dir: "css", file: "CustomSvt.css")}' rel='stylesheet' type='text/css'>
    <title>Ver tramite</title>
    <style>
    .col-xs-uno-y-medio {
        width: 110px !important;
        line-height: 37px;
        padding-left: 3px;
    }
    .col-xs-1{
        line-height: 37px;
    }
    </style>
</head>
<body>
<g:if test="${flash.message}">
    <div class="alert ${flash.tipo == 'error' ? 'alert-danger' : flash.tipo == 'success' ? 'alert-success' : 'alert-info'} ${flash.clase}">
        <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
        <g:if test="${flash.tipo == 'error'}">
            <i class="fa fa-warning fa-2x pull-left"></i>
        </g:if>
        <g:elseif test="${flash.tipo == 'success'}">
            <i class="fa fa-check-square fa-2x pull-left"></i>
        </g:elseif>
        <g:elseif test="${flash.tipo == 'notFound'}">
            <i class="icon-ghost fa-2x pull-left"></i>
        </g:elseif>
        <p>
            ${flash.message}
        </p>
    </div>
</g:if>

<!-- botones -->
<div class="btn-toolbar toolbar">
    <div class="btn-group">
        <g:link action="bandejaEntrada" controller="tramite" class="btn btn-primary">
            <i class="fa fa-list"></i> Bandeja de entrada
        </g:link>
    </div>
</div>


<div style="margin-top: 30px;padding-bottom: 10px" class="vertical-container">
    <p class="css-vertical-text">Tramite principal</p>
    <div class="linea"></div>

    <div class="row">
        <div class="col-xs-1 col-xs-uno-y-medio negrilla">
            Creado por:
        </div>
        <div class="col-xs-12 negrilla">
            <input type="text" name="" style="width: 708px" class="form-control required label-shared" value="${""+tramite.de.departamento.codigo+": "+tramite.de}" title="Dirección: ${tramite.de.departamento}" disabled>
            el día
            <input type="text" class="form-control required label-shared" maxlength="30" value="${tramite.fechaCreacion?.format('dd-MM-yyyy HH:mm')}" disabled style="width: 160px">
        </div>
    </div>
    <div class="row">
        <div class="col-xs-1 col-xs-uno-y-medio negrilla">
            Para:
        </div>
        <div class="col-xs-4 negrilla" style="padding-left: 0px">
            <input type="text" name=""  class="form-control required " value="${para}" title="Dirección: ${para}" disabled>
        </div>
        <div class="col-xs-1 negrilla">
            Enviado:
        </div>
        <div class="col-xs-2 negrilla" style="padding: 0px">
            <input type="text" class="form-control required "  value="${tramite.fechaEnvio?.format('dd-MM-yyyy HH:mm') }" disabled  style="width: 160px">
        </div>
        <div class="col-xs-1 negrilla">
            Recibido:
        </div>
        <div class="col-xs-2 negrilla" style="padding: 0px">
            <input type="text" class="form-control required "  value="${fechaRecibido}" disabled  style="width: 160px">
        </div>
    </div>

    %{--<div class="row">--}%
    %{--<div class="col-xs-3 negrilla">--}%
    %{--Enviado el:--}%
    %{--<input type="text" class="form-control required label-shared"  maxlength="30" value="${tramite.fechaEnvio?.format('dd-MM-yyyy hh:mm')}" disabled style="width: 160px">--}%
    %{--</div>--}%
    %{--</div>--}%
    <div class="row">
        <div class="col-xs-1 col-xs-uno-y-medio negrilla">
            Código:
        </div>
        <div class="col-xs-3 negrilla" style="padding: 0px">
            <input type="text" class="form-control required "  value="${tramite.codigo+''+tramite.numero }" disabled >
        </div>
        <div class="col-xs-1 negrilla">
            Tipo:
        </div>
        <div class="col-xs-2 negrilla" style="padding: 0px">
            <input type="text" class="form-control required "  value="${tramite.tipoDocumento?.descripcion }" disabled >
        </div>
        <div class="col-xs-1  negrilla">
            Prioridad:
        </div>
        <div class="col-xs-2 negrilla" style="padding: 0px">
            <input type="text" class="form-control required "  value="${tramite.prioridad?.descripcion}" disabled >
        </div>
    </div>

    <div class="row">
        <div class="col-xs-1 col-xs-uno-y-medio negrilla">
            Asunto:
        </div>
        <div class="col-xs-10 negrilla" style="padding: 0px">
            <input type="text" name="" style="width: 905px" class="form-control required" value="${tramite.asunto}" title="Dirección: ${tramite.de.departamento}" disabled>
        </div>
    </div>
</div>
<div style="margin-top: 30px;" class="vertical-container">
    <p class="css-vertical-text">Seguimiento</p>
    <div class="linea"></div>
    <div id="detalle" style="width: 95%;height: 400px;overflow: auto;margin-left:18px ;margin-top: 20px;margin-bottom: 20px;border: 1px solid #000000"></div>
</div>

</body>
</html>