<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 04/06/14
  Time: 01:02 PM
--%>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js/plugins/lzm.context/js', file: 'lzm.context-0.5.js')}"></script>
<link href="${resource(dir: 'js/plugins/lzm.context/css', file: 'lzm.context-0.5.css')}" rel="stylesheet">

<div style="height: 55px"  class="container-celdas">
    <span class="grupo">
        <table class="table table-bordered table-condensed table-hover">
            <thead>
            <tr>
                <th class="cabecera">Documento</th>
                <th class="cabecera">Fecha Recepción</th>
                <th class="cabecera">De</th>
                <th class="cabecera">Creado Por</th>
                <th class="cabecera">Prioridad</th>
                <th class="cabecera">Fecha Respuesta</th>
                <th class="cabecera">Doc. Padre</th>

            </tr>

            </thead>
            <tbody>

            </tbody>
        </table>

    </span>

</div>


<div style="height: 450px"  class="container-celdas">
    <span class="grupo">
        <table class="table table-bordered table-condensed table-hover">
            <thead>
            <tr>
                %{--<th class="cabecera">Documento</th>--}%
                %{--<th class="cabecera">Fecha Recepción</th>--}%
                %{--<th class="cabecera">De</th>--}%
                %{--<th class="cabecera">Creado Por</th>--}%
                %{--<th class="cabecera">Prioridad</th>--}%
                %{--<th class="cabecera">Fecha Respuesta</th>--}%
                %{--<th class="cabecera">Doc. Padre</th>--}%
            </tr>
            </thead>
            <tbody>
            <g:each in="${pxtTramites}" var="pxt">
                <g:each in="${tramites}" var="tramite">
                    <g:if test="${pxt?.id == tramite?.id}">
                        <tr id="${tramite?.tramite?.id}" data-id="${tramite?.tramite?.id}">
                            <td>${tramite?.tramite?.codigo}</td>
                            <g:if test="${tramite?.tramite?.deDepartamento}">
                                <td>${tramite?.tramite?.deDepartamento?.descripcion}</td>
                            </g:if>
                            <g:else>
                                <td>${tramite?.tramite?.de?.nombre + " " + tramite?.tramite?.de?.apellido}</td>
                            </g:else>
                            <td>${tramite?.tramite?.getPara()?.persona}</td>
                            <td>${tramite?.tramite?.asunto}</td>
                            <td>${tramite?.fechaEnvio?.format('dd-MM-yyyy HH:mm')}</td>
                            <td>${tramite?.fechaRecepcion?.format('dd-MM-yyyy HH:mm')}</td>
                            <g:if test="${tramite?.tramite?.padre}">
                                <td>${tramite?.tramite?.padre?.codigo}</td>
                            </g:if>
                            <g:else>
                                <td>Trámite Padre</td>
                            </g:else>

                        </tr>
                    </g:if>
                </g:each>
            </g:each>
            </tbody>
        </table>
    </span>
</div>

<script type="text/javascript">
    $(function () {
        $("tr").contextMenu({
            items  : createContextMenu,
            onShow : function ($element) {
                $element.addClass("trHighlight");
            },
            onHide : function ($element) {
                $(".trHighlight").removeClass("trHighlight");
            }
        });
    });
</script>





