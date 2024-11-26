package io.inzure.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import io.inzure.app.ui.views.LoginView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SplashScreen()
        }

        // Verificar autenticación después de la animación
        lifecycleScope.launch {
            delay(3000) // Espera 3 segundos para la pantalla de carga
            checkAuthentication() // Verificar la autenticación del usuario
        }
    }

    // Verificar si el usuario tiene sesión activa
    private fun checkAuthentication() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            // Usuario no autenticado, enviar a LoginView
            navigateToLoginView()
        } else {
            // Usuario autenticado, enviar a MainActivity
            navigateToMainActivity()
        }
    }

    private fun navigateToLoginView() {
        startActivity(Intent(this, LoginView::class.java))
        finish() // Finalizar SplashActivity
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish() // Finalizar SplashActivity
    }
}

@Composable
fun SplashScreen() {
    // Crear una transición infinita
    val infiniteTransition = rememberInfiniteTransition()

    // Animar el valor de rotación del logo de 0 a 360 grados
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500), // 1.5 segundos por cada rotación
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Fondo blanco
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo con animación de rotación
            Image(
                painter = painterResource(id = R.drawable.logo2), // Reemplaza con tu recurso de logo
                contentDescription = "Logo",
                modifier = Modifier
                    .size(100.dp)
                    .rotate(rotation) // Aplicar la rotación animada
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Texto debajo del logo
            Text(
                text = "Inzure",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}