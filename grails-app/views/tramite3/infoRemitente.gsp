<%@ page import="happy.tramites.Departamento" %>

<g:if test="${!tramite}">
    <elm:notFound elem="Trámite" genero="o"/>
</g:if>
<g:else>

    <g:if test="${tramite?.paraExterno}">
        <div class="row">
            <div class="col-md-3 text-info">
                Institución
            </div>

            <div class="col-md-9">
                <g:fieldValue bean="${tramite}" field="paraExterno"/>
            </div>

        </div>
    </g:if>

    <g:if test="${tramite?.numeroDocExterno}">
        <div class="row">
            <div class="col-md-3 text-info">
                Número documento externo
            </div>

            <div class="col-md-9">
                <g:fieldValue bean="${tramite}" field="numeroDocExterno"/>
            </div>

        </div>
    </g:if>

    <g:if test="${tramite?.telefono}">
        <div class="row">
            <div class="col-md-3 text-info">
                Teléfono
            </div>

            <div class="col-md-9">
                <g:fieldValue bean="${tramite}" field="telefono"/>
            </div>

        </div>
    </g:if>

    <g:if test="${tramite?.contacto}">
        <div class="row">
            <div class="col-md-3 text-info">
                Contacto
            </div>

            <div class="col-md-9">
                <g:fieldValue bean="${tramite}" field="contacto"/>
            </div>

        </div>
    </g:if>


</g:else>