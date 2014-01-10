<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Excel</title>
    <style  type="text/css">
    .contenido{
        overflow: hidden;
        border :1px solid #000000;
        height: 650px;
        position: relative;
    }
    #excel{
        margin: 0px;
        padding: 0px;
        height: 600px;
        width: ${120*letras.size()+120}px;
    }
    .header-columnas{
        margin: 0px;
        padding: 0px;
        border: none;
        height: 25px;
        background: #EEEEEE;
        float: left;
        width: ${120*letras.size()+60}px;;
    }
    .header-filas{
        margin: 0px;
        padding: 0px;
        border: none;
        width: 50px;
        background: #EEEEEE;
        height: ${25*100+50}px;
        float: left;
    }
    .cell-header{
        margin: 0px;
        padding: 0px;
        width:120px;
        border: 1px solid #AAAAAA;
        float: left;
        height: 100%;

    }
    .cell-header-filas{
        margin: 0px;
        padding: 0px;
        width:150px;
        border: 1px solid #AAAAAA;
        float: left;
        height: 25px;
        width: 100%;
        text-align: center;
    }
    .header{
        text-align: center;
    }
    #all{
        margin: 0px;
        padding: 0px;
        width: 50px;
        height: 100%;
        border: 1px solid #AAAAAA;
        float: left;
    }
    #celdas{
    %{--width: ${150*letras.size()}px;--}%
        width: ${120*letras.size()}px;
        height:${25*100+50}px ;
        margin: 0px;
        padding: 0px;
        float: left;
        overflow: auto;
    }
    .spreadsheet{
        margin: 0px;
        padding: 0px;
        border: none;
        width: 100%;

    }
    .cell{
        margin: 0px;
        padding: 0px;
        border: 1px solid #AAAAAA;
        width: 120px;
        height: 25px;
    }
    .container-celdas{
        width: 1090px;
        height: 610px;
        float: left;
        overflow: auto;
        overflow-y: auto;
    }
    #container-filas{
        height: 590px;
        width: 50px;
        overflow: hidden;
        float: left;
    }
    #container-cols{
        height: 25px;
        width: 1125px;
        /*display: inline;*/
        overflow: hidden;
        /*float: left;*/
    }
    table.header{
        border: none;
        padding: 0px;
        margin: 0px;
        height: 25px;
        /*margin-left: -1px;*/
        width: ${120*letras.size()}px;
        border-spacing:0px !important;
    }
    </style>

</head>
<body>
<div class="btn-toolbar toolbar" id="controles">

</div>
<div class="contenido">
    <div class="" id="excel">
        <div id="container-cols">
            <div class="header-columnas">
                <div id="all"></div>
                <table class="header">
                    <tbody>
                    <tr>
                        <g:each in="${letras}" var="l" status="i">
                            <td class="cell-header header h-${l}" fila="${l}" fila-num="${i}">
                                ${l}
                            </td>
                        </g:each>
                    </tr>
                    </tbody>
                </table>
                %{--<g:each in="${letras}" var="l" status="i">--}%
                %{--<div class="cell-header header h-${l}" fila="${l}" fila-num="${i}">--}%
                %{--${l}--}%
                %{--</div>--}%
                %{--</g:each>--}%
            </div>
        </div>
        <div id="container-filas">
            <div class="header-filas">
                <g:each in="${1..100}" var="n" status="i">
                    <div class="cell-header-filas">${n}</div>
                </g:each>
            </div>
        </div>
        <div class="container-celdas">
            <div id="celdas">
                <table class="spreadsheet">
                    <tbody>
                    <g:each in="${1..100}" var="n" status="i">
                        <tr>
                            <g:each in="${letras}" var="l" status="j">
                                <td class="cell ${l} " fila="${n}" columna="${l}" colNum="${j}"></td>
                            </g:each>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
        </div>



    </div>
</div>

<script>

    $(function () {
        var cellWidth=150;
        var celHegth=25;
        var select=null;
        var headerTop = $(".header-columnas");
        var headerLeft=$(".header-filas");

        $( ".h-A" ).resizable({
            handles: "e",
            minWidth:30,
            alsoResize: ".A"
        });
        $(".container-celdas").scroll(function(){
            $("#container-filas").scrollTop($(".container-celdas").scrollTop());
            $("#container-cols").scrollLeft($(".container-celdas").scrollLeft());
        });

    });
</script>
</body>
</html>