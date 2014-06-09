package happy.seguridad


class Sesn implements Serializable {
    Persona usuario
    Prfl perfil
    Date fechaInicio
    Date fechaFin
    static auditable = [ignore: []]

    static mapping = {
        table 'sesn'
        cache usage: 'read-write', include: 'non-lazy'
        version false
        id generator: 'identity'
        sort "perfil"
        columns {
            id column: 'sesn__id'
            perfil column: 'prfl__id'
            usuario column: 'prsn__id'
            fechaInicio column: 'sesnfcin'
            fechaFin column: 'sesnfcfn'
        }
    }


    static constraints = {
       fechaInicio(blank:true,nullable: true)
       fechaFin(blank:true,nullable: true)
    }

    boolean getEstaActivo() {
        def now = new Date()
//        now = now.plus(5)
//        println " ${this.perfil} now "+now+" inicio  "+fechaInicio+" fin "+fechaFin
        if(fechaInicio == null)
            return true
        else{
          if(fechaFin>=now)
              return true
            else
              return false
        }

    }

    String toString() {
        return "${this.perfil}"
    }

}
