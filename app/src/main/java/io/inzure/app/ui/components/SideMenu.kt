// SideMenu.kt
package io.inzure.app.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import io.inzure.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.inzure.app.data.model.User

@Composable
fun SideMenu(
    screenWidth: Dp,
    onNavigateToProfile: () -> Unit,
    showChatView: MutableState<Boolean>,
    scope: CoroutineScope,
    drawerState: DrawerState,
    onNavigateToAdmin: () -> Unit // Función de navegación al AdminView
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    // Obtener el ID del usuario autenticado
    val userId = auth.currentUser?.uid ?: return

    // Variables de estado para almacenar los datos del usuario
    var firstName by remember { mutableStateOf("Cargando...") }
    var lastName by remember { mutableStateOf("Cargando...") }
    var email by remember { mutableStateOf("Cargando...") }
    var imageUri by remember { mutableStateOf<String?>(null) }
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
                        email = userData.email
                        imageUri = userData.image
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
    val name = "${firstName.split(" ").first()} ${lastName.split(" ").first()}"

    Box(
        modifier = Modifier
            .width(screenWidth * 0.75f)
            .fillMaxHeight()
            .background(Color(0xFF072A4A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Imagen de perfil
            if (!imageUri.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Foto de Perfil",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_profile_default),
                    contentDescription = "Foto de Perfil",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información del usuario
            Text(
                text = name,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )

            Text(
                text = email,
                fontSize = 14.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Opciones del menú lateral con la función MenuOption
            MenuOption(
                iconRes = R.drawable.ic_profile2,
                text = "Perfil",
                spacerHeight = 20.dp,
                onClick = { onNavigateToProfile() }
            )
            MenuOption(
                iconRes = R.drawable.ic_file,
                text = "Mis Pólizas",
                spacerHeight = 20.dp,
                onClick = { /* Acción para "Mis Pólizas" */ }
            )
            MenuOption(
                iconRes = R.drawable.ic_search,
                text = "Buscador",
                spacerHeight = 20.dp,
                onClick = { /* Acción para "Buscador" */ }
            )
            MenuOption(
                iconRes = R.drawable.ic_history,
                text = "Historial",
                spacerHeight = 20.dp,
                onClick = { /* Acción para "Historial" */ }
            )
            MenuOption(
                iconRes = R.drawable.ic_chat,
                text = "Chat",
                spacerHeight = 20.dp,
                onClick = {
                    showChatView.value = true
                    scope.launch { drawerState.close() }
                }
            )
            MenuOption(
                iconRes = R.drawable.ic_learn,
                text = "Educativo",
                spacerHeight = 20.dp,
                onClick = { /* Acción para "Educativo" */ }
            )

            // Nueva opción: Administrador
            MenuOption(
                iconRes = R.drawable.ic_profile2, // Asegúrate de tener este recurso o reemplázalo
                text = "Administrador",
                spacerHeight = 20.dp,
                onClick = {
                    scope.launch { drawerState.close() }
                    onNavigateToAdmin() // Llamar a la navegación al AdminView
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón de cerrar sesión
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { /* Acción para cerrar sesión */ },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_logout),
                    contentDescription = "Cerrar sesión",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cerrar Sesión",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MenuOption(
    iconRes: Int,
    text: String,
    spacerHeight: Dp,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(spacerHeight))
    }
}
