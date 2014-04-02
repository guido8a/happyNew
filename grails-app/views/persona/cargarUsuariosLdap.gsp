<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Cargar usuarios del LDAP</title>
</head>
<body>
<b>Se Procesaron ${users.size()} usuarios</b><br/>
<b>${mod.size()} Fueron actualizados: </b><br/>
<g:each in="${mod}" var="u">
    ${u.toString()} - ${u.login} - ${u.mail}<br/>
</g:each>
<b>${nuevos.size()} nuevos usuarios fueron ingresados:</b> <br/>
<g:each in="${nuevos}" var="u">
    ${u.toString()} - ${u.login} - ${u.mail}<br/>
</g:each>
<br/>
<b>Usuarios NO registrados en el LDAP:</b><br/>
<g:each in="${reg}" var="u">
    <g:if test="${!users.contains(u)}">
        ${u.toString()} - ${u.login} - ${u.mail}<br/>
    </g:if>
</g:each>
<a href="${g.createLink(controller: 'departamento',action: 'arbol')}" class="btn btn-azul">Administrar</a>
</body>
</html>