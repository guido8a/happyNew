%{--<%----}%
  %{--Created by IntelliJ IDEA.--}%
  %{--User: gato--}%
  %{--Date: 26/02/14--}%
  %{--Time: 11:34 AM--}%
%{----%>--}%


%{--<%@ page contentType="text/html;charset=UTF-8" %>--}%
%{--<html>--}%
%{--<head>--}%
    %{--<meta name="layout" content="main">--}%
    %{--<title>BLOQUEADO</title>--}%
%{--</head>--}%

%{--<body>--}%



%{--<div class="row" style="margin-top: 0px; margin-left: 200px">--}%
    %{--<span class="grupo">--}%
        %{--<label class="alert alert-danger"--}%
               %{--style="text-align: center; float: left"><i class="fa fa-exclamation-triangle fa-4x"></i> Su cuenta se encuentra bloqueda, debido a que no han sido atendidos los siguientes trámites </label>--}%
    %{--</span>--}%
%{--</div>--}%

%{--<div class="row" style="margin-top: 5px; margin-left: 1px">--}%
    %{--<span class="grupo">--}%
%{--<g:each in="${tramitePasado}" var="tramite">--}%
    %{--<label class="well well-sm">Trámite: ${tramite?.numero}--}%
    %{--</label></g:each>--}%
%{--</span>--}%
%{--</div>--}%


%{--<div style="height: 450px"  class="container-celdas">--}%
    %{--<span class="grupo">--}%
        %{--<table class="table table-bordered table-condensed table-hover">--}%
            %{--<thead>--}%
            %{--<tr>--}%

                %{--<th class="cabecera">Documento</th>--}%

                %{--<th class="cabecera">De</th>--}%
                %{--<th class="cabecera">Creado Por</th>--}%
                %{--<th class="cabecera">Para</th>--}%
                %{--<th class="cabecera">Destinatario</th>--}%
                %{--<th class="cabecera">Prioridad</th>--}%
                %{--<th class="cabecera">Fecha Recepción</th>--}%
                %{--<th class="cabecera">Fecha Límite</th>--}%
                %{--<th class="cabecera">Fecha Envio</th>--}%
            %{--</tr>--}%

            %{--</thead>--}%
            %{--<tbody>--}%
            %{--<g:each in="${tramitesPasados}" var="tramite">--}%

                %{--<tr>--}%

                    %{--<td>${tramite?.numero}</td>--}%
                    %{--<td>${tramite?.de}</td>--}%
                    %{--<td>${tramite?.de?.departamento?.descripcion}</td>--}%
                    %{--<td></td>--}%
                    %{--<td></td>--}%
                    %{--<td>${tramite?.prioridad?.descripcion}</td>--}%
                    %{--<td>${tramite?.fechaIngreso}</td>--}%
                    %{--<td>${tramite?.fechaLimiteRespuesta}</td>--}%
                    %{--<td>${tramite?.fechaEnvio}</td>--}%

                %{--</tr>--}%
            %{--</g:each>--}%

            %{--</tbody>--}%
        %{--</table>--}%

    %{--</span>--}%

%{--</div>--}%

%{--</body>--}%
%{--</html>--}%