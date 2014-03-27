<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 26/03/14
  Time: 01:03 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Permiso de Imprimir para el trámite</title>
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

    .fieldLista {
        width   : 450px;
        height  : 250px;
        border  : 1px solid #0088CC;
        margin  : 10px 10px 20px 10px;
        padding : 15px;
        /*float   : left;*/
    }

    .divBotones {
        width      : 30px;
        height     : 130px;
        margin-top : 75px;
        /*float      : left;*/
    }

    .vertical-container {
        padding-bottom : 10px;;
    }
    </style>
</head>

<body>

<div>
    <fieldset class="ui-corner-all fieldLista">
        <legend style="margin-bottom: 1px">
            Personal activo
        </legend>
        <ul id="ulDisponibles" style="margin-left:0;max-height: 195px; overflow: auto;" class="fa-ul selectable">
        <g:each in="${activos}" var="perso">
        <li data-id="${perso.id}"><i class="fa fa-user"></i>  ${perso.nombre} ${perso.apellido}</li>
        </g:each>
        </ul>
    </fieldset>

    <div style="margin-left: 150px">
        <div class="btn-group">
            <a href="#" class="btn btn-default" title="Agregar todos" id="btnAddAll">
                <i class="fa fa-angle-double-down"></i>
            </a>
            <a href="#" class="btn btn-default" title="Agregar seleccionados" id="btnAddSelected">
                <i class="fa fa-angle-down"></i>
            </a>
            <a href="#" class="btn btn-default" title="Quitar seleccionados" id="btnRemoveSelected">
                <i class="fa fa-angle-up"></i>
            </a>
            <a href="#" class="btn btn-default" title="Quitar todos" id="btnRemoveAll">
                <i class="fa fa-angle-double-up"></i>
            </a>
        </div>
    </div>

    <fieldset class="ui-corner-all fieldLista">
        <legend style="margin-bottom: 1px">
            Seleccionados
        </legend>

        <ul id="ulSeleccionados" style="margin-left:0;max-height: 195px; overflow: auto;" class="fa-ul selectable">

        </ul>
    </fieldset>

</div>
<script type="text/javascript">

    $(".selectable li").click(function () {
        $(this).toggleClass("selected");
    });

    $("#btnAddAll").click(function () {
        $("#ulDisponibles li").removeClass("selected").appendTo($("#ulSeleccionados"));
    });
    $("#btnAddSelected").click(function () {
        $("#ulDisponibles li.selected").removeClass("selected").appendTo($("#ulSeleccionados"));
    });
    $("#btnRemoveSelected").click(function () {
        $("#ulSeleccionados li.selected").removeClass("selected").appendTo($("#ulDisponibles"));
    });
    $("#btnRemoveAll").click(function () {
        $("#ulSeleccionados li").removeClass("selected").appendTo($("#ulDisponibles"));
    });


</script>

</body>
</html>
