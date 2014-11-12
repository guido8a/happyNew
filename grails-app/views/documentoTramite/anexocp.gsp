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
                <input type="file" class="hide" multiple="" name="file" id="file">
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
                $(".fileinput-button").click(function () {
                    $("#file").click();
                });
                $("#file").change(function (ev) {
//                    console.log(ev);
//                    console.log($(this));
//                    console.log($(this).val());
//                    console.log($(this)[0].files);
                });
            });
        </script>
    </body>
</html>