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
        cursor: pointer;
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
    .enviado{
        background-color:#e0e0e0 ;
        border:1px solid #a5a5a5 ;
    }
    .borrador{
        background-color:#FFFFCC ;
        border:1px solid #eaeab7;
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
        background-color: #e0e0e0 ! important;
    }
    tr.alerta, tr.alerta td {
        background-color: #f2c1b9;
        font-weight: bold;
    }



    </style>

    <link href="${resource(dir: 'css', file: 'custom/loader.css')}" rel="stylesheet">

</head>

<body>

<div class="row" style="margin-top: 0px; margin-left: 1px">
    <span class="grupo">
        <label class="well well-sm"
               style="text-align: center; float: left">Usuario: ${persona?.titulo + " " + persona?.nombre + " " + persona?.apellido + " - " +
                persona?.departamento?.descripcion}</label>

    </span>
</div>

<elm:flashMessage tipo="${flash.tipo}" clase="${flash.clase}">${flash.message}</elm:flashMessage>


<div class="btn-toolbar toolbar" style="margin-top: 10px !important">
    <div class="btn-group">

        <a href="#" class="btn btn-primary btnBuscar"><i class="fa fa-book"></i> Buscar</a>


        <g:link action="" class="btn btn-primary btnTramites">
            <i class="fa fa-gears"></i> Trámites
        </g:link>

        <g:link action="" class="btn btn-success btnActualizar">
            <i class="fa fa-refresh"></i> Actualizar
        </g:link>

        <g:link action="" class="btn btn-info btnEnviar">
            <i class="fa fa-pencil"></i> Enviar
        </g:link>

    </div>

    <span class="grupo">
        <div>
            <div data-type="" class="alert borrador alertas" clase="E001" style="margin-left: 30px;padding-left: 30px; padding-top: 10px; width: 150px">
                (<span id="numBor"></span>)
            Borradores
            </div>
        </div>

        <div id="alertaRevisados">
            <div data-type="revisado" class="alert alert-success alertas" clase="E002" style="margin-left: 20px;padding-left: 30px; padding-top: 10px; width: 150px">
                (<span id="numRev"></span>)
            Revisados
            </div>
        </div>

        <div id="alertaEnviados">
            <div data-type="enviado" class="alert enviado alertas" clase="E003" style="width: 150px;padding-left: 30px; padding-top: 10px;">
                (<span id="numEnv"></span>)
            Enviados
            </div>
        </div>

        <div id="alertaNoRecibidos">
            <div data-type="noRecibido" class="alert alert-danger alertas" clase="alerta" style="padding-left: 30px; padding-top: 10px; width: 150px">
                (<span id="numNoRec"></span>)
            No recibidos
            </div>
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


<div id="" style=";height: 600px;overflow: auto;position: relative">
    <div class="modalTabelGray" id="bloqueo-salida"></div>
    <div id="bandeja"></div>

</div>

<elm:select from="${personal}" name="selector" optionKey="id" class="form-control hide" style="width: 300px; margin-left: 130px; margin-top: -30px"/>


