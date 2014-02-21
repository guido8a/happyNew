<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 18/02/14
  Time: 12:52 PM
--%>


<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Bandeja de Salida</title>

    <style type="text/css">

    .etiqueta {
        float: left;
        /*width: 100px;*/
        margin-left: 5px;
        /*margin-top: 5px;*/

    }

    .alert {
        padding: 0;
    !important;
    }

    .alert-blanco {
        color: #666;
        background-color: #ffffff;
        border-color: #d0d0d0;
    }

    .alertas {
        float: left;
        width: 100px;
        height: 40px;
        margin-left: 20px;
        /*margin-top: -5px;*/
    }

    .cabecera {
        text-align: center;
        font-size: 13px;
    !important;
    }

    .container-celdas {
        width: 1070px;
        height: 310px;
        float: left;
        overflow: auto;
        overflow-y: auto;
    }

    .uno {
        float: left;

        width: 450px;

    }

    .dos {

        float: left;
        width: 350px;

    }

    .tres {
        float: left;
        width: 270px;

    }

    .fila {

        /*height: 10px;*/
        clear: both;
    }

    .css-vertical-text {
        /*position          : absolute;*/
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
        /*text-shadow       : -2px 2px 1px rgba(0, 0, 0, 0.25);*/

        /*text-shadow: 0px 0px 1px #333;*/
    }

    .tituloChevere {

        color: #0088CC;
        border: 0px solid red;
        white-space: nowrap;
        display: block;
        /*width       : 98%;*/
        height: 25px;
        font-family: 'open sans condensed';
        font-weight: bold;
        font-size: 16px;
        /*text-shadow : -2px 2px 1px rgba(0, 0, 0, 0.25);*/
        /*margin-top  : 10px;*/
        line-height: 18px;

        /*text-shadow: 0px 0px 1px #333;*/
    }

    .table-hover tbody tr:hover td, .table-hover tbody tr:hover th {
        background-color: #FFBD4C;
    }



    </style>

</head>

<body>

<div class="row" style="margin-top: 0px">
    <span class="grupo">
        <label class="well well-sm"
               style="text-align: center; float: left">Usuario: ${persona?.titulo + " " + persona?.nombre + " " + persona?.apellido + " - " +
                persona?.departamento?.descripcion}</label>

        <div class="alert alert-success alertas" style="margin-left: 40px;"><label
                class="etiqueta">Revisados</label></div>

        <div class="alert alert-blanco alertas" style="width: 160px;"><p
                class="etiqueta">Enviados</p></div>

        <div class="alert alert-danger alertas"><label class="etiqueta">No recibido</label></div>
    </span>
</div>


<div class="btn-toolbar toolbar" style="margin-top: 10px !important">
    <div class="btn-group">

        <a href="#" class="btn btn-primary btnBuscar"><i class="fa fa-book"></i> Buscar</a>


        <g:link action="" class="btn btn-primary btnTramites">
            <i class="fa fa-gears"></i> Trámites
        </g:link>

        %{--<g:link action="archivados" class="btn btn-primary btnArchivados" controller="tramite">--}%
            %{--<i class="fa fa-folder"></i> Archivados--}%
        %{--</g:link>--}%

        <g:link action="" class="btn btn-success btnActualizar">
            <i class="fa fa-refresh"></i> Actualizar
        </g:link>

    </div>

</div>


<div class="buscar" hidden="hidden">

    <fieldset>
        <legend>Búsqueda</legend>

        <div>
            <div class="col-md-2">
                <label># Memorando</label>
                <g:textField name="memorando" value="" maxlength="15" class="form-control"/>
            </div>

            <div class="col-md-2">
                <label>Asunto</label>
                <g:textField name="asunto" value="" style="width: 300px" maxlength="30" class="form-control"/>
            </div>

            <div class="col-md-2" style="margin-left: 130px">
                <label>Fecha</label>
                <elm:datepicker name="fechaBusqueda" class="datepicker form-control" value=""/>
            </div>


            <div style="padding-top: 25px">
                <a href="#" name="busqueda" class="btn btn-success btnBusqueda"><i
                        class="fa fa-check-square-o"></i> Buscar</a>

                <a href="#" name="salir" class="btn btn-danger btnSalir"><i class="fa fa-times"></i> Cerrar</a>
            </div>

        </div>

    </fieldset>

