package io.inzure.app.ui.views

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.inzure.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear un estado para manejar la lista de chats
        val chats = mutableListOf<Chat>()

        // Ejecutar la carga de datos
        CoroutineScope(Dispatchers.IO).launch {
            val fetchedChats = fetchChats() // Llamar a Firestore para obtener los chats
            chats.addAll(fetchedChats)

            // Actualizar la vista principal con los datos cargados
            runOnUiThread {
                setContent {
                    ChatListView(
                        chats = chats, // Pasar los datos cargados
                        onClose = { finish() } // Terminar la actividad al cerrar
                    )
                }
            }
        }
    }
}

// Clase de datos para los mensajes
data class Chat(
    val uid: String, // UID del destinatario
    val userName: String,
    val userCompany: String,
    val userImageUrl: String // URL de la imagen
)

data class Message(
    val text: String = "",
    val isSentByUser: Boolean = false,
    val timestamp: Long = 0L // Agregar timestamp
)

suspend fun fetchChats(): List<Chat> {
    val db = FirebaseFirestore.getInstance()
    val suggestedChats = mutableListOf<Chat>()

    try {
        // Obtener usuarios con rol "insurer"
        val querySnapshot = db.collection("Users")
            .whereEqualTo("role", "insurer")
            .get()
            .await()

        // Mapear datos al modelo Chat
        for (document in querySnapshot.documents) {
            val uid = document.id // Obtener el UID del documento
            val firstName = document.getString("firstName") ?: "Nombre"
            val lastName = document.getString("lastName") ?: "Apellido"
            val companyName = document.getString("companyName") ?: "Sin compañía"
            val imageUrl = document.getString("image") ?: R.drawable.ic_profile_default

            // Asigna los datos al modelo Chat (incluye el UID si lo necesitas)
            suggestedChats.add(
                Chat(
                    uid = uid,
                    userName = "$firstName $lastName",
                    userCompany = companyName,
                    userImageUrl = imageUrl.toString()
                )
            )

            // Opcional: Si necesitas manejar el UID adicionalmente
            Log.d("Firestore", "Usuario encontrado: $uid, $firstName $lastName")
        }
    } catch (e: Exception) {
        // Manejar errores (ej. problemas de red o permisos)
        e.printStackTrace()
    }

    return suggestedChats
}

@Composable
fun ChatListView(chats: List<Chat>, onClose: () -> Unit) {
    var isExploringChats by remember { mutableStateOf(false) }
    var selectedChat by remember { mutableStateOf<Chat?>(null) }
    var suggestedChats by remember { mutableStateOf<List<Chat>>(emptyList()) } // Estado para los chats sugeridos
    var isLoading by remember { mutableStateOf(false) } // Estado de carga

    when {
        selectedChat != null -> {
            // Mostrar IndividualChatView cuando hay un chat seleccionado
            IndividualChatView(
                chat = selectedChat!!,
                onClose = { selectedChat = null }
            )
        }
        isExploringChats -> {
            if (isLoading) {
                // Mostrar un indicador de carga mientras se obtienen los datos
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                // Mostrar ExploreChatsView con los datos sugeridos
                ExploreChatsView(
                    chats = suggestedChats,
                    onClose = { isExploringChats = false },
                    onChatSelected = { chat ->
                        selectedChat = chat
                    }
                )
            }
        }
        else -> {
            // Mostrar la lista principal de chats
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color(0xFF072A4A))
                    .padding(top = (LocalConfiguration.current.screenHeightDp * 0.22).dp)
            ) {
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

                        IconButton(onClick = {
                            isExploringChats = true
                            isLoading = true // Activar el estado de carga

                            // Llamar a initializeExploreChatsView
                            CoroutineScope(Dispatchers.Main).launch {
                                suggestedChats = fetchChats() // Obtener los chats
                                isLoading = false // Desactivar el estado de carga
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_new_chat),
                                contentDescription = "Buscar más chats",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
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
                            userImageUrl = chat.userImageUrl,
                            onClick = { selectedChat = chat }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExploreChatsView(chats: List<Chat>, onClose: () -> Unit, onChatSelected: (Chat) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color(0xFF072A4A))
            .padding(top = (LocalConfiguration.current.screenHeightDp * 0.22).dp) // Mover la vista hacia abajo
    ) {
        // Encabezado del explorador de chats
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
                    text = "Explorar Más",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onClose) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Cerrar exploración",
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
                    userImageUrl = chat.userImageUrl,
                    onClick = { onChatSelected(chat) } // Seleccionar el chat y abrir conversación
                )
            }
        }
    }
}



