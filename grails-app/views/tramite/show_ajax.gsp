<%@ page import="happy.tramites.Tramite" %>

<g:if test="${!tramiteInstance}">
    <elm:notFound elem="Tramite" genero="o"/>
</g:if>
<g:else>

    <g:if test="${tramiteInstance?.anio}">
        <div class="row">
            <div class="col-md-2 text-info">
                Anio
            </div>

            <div class="col-md-3">
                ${tramiteInstance?.anio?.encodeAsHTML()}
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.padre}">
        <div class="row">
            <div class="col-md-2 text-info">
                Padre
            </div>

            <div class="col-md-3">
                ${tramiteInstance?.padre?.encodeAsHTML()}
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.tipoDocumento}">
        <div class="row">
            <div class="col-md-2 text-info">
                Tipo Documento
            </div>

            <div class="col-md-3">
                ${tramiteInstance?.tipoDocumento?.encodeAsHTML()}
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.de}">
        <div class="row">
            <div class="col-md-2 text-info">
                De
            </div>

            <div class="col-md-3">
                ${tramiteInstance?.de?.encodeAsHTML()}
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.tipoPersona}">
        <div class="row">
            <div class="col-md-2 text-info">
                Tipo Persona
            </div>

            <div class="col-md-3">
                ${tramiteInstance?.tipoPersona?.encodeAsHTML()}
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.estadoTramite}">
        <div class="row">
            <div class="col-md-2 text-info">
                Estado Tramite
            </div>

            <div class="col-md-3">
                ${tramiteInstance?.estadoTramite?.encodeAsHTML()}
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.tipoTramite}">
        <div class="row">
            <div class="col-md-2 text-info">
                Tipo Tramite
            </div>

            <div class="col-md-3">
                ${tramiteInstance?.tipoTramite?.encodeAsHTML()}
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.origenTramite}">
        <div class="row">
            <div class="col-md-2 text-info">
                Origen Tramite
            </div>

            <div class="col-md-3">
                ${tramiteInstance?.origenTramite?.encodeAsHTML()}
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.codigo}">
        <div class="row">
            <div class="col-md-2 text-info">
                Codigo
            </div>

            <div class="col-md-3">
                <g:fieldValue bean="${tramiteInstance}" field="codigo"/>
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.numero}">
        <div class="row">
            <div class="col-md-2 text-info">
                Numero
            </div>

            <div class="col-md-3">
                <g:fieldValue bean="${tramiteInstance}" field="numero"/>
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.fecha}">
        <div class="row">
            <div class="col-md-2 text-info">
                Fecha
            </div>

            <div class="col-md-3">
                <g:formatDate date="${tramiteInstance?.fecha}" format="dd-MM-yyyy"/>
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.fechaLimiteRespuesta}">
        <div class="row">
            <div class="col-md-2 text-info">
                Fecha Limite Respuesta
            </div>

            <div class="col-md-3">
                <g:formatDate date="${tramiteInstance?.fechaLimiteRespuesta}" format="dd-MM-yyyy"/>
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.asunto}">
        <div class="row">
            <div class="col-md-2 text-info">
                Asunto
            </div>

            <div class="col-md-3">
                <g:fieldValue bean="${tramiteInstance}" field="asunto"/>
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.anexo}">
        <div class="row">
            <div class="col-md-2 text-info">
                Anexo
            </div>

            <div class="col-md-3">
                <g:fieldValue bean="${tramiteInstance}" field="anexo"/>
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.texto}">
        <div class="row">
            <div class="col-md-2 text-info">
                Texto
            </div>

            <div class="col-md-3">
                <g:fieldValue bean="${tramiteInstance}" field="texto"/>
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.ampliacionPlazo}">
        <div class="row">
            <div class="col-md-2 text-info">
                Ampliacion Plazo
            </div>

            <div class="col-md-3">
                <g:fieldValue bean="${tramiteInstance}" field="ampliacionPlazo"/>
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.fechaRespuesta}">
        <div class="row">
            <div class="col-md-2 text-info">
                Fecha Respuesta
            </div>

            <div class="col-md-3">
                <g:formatDate date="${tramiteInstance?.fechaRespuesta}" format="dd-MM-yyyy"/>
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.fechaIngreso}">
        <div class="row">
            <div class="col-md-2 text-info">
                Fecha Ingreso
            </div>

            <div class="col-md-3">
                <g:formatDate date="${tramiteInstance?.fechaIngreso}" format="dd-MM-yyyy"/>
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.fechaModificacion}">
        <div class="row">
            <div class="col-md-2 text-info">
                Fecha Modificacion
            </div>

            <div class="col-md-3">
                <g:formatDate date="${tramiteInstance?.fechaModificacion}" format="dd-MM-yyyy"/>
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.fechaLectura}">
        <div class="row">
            <div class="col-md-2 text-info">
                Fecha Lectura
            </div>

            <div class="col-md-3">
                <g:formatDate date="${tramiteInstance?.fechaLectura}" format="dd-MM-yyyy"/>
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.externo}">
        <div class="row">
            <div class="col-md-2 text-info">
                Externo
            </div>

            <div class="col-md-3">
                <g:fieldValue bean="${tramiteInstance}" field="externo"/>
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.nota}">
        <div class="row">
            <div class="col-md-2 text-info">
                Nota
            </div>

            <div class="col-md-3">
                <g:fieldValue bean="${tramiteInstance}" field="nota"/>
            </div>

        </div>
    </g:if>

    <g:if test="${tramiteInstance?.estado}">
        <div class="row">
            <div class="col-md-2 text-info">
                Estado
            </div>

            <div class="col-md-3">
                <g:fieldValue bean="${tramiteInstance}" field="estado"/>
            </div>

        </div>
    </g:if>

</g:else>