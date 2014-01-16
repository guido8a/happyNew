<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 1/16/14
  Time: 12:48 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Configurar usuario</title>
    </head>

    <body>

        <div class="well">
            Usuario ${usuario.nombre} ${usuario.apellido}
        </div>


        <div class="panel-group" id="accordion">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#accordion" href="#collapsePerfiles">
                            Perfiles <small>Asignar uno o más perfiles al usuario</small>
                        </a>
                    </h4>
                </div>

                <div id="collapsePerfiles" class="panel-collapse collapse in">
                    <div class="panel-body">
                        <ul>
                            <g:each in="${happy.seguridad.Prfl.list()}" var="perfil">
                                <li>${perfil.nombre}</li>
                            </g:each>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="panel panel-default">
                <div class="panel-heading">
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#accordion" href="#collapsePermisos">
                            Permisos <small>Asignar permisos de edición de documentos</small>
                        </a>
                    </h4>
                </div>

                <div id="collapsePermisos" class="panel-collapse collapse">
                    <div class="panel-body">
                        PERMISOS
                    </div>
                </div>
            </div>

            <div class="panel panel-default">
                <div class="panel-heading">
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#accordion" href="#collapseAcceso">
                            Acceso <small>Restringir temporalmente el acceso al sistema</small>
                        </a>
                    </h4>
                </div>

                <div id="collapseAcceso" class="panel-collapse collapse">
                    <div class="panel-body">
                        ACCESOS
                    </div>
                </div>
            </div>

        </div>

    </body>
</html>