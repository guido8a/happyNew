<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 1/16/14
  Time: 11:31 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Bandeja de Entrada</title>

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

    tr.recibidoColor, tr.recibidoColor td {
        background-color: #D9EDF7! important;
    }

    tr.retrasadoColor, tr.retrasadoColor td {
        background-color: #F2DEDE! important;
    }

    tr.pendienteColor, tr.pendienteColor td {
        background-color: #FFFFCC! important;
    }

    tr.pendienteColor.pendienteRojo, tr.pendienteColor.pendienteRojo td {
        background-color: #fc2c04! important;
        color: #ffffff;
    }




    </style>

</head>

<body>

<div class="row" style="margin-top: 0px; margin-left: 1px">
    <span class="grupo">
        <label class="well well-sm"
               style="text-align: center; float: left">Usuario: ${persona?.titulo + " " + persona?.nombre + " " + persona?.apellido + " - " +
                persona?.departamento?.descripcion}</label>
 </span>
</div>

<div class="btn-toolbar toolbar">
    <div class="btn-group">

        <a href="#" class="btn btn-primary btnBuscar"><i class="fa fa-book"></i> Buscar</a>
        <g:link action="archivados" class="btn btn-primary btnArchivados" controller="tramite">
            <i class="fa fa-folder"></i> Archivados
        </g:link>

        <g:link action="" class="btn btn-success btnActualizar">
            <i class="fa fa-refresh"></i> Actualizar
        </g:link>

    </div>

    <span class="grupo">
        <div id="alertaRecibido">
            <div data-type="recibido" class="alert alert-info alertas" style="width: 190px;">
                <label  class="etiqueta" style="padding-top: 10px; padding-left: 10px">Documentos Recibidos</label>
            </div>
        </div>

        <div id="alertaPendientes">
            <div data-type="pendiente" class="alert alert-blanco alertas" style="width: 270px;">
                <label class="etiqueta" style="padding-top: 10px; padding-left: 10px">Documentos Pendientes o No Recibidos</label>
            </div>
        </div>

        <div id="alertaRetrasados">
            <div data-type="retrasado" class="alert alert-danger alertas"  style="width: 190px">
                <label class="etiqueta" style="padding-left: 10px; padding-top: 10px">Documentos Retrasados</label></div>
        </div>
    </span>
</div>


