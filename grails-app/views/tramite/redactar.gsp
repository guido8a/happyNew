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
        <textarea class="editor" rows="100" cols="80"></textarea>


        <script>
            $(function () {
                //  Checks whether CKEDITOR is defined or not

                if (typeof CKEDITOR != "undefined") {
                    $('textarea.editor').ckeditor({
                        height  : 600,
                        toolbar : [
                            [ 'Save', 'NewPage', 'Preview', 'Print' ],
                            [ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo' ],
                            [ 'Find', 'Replace', '-', 'SelectAll' ],
                            [ 'Image', 'Table', 'HorizontalRule', 'Smiley', 'SpecialChar', 'PageBreak', 'Iframe' ],
                            '/',
                            [ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'RemoveFormat' ],
                            [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock' ],
                            '/',
                            [ 'Styles', 'Format', 'Font', 'FontSize' ],
                            [ 'TextColor', 'BGColor' ] ,
                            [ 'Maximize' ],
                            [ '-' ] ,
                            [ 'About' ]
                        ]
                    });
                }
            });
        </script>
    </body>
</html>


