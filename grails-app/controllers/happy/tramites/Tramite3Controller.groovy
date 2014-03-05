package happy.tramites

import happy.seguridad.Persona

class Tramite3Controller {
    def save() {
        /*
            [
                tramite:[
                    padre.id:,
                    id:,
                    hiddenCC:-10_41_150_1004_,
                    para:-10,
                    tipoDocumento.id:5,
                    prioridad.id:3,
                    asunto:fgs dfgsdf
                ],
                origen:[
                   tipoPersona.id:2,
                   cedula:1234523,
                   nombre:sdfg sdfgsd,
                   nombreContacto:sdfgsdfgsdf,
                   apellidoContacto:sdgsdfgsd,
                   titulo:sdfg,
                   cargo:sdfgsdgsdf,
                   mail:235sgdfgs,
                   telefono:sdfgsdfgsdf
                ]
            ]
         */
        def persona = Persona.get(session.usuario.id)
        def estadoTramiteBorrador = EstadoTramite.findByCodigo("E001");

        def paramsOrigen = params.remove("origen")
        def paramsTramite = params.remove("tramite")

        paramsTramite.de = persona
        paramsTramite.estadoTramite = estadoTramiteBorrador
        paramsTramite.fecha = new Date()
        paramsTramite.anio = Anio.findByNumero(paramsTramite.fecha.format("yyyy"))
        /* CODIGO DEL TRAMITE:
         *      tipoDoc.codigo-secuencial-dtpoEnvia.codigo-anio(yy)
         *      INF-1-DGCP-14       MEM-10-CEV-13
         */
        //el numero del ultimo tramite del anio, por tipo doc y dpto
        def num = Tramite.withCriteria {
            eq("anio", paramsTramite.anio)
            eq("tipoDocumento", TipoDocumento.get(paramsTramite.tipoDocumento.id))
            de {
                eq("departamento", persona.departamento)
            }
            projections {
                max "numero"
            }
        }
        if (num.size() > 0) {
            num = num.first()
        }
        num = num + 1
        paramsTramite.numero = num
        paramsTramite.codigo = TipoDocumento.get(paramsTramite.tipoDocumento.id).codigo + "-" + num + "-" + persona.departamento.codigo + "-" + paramsTramite.anio.numero[2..3]

        def tramite
        def error = false
        if (paramsTramite.id) {
            tramite = Tramite.get(paramsTramite.id)
        } else {
            tramite = new Tramite()
        }
        tramite.properties = paramsTramite

//        if (!tramite.save(flush: true)) {
//            println "error save tramite " + tramite.errors
//            flash.tipo = "error"
//            flash.message = "Ha ocurrido un error al grabar el tramite, por favor, verifique la información ingresada"
//            redirect(action: "crearTramite")
//            return
//        } else {

        /*
         * para/cc: si es negativo el id > es a la bandeja de entrada del departamento
         *          si es positivo es una persona
         */
        if (paramsTramite.para) {
            def para = paramsTramite.para.toInteger()
            def paraDocumentoTramite = new PersonaDocumentoTramite([
                    tramite: tramite,
                    rolPersonaTramite: RolPersonaTramite.findByCodigo('R001')
            ])
            if (para > 0) {
                //persona
                paraDocumentoTramite.persona = Persona.get(para)
            } else {
                //departamento
                paraDocumentoTramite.departamento = Departamento.get(para * -1)
            }
//            if (!paraDocumentoTramite.save(flush: true)) {
//                println "error para: " + paraDocumentoTramite.errors
//            }
        }
        if (paramsTramite.hiddenCC.toString().size() > 0) {
            (paramsTramite.hiddenCC.split("_")).each { cc ->
                def ccDocumentoTramite = new PersonaDocumentoTramite([
                        tramite: tramite,
                        rolPersonaTramite: RolPersonaTramite.findByCodigo('R002')
                ])
                if (cc > 0) {
                    //persona
                    ccDocumentoTramite.persona = Persona.get(cc)
                } else {
                    //departamento
                    ccDocumentoTramite.departamento = Departamento.get(cc * -1)
                }
                if (!ccDocumentoTramite.save(flush: true)) {
                    println "error cc: " + ccDocumentoTramite.errors
                }
            }


        }

//        }
        render "OK"
    }
}
