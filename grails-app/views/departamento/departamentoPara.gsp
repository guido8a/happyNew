<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 20/05/16
  Time: 01:09 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Asignar departamentos</title>
</head>

<body>

<div class="linea"></div>


<div class="row">
    <div class="col-md-12">

        <div class="alert alert-success col-md-11" role="alert">
            <span class="glyphicon glyphicon-inbox" aria-hidden="true"></span>
            Departamentos a los cuales <strong>${departamento?.descripcion}</strong> puede enviar trámites.
        </div>

        <label class="col-md-2">
            <h4>Departamentos</h4>
        </label>
        <div id="divDepartamentos" class="col-md-6">

        </div>
        <div class="col-md-4">
            <a href="#" class="btn btn-success" id="btnAgregar" title="Agregar departamento">
                <i class="fa fa-plus"></i>
            </a>
            <a href="#" class="btn btn-primary" id="btnAgregarTodos" title="Agregar todos los departamentos">
                <i class="fa fa-plus"></i> Agregar todos
            </a>
        </div>
    </div>
</div>


<div style="margin-top: 30px; min-height: 400px;font-size: 11px" class="vertical-container">
    <p class="css-vertical-text" style="bottom: -10px">Asignados</p>

    <div class="linea"></div>

    <div class="row">
        <div class="col-md-10" id="divTabla">

        </div>
    </div>


</div>


<script>

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


    $("#btnAgregar").click(function () {
        var dp = $("#departamentoP").val();
        if(dp != null){
            $.ajax({
                type:'POST',
                url:"${createLink(controller: 'departamento', action: 'grabarDepartamento_ajax')}",
                data:{
                    id: ${departamento?.id},
                    dpto: dp
                },
                success:function (msg){
                    if(msg =='ok'){
                        log("Departamento agregado correctamente","success")
                        cargarTablaDepartamentos();
                        cargarDepartamentos();
                    }else{
                        log("Error al agregar el departamento","error")
                    }
                }
            });
        }
    });

    $("#btnAgregarTodos").click(function () {
        openLoader()
        $.ajax({
            type:'POST',
            url:"${createLink(controller: 'departamento', action: 'agregarTodos_ajax')}",
            data:{
                id: ${departamento?.id}
            },
            success:function (msg){
                if(msg =='ok'){
                    log("Departamentos agregados correctamente","success")
                    cargarTablaDepartamentos();
                    cargarDepartamentos();
                    closeLoader()
                }else{
                    log("Error al agregar los departamentos","error")
                }
            }
        });
    });


</script>


</body>
</html>