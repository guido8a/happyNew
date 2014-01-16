<%@ page import="happy.seguridad.Prfl" %>

<g:if test="${!prflInstance}">
    <elm:notFound elem="Prfl" genero="o"/>
</g:if>
<g:else>

    <g:if test="${prflInstance?.codigo}">
        <div class="row">
            <div class="col-md-2 text-info">
                Codigo
            </div>

            <div class="col-md-3">
                <g:fieldValue bean="${prflInstance}" field="codigo"/>
            </div>

        </div>
    </g:if>

    <g:if test="${prflInstance?.descripcion}">
        <div class="row">
            <div class="col-md-2 text-info">
                Descripcion
            </div>

            <div class="col-md-3">
                <g:fieldValue bean="${prflInstance}" field="descripcion"/>
            </div>

        </div>
    </g:if>

    <g:if test="${prflInstance?.nombre}">
        <div class="row">
            <div class="col-md-2 text-info">
                Nombre
            </div>

            <div class="col-md-3">
                <g:fieldValue bean="${prflInstance}" field="nombre"/>
            </div>

        </div>
    </g:if>

    <g:if test="${prflInstance?.observaciones}">
        <div class="row">
            <div class="col-md-2 text-info">
                Observaciones
            </div>

            <div class="col-md-3">
                <g:fieldValue bean="${prflInstance}" field="observaciones"/>
            </div>

        </div>
    </g:if>

    <g:if test="${prflInstance?.padre}">
        <div class="row">
            <div class="col-md-2 text-info">
                Padre
            </div>

            <div class="col-md-3">
                ${prflInstance?.padre?.encodeAsHTML()}
            </div>

        </div>
    </g:if>

    <g:if test="${prflInstance?.perfiles}">
        <div class="row">
            <div class="col-md-2 text-info">
                Perfiles
            </div>

            <div class="col-md-3">
                <ul>
                    <g:each in="${prflInstance.perfiles}" var="p">
                        <li>${p?.encodeAsHTML()}</li>
                    </g:each>
                </ul>
            </div>

        </div>
    </g:if>

    <g:if test="${prflInstance?.permisos}">
        <div class="row">
            <div class="col-md-2 text-info">
                Permisos
            </div>

            <div class="col-md-3">
                <ul>
                    <g:each in="${prflInstance.permisos}" var="p">
                        <li>${p?.encodeAsHTML()}</li>
                    </g:each>
                </ul>
            </div>

        </div>
    </g:if>

</g:else>