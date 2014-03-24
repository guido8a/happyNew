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
        <!-- The File Upload processing plugin -->
        <script src="${resource(dir: 'js/plugins/jQuery-File-Upload-9.5.6/js', file: 'jquery.fileupload-process.js')}"></script>
        <!-- The File Upload image preview & resize plugin -->
        <script src="${resource(dir: 'js/plugins/jQuery-File-Upload-9.5.6/js', file: 'jquery.fileupload-image.js')}"></script>

        <link href="${resource(dir: 'js/plugins/jQuery-File-Upload-9.5.6/css', file: 'jquery.fileupload.css')}" rel="stylesheet">


        <style type="text/css">
        .row {
            margin-top : 2px;
        }

        .negrilla {
            padding-left : 0px;
        }

        .col-xs-1 {
            line-height : 25px;
        }

        .col-buen-height {
            line-height : 25px;
        }

        .hoja {
            /*background: #abcdef;*/
            margin : auto;
            width  : 16cm;
        }

        .cont {
            margin-top : 10px;
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
                <input type="file" multiple="" name="file" id="file">
            </span>

            <div id="progress" class="progress progress-striped active hide">
                <div class="progress-bar progress-bar-success"></div>
            </div>

            <div id="files"></div>
        </div>
        <script type="text/javascript">
            $(function () {
                $('#file').fileupload({
                    url         : '${createLink(action:'uploadFile')}',
                    formData    : {
                        id : "${tramite.id}"
                    },
                    dataType    : 'json',
                    maxFileSize : 1000000 // 1 MB
                }).on('fileuploadadd',function (e, data) {
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
                }).on('fileuploadprocessalways',function (e, data) {
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
                }).on('fileuploadprogressall',function (e, data) {
//                    console.log("fileuploadprogressall");
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    $('#progress .progress-bar').css(
                            'width',
                            progress + '%'
                    );
                }).on('fileuploaddone',function (e, data) {
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
            });
        </script>
    </body>
</html>