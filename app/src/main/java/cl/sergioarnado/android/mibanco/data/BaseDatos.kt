package cl.sergioarnado.android.mibanco.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cl.sergioarnado.android.mibanco.clientesData.ClientesDao

@Database(entities = [Clientes::class], version = 1)

@TypeConverters(LocalDataConverter::class)
abstract class BaseDatos : RoomDatabase() {
    abstract fun clientesDao(): ClientesDao
}