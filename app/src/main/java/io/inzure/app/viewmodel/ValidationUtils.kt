package io.inzure.app.viewmodel

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object ValidationUtils {
    suspend fun isEmailUnique(email: String, currentUserId: String, firestore: FirebaseFirestore): Boolean {
        return try {
            val querySnapshot = firestore.collection("Users")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                true // El email no está registrado
            } else {
                // Verificar si el email pertenece al usuario actual
                val otherUsers = querySnapshot.documents.filter { it.id != currentUserId }
                otherUsers.isEmpty()
            }
        } catch (e: Exception) {
            Log.e("Validation", "Error al verificar la unicidad del email: ${e.message}")
            false
        }
    }

    suspend fun isPhoneUnique(phone: String, currentUserId: String, firestore: FirebaseFirestore): Boolean {
        // Validar que el número telefónico tenga exactamente 10 dígitos
        val phoneRegex = Regex("^\\d{10}$")
        if (!phoneRegex.matches(phone)) {
            Log.e("Validation", "El número telefónico debe tener exactamente 10 dígitos.")
            return false
        }

        return try {
            val querySnapshot = firestore.collection("Users")
                .whereEqualTo("phone", phone)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                true // El número no está registrado
            } else {
                // Verificar si el número pertenece al usuario actual
                val otherUsers = querySnapshot.documents.filter { it.id != currentUserId }
                otherUsers.isEmpty()
            }
        } catch (e: Exception) {
            Log.e("Validation", "Error al verificar la unicidad del teléfono: ${e.message}")
            false
        }
    }
}
