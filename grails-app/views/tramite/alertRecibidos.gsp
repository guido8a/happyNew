<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 19/02/14
  Time: 12:53 PM
--%>

<div data-type="recibido" class="alert alert-info alertas" style="margin-left: 40px; width: 190px;">
    <label  class="etiqueta" style="padding-top: 10px; padding-left: 10px"> ${tramitesRecibidos} Documentos Recibidos</label>
</div>



<script type="text/javascript">
    $(".alertas").click(function() {
        var type=$(this).data("type");
        getRows(type);
    });
</script>