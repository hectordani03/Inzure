// MainView.kt
package io.inzure.app.ui.views

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

import io.inzure.app.data.model.SearchItem
import io.inzure.app.ui.components.BottomSheetContent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    onNavigateToProfile: () -> Unit,
    onNavigateToCarInsurance: () -> Unit,
    onNavigateToLifeInsurance: () -> Unit,
    onNavigateToEnterpriseInsurance: () -> Unit, // Nuevo parámetro
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
                        showChatView.value = true
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
                        onNavigateToUsers = onNavigateToUsers
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
                        WelcomeMessage(firstName = firstName, lastName = lastName)
                        InsuranceCategories(
                            onNavigateToCarInsurance = onNavigateToCarInsurance,
                            onNavigateToLifeInsurance = onNavigateToLifeInsurance,
                            onNavigateToEnterpriseInsurance = onNavigateToEnterpriseInsurance // Pasar la nueva función
                        )
                        LearnAboutInsurance(
                            onNavigateToGeneral = { onNavigateToGeneral() }, // Sustituir con el Intent correspondiente
                            onNavigateToAutos = { onNavigateToAutos() }, // Sustituir con el Intent correspondiente
                            onNavigateToPersonal = { onNavigateToPersonal() },
                            onNavigateToEmpresarial = { onNavigateToEmpresarial() } // Sustituir con el Intent correspondiente
                        )

                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeMessage(firstName: String, lastName: String) {
    val name = "${firstName.split(" ").first()} ${lastName.split(" ").first()}"
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "¡Bienvenid@, $name!",
            modifier = Modifier
                .align(Alignment.Center)
                .background(color = Color(0xFF072A4A), shape = RoundedCornerShape(8.dp))
                .padding(16.dp),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun InsuranceCategories(
    onNavigateToCarInsurance: () -> Unit,
    onNavigateToLifeInsurance: () -> Unit,
    onNavigateToEnterpriseInsurance: () -> Unit // Nuevo parámetro
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Categorías de Seguros",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InsuranceCategory("Autos", R.drawable.ic_auto, onClick = onNavigateToCarInsurance)
            InsuranceCategory("Personal", R.drawable.ic_personal, onClick = onNavigateToLifeInsurance)
            InsuranceCategory("Empresarial", R.drawable.ic_empresarial, onClick = onNavigateToEnterpriseInsurance) // Actualizar onClick
        }
    }
}

@Composable
fun InsuranceCategory(name: String, iconResId: Int, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = name,
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = name,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun LearnAboutInsurance(
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
        InsuranceImage(onClick = onNavigateToGeneral)
        Spacer(modifier = Modifier.height(20.dp))
        InsuranceImage2(onClick = onNavigateToAutos)
        Spacer(modifier = Modifier.height(10.dp))
        InsuranceImage3(onClick = onNavigateToPersonal)
        Spacer(modifier = Modifier.height(10.dp))
        InsuranceImage4(onClick = onNavigateToEmpresarial)
    }
}

@Composable
fun InsuranceImage(onClick: () -> Unit) {
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
fun InsuranceImage2(onClick: () -> Unit) {
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
fun InsuranceImage3(onClick: () -> Unit) {
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
fun InsuranceImage4(onClick: () -> Unit) {
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
