<%@ page contentType="text/html;charset=UTF-8" %>
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
        height: 220px;
        float: left;
        margin: 4px;
        font-family: 'open sans condensed';
        border: none;

    }

    .imagen {
        width: 160px;
        height: 120px;
        margin: auto;
        margin-top: 10px;
    }

    .texto {
        width: 90%;
        height: 50px;
        padding-top: 0px;
        /*border: solid 1px black;*/
        margin: auto;
        margin: 8px;
        /*font-family: fantasy; */
        font-size: 16px;

        /*
                font-weight: bolder;
        */
        font-style: normal;
        /*text-align: justify;*/
    }

    .fuera {
        margin-left: 15px;
        margin-top: 20px;
        /*background-color: #317fbf; */
        background-color: rgba(200, 200, 200, 0.9);
        border: none;

    }

    .desactivado {
        color: #bbc;
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
        Sistema de Administración de Documentos</h2></div>

    <div class="body ui-corner-all" style="width: 850px;position: relative;margin: auto;margin-top: 0px;height: 510px;
    background: #40709a;">

        %{--<g:if test="${prms.contains('rubroPrincipal')}">--}%
            <a href="${createLink(controller: 'tramite', action: 'bandejaEntrada')}" style="text-decoration: none">
        %{--</g:if>--}%
        <div class="ui-corner-all  item fuera">
            <div class="ui-corner-all ui-widget-content item">
                <div class="imagen">
                    <img src="${resource(dir: 'images', file: 'personales.png')}" width="100%" height="100%"/>
                </div>

                <div class="texto"><b>Bandeja Personal</b>: trámites que se han enviado al ususario, recibidos y pendientes</div>
            </div>
        </div>
        %{--<g:if test="${prms.contains('rubroPrincipal')}">--}%
            </a>
        %{--</g:if>--}%

        %{--<g:if test="${prms.contains('registroObra')}">--}%
            <a href= "${createLink(controller:'tramite3', action: 'bandejaEntradaDpto')}" style="text-decoration: none">
        %{--</g:if>--}%
        <div class="ui-corner-all item fuera">
            <div class="ui-corner-all ui-widget-content item">
                <div class="imagen">
                    <img src="${resource(dir: 'images', file: 'oficina.png')}" width="100%" height="100%"/>
                </div>

                <div class="texto"><b>Bandeja de la Oficina</b>: recepción y envío de trámites a otras oficinas</div>
            </div>
        </div>
        %{--<g:if test="${prms.contains('registroObra')}">--}%
            </a>
        %{--</g:if>--}%

        %{--<g:if test="${prms.contains('registrarPac')}">--}%
            <a href= "${createLink(controller:'busquedaExternos', action: 'seguimientoExternos')}" style="text-decoration: none">
        %{--</g:if>--}%
        <div class="ui-corner-all item fuera">
            <div class="ui-corner-all ui-widget-content item">
                <div class="imagen">
                    <img src="${resource(dir: 'images', file: 'ingreso.jpeg')}" width="100%" height="100%"/>
                </div>

                <div class="texto"><b>Trámites externos</b>: recepción de documentos externos</div>
            </div>
        </div>
        %{--<g:if test="${prms.contains('registrarPac')}">--}%
            </a>
        %{--</g:if>--}%

        %{--<g:if test="${prms.contains('verContrato')}">--}%
            <a href= "${createLink(controller:'tramite3', action: 'archivadosDpto')}" style="text-decoration: none">
        %{--</g:if>--}%
        <div class="ui-corner-all  item fuera">
            <div class="ui-corner-all ui-widget-content item">
                <div class="imagen">
                    <img src="${resource(dir: 'images', file: 'archivo.jpeg')}" width="100%" height="100%"/>
                </div>

                <div class="texto"><b>Archivo</b>: trámites archivados...</div>
            </div>
        </div>
        %{--<g:if test="${prms.contains('verContrato')}">--}%
            </a>
        %{--</g:if>--}%

        <g:link controller="reportes" action="index" style="text-decoration: none">
            <div class="ui-corner-all  item fuera">
                <div class="ui-corner-all ui-widget-content item">
                    <div class="imagen">
                        <img src="${resource(dir: 'images', file: 'reporte.jpeg')}" width="100%" height="100%"/>
                    </div>

                    <div class="texto"><b>Reportes</b>: formatos pdf, hoja de cálculo, texto plano y html.
                    trámites resagados, tiempos de respuesta...</div>
                </div>
            </div>
        </g:link>
    %{--<g:link  controller="documento" action="list" title="Documentos de los Proyectos">--}%
        <div class="ui-corner-all  item fuera">
            <div class="ui-corner-all ui-widget-content item">
                <div class="imagen">
                    <img src="${resource(dir: 'images', file: 'manuales1.png')}" width="100%" height="100%"/>
                </div>

                <div class="texto"><b>Manuales del sistema:</b>
                    <g:link controller="manual" action="manualIngreso" target="_blank">Uso del sistema</g:link>,
                    <g:link controller="manual" action="manualIngreso" target="_blank">Trámites externos</g:link>
                    <g:link controller="manual" action="manualIngreso" target="_blank">Reportes</g:link>,
                </div>
            </div>
        </div>

        <div style="text-align: center ; color:#002040">Desarrollado por: TEDEIN S.A. Versión ${message(code: 'version', default: '0.1.0x')}</div>

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
