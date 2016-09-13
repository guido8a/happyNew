<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 16/06/16
  Time: 11:34 AM
--%>
<g:if test="${paras}">
    <table class="table table-bordered table-condensed table-hover">
        <thead>
        <tr>
            <th style="width: 50%">Departamento</th>
            <th style="width: 10%">Acciones</th>
        </tr>
        </thead>
    </table>

    <div class="row-fluid"  style="width: 99.7%;height: 500px;overflow-y: auto;float: right;">
        <div class="span12">
            <div style="width: 850px; height: 500px;">

                <table class="table table-bordered table-condensed table-hover">
                    <tbody>
                    <g:each in="${paras}" var="p">
                        <tr>
                            <td>${p?.deparatamentoPara?.descripcion}</td>
                            <td style="text-align: center">
                                <a href="#" class="btn btn-danger btn-sm btnBorrar" title="Borrar" data-id="${p?.id}">
                                    <i class="fa fa-trash"></i>
                                </a>
                            </td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

</g:if>
<g:else>
    <div class="alert alert-info" role="alert">
        <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
        <span class="sr-only">Error:</span>
        No tiene ninguno departamento asignado!
    </div>
</g:else>


<script>
    $(".btnBorrar").click(function () {
        var titulo = "Borrar departamento"
        var id = $(this).data('id');
        bootbox.confirm(titulo, function (result) {
            if (result) {
//                console.log("a borrar: ", id);
                $.ajax({
                    type: 'POST',
                    url: "${createLink(controller: 'departamento', action: 'borrarDepartamento_ajax')}",
                    data: {
                        id: id
                    },
                    success: function (msg) {
                        if (msg == 'ok') {
                            log("Departamento borrado correctamente", "success")
                            cargarDepartamentos();
                            cargarTablaDepartamentos();
                        } else {
                            log("Error al borrar el departamento", "success")
                        }
                    }
                });
            }
        });
    });
</script>