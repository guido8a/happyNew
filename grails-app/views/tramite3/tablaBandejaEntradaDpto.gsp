<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 29/07/15
  Time: 09:25 AM
--%>

<%--

    trmt__id:11123,
    trmtcdgo:SUM-20413-AP-15,
    trmtasnt:cm-9165  PAGO SERVICIO DE TELÉFONÍA MÓVIL $45.54,
    tpdccdgo:SUM,
    rltrcdgo:R001,
    rltrdscr:PARA,
    deprdscr:María Belén León Cadena,
    depr__id:5564,
    deprlogn:mleon,
    deprdpto:AP,
    deprdpds:ADMINISTRACION PRESUPUESTARIA,
    dedpdscr:null,
    dedp__id:741,
    prtr__id:35720,
    tptrcdgo:N,
    trmtprex:null,
    prtrprsn:null,
    prtrdpto:null,
    prtrdpds:ADMINISTRACION PRESUPUESTARIA,
    trmttppd:BAJA,
    trmtfcrc:null,
    trmtfcen:2015-07-28 16:27:45.954,
    trmtfclr:null,
    trmtfcbq:2015-07-29 09:57:00.0,
    trmtanxo:null,
    trmtdctr:0,
    edtxcdgo:null

--%>

<g:set var="now" value="${new Date()}"/>
<g:if test="${rows.size() == 0}">
    <tr>
        <td colspan="9" class="info text-center">
            <h4 class="text-info">
                <i class="fa icon-ghost fa-2x text-shadow"></i>
                No se encontraron
                <g:if test="${busca}">
                    resultados para su búsqueda
                </g:if>
                <g:else>
                    trámites en su bandeja de entrada
                </g:else>
            </h4>
        </td>
    </tr>
</g:if>
<g:else>
    <g:each in="${rows}" var="row">
        <g:set var="clase" value=""/>

        <g:if test="${row.trmtfcrc}">%{-- fecha de recepcion --}%
            <g:if test="${row.trmtfclr < now}">%{-- fecha limite respuesta --}%
                <g:set var="clase" value="retrasado"/>
            %{--<g:if test="${tramite.respuestasVivas.size() > 0}">--}%
            %{--<g:set var="clase" value="recibido"/>--}%
            %{--</g:if>--}%
            </g:if>
            <g:else>
                <g:set var="clase" value="recibido"/>
            </g:else>
        </g:if>
        <g:else>
            <g:if test="${row.trmtfcbq && row.trmtfcbq < now}">%{-- fecha bloqueo --}%
                <g:set var="clase" value="sinRecepcion"/>
            </g:if>
            <g:else>
                <g:set var="clase" value="blanco porRecibir"/>
            </g:else>
        </g:else>

        <g:if test="${row.trmtanxo == 1 && row.trmtdctr > 0}">%{-- anexo y DocumentoTramite.count --}%
            <g:set var="clase" value="${clase + ' conAnexo'}"/>
        </g:if>
        <g:else>
            <g:set var="clase" value="${clase + ' sinAnexo'}"/>
        </g:else>

        <g:set var="clase" value="${clase + ' ' + row.rltrcdgo}"/>%{-- rol codigo --}%

        <g:if test="${row.edtxcdgo}">%{-- estado tramite externo --}%
            <g:set var="clase" value="${clase + ' estadoExterno'}"/>
        </g:if>

        <g:set var="de" value=""/>
        <g:if test="${row.tpdccdgo == 'DEX'}">%{-- tipo doc codigo --}%
            <g:set var="de" value="E_${row.trmt__id}"/> %{-- tramite id --}%
            <g:set var="clase" value="${clase + ' dex'}"/>
        </g:if>
        <g:else>
            <g:if test="${row.dedp__id}">%{-- de departamento id --}%
                <g:set var="de" value="D_${row.dedp__id}"/>
            </g:if>
            <g:else>
                <g:set var="de" value="P_${row.depr__id}"/>%{-- de persona id --}%
            </g:else>
        </g:else>

        <g:set var="paraLbl"/>
        <g:set var="paraTitle"/>
        %{--<g:if test="${row.tpdccdgo == 'OFI'}">--}%%{--tipo doc cdgo--}%
            %{--<g:set var="paraLbl" value="${row.trmtprex}"/>--}%
            %{--<g:set var="paraTitle" value="${row.trmtprex} (ext.)"/>     --}%%{--para externo--}%
        %{--</g:if>--}%
        %{--<g:else>--}%
            <g:if test="${row.prtrprsn}">%{--para persona--}%
                <g:set var="paraLbl" value="${row.prtrprsn}"/>
                <g:set var="paraTitle" value="${row.prtrprsn} (${row.prtrdpto})"/>
            </g:if>
            <g:else>
                <g:set var="paraLbl" value="${row.prtrdpto}"/>%{-- para dpto cdgo--}%
                <g:set var="paraTitle" value="${row.prtrdpds}"/> %{--para dpto descripcion--}%
            </g:else>
        %{--</g:else>--}%

        <tr data-id="${row.trmt__id}"
            class="doc ${clase}" de="${de}"
            codigo="${row.trmtcdgo}" departamento="${row.deprdpto}" %{-- tramite cdgo y de.dpto.cdgo--}%
            prtr="${row.prtr__id}">%{-- id pers doc tram--}%

            <td class="codigo" title="${row.trmtasnt}">%{-- asunto--}%
                <g:if test="${row.tptrcdgo == 'C'}">%{-- tipo tramite cdgo --}%
                    <i class="fa fa-eye-slash" style="margin-left: 10px"></i>
                </g:if>
                <g:if test="${row.trmtdctr > 0}">%{-- DocumentoTramite.count --}%
                    <i class="fa fa-paperclip"></i>
                </g:if>
                ${row.trmtcdgo}%{-- tramite cdgo --}%
            </td>
            <td class="envio" style="width: 115px;">${row.trmtfcen?.format('dd-MM-yyyy HH:mm')}</td>%{-- fecha envio --}%
            <td class="recepcion" style="width: 115px;">${row.trmtfcrc?.format('dd-MM-yyyy HH:mm')}</td>%{-- fecha recepcion --}%
            <td class="dpto" title="${row.deprdpds}">${row.deprdpto}</td>%{-- de.dpto descripcion y cdgo --}%
            <td class="de" title="${row.deprdscr}">${row.deprlogn ?: row.deprdscr}</td>%{-- de nombres y login--}%
            <td class="para" title="${paraTitle}">${paraLbl}</td>
            <td class="prioridad">${row.trmttppd}</td>%{-- prioridad --}%
            <td class="limiteRes">${row.trmtfclr?.format('dd-MM-yyyy HH:mm')}</td>%{-- fecha limite respuesta --}%
            <td class="rol">${row.rltrdscr}</td>%{-- rol --}%
        </tr>
    </g:each>

    <script type="text/javascript">
        $(function () {
            $("tr.doc").contextMenu({
                items  : createContextMenu,
                onShow : function ($element) {
                    $element.addClass("trHighlight");
                },
                onHide : function ($element) {
                    $(".trHighlight").removeClass("trHighlight");
                }
            });

            $('[title!=""]').qtip({
                style    : {
                    classes : 'qtip-tipsy'
                },
                position : {
                    my : "bottom center",
                    at : "top center"
                }
            });
            $('.titleEspecial').qtip({
                style    : {
                    classes : 'qtip-tipsy'
                },
                position : {
                    my : "bottom center",
                    at : "top center"
                },
                show     : {
                    solo : true
                },
                hide     : {
                    fixed : true,
                    delay : 300
                }
            });
        });
    </script>
</g:else>