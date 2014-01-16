<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Creación de trámites o documentos principales</title>
    <link href='${resource(dir: "css", file: "CustomSvt.css")}' rel='stylesheet' type='text/css'>
    <style>
    .filaDest{
        width: 95%;
        height: 20px;
        border-bottom: 1px solid black;
        margin: 10px;
        vertical-align: middle;
        text-align: left;
        line-height: 10px;
        padding-left: 10px;
        padding-bottom: 20px;
        font-size: 10px;
    }
    .span-rol{
        padding-right: 10px;
        padding-left: 10px;
        height: 16px;
        line-height: 16px;
        background: #FFBD4C;
        margin-right: 5px;
        font-weight: bold;
        font-size: 12px;
    }
    .span-eliminar{
        padding-right: 10px;
        padding-left: 10px;
        height: 16px;
        line-height: 16px;
        background: rgba(255, 2, 10, 0.35);
        margin-right: 5px;
        font-weight: bold;
        font-size: 12px;
        cursor: pointer;
        float: right;
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
<div style="vertical-align: middle;float: left;width: 100%" class="vertical-container"  >
    <p class="css-vertical-text">Destinatarios</p>
    <div class="linea"></div>

    <div style="width: 300px;height: 250px;margin: 10px;padding: 15px;float: left">
        <div class="row negrilla">
            Direccion:
            <select name="direc" id="direccion" class="many-to-one form-control">
                <g:each in="${happy.tramites.Departamento.list(['sort':'descripcion'])}" var="d">
                    <option value="${d.id}" cod="${d.codigo}">${d.descripcion}</option>
                </g:each>
            </select>
            %{--<g:select name="direc" id="direccion" class="many-to-one form-control" from="${happy.tramites.Departamento.list(['sort':'descripcion'])}" value="" optionKey="id" optionValue="descripcion"></g:select>--}%
        </div>
        <div class="row negrilla">
            Usuario:
            <div id="div_usuarios">
                <g:select name="usuario" id="usuario" class="many-to-one form-control" from="" value="" ></g:select>
            </div>
        </div>
        <div class="row negrilla">
            Rol:
            <g:select name="rol" id="rol" class="many-to-one form-control" from="${happy.tramites.RolPersonaTramite.findAllByCodigoIlike('R%')}" value="" optionKey="id" optionValue="descripcion" ></g:select>
        </div>
    </div>
    <div style="width: 100px;height: 250px;margin: 10px;padding: 15px;float: left">
        <div class="row negrilla" style="text-align: center;margin-top: 100px">
            <a href="#" id="agregar-usu" class="btn btn-primary" style="margin: auto">Agregar</a>
        </div>
    </div>
    <fieldset style="width: 500px;height: 250px;border: 1px solid #0088CC;margin: 10px;padding: 15px;float: left;margin-bottom: 20px;" class="ui-corner-all" id="dest">
        <legend style="margin-bottom: 1px">
            Destinatarios:
        </legend>

    </fieldset>



</div>
<script type="text/javascript">
    $("#direccion").change(function(){
        $.ajax({
            url     : '${createLink(controller: "tramite", action: "cargaUsuarios")}',
            data :"dir="+$("#direccion").val(),
            success : function (msg) {
//                console.log(msg)
                $("#div_usuarios").html(msg)
            }
        });
    })

    $("#agregar-usu").click(function(){
        var usu = $("#usuario").val()
        var rol = $("#rol").val()
        var verificacion=false
        var band=true
        var message
        if(usu*1<1){
            message="<b>Por favor, escoja un usuario</b>"
            band=false
        }
        if($(".para").size()>0 && rol=="1" ){
            message="<b>Solo puede asignar un destinatario con el rol : PARA</b>"
            band=false
        }
        if($(".filaDest").size()==6 ){
            message="<b>Ya ha asignado el máximo de 6 destinatarios</b>"
            band=false
        }

        if(band){
            var div = $("<div class='filaDest ui-corner-all'>")
            var span = $("<span class='span-eliminar ui-corner-all' title='Click para eliminar'>Eliminar</span>")
            div.html("<span class='span-rol ui-corner-all'>"+$("#rol option:selected").text()+"</span>"+$("#direccion option:selected").attr("cod")+": "+$("#usuario option:selected").text())
            div.append(span)
            if(rol=="1")
                div.addClass("para")
            div.attr("prsn",$("#usuario").val())
            div.attr("rol",rol)
            span.bind("click",function(){
                $(this).parent().remove()
            })
            $("#dest").append(div)
        }else{
            bootbox.alert(message)
        }

    })
    $(".span-eliminar").click(function(){
        $(this).parent().remove()
    })
</script>

</body>
</html>