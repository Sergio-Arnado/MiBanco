package cl.sergioarnado.android.mibanco.clientesData

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import cl.sergioarnado.android.mibanco.data.Clientes
import androidx.room.Query

@Dao
interface ClientesDao {

    @Query("SELECT * FROM clientes ORDER BY fechaDeCreacion DESC")
    suspend fun DatosPorfechaDeCreacion(): List<Clientes>


    @Query ("SELECT * FROM clientes WHERE id = :id")
    suspend fun encontrarPorId(id:Long): Clientes


    @Insert
    suspend fun ingresarSolicitud(clientes: Clientes)



    @Query("INSERT INTO clientes (id, nombreCompleto, rut, fechaNacimiento, email, telefono, latitud, longitud, imagenFrente, imagenTrasera, fechaDeCreacion) VALUES (:nuevoid, :telefono, :rut, :nombreCompleto, :email, :fechaNacimiento, :latitud, :longitud, :imagenFrente, :imagenTrasera, :fechaDeCreacion)")
    suspend fun modificarSolicitud(
        nuevoid: Int,
        nombreCompleto: String,
        rut: String,
        fechaNacimiento: Long,
        email: String,
        telefono: Int,
        latitud: Double,
        longitud: Double,
        imagenFrente: ByteArray,
        imagenTrasera: ByteArray,
        fechaDeCreacion: Long
    )

    @Query("DELETE FROM clientes WHERE ID = :IDborrado")
    suspend fun eliminarSolicitud(IDborrado: Int)
}