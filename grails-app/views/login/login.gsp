<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <link href="${resource(dir: 'bootstrap-3.0.1/css', file: 'bootstrap.spacelab.css')}" rel="stylesheet">

        <meta name="layout" content="login">
        <title>Login</title>

        <style type="text/css">
        .archivo {
            width      : 100%;
            float      : left;
            margin-top : 30px;
            text-align : center;
        }

        .creditos p {
            text-align : justify;
        }
        </style>

    </head>

    <body>

        <div style="text-align: center; margin-top: -60px; height: ${(flash.message) ? '650' : '580'}px;" class="well">
            %{--<h1 class="titl" style="font-size: 38px; color: #06a; font-family:'Book Antiqua'; margin-top: -20px;">Nuevo S.A.D.</h1>--}%
            <div class="page-header" style="margin-top: -10px;">
                <h1>S.A.D. Web</h1>
                %{--</div>--}%
                <h3>
                    <p class="text-info">GOBIERNO AUTÓNOMO DESCENTRALIZADO PROVINCIA DE PICHINCHA</p>

                    <p class="text-info">Sistema de Administración de Documentos</p>
                </h3>
            </div>

            %{--<h1 class="titl" style="font-size: 24px; color: #06a">Ingreso al Sistema</h1></div>--}%
            <elm:flashMessage tipo="${flash.tipo}" icon="${flash.icon}"
                              clase="${flash.clase}">${flash.message}</elm:flashMessage>

            <div class="dialog ui-corner-all" style="height: 295px;padding: 10px;width: 910px;margin: auto;margin-top: 5px">
                <div style="text-align: center; margin-top: 10px; color: #810;">
                    <img src="${resource(dir: 'images', file: 'logoSAD.png')}"/>
                </div>

                <div style="width: 100%;height: 20px;float: left;margin-top: 20px;text-align: center">
                    <a href="#" id="ingresar" class="btn btn-primary" style="width: 400px; margin: auto">
                        <i class="icon-off"></i>Ingresar</a>
                </div>

                <div class="archivo">
                    Le recomendamos descargar y leer el
                    <a href="${createLink(uri: '/manual del usuario.pdf')}"><img
                            src="${resource(dir: 'images', file: 'pdf_pq.png')}"/>manual del usuario</a>
                </div>


                %{--<div style="text-align: center ; color:#004060; margin-top:120px; font-size: 10px;">Desarrollado por: TEDEIN S.A. Versión ${message(code: 'version', default: '1.1.0x')}</div>--}%
                <p class="pull-left" style="font-size: 10px;">
                    <a href="#" id="aCreditos">
                        Créditos
                    </a>
                </p>

                <p class="text-info pull-right" style="font-size: 10px;">
                    Desarrollado por: TEDEIN S.A. Versión ${message(code: 'version', default: '1.1.0x')}
                </p>
            </div>
        </div>

        <div class="modal fade" id="modal-ingreso" tabindex="-1" role="dialog" aria-labelledby=""
             aria-hidden="true">
            <div class="modal-dialog" id="modalBody" style="width: 380px;">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title">Ingreso a S.A.D. web</h4>
                    </div>

                    <div class="modal-body" style="width: 280px; margin: auto">
                        <g:form name="frmLogin" action="validar" class="form-horizontal">
                            <div class="form-group">
                                <label class="col-md-5" for="login">Usuario</label>

                                <div class="controls col-md-5">
                                    %{--<input type="text" id="login" placeholder="Usuario">--}%
                                    <input name="login" id="login" type="text" class="form-control required"
                                           placeholder="Usuario" required autofocus style="width: 160px;">
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-5" for="pass">Contraseña</label>

                                <div class="controls col-md-5">
                                    %{--<input type="password" id="pass" placeholder="Usuario">--}%
                                    <input name="pass" id="pass" type="password" class="form-control required"
                                           placeholder="Contraseña" required style="width: 160px;">
                                </div>
                            </div>

                        %{--<input name="login" type="text" class="form-control required " placeholder="Usuario" required autofocus>--}%
                        %{--<input name="pass" type="password" class="form-control required" placeholder="Contraseña" required>--}%

                            <div class="divBtn" style="width: 100%">
                                <a href="#" class="btn btn-primary btn-lg btn-block" id="btn-login"
                                   style="width: 140px; margin: auto">
                                    <i class="fa fa-lock"></i> Ingresar
                                </a>
                            </div>

                            <div class="control-group" style="margin-top: 20px;">
                                <a href="#" id="btnOlvidoPass"
                                   style="color: #223;margin-left: 70px;text-decoration: none;font-family: 'Open Sans Condensed', serif;font-weight: bold">
                                    Olvidó su contraseña?
                                </a>
                            </div>

                        </g:form>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal fade" id="recuperarPass-dialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
            <div class="modal-dialog" id="modalRcps" style="width: 380px;">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title">Recuperar la contraseña</h4>
                    </div>

                    <div class="modal-body" style="width: 280px; margin: auto">
                        <g:form name="frmPass" action="olvidoPass" class="form-horizontal">
                            <div class="form-group">
                                <div>
                                    Ingrese el email registrado en su usuario para enviarle una nueva contraseña
                                    para ingresar al sistema.<br>
                                </div>

                                <div>

                                    <input name="email" id="mail" type="text" class="form-control required"
                                           placeholder="mail" required autofocus style="width: 270px; margin-top: 10px;">
                                </div>
                            </div>

                            <div class="divBtn" style="width: 100%">
                                <a href="#" class="btn btn-primary" id="btn-pass" type="button"
                                   style="width: 140px; margin: auto">
                                    <i class="fa fa-lock"></i> Aceptar
                                </a>
                            </div>

                        </g:form>
                    </div>
                </div>
            </div>
        </div>

        <div id="divCreditos" class="hidden">
            <div class="creditos">
                <p>
                    El Sistema de Administración de Documentos plataforma Web (SADW) es propiedad del
                    Gobierno de la Provincia de Pichincha, contratado bajo consultoría con la empresa TEDEIN S.A.
                    Sistema Desarrollado en base a la primera versión del SAD y con la asesoría técnica de la Gestión
                    de Sistemas y Tecnologías de Información del GADPP.
                </p>

                <p>
                    Los derechos de Autor de este software y los programas fuentes son de propiedad del Gobierno
                    de la Provincia de Pichincha por lo que toda reproducción parcial o total del mismo está
                    prohibida para el contratista y/o terceras personas ajenas.
                </p>
            </div>
        </div>

        <script type="text/javascript">
            var $frm = $("#frmLogin");
            function doLogin() {
                if ($frm.valid()) {
                    $("#btn-login").replaceWith(spinner);
                    $("#frmLogin").submit();
                }
            }

            function doPass() {
                if ($("#frmPass").valid()) {
                    $("#btn-pass").replaceWith(spinner);
                    $("#frmPass").submit();
                }
            }

            $(function () {

                $("#aCreditos").click(function () {
                    bootbox.dialog({
                        title   : "Créditos",
                        message : $("#divCreditos").html(),
                        buttons : {
                            aceptar : {
                                label     : "Aceptar",
                                className : "btn-primary",
                                callback  : function () {
                                }
                            }
                        }
                    });
                    return false;
                });

                $("#ingresar").click(function () {
                    var initModalHeight = $('#modal-ingreso').outerHeight();
                    //alto de la ventana de login: 270
                    $("#modalBody").css({'margin-top' : ($(document).height() / 2 - 135)}, {'margin-left' : $(window).width() / 2});
                    $("#modal-ingreso").modal('show');

                    setTimeout(function () {
                        $("#login").focus();
                    }, 500);

                });

                $("#btnOlvidoPass").click(function () {
                    $("#recuperarPass-dialog").modal("show");
                    $("#modal-ingreso").modal("hide");
                });

                $frm.validate();
                $("#btn-login").click(function () {
                    doLogin();
                });

                $("#btn-pass").click(function () {
                    doPass();
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