@Composable
fun ChatItem(
    userName: String,
    userCompany: String,
    userImageUrl: String,
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
        // Cargar imagen dinámica con Coil
        AsyncImage(
            model = userImageUrl, // URL de la imagen
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.ic_profile_default), // Placeholder
            error = painterResource(id = R.drawable.ic_profile_default), // Imagen en caso de error
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
    val userIdSender = currentUser?.uid // ID del usuario que envía el mensaje
    val userIdReceiver = chat.uid // ID del usuario al que se envía el mensaje

    // Verifica si el usuario está autenticado
    if (userIdSender == null) {
        Log.w("Firestore", "El usuario no está autenticado.")
        return
    }

    val chatId = generateChatId(userIdSender, userIdReceiver) // ID único para el chat
    val listState = rememberLazyListState()
    var currentMessage by remember { mutableStateOf("") } // Estado para el mensaje actual
    val messages = remember { mutableStateListOf<Message>() } // Estado para los mensajes cargados

    LaunchedEffect(Unit) {
        try {
            val querySnapshot = db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp") // Ordenar los mensajes por timestamp desde Firestore
                .get()
                .await()

            val loadedMessages = querySnapshot.documents.map { document ->
                val senderId = document.getString("senderId") ?: ""
                val receiverId = document.getString("receiverId") ?: ""
                val timestamp = document.getLong("timestamp") ?: 0L
                Message(
                    text = document.getString("text") ?: "",
                    isSentByUser = senderId == userIdSender, // Verifica si el mensaje fue enviado por el usuario actual
                    timestamp = timestamp // Cargar el timestamp
                )
            }

            messages.clear()
            messages.addAll(loadedMessages)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // Desplazar automáticamente al último mensaje cuando se carga un mensaje nuevo
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
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
            AsyncImage(
                model = chat.userImageUrl, // URL de la imagen
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.ic_profile_default), // Placeholder
                error = painterResource(id = R.drawable.ic_profile_default), // Imagen en caso de error
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
                    ChatBubble(message, isSentByUser = message.isSentByUser)
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
                onValueChange = { newValue -> currentMessage = newValue },
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
                            "senderId" to userIdSender, // ID del remitente
                            "receiverId" to userIdReceiver, // ID del receptor
                            "timestamp" to System.currentTimeMillis()
                        )
                        db.collection("chats").document(chatId).collection("messages")
                            .add(messageMap)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Mensaje enviado correctamente.")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error al enviar el mensaje: $e")
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


// Genera un ID único para el chat usando los IDs de los participantes
fun generateChatId(userId1: String, userId2: String): String {
    return if (userId1 < userId2) {
        "${userId1}_$userId2"
    } else {
        "${userId2}_$userId1"
    }
}


@Composable
fun ChatBubble(message: Message, isSentByUser: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isSentByUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isSentByUser) Alignment.End else Alignment.Start,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            // Globo de texto
            Box(
                modifier = Modifier
                    .background(
                        if (isSentByUser) Color(0xFF007AFF) else Color(0xFF04305A),
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomEnd = if (isSentByUser) 0.dp else 12.dp,
                            bottomStart = if (isSentByUser) 12.dp else 0.dp
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

            // Timestamp
            Text(
                text = formatTimestamp(message.timestamp), // Formatear el timestamp
                color = Color.LightGray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()) // Formato de 12 horas
    return sdf.format(java.util.Date(timestamp))
}