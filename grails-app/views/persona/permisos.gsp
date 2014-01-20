<style type="text/css">
.table {
    font-size     : 13px;
    width         : auto !important;
    margin-bottom : 0 !important;
}

.container-celdasPerm {
    max-height : 200px;
    width      : 815px;
    overflow   : auto;
}

.col100 {
    width : 100px;
}

.col200 {
    width : 250px;
}

.col300 {
    width : 304px;
}

</style>

<g:if test="${permisos.size() > 0}">
    <h4>Historial</h4>

    <div class="">
        <div id="container-cols">
            <div class="header-columnas">
                <div id="all"></div>
                <table class=" table table-bordered table-condensed">
                    <thead>
                        <tr>
                            <th class="col200">Permiso</th>
                            <th class="col100">Desde</th>
                            <th class="col100">Hasta</th>
                            <th class="col300">Observaciones</th>
                        </tr>
                    </thead>
                </table>
            </div>
        </div>

        <div class="container-celdasPerm">
            <div id="celdas">
                <table class=" table table-bordered table-condensed">
                    <tbody>
                        <g:each in="${permisos}" var="permiso">
                            <tr data-id="${permiso.id}" class="${permiso.estado == 'A' ? 'success' : permiso.estado == 'F' ? 'active' : 'danger'}">
                                <td class="col200">${permiso.permisoTramite.descripcion}</td>
                                <td class="col100">${permiso.fechaInicio.format("dd-MM-yyyy")}</td>
                                <td class="col100">${permiso.fechaFin ? permiso.fechaFin.format("dd-MM-yyyy") : ""}</td>
                                <td class="col300">${permiso.observaciones}</td>
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
        var id = null;
        context.settings({
            onShow : function (e) {
                $("tr.success").removeClass("success");
                var $tr = $(e.target).parent();
                $tr.addClass("success");
                id = $tr.data("id");
            }
        });
        context.attach('tbody>tr', [
            {
                header : 'Acciones'
            },
            {
                text   : 'Terminar',
                icon   : "<i class='fa fa-stop'></i>",
                action : function (e) {
                    $("tr.success").removeClass("success");
                    e.preventDefault();
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
                                    log(parts[1], parts[0] == "OK" ? "success" : "error");
                                    loadPermisos();
                                }
                            });
                        }
                    });
                }
            },
            {
                text   : 'Eliminar',
                icon   : "<i class='fa fa-trash-o'></i>",
                action : function (e) {
                    $("tr.success").removeClass("success");
                    e.preventDefault();
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
                                    log(parts[1], parts[0] == "OK" ? "success" : "error");
                                    loadPermisos();
                                }
                            });
                        }
                    });
                }
            }
        ]);
    });
</script>