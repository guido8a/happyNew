<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 18/03/14
  Time: 12:11 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="noMenu">
    <title>Buscar Trámites Externos</title>

    <style type="text/css">

    .css-vertical-text {
        /*position          : absolute;*/
        left              : 5px;
        bottom            : 5px;
        color             : #0088CC;
        border            : 0px solid red;
        writing-mode      : tb-rl;
        -webkit-transform : rotate(270deg);
        -moz-transform    : rotate(270deg);
        -o-transform      : rotate(270deg);
        white-space       : nowrap;
        display           : block;
        width             : 20px;
        height            : 20px;
        font-size         : 25px;
        font-family       : 'Tulpen One', cursive;
        font-weight       : bold;
        font-size         : 35px;
        /*text-shadow       : -2px 2px 1px rgba(0, 0, 0, 0.25);*/

        /*text-shadow: 0px 0px 1px #333;*/
    }

    .tituloChevere {

        color       : #0088CC;
        border      : 0px solid red;
        white-space : nowrap;
        display     : block;
        /*width       : 98%;*/
        height      : 25px;
        font-family : 'open sans condensed';
        font-weight : bold;
        font-size   : 16px;
        /*text-shadow : -2px 2px 1px rgba(0, 0, 0, 0.25);*/
        /*margin-top  : 10px;*/
        line-height : 18px;

        /*text-shadow: 0px 0px 1px #333;*/
    }

    .table-hover tbody tr:hover td, .table-hover tbody tr:hover th {
        background-color: #FFBD4C;
    }

    .negrilla {
        font-weight: bold;

    }


    </style>



</head>

<body>



<div class="buscar" style="margin-bottom: 20px">

    <fieldset>
        <legend class="negrilla">Búsqueda de Trámites Externos</legend>

        <div>
            <div class="col-md-2">
                <label for="memorando">Documento</label>
                <g:textField name="memorando" value="" maxlength="15" class="form-control" style="width: 200px; margin-right: 20px"/>
            </div>

            <div style="padding-top: 22px">
                <a href="#" name="busqueda" class="btn btn-success btnBusqueda" style="margin-left: 35px"><i class="fa fa-check-square-o"></i> Buscar</a>
            </div>

        </div>


    </fieldset>

</div>

<div id="tabla">

</div>

<script type="text/javascript">


    function loading(div) {
        y = 0;
        $("#" + div).html("<div class='tituloChevere' id='loading'>Cargando, Espere por favor</div>")
        var interval = setInterval(function () {
            if (y == 30) {
                $("#detalle").html("<div class='tituloChevere' id='loading'>Cargando, Espere por favor</div>")
                y = 0
            }
            $("#loading").append(".");
            y++
        }, 500);
        return interval
    }



    $(".btnBusqueda").click(function () {

        var interval = loading("tabla")

        var memorando = $("#memorando").val();

        var datos = "memorando=" + memorando

        $.ajax ({ type : "POST", url: "${g.createLink(controller: 'busquedaExternos', action: 'seguimientoExternos')}",
            data: datos,
            success: function (msg) {
                clearInterval(interval)
                $("#tabla"). html(msg);

            }
        });

    });



</script>




</body>
</html>