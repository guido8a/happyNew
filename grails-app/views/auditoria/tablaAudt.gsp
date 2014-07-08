<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js/plugins/lzm.context/js', file: 'lzm.context-0.5.js')}"></script>
<link href="${resource(dir: 'js/plugins/lzm.context/css', file: 'lzm.context-0.5.css')}" rel="stylesheet">
<table class="table table-bordered table-condensed table-hover">
    <thead>
    <tr>
        <th>Fecha</th>
        <th>Usuario</th>
        <th>Tabla</th>
        <th>Operación</th>
        <th>Registro</th>
        <th>Campo</th>
        <th>Valor Antes</th>
        <th>Valor Despues</th>
    </tr>
    </thead>
    <tbody>
    <g:each in="${res}" var="r">
        <tr>
            <td>${r.fecha.format("dd-MM-yyyy HH:mm:ss")}</td>
            <td>${r.usuario}</td>
            <td>${dominio}</td>
            <td>${r.operacion}</td>
            <td>${r.registro}</td>
            <td>${r.campo}</td>
            <td>${r.old_value}</td>
            <td>${r.new_value}</td>
        </tr>
    </g:each>
    </tbody>
</table>