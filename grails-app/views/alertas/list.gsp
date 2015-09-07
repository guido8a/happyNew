<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 18/02/14
  Time: 12:52 PM
--%>


<%@ page import="happy.tramites.EstadoTramite; org.apache.commons.lang.WordUtils" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Alertas</title>
    <style type="text/css">
    .d0{
        background: #e0ffc8;
    }
    .d1{
        background: #fff949;
    }
    .d2{
        background: #ff9d4d;
    }
    .dmas{
        background: #ff573f;
    }
    </style>
</head>

<body>

<div class="row" style="margin-top: 0px; margin-left: 1px">
    <span class="grupo">
        <label class="well well-sm" style="text-align: center; float: left">
            Usuario: ${session.usuario}
        </label>
    </span>
</div>

<div class="btn-toolbar toolbar" style="margin-top: 10px !important">
    <div class="btn-group">
        <g:link action="list" class="btn btn-success btnActualizar">
            <i class="fa fa-refresh"></i> Actualizar
        </g:link>
        <g:link action="index" controller="inicio" class="btn btn-default ">
            <i class="fa fa-sing-out"></i> Salir
        </g:link>
    </div>
</div>

<table class="table table-bordered  table-condensed ">
    <thead>
    <tr>
        <th></th>
        <th>Alerta</th>
        <th>Tramite</th>
        <th>Fecha</th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    <g:each in="${alertas}" var="a">
        <tr>
            <td class="d${(((new Date()) - a.fechaCreacion)>2)?"mas":(new Date()) - a.fechaCreacion }"></td>
            <td>${a.mensaje}</td>
            <td>${a.tramite?.codigo}</td>
            <td>${a.fechaCreacion?.format("dd-MM-yyyy HH:mm")}</td>
            <td style="text-align: center">
                <g:if test="${a.controlador!='' && a.controlador.size()>1}">
                    <a href="#" link="${g.createLink(controller: a.controlador,action: a.accion)}/${a.datos}" class="btn btn-azul btn-small rev" iden="${a.id}" title="Revisar">
                        <i class="fa fa-check"></i>
                    </a>
                </g:if>
                <g:else>
                    <a href="#" link="${g.createLink(controller: 'inicio',action: 'index')}" class="btn btn-azul btn-small rev" iden="${a.id}" title="Revisar">
                        <i class="fa fa-check"></i>
                    </a>
                </g:else>
            </td>
        </tr>
    </g:each>
    </tbody>
</table>

<script type="text/javascript">
    $(".rev").click(function(){
        var boton = $(this)
        $.ajax({
            url     : '${createLink(controller: "alertas", action: "revisar")}',
            data    : 'id='+boton.attr("iden"),
            success : function (msg) {
                if (msg == "ok") {
                    location.href = boton.attr("link");
                }
            }
        });
    })
</script>
</body>
</html>