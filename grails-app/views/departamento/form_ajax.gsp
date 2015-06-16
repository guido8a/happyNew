<%@ page import="org.omg.CORBA.NO_IMPLEMENT; happy.tramites.Departamento" %>

<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<g:if test="${!departamentoInstance}">
    <elm:notFound elem="Departamento" genero="o"/>
</g:if>
<g:else>
    <g:form class="form-horizontal" name="frmDepartamento" role="form" action="save_ajax" method="POST">
        <g:hiddenField name="id" value="${departamentoInstance?.id}"/>

    %{--<div class="form-group ${hasErrors(bean: departamentoInstance, field: 'tipoDepartamento', 'error')} ">--}%
    %{--<span class="grupo">--}%
    %{--<label for="tipoDepartamento" class="col-md-2 control-label text-info">--}%
    %{--Tipo Departamento--}%
    %{--</label>--}%

    %{--<div class="col-md-6">--}%
    %{--optionKey="id" optionValue="descripcion"--}%
    %{--value="${departamentoInstance?.tipoDepartamento?.id}" class="many-to-one form-control"/>--}%
    %{--</div>--}%

    %{--</span>--}%
    %{--</div>--}%

    %{--<g:if test="${departamentoInstance.padre}">--}%
        <div class="form-group ${hasErrors(bean: departamentoInstance, field: 'padre', 'error')} ">
            <span class="grupo">
                <label for="padre" class="col-md-2 control-label text-info">
                    Depende de
                </label>

                <div class="col-md-6">
                    <g:select id="padre" name="padre.id" from="${happy.tramites.Departamento.findAllByIdNotEqual(departamentoInstance.id, [sort: 'descripcion'])}"
                              optionKey="id" optionValue="descripcion" noSelection="['': '']"
                              value="${departamentoInstance?.padre?.id}" class="many-to-one form-control" style="width: 440px;"/>
                </div>
            </span>
        </div>
    %{--</g:if>--}%

        <div class="form-group ${hasErrors(bean: departamentoInstance, field: 'codigo', 'error')} required">
            <span class="grupo">
                <label for="codigo" class="col-md-2 control-label text-info">
                    Código
                </label>

                <div class="col-md-3" style="width: 120px;">
                    <g:if test="${tramites == 0}">
                        <g:textField name="codigo" maxlength="15" required="" class="form-control required allCaps"
                                     value="${departamentoInstance?.codigo}" style="width: 100px;"/>
                    </g:if>
                    <g:else>
                        <span class="uneditable-input">
                            ${departamentoInstance?.codigo}
                            <g:hiddenField name="codigo" value="${departamentoInstance?.codigo}"/>
                        </span>
                    </g:else>
                </div>
                *
            </span>
        </div>

        <div class="form-group ${hasErrors(bean: departamentoInstance, field: 'descripcion', 'error')} required">
            <span class="grupo">
                <label for="descripcion" class="col-md-2 control-label text-info">
                    Descripción
                </label>

                <div class="col-md-6">
                    <g:textField name="descripcion" maxlength="128" required="" class="form-control required allCaps"
                                 value="${departamentoInstance?.descripcion}" style="width: 445px;"/>
                </div>
                *
            </span>
        </div>

        <div class="form-group ${hasErrors(bean: departamentoInstance, field: 'externo', 'error')} required">
            <span class="grupo">

                <label for="externo" class="col-md-2 control-label text-info">
                    Externo
                </label>
                <div class="col-md-3">
                <g:select name="externo" from="${[1: 'SI', 0: 'NO']}" optionKey="key" optionValue="value"
                              class="form-control" value="${departamentoInstance.externo}" style="width: 80px;"/>
                </div>
                *
            </span>
            <span class="grupo" style="margin-left: 30px">

                <label for="externo" class="col-md-2 control-label text-info">
                    Remoto
                </label>
                <div class="col-md-3">
                    <g:select name="remoto" from="${[1: 'SI', 0: 'NO']}" optionKey="key" optionValue="value"
                              class="form-control" value="${departamentoInstance.remoto}"/>
                </div>
                <span style="margin-left: -320px">*</span>
            </span>
        </div>

        <div class="form-group ${hasErrors(bean: departamentoInstance, field: 'telefono', 'error')} ">
            <span class="grupo">
                <label for="telefono" class="col-md-2 control-label text-info">
                    Teléfono
                </label>

                <div class="col-md-6">
                    <g:textField name="telefono" maxlength="15" class="form-control allCaps" value="${departamentoInstance?.telefono}"
                                 style="width: 200px;"/>
                </div>

            </span>
        </div>

        <div class="form-group ${hasErrors(bean: departamentoInstance, field: 'extension', 'error')} ">
            <span class="grupo">
                <label for="extension" class="col-md-2 control-label text-info">
                    Extensión
                </label>

                <div class="col-md-6">
                    <g:textField name="extension" maxlength="7" class="form-control allCaps"
                                 value="${departamentoInstance?.extension}" style="width: 100px;"/>
                </div>

            </span>
        </div>

        <div class="form-group ${hasErrors(bean: departamentoInstance, field: 'direccion', 'error')} ">
            <span class="grupo">
                <label for="direccion" class="col-md-2 control-label text-info">
                    Ubicación
                </label>

                <div class="col-md-6">
                    <g:textArea name="direccion" cols="80" rows="3" maxlength="255" class="form-control"
                                value="${departamentoInstance?.direccion}" style="width: 440px;"/>
                </div>

            </span>
        </div>

    </g:form>

    <script type="text/javascript">
        var validator = $("#frmDepartamento").validate({
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
                codigo : {
                    remote : {
                        url  : "${createLink(action: 'validarCodigo_ajax')}",
                        type : "post",
                        data : {
                            id : "${departamentoInstance.id}"
                        }
                    }
                }
            },
            messages       : {
                codigo : {
                    remote : "Código ya en uso"
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
    </script>

</g:else>