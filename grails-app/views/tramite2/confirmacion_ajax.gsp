<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 30/01/19
  Time: 11:07
--%>

<div style="text-align: center">

 %{--Se creará un trámite del tipo: <br>--}%

 <label style="font-size: 30px; color: ${color}">${documento?.descripcion ?: 'Sin tipo seleccionado!'}</label>
</div>

<div>
 <br>

 Para: <label style="margin-left: 30px; font-size: 14px">${para}</label>

 <br>

 Asunto: <label style="margin-left: 17px">${asunto?.size() > 40 ? (asunto?.substring(0,40) + " ...") : asunto}</label>
</div>

