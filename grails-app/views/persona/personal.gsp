<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 2/18/14
  Time: 12:39 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Configuración personal</title>

        %{--<script src="${resource(dir: 'js/plugins/jQuery-File-Upload-9.5.6/js/vendor', file: 'jquery.ui.widget.js')}"></script>--}%
        %{--<script src="${resource(dir: 'js/plugins/jQuery-File-Upload-9.5.6/js', file: 'jquery.iframe-transport.js')}"></script>--}%
        %{--<script src="${resource(dir: 'js/plugins/jQuery-File-Upload-9.5.6/js', file: 'jquery.fileupload.js')}"></script>--}%

        <!-- The jQuery UI widget factory, can be omitted if jQuery UI is already included -->
        <script src="${resource(dir: 'js/plugins/jQuery-File-Upload-9.5.6/js/vendor', file: 'jquery.ui.widget.js')}"></script>
        <!-- The Load Image plugin is included for the preview images and image resizing functionality -->
        <script src="${resource(dir: 'js/plugins/jQuery-File-Upload-9.5.6/js/imgResize', file: 'load-image.min.js')}"></script>
        <!-- The Canvas to Blob plugin is included for image resizing functionality -->
        <script src="${resource(dir: 'js/plugins/jQuery-File-Upload-9.5.6/js/imgResize', file: 'canvas-to-blob.min.js')}"></script>
        <!-- The Iframe Transport is required for browsers without support for XHR file uploads -->
        <script src="${resource(dir: 'js/plugins/jQuery-File-Upload-9.5.6/js', file: 'jquery.iframe-transport.js')}"></script>
        <!-- The basic File Upload plugin -->
        <script src="${resource(dir: 'js/plugins/jQuery-File-Upload-9.5.6/js', file: 'jquery.fileupload.js')}"></script>
        <!-- The File Upload processing plugin -->
        <script src="${resource(dir: 'js/plugins/jQuery-File-Upload-9.5.6/js', file: 'jquery.fileupload-process.js')}"></script>
        <!-- The File Upload image preview & resize plugin -->
        <script src="${resource(dir: 'js/plugins/jQuery-File-Upload-9.5.6/js', file: 'jquery.fileupload-image.js')}"></script>

        <link href="${resource(dir: 'js/plugins/jQuery-File-Upload-9.5.6/css', file: 'jquery.fileupload.css')}" rel="stylesheet">

        <style type="text/css">
        .table {
            font-size     : 13px;
            width         : auto !important;
            margin-bottom : 0 !important;
        }

        .container-celdasAcc {
            max-height : 200px;
            width      : 804px; /*554px;*/
            overflow   : auto;
        }

        .col100 {
            width : 100px;
        }

        .col200 {
            width : 250px;
        }

        .col300 {
            width : 304px;
        }

        .col-md-1.xs {
            width : 45px;
        }

        </style>

    </head>

    <body>
        <div class="form-group keeptogether">
            <div>
                <span class="col-md-12" style="text-align: center">
                    <div class="panel panel-default" style="margin-left: 30px;">
                        <div class="panel-heading">
                            Configuración personal de los datos del usuario: <strong>${usuario.nombre} ${usuario.apellido}</strong>
                        </div>
                    </div>
                </span>
            </div>
        </div>

        <div class="panel-group" id="accordion">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#accordion" href="#collapsePass">
                            Cambiar password
                        </a>
                    </h4>
                </div>

                <div id="collapsePass" class="panel-collapse collapse ">
                    <div class="panel-body">
                        <g:form class="form-horizontal" name="frmPass" role="form" action="savePass_ajax" method="POST">
                            <div class="form-group required">
                                <span class="grupo">
                                    <label for="accsFechaInicial" class="col-md-2 xs control-label text-info">
                                        Password actual
                                    </label>

                                    <div class="col-md-3">
                                        <div class="input-group">
                                            <g:passwordField name="password_actual" class="form-control required"/>
                                            <span class="input-group-addon"><i class="fa fa-unlock"></i></span>
                                        </div>
                                    </div>
                                </span>
                            </div>

                            <div class="form-group required">
                                <span class="grupo">
                                    <label for="accsFechaInicial" class="col-md-2 xs control-label text-info">
                                        Nuevo password
                                    </label>

                                    <div class="col-md-3">
                                        <div class="input-group">
                                            <g:passwordField name="password" class="form-control required"/>
                                            <span class="input-group-addon"><i class="fa fa-lock"></i></span>
                                        </div>
                                    </div>
                                </span>
                                <span class="grupo">
                                    <label for="accsFechaInicial" class="col-md-2 xs control-label text-info">
                                        Repita password
                                    </label>

                                    <div class="col-md-3">
                                        <div class="input-group">
                                            <g:passwordField name="password_again" class="form-control required" equalTo="#password"/>
                                            <span class="input-group-addon"><i class="fa fa-lock"></i></span>
                                        </div>
                                    </div>
                                </span>

                                <div class="col-md-2">
                                    <a href="#" class="btn btn-success" id="btnPass">
                                        <i class="fa fa-save"></i> Guardar
                                    </a>
                                </div>
                            </div>
                        </g:form>
                    </div>
                </div>
            </div>

            <div class="panel panel-default">
                <div class="panel-heading">
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#accordion" href="#collapseFoto">
                            Cambiar foto
                        </a>
                    </h4>
                </div>

                <div id="collapseFoto" class="panel-collapse collapse in ">
                    <div class="panel-body">
                        <g:if test="${usuario.foto && usuario.foto != ''}">
                            FOTO AQUI
                        </g:if>
                        <g:else>
                            <div class="alert alert-info">
                                <i class="fa fa-picture-o fa-2x"></i>
                                No ha subido ninguna fotografía
                            </div>
                        </g:else>

                        <span class="btn btn-success fileinput-button">
                            <i class="glyphicon glyphicon-plus"></i>
                            <span>Seleccionar imagen</span>
                            <!-- The file input field used as target for the file upload widget -->
                            <input type="file" multiple="" name="file" id="file">
                        </span>

                    </div>
                </div>
            </div>

            <div class="panel panel-default">
                <div class="panel-heading">
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#accordion" href="#collapseAcceso">
                            Cambiar restricciones al sistema
                        </a>
                    </h4>
                </div>

                <div id="collapseAcceso" class="panel-collapse collapse">
                    <div class="panel-body">
                        <h4>Agregar restricción</h4>

                        <p>
                            Se agregará una restricción de acceso al sistema entre las fechas seleccionadas (inclusive).
                        </p>
                        <g:form class="form-horizontal" name="frmAccesos" role="form" action="saveAccesos_ajax" method="POST">
                            <div class="form-group required">
                                <span class="grupo">
                                    <label for="accsFechaInicial" class="col-md-1 xs control-label text-info">
                                        Desde
                                    </label>

                                    <div class="col-md-2">
                                        <elm:datepicker name="accsFechaInicial" title="desde"
                                                        class="datepicker form-control required" daysOfWeekDisabled="0,6"
                                                        onChangeDate="validarFechasAcceso"/>
                                    </div>
                                </span>

                                <span class="grupo">
                                    <label for="accsFechaFinal" class="col-md-1 xs control-label text-info">
                                        Hasta
                                    </label>

                                    <div class="col-md-2">
                                        <elm:datepicker name="accsFechaFinal" title="hasta"
                                                        class="datepicker form-control required" daysOfWeekDisabled="0,6"/>
                                    </div>
                                </span>

                                <span class="grupo">
                                    <label for="accsObservaciones" class="col-md-1 xs control-label text-info">
                                        Obs.
                                    </label>

                                    <div class="col-md-3">
                                        <g:textField class=" form-control" name="accsObservaciones" style="width:100%;"/>
                                    </div>
                                </span>

                                <div class="col-md-2 text-center">
                                    <a href="#" class="btn btn-success" id="btnAccesos">
                                        <i class="fa fa-plus"></i> Agregar
                                    </a>
                                </div>
                            </div>
                        </g:form>
                        <div id="divAccesos"></div>
                    </div>
                </div>
            </div>

        </div>


        <script type="text/javascript">

            function loadAccesos() {
                var $div = $("#divAccesos");
                $div.html(spinnerSquare64);
                $.ajax({
                    type    : "POST",
                    url     : "${createLink(action:'accesos')}",
                    data    : {
                        id : "${usuario.id}"
                    },
                    success : function (msg) {
                        $div.html(msg);
                    }
                });
            }

            $(function () {
                var $btnAccesos = $("#btnAccesos");
                var $frmAccesos = $("#frmAccesos");
                var $btnPass = $("#btnPass");
                var $frmPass = $("#frmPass");

                $('#file').fileupload({
                    url                : '${createLink(action:'uploadFile')}',
                    dataType           : 'json',
                    maxNumberOfFiles   : 1,
                    add                : function (e, data) {
                        console.log("add", data);
                        data.context = $('<p/>').text('Uploading...').appendTo(document.body);
                        data.submit();
                    },
                    done               : function (e, data) {
                        console.log("done");
                        data.context.text('Upload finished.');
                    },
                    // Enable image resizing, except for Android and Opera,
                    // which actually support image resizing, but fail to
                    // send Blob objects via XHR requests:
                    disableImageResize : /Android(?!.*Chrome)|Opera/
                            .test(window.navigator && navigator.userAgent),
                    imageMaxWidth      : 80,
                    imageMaxHeight     : 80,
                    imageCrop          : true // Force cropped images
                });

                function submitPass() {
                    var url = $frmPass.attr("action");
                    var data = $frmPass.serialize();
                    $btnPass.hide().after(spinner);
                    $.ajax({
                        type    : "POST",
                        url     : url,
                        data    : data,
                        success : function (msg) {
                            var parts = msg.split("_");
                            log(parts[1], parts[0] == "OK" ? "success" : "error");
                            spinner.remove();
                            $btnPass.show();
                            $frmPass.find("input").val("");
                            validatorPass.resetForm();
                        }
                    });
                }

                loadAccesos();

                $frmPass.find("input").keyup(function (ev) {
                    if (ev.keyCode == 13) {
                        submitPass();
                    }
                });

                var validatorPass = $frmPass.validate({
                    errorClass     : "help-block",
                    errorPlacement : function (error, element) {
                        if (element.parent().hasClass("input-group")) {
                            error.insertAfter(element.parent());
                        } else {
                            error.insertAfter(element);
                        }
                        element.parents(".grupo").addClass('has-error');
                    },
                    rules          : {
                        password_actual : {
                            remote : {
                                url  : "${createLink(action:'validarPass_ajax')}",
                                type : "post"
                            }
                        }
                    },
                    messages       : {
                        password_actual : {
                            remote : "El password actual no coincide"
                        }
                    },
                    success        : function (label) {
                        label.parents(".grupo").removeClass('has-error');
                    }
                });
                $btnPass.click(function () {
                    submitPass();
                });

                $frmAccesos.validate({
                    errorClass     : "help-block",
                    errorPlacement : function (error, element) {
                        if (element.parent().hasClass("input-group")) {
                            error.insertAfter(element.parent());
                        } else {
                            error.insertAfter(element);
                        }
                        element.parents(".grupo").addClass('has-error');
                    },
                    success        : function (label) {
                        label.parents(".grupo").removeClass('has-error');
                    }
                });
                $btnAccesos.click(function () {
                    if ($frmAccesos.valid()) {
                        var url = $frmAccesos.attr("action");
                        var data = "usuario.id=${usuario.id}";
                        data += "&" + $frmAccesos.serialize();
                        $btnAccesos.hide().after(spinner);
                        $.ajax({
                            type    : "POST",
                            url     : url,
                            data    : data,
                            success : function (msg) {
                                var parts = msg.split("_");
                                log(parts[1], parts[0] == "OK" ? "success" : "error");
                                spinner.remove();
                                $btnAccesos.show();
                                $frmAccesos.find("input, textarea").val("");
                                $("#accsFechaInicial").val("date.struct");
                                $("#accsFechaFinal").val("date.struct");
                                loadAccesos();
                            }
                        });
                    }

                    return false;
                });
            });
        </script>

    </body>
</html>