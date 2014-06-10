<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 2/18/14
  Time: 12:39 PM
--%>

<%@ page import="happy.seguridad.Prfl; happy.seguridad.Sesn" contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Configuración personal</title>

        <style type="text/css">
        .table {
            font-size     : 13px;
            width         : auto !important;
            margin-bottom : 0 !important;
        }

        .container-celdasAcc {
            max-height : 200px;
            width      : 804px; /*554px;*/
            overflow   : auto;
        }

        .col100 {
            width : 100px;
        }

        .col200 {
            width : 250px;
        }

        .col300 {
            width : 304px;
        }

        .col-md-1.xs {
            width : 45px;
        }

        </style>

    </head>

    <body>
        %{--<div class="form-group">--}%
        %{--<div class="alert alert-info">--}%
        %{--Datos del usuario: <strong>${usuario.nombre} ${usuario.apellido}</strong>--}%
        %{--</div>--}%
        %{--</div>--}%

        %{--${session.perfil}--}%

        %{--<div class="panel-group" id="accordion">--}%

        %{--<div class="panel panel-default">--}%
        %{--<div class="panel-heading">--}%
        %{--<h4 class="panel-title">--}%
        %{--<a data-toggle="collapse" data-parent="#accordion" href="#collapseAcceso">--}%
        %{--Ausentismo: permisos y vacaciones--}%
        %{--</a>--}%
        %{--</h4>--}%
        %{--</div>--}%

        %{--<div id="collapseAcceso" class="panel-collapse collapse in" style="padding: 10px">--}%
        %{--<div class="panel-body">--}%
        <h4>Ausentismos registrados de ${usuario.login}</h4>

        <div id="divAccesos"></div>
        %{--</div>--}%
        %{--</div>--}%
        %{--</div>--}%
        %{--</div>--}%


        <script type="text/javascript">

            function loadAccesos() {
                var $div = $("#divAccesos");
                $div.html(spinnerSquare64);
                $.ajax({
                    type    : "POST",
                    url     : "${createLink(action:'accesos')}",
                    data    : {
                        id : "${usuario.id}"
                    },
                    success : function (msg) {
                        $div.html(msg);
                    }
                });
            }

            $(function () {
                var $btnAccesos = $("#btnAccesos");
                var $frmAccesos = $("#frmAccesos");
                var $btnPass = $("#btnPass");
                var $frmPass = $("#frmPass");

                loadAccesos();

                $frmAccesos.validate({
                    errorClass     : "help-block",
                    errorPlacement : function (error, element) {
                        if (element.parent().hasClass("input-group")) {
                            error.insertAfter(element.parent());
                        } else {
                            error.insertAfter(element);
                        }
                        element.parents(".grupo").addClass('has-error');
                    },
                    success        : function (label) {
                        label.parents(".grupo").removeClass('has-error');
                    }
                });
                $btnAccesos.click(function () {
                    if ($frmAccesos.valid()) {
                        var url = $frmAccesos.attr("action");
                        var data = "usuario.id=${usuario.id}";
                        data += "&" + $frmAccesos.serialize();
                        $btnAccesos.hide().after(spinner);
                        $.ajax({
                            type    : "POST",
                            url     : url,
                            data    : data,
                            success : function (msg) {
                                var parts = msg.split("_");
//                        console.log(parts)
                                if (parts.length == 3) {
                                    log(parts[1] + ". Usted será desconectado del sistema en 5 segundos", parts[0] == "OK" ? "success" : "error");
                                    spinner.remove();
                                    $("#btnAccesos-svt").show();
                                    $frmAccesos.find("input, textarea").val("");
                                    $("#accsFechaInicial").val("date.struct");
                                    $("#accsFechaFinal").val("date.struct");
                                    loadAccesos();
                                    setInterval(function () {
                                        location.href = "${g.createLink(controller: 'login',action: 'logout')}"
                                    }, 5000);
                                } else {
                                    log(parts[1], parts[0] == "OK" ? "success" : "error");
                                    spinner.remove();
                                    $("#btnAccesos-svt").show();
                                    $frmAccesos.find("input, textarea").val("");
                                    $("#accsFechaInicial").val("date.struct");
                                    $("#accsFechaFinal").val("date.struct");
                                    loadAccesos();
                                }
                            }
                        });
                    }

                    return false;
                });
                $("#btnAccesos-svt").click(function () {
                    if ($frmAccesos.valid()) {
//                console.log($("#nuevo-triangulo").val())
                        if ($("#nuevo-triangulo").val() != "" && !isNaN($("#nuevo-triangulo").val())) {
                            var url = $frmAccesos.attr("action");
                            var data = "usuario.id=${usuario.id}";
                            data += "&" + $frmAccesos.serialize();
                            $("#btnAccesos-svt").hide().after(spinner);
                            $.ajax({
                                type    : "POST",
                                url     : url,
                                data    : data,
                                success : function (msg) {
                                    var parts = msg.split("_");

                                    if (parts.length == 3) {
                                        log(parts[1] + ". Usted será desconectado del sistema en 5 segundos", parts[0] == "OK" ? "success" : "error");
                                        spinner.remove();
                                        $("#btnAccesos-svt").show();
                                        $frmAccesos.find("input, textarea").val("");
                                        $("#accsFechaInicial").val("date.struct");
                                        $("#accsFechaFinal").val("date.struct");
                                        loadAccesos();
                                        setInterval(function () {
                                            location.href = "${g.createLink(controller: 'login',action: 'logout')}"
                                        }, 5000);
                                    } else {
                                        log(parts[1], parts[0] == "OK" ? "success" : "error");
                                        spinner.remove();
                                        $("#btnAccesos-svt").show();
                                        $frmAccesos.find("input, textarea").val("");
                                        $("#accsFechaInicial").val("date.struct");
                                        $("#accsFechaFinal").val("date.struct");
                                        loadAccesos();
                                    }

                                }
                            });
                        } else {
                            bootbox.alert("Debe escoger un usuario para asignarle las funciones de recepcón, en caso de no haber usuarios activos comuniquese con el administrador del sistema.")
                        }

                    }

                    return false;
                });
            })
            ;
        </script>

    </body>
</html>