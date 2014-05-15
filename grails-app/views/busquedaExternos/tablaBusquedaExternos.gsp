<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 15/05/14
  Time: 03:25 PM
--%>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<table class="table table-bordered table-condensed table-hover">
    <thead>
    <tr>
        <th class="cabecera">Documento</th>
        <th class="cabecera" style="width: 150px;">Fecha Envío</th>
        <th class="cabecera" style="width: 150px;">Fecha Recepción</th>
        <th class="cabecera">De</th>
        <th class="cabecera">Creado por</th>
        <th class="cabecera">Para</th>
        <th class="cabecera">Receptor</th>
        <th class="cabecera">Estado</th>
    </tr>

    </thead>
    <tbody>
    <g:each in="${tramites}" var="tramite">

        <g:set var="now" value="${new java.util.Date()}"/>

        <g:set var="clase" value=""/>


        <tr data-id="${tramite?.id}"
            class="${clase}"
            codigo="${tramite.codigo}" departamento="${tramite?.de?.departamento?.codigo}"
            anexo="${anexo}" prtr="${tramite?.id}">

                <td title="${tramite?.asunto}">
                    ${tramite?.codigo}
                </td>

            <td style="width: 150px;">${tramite?.fechaEnvio?.format('dd-MM-yyyy HH:mm')}</td>
            <td style="width: 150px;">${tramite?.getPara()?.fechaRecepcion?.format('dd-MM-yyyy HH:mm')}</td>
            <td title="${tramite?.de?.departamento?.descripcion}">${tramite?.de?.departamento?.codigo}</td>
            <td title="${tramite?.de}">${tramite?.de?.login ?: tramite?.de?.toString()}</td>

            <g:if test="${tramite?.getPara()?.persona}">
                <td>${tramite?.getPara()?.persona?.nombre + " " + tramite?.getPara()?.persona?.apellido}</td>
            </g:if>
            <g:else>
                <td>${tramite?.getPara()?.departamento?.descripcion}</td>
            </g:else>
            <td>${tramite?.paraExterno}</td>
            <td>${tramite?.estadoTramiteExterno?.descripcion}</td>
        </tr>
    </g:each>

    </tbody>
</table>

<script type="text/javascript">
    $(function () {
        $(".cabecera").click(function () {
            var $col = $(this);
            var order = "";
            if ($col.data("order") == "asc") {
                order = "desc";
            } else if ($col.data("order") == "desc") {
                order = "asc";
            }
            var data = {
                domain : $col.data("domain"),
                sort   : $col.data("sort"),
                order  : order
            };
            cargarBandeja(false, data);
        });
    });
</script>