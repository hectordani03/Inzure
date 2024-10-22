package io.inzure.app.auth

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthManager(private val context: Context) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // Iniciar sesión
    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveSession()
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Login failed")
                }
            }
    }

    // Cerrar sesión
    fun logout() {
        firebaseAuth.signOut()
        clearSession()
    }

    // Guardar sesión
    private fun saveSession() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", true)
        editor.apply()
    }

    // Limpiar sesión
    private fun clearSession() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", false)
        editor.apply()
    }

    // Verificar si el usuario está autenticado
    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    // Obtener el usuario actual
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}

/*
* En esta parte del codigo puedes poner que pokemon eres
* Max: Jigglypuff
* Hector:
* Esteban:
* Danae:
* Fernando: Incineroar
* Saul: eevee
* */