// InsurerRepository.kt
package io.inzure.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import io.inzure.app.data.model.Insurer
import kotlinx.coroutines.tasks.await

class InsurerRepository {

    private var listenerRegistration: ListenerRegistration? = null
    private val db = FirebaseFirestore.getInstance()

    suspend fun addInsurer(Insurer: Insurer): Boolean {
        return try {
            db.collection("Insurers")
                .add(Insurer)
                .await()
            true
        } catch (e: Exception) {
            Log.e("InsurerRepository", "Error adding Insurer: ${e.message}")
            false
        }
    }

    suspend fun updateInsurer(Insurer: Insurer): Boolean {
        return try {
            db.collection("Insurers")
                .document(Insurer.id)
                .set(Insurer)
                .await()
            true
        } catch (e: Exception) {
            Log.e("InsurerRepository", "Error updating Insurer: ${e.message}")
            false
        }
    }

    suspend fun deleteInsurer(Insurer: Insurer): Boolean {
        return try {
            db.collection("Insurers")
                .document(Insurer.id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("InsurerRepository", "Error deleting Insurer: ${e.message}")
            false
        }
    }

    fun getInsurers(onInsurersChanged: (List<Insurer>) -> Unit) {
        listenerRegistration?.remove()

        listenerRegistration = db.collection("Insurers")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("InsurerRepository", "Error fetching Insurers: ${error.message}")
                    return@addSnapshotListener
                }

                val InsurersList = snapshot?.documents?.mapNotNull { document ->
                    val Insurer = document.toObject(Insurer::class.java)
                    Insurer?.copy(id = document.id)
                } ?: emptyList()

                onInsurersChanged(InsurersList)
            }
    }

    fun removeListener() {
        listenerRegistration?.remove()
    }
}
