<%@ page import="happy.seguridad.Modulo" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!moduloInstance}">
    <elm:notFound elem="Modulo" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frm" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${moduloInstance?.id}" />
        
        <div class="form-group ${hasErrors(bean: moduloInstance, field: 'descripcion', 'error')} ">
            <span class="grupo">
                <label for="descripcion" class="col-md-2 control-label text-info">
                    Descripción
                </label>
                <div class="col-md-6">
                    <g:textField name="descripcion" class="form-control" value="${moduloInstance?.descripcion}"/>
                </div> *
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: moduloInstance, field: 'nombre', 'error')} ">
            <span class="grupo">
                <label for="nombre" class="col-md-2 control-label text-info">
                    Nombre
                </label>
                <div class="col-md-6">
                    <g:textField name="nombre" class="form-control" value="${moduloInstance?.nombre}"/>
                </div> *
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: moduloInstance, field: 'orden', 'error')} required">
            <span class="grupo">
                <label for="orden" class="col-md-2 control-label text-info">
                    Orden
                </label>
                <div class="col-md-2">
                    <g:field name="orden" type="number" value="${moduloInstance.orden}" class="digits form-control required" required=""/>
                </div>
                 *
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmModulo").validate({
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