<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 3/17/14
  Time: 3:13 PM
--%>

<%@ page import="happy.tramites.Departamento" contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Departamentos</title>

        <script src="${resource(dir: 'js/plugins/jstree-e22db21/dist', file: 'jstree.min.js')}"></script>
        <link href="${resource(dir: 'js/plugins/jstree-e22db21/dist/themes/default', file: 'style.min.css')}" rel="stylesheet">

        <style type="text/css">

        #list-cuenta {
            width : 950px;
        }

        #tree {
            background : #DEDEDE;
            overflow-y : auto;
            height     : 600px;
        }

        .jstree-search {
            color : #5F87B2 !important;
        }

        .leyenda {
            background    : #ddd;
            border        : solid 1px #aaa;
            padding-left  : 5px;
            padding-right : 5px;
        }
        </style>

    </head>

    <body>
        <g:set var="iconActivar" value="fa-hdd-o"/>
        <g:set var="iconDesactivar" value="fa-power-off"/>

        <div id="list-cuenta">

            <!-- botones -->
            <div class="btn-toolbar toolbar">
                <div class="btn-group">
                    <g:link controller="inicio" action="parametros" class="btn btn-default">
                        <i class="fa fa-arrow-left"></i> Regresar
                    </g:link>
                </div>

                <div class="btn-group" style="margin-top: 4px;">
                    <g:link action="arbol" params="[sort: 'nombre']" class="btn btn-sm btn-info">
                        <i class="fa fa-sort-alpha-asc"></i> Ordenar por nombre
                    </g:link>
                    <g:link action="arbol" params="[sort: 'apellido']" class="btn btn-sm btn-info">
                        <i class="fa fa-sort-alpha-asc"></i> Ordenar por apellido
                    </g:link>
                </div>

                <div class="btn-group" style="margin-top: 4px;">
                    <g:link controller="persona" action="cargarUsuariosLdap" class="btn btn-sm btn-primary">
                        <i class="fa fa-users"></i> Usuarios LDAP
                    </g:link>
                </div>

                <div class="btn-group" style="margin-top: 4px;">
                    <div class="input-group">
                        <g:textField name="search" class="form-control input-sm"/>
                        <span class="input-group-btn">
                            <a href="#" id="btnSearch" class="btn btn-sm btn-info" type="button">
                                <i class="fa fa-search"></i>&nbsp;
                            </a>
                        </span>
                    </div><!-- /input-group -->
                </div>

                <div class="btn-group pull-right ui-corner-all leyenda">
                    <i class="fa fa-user text-info"></i> Usuario<br/>
                    <i class="fa fa-user text-warning"></i> Autoridad
                </div>
            </div>

            <div id="loading" class="text-center">
                <p>
                    Cargando los departamentos
                </p>

                <p>
                    <img src="${resource(dir: 'images/spinners', file: 'loading_new.GIF')}" alt='Cargando...'/>
                </p>

                <p>
                    Por favor espere
                </p>
            </div>

            <div id="tree" class="hide">

            </div>
        </div>

        <elm:select name="selDptoOrig" from="${Departamento.findAllByActivo(1, [sort: 'descripcion'])}"
                    optionKey="id" optionValue="descripcion" optionClass="id" class="form-control hide"/>

        <script type="text/javascript">

            var index = 0;

            var $btnCloseModal = $('<button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>');
            var $btnSave = $('<button type="button" class="btn btn-success"><i class="fa fa-save"></i> Guardar</button>');

            function submitForm() {
                var $form = $("#frmDepartamento");
                var $btn = $("#dlgCreateEdit").find("#btnSave");
                if ($form.valid()) {
                    $btn.replaceWith(spinner);
                    openLoader("Grabando");
                    $.ajax({
                        type    : "POST",
                        url     : $form.attr("action"),
                        data    : $form.serialize(),
                        success : function (msg) {
                            var parts = msg.split("_");
                            log(parts[1], parts[0] == "OK" ? "success" : "error"); // log(msg, type, title, hide)
                            if (parts[0] == "OK") {
                                location.reload(true);
                            } else {
                                closeLoader();
                                spinner.replaceWith($btn);
                                return false;
                            }
                        }
                    });
                } else {
                    return false;
                } //else
            }
            function submitFormPersona(id) {
                var $form = $("#frmPersona");
                var $btn = $("#dlgCreateEditPersona").find("#btnSave");
                if ($form.valid()) {
                    $btn.replaceWith(spinner);
                    openLoader("Grabando");
                    $.ajax({
                        type    : "POST",
                        url     : $form.attr("action"),
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
                                                        url     : '${createLink(controller: 'persona', action:'cambioDpto_ajax')}',
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

            function createEditRow(id, tipo) {
                var data = tipo == "Crear" ? { padre : id} : {id : id};
                $.ajax({
                    type    : "POST",
                    url     : "${createLink(action:'form_ajax')}",
                    data    : data,
                    success : function (msg) {
                        var b = bootbox.dialog({
                            id      : "dlgCreateEdit",
//                            class   : "long",
                            title   : tipo + " Departamento",
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
                            var $input = b.find(".form-control").not(".datepicker").first();
                            var val = $input.val();
                            $input.focus();
                            $input.val("");
                            $input.val(val);
                        }, 500);
                    } //success
                }); //ajax
            } //createEdit

            function createEditRowPersona(id, tipo) {
                var data = tipo == "Crear" ? { 'departamento.id' : id} : {id : id};
                $.ajax({
                    type    : "POST",
                    url     : "${createLink(controller: 'persona', action:'form_ajax')}",
                    data    : data,
                    success : function (msg) {
                        var b = bootbox.dialog({
                            id      : "dlgCreateEditPersona",
                            class   : "long",
                            title   : tipo + " Persona",
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
                                        return submitFormPersona(id);
                                    } //callback
                                } //guardar
                            } //buttons
                        }); //dialog
                        setTimeout(function () {
                            var $input = b.find(".form-control").not(".datepicker").first();
                            var val = $input.val();
                            $input.focus();
                            $input.val("");
                            $input.val(val);
                        }, 500);
                    } //success
                }); //ajax
            } //createEditPersona

            function cambiarEstadoRowPersona(itemId, strId, activar, tramites) {
                var icon, textMsg, textBtn, textLoader, url, clase;
                if (activar) {
                    clase = "success";
                    icon = "${iconActivar}";
                    textMsg = "<p>¿Está seguro que desea activar la persona seleccionada?</p>";
                    textBtn = "Activar";
                    textLoader = "Activando";
                    url = "${createLink(controller: 'persona', action:'activar_ajax')}";
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
                } else {
                    clase = "danger";
                    icon = "${iconDesactivar}";
                    textMsg = "<p>¿Está seguro que desea desactivar la persona seleccionada?</p>";
                    textBtn = "Desactivar";
                    textLoader = "Desactivando";
                    if (tramites > 0) {
//                        textMsg += "<p>" + tramites + " trámite" + (tramites == 1 ? '' : 's') + " será" + (tramites == 1 ? '' : 'n') + " " +
//                                   "redireccionados de su bandeja de entrada personal a la bandeja de entrada de la oficina.</p>";
                        $.ajax({
                            type    : "POST",
                            url     : "${createLink(controller: 'persona', action:'verDesactivar_ajax')}",
                            data    : {
                                id       : itemId,
                                tramites : tramites
                            },
                            success : function (msg) {
                                bootbox.dialog({
                                    title   : "Alerta",
                                    message : msg,
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
                                                        id    : itemId,
                                                        quien : $("#cmbRedirect").val()
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
                        });
                    } else {
                        textBtn = "Desactivar";
                        textLoader = "Desactivando";

                        textMsg += "<p>No tiene trámites en su bandeja de entrada personal.</p>";

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
                    url = "${createLink(controller: 'persona', action:'desactivar_ajax')}";
                }
            } //cambiar estado row persona

            function cambiarEstadoRow(itemId, strId, activar, tramites) {
                var icon, textMsg, textBtn, textLoader, url, clase;
                if (activar) {
                    clase = "success";
                    icon = "${iconActivar}";
                    textMsg = "<p>¿Está seguro que desea activar el departamento seleccionado?</p>";
                    textBtn = "Activar";
                    textLoader = "Activando";
                    url = "${createLink(action:'activar_ajax')}";
                } else {
                    clase = "danger";
                    icon = "${iconDesactivar}";
                    textMsg = "<p>¿Está seguro que desea desactivar el departamento seleccionado?</p>";
                    if (tramites > 0) {
                        textMsg += "<p id='pWarning'>" + tramites + " trámite" + (tramites == 1 ? '' : 's') + " será" + (tramites == 1 ? '' : 'n') + " " +
                                   "redireccionados de su bandeja a la bandeja de entrada de la oficina del departamento que seleccione a continuación.</p>";

                    } else {
                        textMsg += "<p>No tiene trámites en su bandeja de entrada.</p>"
                    }
                    var $sel = $("#selDptoOrig").clone();

                    %{--textMsg += "${g.select(name:'selDpto', from:Departamento.list([sort:'descripcion']), class: 'form-control')}";--}%
                    textBtn = "Desactivar";
                    textLoader = "Desactivando";
                    url = "${createLink(action:'desactivar_ajax')}";
                }
                bootbox.dialog({
                    id      : "dlgWarning",
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
                                        closeLoader();
                                    }
                                });
                            }
                        }
                    }
                });
                if ($sel) {
                    $sel.removeClass("hide");
                    $sel.attr("name", "selDpto");
                    $sel.attr("id", "selDpto");
                    $sel.find("option." + itemId).remove();
                    $("#pWarning").append($sel);
                }
            } //cambiar estado row

            function createContextMenu(node) {
                var nodeStrId = node.id;
                var $node = $("#" + nodeStrId);
                var nodeId = nodeStrId.split("_")[1];
                var nodeType = $node.data("jstree").type;

//                var parentStrId = node.parent;
//                var $parent = $("#" + parentStrId);
//                var parentId = parentStrId.split("_")[1];

                var nodeHasChildren = $node.hasClass("hasChildren");
                var nodeOcupado = $node.hasClass("ocupado");

                var nodeTramites = $node.data("tramites");

                if (nodeType == "root") {
                    var items = {
                        crear    : {
                            label  : "Nuevo departamento hijo",
                            icon   : "fa fa-plus-circle text-success",
                            action : function (obj) {
                                createEditRow(nodeId, "Crear");
                            }
                        },
                        imprimir : {
                            label   : "Imprimir",
                            icon    : "fa fa-print",
                            action  : false,
                            submenu : {
                                si : {
                                    label  : "Con usuarios",
                                    icon   : "fa fa-users text-info",
                                    action : function () {
                                        location.href = "${createLink(controller: 'departamentoExport', action: 'crearPdf')}/-1?usu=true&sort=${params.sort}";
                                    }
                                },
                                no : {
                                    label  : "Solo departamentos",
                                    icon   : "fa fa-home text-info",
                                    action : function () {
                                        location.href = "${createLink(controller: 'departamentoExport', action: 'crearPdf')}/-1?usu=false&sort=${params.sort}";
                                    }
                                }
                            }
                        }
                    };
                }
                else if (nodeType.contains('padre') || nodeType.contains('hijo')) {
                    items = {
                        crear        : {
                            label  : "Nuevo departamento hijo",
                            icon   : "fa fa-plus-circle text-success",
                            action : function (obj) {
                                createEditRow(nodeId, "Crear");
                            }
                        },
                        editar       : {
                            label  : "Editar departamento",
                            icon   : "fa fa-pencil text-info",
                            action : function (obj) {
                                createEditRow(nodeId, "Editar");
                            }
                        },
                        ver          : {
                            label  : "Ver departamento",
                            icon   : "fa fa-laptop text-info",
                            action : function (obj) {
                                $.ajax({
                                    type    : "POST",
                                    url     : "${createLink(action:'show_ajax')}",
                                    data    : {
                                        id : nodeId
                                    },
                                    success : function (msg) {
                                        bootbox.dialog({
                                            title   : "Ver Departamento",
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
                        crearPersona : {
                            separator_before : true,
                            label            : "Nueva persona",
                            icon             : "fa fa-user text-success",
                            action           : function (obj) {
                                createEditRowPersona(nodeId, "Crear");
                            }
                        }
                    };

                    if (nodeType.indexOf('Inactivo') !== -1) {
                        delete items.crear;
                        delete items.crearPersona;
                        delete items.desactivar;

                        items.activar = {
                            separator_before : true,
                            label            : "Activar",
                            icon             : "fa ${iconActivar} text-success",
                            action           : function (obj) {
                                cambiarEstadoRow(nodeId, nodeStrId, true, nodeTramites);
                            }
                        }
                    }

                    if (!nodeHasChildren && !nodeOcupado) {
                        if (!nodeType.contains('Inactivo')) {
                            items.desactivar = {
                                separator_before : true,
                                label            : "Desactivar",
                                icon             : "fa ${iconDesactivar}",
                                action           : function (obj) {
                                    cambiarEstadoRow(nodeId, nodeStrId, false, nodeTramites);
                                }
                            };
                        }
                        items.eliminar = {
                            label  : "Eliminar departamento",
                            icon   : "fa fa-trash-o text-danger",
                            action : function (obj) {
                                var $node = $('#' + nodeStrId);
                                bootbox.dialog({
                                    title   : "Alerta",
                                    message : "<i class='fa fa-trash-o fa-3x pull-left text-danger text-shadow'></i>" +
                                              "<p>¿Está seguro que desea eliminar el departamento seleccionado? Esta acción no se puede deshacer.</p>",
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
                                                        id : nodeId
                                                    },
                                                    success : function (msg) {
                                                        var parts = msg.split("_");
                                                        log(parts[1], parts[0] == "OK" ? "success" : "error"); // log(msg, type, title, hide)
                                                        if (parts[0] == "OK") {
                                                            location.reload(true);
//                                                            var siblings = $node.siblings().size();
//                                                            if (siblings == 0) {
//                                                                $('#tree').jstree('set_type', "#" + nodeStrId, "hijo");
//                                                            }
//                                                            $('#tree').jstree('delete_node', $node);
                                                        } else {
                                                            closeLoader();
                                                            return false;
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                            }
                        };
                    }

                    items.imprimir = {
                        label   : "Imprimir",
                        icon    : "fa fa-print",
                        action  : false,
                        submenu : {
                            si : {
                                label  : "Con usuarios",
                                icon   : "fa fa-users text-info",
                                action : function () {
                                    location.href = "${createLink(controller: 'departamentoExport', action: 'crearPdf')}/" + nodeId + "?usu=true&sort=${params.sort}";
                                }
                            },
                            no : {
                                label  : "Solo departamentos",
                                icon   : "fa fa-home text-info",
                                action : function () {
                                    location.href = "${createLink(controller: 'departamentoExport', action: 'crearPdf')}/" + nodeId + "?usu=false&sort=${params.sort}";
                                }
                            }
                        }
                    };
                    if (nodeType.contains('hijo')) {
                        delete items.imprimir.submenu.no
                    }

                }
                else if (nodeType.contains('usuario') || nodeType.contains('jefe')) {
                    items = {
                        editar : {
                            label  : "Editar persona",
                            icon   : "fa fa-pencil text-info",
                            action : function (obj) {
                                createEditRowPersona(nodeId, "Editar");
                            }
                        },
                        ver    : {
                            label  : "Ver Persona",
                            icon   : "fa fa-laptop text-info",
                            action : function (obj) {
                                $.ajax({
                                    type    : "POST",
                                    url     : "${createLink(controller: 'persona', action:'show_ajax')}",
                                    data    : {
                                        id : nodeId
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
                        }
                    };

                    if (nodeType.contains('Inactivo')) {
                        items.activar = {
                            separator_before : true,
                            label            : "Activar",
                            icon             : "fa ${iconActivar} text-success",
                            action           : function (obj) {
                                cambiarEstadoRowPersona(nodeId, nodeStrId, true, nodeTramites);
                            }
                        }
                    } else {
                        items.desactivar = {
                            separator_before : true,
                            label            : "Desctivar",
                            icon             : "fa ${iconDesactivar}",
                            action           : function (obj) {
                                cambiarEstadoRowPersona(nodeId, nodeStrId, false, nodeTramites);
                            }
                        }
                    }

                }

                return items;
            }

            $(function () {

                $(".btnCopiar").click(function () {
                    openLoader("Copiando");
                });

                $("#btnCreate").click(function () {
                    createEditRow(null, "Crear");
                });

                $('#tree').on("loaded.jstree", function () {
                    $("#loading").hide();
                    $("#tree").removeClass("hide").show();
                }).on("select_node.jstree", function (node, selected, event) {
//                    $('#tree').jstree('toggle_node', selected.selected[0]);
                }).jstree({
                    plugins     : [ "types", "state", "contextmenu", "wholerow" , "search"],
                    core        : {
                        multiple       : false,
                        check_callback : true,
                        themes         : {
                            variant : "small",
                            dots    : true,
                            stripes : true
                        },
                        data           : {
                            async : false,
                            url   : '${createLink(action:"loadTreePart")}',
                            data  : function (node) {
                                return {
                                    id    : node.id,
                                    sort  : "${params.sort?:'apellido'}",
                                    order : "${params.order?:'asc'}"
                                };
                            }
                        }
                    },
                    contextmenu : {
                        show_at_node : false,
                        items        : createContextMenu
                    },
                    state       : {
                        key : "departamentos"
                    },
                    search      : {
                        fuzzy             : false,
                        show_only_matches : true,
                        ajax              : {
                            url     : "${createLink(action:'arbolSearch_ajax')}",
                            success : function (msg) {
                                var json = $.parseJSON(msg);
                                $.each(json, function (i, obj) {
                                    $('#tree').jstree("open_node", obj);
                                });
                            }
                        }
                    },
                    types       : {
                        root            : {
                            icon : "fa fa-folder text-warning"
                        },
                        padreActivo     : {
                            icon : "fa fa-building-o text-info"
                        },
                        padreInactivo   : {
                            icon : "fa fa-building-o text-muted"
                        },
                        hijoActivo      : {
                            icon : "fa fa-home text-success"
                        },
                        hijoInactivo    : {
                            icon : "fa fa-home text-muted"
                        },
                        usuarioActivo   : {
                            icon : "fa fa-user text-info"
                        },
                        usuarioInactivo : {
                            icon : "fa fa-user text-muted"
                        },
                        jefeActivo      : {
                            icon : "fa fa-user text-warning"
                        },
                        jefeInactivo    : {
                            icon : "fa fa-user text-muted"
                        }
                    }
                });

                $('#btnSearch').click(function () {
                    $('#tree').jstree(true).search($.trim($("#search").val()));
                    return false;
                });
                $("#search").keypress(function (ev) {
                    if (ev.keyCode == 13) {
                        $('#tree').jstree(true).search($.trim($("#search").val()));
                        return false;
                    }
                });
            });
        </script>

    </body>
</html>
