<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Asignar departamentos</title>

    <style>

    option.selected {
        background : #DDD;
        color      : #999;
    }

    li {
    }

    .selectable li {
        cursor        : pointer;
        border-bottom : solid 1px #0088CC;
        margin-left   : 20px;
    }

    .selectable li:hover {
        background : #B5D1DF;
    }

    .selectable li.selected {
        background : #81B5CF;
        color      : #0A384F;
    }

    .divFieldsListas {
        height     : 685px;
        width      : 970px;
        overflow-x : auto;
    }

    .fieldLista {
        width   : 450px;
        height  : 650px;
        border  : 1px solid #0088CC;
        margin  : 10px 10px 20px 10px;
        padding : 15px;
        float   : left;
    }

    .divBotones {
        width      : 30px;
        height     : 130px;
        margin-top : 75px;
        float      : left;
    }

    .vertical-container {
        padding-bottom : 10px;;
    }

    .texto {
        max-height : 80px;
        overflow   : auto;
        background : #EFE4D1;
        padding    : 3px;
    }

    .claseMin {
        max-height : 60px;
        overflow   : auto;
    }
    </style>
</head>

<body>


<div class="row">
    <div class="col-md-12">

        <div class="alert alert-success col-md-11" role="alert" style="height: 45px">
            <span class="glyphicon glyphicon-inbox" aria-hidden="true"></span>
            <span style="font-size: large">Departamentos a los cuales <strong>${departamento?.descripcion}</strong>
                puede enviar trámites.</span>
        </div>

        %{--<label class="col-md-2">--}%
            %{--<h4>Departamentos</h4>--}%
        %{--</label>--}%
        %{--<div id="divDepartamentos" class="col-md-6">--}%

        %{--</div>--}%
        %{--<div class="col-md-4">--}%
            %{--<a href="#" class="btn btn-success" id="btnAgregar" title="Agregar departamento">--}%
                %{--<i class="fa fa-plus"></i>--}%
            %{--</a>--}%
            %{--<a href="#" class="btn btn-primary" id="btnAgregarTodos" title="Agregar todos los departamentos">--}%
                %{--<i class="fa fa-plus"></i> Agregar todos--}%
            %{--</a>--}%
            %{--<a href="#" class="btn btn-danger" id="btnEliminarTodos" title="Agregar todos los departamentos">--}%
                %{--<i class="fa fa-plus"></i> Eliminar todos--}%
            %{--</a>--}%
        %{--</div>--}%


        %{--<div style="margin-top: 30px; min-height: 400px;font-size: 11px" class="vertical-container">--}%
        %{--<p class="css-vertical-text" style="bottom: -10px">Puede enviar trámites para</p>--}%

        %{--<div class="linea"></div>--}%

        %{--<div class="row">--}%
        %{--<div class="col-md-10" id="divTabla">--}%

        %{--</div>--}%
        %{--</div>--}%


        %{--</div>--}%

            <div class="divFieldsListas">
                <fieldset class="ui-corner-all fieldLista">
                    <legend style="margin-bottom: 1px">
                        Disponibles
                    </legend>

                    <ul id="ulDisponibles" style="margin-left:0;max-height: 575px; overflow: auto;" class="fa-ul selectable">
                        <g:each in="${disponibles}" var="disp">
                                <li data-id="${disp.id}" class="clickable interno">
                                    <i class="fa fa-li fa-building"></i> ${disp.descripcion}
                                </li>
                        </g:each>
                    </ul>
                </fieldset>

                <div class="divBotones">
                    <div class="btn-group-vertical">
                        <a href="#" class="btn btn-default" title="Agregar todos" id="btnAddAll">
                            <i class="fa fa-angle-double-right"></i>
                        </a>
                        <a href="#" class="btn btn-default" title="Agregar seleccionados" id="btnAddSelected">
                            <i class="fa fa-angle-right"></i>
                        </a>
                        <a href="#" class="btn btn-default" title="Quitar seleccionados" id="btnRemoveSelected">
                            <i class="fa fa-angle-left"></i>
                        </a>
                        <a href="#" class="btn btn-default" title="Quitar todos" id="btnRemoveAll">
                            <i class="fa fa-angle-double-left"></i>
                        </a>
                    </div>
                </div>

                <fieldset class="ui-corner-all fieldLista">
                    <legend style="margin-bottom: 1px">
                        Seleccionados
                    </legend>

                    <ul id="ulSeleccionados" style="margin-left:0;max-height: 600px; overflow: auto;" class="fa-ul selectable">
                            <g:each in="${paras}" var="para">
                                    <li data-id="${para.id}" class="clickable">
                                        <i class="fa fa-li fa-building-o"></i> ${para.deparatamentoPara.descripcion}
                                    </li>
                            </g:each>
                    </ul>
                </fieldset>
            </div>
    </div>
