// AdminPersonalInformationView.kt
package io.inzure.app.ui.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.inzure.app.R
import io.inzure.app.data.model.User
import io.inzure.app.viewmodel.UserViewModel
import io.inzure.app.viewmodel.ValidationUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
class AdminPersonalInformationView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdminPersonalInformationMainScreen()
        }
    }
}

@Composable
fun AdminPersonalInformationMainScreen(userViewModel: UserViewModel = viewModel()) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "admin_personal_info_screen") {
        composable("admin_personal_info_screen") { AdminPersonalInfoScreen(navController, userViewModel) }
        composable("update_success") { UpdateSuccessScreen(navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPersonalInfoScreen(navController: NavController, userViewModel: UserViewModel) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    // Obtener el ID del usuario autenticado
    val userId = auth.currentUser?.uid ?: run {
        // Manejar el caso donde el usuario no está autenticado
        // Podrías redirigir a una pantalla de login o mostrar un mensaje
        return
    }

    // Variables de estado para almacenar los datos del usuario
    var firstName by remember { mutableStateOf("Cargando...") }
    var lastName by remember { mutableStateOf("Cargando...") }
    var email by remember { mutableStateOf("Cargando...") }
    var phone by remember { mutableStateOf("Cargando...") }
    val context = LocalContext.current

    // Estado para el diálogo de edición
    var showDialog by remember { mutableStateOf(false) }
    var editingLabel by remember { mutableStateOf("") }
    var editingValue by remember { mutableStateOf("") }
    var currentEditAction: ((String) -> Unit)? by remember { mutableStateOf(null) }

    // Estado para el mensaje de error
    var errorMessage by remember { mutableStateOf("") }

// Agregar variable de estado para el diálogo de contraseña
    var showPasswordDialog by remember { mutableStateOf(false) }
    var enteredPassword by remember { mutableStateOf("") }
    // Estado para el mensaje de success
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Coroutine scope para operaciones asíncronas
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        try {
            val userDoc = firestore.collection("Users").document(userId).get().await()
            if (userDoc.exists()) {
                val userData = userDoc.toObject(User::class.java)
                userData?.let {
                    firstName = it.firstName
                    lastName = it.lastName
                    email = it.email
                    phone = it.phone
                }
            } else {
                Log.e("Firestore", "El documento del usuario no existe en Firestore")
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener el documento del usuario: ", e)
        }
    }

    // Lista de información del usuario
    val userInfo = listOf(
        "Nombre(s)" to firstName to { newValue: String ->
            firstName = newValue
            updateAdminUserInfo(userId, userViewModel, firstName, lastName, email, phone)
        },
        "Apellidos" to lastName to { newValue: String ->
            lastName = newValue
            updateAdminUserInfo(userId, userViewModel, firstName, lastName, email, phone)
        },
        "Correo Electrónico" to email to { newValue: String ->
            email = newValue
            // Puedes agregar validaciones adicionales si es necesario
        },
        "Número telefónico" to phone to { newValue: String ->
            phone = newValue
            updateAdminUserInfo(userId, userViewModel, firstName, lastName, email, phone)
        }
    )

    Scaffold(
        topBar = { TopBarProfileAdmin() },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Información Personal del Administrador",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF072A4A),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Iterar sobre userInfo para crear las tarjetas dinámicamente
            items(userInfo) { (data, onEdit) ->
                val (label, value) = data
                AdminPersonalInfoCard(
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
        EditAdminPersonalInfoDialog(
            label = editingLabel,
            initialValue = editingValue,
            errorMessage = errorMessage,
            onSave = { newValue ->
                // Realizar validación si es necesario
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
                                userViewModel.updatePhone(
                                    userId = userId,
                                    newPhone = newValue,
                                    onSuccess = {
                                        navController.navigate("update_success") {
                                            popUpTo("admin_personal_info_screen") { inclusive = true }
                                        }
                                    },
                                    onError = { exception ->
                                        errorMessage = exception.message.toString()
                                    }
                                )
                                showDialog = false
                            }
                        }
                        else -> {
                            // Para Nombre y Apellidos, sin validación adicional
                            currentEditAction?.invoke(newValue)
                            showDialog = false
                        }
                    }
                }
            },
            onCancel = { showDialog = false }
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

@Composable
fun AdminPersonalInfoCard(label: String, value: String, onEditClick: () -> Unit) {
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

@Composable
fun EditAdminPersonalInfoDialog(
    label: String,
    initialValue: String,
    errorMessage: String = "",
    onSave: (String) -> Unit,
    onCancel: () -> Unit
) {
    var editedValue by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Editar $label") },
        text = {
            Column {
                TextField(
                    value = editedValue,
                    onValueChange = { editedValue = it },
                    label = { Text("Nuevo $label") },
                    modifier = Modifier.fillMaxWidth()
                )

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateSuccessScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Actualización Exitosa", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF072A4A),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile2),
                    contentDescription = "Éxito",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "La información se ha actualizado correctamente.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = {
                    navController.popBackStack()
                }) {
                    Text("Aceptar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarProfileAdmin() {
    TopAppBar(
        title = {
            Text(
                "Información Personal del Administrador",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = { /* Lógica del menú si es necesario */ }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menú", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0D47A1)
        )
    )
}

// Función para actualizar la información del administrador en Firestore a través del ViewModel
fun updateAdminUserInfo(
    userId: String,
    userViewModel: UserViewModel,
    firstName: String,
    lastName: String,
    email: String,
    phone: String
) {
    val updatedUser = User(
        id = userId,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone
    )
    userViewModel.updateUser(updatedUser)
}
