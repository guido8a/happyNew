<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 1/21/14
  Time: 3:39 PM
--%>


<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Documentos Archivados</title>

    <style type="text/css">

    .etiqueta {
        float: left;
        width: 100px;
        /*margin-top: 5px;*/

    }

    .textEtiqueta {
        float: left;

        width: 350px;
        height: 35px;
        margin-left: 20px;
        /*margin-top: 5px;*/
    }

    .alertas {

        float: left;
        width: 250px;
        height: 35px;
        margin-left: 90px;
        /*margin-top: 5px;*/
    }

    .cabecera {
        text-align: center;

    }

    .container-celdas{
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

    .tres{
        float: left;
        width: 270px;

    }

    .fila {

        /*height: 10px;*/
        clear: both;
    }


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
        height      : 30px;
        font-family : 'open sans condensed';
        font-weight : bold;
        font-size   : 20px;
        /*text-shadow : -2px 2px 1px rgba(0, 0, 0, 0.25);*/
        /*margin-top  : 10px;*/
        line-height : 20px;

        /*text-shadow: 0px 0px 1px #333;*/
    }

    .table-hover tbody tr:hover td, .table-hover tbody tr:hover th {
        background-color: #FFBD4C;
    }



    </style>

</head>

<body>

<div class="row">
    <span class="grupo">
        <label class="etiqueta tituloChevere" style="margin-left: 20px">Usuario:</label>
        <g:textField name="usuario" value="${persona?.titulo + " " + persona?.nombre + " " + persona?.apellido}" readonly="true" class="textEtiqueta"/>

        <label class="etiqueta tituloChevere" style="margin-left: 30px">Dirección:</label>
        <g:textField name="direccion" value="${persona?.departamento?.descripcion}" readonly="true" class="textEtiqueta"/>

    </span>
</div>

<div class="row" style="margin-top: 20px">
    <span class="grupo">
        <div class="alert alert-success alertas"><label class="tituloChevere">Documentos Pendientes</label></div>
        <div class="alert alert-info alertas"><label class="tituloChevere">Documentos Revisados</label></div>
        <div class="alert alert-danger alertas"><label class="tituloChevere">Documentos no Recibidos</label></div>


    </span>

</div>


<div class="btn-toolbar toolbar" style="margin-top: 10px !important">
    <div class="btn-group">

        <a href="#" class="btn btn-primary btnBuscar"><i class="fa fa-book"></i> Buscar</a>


        %{--<g:link action="" class="btn btn-primary btnTramites">--}%
            %{--<i class="fa fa-gears"></i> Trámites--}%
        %{--</g:link>--}%

        %{--<g:link action="" class="btn btn-primary btnArchivados">--}%
            %{--<i class="fa fa-folder"></i> Archivados--}%
        %{--</g:link>--}%

        <g:link action="" class="btn btn-success btnActualizar">
            <i class="fa fa-refresh"></i> Actualizar
        </g:link>

        <g:link action="bandejaEntrada" class="btn btn-danger btnRegresar">
            <i class="fa fa-hand-o-left"></i> Regresar
        </g:link>


    </div>

</div>


<div class="buscar" hidden="hidden">

    <fieldset>
        <legend>Búsqueda</legend>

        <div>
            <div class="col-md-2">
                <label> # Memorando</label>
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
                <a href="#" name="busqueda" class="btn btn-success btnBusqueda"><i class="fa fa-check-square-o"></i> Buscar</a>

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
        var cellWidth=150;
        var celHegth=25;
        var select=null;
        var headerTop = $(".header-columnas");
//        var headerLeft=$(".header-filas");

        $( ".h-A" ).resizable({
            handles: "e",
            minWidth:30,
            alsoResize: ".A"
        });
        $(".container-celdas").scroll(function(){
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
            onShow : function (e) {
                $("tr.success").removeClass("success");
                var $tr = $(e.target).parent();
                $tr.addClass("success");
                id = $tr.data("id");
            }
        });
        context.attach('tbody>tr', [
            {
                header : 'Acciones'
            },
            {
                text   : 'Contestar Documento',
                icon   : "<i class='fa fa-comments-o'></i>",
                action : function (e) {
                    $("tr.success").removeClass("success");
                    e.preventDefault();
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
                }
            },
            {
                text   : 'Archivar Documentos',
                icon   : "<i class='fa fa-folder-open-o'></i>",
                action : function (e) {
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
        $.ajax({type : "POST", url : "${g.createLink(controller: 'tramite',action:'tablaArchivados')}",
            data     : datos,
            success  : function (msg) {
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

        $.ajax ({ type : "POST", url: "${g.createLink(controller: 'tramite', action: 'busquedaArchivados')}",
            data: datos,
            success: function (msg) {
                clearInterval(interval)
                $("#bandeja"). html(msg);


            }



        });

    });


</script>

</body>
</html>