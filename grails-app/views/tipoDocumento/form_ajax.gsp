<%@ page import="happy.tramites.TipoDocumento" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!tipoDocumentoInstance}">
    <elm:notFound elem="TipoDocumento" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmTipoDocumento" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${tipoDocumentoInstance?.id}" />
        
        <div class="form-group ${hasErrors(bean: tipoDocumentoInstance, field: 'codigo', 'error')} required">
            <span class="grupo">
                <label for="codigo" class="col-md-2 control-label text-info">
                    Código
                </label>
                <div class="col-md-6">
                    <g:textField name="codigo" maxlength="4" required="" class="form-control required" value="${tipoDocumentoInstance?.codigo}"/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tipoDocumentoInstance, field: 'descripcion', 'error')} required">
            <span class="grupo">
                <label for="descripcion" class="col-md-2 control-label text-info">
                    Descripción
                </label>
                <div class="col-md-6">
                    <g:textField name="descripcion" maxlength="31" required="" class="form-control required" value="${tipoDocumentoInstance?.descripcion}"/>
                </div>
                 *
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmTipoDocumento").validate({
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