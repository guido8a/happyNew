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
        height: 25px;
        margin-left: 20px;
        /*margin-top: 5px;*/
    }

    .alertas {

        float: left;
        width: 250px;
        height: 25px;
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


    </style>

</head>

<body>

<div class="row" style="margin-top: 0px; margin-left: 4px">
    <span class="grupo">
        <label class="well well-sm"
               style="text-align: center; float: left">Usuario: ${(persona?.titulo ?: '') + " " + persona?.nombre + " " + persona?.apellido + " - " +
                persona?.departamento?.descripcion}</label>
    </span>
</div>


<div class="btn-toolbar toolbar" style="margin-top: 10px !important">
    <div class="btn-group">

        <a href="#" class="btn btn-primary btnBuscar"><i class="fa fa-book"></i> Buscar</a>

        <g:link action="" class="btn btn-success btnActualizar">
            <i class="fa fa-refresh"></i> Actualizar
        </g:link>

        <g:if test="${si == 'si'}">
            <g:link action="bandejaEntradaDpto" class="btn btn-danger btnRegresar" controller="tramite3">
                <i class="fa fa-hand-o-left"></i> Regresar
            </g:link>
        </g:if>
        <g:else>
            <g:link action="bandejaEntrada" class="btn btn-danger btnRegresar">
                <i class="fa fa-hand-o-left"></i> Regresar
            </g:link>

        </g:else>


    </div>

</div>


<div class="buscar" hidden="hidden" style="margin-bottom: 20px">

    <fieldset>
        <legend>Búsqueda</legend>

        <div>
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

<div class="modal fade " id="dialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Detalles</h4>
            </div>

            <div class="modal-body" id="dialog-body" style="padding: 15px">

            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cerrar</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
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

    //    $(function () {


    function createContextMenu (node){
        var $tr = $(node);

        var items = {
            header : {
                label : "Sin Acciones",
                header: true
            }
        };

        var id = $tr.data("id");
        var codigo = $tr.attr("codigo");
        var estado = $tr.attr("estado");
        var padre = $tr.attr("padre");
        var de = $tr.attr("de");
        var archivo = $tr.attr("departamento") + "/" + $tr.attr("anio") + "/" + $tr.attr("codigo");
        var idPxt = $tr.attr("prtr");
        var valAnexo = $tr.attr("anexo");

        var porRecibir = $tr.hasClass("porRecibir");
        var sinRecepcion = $tr.hasClass("sinRecepcion");
        var recibido = $tr.hasClass("recibido");
        var retrasado = $tr.hasClass("retrasado");
        var conAnexo = $tr.hasClass("conAnexo")
        var conPadre = $tr.hasClass("padre")

        var detalles = {
            label   : 'Detalles',
            icon   : "fa fa-search",
            action : function (e) {
                $.ajax({
                    type    : 'POST',
                    url     : '${createLink(controller: 'tramite3', action: 'detalles')}',
                    data    : {
                        id : id
                    },
                    success : function (msg) {
                        $("#dialog-body").html(msg)
                    }
                });
                $("#dialog").modal("show")
            }
        };


        var ver = {
            label   : 'Ver',
            icon   : "fa fa-search",
            action : function (e) {
                $.ajax({
                    type    : 'POST',
                    url     : '${createLink(action: 'revisarConfidencial')}/' + id,
                    success : function (msg) {
                        if (msg == 'ok') {
                            window.open("${resource(dir:'tramites')}/" + archivo + ".pdf");
                        } else if (msg == 'no') {
//                                    log("No tiene permiso para ver este trámite", 'danger')
                            bootbox.alert('No tiene permiso para ver el PDF de este trámite')
                        }
                    }

                });
            }
        };


        items.header.label = "Acciones";

        items.ver = ver
        items.detalles = detalles
        return items




    };

    $(".btnBuscar").click(function () {

        $(".buscar").attr("hidden", false)

    });


    $(".btnSalir").click(function () {


        $(".buscar").attr("hidden", true)
        openLoader();
        cargarBandeja();
        closeLoader();

    });

    $(".btnActualizar").click(function () {
        openLoader();
        cargarBandeja();
        closeLoader();
        log("Tabla de trámites archivados actualizada", 'success', 'Trámites Archivados', true)
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