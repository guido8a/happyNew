package happy.seguridad

import happy.tramites.Departamento
import happy.tramites.PermisoTramite
import happy.tramites.PermisoUsuario
import sun.misc.Perf


class PersonaController extends happy.seguridad.Shield {

    static allowedMethods = [save: "POST", delete: "POST", save_ajax: "POST", delete_ajax: "POST"]

    def index() {
        redirect(action: "list", params: params)
    } //index

    def accesos() {
        def usu = Persona.get(params.id)
        def accesos = Accs.findAllByUsuario(usu, [sort: 'accsFechaInicial'])
        return [accesos: accesos]
    }

    def permisos() {
        def usu = Persona.get(params.id)
        def permisos = PermisoUsuario.findAllByPersona(usu, [sort: 'fechaInicio'])
        return [permisos: permisos]
    }

    def config() {
        def usu = Persona.get(params.id)
        def perfilesUsu = Sesn.findAllByUsuario(usu).perfil.id
        def permisosUsu = PermisoUsuario.findAllByPersona(usu).permisoTramite.id
        return [usuario: usu, perfilesUsu: perfilesUsu, permisosUsu: permisosUsu]
    }

    def savePermisos_ajax() {
        println params
        def perm = new PermisoUsuario(params)
        println perm
        if (!perm.save(flush: true)) {
            println "error accesos: " + perm.errors
            render "NO_" + g.renderErrors(bean: perm)
        } else {
            println "OK"
            render "OK_Permiso agregado"
        }
        println perm.errors
    }


    def terminarPermiso_ajax() {
        def perm = PermisoUsuario.get(params.id)
        def now = new Date().clearTime()
        if (perm.fechaFin <= now) {
            render "OK_El permiso ya ha caducado, no puede terminarlo de nuevo."
        } else {
            if (perm.fechaInicio <= now && perm.fechaFin >= now) {
                perm.fechaFin = now
                if (!perm.save(flush: true)) {
                    render "NO_" + renderErrors(bean: perm)
                } else {
                    render "OK_Terminación del permiso exitosa"
                }
            } else {
                render "NO_No puede terminar un permiso que no ha empezado aún. Puede eliminarlo."
            }
        }
    }

    def eliminarPermiso_ajax() {
        def perm = PermisoUsuario.get(params.id)
        def now = new Date()
        if (perm.fechaFin <= now) {
            render "OK_El permiso ya ha caducado, no puede ser eliminado."
        } else {
            if (perm.fechaInicio <= now && perm.fechaFin >= now) {
                render "NO_No puede eliminar un permiso en curso. Puede terminarlo."
            } else {
                try {
                    perm.delete(flush: true)
                    render "OK_Permiso eliminado."
                } catch (e) {
                    render "NO_Ha ocurrido un error al eliminar el permiso."
                }
            }
        }
    }

    def saveAccesos_ajax() {
        def accs = new Accs(params)
        if (!accs.save(flush: true)) {
            println "error accesos: " + accs.errors
            render "NO_" + g.renderErrors(bean: accs)
        } else {
            render "OK_Restricción agregada"
        }
    }

    def terminarAcceso_ajax() {
        def accs = Accs.get(params.id)
        def now = new Date().clearTime()
        if (accs.accsFechaFinal <= now) {
            render "OK_La restricción ya ha terminado, no puede terminarla de nuevo."
        } else {
            if (accs.accsFechaInicial <= now && accs.accsFechaFinal >= now) {
                accs.accsFechaFinal = now
                if (!accs.save(flush: true)) {
                    render "NO_" + renderErrors(bean: accs)
                } else {
                    render "OK_Terminación de la restricción exitosa"
                }
            } else {
                render "NO_No puede terminar una restricción que no ha empezado aún. Puede eliminarla."
            }
        }
    }

    def eliminarAcceso_ajax() {
        def accs = Accs.get(params.id)
        def now = new Date()
        if (accs.accsFechaFinal <= now) {
            render "OK_La restricción ya ha terminado, no puede ser eliminada."
        } else {
            if (accs.accsFechaInicial <= now && accs.accsFechaFinal >= now) {
                render "NO_No puede eliminar una restricción en curso. Puede terminarla."
            } else {
                try {
                    accs.delete(flush: true)
                    render "OK_Restricción eliminada."
                } catch (e) {
                    render "NO_Ha ocurrido un error al eliminar la restricción."
                }
            }
        }
    }

