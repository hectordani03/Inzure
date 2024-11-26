package io.inzure.app.ui.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalContext
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import io.inzure.app.viewmodel.UserViewModel
import io.inzure.app.data.model.User
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextField
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import io.inzure.app.viewmodel.ValidationUtils
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation

class UsersView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UsersListView()
        }
    }
}

@Composable
fun UsersListView() {
    val userViewModel: UserViewModel = viewModel()
    val users by userViewModel.users.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var userToEdit by remember { mutableStateOf<User?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf<User?>(null) }

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: return
    var role by remember { mutableStateOf("") }

    firestore.collection("Users")
        .document(userId)
        .get()
        .addOnSuccessListener { userDoc ->
            val userData = userDoc.toObject(User::class.java)
            if (userData != null) {
                role = userData.role
            }
        }

// Filtrar los usuarios según el rol
    val filteredUsers = remember(users, role) {
        users.filter { user ->
            user.id != userId && // Excluir al usuario logueado
                    when (role) {
                        "admin" -> user.role in listOf("admin", "editor") // Admin ve solo admins y editores
                        "editor" -> user.role == "editor" // Si es editor, muestra solo editores
                        else -> false // Si no tiene un rol conocido, no mostrar
                    }
        }
    }
    LaunchedEffect(Unit) {
        userViewModel.getUsers()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add User")
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
                    text = "Users List",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if (users.isEmpty()) {
                    Text(
                        text = "No users found.",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                } else {
                    filteredUsers.forEach { user ->
                        UserCard(
                            user = user,
                            onEdit = { userToEdit = user },
                            onDelete = {
                                if (role == "admin") { // Solo permitir eliminar si el rol es admin
                                    showDeleteConfirmation = user
                                }
                            },
                            currentUserRole = role
                        )
                    }
                }
            }
        }
    )

    // Diálogo para agregar usuario
    if (showDialog) {
        AddUserDialog(
            onDismiss = { showDialog = false },
            onSave = { newUser ->
                userViewModel.addUser(newUser)
                showDialog = false
            }
        )
    }

    userToEdit?.let { user ->
        EditUserDialog(
            user = user,
            onDismiss = { userToEdit = null },
            onSave = { updatedUser ->
                userViewModel.updateUser(updatedUser)
                userToEdit = null
            }
        )
    }

    showDeleteConfirmation?.let { user ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text(text = "Eliminar Usuario") },
            text = { Text(text = "¿Estás seguro que quieres eliminar este usuario?") },
            confirmButton = {
                TextButton(onClick = {
                    userViewModel.deleteUser(user) // Llama a la función de eliminación
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
fun UserCard(user: User, onEdit: () -> Unit, onDelete: () -> Unit, currentUserRole: String) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Elevación ajustada para Material 3
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Name: ${user.firstName}", fontWeight = FontWeight.Bold)
                Text(text = "Last Name: ${user.lastName}")
                Text(text = "Email: ${user.email}")
                Text(text = "Phone: ${user.phone}")
                Text(text = "Role: ${user.role}")
            }
            IconButton(onClick = onEdit) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit User")
            }
            if (currentUserRole == "admin") {
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete User")
                }
            }
        }
    }
}

@Composable
fun AddUserDialog(onDismiss: () -> Unit, onSave: (User) -> Unit) {
    val userViewModel: UserViewModel = viewModel()
    var firstName by remember { mutableStateOf(TextFieldValue()) }
    var lastName by remember { mutableStateOf(TextFieldValue()) }
    var email by remember { mutableStateOf(TextFieldValue()) }
    var phone by remember { mutableStateOf(TextFieldValue()) }
    var role by remember { mutableStateOf("editor") }
    var password by remember { mutableStateOf(TextFieldValue()) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue()) }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    val coroutineScope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()

    var showPasswordDialog by remember { mutableStateOf(false) }
    var enteredPassword by remember { mutableStateOf("") }

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
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Apellidos") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Número de Celular") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Contraseña") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Menú desplegable para el rol
                DropdownMenuField(
                    label = "Rol",
                    options = listOf("editor", "admin"),
                    selectedOption = role,
                    onOptionSelected = { role = it }
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
                        if (firstName.text.isNotBlank() && lastName.text.isNotBlank() && email.text.isNotBlank() && phone.text.isNotBlank()
                            && password.text.isNotBlank() && confirmPassword.text.isNotBlank()) {
                            showPasswordDialog = true // Mostrar el diálogo de reautenticación

                        } else {
                            Log.e("Validation", "Todos los campos deben estar completos.")
                            // Mostrar mensaje de error por campos vacíos
                        }
                    }) {
                        Text("Save")
                    }


                }
            }
        }
    }

