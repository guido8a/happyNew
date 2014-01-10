<%@ page import="happy.PasoProceso" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!pasoProcesoInstance}">
    <elm:notFound elem="PasoProceso" genero="o" />
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmPasoProceso" role="form" action="save" method="POST">
        <g:hiddenField name="id" value="${pasoProcesoInstance?.id}" />
        
        <div class="form-group ${hasErrors(bean: pasoProcesoInstance, field: 'proceso', 'error')} ">
            <span class="grupo">
                <label for="proceso" class="col-md-2 control-label text-info">
                    Proceso
                </label>
                <div class="col-md-6">
                    <g:select id="proceso" name="proceso.id" from="${happy.Proceso.list()}" optionKey="id" value="${pasoProcesoInstance?.proceso?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: pasoProcesoInstance, field: 'padre', 'error')} ">
            <span class="grupo">
                <label for="padre" class="col-md-2 control-label text-info">
                    Padre
                </label>
                <div class="col-md-6">
                    <g:select id="padre" name="padre.id" from="${happy.PasoProceso.list()}" optionKey="id" value="${pasoProcesoInstance?.padre?.id}" class="many-to-one form-control" noSelection="['null': '']"/>
                </div>
                
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: pasoProcesoInstance, field: 'nombre', 'error')} required">
            <span class="grupo">
                <label for="nombre" class="col-md-2 control-label text-info">
                    Nombre
                </label>
                <div class="col-md-6">
                    <g:textArea name="nombre" cols="40" rows="5" maxlength="255" required="" class="form-control required" value="${pasoProcesoInstance?.nombre}"/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: pasoProcesoInstance, field: 'orden', 'error')} required">
            <span class="grupo">
                <label for="orden" class="col-md-2 control-label text-info">
                    Orden
                </label>
                <div class="col-md-2">
                    <g:field name="orden" type="number" value="${pasoProcesoInstance.orden}" class="digits form-control required" required=""/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: pasoProcesoInstance, field: 'tiempo', 'error')} required">
            <span class="grupo">
                <label for="tiempo" class="col-md-2 control-label text-info">
                    Tiempo
                </label>
                <div class="col-md-2">
                    <g:field name="tiempo" type="number" value="${pasoProcesoInstance.tiempo}" class="digits form-control required" required=""/>
                </div>
                 *
            </span>
        </div>
        
        <div class="form-group ${hasErrors(bean: pasoProcesoInstance, field: 'funciones', 'error')} ">
            <span class="grupo">
                <label for="funciones" class="col-md-2 control-label text-info">
                    Funciones
                </label>
                <div class="col-md-6">
                    <g:textArea name="funciones" cols="40" rows="5" maxlength="255" class="form-control" value="${pasoProcesoInstance?.funciones}"/>
                </div>
                
            </span>
        </div>
        
    </g:form>

    <script type="text/javascript">
        var validator = $("#frmPasoProceso").validate({
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