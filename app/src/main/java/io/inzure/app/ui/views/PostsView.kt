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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.inzure.app.viewmodel.PostsViewModel
import io.inzure.app.data.model.Posts
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostsView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PostsListView()
        }
    }
}

@Composable
fun PostsListView() {
    val postsViewModel: PostsViewModel = viewModel()
    val posts by postsViewModel.posts.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var postsToEdit by remember { mutableStateOf<Posts?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf<Posts?>(null) }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val context = LocalContext.current // Obtén el contexto aquí


    LaunchedEffect(Unit) {
        postsViewModel.getPosts()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Post")
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
                    text = "Lista de Publicaciones",
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
                        PostCard2(
                            posts = post,
                            onEdit = { postsToEdit = post },
                            onDelete = { showDeleteConfirmation = post }
                        )
                    }
                }
            }
        }
    )

    // Diálogo para agregar post
    if (showDialog) {
        AddPostDialog2(
            currentUserId = currentUserId,
            onDismiss = { showDialog = false },
            onSave = { post, imageUri ->
                postsViewModel.addPost(post, imageUri) // Llama al método correcto en el ViewModel
                showDialog = false
            },
            onSelectImage = { uri ->
                // Lógica adicional si necesitas manejar la imagen seleccionada
            }
        )
    }

    // Diálogo para editar post
    postsToEdit?.let { post ->
        EditPostDialog2(
            posts = post,
            onDismiss = { postsToEdit = null },
            onSave = { updatedPost, newImageUri ->
                postsViewModel.updatePost(updatedPost, newImageUri) { success ->
                    if (success) {
                        Toast.makeText(context, "Post updated successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to update post.", Toast.LENGTH_SHORT).show()
                    }
                    postsToEdit = null
                }
            }
        )
    }
    // Confirmación para eliminar post
// Diálogo para confirmar la eliminación del post
    showDeleteConfirmation?.let { post ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text(text = "Delete Post") },
            text = { Text(text = "Are you sure you want to delete this post?") },
            confirmButton = {
                TextButton(onClick = {
                    postsViewModel.deletePost(post)
                    showDeleteConfirmation = null
                }) {
                    Text("Yes")
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
fun PostCard2(posts: Posts, onEdit: () -> Unit, onDelete: () -> Unit) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Title: ${posts.titulo}", fontWeight = FontWeight.Bold)
                Text(text = "Description: ${posts.descripcion}")
            }
            Image(
                painter = rememberAsyncImagePainter(posts.image),
                contentDescription = "Foto de Perfil",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            IconButton(onClick = onEdit) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Post")
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Post")
            }
        }
    }
}

@Composable
fun AddPostDialog2(
    currentUserId: String,
    onDismiss: () -> Unit,
    onSave: (Posts, Uri?) -> Unit,
    onSelectImage: (Uri) -> Unit
) {
    var titulo by remember { mutableStateOf(TextFieldValue()) }
    var descripcion by remember { mutableStateOf(TextFieldValue()) }
    var tipo by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current // Obtén el contexto aquí

    // Define el launcher aquí, dentro del contexto de la función Composable
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp), // Más padding para dar espacio
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp), // Padding interno
                verticalArrangement = Arrangement.spacedBy(16.dp) // Espaciado entre elementos
            ) {
                // Input de título
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Input de descripción
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Menú desplegable para tipo
                DropdownMenu2(
                    tipo = tipo,
                    onTipoSelected = { tipo = it }
                )

                // Botón para seleccionar imagen
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seleccionar Imagen")
                }

                // Espacio y previsualización de la imagen
                imagenUri?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp) // Mayor altura para la previsualización
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
                        if (tipo.isNotBlank()) {
                            if (descripcion.text.isNotBlank() || imagenUri != null) {
                                val currentDate = SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm:ss",
                                    Locale.getDefault()
                                ).format(Date())
                                val newPost = Posts(
                                    titulo = titulo.text,
                                    descripcion = descripcion.text,
                                    userId = currentUserId,
                                    tipo = tipo,
                                    date = currentDate
                                )
                                onSave(newPost, imagenUri)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Debe agregar una descripción o una imagen.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Debe seleccionar un tipo.",
                                Toast.LENGTH_SHORT
                            ).show()
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
fun EditPostDialog2(posts: Posts, onDismiss: () -> Unit, onSave: (Posts, Uri?) -> Unit) {
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
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Edit Post", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Seleccionar una nueva imagen
                Button(onClick = {
                    launcher.launch("image/*")
                }) {
                    Text("Selecciona Nueva Imagen")
                }

                // Mostrar la nueva imagen seleccionada
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
                } ?: run {
                    // Mostrar la imagen existente, si ya tenía
                    posts.image?.let { imageUrl ->
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (title.text.isNotBlank() && description.text.isNotBlank()) {
                            // Actualizar el post
                            val updatedPost = posts.copy(
                                titulo = title.text,
                                descripcion = description.text
                            )
                            onSave(updatedPost, newImageUri)
                        } else {
                            Toast.makeText(
                                context,
                                "Title and description cannot be empty.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                        Text("Actualizar")
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenu2(tipo: String, onTipoSelected: (String) -> Unit) {
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