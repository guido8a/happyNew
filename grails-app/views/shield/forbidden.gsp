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
    <title>No disponible</title>
</head>

<body>

<div class="alert alert-warning text-shadow">
    <i class="fa fa-ban fa-4x pull-left"></i>

    <h1 class="text-warning tituloError">Atención</h1>


    <g:if test="${msn}">
        <p style="font-size: 16px; margin-top: 25px;text-shadow: none"><b>${msn}.</b></p>
    </g:if>
    <g:else>
        <p style="font-size: 16px; margin-top: 25px;text-shadow: none">La página solicitada no está disponible.</p>
    </g:else>
%{--
    <p style="font-size: 16px; margin-top: 25px;text-shadow: none">Por favor utilice el menú de navegación para acceder a
    las diferentes pantallas del sistema.</p>
--}%
</div>

</body>
</html>