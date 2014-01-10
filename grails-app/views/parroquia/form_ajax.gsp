<%@ page import="happy.geografia.Parroquia" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!parroquiaInstance}">
    <elm:notFound elem="Parroquia" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmParroquia" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${parroquiaInstance?.id}" />
        
        <div class="form-group ${hasErrors(bean: parroquiaInstance, field: 'canton', 'error')} ">
            <span class="grupo">
                <label for="canton" class="col-md-2 control-label text-info">
                    Canton
                </label>
                <div class="col-md-6">
                    <g:select id="canton" name="canton.id" from="${happy.geografia.Canton.list()}" optionKey="id" value="${parroquiaInstance?.canton?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: parroquiaInstance, field: 'codigo', 'error')} required">
            <span class="grupo">
                <label for="codigo" class="col-md-2 control-label text-info">
                    Codigo
                </label>
                <div class="col-md-6">
                    <g:textField name="codigo" maxlength="6" required="" class="form-control required" value="${parroquiaInstance?.codigo}"/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: parroquiaInstance, field: 'nombre', 'error')} required">
            <span class="grupo">
                <label for="nombre" class="col-md-2 control-label text-info">
                    Nombre
                </label>
                <div class="col-md-6">
                    <g:textField name="nombre" maxlength="63" required="" class="form-control required" value="${parroquiaInstance?.nombre}"/>
                </div>
                 *
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmParroquia").validate({
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