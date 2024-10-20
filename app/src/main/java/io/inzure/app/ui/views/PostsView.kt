package io.inzure.app.ui.views

import android.os.Bundle
import android.app.DatePickerDialog
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalContext
import io.inzure.app.R
import java.util.*

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
    var showDialog by remember { mutableStateOf(false) }
    var posts by remember { mutableStateOf(listOf<Post>()) }

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
            ) {
                Text(
                    text = "Insurance Posts",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                posts.forEach { post ->
                    PostCard(
                        post = post,
                        onEdit = { /* Handle edit */ },
                        onDelete = {
                            posts = posts.filter { it != post }
                        }
                    )
                }
            }
        }
    )

    if (showDialog) {
        AddPostDialog(
            onDismiss = { showDialog = false },
            onSave = { newPost ->
                posts = posts + newPost
                showDialog = false
            }
        )
    }
}

@Composable
fun PostCard(post: Post, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
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
            // Imagen del post
            Image(
                bitmap = ImageBitmap.imageResource(id = R.drawable.ic_auto), // Imagen por defecto
                contentDescription = "Post Image",
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 8.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Title: ${post.title}", fontWeight = FontWeight.Bold)
                Text(text = "Price: ${post.price}")
                Text(text = "Start Date: ${post.startDate}")
                Text(text = "End Date: ${post.endDate}")
                Text(text = "Type: ${post.type}")
                Text(text = "Description: ${post.description}", maxLines = 3)
            }
            IconButton(onClick = onEdit) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Post")
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Post")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPostDialog(onDismiss: () -> Unit, onSave: (Post) -> Unit) {
    var title by remember { mutableStateOf(TextFieldValue()) }
    var description by remember { mutableStateOf(TextFieldValue()) }
    var type by remember { mutableStateOf("Personal") }
    var expanded by remember { mutableStateOf(false) }
    var price by remember { mutableStateOf(TextFieldValue()) }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var imageSelected by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Create New Post", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // Botón para seleccionar imagen (Placeholder)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(if (imageSelected) Color.Green else Color.Gray)
                        .clickable {
                            // Implementar lógica de selección de imagen
                            imageSelected = true // Cambiar a verdadero cuando se seleccione una imagen
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (imageSelected) "Image Selected" else "Tap to Select Image",
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titulo") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripcion del seguro") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Dropdown para tipo de seguro
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        label = { Text("Tipo del seguro") },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .clickable { expanded = true }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("Personal", "Automovilístico", "Empresarial").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    type = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Fecha de inicio") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            showDatePicker(context, calendar) { selectedDate ->
                                startDate = selectedDate
                            }
                        }) {
                            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Select Start Date")
                        }
                    }
                )
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("Fecha de fin") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            showDatePicker(context, calendar) { selectedDate ->
                                endDate = selectedDate
                            }
                        }) {
                            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Select End Date")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (title.text.isNotBlank() && description.text.isNotBlank() && price.text.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank()) {
                            val newPost = Post(
                                title = title.text,
                                description = description.text,
                                type = type,
                                price = price.text,
                                startDate = startDate,
                                endDate = endDate
                            )
                            onSave(newPost)
                        }
                    }) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

// Función auxiliar para mostrar el DatePickerDialog
private fun showDatePicker(context: Context, calendar: Calendar, onDateSelected: (String) -> Unit) {
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            onDateSelected(selectedDate)
        },
        year,
        month,
        day
    ).show()
}

data class Post(
    val title: String,
    val description: String,
    val type: String,
    val price: String,
    val startDate: String,
    val endDate: String
)

