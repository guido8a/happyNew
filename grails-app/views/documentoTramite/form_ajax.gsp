<%@ page import="happy.tramites.DocumentoTramite" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!documentoTramiteInstance}">
    <elm:notFound elem="DocumentoTramite" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmDocumentoTramite" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${documentoTramiteInstance?.id}" />
        
        <div class="form-group ${hasErrors(bean: documentoTramiteInstance, field: 'tramite', 'error')} ">
            <span class="grupo">
                <label for="tramite" class="col-md-2 control-label text-info">
                    Tramite
                </label>
                <div class="col-md-6">
                    <g:select id="tramite" name="tramite.id" from="${happy.tramites.Tramite.list()}" optionKey="id" value="${documentoTramiteInstance?.tramite?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: documentoTramiteInstance, field: 'anexo', 'error')} ">
            <span class="grupo">
                <label for="anexo" class="col-md-2 control-label text-info">
                    Anexo
                </label>
                <div class="col-md-6">
                    <g:select id="anexo" name="anexo.id" from="${happy.tramites.Tramite.list()}" optionKey="id" value="${documentoTramiteInstance?.anexo?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: documentoTramiteInstance, field: 'fecha', 'error')} ">
            <span class="grupo">
                <label for="fecha" class="col-md-2 control-label text-info">
                    Fecha
                </label>
                <div class="col-md-4">
                    <elm:datepicker name="fecha" title="fecha"  class="datepicker form-control" value="${documentoTramiteInstance?.fecha}" default="none" noSelection="['': '']" />
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: documentoTramiteInstance, field: 'resumen', 'error')} ">
            <span class="grupo">
                <label for="resumen" class="col-md-2 control-label text-info">
                    Resumen
                </label>
                <div class="col-md-6">
                    <g:textArea name="resumen" cols="40" rows="5" maxlength="1024" class="form-control allCaps" value="${documentoTramiteInstance?.resumen}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: documentoTramiteInstance, field: 'clave', 'error')} ">
            <span class="grupo">
                <label for="clave" class="col-md-2 control-label text-info">
                    Clave
                </label>
                <div class="col-md-6">
                    <g:textField name="clave" maxlength="63" class="form-control allCaps" value="${documentoTramiteInstance?.clave}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: documentoTramiteInstance, field: 'path', 'error')} ">
            <span class="grupo">
                <label for="path" class="col-md-2 control-label text-info">
                    Path
                </label>
                <div class="col-md-6">
                    <g:textArea name="path" cols="40" rows="5" maxlength="1024" class="form-control allCaps" value="${documentoTramiteInstance?.path}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: documentoTramiteInstance, field: 'descripcion', 'error')} ">
            <span class="grupo">
                <label for="descripcion" class="col-md-2 control-label text-info">
                    Descripcion
                </label>
                <div class="col-md-6">
                    <g:textField name="descripcion" maxlength="63" class="form-control allCaps" value="${documentoTramiteInstance?.descripcion}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: documentoTramiteInstance, field: 'fechaLectura', 'error')} ">
            <span class="grupo">
                <label for="fechaLectura" class="col-md-2 control-label text-info">
                    Fecha Lectura
                </label>
                <div class="col-md-4">
                    <elm:datepicker name="fechaLectura" title="fechaLectura"  class="datepicker form-control" value="${documentoTramiteInstance?.fechaLectura}" default="none" noSelection="['': '']" />
                </div>
                
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmDocumentoTramite").validate({
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