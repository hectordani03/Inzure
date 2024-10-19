package io.inzure.app.ui.views

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import io.inzure.app.MainActivity
import io.inzure.app.R
import io.inzure.app.ui.theme.InzureTheme
import java.text.SimpleDateFormat
import java.util.*

class RegisterView : ComponentActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializa Firestore
        firestore = FirebaseFirestore.getInstance()

        setContent {
            InzureTheme {
                Scaffold { paddingValues ->
                    registerView(
                        paddingValues,
                        onBackClick = {
                            val intent = Intent(this@RegisterView, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        },
                        onLoginClick = {
                            val intent = Intent(this@RegisterView, LoginView::class.java)
                            startActivity(intent)
                            finish()
                        },
                        onRegister = { userData, isInsurer ->
                            saveUserToFirestore(userData, isInsurer)
                        }
                    )
                }
            }
        }
    }

    private fun saveUserToFirestore(userData: Map<String, Any>, isInsurer: Boolean) {
        val subCollectionName = if (isInsurer) "UserInsurer" else "UserClient"
        val name = userData["name"] as? String ?: "Unknown"
        val lastName = userData["lastName"] as? String ?: "User"
        val customId = "${name}_${lastName}".replace(" ", "_").lowercase()

        firestore.collection("Users")
            .document(subCollectionName)
            .collection("userData")
            .document(customId)
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario registrado con éxito en $subCollectionName con ID $customId!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error en el registro: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun registerView(
    paddingValues: PaddingValues,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegister: (Map<String, Any>, Boolean) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var fiscalId by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isInsurer by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }

    val context = LocalContext.current

    // FocusRequesters para cada campo
    val nameFocusRequester = FocusRequester()
    val lastNameFocusRequester = FocusRequester()
    val emailFocusRequester = FocusRequester()
    val phoneFocusRequester = FocusRequester()
    val selectedDateFocusRequester = FocusRequester()
    val passwordFocusRequester = FocusRequester()
    val confirmPasswordFocusRequester = FocusRequester()

    // FocusRequesters para los campos del asegurador
    val companyNameFocusRequester = FocusRequester()
    val fiscalIdFocusRequester = FocusRequester()
    val directionFocusRequester = FocusRequester()
    val licenseNumberFocusRequester = FocusRequester()

    // Función para validar la estructura y dominio del correo electrónico
    fun isEmailValid(email: String): Boolean {
        val emailPattern = Patterns.EMAIL_ADDRESS
        val validDomains = listOf(
            // Proveedores de correo electrónico más comunes
            "gmail.com", "hotmail.com", "yahoo.com", "outlook.com",
            "icloud.com", "live.com", "protonmail.com", "mail.com",
            "aol.com", "zoho.com", "gmx.com", "yandex.com",

            // Variaciones por país
            "com.mx", "gob.mx", "org.mx", "edu.mx",
            "es", "co.uk", "fr", "de", "it", "ca", "br",
            "ru", "in", "cn", "jp", "kr", "au", "ar",

            // Dominios educativos y gubernamentales
            "edu", "gov", "mil", "org", "ac.uk", "edu.au",
            "edu.co", "edu.in", "edu.cn", "edu.jp",

            // Proveedores de correos alternativos y de trabajo
            "me.com", "fastmail.com", "hushmail.com", "tutanota.com",
            "naver.com", "qq.com", "gawab.com", "runbox.com",

            // Dominios específicos de empresas y servicios
            "mail.ru", "ymail.com", "rocketmail.com", "mac.com",
            "verizon.net", "sbcglobal.net", "bellsouth.net", "comcast.net",

            // Dominios de universidades y otras organizaciones
            "berkeley.edu", "mit.edu", "stanford.edu", "harvard.edu",
            "ucol.mx", "unam.mx", "unam.es", "unizar.es", "uam.mx",

            // Dominios regionales adicionales
            "ch", "nl", "pt", "se", "no", "dk", "fi", "pl", "gr",
            "cz", "hu", "ro", "ua", "sk", "bg", "hr", "rs"
        )

        // Verifica si el correo tiene un formato válido
        if (!emailPattern.matcher(email).matches()) {
            return false
        }

        // Extrae el dominio del correo electrónico
        val domain = email.substringAfter("@")
        return validDomains.any { domain.endsWith(it) }
    }

    // Estados de validación de la contraseña
    var hasUpperCase by remember { mutableStateOf(false) }
    var hasLowerCase by remember { mutableStateOf(false) }
    var hasDigit by remember { mutableStateOf(false) }
    var hasSpecialChar by remember { mutableStateOf(false) }
    var hasMinLength by remember { mutableStateOf(false) }
    var hasNoRepeatedDigits by remember { mutableStateOf(false) }


    // Función para validar la contraseña
    fun validatePassword(password: String) {
        hasUpperCase = password.any { it.isUpperCase() }
        hasLowerCase = password.any { it.isLowerCase() }
        hasDigit = password.any { it.isDigit() }
        hasSpecialChar = password.any { !it.isLetterOrDigit() }
        hasMinLength = password.length >= 8
        hasNoRepeatedDigits = !password.contains(Regex("(\\d)\\1{1,}"))
    }
    // Función para mostrar el estado de cada requisito de la contraseña
    @Composable
    fun PasswordCriteriaRow(isValid: Boolean, text: String) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 2.dp)
        ) {
            val icon = if (isValid) Icons.Default.Check else Icons.Default.Close
            val color = if (isValid) Color.Green else Color.Red
            Icon(imageVector = icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, color = color, fontSize = 12.sp)
        }
    }

    // Función para verificar la edad del usuario
    fun isAdult(selectedDate: String): Boolean {
        if (selectedDate.isEmpty()) return false
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val birthDate = sdf.parse(selectedDate) ?: return false
        val calendar = Calendar.getInstance()
        calendar.time = birthDate

        val today = Calendar.getInstance()
        val age = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR)
        return if (today.get(Calendar.DAY_OF_YEAR) < calendar.get(Calendar.DAY_OF_YEAR)) {
            age - 1 >= 18
        } else {
            age >= 18
        }
    }

    // Función para validar todos los campos
    fun validateFields(): Boolean {
        return when {
            name.isEmpty() -> {
                nameFocusRequester.requestFocus()
                Toast.makeText(context, "El campo Nombre es requerido", Toast.LENGTH_SHORT).show()
                false
            }
            lastName.isEmpty() -> {
                lastNameFocusRequester.requestFocus()
                Toast.makeText(context, "El campo Apellidos es requerido", Toast.LENGTH_SHORT).show()
                false
            }
            email.isEmpty() || !isEmailValid(email) -> {
                emailFocusRequester.requestFocus()
                Toast.makeText(context, "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show()
                false
            }
            phone.length != 10 -> {
                phoneFocusRequester.requestFocus()
                Toast.makeText(context, "El teléfono debe tener exactamente 10 dígitos", Toast.LENGTH_SHORT).show()
                false
            }
            selectedDate.isEmpty() || !isAdult(selectedDate) -> {
                selectedDateFocusRequester.requestFocus()
                Toast.makeText(context, "Debes tener al menos 18 años para registrarte", Toast.LENGTH_SHORT).show()
                false
            }
            password.isEmpty() -> {
                passwordFocusRequester.requestFocus()
                Toast.makeText(context, "El campo Contraseña es requerido", Toast.LENGTH_SHORT).show()
                false
            }
            confirmPassword.isEmpty() -> {
                confirmPasswordFocusRequester.requestFocus()
                Toast.makeText(context, "El campo Confirmar Contraseña es requerido", Toast.LENGTH_SHORT).show()
                false
            }
            password != confirmPassword -> {
                confirmPasswordFocusRequester.requestFocus()
                Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                false
            }
            isInsurer && companyName.isEmpty() -> {
                companyNameFocusRequester.requestFocus()
                Toast.makeText(context, "El campo Nombre de la Compañía Aseguradora es requerido", Toast.LENGTH_SHORT).show()
                false
            }
            isInsurer && fiscalId.isEmpty() -> {
                fiscalIdFocusRequester.requestFocus()
                Toast.makeText(context, "El campo Número de Identificación Fiscal es requerido", Toast.LENGTH_SHORT).show()
                false
            }
            isInsurer && direction.isEmpty() -> {
                directionFocusRequester.requestFocus()
                Toast.makeText(context, "El campo Dirección de la Sede Principal es requerido", Toast.LENGTH_SHORT).show()
                false
            }
            isInsurer && licenseNumber.isEmpty() -> {
                licenseNumberFocusRequester.requestFocus()
                Toast.makeText(context, "El campo Número de Licencia es requerido", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen de registro
        Image(
            painter = painterResource(id = R.drawable.register_image),
            contentDescription = "Registro",
            modifier = Modifier.size(150.dp),
            contentScale = ContentScale.Crop
        )

        // Campo de nombre
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(nameFocusRequester),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de apellidos
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Apellidos") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(lastNameFocusRequester),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de correo electrónico
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(emailFocusRequester),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de teléfono
        TextField(
            value = phone,
            onValueChange = { if (it.length <= 10) phone = it.filter { char -> char.isDigit() } },
            label = { Text("Teléfono") },
            leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(phoneFocusRequester),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de fecha de nacimiento
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
            onValueChange = {},
            label = { Text("Fecha de Nacimiento") },
            leadingIcon = { Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(selectedDateFocusRequester)
                .clickable { datePickerDialog.show() },
            enabled = false,
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de contraseña
        TextField(
            value = password,
            onValueChange = {
                password = it
                validatePassword(it)
            },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )



        Spacer(modifier = Modifier.height(16.dp))

        // Confirmación de contraseña
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Contraseña") },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                val icon = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Switch para seleccionar si el usuario es un asegurador
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "¿Eres un asegurador?", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Switch(checked = isInsurer, onCheckedChange = { isInsurer = it })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campos adicionales para aseguradores
        if (isInsurer) {
            TextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = { Text("Nombre de la Compañía Aseguradora") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = fiscalId,
                onValueChange = { fiscalId = it },
                label = { Text("Número de Identificación Fiscal") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = direction,
                onValueChange = { direction = it },
                label = { Text("Dirección de la Sede Principal") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = licenseNumber,
                onValueChange = { licenseNumber = it },
                label = { Text("Número de Licencia") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
// Botón de registro
        Button(
            onClick = {
                if (validateFields()) {
                    val userData = hashMapOf(
                        "email" to email,
                        "name" to name,
                        "lastName" to lastName,
                        "phone" to phone,
                        "birthDate" to selectedDate
                    ).apply {
                        if (isInsurer) {
                            put("companyName", companyName)
                            put("fiscalId", fiscalId)
                            put("direction", direction)
                            put("licenseNumber", licenseNumber)
                        }
                    }
                    onRegister(userData, isInsurer)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Registrar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterViewPreview() {
    InzureTheme {
        registerView(PaddingValues(0.dp), onBackClick = {}, onLoginClick = {}, onRegister = { _, _ -> })
    }
}