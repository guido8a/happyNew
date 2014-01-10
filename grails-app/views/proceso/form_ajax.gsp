<%@ page import="happy.Proceso" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!procesoInstance}">
    <elm:notFound elem="Proceso" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmProceso" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${procesoInstance?.id}" />
        
        <div class="form-group ${hasErrors(bean: procesoInstance, field: 'tipoProceso', 'error')} ">
            <span class="grupo">
                <label for="tipoProceso" class="col-md-2 control-label text-info">
                    Tipo Proceso
                </label>
                <div class="col-md-6">
                    <g:select id="tipoProceso" name="tipoProceso.id" from="${happy.TipoProceso.list()}" optionKey="id" value="${procesoInstance?.tipoProceso?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: procesoInstance, field: 'nombre', 'error')} required">
            <span class="grupo">
                <label for="nombre" class="col-md-2 control-label text-info">
                    Nombre
                </label>
                <div class="col-md-6">
                    <g:textArea name="nombre" cols="40" rows="5" maxlength="255" required="" class="form-control required" value="${procesoInstance?.nombre}"/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: procesoInstance, field: 'descripcion', 'error')} ">
            <span class="grupo">
                <label for="descripcion" class="col-md-2 control-label text-info">
                    Descripcion
                </label>
                <div class="col-md-6">
                    <g:textArea name="descripcion" cols="40" rows="5" maxlength="1023" class="form-control" value="${procesoInstance?.descripcion}"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: procesoInstance, field: 'numero', 'error')} required">
            <span class="grupo">
                <label for="numero" class="col-md-2 control-label text-info">
                    Numero
                </label>
                <div class="col-md-2">
                    <g:field name="numero" type="number" value="${procesoInstance.numero}" class="digits form-control required" required=""/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: procesoInstance, field: 'tiempo', 'error')} required">
            <span class="grupo">
                <label for="tiempo" class="col-md-2 control-label text-info">
                    Tiempo
                </label>
                <div class="col-md-2">
                    <g:field name="tiempo" type="number" value="${procesoInstance.tiempo}" class="digits form-control required" required=""/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: procesoInstance, field: 'fecha', 'error')} ">
            <span class="grupo">
                <label for="fecha" class="col-md-2 control-label text-info">
                    Fecha
                </label>
                <div class="col-md-4">
                    <elm:datepicker name="fecha"  class="datepicker form-control" value="${procesoInstance?.fecha}" default="none" noSelection="['': '']" />
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: procesoInstance, field: 'observaciones', 'error')} ">
            <span class="grupo">
                <label for="observaciones" class="col-md-2 control-label text-info">
                    Observaciones
                </label>
                <div class="col-md-6">
                    <g:textArea name="observaciones" cols="40" rows="5" maxlength="255" class="form-control" value="${procesoInstance?.observaciones}"/>
                </div>
                
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmProceso").validate({
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