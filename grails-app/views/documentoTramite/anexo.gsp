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
            margin   : 0;
            position : absolute;
            top      : 0;
            left     : 0;
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
            padding    : 0;
            margin-top : 10px;
        }

        .progress-svt {
            width            : 0;
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
        <elm:headerTramite tramite="${tramite}" extraTitulo="- Cargar anexos"/>

        <g:if test="${tramite.tipoDocumento.codigo != 'DEX'}">
            <g:if test="${tramite.deDepartamento}">
                <g:link style="position: relative;height: 40px;margin-top: 10px" controller="tramite2" action="crearTramiteDep" params="[esRespuestaNueva: tramite.esRespuestaNueva]"
                        id="${tramite.id}" class=" btn-editar btn  btn-azul btnRegresar" title="Editar encabezado">
                    Editar encabezado
                </g:link>
            </g:if>
            <g:else>
                <g:link style="position: relative;height: 40px;margin-top: 10px" controller="tramite" action="crearTramite" params="[esRespuestaNueva: tramite.esRespuestaNueva]"
                        id="${tramite.id}" class="  btn-editar btn btn-azul btnRegresar" title="Editar encabezado">
                    Editar encabezado
                </g:link>
            </g:else>

            <g:link style="position: relative;height: 40px;margin-top: 10px" controller="tramite" action="redactar" id="${tramite.id}" class="btn btn-redactar btn-azul">
                Redactar
            </g:link>
        </g:if>
        <g:else>
            <g:if test="${tramite.deDepartamento}">
                <g:link style="position: relative;height: 40px;margin-top: 10px" controller="tramite3" action="bandejaEntradaDpto" class="btn btn-azul">
                    Bandeja de entrada
                </g:link>
            </g:if>
            <g:else>
                <g:link style="position: relative;height: 40px;margin-top: 10px" controller="tramite" action="bandejaEntrada" class="btn btn-azul">
                    Redactar
                </g:link>
            </g:else>
        </g:else>
        %{--<g:link controller="tramite" action="redactar" id="${tramite.id}" class="btn btn-azul" style="margin-top: 8px">--}%
        %{--Editar encabezado--}%
        %{--</g:link>--}%
        <span class="btn btn-success fileinput-button" style="position: relative;height: 40px;margin-top: 10px">
            <i class="glyphicon glyphicon-plus"></i>
            <span>Seleccionar archivos</span>
            <input type="file" name="file" id="file" class="file" multiple accept=".doc, .docx, .pdf, .odt, .xls, .xlsx, .jpeg, .jpg, .png">
        </span>
        %{--<span class="btn btn-success fileinput-button" id="adj-tramites" style="position: relative;height: 40px;margin-top: 10px">--}%
        %{--<i class="glyphicon glyphicon-plus"></i>--}%
        %{--<i class="fa fa-folder-open-o"></i>--}%
        %{--<span>Adjuntar Trámites</span>--}%
        %{--</span>--}%
        <span class="btn btn-default fileinput-button" id="reset-files" style="position: relative;height: 40px;margin-top: 10px">
            <i class="glyphicon glyphicon-refresh"></i>
            <span>Limpiar</span>
        </span>

        <div class="alert alert-info" style="margin-top: 10px;">
            <i class="fa fa-info-circle fa-2x"></i>
            Se recuerda que puede cargar archivos de <strong>hasta 5 mb</strong> de tipo <strong>.doc, .docx, .pdf, .odt, .xls, .xlsx, .jpeg, .jpg, .png</strong>
        </div>

        <div style="margin-top:15px;margin-bottom: 20px" class="vertical-container" id="files">
            <p class="css-vertical-text" id="titulo-arch" style="display: none">Archivos</p>

            <div class="linea" id="linea-arch" style="display: none"></div>
        </div>
        %{--<div class="progress-bar-svt ui-corner-all" id="p-b"><div class="progress-svt background-image" id="p" style="width: 50%">50%</div></div>--}%
        <div id="anexos">

        </div>

        <div class="modal fade " id="dialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title">Adjuntar Trámites</h4>
                    </div>

                    <div class="modal-body" id="dialog-body">

                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cerrar</button>
                        <a href="#" id="adj-tramite" class="btn btn-primary">Adjuntar seleccionados</a>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div>

        <script type="text/javascript">
            var okContents = {
                'image/png'  : "png",
                'image/jpeg' : "jpeg",
                'image/jpg'  : "jpg",

                'application/pdf'        : 'pdf',
                'application/download'   : 'pdf',
                'application/vnd.ms-pdf' : 'pdf',

                'application/excel'                                                 : 'xls',
                'application/vnd.ms-excel'                                          : 'xls',
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
            function reset() {
                $("#files").find(".fileContainer").remove()
            }
            function createContainer() {

                var file = document.getElementById("file");

                var next = $("#files").find(".fileContainer").size();
                if (isNaN(next))
                    next = 1;
                else
                    next++;
                var ar = file.files[next - 1];
//        console.log("ar",ar,file,next)
//        console.log(file.files[0])
                var div = $('<div class="fileContainer ui-corner-all d-' + next + '">');
                var row1 = $("<div class='row resumen'>");
//                var row2 = $("<div class='row claves'>");
                var row3 = $("<div class='row botones'  style='text-align: right'>");
                var row4 = $("<div class='row'>");
                row1.append("<div class='col-md-1 etiqueta'>Descripción</div>");
                row1.append("<div class='col-md-5'><textarea maxlength='254' style='resize: none' class='form-control " + next + "' required id='descripcion' name='descripcion' cols='5' rows='5'></textarea></div>");
                row1.append(" <div class='col-md-1 etiqueta'>Palabras clave</div>");
                row1.append("<div class='col-md-5'><textarea maxlength='63' style='resize: none;' class='form-control  " + next + "' required id='clave' name='clave' cols='5' rows='5'></textarea> </div>");
//                row2.append(" <div class='col-md-1 etiqueta'>Palabras clave</div>");
//                row2.append(" <div class='col-md-11'><input type='text' class='form-control " + next + "' id='clave' name='clave'/></div>");
                row3.append(" <a href='#' class='btn btn-azul subir' style='margin-right: 15px' clase='" + next + "'><i class='fa fa-upload'></i> Subir Archivo</a>");
                div.append("<div class='row' style='margin-top: 0px'><div class='titulo-archivo col-md-10'><span style='color: #327BBA'>Archivo:</span> " + ar.name + "</div></div>");
                div.append(row1);
//                div.append(row2);
                div.append(row3);
                $("#files").append(div);
                if ($("#files").height() * 1 > 120) {
                    $("#titulo-arch").show();
                    $("#linea-arch").show();
                } else {
                    $("#titulo-arch").hide();
                    $("#linea-arch").hide();
                }
            }
            function boundBotones() {
                $(".subir").unbind("click");
                $(".subir").bind("click", function () {
                    error = false;
                    $("." + $(this).attr("clase")).each(function () {
                        if ($(this).val().trim() == "") {
                            error = true;
                        }
                    });
                    if (error) {
                        bootbox.alert("llene todos los campos")
                    } else {
                        /*Aqui subir*/
                        upload($(this).attr("clase") * 1 - 1);
                    }
                });
            }
            var request = [];
            var tam = 0;
            function upload(indice) {
                var tramite = "${tramite.id}";
                var file = document.getElementById("file");
                /* Create a FormData instance */
                var formData = new FormData();
                tam = file.files[indice];
                var type = tam.type;
                if (okContents[type]) {
                    tam = tam["size"];
                    var kb = tam / 1000;
                    var mb = kb / 1000;
                    if (mb <= 5) {
                        formData.append("file", file.files[indice]);
                        formData.append("id", tramite);
                        $("." + (indice + 1)).each(function () {
                            //            console.log($(this))
                            formData.append($(this).attr("name"), $(this).val());
                        });
                        var rs = request.length;
                        //        console.log("rs ",rs)
                        $(".d-" + (indice + 1)).addClass("subiendo").addClass("rs-" + rs);
                        $(".rs-" + rs).find(".resumen").remove();
                        $(".rs-" + rs).find(".botones").remove();
                        $(".rs-" + rs).find(".claves").remove();
                        $(".rs-" + rs).append('<div class="progress-bar-svt ui-corner-all" id="p-b"><div class="progress-svt background-image" id="p-' + rs + '"></div></div>').css({
                            height     : 100,
                            fontWeight : "bold"
                        });
                        request[rs] = new XMLHttpRequest();
                        request[rs].open("POST", "${g.createLink(controller: 'documentoTramite',action: 'uploadSvt')}");
                        //        console.log("request ",request[rs],tam)
                        //        console.log( $(".rs-"+rs),"rs-"+rs,request[rs]);

                        request[rs].upload.onprogress = function (ev) {
                            //            console.log("buscando?","#p-"+rs, $("#p-"+rs))
                            var loaded = ev.loaded;
                            var width = (loaded * 100 / tam);
                            if (width > 100)
                                width = 100;
                            //        console.log(width)
                            $("#p-" + rs).css({width : parseInt(width) + "%"});
                            if ($("#p-" + rs).width() > 50) {
                                $("#p-" + rs).html("" + parseInt(width) + "%");
                            }
                        };
                        request[rs].send(formData);
                        request[rs].onreadystatechange = function () {
                            //            console.log("rs??",rs)
                            if (request[rs].readyState == 4 && request[rs].status == 200) {
                                if ($("#files").height() * 1 > 120) {
                                    $("#titulo-arch").show();
                                    $("#linea-arch").show();
                                } else {
                                    $("#titulo-arch").hide();
                                    $("#linea-arch").hide();
                                }
                                $(".rs-" + rs).html("<i class='fa fa-check' style='color:#327BBA;margin-right: 10px'></i> " + $(".rs-" + rs).find(".titulo-archivo").html() + " subido exitosamente").css({
                                    height     : 50,
                                    fontWeight : "bold"
                                }).removeClass("subiendo")
                                cargaDocs();

                            }
                        };
                    } else {
                        var $div = $(".fileContainer.d-" + (indice + 1));
                        $div.addClass("bg-danger").addClass("text-danger");
                        var $p = $("<div>").addClass("alert divError").html("No puede subir archivos de más de 5 megabytes");
                        $div.prepend($p);
                        return false;
                    }
                } else {
                    var $div = $(".fileContainer.d-" + (indice + 1));
                    $div.addClass("bg-danger").addClass("text-danger");
                    var $p = $("<div>").addClass("alert divError").html("No puede subir archivos de tipo <b>" + type + "</b>");
                    $div.prepend($p);
                    return false;
                }
            }

            var archivos = [];
            function btnCerrar($panel, $footer, isError) {
                var clase = isError ? "danger" : "success";
                var icon = isError ? "times" : "check";
                var $btnCerrar = $("<a href='#' class='btn btn-" + clase + "'/>");
                $btnCerrar.append("<i class='fa fa-" + icon + "'></i>");
                $btnCerrar.append("Cerrar");
                $footer.html($btnCerrar);
                $btnCerrar.click(function () {
                    $panel.hide({
                        effect   : "fold",
                        duration : 800,
                        complete : function () {
                            $panel.remove();
                        }
                    });
                });
            }
            function cargaDocs() {
                $("#anexos").html("");
//        openLoader("Cargando")
                $.ajax({
                    type    : "POST",
                    url     : "${g.createLink(controller: 'documentoTramite',action:'cargaDocs')}",
                    data    : "id=${tramite.id}",
                    async   : false,
                    success : function (msg) {
                        $("#anexos").html(msg);
//                closeLoader()
                    }
                });
            }

            function revisarAnexosDex(mensaje, url) {
                if ("${tramite.tipoDocumento.codigo}" == "DEX") {
                    $.ajax({
                        type    : "POST",
                        url     : "${g.createLink(controller: 'documentoTramite',action:'validarAnexosDEX')}",
                        data    : {
                            id :${tramite.id}
                        },
                        async   : false,
                        success : function (msg) {
//                            console.log(msg, msg == "true");
                            if (msg == "true") {
                                location.href = url;
                            } else {
                                bootbox.alert(mensaje);
                            }
                        }
                    });
                } else {
                    location.href = url;
                }
            }

            $(function () {

                $(".btn-editar").click(function () {
                    revisarAnexosDex("Debe cargar al menos un anexo antes de editar el encabezado.", $(this).attr("href"));
                    return false;
                });
                $(".btn-redactar").click(function () {
                    revisarAnexosDex("Debe cargar al menos un anexo antes de redactar el documento.", $(this).attr("href"));
                    return false;
                });

                $("#adj-tramites").click(function () {
                    openLoader();
                    $.ajax({
                        type    : "POST",
                        url     : "${g.createLink(controller: 'documentoTramite',action:'cargaTramites')}",
                        data    : "",
                        async   : false,
                        success : function (msg) {
                            $("#dialog-body").html(msg);
                            $("#dialog").modal("show");
                            closeLoader();
                        }
                    });
                });

                $("#adj-tramite").click(function () {
                    var data = "ids=";
                    $(".chk").each(function () {
                        if ($(this).prop("checked") == true) {
                            data += $(this).attr("iden") + ";";
                        }
                    });
                    if (data == "ids=") {
                        bootbox.alert("Seleccione al menos un tramite para adjuntar")
                    } else {
                        openLoader();
                        data += "&tramite=${tramite.id}";
                        $.ajax({
                            type    : "POST",
                            url     : "${g.createLink(controller: 'documentoTramite',action:'adjuntarTramites')}",
                            data    : data,
                            async   : false,
                            success : function (msg) {
                                cargaDocs();
                                $("#dialog").modal("hide");
                                closeLoader();
                            }
                        });
                    }
                });

                cargaDocs();
                $("#reset-files").click(function () {
                    reset();
                    $("#file").val("");
                    $("#titulo-arch").hide();
                    $("#linea-arch").hide()
                });

                $("#file").change(function () {
                    reset();
                    archivos = $(this)[0].files;
                    var length = archivos.length;
                    for (i = 0; i < length; i++) {
                        createContainer();
                    }
                    boundBotones();
                });
            });
        </script>
    </body>
</html>