</div>

<script type="text/javascript">



    $(".selectable li").click(function () {
        $(this).toggleClass("selected");
    });
    $("#btnAddAll").click(function () {
        openLoader();
        agregarTodos();
    });
    $("#btnAddSelected").click(function () {

        var seles = [];
        $("#ulDisponibles").find("li.selected").removeClass("selected").each(function () {
            var id = $(this).data("id");
            seles += id + ","
        });

        if(seles == ""){
            log("Seleccione al menos un departamento","error")
        }else{
            openLoader();
            agregarDepartamentos(seles);
        }

    });
    $("#btnRemoveSelected").click(function () {

        var seles = [];
        $("#ulSeleccionados").find("li.selected").removeClass("selected").each(function () {
            var id = $(this).data("id");
            seles += id + ","
        });

        if(seles == ''){
            log("Seleccione al menos un departamento","error")
        }else{
            openLoader();
            quitarDepartamentos(seles)
        }
    });

    $("#btnRemoveAll").click(function () {
      openLoader();
      quitarTodos();
    });


    function agregarDepartamentos (seleccionados) {
        $.ajax({
            type: 'POST',
            url: '${createLink(controller: 'departamento', action: 'agregarDepartamentos_ajax')}',
            data:{
                sele: seleccionados,
                id: '${departamento?.id}'
            },
            success: function (msg){
                if(msg == 'ok'){
                    closeLoader();
                    location.href="${createLink(controller: 'departamento', action: 'departamentoPara')}/" + '${departamento?.id}'
                }else{
                    log("Error al agregar los departamentos","error")
                    closeLoader();
                }
            }
        })
    }

    function quitarDepartamentos (seleccionados){
        $.ajax({
            type: 'POST',
            url: '${createLink(controller: 'departamento', action: 'quitarDepartamentos_ajax')}',
            data:{
                sele: seleccionados
            },
            success: function (msg){
                if(msg == 'ok'){
                    closeLoader();
                    location.href="${createLink(controller: 'departamento', action: 'departamentoPara')}/" + '${departamento?.id}'
                }else{
                    log("Error al borrar los departamentos","error")
                    closeLoader();
                }
            }
        })
    }

    function agregarTodos () {
        $.ajax({
            type:'POST',
            url:"${createLink(controller: 'departamento', action: 'agregarTodos_ajax')}",
            data:{
                id: ${departamento?.id}
            },
            success:function (msg){
                if(msg =='ok'){
                    log("Departamentos agregados correctamente","success");
                    closeLoader();
                    location.href="${createLink(controller: 'departamento', action: 'departamentoPara')}/" + '${departamento?.id}'
                }else{
                    log("Error al agregar los departamentos","error");
                    closeLoader()
                }
            }
        });
    }

    function quitarTodos () {
        $.ajax({
            type:'POST',
            url:"${createLink(controller: 'departamento', action: 'eliminarTodos_ajax')}",
            data:{
                id: ${departamento?.id}
            },
            success:function (msg){
                if(msg =='ok'){
                    log("Departamentos eliminados correctamente","success");
                    location.href="${createLink(controller: 'departamento', action: 'departamentoPara')}/" + '${departamento?.id}'
                    closeLoader()
                }else{
                    log("Error al eliminar los departamentos","error")
                    closeLoader()
                }
            }
        });
    }

    //antiguo
    cargarDepartamentos();

    function cargarDepartamentos (){
        $.ajax({
            type:'POST',
            url:"${createLink(controller: 'departamento', action: 'departamentos_ajax')}",
            data:{
                id: ${departamento?.id}
            },
            success:function (msg){
                $("#divDepartamentos").html(msg)
            }
        });
    }

    cargarTablaDepartamentos();


    function cargarTablaDepartamentos () {
        $.ajax({
            type:'POST',
            url:"${createLink(controller: 'departamento', action: 'tablaDepartamentos_ajax')}",
            data:{
                id: ${departamento?.id}
            },
            success:function (msg){
                $("#divTabla").html(msg)
            }
        });
    }


    %{--$("#btnAgregar").click(function () {--}%
        %{--var dp = $("#departamentoP").val();--}%
        %{--if(dp != null){--}%
            %{--$.ajax({--}%
                %{--type:'POST',--}%
                %{--url:"${createLink(controller: 'departamento', action: 'grabarDepartamento_ajax')}",--}%
                %{--data:{--}%
                    %{--id: ${departamento?.id},--}%
                    %{--dpto: dp--}%
                %{--},--}%
                %{--success:function (msg){--}%
                    %{--if(msg =='ok'){--}%
                        %{--log("Departamento agregado correctamente","success")--}%
                        %{--cargarTablaDepartamentos();--}%
                        %{--cargarDepartamentos();--}%
                    %{--}else{--}%
                        %{--log("Error al agregar el departamento","error")--}%
                    %{--}--}%
                %{--}--}%
            %{--});--}%
        %{--}--}%
    %{--});--}%

    %{--$("#btnAgregarTodos").click(function () {--}%
        %{--var titulo = "Agregar todos los departamentos como destinatarios de<br>" + "${departamento?.descripcion}"--}%
        %{--bootbox.confirm(titulo, function (result) {--}%
            %{--if (result) {--}%
                %{--openLoader();--}%
                %{--openLoader()--}%
                %{--$.ajax({--}%
                    %{--type:'POST',--}%
                    %{--url:"${createLink(controller: 'departamento', action: 'agregarTodos_ajax')}",--}%
                    %{--data:{--}%
                        %{--id: ${departamento?.id}--}%
                    %{--},--}%
                    %{--success:function (msg){--}%
                        %{--if(msg =='ok'){--}%
                            %{--log("Departamentos agregados correctamente","success")--}%
                            %{--cargarTablaDepartamentos();--}%
                            %{--cargarDepartamentos();--}%
                            %{--closeLoader()--}%
                        %{--}else{--}%
                            %{--log("Error al agregar los departamentos","error")--}%
                        %{--}--}%
                    %{--}--}%
                %{--});--}%

            %{--}--}%
        %{--});--}%
    %{--});--}%

    %{--$("#btnEliminarTodos").click(function () {--}%
        %{--var titulo = "Eliminar  todos los departamentos ingresados para <br>" + "${departamento?.descripcion}"--}%
        %{--bootbox.confirm(titulo, function (result) {--}%
            %{--if (result) {--}%
                %{--openLoader()--}%
                %{--$.ajax({--}%
                    %{--type:'POST',--}%
                    %{--url:"${createLink(controller: 'departamento', action: 'eliminarTodos_ajax')}",--}%
                    %{--data:{--}%
                        %{--id: ${departamento?.id}--}%
                    %{--},--}%
                    %{--success:function (msg){--}%
                        %{--if(msg =='ok'){--}%
                            %{--log("Departamentos eliminados correctamente","success")--}%
                            %{--cargarTablaDepartamentos();--}%
                            %{--cargarDepartamentos();--}%
                            %{--closeLoader()--}%
                        %{--}else{--}%
                            %{--log("Error al eliminar los departamentos","error")--}%
                        %{--}--}%
                    %{--}--}%
                %{--});--}%

            %{--}--}%
        %{--});--}%
    %{--});--}%


</script>


</body>
</html>