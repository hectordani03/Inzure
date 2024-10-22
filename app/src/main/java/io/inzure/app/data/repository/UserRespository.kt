package io.inzure.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import io.inzure.app.data.model.User
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.ListenerRegistration

class UserRepository {

    private var listenerRegistration: ListenerRegistration? = null
    private val db = FirebaseFirestore.getInstance()

    suspend fun addUser(user: User): Boolean {
        return try {
            val document = if (user.role == "Admin") "UserAdmin" else "UserEditor"
            val ref = db.collection("Users")
                .document(document)
                .collection("userData")
                .add(user)
                .await()
            user.id = ref.id
            db.collection("Users")
                .document(document)
                .collection("userData")
                .document(ref.id)
                .update("id", ref.id)
                .await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error adding user: ${e.message}")
            false
        }
    }

    suspend fun updateUser(user: User): Boolean {
        return try {
            val document = if (user.role == "Admin") "UserAdmin" else "UserEditor"
            db.collection("Users")
                .document(document)
                .collection("userData")
                .document(user.id) // Asegúrate de usar el ID del usuario
                .set(user)
                .await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating user: ${e.message}")
            false
        }
    }

    suspend fun deleteUser(userId: String, role: String): Boolean {
        return try {
            val document = if (role == "Admin") "UserAdmin" else "UserEditor"
            db.collection("Users")
                .document(document)
                .collection("userData")
                .document(userId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error deleting user: ${e.message}")
            false
        }
    }

    suspend fun getUsers(): List<User> {
        return try {
            val adminSnapshot = db.collection("Users")
                .document("UserAdmin")
                .collection("userData")
                .get()
                .await()

            val editorSnapshot = db.collection("Users")
                .document("UserEditor")
                .collection("userData")
                .get()
                .await()

            // Log de tamaño de snapshots
            Log.d("UserRepository", "Admin snapshot size: ${adminSnapshot.size()} | Editor snapshot size: ${editorSnapshot.size()}")

            // Mapear los documentos a objetos User
            val adminUsers = adminSnapshot.documents.mapNotNull { it.toObject<User>() }
            val editorUsers = editorSnapshot.documents.mapNotNull { it.toObject<User>() }

            Log.d("UserRepository", "Admin Users: $adminUsers")
            Log.d("UserRepository", "Editor Users: $editorUsers")

            // Retorna la lista combinada
            val totalUsers = adminUsers + editorUsers
            Log.d("UserRepository", "Total Users Retrieved: ${totalUsers.size}")

            totalUsers
        } catch (e: Exception) {
            Log.e("UserRepository", "Error retrieving users: ${e.message}")
            emptyList()
        }
    }


    fun getUsersRealtime(onUsersChanged: (List<User>) -> Unit) {
        listenerRegistration?.remove()

        listenerRegistration = db.collectionGroup("userData")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("UserRepository", "Error fetching users: ${error.message}")
                    return@addSnapshotListener
                }

                val usersList = snapshot?.documents?.mapNotNull { it.toObject<User>() } ?: emptyList()
                onUsersChanged(usersList)
            }
    }

    fun removeListener() {
        listenerRegistration?.remove()
    }
}
