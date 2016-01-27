<!DOCTYPE HTML>
<html>
    <head>
        <meta name="layout" content="main2">
        <title>Redactar trámite</title>

        <script src="${resource(dir: 'js/plugins/ckeditor', file: 'ckeditor.js')}"></script>
        <script src="${resource(dir: 'js/plugins/ckeditor/adapters', file: 'jquery.js')}"></script>
        <style type="text/css">
/*
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

*/
        .hoja {
            /*background: #abcdef;*/
            margin : auto;
            float  : right;
            width  : 19cm;
        }

        .nota {
            position           : absolute;
            left               : 15px;
            top                : 240px;
            padding            : 10px;
            background         : #EFEFD1;
            border             : solid 1px #867722;
            width              : 235px;
            /*max-height : 345px;*/
            /*overflow   : auto;*/
            z-index            : 1;

            -webkit-box-shadow : 7px 7px 5px 0px rgba(50, 50, 50, 0.75);
            -moz-box-shadow    : 7px 7px 5px 0px rgba(50, 50, 50, 0.75);
            box-shadow         : 7px 7px 5px 0px rgba(50, 50, 50, 0.75);
        }

        .nota .contenido {
            overflow   : auto;
            /*max-height : 325px;*/
        }

        .nota:after {
            position : absolute;
            top      : -10px;
            left     : 40%;
            content  : url("${resource(dir:'images',file:'pin.png')}");
            z-index  : 2;
            display  : block;
            width    : 16px;
            height   : 16px;
        }

        .padre {
            background   : #BCCCDC;
            border-color : #2C5E8F;
            width        : 290px;
        }

        .nota.padre .contenido {
            /*height : 200px;*/
            /*max-height : 285px;*/
        }

        .padre h4 {
            font-size     : 15px;
            margin-top    : 0;
            margin-bottom : 5px;
            height        : 40px;
            overflow      : auto;
            /*cursor        : move;*/
        }

        .btn-editar {
            position : absolute;
            right    : 10px;
            top      : 32px;
        }

        .membrete {
            cursor                : pointer;
            margin-top            : 2px;
            margin-left           : 15px !important;
            font-size             : 15px;
            padding               : 3px 8px;

            -webkit-border-radius : 5px;
            -moz-border-radius    : 5px;
            border-radius         : 5px;
        }

        .cambiado {
            background : #A0BF99;
        }
        </style>
    </head>

    <body>

        <g:if test="${tramite.nota && tramite.nota.trim() != ''}">
            <div class="nota ui-corner-all">
                <div class="contenido">
                    ${tramite.nota}
                </div>
            </div>
        </g:if>
        <g:if test="${tramite.padre}">
            <g:if test="${tramite.padre.personaPuedeLeer(session.usuario)}">
                <div class="nota padre ui-corner-all" id="divInfo" style="height: 200px;">
                    <h4 style="height: 100%" class="text-info">${tramite.padre.codigo} - ${tramite.padre.asunto}</h4>

                    <div class="contenido" id="divInfoContenido">
                        %{--<g:each in="${0..15}" var="i">--}%
                        <util:renderHTML html="${tramite.padre.texto}"/>
                        %{--</g:each>--}%
                    </div>
                </div>
            </g:if>
        </g:if>

        <div class="hoja">

            <div class="btn-toolbar toolbar">
                <div class="btn-group">
                    <a href="#" class="btn btn-sm btn-success btnSave">
                        <i class="fa fa-save"></i> Guardar texto
                    </a>
                </div>
                <div class="btn-group">
                    <a href="#" class="btn btn-sm btn-primary btnPrint">
                        <i class="fa fa-file"></i> ver PDF
                    </a>
                    %{--<g:if test="${tramite.tipoDocumento.codigo == 'DEX'}">--}%
                    %{--<g:link action="saveDEX" class="btn btn-sm btn-info btnTerminar" title="Guardar, enviar y recibir">--}%
                    %{--<i class="fa fa-check"></i> Guardar y Terminar--}%
                    %{--</g:link>--}%
                    %{--</g:if>--}%
                </div>

                <div class="btn-group">
                    <g:if test="${tramite.deDepartamento && !esEditor}">
                    %{--<g:link controller="tramite3" action="bandejaEntradaDpto" class="leave btn btn-sm btn-azul btnRegresar" style="margin-left: 20px;">--}%
                    %{--<i class="fa fa-list-ul"></i> Bandeja de Entrada--}%
                    %{--</g:link>--}%
                        <g:link controller="tramite2" action="bandejaSalidaDep" class="btnBandeja leave btn btn-sm btn-azul btnRegresar">
                        %{--<i class="fa fa-list-ul"></i> Bandeja de Salida--}%
                            <i class="fa fa-list-ul"></i> Guardar texto y Salir
                        </g:link>
                    </g:if>
                    <g:else>
                    %{--<g:link action="bandejaEntrada" class="leave btn btn-sm btn-azul btnRegresar" style="margin-left: 20px;">--}%
                    %{--<i class="fa fa-list-ul"></i> Bandeja de Entrada--}%
                    %{--</g:link>--}%
                        <g:link controller="tramite2" action="bandejaSalida" class="btnBandeja leave btn btn-sm btn-azul btnRegresar">
                        %{--<i class="fa fa-list-ul"></i> Bandeja de Salida--}%
                            <i class="fa fa-list-ul"></i> Guardar texto y Salir
                        </g:link>
                    </g:else>
                    <g:if test="${!esEditor}">
                        <g:if test="${tramite.deDepartamento}">
                            <g:link controller="tramite2" action="crearTramiteDep" id="${tramite.id}" params="[esRespuesta: tramite.esRespuesta, esRespuestaNueva: tramite.esRespuestaNueva]"
                                    class="leave btn-editar btn btn-sm btn-azul btnRegresar" title="Editar encabezado">
                                <i class="fa fa-pencil"></i>
                            </g:link>
                        </g:if>
                        <g:else>
                            <g:link action="crearTramite" id="${tramite.id}" params="[esRespuesta: tramite.esRespuesta, esRespuestaNueva: tramite.esRespuestaNueva]"
                                    class="leave btn-editar btn btn-sm btn-azul btnRegresar" title="Editar encabezado">
                                <i class="fa fa-pencil"></i>
                            </g:link>
                        </g:else>
                    </g:if>
                </div>

                <div class="btn-group membrete" data-con="${tramite.conMembrete ?: '0'}">
                    <g:if test="${tramite.conMembrete == '1'}">
                        <i class="fa fa-check-square-o"></i> Membrete
                    </g:if>
                    <g:else>
                        <i class="fa fa-square-o"></i> Membrete
                    </g:else>
                </div>

            </div>
            <elm:headerTramite tramite="${tramite}"/>

            <textarea id="editorTramite" class="editor" rows="100" cols="80">${tramite.texto}</textarea>
        </div>



        <script type="text/javascript">

            /* deshabilita navegación --inicailiza */
            //            $(document).ready(function(){
            //                initControls();
            //            });
            //
            //            /* deshabilita navegación hacia atras */
            //            function initControls(){
            ////                console.log("hola");
            ////                window.location.hash = "red";
            ////                window.location.hash = "Red" //chrome
            ////                window.onhashchange = function(){window.location.hash="Red";}
            //
            //
            //                window.location.hash="no-back-button";
            //                window.location.hash="Again-No-back-button" //chrome
            //                window.onhashchange=function(){window.location.hash="no-back-button";}
            //            }
            //
            //            /* deshabilita navegación por teclas */
            //            $(document).keyup(function(e) {
            //                switch(e.keyCode) {
            //                    case 37 : window.location = $('.prev').attr('href'); break;
            //                    case 39 : window.location = $('.next').attr('href'); break;
            //                }
            //            });
            //
            //            /* deshabilita navegación hacia adelante */
            //            $('.disableNav').bind('focus', function (event) {
            //                navEnabled = false;
            //            }).bind('blur', function (event) {
            //                navEnabled = true;
            //            });

            function arreglarTexto(texto) {
                texto = $.trim(texto);
                texto = texto.replace(/(?:\&)/g, "&amp;");
                texto = texto.replace(/(?:<)/g, "&lt;");
                texto = texto.replace(/(?:>)/g, "&gt;");
                texto = texto.replace(/(?:\r\n|\r|\n)/g, '');
                return texto;
            }

            var textoInicial = "${tramite.texto}";

