package io.inzure.app.ui.views

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.inzure.app.R
import io.inzure.app.data.model.User
import io.inzure.app.ui.components.SideMenu
import io.inzure.app.ui.components.TopBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
        // Agrega más composables según tus necesidades
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
    var firstName by remember { mutableStateOf("No disponible") }
    var lastName by remember { mutableStateOf("No disponible") }
    var imageUri by remember { mutableStateOf<String?>(null) }

    // Obtener datos del usuario desde Firestore
    LaunchedEffect(userId) {
        if (userId != null) {
            firestore.collection("Users")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    var userRole: String? = null
                    for (document in querySnapshot.documents) {
                        val documentPath = document.id
                        firestore.collection("Users")
                            .document(documentPath)
                            .collection("userData")
                            .document(userId)
                            .get()
                            .addOnSuccessListener { userDoc ->
                                if (userDoc.exists()) {
                                    userRole = documentPath
                                    val userData = userDoc.toObject(User::class.java)
                                    if (userData != null) {
                                        firstName = userData.firstName
                                        lastName = userData.lastName
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

    // Inicializar DrawerState y CoroutineScope para el SideMenu
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val showChatView = remember { mutableStateOf(false) }

    // Animación de color para el sombreado al abrir el Drawer
    val scrimColor by animateColorAsState(
        targetValue = if (drawerState.isOpen) Color.Black.copy(alpha = 0.5f) else Color.Transparent,
        animationSpec = tween(durationMillis = 500)
    )

    // Uso de ModalNavigationDrawer para el menú lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = scrimColor,
        drawerContent = {
            SideMenu(
                screenWidth = screenWidth,
                onNavigateToProfile = { /* Navegación a perfil si es necesario */ },
                showChatView = showChatView,
                scope = scope,
                drawerState = drawerState
            )
        }
    ) {
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
                    // Imagen de perfil
                    Image(
                        painter = painterResource(R.drawable.ic_profile3),
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
                    text = "$firstName $lastName",
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
                OptionButton("Información Personal", R.drawable.ic_profile2) {
                    navController.navigate("personal_information")
                }
                OptionButton("Mis Seguros", R.drawable.ic_profile2) {
                    // Implementar navegación a 'Mis Seguros'
                }
                OptionButton("Mis Agentes", R.drawable.ic_profile2) {
                    // Implementar navegación a 'Mis Agentes'
                }
            }
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
                color = Color.Black // Texto en negro
            )
        }
    }
}
