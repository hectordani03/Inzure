// AgentViewModel.kt
package io.inzure.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.inzure.app.data.model.Agent
import io.inzure.app.data.repository.AgentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AgentViewModel : ViewModel() {

    private val repository = AgentRepository()

    private val _agents = MutableStateFlow<List<Agent>>(emptyList())
    val agents: StateFlow<List<Agent>> get() = _agents

    private val _agent = MutableStateFlow<Agent?>(null)
    val agent: StateFlow<Agent?> get() = _agent

    fun getAgents() {
        repository.getAgents { agentsList ->
            _agents.value = agentsList
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeListener()
    }

    fun addAgent(agent: Agent) {
        viewModelScope.launch {
            try {
                val success = repository.addAgent(agent)
                if (!success) {
                    Log.e("AgentViewModel", "Error: El agente no se pudo agregar. Verifica el repositorio.")
                }
            } catch (e: Exception) {
                Log.e("AgentViewModel", "Excepción al agregar agente: ${e.message}")
            }
        }
    }

    fun updateAgent(agent: Agent) {
        viewModelScope.launch {
            try {
                val success = repository.updateAgent(agent)
                if (!success) {
                    Log.e("AgentViewModel", "Error: El agente no se pudo actualizar. Verifica el repositorio.")
                }
            } catch (e: Exception) {
                Log.e("AgentViewModel", "Excepción al actualizar agente: ${e.message}")
            }
        }
    }

    fun deleteAgent(agent: Agent) {
        viewModelScope.launch {
            try {
                val success = repository.deleteAgent(agent)
                if (!success) {
                    Log.e("AgentViewModel", "Error: El agente no se pudo eliminar. Verifica el repositorio.")
                }
            } catch (e: Exception) {
                Log.e("AgentViewModel", "Excepción al eliminar agente: ${e.message}")
            }
        }
    }
}
