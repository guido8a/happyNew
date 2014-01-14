<%@ page import="happy.tramites.PersonaDocumentoTramite" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!permisoDocumentoTramiteInstance}">
    <elm:notFound elem="PermisoDocumentoTramite" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmPermisoDocumentoTramite" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${permisoDocumentoTramiteInstance?.id}" />
        
        <div class="form-group ${hasErrors(bean: permisoDocumentoTramiteInstance, field: 'tramite', 'error')} ">
            <span class="grupo">
                <label for="tramite" class="col-md-2 control-label text-info">
                    Tramite
                </label>
                <div class="col-md-6">
                    <g:select id="tramite" name="tramite.id" from="${happy.tramites.Tramite.list()}" optionKey="id" value="${permisoDocumentoTramiteInstance?.tramite?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: permisoDocumentoTramiteInstance, field: 'persona', 'error')} ">
            <span class="grupo">
                <label for="persona" class="col-md-2 control-label text-info">
                    Persona
                </label>
                <div class="col-md-6">
                    <g:select id="persona" name="persona.id" from="${happy.seguridad.Persona.list()}" optionKey="id" value="${permisoDocumentoTramiteInstance?.persona?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: permisoDocumentoTramiteInstance, field: 'permiso', 'error')} ">
            <span class="grupo">
                <label for="permiso" class="col-md-2 control-label text-info">
                    Permiso
                </label>
                <div class="col-md-6">
                    <g:textField name="permiso" maxlength="4" class="form-control" value="${permisoDocumentoTramiteInstance?.permiso}"/>
                </div>
                
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmPermisoDocumentoTramite").validate({
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