// Mostrar el diálogo de reautenticación
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Reautenticación Requerida") },
            text = {
                Column {
                    Text("Por favor, ingresa tu contraseña actual para continuar:")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = enteredPassword,
                        onValueChange = { enteredPassword = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (enteredPassword.isNotEmpty()) {
                        auth.signInWithEmailAndPassword(auth.currentUser!!.email!!, enteredPassword)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    showPasswordDialog = false // Cerrar el diálogo

                                    // Continuar con la lógica de validación y creación del usuario
                                    coroutineScope.launch {
                                        val isEmailValid = ValidationUtils.isEmailUnique(email.text, currentUserId = "", firestore)
                                        val isPhoneValid = ValidationUtils.isPhoneUnique(phone.text, currentUserId = "", firestore)

                                        if (password != confirmPassword) {
                                            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()

                                        } else if (!isEmailValid) {
                                            Toast.makeText(context, "El email ya está registrado o es inválido.", Toast.LENGTH_SHORT).show()

                                        } else if (!isPhoneValid) {
                                            Toast.makeText(context, "El número telefónico ya está registrado o es inválido.", Toast.LENGTH_SHORT).show()

                                        } else {
                                            auth.createUserWithEmailAndPassword(email.text, password.text)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        // Usuario creado exitosamente en Authentication
                                                        val firebaseUser = task.result?.user

                                                        if (firebaseUser != null) {
                                                            // Enviar correo de verificación
                                                            firebaseUser.sendEmailVerification().addOnCompleteListener { emailTask ->
                                                                if (emailTask.isSuccessful) {
                                                                    Log.d("FirebaseAuth", "Correo de verificación enviado.")
                                                                } else {
                                                                    Log.e("FirebaseAuth", "Error al enviar correo de verificación: ${emailTask.exception?.message}")
                                                                }
                                                            }

                                                            // Guardar los datos adicionales del usuario en Firestore
                                                            val newUser = User(
                                                                id = firebaseUser.uid,
                                                                firstName = firstName.text,
                                                                lastName = lastName.text,
                                                                email = email.text,
                                                                phone = phone.text,
                                                                role = role
                                                            )
                                                            userViewModel.addUser(newUser)

                                                            // Cerrar sesión del usuario recién creado
                                                            auth.signOut()

                                                            // Reautenticar al administrador con las credenciales previamente ingresadas
                                                            auth.signInWithEmailAndPassword(auth.currentUser!!.email!!, enteredPassword)
                                                                .addOnCompleteListener { adminTask ->
                                                                    if (adminTask.isSuccessful) {
                                                                        Log.d("FirebaseAuth", "Sesión del administrador restaurada correctamente.")
                                                                    } else {
                                                                        Log.e("FirebaseAuth", "Error al restaurar sesión del administrador: ${adminTask.exception?.message}")
                                                                    }
                                                                }
                                                        }
                                                    } else {
                                                        Log.e("FirebaseAuth", "Error al crear usuario: ${task.exception?.message}")
                                                    }
                                                }
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Contraseña incorrecta.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "La contraseña no puede estar vacía.", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Confirmar")
                }

            },
            dismissButton = {
                TextButton(onClick = {
                    showPasswordDialog = false // Cierra el diálogo sin realizar la acción
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

}


@Composable
fun EditUserDialog(user: User, onDismiss: () -> Unit, onSave: (User) -> Unit) {
    var firstName by remember { mutableStateOf(TextFieldValue(user.firstName)) }
    var lastName by remember { mutableStateOf(TextFieldValue(user.lastName)) }
    var email by remember { mutableStateOf(TextFieldValue(user.email)) }
    var phone by remember { mutableStateOf(TextFieldValue(user.phone)) }
    var role by remember { mutableStateOf(user.role) }


    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Edit User", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = user.firstName,
                    onValueChange = {}, // No hacemos nada aquí porque es de solo lectura
                    enabled = false, // Hace que el campo sea readonly
                    label = { Text("Nombre") }
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = {}, // No hacemos nada aquí porque es de solo lectura
                    enabled = false, // Hace que el campo sea readonly
                    label = { Text("Apellidos") }
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = {}, // No hacemos nada aquí porque es de solo lectura
                    enabled = false, // Hace que el campo sea readonly
                    label = { Text("Email") }
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = {}, // No hacemos nada aquí porque es de solo lectura
                    enabled = false, // Hace que el campo sea readonly
                    label = { Text("Número de Celular") }
                )
                DropdownMenuField(
                    label = "Rol",
                    options = listOf("editor", "admin"),
                    selectedOption = role,
                    onOptionSelected = { role = it }
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
                        if (firstName.text.isNotBlank() && lastName.text.isNotBlank() && email.text.isNotBlank() && phone.text.isNotBlank()) {
                            val updatedUser = User(
                                id = user.id,
                                firstName = firstName.text,
                                lastName = lastName.text,
                                email = email.text,
                                phone = phone.text,
                                role = role,
                            )
                            onSave(updatedUser)
                        }
                    }) {
                        Text("Update")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

