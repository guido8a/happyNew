<g:form name="frm-ampliar" action="ampliarPlazo_ajax">
    <table class="table table-bordered table-condensed">
        <thead>
            <tr>
                <th>Documento</th>
                <th>De</th>
                <th>Para</th>
                <th>Fecha envío</th>
                <th>Fecha recepción</th>
                <th>Fecha límite</th>
                <th>Estado</th>
                <th>Nueva fecha límite</th>
            </tr>
        </thead>
        <tbody>
            <g:each in="${personas}" var="pers">
                <g:if test="${pers.persona?.departamentoId == dpto.id || pers.departamentoId == dpto.id}">
                    <tr>
                        <td>${pers.tramite.codigo} ${pers.tramite.externo == '1' ? " (ext)" : ''}</td>
                        <td>${pers.tramite.deDepartamento ? pers.tramite.deDepartamento.codigo : pers.tramite.de.login}</td>
                        <td>
                            ${pers.rolPersonaTramite.descripcion}
                            ${pers.departamento ? pers.departamento.codigo : pers.persona}
                            ${pers.tramite.externo == '1' && pers.tramite.paraExterno ? " (" + pers.tramite.paraExterno + ")" : ''}
                        </td>
                        <td>${pers.fechaEnvio?.format("dd-MM-yyyy HH:mm")}</td>
                        <td>${pers.fechaRecepcion?.format("dd-MM-yyyy HH:mm")}</td>
                        <td>${pers.fechaLimiteRespuesta?.format("dd-MM-yyyy HH:mm")}</td>
                        <td>${pers.estado?.descripcion}</td>
                        <td>
                            <div class="col-md-9">
                                <g:if test="${pers.fechaLimiteRespuesta}">
                                    <elm:datepicker minDate="${pers.fechaLimiteRespuesta?.format('dd-MM-yyyy')}"
                                                    class="form-control input-sm required" name="fecha_${pers.id}"
                                                    daysOfWeekDisabled="0,6"
                                                    value="${pers.fechaLimiteRespuesta}"/>
                                </g:if>
                            </div>

                            <div class="col-md-3">
                                ${pers.fechaLimiteRespuesta?.format("HH:mm")}
                            </div>
                        </td>
                    </tr>
                </g:if>
            </g:each>
        </tbody>
    </table>

    %{--<div class="row">--}%
        %{--<div class="col-md-2">Solicitado por</div>--}%

        %{--<div class="col-md-4"><g:textField name="aut" class="form-control"/></div>--}%
    %{--</div>--}%
</g:form>
<script type="text/javascript">
    $(function () {
        $("#frm-ampliar").validate();
    });
</script>