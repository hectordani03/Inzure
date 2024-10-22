package io.inzure.app.ui.views

import android.os.Bundle
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import io.inzure.app.R
import java.util.*
import io.inzure.app.data.model.Insurance
import io.inzure.app.viewmodel.InsuranceViewModel
import coil.compose.rememberAsyncImagePainter

class InsuranceView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InsuranceListView()
        }
    }
}

@Composable
fun InsuranceListView() {
    val insuranceViewModel: InsuranceViewModel = viewModel()
    val insurances by insuranceViewModel.insurances.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var insuranceToEdit by remember { mutableStateOf<Insurance?>(null) }

    LaunchedEffect(Unit) {
        insuranceViewModel.startRealtimeUpdates() // Escucha en tiempo real para todos los seguros
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Insurance")
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
                    text = "Insurance List",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                insurances.forEach { insurance ->
                    InsuranceCard(
                        insurance = insurance,
                        onEdit = { insuranceToEdit = it },
                        onDelete = { insuranceViewModel.deleteInsurance(insurance.type, insurance.id) }
                    )
                }
            }
        }
    )

    if (showDialog || insuranceToEdit != null) {
        AddEditInsuranceDialog(
            insurance = insuranceToEdit,
            onDismiss = {
                showDialog = false
                insuranceToEdit = null
            },
            onSave = { newInsurance, imageUri ->
                if (insuranceToEdit != null) {
                    insuranceViewModel.updateInsurance(newInsurance.type, newInsurance, imageUri)
                } else {
                    insuranceViewModel.addInsurance(newInsurance.type, newInsurance, imageUri)
                }
                showDialog = false
                insuranceToEdit = null
            }
        )
    }
}

@Composable
fun InsuranceCard(
    insurance: Insurance,
    onEdit: (Insurance) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Name: ${insurance.name}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Description: ${insurance.description}")
            Text(text = "Type: ${insurance.type}")
            Text(text = "Price: ${insurance.price}")

            if (insurance.image.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(model = insurance.image),
                    contentDescription = "Insurance image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }
            Row(horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { onEdit(insurance) }) {
                    Text("Edit")
                }
                TextButton(onClick = { onDelete() }) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun AddEditInsuranceDialog(
    insurance: Insurance? = null,
    onDismiss: () -> Unit,
    onSave: (Insurance, Uri?) -> Unit
) {
    var name by remember { mutableStateOf(TextFieldValue(insurance?.name ?: "")) }
    var type by remember { mutableStateOf(insurance?.type ?: "Personal") }
    var price by remember { mutableStateOf(TextFieldValue(insurance?.price?.toString() ?: "")) }
    var description by remember { mutableStateOf(TextFieldValue(insurance?.description ?: "")) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    // Inicializa el ActivityResultLauncher para seleccionar imágenes
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri // Asigna la URI seleccionada a la variable de estado
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (insurance != null) "Edit Insurance" else "Add Insurance",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Box para seleccionar la imagen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.Gray)
                        .clickable {
                            launcher.launch("image/*") // Lanza el selector de imágenes
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUri),
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("Tap to Select Image", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Otros campos de entrada (nombre, tipo, precio, descripción)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del seguro") },
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenuField(
                    label = "Tipo de seguro",
                    options = listOf("Business", "Car", "Personal"),
                    selectedOption = type,
                    onOptionSelected = { type = it }
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (name.text.isNotBlank() && price.text.isNotBlank() && description.text.isNotBlank()) {
                            val newInsurance = Insurance(
                                id = insurance?.id ?: "",
                                name = name.text,
                                type = type,
                                price = price.text.toDoubleOrNull() ?: 0.0,
                                description = description.text,
                                image = insurance?.image ?: "",
                                active = true
                            )
                            onSave(newInsurance, imageUri)
                        }
                    }) {
                        Text(if (insurance != null) "Actualizar" else "Guardar")
                    }
                }
            }
        }
    }
}


@Composable
fun InsuranceDropdownMenuField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}