</div>


%{--//bandeja--}%


<div id="bandeja">

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

    $(function () {

//        $(".btnCrear").click(function() {
//            createEditRow();
//            return false;
//        });

        context.settings({
            onShow: function (e) {
                $("tr.success").removeClass("success");
                var $tr = $(e.target).parent();
                $tr.addClass("success");
                id = $tr.data("id");
            }
        });
        context.attach('tbody>tr', [
            {
                header: 'Acciones'
            },
//            {
//                text: 'Recibir Documento',
//                icon: "<i class='fa fa-check-square-o'></i>",
//                action: function (e) {
//                    $("tr.success").removeClass("success");
//                    e.preventDefault();
//                }
//            },
            %{--{--}%
                %{--text: 'Contestar Documento',--}%
                %{--icon: "<i class='fa fa-external-link'></i>",--}%
                %{--action: function (e) {--}%
                    %{--$("tr.success").removeClass("success");--}%
                    %{--e.preventDefault();--}%
                    %{--$.ajax({--}%
                    %{--type    : "POST",--}%
                    %{--url     : "${createLink(action:'show_ajax')}",--}%
                    %{--data    : {--}%
                    %{--id : id--}%
                    %{--},--}%
                    %{--success : function (msg) {--}%
                    %{--//                            bootbox.dialog({--}%
                    %{--//                                title   : "Ver Año",--}%
                    %{--//                                message : msg,--}%
                    %{--//                                buttons : {--}%
                    %{--//                                    ok : {--}%
                    %{--//                                        label     : "Aceptar",--}%
                    %{--//                                        className : "btn-primary",--}%
                    %{--//                                        callback  : function () {--}%
                    %{--//                                        }--}%
                    %{--//                                    }--}%
                    %{--//                                }--}%
                    %{--//                            });--}%
                    %{--}--}%
                    %{--});--}%
                %{--}--}%
            %{--},--}%
            {
                text: 'Archivar Documentos',
                icon: "<i class='fa fa-folder-open-o'></i>",
                action: function (e) {
                    $("tr.success").removeClass("success");
                    e.preventDefault();
//                    createEditRow(id);
                }

            }
//            {divider : true},
//            {
//                text   : 'Eliminar',
//                icon   : "<i class='fa fa-trash-o'></i>",
//                action : function (e) {
//                    $("tr.success").removeClass("success");
//                    e.preventDefault();
//                    deleteRow(id);
//                }
//            }
        ]);
    });

    $(".btnBuscar").click(function () {

        $(".buscar").attr("hidden", false)

    });


    $(".btnSalir").click(function () {


        $(".buscar").attr("hidden", true)

    });


    $(".btnActualizar").click(function () {

        cargarBandeja();

        bootbox.alert('<label><i class="fa fa-exclamation-triangle"></i> Tabla de trámites actualizada!</label>')

        return false;


    });

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


    function cargarBandeja() {

        var interval = loading("bandeja")
        var datos = ""
        $.ajax({type: "POST", url: "${g.createLink(controller: 'tramite',action:'tablaBandejaSalida')}",
            data: datos,
            success: function (msg) {
                clearInterval(interval)
                $("#bandeja").html(msg);

            }
        });
    }

    cargarBandeja();


    $(".btnBusqueda").click(function () {

        var interval = loading("bandeja")

        var memorando = $("#memorando").val();
        var asunto = $("#asunto").val();
        var fecha = $("#fechaBusqueda_input").val();

        var datos = "memorando=" + memorando + "&asunto=" + asunto + "&fecha=" + fecha

        $.ajax({ type: "POST", url: "${g.createLink(controller: 'tramite', action: 'busquedaBandeja')}",
            data: datos,
            success: function (msg) {
                clearInterval(interval)
                $("#bandeja").html(msg);


            }



        });

    });






</script>

</body>
</html>