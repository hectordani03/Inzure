package io.inzure.app.ui.views

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import io.inzure.app.data.model.Posts
import io.inzure.app.viewmodel.PostsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyPostsView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyPostsListView()
        }
    }
}

@Composable
fun MyPostsListView() {
    val postsViewModel: PostsViewModel = viewModel()
    val posts by postsViewModel.posts.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var postsToEdit by remember { mutableStateOf<Posts?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf<Posts?>(null) }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        postsViewModel.getUsersPosts()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Post")
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Cambio agregado
            ) {
                Text(
                    text = "Mis Publicaciones",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if (posts.isEmpty()) {
                    Text(
                        text = "No tienes publicaciones.",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                } else {
                    posts.map { it.post }.forEach { post ->
                        PostCard(
                            posts = post,
                            onEdit = { postsToEdit = post },
                            onDelete = { showDeleteConfirmation = post }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp)) // Línea agregada
                }
            }
        }
    )

    if (showDialog) {
        AddPostDialog(
            currentUserId = currentUserId,
            onDismiss = { showDialog = false },
            onSave = { post, imageUri ->
                postsViewModel.addPost(post, imageUri)
                showDialog = false
            },
            onSelectImage = { uri -> }
        )
    }

    postsToEdit?.let { post ->
        EditPostDialog(
            posts = post,
            onDismiss = { postsToEdit = null },
            onSave = { updatedPost, newImageUri ->
                postsViewModel.updatePost(updatedPost, newImageUri) { success ->
                    if (success) {
                        Toast.makeText(context, "Publicación actualizada exitosamente.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error al actualizar la publicación.", Toast.LENGTH_SHORT).show()
                    }
                    postsToEdit = null
                }
            }
        )
    }

    showDeleteConfirmation?.let { post ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text(text = "Eliminar Publicación") },
            text = { Text(text = "¿Estás seguro de que deseas eliminar esta publicación?") },
            confirmButton = {
                TextButton(onClick = {
                    postsViewModel.deletePost(post)
                    showDeleteConfirmation = null
                }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = null }) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
fun PostCard(posts: Posts, onEdit: () -> Unit, onDelete: () -> Unit) {
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
            posts.image?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Post Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                )
            } ?: Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.LightGray)
            ) {
                Text(
                    text = "Sin Imagen",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

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
                    // Título y descripción
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = posts.titulo,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = posts.descripcion,
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
}


@Composable
fun AddPostDialog(
    currentUserId: String,
    onDismiss: () -> Unit,
    onSave: (Posts, Uri?) -> Unit,
    onSelectImage: (Uri) -> Unit
) {
    var titulo by remember { mutableStateOf(TextFieldValue()) }
    var descripcion by remember { mutableStateOf(TextFieldValue()) }
    var tipo by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imagenUri = uri
            onSelectImage(uri)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    tipo = tipo,
                    onTipoSelected = { tipo = it }
                )
                Button(onClick = { launcher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                    Text("Seleccionar Imagen")
                }
                imagenUri?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Button(onClick = {
                        if (tipo.isNotBlank()) {
                            val newPost = Posts(
                                titulo = titulo.text,
                                descripcion = descripcion.text,
                                userId = currentUserId,
                                tipo = tipo,
                                date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                            )
                            onSave(newPost, imagenUri)
                        } else {
                            Toast.makeText(context, "Debe seleccionar un tipo.", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenu(tipo: String, onTipoSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val tipos = listOf("Autos", "Personal", "Empresarial")

    Box {
        OutlinedTextField(
            value = tipo,
            onValueChange = {},
            label = { Text("Tipo") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = true }
                )
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            tipos.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        onTipoSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun EditPostDialog(posts: Posts, onDismiss: () -> Unit, onSave: (Posts, Uri?) -> Unit) {
    var title by remember { mutableStateOf(TextFieldValue(posts.titulo)) }
    var description by remember { mutableStateOf(TextFieldValue(posts.descripcion)) }
    var newImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            newImageUri = uri
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Editar Publicación", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                // Campo para título
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Campo para descripción
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Botón para seleccionar nueva imagen
                Button(
                    onClick = {
                        launcher.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seleccionar Nueva Imagen")
                }

                // Previsualización de imagen nueva o existente
                newImageUri?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray)
                    )
                } ?: posts.image?.let { imageUrl ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray)
                    )
                }

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Button(onClick = {
                        if (title.text.isNotBlank() && description.text.isNotBlank()) {
                            val updatedPost = posts.copy(
                                titulo = title.text,
                                descripcion = description.text
                            )
                            onSave(updatedPost, newImageUri)
                        } else {
                            Toast.makeText(
                                context,
                                "El título y la descripción no pueden estar vacíos.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                        Text("Guardar Cambios")
                    }
                }
            }
        }
    }
}

