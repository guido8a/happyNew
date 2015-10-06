<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 08/09/14
  Time: 03:30 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Redireccionar tramites</title>
    </head>

    <body>
        <div class="well">
            Persona de quien se desea redireccionar sus trámites:

            <p class="text-info">Ingrese uno varios criterios de búsqueda</p>
            <form class="form-inline" role="form">
                <div class="form-group">
                    <g:textField name="nombre" class="form-control" placeholder="Nombre"/>
                </div>

                <div class="form-group">
                    <g:textField name="apellido" class="form-control" placeholder="Apellido"/>
                </div>

                <div class="form-group">
                    <g:textField name="user" class="form-control" placeholder="Usuario"/>
                </div>

                <a href="#" class="btn btn-info" id="btnBuscar"><i class="fa fa-search"></i> Buscar</a>
            </form>
        </div>
    Se muestran máximo 10 coincidencias por búsqueda.
        <div class="well" id="divPersonas">
            <div class="alert alert-info">

                <i class="fa fa-info-circle text-shadow pull-left fa-6x"></i>

                <p>
                    Ingrese uno o varios criterios de búsqueda para ubicar a la persona.
                </p>

                <p>
                    Haga clic en el botón Buscar para mostrar los resultados.
                </p>

                <p>
                    No se mostrarán más de 10 coincidencias por búsqueda.
                </p>
            </div>
        </div>



        <script type="text/javascript">
            $(function () {
                $("#btnBuscar").click(function () {
                    $("#divPersonas").html(spinner);
                    $.ajax({
                        type    : 'POST',
                        url     : '${createLink(controller: 'tramiteAdmin', action: 'buscarPersonasRedireccionar')}',
                        data    : {
                            nombre   : $.trim($("#nombre").val()),
                            apellido : $.trim($("#apellido").val()),
                            user     : $.trim($("#user").val())
                        },
                        success : function (msg) {
                            $("#divPersonas").html(msg);
                        }
                    });
                    return false;
                });
            });
        </script>
    </body>
</html>