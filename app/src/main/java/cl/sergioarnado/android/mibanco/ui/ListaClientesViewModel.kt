package cl.sergioarnado.android.mibanco.ui

import android.text.Editable.Factory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.sergioarnado.android.mibanco.app
import cl.sergioarnado.android.mibanco.clientesData.ClientesDao
import cl.sergioarnado.android.mibanco.data.Clientes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ListaClientesViewModel(val clientesDao: ClientesDao) : ViewModel() {

    var clientes by mutableStateOf(listOf<Clientes>())

    fun ingresarSolicitud(clientes: Clientes){
        viewModelScope.launch(Dispatchers.IO) {
            clientesDao.ingresarSolicitud(clientes)
            actualizarCliente()
        }

    }

    fun actualizarCliente(): List <Clientes>{
        viewModelScope.launch(Dispatchers.IO) {
            clientes = clientesDao.DatosPorfechaDeCreacion()
        }
        return clientes

    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val app = (this[APPLICATION_KEY] as app)
                ListaClientesViewModel(app.clientesDao)
            }
        }
    }
}