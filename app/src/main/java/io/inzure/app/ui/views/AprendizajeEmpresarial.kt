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

class AprendizajeEmpresarial : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(scrim = 0, darkScrim = 0),
            navigationBarStyle = SystemBarStyle.light(scrim = 0, darkScrim = 0)
        )
        setContent {
            LearnInsuranceViewEmpresarial(
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
                onNavigateToChat = {
                },
                onNavigateToEducativo = {
                },
                onNavigateToQuiz = {
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnInsuranceViewEmpresarial(
    onNavigateToProfile: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onMenuClick: () -> Unit,
    onNavigateToEducativo: () -> Unit,
    onNavigateToQuiz: () -> Unit,
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
                    onNavigateToQuiz = {
                        scope.launch { drawerState.close() }
                        onNavigateToQuiz()
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
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)
            ) {
                IntroductionSectionEmpresarial()
                WhatIsInsuranceSectionEmpresarial()
                WhyInsuranceImportantSectionEmpresarial()
                BasicConceptsSectionEmpresarial()
                BenefitsOfBeingInsuredSectionEmpresarial()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun IntroductionSectionEmpresarial() {
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
                text = "Aprende sobre seguros empresariales",
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
            text = "Protege tu negocio y empleados con seguros empresariales que brindan respaldo ante imprevistos.",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
        // Primera imagen ilustrativa
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.a_empresarial1), // Asegúrate de tener este recurso
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
fun WhatIsInsuranceSectionEmpresarial() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "¿Qué son los seguros empresariales?",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF072A4A)
        )
        // Descripción
        val concepts = listOf(
            "Diseñados para proteger los activos, operaciones y empleados de una empresa.",
            "Ofrecen protección financiera ante riesgos específicos."
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
            painter = painterResource(id = R.drawable.a_empresarial2), // Asegúrate de tener este recurso
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
fun WhyInsuranceImportantSectionEmpresarial() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Tipos de seguros empresariales:",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF072A4A)
        )
        // Lista de beneficios
        Spacer(modifier = Modifier.height(8.dp))
        val concepts = listOf(
            "Seguro de responsabilidad civil:\n" +
                    "Protege contra demandas por daños a terceros (clientes, proveedores).\n" +
                    "Ejemplo: Un cliente se lesiona dentro de tu negocio y el seguro cubre los gastos legales y médicos.",
            "Seguro contra robos y daños:\n" +
                    "Cobertura ante pérdidas por robos o vandalismo.\n" +
                    "Incluye maquinaria, mercancías y equipo.",
            "Seguro para empleados:\n" +
                    "Beneficios médicos o de vida para los trabajadores.\n" +
                    "Ejemplo: Cobertura médica por accidentes laborales."
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
                painter = painterResource(id = R.drawable.a_empresarial3), // Asegúrate de tener este recurso
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
fun BasicConceptsSectionEmpresarial() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Beneficios para las empresas:",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF072A4A)
        )
        // Definiciones
        Spacer(modifier = Modifier.height(8.dp))
        val concepts2 = listOf(
            "Reducción de riesgos financieros.",
            "Cumplimiento de regulaciones legales.",
            "Mayor confianza de clientes y empleados."
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
            painter = painterResource(id = R.drawable.a_empresarial4), // Asegúrate de tener este recurso
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
fun BenefitsOfBeingInsuredSectionEmpresarial() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Consejos para pequeños negocios:",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF072A4A)
        )
        // Lista de beneficios
        Spacer(modifier = Modifier.height(8.dp))
        val concepts3 = listOf(
            "Evalúa los riesgos específicos de tu industria.",
            "Contrata coberturas esenciales (incendios, robos, responsabilidad civil).",
            "Revisa anualmente las condiciones de tu póliza."
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
            painter = painterResource(id = R.drawable.a_general2), // Asegúrate de tener este recurso
            contentDescription = "Beneficios de estar asegurado",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}
