package io.inzure.app.ui.views

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.inzure.app.MainActivity
import io.inzure.app.R
import io.inzure.app.ui.theme.InzureTheme
import java.util.*

class RegisterView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InzureTheme {
                Scaffold { paddingValues ->
                    registerView(paddingValues, onBackClick = {
                        val intent = Intent(this@RegisterView, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, onLoginClick = {
                        val intent = Intent(this@RegisterView, LoginView::class.java)
                        startActivity(intent)
                        finish()
                    })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun registerView(paddingValues: PaddingValues, onBackClick: () -> Unit, onLoginClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var last_name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var company_name by remember { mutableStateOf("") }
    var fiscal_id by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf("") }
    var license_number by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isInsurer by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Volver",
            modifier = Modifier
                .align(Alignment.Start)
                .clickable { onBackClick() }
        )

        Image(
            painter = painterResource(id = R.drawable.register_image),
            contentDescription = "Login illustration",
            modifier = Modifier.size(250.dp),
            contentScale = ContentScale.Crop
        )

        Text(
            text = "Registrate",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 12.dp)
                .align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Campo de nombre
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_name),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de apellidos
        TextField(
            value = last_name,
            onValueChange = { last_name = it },
            label = { Text("Apellidos") },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_name),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de email
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de teléfono (solo números)
        TextField(
            value = phone,
            onValueChange = { phone = it.filter { char -> char.isDigit() } },
            label = { Text("Celular") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // DatePicker
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            }, year, month, day
        )

        TextField(
            value = selectedDate,
            onValueChange = { },
            label = { Text("Fecha de nacimiento", color = Color.DarkGray) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = Color.DarkGray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() },
            enabled = false,
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledTextColor = Color.DarkGray,
                disabledLabelColor = Color.DarkGray,
                disabledLeadingIconColor = Color.DarkGray,
                disabledPlaceholderColor = Color.DarkGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de contraseña
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            },
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de confirmar contraseña
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Contraseña") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            },
            trailingIcon = {
                val icon = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar campos adicionales si el switch está activado
        if (isInsurer) {
            TextField(
                value = company_name,
                onValueChange = { company_name = it },
                label = { Text("Nombre de la compañia asegurdadora") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.register_image),
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = fiscal_id,
                onValueChange = { fiscal_id = it },
                label = { Text("Numero de identificacion fiscal") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.register_image),
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = direction,
                onValueChange = { direction = it },
                label = { Text("Direccion de la sede principal") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.register_image),
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = license_number,
                onValueChange = { license_number = it },
                label = { Text("Numero de licencia") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.register_image),
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = "¿Eres un asegurador?",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 12.dp)
                .align(Alignment.Start)
        )

        Switch(
            checked = isInsurer,
            onCheckedChange = { isInsurer = it },
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { /* TODO: Lógica de registro */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Continuar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Al registrarte estas aceptando los",
                fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Terminos y condiciones",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { /* TODO: Navegar a terminos y condiciones */ }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "¿Ya tienes una cuenta?")
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Inicia Sesion",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onLoginClick() }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterViewPreview() {
    InzureTheme {
        registerView(PaddingValues(0.dp), onBackClick = {}, onLoginClick = {})
    }
}