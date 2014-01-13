<%@ page import="happy.tramites.PermisoUsuario" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!permisoUsuarioInstance}">
    <elm:notFound elem="PermisoUsuario" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmPermisoUsuario" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${permisoUsuarioInstance?.id}" />
        
        <div class="form-group ${hasErrors(bean: permisoUsuarioInstance, field: 'persona', 'error')} ">
            <span class="grupo">
                <label for="persona" class="col-md-2 control-label text-info">
                    Persona
                </label>
                <div class="col-md-6">
                    <g:select id="persona" name="persona.id" from="${happy.seguridad.Persona.list()}" optionKey="id" value="${permisoUsuarioInstance?.persona?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: permisoUsuarioInstance, field: 'permisoTramite', 'error')} ">
            <span class="grupo">
                <label for="permisoTramite" class="col-md-2 control-label text-info">
                    Permiso Tramite
                </label>
                <div class="col-md-6">
                    <g:select id="permisoTramite" name="permisoTramite.id" from="${happy.tramites.PermisoTramite.list()}" optionKey="id" value="${permisoUsuarioInstance?.permisoTramite?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: permisoUsuarioInstance, field: 'fechaInicio', 'error')} required">
            <span class="grupo">
                <label for="fechaInicio" class="col-md-2 control-label text-info">
                    Fecha Inicio
                </label>
                <div class="col-md-4">
                    <elm:datepicker name="fechaInicio" title="fechaInicio"  class="datepicker form-control required" value="${permisoUsuarioInstance?.fechaInicio}"  />
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: permisoUsuarioInstance, field: 'fechaFin', 'error')} ">
            <span class="grupo">
                <label for="fechaFin" class="col-md-2 control-label text-info">
                    Fecha Fin
                </label>
                <div class="col-md-4">
                    <elm:datepicker name="fechaFin" title="fechaFin"  class="datepicker form-control" value="${permisoUsuarioInstance?.fechaFin}" default="none" noSelection="['': '']" />
                </div>
                
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmPermisoUsuario").validate({
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