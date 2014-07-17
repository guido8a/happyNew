<%@ page import="happy.tramites.EstadoTramite" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!estadoTramiteInstance}">
    <elm:notFound elem="EstadoTramite" genero="o"/>
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmEstadoTramite" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${estadoTramiteInstance?.id}"/>

        <div class="form-group ${hasErrors(bean: estadoTramiteInstance, field: 'codigo', 'error')} required">
            <span class="grupo">
                <label for="codigo" class="col-md-2 control-label text-info">
                    Código
                </label>

                <div class="col-md-2">
                    <g:if test="${!estadoTramiteInstance?.codigo}">
                        <g:textField name="codigo" maxlength="4" required="" class="form-control required allCaps" value="${estadoTramiteInstance?.codigo}"/>
                    </g:if>
                    <g:else>
                        <span class="uneditable-input">
                            ${estadoTramiteInstance?.codigo}
                            <g:hiddenField name="codigo" value="${estadoTramiteInstance?.codigo}"/>
                        </span>
                    </g:else>
                </div>
                *
            </span>
        </div>

        <div class="form-group ${hasErrors(bean: estadoTramiteInstance, field: 'descripcion', 'error')} required">
            <span class="grupo">
                <label for="descripcion" class="col-md-2 control-label text-info">
                    Descripción
                </label>

                <div class="col-md-6">
                    <g:textField name="descripcion" maxlength="31" required="" class="form-control required allCaps" value="${estadoTramiteInstance?.descripcion}"/>
                </div>
                *
            </span>
        </div>

    </g:form>

    <script type="text/javascript">
        var validator = $("#frmEstadoTramite").validate({
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