<g:form action="grabar" method="post">
    <!-- <div style="height: 400px; overflow: auto;"> -->
    <input type="hidden" id="mdlo__id" value="${mdlo__id}">
    <input type="hidden" id="tpac__id" value="${mdlo__id}">
    <g:if test="${datos?.size() > 0}">
        <div class="ui-corner-all" style="height: 500px; overflow:auto; margin-bottom: 5px; margin-left: -20px; background-color: #efeff8;
        border-style: solid; border-color: #AAA; border-width: 1px; ">
            <table border="0" cellpadding="0" width="800px">
                <thead style="color: #101010; background-color: #69b0d3">
                    <tr>
                        <th style="padding:4px;" width="100px">Activado</th>
                        <th "width="200px">Permiso</th>
                        <th "width="300px">Descripción</th>
                    </tr>
                </thead>
                <tbody>
                %{--<hr>Hola ${datos}</hr>--}%
                    <g:each in="${datos}" status="i" var="d">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}" style="background: ${(d[3]) ? '#7cf' : ''}">
                            <td><input type="checkbox" name="cdgo" class="ndm ${d[4]}"
                                       value="${d[0].encodeAsHTML()}" ${(d[3]) ? 'checked' : ''}></td>
                            <td>${d[1]?.encodeAsHTML()}</td>
                            <td>${d[2]?.encodeAsHTML()}</td>
                        </tr>
                    </g:each>
                </tbody>
            </table>
        </div>
    </g:if>
    <input id="aceptaAJX" type="button" class="btn btn-info grabaPrms" value="Fijar permisos del Perfil">
</g:form>

<script type="text/javascript">

    function armarAccn() {
        var datos = new Array()
        $(".ndm:checked").each(
                function () {
                    datos.push($(this).val());
                });
        datos += "&menu=" + $('#mdlo__id').val() + "&grabar=S";
        return datos
    }
    //    $(document).ready(function () {

    function showAlert(msg) {
        bootbox.alert("<div class='alert alert-danger'>" +
                      "<i class='fa fa-warning fa-2x text-danger pull-left'></i> " +
                      msg +
                      "</div>");
    }

    $(".ndm").click(function () {
        /* cambio para el merge
         *  Si es director  no puede ser jefe ni recibir
         *  Si es jefe      no puede ser director ni recibir
         *  Si es editor    no puede ser recibir ni recepcion
         *
         * DIRECTOR  : P001
         * JEFE      : P002
         * RECIBIR   : P010
         * RECEPCION : E001
         * EDITOR    : P016
         */
        var $this = $(this);
        var checked = $this.is(":checked");

        var codDirector = "P001";
        var codJefe = "P002";
        var codRecibir = "P010";
        var codRecepcion = "E001";
        var codEditor = "P016";

        if (checked) {
            // que es lo q se checkeo
            var esDirector = $this.hasClass(codDirector);
            var esJefe = $this.hasClass(codJefe);
            var esRecibir = $this.hasClass(codRecibir);
            var esRecepcion = $this.hasClass(codRecepcion);
            var esEditor = $this.hasClass(codEditor);

            // los q ya estan checkeados
            var tieneDirector = $("." + codDirector).is(":checked");
            var tieneJefe = $("." + codJefe).is(":checked");
            var tieneRecibir = $("." + codRecibir).is(":checked");
            var tieneRecepcion = $("." + codRecepcion).is(":checked");
            var tieneEditor = $("." + codEditor).is(":checked");

            var msg = "";

            if (esDirector) {
                // si tiene jefe y/o recibir no debe checkearse
                if (tieneJefe || tieneRecibir) {
                    msg += "No puede asignar permiso de DIRECTOR ni de RECIBIR a un JEFE";
                }
            }
            if (esJefe) {
                // si tiene director y/o recibir no debe checkearse
                if (tieneDirector || tieneRecibir) {
                    msg += "No puede asignar permiso de JEFE ni de RECIBIR a un DIRECTOR";
                }
            }
            if (esRecibir) {
                // si tiene director, jefe, editor no debe checkearse
                if (tieneDirector || tieneJefe || tieneEditor) {
                    msg += "No puede asignar permiso de RECIBIR a un JEFE ni a un DIRECTOR ni a un EDITOR";
                }
            }
            if (esRecepcion) {
                // si tiene editor no debe checkearse
                if (tieneEditor) {
                    msg += "No puede asignar permiso de RECEPCION a un EDITOR";
                }
            }
            if (esEditor) {
                // si tiene recibir o recepcion no debe checkearse
                if (tieneRecibir || tieneRecepcion) {
                    msg += "No puede asignar permiso de EDITOR si ya tiene permiso de RECIBIR o de RECEPCION";
                }
            }

            if (msg != "") {
                $this.prop("checked", false);
                showAlert(msg);
            }
        }
    });

    $("#aceptar").click(function () {
        alert("ohhhhh")
    });

    $("#aceptaAJX").click(function () {
        bootbox.confirm("Este proceso actualizará los permisos de trámites de todos los usuarios que poseen el perfil: <h4>" +
                        $('#perfil').find("option:selected").text() + "</h4><br/>¿Está usted Seguro?", function (res) {
            if (res) {
                var data = armarAccn();
//                alert("armado: " + data);
                $.ajax({
                    type    : "POST",
                    url     : '${createLink(controller: 'prfl', action:'grabar_perm')}',   // "../grabar",
                    data    : "&ids=" + data + "&tpac=" + $('#tpac__id').val() + "&prfl=" + $('#perfil').val(),
                    success : function (msg) {
//                        $("#ajx").html(msg)
                        var parts = msg.split("_");
                        log(parts[1], parts[0] == "NO" ? "error" : "success");
                    }
                });
            }
        });
    });
    //    });


</script>