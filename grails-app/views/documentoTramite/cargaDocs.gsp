<g:if test="${docs.size() > 0}">
    <script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>

    <div style="margin-top:15px;margin-bottom: 20px" class="vertical-container">
        <p class="css-vertical-text">Anexos</p>

        <div class="linea"></div>
        <g:each in="${docs}" var="anexo">
            <g:if test="${anexo.anexo}">
                <div class="fileContainer ui-corner-all">
                    <div class='row' style='margin-top: 0px'>
                        <div class='titulo-archivo col-md-11'>
                            <span style='color: #327BBA'>Tramite:</span>
                            ${anexo.anexo.codigo} - ${anexo.anexo.asunto}
                            <a href='#' class='btn btn-success verDetalle' style='margin-right: 15px' title="Ver" tramite="${anexo.anexoId}" iden="${anexo.id}">
                                <i class="fa fa-search"></i>
                            </a>
                        </div>

                        <div class="col-md-1">
                            <a href='#' class='btn btn-danger borrar' style='margin-right: 15px' title="Borrar Anexo" iden="${anexo.id}">
                                <i class="fa fa-trash-o"></i>
                            </a>
                        </div>
                    </div>
                </div>
            </g:if>
            <g:else>
                <div class="fileContainer ui-corner-all" style="height: 160px">
                    <div class='row' style='margin-top: 0px'>
                        <div class='titulo-archivo col-md-11'>
                            <span style='color: #327BBA'>Archivo:</span>
                            ${anexo.path}
                            <a href='#' class='btn btn-success bajar' style='margin-right: 15px' title="Descargar Archivo" iden="${anexo.id}">
                                <i class="fa fa-download"></i>
                            </a>
                        </div>

                        <div class="col-md-1">
                            <g:if test="${editable}">
                                <a href='#' class='btn btn-danger borrar' style='margin-right: 15px' title="Borrar Anexo" iden="${anexo.id}">
                                    <i class="fa fa-trash-o"></i>
                                </a>
                            </g:if>
                        </div>
                    </div>

                    <div class='row'>
                        <div class='col-md-1 etiqueta'>Palabras clave:</div>

                        <div class='col-md-5 ' title="palabras clave:">
                            ${anexo.clave}
                        </div>

                        <div class='col-md-1 etiqueta'>Descripción:</div>

                        <div class='col-md-5'>
                            ${anexo.descripcion}
                        </div>
                    </div>

                </div>
            </g:else>
        </g:each>
    </div>

    <div class="modal fade " id="dialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title">Detalles</h4>
                </div>

                <div class="modal-body" id="dialog-body" style="padding: 15px">

                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cerrar</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div>

    <script type="text/javascript">
        $(".verDetalle").click(function () {
            var id = $(this).attr("tramite");
            $.ajax({
                type    : 'POST',
                url     : '${createLink(controller: 'tramite3', action: 'detalles')}',
                data    : {
                    id : id
                },
                success : function (msg) {
                    $("#dialog-body").html(msg)
                }
            });
            $("#dialog").modal("show")
            return false;
        });
        $(".borrar").click(function () {
            var id = $(this).attr("iden")
            bootbox.confirm("Está seguro?", function (result) {
                if (result) {
//                    openLoader("Borrando")
                    $.ajax({
                        type    : "POST",
                        url     : "${g.createLink(controller: 'documentoTramite',action: 'borrarDoc')}",
                        data    : "id=" + id,
                        success : function (msg) {
//                            closeLoader()
                            if (msg == "ok") {
                                cargaDocs();
//                                closeLoader()
                            } else {
                                var mensaje = msg.split("_")
                                mensaje = mensaje[1]
                                bootbox.alert(mensaje)
                            }
                        }
                    });
                }

            })
        });
        $(".bajar").click(function () {
            var id = $(this).attr("iden")
            openLoader()
            $.ajax({
                type    : "POST",
                url     : "${g.createLink(controller: 'documentoTramite',action: 'generateKey')}",
                data    : "id=" + id,
                success : function (msg) {
                    closeLoader()
                    if (msg == "ok") {
                        location.href = "${g.createLink(action: 'descargarDoc')}/" + id
                    } else {
                        bootbox.confirm("El archivo solicitado no se encuentra en el servidor. Desea borrar el anexo?", function (result) {
                            if (result) {
//                    openLoader("Borrando")
                                $.ajax({
                                    type    : "POST",
                                    url     : "${g.createLink(controller: 'documentoTramite',action: 'borrarDocNoFile')}",
                                    data    : "id=" + id,
                                    success : function (msg) {
                                        if (msg == "ok") {
                                            cargaDocs();
                                        } else {
                                            bootbox.alert("No se pudo eliminar el archivo anexo")
                                        }
                                    }
                                });
                            }

                        });
                    }
                }
            });
        })

    </script>
</g:if>