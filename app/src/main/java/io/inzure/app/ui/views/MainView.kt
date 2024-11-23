package io.inzure.app.ui.views

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import io.inzure.app.R
import io.inzure.app.ui.components.SideMenu
import io.inzure.app.ui.components.TopBar
import io.inzure.app.ui.views.BottomBar
import kotlinx.coroutines.launch

// Clase de datos para la lista de seguros
data class InsuranceData(
    val imageRes: Int,
    val companyLogo: Int,
    val companyName: String,
    val description: String
)

// Clase de datos para chats
data class Chat(
    val userName: String,
    val userCompany: String,
    val userImageRes: Int
)

// Data class para representar un mensaje
data class Message(val text: String, val isSentByUser: Boolean)

// Lista de ejemplo de mensajes
val sampleMessages = listOf(
    Message("Hola, Buen día, está interesado en algún seguro?", false),
    Message("Sí, me interesa el seguro automovilístico", true),
    Message("Me podría dar más información", true),
    Message("Claro, por el momento estamos manejando un seguro de cobertura total que incluye...", false),
    Message("Perfecto, ¿cuáles son los precios?", true)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    onNavigateToProfile: () -> Unit,
    onNavigateToCarInsurance: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToChat: () -> Unit
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

    // Lista de seguros de ejemplo
    val insuranceList = listOf(
        InsuranceData(
            imageRes = R.drawable.aseguradora1,
            companyLogo = R.drawable.ic_qualitas,
            companyName = "Qualitas Seguros",
            description = "Explora por los diversos seguros que tenemos"
        ),
        InsuranceData(
            imageRes = R.drawable.aseguradora3,
            companyLogo = R.drawable.ic_aseguradora3,
            companyName = "MetLife",
            description = "Descubriendo la vida juntos"
        ),
        InsuranceData(
            imageRes = R.drawable.aseguradora2,
            companyLogo = R.drawable.ic_aseguradora2,
            companyName = "GNP Seguros",
            description = "Cobertura completa para tu auto"
        ),
        InsuranceData(
            imageRes = R.drawable.aseguradora4,
            companyLogo = R.drawable.ic_aseguradora4,
            companyName = "HDI Seguros",
            description = "Hacer fáciles tus momentos difíciles"
        ),
        InsuranceData(
            imageRes = R.drawable.aseguradora5,
            companyLogo = R.drawable.ic_aseguradora5,
            companyName = "Zurich Seguros",
            description = "Cobertura total para tu hogar"
        )
    )

    // Implementación del BottomSheetScaffold para manejar el comportamiento de la Bottom Bar
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 0.dp, // Comienza colapsado
        containerColor = Color(0xFF072A4A), // Fondo azul del Bottom Sheet
        sheetContent = {
            BottomSheetContent(insuranceList)
        }
    ) {
        // Uso de ModalNavigationDrawer para el menú lateral
        ModalNavigationDrawer(
            drawerState = drawerState,
            scrimColor = scrimColor,
            drawerContent = {
                // Uso del componente SideMenu importado desde SideMenu.kt
                SideMenu(
                    screenWidth = screenWidth,
                    onNavigateToProfile = onNavigateToProfile,
                    showChatView = showChatView,
                    scope = scope,
                    drawerState = drawerState
                )
            }
        ) {
            // Uso de Scaffold para mantener la TopBar y la BottomBar fijas
            Scaffold(
                topBar = {
                    // Importa TopBar desde SideMenu.kt
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
                    // Mostrar la vista de chats si el estado está activado
                    val chatList = listOf(
                        Chat("Jose Joshua", "Asegurador de Qualitas", R.drawable.ic_profile5),
                        Chat("Maria Lopez", "Asegurador de MetLife", R.drawable.ic_profile4),
                        Chat("Carlos Perez", "Asegurador de HDI", R.drawable.ic_profile)
                    )
                    ChatListView(chats = chatList, onClose = { showChatView.value = false })
                } else {
                    // Contenido principal de la pantalla
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(innerPadding)
                    ) {
                        WelcomeMessage()
                        InsuranceCategories(onNavigateToCarInsurance)
                        LearnAboutInsurance()
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListView(chats: List<Chat>, onClose: () -> Unit) {
    var selectedChat by remember { mutableStateOf<Chat?>(null) }

    selectedChat?.let { chat ->
        // Mostrar vista de chat individual solo si selectedChat no es null
        IndividualChatView(chat = chat, onClose = { selectedChat = null })
    } ?: run {
        // Mostrar lista de chats cuando no hay chat seleccionado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xFF072A4A))
                .padding(top = (LocalConfiguration.current.screenHeightDp * 0.22).dp) // Mover la vista hacia abajo
        ) {
            // Encabezado del desplegable de chats con gesto de arrastre
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF04305A))
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            if (dragAmount > 20) {
                                onClose()
                            }
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White)
                        .padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Mis Chats",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onClose) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chat),
                            contentDescription = "Cerrar",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF072A4A))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chats) { chat ->
                    ChatItem(
                        userName = chat.userName,
                        userCompany = chat.userCompany,
                        userImageRes = chat.userImageRes,
                        onClick = { selectedChat = chat } // Seleccionar el chat al hacer clic
                    )
                }
            }
        }
    }
}

