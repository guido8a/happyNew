<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="happy.seguridad.Persona" %>

<html xmlns="http://www.w3.org/1999/html">
<head>
    <title>S.A.D. Web</title>
    <meta name="layout" content="main"/>
    <style type="text/css">
    @page {
        size: 8.5in 11in;  /* width height */
        margin: 0.25in;
    }

    .item {
        width: 260px;
        height: 260px;
        float: left;
        margin: 4px;
        font-family: 'open sans condensed';
        border: none;

    }

    .imagen {
        width: 160px;
        height: 160px;
        margin: auto;
        margin-top: 10px;
    }

    .texto {
        width: 90%;
        height: 50px;
        padding-top: 0px;
        margin: auto;
        margin: 8px;
        font-size: 16px;
        font-style: normal;
    }

    .fuera {
        margin-left: 15px;
        margin-top: 20px;
        /*background-color: #317fbf; */
        background-color: rgba(200, 200, 200, 0.9);
        border: none;
    }

    .titl {
        font-family: 'open sans condensed';
        font-weight: bold;
        text-shadow: -2px 2px 1px rgba(0, 0, 0, 0.25);
        color: #0070B0;
        margin-top: 20px;
    }
    </style>
</head>

<body>
<div class="dialog">
    <div style="text-align: center;"><h2 class="titl"
    >S.A.D. Web<br>
        GOBIERNO AUTÓNOMO DESCENTRALIZADO PROVINCIA DE PICHINCHA<br/>
        Sistema de Administración de Documentos</h2>
    </div>

    <g:if test="${!(session.usuario.getPuedeDirector() || session.usuario.getPuedeJefe())}">

    <div class="body ui-corner-all" style="width: 575px;position: relative;margin: auto;margin-top: 40px;height: 280px; ">
    %{--<div class="body ui-corner-all" style="width: 575px;position: relative;margin: auto;margin-top: 0px;height: 280px; background: #40709a;">--}%

        <g:if test="${session.usuario.esTriangulo()}">
            <a href= "${createLink(controller:'tramite3', action: 'bandejaEntradaDpto')}" style="text-decoration: none">
        </g:if>
        <g:else>
            <a href= "${createLink(controller:'tramite', action: 'bandejaEntrada')}" style="text-decoration: none">
        </g:else>
        <div class="ui-corner-all  item fuera">
            <div class="ui-corner-all ui-widget-content item">
                <div class="imagen">
                    <img src="${resource(dir: 'images', file: 'personales1.png')}" width="100%" height="100%"/>
                </div>

                <div class="texto"><span class="text-success"><strong>Bandeja de entrada</strong></span>: trámites que le han enviado y pendientes de contestación</div>
            </div>
        </div>

    </a>


        <g:if test="${session.usuario.esTriangulo()}">
            <a href= "${createLink(controller:'tramite2', action: 'bandejaSalidaDep')}" style="text-decoration: none">
        </g:if>
        <g:else>
            <a href= "${createLink(controller:'tramite2', action: 'bandejaSalida')}" style="text-decoration: none">
        </g:else>
        <div class="ui-corner-all item fuera">
            <div class="ui-corner-all ui-widget-content item">
                <div class="imagen">
                    <img src="${resource(dir: 'images', file: 'salida1.png')}" width="100%" height="100%"/>
                </div>

                <div class="texto"><span class="text-info"><strong>Bandeja de salida</strong></span>: Documentos por enviar y trámites que no le han recibido</div>
            </div>
        </div>
    </a>

    </g:if>

    <div style="text-align: center; margin-top: 40px">

        <g:if test="${session.usuario.getPuedeDirector()}">
        <a href= "${createLink(controller:'retrasadosWeb', action: 'reporteRetrasadosConsolidadoDir',
                params: [dpto: Persona.get(session.usuario.id).departamento.id, inicio: "1", dir: "1"])}" style="text-decoration: none">
        <img src="${resource(dir: 'images', file: 'ingreso_adm1.jpeg')}" width="640px" height="330px"/>
        </g:if>

        <g:if test="${session.usuario.getPuedeJefe()}">
        <a href= "${createLink(controller:'retrasadosWeb', action: 'reporteRetrasadosConsolidado',
                params: [dpto: Persona.get(session.usuario.id).departamento.id, inicio: "1"])}" style="text-decoration: none">
        <img src="${resource(dir: 'images', file: 'ingreso_adm1.jpeg')}" width="640px" height="330px"/>
        </g:if>

    </div>

        %{--<div class="texto"><b>Trámites externos</b>: recepción de documentos externos</div>--}%
        %{--</div>--}%
        %{--</div>--}%
        %{--<g:if test="${prms.contains('seguimientoExternos')}">--}%
        %{--</a>--}%
        %{--</g:if>--}%

        %{--<g:if test="${prms.contains('archivadosDpto')}">--}%
        %{--<a href= "${createLink(controller:'tramite3', action: 'archivadosDpto')}" style="text-decoration: none">--}%
        %{--</g:if>--}%
        %{--<div class="ui-corner-all  item fuera">--}%
        %{--<div class="ui-corner-all ui-widget-content item">--}%
        %{--<div class="imagen">--}%
        %{--<img src="${resource(dir: 'images', file: 'archivo.jpeg')}" width="100%" height="100%"/>--}%
        %{--</div>--}%

        %{--<div class="texto"><b>Archivo</b>: trámites archivados...</div>--}%
        %{--</div>--}%
        %{--</div>--}%
        %{--<g:if test="${prms.contains('archivadosDpto')}">--}%
        %{--</a>--}%
        %{--</g:if>--}%

        %{--<g:link controller="reportes" action="index" style="text-decoration: none">--}%
        %{--<div class="ui-corner-all  item fuera">--}%
        %{--<div class="ui-corner-all ui-widget-content item">--}%
        %{--<div class="imagen">--}%
        %{--<img src="${resource(dir: 'images', file: 'reporte.jpeg')}" width="100%" height="100%"/>--}%
        %{--</div>--}%

        %{--<div class="texto"><b>Reportes</b>: formatos pdf, hoja de cálculo, texto plano y html.--}%
        %{--trámites resagados, tiempos de respuesta...</div>--}%
        %{--</div>--}%
        %{--</div>--}%
        %{--</g:link>--}%
        %{--<g:link  controller="documento" action="list" title="Documentos de los Proyectos">--}%
        %{--<div class="ui-corner-all  item fuera">--}%
        %{--<div class="ui-corner-all ui-widget-content item">--}%
        %{--<div class="imagen">--}%
        %{--<img src="${resource(dir: 'images', file: 'manuales1.png')}" width="100%" height="100%"/>--}%
        %{--</div>--}%

        %{--<div class="texto"><b>Manuales del sistema:</b>--}%
        %{--<g:link controller="manual" action="manualIngreso" target="_blank">Uso del sistema</g:link>,--}%
        %{--<g:link controller="manual" action="manualIngreso" target="_blank">Trámites externos</g:link>--}%
        %{--<g:link controller="manual" action="manualIngreso" target="_blank">Reportes</g:link>,--}%
        %{--</div>--}%
        %{--</div>--}%
        %{--</div>--}%

        %{--<div style="text-align: center ; color:#002040">Desarrollado por: TEDEIN S.A. Versión ${message(code: 'version', default: '0.1.0x')}</div>--}%

    </div>
    <script type="text/javascript">
        $(".fuera").hover(function () {
            var d = $(this).find(".imagen")
            d.width(d.width() + 10)
            d.height(d.height() + 10)
//        $.each($(this).children(),function(){
//            $(this).width( $(this).width()+10)
//        });
        }, function () {
            var d = $(this).find(".imagen")
            d.width(d.width() - 10)
            d.height(d.height() - 10)
        })
    </script>
</body>
</html>
