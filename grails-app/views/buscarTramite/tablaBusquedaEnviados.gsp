<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 14/04/14
  Time: 03:41 PM
--%>



<div style="height: 450px"  class="container-celdas">
    <span class="grupo">
        <table class="table table-bordered table-condensed table-hover">
            <thead>
            <tr>

                %{--<th class="cabecera">Asunto</th>--}%
                <th class="alinear">Documento</th>
                <th class="alinear">Para</th>
                <th class="alinear">Envia</th>
                <th class="alinear">Asunto</th>
                <th class="alinear">Prioridad</th>
                <th class="alinear">De</th>
                <th class="alinear">Fecha Creación</th>
                <th class="alinear">Fecha Envio</th>
            </tr>

            </thead>
            <tbody>
            <g:each in="${tramites}" var="tramite">
                <tr id="${tramite?.trmt__id}" data-id="${tramite.trmt__id}">
                    <td>${tramite?.trmtcdgo}</td>
                    <g:if test="${tramite.pr_prsn}">
                        <td>${tramite.pr_prsn}</td>
                    </g:if>
                    <g:elseif test="${tramite?.pr_dpto}">
                        <td>${tramite?.pr_dpto}</td>
                    </g:elseif>
                    <g:else>
                        <td></td>
                    </g:else>
                    <td>${tramite?.en_prsn}</td>
                    <td>${tramite?.trmtasnt}</td>
                    <td>${tramite?.tppddscr}</td>
                    <g:if test="${tramite?.de_prsn}">
                        <td>${tramite?.de_prsn}</td>
                    </g:if>
                    <g:elseif test="${tramite?.de_dpto}">
                        <td>${tramite?.de_dpto}</td>
                    </g:elseif>
                    <g:else>
                        <td></td>
                    </g:else>
                    <td>${tramite?.fc_trmt.format('dd-MM-yyyy HH:mm')}</td>
                    <g:if test="${tramite?.fc_envi}">
                        <td>${tramite?.fc_envi.format('dd-MM-yyyy HH:mm')}</td>
                    </g:if>
                    <g:else>
                        <td></td>
                    </g:else>


                %{--<td>${tramite?.fechaLimiteRespuesta?.format('dd-MM-yyyy HH:mm')}</td>--}%

                </tr>
            </g:each>

            </tbody>
        </table>

    </span>

</div>
