package cl.sergioarnado.android.mibanco

import android.app.Application
import androidx.room.Room
import cl.sergioarnado.android.mibanco.data.BaseDatos

class app: Application() {

    val db by lazy { Room.databaseBuilder(this, BaseDatos::class.java, "clientes.db").build()  }
    val clientesDao by lazy { db.clientesDao() }
}