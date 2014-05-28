
<%@ page import="happy.utilitarios.Parametros" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Lista de Parámetros</title>
    </head>
    <body>

    <div class="btn-toolbar toolbar">
        <div class="btn-group">
            <g:link controller="inicio" action="parametros" class="btn btn-default">
                <i class="fa fa-arrow-left"></i> Regresar
            </g:link>
        </div>
    </div>

    <h3> Parámetros del Sistema</h3>

    <elm:flashMessage tipo="${flash.tipo}" clase="${flash.clase}">${flash.message}</elm:flashMessage>

        <table class="table table-condensed table-bordered table-striped">
            <thead>
                <tr>
                    
                    <g:sortableColumn property="horaInicio" title="Hora Inicio" />
                    
                    %{--<g:sortableColumn property="minutoInicio" title="Minuto Inicio" />--}%
                    
                    <g:sortableColumn property="horaFin" title="Hora Fin" />
                    
                    %{--<g:sortableColumn property="minutoFin" title="Minuto Fin" />--}%
                    
                    <g:sortableColumn property="ipLDAP" title="IP y puerto LDAP" />
                    
                    <g:sortableColumn property="ouPrincipal" title="Imágenes" />
                    
                </tr>
            </thead>
            <tbody>
                <g:each in="${parametrosInstanceList}" status="i" var="parametrosInstance">
                    <tr data-id="${parametrosInstance.id}">

                        <td>${parametrosInstance.horaInicio.toString().padLeft(2,'0')}:${parametrosInstance.minutoInicio.toString().padLeft(2,'0')}</td>
                        
                        %{--<td>${fieldValue(bean: parametrosInstance, field: "minutoInicio")}</td>--}%

                        <td>${parametrosInstance.horaFin.toString().padLeft(2,'0')}:${parametrosInstance.minutoFin.toString().padLeft(2,'0')}</td>
                        
                        %{--<td>${fieldValue(bean: parametrosInstance, field: "minutoFin")}</td>--}%
                        
                        <td>${fieldValue(bean: parametrosInstance, field: "ipLDAP")}</td>
                        
                        <td>${fieldValue(bean: parametrosInstance, field: "imagenes")}</td>
                        
                    </tr>
                </g:each>
            </tbody>
        </table>

        <elm:pagination total="${parametrosInstanceCount}" params="${params}"/>

        <script type="text/javascript">
            var id = null;
            function submitForm() {
                var $form = $("#frmParametros");
                var $btn = $("#dlgCreateEdit").find("#btnSave");
                if ($form.valid()) {
                $btn.replaceWith(spinner);
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
                    message : "<i class='fa fa-trash-o fa-3x pull-left text-danger text-shadow'></i><p>¿Está seguro que desea eliminar el Parámetro seleccionado? Esta acción no se puede deshacer.</p>",
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
                var data = id ? { id: id } : {};
                $.ajax({
                    type    : "POST",
                    url     : "${createLink(action:'form_ajax')}",
                    data    : data,
                    success : function (msg) {
                        var b = bootbox.dialog({
                            id      : "dlgCreateEdit",
                            title   : title + " Parametros",
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
                            b.find(".form-control").first().focus()
                        }, 500);
                    } //success
                }); //ajax
            } //createEdit

            $(function () {

                $(".btnCrear").click(function() {
                    createEditRow();
                    return false;
                });

                $("tr").contextMenu({
                    items  : {
                        header   : {
                            label  : "Acciones",
                            header : true
                        },
                        ver      : {
                            label  : "Ver",
                            icon   : "fa fa-search",
                            action : function ($element) {
                                var id = $element.data("id");
                                $.ajax({
                                    type    : "POST",
                                    url     : "${createLink(action:'show_ajax')}",
                                    data    : {
                                        id : id
                                    },
                                    success : function (msg) {
                                        bootbox.dialog({
                                            title   : "Ver Parámetros",
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
                        editar   : {
                            label  : "Editar",
                            icon   : "fa fa-pencil",
                            action : function ($element) {
                                var id = $element.data("id");
                                createEditRow(id);
                            }
                        }/*,
                        eliminar : {
                            label            : "Eliminar",
                            icon             : "fa fa-trash-o",
                            separator_before : true,
                            action           : function ($element) {
                                var id = $element.data("id");
                                deleteRow(id);
                            }
                        }*/
                    },
                    onShow : function ($element) {
                        $element.addClass("trHighlight");
                    },
                    onHide : function ($element) {
                        $(".trHighlight").removeClass("trHighlight");
                    }
                });
            });
        </script>

    </body>
</html>
