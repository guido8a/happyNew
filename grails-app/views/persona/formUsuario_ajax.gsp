<%@ page import="happy.seguridad.Persona" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>

<div class="col2">
    <g:if test="${!personaInstance}">
        <elm:notFound elem="Persona" genero="o"/>
    </g:if>
    <g:else>
        <g:form class="form-horizontal" name="frmPersona" role="form" action="save" method="POST">
            <g:hiddenField name="id" value="${personaInstance?.id}"/>

            <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'fechaInicio', 'error')} ">
                <span class="grupo">
                    <label for="login" class="col-md-3 control-label text-info">
                        Login
                    </label>

                    <div class="col-md-4">
                        <g:textField name="login" maxlength="15" class="form-control allCaps" value="${personaInstance?.login}"/>
                    </div>

                </span>
            </div>
            <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'fechaInicio', 'error')} ">
                <span class="grupo">
                    <label for="fechaInicio" class="col-md-3 control-label text-info">
                        Fecha Inicio
                    </label>

                    <div class="col-md-4">
                        <elm:datepicker name="fechaInicio" title="fechaInicio" class="datepicker form-control" value="${personaInstance?.fechaInicio}" default="none" noSelection="['': '']"/>
                    </div>

                </span>
            </div>

            <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'fechaFin', 'error')} ">
                <span class="grupo">
                    <label for="fechaFin" class="col-md-3 control-label text-info">
                        Fecha Fin
                    </label>

                    <div class="col-md-4">
                        <elm:datepicker name="fechaFin" title="fechaFin" class="datepicker form-control" value="${personaInstance?.fechaFin}" default="none" noSelection="['': '']"/>
                    </div>

                </span>
            </div>

%{--
            <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'cargo', 'error')} ">
                <span class="grupo">
                    <label for="cargo" class="col-md-3 control-label text-info">
                        Cargo
                    </label>

                    <div class="col-md-8">
                        <g:textField name="cargo" maxlength="127" class="form-control allCaps" value="${personaInstance?.cargo}"/>
                    </div>

                </span>
            </div>
--}%

            <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'activo', 'error')} required">
                <span class="grupo">
                    <label for="activo" class="col-md-3 control-label text-info">
                        Activo
                    </label>

                    <div class="col-md-3">
                        %{--<g:field name="activo" type="number" value="${personaInstance.activo}" class="digits form-control required" required=""/>--}%
                        <g:select name="activo" from="[0: 'NO', 1: 'SI']" value="${personaInstance.activo}" class="form-control required" required=""
                                  optionKey="key" optionValue="value"/>
                    </div>
                    *
                </span>
            </div>

            <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'fechaCambioPass', 'error')} ">
                <span class="grupo">
                    <label for="fechaCambioPass" class="col-md-3 control-label text-info">
                        Fecha Cambio de Contraseña
                    </label>

                    <div class="col-md-4">
                        <elm:datepicker name="fechaCambioPass" title="fechaCambioPass" class="datepicker form-control" value="${personaInstance?.fechaCambioPass}" default="none" noSelection="['': '']"/>
                    </div>

                </span>
            </div>

        </g:form>

        <script type="text/javascript">
            var validator = $("#frmPersona").validate({
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
</div>