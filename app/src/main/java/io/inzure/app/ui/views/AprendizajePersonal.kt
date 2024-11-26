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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.inzure.app.R
import io.inzure.app.ui.components.TopBar
import io.inzure.app.ui.components.BottomBar
import io.inzure.app.ui.components.SideMenu
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.CircleShape
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.MutableState
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.unit.dp

class AprendizajePersonal : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(scrim = 0, darkScrim = 0),
            navigationBarStyle = SystemBarStyle.light(scrim = 0, darkScrim = 0)
        )
        setContent {
            LearnInsuranceViewPersonal(
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
fun LearnInsuranceViewPersonal(
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
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val showChatView = remember { mutableStateOf(false) }

    // Obtenemos el ancho de la pantalla en píxeles
    val screenWidthPx = context.resources.displayMetrics.widthPixels

    // Convertimos el ancho al 70% en píxeles y luego a dp usando density
    val density = LocalDensity.current.density
    val drawerWidthInDp = (screenWidthPx * 0.7f) / density

    // Uso de ModalNavigationDrawer para el menú lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier.width(drawerWidthInDp.dp) // Ajusta al 70% del ancho de la pantalla
            ) {
                // Uso del componente SideMenu importado desde SideMenu.kt
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
                IntroductionSectionPersonal()
                WhatIsInsuranceSectionPersonal()
                WhyInsuranceImportantSectionPersonal()
                BasicConceptsSectionPersonal()
                BenefitsOfBeingInsuredSectionPersonal()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun IntroductionSectionPersonal() {
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
                text = "Aprende sobre seguros personales",
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
            text = "Protege tu vida, salud y bienes con seguros personales que brindan respaldo ante imprevistos.",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
        // Primera imagen ilustrativa
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.a_personal1), // Asegúrate de tener este recurso
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
fun WhatIsInsuranceSectionPersonal() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "¿Qué son los seguros personales?",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF072A4A)
        )
        // Descripción
        val concepts = listOf(
            "Diseñados para proteger tu vida, salud y bienes personales.",
            "Ofrecen seguridad financiera ante eventos inesperados."
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
            painter = painterResource(id = R.drawable.a_personal2), // Asegúrate de tener este recurso
            contentDescription = "Protección personal",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}

@Composable
fun WhyInsuranceImportantSectionPersonal() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Tipos de seguros personales:",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF072A4A)
        )
        // Lista de tipos de seguros personales
        Spacer(modifier = Modifier.height(8.dp))
        val concepts = listOf(
            "Seguro de vida:\n" +
                    "Proporciona un beneficio económico a tus beneficiarios en caso de fallecimiento.\n" +
                    "Ejemplo: Ayuda a cubrir gastos funerarios y deudas pendientes.",
            "Seguro de salud:\n" +
                    "Cubre gastos médicos y hospitalarios.\n" +
                    "Incluye consultas, tratamientos y medicamentos.",
            "Seguro de automóvil:\n" +
                    "Protege tu vehículo contra daños, robos y accidentes.\n" +
                    "Incluye cobertura de responsabilidad civil."
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
                painter = painterResource(id = R.drawable.a_personal3), // Asegúrate de tener este recurso
                contentDescription = "Protección de bienes personales",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
            )
            // Puedes agregar un botón de reproducción aquí si es necesario
        }
    }
}

@Composable
fun BasicConceptsSectionPersonal() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Beneficios de los seguros personales:",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF072A4A)
        )
        // Definiciones
        Spacer(modifier = Modifier.height(8.dp))
        val concepts2 = listOf(
            "Seguridad financiera para tu familia.",
            "Acceso a servicios de salud de calidad.",
            "Protección de tus bienes personales."
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
            painter = painterResource(id = R.drawable.a_personal4), // Asegúrate de tener este recurso
            contentDescription = "Beneficios de estar asegurado",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}

@Composable
fun BenefitsOfBeingInsuredSectionPersonal() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Consejos para asegurar tus bienes personales:",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF072A4A)
        )
        // Lista de consejos
        Spacer(modifier = Modifier.height(8.dp))
        val concepts3 = listOf(
            "Evalúa tus necesidades y prioridades de cobertura.",
            "Compara diferentes pólizas y compañías aseguradoras.",
            "Revisa y actualiza tu póliza anualmente."
        )
        concepts3.forEach { concept ->
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
            painter = painterResource(id = R.drawable.a_personal5), // Asegúrate de tener este recurso
            contentDescription = "Consejos de aseguramiento personal",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}