//            window.onbeforeunload = function (e) {
//                textoInicial = textoInicial.replace(/(?:\r\n|\r|\n)/g, '');
//                var textoActual = arreglarTexto($("#editorTramite").val());
//                var esIgual = textoInicial == textoActual;
//                if (esIgual && textoActual != "") {
//                    return null;
//                } else {
//                    return "Alerta";
//                }
//
////                var textoActual = $("#editorTramite").val();
////                var textoActual2 = textoActual.replace("\\n", "");
////                var textoActual3 = textoActual.strReplaceAll("\\n", "");
////                var textoActual4 = textoActual.replace(/(?:\r\n|\r|\n)/g, '');
////                console.log(textoInicial);
////                console.log(textoActual);
////                console.log(textoActual2);
////                console.log(textoActual3);
////                console.log(textoActual4);
////                console.log(textoInicial == textoActual);
////                console.log(textoInicial == textoActual2);
////                console.log(textoInicial == textoActual3);
////                console.log(textoInicial == textoActual4);
////                return "ASDFASDFASDFASD";
////                if (esIgual) {
//////                    return null;
////                    return "Alert";
////                } else {
////                    return "Alerta";
////                }
//            };

            function doSave(url) {
                openLoader("Guardando");

                $.ajax({
                    type     : "POST",
                    url      : '${createLink(controller:"tramite", action: "saveTramite")}',
                    data     : {
                        id            : "${tramite.id}",
                        editorTramite : $("#editorTramite").val(),
                        para          : $("#para").val(),
                        asunto        : $("#asunto").val()
                    },
                    success  : function (msg) {
                        closeLoader();
                        var parts = msg.split("_");
                        if (parts[0] == "OK") {
                            textoInicial = arreglarTexto($("#editorTramite").val());
                        }
                        log(parts[1], parts[0] == "NO" ? "error" : "success");
                        if (url) {
                            location.href = url;
                        }
                    },
                    complete : function () {
                        resetTimer();
                    }
                });
            }

            $(function () {

//                var $also = $("#divInfoContenido");
//                var $div = $("#divInfo");
//                console.log($also.width(), $div.width(), $also.height(), $div.height(), "dw=" + ($div.width() - $also.width()), "dh=" + ($div.height() - $also.height()));

//                $(".leave").click(function () {
//                    validaTexto(textoInicial, $(this).attr("href"));
//                    return false;
//                });

                $(".membrete").click(function () {
                    var esto = $(this);
                    if (esto.data("con") == '0') {
                        esto.data("con", '1').html('<i class="fa fa-check-square-o"></i> Membrete');
                    } else {
                        esto.data("con", '0').html('<i class="fa fa-square-o"></i> Membrete');
                    }
                    %{--if (esto.data("con") != "${tramite.conMembrete ?: '0'}") {--}%
                    %{--esto.addClass("cambiado");--}%
                    %{--} else {--}%
                    %{--esto.removeClass("cambiado");--}%
                    %{--}--}%
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(controller:'tramite',action:'cambiarMembrete')}",
                        data    : {
                            id       : "${tramite.id}",
                            membrete : esto.data("con")
                        },
                        success : function (msg) {
                            var parts = msg.split("*");
                            log(parts[1], parts[0] == "OK" ? 'success' : "error");
                        }
                    });
                });

                $(".header-tramite").append($(".btn-editar"));

                $("#divInfo").resizable({
                    maxWidth  : 450,
                    maxHeight : 500,
                    minWidth  : 200,
                    minHeight : 100,
                    resize    : function (event, ui) {
                        var $div = ui.element;
                        var $also = ui.element.find("#divInfoContenido");
                        var divH = ui.size.height;
                        var divW = ui.size.width;

                        var nw = divW - 20;
                        var nh = divH - 60;

                        $also.css({
                            width     : nw,
                            height    : nh,
                            maxHeight : nh
                        });
                    }/*,
                     stop    : function (event, ui) {
                     var $div = ui.element;
                     var $also = ui.element.find("#divInfoContenido");

                     var masW = ui.size.width - ui.originalSize.width;
                     var masH = ui.size.height - ui.originalSize.height;

                     var alsoW = $also.width();
                     var alsoH = $also.height();

                     var newW = alsoW + masW;
                     var newH = alsoH + masH;

                     $also.width(newW);
                     $also.height(newH);

                     console.log(masW + "+" + alsoW + "=" + newW, masH + "+" + alsoH + "=" + newH);
                     }*/
                });
                /*.draggable({
                    handle : ".text-info"
                });*/

                $("#btnInfoPara").click(function () {
                    var para = $("#para").val();
                    var paraExt = $("#paraExt").val();
                    var id;
                    var url = "";
                    if (para) {
                        if (parseInt(para) > 0) {
                            url = "${createLink(controller: 'persona', action: 'show_ajax')}";
                            id = para;
                        } else {
                            url = "${createLink(controller: 'departamento', action: 'show_ajax')}";
                            id = parseInt(para) * -1;
                        }
                    }
                    if (paraExt) {
                        url = "${createLink(controller: 'origenTramite', action: 'show_ajax')}";
                        id = paraExt;
                    }
                    $.ajax({
                        type    : "POST",
                        url     : url,
                        data    : {
                            id : id
                        },
                        success : function (msg) {
                            bootbox.dialog({
                                title   : "Información",
                                message : msg,
                                buttons : {
                                    aceptar : {
                                        label     : "Aceptar",
                                        className : "btn-primary",
                                        callback  : function () {
                                        }
                                    }
                                }
                            });
                        }
                    });
                    return false;
                });

                $(".btnTerminar").click(function () {
                    bootbox.confirm("Está seguro de querer terminar este trámite? <br/>Esto enviará y recibirá automáticamente el trámite y no podrá ser editado.", function (res) {
                        if (res) {
                            openLoader("Guardando");
                            $.ajax({
                                type    : "POST",
                                url     : '${createLink(action: "saveDEX")}',
                                data    : {
                                    id            : "${tramite.id}",
                                    editorTramite : $("#editorTramite").val()
                                },
                                success : function (msg) {
                                    closeLoader();
                                    var parts = msg.split("*");
                                    if (parts[0] == "OK") {
                                        textoInicial = $("#editorTramite").val();
                                        location.href = parts[1];
                                    } else {
                                        bootbox.alert(parts[1]);
                                    }
                                }
                            });
                        }
                    });
                    return false;
                });

                $(".btnSave").click(function () {
                    doSave();
                    return false;
                });

                $(".btnBandeja").click(function () {
                    var url = $(this).attr("href");
                    doSave(url);
                    return false;
                });

                function imprimir() {
                    openLoader("Generando PDF");
                    var url = '${createLink(controller:"tramiteExport", action: "crearPdf")}';
                    var data = {
                        id            : "${tramite.id}",
                        editorTramite : $("#editorTramite").val(),
                        para          : $("#para").val(),
                        asunto        : $("#asunto").val(),
                        type          : "download",
                        enviar        : 1,
                        timestamp     : new Date().getTime()
                    };
                    $.ajax({
                        type     : "POST",
                        url      : url,
                        data     : data,
                        success  : function (msg) {
                            var parts = msg.split("*");
                            if (parts[0] == "OK") {
                                textoInicial = arreglarTexto($("#editorTramite").val());
                                closeLoader();
                                window.open("${resource(dir:'tramites')}/" + parts[1]);
                            }
                        },
                        complete : function () {
                            resetTimer();
                        }
                    });
                }

                $(".btnPrint").click(function () {
                    imprimir();
//                    bootbox.dialog({
//                        title   : "Alerta",
//                        message : "¿Desea generar el PDF con membrete?",
//                        buttons : {
//                            cancelar : {
//                                label     : "Cancelar",
//                                className : "btn-primary",
//                                callback  : function () {
//                                }
//                            },
//                            si       : {
//                                label     : "Con membrete",
//                                className : "btn-default",
//                                callback  : function () {
//                                    imprimir(1);
//                                }
//                            },
//                            no       : {
//                                label     : "Sin membrete",
//                                className : "btn-default",
//                                callback  : function () {
//                                    imprimir(0);
//                                }
//                            }
//                        }
//                    });
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
//                            [ 'Source', 'ServerSave', *//*'NewPage', *//*'CreatePdf',*/ /*'-',*/ /*'Scayt'*/],
//                            [ 'Source'],

                            ['Scayt', '-', 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo'],
                            ['Find', 'Replace', '-', 'SelectAll'],
                            ['Table', 'HorizontalRule', 'PageBreak'],
                            ['Image'/*, 'Timestamp'*/, '-', 'TextColor', 'BGColor', '-', 'About'],
                            '/',
                            ['Bold', 'Italic', 'Underline', /*'Strike', */'Subscript', 'Superscript'/*, '-', 'RemoveFormat'*/],
                            ['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'FontSize']
//                            [/* 'Font', 'FontSize'*/ /*, '-', 'TextColor', 'BGColor'*/]
//                            ['About' ]
                        ]
                    });
                }

                CKEDITOR.on('instanceReady', function (ev) {
                    // Prevent drag-and-drop.
                    ev.editor.document.on('drop', function (ev) {
                        ev.data.preventDefault(true);
                    });
                });

            });
        </script>
    </body>
</html>


