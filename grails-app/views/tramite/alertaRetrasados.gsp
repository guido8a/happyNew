<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 19/02/14
  Time: 03:26 PM
--%>

<div data-type="retrasado" class="alert alert-danger alertas"  style="width: 190px">
    <label class="etiqueta" style="padding-left: 10px; padding-top: 10px"> ${tramitesAtrasados} Documentos Retrasados</label></div>


<script type="text/javascript">
    $(".alertas").click(function() {
        var type=$(this).data("type");
        getRows(type);
    });
</script>