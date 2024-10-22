package io.inzure.app.ui.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.border
import androidx.compose.ui.zIndex
import io.inzure.app.R

class ProfileView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "profile") {
        composable("profile") { ProfileScreen(navController) }
        composable("personal_information") { PersonalInformationView() }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavigationBar() }
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
                    painter = painterResource(R.drawable.ic_profile3), // Reemplaza con tu imagen
                    contentDescription = "Foto de Perfil",
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre del usuario
            Text(
                text = "Jose Joshua",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Descripción del usuario
            Text(
                text = "Me gustan los gatos",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botones de opciones estilizados
            OptionButton("Informacion Personal", R.drawable.ic_profile2) {
                navController.navigate("personal_information")
            }
            OptionButton("Mis Seguros", R.drawable.ic_profile2) {
                // Implementar navegación a 'Mis Seguros'
            }
            OptionButton("Mis agentes", R.drawable.ic_profile2) {
                // Implementar navegación a 'Mis agentes'
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Text(
                "Mi perfil",
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
fun BottomNavigationBar() {
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

@Composable
fun OptionButton(text: String, icon: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
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
                color = Color.Black // Cambiado a negro
            )
        }
    }
}
