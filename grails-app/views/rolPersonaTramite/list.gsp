<%@ page import="happy.tramites.RolPersonaTramite" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Rol de Personas</title>
    </head>

    <body>

        <elm:flashMessage tipo="${flash.tipo}" clase="${flash.clase}">${flash.message}</elm:flashMessage>

        <!-- botones -->
        <div class="btn-toolbar toolbar">
            <div class="btn-group">
                <g:link action="form" class="btn btn-default btnCrear">
                    <i class="fa fa-file-o"></i> Crear
                </g:link>
            </div>
        <g:link class="btn btn-default col-md-2" style="width: 100px;" controller="inicio" action="parametros"><i class="fa fa-arrow-left"></i> Regresar</g:link>

        <div class="btn-group pull-right col-md-3">
                <div class="input-group">
                    <input type="text" class="form-control span2 input-search" placeholder="Buscar" value="${params.search}">
                    <span class="input-group-btn">
                        <g:link action="list" class="btn btn-default btn-search">
                            <i class="fa fa-search"></i>&nbsp;
                        </g:link>
                    </span>
                </div><!-- /input-group -->
            </div>
        </div>

        <table class="table table-condensed table-bordered">
            <thead>
                <tr>

                    <g:sortableColumn property="codigo" title="Código"/>

                    <g:sortableColumn property="descripcion" title="Descripción"/>

                </tr>
            </thead>
            <tbody>
                <g:each in="${rolPersonaTramiteInstanceList}" status="i" var="rolPersonaTramiteInstance">
                    <tr data-id="${rolPersonaTramiteInstance.id}">

                        <td><elm:textoBusqueda texto='${fieldValue(bean: rolPersonaTramiteInstance, field: "codigo")}' search='${params.search}' /></td>

                        <td><elm:textoBusqueda texto='${fieldValue(bean: rolPersonaTramiteInstance, field: "descripcion")}' search='${params.search}' /></td>

                    </tr>
                </g:each>
            </tbody>
        </table>

        <elm:pagination total="${rolPersonaTramiteInstanceCount}" params="${params}"/>

        <script type="text/javascript">
            var id = null;
            function submitForm() {
                var $form = $("#frmRolPersonaTramite");
                var $btn = $("#dlgCreateEdit").find("#btnSave");
                if ($form.valid()) {
                    $btn.replaceWith(spinner);
                    openLoader("Grabando");
                    $.ajax({
                        type    : "POST",
                        url     : '${createLink(action:'save_ajax')}',
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
            }
            function deleteRow(itemId) {
                bootbox.dialog({
                    title   : "Alerta",
                    message : "<i class='fa fa-trash-o fa-3x pull-left text-danger text-shadow'></i><p>¿Está seguro que desea eliminar el Rol de Persona seleccionado? Esta acción no se puede deshacer.</p>",
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
                                openLoader("Eliminando");
                                $.ajax({
                                    type    : "POST",
                                    url     : '${createLink(action:'delete_ajax')}',
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
            function createEditRow(id) {
                var title = id ? "Editar" : "Crear";
                var data = id ? { id : id } : {};
                $.ajax({
                    type    : "POST",
                    url     : "${createLink(action:'form_ajax')}",
                    data    : data,
                    success : function (msg) {
                        var b = bootbox.dialog({
                            id      : "dlgCreateEdit",
                            title   : title + " Rol de Persona",
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
                                        return submitForm();
                                    } //callback
                                } //guardar
                            } //buttons
                        }); //dialog
                        setTimeout(function () {
                            b.find(".form-control").not(".datepicker").first().focus()
                        }, 500);
                    } //success
                }); //ajax
            } //createEdit

            $(function () {

                $(".btnCrear").click(function () {
                    createEditRow();
                    return false;
                });

                context.settings({
                    onShow : function (e) {
                        $("tr.success").removeClass("success");
                        var $tr = $(e.target).parent();
                        $tr.addClass("success");
                        id = $tr.data("id");
                    }
                });
                context.attach('tbody>tr', [
                    {
                        header : 'Acciones'
                    },
                    {
                        text   : 'Ver',
                        icon   : "<i class='fa fa-search'></i>",
                        action : function (e) {
                            $("tr.success").removeClass("success");
                            e.preventDefault();
                            $.ajax({
                                type    : "POST",
                                url     : "${createLink(action:'show_ajax')}",
                                data    : {
                                    id : id
                                },
                                success : function (msg) {
                                    bootbox.dialog({
                                        title   : "Ver Rol de Persona",
                                        message : msg,
                                        buttons : {
                                            ok : {
                                                label     : "Aceptar",
                                                className : "btn-primary",
                                                callback  : function () {
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    },
                    {
                        text   : 'Editar',
                        icon   : "<i class='fa fa-pencil'></i>",
                        action : function (e) {
                            $("tr.success").removeClass("success");
                            e.preventDefault();
                            createEditRow(id);
                        }
                    },
                    {divider : true},
                    {
                        text   : 'Eliminar',
                        icon   : "<i class='fa fa-trash-o'></i>",
                        action : function (e) {
                            $("tr.success").removeClass("success");
                            e.preventDefault();
                            deleteRow(id);
                        }
                    }
                ]);
            });
        </script>

    </body>
</html>
