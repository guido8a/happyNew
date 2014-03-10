<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 24/02/14
  Time: 12:18 PM
--%>

<div data-type="enviado" class="alert alert-blanco alertas" style="width: 150px;"><p
        class="etiqueta" style="padding-top: 10px; padding-left: 40px">${tramites} Enviados</p></div>


<script type="text/javascript">
    $(".alertas").click(function() {
        var type=$(this).data("type");
        getRows(type);
    });
</script>