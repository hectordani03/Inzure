// PostsView.kt
package io.inzure.app.ui.views

import android.app.Activity
import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
        AddPostDialog(
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
        EditPostDialog(
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
