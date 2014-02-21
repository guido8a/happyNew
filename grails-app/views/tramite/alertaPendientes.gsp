<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 19/02/14
  Time: 03:25 PM
--%>

<g:if test="${tramitesPendientesRojos > 0}">


    <div data-type="pendiente" class="alert alert-otroRojo alertas" style="width: 270px;">
        <label class="etiqueta" style="padding-top: 10px; padding-left: 10px">${tramitesPendientes} Documentos Pendientes o No Recibidos</label>
    </div>
</g:if>
<g:else>

    <div data-type="pendiente" class="alert alert-blanco alertas" style="width: 270px;">
        <label class="etiqueta" style="padding-top: 10px; padding-left: 10px">${tramitesPendientes} Documentos Pendientes o No Recibidos</label>
    </div>
</g:else>




<script type="text/javascript">
    $(".alertas").click(function() {
        var type=$(this).data("type");
        getRows(type);
    });
</script>