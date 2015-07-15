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

<g:if test="${paraRecibio != ''}">
    <h5>No puede quitar el enviado del trámite ${tramite.codigo}</h5>

    <div class="alert alert-danger" style="padding: 5px;">${paraRecibio}</div>
</g:if>
<g:else>
    <g:if test="${contestados != ''}">
    %{--<g:if test="${cont != 0}">--}%
        <h5>No puede quitar el enviado del trámite ${tramite.codigo}</h5>
        <ul>
            <util:renderHTML html="${contestados}"/>
        </ul>
    </g:if>
    <g:else>
        <i class='fa fa-magic fa-3x pull-left text-danger text-shadow'></i>

        <p>
            ¿Está seguro que desea quitar el enviado del trámite ${tramite.codigo}?<br/>Esta acción no se puede deshacer.
        </p>

        <p>
            A continuación se muestra una lista con las personas a las cuales se envió el trámite con su respectivo rol, seleccione aquellos
            a quienes desea quitar el enviado.
        </p>

        <div class="alert alert-info" style="padding: 10px; font-size: larger;">
            Si quita el enviado de la persona con rol PARA se eliminarán las copias aunque ya se encuentren recibidas o archivadas.
        </div>

        <div class="lista">
            <table class="table table-bordered table-hover table-condensed">
                <thead>
                    <th>Rol</th>
                    <th>Persona/Departamento</th>
                    <th class="text-center">
                        <g:if test="${([paras] + ccs).size() == 1}">
                            <i class="chk chkAll fa fa-check-square fa-lg"></i>
                        </g:if>
                        <g:else>
                            <i class="chk chkAll fa fa-square-o fa-lg"></i>
                        </g:else>
                    %{--<i class="chk chkAll fa fa-square-o fa-lg"></i>--}%
                    </th>
                </thead>
                <tbody>
                    <g:each in="${tramites}" var="para">
                        <g:if test="${para}">
                            <tr>
                                <td>${para.rolPersonaTramite?.descripcion}</td>
                                <td>${para.departamento ? para.departamento.descripcion : para.persona?.login}</td>
                                <td class="text-center">
                                    <g:if test="${estadosNo.contains(para.estado)}">
                                        Trámite ${para.estado.descripcion}
                                        <i class="chk chkOne fa fa-square-o fa-lg" id="${para.id}"></i>
                                    </g:if>
                                    <g:else>
                                        <g:if test="${para.fechaEnvio}">
                                            <g:if test="${para.fechaRecepcion}">
                                                recibido el<br/>
                                                ${para.fechaRecepcion?.format("dd-MM-yyyy HH:mm")}
                                                <i class="chk chkOne fa fa-square-o fa-lg" id="${para.id}"></i>
                                            </g:if>
                                            <g:else>
                                                <g:if test="${([paras] + ccs).size() == 1}">
                                                    <i class="chk chkOne fa fa-check-square fa-lg" id="${para.id}"></i>
                                                </g:if>
                                                <g:else>
                                                    <i class="chk chkOne fa fa-square-o fa-lg" id="${para.id}"></i>
                                                </g:else>
                                            </g:else>
                                        </g:if>
                                        <g:else>
                                            No enviado
                                        </g:else>
                                    </g:else>
                                </td>
                            </tr>
                        </g:if>
                    </g:each>
                </tbody>
            </table>
        </div>

    %{--<div class="row">--}%
    %{--<div class="col-md-3">Autorizado por</div>--}%

    %{--<div class="col-md-9"><g:textField name="aut" class="form-control"/></div>--}%
    %{--</div>--}%

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
    </g:else>
</g:else>