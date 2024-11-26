package io.inzure.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.inzure.app.data.model.Post
import io.inzure.app.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private val repository = PostRepository()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> get() = _posts

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> get() = _post

    fun getPosts() {
        repository.getPosts { postsList ->
            _posts.value = postsList
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeListener()
    }

    fun addPost(post: Post) {
        viewModelScope.launch {
            try {
                val success = repository.addPost(post)
                if (!success) {
                    Log.e("PostViewModel", "Error: El post no se pudo agregar. Verifica el repositorio.")
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Excepción al agregar post: ${e.message}")
            }
        }
    }

    fun updatePost(post: Post) {
        viewModelScope.launch {
            try {
                val success = repository.updatePost(post)
                if (!success) {
                    Log.e("PostViewModel", "Error: El post no se pudo actualizar. Verifica el repositorio.")
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Excepción al actualizar post: ${e.message}")
            }
        }
    }

    fun deletePost(post: Post) {
        viewModelScope.launch {
            try {
                val success = repository.deletePost(post)
                if (!success) {
                    Log.e("PostViewModel", "Error: El post no se pudo eliminar. Verifica el repositorio.")
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "Excepción al eliminar post: ${e.message}")
            }
        }
    }

    // Función para agregar un post localmente
    fun addPostManually(post: Post) {
        _posts.value = _posts.value + post
    }

    // Función para actualizar un post localmente
    fun updatePostManually(updatedPost: Post) {
        _posts.value = _posts.value.map { post ->
            if (post.id == updatedPost.id) updatedPost else post
        }
    }

    // Función para eliminar un post localmente
    fun deletePostManually(post: Post) {
        _posts.value = _posts.value.filter { it.id != post.id }
    }
}
