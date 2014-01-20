<style type="text/css">
.table {
    font-size     : 13px;
    width         : auto !important;
    margin-bottom : 0 !important;
}

.container-celdas {
    max-height : 200px;
    width      : 554px;
    overflow   : auto;
}

.col100 {
    width : 100px;
}

.col300 {
    width : 304px;
}

</style>

<g:if test="${accesos.size() > 0}">
    <h4>Historial</h4>

    <div class="">
        <div id="container-cols">
            <div class="header-columnas">
                <div id="all"></div>
                <table class=" table table-bordered table-condensed">
                    <thead>
                        <tr>
                            <th class="col100">Desde</th>
                            <th class="col100">Hasta</th>
                            <th class="col300">Observaciones</th>
                        </tr>
                    </thead>
                </table>
            </div>
        </div>

        <div class="container-celdas">
            <div id="celdas">
                <table class=" table table-bordered table-condensed">
                    <tbody>
                        <g:each in="${accesos}" var="acceso">
                            <tr data-id="${acceso.id}" class="${acceso.estado == 'A' ? 'success' : acceso.estado == 'F' ? 'active' : 'danger'}">
                                <td class="col100">${acceso.accsFechaInicial.format("dd-MM-yyyy")}</td>
                                <td class="col100">${acceso.accsFechaFinal.format("dd-MM-yyyy")}</td>
                                <td class="col300">${acceso.accsObservaciones}</td>
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
                    bootbox.confirm("<i class='fa fa-warning fa-3x pull-left text-warning text-shadow'></i><p>Esto cambiará la fecha final de la restricción a la fecha actual. ¿Desea continuar?</p>", function (res) {
                        if (res) {
                            $.ajax({
                                type    : "POST",
                                url     : "${createLink(action: 'terminarAcceso_ajax')}",
                                data    : {
                                    id : id
                                },
                                success : function (msg) {
                                    var parts = msg.split("_");
                                    log(parts[1], parts[0] == "OK" ? "success" : "error");
                                    loadAccesos();
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
                    bootbox.confirm("<i class='fa fa-trash-o fa-3x pull-left text-danger text-shadow'></i><p>Esto eliminará completamente la restricción. ¿Desea continuar?</p>", function (res) {
                        if (res) {
                            $.ajax({
                                type    : "POST",
                                url     : "${createLink(action: 'eliminarAcceso_ajax')}",
                                data    : {
                                    id : id
                                },
                                success : function (msg) {
                                    var parts = msg.split("_");
                                    log(parts[1], parts[0] == "OK" ? "success" : "error");
                                    loadAccesos();
                                }
                            });
                        }
                    });
                }
            }
        ]);
    });
</script>