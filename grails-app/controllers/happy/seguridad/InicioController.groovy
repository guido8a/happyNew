package happy.seguridad

class InicioController extends happy.seguridad.Shield {
    def dbConnectionService

    def index() {
        if(session.usuario.puedeJefe()){
            redirect(controller: "retrasadosWeb", action: "reporteRetrasadosConsolidadoDir", params: [dpto: Persona.get(session.usuario.id).departamento.id,inicio:"1","dir":"1"])
        }else{
            if(session.usuario.puedeJefe()){
                redirect(controller: "retrasadosWeb", action: "reporteRetrasadosConsolidado", params: [dpto: Persona.get(session.usuario.id).departamento.id,inicio:"1"])
            }else{
//                def cn = dbConnectionService.getConnection()
//                def prms = []
//                def acciones = "'bandejaEntrada', 'bandejaEntradaDpto', 'seguimientoExternos', 'archivadosDpto'"
//                def tx = "select accnnmbr from prms, accn where prfl__id = " + Prfl.findByNombre(session.perfil.toString()).id +
//                        " and accn.accn__id = prms.accn__id and accnnmbr in (${acciones})"
//                cn.eachRow(tx) { d ->
//                    prms << d.accnnmbr
//                }
//                cn.close()
//
//                def usu = Persona.get(session.usuario?.id)
////        println "get tri " +usu.departamento.getTriangulos()
//
////            if (usu) {
////                def now = new Date().clearTime()
////                if ((usu.password == usu.cedula.encodeAsMD5()) /*|| usu.fechaCambioPass <= now*/) {
////                    redirect(controller: "login", action: "cambiarPass")
////                    return
////                }
////            }
//
//                return  [prms: prms]
            }

        }

    }

    def parametros = {

        if(session.usuario.puedeAdmin) {
            return []
        }else{
            flash.message="Está tratando de ingresar a un pantalla restringida para su perfil. Está acción será registrada."
            response.sendError(403)
        }
    }
}