<div class="buscar" hidden="hidden" style="margin-bottom: 20px;">

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

        var contestar = {
            text: 'Contestar Documento',
            icon: "<i class='fa fa-external-link'></i>",
            action: function (e) {
                $("tr.trHighlight").removeClass("trHighlight");
                e.preventDefault();

                location.href="${g.createLink(action: 'crearTramite')}/"+id;
            }
        };

        var seguimiento = {

            text: 'Seguimiento Tramite',
            icon: "<i class='fa fa-code-fork'></i>",
            action: function (e) {
                $("tr.trHighlight").removeClass("trHighlight");
                e.preventDefault();

                location.href="${g.createLink(controller: 'tramite3', action: 'seguimientoTramite')}/"+id;
            }

        };

        var archivar =  {
                    text: 'Archivar Documentos',
                    icon: "<i class='fa fa-folder-open-o'></i>",
                    action: function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();
                        $.ajax({
                           type : "POST",
                            url: "${createLink(controller: 'tramite', action: "revisarHijos")}/" + id,
                            success: function (msg){
                                console.log("mensaje" + msg)
                                if(msg == 'ok'){
                                    var b = bootbox.dialog({
                                        id: "dlgArchivar",
                                        title: 'Archivar Tramite',
                                        message: "Desea archivar el siguiente tramite?",
                                        buttons: {
                                            cancelar : {
                                               label : '<i class="fa fa-times"></i> Cancelar',
                                                className : 'btn-danger',
                                                callback :  function () {

                                                }
                                            },
                                            archivar: {
                                                id   : 'btnArchivar',
                                                label : '<i class="fa fa-check"></i> Archivar',
                                                className : "btn-success",
                                                callback: function () {

                                                    $.ajax({
                                                        type: 'POST',
                                                        url: '${createLink(action: 'archivar')}/'+ id,
                                                        success : function (msg) {
                                                            openLoader();
                                                            cargarAlertaRecibidos();
                                                            cargarAlertaPendientes();
                                                            cargarAlertaRetrasados();
                                                            cargarBandeja();
                                                            closeLoader();
                                                            bootbox.alert(msg)
                                                        }


                                                    });


                                                }

                                            }


                                        }

                                    })
                                }else if (msg == 'no'){

                                    bootbox.alert("Este tramite no puede ser archivado!")
                                }

                            }

                        });
                    }

                };


        var distribuir =             {
            text: 'Distribuir a Jefes',
            icon: "<i class='fa fa-eye'></i>",
            action: function (e){
                $("tr.trHighlight").removeClass("trHighlight");
                e.preventDefault();
                $.ajax ({
                    type : "POST",
                    url  : "${createLink(action: 'observaciones')}/" + id,
//                        data  : id,
                    success :function (msg){
                        var b = bootbox.dialog({
                            id: "dlgObservaciones",
                            title : "Distribución al Jefe: Observaciones",
                            message : msg,
                            buttons : {
                                cancelar : {
                                    label  : "Cancelar",
                                    className : 'btn-danger',
                                    callback :  function () {

                                    }

                                },
                                guardar : {
                                    id   : 'btnSave',
                                    label : '<i class="fa fa-save"></i> Guardar',
                                    className : "btn-success",
                                    callback: function () {

                                        $.ajax({
                                            type: 'POST',
                                            url: '${createLink(action: 'guardarObservacion')}/'+ id,
                                            data :{

                                                texto: $("#observacion").val()
                                            },
                                            success : function (msg) {

                                                bootbox.alert(msg)

                                            }


                                        });


                                    }


                                }

                            }

                        })
                    }
                });


            }

        }

        var anular =      {
                    text: 'Anular',
                    icon: "<i class='fa fa-times'></i>",
                    action: function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();

                        location.href="${g.createLink(action: 'anular')}";
                    }
                }


                context.settings({
            onShow: function (e) {
                $("tr.trHighlight").removeClass("trHighlight");
                var $tr = $(e.target).parent();
                $tr.addClass("trHighlight");
                id = $tr.data("id");
            }



        });

        context.attach('.E003',[
            {
               header: 'Acciones'
            },
                contestar,
                archivar,
                seguimiento,
                anular,

            {

                text: 'Recibir Documento',
                icon: "<i class='fa fa-check-square-o'></i>",
                action: function (e) {
                    $("tr.trHighlight").removeClass("trHighlight");
                    e.preventDefault();
                    $.ajax({
                        type: 'POST',
                        url: "${createLink(action: 'recibir')}/" + id,
                        success: function (msg){
                            var b = bootbox.dialog({
                                id: "dlgRecibido",
                                title: "Trámite a ser recibido",
                                message: msg,
                                buttons : {
                                    cancelar : {
                                        label :  '<i class="fa fa-times"></i> Cancelar',
                                        className : 'btn-danger',
                                        callback: function () {
                                        }
                                    },
                                    recibir : {
                                        id : 'btnRecibir',
                                        label: '<i class="fa fa-thumbs-o-up"></i> Recibir',
                                        className: 'btn-success',
                                        callback: function () {
                                            $.ajax ({
                                                type: 'POST',
                                                url: '${createLink(action: 'guardarRecibir')}/' + id,
                                                success: function (msg) {
                                                    openLoader();
                                                    cargarAlertaRecibidos();
                                                    cargarAlertaPendientes();
                                                    cargarAlertaRetrasados();
                                                    cargarBandeja();
                                                    closeLoader();
                                                    bootbox.alert(msg)
                                                }
                                            });
                                        }
                                    }
                                }
                            })
                        }
                    });
                }
            }
        ]);

        context.attach('.E004', [
            {
                header: 'Acciones'
            },
                   {
                text: 'Contestar Documento',
                icon: "<i class='fa fa-external-link'></i>",
                action: function (e) {
                    $("tr.trHighlight").removeClass("trHighlight");
                    e.preventDefault();

                    location.href="${g.createLink(action: 'crearTramite')}?padre="+id;
                }
            },


            archivar,
            anular,
            seguimiento


        ]);
    });

    $(".btnBuscar").click(function () {

        $(".buscar").attr("hidden", false)

    });


    $(".btnSalir").click(function () {


        $(".buscar").attr("hidden", true)
        cargarBandeja();

    });

    $(".btnActualizar").click(function () {

        openLoader();
        cargarAlertaRecibidos();
        cargarAlertaPendientes();
        cargarAlertaRetrasados();
        cargarBandeja();
        closeLoader();

        bootbox.alert('<label><i class="fa fa-exclamation-triangle"></i> Tabla de trámites y alertas actualizadas!</label>')

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
        $.ajax({type: "POST", url: "${g.createLink(controller: 'tramite',action:'tablaBandeja')}",
            data: datos,
            success: function (msg) {
                clearInterval(interval)
                $("#bandeja").html(msg);

            }
        });
    }

    cargarBandeja();


    function cargarAlertaRecibidos () {

        var interval = loading("alertaRecibido")
        var datos = ""
        $.ajax({type: "POST", url: "${g.createLink(controller: 'tramite',action:'alertRecibidos')}",
            data: datos,
            success: function (msg) {
                clearInterval(interval)
                $("#alertaRecibido").html(msg);

            }
        });


    }

    cargarAlertaRecibidos();



    setInterval(function () {

        openLoader();
        cargarAlertaRecibidos();
        cargarAlertaPendientes();
        cargarAlertaRetrasados();
        closeLoader();

    },300000);


    function cargarAlertaPendientes () {

        var interval = loading("alertaPendientes")
        var datos = ""
        $.ajax({type: "POST", url: "${g.createLink(controller: 'tramite',action:'alertaPendientes')}",
            data: datos,
            success: function (msg) {
                clearInterval(interval)
                $("#alertaPendientes").html(msg);

            }
        });
    }

    cargarAlertaPendientes();


    function cargarAlertaRetrasados () {

        var interval = loading("alertaRetrasados")
        var datos = ""
        $.ajax({type: "POST", url: "${g.createLink(controller: 'tramite',action:'alertaRetrasados')}",
            data: datos,
            success: function (msg) {
                clearInterval(interval)
                $("#alertaRetrasados").html(msg);

            }
        });
    }

    cargarAlertaRetrasados();


    function cargarRojoPendiente() {

        var interval = loading("alertaPendientes")
        var datos = ""
        $.ajax({
            type: 'POST',
            url :  "${g.createLink(controller: 'tramite', action: 'rojoPendiente')}",
            datos: datos,
            success: function (msg){
                clearInterval(interval)
                $("#alertaPendientes").html(msg);
            }

        })


    }


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