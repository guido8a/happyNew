<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 17/03/14
  Time: 01:15 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Observaciones - Trámite: + ${tramite?.codigo}</title>
    </head>

    <body>

        <p>
            ADVERTENCIA: El siguiente trámite está por ser
            ${params.tipo == "archivar" ? "archivado" : "anulado"}!
        </p>

        <g:if test="${params.tipo == 'anular' && hijos.size() > 0}">
            <p>
                Tenga en cuenta que se anularán los siguientes trámites derivados:
            <ul>
                <g:each in="${hijos}" var="hijo">
                    <li>${hijo.codigo} ${hijo.asunto}</li>
                </g:each>
            </ul>
            </p>
        </g:if>

        <label for="observacionArchivar">Trámite:</label> ${tramite?.codigo}

    <g:textArea name="observacionArchivar" maxlength="255" class="form-control" style="resize: none; height: 150px; width: 530px"
                value="${observacion?.observaciones}"/>

    </body>
</html>