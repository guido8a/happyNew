<%@ page import="happy.tramites.EstadoTramiteExterno" %>

<i class='fa fa-exchange fa-3x pull-left text-default text-shadow'></i>

<p class='lead'>
    Va a cambiar el estado del trámite <strong>${params.tramiteInfo}</strong>.
</p>

<p>
    (Estado actual: ${tramite.estadoTramiteExterno.descripcion})
</p>

<g:select name="estadoExterno" from="${EstadoTramiteExterno.list([sort: 'descripcion'])}"
          optionKey="id" optionValue="descripcion" class="form-control" value="${tramite.estadoTramiteExternoId}"/>