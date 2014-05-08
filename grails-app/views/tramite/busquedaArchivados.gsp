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
                <th class="cabecera">De</th>
                <th class="cabecera">Para</th>
                <th class="cabecera">Asunto</th>
                <th class="cabecera">Fecha Envío</th>
                <th class="cabecera">Fecha Recepción</th>
                <th class="cabecera">Padre</th>
                <th class="cabecera">Rol</th>
                <th class="cabecera">Observaciones</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${pxtTramites}" var="pxt">
            <g:each in="${tramites}" var="tramite">
                    <g:if test="${pxt?.id == tramite?.id}">
                        <tr>
                            <td>${tramite?.tramite?.codigo}</td>
                            <g:if test="${tramite?.tramite?.deDepartamento}">
                                <td>${tramite?.tramite?.deDepartamento?.descripcion}</td>
                            </g:if>
                            <g:else>
                                <td>${tramite?.tramite?.de?.nombre + " " + tramite?.tramite?.de?.apellido}</td>
                            </g:else>
                            <td>${tramite?.tramite?.getPara()?.persona}</td>
                            <td>${tramite?.tramite?.asunto}</td>
                            <td>${tramite?.fechaEnvio?.format('dd-MM-yyyy HH:mm')}</td>
                            <td>${tramite?.fechaRecepcion?.format('dd-MM-yyyy HH:mm')}</td>
                            <g:if test="${tramite?.tramite?.padre}">
                                <td>${tramite?.tramite?.padre?.codigo}</td>
                            </g:if>
                            <g:else>
                                <td>Trámite Padre</td>
                            </g:else>
                            <td>${tramite?.rolPersonaTramite?.descripcion}</td>
                            <td>${tramite?.tramite?.observaciones}</td>
                        </tr>
                    </g:if>
                </g:each>
            </g:each>
            </tbody>
        </table>
    </span>
</div>
