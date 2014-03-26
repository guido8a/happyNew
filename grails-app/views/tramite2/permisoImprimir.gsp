<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 26/03/14
  Time: 01:03 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
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
    </style>
</head>

<body>


<div></div>

<div>
    <fieldset class="ui-corner-all fieldLista">
        <legend style="margin-bottom: 1px">
            Personal activo
        </legend>
        <ul id="ulDisponibles" style="margin-left:0;max-height: 195px; overflow: auto;" class="fa-ul selectable">
        %{--<g:each in="${disponibles}" var="disp">--}%
        %{--<g:if test="${disp.id.toInteger() < 0}">--}%
        %{--<li data-id="${disp.id}">--}%
        %{--<i class="fa fa-li fa-building-o"></i> ${disp.label}--}%
        %{--</li>--}%
        %{--</g:if>--}%
        %{--<g:else>--}%
        %{--<li data-id="${disp.id}">--}%
        %{--<i class="fa fa-li fa-user"></i> ${disp.label}--}%
        %{--</li>--}%
        %{--</g:else>--}%
        %{--</g:each>--}%
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

        <ul id="ulSeleccionados" style="margin-left:0;max-height: 195px; overflow: auto;" class="fa-ul selectable">

        </ul>
    </fieldset>

</div>

</body>
</html>

