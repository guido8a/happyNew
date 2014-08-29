<g:if test="${permisos.size() > 0}">
    <h4>Historial</h4>

    <div class="">
        <div class="container-colsPer">
            <div class="header-columnas">
                <div id="all"></div>
                <table class=" table table-bordered table-condensed">
                    <thead>
                        <tr>
                            <th class="col200">Permiso</th>
                            <th class="col100">Desde</th>
                            <th class="col100">Hasta</th>
                            <th class="col200">Observaciones</th>
                            <th class="col200">Asignado por</th>
                        </tr>
                    </thead>
                </table>
            </div>
        </div>

        <div class="container-celdasPerm">
            <div id="celdas">
                <table class=" table table-bordered table-condensed" id="tablaPerm">
                    <tbody>
                        <g:each in="${permisos}" var="permiso">
                            <tr data-id="${permiso.id}" class="rowPerm ${permiso.estado == 'A' ? 'success' : permiso.estado == 'F' ? 'active' : 'danger'}">
                                <td class="col200">${permiso.permisoTramite.descripcion}</td>
                                <td class="col100">${permiso.fechaInicio.format("dd-MM-yyyy")}</td>
                                <td class="col100">${permiso.fechaFin ? permiso.fechaFin.format("dd-MM-yyyy") : ""}</td>
                                %{--<td class="col200">${permiso.observaciones}</td>--}%
                                <td class="col200">${permiso.asignadoPor.nombre} ${permiso.asignadoPor.apellido}</td>
                            </tr>
                        </g:each>
                    </tbody>
                </table>
            </div>
        </div>

    </div>

</g:if>

<script type="text/javascript">
    $(function () {

        $(".rowPerm").contextMenu({
            items  : {
                header   : {
                    label  : "Acciones",
                    header : true
                },
                terminar : {
                    label  : "Terminar",
                    icon   : "fa fa-stop",
                    action : function ($element) {
                        var id = $element.data("id");
                        bootbox.confirm("<i class='fa fa-warning fa-3x pull-left text-warning text-shadow'></i><p>Esto cambiará la fecha final del permiso a la fecha actual. ¿Desea continuar?</p>", function (res) {
                            if (res) {
                                $.ajax({
                                    type    : "POST",
                                    url     : "${createLink(action: 'terminarPermiso_ajax')}",
                                    data    : {
                                        id : id
                                    },
                                    success : function (msg) {
                                        var parts = msg.split("_");
                                        log(parts[1], parts[0] == "OK" ? "success" : parts[0] == "NO" ? "error" : "info");
                                        loadPermisos();
                                    }
                                });
                            }
                        });
                    }
                },
                eliminar : {
                    label  : "Eliminar",
                    icon   : "fa fa-trash-o",
                    action : function ($element) {
                        var id = $element.data("id");
                        bootbox.confirm("<i class='fa fa-trash-o fa-3x pull-left text-danger text-shadow'></i><p>Esto eliminará completamente el permiso ¿Desea continuar?</p>", function (res) {
                            if (res) {
                                $.ajax({
                                    type    : "POST",
                                    url     : "${createLink(action: 'eliminarPermiso_ajax')}",
                                    data    : {
                                        id : id
                                    },
                                    success : function (msg) {
                                        var parts = msg.split("_");
                                        log(parts[1], parts[0] == "OK" ? "success" : parts[0] == "NO" ? "error" : "info");
                                        loadPermisos();
                                    }
                                });
                            }
                        });
                    }
                }
            },
            onShow : function ($element) {
                $element.addClass("trHighlight");
            },
            onHide : function ($element) {
                $(".trHighlight").removeClass("trHighlight");
            }
        });
    });
</script>