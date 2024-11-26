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

class AprendizajeGeneral : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(scrim = 0, darkScrim = 0),
            navigationBarStyle = SystemBarStyle.light(scrim = 0, darkScrim = 0)
        )
        setContent {
            LearnInsuranceView(
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
                },
                onNavigateToChat = {
                },
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnInsuranceView(
    onNavigateToProfile: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onMenuClick: () -> Unit,
    onNavigateToEducativo: () -> Unit,
    onNavigateToChat: () -> Unit,
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
                IntroductionSectionGeneral()
                WhatIsInsuranceSectionGeneral()
                WhyInsuranceImportantSectionGeneral()
                BasicConceptsSectionGeneral()
                BenefitsOfBeingInsuredSectionGeneral()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}





@Composable
fun IntroductionSectionGeneral() {
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
                text = "Aprende sobre seguros",
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
            text = "Explora diferentes tipos de seguros y cómo pueden protegerte.",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
        // Primera imagen ilustrativa
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.a_general1), // Asegúrate de tener este recurso
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
fun WhatIsInsuranceSectionGeneral() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "¿Qué es un seguro?",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF072A4A)
        )
        // Descripción
        Text(
            text = "Un contrato entre el asegurado y la aseguradora, donde esta última se compromete a indemnizar al asegurado en caso de que ocurra un evento cubierto.",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 8.dp)
        )
        // Ilustración
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.a_general2), // Asegúrate de tener este recurso
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
fun WhyInsuranceImportantSectionGeneral() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "¿Por qué son importantes los seguros?",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF072A4A)
        )
        // Lista de beneficios
        Spacer(modifier = Modifier.height(8.dp))
        val concepts = listOf(
            "Proveen seguridad financiera.",
            "Protegen contra riesgos imprevistos.",
            "Ayudan a mantener estabilidad económica en situaciones adversas."
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
                painter = painterResource(id = R.drawable.a_general3), // Asegúrate de tener este recurso
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
fun BasicConceptsSectionGeneral() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Conceptos básicos:",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF072A4A)
        )
        // Definiciones
        Spacer(modifier = Modifier.height(8.dp))
        val concepts2 = listOf(
            "Póliza: El documento que especifica el contrato entre asegurado y aseguradora.",
            "Prima: La cantidad que pagas por el seguro.",
            "Cobertura: Las situaciones que el seguro protege.",
            "Deducible: La cantidad que debes pagar antes de que el seguro entre en acción."
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
            painter = painterResource(id = R.drawable.a_general4), // Asegúrate de tener este recurso
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
fun BenefitsOfBeingInsuredSectionGeneral() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Beneficios de estar asegurado:",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF072A4A)
        )
        // Lista de beneficios
        Spacer(modifier = Modifier.height(8.dp))
        val concepts3 = listOf(
            "Reducción de gastos inesperados.",
            "Tranquilidad mental.",
            "Protección para tus seres queridos y bienes."
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
            painter = painterResource(id = R.drawable.a_general5), // Asegúrate de tener este recurso
            contentDescription = "Beneficios de estar asegurado",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}
