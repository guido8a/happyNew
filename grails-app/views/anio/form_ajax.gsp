<%@ page import="happy.tramites.Anio" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!anioInstance}">
    <elm:notFound elem="Anio" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmAnio" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${anioInstance?.id}" />
        
        <div class="form-group ${hasErrors(bean: anioInstance, field: 'numero', 'error')} required">
            <span class="grupo">
                <label for="numero" class="col-md-2 control-label text-info">
                    Año
                </label>
                <div class="col-md-6">
                    <g:textField name="numero" maxlength="4" required="" class="digits form-control required" value="${anioInstance?.numero}"/>
                </div>
                 *
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmAnio").validate({
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
            },
            rules          : {
                numero : {
                    remote : {
                        url  : "${createLink(action: 'validarAnio_ajax')}",
                        type : "post",
                        data : {
                            id : "${anioInstance.id}"
                        }
                    }
                }
            },
            messages       : {
                numero : {
                    remote : "Año ya ingresado"
                }
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