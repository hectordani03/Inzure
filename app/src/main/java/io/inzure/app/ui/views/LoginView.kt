package io.inzure.app.ui.views

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.inzure.app.MainActivity
import io.inzure.app.R
import io.inzure.app.ui.theme.InzureTheme
import io.inzure.app.auth.AuthManager
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import io.inzure.app.viewmodel.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore

class LoginView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Comprobamos si el usuario está logueado, en caso de que sí, lo redireccionamos a la pantalla principal
        val am = AuthManager(this)
        if (am.isUserLoggedIn()) {
            if (Intent.ACTION_MAIN == intent.action && intent.hasCategory(Intent.CATEGORY_LAUNCHER)) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                finish()
            }
        }

        setContent {
            InzureTheme {
                Scaffold { paddingValues ->
                    loginView(
                        paddingValues,
                        onBackClick = {
                            val intent = Intent(this@LoginView, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        },
                        onRegisterClick = {
                            val intent = Intent(this@LoginView, RegisterView::class.java)
                            startActivity(intent)
                        },
                        onLoginClick = { email: String, password: String ->
                            if (email.isEmpty() || password.isEmpty()) {
                                Toast.makeText(this@LoginView, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show()
                                return@loginView
                            }

                            val authManager = AuthManager(this@LoginView)
                            authManager.login(email, password, onSuccess = {
                                val user = authManager.getCurrentUser()
                                if (user != null && user.isEmailVerified) {
                                    val userId = user.uid
                                    // Consultar Firestore para obtener el rol del usuario
                                    val firestore = FirebaseFirestore.getInstance()
                                    firestore.collection("Users").document(userId).get()
                                        .addOnSuccessListener { document ->
                                            val role = document.getString("role")
                                            when (role) {
                                                "admin", "editor" -> {
                                                    // Redirigir a AdminView si el rol es 'admin' o 'editor'
                                                    val intent =
                                                        Intent(
                                                            this@LoginView,
                                                            AdminView::class.java
                                                        )
                                                    startActivity(intent)
                                                    finish()
                                                }

                                                "insurer", "client" -> {
                                                    // Redirigir a MainActivity si el rol es 'insurer' o 'client'
                                                    val intent =
                                                        Intent(
                                                            this@LoginView,
                                                            MainActivity::class.java
                                                        )
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }
                                        }
                                } else {
                                    // Mostrar el popup si el correo no está verificado
                                    Toast.makeText(this@LoginView, "Correo no verificado, ingrese a su correo electrónico para verificarlo", Toast.LENGTH_SHORT).show()

                                }
                            }, onError = {
                                Toast.makeText(this@LoginView, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                            })
                        },
                        onForgotPasswordClick = { email ->
                            if (email.isEmpty()) {
                                Toast.makeText(this@LoginView, "Por favor, ingrese su correo electrónico", Toast.LENGTH_SHORT).show()
                            } else {
                                val authManager = AuthManager(this@LoginView)
                                authManager.sendPasswordResetEmail(email,
                                    onSuccess = {
                                        Toast.makeText(this@LoginView, "Correo de restablecimiento enviado. Revisa tu bandeja de entrada.", Toast.LENGTH_LONG).show()
                                    },
                                    onError = {
                                        Toast.makeText(this@LoginView, "Error al enviar el correo de restablecimiento, verifica que el correo sea válido", Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun loginView(
    paddingValues: PaddingValues,
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: (String, String) -> Unit,
    onForgotPasswordClick: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = AuthManager(context)
    val userViewModel: UserViewModel = viewModel()

    // Se añade scrollState para hacer la columna scrolleable
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(paddingValues)
            .padding(16.dp),
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
            painter = painterResource(id = R.drawable.ic_image_login),
            contentDescription = "Login illustration",
            modifier = Modifier.size(250.dp),
            contentScale = ContentScale.Crop
        )

        Text(
            text = "Inicia Sesión",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 12.dp)
                .align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.email_icon),
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
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.lock_2),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = image,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
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

        Spacer(modifier = Modifier.height(8.dp))

        // Sección para "¿Olvidaste tu contraseña?"
        Text(
            text = "¿Olvidaste tu contraseña?",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { onForgotPasswordClick(email) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                onLoginClick(email, password)

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    auth.login(email, password, onSuccess = {
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val authEmail = currentUser?.email
                        val userDocRef = db.collection("Users").document(currentUser!!.uid)
                        userDocRef.get().addOnSuccessListener { document ->
                            val firestoreEmail = document.getString("email")
                            if (authEmail != null && authEmail != firestoreEmail) {
                                userDocRef.update("email", authEmail)
                            }
                        }
                    }, onError = {
                        // Manejo de errores
                    })
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        // Mostrar el popup después del inicio de sesión si el correo no está verificado
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Verificación de Correo") },
                text = { Text(text = "Por favor, verifica tu correo electrónico para acceder a todas las funciones.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val user = auth.getCurrentUser()
                            user?.sendEmailVerification()?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    showDialog = false
                                    Toast.makeText(
                                        context,
                                        "Correo de verificación reenviado. Por favor revisa tu bandeja de entrada.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    showDialog = false
                                    Toast.makeText(
                                        context,
                                        "Error al reenviar el correo de verificación.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    ) {
                        Text("Reenviar correo")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "─────────  O  ─────────",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = { /* TODO: Lógica de inicio con Google */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.google_icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Inicia con Google", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "¿Nuevo aquí?")
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Regístrate",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onRegisterClick() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginViewPreview() {
    InzureTheme {
        loginView(
            PaddingValues(0.dp),
            onBackClick = {},
            onRegisterClick = {},
            onLoginClick = { _, _ -> },
            onForgotPasswordClick = {}
        )
    }
}