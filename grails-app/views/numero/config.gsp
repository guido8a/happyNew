<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 4/30/14
  Time: 4:47 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Parametrización de los números de documento</title>

        <style type="text/css">
        th {
            text-align     : center;
            vertical-align : middle !important;
        }
        </style>
    </head>

    <body>
        <div class="alert alert-info">
            El valor indicado en cada casilla es el último número utilizado, de modo que el siguiente
            documento se generará con el indicado más 1 (el siguiente).<br/>
            En la pantalla se muestran únicamente los departamentos que tienen asignado al menos un tipo de documento.
        </div>
        <!-- botones -->
        <div class="btn-toolbar toolbar">
            <div class="btn-group">
                <a href="#" class="btn btn-default btn-save">
                    <i class="fa fa-save"></i> Guardar
                </a>
            </div>
        <g:link class="btn btn-default col-md-2" style="width: 100px;" controller="inicio" action="parametros"><i class="fa fa-arrow-left"></i> Regresar</g:link>
            %{--<div class="btn-group pull-right col-md-3">--}%
            %{--<div class="input-group">--}%
            %{--<input type="text" class="form-control span2 input-search" placeholder="Buscar" value="${params.search}">--}%
            %{--<span class="input-group-btn">--}%
            %{--<a href="#" class="btn btn-default btn-search">--}%
            %{--<i class="fa fa-search"></i>&nbsp;--}%
            %{--</a>--}%
            %{--</span>--}%
            %{--</div><!-- /input-group -->--}%
            %{--</div>--}%
        </div>
        <g:form action="saveConfig" name="frm-config">
            <util:renderHTML html="${html}"/>
        </g:form>

        <script type="text/javascript">
            function search() {
                $(".warning").removeClass("warning");
                var search = $.trim($(".input-search").val());
                $(".departamento:contains('" + search + "')").each(function () {
                    $(this).parents("tr").addClass("warning");
                });
            }
            $(function () {
                $(".btn-save").click(function () {
                    $("#frm-config").submit();
                    return false;
                });
                $(".btn-search").click(function () {
                    search();
                    return false;
                });
                $(".input-search").keyup(function (ev) {
                    if (ev.keyCode == 13) {
                        search();
                    }
                });
            });
        </script>

    </body>
</html>