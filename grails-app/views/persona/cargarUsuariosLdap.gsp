<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Cargar/Actualizar usuarios desde el LDAP</title>
</head>
<body>
<h1>Cargar/Actualizar usuarios del LDAP</h1>
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
<g:if test="${noNombre.size()>0}">
<b>Usuarios sin el campo nombre en el LDAP:</b><br/>
    <g:each in="${noNombre}" var="u">
        ${u.nombre}<br/>
    </g:each>
</g:if>
<g:if test="${noApellido.size()>0}">
    <b>Usuarios sin el campo apellido en el LDAP:</b><br/>
    <g:each in="${noApellido}" var="u">
        ${u.nombre}<br/>
    </g:each>
</g:if>
<g:if test="${noMail.size()>0}">
    <b>Usuarios sin el campo mail en el LDAP:</b><br/>
    <g:each in="${noMail}" var="u">
        ${u.nombre}<br/>
    </g:each>
</g:if>
<a href="${g.createLink(controller: 'departamento',action: 'arbol')}" class="btn btn-azul">Administrar</a>
</body>
</html>