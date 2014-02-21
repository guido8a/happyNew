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

       <div>
           <label></label>
       </div>


        <textarea class="editor" rows="100" cols="80"></textarea>


        <script>
            $(function () {
                //  Checks whether CKEDITOR is defined or not

                if (typeof CKEDITOR != "undefined") {
                    $('textarea.editor').ckeditor({
                        height  : 600,
                        toolbar : [
                            [ 'Save', 'NewPage', 'Preview', 'Print' , '-', 'Scayt'],
                            [ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo' ],
                            [ 'Find', 'Replace', '-', 'SelectAll' ],
                            [ 'Table', 'HorizontalRule', 'PageBreak'],
                            ['Image'],
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


