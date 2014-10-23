
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Reporte de Usuarios Bloqueados</title>
    <style type="text/css">
    .titulo {
        font-weight : bold;
        color       : #222;
    }

    .titl {
        font-family : 'open sans condensed';
        font-weight : bold;
        text-shadow : -2px 2px 1px rgba(0, 0, 0, 0.25);
        color       : #0070B0;
        margin-top  : 20px;
    }

    .numero {
        text-align : right;
    }

    .divChart {
        height       : 360px;
        width        : 360px;
        float        : left;
        margin-right : 10px;
    }

    .tableContainer {
        width : 920px;
    }

    .chartContainer {
        height : 360px;
    }

    th {
        border-color     : #444;
        background-color : #e4e4e8;
    }
    </style>
</head>

<body>
<div style="margin-left:230px;"><h2 class="titl">S.A.D. Web - Usuarios Bloqueados</h2>
</div>

<div class="btn-toolbar toolbar" style="margin-top: 20px; ">

    <div class="btn-group">
        <g:link class="btn btn-default" controller="bloqueados" action="reporteConsolidado">
            <i class="fa fa-file-pdf-o"></i> Reporte en  pdf
        </g:link>
    </div>
</div>
<div class="tableContainer ">
    <util:renderHTML html="${tabla}"/>
</div>


</body>
</html>