<%@ page import="happy.tramites.PermisoTramite" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!permisoTramiteInstance}">
    <elm:notFound elem="PermisoTramite" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmPermisoTramite" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${permisoTramiteInstance?.id}" />
        
        <div class="form-group ${hasErrors(bean: permisoTramiteInstance, field: 'codigo', 'error')} required">
            <span class="grupo">
                <label for="codigo" class="col-md-2 control-label text-info">
                    Codigo
                </label>
                <div class="col-md-3">
                    <g:textField name="codigo" maxlength="4" required="" class="allCaps form-control required"
                                 value="${permisoTramiteInstance?.codigo}" style="width:80px"/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: permisoTramiteInstance, field: 'descripcion', 'error')} required">
            <span class="grupo">
                <label for="descripcion" class="col-md-2 control-label text-info">
                    Descripcion
                </label>
                <div class="col-md-6">
                    <g:textField name="descripcion" maxlength="63" required="" class="allCaps form-control required" value="${permisoTramiteInstance?.descripcion}"/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: permisoTramiteInstance, field: 'texto', 'error')} required">
            <span class="grupo">
                <label for="texto" class="col-md-2 control-label text-info">
                    Texto
                </label>
                <div class="col-md-6">
                    <g:textArea name="texto" cols="80" rows="5" maxlength="255" required="" class="form-control required"
                                value="${permisoTramiteInstance?.texto}" style="width:440px"/>
                </div>
                 *
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmPermisoTramite").validate({
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