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

    fun startRealtimeUpdates() {
        repository.getUsersRealtime { usersList ->
            _users.value = usersList
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeListener()
    }

    fun getUsers() {
        viewModelScope.launch {
            val usersList = repository.getUsers()
            _users.value = usersList
        }
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            val success = repository.addUser(user)
            if (success) getUsers()
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            val success = repository.updateUser(user)
            if (success) {
                Log.d("UserViewModel", "User updated successfully")
                getUsers()
            } else {
                Log.e("UserViewModel", "Failed to update user")
            }
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            val success = repository.deleteUser(user.id, user.role)
            if (success) getUsers()
        }
    }
}
