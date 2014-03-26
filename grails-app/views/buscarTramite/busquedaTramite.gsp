<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 14/03/14
  Time: 11:18 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Búsqueda de Trámites</title>


    <style type="text/css">

     .container-celdas {
        width: 1070px;
        height: 310px;
        float: left;
        overflow: auto;
        overflow-y: auto;
    }


/*
    .css-vertical-text {
        */
/*position          : absolute;*//*

        left: 5px;
        bottom: 5px;
        color: #0088CC;
        border: 0px solid red;
        writing-mode: tb-rl;
        -webkit-transform: rotate(270deg);
        -moz-transform: rotate(270deg);
        -o-transform: rotate(270deg);
        white-space: nowrap;
        display: block;
        width: 20px;
        height: 20px;
        font-size: 25px;
        font-family: 'Tulpen One', cursive;
        font-weight: bold;
        font-size: 35px;
        */
/*text-shadow       : -2px 2px 1px rgba(0, 0, 0, 0.25);*//*


        */
/*text-shadow: 0px 0px 1px #333;*//*

    }
*/

/*
    .tituloChevere {

        color: #0088CC;
        border: 0px solid red;
        white-space: nowrap;
        display: block;
        */
/*width       : 98%;*//*

        height: 25px;
        font-family: 'open sans condensed';
        font-weight: bold;
        font-size: 16px;
        */
/*text-shadow : -2px 2px 1px rgba(0, 0, 0, 0.25);*//*

        */
/*margin-top  : 10px;*//*

        line-height: 18px;

        */
/*text-shadow: 0px 0px 1px #333;*//*

    }
*/

    </style>

</head>

<body>


<div style="margin-top: 0px;" class="vertical-container">

    <p class="css-vertical-text" style="margin-top: -10px;">Buscar</p>

    <div class="linea"></div>

    <div style="margin-bottom: 20px">
        <div class="col-md-2">
            <label>Documento</label>
            <g:textField name="memorando" value="" maxlength="15" class="form-control"/>
        </div>

        <div class="col-md-2">
            <label>Asunto</label>
            <g:textField name="asunto" value="" style="width: 300px" maxlength="30" class="form-control"/>
        </div>

        <div class="col-md-2" style="margin-left: 130px">
            <label>Fecha Envio</label>
            <elm:datepicker name="fechaBusqueda" class="datepicker form-control" value=""/>
        </div>

        <div class="col-md-2" style="margin-left: 1px">
            <label>Fecha Recepción</label>
            <elm:datepicker name="fechaRecepcion" class="datepicker form-control" value=""/>
        </div>


        <div style="padding-top: 25px">
            <a href="#" name="busqueda" class="btn btn-success btnBusqueda"><i
                    class="fa fa-check-square-o"></i> Buscar</a>
        </div>

    </div>

</div>

%{--//bandeja--}%

<div style="margin-top: 30px; min-height: 460px" class="vertical-container" id="divBandeja">

    <p class="css-vertical-text">Resultado</p>

    <div class="linea"></div>

    <div id="bandeja">


    </div>

</div>


<script>
    $(function () {
        var cellWidth = 150;
        var celHegth = 25;
        var select = null;
        var headerTop = $(".header-columnas");
//        var headerLeft=$(".header-filas");

        $(".h-A").resizable({
            handles: "e",
            minWidth: 30,
            alsoResize: ".A"
        });
        $(".container-celdas").scroll(function () {
//            $("#container-filas").scrollTop($(".container-celdas").scrollTop());
            $("#container-cols").scrollLeft($(".container-celdas").scrollLeft());
        });

    });
</script>

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

        var interval = loading("bandeja")

        var memorando = $("#memorando").val();
        var asunto = $("#asunto").val();
        var fecha = $("#fechaBusqueda_input").val();
        var fechaRecepcion = $("#fechaRecepcion_input").val();

        var datos = "memorando=" + memorando + "&asunto=" + asunto + "&fecha=" + fecha + "&fechaRecepcion=" + fechaRecepcion

        $.ajax({ type: "POST", url: "${g.createLink(controller: 'buscarTramite', action: 'tablaBusquedaTramite')}",
            data: datos,
            success: function (msg) {
                clearInterval(interval)
                $("#bandeja").html(msg);
            }
        });

    });


    context.settings({
        onShow: function (e) {
            $("tr.trHighlight").removeClass("trHighlight");
            var $tr = $(e.target).parent();
            $tr.addClass("trHighlight");
            id = $tr.data("id");
        }


    });


    context.attach('tr', [
        {
            header: 'Acciones'
        },
        {
            text: 'Seguimiento Tramite',
            icon: "<i class='fa fa-code-fork'></i>",
            action: function (e) {
                $("tr.trHighlight").removeClass("trHighlight");
                e.preventDefault();

                location.href="${g.createLink(controller: 'tramite3', action: 'seguimientoTramite')}/"+id;
            }

        }

    ]);
</script>


</body>
</html>