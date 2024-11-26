// MyPostsScreen.kt
package io.inzure.app.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import io.inzure.app.R
import io.inzure.app.data.model.Post
import io.inzure.app.viewmodel.PostViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostsScreen(drawerState: DrawerState) {
    val postViewModel: PostViewModel = viewModel()
    val posts by postViewModel.posts.collectAsState()
    var showAddPostDialog by remember { mutableStateOf(false) }
    var postToEdit by remember { mutableStateOf<Post?>(null) }
    var postToDelete by remember { mutableStateOf<Post?>(null) }

    // CoroutineScope para manejar la apertura del drawer desde la barra de navegación
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Posts", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menú",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF072A4A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddPostDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Post")
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF2F2F2))
            ) {
                if (posts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay posts disponibles.",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(posts) { post ->
                            InsuranceCard(
                                title = post.title,
                                description = post.content,
                                imageResId = R.drawable.ic_qualitas, // Asegúrate de tener este recurso
                                onEdit = { postToEdit = post },
                                onDelete = { postToDelete = post }
                            )
                        }
                    }
                }
            }
        }
    )

    // Diálogo para agregar un nuevo post
    if (showAddPostDialog) {
        AddNewPostDialog(
            onDismiss = { showAddPostDialog = false },
            onSave = { newPost ->
                postViewModel.addPostManually(newPost) // Llamar a una función que actualice localmente
                showAddPostDialog = false
            }
        )
    }

    // Diálogo para editar un post existente
    postToEdit?.let { post ->
        EditExistingPostDialog(
            post = post,
            onDismiss = { postToEdit = null },
            onSave = { updatedPost ->
                postViewModel.updatePostManually(updatedPost) // Actualizar manualmente
                postToEdit = null
            }
        )
    }

    // Confirmación para eliminar un post
    postToDelete?.let { post ->
        AlertDialog(
            onDismissRequest = { postToDelete = null },
            title = { Text(text = "Eliminar Post") },
            text = { Text(text = "¿Estás seguro de que deseas eliminar este post?") },
            confirmButton = {
                TextButton(onClick = {
                    postViewModel.deletePostManually(post) // Eliminar manualmente
                    postToDelete = null
                }) {
                    Text("Sí", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { postToDelete = null }) {
                    Text("No")
                }
            }
        )
    }
}


@Composable
fun EditExistingPostDialog(post: Post, onDismiss: () -> Unit, onSave: (Post) -> Unit) {
    var title by remember { mutableStateOf(post.title) }
    var content by remember { mutableStateOf(post.content) }
    var authorId by remember { mutableStateOf(post.authorId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar Post") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Contenido") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 5
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = authorId,
                    onValueChange = { authorId = it },
                    label = { Text("ID del Autor") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank() && authorId.isNotBlank()) {
                        val updatedPost = post.copy(
                            title = title,
                            content = content,
                            authorId = authorId
                            // Mantener el timestamp original
                        )
                        onSave(updatedPost)
                    }
                }
            ) {
                Text("Actualizar", color = Color(0xFF6200EE))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AddNewPostDialog(onDismiss: () -> Unit, onSave: (Post) -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var authorId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Agregar Nuevo Post") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Contenido") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 5
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = authorId,
                    onValueChange = { authorId = it },
                    label = { Text("ID del Autor") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank() && authorId.isNotBlank()) {
                        val newPost = Post(
                            title = title,
                            content = content,
                            authorId = authorId,
                            timestamp = System.currentTimeMillis()
                        )
                        onSave(newPost)
                    }
                }
            ) {
                Text("Guardar", color = Color(0xFF6200EE))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun InsuranceCard(
    title: String,
    description: String,
    imageResId: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
    ) {
        // Efecto de sombra y blur
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .drawBehind {
                    for (i in 1..3) {
                        drawRoundRect(
                            color = Color.Gray.copy(alpha = 0.1f),
                            size = size.copy(height = size.height + (i * 0.5).dp.toPx()),
                            cornerRadius = CornerRadius(8.dp.toPx()),
                            blendMode = BlendMode.Multiply,
                            topLeft = Offset(0f, (i * 2).dp.toPx())
                        )
                    }
                    // Sombra principal
                    drawRoundRect(
                        color = Color.Gray.copy(alpha = 0.2f),
                        size = size.copy(height = size.height + 0.5.dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx()),
                        blendMode = BlendMode.Multiply,
                        topLeft = Offset(0f, 4.dp.toPx())
                    )
                }
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Imagen principal
            Image(
                painter = rememberAsyncImagePainter(imageResId),
                contentDescription = "Insurance Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            )

            // Caja de información en la parte inferior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Icono en la izquierda
                    Image(
                        painter = painterResource(id = R.drawable.ic_qualitas), // Asegúrate de tener este recurso
                        contentDescription = "Insurance Logo",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Título y descripción
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = description,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    // Iconos de editar y eliminar
                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar Post",
                                tint = Color(0xFF6200EE)
                            )
                        }
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar Post",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }





    /**
     * Función para formatear la marca de tiempo en un formato legible.
     *
     * @param timestamp Marca de tiempo en milisegundos.
     * @return Cadena con la fecha y hora formateadas.
     */
    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}