<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 11/02/15
  Time: 04:05 PM
--%>

<%@ page import="happy.seguridad.Persona" contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Auditoría</title>
        <style>
        .pg-btn {
            height       : 35px;
            width        : 35px;
            border       : 1px solid #999;
            margin-right : 3px;
            line-height  : 35px;
            font-weight  : bold;
            color        : #000000;
            cursor       : pointer;
            float        : left;
            margin-top   : 3px;;
        }

        .pg-active {
            border : 1px solid #0088CF;
            cursor : default;
        }
        </style>
    </head>

    <body>
        <elm:flashMessage tipo="${flash.tipo}" clase="${flash.clase}">${flash.message}</elm:flashMessage>
        <div style="margin-top: 30px; min-height: 100px;font-size: 11px" class="vertical-container">
            <p class="css-vertical-text" style="bottom: -10px">Parámetros</p>

            <div class="linea"></div>

            <div class="row">
                <div class="col-xs-1 negrilla">Desde:</div>

                <div class="col-xs-2">
                    <elm:datepicker name="desde" value="${new Date()}" class="datepicker form-control required"/>
                </div>

                <div class="col-xs-1 negrilla">Hasta:</div>

                <div class="col-xs-2">
                    <elm:datepicker name="hasta" value="${new Date()}" class="datepicker form-control required"/>
                </div>

                <div class="col-xs-1 negrilla">Operacion:</div>

                <div class="col-xs-2">
                    <g:select name="operacion" id="operacion" from="${operaciones}" optionKey="key" optionValue="value"
                              class="form-control required" noSelection="['-1': 'Todas']"/>
                </div>
            </div>

            <div class="row">
                <div class="col-xs-1 negrilla">Tabla:</div>

                <div class="col-xs-2">
                    <g:select name="domains" id="domains" from="${domains}" optionKey="key" optionValue="value"
                              class="form-control required" noSelection="['-1': 'Todas']"/>
                </div>

                <div class="col-xs-1 negrilla">Usuario:</div>

                <div class="col-xs-2">
                    <g:textField name="usuario" id="usuario" class="form-control"/>
                </div>

                <div class="col-xs-1 negrilla">Trámite:</div>

                <div class="col-xs-2">
                    <g:textField name="tramite" id="tramite" class="form-control allCaps"/>
                </div>

                <div class="col-xs-1 negrilla">
                    <a href="#" id="buscar" class="btn btn-azul">
                        <i class="fa fa-search"></i>
                        Buscar
                    </a>
                </div>
            </div>
        </div>

        <div style="margin-top: 30px; min-height: 100px;font-size: 11px;margin-bottom: 15px" class="vertical-container">
            <p class="css-vertical-text">Búsqueda</p>

            <div class="linea"></div>

            <div class="tablaAudt">

            </div>
        </div>

        <script type="text/javascript">

            function buscarAudt(offset, maxView) {
                var data = {
                    desde     : $("#desde_input").val(),
                    hasta     : $("#hasta_input").val(),
                    operacion : $("#operacion").val(),
                    domain    : $("#domains").val(),
                    usuario   : $("#usuario").val(),
                    tramite   : $("#tramite").val()
                };
                if (offset) {
                    data.offset = offset;
                }
                if (maxView) {
                    data.maxView = maxView;
                }

                openLoader("Buscando");
                $.ajax({
                    type    : "POST",
                    url     : "${createLink(controller:'auditoria', action:'tablaAudt')}",
                    data    : data,
                    success : function (msg) {
                        closeLoader();
                        $(".tablaAudt").html(msg)
                    }
                });

            }

            $("#buscar").click(function () {
                buscarAudt();
                return false;
                %{--openLoader("Buscando");--}%
                %{--$.ajax({--}%
                %{--type    : "POST",--}%
                %{--url     : "${createLink(controller:'auditoria', action:'tablaAudt')}",--}%
                %{--data    : {--}%
                %{--desde     : $("#desde_input").val(),--}%
                %{--hasta     : $("#hasta_input").val(),--}%
                %{--operacion : $("#operacion").val(),--}%
                %{--domain    : $("#domains").val(),--}%
                %{--usuario   : $("#usuario").val(),--}%
                %{--tramite   : $("#tramite").val()--}%
                %{--},--}%
                %{--success : function (msg) {--}%
                %{--closeLoader();--}%
                %{--$(".tablaAudt").html(msg)--}%
                %{--}--}%
                %{--});--}%
            });
        </script>
    </body>
</html>