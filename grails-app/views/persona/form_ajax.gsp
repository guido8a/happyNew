<%@ page import="happy.seguridad.Persona" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>

<div class="col2">
    <g:if test="${!personaInstance}">
        <elm:notFound elem="Persona" genero="o"/>
    </g:if>
    <g:else>
        <g:form class="form-horizontal" name="frmPersona" role="form" action="save" method="POST">
            <g:hiddenField name="id" value="${personaInstance?.id}"/>

            <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'cedula', 'error')} required">
                <span class="grupo">
                    <label for="cedula" class="col-md-3 control-label text-info">
                        Cédula
                    </label>

                    <div class="col-md-6">
                        <g:textField name="cedula" maxlength="10" required="" cedula="true" class="form-control required" value="${personaInstance?.cedula}"/>
                    </div>
                    *
                </span>
            </div>

            <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'nombre', 'error')} required">
                <span class="grupo">
                    <label for="nombre" class="col-md-3 control-label text-info">
                        Nombre
                    </label>

                    <div class="col-md-6">
                        <g:textField name="nombre" maxlength="31" required="" class="form-control required allCaps" value="${personaInstance?.nombre}"/>
                    </div>
                    *
                </span>
            </div>

            <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'apellido', 'error')} required">
                <span class="grupo">
                    <label for="apellido" class="col-md-3 control-label text-info">
                        Apellido
                    </label>

                    <div class="col-md-6">
                        <g:textField name="apellido" maxlength="31" required="" class="form-control required allCaps" value="${personaInstance?.apellido}"/>
                    </div>
                    *
                </span>
            </div>

            <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'sigla', 'error')} ">
                <span class="grupo">
                    <label for="sigla" class="col-md-3 control-label text-info">
                        Sigla
                    </label>

                    <div class="col-md-6">
                        <g:textField name="sigla" maxlength="4" class="form-control allCaps" value="${personaInstance?.sigla}"/>
                    </div>

                </span>
            </div>

            <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'titulo', 'error')} ">
                <span class="grupo">
                    <label for="titulo" class="col-md-3 control-label text-info">
                        Título
                    </label>

                    <div class="col-md-6">
                        <g:textField name="titulo" maxlength="4" class="form-control allCaps" value="${personaInstance?.titulo}"/>
                    </div>

                </span>
            </div>

            <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'fechaNacimiento', 'error')} ">
                <span class="grupo">
                    <label for="fechaNacimiento" class="col-md-3 control-label text-info">
                        Fecha Nacimiento
                    </label>

                    <div class="col-md-4">
                        <elm:datepicker name="fechaNacimiento" title="fechaNacimiento" class="datepicker form-control" maxDate="-15y"
                                        value="${personaInstance?.fechaNacimiento}" default="none" noSelection="['': '']"/>
                        <g:datePicker name="test" />
                    </div>

                </span>
            </div>

            <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'mail', 'error')} ">
                <span class="grupo">
                    <label for="mail" class="col-md-3 control-label text-info">
                        E-mail
                    </label>

                    <div class="col-md-6">
                        <g:textField name="mail" maxlength="63" email="true" class="form-control allCaps" value="${personaInstance?.mail}"/>
                    </div>

                </span>
            </div>

            <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'telefono', 'error')} ">
                <span class="grupo">
                    <label for="telefono" class="col-md-3 control-label text-info">
                        Teléfono
                    </label>

                    <div class="col-md-6">
                        <g:textField name="telefono" maxlength="15" telefono="true" class="form-control" value="${personaInstance?.telefono}"/>
                    </div>

                </span>
            </div>

            <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'celular', 'error')} ">
                <span class="grupo">
                    <label for="celular" class="col-md-3 control-label text-info">
                        Celular
                    </label>

                    <div class="col-md-6">
                        <g:textField name="celular" maxlength="15" celular="true" class="form-control" value="${personaInstance?.celular}"/>
                    </div>

                </span>
            </div>

            <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'departamento', 'error')} ">
                <span class="grupo">
                    <label for="departamento" class="col-md-3 control-label text-info">
                        Departamento
                    </label>

                    <div class="col-md-6">
                        <g:select id="departamento" name="departamento.id" from="${happy.tramites.Departamento.list()}"
                                  optionKey="id" optionValue="descripcion"
                                  value="${personaInstance?.departamento?.id}" class="many-to-one form-control"/>
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
                },
                rules          : {
                    cedula : {
                        remote : {
                            url  : "${createLink(action: 'validarCedula_ajax')}",
                            type : "post",
                            data : {
                                id : "${personaInstance.id}"
                            }
                        }
                    }
                },
                messages       : {
                    cedula : {
                        remote : "Cédula ya ingresada"
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

            $("#apellido, #nombre").blur(function () {
                var nombre = $("#nombre").val();
                var apellido = $("#apellido").val();
                var sigla = (nombre + " " + apellido).acronym().toUpperCase();
                $("#sigla").val(sigla);
            });

        </script>

    </g:else>
</div>