<g:if test="${docs.size()>0}">
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
                            <a href='${g.createLink(controller: "tramite3",action: "seguimientoTramite",id: anexo.anexo.id)}' class='btn btn-success '   style='margin-right: 15px' title="Ver" iden="${anexo.id}">
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
                        <div class='col-md-1 etiqueta'>Resumen:</div>
                        <div class='col-md-5 ' title="Resumen: ${anexo.resumen}">
                            ${anexo.resumenCorto}
                        </div>
                        <div class='col-md-1 etiqueta'>Descripción:</div>
                        <div class='col-md-5'>
                            ${anexo.descripcion}
                        </div>
                    </div>
                    <div class='row' style="margin-bottom: 10px">
                        <div class='col-md-1 etiqueta'>Palabras clave:</div>
                        <div class='col-md-11'>
                            ${anexo.clave}
                        </div>
                    </div>
                </div>
            </g:else>
        </g:each>
    </div>
    <script type="text/javascript">
        $(".borrar").click(function(){
            var id = $(this).attr("iden")
            bootbox.confirm("Esta seguro?",function(result){
                if(result){
//                    openLoader("Borrando")
                    $.ajax({
                        type    : "POST",
                        url     : "${g.createLink(controller: 'documentoTramite',action: 'borrarDoc')}",
                        data    : "id="+id,
                        success : function (msg) {
//                            closeLoader()
                            if(msg=="ok"){
                                cargaDocs();
//                                closeLoader()
                            }else{
                                var mensaje = msg.split("_")
                                mensaje = mensaje[1]
                                bootbox.alert(mensaje)
                            }
                        }
                    });
                }

            })
        });
        $(".bajar").click(function(){
            var id = $(this).attr("iden")
            openLoader()
            $.ajax({
                type    : "POST",
                url     : "${g.createLink(controller: 'documentoTramite',action: 'generateKey')}",
                data    : "id="+id,
                success : function (msg) {
                    closeLoader()
                    if(msg=="ok"){
                        location.href="${g.createLink(action: 'descargarDoc')}/"+id
                    }
                }
            });
        })

    </script>
</g:if>