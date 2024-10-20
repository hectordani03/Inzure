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
    var showDialog by remember { mutableStateOf(false) }
    var insurances by remember { mutableStateOf(listOf<Insurance>()) }

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
                        onEdit = { /* Handle edit */ },
                        onDelete = {
                            insurances = insurances.filter { it != insurance }
                        }
                    )
                }
            }
        }
    )

    if (showDialog) {
        AddInsuranceDialog(
            onDismiss = { showDialog = false },
            onSave = { newInsurance ->
                insurances = insurances + newInsurance
                showDialog = false
            }
        )
    }
}

@Composable
fun InsuranceCard(insurance: Insurance, onEdit: () -> Unit, onDelete: () -> Unit) {
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
            // Imagen del seguro
            Image(
                bitmap = ImageBitmap.imageResource(id = R.drawable.ic_auto), // Imagen por defecto
                contentDescription = "Insurance Logo",
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 8.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Name: ${insurance.name}", fontWeight = FontWeight.Bold)
                Text(text = "Type: ${insurance.type}")
                Text(text = "Price: ${insurance.price}")
                Text(text = "Start Date: ${insurance.startDate}")
                Text(text = "End Date: ${insurance.endDate}")
            }
            IconButton(onClick = onEdit) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Insurance")
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Insurance")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInsuranceDialog(onDismiss: () -> Unit, onSave: (Insurance) -> Unit) {
    var name by remember { mutableStateOf(TextFieldValue()) }
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
                Text(text = "Add Insurance", fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del seguro") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Usar ExposedDropdownMenuBox para el dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        label = { Text("Tipo de seguro") },
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
                    label = { Text("Fecha de Inicio de Vigencia") },
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
                    label = { Text("Fecha de Expiración") },
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
                        if (name.text.isNotBlank() && price.text.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank()) {
                            val newInsurance = Insurance(
                                name = name.text,
                                type = type,
                                price = price.text,
                                startDate = startDate,
                                endDate = endDate
                            )
                            onSave(newInsurance)
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

data class Insurance(
    val name: String,
    val type: String,
    val price: String,
    val startDate: String,
    val endDate: String
)
