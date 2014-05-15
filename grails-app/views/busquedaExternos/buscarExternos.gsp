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
            <div class="col-md-2" style="margin-right: 20px">
                <label for="memorando">Documento</label>
                <g:textField name="memorando" value="" maxlength="15" class="form-control allCaps" style="width: 180px"/>

            </div>

            <div class="col-md-2">
            <a href="#" name="busqueda" class="btn btn-success btnBusqueda" style="margin-top: 22px"><i class="fa fa-check-square-o"></i> Buscar</a>
            </div>

        </div>


    </fieldset>

</div>

<div id="tabla">

</div>


<div class="modal fade " id="dialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Detalles</h4>
            </div>

            %{--<div style="margin-bottom: 20px;min-height: 140px" class="vertical-container">--}%
                %{--<p class="css-vertical-text">Detalles</p>--}%
                <div class="modal-body" id="dialog-body" style="padding: 15px">

                </div>
            %{--</div>       --}%
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cerrar</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div>

<script type="text/javascript">


    var detalles = {
        text   : 'Detalles',
        icon   : "<i class='fa fa-search'></i>",
        action : function (e) {
            $("tr.trHighlight").removeClass("trHighlight");
            e.preventDefault();
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

    context.settings({
        onShow : function (e) {
            $("tr.trHighlight").removeClass("trHighlight");
            var $tr = $(e.target).parents("tr");
            $tr.addClass("trHighlight");
            id = $tr.data("id");
//            idPxt = $tr.attr("prtr");
//            archivo = $tr.attr("departamento") + "/" + $tr.attr("codigo")
//            valAnexo = $tr.attr("anexo");
        }
    });

    context.attach('tr', [
        {
            header : 'Acciones'
        },

        detalles

    ]);


    $(".btnBusqueda").click(function () {

        var memorando = $("#memorando").val();
        var datos = "memorando=" + memorando

        $.ajax ({ type : "POST", url: "${g.createLink(controller: 'busquedaExternos', action: 'tablaBusquedaExternos')}",
            data: datos,
            success: function (msg) {
                openLoader();
                $("#tabla"). html(msg);
                closeLoader();
            }
        });
    });


    $("input").keyup(function (ev) {
        if (ev.keyCode == 13) {

            var memorando = $("#memorando").val();
            var datos = "memorando=" + memorando

            $.ajax ({ type : "POST", url: "${g.createLink(controller: 'busquedaExternos', action: 'tablaBusquedaExternos')}",
                data: datos,
                success: function (msg) {
                    openLoader();
                    $("#tabla"). html(msg);
                    closeLoader();
                }
            });

        }
    });


</script>




</body>
</html>