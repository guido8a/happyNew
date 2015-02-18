<%@ page import="happy.tramites.EstadoTramiteExterno" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!estadoTramiteExternoInstance}">
    <elm:notFound elem="EstadoTramiteExterno" genero="o"/>
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmEstadoTramiteExterno" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${estadoTramiteExternoInstance?.id}"/>

        <div class="form-group ${hasErrors(bean: estadoTramiteExternoInstance, field: 'codigo', 'error')} required">
            <span class="grupo">
                <label for="codigo" class="col-md-2 control-label text-info">
                    Código
                </label>

                <div class="col-md-2">
                    <g:if test="${!estadoTramiteExternoInstance?.codigo}">
                        <g:textField name="codigo" maxlength="4" required="" class="form-control required allCaps" value="${estadoTramiteExternoInstance?.codigo}"/>
                    </g:if>
                    <g:else>
                        <span class="uneditable-input">
                            ${estadoTramiteExternoInstance?.codigo}
                            <g:hiddenField name="codigo" value="${estadoTramiteExternoInstance?.codigo}"/>
                        </span>
                    </g:else>
                </div>
                *
            </span>
        </div>

        <div class="form-group ${hasErrors(bean: estadoTramiteExternoInstance, field: 'descripcion', 'error')} required">
            <span class="grupo">
                <label for="descripcion" class="col-md-2 control-label text-info">
                    Descripción
                </label>

                <div class="col-md-7">
                    <g:textField name="descripcion" maxlength="31" required="" class="allCaps form-control required" value="${estadoTramiteExternoInstance?.descripcion}"/>
                </div>
                *
            </span>
        </div>

    </g:form>

    <script type="text/javascript">
        var validator = $("#frmEstadoTramiteExterno").validate({
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
                            id : "${estadoTramiteExternoInstance.id}"
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