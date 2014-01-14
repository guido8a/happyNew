<%@ page import="happy.tramites.Tramite" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!tramiteInstance}">
    <elm:notFound elem="Tramite" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmTramite" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${tramiteInstance?.id}" />
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'anio', 'error')} ">
            <span class="grupo">
                <label for="anio" class="col-md-2 control-label text-info">
                    Anio
                </label>
                <div class="col-md-6">
                    <g:select id="anio" name="anio.id" from="${happy.tramites.Anio.list()}" optionKey="id" value="${tramiteInstance?.anio?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'padre', 'error')} ">
            <span class="grupo">
                <label for="padre" class="col-md-2 control-label text-info">
                    Padre
                </label>
                <div class="col-md-6">
                    <g:select id="padre" name="padre.id" from="${happy.tramites.Tramite.list()}" optionKey="id" value="${tramiteInstance?.padre?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'tipoDocumento', 'error')} ">
            <span class="grupo">
                <label for="tipoDocumento" class="col-md-2 control-label text-info">
                    Tipo Documento
                </label>
                <div class="col-md-6">
                    <g:select id="tipoDocumento" name="tipoDocumento.id" from="${happy.tramites.TipoDocumento.list()}" optionKey="id" value="${tramiteInstance?.tipoDocumento?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'de', 'error')} ">
            <span class="grupo">
                <label for="de" class="col-md-2 control-label text-info">
                    De
                </label>
                <div class="col-md-6">
                    <g:select id="de" name="de.id" from="${happy.seguridad.Persona.list()}" optionKey="id" value="${tramiteInstance?.de?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'tipoPersona', 'error')} ">
            <span class="grupo">
                <label for="tipoPersona" class="col-md-2 control-label text-info">
                    Tipo Persona
                </label>
                <div class="col-md-6">
                    <g:select id="tipoPersona" name="tipoPersona.id" from="${happy.tramites.TipoPersona.list()}" optionKey="id" value="${tramiteInstance?.tipoPersona?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'estadoTramite', 'error')} ">
            <span class="grupo">
                <label for="estadoTramite" class="col-md-2 control-label text-info">
                    Estado Tramite
                </label>
                <div class="col-md-6">
                    <g:select id="estadoTramite" name="estadoTramite.id" from="${happy.tramites.EstadoTramite.list()}" optionKey="id" value="${tramiteInstance?.estadoTramite?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'tipoTramite', 'error')} ">
            <span class="grupo">
                <label for="tipoTramite" class="col-md-2 control-label text-info">
                    Tipo Tramite
                </label>
                <div class="col-md-6">
                    <g:select id="tipoTramite" name="tipoTramite.id" from="${happy.tramites.TipoTramite.list()}" optionKey="id" value="${tramiteInstance?.tipoTramite?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'origenTramite', 'error')} ">
            <span class="grupo">
                <label for="origenTramite" class="col-md-2 control-label text-info">
                    Origen Tramite
                </label>
                <div class="col-md-6">
                    <g:select id="origenTramite" name="origenTramite.id" from="${happy.tramites.OrigenTramite.list()}" optionKey="id" value="${tramiteInstance?.origenTramite?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'codigo', 'error')} ">
            <span class="grupo">
                <label for="codigo" class="col-md-2 control-label text-info">
                    Codigo
                </label>
                <div class="col-md-6">
                    <g:textField name="codigo" maxlength="20" class="form-control" value="${tramiteInstance?.codigo}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'numero', 'error')} required">
            <span class="grupo">
                <label for="numero" class="col-md-2 control-label text-info">
                    Numero
                </label>
                <div class="col-md-6">
                    <g:textField name="numero" maxlength="20" required="" class="form-control required" value="${tramiteInstance?.numero}"/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'fecha', 'error')} ">
            <span class="grupo">
                <label for="fecha" class="col-md-2 control-label text-info">
                    Fecha
                </label>
                <div class="col-md-4">
                    <elm:datepicker name="fecha" title="fecha"  class="datepicker form-control" value="${tramiteInstance?.fecha}" default="none" noSelection="['': '']" />
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'fechaLimiteRespuesta', 'error')} ">
            <span class="grupo">
                <label for="fechaLimiteRespuesta" class="col-md-2 control-label text-info">
                    Fecha Limite Respuesta
                </label>
                <div class="col-md-4">
                    <elm:datepicker name="fechaLimiteRespuesta" title="fechaLimiteRespuesta"  class="datepicker form-control" value="${tramiteInstance?.fechaLimiteRespuesta}" default="none" noSelection="['': '']" />
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'asunto', 'error')} ">
            <span class="grupo">
                <label for="asunto" class="col-md-2 control-label text-info">
                    Asunto
                </label>
                <div class="col-md-6">
                    <g:textArea name="asunto" cols="40" rows="5" maxlength="1023" class="form-control" value="${tramiteInstance?.asunto}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'anexo', 'error')} ">
            <span class="grupo">
                <label for="anexo" class="col-md-2 control-label text-info">
                    Anexo
                </label>
                <div class="col-md-6">
                    <g:textArea name="anexo" cols="40" rows="5" maxlength="1023" class="form-control" value="${tramiteInstance?.anexo}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'texto', 'error')} ">
            <span class="grupo">
                <label for="texto" class="col-md-2 control-label text-info">
                    Texto
                </label>
                <div class="col-md-6">
                    <g:textField name="texto" class="form-control" value="${tramiteInstance?.texto}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'ampliacionPlazo', 'error')} required">
            <span class="grupo">
                <label for="ampliacionPlazo" class="col-md-2 control-label text-info">
                    Ampliacion Plazo
                </label>
                <div class="col-md-2">
                    <g:field name="ampliacionPlazo" type="number" value="${tramiteInstance.ampliacionPlazo}" class="digits form-control required" required=""/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'fechaRespuesta', 'error')} ">
            <span class="grupo">
                <label for="fechaRespuesta" class="col-md-2 control-label text-info">
                    Fecha Respuesta
                </label>
                <div class="col-md-4">
                    <elm:datepicker name="fechaRespuesta" title="fechaRespuesta"  class="datepicker form-control" value="${tramiteInstance?.fechaRespuesta}" default="none" noSelection="['': '']" />
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'fechaIngreso', 'error')} ">
            <span class="grupo">
                <label for="fechaIngreso" class="col-md-2 control-label text-info">
                    Fecha Ingreso
                </label>
                <div class="col-md-4">
                    <elm:datepicker name="fechaIngreso" title="fechaIngreso"  class="datepicker form-control" value="${tramiteInstance?.fechaIngreso}" default="none" noSelection="['': '']" />
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'fechaModificacion', 'error')} ">
            <span class="grupo">
                <label for="fechaModificacion" class="col-md-2 control-label text-info">
                    Fecha Modificacion
                </label>
                <div class="col-md-4">
                    <elm:datepicker name="fechaModificacion" title="fechaModificacion"  class="datepicker form-control" value="${tramiteInstance?.fechaModificacion}" default="none" noSelection="['': '']" />
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'fechaLectura', 'error')} ">
            <span class="grupo">
                <label for="fechaLectura" class="col-md-2 control-label text-info">
                    Fecha Lectura
                </label>
                <div class="col-md-4">
                    <elm:datepicker name="fechaLectura" title="fechaLectura"  class="datepicker form-control" value="${tramiteInstance?.fechaLectura}" default="none" noSelection="['': '']" />
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'externo', 'error')} ">
            <span class="grupo">
                <label for="externo" class="col-md-2 control-label text-info">
                    Externo
                </label>
                <div class="col-md-6">
                    <g:textField name="externo" maxlength="1" class="form-control" value="${tramiteInstance?.externo}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'nota', 'error')} ">
            <span class="grupo">
                <label for="nota" class="col-md-2 control-label text-info">
                    Nota
                </label>
                <div class="col-md-6">
                    <g:textArea name="nota" cols="40" rows="5" maxlength="1023" class="form-control" value="${tramiteInstance?.nota}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: tramiteInstance, field: 'estado', 'error')} ">
            <span class="grupo">
                <label for="estado" class="col-md-2 control-label text-info">
                    Estado
                </label>
                <div class="col-md-6">
                    <g:textField name="estado" maxlength="1" class="form-control" value="${tramiteInstance?.estado}"/>
                </div>
                
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmTramite").validate({
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