<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js/plugins/lzm.context/js', file: 'lzm.context-0.5.js')}"></script>
<link href="${resource(dir: 'js/plugins/lzm.context/css', file: 'lzm.context-0.5.css')}" rel="stylesheet">
<table class="table table-bordered table-condensed table-hover" style="margin-bottom: 5px">
    <thead>
        <tr>
            %{--<th>id</th>--}%
            <th>Fecha</th>
            <th>Usuario</th>
            <th>Tabla</th>
            <th>Operación</th>
            <th>Registro</th>
            <th>Campo</th>
            <th>Valor Antes</th>
            <th>Valor Despues</th>
            <th>URL</th>
        </tr>
    </thead>
    <tbody>
        <g:each in="${res}" var="r">
            <tr>
                %{--<td>${r.id}</td>--}%
                <td>${r.fecha.format("dd-MM-yyyy HH:mm:ss")}</td>
                <td>${r.usuario}</td>
                <g:if test="${dominio}">
                    <td>${dominio}</td>
                </g:if>
                <g:else>
                    <td>${r.dominio.split("\\.")[2]}</td>
                </g:else>
                <td>${r.operacion}</td>
                <td>${r.registro}</td>
                <td>${r.campo}</td>
                <td>${r.old_value}</td>
                <td>${r.new_value}</td>
                <td>${r.controlador}/${r.accion}</td>
            </tr>
        </g:each>
    </tbody>
</table>

<div id="paginate" style="width: 100%;height: ${heigth}px;margin-bottom: 10px;text-align: center">
%{--<div style="" class="pg-btn ui-corner-all pg-active" num="1">1</div>--}%
    <g:set var="actual" value="${(offset == 0) ? 1 : (offset / show) + 1}"></g:set>

    <g:each in="${rango}" status="i" var="num">
        <div style="" class="pg-btn ui-corner-all ${(actual.toInteger() == num) ? 'pg-active' : ''} " num="${num}">${num}</div>
    </g:each>
</div>
<script type="text/javascript">
    $(".pg-btn").click(function () {
        var btn = $(this)
        if (!btn.hasClass("pg-active")) {
            openLoader("Cargando")
            $.ajax({
                type    : "POST",
                url     : "${createLink(controller:'auditoria', action:'tablaAudt')}",
                data    : {
                    desde     : "${desde}",
                    hasta     : "${hasta}",
                    operacion : "${operacion}",
                    domain    : "${domain}",
                    usuario   : "${usuario}",
                    maxView   : "${maxView}",
                    offset    : (parseFloat($(this).attr("num")) - 1) * parseFloat("${show}")
                },
                success : function (msg) {
                    closeLoader()
                    $(".tablaAudt").html(msg)
                }
            });
        }
    })
</script>