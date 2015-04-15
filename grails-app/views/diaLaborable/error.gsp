<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 3/31/14
  Time: 3:42 PM
--%>

<%@ page import="happy.tramites.Anio" contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Cerrar año</title>
    </head>

    <body>
        <elm:flashMessage tipo="error" contenido="${flash.message}"/>

        <util:renderHTML html="${js}"/>
    </body>
</html>