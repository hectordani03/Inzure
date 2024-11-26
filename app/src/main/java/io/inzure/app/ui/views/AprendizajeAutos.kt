package io.inzure.app.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.inzure.app.R
import io.inzure.app.ui.components.TopBar
import io.inzure.app.ui.components.BottomBar
import io.inzure.app.ui.components.SideMenu
import androidx.compose.foundation.shape.RoundedCornerShape
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.width
import androidx.compose.ui.platform.LocalDensity

class AprendizajeAutos : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(scrim = 0, darkScrim = 0),
            navigationBarStyle = SystemBarStyle.light(scrim = 0, darkScrim = 0)
        )
        setContent {
            LearnInsuranceViewAutos(
                onNavigateToProfile = {
                },
                onNavigateToUsers = {
                },
                onMenuClick = {
                },
                onNavigateToAdmin = {
                },
                onNavigateToLogin = {
                },
                onNavigateToEducativo = {
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnInsuranceViewAutos(
    onNavigateToProfile: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onMenuClick: () -> Unit,
    onNavigateToEducativo: () -> Unit

) {
    val context = LocalContext.current as Activity
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val showChatView = remember { mutableStateOf(false) }

    // Obtenemos el ancho de la pantalla en píxeles
    val screenWidthPx = context.resources.displayMetrics.widthPixels

    // Convertimos el ancho al 70% en píxeles y luego a dp usando density
    val density = LocalDensity.current.density
    val drawerWidthInDp = (screenWidthPx * 1f) / density

    // Uso de ModalNavigationDrawer para el menú lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier.width(drawerWidthInDp.dp) // Ajusta al 70% del ancho de la pantalla
            ) {
                // Uso del componente SideMenu importado desde SideMenu.kt
                SideMenu(
                    screenWidth = drawerWidthInDp.dp,
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
                    onNavigateToLogin = {
                        scope.launch { drawerState.close() }
                        onNavigateToLogin()
                    }, // Pasar la nueva función de navegación al Login
                    showChatView = showChatView,
                    scope = scope,
                    drawerState = drawerState
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    onNavigateToProfile = onNavigateToProfile
                )
            },
            bottomBar = {
                BottomBar(
                    onSwipeUp = { /* Acción al deslizar hacia arriba en el BottomBar */ },
                    onNavigateToUsers = onNavigateToUsers
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)
            ) {
                IntroductionSectionAutos()
                WhatIsInsuranceSectionAutos()
                WhyInsuranceImportantSectionAutos()
                BasicConceptsSectionAutos()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun IntroductionSectionAutos() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {
        // Título principal con ícono de educación
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Aprende sobre seguros automovilísticos",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color(0xFF072A4A),
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_learn), // Asegúrate de tener este recurso
                contentDescription = "Educación",
                modifier = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(Color(0xFF072A4A))
            )
        }
        // Subtítulo
        Text(
            text = "Protege tu vehículo y viaja con tranquilidad. Aquí aprenderás qué cubren los seguros automovilísticos, los tipos de coberturas disponibles y cómo elegir la mejor opción para ti.",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
        // Primera imagen ilustrativa
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.a_autos1), // Asegúrate de tener este recurso
            contentDescription = "Familia protegida",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}

@Composable
fun WhatIsInsuranceSectionAutos() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "¿Qué cubre un seguro automovilístico?",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF072A4A)
        )
        // Lista de beneficios
        Spacer(modifier = Modifier.height(8.dp))
        val concepts = listOf(
            "Responsabilidad civil: Daños a terceros.",
            "Cobertura de daños materiales: Reparación de tu vehículo.",
            "Cobertura contra robo: Robo total o parcial del auto.",
            "Gastos médicos: Cobertura para lesiones del conductor y ocupantes."
        )
        concepts.forEach { concept ->
            Text(
                text = "• $concept",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        // Ilustración
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.a_autos2), // Asegúrate de tener este recurso
            contentDescription = "Familia bajo protección",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}

@Composable
fun WhyInsuranceImportantSectionAutos() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Tipos de cobertura:",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF072A4A)
        )
        // Lista de beneficios
        Spacer(modifier = Modifier.height(8.dp))
        val concepts = listOf(
            "Cobertura básica: Solo cubre daños a terceros.",
            "Cobertura limitada: Daños a terceros + robo del auto.",
            "Cobertura amplia: Incluye todo lo anterior + daños a tu auto."
        )
        concepts.forEach { concept ->
            Text(
                text = "• $concept",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        // Imagen ilustrativa con botón de reproducción
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.a_autos3), // Asegúrate de tener este recurso
                contentDescription = "Protección de bienes",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

@Composable
fun BasicConceptsSectionAutos() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Factores que afectan el precio del seguro:",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF072A4A)
        )
        // Definiciones
        Spacer(modifier = Modifier.height(8.dp))
        val concepts2 = listOf(
            "Tipo de vehículo.",
            "Edad y género del conductor.",
            "Historial de manejo.",
        )
        concepts2.forEach { concept ->
            Text(
                text = "• $concept",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        // Ilustración
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.a_autos4), // Asegúrate de tener este recurso
            contentDescription = "Beneficios de estar asegurado",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}
