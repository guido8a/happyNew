<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 1/21/14
  Time: 12:42 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="login">
        <title>Cambio de password</title>
    </head>

    <body>
        <elm:flashMessage tipo="${flash.tipo}" icon="${flash.icon}" clase="${flash.clase}">${flash.message}</elm:flashMessage>

        <g:form name="frmLogin" action="guardarPass" class="form-signin well" role="form" style="width: 300px;">
            <g:hiddenField name="id" value="${usu.id}"/>
            <h2 class="text-center">Cambiar password</h2>

            <p>Su password ha caducado. Por favor cámbielo.</p>

            <input name="pass" id="pass" type="password" class="form-control required" notEqual="${usu.cedula}" placeholder="Nuevo password" required>
            <input name="pass2" id="pass2" type="password" class="form-control required" equalTo="#pass" placeholder="Repita su password" required>

            <div class="divBtn">
                <a href="#" class="btn btn-success btn-lg btn-block btn-login">
                    <i class="fa fa-save"></i> Guardar
                </a>
            </div>
        </g:form>

        <script type="text/javascript">
            var $frm = $("#frmLogin");
            function doLogin() {
                if ($frm.valid()) {
                    $(".btn-login").replaceWith(spinner);
                    $("#frmLogin").submit();
                }
            }
            $(function () {
                $frm.validate({
                    messages : {
                        pass : {
                            notEqual : "No ingrese su número de cédula"
                        }
                    }
                });
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