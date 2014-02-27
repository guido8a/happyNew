<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 1/20/14
  Time: 4:51 PM
--%>

<div style="height: 450px"  class="container-celdas">
    <span class="grupo">
        <table class="table table-bordered table-condensed table-hover">
            <thead>
            <tr>

                <th class="cabecera">Documento</th>
                <th class="cabecera">Fecha Recepción</th>
                <th class="cabecera">De</th>
                <th class="cabecera">Creado Por</th>
                <th class="cabecera">Para</th>
                <th class="cabecera">Destinatario</th>
                <th class="cabecera">Prioridad</th>
                <th class="cabecera">Fecha Límite</th>
                <th class="cabecera">Doc. Principal</th>
                %{--<th class="cabecera">Recepción</th>--}%
            </tr>

            </thead>
            <tbody>
            <g:each in="${tramites}" var="tramite">

                <g:set var="type" value=""/>
                %{--<g:if test="${tramite.estadoTramite?.codigo == 'E004'}">--}%
                    %{--<g:set var="type" value="recibido"/>--}%
                %{--</g:if>--}%

                <g:if test="${idTramitesRecibidos.contains(tramite.tramite.id)}">
                    <g:set var="type" value="recibido"/>
                </g:if>
                <g:if test="${tramite?.tramite?.estadoTramite?.codigo == 'EX03'}">
                    <g:set var="type" value="pendiente"/>
                </g:if>
                <g:if test="${idTramitesRetrasados.contains(tramite.tramite.id)}">
                    <g:set var="type" value="retrasado"/>
                </g:if>

                <g:if test="${idRojos.contains(tramite.tramite.id)}">
                    %{--<g:set var="type" value="pendienteRojo"/>--}%
                    <g:set var="type" value="pendiente pendienteRojo"/>
                </g:if>




                <tr data-id="${tramite?.tramite?.id}" class="${type}">

                    <td>${tramite?.tramite?.numero}</td>
                    <td>${tramite?.tramite?.fechaRespuesta}</td>
                    <td>${tramite?.tramite?.de}</td>
                    <td>${tramite?.tramite?.de?.departamento?.descripcion}</td>
                    <td></td>
                    <td></td>
                    <td>${tramite?.tramite?.estado}</td>
                    <td>${tramite?.tramite?.fechaLimiteRespuesta}</td>
                    <td>${tramite?.tramite?.padre}</td>
                    %{--<td style="text-align: center">--}%
                    %{--<g:link action="" class="btn btn-success btnRecibir">--}%
                    %{--<i class="fa fa-check-circle"></i> Recibir--}%
                    %{--</g:link>--}%

                    %{--</td>--}%
                </tr>
            </g:each>

            </tbody>
        </table>

    </span>

</div>

<script type="text/javascript">
    function clean() {
        $(".recibidoColor").removeClass("recibidoColor");
        $(".retrasadoColor").removeClass("retrasadoColor");
        $(".pendienteColor").removeClass("pendienteColor");
        $(".pendienteRojoColor").removeClass("pendienteRojoColor");
    }
    function getRows(clase) {
        clean();
        $("."+clase).addClass(clase+"Color");
    }
</script>