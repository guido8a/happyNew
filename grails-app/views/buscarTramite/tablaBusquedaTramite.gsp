<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 14/03/14
  Time: 11:20 AM
--%>


<div style="height: 450px" class="container-celdas">
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

                    <g:set var="padre" value=""/>
                    <g:set var="clase" value="${'nada'}"/>

                    <g:if test="${happy.tramites.Tramite.get(tramite?.trmt__id).de?.id == session.usuario.id}">
                        <g:if test="${happy.tramites.Tramite.get(tramite?.trmt__id).padre}">
                            <g:set var="padre" value="${happy.tramites.Tramite.get(tramite?.trmt__id).padre?.id}"/>
                            <g:set var="clase" value="${'padre'}"/>
                        </g:if>
                    </g:if>

                    <g:if test="${tramite.trmtanxo == 1}">
                        <g:set var="clase" value="${clase + ' conAnexo'}"/>
                    </g:if>

                    <g:if test="${tramite?.fc_recp}">
                        <g:set var="clase" value="${clase + ' recibido'}"/>
                    </g:if>

                    <tr id="${tramite?.trmt__id}" data-id="${tramite?.trmt__id}" padre="${padre}" class="${clase}">
                        <td>
                            ${tramite?.trmtcdgo}
                            <g:if test="${tramite.trmtanxo == 1}">
                                <i class="fa fa-paperclip fa-fw" style="margin-left: 10px"></i>
                            </g:if>
                            <g:if test="${tramite?.es_extr == 1}">
                                (ext)
                            </g:if>
                        </td>

                        <td>
                            <g:if test="${tramite?.pr_prsn}">
                                ${tramite?.pr_prsn}
                            </g:if>
                            <g:elseif test="${tramite?.pr_dpto}">
                                ${tramite?.pr_dpto}
                            </g:elseif>
                            <g:if test="${tramite?.es_extr == 1 && tramite?.pr_extr}">
                                (${tramite?.pr_extr})
                            </g:if>
                        </td>

                        %{--<g:if test="${tramite?.es_extr == 1}">--}%
                        %{--<td>--}%
                        %{--${tramite?.pr_extr}--}%
                        %{--</td>--}%
                        %{--</g:if>--}%
                        %{--<g:elseif test="${tramite?.pr_prsn}">--}%
                        %{--<td>${tramite?.pr_prsn}</td>--}%
                        %{--</g:elseif>--}%
                        %{--<g:elseif test="${tramite?.pr_dpto}">--}%
                        %{--<td>${tramite?.pr_dpto}</td>--}%
                        %{--</g:elseif>--}%
                        %{--<g:else>--}%
                        %{--<td></td>--}%
                        %{--</g:else>--}%

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
                        <td>${tramite?.fc_trmt?.format('dd-MM-yyyy HH:mm')}</td>
                        <g:if test="${tramite?.fc_envi}">
                            <td>${tramite?.fc_envi?.format('dd-MM-yyyy HH:mm')}</td>
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
