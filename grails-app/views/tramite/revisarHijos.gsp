<p>
    ADVERTENCIA: El trámite <strong>${pxt.tramite?.codigo}</strong> está por ser
${params.tipo == "archivar" ? "archivado" : "anulado"}!
</p>

<g:if test="${params.tipo == 'anular' && hijos.size() > 0}">
    <p>
        Tenga en cuenta que se anularán los siguientes trámites derivados:
    <ul>
        <g:each in="${hijos}" var="hijo">
            <li>${hijo.codigo} ${hijo.asunto}</li>
        </g:each>
    </ul>
    </p>
</g:if>

<div class="row">
    <div class="col-md-3">Solicitado por</div>

    <div class="col-md-9"><g:textField name="aut" class="form-control"/></div>
</div>

<label for="observacionArchivar">Observaciones:</label>
<g:textArea name="observacionArchivar" maxlength="255" class="form-control" style="resize: none; height: 150px; "
            value=""/>
