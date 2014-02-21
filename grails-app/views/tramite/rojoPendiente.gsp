<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 21/02/14
  Time: 12:20 PM
--%>

<div data-type="pendienteRojo" class="alert alert-otroRojo alertas" style="width: 270px;">
    <label class="etiqueta" style="padding-top: 10px; padding-left: 10px">${tramitesPendientes} Documentos Pendientes o No Recibidos</label>
</div>

<script type="text/javascript">
    $(".alertas").click(function() {
        var type=$(this).data("type");
        getRows(type);
    });
</script>