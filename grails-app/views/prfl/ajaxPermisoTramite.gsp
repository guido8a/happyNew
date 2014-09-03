<g:form action="grabar" method="post">
    <!-- <div style="height: 400px; overflow: auto;"> -->
    <input type="hidden" id="mdlo__id" value="${mdlo__id}">
    <input type="hidden" id="tpac__id" value="${mdlo__id}">
    <g:if test="${datos?.size() > 0}">
        <div class="ui-corner-all" style="height: 500px; overflow:auto; margin-bottom: 5px; margin-left: -20px; background-color: #efeff8;
        border-style: solid; border-color: #AAA; border-width: 1px; ">
            <table border="0" cellpadding="0" width="900px">
                <thead style="color: #101010; background-color: #69b0d3">
                    <tr>
                        <th style="padding:4px;" width="60px">Activado</th>
                        <th width="150px" style="text-align: center">Permiso</th>
                        <th width="690px" style="text-align: center">Descripción</th>
                    </tr>
                </thead>
                <tbody>
                %{--<hr>Hola ${datos}</hr>--}%
                    <g:each in="${datos}" status="i" var="d">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}" style="background: ${(d[3]) ? '#7cf' : ''}">
                            <td style="text-align: center"><input type="checkbox" name="cdgo" class="ndm ${d[4]}"
                                                                  value="${d[0].encodeAsHTML()}" ${(d[3]) ? 'checked' : ''}>
                            </td>
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
         *
         * Solo cuando es administrador puede anular, asociar, reactivar, redireccionar
         *
         * ADMINISTRACION   : P013
         * ANULAR           : P009
         * ASOCIAR          : P014
         * REACTIVAR        : P012
         * REDIRECCIONAR    : P008
         */
        var $this = $(this);
        var checked = $this.is(":checked");

        var codDirector = "P001";
        var codJefe = "P002";
        var codRecibir = "P010";
        var codRecepcion = "E001";
        var codEditor = "P016";

        var codAdmin = "P013";
        var codAnular = "P009";
        var codAsociar = "P014";
        var codReactivar = "P012";
        var codRedirect = "P008";

        var finalChecked = false;
        var msg = "";

        // que es lo q se checkeo
        var esAdmin = $this.hasClass(codAdmin);
        var esAnular = $this.hasClass(codAnular);
        var esAsociar = $this.hasClass(codAsociar);
        var esReactivar = $this.hasClass(codReactivar);
        var esRedirect = $this.hasClass(codRedirect);

        // los q ya estan checkeados
        var tieneAdmin = $("." + codAdmin).is(":checked");
        var tieneAnular = $("." + codAnular).is(":checked");
        var tieneAsociar = $("." + codAsociar).is(":checked");
        var tieneReactivar = $("." + codReactivar).is(":checked");
        var tieneRedirect = $("." + codRedirect).is(":checked");

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
                    msg += "No puede asignar permiso de RECEPCIÓN a un EDITOR";
                }
            }
            if (esEditor) {
                // si tiene recibir o recepcion no debe checkearse
                if (tieneRecibir || tieneRecepcion) {
                    msg += "No puede asignar permiso de EDITOR si ya tiene permiso de RECIBIR o de RECEPCIÓN";
                }
            }

            if (esAnular || esAsociar || esReactivar || esRedirect) {
                // si no tiene admin no debe chechearse
                if (!tieneAdmin) {
                    msg += "No puede asignar permiso de ANULAR, ASOCIAR, REACTIVAR, REDIRECCIONAR si no tiene permiso de ADMINISTRACIÓN";
                }
            }

            if (msg != "") {
                $this.prop("checked", finalChecked);
                showAlert(msg);
            }
        } else {
            if (esAdmin) {
                // si tiene anular, asociar, reactivar o redirect no debe descheckearse
                if (tieneAnular || tieneAsociar || tieneReactivar || tieneRedirect) {
                    msg += "No puede quitar el permiso de ADMINISTRACIÓN si tiene permiso de ANULAR, ASOCIAR, REACTIVAR o REDIRECCIONAR";
                    finalChecked = true;
                }
            }
            if (msg != "") {
                $this.prop("checked", finalChecked);
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