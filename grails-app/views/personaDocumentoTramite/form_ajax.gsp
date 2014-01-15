<%@ page import="happy.tramites.PersonaDocumentoTramite" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!personaDocumentoTramiteInstance}">
    <elm:notFound elem="PersonaDocumentoTramite" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmPersonaDocumentoTramite" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${personaDocumentoTramiteInstance?.id}" />
        
        <div class="form-group ${hasErrors(bean: personaDocumentoTramiteInstance, field: 'rolPersonaTramite', 'error')} ">
            <span class="grupo">
                <label for="rolPersonaTramite" class="col-md-2 control-label text-info">
                    Rol Persona Tramite
                </label>
                <div class="col-md-6">
                    <g:select id="rolPersonaTramite" name="rolPersonaTramite.id" from="${happy.tramites.RolPersonaTramite.list()}" optionKey="id" value="${personaDocumentoTramiteInstance?.rolPersonaTramite?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaDocumentoTramiteInstance, field: 'persona', 'error')} ">
            <span class="grupo">
                <label for="persona" class="col-md-2 control-label text-info">
                    Persona
                </label>
                <div class="col-md-6">
                    <g:select id="persona" name="persona.id" from="${happy.seguridad.Persona.list()}" optionKey="id" value="${personaDocumentoTramiteInstance?.persona?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaDocumentoTramiteInstance, field: 'tramite', 'error')} ">
            <span class="grupo">
                <label for="tramite" class="col-md-2 control-label text-info">
                    Tramite
                </label>
                <div class="col-md-6">
                    <g:select id="tramite" name="tramite.id" from="${happy.tramites.Tramite.list()}" optionKey="id" value="${personaDocumentoTramiteInstance?.tramite?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaDocumentoTramiteInstance, field: 'fecha', 'error')} ">
            <span class="grupo">
                <label for="fecha" class="col-md-2 control-label text-info">
                    Fecha
                </label>
                <div class="col-md-4">
                    <elm:datepicker name="fecha" title="fecha"  class="datepicker form-control" value="${personaDocumentoTramiteInstance?.fecha}" default="none" noSelection="['': '']" />
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaDocumentoTramiteInstance, field: 'observaciones', 'error')} ">
            <span class="grupo">
                <label for="observaciones" class="col-md-2 control-label text-info">
                    Observaciones
                </label>
                <div class="col-md-6">
                    <g:textArea name="observaciones" cols="40" rows="5" maxlength="1023" class="form-control allCaps" value="${personaDocumentoTramiteInstance?.observaciones}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: personaDocumentoTramiteInstance, field: 'permiso', 'error')} ">
            <span class="grupo">
                <label for="permiso" class="col-md-2 control-label text-info">
                    Permiso
                </label>
                <div class="col-md-6">
                    <g:textField name="permiso" maxlength="4" class="form-control allCaps" value="${personaDocumentoTramiteInstance?.permiso}"/>
                </div>
                
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmPersonaDocumentoTramite").validate({
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