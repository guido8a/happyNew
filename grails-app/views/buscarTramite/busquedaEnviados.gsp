<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 14/04/14
  Time: 03:40 PM
--%>

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
    <title>Búsqueda de Trámites Enviados</title>


    <style type="text/css">

    .container-celdas {
        width: 1070px;
        height: 310px;
        float: left;
        overflow: auto;
        overflow-y: auto;
    }

    .alinear {

        text-align: center !important;
    }

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

        <div class="col-md-2" style="margin-left: 150px">
            <label>Fecha Desde</label>
            <elm:datepicker name="fechaRecepcion" class="datepicker form-control" value=""/>
        </div>


        <div class="col-md-2" style="margin-left: 15px">
            <label>Fecha Hasta</label>
            <elm:datepicker name="fechaBusqueda" class="datepicker form-control" value=""/>
        </div>



        <div style="padding-top: 25px">
            <a href="#" name="busqueda" class="btn btn-success btnBusqueda"><i
                    class="fa fa-check-square-o"></i> Buscar</a>

            <a href="#" name="borrar" class="btn btn-primary btnBorrar"><i
                    class="fa fa-eraser"></i> Limpiar</a>

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

        $.ajax({ type: "POST", url: "${g.createLink(controller: 'buscarTramite', action: 'tablaBusquedaEnviados')}",
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
            var $tr = $(e.target).parents("tr");
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

    $(".btnBorrar").click(function () {

        $("#memorando").val("");
        $("#asunto").val("");
        $("#fechaRecepcion_input").val('');
        $("#fechaBusqueda_input").val('')


    });


</script>


</body>
</html>