<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main"/>
        <title>Gestión de Permisos, Módulos y Perfiles</title>
    </head>

    <body>

        <div class="btn-toolbar toolbar">
            <div class="btn-group">
                <g:link action="form" class="btn btn-primary btnCrear">
                    <i class="fa fa-file-o"></i> Crear perfil
                </g:link>
                <a href="#" class="btn btn-primary btnEdit">
                    <i class="fa fa-pencil"></i> Editar perfil
                </a>
                <a href="#" class="btn btn-primary btnDelete">
                    <i class="fa fa-trash-o"></i> Eliminar perfil
                </a>
            </div>

            <div class="btn-group">
                <a href="#" class="btn btn-primary btnCrearMdlo">
                    <i class="fa fa-file-o"></i> Crear módulo
                </a>
                <a href="#" class="btn btn-primary btnEditMdlo">
                    <i class="fa fa-pencil"></i> Editar módulo
                </a>
                <a href="#" class="btn btn-primary btnDeleteMdlo">
                    <i class="fa fa-trash-o"></i> Eliminar módulo
                </a>
            </div>
        </div>

        <div id="tipo" class="alert ">
            Selecione el tipo de acción
            <div class="btn-group" data-toggle="buttons">
                <g:each var="tp" in="${happy.seguridad.Tpac.list()}" status="i">
                    <label class="btn btn-primary tipo ${(tp.id == 1) ? 'active' : ''}">
                        <input type="radio" id="tpac${i}" name="tpac" value="${tp.id}" ${(tp.id == 1) ? 'checked=' : ''}> ${tp.tipo}
                    </label>
                </g:each>
            </div>

            <span style="font-size: 10pt; color: black; margin-left: 160px;">Seleccione un Perfil
            <g:select optionKey="id" from="${happy.seguridad.Prfl.list()}" name="perfil" value="${prflInstace?.id}" style="width: 180px;"/>
            </span>
        </div>

        <div class="" id="parm">
            <g:form action="registro" method="post">
                <input type="hidden" id="prfl__id" name="id" value="${prflInstance?.id}"/>

                <h3>Seleccione el módulo y fije los permisos</h3>

                <div class="btn-group" data-toggle="buttons">
                    <g:each in="${lstacmbo}" status="i" var="d">
                        <label class="btn btn-primary modulo">
                            <input type="radio" id="check${i}" name="modulo" value="${d[0]?.encodeAsHTML()}" nombre="${d[1]?.encodeAsHTML()}"> ${d[1]?.encodeAsHTML()}
                        </label>
                    </g:each>
                </div>
                <br>

            </g:form>
            <div id="ajx" style="width:820px; padding-left: 20px; "></div>

            <div id="ajx_prfl" style="width:520px;"></div>

            <div id="ajx_menu" style="width:520px;"></div>

        </div>

        <div id="datosPerfil" class="container entero  ui-corner-bottom">
        </div>


        <script type="text/javascript">
            function submitForm(tipo) {
                var $form = $("#frm");
                var $btn = $("#dlgCreateEdit").find("#btnSave");
                var url = "";
                switch (tipo) {
                    case "perfil":
                        url = '${createLink(controller: 'prfl', action:'save_ajax')}';
                        break;
                    case "modulo":
                        url = '${createLink(controller: 'modulo', action:'save_ajax')}';
                        break;
                }
                if ($form.valid()) {
                    $btn.replaceWith(spinner);
                    $.ajax({
                        type    : "POST",
                        url     : url,
                        data    : $form.serialize(),
                        success : function (msg) {
                            var parts = msg.split("_");
                            log(parts[1], parts[0] == "OK" ? "success" : "error"); // log(msg, type, title, hide)
                            if (parts[0] == "OK") {
                                location.reload(true);
                            } else {
                                spinner.replaceWith($btn);
                                return false;
                            }
                        }
                    });
                } else {
                    return false;
                } //else
            } //submit form
            function deleteRow(itemId, tipo) {
                var url = "", str = "";
                switch (tipo) {
                    case "perfil":
                        url = '${createLink(controller: 'prfl', action:'delete_ajax')}';
                        str = "perfil";
                        break;
                    case "modulo":
                        url = '${createLink(controller: 'modulo', action:'delete_ajax')}';
                        str = "módulo";
                        break;
                }
                bootbox.dialog({
                    title   : "Alerta",
                    message : "<i class='fa fa-trash-o fa-3x pull-left text-danger text-shadow'></i><p>¿Está seguro que desea eliminar el " + str + " seleccionado? Esta acción no se puede deshacer.</p>",
                    buttons : {
                        cancelar : {
                            label     : "Cancelar",
                            className : "btn-primary",
                            callback  : function () {
                            }
                        },
                        eliminar : {
                            label     : "<i class='fa fa-trash-o'></i> Eliminar",
                            className : "btn-danger",
                            callback  : function () {
                                $.ajax({
                                    type    : "POST",
                                    url     : url,
                                    data    : {
                                        id : itemId
                                    },
                                    success : function (msg) {
                                        var parts = msg.split("_");
                                        log(parts[1], parts[0] == "OK" ? "success" : "error"); // log(msg, type, title, hide)
                                        if (parts[0] == "OK") {
                                            location.reload(true);
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
            function createEditRow(id, tipo) {
                var title = id ? "Editar" : "Crear";
                var data = id ? { id : id } : {};
                var url = "", str = "";
                switch (tipo) {
                    case "perfil":
                        url = '${createLink(controller: 'prfl', action:'form_ajax')}';
                        title += " perfil";
                        break;
                    case "modulo":
                        url = '${createLink(controller: 'modulo', action:'form_ajax')}';
                        title += " módulo";
                        break;
                }
                $.ajax({
                    type    : "POST",
                    url     : url,
                    data    : data,
                    success : function (msg) {
                        var b = bootbox.dialog({
                            id      : "dlgCreateEdit",
                            title   : title,
                            message : msg,
                            buttons : {
                                cancelar : {
                                    label     : "Cancelar",
                                    className : "btn-primary",
                                    callback  : function () {
                                    }
                                },
                                guardar  : {
                                    id        : "btnSave",
                                    label     : "<i class='fa fa-save'></i> Guardar",
                                    className : "btn-success",
                                    callback  : function () {
                                        return submitForm(tipo);
                                    } //callback
                                } //guardar
                            } //buttons
                        }); //dialog
                        setTimeout(function () {
                            b.find(".form-control").first().focus()
                        }, 500);
                    } //success
                }); //ajax
            } //createEdit

            $(function () {
                $( document ).ready(function() {
                    $("#check0").click();
                });

                $("#perfil").change(function () {
                            $("#check0").click();
                        }
                )

                $(".btnCrear").click(function () {
                    createEditRow(null, "perfil");
                    return false;
                });
                $(".btnEdit").click(function () {
                    createEditRow($("#perfil").val(), "perfil");
                    return false;
                });
                $(".btnDelete").click(function () {
                    deleteRow($("#perfil").val(), "perfil");
                    return false;
                });
                $(".btnCrearMdlo").click(function () {
                    createEditRow(null, "modulo");
                    return false;
                });
                $(".btnEditMdlo").click(function () {
                    createEditRow($(".modulo.active").find("input").val(), "modulo");
                    return false;
                });
                $(".btnDeleteMdlo").click(function () {
                    deleteRow($(".modulo.active").find("input").val(), "modulo");
                    return false;
                });

                $(".modulo").click(function () {
                    setTimeout(function () {
                        $.ajax({
                            type    : "POST",
                            url     : "${createLink(action:'ajaxPermisos')}",
                            data    : {
                                ids  : $(".modulo.active").find("input").val(),
                                prfl : $('#perfil').val(),
                                tpac : $(".tipo.active").find("input").val()
                            },
                            success : function (msg) {
                                $("#ajx").html(msg)
                            }
                        });
                    }, 1);
                });

            });
        </script>

    </body>
</html>