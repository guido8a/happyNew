<style>

option.selected {
    background : #DDD;
    color      : #999;
}

li {
}

.selectable li {
    cursor        : pointer;
    border-bottom : solid 1px #0088CC;
    margin-left   : 20px;
}

.selectable li:hover {
    background : #B5D1DF;
}

.selectable li.disabled {
    cursor        : default;
    border-bottom : solid 1px #888888;
    margin-left   : 20px;
}

.selectable li.disabled:hover {
    background : inherit;
}

.selectable li.selected {
    background : #81B5CF;
    color      : #0A384F;
}

.fieldLista {
    width   : 450px;
    height  : 250px;
    border  : 1px solid #0088CC;
    margin  : 10px 10px 20px 10px;
    padding : 15px;
    float   : left;
}

.divBotones {
    width      : 30px;
    height     : 130px;
    margin-top : 75px;
    float      : left;
}

.vertical-container {
    padding-bottom : 10px;;
}

.texto {
    max-height : 80px;
    overflow   : auto;
    background : #EFE4D1;
    padding    : 3px;
}
</style>

<g:if test="${error}">
    <div class="alert alert-danger" style="padding: 10px; font-size: larger;">
        <util:renderHTML html="${error}"/>
    </div>
</g:if>
<g:else>
    <div class="alert alert-info">
        <b>Trámite:</b> ${tramite.codigo}<br/>
        <b>De:</b> ${tramite.deDepartamento ? tramite.deDepartamento.codigo : tramite.de.departamento.codigo + " " + tramite.de.login}<br/>
        <g:if test="${tramite.para}">
            <b>Para:</b> ${tramite.para.departamento ? tramite.para.departamento.codigo : tramite.para.persona.login + " (" + tramite.para.persona.departamento.codigo + ")"}
        </g:if>
        <g:if test="${tramite.copias.size() > 0}">
            <g:set var="copias" value=""/>
            <g:each in="${tramite.copias}" var="copia">
                <g:if test="${copias != ''}">
                    <g:set var="copias" value="${copias + ', '}"/>
                </g:if>
                <g:set var="copias" value="${copias + (copia.departamento ? copia.departamento?.codigo : copia.persona.login + ' (' + copia.persona.departamento?.codigo + ')')}"/>
            </g:each>
            <br/><b>CC:</b> ${copias}
        </g:if>
    </div>

    <fieldset class="ui-corner-all fieldLista">
        <legend style="margin-bottom: 1px">
            Disponibles
        </legend>

        <ul id="ulDisponibles" style="margin-left:0;max-height: 195px; overflow: auto;" class="fa-ul selectable">
            <g:each in="${disponibles}" var="disp">
                <g:if test="${disp.id.toInteger() < 0}">
                    <li data-id="${disp.id}" class="clickable">
                        <i class="fa fa-li fa-building-o"></i> ${disp.label}
                    </li>
                </g:if>
                <g:else>
                    <li data-id="${disp.id}" class="clickable">
                        <i class="fa fa-li fa-user"></i> ${disp.label}
                    </li>
                </g:else>
            </g:each>
        </ul>
    </fieldset>

    <div class="divBotones">
        <div class="btn-group-vertical">
            <a href="#" class="btn btn-default" title="Agregar todos" id="btnAddAll">
                <i class="fa fa-angle-double-right"></i>
            </a>
            <a href="#" class="btn btn-default" title="Agregar seleccionados" id="btnAddSelected">
                <i class="fa fa-angle-right"></i>
            </a>
            <a href="#" class="btn btn-default" title="Quitar seleccionados" id="btnRemoveSelected">
                <i class="fa fa-angle-left"></i>
            </a>
            <a href="#" class="btn btn-default" title="Quitar todos" id="btnRemoveAll">
                <i class="fa fa-angle-double-left"></i>
            </a>
        </div>
    </div>

    <fieldset class="ui-corner-all fieldLista">
        <legend style="margin-bottom: 1px">
            Seleccionados
        </legend>

        <ul id="ulSeleccionados" style="margin-left:0;max-height: 195px; overflow: auto;" class="fa-ul selectable">
            <g:if test="${tramite.id}">
                <g:each in="${tramite.copias}" var="disp">
                    <g:if test="${disp.persona}">
                        <li class="disabled text-muted" data-id="${disp.persona.id}">
                            <i class="fa fa-li fa-user"></i> ${disp.persona.toString()}
                        </li>
                    </g:if>
                    <g:else>
                        <li class="disabled text-muted" data-id="-${disp.departamento.id}">
                            <i class="fa fa-li fa-building-o"></i> ${disp.departamento.descripcion}
                        </li>
                    </g:else>
                </g:each>
            </g:if>
        </ul>
    </fieldset>

    <script type="text/javascript">
        function moveSelected($from, $to, muevePara) {
            var para = $("#para").val();
            $from.find("li.selected").removeClass("selected").each(function () {
                var id = $(this).data("id");
                if ((id == para && muevePara) || id != para) {
                    $(this).appendTo($to);
                }
            });
            $("li.selected").removeClass("selected");
        }

        function removeAll() {
            var $ul = $("#ulSeleccionados");
            $ul.find("li").not(".disabled").addClass("selected");
            moveSelected($ul, $("#ulDisponibles"), true);
        }

        $(function () {
            $(".selectable li").not(".disabled").click(function () {
                $(this).toggleClass("selected");
            });
            $(".clickable").dblclick(function () {
                $(this).addClass("selected");
                if ($(this).parents("ul").attr("id") == "ulSeleccionados") {
                    moveSelected($("#ulSeleccionados"), $("#ulDisponibles"), false, true);
                } else if ($(this).parents("ul").attr("id") == "ulDisponibles") {
                    moveSelected($("#ulDisponibles"), $("#ulSeleccionados"), false, true);
                }
            });
            $("#btnAddAll").click(function () {
                var $ul = $("#ulDisponibles");
                $ul.find("li").not(".disabled").addClass("selected");
                moveSelected($ul, $("#ulSeleccionados"), false);
                return false;
            });
            $("#btnAddSelected").click(function () {
                moveSelected($("#ulDisponibles"), $("#ulSeleccionados"), false);
                return false;
            });
            $("#btnRemoveSelected").click(function () {
                moveSelected($("#ulSeleccionados"), $("#ulDisponibles"), true);
                return false;
            });
            $("#btnRemoveAll").click(function () {
                removeAll();
                return false;
            });
        });
    </script>
</g:else>