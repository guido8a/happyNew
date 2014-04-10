<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 1/21/14
  Time: 1:01 PM
--%>


<div style="height: 450px"  class="container-celdas">
    <span class="grupo">
        <table class="table table-bordered table-condensed table-hover">
            <thead>
            <tr>
                <th class="cabecera">Documento</th>
                <th class="cabecera">Fecha Envío</th>
                <th class="cabecera">Fecha Recepción</th>
                <th class="cabecera">De</th>
                <th class="cabecera">Para</th>
                <th class="cabecera">Prioridad</th>
                <th class="cabecera">Fecha Respuesta</th>
                <th class="cabecera">Rol</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${tramites}" var="tramite">
                <g:each in="${pxtTramites}" var="pxt">
                    <g:if test="${tramite?.tramite?.id == pxt?.tramite?.id}">
                        <tr>
                            <td>${tramite?.tramite?.codigo}</td>
                            <td>${tramite?.fechaRecepcion?.format('dd-MM-yyyy HH:mm')}</td>
                            <td>${tramite?.tramite?.de}</td>
                            <td>${tramite?.tramite?.de?.departamento?.descripcion}</td>
                            <td>${tramite?.tramite?.prioridad?.descripcion}</td>
                            <td>${tramite?.fechaLimiteRespuesta?.format('dd-MM-yyyy HH:mm')}</td>
                            <td>${tramite?.tramite?.padre?.codigo}</td>
                        </tr>
                    </g:if>
                </g:each>
            </g:each>
            </tbody>
        </table>
    </span>
</div>