@Composable
fun ChatItem(
    userName: String,
    userCompany: String,
    userImageRes: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF04305A), RoundedCornerShape(12.dp))
            .padding(12.dp)
            .clickable { onClick() }, // Acción de clic para seleccionar el chat
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = userImageRes),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = userName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = userCompany,
                fontSize = 14.sp,
                color = Color.LightGray
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_chat),
            contentDescription = "Chat",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun IndividualChatView(chat: Chat, onClose: () -> Unit) {
    val listState = rememberLazyListState()
    var currentMessage by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>().apply { addAll(sampleMessages) } }

    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size - 1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF072A4A))
            .padding(top = 170.dp) // Baja toda la vista un poco verticalmente
            .padding(horizontal = 16.dp)
    ) {
        // Encabezado de chat individual
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto de perfil
            Image(
                painter = painterResource(id = chat.userImageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Nombre y descripción
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.userName,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = chat.userCompany,
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }

            // Botón de cerrar
            IconButton(onClick = onClose) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Cerrar",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de mensajes en el chat
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 50.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 70.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(message)
                }
            }
        }

        // Caja de texto para enviar mensajes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .offset(y = (-120).dp)
                .background(Color(0xFF04305A), RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = currentMessage,
                onValueChange = { currentMessage = it },
                placeholder = {
                    Text(
                        text = "Escribe algo...",
                        color = Color.LightGray
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Transparent),
                textStyle = TextStyle(color = Color.White),
                maxLines = 1,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedPlaceholderColor = Color.LightGray,
                    focusedPlaceholderColor = Color.LightGray,
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White
                ),
                keyboardOptions = KeyboardOptions.Default,
                keyboardActions = KeyboardActions.Default
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (currentMessage.isNotBlank()) {
                        messages.add(Message(currentMessage, true)) // Agrega el mensaje enviado
                        currentMessage = "" // Limpia el campo de entrada
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_send), // Reemplaza con tu ícono de enviar
                    contentDescription = "Enviar",
                    tint = Color(0xFF007AFF),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isSentByUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            contentAlignment = if (message.isSentByUser) Alignment.CenterEnd else Alignment.CenterStart,
            modifier = Modifier
                .padding(horizontal = 8.dp)
        ) {
            // Globo de texto
            Box(
                modifier = Modifier
                    .background(
                        if (message.isSentByUser) Color(0xFF007AFF) else Color(0xFF04305A),
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomEnd = if (message.isSentByUser) 0.dp else 12.dp,
                            bottomStart = if (message.isSentByUser) 12.dp else 0.dp
                        )
                    )
                    .padding(12.dp)
                    .widthIn(max = 250.dp)
            ) {
                Text(
                    text = message.text,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            // Pico del globo de texto
            Canvas(
                modifier = Modifier
                    .size(15.dp)
                    .align(
                        if (message.isSentByUser) Alignment.BottomEnd else Alignment.BottomStart
                    )
                    .offset(x = if (message.isSentByUser) (-5).dp else 5.dp, y = 0.dp)
            ) {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width / 2, size.height)
                    close()
                }
                drawPath(
                    path = path,
                    color = if (message.isSentByUser) Color(0xFF007AFF) else Color(0xFF04305A)
                )
            }
        }
    }
}

