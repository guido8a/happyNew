<%@ page import="happy.tramites.TipoPrioridad" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!tipoPrioridadInstance}">
    <elm:notFound elem="TipoPrioridad" genero="o"/>
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmTipoPrioridad" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${tipoPrioridadInstance?.id}"/>

        <div class="form-group ${hasErrors(bean: tipoPrioridadInstance, field: 'codigo', 'error')} required">
            <span class="grupo">
                <label for="codigo" class="col-md-2 control-label text-info">
                    Código
                </label>

                <div class="col-md-2">
                    <g:if test="${!tipoPrioridadInstance?.codigo}">
                        <g:textField name="codigo" maxlength="4" required="" class="form-control required allCaps" value="${tipoPrioridadInstance?.codigo}"/>
                    </g:if>
                    <g:else>
                        <span class="uneditable-input">
                            ${tipoPrioridadInstance?.codigo}
                            <g:hiddenField name="codigo" value="${tipoPrioridadInstance?.codigo}"/>
                        </span>
                    </g:else>
                </div>
                *
            </span>
        </div>

        <div class="form-group ${hasErrors(bean: tipoPrioridadInstance, field: 'descripcion', 'error')} required">
            <span class="grupo">
                <label for="descripcion" class="col-md-2 control-label text-info">
                    Descripción
                </label>

                <div class="col-md-6">
                    <g:textField name="descripcion" maxlength="31" required="" class="form-control required allCaps" value="${tipoPrioridadInstance?.descripcion}"/>
                </div>
                *
            </span>
        </div>

        <div class="form-group ${hasErrors(bean: tipoPrioridadInstance, field: 'tiempo', 'error')} required">
            <span class="grupo">
                <label for="tiempo" class="col-md-2 control-label text-info">
                    Tiempo
                </label>

                <div class="col-md-4">
                    <div class="input-group">
                        <g:field name="tiempo" type="number" value="${tipoPrioridadInstance.tiempo}" class="digits form-control required" required=""/>
                        <span class="input-group-addon">horas</span>
                    </div>
                </div>
                *
            </span>
        </div>

    </g:form>

    <script type="text/javascript">
        var validator = $("#frmTipoPrioridad").validate({
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

            rules    : {
                codigo : {
                    remote : {
                        url  : "${createLink(action: 'validarCodigo_ajax')}",
                        type : "post",
                        data : {
                            id : "${tipoPrioridadInstance.id}"
                        }
                    }
                }
            },
            messages : {
                codigo : {
                    remote : "Código ya ingresado"
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