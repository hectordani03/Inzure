package io.inzure.app.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.inzure.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInformationView() {
    Scaffold(
        topBar = { TopBarProfile() },
        bottomBar = { BottomNavigationBarProfile() }
    ) { paddingValues ->
        // Página scrolleable
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
                    Image(
                        painter = painterResource(R.drawable.profile_2), // Reemplaza con tu imagen
                        contentDescription = "Foto de Perfil",
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Título de la sección
                Text(
                    text = "Informacion Personal",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Tarjetas de información mejoradas con funcionalidad de edición
            item {
                var nombre by remember { mutableStateOf("Jose Joshua") }
                PersonalInfoCard(
                    label = "Nombre",
                    value = nombre,
                    onEditClick = { newValue -> nombre = newValue }
                )
            }
            item {
                var intereses by remember { mutableStateOf("Me gustan los gatos") }
                PersonalInfoCard(
                    label = "Intereses",
                    value = intereses,
                    onEditClick = { newValue -> intereses = newValue }
                )
            }
            item {
                var correo by remember { mutableStateOf("jjemail01@gmail.com") }
                PersonalInfoCard(
                    label = "Correo",
                    value = correo,
                    onEditClick = { newValue -> correo = newValue }
                )
            }
            item {
                var telefono by remember { mutableStateOf("3141669964") }
                PersonalInfoCard(
                    label = "Teléfono",
                    value = telefono,
                    onEditClick = { newValue -> telefono = newValue }
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
                "Mi Informacion Personal",
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
fun BottomNavigationBarProfile() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
            .background(Color(0xFF072A4A))
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarIconCar(R.drawable.ic_file, "Home")
            BottomBarIconCar(R.drawable.ic_history, "Search")
            BottomBarIconCar(R.drawable.ic_search, "Notifications")
            BottomBarIconCar(R.drawable.ic_profile2, "Settings")
        }
    }
}
