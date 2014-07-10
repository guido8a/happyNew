<%@ page contentType="text/html" %>

<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Parámetros</title>

        <style type="text/css">

        .tab-content, .left, .right {
            height : 600px;
        }

        .nav-tabs {
            background-color : #ffffff;
            color            : #222222;
        !important;
        }

        .tab-content {
            /*background  : #EFE4D1;*/
            /*background: #d8e8ef;*/
            background    : #eeeeee;
            border-left   : solid 1px #DDDDDD;
            border-bottom : solid 1px #DDDDDD;
            border-right  : solid 1px #DDDDDD;
            padding-top   : 10px;
        }

        .descripcion {
            /*margin-left : 20px;*/
            font-size : 12px;
            border    : solid 2px cadetblue;
            padding   : 0 10px;
            margin    : 0 10px 0 0;
        }

        .info {
            font-style : italic;
            color      : navy;
        }

        .descripcion h4 {
            color      : cadetblue;
            text-align : center;
        }

        .left {
            width      : 600px;
            text-align : justify;
            /*background : red;*/
        }

        .right {
            width       : 300px;
            margin-left : 20px;
            padding     : 20px;
            /*background  : blue;*/
        }

        .fa-ul li {
            margin-bottom : 10px;
        }

        </style>

    </head>

    <body>

        <g:set var="iconGen" value="fa fa-angle-right"/>
        <g:set var="iconTrmt" value="fa fa-angle-right"/>

        <!-- Nav tabs -->
        <ul class="nav nav-tabs">
            <li class="active"><a href="#generales" data-toggle="tab">Parámetros Generales</a></li>
            %{--<li><a href="#tramite" data-toggle="tab">Trámites</a></li>--}%
        </ul>

        <!-- Tab panes -->
        <!-- Tab panes -->
        <div class="tab-content ui-corner-bottom">
            <div class="tab-pane active" id="generales">
                <div class="left pull-left">
                    <ul class="fa-ul">
%{--
                        <li>
                            <i class="fa-li ${iconGen}"></i>
                            <span id="tpdp">
                                <g:link controller="tipoDepartamento" action="list">Codificación de la estructura departamental</g:link>
                                para clasificar  jerárquicamente las dependencias o departamentos del GADPP
                            </span>

                            <div class="descripcion hide">
                                <h4>Codificación de la Estructura</h4>

                                <p>Sirve para para la clasificación jerárquica de las distintas dependencias que conforman elGADPP.</p>

                                <p>De este modo se puede por ejemplo, obtener las Gestiones que pertenecen a una Dirección.</p>
                            </div>
                        </li>
--}%
%{--
                        <li>
                            <i class="fa-li ${iconGen}"></i>
                            <span id="perm">
                                <g:link controller="permisoTramite" action="list">Permisos</g:link> que se aplican a los
                                usuarios dentro del sistema
                            </span>

                            <div class="descripcion hide">
                                <h4>Permisos</h4>

                                <p>Sirve para fijar los permisos que que pueden ser otorgados en el sistema a un usuario de
                                acuerdo a su cargo y función dentro de la Dependencia u Oficina a la que pertenece.</p>

                                <p>Un usuario puede poseer varios permisos conforme a su perfil y cargo.</p>
                            </div>
                        </li>
--}%

%{--
                        <li>
                            <i class="fa-li ${iconGen}"></i>
                            <span id="tppr">
                                <g:link controller="tipoPersona" action="list">Tipo de persona</g:link> para distinguir entre
                                personas naturales y empresas
                            </span>

                            <div class="descripcion hide">
                                <h4>Tipo de Persona</h4>

                                <p>Sirve para distiguir entre personas natuales, empresas e instituciones del estado</p>

                                <p>Se aplica a los trámites externos y sirve para determinar el origen o destino de un trámite externo</p>
                            </div>
                        </li>
--}%

