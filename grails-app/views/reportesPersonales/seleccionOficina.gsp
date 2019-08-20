<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 16/08/19
  Time: 9:52
--%>
<label for="departamentos" class="col-md-1 control-label text-info">
    Departamentos
</label>
<g:select name="departamentos" class="form-control" from="${departamentos}" optionValue="descripcion" optionKey="id" id="departamento"/>


<div class="row">
        <label for="fechaInicio_name" class="col-md-2 control-label text-info">
            Fecha Inicio
        </label>

        <div class="col-md-4">
            <elm:datepicker name="fechaInicio_name" title="fechaInicio" id="fechaInicio" class="datepicker form-control"
                            default="none" noSelection="['': '']" value="01-06-2019"/>
        </div>

        <label for="fechaFin_name" class="col-md-2 control-label text-info">
            Fecha Fin
        </label>

        <div class="col-md-4">
            <elm:datepicker name="fechaFin_name" title="fechaFin" id="fechaFin" class="datepicker form-control"
                            default="none" noSelection="['': '']"/>
        </div>
</div>