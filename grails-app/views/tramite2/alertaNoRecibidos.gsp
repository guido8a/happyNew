<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 24/02/14
  Time: 01:08 PM
--%>

<div data-type="noRecibido" class="alert alert-danger alertas"
     style="padding-left: 30px; padding-top: 10px; width: 150px"><label class="etiqueta">${tramitesNoRecibidos} No recibido</label></div>

<script type="text/javascript">
    $(".alertas").click(function() {
        var type=$(this).data("type");
        getRows(type);
    });
</script>