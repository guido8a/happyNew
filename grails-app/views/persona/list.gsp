<%@ page import="happy.seguridad.Prfl; happy.tramites.RolPersonaTramite; happy.tramites.PermisoUsuario; happy.seguridad.Sesn; happy.tramites.PersonaDocumentoTramite; happy.tramites.Tramite; happy.tramites.ObservacionTramite; happy.seguridad.Accs; happy.seguridad.Persona" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Personal GADPP</title>

        <style type="text/css">
        .table {
            font-size     : 12px;
            margin-bottom : 0 !important;
        }

        .perfiles option:first-child {
            font-weight : normal !important;
        }
        </style>
    </head>

    <body>

        <g:set var="iconActivar" value="fa-hdd-o"/>
        <g:set var="iconDesactivar" value="fa-power-off"/>

        <elm:flashMessage tipo="${flash.tipo}" clase="${flash.clase}">${flash.message}</elm:flashMessage>

        <!-- botones -->
        <div class="btn-toolbar toolbar">
            <div class="btn-group">
                <g:link action="form" class="btn btn-default btnCrear">
                    <i class="fa fa-file-o"></i> Crear
                </g:link>
            </div>

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

        <table class="table table-condensed table-bordered" width='100%'>
            <thead>
                <tr>
                    <th style="width:10px;"></th>
                    <g:sortableColumn property="login" title="Usuario" params="${params}"/>
                    <g:sortableColumn property="nombre" title="Nombre" params="${params}"/>
                    <g:sortableColumn property="apellido" title="Apellido" params="${params}"/>
                    <g:sortableColumn property="departamento" title="Departamento" params="${params}"/>
                    <th style="width: 220px;">
                        <g:select name="perfil" from="${Prfl.list([sort: 'nombre'])}" optionKey="id" optionValue="nombre"
                                  class="form-control input-sm perfiles" noSelection="['': 'Todos los perfiles']" value="${params.perfil}"/>
                    </th>
                    %{--<th>Autoridad</th>--}%
                </tr>
            </thead>
            <tbody>
                <g:each in="${personaInstanceList}" status="i" var="personaInstance">
                    <g:set var="del" value="${true}"/>
                    <g:if test="${Tramite.countByDe(personaInstance) > 0}">
                        <g:set var="del" value="${false}"/>
                    </g:if>
                    <g:if test="${PersonaDocumentoTramite.countByPersona(personaInstance) > 0}">
                        <g:set var="del" value="${false}"/>
                    </g:if>
                    <g:if test="${ObservacionTramite.countByPersona(personaInstance) > 0}">
                        <g:set var="del" value="${false}"/>
                    </g:if>
                    <g:if test="${Accs.countByUsuarioOrAsignadoPor(personaInstance, personaInstance) > 0}">
                        <g:set var="del" value="${false}"/>
                    </g:if>
                    <g:if test="${Sesn.countByUsuario(personaInstance) > 0}">
                        <g:set var="del" value="${false}"/>
                    </g:if>
                    <g:if test="${PermisoUsuario.countByPersonaOrAsignadoPor(personaInstance, personaInstance) > 0}">
                        <g:set var="del" value="${false}"/>
                    </g:if>

                    <g:set var="rolPara" value="${RolPersonaTramite.findByCodigo('R001')}"/>
                    <g:set var="rolCopia" value="${RolPersonaTramite.findByCodigo('R002')}"/>
                    <g:set var="rolImprimir" value="${RolPersonaTramite.findByCodigo('I005')}"/>

                    <g:set var="tramites" value="${PersonaDocumentoTramite.findAll("from PersonaDocumentoTramite as p inner join fetch p.tramite as tramites where p.persona=${personaInstance.id} and p.rolPersonaTramite in (${rolPara.id + "," + rolCopia.id + "," + rolImprimir.id}) and p.fechaEnvio is not null and tramites.estadoTramite in (3,4) order by p.fechaEnvio desc ")}"/>

                    <tr data-id="${personaInstance.id}" data-tramites="${tramites.size()}" class="${personaInstance.activo == 1 ? 'activo' : 'inactivo'} ${del ? 'eliminar' : ''}">
                        <td>
                            <i class="fa fa-user text-${personaInstance.activo == 0 ? 'muted' : personaInstance.jefe == 1 ? 'warning' : 'info'}"></i>
                        </td>
                        <td><elm:textoBusqueda texto='${fieldValue(bean: personaInstance, field: "login")}' search='${params.search}'/></td>
                        <td><elm:textoBusqueda texto='${fieldValue(bean: personaInstance, field: "nombre")}' search='${params.search}'/></td>
                        <td><elm:textoBusqueda texto='${fieldValue(bean: personaInstance, field: "apellido")}' search='${params.search}'/></td>
                        <td><elm:textoBusqueda texto='${personaInstance.departamento?.descripcion}' search='${params.search}'/></td>
                        <td>${Sesn.withCriteria {
                            eq("usuario", personaInstance)
                            perfil {
                                order("nombre")
                            }
                        }.perfil.nombre.join(", ")}</td>
                        %{--<td>${personaInstance.jefe == 1 ? "SI" : "NO"}</td>--}%
                    </tr>
                </g:each>
            </tbody>
        </table>

        <elm:pagination total="${personaInstanceCount}" params="${params}"/>

        <script type="text/javascript">
            var id = null, tramites = 0;
            function submitForm() {
                var $form = $("#frmPersona");
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
                            if (parts[0] != "INFO") {
                                log(parts[1], parts[0] == "OK" ? "success" : "error"); // log(msg, type, title, hide)
                                if (parts[0] == "OK") {
                                    location.reload(true);
                                } else {
                                    spinner.replaceWith($btn);
                                    return false;
                                }
                            } else {
                                closeLoader();
                                bootbox.dialog({
                                    title   : "Alerta",
                                    message : "<i class='fa fa-warning fa-3x pull-left text-warning text-shadow'></i>" + parts[1],
                                    buttons : {
                                        cancelar : {
                                            label     : "Cancelar",
                                            className : "btn-primary",
                                            callback  : function () {
                                            }
                                        },
                                        aceptar  : {
                                            label     : "<i class='fa fa-thumbs-o-up '></i> Continuar",
                                            className : "btn-success",
                                            callback  : function () {
                                                var $sel = $("#selWarning");
                                                var resp = $sel.val();
                                                var dpto = $sel.data("dpto");
                                                if (resp == 1 || resp == "1") {
                                                    openLoader("Cambiando");
                                                    $.ajax({
                                                        type    : "POST",
                                                        url     : '${createLink(action:'cambioDpto_ajax')}',
                                                        data    : {
                                                            id   : id,
                                                            dpto : dpto
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
                                    }
                                });
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
                    message : "<i class='fa fa-trash-o fa-3x pull-left text-danger text-shadow'></i>" +
                              "<p>¿Está seguro que desea eliminar la Persona seleccionada? Esta acción no se puede deshacer.</p>",
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
            function cambiarEstadoRow(itemId, activar, tramites) {
                var icon, textMsg, textBtn, textLoader, url, clase;
                if (activar) {
                    clase = "success";
                    icon = "${iconActivar}";
                    textMsg = "<p>¿Está seguro que desea activar la persona seleccionada?</p>";
                    textBtn = "Activar";
                    textLoader = "Activando";
                    url = "${createLink(action:'activar_ajax')}";
                } else {
                    clase = "danger";
                    icon = "${iconDesactivar}";
                    textMsg = "<p>¿Está seguro que desea desactivar la persona seleccionada?</p>"
                    if (tramites > 0) {
                        textMsg += "<p>" + tramites + " trámite" + (tramites == 1 ? '' : 's') + " será" + (tramites == 1 ? '' : 'n') + " " +
                                   "redireccionados de su bandeja de entrada personal a la bandeja de entrada de la oficina.</p>";
                    } else {
                        textMsg += "<p>No tiene trámites en su bandeja de entrada personal.</p>"
                    }
                    textBtn = "Desactivar";
                    textLoader = "Desactivando";
                    url = "${createLink(action:'desactivar_ajax')}";
                }
                bootbox.dialog({
                    title   : "Alerta",
                    message : "<i class='fa " + icon + " fa-3x pull-left text-" + clase + " text-shadow'></i>" + textMsg,
                    buttons : {
                        cancelar : {
                            label     : "Cancelar",
                            className : "btn-primary",
                            callback  : function () {
                            }
                        },
                        eliminar : {
                            label     : "<i class='fa " + icon + "'></i> " + textBtn,
                            className : "btn-" + clase,
                            callback  : function () {
                                openLoader(textLoader);
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
                var title = id ? "Editar " : "Crear ";
                var data = id ? { id : id } : {};

                var url = "";
                switch (tipo) {
                    case "persona":
                        url = "${createLink(action:'form_ajax')}";
                        break;
                    case "usuario":
                        url = "${createLink(action:'formUsuario_ajax')}";
                        break;
                }

                $.ajax({
                    type    : "POST",
                    url     : url,
                    data    : data,
                    success : function (msg) {
                        var b = bootbox.dialog({
                            id      : "dlgCreateEdit",
                            class   : "long",
                            title   : title + tipo,
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

                $("#perfil").change(function () {
                    openLoader();
                    var params = "${params}";
                    var id = $(this).val();
                    var strParams = "";
                    params = str_replace('[', '', params);
                    params = str_replace(']', '', params);
                    params = str_replace(':', '=', params);
                    params = params.split(",");
                    for (var i = 0; i < params.length; i++) {
                        params[i] = $.trim(params[i]);
                        if (params[i].startsWith("perfil")) {
                            params[i] = "perfil=" + id;
                        }
                        if (!params[i].startsWith("action") && !params[i].startsWith("controller") && !params[i].startsWith("format") && !params[i].startsWith("offset")) {
                            strParams += params[i] + "&"
                        }
                    }
                    location.href = "${createLink(action: 'list')}?" + strParams
                });

                $(".btnCrear").click(function () {
                    createEditRow(null, "persona");
                    return false;
                });

                context.settings({
                    onShow : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        var $tr = $(e.target).parent();
                        $tr.addClass("trHighlight");
                        id = $tr.data("id");
                        tramites = $tr.data("tramites");
                    }
                });

                var ver = {
                    text   : 'Ver',
                    icon   : "<i class='fa fa-search'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();
                        $.ajax({
                            type    : "POST",
                            url     : "${createLink(action:'show_ajax')}",
                            data    : {
                                id : id
                            },
                            success : function (msg) {
                                bootbox.dialog({
                                    title   : "Ver Persona",
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
                };
                var editar = {
                    text   : 'Editar',
                    icon   : "<i class='fa fa-pencil'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();
                        createEditRow(id, "persona");
                    }
                };
                var config = {
                    text   : 'Perfiles',
                    icon   : "<i class='fa fa-gears'></i>",
                    action : function (e) {
                        location.href = "${createLink(action: 'config')}/" + id;
                    }

                };
                var ausentismo = {
                    text   : 'Ausentismo',
                    icon   : "<i class='fa fa-gears'></i>",
                    action : function (e) {
                        location.href = "${createLink(action: 'ausentismo')}/" + id;
                    }

                };
                var desactivar = {
                    text   : 'Desactivar',
                    icon   : "<i class='fa ${iconDesactivar}'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();
                        cambiarEstadoRow(id, false, tramites);
                    }
                };
                var activar = {
                    text   : 'Activar',
                    icon   : "<i class='fa ${iconActivar}'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();
                        cambiarEstadoRow(id, true, tramites);
                    }
                };
                var eliminar = {
                    text   : 'Eliminar',
                    icon   : "<i class='fa fa-trash-o'></i>",
                    action : function (e) {
                        $("tr.trHighlight").removeClass("trHighlight");
                        e.preventDefault();
                        deleteRow(id);
                    }
                };

                context.attach('.activo', [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    editar,
                    {divider : true},
                    config,
                    ausentismo,
                    {divider : true},
                    desactivar
                ]);

                context.attach('.activo.eliminar', [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    editar,
                    {divider : true},
                    config,
                    ausentismo,
                    {divider : true},
                    desactivar,
                    eliminar
                ]);

                context.attach('.inactivo', [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    editar,
                    {divider : true},
                    activar
                ]);

                context.attach('.inactivo.eliminar', [
                    {
                        header : 'Acciones'
                    },
                    ver,
                    editar,
                    {divider : true},
                    activar,
                    eliminar
                ]);
            });
        </script>

    </body>
</html>
