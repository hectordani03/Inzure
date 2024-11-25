// AgentRepository.kt
package io.inzure.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import io.inzure.app.data.model.Agent
import kotlinx.coroutines.tasks.await

class AgentRepository {

    private var listenerRegistration: ListenerRegistration? = null
    private val db = FirebaseFirestore.getInstance()

    suspend fun addAgent(agent: Agent): Boolean {
        return try {
            db.collection("Agents")
                .add(agent)
                .await()
            true
        } catch (e: Exception) {
            Log.e("AgentRepository", "Error adding agent: ${e.message}")
            false
        }
    }

    suspend fun updateAgent(agent: Agent): Boolean {
        return try {
            db.collection("Agents")
                .document(agent.id)
                .set(agent)
                .await()
            true
        } catch (e: Exception) {
            Log.e("AgentRepository", "Error updating agent: ${e.message}")
            false
        }
    }

    suspend fun deleteAgent(agent: Agent): Boolean {
        return try {
            db.collection("Agents")
                .document(agent.id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("AgentRepository", "Error deleting agent: ${e.message}")
            false
        }
    }

    fun getAgents(onAgentsChanged: (List<Agent>) -> Unit) {
        listenerRegistration?.remove()

        listenerRegistration = db.collection("Agents")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AgentRepository", "Error fetching agents: ${error.message}")
                    return@addSnapshotListener
                }

                val agentsList = snapshot?.documents?.mapNotNull { document ->
                    val agent = document.toObject(Agent::class.java)
                    agent?.copy(id = document.id)
                } ?: emptyList()

                onAgentsChanged(agentsList)
            }
    }

    suspend fun getAgentById(agentId: String): Agent? {
        return try {
            val document = db.collection("Agents").document(agentId).get().await()
            if (document.exists()) {
                document.toObject(Agent::class.java)?.copy(id = document.id)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AgentRepository", "Error fetching agent: ${e.message}")
            null
        }
    }

    fun removeListener() {
        listenerRegistration?.remove()
    }
}
