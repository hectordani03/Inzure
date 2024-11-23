package io.inzure.app.viewmodel

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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

    fun loadLoggedUser(userId: String) {
        viewModelScope.launch {
            val user = repository.getUserById(userId)
            _user.value = user
        }
    }

    fun selectImage(onImageSelected: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                repository.selectImage { imageUri ->
                    onImageSelected(imageUri)
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error seleccionando imagen: ${e.message}")
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



}
