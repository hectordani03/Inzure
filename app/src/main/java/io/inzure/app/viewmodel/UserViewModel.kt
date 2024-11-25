package io.inzure.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.inzure.app.data.model.User
import io.inzure.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> get() = _users
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    fun getUsers() {
        repository.getUsers { usersList ->
            _users.value = usersList
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeListener()
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            try {
                val success = repository.addUser(user)
                if (!success) {
                    Log.e("UserViewModel", "Error: El usuario no se pudo agregar. Verifica el repositorio.")
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Excepción al agregar usuario: ${e.message}")
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            try {
                val success = repository.updateUser(user)
                if (!success) {
                    Log.e("UserViewModel", "Error: El usuario no se pudo actualizar. Verifica el repositorio.")
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Excepción al actualizar usuario: ${e.message}")
            }
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            try {
                val success = repository.deleteUser(user)
                if (!success) {
                    Log.e("UserViewModel", "Error: El usuario no se pudo eliminar. Verifica el repositorio.")
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Excepción al eliminar usuario: ${e.message}")
            }
        }
    }

    fun updateProfileImage(userId: String, imageUri: String?, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        if (imageUri.isNullOrEmpty()) {
            onError(IllegalArgumentException("No se seleccionó ninguna imagen"))
            return
        }

        viewModelScope.launch {
            try {
                repository.updateProfileImage(userId, imageUri)
                onSuccess()
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error actualizando imagen: ${e.message}")
                onError(e)
            }
        }
    }

    fun deleteProfileImage(
        userId: String,
        imageUri: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.deleteImageFromStorage(imageUri)
                repository.clearImageUriInFirestore(userId)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun updateEmail(
        newEmail: String,
        onRedirectToLogin: () -> Unit,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val success = repository.updateEmail(newEmail){
                    onRedirectToLogin()
                }
                if (success) {
                    onSuccess()
                } else {
                    onError(Exception("No se pudo enviar el correo de verificación para actualizar el correo electrónico."))
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}
