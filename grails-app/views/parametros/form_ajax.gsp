<%@ page import="happy.utilitarios.Parametros" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!parametrosInstance}">
    <elm:notFound elem="Parametros" genero="o"/>
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmParametros" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${parametrosInstance?.id}"/>

        <div class="form-group ${hasErrors(bean: parametrosInstance, field: 'institucion', 'error')} required">
            <span class="grupo">
                <label for="institucion" class="col-md-2 control-label text-info">
                    Institución
                </label>

                <div class="col-md-6">
                    <g:textField name="institucion" required="" class="form-control required"
                                 value="${parametrosInstance?.institucion}" style="width:400px;" maxlength="255"/>
                </div>
                *
            </span>
        </div>


        %{--<div class="panel panel-default">--}%
            %{--<p class="text-primary"><strong>Hora de inicio y fin de la jornada de trabajo</strong></p>--}%

            <div class="form-group ${hasErrors(bean: parametrosInstance, field: 'horaInicio', 'error')} required">
                <span class="grupo">
                    <label for="horaInicio" class="col-md-3 control-label text-info" style="margin-top:-10px;">
                        Hora Inicio de la Jornada de Trabajo
                    </label>

                    <div class="col-md-3">
                        <g:select name="horaInicio" from="${0..23}" value="${parametrosInstance.horaInicio ?: 8}"
                                  optionValue="${{ it.toString().padLeft(2, '0') }}"/>
                        <g:select name="minutoInicio" from="${0..59}" value="${parametrosInstance.minutoInicio ?: 00}"
                                  optionValue="${{ it.toString().padLeft(2, '0') }}"/>
                    </div>
                </span>
                <span class="grupo">
                    <label for="horaFin" class="col-md-3 control-label text-info" style="margin-top:-10px;">
                        Hora Fin de la Jornada de Trabajo
                    </label>

                    <div class="col-md-3">
                        <g:select name="horaFin" from="${0..23}" value="${parametrosInstance.horaFin ?: 16}"
                                  optionValue="${{ it.toString().padLeft(2, '0') }}"/>
                        <g:select name="minutoFin" from="${0..59}" value="${parametrosInstance.minutoFin ?: 00}"
                                  optionValue="${{ it.toString().padLeft(2, '0') }}"/>
                    </div>
                </span>
            </div>
        %{--</div>--}%

        <div class="panel panel-default">
            <p class="text-primary"><strong>Parámetros para el LDAP</strong></p>

            <div class="form-group ${hasErrors(bean: parametrosInstance, field: 'ipLDAP', 'error')} required">
                <span class="grupo">
                    <label for="ipLDAP" class="col-md-2 control-label text-info">
                        IP LDAP y Puerto
                    </label>

                    <div class="col-md-6 grupo" style="width: 200px">
                        <g:textField name="ipLDAP" required="" class="form-control required"
                                     value="${parametrosInstance?.ipLDAP}" maxlength="20"/>
                    </div>
                </span>
                <span class="grupo">
                    <label for="passAdm" class="col-md-2 control-label text-info">
                        Clave
                    </label>

                    <div class="col-md-3">
                        <g:textField name="passAdm" required="" class="form-control required"
                                     value="${parametrosInstance?.passAdm}" maxlength="31"/>
                    </div>
                </span>
            </div>

            <div class="form-group ${hasErrors(bean: parametrosInstance, field: 'ouPrincipal', 'error')} required">
                <span class="grupo">
                    <label for="ouPrincipal" class="col-md-2 control-label text-info">
                        OU Principal
                    </label>

                    <div class="col-md-9">
                        <g:textField name="ouPrincipal" required="" class="form-control required"
                                     value="${parametrosInstance?.ouPrincipal}" maxlength="63"/>
                    </div>
                    *
                </span>
            </div>

            <div class="form-group ${hasErrors(bean: parametrosInstance, field: 'textoCn', 'error')} required">
                <span class="grupo">
                    <label for="textoCn" class="col-md-2 control-label text-info">
                        CN
                    </label>

                    <div class="col-md-7">
                        <g:textArea name="textoCn" required="" class="form-control required"
                                    value="${parametrosInstance?.textoCn}"
                                    style="resize:none; width: 440px; height: 100px; font-family: 'Courier New', Courier, monospace; font-size: 12px;
                                font-weight: bold; " maxlength="511"/>
                    </div>
                    *
                </span>
            </div>
        </div>

        <div class="form-group ${hasErrors(bean: parametrosInstance, field: 'imagenes', 'error')} required">
            <span class="grupo">
                <label for="imagenes" class="col-md-2 control-label text-info">
                    Ruta de Imágenes
                </label>

                <div class="col-md-6">
                    <g:textField name="imagenes" required="" class="form-control required"
                                 value="${parametrosInstance?.imagenes}" style="width:400px;" maxlength="255"/>
                </div>
                *
            </span>
        </div>


        <div class="form-group ${hasErrors(bean: parametrosInstance, field: 'bloqueo', 'error')} required">
            <span class="grupo">
                <label for="bloqueo" class="col-md-3 control-label text-info">
                    Bloqueo en Horas por no recepción
                </label>

                <div class="col-md-2">
                    <g:select name="bloqueo" from="${1..4}" value="${parametrosInstance?.bloqueo ?: 1}"
                    optionValue="${{ it.toString().padLeft(2, '0') }}"/>

%{--
                    <g:textField name="bloqueo" required="" class="form-control required"
                                 value="${parametrosInstance?.bloqueo}" style="width:60px;" maxlength="1"/>
--}%
                </div>
            </span>
            <span class="grupo">
                <label for="validaLDAP" class="col-md-4 control-label text-info">
                    Validar usuarios contra LDAP
                </label>

                <div class="col-md-2">
                    <g:select name="validaLDAP" from="[0: 'NO', 1: 'SI']" value="${parametrosInstance.validaLDAP}"
                              class="form-control required" required="" optionKey="key" optionValue="value"/>
%{--
                    <g:textField name="validaLDAP" required="" class="form-control required"
                                 value="${parametrosInstance?.validaLDAP}" style="width:100px;" maxlength="1"/>
--}%
                </div>
            </span>
        </div>
        <div class="form-group ${hasErrors(bean: parametrosInstance, field: 'telefono', 'error')} ">
            <span class="grupo">
                <label for="telefono" class="col-md-6 control-label text-info">
                    Teléfono para trámites Externos
                </label>
                <div class="col-md-6 required">
                    <g:textField name="telefono" required="" class="form-control required"
                                 value="${parametrosInstance?.telefono}" style="width:180px;" min-lenght="7"  maxlength="15"/>
                </div>
            </span>
        </div>
        <div class="form-group ${hasErrors(bean: parametrosInstance, field: 'departamentos', 'error')} ">
            <span class="grupo">
                <label for="telefono" class="col-md-2 control-label text-info">
                    Departamentos Externos
                </label>
                <div class="col-md-6">
                    <g:textField name="departamentos" class="form-control allCaps" value="${parametrosInstance?.departamentos}" maxlength="20" style="width: 400px"/>

                </div>
            </span>
            <label class="col-md-12 text-danger" style="margin-left: 90px">
            * Importante: Coloque las siglas de los departamentos separadas por comas.
        </label>
        </div>

    </g:form>




    <script type="text/javascript">
        var validator = $("#frmParametros").validate({
            errorClass: "help-block",
            errorPlacement: function (error, element) {
                if (element.parent().hasClass("input-group")) {
                    error.insertAfter(element.parent());
                } else {
                    error.insertAfter(element);
                }
                element.parents(".grupo").addClass('has-error');
            },
            success: function (label) {
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