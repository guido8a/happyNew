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
    </head>

    <body>

        <div class="row">
            <g:each in="${files}" var="file" status="i">
                <div class="col-sm-3 ${i}">
                    <div class="thumbnail">
                        <a href="#" class="btn btn-danger btn-xs btn-delete pull-right" title="Eliminar" data-file="${file.file}" data-i="${i}" style="margin-bottom: 5px">
                            <i class="fa fa-trash-o"></i>
                        </a>
                        <img src="${resource(dir: file.dir, file: file.file)}"/>

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

        <script type="text/javascript">
            $(function () {
                var effects = ["blind", "bounce", "clip", "drop", "explode", "fold", "highlight", "puff", "pulsate", "scale", "shake", "size", "slide"];
                $(".btn-add").click(function () {
                    window.opener.CKEDITOR.tools.callFunction(${funcNum}, $(this).parents(".thumbnail").children("img").attr("src"));
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