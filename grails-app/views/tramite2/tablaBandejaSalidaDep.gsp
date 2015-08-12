<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 11/08/15
  Time: 11:25 AM
--%>

<%--
    * trmt__id=7175,
    * trmtcdgo=MEM-3430-DGCP-15,
    * trmtpdre=null,
    * trmtasnt=Circular instruyendo para que en pedido de pago incluya valor a pagarse,
    * tpdccdgo=MEM,
    * edtrcdgo=E001,
    * deprdscr=José Alcides López Rosero,
    * depr__id=5356,
    * deprdpto=DGCP,
    dedpdscr=DIRECCION DE GESTION DE COMPRAS PUBLICAS,
    * tptrcdgo=N,
    trmtprex=null,
    * trmtextr=0,
    * prtrprsn=,
    * prtrprdp=DGCU,
    * prtrdpto=DGCU,
    * trmttppd=MEDIA,
    * paradpto=María Del Pilar Velasco Guerron,
    * copiprsn=,
    * copidpto=cc:DGA, cc:DGAP, cc:DGFZ, cc:DGRP, cc:DGV, cc:GLI,
             cc:ACCPM, cc:DGES, cc:GTH, cc:UGCR,
    * paratitl= PARA: DGCU COPIA: DGA COPIA: DGAP COPIA: DGFZ COPIA: DGRP
              COPIA: DGV COPIA: GLI COPIA: ACCPM COPIA: DGES COPIA: GTH COPIA: UGCR,
    * trmtfccr=2015-07-24 12:36:00.602,
    * trmtfcen=null,
    * trmtfcbq=null,
    * trmtanxo=0,
    * trmtdctr=0,
    * edtxcdgo=null,
    * trmtimpr=null,
    * copiextr=0
    * trmtesrn=N
--%>
<g:set var="now" value="${new Date()}"/>
<g:if test="${rows.size() == 0}">
    <tr>
        <td colspan="10" class="info text-center">
            <h4 class="text-info">
                <i class="fa icon-ghost fa-2x text-shadow"></i>
                No se encontraron
                <g:if test="${busca}">
                    resultados para su búsqueda
                </g:if>
                <g:else>
                    trámites en su bandeja de salida
                </g:else>
            </h4>
        </td>
    </tr>
