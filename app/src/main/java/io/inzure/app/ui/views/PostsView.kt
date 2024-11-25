// PostsView.kt
package io.inzure.app.ui.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import io.inzure.app.viewmodel.PostViewModel
import io.inzure.app.data.model.Post
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.window.Dialog
import java.util.Date

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
    val postViewModel: PostViewModel = viewModel()
    val posts by postViewModel.posts.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var postToEdit by remember { mutableStateOf<Post?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf<Post?>(null) }

    LaunchedEffect(Unit) {
        postViewModel.getPosts()
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
            ) {
                Text(
                    text = "Posts List",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if (posts.isEmpty()) {
                    Text(
                        text = "No posts found.",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                } else {
                    posts.forEach { post ->
                        PostCard(
                            post = post,
                            onEdit = { postToEdit = post },
                            onDelete = { showDeleteConfirmation = post }
                        )
                    }
                }
            }
        }
    )

    // Diálogo para agregar post
    if (showDialog) {
        AddPostDialog(
            onDismiss = { showDialog = false },
            onSave = { newPost ->
                postViewModel.addPost(newPost)
                showDialog = false
            }
        )
    }

    // Diálogo para editar post
    postToEdit?.let { post ->
        EditPostDialog(
            post = post,
            onDismiss = { postToEdit = null },
            onSave = { updatedPost ->
                postViewModel.updatePost(updatedPost)
                postToEdit = null
            }
        )
    }

    // Confirmación para eliminar post
    showDeleteConfirmation?.let { post ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text(text = "Delete Post") },
            text = { Text(text = "Are you sure you want to delete this post?") },
            confirmButton = {
                TextButton(onClick = {
                    postViewModel.deletePost(post)
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
fun PostCard(post: Post, onEdit: () -> Unit, onDelete: () -> Unit) {
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
                Text(text = "Title: ${post.title}", fontWeight = FontWeight.Bold)
                Text(text = "Content: ${post.content}")
                Text(text = "Author ID: ${post.authorId}")
                Text(text = "Timestamp: ${Date(post.timestamp)}")
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

@Composable
fun AddPostDialog(onDismiss: () -> Unit, onSave: (Post) -> Unit) {
    var title by remember { mutableStateOf(TextFieldValue()) }
    var content by remember { mutableStateOf(TextFieldValue()) }
    var authorId by remember { mutableStateOf(TextFieldValue()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Campos de texto
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = authorId,
                    onValueChange = { authorId = it },
                    label = { Text("Author ID") },
                    modifier = Modifier.fillMaxWidth()
                )

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
                        if (title.text.isNotBlank() && content.text.isNotBlank() && authorId.text.isNotBlank()) {
                            val newPost = Post(
                                title = title.text,
                                content = content.text,
                                authorId = authorId.text,
                                timestamp = System.currentTimeMillis()
                            )
                            onSave(newPost)
                        }
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun EditPostDialog(post: Post, onDismiss: () -> Unit, onSave: (Post) -> Unit) {
    var title by remember { mutableStateOf(TextFieldValue(post.title)) }
    var content by remember { mutableStateOf(TextFieldValue(post.content)) }
    var authorId by remember { mutableStateOf(TextFieldValue(post.authorId)) }

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
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = authorId,
                    onValueChange = { authorId = it },
                    label = { Text("Author ID") },
                    modifier = Modifier.fillMaxWidth()
                )

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
                        if (title.text.isNotBlank() && content.text.isNotBlank() && authorId.text.isNotBlank()) {
                            val updatedPost = post.copy(
                                title = title.text,
                                content = content.text,
                                authorId = authorId.text,
                                timestamp = System.currentTimeMillis() // Puedes mantener el timestamp original si lo prefieres
                            )
                            onSave(updatedPost)
                        }
                    }) {
                        Text("Update")
                    }
                }
            }
        }
    }
}
