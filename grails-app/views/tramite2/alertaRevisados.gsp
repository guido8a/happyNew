<div data-type="revisado" class="alert alert-success alertas" style="margin-left: 40px; width: 150px"><label
        class="etiqueta" style="padding-top: 10px; padding-left: 30px">${tramites} Revisados</label></div>


<script type="text/javascript">
    $(".alertas").click(function() {
        var type=$(this).data("type");
        getRows(type);
    });
</script>