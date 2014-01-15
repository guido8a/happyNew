package happy.tramites


class TramiteController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def crearTramite(){
        //
        session.usuario=new happy.seguridad.Persona()
        session.usuario.nombre="Juan"
        def de = session.usuario
        def fecha = new Date()
        [de:de,fecha:fecha]
    }




}