%{--
                        <li>
                            <i class="fa-li ${iconGen}"></i>
                            <span id="anio">
                                <g:link controller="anio" action="list">Año de proceso</g:link> de acuerdo a la normativa
                                de control se debe contar con una numeración secuencial cada año
                            </span>

                            <div class="descripcion hide">
                                <h4>Año</h4>

                                <p>De acuerdo a las normativas de control, cada documento contará con la generación automática de numeración
                                por tipo de documento y año.</p>

                                <p>Al iniciar cada año, la numeración será reiniciada automáticamente</p>
                            </div>
                        </li>
--}%

                        <li>
                            <i class="fa-li ${iconGen}"></i>
                            <span id="departamento">
                                <g:link controller="departamento" action="arbol">Estructura Departamental</g:link> del GADPP conforme al
                                organigrama de procesos institucional.
                            </span>

                            <div class="descripcion hide">
                                <h4>Estructura Departamental</h4>

                                <p>Distribución organizacional del GADPP.</p>
                                <p>Conforme a la estructura del orgánico - funcional.</p>
                            </div>
                        </li>

                        <li>
                            <i class="fa-li ${iconTrmt}"></i>
                            <span id="tpdc">
                                <g:link controller="tipoDocumento" action="list">Tipo de documento</g:link>
                                para diferenciar los distintos documentos que se producen dentro de un trámite, por ejemplo:
                                Memorando, Oficio, Sumilla, Circular, etc.
                            </span>

                            <div class="descripcion hide">
                                <h4>Tipo de Documento</h4>

                                <p>Determina el tipo de documento que se utiliza en los distintos trámites, pueden ser:</p>

                                <p>Memorando, Oficio, Sumilla, Circular, etc.</p>
                            </div>
                        </li>
                        <li>
                            <i class="fa-li ${iconTrmt}"></i>
                            <span id="tppd">
                                <g:link controller="tipoPrioridad" action="list">Tipo de Prioridad</g:link> que posee los
                                distintos trámites
                            </span>

                            <div class="descripcion hide">
                                <h4>Tipo de Prioridad</h4>

                                <p>El tipo de prioridad determina el tiempo que se tiene para dar contestacón al trámite.</p>

                            </div>
                        </li>
                        <li>
                            <i class="fa-li ${iconTrmt}"></i>
                            <span id="anio">
                                <g:link controller="diaLaborable" action="calendario">Días Laborables</g:link> para determinar
                                la secuencia de días que se trabajan y calcular el número de horas laborables requeridos para
                                responder un trámite
                            </span>

                            <div class="descripcion hide">
                                <h4>Días Laborables</h4>

                                <p>Se usa un calendario para determinar los días laborables y festivos.</p>
                                <p>Esto sirve para fijar la secuencia de días que se trabajan y calcular el número de horas
                                laborables requeridos para responder un trámite</p>

                            </div>
                        </li>

                        <li>
                            <i class="fa-li ${iconTrmt}"></i>
                            <span id="prmt">
                                <g:link controller="parametros" action="list">Parámetros del sistema</g:link> sirve para
                                fijar las horas de la jornada de trabajo por defecto y las direcciones de conexión para la
                                autenticación de los usuarios
                            </span>

                            <div class="descripcion hide">
                                <h4>Parámetros del Sistema</h4>

                                <p>Fija las horas de la jornada de trabajo por defecto.</p>
                                <p>Ingreso de las direcciones de conexión del servidor LDAP para la
                                autenticación de los usuarios</p>
                                <p>Registro de la Unidad organizacional principal del LDAP</p>

                            </div>
                        </li>


                        <li>
                            <i class="fa-li ${iconTrmt}"></i>
                            <span id="nmro">
                                <g:link controller="numero" action="config">Fijar números consecutivos por tipo de documento</g:link> sirve para
                                fijar los números consecutivos que se aplican por departamento y tipo de documento
                            </span>

                            <div class="descripcion hide">
                                <h4>Fijar Consecutivos</h4>

                                <p>Fija los números consecutivos para cada tipo de documento que puede emitir el Departamento.</p>
                                <p>Primero se debe definir que tipos de documentos puede tramitar un Departamento para luego
                                actualizar los números consecutivos que se han de usar</p>
                                <p>Este proceso debe usarse sólo al poner en marcha el sistema</p>

                            </div>
                        </li>


                        <li>
                            <i class="fa-li ${iconTrmt}"></i>
                            <span id="trex">
                                <g:link controller="estadoTramiteExterno" action="list">Estado de los trámites externos</g:link> sirve para
                                fijar el estado que puede tener un trámite externo
                            </span>

                            <div class="descripcion hide">
                                <h4>Estado de Trámite Externo</h4>

                                <p>Fija el estado de untrámite externo:</p>
                                <p>Por defecto empieza en "EN TRAMITE"</p>

                            </div>
                        </li>

                    </ul>

                </div>

                <div class="generales right pull-right">
                </div>
            </div>

