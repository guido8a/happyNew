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
        </style>
    </head>

    <body>

        <elm:headerTramite tramite="${tramite}"/>

        <textarea id="editorTramite" class="editor" rows="100" cols="80">${tramite.texto}</textarea>

        <script>
            $(function () {
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
                        serverSave              : {
                            saveUrl  : '${createLink(controller:"tramite", action: "saveTramite")}',
                            saveData : {
                                id : "${tramite.id}"
                            },
                            saveDone : function (msg) {
                                var parts = msg.split("_");
                                log(parts[1], parts[0] == "NO" ? "error" : "success");
                            }
                        },
                        createPdf               : {
                            saveUrl    : '${createLink(controller:"tramiteExport", action: "crearPdf")}',
                            saveData   : {
                                id : "${tramite.id}"
                            },
                            createDone : function (msg) {
                                console.log(msg);
                            }
                        },
                        toolbar                 : [
                            [ 'ServerSave', 'NewPage', 'Preview', 'CreatePdf' , '-', 'Scayt'],
                            [ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo' ],
                            [ 'Find', 'Replace', '-', 'SelectAll' ],
                            [ 'Table', 'HorizontalRule', 'PageBreak'],
                            ['Image', 'Timestamp'],
                            ['About' ],
                            '/',
                            [ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'RemoveFormat' ],
                            [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock' ],

                            [ 'Font', 'FontSize' , '-', 'TextColor', 'BGColor' ]
                        ]
                    });
                }
            });
        </script>
    </body>
</html>


