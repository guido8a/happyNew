<%@ page import="happy.seguridad.Persona" %>
<div style="max-height: 500px; overflow-y: auto; overflow-x: hidden;font-size: 11px">
    <g:each in="${tramites}" var="t" status="i">
        <g:if test="${i==0}">
            <div style="margin-bottom: 20px;min-height: 140px" class="vertical-container">
                <p class="css-vertical-text">D. Principal</p>

                <div class="linea"></div>

                <div class="row">
                    <div class="col-xs-1 negrilla">No:</div>

                    <div class="col-xs-3">${t.codigo}</div>
                    <div class="col-xs-1 negrilla">Fecha:</div>

                    <div class="col-xs-3">${t.fechaCreacion.format("dd-MM-yyyy HH:mm")}</div>
                </div>


                <div class="row">
                    <div class="col-xs-1 negrilla">De:</div>
                    <div class="col-xs-3">${t.deDepartamento ? t.deDepartamento.codigo :""+t.de.departamento.codigo+":"+t.de.nombre + ' ' + t.de.apellido}</div>
                    <div class="col-xs-5">
                        <g:each in="${happy.tramites.PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteNotInList(t,rolesNo)}" var="pdt" status="j">
                            <span style="font-weight: bold">${pdt.rolPersonaTramite.descripcion}:</span>
                            ${(pdt.departamento)?pdt.departamento:""+pdt.persona.departamento.codigo+":"+pdt.persona}
                            ${pdt.fechaRecepcion?"("+pdt.fechaRecepcion.format("dd-MM-yyyy")+")":""}<br>
                        </g:each>
                    </div>
                </div>

                <div class="row">
                    <div class="col-xs-1 negrilla">Asunto:</div>

                    <div class="col-xs-8">${t.asunto}</div>
                </div>

                <g:if test="${t.personaPuedeLeer(Persona.get(session.usuario.id)) && t.texto !=''}">
                    <div class="row" style="margin-bottom: 10px">
                        <div class="col-xs-1 negrilla">Texto:</div>

                        <div class="col-xs-10" style="background: #dedede; max-height: 300px; overflow: auto;">
                            %{--<g:each in="${0..5}" var="i">--}%
                            <util:renderHTML html="${t.texto}"/>
                            %{--</g:each>--}%
                        </div>
                    </div>
                </g:if>
                <g:if test="${t.observaciones}">
                    <div class="row" style="margin-bottom: 10px">
                        <div class="col-xs-1 negrilla">Obs:</div>
                        <div class="col-xs-8">${t.observaciones}</div>
                    </div>
                </g:if>
            </div>
        </g:if>
        <g:else>
            <div style="margin-bottom: 20px" class="vertical-container">
                <p class="css-vertical-text">Tramite</p>

                <div class="linea"></div>

                <div class="row">
                    <div class="col-xs-1 negrilla">No:</div>
                    <div class="col-xs-3">${t.codigo}</div>
                    <div class="col-xs-1 negrilla">Fecha:</div>
                    <div class="col-xs-3">${t.fechaCreacion.format("dd-MM-yyyy HH:mm")}</div>
                </div>

                <div class="row">
                    <div class="col-xs-1 negrilla">De:</div>
                    <div class="col-xs-3">${t.deDepartamento ? t.deDepartamento.codigo :""+t.de.departamento.codigo+":"+t.de.nombre + ' ' + t.de.apellido}</div>
                    <div class="col-xs-5">
                        <g:each in="${happy.tramites.PersonaDocumentoTramite.findAllByTramiteAndRolPersonaTramiteNotInList(t,rolesNo)}" var="pdt" status="j">
                            <span style="font-weight: bold">${pdt.rolPersonaTramite.descripcion}:</span>
                            ${(pdt.departamento)?pdt.departamento:""+pdt.persona.departamento.codigo+":"+pdt.persona}
                            ${pdt.fechaRecepcion?"("+pdt.fechaRecepcion.format("dd-MM-yyyy")+")":""}<br>
                        </g:each>
                    </div>
                </div>

                <div class="row">
                    <div class="col-xs-1 negrilla">Asunto:</div>

                    <div class="col-xs-8">${t.asunto}</div>
                </div>

                <g:if test="${t.personaPuedeLeer(Persona.get(session.usuario.id) ) && t.texto !=''}">
                    <div class="row" style="margin-bottom: 10px">
                        <div class="col-xs-1 negrilla">Texto:</div>

                        <div class="col-xs-10" style="background: #dedede; max-height: 300px; overflow: auto;">
                            %{--<g:each in="${0..5}" var="i">--}%
                            <util:renderHTML html="${t.texto}"/>
                            %{--</g:each>--}%
                        </div>
                    </div>
                </g:if>

                <g:if test="${t.observaciones}">
                    <div class="row" style="margin-bottom: 10px">
                        <div class="col-xs-1 negrilla">Obs:</div>

                        <div class="col-xs-8">${tramite.observaciones}</div>
                    </div>
                </g:if>
            </div>
        </g:else>

    </g:each>


</div>