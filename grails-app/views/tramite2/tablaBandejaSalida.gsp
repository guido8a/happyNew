<div style="margin-top: 10px; height: 450px"  class="container-celdas">
    <span class="grupo">
        <table class="table table-bordered  table-condensed table-hover">
            <thead>
            <tr>
                <th class="cabecera">Documento</th>
                <th class="cabecera">Fec. Creación</th>
                <th class="cabecera">Para</th>
                <th class="cabecera">Destinatario</th>
                <th class="cabecera">Prioridad</th>
                <th class="cabecera">Fecha Envio</th>
                <th class="cabecera">Fecha Límite</th>
                <th class="cabecera">Estado</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${tramites}" var="tramite">
                    <g:set var="limite" value="${tramite.getFechaLimite()}"/>
                <tr data-id="${tramite?.id}" class="${(limite)?((limite<new Date())?'alerta':tramite.estadoTramite.codigo):tramite.estadoTramite.codigo}">
                    <td>${tramite?.codigo}</td>
                    <td>${tramite.fechaCreacion?.format("dd-MM-yyyy")}</td>
                    <g:set var="para" value="${tramite.getPara()}"/>
                    <td title="${para.departamento}">${para.departamento?.codigo}</td>
                    <td>${para.persona}</td>
                    <td>${tramite?.prioridad.descripcion}</td>
                    <td>${tramite.fechaEnvio?.format("dd-MM-yyyy hh:mm")}</td>
                    <td>${limite?limite.format("dd-MM-yyyy hh:mm"):''}</td>
                    <td>${tramite?.estadoTramite.descripcion}</td>
                </tr>
            </g:each>
            </tbody>
        </table>

    </span>

</div>


<script type="text/javascript">
    function clean() {
        $(".revisadoColor").removeClass("revisadoColor");
        $(".enviadoColor").removeClass("enviadoColor");
        $(".noRecibidoColor").removeClass("noRecibidoColor");
//        $(".pendienteRojoColor").removeClass("pendienteRojoColor");
    }
    function getRows(clase) {
        clean();
        $("."+clase).addClass(clase+"Color");
    }
</script>
