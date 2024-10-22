package io.inzure.app.data.repository

import android.net.Uri
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import io.inzure.app.data.model.Insurance

class InsuranceRepository {

    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null
    private val storage = FirebaseStorage.getInstance()

    // Función para obtener las pólizas en tiempo real
    fun getInsurancesRealtime(onInsurancesChanged: (List<Insurance>) -> Unit) {
        // Remueve cualquier listener previo
        listenerRegistration?.remove()

        // Usa collectionGroup para obtener todos los seguros de todas las subcolecciones "serviceData"
        listenerRegistration = db.collectionGroup("serviceData")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("InsuranceRepository", "Error fetching insurances: ${error.message}")
                    return@addSnapshotListener
                }

                val insuranceList = snapshot?.documents?.mapNotNull { it.toObject<Insurance>() } ?: emptyList()
                onInsurancesChanged(insuranceList)
            }
    }


    suspend fun addInsurance(type: String, insurance: Insurance, imageUri: Uri?): Boolean {
        return try {
            // Referencia al documento en Firestore
            val documentRef = db.collection("insuranceServices")
                .document(type)
                .collection("serviceData")
                .document()

            // Subir imagen si existe
            imageUri?.let {
                val imageUrl = uploadImage(documentRef.id, it)
                if (imageUrl != null) insurance.image = imageUrl
            }

            // Asignar el ID al seguro
            insurance.id = documentRef.id

            // Guardar el documento en Firestore
            documentRef.set(insurance).await()
            Log.d("InsuranceRepository", "Insurance added successfully: ${insurance.name}")
            true
        } catch (e: Exception) {
            Log.e("InsuranceRepository", "Error adding insurance: ${e.message}")
            false
        }
    }


    // Función para actualizar una póliza
    suspend fun updateInsurance(type: String, insurance: Insurance, imageUri: Uri?): Boolean {
        return try {
            val documentRef = db.collection("insuranceServices")
                .document(type)
                .collection("serviceData")
                .document(insurance.id)

            // Subir imagen si existe
            imageUri?.let {
                val imageUrl = uploadImage(insurance.id, it)
                if (imageUrl != null) insurance.image = imageUrl
            }

            // Actualizar el documento en Firestore
            documentRef.set(insurance).await()
            true
        } catch (e: Exception) {
            Log.e("InsuranceRepository", "Error updating insurance: ${e.message}")
            false
        }
    }

    // Función para eliminar una póliza
    suspend fun deleteInsurance(type: String, insuranceId: String): Boolean {
        return try {
            db.collection("insuranceServices")
                .document(type)
                .collection("serviceData")
                .document(insuranceId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("InsuranceRepository", "Error deleting insurance: ${e.message}")
            false
        }
    }

    // Función para subir imágenes a Firebase Storage
    private suspend fun uploadImage(insuranceId: String, imageUri: Uri): String? {
        return try {
            val storageRef: StorageReference = storage.reference.child("insurance_images/$insuranceId")
            val uploadTask = storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e("InsuranceRepository", "Error uploading image: ${e.message}")
            null
        }
    }

    // Remover el listener para evitar fugas de memoria
    fun removeListener() {
        listenerRegistration?.remove()
    }
}
