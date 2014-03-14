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
        float       : left;
        /*width       : 100px;*/
        /*height      : 40px;*/
        margin-left : 20px;
        padding     : 10px;
        cursor      : pointer;
        /*margin-top: -5px;*/
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

    tr.E004, tr.recibidoColor td {
        background-color: #D9EDF7! important;
    }

    tr.E003, tr.retrasadoColor td {
        background-color: transparent ;
    }

    tr.pendiente {
        background-color: #FFFFCC! important;
    }

    tr.retrasado {
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
        <div>
            <div data-type="recibido" class="alert alert-info alertas"  clase="E004">
                (<span id="numRec"></span>)
            Recibidos
            </div>
        </div>

        <div>
            <div data-type="pendiente" class="alert alert-otroRojo alertas"  clase="E003">
                (<span id="numPen"></span>)
            No recibidos
            </div>
        </div>

        <div>
            <div data-type="pendiente" class="alert alert-blanco alertas"  clase="E003">
                (<span id="numEnv"></span>)
            Pendientes
            </div>
        </div>

        <div>
            <div data-type="retrasado" class="alert alert-danger alertas" clase="retrasado" >
                (<span id="numRet"></span>)
            Retrasados
            </div>
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


<script type="text/javascript">

    function cargarBandeja(band) {
        var datos = ""
        $.ajax({type: "POST", url: "${g.createLink(controller: 'tramite',action:'tablaBandeja')}",
            data: datos,
            async:false,
            success: function (msg) {
                $("#bandeja").html(msg);
                cargarAlertaPendientes()
                cargarAlertaRetrasados()
                cargarEnviados()
                cargarAlertaRecibidos()
                if(band)
                    bootbox.alert("Datos actualizados")
            }
        });
    }
    function cargarAlertaPendientes () {
        $("#numPen").html($(".pendiente").size())
    }
    function cargarAlertaRetrasados () {
        $("#numRet").html($(".retrasado").size())
    }
    function cargarEnviados() {
        $("#numEnv").html($(".E003").size())
    }
    function cargarAlertaRecibidos () {
        $("#numRec").html($(".E004").size())
    }


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
                        if(msg == 'ok'){

                            $.ajax({
                                type : 'POST',
                                url : "${createLink(controller: 'tramite', action: 'observacionArchivado')}/" + id,
                                success: function (msg){

                                    var b = bootbox.dialog({
                                        id: "dlgArchivar",
                                        title: 'Archivar Tramite',
                                        message: msg,
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
                                                        data: {
                                                            texto: $("#observacionArchivar").val()
                                                        },
                                                        success : function (msg) {
                                                            openLoader();
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

        var archivo
        context.settings({
            onShow: function (e) {
                $("tr.trHighlight").removeClass("trHighlight");
                var $tr = $(e.target).parent();
                $tr.addClass("trHighlight");
                id = $tr.data("id");
                archivo = $tr.attr("codigo")
            }



        });

        context.attach('.E003',[
            {
                header: 'Acciones'
            },
            {
                text: 'Ver',
                icon: "<i class='fa fa-search'></i>",
                action: function (e) {
                    $("tr.trHighlight").removeClass("trHighlight");
                    e.preventDefault();
                    %{--location.href="${g.createLink(action: 'verPdf',controller: 'tramiteExport')}/"+id;--}%
                    location.href = "${resource(dir:'tramites')}/"+archivo+".pdf";
                }
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
                text: 'Ver',
                icon: "<i class='fa fa-search'></i>",
                action: function (e) {
                    $("tr.trHighlight").removeClass("trHighlight");
                    e.preventDefault();
                    location.href = "${resource(dir:'tramites')}/"+archivo+".pdf";
                }
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
        cargarBandeja(false);
        closeLoader();
        return false;
    });

    cargarBandeja();

    setInterval(function () {

        openLoader();
        cargarBandeja(false)
        closeLoader();

    },300000);


    $(".alertas").click(function(){
        var clase = $(this).attr("clase")
        $("tr").each(function(){
            if($(this).hasClass(clase)){
                if($(this).hasClass("trHighlight"))
                    $(this).removeClass("trHighlight")
                else
                    $(this).addClass("trHighlight")
            }else{
                $(this).removeClass("trHighlight")
            }
        });

    })

    $(".btnBusqueda").click(function () {
        var memorando = $("#memorando").val();
        var asunto = $("#asunto").val();
        var fecha = $("#fechaBusqueda_input").val();
        var datos = "memorando=" + memorando + "&asunto=" + asunto + "&fecha=" + fecha

        $.ajax({ type: "POST", url: "${g.createLink(controller: 'tramite', action: 'busquedaBandeja')}",
            data: datos,
            success: function (msg) {
                $("#bandeja").html(msg);


            }



        });

    });


</script>

</body>
</html>