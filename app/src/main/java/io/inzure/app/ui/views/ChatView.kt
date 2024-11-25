package io.inzure.app.ui.views

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Path
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.inzure.app.R
import java.util.UUID

// Clase de datos para los mensajes
data class Chat(val userName: String, val userCompany: String, val userImageRes: Int)
data class Message(val text: String, val isSentByUser: Boolean)

// Lista de mensajes de ejemplo
val sampleMessages = listOf(
    Message("Hola, Buen día, está interesado en algún seguro?", false),
    Message("Sí, me interesa el seguro automovilístico", true),
    Message("Me podría dar más información", true),
    Message("Claro, por el momento estamos manejando un seguro de cobertura total que incluye...", false),
    Message("Perfecto, ¿cuáles son los precios?", true)
)

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
    val db = FirebaseFirestore.getInstance() // Instancia de Firestore
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userIdSender = currentUser?.uid
    val userIdReceiver = "QmFVXVwNGGPZPpUQkFiPMxChTmR2"
    var displayName = ""
    if (userIdSender != null) {
        db.collection("Users").document(userIdSender).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val firstname = document.getString("firstName") ?: "Sin nombre"
                    val lastname = document.getString("lastName") ?: "Sin apellido"
                    displayName = "${firstname}" + "${lastname}"
                    Log.d("Firestore", "Nombre del usuario: $displayName")
                } else {
                    Log.w("Firestore", "No se encontró el documento para el usuario.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al obtener el nombre del usuario: $e")
            }
    } else {
        Log.w("Firestore", "El usuario no está autenticado.")
    }
    val chatId = "${userIdSender}_${userIdReceiver}" // Genera un ID único para cada chat
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
            .padding(top = 170.dp)
            .padding(horizontal = 16.dp)
    ) {
        // Encabezado del chat individual
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = chat.userImageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

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
                        val newMessage = Message(currentMessage, true)
                        messages.add(newMessage) // Agrega el mensaje a la lista local
                        currentMessage = "" // Limpia el campo de entrada

                        // Guardar mensaje en Firestore
                        val messageMap = hashMapOf(
                            "text" to newMessage.text,
                            "isSentByUser" to newMessage.isSentByUser,
                            "userId" to userIdSender, // Agregar el ID del usuario al mensaje
                            "timestamp" to System.currentTimeMillis()
                        )
                        db.collection("chats").document(chatId).collection("messages")
                            .add(messageMap)
                            .addOnSuccessListener {
                                // Mensaje guardado con éxito
                            }
                            .addOnFailureListener { e ->
                                // Error al guardar el mensaje
                                e.printStackTrace()
                            }
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_send),
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