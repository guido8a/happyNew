<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Creación de trámites o documentos principales</title>
    <link href='${resource(dir: "css", file: "CustomSvt.css")}' rel='stylesheet' type='text/css'>
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
        <g:link action="form" class="btn btn-default btnCrear">
            <i class="fa fa-file-o"></i> Crear
        </g:link>
    </div>
</div>


<div style="" class="vertical-container">
    <g:form class="frmRubro" action="save">
        <p class="css-vertical-text">Tramite</p>
        <div class="linea"></div>
        <div class="row">
            <div class="col-xs-3 negrilla">
                De:
                <input type="text" name="de.de" class="form-control required label-shared"  id="de" maxlength="30"  value="${de.nombre}"  title="${de.nombre}" disabled>
            </div>
            <div class="col-xs-3 negrilla">
                Creado el:
                <input type="text" name="de.de" class="form-control required label-shared"  id="creado" maxlength="30"  value="${fecha.format('dd-MM-yyyy  HH:mm')}" disabled style="width: 150px">
            </div>
        </div>
        <div class="row">
            <div class="col-xs-3 negrilla">
                Tipo de documento:
                <g:select name="tramite.tipoDocumento.id" class="many-to-one form-control" from="${happy.tramites.TipoDocumento.list(['sort':'descripcion'])}" value="" optionKey="id" optionValue="descripcion"></g:select>
            </div>
            <div class="col-xs-2 negrilla">
                Prioridad:
                <g:select name="tramite.prioridad.id" class="many-to-one form-control" from="${happy.tramites.TipoPrioridad.list(['sort':'tiempo',order: 'desc'])}" value="" optionKey="id" optionValue="descripcion"></g:select>
            </div>
            <div class="col-xs-3 negrilla">
                Fecha límite de respuesta:
                <elm:datetimepicker name="fechaLimiteRespuesta" title="Fecha límite de respuesta " class="datepicker form-control" value="" default="none" noSelection="['': '']"/>
            </div>
            <div class="col-xs-2 negrilla">
                <br/>
                Externo:
                <input type="checkbox" id="externo" style="width: 30px">
            </div>
        </div>
        <div class="row">
            <div class="col-xs-12 negrilla">
                Asunto:
                <input type="text" name="tramite.asunto" class="form-control required"  id="asunto" maxlength="1023" style="width: 900px;display: inline" >
            </div>
        </div>
    </g:form>
</div>


</body>
</html>