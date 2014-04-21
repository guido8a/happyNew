<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    %{--<meta name="layout" content="main">--}%
    <title>Configurar usuario</title>

    <style type="text/css">
    .perfil .fa-li, .perfil span, .permiso .fa-li, .permiso span {
        cursor : pointer;
    }

    .table {
        font-size     : 13px;
        width         : auto !important;
        margin-bottom : 0 !important;
    }

    .container-celdasAcc {
        max-height : 200px;
        width      : 804px; /*554px;*/
        overflow   : auto;
    }

    .container-celdasPerm {
        max-height : 200px;
        width      : 1030px;
        overflow   : auto;
    }

    .col100 {
        width : 100px;
    }

    .col200 {
        width : 250px;
    }

    .col300 {
        width : 304px;
    }

    .col-md-1.xs {
        width : 45px;
    }

    .fecha {
        width : 160px;
    }

    </style>

</head>

<body>

%{--<div class="well well-sm">--}%
<div class="form-group keeptogether">
    <div>
        <span class="col-md-10" style="text-align: center">
            <div class="panel panel-default" style="margin-left: 30px;">
                <div class="panel-heading"><strong>${departamentoInstance.descripcion}</strong>
                </div>
            </div>
        </span>
    </div>
</div>

    <div class="panel panel-default">
            <h4 class="panel-default">
                %{--<a data-toggle="collapse" data-parent="#accordion" href="#collapsePerfiles">--}%
                Tipo de Documentación <small>Asignar uno o más tipos</small>
                %{--</a>--}%
            </h4>

        <div id="collapsePerfiles" class="panel-collapse collapse in">
            <div class="panel panel-default">
                <p>
                    <a href="#" class="btn btn-default btn-sm" id="allPerf">Asignar todos los tipos</a>
                    <a href="#" class="btn btn-default btn-sm" id="nonePerf">Quitar todos los tipos</a>
                </p>
                <g:form name="frmTipoDocumentos" action="savetipoDoc_ajax">
                    <ul class="fa-ul">
                        <g:each in="${happy.tramites.TipoDocumento.list([sort: 'descripcion'])}" var="perfil">
                            <li class="perfil">
                                <i data-id="${perfil.id}"
                                   class="fa-li fa ${permisos.contains(perfil?.id) ? "fa-check-square" : "fa-square-o"}"></i>
                                <span>${perfil.descripcion}</span>
                            </li>
                        </g:each>
                    </ul>
                </g:form>
            </div>
        </div>
    </div>


<script type="text/javascript">


    $(function () {

        $("#allPerf").click(function () {
            alert("SF");
            $(".perfil .fa-li").removeClass("fa-square-o").addClass("fa-check-square");
            return false;
        });

        $("#nonePerf").click(function () {
            $(".perfil .fa-li").removeClass("fa-check-square").addClass("fa-square-o");
            return false;
        });

        $(".perfil .fa-li, .perfil span").click(function () {
            var ico = $(this).parent(".perfil").find(".fa-li");
            if (ico.hasClass("fa-check-square")) { //descheckear
                ico.removeClass("fa-check-square").addClass("fa-square-o");
            } else { //checkear
                ico.removeClass("fa-square-o").addClass("fa-check-square");
            }
        });

        %{--$btnPerfiles.click(function () {--}%
            %{--var $frm = $("#frmPerfiles");--}%
            %{--var url = $frm.attr("action");--}%
            %{--var data = "id=${departamentoInstance.id}";--}%
            %{--var band = false;--}%
            %{--$(".perfil .fa-li").each(function () {--}%
                %{--var ico = $(this);--}%
                %{--if (ico.hasClass("fa-check-square")) {--}%
                    %{--data += "&perfil=" + ico.data("id");--}%
                    %{--band = true;--}%
                %{--}--}%
            %{--});--}%
            %{--if (!band) {--}%
                %{--bootbox.confirm("<i class='fa fa-warning fa-3x pull-left text-warning text-shadow'></i><p>No ha seleccionado ningún perfil. El usuario no podrá ingresar al sistema. ¿Desea continuar?.</p>", function (result) {--}%
                    %{--if (result) {--}%
                        %{--doSave(url, data);--}%
                    %{--}--}%
                %{--})--}%
            %{--} else {--}%
                %{--doSave(url, data);--}%
            %{--}--}%
            %{--return false;--}%
        %{--});--}%

    });
</script>

</body>
</html>