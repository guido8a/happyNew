<%@ page import="happy.seguridad.Persona" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!personaInstance}">
    <elm:notFound elem="Persona" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmPersona" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${personaInstance?.id}" />
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'cedula', 'error')} ">
            <span class="grupo">
                <label for="cedula" class="col-md-2 control-label text-info">
                    Cedula
                </label>
                <div class="col-md-6">
                    <g:textField name="cedula" maxlength="10" class="form-control" value="${personaInstance?.cedula}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'nombre', 'error')} ">
            <span class="grupo">
                <label for="nombre" class="col-md-2 control-label text-info">
                    Nombre
                </label>
                <div class="col-md-6">
                    <g:textField name="nombre" maxlength="30" class="form-control" value="${personaInstance?.nombre}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'apellido', 'error')} ">
            <span class="grupo">
                <label for="apellido" class="col-md-2 control-label text-info">
                    Apellido
                </label>
                <div class="col-md-6">
                    <g:textField name="apellido" maxlength="30" class="form-control" value="${personaInstance?.apellido}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'fechaNacimiento', 'error')} ">
            <span class="grupo">
                <label for="fechaNacimiento" class="col-md-2 control-label text-info">
                    Fecha Nacimiento
                </label>
                <div class="col-md-4">
                    <elm:datepicker name="fechaNacimiento"  class="datepicker form-control" value="${personaInstance?.fechaNacimiento}" default="none" noSelection="['': '']" />
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'departamento', 'error')} ">
            <span class="grupo">
                <label for="departamento" class="col-md-2 control-label text-info">
                    Departamento
                </label>
                <div class="col-md-6">
                    <g:select id="departamento" name="departamento.id" from="${happy.Departamento.list()}" optionKey="id" value="${personaInstance?.departamento?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'fechaInicio', 'error')} ">
            <span class="grupo">
                <label for="fechaInicio" class="col-md-2 control-label text-info">
                    Fecha Inicio
                </label>
                <div class="col-md-4">
                    <elm:datepicker name="fechaInicio"  class="datepicker form-control" value="${personaInstance?.fechaInicio}" default="none" noSelection="['': '']" />
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'fechaFin', 'error')} ">
            <span class="grupo">
                <label for="fechaFin" class="col-md-2 control-label text-info">
                    Fecha Fin
                </label>
                <div class="col-md-4">
                    <elm:datepicker name="fechaFin"  class="datepicker form-control" value="${personaInstance?.fechaFin}" default="none" noSelection="['': '']" />
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'sigla', 'error')} ">
            <span class="grupo">
                <label for="sigla" class="col-md-2 control-label text-info">
                    Sigla
                </label>
                <div class="col-md-6">
                    <g:textField name="sigla" maxlength="3" class="form-control" value="${personaInstance?.sigla}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'titulo', 'error')} ">
            <span class="grupo">
                <label for="titulo" class="col-md-2 control-label text-info">
                    Titulo
                </label>
                <div class="col-md-6">
                    <g:textField name="titulo" maxlength="4" class="form-control" value="${personaInstance?.titulo}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'cargo', 'error')} ">
            <span class="grupo">
                <label for="cargo" class="col-md-2 control-label text-info">
                    Cargo
                </label>
                <div class="col-md-6">
                    <g:textField name="cargo" maxlength="50" class="form-control" value="${personaInstance?.cargo}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'login', 'error')} required">
            <span class="grupo">
                <label for="login" class="col-md-2 control-label text-info">
                    Login
                </label>
                <div class="col-md-6">
                    <g:textField name="login" maxlength="16" required="" class="form-control required" value="${personaInstance?.login}"/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'password', 'error')} required">
            <span class="grupo">
                <label for="password" class="col-md-2 control-label text-info">
                    Password
                </label>
                <div class="col-md-6">
                    <g:textField name="password" maxlength="63" required="" class="form-control required" value="${personaInstance?.password}"/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'autorizacion', 'error')} ">
            <span class="grupo">
                <label for="autorizacion" class="col-md-2 control-label text-info">
                    Autorizacion
                </label>
                <div class="col-md-6">
                    <g:textField name="autorizacion" maxlength="63" class="form-control" value="${personaInstance?.autorizacion}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'email', 'error')} ">
            <span class="grupo">
                <label for="email" class="col-md-2 control-label text-info">
                    Email
                </label>
                <div class="col-md-6">
                    <div class="input-group"><span class="input-group-addon"><i class="fa fa-envelope"></i></span><g:field type="email" name="email" class="form-control" value="${personaInstance?.email}"/></div>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'activo', 'error')} required">
            <span class="grupo">
                <label for="activo" class="col-md-2 control-label text-info">
                    Activo
                </label>
                <div class="col-md-2">
                    <g:field name="activo" type="number" value="${personaInstance.activo}" class="digits form-control required" required=""/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'fechaActualizacionPass', 'error')} ">
            <span class="grupo">
                <label for="fechaActualizacionPass" class="col-md-2 control-label text-info">
                    Fecha Actualizacion Pass
                </label>
                <div class="col-md-4">
                    <elm:datepicker name="fechaActualizacionPass"  class="datepicker form-control" value="${personaInstance?.fechaActualizacionPass}" default="none" noSelection="['': '']" />
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'telefono', 'error')} ">
            <span class="grupo">
                <label for="telefono" class="col-md-2 control-label text-info">
                    Telefono
                </label>
                <div class="col-md-6">
                    <g:textField name="telefono" maxlength="15" class="form-control" value="${personaInstance?.telefono}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'jefe', 'error')} required">
            <span class="grupo">
                <label for="jefe" class="col-md-2 control-label text-info">
                    Jefe
                </label>
                <div class="col-md-2">
                    <g:field name="jefe" type="number" value="${personaInstance.jefe}" class="digits form-control required" required=""/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'celular', 'error')} ">
            <span class="grupo">
                <label for="celular" class="col-md-2 control-label text-info">
                    Celular
                </label>
                <div class="col-md-6">
                    <g:textField name="celular" maxlength="15" class="form-control" value="${personaInstance?.celular}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'foto', 'error')} ">
            <span class="grupo">
                <label for="foto" class="col-md-2 control-label text-info">
                    Foto
                </label>
                <div class="col-md-6">
                    <g:textArea name="foto" cols="40" rows="5" maxlength="255" class="form-control" value="${personaInstance?.foto}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'codigo', 'error')} ">
            <span class="grupo">
                <label for="codigo" class="col-md-2 control-label text-info">
                    Codigo
                </label>
                <div class="col-md-6">
                    <g:textField name="codigo" maxlength="15" class="form-control" value="${personaInstance?.codigo}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'accesos', 'error')} ">
            <span class="grupo">
                <label for="accesos" class="col-md-2 control-label text-info">
                    Accesos
                </label>
                <div class="col-md-6">
                    
