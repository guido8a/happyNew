<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 1/16/14
  Time: 11:40 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <link href="${resource(dir: 'bootstrap-3.0.1/css', file: 'bootstrap.spacelab.css')}" rel="stylesheet">

        <meta name="layout" content="login">
        <title>Perfil</title>
    </head>

    <body>
        <div style="text-align: center; width: 100%; margin-left: 35%; margin-top: 20%">
            <elm:flashMessage tipo="${flash.tipo}" icon="${flash.icon}" clase="${flash.clase}">${flash.message}</elm:flashMessage>

            <g:form name="frmLogin" action="savePer" class="form-signin well" role="form" style="width: 300px;">
                <h2 class="text-center">Perfil</h2>
                <g:select name="prfl" class="form-control" from="${perfilesUsr}" optionKey="id" optionValue="perfil"/>
                <div class="divBtn">
                    <a href="#" class="btn btn-primary btn-lg btn-block btn-login" style="width: 140px; margin: auto">
                        <i class="fa fa-lock"></i> Entrar
                    </a>
                </div>
            </g:form>
        </div>
        <script type="text/javascript">
            var $frm = $("#frmLogin");
            function doLogin() {
                if ($frm.valid()) {
                    $(".btn-login").replaceWith(spinner);
                    $("#frmLogin").submit();
                }
            }
            $(function () {
                $frm.validate();
                $(".btn-login").click(function () {
                    doLogin();
                });
                $("input").keyup(function (ev) {
                    if (ev.keyCode == 13) {
                        doLogin();
                    }
                })
            });
        </script>

    </body>
</html>