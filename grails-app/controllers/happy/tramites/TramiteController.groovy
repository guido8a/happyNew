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

    def cargaUsuarios(){
        def dir = Departamento.get(params.dir)
        def users= happy.seguridad.Persona.findAllByDepartamento(dir)
//        println "users "+users
        for(int i=users.size()-1;i>-1;i--){
//            println " "+users[i].estaActivo()+"  "+users[i].puedeRecibir()
            if(!(users[i].estaActivo() && users[i].puedeRecibir())){
                users.remove(i)
            }
        }
//        println "users 2 "+users
        render g.select(from:users,name:"usuario",id:"usuario",class: "many-to-one form-control",optionKey: "id")
//        <g:select name="usuario" id="usuario" class="many-to-one form-control" from="" value="" ></g:select>
    }
}
