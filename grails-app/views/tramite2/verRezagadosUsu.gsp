<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 18/02/14
  Time: 12:52 PM
--%>


<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Tramites rezagados</title>

    <style type="text/css">


    .etiqueta {
        float: left;
        /*width: 100px;*/
        margin-left: 5px;
        /*margin-top: 5px;*/

    }

    .alert {
        padding: 0;
    }

    .alert-blanco {
        color: #666;
        background-color: #ffffff;
        border-color: #d0d0d0;
    }

    .alertas {
        float: left;
        width: 100px;
        height: 40px;
        margin-left: 20px;
        /*margin-top: -5px;*/
    }

    .cabecera {
        text-align: center;
        font-size: 13px;
    }

    .container-celdas {
        width: 1070px;
        height: 310px;
        float: left;
        overflow: auto;
        overflow-y: auto;
    }
    .enviado{
        background-color:#e0e0e0 ;
        border:1px solid #a5a5a5 ;
    }
    .borrador{
        background-color:#FFFFCC ;
        border:1px solid #eaeab7;
    }
    .table-hover tbody tr:hover td, .table-hover tbody tr:hover th {
        background-color: #FFBD4C;
    }
    tr.E002, tr.revisadoColor td {
        background-color: #DFF0D8! important;
    }
    tr.E001, tr.borrador td {
        background-color: #FFFFCC! important;
    }
    tr.E003, tr.enviado td {
        background-color: #e0e0e0 ! important;
    }
    tr.alerta, tr.alerta td {
        background-color: #f2c1b9;
        font-weight: bold;
    }
    .alertas{
        cursor: pointer;
    }


    </style>

    <link href="${resource(dir: 'css', file: 'custom/loader.css')}" rel="stylesheet">

</head>

<body>


<div id="modalTabelGray"></div>




<div class="row" style="margin-top: 0px; margin-left: 1px">
    <span class="grupo">
        <label class="well well-sm"
               style="text-align: center; float: left">Usuario: ${session.usuario}</label>

    </span>
</div>
<div class="row" style="margin-top: 0px; margin-left: 1px">
    <span class="grupo">
        <label class="alert alert-danger " style="text-align: center; float: left;padding: 10px;padding-top: 5px;height: 35px;line-height: 25px">El sistema se desbloqueará cuando el/los tramites sean recibidos</label>

    </span>
</div>


<div class="btn-toolbar toolbar" style="margin-top: 10px !important">
    <div class="btn-group">
        <g:link action="verRezagadosUsu" class="btn btn-success btnActualizar">
            <i class="fa fa-refresh"></i> Actualizar
        </g:link>

    </div>


</div>


<div id="bandeja" style=";height: 600px;overflow: auto">
    <table class="table table-bordered  table-condensed table-hover">
        <thead>
        <tr>
            <th class="cabecera">Documento</th>
            <th>De</th>
            <th class="cabecera">Para</th>
            <th class="cabecera">Destinatario</th>
            <th class="cabecera">Prioridad</th>
            <th class="cabecera">Fecha Envio</th>
            <th class="cabecera">Fecha Límite</th>
            <th class="cabecera">Estado</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${tramites}" var="pdt">
            <g:set var="limite" value="${pdt.tramite.getFechaLimite()}"/>
            <tr  class="${(limite)?((limite<new Date())?'alerta':pdt.tramite.estadoTramite.codigo):pdt.tramite.estadoTramite.codigo}">
                <td>${pdt.tramite?.codigo}</td>
                <td>${pdt.tramite.de}</td>
                <td >${pdt.departamento}</td>
                <td>${pdt.persona}</td>
                <td>${pdt.tramite?.prioridad.descripcion}</td>
                <td>${pdt.tramite.fechaEnvio?.format("dd-MM-yyyy HH:mm")}</td>
                <td>${limite?limite.format("dd-MM-yyyy HH:mm"):''}</td>
                <td>${pdt.tramite?.estadoTramite.descripcion}</td>
            </tr>
        </g:each>
        </tbody>
    </table>

</div>

</body>
</html>