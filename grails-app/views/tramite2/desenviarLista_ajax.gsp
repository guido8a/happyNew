<style type="text/css">
.lista {
    height     : 300px;
    /*background : red;*/
    overflow-x : hidden;
    overflow-y : auto;
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
            <th class="text-center"><i class="fa fa-square-o fa-lg"></i></th>
        </thead>
        <tbody>
            <g:each in="${paras}" var="para">
                <tr>
                    <td>${para.rolPersonaTramite.descripcion}</td>
                    <td>${para.departamento ? para.departamento.descripcion : para.persona.login}</td>
                    <td class="text-center">${para.fechaRecepcion?.format("dd-MM-yyyy HH:mm")}</td>
                </tr>
            </g:each>
            <g:each in="${ccs}" var="cc">
                <tr>
                    <td>${cc.rolPersonaTramite.descripcion}</td>
                    <td>${cc.departamento ? cc.departamento.descripcion : cc.persona.login}</td>
                    <td class="text-center">${cc.fechaRecepcion?.format("dd-MM-yyyy HH:mm")}</td>
                </tr>
            </g:each>
        </tbody>
    </table>
</div>