<%@ page import="happy.utilitarios.Parametros" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!parametrosInstance}">
    <elm:notFound elem="Parametros" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmParametros" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${parametrosInstance?.id}" />

        <div class="panel panel-default">
        <p class="text-primary"><strong>Hora de inicio y fin de la jornada de trabajo</strong></p>
        <div class="form-group ${hasErrors(bean: parametrosInstance, field: 'horaInicio', 'error')} required">
            <span class="grupo">
                <label for="horaInicio" class="col-md-2 control-label text-info">
                    Hora Inicio
                </label>
                <div class="col-md-4">
                    <g:select name="horaInicio" from="${0..23}" value="${parametrosInstance.horaInicio ?: 8}"
                              optionValue="${{ it.toString().padLeft(2, '0') }}"/>
                    <g:select name="minutoInicio" from="${0..59}" value="${parametrosInstance.minutoInicio ?: 30}"
                              optionValue="${{ it.toString().padLeft(2, '0') }}"/>
                </div>
            </span>
            <span class="grupo">
                <label for="horaFin" class="col-md-2 control-label text-info">
                    Hora Fin
                </label>
                <div class="col-md-4">
                    <g:select name="horaFin" from="${0..23}" value="${parametrosInstance.horaFin ?: 16}"
                              optionValue="${{ it.toString().padLeft(2, '0') }}"/>
                    <g:select name="minutoFin" from="${0..59}" value="${parametrosInstance.minutoFin ?: 30}"
                              optionValue="${{ it.toString().padLeft(2, '0') }}"/>
                </div>
            </span>
        </div>
        </div>


%{--
        <div class="form-group ${hasErrors(bean: parametrosInstance, field: 'horaFin', 'error')} required">
            <span class="grupo">
                <label for="horaFin" class="col-md-2 control-label text-info">
                    Hora Fin
                </label>
                <div class="col-md-4">
                    <g:select name="horaFin" from="${0..23}" value="${parametrosInstance.horaFin ?: 16}"
                              optionValue="${{ it.toString().padLeft(2, '0') }}"/>
                    <g:select name="minutoFin" from="${0..59}" value="${parametrosInstance.minutoFin ?: 30}"
                              optionValue="${{ it.toString().padLeft(2, '0') }}"/>
                </div>
            </span>
        </div>
--}%
        <div class="panel panel-default">
                <p class="text-primary"><strong>Parámetros para el LDAP</strong></p>
                <div class="form-group ${hasErrors(bean: parametrosInstance, field: 'ipLDAP', 'error')} required">
            <span class="grupo">
                <label for="ipLDAP" class="col-md-2 control-label text-info">
                    Ip LDAP
                </label>
                <div class="col-md-6" style="width: 200px">
                    <g:textField name="ipLDAP" required="" class="form-control required" value="${parametrosInstance?.ipLDAP}" maxlength="20"/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: parametrosInstance, field: 'ouPrincipal', 'error')} required">
            <span class="grupo">
                <label for="ouPrincipal" class="col-md-2 control-label text-info">
                    OU Principal
                </label>
                <div class="col-md-9">
                    <g:textField name="ouPrincipal" required="" class="form-control required" value="${parametrosInstance?.ouPrincipal}" maxlength="63" />
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
                    <g:textArea name="textoCn" required="" class="form-control required" value="${parametrosInstance?.textoCn}"
                                style="resize:none; width: 440px; height: 100px; font-family: 'Courier New', Courier, monospace; font-size: 12px;
                                font-weight: bold; " maxlength="511"/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: parametrosInstance, field: 'passAdm', 'error')} required">
            <span class="grupo">
                <label for="passAdm" class="col-md-2 control-label text-info">
                    Clave Administrador
                </label>
                <div class="col-md-6">
                    <g:textField name="passAdm" required="" class="form-control required" value="${parametrosInstance?.passAdm}" maxlength="31"/>
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
                    <g:textField name="imagenes" required="" class="form-control required" value="${parametrosInstance?.imagenes}" style="width:400px;" maxlength="255"/>
                </div>
                 *
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmParametros").validate({
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