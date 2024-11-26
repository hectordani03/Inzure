// ProfileView.kt
package io.inzure.app.ui.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.inzure.app.InzureTheme
import io.inzure.app.R
import io.inzure.app.data.model.User
import io.inzure.app.ui.components.BottomBar
import io.inzure.app.ui.components.SideMenu
import io.inzure.app.ui.components.TopBar
import kotlinx.coroutines.launch

// Importar MyPostsScreen y otras vistas necesarias
import io.inzure.app.ui.views.MyPostsListView
import io.inzure.app.ui.views.LoginView
import io.inzure.app.ui.views.PersonalInformationView

class ProfileView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(scrim = 0, darkScrim = 0),
            navigationBarStyle = SystemBarStyle.light(scrim = 0, darkScrim = 0)
        )
        setContent {
            InzureTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideMenu(
                onNavigateToProfile = {
                    navController.navigate("profile") {
                        popUpTo("profile") { inclusive = true }
                    }
                },
                onNavigateToAdmin = { navController.navigate("admin") },
                onNavigateToLogin = {
                    // Cerrar sesión y redirigir al login
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(context, LoginView::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                },
                showChatView = remember { mutableStateOf(false) },
                scope = scope,
                drawerState = drawerState,
                screenWidth = screenWidth
            )
        }
    ) {
        NavHost(navController, startDestination = "profile") {
            composable("profile") { ProfileScreen(navController) }
            composable("personal_information") { PersonalInformationView() }
            composable("my_posts") { MyPostsListView() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    // Inicializar Firebase
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: return

    // Estados para almacenar información del usuario
    var firstName by remember { mutableStateOf("Cargando...") }
    var lastName by remember { mutableStateOf("Cargando...") }
    var description by remember { mutableStateOf("Cargando...") }
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
                        description = userData.description
                        imageUri = userData.image
                    } else {
                        Log.e(
                            "Firestore",
                            "El documento existe, pero no se pudo mapear a un objeto User"
                        )
                    }
                } else {
                    Log.e("Firestore", "El documento del usuario no existe en Firestore")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al obtener el documento del usuario: ", e)
            }
    }

    // CoroutineScope para manejar la apertura del drawer desde la barra de navegación
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val showChatView = remember { mutableStateOf(false) }

    // Animación de color para el sombreado al abrir el Drawer
    val scrimColor by animateColorAsState(
        targetValue = if (drawerState.isOpen) Color.Black.copy(alpha = 0.5f) else Color.Transparent,
        animationSpec = tween(durationMillis = 500)
    )

    // Obtener el contexto para iniciar actividades
    val context = LocalContext.current

    // Nota: Eliminamos ModalNavigationDrawer de ProfileScreen
    // Ya está manejado en MainScreen

    // Uso de Scaffold para mantener la TopBar y la BottomBar fijas
    Scaffold(
        topBar = {
            TopBar(
                onMenuClick = {
                    scope.launch {
                        drawerState.open() // Abrir el Drawer al hacer clic en el menú
                    }
                },
                onNavigateToProfile = { /* Implementar si es necesario */ }
            )
        },
        bottomBar = {
            BottomBar(
                onSwipeUp = { /* Implementar acción si es necesario */ },
                onNavigateToUsers = { /* Implementar navegación a Users si es necesario */ }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
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
                // Sección de perfil
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
                    Image(
                        painter = painterResource(R.drawable.ic_profile_default),
                        contentDescription = "Foto de Perfil",
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre del usuario
            val name = "${firstName.split(" ").first()} ${lastName.split(" ").first()}"
            Text(
                text = name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Descripción del usuario
            Text(
                text = description,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botones de opciones estilizados
            OptionButton("Información Personal", R.drawable.ic_profile2) {
                navController.navigate("personal_information")
            }
            OptionButton("Mis Posts", R.drawable.ic_profile2) {
                // Navegar a la ruta "my_posts" utilizando NavController
                navController.navigate("my_posts")
            }
            OptionButton("Mis Agentes", R.drawable.ic_profile2) {
                // Implementar navegación a 'Mis Agentes'
                // Por ejemplo:
                // navController.navigate("my_agents")
            }
        }
    }
}

/**
 * Función composable para un botón de opción personalizado.
 *
 * @param text Texto que se mostrará en el botón.
 * @param icon Recurso de imagen para el icono del botón.
 * @param onClick Función que se ejecutará al hacer clic en el botón.
 */
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
                color = Color.Black // Texto en negro
            )
        }
    }
}
