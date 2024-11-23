package io.inzure.app.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import io.inzure.app.data.model.User
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import io.inzure.app.viewmodel.GlobalUserSession

class UserRepository {

    private var listenerRegistration: ListenerRegistration? = null
    private val db = FirebaseFirestore.getInstance()

    suspend fun addUser(user: User): Boolean {
        return try {
            db.collection("Users")
                .add(user)
                .await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error adding user: ${e.message}")
            false
        }
    }

    suspend fun updateUser(user: User): Boolean {
        return try {
            db.collection("Users")
                .document(user.id)
                .set(user)
                .await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating user: ${e.message}")
            false
        }
    }

    suspend fun deleteUser(user: User): Boolean {
        return try {
            db.collection("Users")
                .document(user.id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error deleting user: ${e.message}")
            false
        }
    }

    suspend fun getUserById(userId: String): User? {
        val role = GlobalUserSession.role ?: return null

        val documentPath = when (role) {
            "Client" -> "UserClient"
            "Admin" -> "UserAdmin"
            "Editor" -> "UserEditor"
            "Insurer" -> "UserInsurer"
            else -> return null
        }

        return try {
            val snapshot = db.collection("Users")
                .document(documentPath)
                .collection("userData")
                .document(userId)
                .get()
                .await()

            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener el usuario: ", e)
            null
        }
    }

    fun getUsers(onUsersChanged: (List<User>) -> Unit) {
        listenerRegistration?.remove()

        listenerRegistration = db.collection("Users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("UserRepository", "Error fetching users: ${error.message}")
                    return@addSnapshotListener
                }

                val usersList = snapshot?.documents?.mapNotNull { document ->
                    val user = document.toObject<User>()
                    user?.copy(id = document.id)
                } ?: emptyList()

                onUsersChanged(usersList)
            }
    }

    suspend fun selectImage(onImageSelected: (String?) -> Unit) {
        try {
            // Aquí puedes usar un ActivityResultLauncher para abrir la galería y seleccionar una imagen
            val imageUri = "file:///path/to/selected/image.jpg" // Simula una URI para la imagen seleccionada.
            onImageSelected(imageUri)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error seleccionando imagen: ${e.message}")
            onImageSelected(null)
        }
    }

    suspend fun updateProfileImage(userId: String, imageUri: String) {
        try {
            val storageReference = FirebaseStorage.getInstance().reference
            val fileUri = Uri.parse(imageUri)

            // Obtener la URL de la imagen anterior
            val userDoc = FirebaseFirestore.getInstance().collection("Users").document(userId).get().await()
            val oldImageUrl = userDoc.getString("image") // URL anterior, puede ser nula o vacía

            // Eliminar la imagen anterior solo si existe
            if (!oldImageUrl.isNullOrEmpty()) {
                try {
                    val oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl)
                    oldImageRef.delete().await() // Elimina la imagen anterior
                } catch (e: Exception) {
                    Log.e("UserRepository", "Error eliminando la imagen anterior: ${e.message}")
                }
            }

            // Subir la nueva imagen con el nombre del `userId` y su extensión original
            val fileExtension = fileUri.lastPathSegment?.substringAfterLast('.', "jpg") ?: "jpg" // Extensión predeterminada
            val newImageRef = storageReference.child("profile_images/$userId.$fileExtension")

            // Subir archivo
            newImageRef.putFile(fileUri).await()

            // Obtener URL de descarga
            val downloadUrl = newImageRef.downloadUrl.await()

            // Actualizar el campo `image` en Firestore
            FirebaseFirestore.getInstance().collection("Users")
                .document(userId)
                .update("image", downloadUrl.toString())
                .await()
        } catch (e: Exception) {
            Log.e("UserRepository", "Error actualizando imagen: ${e.message}")
            throw e
        }
    }

    fun removeListener() {
        listenerRegistration?.remove()
    }


}
