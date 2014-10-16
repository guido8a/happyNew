<g:if test="${personasDoc.size() > 0}">
    <div class="alert alert-info" style="padding:5px;">
        <p>Ya se ha asignado el permiso de imprimir a <strong>${personasDoc.persona.login.join(', ')}</strong>.</p>

        <p>Si asigna permiso de imprimir a otro usuario se eliminará el anterior.</p>
    </div>
</g:if>

<label style='margin-left: 30px; margin-top: 30px'>Personal:</label>
<g:select from="${personal}" name="iden" optionKey="id" class="form-control"
          style="width: 300px; margin-left: 130px; margin-top: -30px"/>

<label style='margin-left: 30px; margin-top: 60px'>Observaciones:</label>
<textarea style='width: 300px;margin-left: 10px; height: 70px' id='observImp'></textarea>
