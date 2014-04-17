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
                <th class="alinear">Documento</th>
                <th class="alinear">Para</th>
                <th class="alinear">Asunto</th>
                <th class="alinear">Prioridad</th>
                <th class="alinear">De</th>
                <th class="alinear">Fecha Creación</th>
                <th class="alinear">Fecha Envio</th>
                <th class="alinear">Estado</th>
            </tr>

            </thead>
            <tbody>
            <g:each in="${tramites}" var="tramite">
                <tr id="${tramite?.id}" data-id="${tramite.id}">
                    <td>${tramite?.codigo}</td>
                    <g:if test="${tramite?.para?.persona}">
                        <td>${tramite?.para?.persona?.nombre + " " + tramite?.para?.persona?.apellido}</td>
                    </g:if>
                    <g:else>
                        <td>${tramite?.para?.departamento?.descripcion}</td>
                    </g:else>
                    <td>${tramite?.asunto}</td>
                    <td>${tramite?.prioridad?.descripcion}</td>
                    <g:if test="${tramite?.de}">
                        <td>${tramite?.de?.nombre + " " + tramite?.de?.apellido}</td>
                    </g:if>
                    <g:else>
                        <td>${tramite?.deDepartamento?.descripcion}</td>
                    </g:else>
                    <td>${tramite?.fechaCreacion.format('dd-MM-yyyy HH:mm')}</td>
                    <g:if test="${tramite?.fechaEnvio}">
                        <td>${tramite?.fechaEnvio.format('dd-MM-yyyy HH:mm')}</td>
                    </g:if>
                    <g:else>
                        <td></td>
                    </g:else>
                    <td>${tramite?.estadoTramite?.descripcion}</td>
                </tr>
            </g:each>

            </tbody>
        </table>

    </span>

</div>