<script type="text/javascript">

    function cargarBandeja(band) {
        $("#bandeja").html("")
        $.ajax({type: "POST", url: "${g.createLink(controller: 'tramite2',action:'tablaBandejaSalida')}",
            data: "",
            async:false,
            success: function (msg) {
                $("#bandeja").html(msg).show("slide");
                cargarAlertaRevisados();
                cargarAlertaEnviados();
                cargarAlertaNoRecibidos();
                cargarBorrador();
                if(band){
                    bootbox.alert("Datos actualizados")
                }
            }
        });
    }

    function cargarAlertaRevisados () {
        $("#numRev").html($(".E002").size())
    }

    function cargarAlertaEnviados () {
        $("#numEnv").html($(".E003").size())
    }

    function cargarAlertaNoRecibidos () {
        $("#numNoRec").html($(".alerta").size())
    }
    function cargarBorrador () {
//        console.log($(".E001"),$(".E001").size())
        $("#numBor").html($(".E001").size())
    }

    $(function () {

        <g:if test="${bloqueo}">
        $("#bloqueo-salida").show()
        </g:if>

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

        });

        var estado
        var de
        var selPersonal = '<p id="seleccionar"> </p>';
        var $sel = $("#selector").clone();



        var imprimir = {

            text: 'Permiso Imprimir',
            icon: "<i class='fa fa-print'></i>",
            action: function (e){
                $("tr.trHighlight").removeClass("trHighlight");
                e.preventDefault();
                        var b = bootbox.dialog({
                            id: "dlgImprimir",
                            title : "Permiso de impresión para el trámite:  " + codigo,
                            message : "<label style='margin-left: 30px; margin-top: 30px'>Personal:</label>" + selPersonal +
                                      "<label style='margin-left: 30px; margin-top: 60px'>Observaciones:</label>" + "<textarea style='width: 300px;margin-left: 10px; height: 70px' id='observImp'></textarea>",
                            buttons : {
                                cancelar : {
                                    label  : "Cancelar",
                                    className : 'btn-danger',
                                    callback :  function () {
                                    }
                                },
                                guardar : {
                                    id   : 'btnSave',
                                    label : '<i class="fa fa-save"></i> Aceptar',
                                    className : "btn-success",
                                    callback: function () {

                                        $.ajax({
                                            type: 'POST',
                                            url: '${createLink(action: 'permisoImprimir')}/' + id,
                                            data :{
                                                persona: $("#iden").val(),
                                                observaciones: $("#observImp").val()

                                            },
                                            success : function (msg) {
                                                bootbox.alert(msg)
                                            }
                                        });
                                    }
                                }
                            }
                        });

                if($sel){
                    $sel.removeClass('hide');
                    $sel.attr('id', 'iden');
                    $("#seleccionar").append($sel);
                }
            }
        };



        var archivo

        context.settings({
            onShow: function (e) {
                $("tr.trHighlight").removeClass("trHighlight");
                var $tr = $(e.target).parent();
                $tr.addClass("trHighlight");
                id = $tr.data("id");
                codigo = $tr.attr("codigo")
                estado = $tr.attr("estado");
                de = $tr.attr("de");
                archivo = $tr.attr("codigo")
            }
        });
        <g:if test="${!bloqueo}">
        context.attach(".E001", [
            {
                header: 'Acciones'
            },
            {
                text: 'Ver',
                icon: "<i class='fa fa-search'></i>",
                action: function (e) {
                    $("tr.trHighlight").removeClass("trHighlight");
                    %{--location.href="${g.createLink(action: 'seguimientoTramite',controller: 'tramite3')}/"+id--}%
                    window.open("${resource(dir:'tramites')}/"+archivo+".pdf");

                }


            },
            {
                text: 'Editar',
                icon: "<i class='fa fa-pencil'></i>",
                action: function (e) {
                    $("tr.trHighlight").removeClass("trHighlight");
                    location.href="${g.createLink(action: 'redactar',controller: 'tramite')}/"+id
                }

            },
            imprimir

            <g:if test="${revisar}">,
            {
                text: 'Revisar',
                icon: "<i class='fa fa-check'></i>",
                action: function (e) {
                    $("tr.trHighlight").removeClass("trHighlight");
                    location.href="${g.createLink(action: 'revision',controller: 'tramite2')}/"+id
                }

            }
            </g:if>

        ]);
        context.attach(".E002", [
            {
                header: 'Acciones'
            },
            {
                text: 'Ver',
                icon: "<i class='fa fa-search'></i>",
                action: function (e) {
                    $("tr.trHighlight").removeClass("trHighlight");
                    %{--location.href="${g.createLink(action: 'seguimientoTramite',controller: 'tramite3')}/"+id--}%
                    window.open("${resource(dir:'tramites')}/"+archivo+".pdf");

                }

            },
            {
                text: 'Enviar',
                icon: "<i class='fa fa-pencil'></i>",
                action: function (e) {
                    $("tr.trHighlight").removeClass("trHighlight");

                    bootbox.confirm("Esta seguro?",function(result){
                        if(result){
                            openLoader()
                            $.ajax({
                                type    : "POST",
                                url     : "${g.createLink(controller: 'tramite2',action: 'enviar')}",
                                data    : "id="+id,
                                success : function (msg) {
                                    closeLoader()
                                    if(msg=="ok"){
                                        bootbox.alert("Documento enviado.")
                                        cargarBandeja(false)
                                        location.href="${g.createLink(controller: 'tramiteExport',action: 'crearPdf')}/"+id+"?enviar=1"
                                    }else{
                                        var mensaje = msg.split("_")
                                        mensaje = mensaje[1]
                                        bootbox.alert(mensaje)
                                    }
                                }
                            });
                        }

                    })



                    /*ajax aqui*/
                }

            },

            imprimir
        ]);
        context.attach(".E003", [
            {
                header: 'Acciones'
            },
            {
                text: 'Ver',
                icon: "<i class='fa fa-search'></i>",
                action: function (e) {
                    $("tr.trHighlight").removeClass("trHighlight");
                    location.href="${g.createLink(action: 'seguimientoTramite',controller: 'tramite3')}/"+id
                }

            }
        ]);

        context.attach(".alerta", [
            {
                header: 'Sin Acciones'
            }

        ]);

        context.attach(".imprimir", [
            {
                header: 'Acciones'
            },
            {
                text: 'Ver',
                icon: "<i class='fa fa-search'></i>",
                action: function (e) {
                    $("tr.trHighlight").removeClass("trHighlight");
                    location.href="${g.createLink(action: 'seguimientoTramite',controller: 'tramite3')}/"+id
                }

            }
            <g:if test="${revisar}">,
            {
                text: 'Revisar',
                icon: "<i class='fa fa-check'></i>",
                action: function (e) {
                    $("tr.trHighlight").removeClass("trHighlight");
                    location.href="${g.createLink(action: 'revision',controller: 'tramite2')}/"+id
                }

            }
            </g:if>


        ]);
        </g:if>

        $(".btnBuscar").click(function () {
            $(".buscar").attr("hidden", false)
        });


        $(".btnSalir").click(function () {
            $(".buscar").attr("hidden", true)
        });


        $(".btnActualizar").click(function () {
            openLoader()
            cargarBandeja(true);
            closeLoader()
            return false;


        });

        $(".btnEnviar").click(function () {

            var trId = []

            $(".combo").each(function () {
                if($(this).prop('checked') == false){
                }else {
//                    console.log(trId.length)
                    if(trId.length > 0){
                        trId += "," + $(this).attr('tramite')
                    }else{
                        trId += $(this).attr('tramite')
                    }
                }
            });

//            console.log("--->" + trId)

            if(trId == ''){

                log("No se ha seleccionado ningun trámite", 'error');

            }else {


                var b = bootbox.dialog({
                    id: "dlgGuia",
                    title: 'Impresión de la guía de envio de trámites',
                    message: 'Desea imprimir la guía de envio para los trámites seleccionados?',
                    buttons: {
                        no: {
                            label: 'No Imprimir',
                            callback: function () {

                            }
                        },
                        si: {
                            label: '<i class="fa fa-print"></i> Imprimir',
                            callback: function () {

                            }
                        }
                    }
                });

            }
            return false;
        });

        cargarBandeja(false);


        setInterval(function () {
            openLoader()
            cargarBandeja(false);
            closeLoader()

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
    });
</script>

</body>
</html>