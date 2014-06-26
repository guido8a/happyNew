<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 2/24/14
  Time: 12:20 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="noMenu">
        <title>Imágenes disponibles</title>

        <script type="text/javascript" src="${resource(dir: 'js/plugins/MagnificPopup', file: 'MagnificPopup.js')}"></script>
        <link href="${resource(dir: 'js/plugins/MagnificPopup', file: 'MagnificPopup.css')}" rel="stylesheet">

        <style type="text/css">
        .thumbnail {
            width  : 185px;
            height : 265px;
        }

        .mfp-with-zoom .mfp-container,
        .mfp-with-zoom.mfp-bg {
            opacity                     : 0;
            -webkit-backface-visibility : hidden;
            /* ideally, transition speed should match zoom duration */
            -webkit-transition          : all 0.3s ease-out;
            -moz-transition             : all 0.3s ease-out;
            -o-transition               : all 0.3s ease-out;
            transition                  : all 0.3s ease-out;
        }

        .mfp-with-zoom.mfp-ready .mfp-container {
            opacity : 1;
        }

        .mfp-with-zoom.mfp-ready.mfp-bg {
            opacity : 0.8;
        }

        .mfp-with-zoom.mfp-removing .mfp-container,
        .mfp-with-zoom.mfp-removing.mfp-bg {
            opacity : 0;
        }

        .mfp-counter {
            width : 50px;
        }
        </style>
    </head>

    <body>
        <a href="#" id="btnClose" class="btn btn-info" style="margin-bottom: 15px;">Cerrar ventana</a>
        <g:if test="${files.size() > 0}">
            <div class="row">
                <g:each in="${files}" var="file" status="i">
                    <div class="col-sm-3 ${i}">
                        <div class="thumbnail">
                            <a href="#" class="btn btn-danger btn-xs btn-delete pull-right" title="Eliminar" data-file="${file.file}" data-i="${i}" style="margin-bottom: 5px">
                                <i class="fa fa-trash-o"></i>
                            </a>
                            <a class="img" href="${resource(dir: file.dir, file: file.file)}">
                                <img src="${resource(dir: file.dir, file: file.file)}"/>
                            </a>

                            <div class="caption">
                                <p>${file.file}</p>

                                <div class="text-center">
                                    <a href="#" class="btn btn-success btn-sm btn-add">
                                        <i class="fa fa-check"></i> Seleccionar
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </g:each>
            </div>
        </g:if>
        <g:else>
            <div class="alert alert-info">

                <span class="fa-stack fa-lg">
                    <i class="fa fa-picture-o fa-stack-1x text-muted"></i>
                    <i class="fa fa-folder-o fa-stack-2x text-muted"></i>
                </span>

                No tiene imágenes cargadas en el servidor.
            </div>
        </g:else>

        <script type="text/javascript">
            $(function () {

                window.opener.resetTimer();

                $('.row').magnificPopup({
                    delegate : '.img', // child items selector, by clicking on it popup will open
                    type     : 'image',
                    tClose   : 'Cerrar (Esc)',
                    tLoading : 'Cargando...',
                    gallery  : {
                        // options for gallery
                        enabled  : true,
                        tPrev    : 'Anterior (flecha izq.)', // title for left button
                        tNext    : 'Siguiente (flecha der.)', // title for right button
                        tCounter : '<span class="mfp-counter">%curr% de %total%</span>' // markup of counter
                    },
                    zoom     : {
                        enabled : true, // By default it's false, so don't forget to enable it

                        duration : 300, // duration of the effect, in milliseconds
                        easing   : 'ease-in-out', // CSS transition easing function

                        // The "opener" function should return the element from which popup will be zoomed in
                        // and to which popup will be scaled down
                        // By defailt it looks for an image tag:
                        opener   : function (openerElement) {
                            // openerElement is the element on which popup was initialized, in this case its <a> tag
                            // you don't need to add "opener" option if this code matches your needs, it's defailt one.
                            return openerElement.is('img') ? openerElement : openerElement.find('img');
                        }
                    },
                    image    : {
                        verticalFit : true,
                        tError      : '<a href="%url%">La imagen</a> no se pudo cargar.'
                    }
                });

                $("#btnClose").click(function () {
                    window.close();
                });
                var effects = ["blind", "bounce", "clip", "drop", "explode", "fold", "highlight", "puff", "pulsate", "scale", "shake", "size", "slide"];
                $(".btn-add").click(function () {
                    window.opener.CKEDITOR.tools.callFunction(${funcNum}, $(this).parents(".thumbnail").find("img").attr("src"));
                    window.close();
//                    return false;
                });
                $(".btn-delete").click(function () {
                    var file = $(this).data("file");
                    var i = $(this).data("i");
                    var pos = Math.floor((Math.random() * effects.length) + 1);
                    var effect = effects[pos];

                    var msg = "<i class='fa fa-trash-o fa-6x pull-left text-danger text-shadow'></i>" +
                              "<p>¿Está seguro que desea eliminar esta imagen del servidor?</p>" +
                              "<p><b>Esta acción no se puede deshacer.</b></p>" +
                              "<p><b><i>Una vez eliminada la imagen no podrá recuperarla.</i></b></p>";

                    bootbox.dialog({
                        title   : "Alerta",
                        message : msg,
                        buttons : {
                            cancelar : {
                                label     : "Cancelar",
                                className : "btn-primary",
                                callback  : function () {
                                }
                            },
                            eliminar : {
                                label     : "<i class='fa fa-trash-o'></i> Eliminar",
                                className : "btn-danger",
                                callback  : function () {
                                    openLoader("Eliminando");
                                    $.ajax({
                                        type    : "POST",
                                        url     : '${createLink(action:'delete_ajax')}',
                                        data    : {
                                            file : file
                                        },
                                        success : function (msg) {
                                            var parts = msg.split("_");
                                            log(parts[1], parts[0] == "OK" ? "success" : "error"); // log(msg, type, title, hide)
                                            if (parts[0] == "OK") {
                                                closeLoader();
                                                setTimeout(function () {
                                                    $("." + i).hide({
                                                        effect   : effect,
                                                        duration : 1000,
                                                        complete : function () {
                                                            $("." + i).remove();
                                                            if ($(".col-sm-3").length == 0) {
                                                                var alert = '<div class="alert alert-info">';
                                                                alert += '<span class="fa-stack fa-lg">';
                                                                alert += '<i class="fa fa-picture-o fa-stack-1x text-muted"></i>';
                                                                alert += '<i class="fa fa-folder-o fa-stack-2x text-muted"></i>';
                                                                alert += '</span>';
                                                                alert += 'No tiene imágenes cargadas en el servidor.';
                                                                alert += '</div>';
                                                                $(".row").html(alert);
                                                            }
                                                        }
                                                    });
                                                }, 400);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                    return false;
                });
            });
        </script>

    </body>
</html>