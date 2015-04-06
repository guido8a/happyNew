<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 06/04/15
  Time: 03:25 PM
--%>


<i class='fa ${icon} fa-3x pull-left text-danger text-shadow'></i>
<util:renderHTML html="${msg}"/>
<div class="row">
    <div class="col-md-3"><strong>Solicitado por</strong></div>

    <div class="col-md-9">
        %{--<input type="text" class="form-control" id="aut"/>--}%
        <g:select class="form-control" name="aut" from="${personas}" optionKey="key" optionValue="value"/>
    </div>
</div>
<label for='observacion'>Observaciones:</label>
<textarea id="observacion" style="resize: none; height: 150px;" class="form-control" maxlength="255" name="observacion"></textarea>