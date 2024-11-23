package io.inzure.app.ui.views

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.inzure.app.R
import io.inzure.app.data.model.User
import io.inzure.app.viewmodel.UserViewModel

// Importa la función BottomBar
import io.inzure.app.ui.views.BottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInformationView(userViewModel: UserViewModel = viewModel()) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    // Obtener el ID del usuario autenticado
    val userId = auth.currentUser?.uid ?: return

    // Variables de estado para almacenar los datos del usuario
    var firstName by remember { mutableStateOf("Nombre no disponible") }
    var lastName by remember { mutableStateOf("Apellido no disponible") }
    var birthDate by remember { mutableStateOf("Fecha no disponible") }
    var email by remember { mutableStateOf("Correo no disponible") }
    var phone by remember { mutableStateOf("Teléfono no disponible") }
    var imageUri by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        if (userId != null) {
            // Primero obtenemos el rol del usuario desde el documento base
            firestore.collection("Users")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    var userRole: String? = null

                    // Iterar sobre todos los documentos de primer nivel para encontrar el rol
                    for (document in querySnapshot.documents) {
                        val documentPath = document.id

                        // Intentar obtener el usuario en cada documento de rol
                        firestore.collection("Users")
                            .document(documentPath)
                            .collection("userData")
                            .document(userId)
                            .get()
                            .addOnSuccessListener { userDoc ->
                                if (userDoc.exists()) {
                                    userRole = documentPath // Encontró el rol correcto

                                    // Obtener los datos completos del usuario
                                    val userData = userDoc.toObject(User::class.java)
                                    if (userData != null) {
                                        // Asignación de variables de estado para reflejar en la UI
                                        firstName = userData.firstName ?: "Nombre no disponible"
                                        lastName = userData.lastName ?: "Apellido no disponible"
                                        birthDate = userData.birthDate ?: "Fecha no disponible"
                                        email = userData.email ?: "Correo no disponible"
                                        phone = userData.phone ?: "Teléfono no disponible"
                                        imageUri = userData.image
                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error al obtener usuario en $documentPath: ", e)
                            }

                        // Si encontramos el rol, dejamos de buscar
                        if (userRole != null) break
                    }

                    if (userRole == null) {
                        Log.e("Firestore", "No se pudo determinar el rol del usuario")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error al obtener los documentos de usuarios: ", e)
                }
        } else {
            Log.e("Auth", "ID del usuario no disponible")
        }
    }

    Scaffold(
        topBar = { TopBarProfile() },
        bottomBar = {
            BottomBar(
                onSwipeUp = { /* Acción al deslizar hacia arriba */ },
                onNavigateToUsers = { /* Acción de navegación */ }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Fondo azul con imagen de perfil centrada
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(Color(0xFF072A4A)),
                    contentAlignment = Alignment.Center
                ) {
                    // Verifica si hay una imagen en el estado del usuario logueado
                    if (!imageUri.isNullOrEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = "Foto de Perfil",
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Imagen predeterminada si no hay imagen del usuario
                        Image(
                            painter = painterResource(R.drawable.profile_2),
                            contentDescription = "Foto de Perfil",
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Título de la sección
                Text(
                    text = "Información Personal",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Tarjetas de información con valores obtenidos de Firestore
            item {
                PersonalInfoCard(
                    label = "Nombre(s)",
                    value = firstName,
                    onEditClick = { /* Lógica para editar el nombre */ }
                )
            }
            item {
                PersonalInfoCard(
                    label = "Apellidos",
                    value = lastName,
                    onEditClick = { /* Lógica para editar el apellido */ }
                )
            }
            item {
                PersonalInfoCard(
                    label = "Correo Electrónico",
                    value = email,
                    onEditClick = { /* Lógica para editar el correo */ }
                )
            }
            item {
                PersonalInfoCard(
                    label = "Número telefónico",
                    value = phone,
                    onEditClick = { /* Lógica para editar el teléfono */ }
                )
            }
            item {
                PersonalInfoCard(
                    label = "Fecha de Nacimiento",
                    value = birthDate,
                    onEditClick = { /* Lógica para editar la fecha de nacimiento */ }
                )
            }
        }
    }
}

@Composable
fun PersonalInfoCard(label: String, value: String, onEditClick: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 8.dp)
            .height(80.dp), // Altura fija para las tarjetas
        colors = CardDefaults.cardColors(containerColor = Color(0xFFCFD8DC))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Contenido de la tarjeta scrolleable
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()) // Scrolling en caso de mucha información
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
                onClick = { showDialog = true },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = Color.Black)
            }
        }
    }

    // Diálogo de edición de la tarjeta
    if (showDialog) {
        EditPersonalInfoDialog(
            label = label,
            initialValue = value,
            onSave = {
                onEditClick(it)
                showDialog = false
            },
            onCancel = { showDialog = false }
        )
    }
}

@Composable
fun EditPersonalInfoDialog(label: String, initialValue: String, onSave: (String) -> Unit, onCancel: () -> Unit) {
    var editedValue by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = { onCancel() },
        title = { Text("Editar $label") },
        text = {
            Column {
                TextField(
                    value = editedValue,
                    onValueChange = { editedValue = it },
                    label = { Text("Nuevo $label") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(editedValue) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = { onCancel() }) {
                Text("Cancelar")
            }
        }
    )
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
