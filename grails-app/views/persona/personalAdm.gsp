<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 2/18/14
  Time: 12:39 PM
--%>

<%@ page import="happy.seguridad.Prfl; happy.seguridad.Sesn" contentType="text/html;charset=UTF-8" %>
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
        <div class="form-group">
            <div class="alert alert-info">
                Datos del usuario: <strong>${usuario.nombre} ${usuario.apellido}</strong>
            </div>
        </div>

        %{--${session.perfil}--}%

        <div class="panel-group" id="accordion">

            <g:set var="abierto" value="${false}"/>

        %{--<g:if test="${Sesn.findAllByUsuarioAndPerfil(session.usuario, Prfl.findByCodigo('ADM')) != 0 || happy.utilitarios.Parametros.list().first().validaLDAP == 0}">--}%
            <g:if test="${!usuario.connect || happy.utilitarios.Parametros.list().first().validaLDAP == 0}">
                <g:set var="abierto" value="${true}"/>
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a data-toggle="collapse" data-parent="#accordion" href="#collapsePass">
                                Cambiar contraseña
                            </a>
                        </h4>
                    </div>

                    <div id="collapsePass" class="panel-collapse collapse  ${params.tipo == 'foto' ? '' : 'in'}">
                        <div class="panel-body">
                            <g:form class="form-horizontal" name="frmPass" role="form" action="savePass_ajax" method="POST">
                                <div class="form-group required">
                                    %{--<div class="form-group required">--}%
                                    %{--<span class="grupo">--}%
                                    <span class="form-grup col-md-3">
                                        <label for="accsFechaInicial" class="control-label text-info">
                                            Contraseña actual
                                        </label>

                                        %{--<div class="col-md-2">--}%
                                        <div class="input-group">
                                            <g:passwordField name="password_actual" class="form-control required"/>
                                            <span class="input-group-addon"><i class="fa fa-unlock"></i></span>
                                        </div>
                                        %{--</div>--}%
                                    </span>
                                    %{--</div>--}%

                                    <span class="form-grup col-md-3">
                                        <label for="accsFechaInicial" class="control-label text-info">
                                            Nueva contraseña
                                        </label>

                                        %{--<div class="col-md-3">--}%
                                        <div class="input-group">
                                            <g:passwordField name="password" class="form-control required"/>
                                            <span class="input-group-addon"><i class="fa fa-lock"></i></span>
                                        </div>
                                        %{--</div>--}%
                                    </span>
                                    <span class="form-grup col-md-3">
                                        <label for="accsFechaInicial" class="control-label text-info">
                                            Confirme la contraseña
                                        </label>

                                        %{--<div class="col-md-3">--}%
                                        <div class="input-group">
                                            <g:passwordField name="password_again" class="form-control required" equalTo="#password"/>
                                            <span class="input-group-addon"><i class="fa fa-lock"></i></span>
                                        </div>
                                        %{--</div>--}%
                                    </span>

                                    <div class="col-md-2" style="margin-top: 20px;">
                                        <a href="#" class="btn btn-success" id="btnPass">
                                            <i class="fa fa-save"></i> Guardar
                                        </a>
                                    </div>
                                </div>
                            </g:form>
                        </div>
                    </div>
                </div>
            </g:if>

            <div class="panel panel-default">
                <div class="panel-heading">
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#accordion" href="#collapseFoto">
                            Cambiar foto
                        </a>
                    </h4>
                </div>

                <div id="collapseFoto" class="panel-collapse collapse ${params.tipo == 'foto' || !abierto ? 'in' : ''} ">
                    <div class="panel-body">
                        <div class="btn btn-success fileinput-button" style="margin-bottom: 10px;">
                            <i class="glyphicon glyphicon-plus"></i>
                            <span>Seleccionar imagen</span>
                            <!-- The file input field used as target for the file upload widget -->
                            <input type="file" name="file" id="file">
                        </div>

                        <div class="alert alert-warning" style="float: right; width: 600px;">
                            <i class="fa fa-warning fa-3x pull-left"></i>
                            Si la foto subida es muy grande, se mostrará un área de selección para recortar la imagen al formato requerido.
                        </div>
                        <g:if test="${usuario.foto && usuario.foto != ''}">
                            <div id="divFoto">

                            </div>
                        </g:if>
                        <g:else>
                            <div class="alert alert-info">
                                <i class="fa fa-picture-o fa-2x"></i>
                                No ha subido ninguna fotografía
                            </div>
                        </g:else>

                        <div id="progress" class="progress progress-striped active">
                            <div class="progress-bar progress-bar-success"></div>
                        </div>

                        <div id="files"></div>
                    </div>
                </div>
            </div>

            <div class="panel panel-default">
                <div class="panel-heading">
                    <h4 class="panel-title">
                        <a data-toggle="collapse" data-parent="#accordion" href="#collapseAcceso">
                            Ausentismo: permisos y vacaciones
                        </a>
                    </h4>
                </div>

                <div id="collapseAcceso" class="panel-collapse collapse" style="padding: 10px">
                    <div class="panel-body">
                        <h4>Agregar ausentismo</h4>

                        <p style="margin-bottom: 0px">
                            El usuario se hallará ausente entre las fechas seleccionadas (inclusive).
                        </p>
                        <g:form class="form-horizontal" name="frmAccesos" role="form" action="saveAccesos_ajax" method="POST" style="margin-top: -10px">
                            <div class="form-group required" style="margin-top: 0px;margin-left: 0px">
                                <g:if test="${usuario.esTrianguloOff() && triangulos.size() < 2}">
                                    <h3 style="font-size: 16px;color:#3A87AD">Debe asignar las funciones de Recepción a otro usuario dentro de su departamento</h3>
                                </g:if>
                                <div class="row">
                                    <span class="grupo">
                                        <label for="accsFechaInicial" class="col-md-1 xs control-label text-info">
                                            Desde
                                        </label>

                                        <div class="col-md-2">
                                            <elm:datepicker name="accsFechaInicial" title="desde" minDate="+0" onChangeDate="validaFechas"
                                                            class="datepicker form-control required" daysOfWeekDisabled="0,6"/>
                                        </div>
                                    </span>

                                    <span class="grupo">
                                        <label for="accsFechaFinal" class="col-md-1 xs control-label text-info">
                                            Hasta
                                        </label>

                                        <div class="col-md-2">
                                            <elm:datepicker name="accsFechaFinal" title="hasta" minDate="+0"
                                                            class="datepicker form-control required" daysOfWeekDisabled="0,6"/>
                                        </div>
                                    </span>

                                    <span class="grupo">
                                        <label for="accsObservaciones" class="col-md-1 xs control-label text-info">
                                            Obsr.
                                        </label>

                                        <div class="col-md-3">
                                            <g:textField class=" form-control" name="accsObservaciones" style="width:100%;"/>
                                        </div>
                                    </span>
                                    <g:if test="${!usuario.esTrianguloOff() || triangulos.size() > 1}">
                                        <div class="col-md-2 text-center">
                                            <a href="#" class="btn btn-success" id="btnAccesos">
                                                <i class="fa fa-plus"></i> Agregar
                                            </a>
                                        </div>
                                    </g:if>
                                </div>

                                <div class="row">
                                    <g:if test="${usuario.esTrianguloOff() && triangulos.size() < 2}">
                                        <span class="grupo">
                                            <label for="accsObservaciones" class="col-md-3 xs control-label text-info" style="text-align: left">
                                                Nuevo usuario para recepción de tramites:
                                            </label>

                                            <div class="col-md-3">
                                                <g:select name="nuevoTriangulo" from="${personas}" id="nuevo-triangulo" class="requires form-control" optionKey="id"></g:select>
                                            </div>
                                        </span>

                                        <div class="col-md-2 text-center">
                                            <a href="#" class="btn btn-success" id="btnAccesos-svt">
                                                <i class="fa fa-plus"></i> Agregar
                                            </a>
                                        </div>
                                    </g:if>
                                </div>

                            </div>
                        </g:form>
                        <div id="divAccesos"></div>
                    </div>
                </div>
            </div>

        </div>


        <script type="text/javascript">

            function setDatepicker($datepicker, date) {
                var id = $datepicker.attr("id").split("_")[0];
                $datepicker.datepicker('setDate', date);
                $datepicker.val(date.toString("dd-MM-yyyy"));
                $("#" + id + "_day").val(date.toString("dd"));
                $("#" + id + "_month").val(date.toString("MM"));
                $("#" + id + "_year").val(date.toString("yyyy"));
            }

            function validaFechas($elm, e) {
//                var $ini = $("#accsFechaInicial_input");
                var $ini = $elm;
                var $fin = $("#accsFechaFinal_input");
//                var ini = $ini.datepicker('getDate');
                var ini = e.date;
                var fin = $fin.datepicker('getDate');

                //si la fecha de fin es anterior a la de inicio se cambia a la de inicio
                if (fin.compareTo(ini) == -1) {
                    setDatepicker($fin, ini);
                }
                // cambio el min date de la fecha final para q sea la fecha de inicio
                $fin.datepicker('setStartDate', ini);
            }

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

                $("#password_actual").val("");

                $('#file').fileupload({
                    url              : '${createLink(action:'uploadFile')}',
                    dataType         : 'json',
                    maxNumberOfFiles : 1,
                    acceptFileTypes  : /(\.|\/)(jpe?g|png)$/i,
                    maxFileSize      : 1000000 // 1 MB
                }).on('fileuploadadd', function (e, data) {
//                    console.log("fileuploadadd");
                    openLoader("Cargando");
                    data.context = $('<div/>').appendTo('#files');
                    $.each(data.files, function (index, file) {
                        var node = $('<p/>')
                                .append($('<span/>').text(file.name));
                        if (!index) {
                            node
                                    .append('<br>');
                        }
                        node.appendTo(data.context);
                    });
                }).on('fileuploadprocessalways', function (e, data) {
//                    console.log("fileuploadprocessalways");
                    var index = data.index,
                            file = data.files[index],
                            node = $(data.context.children()[index]);
                    if (file.preview) {
                        node
                                .prepend('<br>')
                                .prepend(file.preview);
                    }
                    if (file.error) {
                        node
                                .append('<br>')
                                .append($('<span class="text-danger"/>').text(file.error));
                    }
                    if (index + 1 === data.files.length) {
                        data.context.find('button')
                                .text('Upload')
                                .prop('disabled', !!data.files.error);
                    }
                }).on('fileuploadprogressall', function (e, data) {
//                    console.log("fileuploadprogressall");
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    $('#progress .progress-bar').css(
                            'width',
                            progress + '%'
                    );
                }).on('fileuploaddone', function (e, data) {
//                    closeLoader();
                    setTimeout(function () {
                        location.href = "${createLink(action: 'personal', params:[tipo:'foto'])}";
                    }, 1000);

//                    $.each(data.result.files, function (index, file) {
//                        $('#progress .progress-bar').css(
//                                'width', '0%'
//                        );
//                        $("#files").empty();
////                        loadFoto();
//                        if (file.url) {
////                            var link = $('<a>')
////                                    .attr('target', '_blank')
////                                    .prop('href', file.url);
////                            $(data.context.children()[index])
////                                    .wrap(link);
//                        } else if (file.error) {
//                            var error = $('<span class="text-danger"/>').text(file.error);
//                            $(data.context.children()[index])
//                                    .append('<br>')
//                                    .append(error);
//                        }
//                    });
                }).on('fileuploadfail', function (e, data) {
                    closeLoader();
                    $.each(data.files, function (index, file) {
                        var error = $('<span class="text-danger"/>').text('File upload failed.');
                        $(data.context.children()[index])
                                .append('<br>')
                                .append(error);
                    });
                });

                function loadFoto() {
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action: 'loadFoto')}",
                        success : function (msg) {
                            $("#divFoto").html(msg);
                        }
                    });
                }

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
                loadFoto();

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
//                $btnAccesos.hide().after(spinner);
                        $.ajax({
                            type    : "POST",
                            url     : url,
                            data    : data,
                            success : function (msg) {
                                var parts = msg.split("_");
//                        console.log(parts)
                                if (parts.length == 3) {
                                    log(parts[1] + ". Usted será desconectado del sistema en 5 segundos", parts[0] == "OK" ? "success" : "error");
                                    spinner.remove();
                                    $("#btnAccesos-svt").show();
                                    $frmAccesos.find("input, textarea").val("");
                                    $("#accsFechaInicial").val("date.struct");
                                    $("#accsFechaFinal").val("date.struct");
                                    loadAccesos();
                                    setInterval(function () {
                                        location.href = "${g.createLink(controller: 'login',action: 'logout')}"
                                    }, 5000);
                                } else {
                                    log(parts[1], parts[0] == "OK" ? "success" : "error");
                                    spinner.remove();
                                    $("#btnAccesos-svt").show();
                                    $frmAccesos.find("input, textarea").val("");
                                    $("#accsFechaInicial").val("date.struct");
                                    $("#accsFechaFinal").val("date.struct");
                                    loadAccesos();
                                }
                            }
                        });
                    }

                    return false;
                });
                $("#btnAccesos-svt").click(function () {
                    if ($frmAccesos.valid()) {
//                console.log($("#nuevo-triangulo").val())
                        if ($("#nuevo-triangulo").val() != "" && !isNaN($("#nuevo-triangulo").val())) {
                            var url = $frmAccesos.attr("action");
                            var data = "usuario.id=${usuario.id}";
                            data += "&" + $frmAccesos.serialize();
                            $("#btnAccesos-svt").hide().after(spinner);
                            $.ajax({
                                type    : "POST",
                                url     : url,
                                data    : data,
                                success : function (msg) {
                                    var parts = msg.split("_");

                                    if (parts.length == 3) {
                                        log(parts[1] + ". Usted será desconectado del sistema en 5 segundos", parts[0] == "OK" ? "success" : "error");
                                        spinner.remove();
                                        $("#btnAccesos-svt").show();
                                        $frmAccesos.find("input, textarea").val("");
                                        $("#accsFechaInicial").val("date.struct");
                                        $("#accsFechaFinal").val("date.struct");
                                        loadAccesos();
                                        setInterval(function () {
                                            location.href = "${g.createLink(controller: 'login',action: 'logout')}"
                                        }, 5000);
                                    } else {
                                        log(parts[1], parts[0] == "OK" ? "success" : "error");
                                        spinner.remove();
                                        $("#btnAccesos-svt").show();
                                        $frmAccesos.find("input, textarea").val("");
                                        $("#accsFechaInicial").val("date.struct");
                                        $("#accsFechaFinal").val("date.struct");
                                        loadAccesos();
                                    }

                                }
                            });
                        } else {
                            bootbox.alert("Debe escoger un usuario para asignarle las funciones de recepcón, en caso de no haber usuarios activos comuniquese con el administrador del sistema.")
                        }

                    }

                    return false;
                });
            })
            ;
        </script>

    </body>
</html>