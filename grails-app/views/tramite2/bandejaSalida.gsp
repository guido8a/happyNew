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
    }

    .container-celdas {
        width: 1070px;
        height: 310px;
        float: left;
        overflow: auto;
        overflow-y: auto;
    }
    .table-hover tbody tr:hover td, .table-hover tbody tr:hover th {
        background-color: #FFBD4C;
    }
    tr.E002, tr.revisadoColor td {
        background-color: #DFF0D8! important;
    }
    tr.E001, tr.borrador td {
        background-color: #FFFFCC! important;
    }
    tr.E003, tr.enviado td {
        background-color: #9abbf2 ! important;
    }
    tr.noRecibido, tr.noRecibido td {
        background-color: #f2c1b9 ! important;
    }



    </style>

    <link href="${resource(dir: 'css', file: 'custom/loader.css')}" rel="stylesheet">

</head>

<body>


<div id="modalTabelGray"></div>




<div class="row" style="margin-top: 0px; margin-left: 1px">
    <span class="grupo">
        <label class="well well-sm"
               style="text-align: center; float: left">Usuario: ${persona?.titulo + " " + persona?.nombre + " " + persona?.apellido + " - " +
                persona?.departamento?.descripcion}</label>

    </span>
</div>


<div class="btn-toolbar toolbar" style="margin-top: 10px !important">
    <div class="btn-group">

        <a href="#" class="btn btn-primary btnBuscar"><i class="fa fa-book"></i> Buscar</a>


        <g:link action="" class="btn btn-primary btnTramites">
            <i class="fa fa-gears"></i> Trámites
        </g:link>

        <g:link action="" class="btn btn-success btnActualizar">
            <i class="fa fa-refresh"></i> Actualizar
        </g:link>

    </div>

    <span class="grupo">

        <div id="alertaRevisados">
            <div data-type="revisado" class="alert alert-success alertas" style="margin-left: 40px; width: 150px"><label
                    class="etiqueta" style="padding-top: 10px; padding-left: 30px">Revisados</label></div>
        </div>

        <div id="alertaEnviados">
            <div data-type="enviado" class="alert alert-blanco alertas" style="width: 150px;"><p
                    class="etiqueta" style="padding-top: 10px; padding-left: 40px">Enviados</p></div>
        </div>

        <div id="alertaNoRecibidos">
            <div data-type="noRecibido" class="alert alert-danger alertas"
                 style="padding-left: 30px; padding-top: 10px; width: 150px"><label class="etiqueta">No recibido</label></div>
        </div>


    </span>

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
    function cargarBandeja() {
        var datos = ""
        $.ajax({type: "POST", url: "${g.createLink(controller: 'tramite2',action:'tablaBandejaSalida')}",
            data: datos,
            success: function (msg) {
                $("#bandeja").html(msg);
            }
        });
    }

    function cargarAlertaRevisados () {
        var datos = ""
        $.ajax({
            type: 'POST',
            url:"${createLink(controller: 'tramite2', action: 'alertaRevisados')}",
            data: datos,
            success: function (msg){
                $("#alertaRevisados").html(msg);
            }
        });
    }

    function cargarAlertaEnviados () {
        var datos = ""
        $.ajax({
            type: 'POST',
            url:"${createLink(controller: 'tramite2', action: 'alertaEnviados')}",
            data: datos,
            success : function (msg){
                $("#alertaEnviados").html(msg);

            }

        });

    }

    function cargarAlertaNoRecibidos () {
        var datos =""
        $.ajax ({
            type: 'POST',
            url: "${createLink(controller: 'tramite2', action: 'alertaNoRecibidos')}",
            data: datos,
            success : function (msg) {
                $("#alertaNoRecibidos").html(msg);
                closeLoader()
            }

        });
    }

    $(function () {


        context.settings({
            onShow: function (e) {
                $("tr.trHighlight").removeClass("trHighlight");
                var $tr = $(e.target).parent();
                $tr.addClass("trHighlight");
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
//                    $("tr.trHighlight").removeClass("trHighlight");
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
                    $("tr.trHighlight").removeClass("trHighlight");
                    e.preventDefault();
//                    createEditRow(id);
                }

            }
//            {divider : true},
//            {
//                text   : 'Eliminar',
//                icon   : "<i class='fa fa-trash-o'></i>",
//                action : function (e) {
//                    $("tr.trHighlight").removeClass("trHighlight");
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
        openLoader()
        cargarBandeja();
        cargarAlertaRevisados();
        cargarAlertaEnviados();
        cargarAlertaNoRecibidos();
        bootbox.alert('<label><i class="fa fa-exclamation-triangle"></i> Tabla de trámites y alertas actualizadas</label>')
        return false;
        closeLoader()

    });

    $(".btnActualizar").click()


    setInterval(function () {
        cargarAlertaEnviados();
        cargarAlertaNoRecibidos();
        cargarAlertaRevisados();

    },300000);



    $(".btnBusqueda").click(function () {

        var interval = loading("bandeja")

        var memorando = $("#memorando").val();
        var asunto = $("#asunto").val();
        var fecha = $("#fechaBusqueda").val();

        var datos = "memorando=" + memorando + "&asunto=" + asunto + "&fecha=" + fecha

        $.ajax({ type: "POST", url: "${g.createLink(controller: 'tramite2', action: 'busquedaBandejaSalida')}",
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