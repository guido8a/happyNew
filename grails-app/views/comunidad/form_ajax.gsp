<%@ page import="happy.geografia.Comunidad" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!comunidadInstance}">
    <elm:notFound elem="Comunidad" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmComunidad" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${comunidadInstance?.id}" />
        
        <div class="form-group ${hasErrors(bean: comunidadInstance, field: 'parroquia', 'error')} ">
            <span class="grupo">
                <label for="parroquia" class="col-md-2 control-label text-info">
                    Parroquia
                </label>
                <div class="col-md-6">
                    <g:select id="parroquia" name="parroquia.id" from="${happy.geografia.Parroquia.list()}" optionKey="id" value="${comunidadInstance?.parroquia?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: comunidadInstance, field: 'numero', 'error')} required">
            <span class="grupo">
                <label for="numero" class="col-md-2 control-label text-info">
                    Numero
                </label>
                <div class="col-md-6">
                    <g:textField name="numero" maxlength="8" required="" class="form-control required" value="${comunidadInstance?.numero}"/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: comunidadInstance, field: 'nombre', 'error')} required">
            <span class="grupo">
                <label for="nombre" class="col-md-2 control-label text-info">
                    Nombre
                </label>
                <div class="col-md-6">
                    <g:textField name="nombre" maxlength="63" required="" class="form-control required" value="${comunidadInstance?.nombre}"/>
                </div>
                 *
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmComunidad").validate({
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