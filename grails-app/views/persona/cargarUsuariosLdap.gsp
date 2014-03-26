<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Cargar usuarios del LDAP</title>
</head>
<body>
Se ingresaron ${users.size()} usuarios: <br/>
<g:each in="${users}" var="u">
${u.toString()} - ${u.login} - u.mail<br/>
</g:each>
<a href="${g.createLink(controller: 'departamento',action: 'arbol')}" class="btn btn-azul">Administrar</a>
<br/>
Se encontraron ${noReg?.size()} usuarios no regitrados en el LDAP:
<g:each in="${noReg}" var="u">
    ${u.toString()} - ${u.login} - u.mail<br/>
</g:each>
</body>
</html>