package io.inzure.app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.inzure.app.data.repository.InsuranceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import io.inzure.app.data.model.Insurance
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InsuranceViewModel : ViewModel() {

    private val repository = InsuranceRepository()

    private val _insurances = MutableStateFlow<List<Insurance>>(emptyList())
    val insurances: StateFlow<List<Insurance>> get() = _insurances

    // Función para iniciar la escucha en tiempo real
    fun startRealtimeUpdates() {
        repository.getInsurancesRealtime { insuranceList ->
            _insurances.value = insuranceList
        }
    }

    // Función para agregar un seguro
    fun addInsurance(type: String, insurance: Insurance, imageUri: Uri?) {
        viewModelScope.launch {
            val success = repository.addInsurance(type, insurance, imageUri)
            if (success) startRealtimeUpdates()
        }
    }

    // Función para actualizar un seguro
    fun updateInsurance(type: String, insurance: Insurance, imageUri: Uri?) {
        viewModelScope.launch {
            val success = repository.updateInsurance(type, insurance, imageUri)
            if (success) startRealtimeUpdates()
        }
    }

    // Función para eliminar un seguro
    fun deleteInsurance(type: String, insuranceId: String) {
        viewModelScope.launch {
            val success = repository.deleteInsurance(type, insuranceId)
            if (success) startRealtimeUpdates()
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeListener()
    }
}

