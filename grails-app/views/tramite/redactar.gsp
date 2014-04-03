<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 2/4/14
  Time: 2:57 PM
--%>

%{--<%@ page contentType="text/html;charset=UTF-8" %>--}%
<!DOCTYPE HTML>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Redactar trámite</title>

        <script src="${resource(dir: 'js/plugins/ckeditor', file: 'ckeditor.js')}"></script>
        <script src="${resource(dir: 'js/plugins/ckeditor/adapters', file: 'jquery.js')}"></script>
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
            width  : 19cm;
        }


        </style>
    </head>

    <body>
        <div class="hoja">

            <div class="btn-toolbar toolbar">
                <div class="btn-group">
                    <g:link action="bandejaEntrada" class="btn btn-sm btn-azul btnRegresar" style="margin-left: 20px;">
                        <i class="fa fa-list-ul"></i> Bandeja de Entrada
                    </g:link>
                </div>

                <div class="btn-group">
                    <g:link action="bandejaEntrada" class="btn btn-sm btn-success btnSave">
                        <i class="fa fa-save"></i> Guardar
                    </g:link>
                    <g:link action="bandejaEntrada" class="btn btn-sm btn-primary btnPrint">
                        <i class="fa fa-file"></i> PDF
                    </g:link>
                </div>
            </div>

            <elm:headerTramite2 tramite="${tramite}"/>

            <textarea id="editorTramite" class="editor" rows="100" cols="80">${tramite.texto}</textarea>
        </div>
        <script>
            $(function () {

                $(".btnSave").click(function () {
                    $.ajax({
                        type    : "POST",
                        url     : '${createLink(controller:"tramite", action: "saveTramite")}',
                        data    : {
                            id            : "${tramite.id}",
                            editorTramite : $("#editorTramite").val(),
                            para          : $("#para").val(),
                            asunto        : $("#asunto").val()
                        },
                        success : function (msg) {
                            var parts = msg.split("_");
                            log(parts[1], parts[0] == "NO" ? "error" : "success");
                        }
                    });
                    return false;
                });
                $(".btnPrint").click(function () {
                    var url = '${createLink(controller:"tramiteExport", action: "crearPdf")}';
                    var data = {
                        id            : "${tramite.id}",
                        editorTramite : $("#editorTramite").val(),
                        para          : $("#para").val(),
                        asunto        : $("#asunto").val(),
                        type          : "download",
                        enviar        : 1
                    };
                    $.ajax({
                        type    : "POST",
                        url     : url,
                        data    : data,
                        success : function (msg) {
                            var parts = msg.split("*");
                            if (parts[0] == "OK") {
                                window.open("${resource(dir:'tramites')}/" + parts[1]);
                            }
                        }
                    });
//                    location.href = url + "?" + $.param(data);
                    return false;
                });

                //  Checks whether CKEDITOR is defined or not
                if (typeof CKEDITOR != "undefined") {
                    $('textarea.editor').ckeditor({
                        height                  : 600,
//                        filebrowserUploadUrl : '/notes/add/ajax/upload-inline-image/index.cfm',
//                        filebrowserBrowseUrl : '/browser/browse.php',
                        filebrowserBrowseUrl    : '${createLink(controller: "tramiteImagenes", action: "browser")}',
                        filebrowserUploadUrl    : '${createLink(controller: "tramiteImagenes", action: "uploader")}',
                        %{--imageBrowser_listUrl    : '${createLink(controller: "tramiteImagenes", action: "list")}',--}%
                        filebrowserWindowWidth  : 950,
                        filebrowserWindowHeight : 500,
                        %{--serverSave              : {--}%
                        %{--saveUrl  : '${createLink(controller:"tramite", action: "saveTramite")}',--}%
                        %{--saveData : {--}%
                        %{--id : "${tramite.id}"--}%
                        %{--},--}%
                        %{--saveDone : function (msg) {--}%
                        %{--var parts = msg.split("_");--}%
                        %{--log(parts[1], parts[0] == "NO" ? "error" : "success");--}%
                        %{--}--}%
                        %{--},--}%
                        %{--createPdf               : {--}%
                        %{--saveUrl   : '${createLink(controller:"tramiteExport", action: "crearPdf")}',--}%
                        %{--saveData  : {--}%
                        %{--id   : "${tramite.id}",--}%
                        %{--type : "download"--}%
                        %{--},--}%
                        %{--pdfAction : "download"/*,--}%
                        %{--createDone : function (msg) {--}%
                        %{--location.href = msg;--}%
                        %{--}*/--}%
                        %{--},--}%
                        toolbar                 : [
//                            [ /*'Source',*//*'ServerSave', *//*'NewPage', *//*'CreatePdf',*/ /*'-',*/ /*'Scayt'*/],
                            ['Scayt', '-', 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo' ],
                            [ 'Find', 'Replace', '-', 'SelectAll' ],
                            [ 'Table', 'HorizontalRule', 'PageBreak'],
                            ['Image'/*, 'Timestamp'*/, '-', 'TextColor', 'BGColor', '-', 'About' ],
                            '/',
                            [ 'Bold', 'Italic', 'Underline', /*'Strike', */'Subscript', 'Superscript'/*, '-', 'RemoveFormat'*/ ],
                            [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'Font', 'FontSize' ]
//                            [/* 'Font', 'FontSize'*/ /*, '-', 'TextColor', 'BGColor'*/]
//                            ['About' ]
                        ]
                    });
                }
            });
        </script>
    </body>
</html>


