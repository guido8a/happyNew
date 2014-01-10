<%@ page import="happy.geografia.Provincia" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!provinciaInstance}">
    <elm:notFound elem="Provincia" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmProvincia" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${provinciaInstance?.id}" />
        
        <div class="form-group ${hasErrors(bean: provinciaInstance, field: 'zona', 'error')} ">
            <span class="grupo">
                <label for="zona" class="col-md-2 control-label text-info">
                    Zona
                </label>
                <div class="col-md-6">
                    <g:select id="zona" name="zona.id" from="${happy.geografia.Zona.list()}" optionKey="id" value="${provinciaInstance?.zona?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: provinciaInstance, field: 'numero', 'error')} required">
            <span class="grupo">
                <label for="numero" class="col-md-2 control-label text-info">
                    Numero
                </label>
                <div class="col-md-6">
                    <g:textField name="numero" maxlength="2" required="" class="form-control required" value="${provinciaInstance?.numero}"/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: provinciaInstance, field: 'nombre', 'error')} ">
            <span class="grupo">
                <label for="nombre" class="col-md-2 control-label text-info">
                    Nombre
                </label>
                <div class="col-md-6">
                    <g:textField name="nombre" maxlength="63" class="form-control" value="${provinciaInstance?.nombre}"/>
                </div>
                
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmProvincia").validate({
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