    def savePerfiles_ajax() {
        def usu = Persona.get(params.id)
        def perfilesUsu = Sesn.findAllByUsuario(usu).perfil.id*.toString()
//        println "**************"
//        println Sesn.findAllByUsuario(usu)
//        println Sesn.findAllByUsuario(usu).id
//        println Sesn.findAllByUsuario(usu).id*.toString()
//        println "**************"
        def arrRemove = perfilesUsu, arrAdd = []
        def errores = ""

        if (params.perfil instanceof java.lang.String) {
            params.perfil = [params.perfil]
        }

        params.perfil.each { pid ->
            if (perfilesUsu.contains(pid)) {
                //ya tiene este perfil: le quito de la lista de los de eliminar
                arrRemove.remove(pid)
            } else {
                //no tiene este perfil: le pongo en la lista de agregar
                arrAdd.add(pid)
            }
        }
//        println "params: " + params
//        println "perfilesUsu: " + perfilesUsu
//        println "add: " + arrAdd
//        println "remove: " + arrRemove
        arrRemove.each { pid ->
            def perf = Prfl.get(pid)
            def sesn = Sesn.findByUsuarioAndPerfil(usu, perf)
            try {
                sesn.delete(flush: true)
            } catch (e) {
                println "erorr al eliminar perfil: " + e
                errores += "<li>No se puedo remover el perfil ${perf.nombre}</li>"
            }
        }
        arrAdd.each { pid ->
            def perf = Prfl.get(pid)
            def sesn = new Sesn([usuario: usu, perfil: perf])
            if (!sesn.save(flush: true)) {
                println "error al asignar perfil: " + sesn.errors
                errores += "<li>No se puedo remover el perfil ${perf.nombre}</li>"
            }
        }

        if (errores == "") {
            render "OK_Cambios efectuados exitosamente"
        } else {
            render "<ul>" + errores + "</ul>"
        }
    }

    def list() {
        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        def personaInstanceList = Persona.list(params)
        def personaInstanceCount = Persona.count()
        if (personaInstanceList.size() == 0 && params.offset && params.max) {
            params.offset = params.offset - params.max
        }
        personaInstanceList = Persona.list(params)
        return [personaInstanceList: personaInstanceList, personaInstanceCount: personaInstanceCount]
    } //list

    def show_ajax() {
        if (params.id) {
            def personaInstance = Persona.get(params.id)
            if (!personaInstance) {
                notFound_ajax()
                return
            }
            return [personaInstance: personaInstance]
        } else {
            notFound_ajax()
        }
    } //show para cargar con ajax en un dialog

    def form_ajax() {
        def personaInstance = new Persona(params)
        if (params.id) {
            personaInstance = Persona.get(params.id)
            if (!personaInstance) {
                notFound_ajax()
                return
            }
        }
        return [personaInstance: personaInstance]
    } //form para cargar con ajax en un dialog

    def formUsuario_ajax() {
        def personaInstance = new Persona(params)
        if (params.id) {
            personaInstance = Persona.get(params.id)
            if (!personaInstance) {
                notFound_ajax()
                return
            }
        }
        return [personaInstance: personaInstance]
    }

    def validarCedula_ajax() {
        params.cedula = params.cedula.toString().trim()
        if (params.id) {
            def prsn = Persona.get(params.id)
            if (prsn.cedula == params.cedula) {
                render true
                return
            } else {
                render Persona.countByCedula(params.cedula) == 0
                return
            }
        } else {
            render Persona.countByCedula(params.cedula) == 0
            return
        }
    }

    def save_ajax() {

        params.each { k, v ->
            if (v != "date.struct" && v instanceof java.lang.String) {
                params[k] = v.toUpperCase()
            }
        }

        def personaInstance = new Persona()
        if (params.id) {
            personaInstance = Persona.get(params.id)
            if (!personaInstance) {
                notFound_ajax()
                return
            }
        } //update
        else {
            //llena la parte de usuario si se esta creando la persona
            params.fechaInicio = new Date()
            def p = params.nombre.split(" ")
            params.login = ""
            p.each {
                params.login += it[0]
            }
            p = params.apellido.split(" ")
            params.login += p[0]
            params.password = params.cedula.toString().encodeAsMD5()
            params.activo = 0
            params.fechaCambioPass = new Date() + 30
            params.jefe = 0
            params.codigo = Departamento.get(params.departamento.id).codigo + "_" + params.login
        } //create
        personaInstance.properties = params

        if (!personaInstance.save(flush: true)) {
            def msg = "NO_No se pudo ${params.id ? 'actualizar' : 'crear'} Persona."
            msg += renderErrors(bean: personaInstance)
            render msg
            return
        }
        render "OK_${params.id ? 'Actualización' : 'Creación'} de Persona exitosa."
    } //save para grabar desde ajax

    def delete_ajax() {
        if (params.id) {
            def personaInstance = Persona.get(params.id)
            if (personaInstance) {
                try {
                    personaInstance.delete(flush: true)
                    render "OK_Eliminación de Persona exitosa."
                } catch (e) {
                    render "NO_No se pudo eliminar Persona."
                }
            } else {
                notFound_ajax()
            }
        } else {
            notFound_ajax()
        }
    } //delete para eliminar via ajax

    protected void notFound_ajax() {
        render "NO_No se encontró Persona."
    } //notFound para ajax

}
