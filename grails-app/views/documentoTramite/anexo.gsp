<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 3/21/14
  Time: 3:23 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Anexos</title>
    <style type="text/css">
    .file{
        width: 100%;
        height: 40px;
        margin: 0px;
        position: absolute;
        top: 0px;
        left: 0px;
        opacity: 0;
    }
    #files{
        width: 950px;
    }
    .fileContainer{
        width: 100%;
        height: 290px;
        border: 2px solid #327BBA;
        padding: 15px;
        margin-top:10px;
        margin-bottom: 10px;
    }
    .etiqueta{
        font-weight: bold;
    }
    .titulo-archivo{
        font-weight: bold;
        font-size: 20px;
    }
    </style>

</head>

<body>
<elm:headerTramite tramite="${tramite}" extraTitulo="- Cargar anexos"/>


<span class="btn btn-success fileinput-button" style="position: relative;height: 40px;margin-top: 10px">
    <i class="glyphicon glyphicon-plus"></i>
    <span>Seleccionar archivos</span>
    <input type="file" name="file" id="file" class="file" multiple>
</span>

<div id="progress" class="progress progress-striped active hide">
    <div class="progress-bar progress-bar-success"></div>
</div>

<div id="files">
    %{--<div class="fileContainer ui-corner-all">--}%
    %{--<div class='row'>--}%
    %{--<div class='col-md-1 etiqueta'>Resumen</div>--}%
    %{--<div class='col-md-5 '>--}%
    %{--<textarea class='form-control' required id='resumen' name='resumen' cols='5' rows='5'></textarea>--}%
    %{--</div>--}%
    %{--<div class='col-md-1 etiqueta'>Descripción</div>--}%
    %{--<div class='col-md-5'>--}%
    %{--<textarea class='form-control' required id='descripcion' name='descripcion' cols='5' rows='5'></textarea>--}%
    %{--</div>--}%
    %{--</div>--}%
    %{--<div class='row'>--}%
    %{--<div class='col-md-1 etiqueta'>Palabras clave</div>--}%
    %{--<div class='col-md-11'>--}%
    %{--<input type='text' class='form-control' id='clave' name='clave'/>--}%
    %{--</div>--}%
    %{--</div>--}%
    %{--<div class='row' style='text-align: right'>--}%
    %{--<a href='#' class='btn btn-azul subir' style='margin-right: 15px'>--}%
    %{--<i class="fa fa-upload"></i>--}%
    %{--Subir Archivo--}%
    %{--</a>--}%
    %{--</div>--}%
    %{--</div>--}%
</div>


<script type="text/javascript">
    var okContents = {
        'image/png'  : "png",
        'image/jpeg' : "jpeg",
        'image/jpg'  : "jpg",

        'application/pdf' : 'pdf',

        'application/excel'                                                 : 'xls',
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : 'xlsx',

        'application/mspowerpoint'                                                  : 'pps',
        'application/vnd.ms-powerpoint'                                             : 'pps',
        'application/powerpoint'                                                    : 'ppt',
        'application/x-mspowerpoint'                                                : 'ppt',
        'application/vnd.openxmlformats-officedocument.presentationml.slideshow'    : 'ppsx',
        'application/vnd.openxmlformats-officedocument.presentationml.presentation' : 'pptx',

        'application/msword'                                                      : 'doc',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document' : 'docx',

        'application/vnd.oasis.opendocument.text'         : 'odt',
        'application/vnd.oasis.opendocument.presentation' : 'odp',
        'application/vnd.oasis.opendocument.spreadsheet'  : 'ods'
    };
    function reset(){
        $(".fileContainer").remove()
    }
    function createContainer(){
        var file = document.getElementById("file");

        var next = $(".fileContainer").size()
        if(isNaN(next))
            next=1
        else
            next++
        var ar = file.files[next-1]
        var div = $('<div class="fileContainer ui-corner-all d-'+next+'">')
        var row1 = $("<div class='row'>")
        var row2 = $("<div class='row'>")
        var row3 = $("<div class='row'  style='text-align: right'>")
        var row4 = $("<div class='row'>")
        row1.append(" <div class='col-md-1 etiqueta'>Resumen</div>")
        row1.append("<div class='col-md-5'><textarea class='form-control  "+next+"' required id='resumen' name='resumen' cols='5' rows='5'></textarea> </div>")
        row1.append("<div class='col-md-1 etiqueta'>Descripción</div>")
        row1.append("<div class='col-md-5'><textarea class='form-control "+next+"' required id='descripcion' name='descripcion' cols='5' rows='5'></textarea></div>")
        row2.append(" <div class='col-md-1 etiqueta'>Palabras clave</div>")
        row2.append(" <div class='col-md-11'><input type='text' class='form-control "+next+"' id='clave' name='clave'/></div>")
        row3.append(" <a href='#' class='btn btn-azul subir' style='margin-right: 15px' clase='"+next+"'><i class='fa fa-upload'></i> Subir Archivo</a>")
        div.append("<div class='row' style='margin-top: 0px'><div class='titulo-archivo col-md-7'><span style='color: #327BBA'>Archivo:</span> "+ar.name+"</div></div>")
        div.append(row1)
        div.append(row2)
        div.append(row3)
        $("#files").append(div)
    }
    function boundBotones(){
        $(".subir").unbind("click")
        $(".subir").bind("click",function(){
            error = false
            $("."+$(this).attr("clase")).each(function(){
                if($(this).val().trim()==""){
                    error = true
                }
            })
            if(error){
                bootbox.alert("llene todos los campos")
            }else{
                /*Aqui subir*/
                upload($(this).attr("clase")*1-1)
            }
        });
    }
    var request = new XMLHttpRequest();
    function upload(indice)
    {
        $(".d-"+(indice+1)).addClass("subiendo")
        var tramite = "${tramite.id}"
        var file = document.getElementById("file");
        /* Create a FormData instance */
        var formData = new FormData();
        formData.append("file", file.files[indice]);
        formData.append("id",tramite)
        $("."+(indice+1)).each(function(){
//            console.log($(this))
            formData.append($(this).attr("name"),$(this).val())
        })
        request.open("POST", "${g.createLink(controller: 'documentoTramite',action: 'uploadSvt')}");
        request.send(formData);
    }

    /* Check the response status */
    request.onreadystatechange = function()
    {
        if (request.readyState == 4 && request.status == 200)
        {
            $(".subiendo").html("<i class='fa fa-check' style='color:#327BBA;margin-right: 10px'></i> "+$(".subiendo").find(".titulo-archivo").html()+" subido exitosamente").css({height:50,fontWeight:"bold"}).removeClass("subiendo")

        }
    }

    $(function () {
        var archivos = []
        function btnCerrar($panel, $footer, isError) {
            var clase = isError ? "danger" : "success";
            var icon = isError ? "times" : "check";
            var $btnCerrar = $("<a href='#' class='btn btn-" + clase + "'/>");
            $btnCerrar.append("<i class='fa fa-" + icon + "'></i>");
            $btnCerrar.append("Cerrar");
            $footer.html($btnCerrar);
            $btnCerrar.click(function () {
                $panel.hide({
                    effect   : "fold",
                    duration : 800,
                    complete : function () {
                        $panel.remove();
                    }
                });
            });
        }

        $("#file").change(function(){
            reset();
            archivos = $(this)[0].files
            console.log(archivos)
            for(arch in archivos){
                if(arch!="length" && arch!="item"){
                    createContainer()
                    boundBotones()
                }

            }

        });
    });
</script>
</body>
</html>