<%@ page import="happy.tramites.Numero" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!numeroInstance}">
    <elm:notFound elem="Numero" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmNumero" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${numeroInstance?.id}" />
        
        <div class="form-group ${hasErrors(bean: numeroInstance, field: 'departamento', 'error')} ">
            <span class="grupo">
                <label for="departamento" class="col-md-2 control-label text-info">
                    Departamento
                </label>
                <div class="col-md-6">
                    <g:select id="departamento" name="departamento.id" from="${happy.tramites.Departamento.list()}" optionKey="id" value="${numeroInstance?.departamento?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: numeroInstance, field: 'tipoDocumento', 'error')} ">
            <span class="grupo">
                <label for="tipoDocumento" class="col-md-2 control-label text-info">
                    Tipo Documento
                </label>
                <div class="col-md-6">
                    <g:select id="tipoDocumento" name="tipoDocumento.id" from="${happy.tramites.TipoDocumento.list()}" optionKey="id" value="${numeroInstance?.tipoDocumento?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: numeroInstance, field: 'valor', 'error')} required">
            <span class="grupo">
                <label for="valor" class="col-md-2 control-label text-info">
                    Valor
                </label>
                <div class="col-md-2">
                    <g:field name="valor" type="number" value="${numeroInstance.valor}" class="digits form-control required" required=""/>
                </div>
                 *
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmNumero").validate({
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