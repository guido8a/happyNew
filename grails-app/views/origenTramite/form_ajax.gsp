<%@ page import="happy.tramites.OrigenTramite" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!origenTramiteInstance}">
    <elm:notFound elem="OrigenTramite" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmOrigenTramite" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${origenTramiteInstance?.id}" />
        
        <div class="form-group ${hasErrors(bean: origenTramiteInstance, field: 'tipoPersona', 'error')} ">
            <span class="grupo">
                <label for="tipoPersona" class="col-md-2 control-label text-info">
                    Tipo Persona
                </label>
                <div class="col-md-6">
                    <g:select id="tipoPersona" name="tipoPersona.id" from="${happy.tramites.TipoPersona.list()}" optionKey="id" value="${origenTramiteInstance?.tipoPersona?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: origenTramiteInstance, field: 'cedula', 'error')} required">
            <span class="grupo">
                <label for="cedula" class="col-md-2 control-label text-info">
                    Cedula
                </label>
                <div class="col-md-6">
                    <g:textField name="cedula" maxlength="13" required="" class="form-control required" value="${origenTramiteInstance?.cedula}"/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: origenTramiteInstance, field: 'fecha', 'error')} ">
            <span class="grupo">
                <label for="fecha" class="col-md-2 control-label text-info">
                    Fecha
                </label>
                <div class="col-md-4">
                    <elm:datepicker name="fecha" title="fecha"  class="datepicker form-control" value="${origenTramiteInstance?.fecha}" default="none" noSelection="['': '']" />
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: origenTramiteInstance, field: 'nombre', 'error')} required">
            <span class="grupo">
                <label for="nombre" class="col-md-2 control-label text-info">
                    Nombre
                </label>
                <div class="col-md-6">
                    <g:textField name="nombre" maxlength="127" required="" class="form-control required" value="${origenTramiteInstance?.nombre}"/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: origenTramiteInstance, field: 'nombreContacto', 'error')} ">
            <span class="grupo">
                <label for="nombreContacto" class="col-md-2 control-label text-info">
                    Nombre Contacto
                </label>
                <div class="col-md-6">
                    <g:textField name="nombreContacto" maxlength="31" class="form-control" value="${origenTramiteInstance?.nombreContacto}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: origenTramiteInstance, field: 'apellidoContacto', 'error')} required">
            <span class="grupo">
                <label for="apellidoContacto" class="col-md-2 control-label text-info">
                    Apellido Contacto
                </label>
                <div class="col-md-6">
                    <g:textField name="apellidoContacto" maxlength="31" required="" class="form-control required" value="${origenTramiteInstance?.apellidoContacto}"/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: origenTramiteInstance, field: 'titulo', 'error')} ">
            <span class="grupo">
                <label for="titulo" class="col-md-2 control-label text-info">
                    Titulo
                </label>
                <div class="col-md-6">
                    <g:textField name="titulo" maxlength="4" class="form-control" value="${origenTramiteInstance?.titulo}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: origenTramiteInstance, field: 'cargo', 'error')} ">
            <span class="grupo">
                <label for="cargo" class="col-md-2 control-label text-info">
                    Cargo
                </label>
                <div class="col-md-6">
                    <g:textField name="cargo" maxlength="127" class="form-control" value="${origenTramiteInstance?.cargo}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: origenTramiteInstance, field: 'mail', 'error')} ">
            <span class="grupo">
                <label for="mail" class="col-md-2 control-label text-info">
                    Mail
                </label>
                <div class="col-md-6">
                    <g:textField name="mail" maxlength="63" class="form-control" value="${origenTramiteInstance?.mail}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: origenTramiteInstance, field: 'telefono', 'error')} ">
            <span class="grupo">
                <label for="telefono" class="col-md-2 control-label text-info">
                    Telefono
                </label>
                <div class="col-md-6">
                    <g:textField name="telefono" maxlength="63" class="form-control" value="${origenTramiteInstance?.telefono}"/>
                </div>
                
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmOrigenTramite").validate({
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