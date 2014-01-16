<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 1/15/14
  Time: 4:53 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="login">
        <title>Login</title>

        <style type="text/css">
        input {
            margin-top : 15px;
        }
        </style>
    </head>

    <body>
        <elm:flashMessage tipo="${flash.tipo}" icon="${flash.icon}" clase="${flash.clase}">${flash.message}</elm:flashMessage>

        <g:form name="frmLogin" action="validar" class="form-signin" role="form" style="width: 300px;">
            <h2 class="form-signin-heading">Ingreso</h2>
            <input name="login" type="text" class="form-control" placeholder="Usuario" required autofocus>
            <input name="pass" type="password" class="form-control" placeholder="Password" required>

            <button class="btn btn-lg btn-primary btn-block" type="submit" style="margin-top: 30px;">Entrar</button>
        </g:form>
    </body>
</html>