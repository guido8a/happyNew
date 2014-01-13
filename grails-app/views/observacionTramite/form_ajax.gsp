<%@ page import="happy.tramites.ObservacionTramite" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!observacionTramiteInstance}">
    <elm:notFound elem="ObservacionTramite" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmObservacionTramite" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${observacionTramiteInstance?.id}" />
        
        <div class="form-group ${hasErrors(bean: observacionTramiteInstance, field: 'tramite', 'error')} ">
            <span class="grupo">
                <label for="tramite" class="col-md-2 control-label text-info">
                    Tramite
                </label>
                <div class="col-md-6">
                    <g:select id="tramite" name="tramite.id" from="${happy.tramites.Tramite.list()}" optionKey="id" value="${observacionTramiteInstance?.tramite?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: observacionTramiteInstance, field: 'persona', 'error')} ">
            <span class="grupo">
                <label for="persona" class="col-md-2 control-label text-info">
                    Persona
                </label>
                <div class="col-md-6">
                    <g:select id="persona" name="persona.id" from="${happy.seguridad.Persona.list()}" optionKey="id" value="${observacionTramiteInstance?.persona?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: observacionTramiteInstance, field: 'fecha', 'error')} required">
            <span class="grupo">
                <label for="fecha" class="col-md-2 control-label text-info">
                    Fecha
                </label>
                <div class="col-md-4">
                    <elm:datepicker name="fecha" title="fecha"  class="datepicker form-control required" value="${observacionTramiteInstance?.fecha}"  />
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: observacionTramiteInstance, field: 'observaciones', 'error')} required">
            <span class="grupo">
                <label for="observaciones" class="col-md-2 control-label text-info">
                    Observaciones
                </label>
                <div class="col-md-6">
                    <g:textArea name="observaciones" cols="40" rows="5" maxlength="1023" required="" class="form-control required" value="${observacionTramiteInstance?.observaciones}"/>
                </div>
                 *
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmObservacionTramite").validate({
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