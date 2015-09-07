<%--
  Created by IntelliJ IDEA.
  User: svt
  Date: 4/23/14
  Time: 12:16 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <style>
        td{
            padding: 5px;
        }
    </style>
</head>

<body>
<table border="1">
<g:each in="${tramites}" var="t">
    <tr>
        <td>${t.id}</td>
        <td>${t.codigo}</td>
        <td>${t.estadoTramite.descripcion}</td>
        <td>${t.fechaEnvio.format("dd-MM.yyyy HH:mm")}</td>
        <td>${t.fechaMaximoRespuesta?.format("dd-MM.yyyy HH:mm")}</td>
        <td>${t.estado}</td>
    </tr>
</g:each>
</table>

</body>
</html>