<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 18/02/14
  Time: 04:52 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Observaciones - Trámite + ${tramite?.numero}</title>
</head>

<body>
   <label>Trámite: </label> ${tramite?.numero}

  <g:textArea name="observacion" maxlength="255" class="form-control" style="resize: none; height: 150px; width: 530px" value="${tramite?.observaciones}" />


</body>
</html>