<ul class="one-to-many">
<g:each in="${personaInstance?.accesos?}" var="a">
    <li><g:link controller="accs" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="accs" action="create" params="['persona.id': personaInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'accs.label', default: 'Accs')])}</g:link>
</li>
</ul>

                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaInstance, field: 'sesiones', 'error')} ">
            <span class="grupo">
                <label for="sesiones" class="col-md-2 control-label text-info">
                    Sesiones
                </label>
                <div class="col-md-6">
                    
<ul class="one-to-many">
<g:each in="${personaInstance?.sesiones?}" var="s">
    <li><g:link controller="sesn" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="sesn" action="create" params="['persona.id': personaInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'sesn.label', default: 'Sesn')])}</g:link>
</li>
</ul>

                </div>
                
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmPersona").validate({
            errorClass     : "help-block",
            errorPlacement : function (error, element) {
                if (element.parent().hasClass("input-group")) {
                    error.insertAfter(element.parent());
                } else {
                    error.insertAfter(element);
                }
                element.parents(".grupo").addClass('has-error');
            },
            success        : function (label) {
                label.parents(".grupo").removeClass('has-error');
            }
        });
        $(".form-control").keydown(function (ev) {
            if (ev.keyCode == 13) {
                submitForm();
                return false;
            }
            return true;
        });
    </script>

</g:else>