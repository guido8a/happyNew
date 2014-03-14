<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 14/03/14
  Time: 10:31 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Observaciones - Trámite: + ${tramite?.codigo}</title>
</head>

<body>


<label>ADVERTENCIA: El siguiente trámite está por ser archivado!</label><br>

<label>Trámite: </label> ${tramite?.codigo}

<g:textArea name="observacionArchivar" maxlength="255" class="form-control" style="resize: none; height: 150px; width: 530px" value="${observacion?.observaciones}" />


</body>
</html>