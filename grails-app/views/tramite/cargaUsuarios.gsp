<g:if test="${users.size() > 0}">
    Usuario <g:select from="${users}" name="usuario" id="usuario" class="form-control required" optionKey="id" noSelection="['': 'Seleccione el usuario']"/>

    <script type='text/javascript'>
        var $usu = $("#usuario");
        $usu.change(function () {
            var id = $(this).val();
            var $div = $("#divBtnUsu");
            if (id != "" && $div.children().length == 0) {
                var $btn = $("<a href='#' class='btn btn-xs btn-primary'>Agregar usuario</a>");
                $div.html($btn);

                $btn.click(function () {
                    addItem($usu, "usuario");
                    return false;
                });

            }
            if (id == "") {
                $div.html("");
            }
        });
    </script>
</g:if>