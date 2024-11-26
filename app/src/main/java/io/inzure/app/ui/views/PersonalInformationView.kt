// PersonalInformationView.kt
package io.inzure.app.ui.views

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.inzure.app.R
import io.inzure.app.data.model.User
import io.inzure.app.viewmodel.UserViewModel
import io.inzure.app.viewmodel.ValidationUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.Period
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInformationView(userViewModel: UserViewModel = viewModel()) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    // Obtener el ID del usuario autenticado
    val userId = auth.currentUser?.uid ?: return

    // Variables de estado para almacenar los datos del usuario
    var firstName by remember { mutableStateOf("Cargando...") }
    var lastName by remember { mutableStateOf("Cargando...") }
    var birthDate by remember { mutableStateOf("Cargando...") }
    var email by remember { mutableStateOf("Cargando...") }
    var phone by remember { mutableStateOf("Cargando...") }
    var description by remember { mutableStateOf("Cargando...") }
    var role by remember { mutableStateOf("Cargando...") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var tempImageUri by remember { mutableStateOf<String?>(null) }

    // Estado para el diálogo de edición
    var showDialog by remember { mutableStateOf(false) }
    var editingLabel by remember { mutableStateOf("") }
    var editingValue by remember { mutableStateOf("") }
    var currentEditAction: ((String) -> Unit)? by remember { mutableStateOf(null) }

    var showEditPhotoDialog by remember { mutableStateOf(false) }
    var showDeleteImageDialog by remember { mutableStateOf(false) }

    // Agregar variable de estado para el diálogo de contraseña
    var showPasswordDialog by remember { mutableStateOf(false) }
    var enteredPassword by remember { mutableStateOf("") }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            tempImageUri = it.toString() // Actualiza la URI temporal
        }
    }

    // Estado para el mensaje de success
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Estado para el mensaje de error
    var errorMessage by remember { mutableStateOf("") }

    // Coroutine scope para operaciones asíncronas
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        firestore.collection("Users")
            .document(userId)
            .get()
            .addOnSuccessListener { userDoc ->
                if (userDoc.exists()) {
                    val userData = userDoc.toObject(User::class.java)
                    if (userData != null) {
                        firstName = userData.firstName
                        lastName = userData.lastName
                        birthDate = userData.birthDate
                        email = userData.email
                        phone = userData.phone
                        description = userData.description
                        imageUri = userData.image
                        role = userData.role
                    } else {
                        Log.e("Firestore", "El documento existe, pero no se pudo mapear a un objeto User")
                    }
                } else {
                    Log.e("Firestore", "El documento del usuario no existe en Firestore")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al obtener el documento del usuario: ", e)
            }
    }

    // Lista de información del usuario
    val userInfo = listOf(
        "Nombre(s)" to firstName to { newValue: String ->
            firstName = newValue
            updateUserInfo(userId, userViewModel, firstName, lastName, birthDate, email, phone, description, imageUri, role)
        },
        "Apellidos" to lastName to { newValue: String ->
            lastName = newValue
            updateUserInfo(userId, userViewModel, firstName, lastName, birthDate, email, phone, description, imageUri, role)
        },
        "Correo Electrónico" to email to { newValue: String ->
            email = newValue
        },
        "Número telefónico" to phone to { newValue: String ->
            phone = newValue
            updateUserInfo(userId, userViewModel, firstName, lastName, birthDate, email, phone, description, imageUri, role)
        },
        "Descripción" to description to { newValue: String ->
            description = newValue
            updateUserInfo(userId, userViewModel, firstName, lastName, birthDate, email, phone, description, imageUri, role)
        },
        "Fecha de Nacimiento" to birthDate to { newValue: String ->
            birthDate = newValue
            updateUserInfo(userId, userViewModel, firstName, lastName, birthDate, email, phone, description, imageUri, role)
        },
    )

    Scaffold(
        topBar = { TopBarProfile() },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(Color(0xFF072A4A)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(Color(0xFF072A4A)),
                        contentAlignment = Alignment.Center
                    ) {
                        val displayedImageUri = tempImageUri ?: imageUri

                        if (!displayedImageUri.isNullOrEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(displayedImageUri),
                                contentDescription = "Foto de Perfil",
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(R.drawable.ic_profile_default),
                                contentDescription = "Foto de Perfil",
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }


                        // Botón para editar imagen
                        Box(
                            modifier = Modifier
                                .size(40.dp) // Tamaño del botón circular
                                .clip(CircleShape)
                                .background(Color(0xFF64B5F6)) // Azul claro
                                .align(Alignment.BottomEnd)
                                .clickable {
                                    showEditPhotoDialog = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_add), // El "+" blanco
                                contentDescription = "Editar foto",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        // Botón para eliminar imagen (solo si existe una imagen)
                        if (!imageUri.isNullOrEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp) // Tamaño del botón circular
                                    .clip(CircleShape)
                                    .background(Color(0xFFE57373)) // Rojo claro
                                    .align(Alignment.BottomStart) // Posicionado al lado izquierdo
                                    .clickable {
                                        showDeleteImageDialog = true // Mostrar diálogo de confirmación
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_remove), // El "-" blanco
                                    contentDescription = "Eliminar foto",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Información Personal",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Iterar sobre userInfo para crear las tarjetas dinámicamente
            items(userInfo) { (data, onEdit) ->
                val (label, value) = data
                PersonalInfoCard(
                    label = label,
                    value = value,
                    onEditClick = {
                        // Configurar el diálogo de edición
                        editingLabel = label
                        editingValue = value
                        currentEditAction = onEdit
                        errorMessage = ""
                        showDialog = true
                    }
                )
            }
        }
    }

    // Mostrar el diálogo de edición
    if (showDialog) {
        EditPersonalInfoDialog(
            label = editingLabel,
            initialValue = editingValue,
            errorMessage = errorMessage,
            onSave = { newValue ->
                // Realizar validación
                coroutineScope.launch {
                    when (editingLabel) {
                        // Lógica actualizada para manejar "Correo Electrónico"
                        "Correo Electrónico" -> {
                            val isUnique = ValidationUtils.isEmailUnique(newValue, userId, firestore)
                            if (!isUnique) {
                                errorMessage = "El email ya está registrado."
                            } else {
                                // Mostrar el diálogo para reautenticación
                                editingValue = newValue // Guardar temporalmente el nuevo email
                                enteredPassword = "" // Limpiar el campo de contraseña
                                showPasswordDialog = true
                                showDialog = false
                            }
                        }

                        "Número telefónico" -> {
                            val isUnique = ValidationUtils.isPhoneUnique(newValue, userId, firestore)
                            if (!isUnique) {
                                errorMessage = "El número de teléfono ya está registrado o no es válido."
                            } else {
                                // Número válido, proceder a guardar
                                currentEditAction?.invoke(newValue)
                                showDialog = false
                            }
                        }
                        "Fecha de Nacimiento" -> {
                            val isValidAge = isAgeValid(newValue)
                            if (!isValidAge) {
                                errorMessage = "Debes ser mayor de 18 años."
                            } else {
                                // Fecha válida, proceder a guardar
                                currentEditAction?.invoke(newValue)
                                showDialog = false
                            }
                        }
                        else -> {
                            // No se requiere validación
                            currentEditAction?.invoke(newValue)
                            showDialog = false
                        }
                    }
                }
            },
            onCancel = { showDialog = false }
        )
    }

    // Mostrar el diálogo de edición de imagen
    if (showEditPhotoDialog) {
        AlertDialog(
            onDismissRequest = {
                showEditPhotoDialog = false
                tempImageUri = null // Restablece la URI temporal al cerrar el diálogo
            },
            title = { Text("Editar Foto de Perfil") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE3F2FD))
                            .clickable { launcher.launch("image/*") }
                            .shadow(4.dp, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Seleccionar Imagen",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1976D2)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    tempImageUri?.let { selectedUri ->
                        imageUri = selectedUri // Actualiza la imagen principal
                        tempImageUri = null
                        userViewModel.updateProfileImage(
                            userId = userId,
                            imageUri = imageUri!!,
                            onSuccess = {
                                Log.d("Update", "Imagen actualizada correctamente")
                                showEditPhotoDialog = false
                            },
                            onError = { e ->
                                Log.e("Update", "Error actualizando imagen: ${e.message}")
                            }
                        )
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEditPhotoDialog = false
                    tempImageUri = null // Restablece la URI temporal al cancelar
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Mostrar el diálogo de eliminación de imagen
    if (showDeleteImageDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteImageDialog = false
            },
            title = { Text("Eliminar Foto de Perfil") },
            text = {
                Text("¿Estás seguro de que deseas eliminar tu foto de perfil?")
            },
            confirmButton = {
                TextButton(onClick = {
                    // Lógica para eliminar la imagen
                    userViewModel.deleteProfileImage(
                        userId = userId,
                        imageUri = imageUri!!,
                        onSuccess = {
                            imageUri = null // Limpia la URI de la imagen actual
                            showDeleteImageDialog = false
                            Log.d("Delete", "Imagen eliminada correctamente")
                        },
                        onError = { e ->
                            showDeleteImageDialog = false
                            Log.e("Delete", "Error eliminando imagen: ${e.message}")
                        }
                    )
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteImageDialog = false
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* Opcional: manejar la acción al cerrar el diálogo */ },
            title = { Text("Correo Actualizado") },
            text = {
                Text("Hemos enviado un correo de verificación a $email. Por favor, verifica tu email antes de iniciar sesión nuevamente.")
            },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    // Opcional: Navegar o cerrar la pantalla si es necesario
                }) {
                    Text("Aceptar")
                }
            }
        )
    }


    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Reautenticación Requerida") },
            text = {
                Column {
                    Text("Por favor, ingresa tu contraseña actual para actualizar el correo:")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = enteredPassword,
                        onValueChange = { enteredPassword = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (enteredPassword.isNotEmpty()) {
                        userViewModel.updateEmail(
                            currentPassword = enteredPassword,
                            newEmail = editingValue, // Usar el valor temporal
                            onRedirectToLogin = {
                                val loginIntent = Intent(context, LoginView::class.java)
                                loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(loginIntent)
                            },
                            onSuccess = {
                                showPasswordDialog = false
                                showSuccessDialog = true
                            },
                            onError = { exception ->
                                errorMessage = exception.message.toString()
                                showPasswordDialog = false
                            }
                        )
                    } else {
                        errorMessage = "La contraseña no puede estar vacía."
                    }
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

}

@SuppressLint("NewApi")
fun isAgeValid(birthDateStr: String): Boolean {
    return try {
        val birthDate = LocalDate.parse(birthDateStr) // Asumiendo formato ISO: yyyy-MM-dd
        val today = LocalDate.now()
        val age = Period.between(birthDate, today).years
        age >= 18
    } catch (e: Exception) {
        Log.e("Validation", "Error al analizar la fecha de nacimiento: ${e.message}")
        false
    }
}

// Función para actualizar la información del usuario en Firestore a través del ViewModel
fun updateUserInfo(
    userId: String,
    userViewModel: UserViewModel,
    firstName: String,
    lastName: String,
    birthDate: String,
    email: String,
    phone: String,
    description: String,
    imageUri: String?,
    role: String,
) {
    val updatedUser = User(
        id = userId,
        firstName = firstName,
        lastName = lastName,
        birthDate = birthDate,
        email = email,
        phone = phone,
        description = description,
        image = imageUri ?: "",
        role = role,
    )
    userViewModel.updateUser(updatedUser)
}

@Composable
fun PersonalInfoCard(label: String, value: String, onEditClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 8.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFCFD8DC))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = label,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = Color.Black)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarProfile() {
    TopAppBar(
        title = {
            Text(
                "Mi Información Personal",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = { /* Lógica del menú */ }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menú", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0D47A1)
        )
    )
}

@Composable
fun EditPersonalInfoDialog(
    label: String,
    initialValue: String,
    errorMessage: String = "",
    onSave: (String) -> Unit,
    onCancel: () -> Unit
) {
    var editedValue by remember { mutableStateOf(initialValue) }

    // Contexto para el DatePickerDialog
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        var year: Int
        var month: Int
        var day: Int

        try {
            val parts = editedValue.split("-")
            year = parts[0].toInt()
            month = parts[1].toInt() - 1 // Los meses en Calendar son 0-based
            day = parts[2].toInt()
        } catch (e: Exception) {
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)
        }

        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                // Formatear la fecha seleccionada
                val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDayOfMonth)
                editedValue = selectedDate.toString() // Formato ISO: yyyy-MM-dd
                showDatePicker = false // Cierra el selector después de seleccionar
            },
            year,
            month,
            day
        ).apply {
            setOnDismissListener {
                showDatePicker = false // Asegúrate de restablecer el estado al cerrar el diálogo
            }
        }.show()
    }

    TextButton(onClick = { showDatePicker = true }) {
        Text(text = if (editedValue.isNotEmpty()) editedValue else "Seleccionar Fecha")
    }


    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Editar $label") },
        text = {
            Column {
                if (label == "Fecha de Nacimiento") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .height(56.dp) // Altura del input
                            .clip(RoundedCornerShape(8.dp)) // Bordes redondeados
                            .background(Color.White) // Fondo blanco
                            .clickable { showDatePicker = true } // Hacer clic en toda el área
                            .shadow(4.dp, RoundedCornerShape(8.dp)), // Sombra
                        contentAlignment = Alignment.CenterStart // Alinear contenido a la izquierda
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = if (editedValue.isNotEmpty()) editedValue else "Seleccionar Fecha",
                                fontSize = 18.sp, // Tamaño más grande del texto
                                color = if (editedValue.isNotEmpty()) Color.Black else Color.Gray,
                                modifier = Modifier.weight(1f) // Ocupa el espacio disponible
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.date_range), // Tu vector asset
                                contentDescription = "Selector de Fecha",
                                tint = Color.Gray, // Color del ícono
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                } else {
                    TextField(
                        value = editedValue,
                        onValueChange = { editedValue = it },
                        label = { Text("Nuevo $label") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(editedValue) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancelar")
            }
        }
    )
}
