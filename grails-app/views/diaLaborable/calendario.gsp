<%@ page import="happy.utilitarios.Parametros" contentType="text/html;charset=UTF-8" %>
<html xmlns="http://www.w3.org/1999/html">
    <head>
        <meta name="layout" content="main">
        %{--<script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>--}%
        %{--<link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">--}%
        <title>Días Laborables</title>

        <style type="text/css">
        div.mes {
            float  : left;
            margin : 0 0 10px 10px;
            height : 175px;
        }

        table.mes {
            border-collapse : collapse;
        }

        table.mes th {
            font-size  : 12px;
            text-align : center;
        }

        .dia {
            width      : 33px;
            text-align : center;
            cursor     : pointer;
        }

        .dia:hover {
            background : #9BBFCF;
        }

        .dia.selected {
            background : #9BBFCF;
        }

        .cambiado {
            background : #ffdfbc;
        }

        .vacio {
            background-color : #AAAAAA;
        }

        .vacacion {
            background-color : #5CAACE;
        }

        h3 {
            text-align : center;
        }

        .demo {
            width      : 20px;
            height     : 17px;
            text-align : center;
            display    : inline-block;
            color      : #444;
            border     : solid 1px #808080;
        }

        .nombreMes {
            text-align : center;
            font-size  : 18px;
        }

        .mesesContainer {
            height        : 530px;
            margin-bottom : 350px;
        }
        </style>
    </head>

    <body>

        <g:set var="parametros" value="${Parametros.list().first()}"/>

        <!-- botones -->
        <div class="btn-toolbar toolbar">
            <div class="btn-group">
                <g:link controller="inicio" action="parametros" class="btn btn-default">
                    <i class="fa fa-arrow-left"></i> Regresar
                </g:link>
            </div>

            <div class="btn-group">
                <a href="#" class="btn btn-success" id="btnGuardar">
                    <i class="icon fa fa-check"></i> Guardar
                </a>
                <a href="#" class="btn btn-default" id="btnDesactivar">
                    <i class="icon fa fa-power-off"></i> Cerrar el año de proceso
                </a>
            </div>
        </div>

        <h3>
            Año ${anio.numero}%{--<g:select style="font-size:large;" name="anio" class="input-small" from="${anio - 5..anio + 5}" value="${params.anio}"/>--}%
            %{--<a href="#" class="btn btn-primary" id="btnCambiar"><i class="icon fa fa-refresh"></i> Cambiar</a>--}%
        </h3>

        <div class="well well-sm">
            Los días marcados con <div class="demo vacacion">&nbsp;</div> son <b>no laborables</b>.<br/>
            Haciendo clic sobre el día se cambia de laborable a no laborable y viceversa.
            <br/>
            Por defecto, la hora de inicio de la jornada es ${parametros.inicioJornada} y el final es ${parametros.finJornada}.
            Los días marcados con <div class="demo cambiado">&nbsp;</div> tienen una hora de inicio o de fin de jornada modificada.
        Puede cambiar estos valores de manera individual para cada día haciendo click derecho.<br/>
            <b>Los cambios se guardan haciendo clic en el botón "Guardar".</b>
        </div>

        <g:set var="mesAct" value="${null}"/>
    <div class="mesesContainer">
        <g:each in="${dias}" var="dia" status="i">
            <g:set var="mes" value="${meses[dia.fecha.format('MM').toInteger()]}"/>
            <g:set var="dia" value="${meses[dia.fecha.format('MM').toInteger()]}"/>
            <g:if test="${mes != mesAct}">
                <g:if test="${mesAct}">
                    </table>
                    </div>
                </g:if>
                <g:set var="mesAct" value="${mes}"/>
                <g:set var="num" value="${1}"/>
                <div class="mes">
                <table class="mes" border="1">
                <thead>
                <tr>
                <th class="nombreMes" colspan="7">${mesAct}</th>
                </tr>
                <tr>
                    <th>Lun</th>
                    <th>Mar</th>
                    <th>Mié</th>
                    <th>Jue</th>
                    <th>Vie</th>
                    <th>Sáb</th>
                    <th>Dom</th>
                </tr>
                </thead>
            </g:if>
            <g:if test="${num % 7 == 1}">
                <tr>
            </g:if>
            <g:if test="${dia.fecha.format("dd").toInteger() == 1}">
                <g:if test="${dia.dia.toInteger() != 1}">%{--No empieza en lunes: hay q dibujar celdas vacias en los dias necesarios--}%
                    <g:each in="${1..(dia.dia.toInteger() - 1 + (dia.dia.toInteger() > 0 ? 0 : 7))}" var="extra">
                        <td class="vacio"></td>
                        <g:set var="num" value="${num + 1}"/>
                    </g:each>
                </g:if>
            </g:if>
            <td class="dia ${dia.ordinal == 0 ? 'vacacion' : ''}
            ${((dia.horaInicio > -1 && dia.horaInicio != parametros.horaInicio) || (dia.minutoInicio > -1 && dia.minutoInicio != parametros.minutoInicio) ||
                    (dia.horaFin > -1 && dia.horaFin != parametros.horaFin) || (dia.minutoFin > -1 && dia.minutoFin != parametros.minutoFin)) ? 'cambiado' : ''}"
                data-fecha="${dia.fecha.format('dd-MM-yyyy')}" data-id="${dia.id}"
                data-inih="${dia.horaInicio > -1 ? dia.horaInicio : parametros.horaInicio}"
                data-inim="${dia.minutoInicio > -1 ? dia.minutoInicio : parametros.minutoInicio}"
                data-finh="${dia.horaFin > -1 ? dia.horaFin : parametros.horaFin}"
                data-finm="${dia.minutoFin > -1 ? dia.minutoFin : parametros.minutoFin}">
                ${dia.fecha.format("dd")}
            </td>

            <g:set var="num" value="${num + 1}"/>

            <g:if test="${i == dias.size() - 1 || (i < dias.size() - 1) && (meses[dias[i + 1].fecha.format('MM').toInteger()] != mesAct)}">
                <g:if test="${dia.dia.toInteger() != 0}">
                    <g:each in="${1..7 - (num % 7 > 0 ? num % 7 : 7) + 1}" var="extra">
                        <td class="vacio"></td>
                    </g:each>
                </g:if>
            </g:if>
        </g:each>
    </table>
    </div>

        <script type="application/javascript">
            $(function () {
                $('.dia').tooltip()
                        .click(function () {
                            $(this).toggleClass("vacacion");
                        });
                %{--$("#anio").val("${params.anio}");--}%
                %{--$("#btnCambiar").click(function () {--}%
                %{--var anio = $("#anio").val();--}%
                %{--if ("" + anio != "${params.anio}") {--}%
                %{--openLoader();--}%
                %{--location.href = "${createLink(action: 'calendario')}?anio=" + anio;--}%
                %{--}--}%
                %{--return false;--}%
                %{--});--}%
                $("#btnGuardar").click(function () {
                    openLoader();
                    var cont = 1;
                    var data = "";
                    $(".dia").each(function () {
                        var $dia = $(this);
                        var fecha = $dia.data("fecha");
                        var id = $dia.data("id");

                        var inih = $dia.data("inih");
                        var inim = $dia.data("inim");
                        var finh = $dia.data("finh");
                        var finm = $dia.data("finm");

                        var laborable = !$dia.hasClass("vacacion");
                        if (data != "") {
                            data += "&";
                        }
                        data += "dia=" + id + ":" + fecha + ":";
                        if (laborable) {
                            data += cont;
                            cont++;
                        } else {
                            data += "0";
                        }
                        data += ":" + inih + ":" + inim + ":" + finh + ":" + finm
                    });
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action: 'saveCalendario')}",
                        data    : data,
                        success : function (msg) {
                            if (msg == "OK") {
                                location.reload(true);
                            } else {

                                bootbox.confirm(msg, function (res) {
                                    if (res) {
                                        openLoader();
                                        location.reload(true);
                                    }
                                });
                            }
                        }
                    });
                    return false;
                });

                $("#btnDesactivar").click(function () {
                    bootbox.dialog({
                        title   : "Alerta",
                        message : "<i class='fa fa-power-off fa-3x pull-left text-shadow'></i>" +
                                  "<p>¿Está seguro que desea cerrar el año de proceso: ${anio.numero}?</p>" +
                                  "<p>Esta acción no se puede deshacer y consiste de:</p>" +
                                  "<ul>" +
                                  "<li>Cerrar el año de proceso ${anio.numero}, por lo que no se podrá crear nuevos trámites este año</li>" +
                                  "<li>Reiniciar la numeración de los trámites</li>" +
                                  "<li>Crear el nuevo año ${anio.numero.toInteger()+1}</li>" +
                                  "<li>Inicializar el calendario laborable y definir los días festivos y el horario de trabajo</li>" +
                                  "</ul>",
                        buttons : {
                            cancelar   : {
                                label     : "Cancelar",
                                className : "btn-primary",
                                callback  : function () {
                                }
                            },
                            desactivar : {
                                label     : "<i class='fa fa-power-off'></i> Desactivar",
                                className : "btn-default",
                                callback  : function () {
                                    openLoader();
                                    location.href = "${createLink(action: 'desactivar')}/${anio.id}";
                                }
                            }
                        }
                    });
                });

                var id;

                function submenuHoras(tipo) {
                    var submenuHoras = [];
                    for (var i = 7; i < 19; i++) {
                        submenuHoras.push({
                            label  : i.toString().lpad('0', 2),
                            action : function ($td, $target) {
//                                var $target = $(e.target);
//                                e.preventDefault();
                                var $dia = $(".dia.selected");
                                var hora = parseInt($target.text());
                                $dia.data(tipo + "h", hora);
                                var $header = $target.parents(".dropdown-context").find(".nav-header");
                                $header.find("." + tipo + "h").text(hora.toString().lpad('0', 2));
                                if (tipo == "ini") {
                                    if (hora.toString() != "${parametros.horaInicio}") {
                                        if (!$dia.hasClass("cambiado")) {
                                            $dia.addClass("cambiado");
                                        }
                                    } else {
                                        $dia.removeClass("cambiado");
                                    }
                                } else if (tipo == "fin") {
                                    if (hora.toString() != "${parametros.horaFin}") {
                                        if (!$dia.hasClass("cambiado")) {
                                            $dia.addClass("cambiado");
                                        }
                                    } else {
                                        $dia.removeClass("cambiado");
                                    }
                                }
                                return false;
                            }
                        });
                    }
                    return submenuHoras;
                }

                function submenuMinutos(tipo) {
                    var submenuMinutos = [];
                    for (var i = 0; i < 60; i += 10) {
                        submenuMinutos.push({
                            label  : i.toString().lpad('0', 2),
                            action : function ($td, $target) {
//                                console.log($target, $item);
//                                var $target = $(e.target);
//                                e.preventDefault();
                                var $dia = $(".dia.selected");
                                var hora = parseInt($target.text());
                                $dia.data(tipo + "m", hora);
                                var $header = $target.parents(".dropdown-context").find(".nav-header");
                                $header.find("." + tipo + "m").text(hora.toString().lpad('0', 2));
                                if (tipo == "ini") {
                                    if (hora.toString() != "${parametros.minutoInicio}") {
                                        if (!$dia.hasClass("cambiado")) {
                                            $dia.addClass("cambiado");
                                        }
                                    } else {
                                        $dia.removeClass("cambiado");
                                    }
                                } else if (tipo == "fin") {
                                    if (hora.toString() != "${parametros.minutoFin}") {
                                        if (!$dia.hasClass("cambiado")) {
                                            $dia.addClass("cambiado");
                                        }
                                    } else {
                                        $dia.removeClass("cambiado");
                                    }
                                }
                                return false;
                            }
                        });
                    }
                    return submenuMinutos;
                }

                $(".dia").contextMenu({
                    items     : {
                        header  : {
                            label  : "Hora aqui",
                            header : true
                        },
                        inicia  : {
                            label   : "Inicia",
                            submenu : {
                                horas   : {
                                    label   : "Horas",
                                    submenu : submenuHoras("ini")
                                },
                                minutos : {
                                    label   : "Minutos",
                                    submenu : submenuMinutos("ini")
                                }
                            }
                        },
                        termina : {
                            label   : "Termina",
                            submenu : {
                                horas   : {
                                    label   : "Horas",
                                    submenu : submenuHoras("fin")
                                },
                                minutos : {
                                    label   : "Minutos",
                                    submenu : submenuMinutos("fin")
                                }
                            }
                        }
                    },
                    onShow    : function ($td) {
                        $td.addClass("selected");
                    },
                    afterShow : function ($td) {
                        var $header = $(".dropdown-header");
                        if ($td.hasClass("vacacion")) {
                            $header.text($td.data("fecha"));
                        } else {
                            $header.html($td.data("fecha") + " <span class='inih'>" + ($td.data("inih").toString().lpad('0', 2)) + "</span>:<span class='inim'>" + ($td.data("inim").toString().lpad('0', 2)) + "</span> - <span class='finh'>" + ($td.data("finh").toString().lpad('0', 2)) + "</span>:<span class='finm'>" + ($td.data("finm").toString().lpad('0', 2)) + "</span>");
                        }
                    },
                    onHide    : function ($element) {
                        $(".selected").removeClass("selected");
                    }
                });
            });
        </script>
    </body>
</html>