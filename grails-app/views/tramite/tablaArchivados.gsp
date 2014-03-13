<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 1/20/14
  Time: 4:51 PM
--%>

<div style="height: 450px"  class="container-celdas">
    <span class="grupo">
        <table class="table table-bordered table-condensed table-hover">
            <thead>
            <tr>

                <th class="cabecera">Documento</th>
                <th class="cabecera">Fecha Recepción</th>
                <th class="cabecera">De</th>
                <th class="cabecera">Creado Por</th>
                <th class="cabecera">Prioridad</th>
                <th class="cabecera">Fecha Respuesta</th>
                <th class="cabecera">Doc. Padre</th>
            </tr>

            </thead>
            <tbody>
            <g:each in="${tramites}" var="tramite">


                <tr>
                    <td>${tramite?.tramite?.codigo}</td>
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