</g:if>
<g:else>
%{--${rows}--}%
    <g:each in="${rows}" var="row">
        <g:set var="clase" value="${row.tpdccdgo}"/> %{--tipo documento codigo--}%
        <g:if test="${row.trmtimpr && row.trmtimpr > 0}">%{--es imprimir o no--}%
            <g:set var="clase" value="${clase + ' imprimir'}"/>
        </g:if>

        <g:if test="${row.trmtfcbq && row.trmtfcbq < now}">%{--fecha bloqueo--}%
            <g:set var="clase" value="${clase + ' alerta'}"/>
        </g:if>
        <g:else>
            <g:set var="clase" value="${clase + ' ' + row.edtrcdgo}"/>%{--estado tramite codigo--}%
        </g:else>

        <g:if test="${row.trmtfcen}">%{--fecha de envio--}%
            <g:set var="clase" value="${clase + ' desenviar'}"/>
        </g:if>

        <g:if test="${row.edtxcdgo}">%{--estado externo--}%
            <g:set var="clase" value="${clase + ' estado'}"/>
        </g:if>

        <g:if test="${row.trmtextr?.toInteger() == 1}">%{--es externo--}%
            <g:if test="${row.tpdccdgo == 'DEX'}">%{--tipo doc. codigo--}%
                <g:set var="clase" value="${clase + ' DEX'}"/>
            </g:if>
            <g:else>
                <g:set var="clase" value="${clase + ' externo'}"/>
            </g:else>
        </g:if>

        <g:if test="${row.copiextr?.toInteger() > 0}">%{--cantidad de copias a dptos. externos--}%
            <g:set var="clase" value="${clase + ' externoCC'}"/>
        </g:if>

        <g:if test="${row.trmtanxo?.toInteger() == 1 || row.trmtdctr?.toInteger() > 0}">%{--anexo y cant. de documentos anexos--}%
            <g:set var="clase" value="${clase + ' conAnexo'}"/>
        </g:if>
        <g:else>
            <g:set var="clase" value="${clase + ' sinAnexo'}"/>
        </g:else>

        <g:if test="${row.tpdccdgo == 'SUM'}">
            <g:set var="clase" value="${clase + ' sumilla'}"/>
        </g:if>
        <g:else>
            <g:set var="clase" value="${clase + ' sinSumilla'}"/>
        </g:else>

        <g:if test="${row.trmtpdre}">
            <g:set var="clase" value="${clase + ' conPadre'}"/>
        </g:if>

        <tr id="${row.trmt__id}" data-id="${row.trmt__id}"
            class="trTramite ${clase}"
            estado="${row.edtrcdgo}" %{--estado tramite codigo--}%
            de="${row.depr__id}" %{--id de la persona q crea el tram.--}%
            codigo="${row.trmtcdgo}" %{--codigo del tramite--}%
            ern="${row.trmtesrn}" %{--es respuesta nueva--}%
            departamento="${row.deprdpto}" %{--dpto. de la pers. q crea el tramite--}%
            anio="${row.trmtfccr.format('yyyy')}" %{--fecha de creacion--}%
            padre="${row.trmtpdre}" %{--padre--}%>
            <td title="${row.trmtasnt}" style="width: 145px;">
                <g:if test="${row.tptrcdgo == 'C'}">
                    <i class="fa fa-eye-slash" style="margin-left: 10px"></i>
                </g:if>
                <g:if test="${row.trmtdctr > 0}">
                    <i class="fa fa-paperclip"></i>
                </g:if>
                ${row.trmtcdgo}
            </td>
            <td>
                ${row.deprdpto}
            </td>
            <td style="width: 115px;">
                ${row.trmtfccr.format("dd-MM-yyyy HH:mm")}
            </td>
            <td>
                <g:if test="${row.tpdccdgo == 'OFI'}">
                    EXT
                </g:if>
                <g:else>
                    ${row.prtrdpto}
                </g:else>
            </td>
            <td class="titleEspecial"
                title="<div style='max-height:150px; overflow-y:auto;'>${row.paratitl}</div>">%{--el title con los destinatarios y si recibieron o no--}%
                <span class="para">
                    <g:if test="${row.prtrprsn}">%{--para persona (squi guarda la persona, interna o externa)--}%
                        ${row.prtrprsn}
                    </g:if>
                    <g:else>
                        <g:set var="triangulos" value="${row.paradpto.split(',')}"/>
                        <g:each in="${triangulos}" var="t" status="i">%{--para dpto--}%
                            <i class="fa fa-download"></i>
                            ${t}${i < triangulos.size() - 1 ? ', ' : ''}
                        </g:each>
                    </g:else>
                </span>
                <span class="copias">
                    ${row.copidpto.replaceAll('cc: *', '[CC] ')}${row.copidpto && row.copidpto != "" && row.copiprsn && row.copiprsn != "" ? ', ' : ''}
                    ${row.copiprsn.replaceAll('cc: *', '[CC] ')}
                </span>

                <g:if test="${!((row.prtrprsn && row.prtrprsn != '') ||
                        (row.paradpto && row.paradpto != '') ||
                        (row.copidpto && row.copidpto != '') ||
                        (row.copiprsn && row.copiprsn != ''))}">
                    <span class="label label-danger" style="margin-top: 3px;">
                        <i class="fa fa-warning"></i> Sin destinatario ni copias
                    </span>
                </g:if>
            </td>
            <td>
                ${row.trmttppd}
            </td>
            <td style="width: 115px;">
                ${row.trmtfcen?.format('dd-MM-yyyy HH:mm')}
            </td>
            <td style="width: 115px;">
                ${row.trmtfcbq?.format('dd-MM-yyyy HH:mm')}
            </td>
            <td>
                ${row.edtrdscr}
            </td>
            <td>
                <g:if test="${row.edtrcdgo == 'E001' && !esEditor}">
                    <g:checkBox name="porEnviar" tramite="${row.trmt__id}" style="margin-left: 20px" class="form-control combo" checked="false"/>
                </g:if>
            </td>
        </tr>
    </g:each>
</g:else>

<script type="text/javascript">
    $(function () {
        $("tr").contextMenu({
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