@Composable
fun BottomSheetContent(insuranceList: List<InsuranceData>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.75f)
            .background(Color(0xFF072A4A))
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Buscar",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campo de búsqueda
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Lupa",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            SearchField()
        }

        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 16.dp)
                .background(Color.White.copy(alpha = 0.6f))
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp)
        ) {
            items(insuranceList) { insurance ->
                InsuranceCardWithImage(
                    imageRes = insurance.imageRes,
                    companyLogo = insurance.companyLogo,
                    companyName = insurance.companyName,
                    description = insurance.description
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField() {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        placeholder = {
            Text(
                text = "Encuentra tu seguro",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        textStyle = TextStyle(
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        ),
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            cursorColor = Color.White
        )
    )
}

@Composable
fun InsuranceCardWithImage(imageRes: Int, companyLogo: Int, companyName: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = companyLogo),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = companyName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Íconos del corazón y de la flecha
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_personal),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(bottom = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow),
                    contentDescription = "Desplegar más",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun WelcomeMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "¡Buenas tardes, Gilberto Ceja!",
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
fun InsuranceCategories(onNavigateToCarInsurance: () -> Unit) {
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
            InsuranceCategory("Personal", R.drawable.ic_personal, onClick = { /* Navegación Personal */ })
            InsuranceCategory("Empresarial", R.drawable.ic_empresarial, onClick = { /* Navegación Empresarial */ })
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
fun LearnAboutInsurance() {
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
        InsuranceImage()
        Spacer(modifier = Modifier.height(20.dp))
        InsuranceImage2()
        Spacer(modifier = Modifier.height(10.dp))
        InsuranceImage3()
        Spacer(modifier = Modifier.height(10.dp))
        InsuranceImage4()
    }
}

@Composable
fun InsuranceImage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .drawBehind {
                for (i in 1..3) {
                    drawRoundRect(
                        color = Color.Gray.copy(alpha = .1f),
                        size = size.copy(height = size.height + (i * .5).dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx()),
                        blendMode = BlendMode.Multiply,
                        topLeft = Offset(0f, (i * 2).dp.toPx())
                    )
                }
                drawRoundRect(
                    color = Color.Gray.copy(alpha = .2f),
                    size = size.copy(height = size.height + .5.dp.toPx()),
                    cornerRadius = CornerRadius(8.dp.toPx()),
                    blendMode = BlendMode.Multiply,
                    topLeft = Offset(0f, 4.dp.toPx())
                )
            }
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
    ) {
        // Imagen principal
        Image(
            painter = painterResource(id = R.drawable.aprende_desde_0),
            contentDescription = "Insurance Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Caja con el texto y la imagen a la derecha
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Texto dentro de la caja
            Text(
                text = "Aprende todo sobre seguros desde cero",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Imagen a la derecha del texto
            Image(
                painter = painterResource(id = R.drawable.ic_learn),
                contentDescription = "Info Image",
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun InsuranceImage2() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(bottom = 16.dp)
            // Aplicamos la sombra usando elevation
            .drawBehind {
                // Dibujamos múltiples sombras con diferentes opacidades para crear efecto blur
                for (i in 1..3) {
                    drawRoundRect(
                        color = Color.Gray.copy(alpha = .1f),
                        size = size.copy(height = size.height + (i * .5).dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx()),
                        blendMode = BlendMode.Multiply,
                        topLeft = Offset(0f, (i * 2).dp.toPx())
                    )
                }
                // Sombra principal
                drawRoundRect(
                    color = Color.Gray.copy(alpha = .2f),
                    size = size.copy(height = size.height + .5.dp.toPx()),
                    cornerRadius = CornerRadius(8.dp.toPx()),
                    blendMode = BlendMode.Multiply,
                    topLeft = Offset(0f, 4.dp.toPx())
                )
            }
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
    ) {
        // Imagen principal
        Image(
            painter = painterResource(id = R.drawable.insurance_image2),
            contentDescription = "Insurance Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Caja con el texto y la imagen a la derecha
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Texto dentro de la caja
            Text(
                text = " Aprende sobre seguros automovilísticos",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Imagen a la derecha del texto
            Image(
                painter = painterResource(id = R.drawable.ic_auto),
                contentDescription = "Info Image",
                modifier = Modifier
                    .size(30.dp)
                    .padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun InsuranceImage3() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(bottom = 16.dp)
            // Aplicamos la sombra usando elevation
            .drawBehind {
                // Dibujamos múltiples sombras con diferentes opacidades para crear efecto blur
                for (i in 1..3) {
                    drawRoundRect(
                        color = Color.Gray.copy(alpha = .1f),
                        size = size.copy(height = size.height + (i * .5).dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx()),
                        blendMode = BlendMode.Multiply,
                        topLeft = Offset(0f, (i * 2).dp.toPx())
                    )
                }
                // Sombra principal
                drawRoundRect(
                    color = Color.Gray.copy(alpha = .2f),
                    size = size.copy(height = size.height + .5.dp.toPx()),
                    cornerRadius = CornerRadius(8.dp.toPx()),
                    blendMode = BlendMode.Multiply,
                    topLeft = Offset(0f, 4.dp.toPx())
                )
            }
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
    ) {
        // Imagen principal
        Image(
            painter = painterResource(id = R.drawable.insurance_image1),
            contentDescription = "Insurance Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Caja con el texto y la imagen a la derecha
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Texto dentro de la caja
            Text(
                text = "Aprende sobre seguros personales",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Imagen a la derecha del texto
            Image(
                painter = painterResource(id = R.drawable.ic_personal),
                contentDescription = "Info Image",
                modifier = Modifier
                    .size(30.dp)
                    .padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun InsuranceImage4() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(bottom = 16.dp)
            // Aplicamos la sombra usando elevation
            .drawBehind {
                // Dibujamos múltiples sombras con diferentes opacidades para crear efecto blur
                for (i in 1..3) {
                    drawRoundRect(
                        color = Color.Gray.copy(alpha = .1f),
                        size = size.copy(height = size.height + (i * .5).dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx()),
                        blendMode = BlendMode.Multiply,
                        topLeft = Offset(0f, (i * 2).dp.toPx())
                    )
                }
                // Sombra principal
                drawRoundRect(
                    color = Color.Gray.copy(alpha = .2f),
                    size = size.copy(height = size.height + .5.dp.toPx()),
                    cornerRadius = CornerRadius(8.dp.toPx()),
                    blendMode = BlendMode.Multiply,
                    topLeft = Offset(0f, 4.dp.toPx())
                )
            }
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
    ) {
        // Imagen principal
        Image(
            painter = painterResource(id = R.drawable.insurance_image3),
            contentDescription = "Insurance Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Caja con el texto y la imagen a la derecha
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Texto dentro de la caja
            Text(
                text = "Aprende sobre seguros empresariales",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Imagen a la derecha del texto
            Image(
                painter = painterResource(id = R.drawable.ic_empresarial),
                contentDescription = "Info Image",
                modifier = Modifier
                    .size(30.dp)
                    .padding(start = 8.dp)
            )
        }
    }
}


