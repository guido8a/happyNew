<g:if test="${accesos.size() > 0}">
    <h4>Historial</h4>

    <div class="">
        <div class="container-colsAcc">
            <div class="header-columnas">
                <div id="all"></div>
                <table class=" table table-bordered table-condensed">
                    <thead>
                        <tr>
                            <th class="col100">Desde</th>
                            <th class="col100">Hasta</th>
                            <th class="col300">Observaciones</th>
                            <th class="col200">Asignado por</th>
                        </tr>
                    </thead>
                </table>
            </div>
        </div>

        <div class="container-celdasAcc">
            <div id="celdas">
                <table class=" table table-bordered table-condensed" id="tablaAcc">
                    <tbody>
                        <g:each in="${accesos}" var="acceso">
                            <tr data-id="${acceso.id}" class="rowAcc ${acceso.estado == 'A' ? 'success' : acceso.estado == 'F' ? 'active' : 'danger'}">
                                <td class="col100">${acceso.accsFechaInicial.format("dd-MM-yyyy")}</td>
                                <td class="col100">${acceso.accsFechaFinal.format("dd-MM-yyyy")}</td>
                                <td class="col300">${acceso.accsObservaciones}</td>
                                <td class="col200">${acceso.asignadoPor.nombre} ${acceso.asignadoPor.apellido}</td>
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
                $("tr.trHighlight").removeClass("trHighlight");
                var $tr = $(e.target).parents("tr");
                $tr.addClass("trHighlight");
                id = $tr.data("id");
            }
        });
        context.attach('.rowAcc', [
            {
                header : 'Acciones'
            },
            {
                text   : 'Terminar',
                icon   : "<i class='fa fa-stop'></i>",
                action : function (e) {
                    $("tr.trHighlight").removeClass("trHighlight");
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
                                    log(parts[1], parts[0] == "OK" ? "success" : parts[0] == "NO" ? "error" : "info");
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
                    $("tr.trHighlight").removeClass("trHighlight");
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
                                    log(parts[1], parts[0] == "OK" ? "success" : parts[0] == "NO" ? "error" : "info");
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