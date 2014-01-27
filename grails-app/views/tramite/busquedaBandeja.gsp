<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 1/21/14
  Time: 1:01 PM
--%>


<div style="margin-top: 10px; height: 450px"  class="container-celdas">
    <span class="grupo">
        <table class="table table-bordered table-striped table-condensed table-hover">
            <thead>
            <tr>
                <th class="cabecera">Documento</th>
                <th class="cabecera">Fecha Recepción</th>
                <th class="cabecera">De</th>
                <th class="cabecera">Creado Por</th>
                <th class="cabecera">Para</th>
                <th class="cabecera">Destinatario</th>
                <th class="cabecera">Prioridad</th>
                <th class="cabecera">Fecha Límite</th>
                <th class="cabecera">Doc. Principal</th>
                %{--<th class="cabecera">Recepción</th>--}%
            </tr>

            </thead>
            <tbody>
            <g:each in="${tramites}" var="tramite">

                <tr>
                    <td>${tramite?.numero}</td>
                    <td>${tramite?.fechaRespuesta}</td>
                    <td>${tramite?.de}</td>
                    <td>${tramite?.de?.departamento?.descripcion}</td>
                    <td></td>
                    <td></td>
                    <td>${tramite?.estado}</td>
                    <td>${tramite?.fechaLimiteRespuesta}</td>
                    <td>${tramite?.padre}</td>
                    %{--<td style="text-align: center">--}%
                        %{--<g:link action="" class="btn btn-success btnRecibir">--}%
                            %{--<i class="fa fa-check-circle"></i> Recibir--}%
                        %{--</g:link>--}%

                    %{--</td>--}%
                </tr>




            </g:each>

            </tbody>
        </table>

    </span>

</div>