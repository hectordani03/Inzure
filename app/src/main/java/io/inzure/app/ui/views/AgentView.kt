package io.inzure.app.ui.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.inzure.app.R

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.inzure.app.InzureTheme

class AgentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InzureTheme {
                AgentView()
            }
        }
    }
}

@Composable
fun AgentView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Fondo blanco
            .verticalScroll(rememberScrollState()) // Habilitar scroll
    ) {
        // Menú de navegación
        AgentNavigationBar()

        // Contenido principal
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))

            // Imagen del agente
            Image(
                painter = painterResource(id = R.drawable.ic_profile), // Sustituir con tu imagen
                contentDescription = "Imagen del agente",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre del agente
            Text(
                text = "Jose Joshua",
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            Text(
                text = "Con más de 10 años de experiencia en el sector asegurador, mi misión es brindar tranquilidad a las familias y empresas a través de soluciones personalizadas en seguros de vida, autos, negocios y personales.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botones de seguros
            ButtonSection()

            Spacer(modifier = Modifier.height(24.dp))

            // Botón "Ir al chat"
            Button(
                onClick = { /* Navegar al chat */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF04305A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.chat_icon), // Ícono de chat
                    contentDescription = "Chat",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Ir al chat", color = Color.White, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sección de contacto
        ContactSection()
    }
}

@Composable
fun AgentNavigationBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF04305A)) // Fondo azul oscuro
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_menu), // Ícono del menú
                contentDescription = "Menú",
                tint = Color.White,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Perfil de Asegurador",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun ButtonSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        InsuranceButton("Seguros de vida")
        InsuranceButton("Seguros empresariales")
        InsuranceButton("Seguros para autos")
    }
}

@Composable
fun InsuranceButton(text: String) {
    Button(
        onClick = { /* Acción al presionar */ },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F3F3)),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(4.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_profile2), // Ícono adecuado
            contentDescription = "Ícono de seguro",
            tint = Color(0xFF04305A),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Color(0xFF04305A), fontSize = 16.sp)
    }
}

@Composable
fun ContactSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF04305A), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Contáctame",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        ContactRow(icon = R.drawable.ic_profile2, text = "(55) 1234-5678")
        Spacer(modifier = Modifier.height(8.dp))
        ContactRow(icon = R.drawable.email_icon, text = "jose@segurosintegrales.com")
        Spacer(modifier = Modifier.height(8.dp))
        ContactRow(icon = R.drawable.ic_profile2, text = "Lunes a viernes de 9:00 AM a 6:00 PM; sábados de 10:00 AM a 2:00 PM.")
    }
}

@Composable
fun ContactRow(icon: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Color.White, fontSize = 14.sp)
    }
}
