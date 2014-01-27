<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="login">
    <title>Login</title>

    <style type="text/css">
    .archivo {
        width: 100%;
        float: left;
        margin-top: 30px;
        text-align: center;
    }
    </style>

</head>

<body>

<div style="text-align: center;"><h1 class="titl" style="font-size: 38px; color: #06a; font-family:'Book Antiqua'; margin-top: -20px;">Nuevo S.A.D.</h1>

    <h1 class="titl" style="font-size: 20px;">Sistema de Administración de Documentos<br>del<br>
        GOBIERNO AUTÓNOMO DESCENTRALIZADO PROVINCIA DE PICHINCHA</h1>

    <h1 class="titl" style="font-size: 24px; color: #06a">Ingreso al Sistema</h1></div>

<div class="dialog ui-corner-all" style="height: 595px;padding: 10px;width: 910px;margin: auto;margin-top: 5px">
    <div style="text-align: center; margin-top: 10px; color: #810;">
        <img src="${resource(dir: 'images', file: 'logoSAD.png')}"/>
    </div>

    <div style="width: 100%;height: 20px;float: left;margin-top: 20px;text-align: center">
        <a href="#" id="ingresar" class="btn btn-success btn-lg btn-block btn-login" style="width: 400px; margin: auto">
            <i class="icon-off"></i>
            Ingresar
        </a>
    </div>

    <div class="archivo">
        Le recomendamos descargar y leer el
        <a href="${createLink(uri: '/Manual sep-oferentes.pdf')}"><img
                src="${resource(dir: 'images', file: 'pdf_pq.png')}"/>manual de usuario</a>
    </div>
    <div style="text-align: center ; color:#004060; margin-top:120px; font-size: 10px;">Desarrollado por: TEDEIN S.A. Versión ${message(code: 'version', default: '1.1.0x')}</div>
</div>




<elm:flashMessage tipo="${flash.tipo}" icon="${flash.icon}" clase="${flash.clase}">${flash.message}</elm:flashMessage>

<div class="modal fade" id="modal-ingreso" tabindex="-1" role="dialog" aria-labelledby=""
     aria-hidden="true">
    <div class="modal-dialog" id="modalBody">
        <div class="modal-content" style="width: 302px;">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Ingreso al Nuevo S.A.D.</h4>
            </div>

            <div class="modal-body" style="width: 240px; margin: auto">
                <g:form name="frmLogin" action="validar" class="form-horizontal" role="form">
                    <input name="login" type="text" class="form-control required " placeholder="Usuario" required autofocus">
                    <input name="pass" type="password" class="form-control required" placeholder="Contraseña" required>

                    <div class="divBtn" style="width: 100%">
                        <a href="#" class="btn btn-success btn-lg btn-block" id="btn-login" style="width: 140px; margin: auto">
                            <i class="fa fa-lock"></i> Ingresar
                        </a>
                    </div>
                </g:form>
            </div>
        </div>
    </div>
</div>



<script type="text/javascript">
    var $frm = $("#frmLogin");
    function doLogin() {
        if ($frm.valid()) {
            $(".btn-login").replaceWith(spinner);
            $("#frmLogin").submit();
        }
    };

    $(function () {
        $("#ingresar").click(function () {
            $("#modal-ingreso").modal('show');
        });

        $frm.validate();
        $("#btn-login").click(function () {
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