<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 14/03/14
  Time: 11:20 AM
--%>


<div style="height: 450px"  class="container-celdas">
    <span class="grupo">
        <table class="table table-bordered table-condensed table-hover">
            <thead>
            <tr>

                %{--<th class="cabecera">Asunto</th>--}%
                <th class="alinear">Documento</th>
                <th class="alinear">Para</th>
                <th class="alinear">Fecha Recepción</th>
                <th class="alinear">De</th>
                <th class="alinear">Creado Por</th>
                <th class="alinear">Prioridad</th>
                <th class="alinear">Fecha Respuesta</th>
                <th class="alinear">Doc. Padre</th>
            </tr>

            </thead>
            <tbody>
            <g:each in="${tramites}" var="tramite">
                <tr>
                    <td>${tramite?.tramite?.codigo}</td>
                    <g:if test="${tramite?.persona?.nombre}">
                        <td><b>${tramite?.rolPersonaTramite?.descripcion}:</b> ${tramite?.persona?.nombre} ${tramite?.persona?.apellido}</td>
                    </g:if>
                    <g:elseif test="${tramite?.departamento?.descripcion}">
                        <td><b>${tramite?.rolPersonaTramite?.descripcion}:</b> ${tramite?.departamento?.descripcion}</td>
                    </g:elseif>
                    <g:else>
                        <td></td>
                    </g:else>
                    %{--<td>${tramite?.tramite?.asunto}</td>--}%
                    <td>${tramite?.fechaRecepcion?.format('dd-MM-yyyy HH:mm')}</td>
                    <td>${tramite?.tramite?.de}</td>
                    <td>${tramite?.tramite?.de?.departamento?.descripcion}</td>
                    <td>${tramite?.tramite?.prioridad?.descripcion}</td>
                    <td>${tramite?.fechaLimiteRespuesta?.format('dd-MM-yyyy HH:mm')}</td>
                    <td>${tramite?.tramite?.padre?.codigo}</td>

                </tr>
            </g:each>

            </tbody>
        </table>

    </span>

</div>
