<%@ page import="happy.tramites.RolPersonaTramite" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!rolPersonaTramiteInstance}">
    <elm:notFound elem="RolPersonaTramite" genero="o"/>
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmRolPersonaTramite" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${rolPersonaTramiteInstance?.id}"/>

        <div class="form-group ${hasErrors(bean: rolPersonaTramiteInstance, field: 'codigo', 'error')} required">
            <span class="grupo">
                <label for="codigo" class="col-md-2 control-label text-info">
                    Código
                </label>

                <div class="col-md-2">
                    <g:if test="${!rolPersonaTramiteInstance?.codigo}">
                        <g:textField name="codigo" maxlength="4" required="" class="form-control required allCaps" value="${rolPersonaTramiteInstance?.codigo}"/>
                    </g:if>
                    <g:else>
                        <span class="uneditable-input">
                            ${rolPersonaTramiteInstance?.codigo}
                            <g:hiddenField name="codigo" value="${rolPersonaTramiteInstance?.codigo}"/>
                        </span>
                    </g:else>
                </div>
                *
            </span>
        </div>

        <div class="form-group ${hasErrors(bean: rolPersonaTramiteInstance, field: 'descripcion', 'error')} required">
            <span class="grupo">
                <label for="descripcion" class="col-md-2 control-label text-info">
                    Descripción
                </label>

                <div class="col-md-6">
                    <g:textField name="descripcion" maxlength="63" required="" class="form-control required allCaps" value="${rolPersonaTramiteInstance?.descripcion}"/>
                </div>
                *
            </span>
        </div>

    </g:form>

    <script type="text/javascript">
        var validator = $("#frmRolPersonaTramite").validate({
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