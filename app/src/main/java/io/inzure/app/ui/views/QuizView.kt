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
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.unit.dp

class QuizView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(scrim = 0, darkScrim = 0),
            navigationBarStyle = SystemBarStyle.light(scrim = 0, darkScrim = 0)
        )
        setContent {
            QuizInsuranceView(
                onNavigateToProfile = { },
                onNavigateToUsers = { },
                onMenuClick = { },
                onNavigateToAdmin = { },
                onNavigateToLogin = { },
                onNavigateToEducativo = { },
                onNavigateToQuiz = { }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizInsuranceView(
    onNavigateToProfile: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onMenuClick: () -> Unit,
    onNavigateToEducativo: () -> Unit,
    onNavigateToQuiz: () -> Unit
) {
    val context = LocalContext.current as Activity
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    var isDrawerOpen by remember { mutableStateOf(false) }
    val showChatView = remember { mutableStateOf(false) } // Estado para el ChatView

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier.width(screenWidth * 0.7f) // Ajusta al 70% del ancho de la pantalla
            ) {
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
                    },
                    onNavigateToQuiz = {
                        scope.launch { drawerState.close() }
                        onNavigateToQuiz()
                    },
                    showChatView = remember { mutableStateOf(false) },
                    scope = scope,
                    drawerState = drawerState,
                    onNavigateToChat = {
                        scope.launch { drawerState.close() }
                        showChatView.value = true // Activar el ChatView
                    },
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
                    onSwipeUp = {
                        scope.launch {
                            bottomSheetScaffoldState.bottomSheetState.expand()
                        }
                    },
                    onNavigateToProfile = onNavigateToProfile,
                    onNavigateToChat = { showChatView.value = true } // Activar el ChatView desde la BottomBar
                )
            },
        ) { innerPadding ->
            QuizContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)
            )
        }
    }
}

@Composable
fun QuizContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current.applicationContext
    val currentQuestionIndex = remember { mutableStateOf(0) }
    val correctAnswersCount = remember { mutableStateOf(0) }
    val totalQuestions = remember { mutableStateOf(0) }

    val primaryColor = Color(0xFF072A4A)
    val secondaryColor = Color.White
    val accentColor = Color(0xFF1E90FF)

    val questions = listOf(
        "¿Qué es un seguro?",
        "¿Cuál es la función de una prima en un seguro?",
        "¿Qué es el deducible?",
        "¿Por qué es importante estar asegurado?"
    )
    val answers = listOf(
        listOf("Un contrato de protección", "Un ahorro", "Una inversión"),
        listOf("Es el monto que paga la aseguradora", "Es el pago periódico del asegurado", "Es un reembolso"),
        listOf("El pago que hace el asegurado antes de la cobertura", "Un documento legal", "El costo del seguro completo"),
        listOf("Para mitigar riesgos financieros", "Para ahorrar dinero", "Para invertir")
    )
    val correctAnswers = listOf(0, 1, 0, 0)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F8)) // Fondo suave
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Título encerrado en un contenedor con el color primario
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(primaryColor)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Quiz: ¿Qué tanto sabes sobre seguros?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = secondaryColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (currentQuestionIndex.value < questions.size) {
                // Contenedor de la pregunta
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(primaryColor.copy(alpha = 0.1f))
                        .border(1.dp, primaryColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = questions[currentQuestionIndex.value],
                        style = MaterialTheme.typography.bodyLarge,
                        color = primaryColor,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Respuestas
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    answers[currentQuestionIndex.value].forEachIndexed { index, answer ->
                        Button(
                            onClick = {
                                // Registrar respuesta
                                totalQuestions.value++
                                if (index == correctAnswers[currentQuestionIndex.value]) {
                                    correctAnswersCount.value++
                                }

                                // Avanzar a la siguiente pregunta
                                currentQuestionIndex.value++
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryColor,
                                contentColor = secondaryColor
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text(
                                text = answer,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // Pantalla de resultados
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "¡Quiz Completado!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = primaryColor,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Box(
                            modifier = Modifier
                                .background(primaryColor, shape = RoundedCornerShape(12.dp))
                                .padding(24.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Tu Puntuación",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = secondaryColor
                                )
                                Text(
                                    text = "${correctAnswersCount.value}/${totalQuestions.value}",
                                    style = MaterialTheme.typography.displayMedium,
                                    color = accentColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}