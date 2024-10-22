package io.inzure.app.ui.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
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
import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.ui.platform.LocalContext
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import io.inzure.app.viewmodel.UserViewModel
import io.inzure.app.data.model.User
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.AlertDialog

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

    LaunchedEffect(Unit) {
        userViewModel.startRealtimeUpdates()
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
                    users.forEach { user ->
                        UserCard(
                            user = user,
                            onEdit = { userToEdit = user }, // Usa el usuario correcto
                            onDelete = { showDeleteConfirmation = user } // Usa el usuario correcto
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

    // Diálogo para editar usuario
    userToEdit?.let { user ->
        EditUserDialog(
            user = user,
            onDismiss = { userToEdit = null },
            onSave = { updatedUser ->
                userViewModel.updateUser(updatedUser) // Actualiza el usuario en Firestore
                userToEdit = null
            }
        )
    }

    showDeleteConfirmation?.let { user ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text(text = "Delete User") },
            text = { Text(text = "Are you sure you want to delete this user?") },
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
fun UserCard(user: User, onEdit: () -> Unit, onDelete: () -> Unit) {
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
                Text(text = "Birth Date: ${user.birthDate}")
            }
            IconButton(onClick = onEdit) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit User")
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete User")
            }
        }
    }
}

@Composable
fun AddUserDialog(onDismiss: () -> Unit, onSave: (User) -> Unit) {
    var firstName by remember { mutableStateOf(TextFieldValue()) }
    var lastName by remember { mutableStateOf(TextFieldValue()) }
    var email by remember { mutableStateOf(TextFieldValue()) }
    var phone by remember { mutableStateOf(TextFieldValue()) }
    var role by remember { mutableStateOf("Editor") }
    var birthDate by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

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

                // Menú desplegable para el rol
                DropdownMenuField(
                    label = "Rol",
                    options = listOf("Editor", "Admin"),
                    selectedOption = role,
                    onOptionSelected = { role = it }
                )

                // Selector de fecha de nacimiento
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { birthDate = it },
                    label = { Text("Fecha de Nacimiento") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            showDatePicker(context, calendar) { selectedDate ->
                                birthDate = selectedDate
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Select Date"
                            )
                        }
                    }
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
                        if (firstName.text.isNotBlank() && lastName.text.isNotBlank() && email.text.isNotBlank() && phone.text.isNotBlank() && birthDate.isNotBlank()) {
                            val newUser = User(
                                firstName = firstName.text,
                                lastName = lastName.text,
                                email = email.text,
                                phone = phone.text,
                                role = role,
                                birthDate = birthDate
                            )
                            onSave(newUser) // Llama a la función para guardar el usuario
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
fun EditUserDialog(user: User,onDismiss: () -> Unit,onSave: (User) -> Unit) {
    var firstName by remember { mutableStateOf(TextFieldValue(user.firstName)) }
    var lastName by remember { mutableStateOf(TextFieldValue(user.lastName)) }
    var email by remember { mutableStateOf(TextFieldValue(user.email)) }
    var phone by remember { mutableStateOf(TextFieldValue(user.phone)) }
    var role by remember { mutableStateOf(user.role) }
    var birthDate by remember { mutableStateOf(user.birthDate) }

    val context = LocalContext.current

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
                DropdownMenuField(
                    label = "Rol",
                    options = listOf("Editor", "Admin"),
                    selectedOption = role,
                    onOptionSelected = { role = it }
                )

                // Selector de fecha de nacimiento
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { },  // This field is read-only
                    label = { Text("Fecha de Nacimiento") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            showDatePicker(context, Calendar.getInstance()) { selectedDate ->
                                birthDate = selectedDate
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Select Date"
                            )
                        }
                    }
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
                        if (firstName.text.isNotBlank() && lastName.text.isNotBlank() && email.text.isNotBlank() && phone.text.isNotBlank() && birthDate.isNotBlank()) {
                            val updatedUser = User(
                                id = user.id,
                                firstName = firstName.text,
                                lastName = lastName.text,
                                email = email.text,
                                phone = phone.text,
                                role = role,
                                birthDate = birthDate
                            )
                            onSave(updatedUser) // Llama al ViewModel para actualizar el usuario
                        }
                    }) {
                        Text("Update")
                    }
                }
            }
        }
    }
}


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuField(label: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
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

