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
        <g:link action="bandejaSalida" controller="tramite2" class="btn btn-primary">
            <i class="fa fa-list"></i> Bandeja de salida
        </g:link>
        <g:if test="${tramite.estadoTramite.codigo!='E002'}">
            <a href="#" id="rev" class="btn btn-success">
                <i class="fa fa-check"></i>
                Revisado y listo para enviar
            </a>
        </g:if>
        <div style="display: inline;margin-left: 10px;height: 37px;line-height: 37px"> Estado del tramite: <span style="font-weight: bold">${tramite.estadoTramite.descripcion}</span></div>
    </div>
</div>
<elm:headerTramite tramite="${tramite}"/>
<div style="margin-top: 15px;" class="vertical-container">
    <div id="detalle" style="width: 95%;height: 450px;overflow: auto;margin-left:-15px ;margin-top: 5px;margin-bottom: 20px;">
        <util:textoTramite tramite="${tramite.id}"/>
    </div>
</div>
<div class="vertical-container" style="margin-top: 25px;color: black;padding-bottom: 10px;margin-bottom: 20px">
    <p class="css-vertical-text">Observaciones</p>
    <div class="linea"></div>
    <div class="row">
        <textarea id="notas" class="form-control" style="width: 95%;height: 200px;" maxlength="1023" title="notas u observaciones" ${(tramite.estadoTramite.codigo!='E001')?'disabled':''}>${tramite.nota}</textarea>
    </div>
    <div class="row">
        <g:if test="${tramite.estadoTramite.codigo=='E001'}">
            <a href="#" id="save-notes" class="btn btn-primary">
                <i class="fa fa-save"></i>
                Guardar
            </a>
        </g:if>
    </div>
</div>
<script type="text/javascript">

    $("#save-notes").click(function(){
        openLoader("Guardando")
        $.ajax({
            type    : "POST",
            url     : "${g.createLink(controller: 'tramite2',action: 'saveNotas')}",
            data    : "tramite=${tramite.id}&notas=" + $("#notas").val(),
            success : function (msg) {
                closeLoader()
                if(msg=="ok"){
                    bootbox.alert("Datos guardados exitosamente")
                }else{
                    bootbox.alert("Ha ocurrido un error")
                }
            }
        });
    });
    $("#rev").click(function(){
       // console.log("wtf")
        bootbox.confirm("Esta seguro?.<br>Una vez revisado no se podran hacer modificaciones u observaciones y el tramite estará disponible para ser enviado.",function(result){
            if(result){
                openLoader()
                $.ajax({
                    type    : "POST",
                    url     : "${g.createLink(controller: 'tramite2',action: 'revisar')}",
                    data    : "id=${tramite.id}",
                    success : function (msg) {
                        closeLoader()
                        if(msg=="ok")
                            location.reload(true);
                        else
                            bootbox.alert("Ha ocurrido un error.")
                    },
                    error   : function () {
                        closeLoader()
                        bootbox.alert("Ha ocurrido un error.")
                    }
                });
            }

        })
    });
</script>

</body>
</html>