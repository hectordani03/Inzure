// PostViewModel.kt
package io.inzure.app.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import io.inzure.app.data.model.Posts
import io.inzure.app.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import io.inzure.app.data.repository.PostRepository.PostWithUser

class PostsViewModel : ViewModel() {

    private val repository = PostRepository()

    private val _carPosts = MutableStateFlow<List<PostRepository.PostWithUser>>(emptyList())
    val carPosts: StateFlow<List<PostRepository.PostWithUser>> get() = _carPosts

    private val _personalPosts = MutableStateFlow<List<PostRepository.PostWithUser>>(emptyList())
    val personalPosts: StateFlow<List<PostRepository.PostWithUser>> get() = _personalPosts

    private val _businessPosts = MutableStateFlow<List<PostRepository.PostWithUser>>(emptyList())
    val businessPosts: StateFlow<List<PostRepository.PostWithUser>> get() = _businessPosts

    private val _posts = MutableStateFlow<List<PostRepository.PostWithUser>>(emptyList())
    val posts: StateFlow<List<PostRepository.PostWithUser>> get() = _posts

    private val _post = MutableStateFlow<Posts?>(null)
    val post: StateFlow<Posts?> get() = _post

    fun getPosts() {
        repository.getPosts { postsList ->
            // Transforma la lista de Posts a PostWithUser
            val transformedPosts = postsList.map { post ->
                PostRepository.PostWithUser(
                    post = post,
                    profileImage = "" // Puedes usar un valor vacío o algún valor predeterminado
                )
            }
            _posts.value = transformedPosts // Asigna la lista transformada
        }
    }

    fun getUsersPosts() {
        repository.getUsersPosts { posts ->
            _posts.value = posts.map { post ->
                PostRepository.PostWithUser(
                    post = post,
                    profileImage = "" // Valor predeterminado o personalizado
                )
            }
        }
    }

    fun getCarPosts() {
        repository.getCarPosts { postsList ->
            Log.d("DEBUG", "Car posts: ${postsList.size}")
            _carPosts.value = postsList
        }
    }

    fun getPersonalPosts() {
        repository.getPersonalPosts { postsList ->
            Log.d("DEBUG", "Personal posts: ${postsList.size}")
            _personalPosts.value = postsList
        }
    }

    fun getBusinessPosts() {
        repository.getBusinessPosts { postsList ->
            Log.d("DEBUG", "Business posts: ${postsList.size}")
            _businessPosts.value = postsList
        }
    }


    override fun onCleared() {
        super.onCleared()
        repository.removeListener()
    }

    fun addPost(posts: Posts, imageUri: Uri?) {
        viewModelScope.launch {
            try {
                // Verificar si hay una imagen proporcionada
                if (imageUri != null) {
                    // Subir imagen y obtener URL
                    val imageUrl = uploadImageToStorage(imageUri)
                    if (imageUrl != null) {
                        // Si la imagen se subió correctamente, actualizar el post con la URL
                        val updatedPost = posts.copy(image = imageUrl) // Usa el campo `image`
                        val success = repository.addPost(updatedPost)
                        if (!success) {
                            Log.e("PostViewModel", "Error: El post no se pudo agregar. Verifica el repositorio.")
                        }
                    } else {
                        Log.e("PostViewModel", "Error al subir la imagen, no se agregará el post.")
                    }
                } else {
                    // Si no hay imagen, agregar el post directamente
                    val success = repository.addPost(posts)
                    if (!success) {
                        Log.e("PostViewModel", "Error: El post no se pudo agregar. Verifica el repositorio.")
                    }
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Excepción al agregar post: ${e.message}")
            }
        }
    }

    private suspend fun uploadImageToStorage(imageUri: Uri): String? {
        return try {
            val storageRef = Firebase.storage.reference.child("posts_images/${UUID.randomUUID()}.jpg")
            val uploadTask = storageRef.putFile(imageUri).await() // Esperar el resultado de la subida
            storageRef.downloadUrl.await().toString() // Retornar la URL de descarga
        } catch (e: Exception) {
            Log.e("PostViewModel", "Error al subir imagen: ${e.message}")
            null
        }
    }

    fun updatePost(posts: Posts, newImageUri: Uri?, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                var updatedPost = posts

                // Si se selecciona una nueva imagen, súbela y elimina la anterior
                if (newImageUri != null) {
                    val storageRef = Firebase.storage.reference.child("posts_images/${UUID.randomUUID()}.jpg")
                    val uploadTask = storageRef.putFile(newImageUri).await()
                    val newImageUrl = storageRef.downloadUrl.await().toString()

                    // Si el post ya tenía una imagen, elimínala
                    posts.image?.let { oldImageUrl ->
                        val oldImageRef = Firebase.storage.getReferenceFromUrl(oldImageUrl)
                        oldImageRef.delete().addOnSuccessListener {
                            Log.d("PostViewModel", "Old image deleted successfully.")
                        }.addOnFailureListener {
                            Log.e("PostViewModel", "Failed to delete old image: ${it.message}")
                        }
                    }

                    // Actualizar el post con la nueva URL de la imagen
                    updatedPost = posts.copy(image = newImageUrl)
                }

                // Actualizar el post en Firestore
                val success = repository.updatePost(updatedPost)
                onComplete(success)
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error updating post: ${e.message}")
                onComplete(false)
            }
        }
    }

    fun deletePost(posts: Posts) {
        viewModelScope.launch {
            try {
                // Eliminar la imagen del Storage
                posts.image?.let { imageUrl ->
                    try {
                        deleteImageFromStorage(imageUrl)
                        Log.d("PostViewModel", "Image deleted successfully.")
                    } catch (e: Exception) {
                        Log.e("PostViewModel", "Error deleting image: ${e.message}")
                    }
                }

                // Eliminar el post de Firestore
                val success = repository.deletePost(posts)
                if (!success) {
                    Log.e("PostViewModel", "Error: El post no se pudo eliminar. Verifica el repositorio.")
                }
            } catch (e: Exception) {
                if (e.message?.contains("PERMISSION_DENIED") == true) {
                    Log.e("PostViewModel", "Permission denied when deleting post.")
                } else {
                    Log.e("PostViewModel", "Excepción al eliminar post: ${e.message}")
                }
            }
        }
    }


    private fun extractStoragePathFromUrl(url: String): String? {
        if (url.isBlank()) return null // Si la URL está vacía, no hay path que extraer

        val baseUrl = "https://firebasestorage.googleapis.com/v0/b/"
        if (url.startsWith(baseUrl)) {
            val startIndex = url.indexOf("/o/") + 3
            val endIndex = url.indexOf("?alt=")
            if (startIndex != -1 && endIndex != -1) {
                return url.substring(startIndex, endIndex).replace("%2F", "/")
            }
        }
        return null // URL no válida
    }

    private suspend fun deleteImageFromStorage(imageUrl: String) {
        val storagePath = extractStoragePathFromUrl(imageUrl)
        if (storagePath != null) {
            val storageReference = FirebaseStorage.getInstance().getReference(storagePath)
            storageReference.delete().await() // Esperar a que la eliminación se complete
        } else {
            throw IllegalArgumentException("El path del Storage no es válido.")
        }
    }


}
