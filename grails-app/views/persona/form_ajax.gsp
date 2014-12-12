<%@ page import="happy.seguridad.Persona" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>

<div class="col2">
    <g:if test="${!personaInstance}">
        <elm:notFound elem="Persona" genero="o"/>
    </g:if>
    <g:else>
        <g:form class="form-horizontal" name="frmPersona" role="form" action="save_ajax" method="POST">
            <g:hiddenField name="id" value="${personaInstance?.id}"/>

        %{--<div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'cedula', 'error')}">--}%
        %{--<span class="grupo">--}%
        %{--<label for="cedula" class="col-md-3 control-label text-info">--}%
        %{--Cédula--}%
        %{--</label>--}%

        %{--<div class="col-md-4">--}%
        %{--<g:textField name="cedula" maxlength="10" cedula="true" class="form-control" value="${personaInstance?.cedula}"/>--}%
        %{--</div>--}%
        %{--</span>--}%
        %{--</div>--}%
            <div class="keeptogether">
                <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'fechaInicio', 'error')} ">
                    <span class="grupo">
                        <label for="login" class="col-md-3 control-label text-info">
                            Usuario
                        </label>

                        <div class="col-md-4">
                            <g:textField name="login" maxlength="15" class="form-control" value="${personaInstance?.login}" required=""/>
                        </div>
                        *
                    </span>
                </div>

                <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'nombre', 'error')} required">
                    <span class="grupo">
                        <label for="nombre" class="col-md-3 control-label text-info">
                            Nombre
                        </label>

                        <div class="col-md-7">
                            <g:textField name="nombre" maxlength="31" required="" class="form-control required" value="${personaInstance?.nombre}"/>
                        </div>
                        *
                    </span>
                </div>

                <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'apellido', 'error')} required">
                    <span class="grupo">
                        <label for="apellido" class="col-md-3 control-label text-info">
                            Apellido
                        </label>

                        <div class="col-md-7">
                            <g:textField name="apellido" maxlength="31" required="" class="form-control required" value="${personaInstance?.apellido}"/>
                        </div>
                        *
                    </span>
                </div>

                %{--<div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'sigla', 'error')} ">--}%
                %{--<span class="grupo">--}%
                %{--<label for="sigla" class="col-md-3 control-label text-info">--}%
                %{--Sigla--}%
                %{--</label>--}%

                %{--<div class="col-md-3">--}%
                %{--<g:textField name="sigla" maxlength="4" class="form-control allCaps" value="${personaInstance?.sigla}"/>--}%
                %{--</div>--}%

                %{--</span>--}%
                %{--</div>--}%

                %{--
                            <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'titulo', 'error')} ">
                                <span class="grupo">
                                    <label for="titulo" class="col-md-3 control-label text-info">
                                        Título
                                    </label>

                                    <div class="col-md-3">
                                        <g:textField name="titulo" maxlength="4" class="form-control" value="${personaInstance?.titulo}"/>
                                    </div>

                                </span>
                            </div>
                --}%

                %{--<div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'fechaNacimiento', 'error')} ">--}%
                %{--<span class="grupo">--}%
                %{--<label for="fechaNacimiento" class="col-md-3 control-label text-info">--}%
                %{--Fecha Nacimiento--}%
                %{--</label>--}%

                %{--<div class="col-md-4">--}%
                %{--<elm:datepicker name="fechaNacimiento" title="fechaNacimiento" class="datepicker form-control" maxDate="-15y"--}%
                %{--value="${personaInstance?.fechaNacimiento}" default="none" noSelection="['': '']"/>--}%
                %{--</div>--}%

                %{--</span>--}%
                %{--</div>--}%

                <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'mail', 'error')} ">
                    <span class="grupo">
                        <label for="mail" class="col-md-3 control-label text-info">
                            E-mail
                        </label>

                        <div class="col-md-7">
                            <g:textField name="mail" maxlength="63" email="true" class="required form-control noCaps" value="${personaInstance?.mail}"/>
                        </div>
                        *
                    </span>
                </div>
            </div>

            <div class="keeptogether">
                <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'telefono', 'error')} ">
                    <span class="grupo">
                        <label for="telefono" class="col-md-3 control-label text-info">
                            Teléfonos
                        </label>

                        <div class="col-md-5">
                            %{--<g:textField name="telefono" maxlength="63" telefono="true" class="form-control" value="${personaInstance?.telefono}"/>--}%
                            <g:textField name="telefono" maxlength="63" class="form-control digits" value="${personaInstance?.telefono}" style="width: 300px;"/>
                        </div>

                    </span>
                </div>

                %{--<div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'celular', 'error')} ">--}%
                %{--<span class="grupo">--}%
                %{--<label for="celular" class="col-md-3 control-label text-info">--}%
                %{--Celular--}%
                %{--</label>--}%

                %{--<div class="col-md-5">--}%
                %{--<g:textField name="celular" maxlength="15" celular="true" class="form-control" value="${personaInstance?.celular}"/>--}%
                %{--</div>--}%

                %{--</span>--}%
                %{--</div>--}%

                <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'departamento', 'error')} ">
                    <span class="grupo">
                        <label for="departamento" class="col-md-3 control-label text-info">
                            Departamento
                        </label>

                        <div class="col-md-9">
                            <g:select id="departamento" name="departamento.id" from="${happy.tramites.Departamento.list([sort: 'descripcion'])}"
                                      optionKey="id" optionValue="descripcion"
                                      value="${personaInstance?.departamento?.id}" class="many-to-one form-control"/>
                        </div>

                    </span>
                </div>

            %{--<div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'cargo', 'error')} ">--}%
            %{--<span class="grupo">--}%
            %{--<label for="cargo" class="col-md-3 control-label text-info">--}%
            %{--Cargo--}%
            %{--</label>--}%

            %{--<div class="col-md-8">--}%
            %{--<g:textField name="cargo" maxlength="127" class="form-control allCaps" value="${personaInstance?.cargo}"/>--}%
            %{--</div>--}%

            %{--</span>--}%
            %{--</div>--}%

            %{--<div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'jefe', 'error')} required">--}%
            %{--<span class="grupo">--}%
            %{--<label for="jefe" class="col-md-3 control-label text-info">--}%
            %{--Es Autoridad--}%
            %{--</label>--}%

            %{--<div class="col-md-3">--}%
            %{--<g:field name="jefe" type="number" value="${personaInstance.jefe}" class="digits form-control required" required=""/>--}%
            %{--<g:select name="jefe" from="[0: 'NO', 1: 'SI']" value="${personaInstance.jefe}" class="form-control required span-2" required=""--}%
            %{--optionKey="key" optionValue="value"/>--}%
            %{--</div>--}%
            %{--*--}%
            %{--</span>--}%
            %{--</div>--}%

                <g:if test="${session.usuario.puedeAdmin && personaInstance.id && personaInstance?.puedeAdmin}">
                    <div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'password', 'error')} required">
                        <span class="grupo">
                            <label for="password" class="col-md-3 control-label text-info">
                                Password
                            </label>

                            <div class="col-md-5">
                                <div class="input-group">
                                    <g:passwordField name="password" class="form-control required" maxlength="15" value="${'pandagnaros'}"/>
                                    <span class="input-group-addon"><i class="fa fa-lock"></i></span>

                                </div>
                            </div>
                            *
                        </span>
                    </div>
                </g:if>

            </div>
            <!------------------------------------------------------------------------------------------------------------------------------->

        %{--<div class="form-group keeptogether ${hasErrors(bean: personaInstance, field: 'activo', 'error')} required">--}%
        %{--<span class="grupo">--}%
        %{--<label for="activo" class="col-md-3 control-label text-info">--}%
        %{--Activo--}%
        %{--</label>--}%

        %{--<div class="col-md-3">--}%
        %{--<g:field name="activo" type="number" value="${personaInstance.activo}" class="digits form-control required" required=""/>--}%
        %{--<g:select name="activo" from="[0: 'NO', 1: 'SI']" value="${personaInstance.activo}" class="form-control required" required=""--}%
        %{--optionKey="key" optionValue="value"/>--}%
        %{--</div>--}%
        %{--*--}%
        %{--</span>--}%
        %{--</div>--}%
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
                    },
                    mail   : {
                        remote : {
                            url  : "${createLink(action: 'validarMail_ajax')}",
                            type : "post",
                            data : {
                                id : "${personaInstance.id}"
                            }
                        }
                    },
                    login  : {
                        remote : {
                            url  : "${createLink(action: 'validarLogin_ajax')}",
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
                    },
                    mail   : {
                        remote : "E-mail ya registrado"
                    },
                    login  : {
                        remote : "Login ya registrado"
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
                var nombre = $.trim($("#nombre").val());
                var apellido = $.trim($("#apellido").val());
                if (nombre != "" || apellido != "") {
//                    var sigla = (nombre + " " + apellido).acronym();
                    var login = nombre.acronym() + "" + apellido.split(" ")[0];
//                    if ($.trim($("#sigla").val()) == "") {
//                        $("#sigla").val(sigla);
//                    }
                    if ($.trim($("#login").val()) == "") {
                        $("#login").val(login.toLowerCase());
                    }
                }
            });

        </script>

    </g:else>
</div>