<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <link href='${resource(dir: "css", file: "CustomSvt.css")}' rel='stylesheet' type='text/css'>
    <title>Revisar tramite</title>
    <style>
    .negrilla{
        padding-left: 0px;
    }
    .col-xs-1{
        line-height: 25px;
    }
    .col-buen-height{
        line-height: 25px;
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
            <i class="fa fa-list"></i> Bandeja de salida
        </g:link>
    </div>
</div>


<div style="margin-top: 30px;padding-bottom: 10px" class="vertical-container">

    <div class="titulo-azul titulo-horizontal" style="margin-left: -50px">
        ${tramite.tipoDocumento?.descripcion }
    </div>
    <div class="row row-low-margin-top" style="margin-top: 5px;">
        <div class="col-xs-4 negrilla" style="padding-left: 0px;margin-top: 2px" >
            No. <span style="font-weight: 500">${tramite.codigo}</span>
        </div>
    </div>
    <div class="row row-low-margin-top" >
        <div class="col-xs-1  negrilla negrilla-puntos">
            DE
        </div>
        <div class="col-xs-10  col-buen-height">
            ${tramite.de.departamento.descripcion}
        </div>
    </div>
    <g:if test="${para}">
        <div class="row row-low-margin-top" >
            <div class="col-xs-1  negrilla negrilla-puntos">
                PARA
            </div>
            <div class="col-xs-10  col-buen-height">
                ${para}
            </div>
        </div>
    </g:if>
    <div class="row row-low-margin-top" >
        <div class="col-xs-1  negrilla negrilla-puntos">
            FECHA
        </div>
        <div class="col-xs-10  col-buen-height">
            <util:fechaConFormato fecha="${tramite.fechaCreacion}" ciudad="Quito"/>
        </div>
    </div>
    <div class="row row-low-margin-top" >
        <div class="col-xs-1  negrilla negrilla-puntos">
            ASUNTO
        </div>
        <div class="col-xs-10  col-buen-height">
            ${tramite.asunto}
        </div>
    </div>

</div>
<div style="margin-top: 15px;" class="vertical-container">
    <div id="detalle" style="width: 95%;height: 500px;overflow: auto;margin-left:-15px ;margin-top: 5px;margin-bottom: 20px;border: 1px solid #000000">
        %{--<plaintext>--}%
        <util:textoTramite tramite="${tramite.id}"/>


        %{--</plaintext>--}%
    </div>
</div>

<script type="text/javascript">

    %{--console.log('${tramite.texto.encodeAsHTML()}');--}%
    %{--$("#detalle").html('${tramite.texto}');--}%

</script>

</body>
</html>