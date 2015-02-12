<%@ page import="happy.seguridad.Persona; happy.tramites.ObservacionTramite; happy.tramites.DocumentoTramite; happy.tramites.PersonaDocumentoTramite; happy.tramites.Tramite" %>
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
            <th>Valor Después</th>
            <th>Acción</th>
        </tr>
    </thead>
    <tbody>
        <g:each in="${res}" var="r">
            <g:set var="clazz" value="${grailsApplication.getDomainClass(r.dominio.split(" ")[1]).clazz}"/>
            <g:set var="obj" value="${clazz.get(r.registro.toLong())}"/>
            <tr class="${r.operacion == 'DELETE' ? 'danger' : r.operacion == 'UPDATE' ? 'info' : r.operacion == 'INSERT' ? 'success' : ''}">
                %{--<td>${r.id}</td>--}%
                <td>${r.fecha.format("dd-MM-yyyy HH:mm:ss")}</td>
                <td>${r.usuario}</td>
                <td>
                    <g:if test="${dominio}">
                        ${dominio}
                    </g:if>
                    <g:else>
                        ${r.dominio.split("\\.")[2]}
                    </g:else>
                </td>
                <td>${operaciones[r.operacion]}</td>
                <td>
                    <g:if test="${obj instanceof Persona}">
                        ${obj.login} (${r.registro})
                    </g:if>
                    <g:elseif test="${obj instanceof Tramite}">
                        ${obj.codigo} (id: ${r.registro})
                    </g:elseif>
                    <g:elseif test="${obj instanceof PersonaDocumentoTramite}">
                        <g:if test="${obj.departamento}">
                            Dep. ${obj.departamento.codigo},
                        </g:if>
                        <g:else>
                            Usu. ${obj.persona.login},
                        </g:else>
                        trám. ${obj.tramite.codigo},
                         rol ${obj.rolPersonaTramite.descripcion} (id: ${r.registro})
                    </g:elseif>
                    <g:elseif test="${obj instanceof DocumentoTramite}">
                        Anexo al trám. ${obj.tramite.codigo} (id: ${r.registro})
                    </g:elseif>
                    <g:elseif test="${obj instanceof ObservacionTramite}">
                        Trám. ${obj.tramite.codigo}, pers. ${obj.persona.login}
                    </g:elseif>
                    <g:else>
                        id: ${r.registro}
                    </g:else>
                </td>
                <td>${r.campo}</td>
                <td><util:renderHTML html="${r.old_value}"/></td>
                <td><util:renderHTML html="${r.new_value}"/></td>
                <td>${r.controlador}/${r.accion}</td>
            </tr>
        </g:each>
    </tbody>
</table>

<div id="paginate" style="width: 100%;height: ${heigth}px;margin-bottom: 10px;text-align: center">
%{--<div style="" class="pg-btn ui-corner-all pg-active" num="1">1</div>--}%
    <g:set var="actual" value="${(offset == 0) ? 1 : (offset / show) + 1}"/>
    <g:each in="${rango}" status="i" var="num">
        <div style="" class="pg-btn ui-corner-all ${(actual.toInteger() == num) ? 'pg-active' : ''} " num="${num}">${num}</div>
    </g:each>
</div>
<script type="text/javascript">
    $(".pg-btn").click(function () {
        var btn = $(this);
        if (!btn.hasClass("pg-active")) {
            var offset = (parseFloat($(this).attr("num")) - 1) * parseFloat("${show}");
            var maxView = "${maxView}";
            buscarAudt(offset, maxView);
            return false;
            %{--openLoader("Cargando");--}%
            %{--$.ajax({--}%
            %{--type    : "POST",--}%
            %{--url     : "${createLink(controller:'auditoria', action:'tablaAudt')}",--}%
            %{--data    : {--}%
            %{--desde     : "${desde}",--}%
            %{--hasta     : "${hasta}",--}%
            %{--operacion : "${operacion}",--}%
            %{--domain    : "${domain}",--}%
            %{--usuario   : "${usuario}",--}%
            %{--maxView   : "${maxView}",--}%
            %{--offset    : (parseFloat($(this).attr("num")) - 1) * parseFloat("${show}")--}%
            %{--},--}%
            %{--success : function (msg) {--}%
            %{--closeLoader();--}%
            %{--$(".tablaAudt").html(msg)--}%
            %{--}--}%
            %{--});--}%
        }
    })
</script>