<%@ page import="happy.tramites.Departamento" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!departamentoInstance}">
    <elm:notFound elem="Departamento" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmDepartamento" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${departamentoInstance?.id}" />
        
        <div class="form-group ${hasErrors(bean: departamentoInstance, field: 'tipoDependencia', 'error')} ">
            <span class="grupo">
                <label for="tipoDependencia" class="col-md-2 control-label text-info">
                    Tipo Dependencia
                </label>
                <div class="col-md-6">
                    <g:select id="tipoDependencia" name="tipoDependencia.id" from="${happy.tramites.TipoDependencia.list()}" optionKey="id" value="${departamentoInstance?.tipoDependencia?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: departamentoInstance, field: 'padre', 'error')} ">
            <span class="grupo">
                <label for="padre" class="col-md-2 control-label text-info">
                    Padre
                </label>
                <div class="col-md-6">
                    <g:select id="padre" name="padre.id" from="${happy.tramites.Departamento.list()}" optionKey="id" value="${departamentoInstance?.padre?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: departamentoInstance, field: 'codigo', 'error')} required">
            <span class="grupo">
                <label for="codigo" class="col-md-2 control-label text-info">
                    Codigo
                </label>
                <div class="col-md-6">
                    <g:textField name="codigo" maxlength="6" required="" class="form-control required" value="${departamentoInstance?.codigo}"/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: departamentoInstance, field: 'descripcion', 'error')} required">
            <span class="grupo">
                <label for="descripcion" class="col-md-2 control-label text-info">
                    Descripcion
                </label>
                <div class="col-md-6">
                    <g:textField name="descripcion" maxlength="63" required="" class="form-control required" value="${departamentoInstance?.descripcion}"/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: departamentoInstance, field: 'telefono', 'error')} ">
            <span class="grupo">
                <label for="telefono" class="col-md-2 control-label text-info">
                    Telefono
                </label>
                <div class="col-md-6">
                    <g:textField name="telefono" maxlength="15" class="form-control" value="${departamentoInstance?.telefono}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: departamentoInstance, field: 'extension', 'error')} ">
            <span class="grupo">
                <label for="extension" class="col-md-2 control-label text-info">
                    Extension
                </label>
                <div class="col-md-6">
                    <g:textField name="extension" maxlength="7" class="form-control" value="${departamentoInstance?.extension}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: departamentoInstance, field: 'direccion', 'error')} ">
            <span class="grupo">
                <label for="direccion" class="col-md-2 control-label text-info">
                    Direccion
                </label>
                <div class="col-md-6">
                    <g:textArea name="direccion" cols="40" rows="5" maxlength="255" class="form-control" value="${departamentoInstance?.direccion}"/>
                </div>
                
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmDepartamento").validate({
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