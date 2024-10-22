package io.inzure.app.ui.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import io.inzure.app.R

class AdminView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdminScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen() {
    val context = LocalContext.current // Obtén el contexto aquí

    Scaffold(
        topBar = { AdminTopBar() },
        bottomBar = { AdminBottomNavigationBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Fondo azul con imagen de perfil centrada
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color(0xFF072A4A)),
                contentAlignment = Alignment.Center
            ) {
                // Imagen de perfil
                Image(
                    painter = painterResource(R.drawable.profile_2),
                    contentDescription = "Foto de Perfil",
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Título de la pantalla
            Text(
                text = "Panel de Administración",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Uso corregido de AdminOptionButton
            AdminOptionButton(text = "Usuarios", icon = R.drawable.ic_profile2) {
                val intent = Intent(context, UsersView::class.java)
                context.startActivity(intent)
            }

            AdminOptionButton(text = "Seguros", icon = R.drawable.ic_profile2) {
                val intent = Intent(context, InsuranceView::class.java)
                context.startActivity(intent)
            }

            AdminOptionButton(text = "Publicaciones", icon = R.drawable.ic_profile2) {
                val intent = Intent(context, PostsView::class.java)
                context.startActivity(intent)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTopBar() {
    TopAppBar(
        title = {
            Text(
                "Admin",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0D47A1)
        )
    )
}

@Composable
fun AdminBottomNavigationBar() {
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
            AdminBottomBarIcon(R.drawable.ic_file, "Home")
            AdminBottomBarIcon(R.drawable.ic_history, "Search")
            AdminBottomBarIcon(R.drawable.ic_search, "Notifications")
            AdminBottomBarIcon(R.drawable.ic_profile2, "Settings")
        }
    }
}

@Composable
fun AdminOptionButton(text: String, icon: Int, onClick: () -> Unit) {
    val context = LocalContext.current

    Button(
        onClick = { onClick() }, // La acción de clic se ejecutará aquí
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 8.dp)
            .height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCFD8DC))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = text,
                modifier = Modifier.size(18.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}



@Composable
fun AdminBottomBarIcon(iconResId: Int, contentDescription: String) {
    IconButton(onClick = { /* Implementar acción de clic */ }) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = contentDescription,
            modifier = Modifier.size(30.dp),
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
        )
    }
}
