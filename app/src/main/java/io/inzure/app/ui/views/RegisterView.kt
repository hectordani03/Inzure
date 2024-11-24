package io.inzure.app.ui.views

import android.app.DatePickerDialog
import android.content.Context
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import com.google.firebase.auth.FirebaseAuth
import io.inzure.app.MainActivity
import io.inzure.app.R
import io.inzure.app.ui.theme.InzureTheme
import java.util.*
import com.google.firebase.firestore.FirebaseFirestore
import com.opencsv.CSVReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.regex.Pattern


class RegisterView : ComponentActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializa Firestore y FirebaseAuth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setContent {
            InzureTheme {
                Scaffold { paddingValues ->
                    RegisterView(
                        paddingValues,
                        OnBackClick = {
                            val intent = Intent(this@RegisterView, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        },
                        OnLoginClick = {
                            val intent = Intent(this@RegisterView, LoginView::class.java)
                            startActivity(intent)
                            finish()
                        },
                        OnRegister = { userData, isInsurer ->
                            registerUserWithFirebaseAuth(userData, isInsurer)
                        }
                    )
                }
            }
        }
    }

    private fun registerUserWithFirebaseAuth(userData: Map<String, Any>, isInsurer: Boolean) {
        val email = userData["email"] as? String ?: ""
        val password = userData["password"] as? String ?: ""

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.let {
                            // Enviar correo de verificación
                            it.sendEmailVerification()
                                .addOnCompleteListener { verificationTask ->
                                    if (verificationTask.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            "Correo de verificación enviado a $email. Por favor verifica tu correo.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        val userId = it.uid
                                        saveUserToFirestore(userId, userData, isInsurer)
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Error al enviar el correo de verificación: ${verificationTask.exception?.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        }
                    } else {
                        val errorMessage = getFirebaseAuthErrorMessage(task.exception)
                        Toast.makeText(
                            this,
                            "Error al registrar usuario: $errorMessage",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        } else {
            Toast.makeText(this, "Correo electrónico o contraseña inválidos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFirebaseAuthErrorMessage(exception: Exception?): String {
        return when (exception?.message) {
            "The email address is already in use by another account." ->
                "La dirección de correo electrónico ya está en uso por otra cuenta."
            "The email address is badly formatted." ->
                "La dirección de correo electrónico tiene un formato incorrecto."
            "The given password is invalid. [ Password should be at least 6 characters ]" ->
                "La contraseña es inválida. Debe tener al menos 6 caracteres."
            "There is no user record corresponding to this identifier. The user may have been deleted." ->
                "No existe un registro de usuario con este correo. El usuario puede haber sido eliminado."
            "The password is invalid or the user does not have a password." ->
                "La contraseña es inválida o el usuario no tiene una contraseña."
            else ->
                exception?.message ?: "Error desconocido"
        }
    }

    private fun saveUserToFirestore(userId: String, userData: Map<String, Any>, isInsurer: Boolean) {
        // Crea una copia mutable de userData
        val userDataToSave = userData.toMutableMap()

        // Elimina la clave "password" si existe
        userDataToSave.remove("password")

        firestore.collection("Users")
            .document(userId)
            .set(userDataToSave)
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario registrado exitosamente!", Toast.LENGTH_SHORT).show()
                // Redirigir a MainActivity o cualquier otra actividad deseada
                val intent = Intent(this@RegisterView, LoginView::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterView(
    paddingValues: PaddingValues,
    OnBackClick: () -> Unit,
    OnLoginClick: () -> Unit,
    OnRegister: (Map<String, Any>, Boolean) -> Unit
) {
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

    fun isRFCValid(rfc: String): Boolean {
        // Expresión regular para validar el RFC de personas físicas y morales
        val rfcPattern = "^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}\$"
        val pattern = Pattern.compile(rfcPattern)
        val matcher = pattern.matcher(rfc)

        return matcher.matches()
    }

    fun validarCedula(context: Context, cedula: String): Boolean {
        return try {
            // Abre el archivo CSV desde la carpeta assets
            val inputStream = context.assets.open("listado_cedula.csv")
            val reader = CSVReader(InputStreamReader(inputStream))

            var line: Array<String>?
            while (reader.readNext().also { line = it } != null) {
                // Verifica si el número de cédula está en la cuarta columna (índice 3)
                if (line?.size ?: 0 > 3 && line!![3] == cedula) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            Toast.makeText(context, "Error al leer el archivo CSV: ${e.message}", Toast.LENGTH_LONG).show()
            false
        }
    }

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
    var hasTwoDigits by remember { mutableStateOf(false) }
    var hasSpecialChar by remember { mutableStateOf(false) }
    var hasMinLength by remember { mutableStateOf(false) }
    var hasNoRepeatedDigits by remember { mutableStateOf(false) }

    // Función para validar la contraseña
    fun validatePassword(password: String): Boolean {
        hasUpperCase = password.any { it.isUpperCase() }
        hasLowerCase = password.any { it.isLowerCase() }
        hasDigit = password.any { it.isDigit() }
        hasTwoDigits = password.count { it.isDigit() } >= 2
        hasSpecialChar = password.any { !it.isLetterOrDigit() }
        hasMinLength = password.length >= 8
        hasNoRepeatedDigits = !password.contains(Regex("(\\d)\\1{1,}"))

        return hasUpperCase && hasLowerCase && hasTwoDigits && hasSpecialChar && hasMinLength && hasNoRepeatedDigits
    }
    // Función para mostrar el estado de cada criterio de la contraseña
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
            last_name.isEmpty() -> {
                lastNameFocusRequester.requestFocus()
                Toast.makeText(context, "El campo Apellidos es requerido", Toast.LENGTH_SHORT).show()
                false
            }
            email.isEmpty() -> {
                emailFocusRequester.requestFocus()
                Toast.makeText(context, "El campo Correo Electrónico es requerido", Toast.LENGTH_SHORT).show()
                false
            }
            !isEmailValid(email) -> {
                emailFocusRequester.requestFocus()
                Toast.makeText(context, "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show()
                false
            }
            phone.isEmpty() -> {
                phoneFocusRequester.requestFocus()
                Toast.makeText(context, "El campo Télefono es requerido", Toast.LENGTH_SHORT).show()
                false
            }
            phone.length != 10 -> {
                phoneFocusRequester.requestFocus()
                Toast.makeText(context, "El teléfono debe tener exactamente 10 dígitos", Toast.LENGTH_SHORT).show()
                false
            }
            selectedDate.isEmpty() -> {
                selectedDateFocusRequester.requestFocus()
                Toast.makeText(context, "El campo Fecha de Nacimiento es requerido", Toast.LENGTH_SHORT).show()
                false
            }
            !isAdult(selectedDate) -> {
                selectedDateFocusRequester.requestFocus()
                Toast.makeText(context, "Debes tener al menos 18 años para registrarte", Toast.LENGTH_SHORT).show()
                false
            }
            password.isEmpty() -> {
                passwordFocusRequester.requestFocus()
                Toast.makeText(context, "El campo Contraseña es requerido", Toast.LENGTH_SHORT).show()
                false
            }
            !validatePassword(password) -> {
                passwordFocusRequester.requestFocus()
                Toast.makeText(context, "La contraseña no cumple con los requisitos", Toast.LENGTH_SHORT).show()
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
            isInsurer && company_name.isEmpty() -> {
                companyNameFocusRequester.requestFocus()
                Toast.makeText(context, "El campo Nombre de la Compañía Aseguradora es requerido", Toast.LENGTH_SHORT).show()
                false
            }
            isInsurer && fiscal_id.isEmpty() -> {
                fiscalIdFocusRequester.requestFocus()
                Toast.makeText(context, "El campo Número de Identificación Fiscal es requerido", Toast.LENGTH_SHORT).show()
                false
            }
            isInsurer && !isRFCValid(fiscal_id) -> {
                fiscalIdFocusRequester.requestFocus()
                Toast.makeText(context, "El RFC no tiene un formato válido", Toast.LENGTH_SHORT).show()
                false
            }
            isInsurer && direction.isEmpty() -> {
                directionFocusRequester.requestFocus()
                Toast.makeText(context, "El campo Dirección de la Sede Principal es requerido", Toast.LENGTH_SHORT).show()
                false
            }
            isInsurer && license_number.isEmpty() -> {
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
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Volver",
            modifier = Modifier
                .align(Alignment.Start)
                .clickable { OnBackClick() }
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
            modifier = Modifier.fillMaxWidth().focusRequester(nameFocusRequester),
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
            modifier = Modifier.fillMaxWidth().focusRequester(lastNameFocusRequester),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
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
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester),
            shape = RoundedCornerShape(8.dp)
        )

// Indicador de criterios de contraseña
        Column(
            modifier = Modifier.padding(start = 8.dp, top = 8.dp)
        ) {
            PasswordCriteriaRow(hasMinLength, "Mínimo 8 caracteres")
            PasswordCriteriaRow(hasUpperCase, "Al menos una letra mayúscula")
            PasswordCriteriaRow(hasLowerCase, "Al menos una letra minúscula")
            PasswordCriteriaRow(hasTwoDigits, "Al menos dos números")
            PasswordCriteriaRow(hasSpecialChar, "Al menos un carácter especial")
            PasswordCriteriaRow(hasNoRepeatedDigits, "No contener dígitos repetidos consecutivamente")
        }

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
            modifier = Modifier.fillMaxWidth().focusRequester(confirmPasswordFocusRequester),
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
                modifier = Modifier.fillMaxWidth().focusRequester(companyNameFocusRequester),
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
                label = { Text("Número de identificacion fiscal (RFC)") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.register_image),
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth().focusRequester(fiscalIdFocusRequester),
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
                label = { Text("Dirección de la sede principal") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.register_image),
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth().focusRequester(directionFocusRequester),
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
                label = { Text("Número de Cédula") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.register_image),
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth().focusRequester(licenseNumberFocusRequester),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                if (validateFields()) {
                    // Verificar el número de cédula solo si el usuario es asegurador
                    if (isInsurer) {
                        val isCedulaValid = validarCedula(context, license_number)

                        if (!isCedulaValid) {
                            Toast.makeText(context, "Número de Cédula no válido.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                    }

                    val userData = hashMapOf(
                        "email" to email,
                        "firstName" to name,
                        "lastName" to last_name,
                        "phone" to phone,
                        "birthDate" to selectedDate,
                        "password" to password,
                        "image" to "",
                        "role" to if (isInsurer) "Insurer" else "Client"
                    ).apply {
                        if (isInsurer) {
                            put("companyName", company_name)
                            put("fiscalId", fiscal_id)
                            put("direction", direction)
                            put("licenseNumber", license_number)
                        }
                    }
                    OnRegister(userData, isInsurer)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Registrarse", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        var showTermsDialog by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Al registrarte estas aceptando los",
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Términos y condiciones",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        showTermsDialog = true
                    }
                )
            }

            if (showTermsDialog) {
                AlertDialog(
                    onDismissRequest = { showTermsDialog = false },
                    text = {
                        LazyColumn(
                            modifier = Modifier.height(400.dp)
                        ) {
                            item {
                                Text(text = getTermsAndConditionsText())
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showTermsDialog = false },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Aceptar")
                        }
                    }
                )
            }

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
                modifier = Modifier.clickable { OnLoginClick() }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterViewPreview() {
    InzureTheme {
        RegisterView(PaddingValues(0.dp), OnBackClick = {}, OnLoginClick = {}, OnRegister = { _, _ -> })
    }
}