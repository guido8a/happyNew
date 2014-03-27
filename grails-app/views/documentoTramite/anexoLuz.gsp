<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 3/21/14
  Time: 3:23 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Anexos</title>

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
        <script src="${resource(dir: 'js/plugins/jQuery-File-Upload-9.5.6/js', file: 'jquery.fileupload-ui.js')}"></script>
        <!-- The File Upload processing plugin -->
        <script src="${resource(dir: 'js/plugins/jQuery-File-Upload-9.5.6/js', file: 'jquery.fileupload-process.js')}"></script>
        <!-- The File Upload image preview & resize plugin -->
        <script src="${resource(dir: 'js/plugins/jQuery-File-Upload-9.5.6/js', file: 'jquery.fileupload-image.js')}"></script>
        <script src="${resource(dir: 'js/plugins/jQuery-File-Upload-9.5.6/js', file: 'jquery.fileupload-validate.js')}"></script>

        <link href="${resource(dir: 'js/plugins/jQuery-File-Upload-9.5.6/css', file: 'jquery.fileupload.css')}" rel="stylesheet">

        <style type="text/css">
        .cont {
            margin-top : 10px;
        }

        #files {
            margin-top : 15px;
        }

        .noMarginTop {
            margin-top : 0;
        }
        </style>
    </head>

    <body>
        <elm:headerTramite tramite="${tramite}" extraTitulo="- Cargar anexos"/>

        <div class="cont">
            <span class="btn btn-success fileinput-button">
                <i class="glyphicon glyphicon-plus"></i>
                <span>Seleccionar archivo</span>
                <!-- The file input field used as target for the file upload widget -->
                <input type="file" name="file" id="file">
            </span>

            <div id="progress" class="progress progress-striped active hide">
                <div class="progress-bar progress-bar-success"></div>
            </div>

            <div id="files"></div>
        </div>

        <script type="text/javascript">
            var okContents = {
                'image/png'  : "png",
                'image/jpeg' : "jpeg",
                'image/jpg'  : "jpg",

                'application/pdf' : 'pdf',

                'application/excel'                                                 : 'xls',
                'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : 'xlsx',

                'application/mspowerpoint'                                                  : 'pps',
                'application/vnd.ms-powerpoint'                                             : 'pps',
                'application/powerpoint'                                                    : 'ppt',
                'application/x-mspowerpoint'                                                : 'ppt',
                'application/vnd.openxmlformats-officedocument.presentationml.slideshow'    : 'ppsx',
                'application/vnd.openxmlformats-officedocument.presentationml.presentation' : 'pptx',

                'application/msword'                                                      : 'doc',
                'application/vnd.openxmlformats-officedocument.wordprocessingml.document' : 'docx',

                'application/vnd.oasis.opendocument.text'         : 'odt',
                'application/vnd.oasis.opendocument.presentation' : 'odp',
                'application/vnd.oasis.opendocument.spreadsheet'  : 'ods'
            };
            $(function () {
                function btnCerrar($panel, $footer, isError) {
                    var clase = isError ? "danger" : "success";
                    var icon = isError ? "times" : "check";
                    var $btnCerrar = $("<a href='#' class='btn btn-" + clase + "'/>");
                    $btnCerrar.append("<i class='fa fa-" + icon + "'></i>");
                    $btnCerrar.append("Cerrar");
                    $footer.html($btnCerrar);
                    $btnCerrar.click(function () {
                        $panel.hide({
                            effect   : "fold",
                            duration : 800,
                            complete : function () {
                                $panel.remove();
                            }
                        });
                    });
                }

                $('#file').fileupload({
                    url                    : '${createLink(action:'uploadFile')}',
                    formData               : {
                        id : "${tramite.id}"
                    },
                    maxNumberOfFiles       : 1,
                    singleFileUploads      : true,
                    limitConcurrentUploads : 1,
                    dataType               : 'json',
                    //                    acceptFileTypes  : /(\.|\/)(jpe?g|png|pdf|xlsx?|ppsx?|pptx?|docx?)$/i,
                    //                    maxFileSize      : 11000000, // 1 MB
                    add                    : function (e, data) {
                        //console.group("ADD");
                        var totalFiles = data.files.length;
                        //The add callback can be understood as the callback for the file upload request queue. It is invoked as soon as files are added to the fileupload widget
                        data.context = $("#files");
                        $.each(data.files, function (index, file) {
                            //console.log(index, file);
                            var $panel = $("<div class='panel panel-primary'/>");
                            var $heading = $("<div class='panel-heading'/>");
                            var $title = $("<h3 class='panel-title'/>");
                            var $body = $("<div class='panel-body'/>");
                            var $footer = $("<div class='panel-footer'/>");

                            var $progress = $("<div class='progress progress-striped active hide'/>");
                            var $progressBar = $("<div class='progress-bar progress-bar-info'/>").appendTo($progress);

                            var fileSize = (file.size / 1024) / 1024;

                            if (fileSize < 11) {
                                if (okContents[file.type]) {
                                    var $form = $("<form/>");

                                    var $divError = $("<div class='alert alert-danger hide divError'/>");

                                    var $row1 = $("<div class='row'/>");
                                    var $row2 = $("<div class='row'/>");

                                    var $resumen = $("<div class='col-md-1'>Resumen</div>" +
                                                     "<div class='col-md-5'>" +
                                                     "<textarea class='form-control' required id='resumen' name='resumen' cols='5' rows='5'></textarea>" +
                                                     "</div>").appendTo($row1);
                                    var $descripcion = $("<div class='col-md-1'>Descripción</div>" +
                                                         "<div class='col-md-5'>" +
                                                         "<textarea class='form-control' required id='descripcion' name='descripcion' cols='5' rows='5'></textarea>" +
                                                         "</div>").appendTo($row1);
                                    var $clave = $("<div class='col-md-1'>Palabras clave</div>" +
                                                   "<div class='col-md-11'>" +
                                                   "<input type='text' class='form-control' id='clave' name='clave'/>" +
                                                   "</div>").appendTo($row2);
                                    $form.append($row1).append($row2);
                                    $body.html($divError).append($form).append($progress);
                                    var $btnSubir = $("<button href='#' class='btn btn-success btnSubir start'/>");
                                    $btnSubir.append("<i class='fa fa-upload'></i>");
                                    $btnSubir.append("Subir");
                                    $footer.append($btnSubir);
                                    //                                    //console.log(file);
                                    $btnSubir.click(function () {
//                                        //console.log($(this));
                                        if (data && data.submit) {
                                            data.submit();
                                        }
                                        return false;
                                    });
                                } else {
                                    var $alert = $("<div class='alert alert-danger'/>");
                                    $alert.html("<h3 class='text-danger noMarginTop'><i class='fa fa-warning'></i> Alerta</h3>" +
                                                "<p>No se acepta este tipo de archivo.</p>");
                                    $body.html($alert);
                                    btnCerrar($panel, $footer, true);
                                }
                            } else {
                                var $alert = $("<div class='alert alert-danger'/>");
                                $alert.html("<h3 class='text-danger noMarginTop'><i class='fa fa-warning'></i> Alerta</h3>" +
                                            "<p>No se aceptan archivos de más de 10MB.</p>");
                                $body.html($alert);
                                btnCerrar($panel, $footer, true);
                            }

                            $title.html("Archivo " + (index + 1) + " de " + totalFiles + ": " + file.name);
                            $heading.append($title);
                            $panel.append($heading);
                            $panel.append($body);
                            $panel.append($footer);

                            data.context.append($panel);
                        });
                        //console.groupEnd();
                        //                        data.submit();
                    },
                    submit                 : function (e, data) {
                        //Callback for the submit event of each file upload. If this callback returns false, the file upload request is not started.
                        //console.group("SUBMIT");
                        $.each(data.files, function (index, file) {
                            var $panel = $(data.context.children()[index]);
                            //console.log(index, $(data.context.children()), $panel);

                            var resumen = $.trim($panel.find("#resumen").val());
                            var descripcion = $.trim($panel.find("#descripcion").val());
                            var clave = $.trim($panel.find("#clave").val());

                            if (resumen == "" || descripcion == "") {
                                var $divError = $panel.find(".divError");
                                $divError.text("Por favor complete los campos de resumen y descripción").removeClass("hide");
                                return false;
                            }

                            var $form = $panel.find("form");
                            var $btnSubir = $panel.find(".btnSubir");
                            var $progress = $panel.find(".progress");
                            var $progressBar = $panel.find(".progressBar");

                            var formData = $form.serialize();
                            data.id = "${tramite.id}";
                            $form.addClass("hide");
                            $btnSubir.hide();
                            $progress.removeClass("hide");
                            $progressBar.css({
                                width : 0
                            });
                            data.formData = {
                                resumen     : resumen,
                                descripcion : descripcion,
                                clave       : clave,
                                id          : "${tramite.id}"
                            };
                        });
                        //console.groupEnd();
                    },
                    //                    processalways    : function (e, data) {
                    //                        //Callback for the end (done or fail) of an individual file processing queue.
                    //                        //console.log('Processing ' + data.files[data.index].name + ' ended.');
                    //                    },
                    progress               : function (e, data) {
                        //Callback for global upload progress events.
                        //console.group("PROGRESS");
                        $.each(data.files, function (index, file) {
                            var $panel = $(data.context.children()[index]);
                            //console.log(index, $(data.context.children()), $panel);
                            var $progressBar = $panel.find(".progress-bar");
                            $progressBar.css({
                                width : parseInt(data.loaded / data.total * 100, 10) + "%"
                            });
                        });
                        //console.groupEnd();
                    },
                    /* progressall      : function (e, data) {
                     //Callback for global upload progress events.
                     //console.log("Progress all ", parseInt(data.loaded / data.total * 100, 10));
                     },*/
                    done                   : function (e, data) {
                        //Callback for successful upload requests. This callback is the equivalent to the success callback provided by jQuery ajax() and will also be called if the server returns a JSON response with an error property.
                        //                        data.context.text('Upload finished.');
                        //console.group("DONE");
                        $.each(data.files, function (index, file) {
                            var responseText = $.parseJSON(data.jqXHR.responseText).files[index];
                            var $panel = $(data.context.children()[index]);
                            //console.log(index, $(data.context.children()), $panel);

                            if (responseText.error) {
                                var $alert = $("<div class='alert alert-danger'/>");
                                $alert.html("<h3 class='text-danger noMarginTop'><i class='fa fa-warning'></i> Error</h3>" +
                                            "<p>" + responseText.error + "</p>");

                                $panel.find(".panel-body").html($alert);

                                $("#spanContinuarSistema").remove();

                                btnCerrar($panel, $panel.find(".panel-footer"), true);
                            } else {
                                var $alert = $("<div class='alert alert-success'/>");
                                $alert.html("<h3 class='text-success noMarginTop'><i class='fa fa-check-circle'></i>Éxito</h3>" +
                                            "<p>El archivo <b>" + responseText.name + "</b> fue anexado exitosamente al trámite.</p>");

                                $panel.find(".panel-body").html($alert);

                                btnCerrar($panel, $panel.find(".panel-footer"), false);
                            }
                            setTimeout(function () {
                                $panel.hide({
                                    effect   : "fold",
                                    duration : 800,
                                    complete : function () {
                                        $panel.remove();
                                    }
                                })
                            }, 200);
                        });
                        //                        //console.group("done");
                        //                        //console.log(data.result);
                        //                        //console.log(data.textStatus);
                        //                        //console.log(data.jqXHR);
                        //console.groupEnd();
                    },
                    fail                   : function (e, data) {
                        //Callback for failed (abort or error) upload requests. This callback is the equivalent to the error callback provided by jQuery ajax() and will not be called if the server returns a JSON response with an error property, as this counts as successful request due to the successful HTTP response
                        //                        data.context.text('Upload failed.');
                        $.each(data.files, function (index, file) {
                            var $panel = $(data.context.children()[index]);

                            var $alert = $("<div class='alert alert-danger'/>");
                            $alert.html("<h3 class='text-danger noMarginTop'><i class='fa fa-warning'></i> Error " + data.jqXHR.status + ": " + data.jqXHR.statusText + "</h3>" +
                                        "<p>" + data.jqXHR.responseText + "</p>");

                            $panel.find(".panel-body").html(data.jqXHR.responseText);

                            $("#spanContinuarSistema").remove();

                            btnCerrar($panel, $panel.find(".panel-footer"), true);
                        });
                        //                        //console.group("fail");
                        //                        //console.log(data.errorThrown);
                        //                        //console.log(data.textStatus);
                        //                        //console.log(data.jqXHR);
                        //                        //console.groupEnd();
                    }
                });
            });
        </script>
    </body>
</html>