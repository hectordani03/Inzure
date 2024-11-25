// AgentsView.kt
package io.inzure.app.ui.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import io.inzure.app.data.model.Agent
import io.inzure.app.viewmodel.AgentViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
class AgentsView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgentsListView()
        }
    }
}

@Composable
fun AgentsListView() {
    val agentsViewModel: AgentViewModel = viewModel()
    val agents by agentsViewModel.agents.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var agentToEdit by remember { mutableStateOf<Agent?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf<Agent?>(null) }

    // Obtener agentes al iniciar la vista
    LaunchedEffect(Unit) {
        agentsViewModel.getAgents()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Agent")
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
                    text = "Agents List",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if (agents.isEmpty()) {
                    Text(
                        text = "No agents found.",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                } else {
                    LazyColumn {
                        items(agents) { agent ->
                            AgentCard(
                                agent = agent,
                                onEdit = { agentToEdit = agent },
                                onDelete = { showDeleteConfirmation = agent }
                            )
                        }
                    }
                }
            }
        }
    )

    // Diálogo para agregar agente
    if (showDialog) {
        AddAgentDialog(
            onDismiss = { showDialog = false },
            onSave = { newAgent ->
                agentsViewModel.addAgent(newAgent)
                showDialog = false
            }
        )
    }

    // Diálogo para editar agente
    agentToEdit?.let { agent ->
        EditAgentDialog(
            agent = agent,
            onDismiss = { agentToEdit = null },
            onSave = { updatedAgent ->
                agentsViewModel.updateAgent(updatedAgent)
                agentToEdit = null
            }
        )
    }

    // Diálogo de confirmación para eliminar agente
    showDeleteConfirmation?.let { agent ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text(text = "Delete Agent") },
            text = { Text(text = "Are you sure you want to delete this agent?") },
            confirmButton = {
                TextButton(onClick = {
                    agentsViewModel.deleteAgent(agent)
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
fun AgentCard(agent: Agent, onEdit: () -> Unit, onDelete: () -> Unit) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Name: ${agent.name}", fontWeight = FontWeight.Bold)
                Text(text = "Email: ${agent.email}")
                Text(text = "Phone: ${agent.phone}")
                Text(text = "Company: ${agent.company}")
                Text(text = "License Number: ${agent.licenseNumber}")
            }
            IconButton(onClick = onEdit) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Agent")
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Agent")
            }
        }
    }
}

@Composable
fun AddAgentDialog(onDismiss: () -> Unit, onSave: (Agent) -> Unit) {
    var name by remember { mutableStateOf(TextFieldValue()) }
    var email by remember { mutableStateOf(TextFieldValue()) }
    var phone by remember { mutableStateOf(TextFieldValue()) }
    var company by remember { mutableStateOf(TextFieldValue()) }
    var licenseNumber by remember { mutableStateOf(TextFieldValue()) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Add Agent", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("Company") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = licenseNumber,
                    onValueChange = { licenseNumber = it },
                    label = { Text("License Number") },
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
                        if (name.text.isNotBlank() &&
                            email.text.isNotBlank() &&
                            phone.text.isNotBlank() &&
                            company.text.isNotBlank() &&
                            licenseNumber.text.isNotBlank()
                        ) {
                            val newAgent = Agent(
                                name = name.text.trim(),
                                email = email.text.trim(),
                                phone = phone.text.trim(),
                                company = company.text.trim(),
                                licenseNumber = licenseNumber.text.trim()
                            )
                            onSave(newAgent)
                        } else {
                            Toast.makeText(context, "Please fill all fields.", Toast.LENGTH_SHORT).show()
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
fun EditAgentDialog(agent: Agent, onDismiss: () -> Unit, onSave: (Agent) -> Unit) {
    var name by remember { mutableStateOf(TextFieldValue(agent.name)) }
    var email by remember { mutableStateOf(TextFieldValue(agent.email)) }
    var phone by remember { mutableStateOf(TextFieldValue(agent.phone)) }
    var company by remember { mutableStateOf(TextFieldValue(agent.company)) }
    var licenseNumber by remember { mutableStateOf(TextFieldValue(agent.licenseNumber)) }

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
                Text(text = "Edit Agent", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("Company") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = licenseNumber,
                    onValueChange = { licenseNumber = it },
                    label = { Text("License Number") },
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
                        if (name.text.isNotBlank() &&
                            email.text.isNotBlank() &&
                            phone.text.isNotBlank() &&
                            company.text.isNotBlank() &&
                            licenseNumber.text.isNotBlank()
                        ) {
                            val updatedAgent = agent.copy(
                                name = name.text.trim(),
                                email = email.text.trim(),
                                phone = phone.text.trim(),
                                company = company.text.trim(),
                                licenseNumber = licenseNumber.text.trim()
                            )
                            onSave(updatedAgent)
                        } else {
                            Toast.makeText(context, "Please fill all fields.", Toast.LENGTH_SHORT).show()
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
fun DropdownMenuFieldAgents(
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
