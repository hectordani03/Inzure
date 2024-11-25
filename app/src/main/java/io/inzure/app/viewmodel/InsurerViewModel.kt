// InsurerViewModel.kt
package io.inzure.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.inzure.app.data.model.Insurer
import io.inzure.app.data.repository.InsurerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InsurerViewModel : ViewModel() {

    private val repository = InsurerRepository()

    private val _Insurers = MutableStateFlow<List<Insurer>>(emptyList())
    val Insurers: StateFlow<List<Insurer>> get() = _Insurers

    private val _Insurer = MutableStateFlow<Insurer?>(null)
    val Insurer: StateFlow<Insurer?> get() = _Insurer

    fun getInsurers() {
        repository.getInsurers { InsurersList ->
            _Insurers.value = InsurersList
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeListener()
    }

    fun addInsurer(Insurer: Insurer) {
        viewModelScope.launch {
            try {
                val success = repository.addInsurer(Insurer)
                if (!success) {
                    Log.e("InsurerViewModel", "Error: El Insurere no se pudo agregar. Verifica el repositorio.")
                }
            } catch (e: Exception) {
                Log.e("InsurerViewModel", "Excepción al agregar Insurere: ${e.message}")
            }
        }
    }

    fun updateInsurer(Insurer: Insurer) {
        viewModelScope.launch {
            try {
                val success = repository.updateInsurer(Insurer)
                if (!success) {
                    Log.e("InsurerViewModel", "Error: El Insurere no se pudo actualizar. Verifica el repositorio.")
                }
            } catch (e: Exception) {
                Log.e("InsurerViewModel", "Excepción al actualizar Insurere: ${e.message}")
            }
        }
    }

    fun deleteInsurer(Insurer: Insurer) {
        viewModelScope.launch {
            try {
                val success = repository.deleteInsurer(Insurer)
                if (!success) {
                    Log.e("InsurerViewModel", "Error: El Insurere no se pudo eliminar. Verifica el repositorio.")
                }
            } catch (e: Exception) {
                Log.e("InsurerViewModel", "Excepción al eliminar Insurere: ${e.message}")
            }
        }
    }
}
