<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Cargar usuarios del LDAP</title>
</head>
<body>
Se Procesaron ${users.size()} usuarios<br/>
${mod.size()} Fueron actualizados<br/>
<g:each in="${mod}" var="u">
    ${u.toString()} - ${u.login} - ${u.mail}<br/>
</g:each>
${nuevos.size()} nuevos usuarios fueron ingresados: <br/>
<g:each in="${nuevos}" var="u">
    ${u.toString()} - ${u.login} - ${u.mail}<br/>
</g:each>
<a href="${g.createLink(controller: 'departamento',action: 'arbol')}" class="btn btn-azul">Administrar</a>
<br/>
Usuarios NO registrados en el LDAP: <br/>
<g:each in="${reg}" var="u">
    <g:if test="${!users.contains(u)}">
        ${u.toString()} - ${u.login} - ${u.mail}<br/>
    </g:if>
</g:each>
</body>
</html>