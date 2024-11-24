package io.inzure.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.inzure.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SideMenu(
    screenWidth: Dp,
    onNavigateToProfile: () -> Unit,
    showChatView: MutableState<Boolean>,
    scope: CoroutineScope,
    drawerState: DrawerState
)  {
    // Contenido del menú lateral
    Box(
        modifier = Modifier
            .width(screenWidth * 0.75f)
            .fillMaxHeight()
            .background(Color(0xFF072A4A))
    ) {
        // Diseño del menú lateral
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Sección de perfil
            Image(
                painter = painterResource(id = R.drawable.ic_profile3),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Información del usuario
            Text(
                text = "Jose Joshua",
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )

            Text(
                text = "josejoshua01@gmail.com",
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
                    showChatView.value = true // Actualizar el estado para mostrar la vista de chat
                    scope.launch { drawerState.close() } // Cerrar el drawer
                }
            )
            MenuOption(
                iconRes = R.drawable.ic_learn,
                text = "Educativo",
                spacerHeight = 20.dp,
                onClick = { /* Acción para "Educativo" */ }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón de cerrar sesión
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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

// Asegúrate de incluir también la función MenuOption si no está ya en otro archivo
@Composable
fun MenuOption(
    iconRes: Int,
    text: String,
    spacerHeight: Dp,
    onClick: () -> Unit
) {
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

class SideMenu {

}
