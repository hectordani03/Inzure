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


class LoginView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Comprobamos si el usuario está logueado, en caso de que sí, lo redireccionamos a la pantalla principal
        val am = AuthManager(this)
        if (am.isUserLoggedIn()) {
            if (Intent.ACTION_MAIN == intent.action && intent.hasCategory(Intent.CATEGORY_LAUNCHER)){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                finish()
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InzureTheme {
                Scaffold { paddingValues ->
                    loginView(paddingValues, onBackClick = {
                        val intent = Intent(this@LoginView, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, onRegisterClick = {
                        val intent = Intent(this@LoginView, RegisterView::class.java)
                        startActivity(intent)
                    }, onLoginClick = { email: String, password: String ->
                        // Evento a ejecutar al hacer click en el botón de inicio de sesión
                        // Aquí se implementa la logica de inicio de sesión
                        // Verificamos que los campos no estén vacíos
                        if (email.isEmpty() || password.isEmpty()) {
                            Toast.makeText(this@LoginView, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show()
                            return@loginView
                        }
                        // Si los campos no están vacíos, iniciamos sesión
                        val authManager = AuthManager(this@LoginView)
                        authManager.login(email, password, onSuccess = {
                            // Si la autenticación es exitosa, redireccionamos a la pantalla principal
                            // La sesion queda guardada en el dispositivo, pudes utlizar la clase AuthManager para obtener la sesion actual
                            val intent = Intent(this@LoginView, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, onError = {
                            // En caso de que los datos sean incorrectos, mostramos que sus credenciales son incorrectas
                            Toast.makeText(this@LoginView, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                        })
                    })
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
    onLoginClick: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Se añade scrollState para hacer la columna scrolleable
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState) // Hacemos el contenido scrolleable
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
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

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

        Text(
            text = "¿Olvidaste tu contraseña?",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.End).clickable { /* TODO: Recuperar contraseña */ }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
            Icon(painter = painterResource(R.drawable.google_icon), contentDescription = null, modifier = Modifier.size(24.dp))
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
        loginView(PaddingValues(0.dp), onBackClick = {}, onRegisterClick = {}, onLoginClick = ({ _, _ ->}))
    }
}