%{--
            <div class="tab-pane" id="tramite">
                <div class="left pull-left">
                    <ul class="fa-ul">
--}%

%{--
                        <li>
                            <i class="fa-li ${iconTrmt}"></i>
                            <span id="tptr">
                                <g:link controller="tipoTramite" action="list">Tipo de trámite</g:link> para distinguir entre
                                trámites normales y confidenciales
                            </span>

                            <div class="descripcion hide">
                                <h4>Tipo de Trámite</h4>

                                <p>Sirve para distiguir entre confidencial y normal</p>

                                <p>Los trámites de tipo normal pueden ser accedidos por todos los usuarios.</p>

                                <p>A los trámites de tipo confidencial sólo tienen acceso los destinatarios del trámite, estos se hallan
                                ocultos para los otros usuarios.</p>
                            </div>
                        </li>
--}%

%{--
                        <li>
                            <i class="fa-li ${iconTrmt}"></i>
                            <span id="edtr">
                                <g:link controller="estadoTramite" action="list">Estado del trámite</g:link> conforme el flujo normal de una
                                de un trámite
                            </span>

                            <div class="descripcion hide">
                                <h4>Estado del Trámite</h4>

                                <p>De acuerdo al flujo normal de un trámite este empieza con el estado Borrador, continúa con Revisado,
                                para por Enviado, Recibido y otros según se trate de un trámite interno o uno externo.</p>
                            </div>
                        </li>
--}%

%{--
                        <li>
                            <i class="fa-li ${iconTrmt}"></i>
                            <span id="rltr">
                                <g:link controller="rolPersonaTramite" action="list">Rol de la persona en el trámite</g:link> define si
                                un usuario es destinatario, recibe una copia o envía el trámite
                            </span>

                            <div class="descripcion hide">
                                <h4>Rol de la Persona</h4>

                                <p>Define el rol que puede cumplir una persona en un trámite determina, pudiendo ser:</p>

                                <p>Envía (de), Destinatario (para), concopia (cc), recibe el documento físico, etc.</p>
                            </div>
                        </li>
--}%

%{--
                    </ul>

                </div>

                <div class="tramite right pull-right">
                </div>
            </div>
--}%

        </div>

        <script type="text/javascript">

            function prepare() {
                $(".fa-ul li span").each(function () {
                    var id = $(this).parents(".tab-pane").attr("id");
                    var thisId = $(this).attr("id");
                    $(this).siblings(".descripcion").addClass(thisId).addClass("ui-corner-all").appendTo($(".right." + id));
                });
            }

            $(function () {
                prepare();
                $(".fa-ul li span").hover(function () {
                    var thisId = $(this).attr("id");
                    $("." + thisId).removeClass("hide");
                }, function () {
                    var thisId = $(this).attr("id");
                    $("." + thisId).addClass("hide");
                });
            });
        </script>

    </body>
</html>