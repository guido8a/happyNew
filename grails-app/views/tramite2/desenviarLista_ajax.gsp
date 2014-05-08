<style type="text/css">
.lista {
    height     : 300px;
    /*background : red;*/
    overflow-x : hidden;
    overflow-y : auto;
}

td {
    vertical-align : middle !important;
}

.chk {
    cursor : pointer;
}
</style>

<i class='fa fa-magic fa-3x pull-left text-danger text-shadow'></i>

<p>
    ¿Está seguro que desea quitar el enviado del trámite ${tramite.codigo}?<br/>Esta acción no se puede deshacer.
</p>

<p>
    A continuación se muestra una lista con las personas a las cuales se envió el trámite con su respectivo rol, seleccione aquellos
    a quienes desea quitar el enviado.
</p>

<div class="lista">
    <table class="table table-bordered table-hover table-condensed">
        <thead>
            <th>Rol</th>
            <th>Persona/Departamento</th>
            <th class="text-center"><i class="chk chkAll fa fa-square-o fa-lg"></i></th>
        </thead>
        <tbody>
            <g:each in="${paras + ccs}" var="para">
                <tr>
                    <td>${para.rolPersonaTramite.descripcion}</td>
                    <td>${para.departamento ? para.departamento.descripcion : para.persona.login}</td>
                    <td class="text-center">
                        <g:if test="${para.fechaEnvio}">
                            <g:if test="${para.fechaRecepcion}">
                                recibido el<br/>
                                ${para.fechaRecepcion?.format("dd-MM-yyyy HH:mm")}
                            </g:if>
                            <g:else>
                                <i class="chk chkOne fa fa-square-o fa-lg" id="${para.id}"></i>
                            </g:else>
                        </g:if>
                        <g:else>
                            No enviado
                        </g:else>
                    </td>
                </tr>
            </g:each>
        </tbody>
    </table>
</div>

<script type="text/javascript">
    $(function () {
        $(".chkAll").click(function () {
            if ($(this).hasClass("fa-check-square")) {
                //esta checkeado: descheckear
                $(this).removeClass("fa-check-square").addClass("fa-square-o");
                $(".chkOne").removeClass("fa-check-square").addClass("fa-square-o");
            } else {
                //no esta checkeado: checkear
                $(this).addClass("fa-check-square").removeClass("fa-square-o");
                $(".chkOne").addClass("fa-check-square").removeClass("fa-square-o");
            }
        });

        $(".chkOne").click(function () {
            if ($(this).hasClass("fa-check-square")) {
                //esta checkeado: descheckear
                $(this).removeClass("fa-check-square").addClass("fa-square-o");
                $(".chkAll").removeClass("fa-check-square").addClass("fa-square-o");
            } else {
                //no esta checkeado: checkear
                $(this).addClass("fa-check-square").removeClass("fa-square-o");
            }
        });
    });
</script>