package cl.sergioarnado.android.mibanco.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Clientes (
    @PrimaryKey(autoGenerate = true) var id:Long? = null,
    var nombreCompleto:String,
    var rut:String,
    var fechaNacimiento:Long,
    var email: String,
    var telefono: String,
    var latitud: Double,
    var longitud: Double,
    var imagenFrente: String,
    var imagenTrasera: String,
    var fechaDeCreacion: Long, )




