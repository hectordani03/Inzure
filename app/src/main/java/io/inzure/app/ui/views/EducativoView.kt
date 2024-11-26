package io.inzure.app.ui.views

import android.os.Bundle
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.google.firebase.firestore.FirebaseFirestore
import io.inzure.app.R
import io.inzure.app.ui.components.SideMenu
import io.inzure.app.ui.components.TopBar
import io.inzure.app.ui.components.BottomBar
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import io.inzure.app.data.model.User
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.inzure.app.data.model.SearchItem
import io.inzure.app.ui.components.BottomSheetContent

class EducativoView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(scrim = 0, darkScrim = 0),
            navigationBarStyle = SystemBarStyle.light(scrim = 0, darkScrim = 0)
        )
        setContent {
            Educativo(onNavigateToLogin = { /* Acción de navegación al login */ },
                onNavigateToProfile = { /* Acción de navegación al perfil */ },
                onNavigateToCarInsurance = { /* Acción de navegación al seguro de autos */ },
                onNavigateToLifeInsurance = { /* Acción de navegación al seguro de vida */ },
                onNavigateToEnterpriseInsurance = { /* Acción de navegación al seguro empresarial */ },
                onNavigateToUsers = { /* Acción de navegación a la lista de usuarios */ },
                onNavigateToAdmin = { /* Acción de navegación al administrador */ },
                onNavigateToChat = { /* Acción de navegación al chat */ },
                onNavigateToGeneral = { /* Acción de navegación al general */},
                onNavigateToAutos = { /* Acción de navegación a los seguros de autos */},
                onNavigateToPersonal = { /* Acción de navegación a los seguros personales */},
                onNavigateToEmpresarial = { /* Acción de navegación a los seguros empresariales */},
                onNavigateToEducativo = { /* Acción de navegación al educativo */}
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Educativo(
    onNavigateToProfile: () -> Unit,
    onNavigateToCarInsurance: () -> Unit,
    onNavigateToLifeInsurance: () -> Unit,
    onNavigateToEnterpriseInsurance: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToGeneral: () -> Unit,
    onNavigateToAutos: () -> Unit,
    onNavigateToPersonal: () -> Unit,
    onNavigateToEmpresarial: () -> Unit,
    onNavigateToEducativo: () -> Unit

) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    var isDrawerOpen by remember { mutableStateOf(false) }
    val showChatView = remember { mutableStateOf(false) }

    // Estado del BottomSheetScaffold
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()

    // Animación de color para el sombreado al abrir el Drawer
    val scrimColor by animateColorAsState(
        targetValue = if (isDrawerOpen) Color.Black.copy(alpha = 0.5f) else Color.Transparent,
        animationSpec = tween(durationMillis = 500)
    )

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: return

    var firstName by remember { mutableStateOf("No disponible") }
    var lastName by remember { mutableStateOf("No disponible") }
    var email by remember { mutableStateOf("No disponible") }
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

    // Lista de seguros de ejemplo
    val insuranceList = listOf(
        SearchItem.InsuranceItem(
            imageRes = R.drawable.aseguradora1,
            companyLogo = R.drawable.ic_qualitas,
            companyName = "Qualitas Seguros",
            description = "Explora por los diversos seguros que tenemos"
        ),
        SearchItem.InsuranceItem(
            imageRes = R.drawable.aseguradora3,
            companyLogo = R.drawable.ic_aseguradora3,
            companyName = "MetLife",
            description = "Descubriendo la vida juntos"
        )
    )

    // Lista de chats de ejemplo
    val chatList = listOf(
        SearchItem.ChatItem(
            userName = "Maria Lopez",
            userCompany = "Asegurador de MetLife",
            userImageRes = R.drawable.ic_profile4,
            onClick = { /* Acción al hacer clic en el chat */ }
        ),
        SearchItem.ChatItem(
            userName = "Carlos Perez",
            userCompany = "Asegurador de HDI",
            userImageRes = R.drawable.ic_profile,
            onClick = { /* Acción al hacer clic en el chat */ }
        )
        // Agrega más chats según sea necesario
    )

    // Combinar las listas de seguros y chats
    val searchItems = remember { insuranceList + chatList }

    // Implementación del BottomSheetScaffold para manejar el comportamiento de la Bottom Bar
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 0.dp, // Comienza colapsado
        containerColor = Color(0xFF072A4A), // Fondo azul del Bottom Sheet
        sheetContent = {
            BottomSheetContent(searchItems)
        }
    ) {
        // Uso de ModalNavigationDrawer para el menú lateral
        ModalNavigationDrawer(
            drawerState = drawerState,
            scrimColor = scrimColor,
            drawerContent = {
                SideMenu(
                    screenWidth = screenWidth,
                    onNavigateToProfile = {
                        scope.launch { drawerState.close() }
                        onNavigateToProfile()
                    },
                    onNavigateToAdmin = {
                        scope.launch { drawerState.close() }
                        onNavigateToAdmin()
                    },
                    onNavigateToEducativo = {
                        scope.launch { drawerState.close() }
                        onNavigateToEducativo()
                    },
                    onNavigateToChat = {
                        scope.launch { drawerState.close() }
                        onNavigateToChat()
                    },
                    onNavigateToLogin = {
                        scope.launch { drawerState.close() }
                        onNavigateToLogin()
                    }, // Pasar la nueva función de navegación al Login
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
                                isDrawerOpen = true
                                drawerState.open() // Abrir el Drawer
                            }
                        },
                        onNavigateToProfile = onNavigateToProfile
                    )
                },
                bottomBar = {
                    BottomBar(
                        onSwipeUp = {
                            scope.launch {
                                bottomSheetScaffoldState.bottomSheetState.expand()
                            }
                        },
                        onNavigateToProfile = onNavigateToProfile
                    )
                }
            ) { innerPadding ->
                if (showChatView.value) {
                    // Lógica para mostrar la vista de chat
                    // Puedes implementar aquí la vista de chat según tus necesidades
                } else {
                    // Contenido principal de la pantalla
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(innerPadding)
                    ) {
                        LearnAboutInsuranceEducativo(
                            onNavigateToGeneral = { onNavigateToGeneral() }, // Sustituir con el Intent correspondiente
                            onNavigateToAutos = { onNavigateToAutos() }, // Sustituir con el Intent correspondiente
                            onNavigateToPersonal = { onNavigateToPersonal() },
                            onNavigateToEmpresarial = { onNavigateToEmpresarial() }
                        )

                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun LearnAboutInsuranceEducativo(
    onNavigateToGeneral: () -> Unit,
    onNavigateToAutos: () -> Unit,
    onNavigateToPersonal: () -> Unit,
    onNavigateToEmpresarial: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Aprende sobre seguros",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        InsuranceImageEducativo(onClick = onNavigateToGeneral)
        Spacer(modifier = Modifier.height(20.dp))
        InsuranceImageEducativo2(onClick = onNavigateToAutos)
        Spacer(modifier = Modifier.height(10.dp))
        InsuranceImageEducativo3(onClick = onNavigateToPersonal)
        Spacer(modifier = Modifier.height(10.dp))
        InsuranceImageEducativo4(onClick = onNavigateToEmpresarial)
    }
}

@Composable
fun InsuranceImageEducativo(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable { onClick() } // Navegación al hacer clic
    ) {
        Image(
            painter = painterResource(id = R.drawable.aprende_desde_0),
            contentDescription = "Insurance Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Aprende todo sobre seguros desde cero",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_learn),
                contentDescription = "Info Image",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun InsuranceImageEducativo2(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable { onClick() } // Navegación al hacer clic
    ) {
        Image(
            painter = painterResource(id = R.drawable.insurance_image2),
            contentDescription = "Insurance Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = " Aprende sobre seguros automovilísticos",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_auto),
                contentDescription = "Info Image",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun InsuranceImageEducativo3(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable { onClick() } // Navegación al hacer clic
    ) {
        Image(
            painter = painterResource(id = R.drawable.insurance_image1),
            contentDescription = "Insurance Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Aprende sobre seguros personales",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_personal),
                contentDescription = "Info Image",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun InsuranceImageEducativo4(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable { onClick() } // Navegación al hacer clic
    ) {
        Image(
            painter = painterResource(id = R.drawable.insurance_image3),
            contentDescription = "Insurance Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Aprende sobre seguros empresariales",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_empresarial),
                contentDescription = "Info Image",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}
