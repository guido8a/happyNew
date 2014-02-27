<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 2/4/14
  Time: 2:57 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Redactar trámite</title>

        <script src="${resource(dir: 'js/plugins/ckeditor', file: 'ckeditor.js')}"></script>
        <script src="${resource(dir: 'js/plugins/ckeditor/adapters', file: 'jquery.js')}"></script>
    </head>

    <body>

        <div class="alert alert-blanco">
            HEADER DEL TRAMITE
        </div>


        <textarea id="editorTramite" class="editor" rows="100" cols="80"></textarea>


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
                            saveUrl  : '${createLink(controller:"tramiteImagenes", action: "saveTramite")}',
                            saveData : {
                                id : "${tramite.id}"
                            },
                            saveDone : function (msg) {
                                console.log("AQUI", msg);
                            }
                        },
                        toolbar                 : [
                            [ 'ServerSave', 'NewPage', 'Preview', 'Print' , '-', 'Scayt'],
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


