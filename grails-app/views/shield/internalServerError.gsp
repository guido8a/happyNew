<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 26/02/14
  Time: 11:34 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Error</title>
</head>

<body>

<div class="row" style="margin-top: 0px; margin-left: 200px">
    <span class="grupo">
        <label class="alert alert-danger"
               style="text-align: center; float: left"><i class="fa fa-exclamation-triangle fa-4x"></i> Ha ocurrido un error </label>
    </span>
</div>

${msn}

<g:if test="${error}">
    <span style="font-size: 16px"><b>Reporte este error al administrador del sistema de forma detallada.</b> </span><br>
    <g:if test="${session.usuario}">
        <span style="font-size: 16px"><b> Usuario:${session.usuario}  </b></span><br>
    </g:if>
    <span style="font-size: 16px"><b>Para continuar usando el sistema pulse <a href="${createLink(controller: 'inicio',action: 'index')}" id="regresar">Aquí</a></b></span><br>
%{--<span style="font-size: 16px"><b>Si desea borrar toda la información del sistema pulse  <a href="${createLink(controller: 'shield',action: 'prueba')}" id="prueba">Aquí</a></b></span><br>--}%
    <script type="text/javascript">
        $("#regresar").button()
        $("#prueba").button()
    </script>
    <div class="message" style="width: 840px;">
        <strong>Error ${request.'javax.servlet.error.status_code'}:</strong> ${request.'javax.servlet.error.message'.encodeAsHTML()}<br/>
        <strong>Servlet:</strong> ${request.'javax.servlet.error.servlet_name'}<br/>
        <strong>URI:</strong> ${request.'javax.servlet.error.request_uri'}<br/>
        <g:if test="${exception}">
            <strong>Exception Message:</strong> ${exception.message?.encodeAsHTML()} <br />
            <strong>Caused by:</strong> ${exception.cause?.message?.encodeAsHTML()} <br />
            <strong>Class:</strong> ${exception.className} <br />
            <strong>At Line:</strong> [${exception.lineNumber}] <br />
            <strong>Code Snippet:</strong><br />
            <div class="snippet">
                <g:each var="cs" in="${exception.codeSnippet}">
                    ${cs?.encodeAsHTML()}<br />
                </g:each>
            </div>
        </g:if>
    </div>
    <g:if test="${exception}">
        <h2>Stack Trace</h2>
        <div class="stack" style="width: 860px;background: rgba(254,0,0,0.2);padding-left: 15px;padding-top: 20px;padding-bottom: 20px; border: black dotted 1px">
            <g:each in="${exception.stackTraceLines}">${it.encodeAsHTML()}<br/></g:each>
        </div>
    </g:if>
</g:if>


</body>
</html>