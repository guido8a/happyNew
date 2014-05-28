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
        .file {
            width    : 100%;
            height   : 40px;
            margin   : 0px;
            position : absolute;
            top      : 0px;
            left     : 0px;
            opacity  : 0;
        }

        .fileContainer {
            width         : 100%;
            /*height: 290px;*/
            border        : 2px solid #327BBA;
            padding       : 15px;
            margin-top    : 10px;
            margin-bottom : 10px;
        }

        .etiqueta {
            font-weight : bold;
        }

        .titulo-archivo {
            font-weight : bold;
            font-size   : 18px;
        }

        .progress-bar-svt {
            border     : 1px solid #e5e5e5;
            width      : 100%;
            height     : 25px;
            background : #F5F5F5;
            padding    : 0px;
            margin-top : 10px;
        }

        .progress-svt {
            width            : 0%;
            height           : 23px;
            padding-top      : 5px;
            padding-bottom   : 2px;
            background-color : #428BCA;
            text-align       : center;
            line-height      : 100%;
            font-size        : 14px;
            font-weight      : bold;
        }

        .background-image {
            background-image  : -webkit-linear-gradient(45deg, rgba(255, 255, 255, .15) 10%, transparent 25%, transparent 50%, rgba(255, 255, 255, .15) 50%, rgba(255, 255, 255, .15) 75%, transparent 75%, transparent);
            background-image  : linear-gradient(45deg, rgba(255, 255, 255, .15) 25%, transparent 25%, transparent 50%, rgba(255, 255, 255, .15) 50%, rgba(255, 255, 255, .15) 75%, transparent 75%, transparent);
            -webkit-animation : progress-bar-stripes-svt 2s linear infinite;
            background-size   : 60px 60px; /*importante, el tamanio tiene que respetarse en la animacion */
            animation         : progress-bar-stripes-svt 2s linear infinite;
        }

        @-webkit-keyframes progress-bar-stripes-svt {
            /*el x del from tiene que ser multiplo del x del background size...... mientas mas grande mas rapida es la animacion*/
            from {
                background-position : 120px 0;
            }
            to {
                background-position : 0 0;
            }
        }

        @keyframes progress-bar-stripes-svt {
            from {
                background-position : 120px 0;
            }
            to {
                background-position : 0 0;
            }
        }

        </style>

    </head>

    <body>
        <div class="btn-toolbar toolbar" style="margin-top: 10px !important">
            <div class="btn-group">
                <a href="javascript: history.go(-1)" class="btn btn-primary regresar">
                    <i class="fa fa-arrow-left"></i> Regresar
                </a>
            </div>
        </div>

        <elm:headerTramite tramite="${tramite}" extraTitulo="- Anexos"/>

        %{--<div class="progress-bar-svt ui-corner-all" id="p-b"><div class="progress-svt background-image" id="p" style="width: 50%">50%</div></div>--}%
        <div id="anexos">

        </div>


        <script type="text/javascript">




            var archivos = []

            function cargaDocs() {
                $("#anexos").html("")
//        openLoader("Cargando")
                $.ajax({type : "POST", url : "${g.createLink(controller: 'documentoTramite',action:'cargaDocs')}",
                    data     : "id=${tramite.id}&ver=true",
                    async    : false,
                    success  : function (msg) {
                        $("#anexos").html(msg)
//                closeLoader()
                    }
                });
            }
            $(function () {
                cargaDocs()
            });
        </script>
    </body>
</html>