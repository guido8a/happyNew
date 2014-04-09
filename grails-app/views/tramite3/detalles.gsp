<g:if test="${principal}">
    <div style="margin-bottom: 20px;min-height: 170px" class="vertical-container" >
        <p class="css-vertical-text"  >Doc. Principal</p>
        <div class="linea" ></div>
        <div class="row">
            <div class="col-xs-1 negrilla">No:</div>
            <div class="col-xs-8">${principal.codigo}</div>
        </div>
        <div class="row">
            <div class="col-xs-1 negrilla">Asunto:</div>
            <div class="col-xs-8">${principal.asunto}</div>
        </div>
        <div class="row" style="margin-bottom: 10px">
            <div class="col-xs-1 negrilla">Obs:</div>
            <div class="col-xs-8">${principal.observaciones}</div>
        </div>
    </div>
</g:if>
<div style="margin-bottom: 20px" class="vertical-container" >
    <p class="css-vertical-text">Tramite</p>
    <div class="linea" ></div>
    <div class="row">
        <div class="col-xs-1 negrilla">No:</div>
        <div class="col-xs-8">${tramite.codigo}</div>
    </div>
    <div class="row">
        <div class="col-xs-1 negrilla">Asunto:</div>
        <div class="col-xs-8">${tramite.asunto}</div>
    </div>
    <div class="row" style="margin-bottom: 10px">
        <div class="col-xs-1 negrilla">Obs:</div>
        <div class="col-xs-8">${tramite.observaciones}</div>
    </div>
</div>