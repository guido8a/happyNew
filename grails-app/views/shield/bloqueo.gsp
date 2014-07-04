<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 26/02/14
  Time: 11:34 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Sistema bloqueado</title>
</head>

<body>

<div class="alert alert-warning ">
    <i class="fa fa-ban fa-4x pull-left"></i>

    <g:if test="${dep}">
        <h1 class="text-warning tituloError">El departamento ${dep} esta bloqueado.</h1>
        <p style="font-size: 16px; margin-top: 25px;text-shadow: none">Durante el bloqueo solo puede ingresar a su <a href="${g.createLink(controller: 'tramite3',action: 'bandejaEntradaDpto')}"> bandeja de entrada</a>. Para terminar el bloqueo del sistema el usuario deberá recibir todos los tramites atrasados.</p>
    </g:if>
    <g:else>
        <h1 class="text-warning tituloError">El usuario ${session.usuario} esta bloqueado.</h1>
        <p style="font-size: 16px; margin-top: 25px;text-shadow: none">Durante el bloqueo solo puede ingresar a su <a href="${g.createLink(controller: 'tramite',action: 'bandejaEntrada')}"> bandeja de entrada</a>. Para terminar el bloqueo del sistema el usuario deberá recibir todos los tramites atrasados.</p>
    </g:else>

    <p style="font-size: 16px; margin-top: 25px;text-shadow: none">Por favor utilice el menú de navegación para acceder a
    las diferentes pantallas del sistema.</p>
</div>

</body>
</html>